import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { UserDashboardComponent } from './components/user-dashboard/user-dashboard.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { OperatorDashboardComponent } from './components/operator-dashboard/operator-dashboard.component';
import { BusListComponent } from './components/bus/bus-list/bus-list.component';
import { BusAddComponent } from './components/bus/bus-add/bus-add.component';
import { BusEditComponent } from './components/bus/bus-edit/bus-edit.component';
import { RouteAddComponent } from './components/route/route-add/route-add.component';
import { RouteListComponent } from './components/route/route-list/route-list.component';
import { RouteEditComponent } from './components/route/route-edit/route-edit.component';
import { FormsModule } from '@angular/forms';
import { SeatAddComponent } from './components/seat/seat-add/seat-add.component';
import { SeatListComponent } from './components/seat/seat-list/seat-list.component';
import { SeatEditComponent } from './components/seat/seat-edit/seat-edit.component';
import { BookingAddComponent } from './components/booking/booking-add/booking-add.component';
import { BookingEditComponent } from './components/booking/booking-edit/booking-edit.component';
import { BookingListComponent } from './components/booking/booking-list/booking-list.component';
import { PaymentAddComponent } from './components/payment/payment-add/payment-add.component';
import { PaymentListComponent } from './components/payment/payment-list/payment-list.component';
import { PaymentEditComponent } from './components/payment/payment-edit/payment-edit.component';
import { UserProfileComponent } from './components/user/user-profile/user-profile.component';
import { UserListComponent } from './components/user/user-list/user-list.component';
import { UserEditComponent } from './components/user/user-edit/user-edit.component';
import { HomeComponent } from './components/home/home.component';

import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { RouterModule } from '@angular/router';

import { AuthGuard } from './guards/auth.guard';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';


import { CancellationAddComponent } from './components/cancellation/cancellation-add/cancellation-add.component';
import { CancellationListComponent } from './components/cancellation/cancellation-list/cancellation-list.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    DashboardComponent,
    NavbarComponent,
    UserDashboardComponent,
    AdminDashboardComponent,
    OperatorDashboardComponent,
    BusListComponent,
    BusAddComponent,
    BusEditComponent,
    RouteAddComponent,
    RouteListComponent,
    RouteEditComponent,
    SeatAddComponent,
    SeatListComponent,
    SeatEditComponent,
    BookingAddComponent,
    BookingEditComponent,
    BookingListComponent,
    PaymentAddComponent,
    PaymentListComponent,
    PaymentEditComponent,
    UserProfileComponent,
    UserListComponent,
    UserEditComponent,
    HomeComponent,
    ForgotPasswordComponent,
    CancellationListComponent,
    CancellationAddComponent
    
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    RouterModule
  ],
  exports: [RouterModule],
  providers: [
    AuthGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    }
    ],
  bootstrap: [AppComponent]
})
export class AppModule { }
