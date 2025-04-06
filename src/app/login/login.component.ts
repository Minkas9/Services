import {Component} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = "";
  password = "";
  error = "";

  constructor(private http: HttpClient, private router: Router) {
  }

  login() {
    //sukuriame konstanta su viena headerio reiksme. Authorization : Basic encodedCreds
    const headers = new HttpHeaders({
      Authorization: "Basic " + btoa(this.username + ":" + this.password)
    })

    this.http.get("http://localhost:8080/api/auth/login", {headers, responseType: 'text'})
      .subscribe({
        next: () => {
          localStorage.setItem("auth", btoa(this.username + ":" + this.password));
          console.log(localStorage.getItem("auth"));
          this.router.navigate(['/main']);
        },
        error: () => this.error = "Invalid credentials"
      });

  }

}
