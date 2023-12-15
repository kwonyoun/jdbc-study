package hello.jdbc.exception.basic;

import java.net.ConnectException;
import java.sql.SQLException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CheckedAppTest {

    @Test
    void checked(){
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() ->  controller.request())
                .isInstanceOf(SQLException.class);
                /**
                 * isInstanceOf(Exception.class) 
                 * -> 상위 클래스이기 때문에 테스트 통과가능하다! 
                 * 하지만 최상위타입이기때문에 다른 체크 예외를 체크할 수 있는 기능이 무효화되고, 중요한 체크예외를 다 놓치게 된다. 
                 * 좋은 방법이 아니다.
                 */
    }

    static class Controller {
        Service service = new Service();
        
        public void request() throws ConnectException, SQLException{
            service.logic();
            /**
             * 여기서 처리할 수 없으니 다시 밖으로 던진다. 
             */
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() throws SQLException, ConnectException{
            repository.call();
            networkClient.call();
            /**
             * 둘 다 밖으로 던져야함. 
             */
        }
    }

    static class NetworkClient {
        public void call() throws ConnectException{
            throw new ConnectException("연결 실패");
            /**
             * ConnectException은 checkException이어서 밖으로 던짐.
             */
        }
    }

    static class Repository {
        public void call() throws SQLException{
            throw new SQLException("ex");
            /**
             * SQLException은 checkException이기때문에 밖으로 던짐.
             */
        }
    }
    
}
