create table if not exists STATION
(
    id   bigint auto_increment not null,
    name varchar(255)          not null unique,
    primary key (id)
);

create table if not exists LINE
(
    id    bigint auto_increment not null,
    name  varchar(255)          not null unique,
    color varchar(20)           not null,
    primary key (id)
);

create table if not exists SECTION
(
    line_id    bigint not null,
    station_id bigint not null,
    distance   int,
    primary key (line_id, station_id)
);