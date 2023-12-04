package hello.jdbc.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

/*
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException{

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
            log.info("db error",e);
            throw e;
        } finally{
            //외부 리소스를 사용하기때문에 꼭 닫아줘야한다.
            //pstmt에서 exception이 터지면 con은 close가 되지않는다. 따라서 try-catch로 감싸야한다.
            // pstmt.close();
            // con.close();

            //호출이 보장되도록 finally에서 실행한다. 
            close(con, pstmt, null); //DB close 메서드 호출
        }

    }

    public Member findById(String memberId) throws SQLException{
        String sql = "select * from member where member_id = ?";
        Connection con =null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
         try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId ="+memberId);
            }
         } catch (SQLException e) {
            log.error("db erorr", e);
            throw e;
         }
    }

    //DB close 메서드
    private void close(Connection con, Statement st, ResultSet rs){
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error",e);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error",e);
            }
        }
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                log.info("error",e);
            }
        }
    }

    //DB 연결 메서드
    private Connection getConnection(){
        return DBConnectionUtil.getConnection();
    }
}
