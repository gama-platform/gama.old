package msi.gama.database.sql;

import java.util.Map;

public interface ISqlConnector {
	SqlConnection connection(final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password, final Boolean transformed);

	Map<String, Object> getConnectionParameters(String host, String dbtype, String port, String database, String user, String passwd);
	
}
