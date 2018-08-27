--SQL
drop table if exists stkorder;
CREATE TABLE stkorder
(
    orderdate char(8) not null,
    orderid varchar(10) not null,
    ordertime varchar(10) not null,
    orderprice decimal(7,3) default  0.00,
    orderqty decimal(10,1) default 0.0,
	matchqty decimal(10,1) default 0.0,
    stkcode varchar(7) not null,
    bsflag varchar(3) not null,
    custid varchar(11) not null,
    canceled tinyint(1) default 0,
    cancelorderid varchar(10) default 0,
    ordertype int default 0,
    addr varchar(30),
    status varchar(2),
	
    PRIMARY KEY(orderdate,orderid)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


drop table if exists marketdate;
CREATE TABLE marketdate
(
    lastorderdate char(8) not null,
	orderdate  char(8) not null,
	nextdate  char(8) not null
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into marketdate values('20170720','20170721','20170724');

drop table if exists serialno;
CREATE TABLE serialno
(
	serialtype varchar(8) not null,
    orderdate char(8),
	startno int(6),
	endno int(6),
	currno int(6)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
delete from serialno;
insert into serialno values('orderid','20170807',0,999999,0);

create table market
(   marketcode char(1) not null,
	status char(1),
	period char(1)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into market values('0','1','1');

create table stkmatch
(
	sno int(10)  not null auto_increment,
	matchdate char(8) not null,
	matchtime varchar(10) not null,
	orderdate char(8) not null,
	orderid char(10) not null,
	matchprice decimal(7,3) default 0.00,
	matchqty decimal(10,1) default 0.0,
	custid varchar(11) not null,
	PRIMARY KEY(sno)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


---kafka相关命令
./kafka-topics.sh --create --topic order --replication-factor 1 --partitions 1 --zookeeper localhost:2181
./kafka-topics.sh --create --topic manage --replication-factor 1 --partitions 1 --zookeeper localhost:2181
./kafka-topics.sh --create --topic hq --replication-factor 1 --partitions 1 --zookeeper localhost:2181
./kafka-topics.sh --list --zookeeper localhost:2181
./kafka-topics.sh --delete --zookeeper localhost:2181 --topic order
./kafka-topics.sh --describe --zookeeper localhost:2181 --topic manage

./kafka-console-consumer.sh --zookeeper localhost:2181 --topic hq --from-beginning
./kafka-console-producer.sh --broker-list localhost:9092 --topic order
./kafka-console-producer.sh --broker-list 10.1.171.31:9092 --topic manage


./zookeeper-server-start.sh ../config/zookeeper.properties &
 ./kafka-server-start.sh ../config/server.properties &