drop table bankrecord;
drop table bankaccount;

alter user scott account unlock;
alter user scott identified by a;


create table bankaccount(
       id varchar2(40) primary key,
       bname varchar2(40) unique not null,
       pwd varchar2(40) not null,
       balance numeric(10,2) default 1.0
);

--balance至少要大于等于1元
alter table bankaccount
   add constraint ck_bankaccount_balance
      check( balance>=1);

-- id  序列
 -- optype   操作类型只能是: 取  存   转入  转出
 -- num   操作金额
  --baid   外键   bankaccount 的id列主键
   --brno 转账操作的流水号   序列
create table bankrecord(
       id int primary key,        
       optime timestamp,
       --optype varchar2(20)  check( optype in('存','取','转入','转出')  ) , 
       optype varchar2(20)  , 
       num    numeric(10,2),    
       --baid   varchar2(40) foreign key references bankaccount(id), 
       baid  varchar2(40),   
       brno   int              
);



--optype的检查约束
alter table bankrecord
   add constraint ck_bankrecord
       check( optype in('存','取','转入','转出')  );      
--主外键约束
alter table bankrecord
    add constraint fk_bankrecord
       foreign key(baid) references bankaccount( id );      
       
--序列:  用于生成bankrecord表中的主键列   id
create sequence seq_bankrecord_id;

--序列:  用于生成  bankrecord表中的brno 转账流水号
create sequence seq_bankrecord_brno;

----------------------
--需求1:  给bankaccount表添加100w用户, 每个用户的balance为1.     问题是这个表已经存在了. . 
insert /*+  nologging */  into BANKACCOUNT( id,bname,pwd,balance  )
select  dbms_random.string('x', 20),
                  dbms_random.string('x', 20)||trunc(dbms_random.value(0, 9999999)) ,
                   '5eb03b8172b979cd387f25e26982b313',
                   1.0
            from dual
         connect by level <= 1000000;



--需求2: 请考虑给这个表加入索引 .    经常用来查询且得到的数据比原始数据集小得多的列的才加索引.
-- 索引类型: B树, 位图, 反向键索引(热块竞争), 函数
create index idx_bankrecord_baid
on bankrecord( baid );

--需求3: 有哪些操作比较复杂，可以生成视图，以节省代码.  
--   周流水

--   月流水 
grant create any view to scott;    --给scott用户赋予创建视图的权限

create or replace view view_bankrecord_aweek
as
select * from scott.bankrecord 
where    optime>  add_months(trunc(sysdate),-1)
order by optime desc;


select * from view_bankrecord_aweek;


insert into bankaccount values( '1','张三','5eb03b8172b979cd387f25e26982b313',1.0);
insert into bankaccount values( '2','李四','5eb03b8172b979cd387f25e26982b313',1.0);

commit;

select * from bankaccount;
select * from bankrecord;


select * from bankrecord where baid=? order by optime desc



