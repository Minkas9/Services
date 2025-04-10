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

/**
 * Service class for managing Services entities.
 * This class provides business logic for:
 * - Retrieving services (with caching)
 * - Adding new services
 * - Updating existing services
 * - Deleting services
 * 
 * It implements caching to improve performance for frequently accessed data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceService {
    // Name of the cache used for storing services
    private static final String CACHE_NAME = "services";
    // Repository for service data persistence
    private final ServiceRepository serviceRepository;
    // Cache manager for handling service caching
    private final CacheManager cacheManager;

    /**
     * Retrieves all services from the database or cache.
     * This method first checks if the services are in the cache.
     * If found in cache, returns the cached services.
     * If not found, retrieves from the database, stores in cache, and returns.
     * 
     * @return List of all services
     */
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

    /**
     * Adds a new service to the database.
     * This method validates the service, saves it to the database,
     * and clears the cache to ensure fresh data on next retrieval.
     * 
     * @param service The service to add
     * @return The saved service with generated ID
     * @throws IllegalArgumentException if service validation fails
     */
    public Services addService(Services service) {
        validateService(service);
        log.info("Adding new service: {}", service.getName());
        Services savedService = serviceRepository.save(service);
        log.info("Service saved with ID: {}", savedService.getId());
        clearCache();
        return savedService;
    }

    /**
     * Deletes a service by its ID.
     * This method checks if the service exists, deletes it from the database,
     * and clears the cache to ensure fresh data on next retrieval.
     * 
     * @param id The ID of the service to delete
     * @throws IllegalArgumentException if service with the given ID doesn't exist
     */
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

    /**
     * Retrieves a service by its ID from the database or cache.
     * This method first checks if the service is in the cache.
     * If found in cache, returns the cached service.
     * If not found, retrieves from the database, stores in cache, and returns.
     * 
     * @param id The ID of the service to retrieve
     * @return The service with the given ID, or null if not found
     */
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

    /**
     * Updates an existing service in the database.
     * This method validates the service, checks if it exists, saves the updates,
     * and clears the cache to ensure fresh data on next retrieval.
     * 
     * @param service The service with updated data
     * @return The updated service
     * @throws IllegalArgumentException if service validation fails or service
     *                                  doesn't exist
     */
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

    /**
     * Validates a service before saving.
     * This method checks if the service is null and if its name is not empty.
     * 
     * @param service The service to validate
     * @throws IllegalArgumentException if validation fails
     */
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

    /**
     * Clears the service cache.
     * This method is called after any operation that modifies services
     * to ensure fresh data on next retrieval.
     */
    private void clearCache() {
        log.info("Clearing service cache");
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.clear();
        }
    }
}