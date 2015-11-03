package org.hibernate.auction.persistence;

import java.io.Serializable;
import org.hibernate.auction.model.Rating;

import java.sql.*;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * Hibernate custom mapping type for Rating.
 * <p>
 * This mapping type persists comment ratings to a <tt>VARCHAR</tt>
 * database column.
 *
 * @see Rating
 * @author Christian Bauer <christian@hibernate.org>
 */
public class RatingUserType implements UserType {

    private static final int[] SQL_TYPES = {Types.VARCHAR};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return Rating.class;
    }

    public boolean equals(Object x, Object y) {
        return x == y;
    }

    public Object deepCopy(Object value) {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Object nullSafeGet(ResultSet resultSet,
            String[] names,
            Object owner)
            throws HibernateException, SQLException {

        String name = resultSet.getString(names[0]);
        return resultSet.wasNull() ? null : Rating.getInstance(name);
    }

    public void nullSafeSet(PreparedStatement statement,
            Object value,
            int index)
            throws HibernateException, SQLException {

        if (value == null) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value.toString());
        }
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return System.identityHashCode(o);
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object o) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

}
