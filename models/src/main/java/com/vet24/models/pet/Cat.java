package com.vet24.models.pet;

import com.vet24.models.user.Role;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cat extends Pet{

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private PetContact petContact;

    public Cat() {
        super();
    }

    public Cat(String petName, PetContact petContact) {
        super(petName);
        this.petContact = petContact;
    }
}
