package com.expedia.www.aurora.alertprocessor.app;



import com.expedia.www.aurora.alertprocessor.conf.HelloWorldConfiguration;
import com.expedia.www.aurora.alertprocessor.dao.AlertInstanceDAO;
import com.expedia.www.aurora.alertprocessor.dao.AlertTypeDAO;
import com.expedia.www.aurora.alertprocessor.dao.AlertTypeDAOProxy;
import com.expedia.www.aurora.alertprocessor.entity.*;
import com.expedia.www.aurora.alertprocessor.exception.RuntimeExceptionMapper;
import com.expedia.www.aurora.alertprocessor.healthcheck.TemplateHealthCheck;
import com.expedia.www.aurora.alertprocessor.resource.AlertResource;
import com.expedia.www.aurora.alertprocessor.resource.HelloWorldResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.ServerProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author _amal
 */

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

    public static void main(String[] args) throws Exception {
        new HelloWorldApplication().run(args);
    }

    private HibernateBundle<HelloWorldConfiguration> hibernate = new HibernateBundle<HelloWorldConfiguration>
            (AlertInstance.class, AlertKeySet.class, AlertModelState.class,
                    AlertSubscription.class, AlertSubscriptionChannel.class, AlertType.class,
                    AlgoParameter.class, AlgorithmDefinition.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(HelloWorldConfiguration helloWorldConfiguration) {
            return helloWorldConfiguration.getDataSourceFactory();
        }
    };

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
        super.initialize(bootstrap);
    }

    @Override
    public void run(HelloWorldConfiguration helloWorldConfiguration, Environment environment)
    {
        final AlertTypeDAO alertTypeDAO = new AlertTypeDAO(hibernate.getSessionFactory());

        /* This is a way to access drop-wizard hibernate session outside resource class. Using this
          one can use the entityDAO layer outisde drop-wizard resources... **/
        final AlertTypeDAOProxy alertTypeDAOProxy = new UnitOfWorkAwareProxyFactory(hibernate)
                .create(AlertTypeDAOProxy.class, AlertTypeDAO.class, alertTypeDAO);

        List<AlertType> alertTypes = alertTypeDAOProxy.getAllAlertTypes();

        final AlertInstanceDAO alertInstanceDAO = new AlertInstanceDAO(hibernate.getSessionFactory());
        final AlertResource alertResource = new AlertResource(alertInstanceDAO, alertTypes);

        final HelloWorldResource resource = new HelloWorldResource(helloWorldConfiguration.getTemplate(),
                helloWorldConfiguration.getDefaultName());

        final TemplateHealthCheck check = new TemplateHealthCheck(helloWorldConfiguration.getTemplate());

        environment.healthChecks().register("template", check);

        /* To add handler for each exception raised by application. This is to return meaningful
         message back to user in case of any exceptions thrown by the application. */
        environment.jersey().register(RuntimeExceptionMapper.class);

        /* For generating application.wadl xml. (XML describing all application resources.. available at
         /api/application.wadl) */
        final Map<String, Object> properties = new HashMap<>();
        properties.put(ServerProperties.WADL_FEATURE_DISABLE, false);
        environment.jersey().getResourceConfig().addProperties(properties);

        environment.jersey().register(resource);
        environment.jersey().register(alertResource);
    }
}
