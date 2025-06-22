Project Setup Instructions
This project has two main backend applications:
1. FastX Booking Service (Main App)
Backend service for user login, route, seat, bus, booking, and payment.
Frontend Angular app connects to this service.
To Run:
Make sure MySQL is running.
Create a database named fastx_booking.
Update DB credentials in application.properties.
Run the Spring Boot app
The server runs on http://localhost:9090.

2. FastX Cancellation Service (Microservice)
Handles cancellation logic.
To Run:
Create a separate database named fastx_cancellation.
Update its own application.properties with DB info.
Run the cancellation service 
It runs on http://localhost:9091.

3. Angular Frontend
UI for users, admins, and operators.
Connects to both services using API URLs.
To Run:
cd frontend
npm install
ng serve
Access at http://localhost:4200

Existing credentials
Email:divyasri@gmail.com
Password:divya123
Role:ADMIN
Email:Priya@gmail.com
Password:Priya123
Role:OPERATOR
Email:Vimala@gmail.com
Password:vimala123
Role:USER



