-- 테이블 생성
create table bgm (
                     created_by integer not null,
                     id integer not null auto_increment,
                     is_deleted bit not null,
                     mini_homepage_id integer not null,
                     updated_by integer,
                     created_at datetime(6) not null,
                     deleted_at datetime(6),
                     updated_at datetime(6) not null,
                     artist varchar(50) not null,
                     bgm_url varchar(255) not null,
                     title varchar(255) not null,
                     primary key (id)
) engine=InnoDB;

create table board (
                       created_by integer not null,
                       id integer not null auto_increment,
                       is_deleted bit not null,
                       is_public bit not null,
                       mini_homepage_id integer not null,
                       updated_by integer,
                       user_id integer not null,
                       created_at datetime(6) not null,
                       deleted_at datetime(6),
                       updated_at datetime(6) not null,
                       type varchar(30) not null,
                       content TEXT not null,
                       mood varchar(255),
                       weather varchar(255),
                       primary key (id)
) engine=InnoDB;

create table comments (
                          board_id integer not null,
                          created_by integer not null,
                          id integer not null auto_increment,
                          is_deleted bit not null,
                          updated_by integer,
                          user_id integer not null,
                          created_at datetime(6) not null,
                          deleted_at datetime(6),
                          updated_at datetime(6) not null,
                          content varchar(1000) not null,
                          primary key (id)
) engine=InnoDB;

create table emotions (
                          created_by integer not null,
                          id integer not null auto_increment,
                          is_deleted bit not null,
                          updated_by integer,
                          created_at datetime(6) not null,
                          deleted_at datetime(6),
                          updated_at datetime(6) not null,
                          name varchar(50) not null,
                          type varchar(50) not null,
                          primary key (id)
) engine=InnoDB;

create table ilchons (
                         created_by integer not null,
                         friend_id integer not null,
                         id integer not null auto_increment,
                         is_deleted bit not null,
                         updated_by integer,
                         user_id integer not null,
                         created_at datetime(6) not null,
                         deleted_at datetime(6),
                         updated_at datetime(6) not null,
                         status varchar(50) not null,
                         friend_nickname varchar(100),
                         user_nickname varchar(100),
                         request_message TEXT,
                         primary key (id)
) engine=InnoDB;

create table mini_homepages (
                                created_by integer not null,
                                id integer not null auto_increment,
                                is_deleted bit not null,
                                today_visits integer not null,
                                total_visits integer not null,
                                updated_by integer,
                                user_id integer not null,
                                created_at datetime(6) not null,
                                deleted_at datetime(6),
                                updated_at datetime(6) not null,
                                title varchar(100) not null,
                                primary key (id)
) engine=InnoDB;

create table user_profiles (
                               created_by integer not null,
                               id integer not null auto_increment,
                               is_active bit not null,
                               is_deleted bit not null,
                               updated_by integer,
                               user_id integer not null,
                               created_at datetime(6) not null,
                               deleted_at datetime(6),
                               updated_at datetime(6) not null,
                               bio TEXT not null,
                               image_url varchar(255) not null,
                               primary key (id)
) engine=InnoDB;

create table users (
                       created_by integer not null,
                       emotion_id integer,
                       id integer not null auto_increment,
                       is_deleted bit not null,
                       updated_by integer,
                       created_at datetime(6) not null,
                       deleted_at datetime(6),
                       updated_at datetime(6) not null,
                       birth varchar(50) not null,
                       name varchar(50) not null,
                       phone varchar(50) not null,
                       email varchar(255) not null,
                       login_id varchar(255) not null,
                       password varchar(255) not null,
                       gender enum ('Female','Male') not null,
                       primary key (id)
) engine=InnoDB;

create table visits (
                        created_by integer not null,
                        id integer not null auto_increment,
                        is_deleted bit not null,
                        mini_homepage_id integer not null,
                        updated_by integer,
                        visitor_id integer,
                        created_at datetime(6) not null,
                        deleted_at datetime(6),
                        updated_at datetime(6) not null,
                        primary key (id)
) engine=InnoDB;

-- 제약 조건 추가 (외래 키 설정)
alter table mini_homepages
    add constraint UK21asrlurfxonlsbp2syg500y2 unique (user_id);

alter table bgm
    add constraint FKm4io41a47mkpph9hclgx68rj7
        foreign key (mini_homepage_id)
            references mini_homepages (id);

alter table board
    add constraint FKd11xb2e4dmhg5h30560yvif8b
        foreign key (mini_homepage_id)
            references mini_homepages (id);

alter table board
    add constraint FK5vlh90qyii65ixwsbnafd55ud
        foreign key (user_id)
            references users (id);

alter table comments
    add constraint FK2sbm05xp09r2igj2t4j2so05l
        foreign key (board_id)
            references board (id);

alter table comments
    add constraint FK8omq0tc18jd43bu5tjh6jvraq
        foreign key (user_id)
            references users (id);

alter table ilchons
    add constraint FK3h8rsub1f7bciafejwbvfvnvf
        foreign key (friend_id)
            references users (id);

alter table ilchons
    add constraint FKeh5vv5nbdnql4cfrkq3m405t0
        foreign key (user_id)
            references users (id);

alter table mini_homepages
    add constraint FKoxdalmx14hr2l7hu9gupiostf
        foreign key (user_id)
            references users (id);

alter table user_profiles
    add constraint FKjcad5nfve11khsnpwj1mv8frj
        foreign key (user_id)
            references users (id);

alter table users
    add constraint FK7bnefnh9gojtd0ipw62shibuo
        foreign key (emotion_id)
            references emotions (id);

alter table visits
    add constraint FKclvuvfjwxl675jc8jbnqnopq
        foreign key (mini_homepage_id)
            references mini_homepages (id);

alter table visits
    add constraint FKgfh1bqentqo4jo71sicugjkee
        foreign key (visitor_id)
            references users (id);