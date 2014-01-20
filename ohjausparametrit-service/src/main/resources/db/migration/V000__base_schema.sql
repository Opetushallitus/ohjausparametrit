create table parameter (
    id int8 not null unique,
    version int8 not null,
    created timestamp,
    createdBy varchar(255),
    modified timestamp,
    modifiedBy varchar(255),
    name varchar(255) not null,
    path varchar(255) not null,
    type varchar(255) not null,
    value varchar(255) not null,
    primary key (id),
    unique (path, name)
);

create sequence hibernate_sequence;

create index pathname on parameter(path, name);

