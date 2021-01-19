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
    id              bigint auto_increment not null,
    line_id         bigint                not null references LINE (id),
    up_station_id   bigint                not null references STATION (id),
    down_station_id bigint                not null references STATION (id),
    distance        int check (distance >= 1 and distance <= 100000),
    primary key (id)
);