package generators;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("all")
public class BoardIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Connection connection = session.connection();
        try{
            SecureRandom secureRandom = new SecureRandom();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM BOARD WHERE id=?");
            while (true) {
                Long id=secureRandom.nextLong(10000000,100000000);

                statement.setLong(1,id);
                ResultSet rs = statement.executeQuery();

                if (!rs.next())
                    return id;
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }
}
