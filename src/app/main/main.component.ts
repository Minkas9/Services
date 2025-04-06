import { Component, OnInit } from '@angular/core';
import { ApiService } from '../services/api.service';
import { Service } from '../models/service.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
  services: Service[] = [];
  isAdmin: boolean = false;
  newService: Service = {
    name: '',
    description: ''
  };
  editingService: Service | null = null;
  error: string = '';
  success: string = '';

  constructor(private apiService: ApiService, private router: Router) { }

  ngOnInit(): void {
    this.isAdmin = this.apiService.isAdmin();
    this.loadServices();
  }

  loadServices(): void {
    this.apiService.getAllServices().subscribe({
      next: (data) => {
        this.services = data;
      },
      error: (error) => {
        this.error = 'Error loading services: ' + error.message;
      }
    });
  }

  addService(): void {
    this.apiService.addService(this.newService).subscribe({
      next: (data) => {
        this.services.push(data);
        this.newService = { name: '', description: '' };
        this.success = 'Service added successfully';
      },
      error: (error) => {
        this.error = 'Error adding service: ' + error.message;
      }
    });
  }

  startEdit(service: Service): void {
    this.editingService = { ...service };
  }

  cancelEdit(): void {
    this.editingService = null;
  }

  updateService(): void {
    if (this.editingService && this.editingService.id) {
      this.apiService.updateService(this.editingService.id, this.editingService).subscribe({
        next: (data) => {
          const index = this.services.findIndex(s => s.id === data.id);
          if (index !== -1) {
            this.services[index] = data;
          }
          this.editingService = null;
          this.success = 'Service updated successfully';
        },
        error: (error) => {
          this.error = 'Error updating service: ' + error.message;
        }
      });
    }
  }

  deleteService(id: number): void {
    console.log('Deleting service with ID:', id);
    console.log('Is admin:', this.isAdmin);
    this.apiService.deleteService(id).subscribe({
      next: () => {
        console.log('Service deleted successfully');
        this.services = this.services.filter(s => s.id !== id);
        this.success = 'Service deleted successfully';
      },
      error: (error) => {
        console.error('Error deleting service:', error);
        this.error = 'Error deleting service: ' + error.message;
      }
    });
  }

  logout(): void {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
