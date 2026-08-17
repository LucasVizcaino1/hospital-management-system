package com.hospital_management_system.demo.mapper;
import com.hospital_management_system.demo.dto.request.SpecialtyRequestDto;
import com.hospital_management_system.demo.dto.response.SpecialtyResponseDto;
import com.hospital_management_system.demo.model.Specialty;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring")
public interface SpecialtyMapper {
    Specialty toEntity(SpecialtyRequestDto dto);

    SpecialtyResponseDto toResponse(Specialty entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(SpecialtyRequestDto dto, @MappingTarget Specialty entity);
}
