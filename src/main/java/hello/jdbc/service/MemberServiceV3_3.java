package hello.jdbc.service;

import java.sql.Connection;
import java.sql.SQLException;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 트랜잭션 - @Transactional AOP
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepository;

    //이 메서드를 호출할 때 트랜잭션을 걸고 시작하겠다. 
    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException{
        bizLogic(fromId, toId, money); 
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
}
