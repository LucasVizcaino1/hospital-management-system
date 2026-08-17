package com.hospital_management_system.demo.mapper;

import com.hospital_management_system.demo.dto.request.PatientRequestDto;
import com.hospital_management_system.demo.dto.response.PatientResponseDto;
import com.hospital_management_system.demo.model.Patient;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = PersonMapper.class)
public interface PatientMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "rol", ignore = true)
    Patient toEntity(PatientRequestDto dto);

    PatientResponseDto toResponse(Patient entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "rol", ignore = true)
    void update(PatientRequestDto dto, @MappingTarget Patient entity);
}