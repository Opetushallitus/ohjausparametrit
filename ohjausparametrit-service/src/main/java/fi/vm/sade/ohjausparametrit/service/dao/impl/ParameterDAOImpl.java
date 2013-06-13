/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 */
package fi.vm.sade.ohjausparametrit.service.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.ohjausparametrit.service.dao.ParameterDAO;
import fi.vm.sade.ohjausparametrit.service.model.Parameter;
import fi.vm.sade.ohjausparametrit.service.model.ParameterValue;
import fi.vm.sade.ohjausparametrit.service.model.QParameter;
import fi.vm.sade.ohjausparametrit.service.model.QParameterValue;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author mlyly
 */
@Component
public class ParameterDAOImpl implements ParameterDAO {

    private static final Logger LOG = LoggerFactory.getLogger(ParameterDAOImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Parameter> findAllParameters() {
        LOG.debug("findAllParameters()");

        QParameter p = QParameter.parameter;
        JPAQuery q = new JPAQuery(em).from(p);
        return q.list(p);
    }

    @Override
    public List<ParameterValue> findAllParameterValues() {
        LOG.debug("findAllParameterValues()");

        QParameterValue p = QParameterValue.parameterValue;
        JPAQuery q = new JPAQuery(em).from(p);
        return q.list(p);
    }

    @Override
    public Parameter save(Parameter p) {
        LOG.debug("save({})", p);

        em.merge(p);
        return p;
    }

    @Override
    public ParameterValue save(ParameterValue pv) {
        LOG.debug("save({})", pv);

        em.merge(pv);
        return pv;
    }

    @Override
    public Parameter findParemeterByPath(String path) {
        LOG.debug("findParemeterByPath({})", path);

        QParameter p = QParameter.parameter;
        JPAQuery q = new JPAQuery(em).from(p).where(p.path.equalsIgnoreCase(path));
        return q.uniqueResult(p);
    }

    @Override
    public ParameterValue findParameterValueByPathAndTarget(String path, String target) {
        LOG.debug("findParameterValueByPathAndTarget({}, {})", path, target);
        QParameterValue p = QParameterValue.parameterValue;
        JPAQuery q = new JPAQuery(em).from(p).where(p.id.path.eq(path).and(p.id.target.eq(target)));
        return q.uniqueResult(p);
    }

    @Override
    public List<ParameterValue> findParameterValuesByPath(String path) {
        LOG.debug("findParameterValuesByPath({})", path);
        QParameterValue p = QParameterValue.parameterValue;
        JPAQuery q = new JPAQuery(em).from(p).where(p.id.path.eq(path));
        return q.list(p);
    }

}
