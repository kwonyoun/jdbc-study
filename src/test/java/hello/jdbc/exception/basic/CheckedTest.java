package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckedTest {

    @Test
    void checked_catch(){
        Service service = new Service();
        service.callCatch(); 
        /** callCatch 메서드
         * -> call메서드
         * -> repository에서 exception을 터뜨림        
         * -> service에서 catch에서 잡아서 로그를 남김
         * ->예외를 잡아서 정상흐름으로 반환/리턴
         * -> 테스트 성공*/
    }

    @Test
    void cheked_throw() {
        Service service = new Service();
        /**
         * service.callThrow() 라는 메서드를 호출하면 MyCheckedException이 터저야 test에 통과한다. 
         */
        Assertions.assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(MyCheckedException.class);
            /**
             * test에서는 예외를 선언하지 않는 이유?
             * test는 예외가 발생하지않아야 통과이기 때문에, 예외가 터지는 상황자체를 통과로 설정한 것이다. 
             */
    }

    /**
     * Exception을 상속받은 예외는 체크예외가 된다. 
     */
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    } 

    /**
     * Checked 예외는
     * 예외를 잡아서 처리하거나, 던지거나 둘중 하나를 필수로 선택해야한다.  
     */
    static class Service {
        Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리하는 코드
         */
        public void callCatch(){
            try {
                repository.call();
            } catch (MyCheckedException e) { //MyCheckedException은 Exception을 상속받기때문에, Exception으로 놓아도된다. 대신 모든 Exception을 다 잡아버린다(하위이기때문). 
                // 예외처리 로직
                log.info("예외 처리, message={}",e.getMessage(), e);
            }
        }

        /**
         * 체크예외를 밖으로 던지는 코드
         * 체크예외는 예외를 잡지않고 밖으로 던지려면 throws 예외를 메서드에 필수로 선언해야한다.
         * @throws MyCheckedException
         */
        public void callThrow() throws MyCheckedException{
            /* 예외선언해주기- 아니면 컴파일 오류남 */
            repository.call();
        }
    }

    static class Repository {
        public void call() throws MyCheckedException{
            //체크예외를 던지기 위해서는 선언해줘야한다. throws MycheckedException 
            //그렇지않으면 컴파일이 되지않는다.
            //잡아서 처리를 하거나 던지거나 둘 중 하나는 해야한다. 
            //체크예외는 밖으로 던지는걸 선언해줘야한다. 
            throw new MyCheckedException("ex");
        }
    }
}
