-- delete relations --

drop table if exists users_like_films;

drop table if exists user_friendship;

drop table if exists films_genres;

drop table if exists films_mpa;

-- delete models --

drop table if exists users;

drop table if exists films;

drop table if exists genres;

drop table if exists mpa;

-- create models--

create table mpa
(
    mpa_id long auto_increment primary key,
    name   varchar not null unique
);

create table genres
(
    genre_id long auto_increment primary key,
    name     varchar not null unique
);

create table users
(
    user_id  long auto_increment primary key,
    login    varchar,
    name     varchar,
    email    varchar,
    birthday date
);

create table films
(
    film_id      long auto_increment primary key,
    likes        integer check (likes >= 0),
    name         varchar,
    description  varchar,
    release_date date,
    duration     long
);

-- create relations --

create table users_like_films
(
    user_id long references users (user_id) not null,
    film_id long references films (film_id) not null
);

create table user_friendship
(
    first_user_id  long references users (user_id) check (first_user_id != second_user_id) not null,
    second_user_id long references users (user_id)                                         not null
);

create table films_genres
(
    film_id  long references films (film_id),
    genre_id long references genres (genre_id)
);

create table films_mpa
(
    film_id long references films (film_id),
    mpa_id  long references mpa (mpa_id)
);

-- insert mpa and genres --

insert into mpa(mpa_id, name)
values (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

insert into genres(genre_id, name)
values (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');