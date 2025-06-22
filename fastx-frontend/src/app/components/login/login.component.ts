import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginData = {
    email: '',
    password: ''
  };

  constructor(private authService: AuthService, private router: Router) {}

  onLogin(form: NgForm) {
    if (form.invalid) return;

    this.authService.login(this.loginData).subscribe({
      next: (response) => {
        // ✅ Store token + user object
        this.authService.storeToken(response.token);

        // ✅ Get role from storage
        const role = this.authService.getRole();

        if (!role) {
          alert('Unable to decode user role. Please try again.');
          return;
        }

        alert('Login successful!');

        // ✅ Navigate to role-based dashboard
        if (role === 'ADMIN') {
          this.router.navigate(['/admin-dashboard']);
        } else if (role === 'OPERATOR') {
          this.router.navigate(['/operator-dashboard']);
        } else if (role === 'USER') {
          this.router.navigate(['/user-dashboard']);
        } else {
          alert('Unknown role! Logging out.');
          this.authService.logout();
        }
      },
      error: (err) => {
        console.error('Login failed:', err);
        alert('Invalid credentials or server error!');
      }
    });
  }
}
