package hello.jdbc.service;

import java.sql.Connection;
import java.sql.SQLException;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료 
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;


    public void accountTransfer(String fromId, String toId, int money) throws SQLException{

        Connection con = dataSource.getConnection();

        /* 비즈니스 로직을 제외한 모든 코드가 트랜잭션이다.  */
        try {
            //false로 설정해야 트랜잭션이 시작.
            con.setAutoCommit(false); 
            /*
             * 자동커밋모드를 수동커밋코드로 변경하는 것을 트랜잭션을 시작한다고한다. 
             */

            //비즈니스 로직
            bizLogic(con, fromId, toId, money);

            con.commit(); //성공시 커밋

        } catch (Exception e) {
            con.rollback(); //예외발생하여 실패시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        } 
    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException{
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);
        //fromId 회원의 돈을 money만큼 감소한다.
        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        //toId회원의 돈을 money만큼 증가한다.
        memberRepository.update(con, toId, toMember.getMoney() + money);
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
