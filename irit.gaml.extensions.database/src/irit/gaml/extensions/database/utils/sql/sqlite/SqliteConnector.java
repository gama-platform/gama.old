package irit.gaml.extensions.database.utils.sql.sqlite;

import java.util.HashMap;
import java.util.Map;

import irit.gaml.extensions.database.utils.sql.ISqlConnector;
import irit.gaml.extensions.database.utils.sql.SqlConnection;
import msi.gama.common.util.FileUtils;
import msi.gama.runtime.IScope;

public class SqliteConnector implements ISqlConnector {

	@Override
	public SqlConnection connection(final IScope scope, String venderName, String url, String port, String dbName, String userName,
			String password, Boolean transformed) {
		SqlConnection sqlConn;
		
		final String DBRelativeLocation = FileUtils.constructAbsoluteFilePath(scope, dbName, true);
		sqlConn = new SqliteConnection(venderName, DBRelativeLocation, transformed);
		
		return sqlConn;
	}

	@Override
	public Map<String, Object> getConnectionParameters(final IScope scope, String host, String dbtype, String port, String database,
			String user, String passwd) {

		Map<String,Object> params = new HashMap<>();
		
		final String DBRelativeLocation = FileUtils.constructAbsoluteFilePath(scope, database, true);
		params.put("dbtype", "sqlite");
		params.put("database", DBRelativeLocation);

		return params;
	}

}
