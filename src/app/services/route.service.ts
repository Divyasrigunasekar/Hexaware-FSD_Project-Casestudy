import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Route } from '../models/route.model';


@Injectable({ providedIn: 'root' })
export class RouteService {
  private baseUrl = 'http://localhost:9090/api/routes';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Route[]> {
    return this.http.get<Route[]>(this.baseUrl);
  }

  getById(id: number): Observable<Route> {
    return this.http.get<Route>(`${this.baseUrl}/${id}`);
  }

  add(route: Route): Observable<Route> {
    return this.http.post<Route>(this.baseUrl, route);
  }

  update(id: number, route: Route): Observable<Route> {
    return this.http.put<Route>(`${this.baseUrl}/${id}`, route);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getFare(routeId: number): Observable<number> {
  return this.http.get<number>(`${this.baseUrl}/${routeId}/fare`);
}
}

