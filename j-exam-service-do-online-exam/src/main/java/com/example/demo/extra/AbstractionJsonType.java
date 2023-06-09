package com.example.demo.extra;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.SerializationException;
import org.hibernate.usertype.UserType;

public abstract class AbstractionJsonType<T> implements UserType<T> {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final Supplier<T> emptySupplier;

	private final JavaType javaType;

	AbstractionJsonType(TypeReference<T> typeRef, Supplier<T> emptySupplier) {
		this.emptySupplier = emptySupplier;
		javaType = MAPPER.getTypeFactory().constructType(typeRef);
	}

	@Override
	public int getSqlType() {
		return Types.JAVA_OBJECT;
	}

	@Override
	public Class<T> returnedClass() {
		return (Class<T>) javaType.getRawClass();
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y) {
			return true;
		}
		if ((x == null) || (y == null)) {
			return false;
		}
		return x.equals(y);
	}

	/**
	 * @throws HibernateException NPE if null
	 */
	@Override
	public int hashCode(Object object) throws HibernateException {
		return Objects.requireNonNull(object).hashCode();
	}

	@Override
	public T nullSafeGet(ResultSet rs, int i, SharedSessionContractImplementor session, Object owner)
			throws HibernateException, SQLException {
		String cellContent = rs.getString(i);

		if (Strings.isBlank(cellContent)) {
			return emptySupplier == null ? null : emptySupplier.get();
		}
		try {
			return MAPPER.readValue(cellContent, javaType);
		} catch (Exception ex) {
			throw new HibernateException(ex);
		}
	}

	@Override
	public void nullSafeSet(
			PreparedStatement ps, Object value, int index, SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		if (value == null) {
			ps.setNull(index, Types.OTHER);
			return;
		}
		try {
			ps.setObject(index, MAPPER.writeValueAsString(value), Types.OTHER);
		} catch (Exception ex) {
			throw new HibernateException(ex);
		}
	}

	/**
	 * Deep copy JsonNode by serializing to bytes with jackson then back to JsonNode
	 *
	 * @param value object
	 * @return Deep Copy
	 * @throws HibernateException IOException from mapper fromCSV tree
	 */
	@Override
	public T deepCopy(Object value) throws HibernateException {
		return MAPPER.convertValue(value, javaType);
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		Object deepCopy = deepCopy(value);

		if (deepCopy instanceof Serializable) {
			return (Serializable) deepCopy;
		}
		throw new SerializationException(
				String.format("deepCopy of %s is not serializable", value), null);
	}

	@Override
	public T assemble(Serializable cached, Object owner) throws HibernateException {
		return deepCopy(cached);
	}

	@Override
	public T replace(T original, T target, Object owner) throws HibernateException {
		return deepCopy(original);
	}
}
