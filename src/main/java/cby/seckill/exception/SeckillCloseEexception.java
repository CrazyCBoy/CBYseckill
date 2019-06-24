package cby.seckill.exception;

/**
 * 秒杀关闭异常
 */
public class SeckillCloseEexception extends SeckillException {
    public SeckillCloseEexception() {
    }

    public SeckillCloseEexception(String message) {
        super(message);
    }

    public SeckillCloseEexception(String message, Throwable cause) {
        super(message, cause);
    }

    public SeckillCloseEexception(Throwable cause) {
        super(cause);
    }

    public SeckillCloseEexception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
