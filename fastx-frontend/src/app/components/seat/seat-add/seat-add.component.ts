import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Seat } from 'src/app/models/seat.model';
import { SeatService } from 'src/app/services/seat.service';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-seat-add',
  templateUrl: './seat-add.component.html',
  styleUrls: ['./seat-add.component.css']
})
export class SeatAddComponent {
  seat: Seat = {
    routeId: 0,
    busId: 0,
    seatNumber: '',
    isBooked: false
  };

  constructor(private seatService: SeatService, private router: Router, private activatedRoute: ActivatedRoute) {
  this.activatedRoute.queryParams.subscribe(params => {
    if (params['busId']) {
      this.seat.busId = +params['busId'];
    }
    if (params['routeId']) {
      this.seat.routeId = +params['routeId'];
    }
     
  });
}
  onAdd(form: NgForm) {
    if (form.invalid) return;

    this.seatService.add(this.seat).subscribe({
      next: () => {
        alert('Seat added successfully!');
        this.router.navigate(['/seat-list']);
      },
      error: (err) => {
        console.error(err);
        alert('Failed to add seat.');
      }
    });
  }
}
