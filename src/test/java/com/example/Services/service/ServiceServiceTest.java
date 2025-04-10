package com.example.Services.service;

import com.example.Services.model.Services;
import com.example.Services.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private ServiceService serviceService;

    private Services testService;

    @BeforeEach
    void setUp() {
        testService = new Services();
        testService.setId(1L);
        testService.setName("Test Service");
        testService.setDescription("Test Description");
    }

    @Test
    void getAllServices_ShouldReturnAllServices() {
        // Arrange
        List<Services> expectedServices = Arrays.asList(testService);
        when(serviceRepository.findAll()).thenReturn(expectedServices);
        when(cacheManager.getCache("services")).thenReturn(cache);
        when(cache.get("all")).thenReturn(null);

        // Act
        List<Services> actualServices = serviceService.getAllServices();

        // Assert
        assertNotNull(actualServices);
        assertEquals(expectedServices.size(), actualServices.size());
        assertEquals(expectedServices.get(0).getName(), actualServices.get(0).getName());
        verify(serviceRepository).findAll();
        verify(cache).put("all", expectedServices);
    }

    @Test
    void addService_ShouldSaveAndReturnService() {
        // Arrange
        when(serviceRepository.save(any(Services.class))).thenReturn(testService);
        when(cacheManager.getCache("services")).thenReturn(cache);

        // Act
        Services savedService = serviceService.addService(testService);

        // Assert
        assertNotNull(savedService);
        assertEquals(testService.getName(), savedService.getName());
        verify(serviceRepository).save(testService);
        verify(cache).clear();
    }

    @Test
    void addService_ShouldThrowException_WhenServiceIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> serviceService.addService(null));
        verify(serviceRepository, never()).save(any());
    }

    @Test
    void getService_ShouldReturnService_WhenExists() {
        // Arrange
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(testService));
        when(cacheManager.getCache("services")).thenReturn(cache);
        when(cache.get(1L)).thenReturn(null);

        // Act
        Services foundService = serviceService.getService(1L);

        // Assert
        assertNotNull(foundService);
        assertEquals(testService.getName(), foundService.getName());
        verify(serviceRepository).findById(1L);
        verify(cache).put(1L, testService);
    }

    @Test
    void getService_ShouldReturnNull_WhenNotExists() {
        // Arrange
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());
        when(cacheManager.getCache("services")).thenReturn(cache);
        when(cache.get(1L)).thenReturn(null);

        // Act
        Services foundService = serviceService.getService(1L);

        // Assert
        assertNull(foundService);
        verify(serviceRepository).findById(1L);
        verify(cache, never()).put(any(), any());
    }

    @Test
    void updateService_ShouldUpdateAndReturnService() {
        // Arrange
        when(serviceRepository.existsById(1L)).thenReturn(true);
        when(serviceRepository.save(any(Services.class))).thenReturn(testService);
        when(cacheManager.getCache("services")).thenReturn(cache);

        // Act
        Services updatedService = serviceService.updateService(testService);

        // Assert
        assertNotNull(updatedService);
        assertEquals(testService.getName(), updatedService.getName());
        verify(serviceRepository).existsById(1L);
        verify(serviceRepository).save(testService);
        verify(cache).clear();
    }

    @Test
    void updateService_ShouldThrowException_WhenServiceNotExists() {
        // Arrange
        when(serviceRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> serviceService.updateService(testService));
        verify(serviceRepository, never()).save(any());
    }

    @Test
    void deleteServiceById_ShouldDeleteService_WhenExists() {
        // Arrange
        when(serviceRepository.existsById(1L)).thenReturn(true);
        when(cacheManager.getCache("services")).thenReturn(cache);

        // Act
        serviceService.deleteServiceById(1L);

        // Assert
        verify(serviceRepository).existsById(1L);
        verify(serviceRepository).deleteById(1L);
        verify(cache).clear();
    }

    @Test
    void deleteServiceById_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(serviceRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> serviceService.deleteServiceById(1L));
        verify(serviceRepository, never()).deleteById(any());
    }
}