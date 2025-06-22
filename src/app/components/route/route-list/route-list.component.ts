import { Component, OnInit } from '@angular/core';
import { Route } from 'src/app/models/route.model';
import { RouteService } from 'src/app/services/route.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-route-list',
  templateUrl: './route-list.component.html',
  styleUrls: ['./route-list.component.css']
})
export class RouteListComponent implements OnInit {
  routes: Route[] = [];
  filteredRoutes: Route[] = [];
  searchTerm: string = '';
  selectedOrigin: string = '';
  selectedDestination: string = '';

  origins: string[] = [];
  destinations: string[] = [];

  constructor(private routeService: RouteService, private router: Router) {}

  ngOnInit(): void {
    this.loadRoutes();
  }

  loadRoutes(): void {
    this.routeService.getAll().subscribe({
      next: (data) => {
        this.routes = data;
        this.filteredRoutes = data;

        // Unique origins/destinations for dropdown filters
        this.origins = [...new Set(data.map(r => r.origin))];
        this.destinations = [...new Set(data.map(r => r.destination))];
      },
      error: (err) => {
        console.error(err);
        alert('Failed to load routes');
      }
    });
  }

  deleteRoute(id: number): void {
    if (confirm('Are you sure you want to delete this route?')) {
      this.routeService.delete(id).subscribe({
        next: () => {
          alert('Route deleted successfully!');
          this.loadRoutes();
        },
        error: (err) => {
          console.error(err);
          alert('Error deleting route.');
        }
      });
    }
  }

  editRoute(id: number): void {
    this.router.navigate(['/route-edit', id]);
  }

  filterRoutes(): void {
    const keyword = this.searchTerm.toLowerCase();

    this.filteredRoutes = this.routes.filter(r =>
      (r.origin.toLowerCase().includes(keyword) || r.destination.toLowerCase().includes(keyword) || r.busId?.toString().includes(keyword)) &&
      (!this.selectedOrigin || r.origin === this.selectedOrigin) &&
      (!this.selectedDestination || r.destination === this.selectedDestination)
    );
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedOrigin = '';
    this.selectedDestination = '';
    this.filterRoutes();
  }

  goBack(): void {
    this.router.navigate(['/admin-dashboard']);
  }
}
