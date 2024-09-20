package fi.oph.ohjausparametrit.ovara.ajastus;

import fi.oph.ohjausparametrit.configurations.security.JdbcSessionMappingStorage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/*
-- name: latest-siirtotiedosto-data
-- Returns latest successful siirtotiedosto-operation data
-- There should always be at least one, as per siirtotiedosto table migration.
        select id, execution_uuid, window_start, window_end, run_start::text, run_end::text, info, success, error_message from siirtotiedosto
where success order by id desc limit 1;

        -- name: insert-new-siirtotiedosto-operation!<
-- Inserts new siirtotiedosto-operation data
insert into siirtotiedosto (id, execution_uuid, window_start, window_end, run_start, run_end, info, success, error_message)
values (nextval('siirtotiedosto_id_seq'), :execution_uuid::uuid, :window_start, now(), now(), null,
        '{}'::jsonb, null, null) returning id, execution_uuid, window_start, window_end, info, success, error_message;

-- name: upsert-siirtotiedosto-data!
        -- Upserts siirtotiedosto-operation data
update siirtotiedosto
set run_end = now(),
info = :info::jsonb,
success = :success::boolean,
error_message = :error_message
where id = :id;
*/

@Repository
@Transactional
public interface SiirtotiedostoProsessiRepository extends CrudRepository<SiirtotiedostoProsessi, String> {

    @Query(value = "SELECT id, execution_uuid, window_start, window_end, run_start, run_end, info, success, error_message from siirtotiedosto where success order by id desc limit 1", nativeQuery = true)
    SiirtotiedostoProsessi findLatestSuccessful();

    @Modifying
    @Query(value = "INSERT into siirtotiedosto (id, execution_uuid, window_start, window_end, run_start, run_end, info, success, error_message)\n" +
            "values (nextval('siirtotiedosto_id_seq'), ?1::uuid, ?2, now(), now(), null,\n" +
            "        '{}'::jsonb, null, null) returning id, execution_uuid, window_start, window_end, info, success, error_message", nativeQuery = true)
    SiirtotiedostoProsessi insertNew(UUID uuid, Date windowStart);

    @Modifying
    @Query(value = "UPDATE siirtotiedosto\n" +
            "SET run_end = now(),\n" +
            "info = ?1::jsonb,\n" +
            "success = ?2::boolean,\n" +
            "error_message = ?3\n" +
            "WHERE id = ?4;", nativeQuery = true)
    SiirtotiedostoProsessi update(Map<String, String> info, boolean success, String errorMessage, int id);
}

/*

public class SiirtotiedostoProsessiRepository {
    //@Query(value = "SELECT id, execution_uuid, window_start, window_end, run_start::text, run_end::text, info, success, error_message from siirtotiedosto where success order by id desc limit 1", nativeQuery = true)
    public SiirtotiedostoProsessi findLatestSuccessful() {
        String sql = "SELECT session_id FROM cas_client_session WHERE mapping_id = ?";
        return queryForOptional(sql, String.class, mappingId)
                .map(sessionRepository::findById)
                .filter(Objects::nonNull)
                .map(session -> new JdbcSessionMappingStorage.HttpSessionAdapter(sessionRepository, session))
                .orElse(null);
    };
}*/
