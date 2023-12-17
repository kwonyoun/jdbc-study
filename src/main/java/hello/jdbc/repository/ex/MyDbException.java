package hello.jdbc.repository.ex;

/**
 * RuntimeException 상속받았기 때문에 얘는 당연히 언체크예외이다.
 */
public class MyDbException extends RuntimeException{

    public MyDbException() {
        super();
    }

    public MyDbException(String message) {
        super(message);
    }

    public MyDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDbException(Throwable cause) {
        super(cause);
    }
    
}
