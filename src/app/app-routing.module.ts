import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from './components/register/register.component';
import { LoginComponent } from './components/login/login.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { OperatorDashboardComponent } from './components/operator-dashboard/operator-dashboard.component';
import { UserDashboardComponent } from './components/user-dashboard/user-dashboard.component';
import { AuthGuard } from './guards/auth.guard';
import { BusAddComponent } from './components/bus/bus-add/bus-add.component';
import { BusEditComponent } from './components/bus/bus-edit/bus-edit.component';
import { BusListComponent } from './components/bus/bus-list/bus-list.component';
import { RouteAddComponent } from './components/route/route-add/route-add.component';
import { RouteEditComponent } from './components/route/route-edit/route-edit.component';
import { RouteListComponent } from './components/route/route-list/route-list.component';
import { BookingAddComponent } from './components/booking/booking-add/booking-add.component';
import { BookingEditComponent } from './components/booking/booking-edit/booking-edit.component';
import { BookingListComponent } from './components/booking/booking-list/booking-list.component';
import { PaymentAddComponent } from './components/payment/payment-add/payment-add.component';
import { PaymentEditComponent } from './components/payment/payment-edit/payment-edit.component';
import { PaymentListComponent } from './components/payment/payment-list/payment-list.component';
import { SeatAddComponent } from './components/seat/seat-add/seat-add.component';
import { SeatEditComponent } from './components/seat/seat-edit/seat-edit.component';
import { SeatListComponent } from './components/seat/seat-list/seat-list.component';
import { UserProfileComponent } from './components/user/user-profile/user-profile.component';
import { UserEditComponent } from './components/user/user-edit/user-edit.component';
import { UserListComponent } from './components/user/user-list/user-list.component';
import { HomeComponent } from './components/home/home.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { CancellationAddComponent } from './components/cancellation/cancellation-add/cancellation-add.component';
import { CancellationListComponent } from './components/cancellation/cancellation-list/cancellation-list.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  // Protected by role
  { path: 'admin-dashboard', component: AdminDashboardComponent, canActivate: [AuthGuard], data: { role: 'ADMIN' } },
  { path: 'operator-dashboard', component: OperatorDashboardComponent, canActivate: [AuthGuard], data: { role: 'OPERATOR' } },
  { path: 'user-dashboard', component: UserDashboardComponent, canActivate: [AuthGuard], data: { role: 'USER' } },
  //for CRUD operations
  { path: 'bus-list', component: BusListComponent },
  { path: 'bus-add', component: BusAddComponent },
  { path: 'bus-edit/:id', component:BusEditComponent },
  { path: 'route-list', component: RouteListComponent },
  { path: 'route-add', component: RouteAddComponent },
  { path: 'route-edit/:id', component: RouteEditComponent },
  { path: 'seat-list', component: SeatListComponent },
  { path: 'seat-add', component: SeatAddComponent },
  { path: 'seat-edit/:id', component: SeatEditComponent },
  { path: 'booking-list', component: BookingListComponent },
  { path: 'booking-add', component: BookingAddComponent },
  { path: 'booking-edit/:id', component: BookingEditComponent },
  { path: 'payment-list', component: PaymentListComponent },
  { path: 'payment-add', component: PaymentAddComponent },
  { path: 'payment-edit/:id', component: PaymentEditComponent },
  { path: 'users/profile', component: UserProfileComponent },
  { path: 'users/list', component: UserListComponent,canActivate: [AuthGuard], data: { role: 'ADMIN' }  },
  { path: 'users/edit/:id', component: UserEditComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'cancellations', component: CancellationListComponent },
  { path: 'cancellation-add', component: CancellationAddComponent },

  
  
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
