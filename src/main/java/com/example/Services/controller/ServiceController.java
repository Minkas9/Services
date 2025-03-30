package com.example.Services.controller;

import com.example.Services.model.Service;
import com.example.Services.service.ServiceService;
import lombok.RequiredArgsConstructor;
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
        return ResponseEntity.ok(serviceService.addService(service));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServiceById(@PathVariable Long id) {
        serviceService.deleteServiceById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<Service> getService(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getService(id));
    }

    @PutMapping("/update/")
    public ResponseEntity<Service> updateService(@RequestBody Service service) {
        return ResponseEntity.ok(serviceService.updateService(service));
    }
}
