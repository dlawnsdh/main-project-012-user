create table if not exists member (
    member_id bigint not null auto_increment,
    email varchar(100) not null unique,
    nickname varchar(100) not null,
    birthday varchar(50) null,
    profile_url varchar(100) not null,
    gender int null,
    memo varchar(255) null,
    created_at datetime not null,
    modified_at datetime not null,
    primary key (member_id)
);