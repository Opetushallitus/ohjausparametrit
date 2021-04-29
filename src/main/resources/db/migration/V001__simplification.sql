
--
-- Clean up old schema
-- 

alter table parameter drop constraint FK747EB3A99686EE89;

drop table if exists parameter cascade;

drop table if exists parameter2 cascade;

drop table if exists template cascade;

drop sequence hibernate_sequence;

--
-- Create new schema
--

create table parameter (
    target varchar(255) not null,
    jsonValue text,
    primary key (target)
);
