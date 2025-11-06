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
        // Map it specifically to FHIR-related paths to avoid conflicts with other requests
        ServletRegistrationBean<RestfulServer> registration = new ServletRegistrationBean<>(fhirServer, "/fhir/*");
        registration.setLoadOnStartup(1);
        // Set a lower order to ensure the default dispatcher servlet has higher priority
        // This ensures static resources and other endpoints are handled by Spring first
        registration.setOrder(1); 
        return registration;
    }
}