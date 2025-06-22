import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PaymentService } from 'src/app/services/payment.service';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-payment-edit',
  templateUrl: './payment-edit.component.html'
})
export class PaymentEditComponent implements OnInit {
  paymentId!: number;
  payment: any = {
    bookingId: 0,
    paymentMethod: '',
    paymentTime: new Date(),
    paymentStatus: ''
  };

  constructor(
    private route: ActivatedRoute,
    private paymentService: PaymentService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.paymentId = +this.route.snapshot.params['id'];
    this.loadPayment();
  }

  loadPayment(): void {
    this.paymentService.getById(this.paymentId).subscribe({
      next: (data) => {
        this.payment = data;
        this.payment.paymentTime = new Date(this.payment.paymentTime); // Convert for form binding
      },
      error: (err) => {
        console.error('Error loading payment', err);
        alert('Failed to load payment.');
      }
    });
  }

  onUpdate(form: NgForm): void {
    if (form.invalid) {
      alert('Please fill all required fields correctly.');
      return;
    }

    this.paymentService.update(this.paymentId, this.payment).subscribe({
      next: () => {
        alert('Payment updated successfully!');
        this.router.navigate(['/payment-list']);
      },
      error: (err) => {
        console.error('Error updating payment', err);
        alert('Failed to update payment.');
      }
    });
  }
  cancel(): void {
  this.router.navigate(['/payment-list']);
}
}
