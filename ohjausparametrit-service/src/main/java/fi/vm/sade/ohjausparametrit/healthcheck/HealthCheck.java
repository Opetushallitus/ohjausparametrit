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
package fi.vm.sade.ohjausparametrit.healthcheck;

import com.google.gson.GsonBuilder;
import com.mongodb.DBCollection;
import fi.vm.sade.generic.healthcheck.HealthChecker;
import fi.vm.sade.generic.healthcheck.SpringAwareHealthCheckServlet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 *
 * @author mlyly
 */
public class HealthCheck extends SpringAwareHealthCheckServlet {

    /* Just make sure the correct artifact gets added... */
    private GsonBuilder _gsonbuiler;

    @Autowired(required = false)
    private MongoTemplate _mongoTemplate;

    @Override
    protected Map<String, HealthChecker> registerHealthCheckers() {
        Map<String, HealthChecker> result = super.registerHealthCheckers();

        result.put("mongo", new HealthChecker() {

            @Override
            public Object checkHealth() throws Throwable {
                Map<String, Object> result = new LinkedHashMap<String, Object>();

                if (_mongoTemplate == null) {
                    throw new IllegalStateException("I need my daily Mongo! (mongotemplate not available)");
                }

                result.put("mongotemplate", "OK");

                // Loop over collections
                for (String collectionName : _mongoTemplate.getCollectionNames()) {
                    LinkedHashMap<String, String> cmap = new LinkedHashMap<String, String>();

                    DBCollection coll = _mongoTemplate.getCollection(collectionName);
                    cmap.put("count", "" + coll.count());

                    result.put("collection_" + collectionName, cmap);
                }

                return result;
            }
        });

        return result;
    }


}
