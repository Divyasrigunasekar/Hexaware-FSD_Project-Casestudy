import { Component } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html'
})
export class ForgotPasswordComponent {
  email = '';
  newPassword = '';
  confirmPassword = '';
  successMessage = '';
  errorMessage = '';

  constructor(private userService: UserService, private router: Router) {}

  resetPassword(): void {
    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    this.userService.resetPassword(this.email, this.newPassword).subscribe({
      next: () => {
        this.successMessage = 'Password reset successful!';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.errorMessage = typeof err.error === 'string' ? err.error : 'Reset failed. Try again.';
      }
    });
  }
}
