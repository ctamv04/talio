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
                StringBuilder builder=new StringBuilder();
                for(int i=0;i<8;i++)
                    builder.append(secureRandom.nextInt(10));

                statement.setString(1,builder.toString());
                ResultSet rs = statement.executeQuery();

                if (!rs.next())
                    return builder.toString();
            }
        } catch (SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }
}
