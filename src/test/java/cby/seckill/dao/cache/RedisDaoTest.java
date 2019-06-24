package cby.seckill.dao.cache;

import cby.seckill.dao.SeckillDao;
import cby.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
//告诉juntisppring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest{

    private  long id=1003;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private SeckillDao seckillDao;
    @Test
    public void testSeckill() {

        //redisDao拿到缓存对象
        //如果为空，从数据库拿，如果拿到的不为空，那么放入redis，然后再取出redis里的sckill
        Seckill seckill=redisDao.getSeckill(id);
        if(seckill==null){
            seckill=seckillDao.queryById(id);
            if(seckill!=null){
                redisDao.putSeckill(seckill);
                seckill=redisDao.getSeckill(id);
                System.out.println(seckill);
            }
        }
    }

}