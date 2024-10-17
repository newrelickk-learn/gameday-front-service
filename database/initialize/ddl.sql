CREATE USER IF NOT EXISTS 'frontservice'@'%' IDENTIFIED BY 'frontservice_password';

GRANT ALL ON db.* TO 'frontservice';

-- auto-generated definition
CREATE TABLE IF NOT EXISTS  company
(
    id   varchar(255) not null
        primary key,
    name varchar(255) null
);


-- auto-generated definition
CREATE TABLE IF NOT EXISTS role
(
    id   int auto_increment
        primary key,
    name varchar(255) null
);

-- auto-generated definition
CREATE TABLE IF NOT EXISTS  user
(
    id         int auto_increment
        primary key,
    email      varchar(255) not null,
    name       varchar(255) null,
    password   varchar(255) not null,
    username   varchar(255) not null,
    company_id varchar(255) null,
    constraint UK_ob8kqyqqgmefl0aco34akdtpe
        unique (email),
    constraint UK_sb8bbouer5wak8vyiiy4pf2bx
        unique (username),
    constraint FK2yuxsfrkkrnkn5emoobcnnc3r
        foreign key (company_id) references company (id)
);



-- auto-generated definition
CREATE TABLE IF NOT EXISTS user_roles
(
    user_id  int not null,
    roles_id int not null,
    primary key (user_id, roles_id),
    constraint FK55itppkw3i07do3h7qoclqd4k
        foreign key (user_id) references user (id),
    constraint FKj9553ass9uctjrmh0gkqsmv0d
        foreign key (roles_id) references role (id)
);

-- auto-generated definition
CREATE TABLE IF NOT EXISTS cart
(
    id      int auto_increment
        primary key,
    active  bit          null,
    cart_id varchar(255) null,
    user_id int          null,
    constraint FKl70asp4l4w0jmbm1tqyofho4o
        foreign key (user_id) references user (id)
);

-- auto-generated definition
create table cart_item
(
    id         int auto_increment
        primary key,
    amount     int          null,
    product_id varchar(255) null,
    cart_id    int          null,
    constraint FK1uobyhgl1wvgt1jpccia8xxs3
        foreign key (cart_id) references cart (id)
);

-- auto-generated definition
CREATE TABLE IF NOT EXISTS cart_items
(
    cart_id  int not null,
    items_id int not null,
    primary key (cart_id, items_id),
    constraint UK_383kkp3af9dpn91t406oqe9n1
        unique (items_id),
    constraint FK99e0am9jpriwxcm6is7xfedy3
        foreign key (cart_id) references cart (id),
    constraint FKnqjva2t0na43f4qxm3xprl2qu
        foreign key (items_id) references cart_item (id)
);

-- auto-generated definition
CREATE TABLE IF NOT EXISTS orders
(
    id           int auto_increment
        primary key,
    active       bit          null,
    order_stage  varchar(255) null,
    cart_id      int          null,
    user_id      int          null,
    coupon_code  varchar(255) null,
    payment_type varchar(255) null,
    purchased    bit          null,
    constraint UK_s1sr8a1rkx80gwq9pl0952dar
        unique (cart_id),
    constraint FKel9kyl84ego2otj2accfd8mr7
        foreign key (user_id) references user (id),
    constraint FKtpihbdn6ws0hu56camb0bg2to
        foreign key (cart_id) references cart (id)
);



CREATE USER IF NOT EXISTS 'newrelic'@'%' IDENTIFIED BY 'newrelic';
GRANT REPLICATION CLIENT ON *.* TO 'newrelic'@'%';
GRANT SELECT ON *.* TO 'newrelic'@'%';
