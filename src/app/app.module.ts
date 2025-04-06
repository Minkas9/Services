import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { MainComponent } from './main/main.component';
import { ApiService } from './services/api.service';
import { AuthInterceptor } from './interceptors/auth.interceptor';

/**
 * The root module of the application
 * Configures all necessary imports, declarations, and providers
 */
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    MainComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule, // Required for making HTTP requests
    FormsModule // Required for form handling
  ],
  providers: [
    ApiService, // Service for API communication
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor, // Register the AuthInterceptor to handle all HTTP requests
      multi: true // Allows multiple interceptors to be registered
    }
  ],
  bootstrap: [AppComponent] // The root component to bootstrap
})
export class AppModule { }
