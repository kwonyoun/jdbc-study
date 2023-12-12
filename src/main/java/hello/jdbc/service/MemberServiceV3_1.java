package hello.jdbc.service;

import java.sql.Connection;
import java.sql.SQLException;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    // private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;


    public void accountTransfer(String fromId, String toId, int money) throws SQLException{

        //트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());


        /* 비즈니스 로직을 제외한 모든 코드가 트랜잭션이다.  */
        try {
            //비즈니스 로직
            bizLogic(fromId, toId, money);
            transactionManager.commit(status); //성공시 커밋
        } catch (Exception e) {
            transactionManager.rollback(status); //실패시 롤백
            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException{
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);
        //fromId 회원의 돈을 money만큼 감소한다.
        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        //toId회원의 돈을 money만큼 증가한다.
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    //예외상황테스트를 위해 toId가 ex인 경우 예외를 발생시킨다. 
    private void validation(Member toMember){
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

    private void release(Connection con){
        if (con != null) {
            try {
                con.setAutoCommit(true); //커넥션 풀 고려 //true로 바꿔줘야 커넥션 풀에서는 true인 상태로 유지가 된다. 
                con.close();
            } catch (Exception e) {
                log.info("erroe",e);
            }
        }
    }

    
}
