package com.example.Services.service;

import com.example.Services.model.Service;
import com.example.Services.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;


import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public Service addService(Service service) {
        return serviceRepository.save(service);
    }

    public void deleteServiceById(Long id) {
        serviceRepository.deleteById(id);
    }

    public Service getService(Long id) {
        return serviceRepository.findById(id).orElse(null);
    }

    public Service updateService(Service service) {
        return serviceRepository.save(service);
    }
}
