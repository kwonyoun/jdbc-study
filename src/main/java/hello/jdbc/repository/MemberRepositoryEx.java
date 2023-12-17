package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import java.sql.SQLException;

public interface MemberRepositoryEx {
 Member save(Member member) throws SQLException;
 Member findById(String memberId) throws SQLException;
 void update(String memberId, int money) throws SQLException;
 void delete(String memberId) throws SQLException;
 /**
  * implements를 하면 인터페이스도 똑같이 예외를 던져줘야한다.
  */
}