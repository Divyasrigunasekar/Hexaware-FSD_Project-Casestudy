import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Booking } from 'src/app/models/booking.model';
import { Route } from 'src/app/models/route.model';
import { BookingService } from 'src/app/services/booking.service';
import { RouteService } from 'src/app/services/route.service';
import { SeatService } from 'src/app/services/seat.service';

@Component({
  selector: 'app-booking-add',
  templateUrl: './booking-add.component.html',
  styleUrls: ['./booking-add.component.css']
})
export class BookingAddComponent implements OnInit {
  booking: Booking = {
    userId: 0,
    routeId: 0,
    bookingTime: new Date().toLocaleString('sv-SE', { timeZone: 'Asia/Kolkata' }).replace(' ', 'T'),
    seatsBooked: 0,
    seatNumbers: [],
    totalAmount: 0,
    status: 'CONFIRMED'
  };

  seatList: { seatNumber: string; isBooked: boolean }[] = [];
  farePerSeat = 0;
  seatError = false;
  canBookRoute: boolean = true;
  routes: Route[] = [];

  constructor(
    private bookingService: BookingService,
    private seatService: SeatService,
    private routeService: RouteService,
    private router: Router
  ) {}

  ngOnInit(): void {
  const userData = localStorage.getItem('user');
  if (userData) {
    const user = JSON.parse(userData);
    this.booking.userId = user.userId;
  } else {
    alert("⚠️ User not logged in. Please log in to continue booking.");
    this.router.navigate(['/login']);
  }

  // 🆕 Load all routes for dropdown
  this.routeService.getAll().subscribe({
    next: data => {
      this.routes = data;
    },
    error: err => {
      console.error('Route list load failed', err);
      alert('❌ Could not load routes. Please try again later.');
    }
  });
}

  onRouteIdChange(): void {
    if (this.booking.routeId > 0) {
      // Step 1: Check route departure time
      this.routeService.getById(this.booking.routeId).subscribe({
        next: (route) => {
          const departure = new Date(route.departureTime);
          const now = new Date();

          if (departure > now) {
            this.canBookRoute = true;
          } else {
            this.canBookRoute = false;
            alert('❌ This bus has already departed. Please choose a future route.');
          }
        },
        error: (err) => {
          console.error('Route time fetch error:', err);
          alert('⚠️ Failed to fetch route timing. Please try again.');
        }
      });

      // Step 2: Load seats for this route
      this.seatService.getSeatsByRoute(this.booking.routeId).subscribe({
        next: (seats) => {
          this.seatList = seats.map(seat => ({
            seatNumber: seat.seatNumber,
            isBooked: seat.booked
          }));
          this.booking.seatNumbers = [];
          this.booking.seatsBooked = 0;
          this.booking.totalAmount = 0;
          this.seatError = false;
        },
        error: (err) => {
          console.error('Seat loading error:', err);
          alert('🚫 Unable to load seat information for this route. Please try again later.');
        }
      });

      // Step 3: Get fare
      this.routeService.getFare(this.booking.routeId).subscribe({
        next: (fare) => {
          this.farePerSeat = fare;
          this.recalculateTotal();
        },
        error: (err) => {
          console.error('Fare loading error:', err);
          alert('⚠️ Failed to retrieve fare details. Please try again.');
        }
      });
    }
  }

  toggleSeat(seat: string, isBooked: boolean): void {
    if (isBooked) return;

    const index = this.booking.seatNumbers.indexOf(seat);
    if (index > -1) {
      this.booking.seatNumbers.splice(index, 1);
    } else {
      this.booking.seatNumbers.push(seat);
    }

    this.recalculateTotal();
    this.seatError = this.booking.seatNumbers.length === 0;
  }

  recalculateTotal(): void {
    this.booking.seatsBooked = this.booking.seatNumbers.length;
    this.booking.totalAmount = this.booking.seatsBooked * this.farePerSeat;
  }

  addBooking(form: any): void {
    this.seatError = this.booking.seatNumbers.length === 0;

    if (form.invalid) {
      alert('🚫 Form is incomplete. Please fill out all required fields.');
      return;
    }

    if (this.seatError) {
      alert('❗ Please select at least one available seat before booking.');
      return;
    }

    this.bookingService.add(this.booking).subscribe({
      next: (savedBooking: Booking) => {
        alert(`✅ Booking successful!\n\n🪑 Seats Booked: ${this.booking.seatNumbers.join(', ')}\n💰 Amount to pay: ₹${this.booking.totalAmount}`);

        // Redirect to payment-add page with bookingId and amount
        this.router.navigate(['/payment-add'], {
          queryParams: {
            bookingId: savedBooking.bookingId,
            amount: savedBooking.totalAmount
          }
        });
      },
      error: (err) => {
        console.error('Booking error:', err);
        alert(`❌ Booking failed!\n\nPlease try again later or contact support.\n\nDetails: ${err.message}`);
      }
    });
  }
}
