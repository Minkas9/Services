package com.example.Services.mapper;

import com.example.Services.dto.ServiceDto;
import com.example.Services.dto.ServiceResponseDto;
import com.example.Services.model.Services;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Service entities and DTOs.
 * This class handles the transformation of:
 * - ServiceDto to Services entity (for creating/updating services)
 * - Services entity to ServiceResponseDto (for API responses)
 * - List of Services to List of ServiceResponseDto
 * 
 * The mapper ensures proper separation between the API layer and the domain
 * model.
 */
@Component
public class ServiceMapper {

    /**
     * Converts a ServiceDto to a Services entity.
     * This method is used when creating or updating services from API requests.
     * 
     * @param dto The DTO containing service data from the API request
     * @return A new Services entity with data from the DTO, or null if dto is null
     */
    public Services toEntity(ServiceDto dto) {
        if (dto == null) {
            return null;
        }
        Services service = new Services();
        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        return service;
    }

    /**
     * Converts a Services entity to a ServiceResponseDto.
     * This method is used when returning service data in API responses.
     * 
     * @param service The Services entity to convert
     * @return A new ServiceResponseDto with data from the entity, or null if
     *         service is null
     */
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

    /**
     * Converts a list of Services entities to a list of ServiceResponseDto objects.
     * This method is used when returning multiple services in API responses.
     * 
     * @param services The list of Services entities to convert
     * @return A new list of ServiceResponseDto objects, or null if services is null
     */
    public List<ServiceResponseDto> toDtoList(List<Services> services) {
        if (services == null) {
            return null;
        }
        return services.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}