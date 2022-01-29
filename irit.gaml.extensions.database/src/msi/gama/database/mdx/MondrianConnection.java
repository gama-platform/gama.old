/*******************************************************************************************************
 *
 * MondrianConnection.java, in irit.gaml.extensions.database, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.database.mdx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.olap4j.OlapConnection;
import org.olap4j.OlapWrapper;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class MondrianConnection.
 */
public class MondrianConnection extends MdxConnection {

	/** The Constant DRIVER. */
	private static final String DRIVER = "mondrian.olap4j.MondrianOlap4jDriver";
	
	/** The driver map. */
	private static java.util.HashMap<String, String> driverMap = new java.util.HashMap<>();
	
	/** The jdbc map. */
	private static java.util.HashMap<String, String> jdbcMap = new java.util.HashMap<>();

	/**
	 * Instantiates a new mondrian connection.
	 *
	 * @param venderName the vender name
	 * @param url the url
	 * @param port the port
	 * @param dbName the db name
	 * @param userName the user name
	 * @param password the password
	 */
	MondrianConnection(final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password) {
		super(venderName, url, port, dbName, userName, password);
		init();
	}

	/**
	 * Instantiates a new mondrian connection.
	 *
	 * @param venderName the vender name
	 * @param url the url
	 * @param port the port
	 * @param dbName the db name
	 * @param catalog the catalog
	 * @param userName the user name
	 * @param password the password
	 */
	MondrianConnection(final String venderName, final String url, final String port, final String dbName,
			final String catalog, final String userName, final String password) {
		super(venderName, url, port, dbName, catalog, userName, password);
		init();
	}

	/**
	 * Instantiates a new mondrian connection.
	 *
	 * @param venderName the vender name
	 * @param dbtype the dbtype
	 * @param url the url
	 * @param port the port
	 * @param dbName the db name
	 * @param catalog the catalog
	 * @param userName the user name
	 * @param password the password
	 */
	MondrianConnection(final String venderName, final String dbtype, final String url, final String port,
			final String dbName, final String catalog, final String userName, final String password) {
		super(venderName, dbtype, url, port, dbName, catalog, userName, password);
		init();
	}

	/**
	 * Inits the.
	 */
	private void init() {
		driverMap.put(MYSQL, MYSQLDriver);
		driverMap.put(POSTGRES, POSTGRESDriver);
		driverMap.put(POSTGIS, POSTGRESDriver);
		driverMap.put(MSSQL, MSSQLDriver);
		driverMap.put(SQLITE, SQLITEDriver);
		jdbcMap.put(MYSQL, "jdbc:mysql://");
		jdbcMap.put(POSTGRES, "jdbc:postgresql://");
		jdbcMap.put(POSTGIS, "jdbc:postgresql://");
		jdbcMap.put(MSSQL, "jdbc:jtds:sqlserver://");
		jdbcMap.put(SQLITE, "jdbc:sqlite:");

	}

	@Override
	public OlapConnection connectMDB(final IScope scope) throws GamaRuntimeException {
		OlapWrapper wrapper;
		Connection conn;
		// if (DEBUG.IS_ON()){
		// DEBUG.OUT("dbtype:"+ dbtype);
		// DEBUG.OUT("driver:"+ driverMap.toString());
		// DEBUG.OUT("jdbc:"+ jdbcMap.toString());
		// DEBUG.OUT("MondrianConnection.connectMDB:"+vender+" - "+dbtype+" - "+" - "+url+" - "
		// + port+" - "+dbName+" - "+catalog+" - "+userName+" - "+password);
		//
		// }
		try {
			if (vender.equalsIgnoreCase(MONDRIAN)) {
				Class.forName(DRIVER);
				conn = DriverManager.getConnection("jdbc:mondrian:" + "JdbcDrivers="
						+ driverMap.get(dbtype.toLowerCase()) + ";" + "Jdbc=" + jdbcMap.get(dbtype.toLowerCase()) + url
						+ ":" + port + "/" + dbName + "?user=" + userName + "&" + "password=" + password + ";"
						// +"Catalog=file:C:\\Program Files\\Apache Software Foundation\\Tomcat
						// 7.0\\webapps\\mondrian\\WEB-INF\\queries\\FoodMart.xml;"
						+ "Catalog=file:" + catalog + ";");

				wrapper = (OlapWrapper) conn;
				olapConnection = wrapper.unwrap(OlapConnection.class);
			} else {
				throw GamaRuntimeException.error("MondrianConnection.connectMDB: The " + vender + " is not supported!",
						scope);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString(), scope);
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString(), scope);
		}
		return olapConnection;
	}

	@Override
	public OlapConnection connectMDB(final IScope scope, final String dbName) throws GamaRuntimeException {
		OlapWrapper wrapper;
		Connection conn;
		DEBUG.OUT("dbtype:" + dbtype);
		DEBUG.OUT("driver:" + driverMap.toString());
		DEBUG.OUT("jdbc:" + jdbcMap.toString());
		try {
			if (vender.equalsIgnoreCase(MONDRIAN)) {
				Class.forName(DRIVER);
				conn = DriverManager.getConnection(
						"jdbc:mondrian:" + "JdbcDrivers=" + driverMap.get(dbtype.toLowerCase()) + ";" + "Jdbc="
								+ jdbcMap.get(dbtype.toLowerCase()) + url + ":" + port + "/" + dbName + "?user="
								+ userName + "&" + "password=" + password + ";" + "Catalog=file:" + catalog + ";");

				wrapper = (OlapWrapper) conn;
				olapConnection = wrapper.unwrap(OlapConnection.class);
			} else {
				throw GamaRuntimeException.error("MondrianConnection.connectMDB: The " + vender + " is not supported!",
						scope);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString(), scope);
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString(), scope);
		}
		return olapConnection;
	}

	@Override
	public OlapConnection connectMDB(final IScope scope, final String dbName, final String catalog)
			throws GamaRuntimeException {
		OlapWrapper wrapper;
		Connection conn;
		DEBUG.OUT("dbtype:" + dbtype);
		DEBUG.OUT("driver:" + driverMap.toString());
		DEBUG.OUT("jdbc:" + jdbcMap.toString());
		try {
			if (vender.equalsIgnoreCase(MONDRIAN)) {
				Class.forName(DRIVER);
				conn = DriverManager.getConnection(
						"jdbc:mondrian:" + "JdbcDrivers=" + driverMap.get(dbtype.toLowerCase()) + ";" + "Jdbc="
								+ jdbcMap.get(dbtype.toLowerCase()) + url + ":" + port + "/" + dbName + "?user="
								+ userName + "&" + "password=" + password + ";" + "Catalog=file:" + catalog + ";");

				wrapper = (OlapWrapper) conn;
				olapConnection = wrapper.unwrap(OlapConnection.class);
			} else {
				throw GamaRuntimeException.error("MondrianConnection.connectMDB: The " + vender + " is not supported!",
						scope);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString(), scope);
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString(), scope);
		}
		return olapConnection;
	}

}
