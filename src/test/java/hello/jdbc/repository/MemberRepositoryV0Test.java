package hello.jdbc.repository;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import hello.jdbc.domain.Member;

public class MemberRepositoryV0Test {
    MemberRepositoryV0 repository = new MemberRepositoryV0();
    @Test
    void crud() throws SQLException {
        
        Member member = new Member("member1",10000);
        repository.save(member);
    }
}
