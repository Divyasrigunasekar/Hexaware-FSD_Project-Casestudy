import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Route } from 'src/app/models/route.model';
import { RouteService } from 'src/app/services/route.service';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-route-add',
  templateUrl: './route-add.component.html',
  styleUrls: ['./route-add.component.css']
})
export class RouteAddComponent implements OnInit {
  route: Route = {
    busId: 0,
    origin: '',
    destination: '',
    departureTime: '',
    arrivalTime: '',
    fare: 0
  };

  minDateTime: string = '';

  constructor(
    private routeService: RouteService,
    public router: Router,
    private activatedRoute: ActivatedRoute
  ) {
    this.activatedRoute.queryParams.subscribe(params => {
      if (params['busId']) {
        this.route.busId = +params['busId'];
      }
    });
  }

  ngOnInit(): void {
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset()); // UTC to local
    this.minDateTime = now.toISOString().slice(0, 16); // 'yyyy-MM-ddTHH:mm'
  }

  addRoute(form: NgForm): void {
    if (form.invalid) {
      alert('Please fix form errors before submitting.');
      return;
    }

    const departure = new Date(this.route.departureTime);
    const now = new Date();

    if (departure < now) {
      alert('Departure time cannot be in the past.');
      return;
    }

    this.routeService.add(this.route).subscribe({
      next: (response) => {
        alert('Route added successfully!');
        this.router.navigate(['/seat-add'], {
          queryParams: {
            busId: response.busId,
            routeId: response.routeId
          }
        });
      },
      error: (err) => {
        console.error(err);
        alert('Error adding route. Please try again.');
      }
    });
  }
}
