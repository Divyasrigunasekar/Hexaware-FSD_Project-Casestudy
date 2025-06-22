import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CancellationRequestDTO, CancellationResponseDTO } from 'src/app/models/cancellation.model';
import { Booking } from 'src/app/models/booking.model';
import { Payment } from 'src/app/models/payment.model';
import { Seat } from 'src/app/models/seat.model';
import { BookingService } from 'src/app/services/booking.service';
import { PaymentService } from 'src/app/services/payment.service';
import { CancellationService } from 'src/app/services/cancellation.service';
import { SeatService } from 'src/app/services/seat.service';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-cancellation-add',
  templateUrl: './cancellation-add.component.html'
})
export class CancellationAddComponent implements OnInit {
  request: CancellationRequestDTO = {
    bookingId: 0,
    paymentId: 0,
    seatIds: [],
    reason: ''
  };

  successMessage = '';
  errorMessage = '';
  warningMessage = '';

  bookings: Booking[] = [];
  payments: Payment[] = [];
  allSeats: Seat[] = [];

  seatSelectionList: { seatId: number; seatNumber: string; selected: boolean }[] = [];

  constructor(
    private bookingService: BookingService,
    private paymentService: PaymentService,
    private cancellationService: CancellationService,
    private seatService: SeatService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserId();
    console.log('[Init] User ID:', userId);

    if (userId) {
      this.bookingService.getByUserId(userId).subscribe({
        next: data => {
          this.bookings = data.filter(b =>
            ['BOOKED', 'CONFIRMED', 'PARTIALLY_CANCELLED'].includes(b.status)
          );
          console.log('[Init] Loaded Bookings:', this.bookings);
        },
        error: err => alert('Failed to load bookings')
      });

      this.paymentService.getByUserId(userId).subscribe({
        next: data => {
          this.payments = data;
          console.log('[Init] Loaded Payments:', this.payments);
        },
        error: err => alert('Failed to load payments')
      });

      this.seatService.getAll().subscribe({
        next: data => {
          this.allSeats = data;
          console.log('[Init] Loaded Seats:', this.allSeats);
        },
        error: err => console.error('Failed to load seats', err)
      });
    }
  }

  onBookingChange(): void {
    const selectedBooking = this.bookings.find(b => b.bookingId === this.request.bookingId);
    console.log('[Booking Change] Selected Booking:', selectedBooking);

    this.warningMessage = ''; // Reset warning

    if (!selectedBooking) return;

    // Check if fully cancelled
    if (selectedBooking.status === 'CANCELLED') {
      this.warningMessage = '❌ This booking is already fully cancelled.';
      this.resetForm();
      return;
    }

    // Check if only one seat left after partial cancellation
    if (selectedBooking.status === 'PARTIALLY_CANCELLED' && selectedBooking.seatNumbers.length === 1) {
      this.warningMessage = '⚠️ This booking is already partially cancelled and cannot be cancelled again.';
      this.resetForm();
      return;
    }

    // Continue if valid
    fetch(`http://localhost:9090/api/routes/${selectedBooking.routeId}`, {
      headers: {
        'Authorization': `Bearer ${this.authService.getToken()}`
      }
    })
      .then(res => res.json())
      .then((route) => {
        const departureTime = new Date(route.departureTime);
        const currentTime = new Date();

        if (departureTime < currentTime) {
          this.warningMessage = '❌ Cancellation not allowed: The journey has already started.';
          this.resetForm();
          return;
        }

        const payment = this.payments.find(p => p.bookingId === selectedBooking.bookingId);

        if (payment?.paymentId) {
          this.request.paymentId = payment.paymentId;
        } else {
          this.warningMessage = '❌ No valid payment found for this booking.';
          this.resetForm();
          return;
        }

        const matchedSeats = this.allSeats.filter(seat =>
          seat.routeId === selectedBooking.routeId &&
          selectedBooking.seatNumbers.includes(seat.seatNumber)
        );

        this.seatSelectionList = matchedSeats.map(seat => ({
          seatId: seat.seatId!,
          seatNumber: seat.seatNumber,
          selected: true
        }));

        this.request.seatIds = this.seatSelectionList.map(s => s.seatId);
      })
      .catch(err => {
        this.warningMessage = '❌ Failed to fetch route details.';
        this.resetForm();
      });
  }

  toggleSeatSelection(seatId: number): void {
    const seat = this.seatSelectionList.find(s => s.seatId === seatId);
    if (!seat) return;

    seat.selected = !seat.selected;

    this.request.seatIds = this.seatSelectionList
      .filter(s => s.selected)
      .map(s => s.seatId);

    console.log('[Seat Toggle] Updated Seat Selection:', this.request.seatIds);
  }

  submit(): void {
    console.log('[Submit] Cancellation Request:', this.request);

    if (!this.request.bookingId || !this.request.paymentId) {
      alert('Please select a booking with a valid payment.');
      return;
    }

    if (!this.request.seatIds.length) {
      alert('Please select at least one seat to cancel.');
      return;
    }

    if (!this.request.reason.trim()) {
      alert('Please provide a cancellation reason.');
      return;
    }

    const selectedBooking = this.bookings.find(b => b.bookingId === this.request.bookingId);
    if (selectedBooking) {
      const totalSeats = selectedBooking.seatNumbers.length;
      const selectedForCancellation = this.request.seatIds.length;

      // Full cancellation warning
      if (totalSeats === selectedForCancellation) {
        const confirmFull = confirm(
          'You are cancelling all booked seats. This will fully cancel your booking. Do you want to proceed?'
        );
        if (!confirmFull) return;
      }
      // Last seat left warning
      else if (totalSeats - selectedForCancellation === 1) {
        const confirmLast = confirm(
          'After this cancellation, only one seat will remain. You may not be allowed to cancel again later. Do you want to continue?'
        );
        if (!confirmLast) return;
      }
    }

    this.cancellationService.cancelBooking(this.request).subscribe({
      next: (res: CancellationResponseDTO) => {
        console.log('[Submit] Success:', res);
        this.successMessage = res.message;
        this.errorMessage = '';
        setTimeout(() => this.router.navigate(['/cancellations']), 1500);
      },
      error: err => {
        console.error('[Submit] Error:', err);
        this.errorMessage = err.error?.message || 'Cancellation failed.';
        this.successMessage = '';
      }
    });
  }

  resetForm(): void {
    this.request.bookingId = 0;
    this.request.paymentId = 0;
    this.request.seatIds = [];
    this.request.reason = '';
    this.seatSelectionList = [];
  }
}
