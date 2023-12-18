package hello.jdbc.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;

/*
 * SQLExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository {

    private final DataSource dataSource;
    private final SQLExceptionTranslator exTranslator;
    public MemberRepositoryV4_2(DataSource dataSource) { 
        this.dataSource = dataSource; 
        this.exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public Member save(Member member) {

        /*
         * PreparedStatement는 파라미터값을 바인딩할 수 있고,
         * Statement는 값을 바인딩할 수 없고 문장 그대로 받는다.
         * SQL Injection 공격을 예방하려면 PreparedStatement를 통한 파라미터 바인딩 방식을 사용해야한다.
         */

        //쿼리문
        String sql = "insert into member(member_id, money) values(?,?)";
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            //연결 메서드 호출
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2,member.getMoney());
            pstmt.executeUpdate(); //준비된 값들이 데이터베이스에 실행된다. 
            return member;
        } catch (SQLException e) {
            throw exTranslator.translate("save", sql, e);
        } finally{
            //호출이 보장되도록 finally에서 실행한다. 
            close(con, pstmt, null); //DB close 메서드 호출
        }

    }

    @Override
    public Member findById(String memberId) {
        String sql = "select * from member where member_id = ?";
        Connection con =null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
         try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery(); //select은 executeQuery() 사용한다. 
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId ="+memberId);
            }
         } catch (SQLException e) {
            throw exTranslator.translate("findById", sql, e);
         }
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "update member set money=? where member_id = ?";
        Connection con =null;
        PreparedStatement pstmt = null;
        try {
            //연결 메서드 호출
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate(); //준비된 값들이 데이터베이스에 실행된다. 
            log.info("resultSize={}",resultSize);
        } catch (SQLException e) {
            throw exTranslator.translate("update", sql, e);
        } finally{
            //호출이 보장되도록 finally에서 실행한다. 
            close(con, pstmt, null); //DB close 메서드 호출
        }
    }

    @Override
    public void delete(String memberId) {
        String sql ="delete from member where member_id = ?";
        Connection con =null;
        PreparedStatement pstmt = null;
        try {
            //연결 메서드 호출
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate(); //준비된 값들이 데이터베이스에 실행된다. 
        } catch (SQLException e) {
            throw exTranslator.translate("delete", sql, e);
        } finally{
            //호출이 보장되도록 finally에서 실행한다. 
            close(con, pstmt, null); //DB close 메서드 호출
        }
    }

    //DB close 메서드
    private void close(Connection con, Statement st, ResultSet rs){
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(st); 
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야한다.
        DataSourceUtils.releaseConnection(con, dataSource);
    }

    //DB 연결 메서드
    private Connection getConnection() throws SQLException{
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야한다. 
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
