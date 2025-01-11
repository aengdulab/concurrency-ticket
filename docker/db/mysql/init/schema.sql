create database if not exists ticket;

use ticket;

create table if not exists member
(
    id   bigint not null auto_increment,
    name varchar(255),
    primary key (id)
) engine = InnoDB;

create table if not exists member_ticket
(
    id        bigint not null auto_increment,
    member_id bigint,
    ticket_id bigint,
    primary key (id)
) engine = InnoDB;

create table if not exists ticket
(
    id       bigint not null auto_increment,
    quantity bigint,
    name     varchar(255),
    primary key (id)
) engine = InnoDB;

alter table member_ticket
    add constraint FKomlguxfcardby8919wyyaxcw3
        foreign key (member_id)
            references member (id);

alter table member_ticket
    add constraint FK4ri79r4gubettxo63roi3ksfj
        foreign key (ticket_id)
            references ticket (id);
