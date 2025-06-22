import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Payment } from '../models/payment.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private baseUrl = 'http://localhost:9090/api/payments';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Payment[]> {
    return this.http.get<Payment[]>(this.baseUrl);
  }

  getById(id: number): Observable<Payment> {
    return this.http.get<Payment>(`${this.baseUrl}/${id}`);
  }

  add(payment: Payment): Observable<Payment> {
    return this.http.post<Payment>(this.baseUrl, payment);
  }

  update(id: number, payment: Payment): Observable<Payment> {
    return this.http.put<Payment>(`${this.baseUrl}/${id}`, payment);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getByUserId(userId: number): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.baseUrl}/user/${userId}`);
  }

  refund(id: number): Observable<string> {
    return this.http.put<string>(`${this.baseUrl}/refund/${id}`, {});
  }

  addPayment(payment: any): Observable<any> {
  return this.http.post(`${this.baseUrl}`, payment);
}
}

