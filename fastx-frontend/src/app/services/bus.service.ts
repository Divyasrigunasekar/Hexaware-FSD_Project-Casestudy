import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Bus } from '../models/bus.model';

@Injectable({ providedIn: 'root' })
export class BusService {
  private baseUrl = 'http://localhost:9090/api/buses';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Bus[]> {
    return this.http.get<Bus[]>(this.baseUrl);
  }

  getById(id: number): Observable<Bus> {
    return this.http.get<Bus>(`${this.baseUrl}/${id}`);
  }

  add(bus: Bus): Observable<Bus> {
    return this.http.post<Bus>(this.baseUrl, bus);
  }

  update(id: number, bus: Bus): Observable<Bus> {
    return this.http.put<Bus>(`${this.baseUrl}/${id}`, bus);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}