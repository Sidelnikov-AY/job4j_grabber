CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

insert into company(id, name) values (1, 'company_A');
insert into company(id, name) values (2, 'company_B');
insert into company(id, name) values (3, 'company_C');
insert into company(id, name) values (4, 'company_D');
insert into company(id, name) values (5, 'company_E');

CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

insert into person(id, name, company_id) values (1, 'person_A1', 1);
insert into person(id, name, company_id) values (2, 'person_A2', 1);
insert into person(id, name, company_id) values (3, 'person_B1', 2);
insert into person(id, name, company_id) values (4, 'person_B2', 2);
insert into person(id, name, company_id) values (5, 'person_C1', 3);
insert into person(id, name, company_id) values (6, 'person_C2', 3);
insert into person(id, name, company_id) values (7, 'person_D1', 4);
insert into person(id, name, company_id) values (8, 'person_D2', 4);
insert into person(id, name, company_id) values (9, 'person_E1', 5);
insert into person(id, name, company_id) values (10, 'person_E2', 5);
insert into person(id, name, company_id) values (11, 'person_E3', 5);

--1. В одном запросе получить
--- имена всех person, которые не состоят в компании с id = 5;
--- название компании для каждого человека.
select p.name, c.name
from person p
join company c
on p.company_id = c.id and c.id <> 5;

--2. Необходимо выбрать название компании с максимальным количеством человек
--+ количество человек в этой компании.
select c.name, count(p.company_id)
from company c
join person p
on c.id = p.company_id
group by c.name
order by count(p.company_id) desc
limit 1

