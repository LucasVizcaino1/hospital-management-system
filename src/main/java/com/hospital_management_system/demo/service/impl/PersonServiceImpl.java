package com.hospital_management_system.demo.service.impl;

import com.hospital_management_system.demo.dto.request.PersonRequestDto;
import com.hospital_management_system.demo.dto.response.PersonResponseDto;
import com.hospital_management_system.demo.exception.ResourceNotFoundException;
import com.hospital_management_system.demo.model.Person;
import com.hospital_management_system.demo.model.State;
import com.hospital_management_system.demo.repository.PersonRepository;
import com.hospital_management_system.demo.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Override
    @Transactional
    public PersonResponseDto createPerson(PersonRequestDto requestDto) {
        Person person = toEntity(requestDto);
        person = personRepository.save(person);
        log.info("Person created. id={}", person.getId());
        return toResponse(person);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonResponseDto getPersonById(Long id) {
        log.info("Getting person with id={}", id);
        return personRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonResponseDto> getAllPersons(Pageable pageable) {
        log.info("Listing all persons, page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return personRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonResponseDto> getPersonsByState(State state, Pageable pageable) {
        log.info("Listing persons by state={}", state);
        return personRepository.findByState(state, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonResponseDto> searchByName(String name, Pageable pageable) {
        log.info("Searching persons with name={}", name);
        return personRepository.searchByName(name, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersonResponseDto> findByEmail(String email) {
        log.info("Finding person by email={}", email);
        return personRepository.findByEmail(email).map(this::toResponse);
    }

    @Override
    @Transactional
    public PersonResponseDto updatePerson(Long id, PersonRequestDto requestDto) {
        log.info("Updating person with id={}", id);
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));

        updateEntity(person, requestDto);

        Person updated = personRepository.save(person);
        log.info("Person updated. id={}", updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deletePerson(Long id) {
        log.info("Deleting person. id={}", id);
        if (!personRepository.existsById(id)) {
            throw new ResourceNotFoundException("Person not found with id: " + id);
        }
        personRepository.deleteById(id);
    }


    private Person toEntity(PersonRequestDto dto) {
        if (dto == null) return null;

        Person person = new Person();
        person.setName(dto.getName());
        person.setLastname(dto.getLastname());
        person.setEmail(dto.getEmail());
        person.setState(dto.getState());
        return person;
    }

    private PersonResponseDto toResponse(Person entity) {
        if (entity == null) return null;

        PersonResponseDto dto = new PersonResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLastname(entity.getLastname());
        dto.setEmail(entity.getEmail());
        dto.setState(entity.getState());
        return dto;
    }

    private void updateEntity(Person person, PersonRequestDto dto) {
        if (person == null || dto == null) return;

        if (dto.getName() != null)     person.setName(dto.getName());
        if (dto.getLastname() != null) person.setLastname(dto.getLastname());
        if (dto.getEmail() != null)    person.setEmail(dto.getEmail());
        if (dto.getState() != null)    person.setState(dto.getState());
    }
}