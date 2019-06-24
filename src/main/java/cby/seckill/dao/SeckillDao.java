package cby.seckill.dao;


import cby.seckill.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/*

 */
public interface SeckillDao {
    /*
    减库存的接口，
     */

    /**
     *
     * @param seckillId
     * @param killTime
     * @return 影响的行数，表示更新的行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /*
    查询接口，根据ID
     */
    Seckill queryById(long seckllId);

    /**
     *     查询列表的接口,根据偏移量，
     *     java没有保存形参的纪录int offer, int limit会变成arg 0  arg1
     *     所以多个参数我们用@Param注释
     * @param offer
     * @param limit 查询的熟练
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offer, @Param("limit")int limit);

    void killByProcedure(Map<String,Object> paramMap);
}
