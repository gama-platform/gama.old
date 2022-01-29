/*******************************************************************************************************
 *
 * MSASConnection.java, in irit.gaml.extensions.database, is part of the source code of the
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

/**
 * The Class MSASConnection.
 */
class MSASConnection extends MdxConnection {

	/** The Constant DRIVER. */
	private static final String DRIVER = "org.olap4j.driver.xmla.XmlaOlap4jDriver";

	/**
	 * Instantiates a new MSAS connection.
	 *
	 * @param venderName the vender name
	 * @param url the url
	 * @param port the port
	 * @param dbName the db name
	 * @param userName the user name
	 * @param password the password
	 */
	MSASConnection(final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password) {
		super(venderName, url, port, dbName, userName, password);
	}

	@Override
	public OlapConnection connectMDB(final IScope scope) throws GamaRuntimeException {
		OlapWrapper wrapper;
		Connection conn;
		try {
			if (vender.equalsIgnoreCase(MSAS)) {
				Class.forName(DRIVER);
				conn = DriverManager.getConnection(
						// "jdbc:xmla:Server=http://localhost/xmla/msxisapi.dll");
						"jdbc:xmla:Server=http://" + url + ":" + port + "/" + dbName + "/msmdpump.dll;", userName,
						password);
				wrapper = (OlapWrapper) conn;
				olapConnection = wrapper.unwrap(OlapConnection.class);
			} else {
				throw GamaRuntimeException.error("MSASConnection.connectMDB: The " + vender + " is not supported!",
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
		try {
			if (vender.equalsIgnoreCase(MSAS)) {
				Class.forName(DRIVER);
				conn = DriverManager.getConnection(
						"jdbc:xmla:Server=http://" + url + ":" + port + "/" + dbName + "/msmdpump.dll;", userName,
						password);
				wrapper = (OlapWrapper) conn;
				olapConnection = wrapper.unwrap(OlapConnection.class);
				// olapConnection.setCatalog(catalog);
			} else {
				throw GamaRuntimeException.error("MSASConnection.connectMDB: The " + vender + " is not supported!",
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
		try {
			if (vender.equalsIgnoreCase(MSAS)) {
				Class.forName(DRIVER);
				conn = DriverManager.getConnection(
						"jdbc:xmla:Server=http://" + url + ":" + port + "/" + dbName + "/msmdpump.dll;", userName,
						password);
				wrapper = (OlapWrapper) conn;
				olapConnection = wrapper.unwrap(OlapConnection.class);
				olapConnection.setCatalog(catalog);
			} else {
				throw GamaRuntimeException.error("MSASConnection.connectMDB: The " + vender + " is not supported!",
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
