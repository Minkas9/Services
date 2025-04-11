package com.example.Services.mapper;

import com.example.Services.dto.ServiceDto;
import com.example.Services.dto.ServiceResponseDto;
import com.example.Services.model.Services;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceMapper {

    public Services toEntity(ServiceDto dto) {
        if (dto == null) {
            return null;
        }
        Services service = new Services();
        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        return service;
    }

    public ServiceResponseDto toDto(Services service) {
        if (service == null) {
            return null;
        }
        ServiceResponseDto dto = new ServiceResponseDto();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        return dto;
    }

    public List<ServiceResponseDto> toDtoList(List<Services> services) {
        if (services == null) {
            return null;
        }
        return services.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}