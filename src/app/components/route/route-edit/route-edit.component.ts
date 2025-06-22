import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RouteService } from 'src/app/services/route.service';
import { Route } from 'src/app/models/route.model';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-route-edit',
  templateUrl: './route-edit.component.html',
  styleUrls: ['./route-edit.component.css']
})
export class RouteEditComponent implements OnInit {
  routeId!: number;

  route: Route = {
    busId: 0,
    origin: '',
    destination: '',
    departureTime: '',
    arrivalTime: '',
    fare: 0
  };

  constructor(
    private routeService: RouteService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.routeId = this.activatedRoute.snapshot.params['id'];
    this.routeService.getById(this.routeId).subscribe({
      next: (data:any) => {
        // Flatten bus object into busId
        this.route = {
          ...data,
          busId: data.bus?.busId ?? 0
        };
      },
      error: (err) => {
        console.error(err);
        alert('Error fetching route details.');
      }
    });
  }

  updateRoute(form: NgForm): void {
    if (form.invalid) {
      alert('Please correct the errors in the form.');
      return;
    }

    this.routeService.update(this.routeId, this.route).subscribe({
      next: () => {
        alert('Route updated successfully!');
        this.router.navigate(['/route-list']);
      },
      error: (err) => {
        console.error(err);
        alert('Error updating route.');
      }
    });
  }

  goToList(): void {
    this.router.navigate(['/route-list']);
  }
}