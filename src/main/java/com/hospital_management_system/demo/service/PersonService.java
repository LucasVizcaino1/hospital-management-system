package com.hospital_management_system.demo.service;


import com.hospital_management_system.demo.dto.request.PersonRequestDto;
import com.hospital_management_system.demo.dto.response.PersonResponseDto;
import com.hospital_management_system.demo.model.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PersonService {
    PersonResponseDto createPerson(PersonRequestDto requestDto);

    PersonResponseDto getPersonById(Long id);

    Page<PersonResponseDto> getAllPersons(Pageable pageable);

    Page<PersonResponseDto> getPersonsByState(State state, Pageable pageable);

    Page<PersonResponseDto> searchByName(String name, Pageable pageable);

    Optional<PersonResponseDto> findByEmail(String email);

    PersonResponseDto updatePerson(Long id, PersonRequestDto requestDto);

    void deletePerson(Long id);
}
