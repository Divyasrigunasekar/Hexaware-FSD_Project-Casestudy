import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Booking } from 'src/app/models/booking.model';
import { BookingService } from 'src/app/services/booking.service';
import { RouteService } from 'src/app/services/route.service';
import { SeatService } from 'src/app/services/seat.service';

@Component({
  selector: 'app-booking-edit',
  templateUrl: './booking-edit.component.html'
})
export class BookingEditComponent implements OnInit {
  bookingId!: number;
  booking: Booking = {
    userId: 0,
    routeId: 0,
    bookingTime: new Date().toISOString(),
    seatsBooked: 0,
    seatNumbers: [],
    totalAmount: 0,
    status: 'CONFIRMED'
  };

  allSeatNumbers: string[] = [];
  seatError = false;
  farePerSeat = 0;

  constructor(
    private bookingService: BookingService,
    private seatService: SeatService,
    private routeService: RouteService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.bookingId = Number(this.activatedRoute.snapshot.paramMap.get('id'));
    if (this.bookingId > 0) {
      this.loadBooking(this.bookingId);
    } else {
      alert('Invalid booking ID');
      this.router.navigate(['/booking-list']);
    }
  }

  loadBooking(id: number): void {
    this.bookingService.getById(id).subscribe({
      next: (data) => {
        this.booking = {
          ...data,
          bookingTime: new Date(data.bookingTime).toISOString(),
          seatNumbers: Array.isArray(data.seatNumbers)
            ? data.seatNumbers
            : String(data.seatNumbers).split(',')
        };
        // Now load seats and fare for the route
        if (this.booking.routeId > 0) {
          this.loadRouteDetails(this.booking.routeId);
        }
      },
      error: (err) => {
        alert('Failed to load booking: ' + err.message);
        this.router.navigate(['/booking-list']);
      }
    });
  }

  loadRouteDetails(routeId: number): void {
    // Load available seats
    this.seatService.getSeatsByRoute(routeId).subscribe({
      next: (seats) => {
        this.allSeatNumbers = seats.map(s => s.seatNumber);
        // Make sure selected seats still exist in available seats
        this.booking.seatNumbers = this.booking.seatNumbers.filter(seat => this.allSeatNumbers.includes(seat));
        this.recalculateTotal();
        this.seatError = this.booking.seatNumbers.length === 0;
      },
      error: (err) => alert('Failed to load seats: ' + err.message)
    });

    // Load fare per seat
    this.routeService.getFare(routeId).subscribe({
      next: (fare) => {
        this.farePerSeat = fare;
        this.recalculateTotal();
      },
      error: (err) => alert('Failed to load fare: ' + err.message)
    });
  }

  onRouteIdChange(): void {
    if (this.booking.routeId > 0) {
      this.loadRouteDetails(this.booking.routeId);
      // Reset seats selection if route changes
      this.booking.seatNumbers = [];
      this.booking.seatsBooked = 0;
      this.booking.totalAmount = 0;
      this.seatError = true;
    } else {
      this.allSeatNumbers = [];
      this.farePerSeat = 0;
      this.booking.seatNumbers = [];
      this.booking.seatsBooked = 0;
      this.booking.totalAmount = 0;
      this.seatError = true;
    }
  }

  onSeatToggle(seat: string, checked: boolean): void {
    if (checked) {
      if (!this.booking.seatNumbers.includes(seat)) {
        this.booking.seatNumbers.push(seat);
      }
    } else {
      this.booking.seatNumbers = this.booking.seatNumbers.filter(s => s !== seat);
    }
    this.recalculateTotal();
    this.seatError = this.booking.seatNumbers.length === 0;
  }

  recalculateTotal(): void {
    this.booking.seatsBooked = this.booking.seatNumbers.length;
    this.booking.totalAmount = this.booking.seatsBooked * this.farePerSeat;
  }

  updateBooking(form: any): void {
  this.seatError = this.booking.seatNumbers.length === 0;

  if (form.invalid || this.seatError) {
    alert('Please correct the errors.');
    return;
  }

  const bookingPayload = {
    ...this.booking,
    id: this.bookingId,
    // ✅ KEEP seatNumbers AS AN ARRAY
    seatNumbers: this.booking.seatNumbers,
    // ✅ Optional: format time if needed by backend
    bookingTime: new Date(this.booking.bookingTime).toISOString().slice(0, 19).replace('T', ' ')
  };

  this.bookingService.update(this.bookingId, bookingPayload).subscribe({
    next: () => {
      alert('Booking updated successfully!');
      this.router.navigate(['/booking-list']);
    },
    error: (err) => alert('Error updating booking: ' + err.message)
  });

}




  getChecked(event: Event): boolean {
    return (event.target as HTMLInputElement)?.checked ?? false;
  }
  isSeatSelected(seat: string): boolean {
  return this.booking.seatNumbers.includes(seat);
}
}
