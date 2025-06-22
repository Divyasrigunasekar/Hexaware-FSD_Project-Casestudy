import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Bus } from 'src/app/models/bus.model';
import { BusService } from 'src/app/services/bus.service';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-bus-edit',
  templateUrl: './bus-edit.component.html',
  styleUrls: ['./bus-edit.component.css']
})
export class BusEditComponent implements OnInit {
  busId!: number;
  bus: Bus = {
    busName: '',
    busNumber: '',
    busType: '',
    totalSeats: 0,
    amenities: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private busService: BusService
  ) {}

  ngOnInit(): void {
    this.busId = +this.route.snapshot.paramMap.get('id')!;
    this.busService.getById(this.busId).subscribe({
      next: (data) => this.bus = data,
      error: (err) => {
        console.error(err);
        alert('Failed to load bus');
      }
    });
  }

  onUpdate(form: NgForm): void {
    if (form.invalid) return;

    this.busService.update(this.busId, this.bus).subscribe({
      next: () => {
        alert('Bus updated successfully!');
        this.router.navigate(['/bus-list']);
      },
      error: (err) => {
        console.error(err);
        alert('Update failed');
      }
    });
  }
}