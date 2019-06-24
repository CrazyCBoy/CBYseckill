package cby.seckill.dao.cache;

import cby.seckill.entity.Seckill;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class RedisDao {
    //类似数据库零连接池的pool
    private JedisPool jedisPool;

    private Logger logger= LoggerFactory.getLogger(this.getClass());


    public RedisDao(String ip ,int port){
        jedisPool=new JedisPool(ip,port);
    }
    //全局定义一个运行期Scheama,性能几乎没有损失
    // class代表类的字节码对象，这个对象通过反射拿到这个字节码属性和方法
    //RuntimeSchema是基于这个class去做一个模式，当再去创建对象时，会根据模式赋予值
    // 这是序列化的本质
    private RuntimeSchema<Seckill> schema=RuntimeSchema.createFrom(Seckill.class);
    public Seckill getSeckill(long seckillId){
        //redis操作逻辑
        try {
            //拿到jedis对象
            Jedis jedis=jedisPool.getResource();

            try {
                //构造key
                String key="seckilll:"+seckillId;
                //redis或者jedis没有实现内部序列化操作
                //get得到的是一个二进制数组 get ->byte[]  反序列化 -》object（Seckill）
                //高并发中序列化重要
                //采用自定义序列化方法
                byte[] bytes = jedis.get(key.getBytes());
                //如果不为空，说明获取到对象了
                if(bytes!=null){
                    //创建一个空对象
                    Seckill seckill=schema.newMessage();
                    //根据字节数组存放数据，空对象会按照schema将数据传到空对象里，完成赋值，被反序列化了
                    ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);
                    //这样写能省5-10倍的空间，更加节省cpu
                    return seckill;
                }
            }finally {
                //关闭连接
                jedis.close();
            }

        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    public  String putSeckill(Seckill seckill){
       /* //set方法，首先Object(Seckill )--》序列化---》》byte[]
        try {
            //获取redis对象
            Jedis jedis=jedisPool.getResource();
            try {
                //构造一个key
                String key="seckill:"+seckill.getSeckillId();
                //array数组，LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE)缓存器，LinkedBuffer.DEFAULT_BUFFER_SIZE缓存大小
                byte[] bytes=ProtostuffIOUtil.toByteArray(seckill,schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //拿到字节数组后：超时缓存
                int timeout =60*60;//1小时
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }*/
        try {
            Jedis jedis = jedisPool.getResource();
            //set Object->序列化->bytes[]
            try {
                String key = "seckill:"+seckill.getSeckillId();
                byte[] bytes = ProtobufIOUtil.toByteArray(seckill,schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

                //缓存超时
                int timeout = 60*60;
                String result = jedis.setex(key.getBytes(),timeout,bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
