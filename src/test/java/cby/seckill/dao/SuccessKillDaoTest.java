package cby.seckill.dao;

import cby.seckill.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
//告诉juntisppring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKillDaoTest {
    @Resource
    private SuccessKillDao successKillDao;
    @Test
    public void insertSuccessKilled() {
        long id = 1001L;
        long phone = 13694578067L;
        int insertCount = successKillDao.insertSuccessKilled(id,phone);
        System.out.println("insertCount = " + insertCount);
        //第一次执行insertCount = 1
        //第二次执行insertCount = 0，说明不允许重复秒杀，
    }

    @Test
    public void queryByIdWithSeckill() {
        long id = 1001L;
        long phone = 13694578067L;
        SuccessKilled successKilled = successKillDao.queryByIdWithSeckill(id,phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }
}