export interface User {
  userId?: number; // Optional because it’s auto-generated
  name: string;
  email: string;
  password: string;
  gender: string;
  contactNumber: string;
  role: string; // 'USER', 'ADMIN', 'OPERATOR'
}