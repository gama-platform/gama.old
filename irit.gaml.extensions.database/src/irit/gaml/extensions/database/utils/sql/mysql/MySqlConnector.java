package irit.gaml.extensions.database.utils.sql.mysql;

import java.util.Map;

import irit.gaml.extensions.database.utils.sql.ISqlConnector;
import irit.gaml.extensions.database.utils.sql.SqlConnection;
import msi.gama.runtime.IScope;

public class MySqlConnector implements ISqlConnector{

	@Override
	public SqlConnection connection(final IScope scope, String venderName, String url, String port, String dbName, String userName,
			String password, Boolean transformed) {
		return new MySqlConnection(scope, venderName, url, port, dbName, userName, password, transformed);
	}

	@Override
	public Map<String, Object> getConnectionParameters(String host, String dbtype, String port, String database,
			String user, String passwd) {
		// TODO Auto-generated method stub
		return null;
	}

}
