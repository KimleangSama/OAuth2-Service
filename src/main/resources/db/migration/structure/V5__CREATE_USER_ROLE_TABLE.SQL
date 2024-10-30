create table if not exists tb_users_roles (
    user_id UUID not null,
    role_id UUID not null,
    foreign key (user_id) references tb_users(id),
    foreign key (role_id) references tb_roles(id),
    primary key (user_id, role_id)
);