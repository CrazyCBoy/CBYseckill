package cby.seckill.service;

import cby.seckill.dto.Exposer;
import cby.seckill.dto.SeckillExecution;
import cby.seckill.entity.Seckill;
import cby.seckill.exception.RepeatKillException;
import cby.seckill.exception.SeckillCloseEexception;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉juntisppring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;
    @Test
    public void getSeckillList() {
        List<Seckill> list=seckillService.getSeckillList();
        logger.info("List={}",list);
    }

    @Test
    public void getById() {
        long id=1001;
        Seckill seckill=seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    @Test
    public void exportSecikiiUrl() {
        long id=1001;
        Exposer exposer=seckillService.exportSecikiiUrl(id);
        if(exposer.isExposed()){
            logger.info("exposer={}"+exposer);
            long phone = 12595678067L;
            String md5=exposer.getMd5();
            try{
                SeckillExecution execution=seckillService.executeSeckill(id,phone,md5);
                logger.info("resut={}",execution);
            }catch (RepeatKillException e){
                logger.error(e.getMessage());
            }catch (SeckillCloseEexception e){
                logger.error(e.getMessage());
            }
        }else {
            //警告秒杀未开启
            logger.warn("exposer={}",exposer);

        }
    }

}