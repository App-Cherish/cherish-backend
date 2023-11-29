create table if not exists SPRING_SESSION
(
    PRIMARY_ID            char(36)     not null
        primary key,
    SESSION_ID            char(36)     not null,
    CREATION_TIME         bigint       not null,
    LAST_ACCESS_TIME      bigint       not null,
    MAX_INACTIVE_INTERVAL int          not null,
    EXPIRY_TIME           bigint       not null,
    PRINCIPAL_NAME        varchar(100) null,
    constraint SPRING_SESSION_IX1
        unique (SESSION_ID)
)
    row_format = DYNAMIC;

create index SPRING_SESSION_IX2
    on SPRING_SESSION (EXPIRY_TIME);

create index SPRING_SESSION_IX3
    on SPRING_SESSION (PRINCIPAL_NAME);

create table if not exists SPRING_SESSION_ATTRIBUTES
(
    SESSION_PRIMARY_ID char(36)     not null,
    ATTRIBUTE_NAME     varchar(200) not null,
    ATTRIBUTE_BYTES    blob         not null,
    primary key (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
    constraint SPRING_SESSION_ATTRIBUTES_FK
        foreign key (SESSION_PRIMARY_ID) references SPRING_SESSION (PRIMARY_ID)
            on delete cascade
)
    row_format = DYNAMIC;

create table if not exists avatar
(
    active             tinyint(1)              null,
    birth              date                    not null,
    avatar_id          bigint auto_increment
        primary key,
    created_date       datetime(6)             null,
    last_modified_date datetime(6)             null,
    gender             enum ('FEMALE', 'MALE') not null,
    name               varchar(255)            not null
);

create table if not exists account
(
    active             tinyint(1)              null,
    account_id         bigint auto_increment
        primary key,
    avatar_id          bigint                  not null,
    created_date       datetime(6)             null,
    last_modified_date datetime(6)             null,
    oauth_id           varchar(255)            not null,
    platform           enum ('APPLE', 'KAKAO') not null,
    constraint unique (oauth_id),
    constraint foreign key (avatar_id) references avatar (avatar_id)
);

create table if not exists back_up
(
    active             tinyint(1)   null,
    diary_count        int          not null,
    avatar_id          bigint       null,
    created_date       datetime(6)  null,
    last_modified_date datetime(6)  null,
    backup_id          varchar(255) not null
        primary key,
    device_type        varchar(255) null,
    os_version         varchar(255) null,
    constraint foreign key (avatar_id) references avatar (avatar_id)
);

create table if not exists session_token
(
    active              tinyint(1)   null,
    avatar_id           bigint       not null,
    created_date        datetime(6)  null,
    expired_date        datetime(6)  null,
    session_token_id    bigint auto_increment
        primary key,
    device_id           varchar(255) null,
    device_type         varchar(255) null,
    session_token_vaule varchar(255) null,
    constraint foreign key (avatar_id) references avatar (avatar_id)
);

create table if not exists diary
(
    active             tinyint(1)                           null,
    avatar_id          bigint                               not null,
    created_date       datetime(6)                          null,
    last_modified_date datetime(6)                          null,
    writing_date       datetime(6)                          not null,
    backup_id          varchar(255)                         null,
    content            TEXT                        not null,
    device_id          varchar(255)                         not null,
    device_type        varchar(255)                         not null,
    diary_id           varchar(255)                         not null
        primary key,
    kind               enum ('EMOTION', 'FREE', 'QUESTION') null,
    title              TEXT                      null,
    constraint foreign key (backup_id) references back_up (backup_id),
    constraint foreign key (avatar_id) references avatar (avatar_id)
);

