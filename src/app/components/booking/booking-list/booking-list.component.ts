import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Booking } from 'src/app/models/booking.model';
import { BookingService } from 'src/app/services/booking.service';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-booking-list',
  templateUrl: './booking-list.component.html'
})
export class BookingListComponent implements OnInit {
  bookings: Booking[] = [];
  filteredBookings: Booking[] = [];
  searchTerm: string = '';
  userRole: string = '';
  userId: number = 0;

  constructor(
    private bookingService: BookingService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userRole = this.authService.getRole() ?? '';
    this.userId = this.authService.getUserId() ?? 0;

    if (this.userRole === 'ADMIN' || this.userRole === 'OPERATOR') {
      this.loadAllBookings();
    } else if (this.userRole === 'USER') {
      this.loadUserBookings(this.userId);
    } else {
      alert('Unauthorized access.');
      this.router.navigate(['/login']);
    }
  }

  loadAllBookings(): void {
    this.bookingService.getAll().subscribe({
      next: (data) => {
        this.bookings = data;
        this.filterBookings();
      },
      error: (err) => alert('Failed to load bookings: ' + err.message)
    });
  }

  loadUserBookings(userId: number): void {
    this.bookingService.getByUserId(userId).subscribe({
      next: (data) => {
        this.bookings = data;
        this.filterBookings();
      },
      error: (err) => alert('Failed to load your bookings: ' + err.message)
    });
  }

  deleteBooking(id: number): void {
    if (confirm('Are you sure you want to delete this booking?')) {
      this.bookingService.delete(id).subscribe({
        next: () => {
          alert('Booking deleted successfully!');
          this.reload();
        },
        error: (err) => alert('Delete failed: ' + err.message)
      });
    }
  }

  cancelBooking(id: number): void {
    if (confirm('Are you sure you want to cancel this booking?')) {
      this.bookingService.cancel(id).subscribe({
        next: () => {
          alert('Booking cancelled successfully!');
          this.reload();
        },
        error: (err) => alert('Cancellation failed: ' + err.message)
      });
    }
  }

  reload(): void {
    this.userRole === 'ADMIN' || this.userRole === 'OPERATOR'
      ? this.loadAllBookings()
      : this.loadUserBookings(this.userId);
  }

  formatSeats(seatNumbers: string[] | string): string {
    return Array.isArray(seatNumbers) ? seatNumbers.join(', ') : seatNumbers;
  }

  filterBookings(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredBookings = this.bookings.filter(b =>
      b.bookingId?.toString().includes(term) ||
      b.routeId?.toString().includes(term) ||
      b.userId?.toString().includes(term) ||
      b.seatsBooked?.toString().includes(term) ||
      b.status?.toLowerCase().includes(term) ||
      b.seatNumbers?.toString().toLowerCase().includes(term)
    );
  }
}
