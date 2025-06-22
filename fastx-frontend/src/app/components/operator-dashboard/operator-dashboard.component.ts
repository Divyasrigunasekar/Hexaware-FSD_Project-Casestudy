import { Component } from '@angular/core';

@Component({
  selector: 'app-operator-dashboard',
  templateUrl: './operator-dashboard.component.html',
  styleUrls: ['./operator-dashboard.component.css']
})
export class OperatorDashboardComponent {
  operatorName: string | null = '';
  showProfile = false;


  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (token) {
      const decoded: any = JSON.parse(atob(token.split('.')[1]));
      this.operatorName = decoded.sub;
    }
  }
  toggleProfile(): void {
    this.showProfile = !this.showProfile;
  }
}
