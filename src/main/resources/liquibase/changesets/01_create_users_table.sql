create table users
(
    id                    int8 generated by default as identity,
    username              varchar(255) unique,
    email                 varchar(255) unique,
    password              varchar(255),
    password_last_changed timestamp,
    is_account_non_locked boolean,
    is_enabled boolean,
    primary key (id)
);
