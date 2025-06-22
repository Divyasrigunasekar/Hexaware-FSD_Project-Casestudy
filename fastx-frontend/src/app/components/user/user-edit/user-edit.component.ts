import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'src/app/models/user.model';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html'
})
export class UserEditComponent implements OnInit {
  userId!: number;
  user: User = {
    name: '',
    email: '',
    password: '',
    gender: '',
    contactNumber: '',
    role: ''
  };
  successMessage = '';
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
  this.userId = Number(this.route.snapshot.paramMap.get('id'));

  this.userService.getUserById(this.userId).subscribe({
    next: (data) => {
      this.user = data;
      this.user.password = ''; // Clear password so user can type new one or leave blank
    },
    error: (err) => {
      console.error(err);
      this.errorMessage = 'User not found!';
    }
  });
}

  updateUser(): void {
    this.userService.updateUser(this.userId, this.user).subscribe({
      next: () => {
        this.successMessage = 'User updated successfully!';
        setTimeout(() => this.router.navigate(['/users/list']), 1500);
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Update failed. Try again.';
      }
    });
  }
  
}
