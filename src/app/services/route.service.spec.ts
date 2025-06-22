import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouteService } from './route.service';
import { Route } from '../models/route.model';

describe('RouteService', () => {
  let service: RouteService;
  let httpMock: HttpTestingController;

  const mockRoute: Route = {
    busId: 1,
    origin: 'City A',
    destination: 'City B',
    departureTime: '10:00',
    arrivalTime: '12:00',
    fare: 200
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule], 
      providers: [RouteService]
    });

    service = TestBed.inject(RouteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); //  Ensure no outstanding requests
  });

  it('should add a route and return it', () => {
    service.add(mockRoute).subscribe((response) => {
      expect(response).toEqual(mockRoute);
    });

    const req = httpMock.expectOne('http://localhost:9090/api/routes');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRoute);

    req.flush(mockRoute); // Send mock response
  });
});
