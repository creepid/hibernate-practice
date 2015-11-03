package org.hibernate.auction.persistence;

import java.io.Serializable;
import org.hibernate.*;
import org.hibernate.auction.model.CreditCardType;

import java.sql.*;
import org.hibernate.usertype.UserType;

/**
 * Hibernate custom mapping type for CreditCardType.
 * <p>
 * This mapping type persists credit card type information to a
 * <tt>SMALLINT</tt> database column.
 *
 * @see CreditCardType
 * @author Christian Bauer <christian@hibernate.org>
 */
public class CreditCardTypeUserType implements UserType {

    private static final int[] SQL_TYPES = {Types.SMALLINT};

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Class returnedClass() {
        return CreditCardType.class;
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

        int typeCode = resultSet.getInt(names[0]);
        return resultSet.wasNull() ? null : CreditCardType.getInstance(typeCode);
    }

    public void nullSafeSet(PreparedStatement statement,
            Object value,
            int index)
            throws HibernateException, SQLException {

        if (value == null) {
            statement.setNull(index, Types.SMALLINT);
        } else {
            statement.setInt(index, ((CreditCardType) value).toInteger().intValue());
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
