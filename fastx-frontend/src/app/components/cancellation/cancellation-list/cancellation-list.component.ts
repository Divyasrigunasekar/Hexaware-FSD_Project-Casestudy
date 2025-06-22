import { Component, OnInit } from '@angular/core';
import { Cancellation } from 'src/app/models/cancellation.model';
import { CancellationService } from 'src/app/services/cancellation.service';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-cancellation-list',
  templateUrl: './cancellation-list.component.html'
})
export class CancellationListComponent implements OnInit {
  cancellations: Cancellation[] = [];
  isAdmin = false;

  constructor(
    private cancellationService: CancellationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.getRole() === 'ADMIN';
    this.loadCancellations();
  }

  loadCancellations(): void {
    if (this.isAdmin) {
      this.cancellationService.getAll().subscribe({
        next: data => this.cancellations = data,
        error: err => alert('Failed to load cancellations')
      });
    } else {
      this.cancellationService.getMyCancellations().subscribe({
        next: data => this.cancellations = data,
        error: err => alert('Failed to load your cancellations')
      });
    }
  }

  delete(id: number): void {
    if (!confirm('Are you sure you want to delete this cancellation?')) return;

    const del$ = this.isAdmin
      ? this.cancellationService.delete(id)
      : this.cancellationService.deleteMy(id);

    del$.subscribe({
      next: () => {
        alert('Deleted successfully!');
        this.loadCancellations();
      },
      error: err => alert('Delete failed: ' + err.message)
    });
  }
}
