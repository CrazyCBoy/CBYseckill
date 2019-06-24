--秒杀执行存储过程
--结束符由;->$$
DELIMITER $$
--定义存储过程
--参数：in：输入参数 out：输出参数
--row_count()返回上一条修改类型sql（update,insert,delete）的影响函数
--row_count:0:未修改数据  >0表示修改的行数 <0:sql错误/未执行修改sql
CREATE PROCEDURE `seckill`.`execute_seckill`(IN v_seckill_id bigint,IN v_phone bigint,IN v_kill_time TIMESTAMP ,out r_result INT )
BEGIN
 DECLARE insert_count INT DEFAULT 0;
 start TRANSACTION ;
 INSERT ignore INTO success_killed(seckill_id,user_phone,create_time) VALUES (v_seckill_id,v_phone,v_kill_time);
 SELECT ROW_COUNT() INTO insert_count;
 IF(insert_count=0) THEN
   ROLLBACK ;
   SET r_result=-1;
 ELSEIF(insert_count<0) THEN
   ROLLBACK ;
   SET r_result=-2;
 ELSE
    update seckill set number = number - 1
     where seckill_id = v_seckill_id
      and start_time >v_kill_time
      and end_time < v_kill_time
      and number > 0;
   SELECT ROW_COUNT() INTO insert_count;
   IF(insert_count=0) THEN
     ROLLBACK ;
     SET r_result=0;
   ELSEIF(insert_count<0) THEN
     ROLLBACK ;
     SET r_result=-2;
   ELSE
     COMMIT ;
     SET r_result=1;
   END IF;
 END IF;
END ;
$$

--调用存储过程

DELIMITER ;
set @r_result = -3;
call execute_seckill(1003,13794578067,now(),@r_result);

--获取结果
select @r_result;
--存储过程优化：1.优化的是事务行级锁持有的时间
--2.不要过度依赖存储过程，一般在银行大范围使用,互联网公司一般不使用
--3.简单的逻辑，可以应用存储过程
--4.QPS可以达到一个秒杀单接近6000qps,

/*
--秒杀执行存储过程
DELIMITER $$  --console
--定义存储过程：
--参数：in表示输入参数；out表示输出参数
create procedure 'seckill'.'exectue_seckill'
（in v_seckill_id bigint,in v_phone bigint,
      in v_kill_time timestamp,out r_result int）
      --开始
      BEGIN
      --定义变量:变量名:insert_count int类型，默认0
        DECLARE insert_count int  DEFAULT 0;
        --开启事务
        START TRANSACTION ;
        --插入用户购买明细
        insert  ignore into success_skilled
            (seckill_id,user_phone,create_time)
            values (v_seckill_id,v_phone,v_kill_time)
        --row_count()函数返回上一条修改类型sql(delete/insert/update)的影响行数
        select row_count() into insert_counnt;
        --判断row_count()：如果为0，表示未修改数据，如果>0，表示修改的行数，如果<0，则表示sql错误
        if(insert_counnt = 0) then
            ROLLBACK ;
            set r_result=-1;
        ELSEIF(insert_counnt<0) then
            ROLLBACK ;
            set r_result=-2;
        else
            update seckill set number  = number -1
            where seckill_id = v_seckill_id
                and end_time > v_kill_time
                and start_time < v_kill_time
                and number > 0;
            select row_count() into insert_counnt;
            IF(insert_count == 0)THEN
                ROLLBACK;
                set r_result = 0;
            ELSEIF(insert_count < 0)THEN
                ROLLBACK;
                set r_result = -2;
            ELSE
                COMMIT;
                set r_result = 1;
            END IF;
        end if;
    end ;
--存储过程定义结束
$$;

--调用存储过程
---1.修改换行符为：
DELIMITER ;
--1.定义变量,定义变量通过@
set @r_result=-3;
--执行存储过程
call execute_seckill(1003,13794578067,now(),@r_result);

--获取结果
select @r_result
*/

