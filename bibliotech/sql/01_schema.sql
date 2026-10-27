
create table "system_user" (
    id serial primary key,
    username varchar(50) unique not null,
    password_hash varchar(255) not null,
    role varchar(20) not null check (role in ('admin', 'librarian'))
);

create table category (
    id serial primary key,
    name varchar(100) unique not null
);

create table book (
    id serial primary key,
    title varchar(200) not null,
    author varchar(150) not null,
    isbn varchar(20) unique not null,
    category_id int not null,
    year int not null,
    constraint fk_book_category foreign key (category_id) references category(id) on delete restrict
);

create table copy (
    id serial primary key,
    book_id int not null,
    barcode varchar(50) unique not null,
    status varchar(20) not null default 'available' check (status in ('available', 'borrowed', 'lost', 'damaged')),
    constraint fk_copy_book foreign key (book_id) references book(id) on delete cascade
);

create table member (
    id serial primary key,
    full_name varchar(150) not null,
    document_id varchar(50) unique not null,
    phone varchar(20),
    status varchar(20) not null default 'active' check (status in ('active', 'supended'))
);

create table loan (
    id serial primary key,
    copy_id int not null,
    member_id int not null,
    loan_date date not null default current_date,
    due_date date not null,
    return_date date,
    constraint fk_loan_copy foreign key (copy_id) references copy(id) on delete restrict,
    constraint fk_loan_member foreign key (member_id) references member(id) on delete restrict
);

create table fine (
    id serial primary key,
    loan_id int unique not null,
    amount decimal(10, 2) not null default 0.00,
    paid boolean not null default false,
    paid_date date,
    constraint fk_fine_loan foreign key (loan_id) references loan(id) on delete cascade
);

create table reservation (
    id serial primary key,
    book_id int not null,
    member_id int not null,
    reserved_at timestamp not null default current_timestamp,
    status varchar(20) not null default 'pending' check (status in ('pending', 'fulfilled', 'cancelled')),
    constraint fk_reservation_book foreign key (book_id) references book(id) on delete cascade,
    constraint fk_reservation_member foreign key (member_id) references member(id) on delete cascade
);