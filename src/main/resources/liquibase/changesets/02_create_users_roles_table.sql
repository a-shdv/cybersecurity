create table users_roles
(
    user_id int8 not null,
    role_id int4 not null,
    primary key (user_id, role_id)
);

alter table if exists users_roles
    add constraint users_roles_role_fk foreign key (role_id) references roles;

alter table if exists users_roles
    add constraint users_roles_users_fk foreign key (user_id) references users;
