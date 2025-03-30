package com.example.Services.service;

import com.example.Services.model.Service;
import com.example.Services.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {
    private final ServiceRepository serviceRepository;
    private final CacheManager cacheManager;

    public List<Service> getAllServices() {
        Cache cache = cacheManager.getCache("services");
        if (cache != null && cache.get("all") != null) {
            return (List<Service>) cache.get("all").get();
        }

        List<Service> services = serviceRepository.findAll();
        if (cache != null) {
            cache.put("all", services);
        }
        return services;
    }

    public Service addService(Service service) {
        Service savedService = serviceRepository.save(service);
        clearCache();
        return savedService;
    }

    public void deleteServiceById(Long id) {
        serviceRepository.deleteById(id);
        clearCache();
    }

    public Service getService(Long id) {
        Cache cache = cacheManager.getCache("services");
        if (cache != null && cache.get(id) != null) {
            return (Service) cache.get(id).get();
        }

        Optional<Service> service = serviceRepository.findById(id);
        service.ifPresent(s -> {
            if (cache != null) {
                cache.put(id, s);
            }
        });
        return service.orElse(null);
    }

    public Service updateService(Service service) {
        Service updatedService = serviceRepository.save(service);
        clearCache();
        return updatedService;
    }

    private void clearCache() {
        Cache cache = cacheManager.getCache("services");
        if (cache != null) {
            cache.clear();
        }
    }
}