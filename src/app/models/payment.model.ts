export interface Booking {
  bookingId: number;
  // add other booking properties if needed
}

export interface Payment {
  paymentId?: number;
  amount?: number;
  paymentMethod: string;
  paymentStatus?: string;
  paymentTime: Date;
  bookingId: number;
}
