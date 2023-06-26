package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException {

    /* "Alt + Insert" -> "Override Methods"에서 RuntimeException 에 해당하는 것을 모두 생성 */

    public NotEnoughStockException() {
        super();
    }

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }

    /* 이것은 없어도 됨...
    protected NotEnoughStockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    */
}
