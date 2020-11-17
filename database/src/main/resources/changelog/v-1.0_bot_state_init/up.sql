CREATE SCHEMA bot;

GO

create table bot.answers (id  bigserial not null, city varchar(255), country varchar(255), floor varchar(255), min_price int8, max_price int8, rooms varchar(255), primary key (id));

GO

create table bot.state_data (id  bigserial not null, chat_id int8, state int4, answer_id int8, user_id int8, primary key (id));

GO

alter table if exists bot.state_data add constraint UK_53kwarw1kswpwl0w3xp9kn4yn unique (chat_id);

GO

alter table if exists bot.state_data add constraint FK8p1ohytbxi4juobmm8e8v1iuw foreign key (answer_id) references bot.answers;

GO

alter table if exists bot.state_data add constraint FKme4orh94jasnpof5j14w7fiip foreign key (user_id) references client.users;