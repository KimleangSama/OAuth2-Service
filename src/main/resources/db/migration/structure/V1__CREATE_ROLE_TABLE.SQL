create table if not exists tb_roles (
    id UUID primary key,
    name varchar(255) not null,
    created_at timestamp(6),
    created_by varchar(255),
    updated_at timestamp(6),
    updated_by varchar(255)
);