package hello.jdbc.repository;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberRepositoryV0Test {
    MemberRepositoryV0 repository = new MemberRepositoryV0();
    @Test
    void crud() throws SQLException {
        
        //save
        Member member = new Member("member7",10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        assertThat(findMember).isEqualTo(member);

        //update
        repository.update(member.getMemberId(),20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delete(member.getMemberId());;
        assertThatThrownBy( ()-> repository.findById(member.getMemberId()))
            .isInstanceOf(NoSuchElementException.class);
        //회원이 없기 때문에 NoSuchElementException 이 발생한다. assertThatThrownBy 는 해당 예외가 발생해야 검증에 성공한다.

    }
}
