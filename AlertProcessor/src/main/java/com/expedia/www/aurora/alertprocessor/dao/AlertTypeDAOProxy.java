package com.expedia.www.aurora.alertprocessor.dao;


import com.expedia.www.aurora.alertprocessor.entity.AlertType;
import io.dropwizard.hibernate.UnitOfWork;

import java.util.List;

public class AlertTypeDAOProxy {
    private AlertTypeDAO alertTypeDAO;

    public AlertTypeDAOProxy(AlertTypeDAO alertTypeDAO) {
        this.alertTypeDAO = alertTypeDAO;
    }

    @UnitOfWork
    public List<AlertType> getAllAlertTypes()
    {
        return alertTypeDAO.findAll();
    }
}
