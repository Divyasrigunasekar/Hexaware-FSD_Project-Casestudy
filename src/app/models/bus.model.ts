export interface Bus {
  busId?: number; // Optional for new buses, required when editing
  busName: string;
  busNumber: string;
  busType: string;
  totalSeats: number;
  amenities?: string; // optional if not always required
}