package cby.seckill.dto;


import cby.seckill.entity.SuccessKilled;
import cby.seckill.enums.SeckillStateEnum;

/**
 * 封装秒杀执行后的结果
 */
public class SeckillExecution {
    //秒杀产品编号
    private long seckiiId;
    //秒杀结果状态
    private  int state;
    //秒杀成功信息
    private String statInfo;

    //秒杀成功对象
    private SuccessKilled successKilled;

    public SeckillExecution(long seckiiId, SeckillStateEnum seckillStateEnum) {
        this.seckiiId = seckiiId;
        this.state = seckillStateEnum.getState();
        this.statInfo = seckillStateEnum.getStateInfo();
    }

    public SeckillExecution(long seckiiId, SeckillStateEnum seckillStateEnum, SuccessKilled successKilled) {
        this.seckiiId = seckiiId;
        this.state = seckillStateEnum.getState();
        this.statInfo = seckillStateEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    public long getSeckiiId() {
        return seckiiId;
    }

    public void setSeckiiId(long seckiiId) {
        this.seckiiId = seckiiId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStatInfo() {
        return statInfo;
    }

    public void setStatInfo(String statInfo) {
        this.statInfo = statInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "seckiiId=" + seckiiId +
                ", state=" + state +
                ", statInfo='" + statInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }
}
