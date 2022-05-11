package irit.gaml.extensions.database.utils.sql.sqlite;

import java.util.Map;

import irit.gaml.extensions.database.utils.sql.ISqlConnector;
import irit.gaml.extensions.database.utils.sql.SqlConnection;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.FileUtils;
import msi.gama.runtime.IScope;

public class SqliteConnector implements ISqlConnector {

	@Override
	public SqlConnection connection(final IScope scope, String venderName, String url, String port, String dbName, String userName,
			String password, Boolean transformed) {
		SqlConnection sqlConn;
		
		final String DBRelativeLocation = FileUtils.constructAbsoluteFilePath(scope, dbName, true);
		final String EXTRelativeLocation = GamaPreferences.External.LIB_SPATIALITE.value(scope).getPath(scope);
		if (EXTRelativeLocation != null && !EXTRelativeLocation.equalsIgnoreCase("")) {
			sqlConn = new SqliteConnection(venderName, DBRelativeLocation, EXTRelativeLocation, transformed); 
		} else {
			sqlConn = new SqliteConnection(venderName, DBRelativeLocation, transformed);
		}
		
		return sqlConn;
	}

	@Override
	public Map<String, Object> getConnectionParameters(String host, String dbtype, String port, String database,
			String user, String passwd) {
		// TODO Auto-generated method stub
		return null;
	}

}
