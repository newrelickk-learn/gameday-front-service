FROM mysql:8.0.26

COPY ./initialize/ddl.sql /docker-entrypoint-initdb.d/init_01.sql
COPY ./initialize/data.sql /docker-entrypoint-initdb.d/init_02.sql
COPY ./config/my.cnf /etc/mysql/conf.d/