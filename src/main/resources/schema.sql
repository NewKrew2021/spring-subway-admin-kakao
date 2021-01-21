
create table if not exists STATION
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key(id)
    );

create table if not exists LINE
(
    id bigint auto_increment not null,
    extra_fare int,
    name varchar(255) not null unique,
    color varchar(20) not null,
    primary key(id)
    );

create table if not exists SECTION
(
    id bigint auto_increment not null,
    line_id bigint not null,
    station_id bigint not null,
    relative_position int,
    foreign key(line_id) references LINE(id) on delete cascade,
    foreign key(station_id) references STATION(id) on delete cascade,
    primary key(id)
    );
