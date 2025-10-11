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
    public FhirContext fhirContext() {
        return FhirContext.forR5();
    }

    @Bean
    public ServletRegistrationBean<RestfulServer> fhirServer(FhirContext fhirContext, TransactionProvider transactionProvider) {
        RestfulServer server = new RestfulServer(fhirContext);

        // *** THIS IS THE FIX ***
        // Pass the provider instance directly, not a list containing it.
        server.setProviders(transactionProvider);

        server.registerInterceptor(new ResponseHighlighterInterceptor());

        ServletRegistrationBean<RestfulServer> registrationBean = new ServletRegistrationBean<>();
        registrationBean.setServlet(server);
        registrationBean.addUrlMappings("/*");
        registrationBean.setLoadOnStartup(1);

        return registrationBean;
    }
}