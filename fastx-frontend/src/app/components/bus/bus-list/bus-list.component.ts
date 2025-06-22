import { Component, OnInit } from '@angular/core';
import { Bus } from 'src/app/models/bus.model';
import { BusService } from 'src/app/services/bus.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-bus-list',
  templateUrl: './bus-list.component.html',
  styleUrls: ['./bus-list.component.css']
})
export class BusListComponent implements OnInit {
  buses: Bus[] = [];
  filteredBuses: Bus[] = [];
  searchTerm: string = '';

  constructor(private busService: BusService, private router: Router) {}

  ngOnInit(): void {
    this.loadBuses();
  }

  loadBuses(): void {
    this.busService.getAll().subscribe({
      next: (data) => {
        this.buses = data;
        this.filteredBuses = data;
      },
      error: (err) => {
        console.error(err);
        alert('Failed to load buses');
      }
    });
  }

  filterBuses(): void {
    const keyword = this.searchTerm.toLowerCase();

    this.filteredBuses = this.buses.filter(bus =>
      bus.busName.toLowerCase().includes(keyword) ||
      bus.busNumber.toLowerCase().includes(keyword) ||
      bus.busType.toLowerCase().includes(keyword) ||
      bus.totalSeats.toString().includes(keyword) ||
      (bus.amenities && bus.amenities.toLowerCase().includes(keyword))
    );
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.filteredBuses = [...this.buses];
  }

  deleteBus(id: number): void {
    if (confirm('Are you sure you want to delete this bus?')) {
      this.busService.delete(id).subscribe({
        next: () => {
          alert('Bus deleted');
          this.loadBuses();
        },
        error: (err) => {
          console.error(err);
          alert('Delete failed');
        }
      });
    }
  }

  editBus(id: number): void {
    this.router.navigate(['/bus-edit', id]);
  }
}
