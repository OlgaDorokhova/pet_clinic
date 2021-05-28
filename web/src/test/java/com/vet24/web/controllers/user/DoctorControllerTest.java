package com.vet24.web.controllers.user;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.vet24.models.dto.medicine.DiagnosisDto;
import com.vet24.models.dto.user.DoctorDto;
import com.vet24.models.mappers.user.DoctorMapper;
import com.vet24.models.user.Doctor;
import com.vet24.service.pet.PetService;
import com.vet24.service.user.DoctorService;
import com.vet24.web.ControllerAbstractIntegrationTest;
import com.vet24.web.controllers.medicine.DoctorController;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

@DBRider
public class DoctorControllerTest extends ControllerAbstractIntegrationTest {

    @Autowired
    DoctorService doctorService;
    @Autowired
    PetService petService;
    @Autowired
    private DoctorMapper doctorMapper;

    final String DOMAIN = "http://localhost:8090";

    @Test
    @DataSet(value = {"/datasets/registration-doctor.yml"}, cleanBefore = true)
    public void shouldBeNotFound()  {
       String diagnosis = "bla-bla-bla";

        Doctor doctor = doctorService.getCurrentDoctor();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(diagnosis, headers);
        ResponseEntity<DiagnosisDto> responseEntity =  testRestTemplate
                .exchange(DOMAIN+DoctorController.ADD_DIAGNOSIS_API, HttpMethod.POST,entity, DiagnosisDto.class,1001);
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

    }

    @Test
    @DataSet(value = {"/datasets/registration-doctor.yml"}, cleanBefore = true)
    public void shouldBeCreated()  {
        String diagnosis = "bla-bla-bla";

        Doctor doctor = doctorService.getCurrentDoctor();
        DoctorDto doctorDto = doctorMapper.doctorToDoctorDto(doctor);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(diagnosis, headers);
        ResponseEntity<DiagnosisDto> responseEntity =  testRestTemplate
                .exchange(DOMAIN+DoctorController.ADD_DIAGNOSIS_API, HttpMethod.POST,entity, DiagnosisDto.class,101);
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assert.assertEquals("bla-bla-bla",responseEntity.getBody().getDescription());
        Assert.assertEquals(doctor.getId(),responseEntity.getBody().getDoctor().getId());
        Assert.assertEquals(petService.getByKey(101L).getId(),responseEntity.getBody().getPet().getId());
        Assert.assertNotNull(responseEntity.getBody().getId());
       // Assert.assertFalse(doctorService.getCurrentDoctor().getDiagnoses().isEmpty());
        //Assert.assertEquals((Diagnosis)doctorService.getCurrentDoctor().getDiagnoses().stream().toArray()[0],
          //      responseEntity.getBody().getId());
    }

    private HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            setBasicAuth(username,password);
        }};
    }



}
