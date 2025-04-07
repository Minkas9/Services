package com.example.Services.controller;

import com.example.Services.dto.ServiceDto;
import com.example.Services.dto.ServiceResponseDto;
import com.example.Services.mapper.ServiceMapper;
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

/**
 * Controller responsible for managing service-related operations.
 * This controller provides endpoints for:
 * - Retrieving services (for all authenticated users)
 * - Adding new services (admin only)
 * - Updating existing services (admin only)
 * - Deleting services (admin only)
 * 
 * All endpoints require JWT authentication except those explicitly marked as
 * public.
 */
@Slf4j
@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
@Tag(name = "Services", description = "Service management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class ServiceController {

    // Service for business logic related to services
    private final ServiceService serviceService;
    // Mapper for converting between entity and DTOs
    private final ServiceMapper serviceMapper;

    /**
     * Retrieves all available services.
     * This endpoint is accessible to all authenticated users.
     * The response is cached for better performance.
     *
     * @return ResponseEntity containing a list of services
     */
    @Operation(summary = "Get all services", description = "Retrieves a list of all available services")
    @GetMapping
    public ResponseEntity<List<ServiceResponseDto>> getAllServices() {
        log.info("Received request to get all services");
        // Get services from the service layer (may be cached)
        List<Services> services = serviceService.getAllServices();
        // Convert entities to DTOs for the response
        List<ServiceResponseDto> dtos = serviceMapper.toDtoList(services);
        log.info("Returning {} services", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Adds a new service to the system.
     * This endpoint is restricted to users with ADMIN role.
     *
     * @param serviceDto DTO containing service details
     * @return ResponseEntity with the created service
     */
    @Operation(summary = "Add new service", description = "Creates a new service (Admin only)")
    @PostMapping("/add")
    public ResponseEntity<ServiceResponseDto> addService(
            @Parameter(description = "Service details", required = true) @RequestBody ServiceDto serviceDto) {
        log.info("Received request to add service: {}", serviceDto.getName());
        // Convert DTO to entity
        Services service = serviceMapper.toEntity(serviceDto);
        // Save the service
        Services savedService = serviceService.addService(service);
        // Convert back to DTO for response
        ServiceResponseDto responseDto = serviceMapper.toDto(savedService);
        log.info("Service created with ID: {}", responseDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * Deletes a service by its ID.
     * This endpoint is restricted to users with ADMIN role.
     *
     * @param id ID of the service to delete
     * @return ResponseEntity with no content if successful
     */
    @Operation(summary = "Delete service", description = "Deletes a service by ID (Admin only)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteServiceById(
            @Parameter(description = "Service ID", required = true) @PathVariable Long id) {
        log.info("Received request to delete service with ID: {}", id);
        // Check if service exists
        if (serviceService.getService(id) == null) {
            log.error("Service with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not found");
        }
        // Delete the service
        serviceService.deleteServiceById(id);
        log.info("Service with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a specific service by its ID.
     * This endpoint is accessible to all authenticated users.
     *
     * @param id ID of the service to retrieve
     * @return ResponseEntity containing the requested service
     */
    @Operation(summary = "Get service by ID", description = "Retrieves a specific service by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDto> getService(
            @Parameter(description = "Service ID", required = true) @PathVariable Long id) {
        log.info("Received request to get service with ID: {}", id);
        // Get the service from the service layer
        Services service = serviceService.getService(id);
        // Check if service exists
        if (service == null) {
            log.error("Service with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // Convert to DTO for response
        ServiceResponseDto responseDto = serviceMapper.toDto(service);
        log.info("Returning service: {}", responseDto.getName());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Updates an existing service.
     * This endpoint is restricted to users with ADMIN role.
     *
     * @param id         ID of the service to update
     * @param serviceDto DTO containing updated service details
     * @return ResponseEntity with the updated service
     */
    @Operation(summary = "Update service", description = "Updates an existing service (Admin only)")
    @PutMapping("/update/{id}")
    public ResponseEntity<ServiceResponseDto> updateService(
            @Parameter(description = "Service ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated service details", required = true) @RequestBody ServiceDto serviceDto) {
        log.info("Received request to update service with ID: {}", id);
        // Check if service exists
        Services existingService = serviceService.getService(id);

        if (existingService == null) {
            log.error("Service with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Convert DTO to entity and set the ID
        Services service = serviceMapper.toEntity(serviceDto);
        service.setId(id);
        // Update the service
        Services updatedService = serviceService.updateService(service);
        // Convert back to DTO for response
        ServiceResponseDto responseDto = serviceMapper.toDto(updatedService);
        log.info("Service with ID {} updated successfully", id);
        return ResponseEntity.ok(responseDto);
    }
}