package net.rezzix.fhirservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ca.uhn.fhir.context.FhirContext;

@Configuration
public class FhirConfig {

    @Bean
    public FhirContext fhirContext() {
        // We are using FHIR R5
        return FhirContext.forR5();
    }
}