import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Service } from '../models/service.model';
import { JwtResponse } from '../models/jwt-response.model';

/**
 * Service responsible for all API communication with the backend
 * Handles authentication, service CRUD operations, and role checking
 */
@Injectable({
  providedIn: 'root'
})
export class ApiService {
  // Base URL for all API requests, configured in environment files
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  /**
   * Authenticates a user with the backend
   * @param username - User's username
   * @param password - User's password
   * @returns Observable with JWT response containing token
   */
  login(username: string, password: string): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.apiUrl}/auth/login`, { username, password });
  }

  /**
   * Retrieves all services from the backend
   * Note: The JWT token is automatically added by the AuthInterceptor
   * @returns Observable with array of services
   */
  getAllServices(): Observable<Service[]> {
    return this.http.get<Service[]>(`${this.apiUrl}/service`);
  }

  /**
   * Retrieves a specific service by its ID
   * @param id - Service ID to retrieve
   * @returns Observable with the requested service
   */
  getServiceById(id: number): Observable<Service> {
    return this.http.get<Service>(`${this.apiUrl}/service/${id}`);
  }

  /**
   * Creates a new service (admin only)
   * @param service - Service object to create
   * @returns Observable with the created service
   */
  addService(service: Service): Observable<Service> {
    return this.http.post<Service>(`${this.apiUrl}/service/add`, service);
  }

  /**
   * Updates an existing service (admin only)
   * @param id - ID of the service to update
   * @param service - Updated service data
   * @returns Observable with the updated service
   */
  updateService(id: number, service: Service): Observable<Service> {
    return this.http.put<Service>(`${this.apiUrl}/service/update/${id}`, service);
  }

  /**
   * Deletes a service by ID (admin only)
   * @param id - ID of the service to delete
   * @returns Observable that completes when deletion is successful
   */
  deleteService(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/service/delete/${id}`);
  }

  /**
   * Checks if the current user has admin privileges
   * Decodes the JWT token from localStorage and checks for ROLE_ADMIN authority
   * @returns true if user is an admin, false otherwise
   */
  isAdmin(): boolean {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        // Decode the JWT token payload (second part of the token)
        const payload = JSON.parse(atob(token.split('.')[1]));
        console.log('Full JWT payload:', payload);
        
        // Check for authorities array in the token
        if (payload.authorities && Array.isArray(payload.authorities)) {
          console.log('Authorities:', payload.authorities);
          return payload.authorities.some((auth: string) => auth === 'ROLE_ADMIN');
        }
        
        // Fallback: If username is 'admin', consider it an admin
        if (payload.sub === 'admin') {
          console.log('Username is admin');
          return true;
        }
        
        return false;
      } catch (error) {
        console.error('Error parsing JWT token:', error);
        return false;
      }
    }
    return false;
  }
} 