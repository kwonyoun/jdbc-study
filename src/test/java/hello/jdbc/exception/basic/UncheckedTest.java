package hello.jdbc.exception.basic;

import java.sql.SQLException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UncheckedTest {
    
    @Test
    void unchecked(){
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() ->  controller.request())
                .isInstanceOf(RuntimeSQlException.class);
                /**
                 * isInstanceOf(Exception.class) 
                 * -> 상위 클래스이기 때문에 테스트 통과가능하다! 
                 * 하지만 최상위타입이기때문에 다른 체크 예외를 체크할 수 있는 기능이 무효화되고, 중요한 체크예외를 다 놓치게 된다. 
                 * 좋은 방법이 아니다.
                 */
    }

    @Test
    void printEx(){
        Controller controller = new Controller();
        try {
            controller.request();
        } catch (Exception e) {
            // e.printStackTrace();
            log.info("ex",e);
        }
    }

    static class Controller {
        Service service = new Service();
        
        public void request() {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
            /**
             * 호출한 두 메서드 모두 컴파일러가 체크하지않은 unchecked 예외가 된다. 
             * 예외를 밖으로 던지지않아도 된다.
             */
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }
    }

    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQlException(e);
                /**
                 * SQLException이 터지면, throw new RuntimeSQLException으로 바꿔서 던진다. 
                 * RuntimeSQLException이 이전예외 SQLException를 포함해서 가지고 있을 수 있다. 
                 */
            }
        }

        public void runSQL() throws SQLException{
            throw new SQLException("ex");
        }
    }

    static class  RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQlException extends RuntimeException{
        public RuntimeSQlException(Throwable cause) {
            super(cause);            
            /**
             * 생성자 중 cuase는 왜 예외가 발생했는지 이전 예외를 같이 넣을 수 있다. 
             * repository클래스 메서드call try-catch문 throw new RuntimeSQLException으로 바꿔서 던지는 부분
             */
        }
    }
}
