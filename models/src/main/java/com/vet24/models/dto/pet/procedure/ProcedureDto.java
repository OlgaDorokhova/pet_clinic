package com.vet24.models.dto.pet.procedure;

import com.vet24.models.enums.ProcedureType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProcedureDto {
    Long id;
    LocalDate date; //if null or blank set now
    ProcedureType type;
    Long medicineId;
    String medicineBatchNumber;
    Boolean isPeriodical;
    Integer periodDays;
}
