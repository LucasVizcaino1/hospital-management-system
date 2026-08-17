package com.hospital_management_system.demo.mapper;

import com.hospital_management_system.demo.dto.request.PersonRequestDto;
import com.hospital_management_system.demo.dto.response.PersonResponseDto;
import com.hospital_management_system.demo.model.Person;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    Person toEntity(PersonRequestDto dto);

    PersonResponseDto toResponse(Person entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(PersonRequestDto dto, @MappingTarget Person entity);
}
