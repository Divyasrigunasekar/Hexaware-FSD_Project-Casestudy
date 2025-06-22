import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Seat } from 'src/app/models/seat.model';
import { SeatService } from 'src/app/services/seat.service';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-seat-edit',
  templateUrl: './seat-edit.component.html',
  styleUrls: ['./seat-edit.component.css']
})
export class SeatEditComponent implements OnInit {
  seatId!: number;
  seat: Seat = {
    routeId: 0,
    busId: 0,
    seatNumber: '',
    isBooked: false
  };

  constructor(private route: ActivatedRoute, private seatService: SeatService, private router: Router) {}

  ngOnInit(): void {
    this.seatId = Number(this.route.snapshot.paramMap.get('id'));
    this.seatService.getById(this.seatId).subscribe({
      next: (data) => this.seat = data,
      error: () => alert('Failed to fetch seat details.')
    });
  }

  onUpdate(form: NgForm) {
    if (form.invalid) return;

    this.seatService.update(this.seatId, this.seat).subscribe({
      next: () => {
        alert('Seat updated successfully!');
        this.router.navigate(['/seat-list']);
      },
      error: () => alert('Failed to update seat.')
    });
  }
}
