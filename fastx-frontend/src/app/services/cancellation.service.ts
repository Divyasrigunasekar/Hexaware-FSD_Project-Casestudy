import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Cancellation, CancellationRequestDTO, CancellationResponseDTO } from '../models/cancellation.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CancellationService {
  private baseUrl = 'http://localhost:9191/api/cancellation';

  constructor(private http: HttpClient) {}

  cancelBooking(data: CancellationRequestDTO): Observable<CancellationResponseDTO> {
    return this.http.post<CancellationResponseDTO>(`${this.baseUrl}`, data);
  }

  getMyCancellations(): Observable<Cancellation[]> {
    return this.http.get<Cancellation[]>(`${this.baseUrl}`);
  }

  getAll(): Observable<Cancellation[]> {
    return this.http.get<Cancellation[]>(`${this.baseUrl}/all`);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  deleteMy(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/my/${id}`);
  }
}
