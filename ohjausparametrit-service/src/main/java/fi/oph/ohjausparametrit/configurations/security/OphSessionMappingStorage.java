package fi.oph.ohjausparametrit.configurations.security;

import org.jasig.cas.client.session.SessionMappingStorage;

public interface OphSessionMappingStorage extends SessionMappingStorage {

    void clean();

}
