package com.example.Services.controller;

import com.example.Services.model.Service;
import com.example.Services.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service/")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @PostMapping("/add")
    public ResponseEntity<Service> addService(@RequestBody Service service) {
        Service createdService = serviceService.addService(service);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServiceById(@PathVariable Long id) {
        if (serviceService.getService(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not found"); // ✅ Return 404 if not found
        }
        serviceService.deleteServiceById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Service> getService(@PathVariable Long id) {
        Service service = serviceService.getService(id);
        return service != null ? ResponseEntity.ok(service) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Service> updateService(@RequestBody Service service) {
        if (service.getId() == null || serviceService.getService(service.getId()) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(serviceService.updateService(service));
    }
}
