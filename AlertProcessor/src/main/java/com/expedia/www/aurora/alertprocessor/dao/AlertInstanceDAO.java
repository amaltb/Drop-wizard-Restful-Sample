package com.expedia.www.aurora.alertprocessor.dao;

import com.expedia.www.aurora.alertprocessor.entity.AlertInstance;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import javax.persistence.TemporalType;
import java.sql.Timestamp;
import java.util.List;

public class AlertInstanceDAO extends AbstractDAO<AlertInstance> {

    public AlertInstanceDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }


    @SuppressWarnings("unchecked")
    public List<AlertInstance> findByTemplate(String templateAliasName, Timestamp startTime, Timestamp endTime)
    {
        return list(
                namedQuery(AlertInstance.FIND_BY_TEMPLATE_NAME)
                        .setParameter("templateAliasName", templateAliasName)
                        .setParameter("startTime", startTime, TemporalType.TIMESTAMP)
                        .setParameter("endTime", endTime, TemporalType.TIMESTAMP));
    }


}

