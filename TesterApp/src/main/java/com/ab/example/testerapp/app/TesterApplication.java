package com.ab.example.testerapp.app;

import com.ab.example.testerapp.conf.TesterAppConfiguration;
import com.ab.example.testerapp.service.TesterAppService;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.client.ssl.TlsConfiguration;
import io.dropwizard.setup.Environment;

import javax.ws.rs.client.Client;
import java.io.File;

public class TesterApplication extends Application<TesterAppConfiguration> {

    public static void main(String[] args) throws Exception {
        new TesterApplication().run(args);
    }

    @Override
    public void run(TesterAppConfiguration testerAppConfiguration, Environment environment) throws Exception {

        final JerseyClientConfiguration clientConfiguration = testerAppConfiguration.getHttpClientConfiguration();
        final TlsConfiguration tlsConfiguration = new TlsConfiguration();
        tlsConfiguration.setTrustStorePath(new File("/Users/ambabu/Documents/PersonalDocuments/code-samples/DropWizardSample/TesterApp/src/main/resources/ssl/truststore.jks"));
        tlsConfiguration.setTrustStorePassword("alert_trust_store");
        clientConfiguration.setTlsConfiguration(tlsConfiguration);
        final Client client = new JerseyClientBuilder(environment)
                .using(clientConfiguration).build("tester-app");

        environment.lifecycle().manage(new TesterAppService(client, testerAppConfiguration));
    }
}
