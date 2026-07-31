package com.hospital_management_system.demo.controller;

import com.hospital_management_system.demo.dto.request.PersonRequestDto;
import com.hospital_management_system.demo.dto.response.PersonResponseDto;
import com.hospital_management_system.demo.model.State;
import com.hospital_management_system.demo.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
@Slf4j
public class PersonController {

    private final PersonService personService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDIC')")
    @Operation(summary = "Create a new person")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Person created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })


    @PostMapping
    public ResponseEntity<PersonResponseDto> createPerson(@Valid @RequestBody PersonRequestDto requestDto) {
        log.info("Create person request received: {}", requestDto);
        PersonResponseDto response = personService.createPerson(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDIC')")
    @Operation(summary = "List all persons (paginated)")
    @GetMapping
    public ResponseEntity<Page<PersonResponseDto>> getAllPersons(@ParameterObject Pageable pageable) {
        log.info("Listing all persons, page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(personService.getAllPersons(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDIC')")
    @Operation(summary = "Get a person by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<PersonResponseDto> getPersonById(@PathVariable Long id) {
        log.info("Getting person with id={}", id);
        return ResponseEntity.ok(personService.getPersonById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDIC')")
    @Operation(summary = "List persons by state (paginated)")
    @GetMapping("/state/{state}")
    public ResponseEntity<Page<PersonResponseDto>> getPersonsByState(
            @PathVariable State state,
            @ParameterObject Pageable pageable) {
        log.info("Listing persons with state={}", state);
        return ResponseEntity.ok(personService.getPersonsByState(state, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MEDIC')")
    @Operation(summary = "Search persons by name (paginated)")
    @GetMapping("/search")
    public ResponseEntity<Page<PersonResponseDto>> searchByName(
            @RequestParam String name,
            @ParameterObject Pageable pageable) {
        log.info("Searching persons with name={}", name);
        return ResponseEntity.ok(personService.searchByName(name, pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a person")
    @PutMapping("/{id}")
    public ResponseEntity<PersonResponseDto> updatePerson(
            @PathVariable Long id,
            @Valid @RequestBody PersonRequestDto requestDto) {
        log.info("Update person request for id={}: {}", id, requestDto);
        return ResponseEntity.ok(personService.updatePerson(id, requestDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a person")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        log.info("Delete person request for id={}", id);
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }
}