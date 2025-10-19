package net.rezzix.fhirservice.config;

public class FhirServerConfig {
/*
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
    }*/
}