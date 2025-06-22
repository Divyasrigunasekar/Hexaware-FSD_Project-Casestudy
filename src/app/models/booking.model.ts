export interface Booking {
  bookingId?: number; // Optional for new bookings
  userId: number;
  routeId: number;
  bookingTime: string; // LocalDateTime from backend is ISO string in frontend
  seatsBooked: number;
  seatNumbers: string[];
  totalAmount: number;
  status: string;
  paymentId?: number;
}