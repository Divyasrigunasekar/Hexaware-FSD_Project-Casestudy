import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { User } from 'src/app/models/user.model';
import { Router } from '@angular/router';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
user: User = {
    name: '',
    email: '',
    password: '',
    gender: '',
    contactNumber: '',
    role: ''
  };

  constructor(private authService: AuthService, private router: Router) {}

  onRegister(form: NgForm) {
  if (form.invalid) return;

  this.authService.register(this.user).subscribe({
    next: () => {
      alert('✅ Registration successful!');
      this.router.navigate(['/login']);
    },
    error: (err) => {
      if (err.error && typeof err.error === 'string') {
        // Specific check for known message
        if (err.error.includes('Email already exists')) {
          alert('❌ Email already exists. Please use another email.');
        } else {
          alert('❌ ' + err.error);
        }
      } else {
        alert('❌ Registration failed. Please try again.');
      }
    }
  });
}

  isValidContactNumber(contact: string): boolean {
  return /^[0-9]{10}$/.test(contact);
}
}
