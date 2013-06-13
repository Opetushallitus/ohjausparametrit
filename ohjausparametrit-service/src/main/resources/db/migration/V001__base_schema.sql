
    create table kuvaukset (
        id  bigserial not null,
        created timestamp not null,
        createdBy varchar(255) not null,
        modified timestamp not null,
        modifiedBy varchar(255) not null,
        primary key (id)
    );

    create table kuvaukset_arvot (
        id int8 not null,
        value varchar(16384),
        key varchar(255) not null,
        primary key (id, key)
    );

    create table parametri (
        path varchar(255) not null unique,
        created timestamp not null,
        createdBy varchar(255) not null,
        modified timestamp not null,
        modifiedBy varchar(255) not null,
        name varchar(255) not null,
        required boolean not null,
        primary key (path)
    );

    create table parametri_arvot (
        type varchar(31) not null,
        path varchar(255) not null,
        target varchar(255) not null,
        created timestamp not null,
        createdBy varchar(255) not null,
        modified timestamp not null,
        modifiedBy varchar(255) not null,
        value_i int4,
        end_ts timestamp,
        start_ts timestamp,
        value_s varchar(4096),
        value_b boolean not null,
        primary key (path, target)
    );

    alter table kuvaukset_arvot 
        add constraint FK18F82625F1312ED 
        foreign key (id) 
        references kuvaukset;
