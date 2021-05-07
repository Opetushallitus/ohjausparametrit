create sequence if not exists hibernate_sequence start 1 increment 1;

create table parameter_history (
    target varchar(255) not null,
    rev int4 not null,
    revtype int2,
    jsonvalue jsonb,
    muokattu timestamp,
    muokkaaja varchar(255),
    primary key (target, rev)
);

create table revinfo (
    rev int4 not null,
    revtstmp int8,
    primary key (rev)
);

alter table if exists parameter_history add constraint parameter_history_rev foreign key (rev) references revinfo;