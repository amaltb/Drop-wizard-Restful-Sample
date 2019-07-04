package com.expedia.www.aurora.alertprocessor.resource;

import com.expedia.www.aurora.alertprocessor.dao.AlertInstanceDAO;
import com.expedia.www.aurora.alertprocessor.entity.AlertInstance;
import com.expedia.www.aurora.alertprocessor.entity.AlertType;
import com.expedia.www.aurora.alertprocessor.util.Request;
import com.expedia.www.aurora.alertprocessor.util.ApplicationUtil;
import io.dropwizard.hibernate.UnitOfWork;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Path("/alerts")
public class AlertResource {

    private AlertInstanceDAO alertInstanceDAO;
    private List<AlertType> alertTypes;

    public AlertResource(AlertInstanceDAO alertInstanceDAO, List<AlertType> alertTypes) {
        this.alertInstanceDAO = alertInstanceDAO;
        this.alertTypes = alertTypes;
    }

    @Path("/report")
    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<AlertInstance> findByTemplateName(@QueryParam("startTime") Optional<String> st,
                                                  @QueryParam("endTime") Optional<String> et,
                                                  @NotNull @Valid Request request)
    {
        Timestamp startTime = ApplicationUtil.validateTimeStamp(st);
        Timestamp endTime = ApplicationUtil.validateTimeStamp(et);

        final long diff = endTime.getTime() - startTime.getTime();

        if (diff > 24 * 60 * 60 * 1000){
            throw new RuntimeException("Support only a maximum of 1-day period.");
        }

        startTime = diff < 2000 ? new Timestamp(endTime.getTime() - 24 * 60 * 60 * 1000) : startTime;

        final String template_alias_name = request.getTemplateAliasName();

        List<AlertInstance> alertInstances = alertInstanceDAO.findByTemplate(template_alias_name, startTime, endTime);

        if(request.getKeySet() != null && !request.getKeySet().isEmpty())
        {
            /*alertInstances = ApplicationUtil.filterAlertInstances(alertInstances, request.getKeySet());*/
        }

        // TODO call a method to generate report based out of these alert instances...
        return alertInstances;
    }
}
