import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  role: string | null = '';

constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.role = this.authService.getRole();

    if (!this.role) {
      this.router.navigate(['/login']); // Prevent unauthorized access
    }
  }
}
