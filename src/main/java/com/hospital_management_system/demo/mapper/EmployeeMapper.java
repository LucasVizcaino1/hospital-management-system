package com.hospital_management_system.demo.mapper;

import com.hospital_management_system.demo.dto.request.EmployeeRequestDto;
import com.hospital_management_system.demo.dto.response.EmployeeResponseDto;
import com.hospital_management_system.demo.model.Employee;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = PersonMapper.class)
public interface EmployeeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    Employee toEntity(EmployeeRequestDto dto);

    @Mapping(source = "person", target = "person")
    EmployeeResponseDto toResponse(Employee entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "person", ignore = true)
    void update(EmployeeRequestDto dto, @MappingTarget Employee entity);
}