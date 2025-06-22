import { Component, OnInit } from '@angular/core';
import { PaymentService } from 'src/app/services/payment.service';

@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent implements OnInit {
  userName: string | null = '';
  payments: any[] = [];
  userId: number | null = null;
  showProfile = false;

  constructor(private paymentService: PaymentService) {}

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (token) {
      const decoded: any = JSON.parse(atob(token.split('.')[1]));
      this.userName = decoded.sub;
      this.userId = decoded.userId ? Number(decoded.userId) : null;

      if (this.userId !== null) {
        this.loadPayments(this.userId);
      }
    }
  }

  loadPayments(userId: number): void {
    this.paymentService.getByUserId(userId).subscribe({
      next: (data) => {
        // Ensure paymentTime is a Date object
        this.payments = data.map(payment => ({
          ...payment,
          paymentTime: payment.paymentTime ? new Date(payment.paymentTime) : null
        }));
      },
      error: (err) => {
        console.error('Error fetching payments for user', err);
      }
    });
  }

  toggleProfile(): void {
    this.showProfile = !this.showProfile;
  }
}

