package irit.gaml.extensions.database.skills;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.mysql.MySQLDataStoreFactory;
import org.geotools.data.sqlserver.SQLServerDataStoreFactory;
import org.geotools.jdbc.JDBCDataStore;

public class GeoSQL {

	static final boolean DEBUG = false; // Change DEBUG = false for release
										// version
	static final String MYSQL = "MySQL";
	static final String MSSQL = "MsSQL";
	static final String MYSQLDriver = new String("com.mysql.jdbc.Driver");
	static final String MSSQLDriver = new String("com.microsoft.sqlserver.jdbc.SQLServerDriver");

	public GeoSQL() throws IOException, SQLException {

		final java.util.Map params = new java.util.HashMap();
		params.put("dbtype", "postgis");
		params.put("host", "localhost");
		params.put("port", "5432");
		params.put("user", "postgres");
		params.put("passwd", "tmt");
		params.put("database", "GAMADB");

		final DataStore dataStore = DataStoreFinder.getDataStore(params);
		if (dataStore != null) {
			// System.out.println("1.1PostGres- data store: " +
			// dataStore.toString());
		} else {
			// System.out.println("1.2Could not connect - check parameters");
		}
		try {
			final MySQLDataStoreFactory sqlDSF = new MySQLDataStoreFactory();
			final JDBCDataStore jdbcDataSore = sqlDSF.createDataStore(params);
			// System.out.println("1.3:JDBC Data Store: " +
			// jdbcDataSore.toString());
			// System.out.println("parameter: " + sqlDSF.getParametersInfo());
		} catch (final Exception e) {
			System.out.println("1.4Loi :" + e.toString());
		}
		final java.util.Map params2 = new java.util.HashMap();
		params2.put("dbtype", "sqlserver");
		params2.put("host", "localhost");
		params2.put("port", "1433");
		params2.put("user", "sa");
		params2.put("passwd", "tmt");
		params2.put("database", "bph");
		final DataStore dataStore2 = DataStoreFinder.getDataStore(params2);
		if (dataStore2 != null) {
			System.out.println("2.1MySQL- data store: " + dataStore2.toString());
		} else {
			System.out.println("2.2Could not connect - check parameters");
		}
		try {
			final SQLServerDataStoreFactory sqlDSF2 = new SQLServerDataStoreFactory();
			// JDBCDataStore jdbcDataSore2= sqlDSF2.createDataStore(params2);
			final BasicDataSource jdbcDataSore2 = sqlDSF2.createDataSource(params2);
			// System.out.println("2.3:" + jdbcDataSore2.toString());
		} catch (final Exception e) {
			System.out.println("2.4Loi :" + e.toString());
		}

		final BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(MSSQLDriver);
		bds.setUrl("jdbc:sqlserver://localhost");
		bds.setUsername("sa");
		bds.setPassword("tmt");
		final Connection connection = bds.getConnection();

		// System.err.println("3.1Connection: " + connection);
		connection.close();

	}

	public static void main(final String[] args)
			throws IOException, ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException {
		final GeoSQL mySQL = new GeoSQL();
	}

}
