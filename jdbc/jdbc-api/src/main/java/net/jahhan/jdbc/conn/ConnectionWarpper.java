package net.jahhan.jdbc.conn;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.StringUtils;

import net.jahhan.common.extension.context.BaseContext;
import net.jahhan.common.extension.utils.LogUtil;
import net.jahhan.jdbc.context.DBContext;
import net.jahhan.jdbc.context.DBVariable;
import net.jahhan.jdbc.event.DBEvent;
import net.jahhan.jdbc.event.EventOperate;

public class ConnectionWarpper implements Connection {
	private DBContext appContext = BaseContext.CTX.getInjector().getInstance(DBContext.class);

	private Connection inner;

	private List<Statement> stmts;

	private boolean _readOnly = false;

	private int type;

	private List<DBEvent> events = new ArrayList<>();

	private void addStatement(Statement stmt) {
		stmts.add(stmt);
	}

	private void resetStatement() {
		stmts.clear();
	}

	public ConnectionWarpper(Connection inner, int type) {
		this.inner = inner;
		this.type = type;
		stmts = new ArrayList<Statement>();
	}

	public int getType() {
		return type;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return inner.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return inner.isWrapperFor(iface);
	}

	@Override
	public Statement createStatement() throws SQLException {
		Statement stmt = inner.createStatement();
		addStatement(stmt);
		return stmt;
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		PreparedStatement stmt = inner.prepareStatement(sql);
		addStatement(stmt);
		return stmt;
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		CallableStatement stmt = inner.prepareCall(sql);
		this.addStatement(stmt);
		return stmt;
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		return inner.nativeSQL(sql);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		inner.setAutoCommit(autoCommit);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return inner.getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		if (inner == null) {
			throw new SQLException("连接已经关闭");
		}
		if (this._readOnly) {
			throw new SQLException("只读的连接不能commit!!!");
		}
		inner.commit();
		for (DBEvent event : events) {
			appContext.realPublishWrite(event);
		}
	}

	@Override
	public void rollback() throws SQLException {
		inner.rollback();
		events.clear();
		DBVariable.getDBVariable().clearLocalCache();
	}

	@Override
	public void close() throws SQLException {
		DBConnFactory.incFreeConn();
		for (Statement stmt : this.stmts) {
			if (stmt == null) {
				continue;
			}
			try {
				stmt.close();
			} catch (Exception e) {
				LogUtil.error("关闭Statement失败，" + e.getMessage(), e);
			}
		}
		this.resetStatement();
		inner.close();
		this.inner = null;
	}

	@Override
	public int hashCode() {
		return inner.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().isInstance(obj)) {
			return false;
		}
		ConnectionWarpper w = (ConnectionWarpper) obj;
		return this.inner.equals(w.inner);
	}

	@Override
	public boolean isClosed() throws SQLException {
		if (this.inner == null) {
			return true;
		}
		return inner.isClosed();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return inner.getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		this._readOnly = readOnly;
		inner.setReadOnly(readOnly);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return inner.isReadOnly();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		inner.setCatalog(catalog);
	}

	@Override
	public String getCatalog() throws SQLException {
		return inner.getCatalog();
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		inner.setTransactionIsolation(level);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return inner.getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return inner.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		inner.clearWarnings();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		Statement stmt = inner.createStatement(resultSetType, resultSetConcurrency);
		addStatement(stmt);
		return stmt;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		PreparedStatement stmt = inner.prepareStatement(sql, resultSetType, resultSetConcurrency);
		addStatement(stmt);
		return stmt;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		CallableStatement stmt = inner.prepareCall(sql, resultSetType, resultSetConcurrency);
		this.addStatement(stmt);
		return stmt;
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return inner.getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		inner.setTypeMap(map);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		inner.setHoldability(holdability);
	}

	@Override
	public int getHoldability() throws SQLException {
		return inner.getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return inner.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return inner.setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		inner.rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		inner.releaseSavepoint(savepoint);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		Statement stmt = inner.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		addStatement(stmt);
		return stmt;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		PreparedStatement stmt = inner.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		addStatement(stmt);
		return stmt;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		CallableStatement stmt = inner.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		this.addStatement(stmt);
		return stmt;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		PreparedStatement stmt = inner.prepareStatement(sql, autoGeneratedKeys);
		this.addStatement(stmt);
		return stmt;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		PreparedStatement stmt = inner.prepareStatement(sql, columnIndexes);
		this.addStatement(stmt);
		return stmt;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		PreparedStatement stmt = inner.prepareStatement(sql, columnNames);
		this.addStatement(stmt);
		return stmt;
	}

	@Override
	public Clob createClob() throws SQLException {
		return inner.createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return inner.createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return inner.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return inner.createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return inner.isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		inner.setClientInfo(name, value);
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		inner.setClientInfo(properties);
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return inner.getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return inner.getClientInfo();
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return inner.createArrayOf(typeName, elements);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return inner.createStruct(typeName, attributes);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		inner.setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		return inner.getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		inner.abort(executor);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		inner.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return inner.getNetworkTimeout();
	}

	public void addEvent(DBEvent event) {
		events.add(event);
		if (DBVariable.getDBVariable().isWriteConnection(event.getDataSource())) {
			String id = event.getId();
			String op = event.getOperate();
			if (StringUtils.isEmpty(id)) {
				return;
			}
			if (op.equals(EventOperate.INSERT) || op.equals(EventOperate.UPDATE)) {
				Object sourceObject = event.getSource();
				DBVariable.getDBVariable().addPojo(sourceObject.getClass(), id, sourceObject);
			} else if (EventOperate.isModify(op)) {
				DBVariable.getDBVariable().delPojo(event.getSource().getClass(), id);
			}
		}
	}

	public String toString() {
		return type + "@" + Integer.toHexString(hashCode());
	}

}
