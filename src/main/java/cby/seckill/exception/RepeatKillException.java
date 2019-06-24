package cby.seckill.exception;

/**
 * 重复秒杀异常（运行期异常）
 * 事务只接收运行期异常的回滚策略
 */
public class RepeatKillException extends  SeckillException{

    public RepeatKillException() {
    }

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepeatKillException(Throwable cause) {
        super(cause);
    }

    public RepeatKillException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
