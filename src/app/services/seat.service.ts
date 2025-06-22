import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Seat } from '../models/seat.model';

@Injectable({ providedIn: 'root' })
export class SeatService {
  private baseUrl = 'http://localhost:9090/api/seats';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Seat[]> {
    return this.http.get<Seat[]>(this.baseUrl);
  }

  getById(id: number): Observable<Seat> {
    return this.http.get<Seat>(`${this.baseUrl}/${id}`);
  }

  add(seat: Seat): Observable<Seat> {
    return this.http.post<Seat>(this.baseUrl, seat);
  }

  update(id: number, seat: Seat): Observable<Seat> {
    return this.http.put<Seat>(`${this.baseUrl}/${id}`, seat);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getavailableSeatsByRoute(routeId: number): Observable<Seat[]> {
    return this.http.get<Seat[]>(`${this.baseUrl}/available/${routeId}`);
  }

  getSeatsByRoute(routeId: number): Observable<{ seatNumber: string, booked: boolean }[]> {
  return this.http.get<{ seatNumber: string, booked: boolean }[]>(`${this.baseUrl}/all/${routeId}`);
}
}
