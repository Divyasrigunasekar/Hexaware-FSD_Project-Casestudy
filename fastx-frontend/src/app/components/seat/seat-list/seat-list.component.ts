import { Component, OnInit } from '@angular/core';
import { Seat } from 'src/app/models/seat.model';
import { SeatService } from 'src/app/services/seat.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-seat-list',
  templateUrl: './seat-list.component.html',
  styleUrls: ['./seat-list.component.css']
})
export class SeatListComponent implements OnInit {
  seats: Seat[] = [];
  filteredSeats: Seat[] = [];
  searchTerm: string = '';

  constructor(private seatService: SeatService, private router: Router) {}

  ngOnInit(): void {
    this.loadSeats();
  }

  loadSeats() {
    this.seatService.getAll().subscribe({
      next: (data) => {
        this.seats = data.map((seat: any) => ({
          ...seat,
          isBooked: seat.booked
        }));
        this.filteredSeats = [...this.seats];
      },
      error: (err) => {
        alert('Error fetching seats.');
        console.error(err);
      }
    });
  }

  filterSeats() {
    const keyword = this.searchTerm.toLowerCase();

    this.filteredSeats = this.seats.filter(seat =>
      seat.seatNumber.toString().toLowerCase().includes(keyword) ||
      seat.routeId?.toString().includes(keyword) ||
      seat.busId?.toString().includes(keyword) ||
      (seat.isBooked ? 'booked' : 'available').includes(keyword)
    );
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.filteredSeats = [...this.seats];
  }

  deleteSeat(id: number) {
    if (confirm('Are you sure you want to delete this seat?')) {
      this.seatService.delete(id).subscribe({
        next: () => {
          alert('Seat deleted successfully.');
          this.loadSeats();
        },
        error: () => alert('Failed to delete seat.')
      });
    }
  }

  editSeat(id: number) {
    this.router.navigate(['/seat-edit', id]);
  }
}
