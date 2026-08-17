package com.hospital_management_system.demo.service.impl;

import com.hospital_management_system.demo.dto.request.PersonRequestDto;
import com.hospital_management_system.demo.dto.response.PersonResponseDto;
import com.hospital_management_system.demo.exception.ResourceNotFoundException;
import com.hospital_management_system.demo.mapper.PersonMapper;
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
    private final PersonMapper personMapper;


    @Override
    @Transactional
    public PersonResponseDto createPerson(PersonRequestDto requestDto) {
        Person person = personMapper.toEntity(requestDto);
        person = personRepository.save(person);
        log.info("Person created. id={}", person.getId());
        return personMapper.toResponse(person);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonResponseDto getPersonById(Long id) {
        log.info("Getting person with id={}", id);
        return personRepository.findById(id)
                .map(personMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonResponseDto> getAllPersons(Pageable pageable) {
        log.info("Listing all persons, page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return personRepository.findAll(pageable).map(personMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonResponseDto> getPersonsByState(State state, Pageable pageable) {
        log.info("Listing persons by state={}", state);
        return personRepository.findByState(state, pageable).map(personMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonResponseDto> searchByName(String name, Pageable pageable) {
        log.info("Searching persons with name={}", name);
        return personRepository.searchByName(name, pageable).map(personMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersonResponseDto> findByEmail(String email) {
        log.info("Finding person by email={}", email);
        return personRepository.findByEmail(email).map(personMapper::toResponse);
    }

    @Override
    @Transactional
    public PersonResponseDto updatePerson(Long id, PersonRequestDto requestDto) {
        log.info("Updating person with id={}", id);
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));

        personMapper.update(requestDto, person);

        Person updated = personRepository.save(person);
        log.info("Person updated. id={}", updated.getId());
        return personMapper.toResponse(updated);
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



}