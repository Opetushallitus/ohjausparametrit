    create table parameter (
        id int8 not null unique,
        version int8 not null,
        created timestamp,
        createdBy varchar(255),
        modified timestamp,
        modifiedBy varchar(255),
        name varchar(255) not null,
        path varchar(255) not null,
        value_b boolean,
        value_l int8,
        value_s varchar(255),
        primary key (id),
        unique (path, name)
    );

    create table template (
        id int8 not null unique,
        version int8 not null,
        created timestamp,
        createdBy varchar(255),
        modified timestamp,
        modifiedBy varchar(255),
        path varchar(255) not null,
        required boolean not null,
        type varchar(255) not null,
        primary key (id),
        unique (path)
    );

    alter table parameter
        add constraint FK747EB3A99686EE89
        foreign key (path)
        references template (path);

    create sequence hibernate_sequence;

    alter table parameter
        add constraint parameter_template
        foreign key (path)
        references template (path);

create index p_pathname on parameter(path, name);
create index t_path on template(path);


