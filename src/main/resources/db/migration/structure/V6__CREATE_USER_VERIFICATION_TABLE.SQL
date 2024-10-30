create table if not exists tb_verification_codes
(
    id         uuid not null primary key,
    created_at timestamp(6),
    created_by varchar(255),
    updated_at timestamp(6),
    updated_by varchar(255),
    code       varchar(255),
    isexpired  boolean,
    isused     boolean,
    user_id    uuid,
    foreign key (user_id) references tb_users (id)
);