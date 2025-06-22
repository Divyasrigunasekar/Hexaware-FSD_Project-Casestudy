export interface Route {
  routeId?: number;
  origin: string;
  destination: string;
  departureTime: string;
  arrivalTime: string;
  fare: number;
  busId?: number; 
}
