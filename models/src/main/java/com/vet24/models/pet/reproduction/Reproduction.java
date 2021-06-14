package com.vet24.models.pet.reproduction;

import com.vet24.models.pet.Pet;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reproduction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column
    private LocalDate estrusStart;

    @Column
    private LocalDate mating;

    @Column
    private LocalDate dueDate;

    @Column
    private Integer childCount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Pet pet;

    public Reproduction(LocalDate estrusStart, LocalDate mating, LocalDate dueDate, Integer childCount, Pet pet) {
        this.estrusStart = estrusStart;
        this.mating = mating;
        this.dueDate = dueDate;
        this.childCount = childCount;
        this.pet = pet;
    }
}
