package com.hospital_management_system.demo.mapper;

import com.hospital_management_system.demo.dto.request.AppointmentRequestDto;
import com.hospital_management_system.demo.dto.response.AppointmentResponseDto;
import com.hospital_management_system.demo.model.Appointment;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {PatientMapper.class, EmployeeMapper.class})
public interface AppointmentMapper {

    @Mapping(source = "dateAttention", target = "date")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "employee", ignore = true)
    Appointment toEntity(AppointmentRequestDto dto);

    @Mapping(source = "date", target = "dateAttention")
    AppointmentResponseDto toResponse(Appointment entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "dateAttention", target = "date")
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "employee", ignore = true)
    void update(AppointmentRequestDto dto, @MappingTarget Appointment entity);
}