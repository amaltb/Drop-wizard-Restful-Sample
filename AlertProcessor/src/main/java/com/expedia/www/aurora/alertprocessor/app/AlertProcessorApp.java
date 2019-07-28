package com.expedia.www.aurora.alertprocessor.app;


import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.expedia.www.aurora.alertprocessor.conf.AlertProcessorConfiguration;
import com.expedia.www.aurora.alertprocessor.dao.AlertInstanceDAO;
import com.expedia.www.aurora.alertprocessor.entity.*;
import com.expedia.www.aurora.alertprocessor.exception.GenericExceptionMapper;
import com.expedia.www.aurora.alertprocessor.output.AuroraEmailNotifier;
import com.expedia.www.aurora.alertprocessor.output.AuroraNotifier;
import com.expedia.www.aurora.alertprocessor.output.AuroraSNSNotifier;
import com.expedia.www.aurora.alertprocessor.processor.AuroraRequestExecutor;
import com.expedia.www.aurora.alertprocessor.resource.AlertResource;
import com.expedia.www.aurora.alertprocessor.util.ServiceMetrics;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.ServerProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author - _amal
 *
 * alert processor application entry point.
 */

public class AlertProcessorApp extends Application<AlertProcessorConfiguration> {

    public static void main(String[] args) throws Exception {
        new AlertProcessorApp().run(args);
    }

    private HibernateBundle<AlertProcessorConfiguration> hibernate = new HibernateBundle<AlertProcessorConfiguration>
            (AlertInstance.class, AlertKeyValue.class, AlertModelState.class, ModelStateParams.class,
                    AlertSubscription.class, AlertSubscriptionChannel.class, AlertType.class,
                    AlgoParameter.class, AlgorithmDefinition.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(AlertProcessorConfiguration alertProcessorConfiguration) {
            return alertProcessorConfiguration.getDataSourceFactory();
        }
    };

    @Override
    public void initialize(Bootstrap<AlertProcessorConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
        super.initialize(bootstrap);
    }

    /**
     * method responsible for starting the application.
     *
     * @param alertProcessorConfiguration
     * @param environment
     */
    @Override
    public void run(AlertProcessorConfiguration alertProcessorConfiguration, Environment environment)
    {
        final AlertInstanceDAO alertInstanceDAO = new AlertInstanceDAO(hibernate.getSessionFactory());
        final AlertResource alertResource = new AlertResource(alertInstanceDAO,
                initializeNotifiers(alertProcessorConfiguration));

        /* To add handler for each exception raised by application. This is to return meaningful
         message back to user in case of any exceptions thrown by the application. */
        environment.jersey().register(GenericExceptionMapper.class);

        /* For generating application.wadl xml. (XML describing all application resources.. available at
         /api/application.wadl) */
        final Map<String, Object> properties = new HashMap<>();
        properties.put(ServerProperties.WADL_FEATURE_DISABLE, false);
        environment.jersey().getResourceConfig().addProperties(properties);

        environment.lifecycle().manage(new ServiceMetrics());
        environment.jersey().register(alertResource);
    }

    private List<AuroraNotifier> initializeNotifiers(AlertProcessorConfiguration config) {
        /*final AuroraRequestExecutor auroraRequestExecutor = new AuroraRequestExecutor(
                Integer.parseInt(config.getRequestRetryCount()));
        final ArrayList<AuroraNotifier> notifiers = new ArrayList<>();

        final AmazonSimpleEmailService ses = AmazonSimpleEmailServiceClientBuilder.defaultClient();
        notifiers.add(new AuroraEmailNotifier(ses, auroraRequestExecutor));
        notifiers.add(new AuroraSNSNotifier(auroraRequestExecutor));

        new Thread(auroraRequestExecutor).start();
        return notifiers;*/
        return null;
    }
}
