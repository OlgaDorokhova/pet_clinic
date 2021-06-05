package com.vet24.web.controllers.pet.clinicalexamination;

import com.vet24.models.dto.pet.clinicalexamination.ClinicalExaminationDto;
import com.vet24.models.dto.pet.reproduction.ReproductionDto;
import com.vet24.models.exception.BadRequestException;
import com.vet24.models.mappers.pet.clinicalexamination.ClinicalExaminationMapper;
import com.vet24.models.pet.Pet;
import com.vet24.models.pet.clinicalexamination.ClinicalExamination;
import com.vet24.models.pet.reproduction.Reproduction;
import com.vet24.models.user.Client;
import com.vet24.models.user.Doctor;
import com.vet24.service.pet.PetService;
import com.vet24.service.pet.clinicalexamination.ClinicalExaminationService;
import com.vet24.service.user.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

@RestController
@RequestMapping("/api/doctor/pet/{petId}/exam")
public class ClinicalExaminationRestController {

    private final PetService petService;
    private final ClinicalExaminationService clinicalExaminationService;
    private final ClinicalExaminationMapper clinicalExaminationMapper;
    private final DoctorService doctorService;

    public ClinicalExaminationRestController(PetService petService,
                                             ClinicalExaminationService clinicalExaminationService,
                                             ClinicalExaminationMapper clinicalExaminationMapper,
                                             DoctorService doctorService) {
        this.petService = petService;
        this.clinicalExaminationService = clinicalExaminationService;
        this.clinicalExaminationMapper = clinicalExaminationMapper;
        this.doctorService = doctorService;
    }

    @GetMapping("/{examinationId}")
    public ResponseEntity<ClinicalExaminationDto> getById(@PathVariable Long petId,
                                                          @PathVariable Long examinationId) {
        ClinicalExamination clinicalExamination = clinicalExaminationService.getByKey(examinationId);
        Doctor doctor = doctorService.getCurrentDoctor();
        Pet pet = petService.getByKey(petId);

        if (pet == null){
            throw new NotFoundException("pet not found");
        }
        if (clinicalExamination == null) {
            throw new NotFoundException("clinical examination not found");
        }
        if (!clinicalExamination.getPet().getId().equals(pet.getId())) {
            throw new BadRequestException("clinical examination not assigned to this pet");
        }

        ClinicalExaminationDto clinicalExaminationDto =
                clinicalExaminationMapper.clinicalExaminationToClinicalExaminationDto(clinicalExamination);
        return new ResponseEntity<>(clinicalExaminationDto, HttpStatus.OK);
    }


    @PostMapping("")
    public ResponseEntity<ClinicalExaminationDto> save(@PathVariable Long petId,
                                                @RequestBody ClinicalExaminationDto clinicalExaminationDto) {
        Pet pet = petService.getByKey(petId);
        ClinicalExamination clinicalExamination =
                clinicalExaminationMapper.clinicalExaminationDtoToClinicalExamination(clinicalExaminationDto);
        Doctor doctor = doctorService.getCurrentDoctor();

        if (pet == null) {
            throw new NotFoundException("pet not found");
        }
        if (!pet.getClient().getId().equals(doctor.getId())) {
            throw new BadRequestException("pet not yours");
        }

        clinicalExamination.setId(null);
        clinicalExaminationService.persist(clinicalExamination);

        pet.addClinicalExamination(clinicalExamination);
        petService.update(pet);

        return new ResponseEntity<>(clinicalExaminationMapper.clinicalExaminationToClinicalExaminationDto(clinicalExamination),
                HttpStatus.CREATED);
    }
}
