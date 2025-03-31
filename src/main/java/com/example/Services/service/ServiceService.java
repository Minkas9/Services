package com.example.Services.service;

import com.example.Services.model.Services;
import com.example.Services.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceService {
    private static final String CACHE_NAME = "services";
    private final ServiceRepository serviceRepository;
    private final CacheManager cacheManager;

    public List<Services> getAllServices() {
        log.info("Fetching all services");
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null && cache.get("all") != null) {
            log.info("Returning services from cache");
            return (List<Services>) cache.get("all").get();
        }

        List<Services> services = serviceRepository.findAll();
        log.info("Found {} services in database", services.size());
        if (cache != null) {
            cache.put("all", services);
        }
        return services;
    }

    public Services addService(Services service) {
        log.info("Adding new service: {}", service.getName());
        validateService(service);
        Services savedService = serviceRepository.save(service);
        log.info("Service saved with ID: {}", savedService.getId());
        clearCache();
        return savedService;
    }

    public void deleteServiceById(Long id) {
        log.info("Attempting to delete service with ID: {}", id);
        if (!serviceRepository.existsById(id)) {
            log.error("Service with ID {} not found", id);
            throw new IllegalArgumentException("Service with id " + id + " not found");
        }
        serviceRepository.deleteById(id);
        log.info("Service with ID {} deleted successfully", id);
        clearCache();
    }

    public Services getService(Long id) {
        log.info("Fetching service with ID: {}", id);
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null && cache.get(id) != null) {
            log.info("Returning service from cache");
            return (Services) cache.get(id).get();
        }

        Optional<Services> service = serviceRepository.findById(id);
        service.ifPresent(s -> {
            if (cache != null) {
                cache.put(id, s);
            }
        });
        log.info("Service found: {}", service.isPresent());
        return service.orElse(null);
    }

    public Services updateService(Services service) {
        log.info("Updating service with ID: {}", service.getId());
        if (!serviceRepository.existsById(service.getId())) {
            log.error("Service with ID {} not found", service.getId());
            throw new IllegalArgumentException("Service with id " + service.getId() + " not found");
        }
        validateService(service);
        Services updatedService = serviceRepository.save(service);
        log.info("Service updated successfully");
        clearCache();
        return updatedService;
    }

    private void validateService(Services service) {
        if (service == null) {
            log.error("Service cannot be null");
            throw new IllegalArgumentException("Service cannot be null");
        }
        if (!StringUtils.hasText(service.getName())) {
            log.error("Service name cannot be empty");
            throw new IllegalArgumentException("Service name cannot be empty");
        }
    }

    private void clearCache() {
        log.info("Clearing service cache");
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.clear();
        }
    }
}