package irit.gaml.extensions.database.utils.sql;

import java.util.Map;

import msi.gama.runtime.IScope;

public interface ISqlConnector {
	SqlConnection connection(final IScope scope, final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password, final Boolean transformed);

	Map<String, Object> getConnectionParameters(String host, String dbtype, String port, String database, String user, String passwd);
	
}
