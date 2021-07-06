package com.vet24.web.controllers.pet.clinicalexamination;

import com.vet24.models.dto.exception.ExceptionDto;
import com.vet24.models.dto.pet.PetDto;
import com.vet24.models.dto.pet.clinicalexamination.ClinicalExaminationDto;
import com.vet24.models.exception.BadRequestException;
import com.vet24.models.mappers.pet.clinicalexamination.ClinicalExaminationMapper;
import com.vet24.models.pet.Pet;
import com.vet24.models.pet.clinicalexamination.ClinicalExamination;
import com.vet24.models.user.Doctor;
import com.vet24.service.pet.PetService;
import com.vet24.service.pet.clinicalexamination.ClinicalExaminationService;
import com.vet24.service.user.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/doctor/exam")
public class ClinicalExaminationController {

    private final PetService petService;
    private final ClinicalExaminationService clinicalExaminationService;
    private final ClinicalExaminationMapper clinicalExaminationMapper;
    private final DoctorService doctorService;

    public ClinicalExaminationController(PetService petService, ClinicalExaminationService clinicalExaminationService,
                                         ClinicalExaminationMapper clinicalExaminationMapper, //почему то появилась ошибка
                                         DoctorService doctorService) {
        this.petService = petService;
        this.clinicalExaminationService = clinicalExaminationService;
        this.clinicalExaminationMapper = clinicalExaminationMapper;
        this.doctorService = doctorService;
    }

//для получения доктора после прикрутки секьюрити

//    public Doctor getCurrentDoctor(){
//        return  (Doctor) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    }

    @Operation(
            summary = "get clinical examination by id",
            description = "is looking for one clinical examination for a unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ok",
                    content = @Content(schema = @Schema(implementation = ClinicalExaminationDto.class))),
            @ApiResponse(responseCode = "400", description = "incorrect query input is specified",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "404", description = "clinical examination or pet with " +
                    "this id not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
    })
    @GetMapping("/{examinationId}")
    public ResponseEntity<ClinicalExaminationDto> getById(@PathVariable Long examinationId) {
        ClinicalExamination clinicalExamination = clinicalExaminationService.getByKey(examinationId);
        Doctor doctor = doctorService.getCurrentDoctor();
        Pet pet = clinicalExamination.getPet();

        if (clinicalExamination == null) {
            throw new NotFoundException("clinical examination not found");
        }

        ClinicalExaminationDto clinicalExaminationDto =
                clinicalExaminationMapper.toDto(clinicalExamination);
        return new ResponseEntity<>(clinicalExaminationDto, HttpStatus.OK);
    }


    @Operation(
            summary = "add new clinical examination",
            description = "to add a new clinical exam, enter the pet ID and fill in the fields: wight, isCanMove, text.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "clinical examination successful " +
                    "created",
                    content = @Content(schema = @Schema(implementation = ClinicalExaminationDto.class))),
            @ApiResponse(responseCode = "404", description = "pet with this id not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping("")
    public ResponseEntity<ClinicalExaminationDto> save(@RequestBody ClinicalExaminationDto clinicalExaminationDto) {
        Pet pet = petService.getByKey(clinicalExaminationDto.getPetId());
        ClinicalExamination clinicalExamination =
                clinicalExaminationMapper.toEntity(clinicalExaminationDto);
        if (pet == null) {
            throw new NotFoundException("pet not found");
        }
        clinicalExamination.setId(null);
        clinicalExamination.setDoctor(doctorService.getCurrentDoctor());
        clinicalExamination.setDate(LocalDate.now());

        clinicalExaminationService.persist(clinicalExamination);
        pet.setWeight(clinicalExamination.getWeight());
        clinicalExamination.setPet(petService.getByKey(clinicalExaminationDto.getPetId()));

        pet.addClinicalExamination(clinicalExamination);
        petService.update(pet);
        return new ResponseEntity<>(clinicalExaminationMapper.toDto(clinicalExamination), HttpStatus.CREATED);
    }

    @Operation(
            summary = "update clinical examination by id",
            description = "enter pet ID and clinical exam ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "clinical examination successful " +
                    "updated",
                    content = @Content(schema = @Schema(implementation = ClinicalExaminationDto.class))),
            @ApiResponse(responseCode = "404", description = "clinical examination or pet with " +
                    "this id not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "400", description = "clinical examination not assigned " +
                    "to this pet OR \n" +
                    "examinationId in path and in body not equals OR \npet has no doctor",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
    })
    @PutMapping("/{examinationId}")
    public ResponseEntity<ClinicalExaminationDto> update(@PathVariable Long examinationId,
                                                         @RequestBody ClinicalExaminationDto clinicalExaminationDto) {
        Pet pet = petService.getByKey(clinicalExaminationDto.getPetId());
        ClinicalExamination clinicalExamination = clinicalExaminationService.getByKey(examinationId);
        if (pet == null) {
            throw new NotFoundException("pet not found");
        }
        Doctor doctor = doctorService.getCurrentDoctor();
        if (doctor == null) {
            throw new NotFoundException("there is no doctor assigned to this pet");
        }

        if (clinicalExamination == null) {
            throw new NotFoundException("clinical examination not found");
        }

        if (!clinicalExamination.getPet().getId().equals(pet.getId())) {
            throw new BadRequestException("clinical examination not assigned to this pet");
        }
        if (!examinationId.equals(clinicalExaminationDto.getId())) {
            throw new BadRequestException("examinationId in path and in body not equals");
        }

        clinicalExamination.setDoctor(doctor);
        clinicalExamination.setDate(LocalDate.now());
        pet.setWeight(clinicalExaminationDto.getWeight());
        clinicalExamination.setDate(LocalDate.now());
        clinicalExamination =
                clinicalExaminationMapper.toEntity(clinicalExaminationDto);
        clinicalExamination.setPet(pet);
        clinicalExaminationService.update(clinicalExamination);

        return new ResponseEntity<>(clinicalExaminationMapper.toDto(clinicalExamination), HttpStatus.OK);

    }


    @Operation(
            summary = "delete clinical examination by id",
            description = "enter a unique ID of the pet's clinical examination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "clinical examination successful deleted"),
            @ApiResponse(responseCode = "404", description = "clinical examination not found",
                    content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    @DeleteMapping(value = "/{examinationId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long examinationId) {

        ClinicalExamination clinicalExamination = clinicalExaminationService.getByKey(examinationId);

        if (clinicalExamination == null) {
            throw new NotFoundException("clinical examination not found");
        }
        clinicalExaminationService.delete(clinicalExamination);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}