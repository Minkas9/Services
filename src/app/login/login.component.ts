import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {ApiService} from "../services/api.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = "";
  password = "";
  error = "";

  constructor(private apiService: ApiService, private router: Router) {
  }

  login() {
    this.apiService.login(this.username, this.password).subscribe({
      next: (response) => {
        console.log('Login response:', response);
        localStorage.setItem("token", response.token);
        // Debug logging
        const token = response.token;
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          console.log('Token payload:', payload);
          console.log('Authorities:', payload.authorities);
          console.log('Is admin check:', this.apiService.isAdmin());
        } catch (error) {
          console.error('Error parsing token:', error);
        }
        this.router.navigate(['/main']);
      },
      error: (err) => {
        console.error("Login failed:", err);
        
        // Check if the error is due to a banned account
        if (err.status === 403 && err.error && err.error.error === "Account Banned") {
          this.error = err.error.message || "Your account has been banned";
        } else {
          this.error = "Invalid credentials";
        }
      }
    });
  }
}
