import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Bus } from 'src/app/models/bus.model';
import { BusService } from 'src/app/services/bus.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-bus-add',
  templateUrl: './bus-add.component.html',
  styleUrls: ['./bus-add.component.css']
})
export class BusAddComponent {
  bus: Bus = {
    busName: '',
    busNumber: '',
    busType: '',
    totalSeats: 0,
    amenities: ''
  };

  constructor(private busService: BusService, private router: Router) {}

  onSubmit(form: NgForm) {
  if (form.invalid) return;

  this.busService.add(this.bus).subscribe({
    next: (response) => {
      alert('Bus added successfully!');
      this.router.navigate(['/route-add'], { queryParams: { busId: response.busId } });
    },
    error: (err) => {
      console.error(err);
      alert('Failed to add bus');
    }
  });
}
}
