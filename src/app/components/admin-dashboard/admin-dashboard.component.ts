import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent {
  adminName: string | null = '';
  showProfile = false;


  ngOnInit(): void {
    const stored = localStorage.getItem('token');
    if (stored) {
      const decoded: any = JSON.parse(atob(stored.split('.')[1]));
      this.adminName = decoded.sub; // or whatever you stored as username/email
    }
  }
  toggleProfile(): void {
    this.showProfile = !this.showProfile;
  }
}
