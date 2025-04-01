package com.example.Services.controller;

import com.example.Services.model.Services;
import com.example.Services.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j // ✅ Enables logging
@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
@Tag(name = "Services", description = "Service management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class ServiceController {

    private final ServiceService serviceService;

    @Operation(summary = "Get all services", description = "Retrieves a list of all available services")
    @GetMapping
    public ResponseEntity<List<Services>> getAllServices() {
        log.info("Received request to get all services");
        List<Services> services = serviceService.getAllServices();
        log.info("Returning {} services", services.size());
        return ResponseEntity.ok(services);
    }

    @Operation(summary = "Add new service", description = "Creates a new service (Admin only)")
    @PostMapping("/add")
    public ResponseEntity<Services> addService(
            @Parameter(description = "Service details", required = true) @RequestBody Services service) {
        log.info("Received request to add service: {}", service.getName());
        Services createdService = serviceService.addService(service);
        log.info("Service created with ID: {}", createdService.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
    }

    @Operation(summary = "Delete service", description = "Deletes a service by ID (Admin only)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteServiceById(
            @Parameter(description = "Service ID", required = true) @PathVariable Long id) {
        log.info("Received request to delete service with ID: {}", id);
        if (serviceService.getService(id) == null) {
            log.error("Service with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not found");
        }
        serviceService.deleteServiceById(id);
        log.info("Service with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get service by ID", description = "Retrieves a specific service by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<Services> getService(
            @Parameter(description = "Service ID", required = true) @PathVariable Long id) {
        log.info("Received request to get service with ID: {}", id);
        Services service = serviceService.getService(id);
        if (service == null) {
            log.error("Service with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.info("Returning service: {}", service.getName());
        return ResponseEntity.ok(service);
    }

    @Operation(summary = "Update service", description = "Updates an existing service (Admin only)")
    @PutMapping("/update/{id}")
    public ResponseEntity<Services> updateService(
            @Parameter(description = "Service ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated service details", required = true) @RequestBody Services service) {
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