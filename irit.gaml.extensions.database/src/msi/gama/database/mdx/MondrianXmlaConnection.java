/*********************************************************************************************
 *
 *
 * 'MondrianXmlaConnection.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.database.mdx;

import java.sql.*;
import org.olap4j.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class MondrianXmlaConnection extends MdxConnection {

	private static final boolean DEBUG = false; // Change DEBUG = false for release version
	static final String DRIVER = new String("org.olap4j.driver.xmla.XmlaOlap4jDriver");

	public MondrianXmlaConnection() {
		super();
	}

	public MondrianXmlaConnection(final String vender) {
		super(vender);
	}

	public MondrianXmlaConnection(final String venderName, final String database) {
		super(venderName, database);
	}

	public MondrianXmlaConnection(final String venderName, final String url, final String port, final String dbName,
		final String userName, final String password) {
		super(venderName, url, port, dbName, userName, password);
	}

	public MondrianXmlaConnection(final String venderName, final String url, final String port, final String dbName,
		final String catalog, final String userName, final String password) {
		super(venderName, url, port, dbName, catalog, userName, password);
	}

	public MondrianXmlaConnection(final String venderName, final String dbtype, final String url, final String port,
		final String dbName, final String catalog, final String userName, final String password) {
		super(venderName, dbtype, url, port, dbName, catalog, userName, password);
	}

	@Override
	public OlapConnection connectMDB() throws GamaRuntimeException {
		OlapWrapper wrapper;
		Connection conn;
		try {
			if ( vender.equalsIgnoreCase(MONDRIANXMLA) ) {
				Class.forName(DRIVER);
				// conn =
				// DriverManager.getConnection("jdbc:xmla:Server=http://" + url + ":" + port + "/" + dbName+"/xmla;"
				// +"Provider=Mondrian;"
				// //+"DataSource=C:/Program Files/Apache Software Foundation/Tomcat 7.0/webapps/mondrian/WEB-INF/datasources.xml;"
				// +"DataSource=/WEB-INF/datasources.xml;"
				//
				// +"Catalog=FoodMart;"
				// +"Cube=Store;"
				//// + ";Cache=org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache"
				//// + ";Cache.Name=MyNiftyConnection"
				//// + ";Cache.Mode=LFU;Cache.Timeout=600;Cache.Size=100"
				// ,userName, password
				// );

				// if (DEBUG){
				// scope.getGui().debug("MondrianXmlaConnection.connectMDB:"+vender+" - "+dbtype+" - "+" - "+url+" - "
				// + port+" - "+dbName+" - "+catalog+" - "+userName+" - "+password);
				//
				// }

				conn = DriverManager.getConnection("jdbc:xmla:Server=http://" + url + ":" + port + "/mondrian/xmla"
					// +";Cache=org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache"
					// +";Cache.Name=MyNiftyConnection"
					// +";Cache.Mode=LFU;Cache.Timeout=600;Cache.Size=100"
				+ ";Provider=mondrian" + ";DataSource=" + dbName + ";Catalog=" + catalog + ";"
					// +"Role='Admin'"
					, userName, password);
				wrapper = (OlapWrapper) conn;
				olapConnection = wrapper.unwrap(OlapConnection.class);
			} else {
				throw GamaRuntimeException.error("MondrianConnection.connectMDB: The " + vender + " is not supported!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		}
		// if ( DEBUG ) {
		// scope.getGui().debug("MondrianXmlaConnection.connected");
		//
		// }
		return olapConnection;

	}

	@Override
	public OlapConnection connectMDB(final String dbName) throws GamaRuntimeException {
		OlapWrapper wrapper;
		Connection conn;
		try {
			if ( vender.equalsIgnoreCase(MONDRIANXMLA) ) {
				Class.forName(DRIVER);

				conn = DriverManager.getConnection("jdbc:xmla:Server=http://" + url + ":" + port + "/mondrian/xmla" +
					";Provider=mondrian" + ";DataSource=" + dbName, userName, password);
				wrapper = (OlapWrapper) conn;
				olapConnection = wrapper.unwrap(OlapConnection.class);
			} else {
				throw GamaRuntimeException.error("MondrianConnection.connectMDB: The " + vender + " is not supported!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		}
		return olapConnection;
	}

	@Override
	public OlapConnection connectMDB(final String dbName, final String catalog) throws GamaRuntimeException {
		OlapWrapper wrapper;
		Connection conn;
		try {
			if ( vender.equalsIgnoreCase(MONDRIANXMLA) ) {
				Class.forName(DRIVER);

				conn = DriverManager.getConnection("jdbc:xmla:Server=http://" + url + ":" + port + "/mondrian/xmla" +
					";Provider=mondrian" + ";DataSource=" + dbName + ";Catalog=" + catalog, userName, password);
				wrapper = (OlapWrapper) conn;
				olapConnection = wrapper.unwrap(OlapConnection.class);
			} else {
				throw GamaRuntimeException.error("MondrianConnection.connectMDB: The " + vender + " is not supported!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		}
		return olapConnection;
	}

}
