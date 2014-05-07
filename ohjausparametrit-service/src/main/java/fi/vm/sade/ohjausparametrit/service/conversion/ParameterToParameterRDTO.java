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
package fi.vm.sade.ohjausparametrit.service.conversion;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;

import fi.vm.sade.ohjausparametrit.api.model.ParameterRDTO;
import fi.vm.sade.ohjausparametrit.service.dao.TemplateRepository;
import fi.vm.sade.ohjausparametrit.service.model.Parameter;
import fi.vm.sade.ohjausparametrit.service.model.Template;

/**
 * Kind of stupid since it's 1-1... but just in case.
 * 
 * @author mlyly
 */
public class ParameterToParameterRDTO implements
        Function<Parameter, ParameterRDTO> {

    private final TemplateRepository templateRepository;

    public ParameterToParameterRDTO(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    LoadingCache<String, String> typeCache = CacheBuilder.newBuilder()
            .maximumSize(10000).expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                public String load(String key) {
                    final Template template = templateRepository
                            .findByPath(key);
                    if (template != null) {
                        return template.getType().name();
                    }
                    return null;
                }
            });

    @Override
    public ParameterRDTO apply(@Nullable Parameter s) {

        if (s == null) {
            return null;
        }

        ParameterRDTO t = new ParameterRDTO();

        if (templateRepository != null) {
            try {
                t.setType(typeCache.get(s.getPath()));
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        t.setCreated(s.getCreated());
        t.setCreatedBy(s.getCreatedBy());
        t.setModified(s.getModified());
        t.setModifiedBy(s.getModifiedBy());
        t.setName(s.getName());
        t.setPath(s.getPath());
        t.setValue(s.getValue());
        return t;
    }

}
