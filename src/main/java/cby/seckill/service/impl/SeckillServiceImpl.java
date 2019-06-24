package cby.seckill.service.impl;

import cby.seckill.dao.SeckillDao;
import cby.seckill.dao.SuccessKillDao;
import cby.seckill.dao.cache.RedisDao;
import cby.seckill.dto.Exposer;
import cby.seckill.dto.SeckillExecution;
import cby.seckill.entity.Seckill;
import cby.seckill.entity.SuccessKilled;
import cby.seckill.enums.SeckillStateEnum;
import cby.seckill.exception.RepeatKillException;
import cby.seckill.exception.SeckillCloseEexception;
import cby.seckill.exception.SeckillException;
import cby.seckill.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKillDao successKillDao;
    //盐，用于混淆md5
    private final String slat="jk@#k54512~-*/errnvpq";
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }
    @Autowired
    private RedisDao redisDao;

    public Exposer exportSecikiiUrl(long seckillId) {
        //通过redis缓存接口的暴露:超时维护一致性
        /**
         * get from cache
         * if null  get db
         * else put cache
         */
        //1.访问redis
        Seckill seckill=redisDao.getSeckill(seckillId);
        if(redisDao.getSeckill(seckillId)==null){
            //如果为null，访问数据库
            seckill=seckillDao.queryById(seckillId);
            if(seckill==null){
                //如果数据库中的为空，表示秒杀单不存在
                return new Exposer(false,seckillId);
            }else {
                //放入redis
                redisDao.putSeckill(seckill);
            }
        };
        Date stateTime=seckill.getStartTime();
        Date endTime=seckill.getEndTime();
        //当前系统时间做比对
        Date nowTime=new Date();
        if(nowTime.getTime()<stateTime.getTime()||nowTime.getTime()>endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),stateTime.getTime(),endTime.getTime());
        }
        //todo表示之后修改 TODO
        //转换特定字符串的过程，md5最大的特点是不可逆
        String md5=getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    @Transactional
    /**
     * 使用注解控制事务方法的优点：
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP(或者剥离到事务方法外部)----
     * 当第一次去数据库的时候，会开启事务，当运行期异常会回滚rollback，当没有异常则commit
     * 3.不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制，例如该方法中有插入，查询等操作
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseEexception {
        if(md5==null || !md5.equals(getMD5(seckillId))){
            throw  new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑，减库存+纪录购买行为
        Date nowTime=new Date();
        try {
            //如果减库存成功 纪录购买行为
            int insertCount=successKillDao.insertSuccessKilled(seckillId,userPhone);
            if(insertCount<=0){
                //意味着重复秒杀
                throw new RepeatKillException("重复秒杀了");
            }else {
                int updateCount=seckillDao.reduceNumber(seckillId,nowTime);
                if(updateCount<=0){
                    throw  new SeckillCloseEexception("秒杀结束");
                }else {
                    //秒杀成功了
                    SuccessKilled successKilled=successKillDao.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
                }
            }
        }catch (SeckillCloseEexception e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }
        catch (Exception e){
            logger.error(e.getMessage(),e);
            throw  new SeckillException("所有编译器异常转化为运行期异常");
        }
    }

    private  String getMD5(long seckillId){
        String base =seckillId +"/"+slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    };

    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseEexception {
        if (md5 == null ||!md5.equals(getMD5(seckillId))){
            return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        //执行存储过程，result被赋值
        try {
            seckillDao.killByProcedure(map);
            /*
            这里要使用MapUtils需要引入依赖
             <dependency>
                <groupId>commons-collections</groupId>
                <artifactId>commons-collections</artifactId>
                   <version>3.2.1</version>
              </dependency>
             */
            int result = MapUtils.getInteger(map,"result",-2);
            System.out.println(result);
            if (result == 1){
                SuccessKilled successKilled = successKillDao
                        .queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,successKilled);
            }else {
                return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
        }
    }

}
