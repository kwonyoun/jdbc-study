package hello.jdbc.connection;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class DBConnectionUtilTest {
    
    @Test
    void connection(){
        Connection connection = DBConnectionUtil.getConnection();
        assertThat(connection).isNotNull();
    }
}
