import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Booking } from '../models/booking.model';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private baseUrl = 'http://localhost:9090/api/bookings';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Booking[]> {
    return this.http.get<Booking[]>(this.baseUrl);
  }

  getById(id: number): Observable<Booking> {
    return this.http.get<Booking>(`${this.baseUrl}/${id}`);
  }

  add(booking: Booking): Observable<Booking> {
    return this.http.post<Booking>(this.baseUrl, booking);
  }

  update(id: number, booking: Booking): Observable<Booking> {
    return this.http.put<Booking>(`${this.baseUrl}/${id}`, booking);
  }

  cancel(id: number): Observable<Booking> {
    return this.http.put<Booking>(`${this.baseUrl}/${id}/cancel`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getByUserId(userId: number): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.baseUrl}/user/${userId}`);
  }
}
