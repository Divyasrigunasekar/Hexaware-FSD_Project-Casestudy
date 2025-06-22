import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { User } from 'src/app/models/user.model';
import { Router } from '@angular/router'; 

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html'
})
export class UserProfileComponent implements OnInit {
  user: User | null = null;
  showProfile = false;

  constructor(private userService: UserService,private router: Router) {}

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: data => this.user = data,
      error: err => console.error('Failed to fetch user:', err)
    });
  }
  goToEdit() {
  if (this.user) {
    this.router.navigate(['/users/edit', this.user.userId]);
  }
}
}
