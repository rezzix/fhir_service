package net.rezzix.fhirservice.config;

import org.springframework.beans.factory.annotation.Value;

public class CodingValidation {
	@Value("${coding.specialities.validation}")
    private String specialitiesValidation;

}
