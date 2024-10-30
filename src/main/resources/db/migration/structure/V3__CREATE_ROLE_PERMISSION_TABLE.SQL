create table if not exists tb_roles_permissions (
    role_id UUID not null,
    permission_id UUID not null,
    foreign key (role_id) references tb_roles(id),
    foreign key (permission_id) references tb_permissions(id),
    primary key (role_id, permission_id)
);