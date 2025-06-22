import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { User } from 'src/app/models/user.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html'
})
export class UserListComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  searchTerm: string = '';

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void {
    if (this.userService.isAdmin()) {
      this.userService.getAllUsers().subscribe({
        next: data => {
          this.users = data;
          this.filteredUsers = data;
        },
        error: err => console.error('Failed to fetch users:', err)
      });
    } else {
      alert('Access Denied!');
      this.router.navigate(['/login']);
    }
  }

  onSearch(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredUsers = this.users.filter(user =>
      user.name.toLowerCase().includes(term) ||
      user.email.toLowerCase().includes(term) ||
      user.role.toLowerCase().includes(term)||
      user.gender.toLowerCase().includes(term)
    );
  }

  deleteUser(userId: number | undefined): void {
    if (!userId) return;
    if (confirm('Are you sure you want to delete this user?')) {
      this.userService.deleteUser(userId).subscribe({
        next: () => {
          alert('User deleted successfully!');
          this.users = this.users.filter(u => u.userId !== userId);
          this.onSearch(); // Re-filter after deletion
        },
        error: (err) => {
          console.error(err);
          alert('Delete failed. Try again.');
        }
      });
    }
  }
}
