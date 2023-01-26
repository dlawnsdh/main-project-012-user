create table if not exists member (
    member_id bigint not null auto_increment,
    email varchar(100) not null unique,
    nickname varchar(100) not null,
    birthday varchar(50) null,
    profile_url varchar(100) not null,
    gender int null,
    memo varchar(255) null,
    registration_id varchar(10) not null,
    created_at datetime not null,
    modified_at datetime not null,
    primary key (member_id)
);

create table if not exists follow (
    id bigint not null auto_increment,
    follower_id bigint not null,
    following_id bigint not null,
    created_at datetime not null,
    modified_at datetime not null,
    primary key (id)
);