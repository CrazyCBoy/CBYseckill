package cby.seckill.dao;

import cby.seckill.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

public interface SuccessKillDao {

    /**
     *    插入购买明细，去重
     * @param seckillId
     * @param userPhone
     * @return 插入的行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);

    /*
    查询秒杀成功的功能
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long SeckillId,@Param("userPhone") long userPhone);
}
