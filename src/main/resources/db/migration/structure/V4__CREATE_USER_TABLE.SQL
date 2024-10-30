create table if not exists tb_users (
    id UUID primary key,
    email varchar(255) not null,
    profile_url varchar(255),
    is_enabled boolean default true,
    is_verified boolean default false,
    password varchar(255),
    provider varchar(255),
    provider_id varchar(255),
    username varchar(255) not null,
    created_at timestamp(6),
    created_by varchar(255),
    updated_at timestamp(6),
    updated_by varchar(255)
);