package com.vet24.models.mappers.pet.clinicalexamination;

import com.vet24.models.dto.pet.clinicalexamination.ClinicalExaminationDto;
import com.vet24.models.mappers.DtoMapper;
import com.vet24.models.mappers.EntityMapper;
import com.vet24.models.pet.clinicalexamination.ClinicalExamination;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClinicalExaminationMapper extends DtoMapper<ClinicalExamination, ClinicalExaminationDto>,
        EntityMapper<ClinicalExaminationDto, ClinicalExamination> {

    @Mapping(source = "pet.id", target = "petId")
    @Override
    ClinicalExaminationDto toDto(ClinicalExamination clinicalExamination);
}
