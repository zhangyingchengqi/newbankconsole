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

--balance����Ҫ���ڵ���1Ԫ
alter table bankaccount
   add constraint ck_bankaccount_balance
      check( balance>=1);

-- id  ����
 -- optype   ��������ֻ����: ȡ  ��   ת��  ת��
 -- num   �������
  --baid   ���   bankaccount ��id������
   --brno ת�˲�������ˮ��   ����
create table bankrecord(
       id int primary key,        
       optime timestamp,
       --optype varchar2(20)  check( optype in('��','ȡ','ת��','ת��')  ) , 
       optype varchar2(20)  , 
       num    numeric(10,2),    
       --baid   varchar2(40) foreign key references bankaccount(id), 
       baid  varchar2(40),   
       brno   int              
);



--optype�ļ��Լ��
alter table bankrecord
   add constraint ck_bankrecord
       check( optype in('��','ȡ','ת��','ת��')  );      
--�����Լ��
alter table bankrecord
    add constraint fk_bankrecord
       foreign key(baid) references bankaccount( id );      
       
--����:  ��������bankrecord���е�������   id
create sequence seq_bankrecord_id;

--����:  ��������  bankrecord���е�brno ת����ˮ��
create sequence seq_bankrecord_brno;

----------------------
--����1:  ��bankaccount�����100w�û�, ÿ���û���balanceΪ1.     ������������Ѿ�������. . 
insert /*+  nologging */  into BANKACCOUNT( id,bname,pwd,balance  )
select  dbms_random.string('x', 20),
                  dbms_random.string('x', 20)||trunc(dbms_random.value(0, 9999999)) ,
                   '5eb03b8172b979cd387f25e26982b313',
                   1.0
            from dual
         connect by level <= 1000000;



--����2: �뿼�Ǹ������������� .    ����������ѯ�ҵõ������ݱ�ԭʼ���ݼ�С�ö���еĲż�����.
-- ��������: B��, λͼ, ���������(�ȿ龺��), ����
create index idx_bankrecord_baid
on bankrecord( baid );

--����3: ����Щ�����Ƚϸ��ӣ�����������ͼ���Խ�ʡ����.  
--   ����ˮ

--   ����ˮ 
grant create any view to scott;    --��scott�û����贴����ͼ��Ȩ��

create or replace view view_bankrecord_aweek
as
select * from scott.bankrecord 
where    optime>  add_months(trunc(sysdate),-1)
order by optime desc;


select * from view_bankrecord_aweek;


insert into bankaccount values( '1','����','5eb03b8172b979cd387f25e26982b313',1.0);
insert into bankaccount values( '2','����','5eb03b8172b979cd387f25e26982b313',1.0);

commit;

select * from bankaccount;
select * from bankrecord;


select * from bankrecord where baid=? order by optime desc



