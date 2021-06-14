package com.vet24.models.dto.pet.procedure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.vet24.models.enums.ProcedureType;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
public class EchinococcusDto extends ProcedureDto {

    @JsonCreator
    public EchinococcusDto(Long id, LocalDate date, Long medicineId,
                           String medicineBatchNumber, Boolean isPeriodical, Integer periodDays) {
        super(id, date, ProcedureType.ECHINOCOCCUS, medicineId, medicineBatchNumber, isPeriodical, periodDays);
    }
}
