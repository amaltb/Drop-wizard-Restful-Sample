package com.expedia.www.aurora.alertprocessor.resource;

import com.expedia.www.aurora.alertprocessor.dao.AlertInstanceDAO;
import com.expedia.www.aurora.alertprocessor.entity.AlertInstance;
import com.expedia.www.aurora.alertprocessor.exception.CustomException;
import com.expedia.www.aurora.alertprocessor.output.AuroraNotifier;
import com.expedia.www.aurora.alertprocessor.util.*;
import io.dropwizard.hibernate.UnitOfWork;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author - _amal
 *
 * Http resource for alert_instance CRUD and report generation.
 *
 * paths: /api/alerts/report
 */

@Path("/alerts")
public class AlertResource {

    private AlertInstanceDAO alertInstanceDAO;
    private List<AuroraNotifier> notifiers;

    public AlertResource(AlertInstanceDAO alertInstanceDAO, List<AuroraNotifier> notifiers) {
        this.alertInstanceDAO = alertInstanceDAO;
        this.notifiers = notifiers;
    }

    @Path("/report")
    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AlertReport findByTemplateName(@QueryParam("startTime") Optional<String> st,
                                          @QueryParam("endTime") Optional<String> et,
                                          @NotNull @Valid Request request) throws CustomException {

        final Map<Predicate, Object> validations = new HashMap<>();
        validations.put(Predicates.REQUEST_PREDICATE, request);

        if(!ApplicationUtil.validateRequest(validations))
        {
            throw new CustomException(HttpStatus.SC_BAD_REQUEST, "Invalid request. Check all parameters.");
        }

        Timestamp startTime = ApplicationUtil.getTimeStamp(st);
        Timestamp endTime = ApplicationUtil.getTimeStamp(et);

        final long diff = endTime.getTime() - startTime.getTime();

        if (diff > 24 * 60 * 60 * 1000){
            throw new CustomException(HttpStatus.SC_BAD_REQUEST, "Support only a maximum of 1-day period.");
        }

        startTime = diff < 2000 ? new Timestamp(endTime.getTime() - 24 * 60 * 60 * 1000) : startTime;
        final String template_alias_name = request.getTemplateAliasName();
        List<AlertInstance> alertInstances = alertInstanceDAO.findByTemplate(template_alias_name, startTime, endTime);

        String report = StringUtils.EMPTY;
        if(!alertInstances.isEmpty())
        {
            if(request.getKeySet() != null && !request.getKeySet().isEmpty()) {
                alertInstances = ApplicationUtil.filterAlertInstances(alertInstances, request.getKeySet());
            }

            // Generating a global subscription list from all the alert instances.
            final NotificationSubscription subscription = ApplicationUtil.getGlobalSubscriptionList(alertInstances);
            // Generating the alert report containing all alert instances.
            report = ApplicationUtil.generateReport(template_alias_name, startTime, endTime, request.getKeySet(), alertInstances);
            // Submitting report for notification to the queue.
            /*ApplicationUtil.notify(report, subscription, notifiers);*/
        }

        // Returning alert report as the API response in case needed.
        return new AlertReport(report);
    }
}
