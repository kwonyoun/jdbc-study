package hello.jdbc.service;

import java.sql.Connection;
import java.sql.SQLException;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {

    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException{

        //이 코드안에서 트랜잭션을 시작한다. 
        //비즈니스로직 실행 -> txTemplate.excuteWithoutResult() 코드가 끝났을 때 이 로직이 성공적으로 반환되면 commmit, 예외가 발생한다면 rollback이 동작한다. 
        txTemplate.executeWithoutResult((status) -> {
            //비즈니스 로직
            try {
                bizLogic(fromId, toId, money);    
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
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
