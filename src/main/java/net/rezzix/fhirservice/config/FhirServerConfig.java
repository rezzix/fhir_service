package net.rezzix.fhirservice.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import net.rezzix.fhirservice.controller.TransactionProvider;

@Configuration
public class FhirServerConfig {

    @Bean
    public ServletRegistrationBean<RestfulServer> fhirServer(TransactionProvider transactionProvider) {
        // Create a new RestfulServer instance for FHIR R5.
        RestfulServer fhirServer = new RestfulServer(FhirContext.forR5());

        // Register the TransactionProvider so it can handle FHIR requests.
        fhirServer.setProviders(transactionProvider);

        // Add an interceptor for nicely formatted HTML responses in the browser.
        fhirServer.registerInterceptor(new ResponseHighlighterInterceptor());

        // Use Spring's ServletRegistrationBean to register the HAPI FHIR servlet
        // and map it to FHIR-specific paths only, leaving /api/** available for other controllers
        // Use a more specific mapping that doesn't conflict with Spring Boot's default servlet
        ServletRegistrationBean<RestfulServer> registration = new ServletRegistrationBean<>(fhirServer, "/fhir/*");
        registration.setLoadOnStartup(1);
        return registration;
    }
}