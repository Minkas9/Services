package com.example.Services.controller;

import com.example.Services.model.Services;
import com.example.Services.service.ServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<Services>> getAllServices() {
        log.info("Received request to get all services");
        List<Services> services = serviceService.getAllServices();
        log.info("Returning {} services", services.size());
        return ResponseEntity.ok(services);
    }

    @PostMapping("/add")
    public ResponseEntity<Services> addService(@RequestBody Services service) {
        log.info("Received request to add service: {}", service.getName());
        Services createdService = serviceService.addService(service);
        log.info("Service created with ID: {}", createdService.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteServiceById(@PathVariable Long id) {
        log.info("Received request to delete service with ID: {}", id);
        if (serviceService.getService(id) == null) {
            log.error("Service with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not found");
        }
        serviceService.deleteServiceById(id);
        log.info("Service with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Services> getService(@PathVariable Long id) {
        log.info("Received request to get service with ID: {}", id);
        Services service = serviceService.getService(id);
        if (service == null) {
            log.error("Service with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.info("Returning service: {}", service.getName());
        return ResponseEntity.ok(service);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Services> updateService(@PathVariable Long id, @RequestBody Services service) {
        log.info("Received request to update service with ID: {}", id);
        Services existingService = serviceService.getService(id);

        if (existingService == null) {
            log.error("Service with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        service.setId(id);
        Services updatedService = serviceService.updateService(service);
        log.info("Service with ID {} updated successfully", id);
        return ResponseEntity.ok(updatedService);
    }
}
