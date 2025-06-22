export interface Cancellation {
  cancellationId?: number;
  bookingId: number;
  paymentId: number;
  userId?: number;
  seatIds?: string;
  reason: string;
  cancelledAt?: string;
}

export interface CancellationRequestDTO {
  bookingId: number;
  paymentId: number;
  seatIds: number[];
  reason: string;
}

export interface CancellationResponseDTO {
  message: string;
}
