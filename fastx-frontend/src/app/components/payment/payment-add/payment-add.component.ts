import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { PaymentService } from 'src/app/services/payment.service';
import { Router, ActivatedRoute } from '@angular/router';
import { BookingService } from 'src/app/services/booking.service';

@Component({
  selector: 'app-payment-add',
  templateUrl: './payment-add.component.html'
})
export class PaymentAddComponent implements OnInit {
  payment = {
    bookingId: 0,
    amount: 0,
    paymentMethod: '',
    paymentTime: new Date().toLocaleString('sv-SE', { timeZone: 'Asia/Kolkata' }).replace(' ', 'T'),
    paymentStatus: '',
  };

  constructor(
    private paymentService: PaymentService,
    private router: Router,
    private bookingService: BookingService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.payment.bookingId = +params['bookingId'] || 0;
      this.payment.amount = +params['amount'] || 0;

      // Set current time in ISO format for datetime-local input
      this.payment.paymentTime = new Date().toISOString().slice(0, 16);
    });
  }

  onSubmit(form: NgForm): void {
  if (form.invalid) return;

  // 🕒 Set current time in IST at the moment of submission
  const now = new Date();
  const istTime = new Date(now.toLocaleString("en-US", { timeZone: "Asia/Kolkata" }));

  const yyyy = istTime.getFullYear();
  const MM = String(istTime.getMonth() + 1).padStart(2, '0');
  const dd = String(istTime.getDate()).padStart(2, '0');
  const HH = String(istTime.getHours()).padStart(2, '0');
  const mm = String(istTime.getMinutes()).padStart(2, '0');
  const ss = String(istTime.getSeconds()).padStart(2, '0');

  this.payment.paymentTime = `${yyyy}-${MM}-${dd}T${HH}:${mm}:${ss}`;

  // Fetch booking info and submit payment
  this.bookingService.getById(this.payment.bookingId).subscribe({
    next: (booking) => {
      this.payment.amount = booking.totalAmount;
      this.payment.paymentStatus = 'SUCCESS';

      this.paymentService.addPayment(this.payment).subscribe({
        next: () => {
          alert('✅ Payment successful!');
          this.router.navigate(['/payment-list']);
        },
        error: (err) => {
          console.error('Payment error:', err);
          alert('❌ Failed to process payment.');
        }
      });
    },
    error: (err) => {
      console.error('Booking fetch error:', err);
      alert('⚠️ Invalid Booking ID. Cannot fetch amount.');
    }
  });


  }
}
