import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Interceptor that automatically adds the JWT token to all HTTP requests
 * This centralizes authentication logic and ensures all API calls include the token
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor() {}

  /**
   * Intercepts all HTTP requests and adds the JWT token if available
   * @param request - The outgoing HTTP request
   * @param next - The HTTP handler to pass the request to
   * @returns An Observable of the HTTP event
   */
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Get the token from localStorage
    const token = localStorage.getItem('token');
    
    // If a token exists, clone the request and add the Authorization header
    if (token) {
      const authReq = request.clone({
        headers: request.headers.set('Authorization', `Bearer ${token}`)
      });
      return next.handle(authReq);
    }
    
    // If no token exists, proceed with the original request
    return next.handle(request);
  }
} 