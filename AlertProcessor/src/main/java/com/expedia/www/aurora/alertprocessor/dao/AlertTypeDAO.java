package com.expedia.www.aurora.alertprocessor.dao;


import com.expedia.www.aurora.alertprocessor.entity.AlertType;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

public class AlertTypeDAO extends AbstractDAO<AlertType> {

    public AlertTypeDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @SuppressWarnings("unchecked")
    public List<AlertType> findAll()
    {
        return list(namedQuery("com.ab.example.dropwizardsample.entity.AlertType.findAll"));
    }

    public long create(AlertType alertType)
    {
        return persist(alertType).getId();
    }
}
