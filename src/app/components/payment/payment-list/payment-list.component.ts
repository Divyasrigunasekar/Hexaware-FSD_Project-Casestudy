import { Component, OnInit } from '@angular/core';
import { PaymentService } from 'src/app/services/payment.service';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-payment-list',
  templateUrl: './payment-list.component.html'
})
export class PaymentListComponent implements OnInit {
  payments: any[] = [];
  filteredPayments: any[] = [];
  searchTerm: string = '';
  userRole: string | null = null;
  userId: number | null = null;

  constructor(
    private paymentService: PaymentService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.userRole = this.authService.getRole();
    this.userId = this.authService.getUserId();
    this.loadPayments();
  }

  loadPayments(): void {
    const processData = (data: any[]) => {
      this.payments = this.convertPaymentTimes(data);
      this.filterPayments();
    };

    if (this.userRole === 'ADMIN') {
      this.paymentService.getAll().subscribe({
        next: processData,
        error: (err) => {
          console.error('Error loading payments', err);
          alert('Failed to load payments.');
        }
      });
    } else if (this.userRole === 'USER' && this.userId !== null) {
      this.paymentService.getByUserId(this.userId).subscribe({
        next: processData,
        error: (err) => {
          console.error('Error loading user payments', err);
          alert('Failed to load payments.');
        }
      });
    }
  }

  convertPaymentTimes(payments: any[]): any[] {
    return payments.map(payment => ({
      ...payment,
      paymentTime: payment.paymentTime ? new Date(payment.paymentTime) : null
    }));
  }

  filterPayments(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredPayments = this.payments.filter(payment =>
      payment.paymentId.toString().includes(term) ||
      payment.bookingId.toString().includes(term) ||
      (payment.paymentMethod && payment.paymentMethod.toLowerCase().includes(term)) ||
      (payment.paymentStatus && payment.paymentStatus.toLowerCase().includes(term)) ||
      (payment.amount?.toString().includes(term))
    );
  }

  editPayment(paymentId: number): void {
    this.router.navigate(['/payment-edit', paymentId]);
  }

  deletePayment(paymentId: number): void {
    if (confirm(`Are you sure you want to delete payment ID ${paymentId}?`)) {
      this.paymentService.delete(paymentId).subscribe({
        next: () => {
          alert('Payment deleted successfully!');
          this.loadPayments();
        },
        error: (err) => {
          console.error('Error deleting payment', err);
          alert('Failed to delete payment.');
        }
      });
    }
  }
}
