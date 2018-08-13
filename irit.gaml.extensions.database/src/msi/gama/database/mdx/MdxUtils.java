/*********************************************************************************************
 *
 *
 * 'MdxUtils.java', in plugin 'msi.gama.core', is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.database.mdx;

import java.util.Map;

import msi.gama.common.util.FileUtils;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import utils.DEBUG;

public class MdxUtils {

	public static MdxConnection createConnectionObject(final IScope scope, final Map<String, Object> params)
			throws GamaRuntimeException {
		final String olaptype = (String) params.get("olaptype");
		final String dbtype = (String) params.get("dbtype");
		final String host = (String) params.get("host");
		final String port = (String) params.get("port");
		final String database = (String) params.get("database");
		String catalog = (String) params.get("catalog");
		final String user = (String) params.get("user");
		final String passwd = (String) params.get("passwd");
		if (DEBUG.IS_ON()) {
			DEBUG.OUT("MdxlUtils.createConnectionObject:" + olaptype + " - " + dbtype + " - " + host + " - " + port
					+ " - " + database + " - " + catalog + " - " + user + " - " + passwd);
		}
		MdxConnection mdxConn;
		// create connection
		if (olaptype.equalsIgnoreCase(MdxConnection.MSAS)) {

			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		} else if (olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			final String fullPath = FileUtils.constructAbsoluteFilePath(scope, catalog, true);
			// catalog=fullPath.replace('\\', '/');
			catalog = fullPath;
			if (DEBUG.IS_ON()) {
				DEBUG.OUT("MdxlUtils.createConnectionObject- full path:" + fullPath);
				DEBUG.OUT("MdxlUtils.createConnectionObject- Catalog path:" + catalog);
			}

			if (DEBUG.IS_ON()) {
				scope.getGui()
						.debug("MdxlUtils.createConnectionObject.catalog.Mondrian:" + olaptype + " - " + dbtype + " - "
								+ " - " + host + " - " + port + " - " + database + " - " + catalog + " - " + user
								+ " - " + passwd + " - ");
			}

			mdxConn = new MondrianConnection(olaptype, dbtype, host, port, database, catalog, user, passwd);
			if (DEBUG.IS_ON()) {
				DEBUG.OUT("MdxlUtils.createConnectionObject.connectionObject.Mondrian.Object:" + mdxConn.toString());
			}

		} else if (olaptype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			if (DEBUG.IS_ON()) {
				scope.getGui()
						.debug("MdxlUtils.createConnectionObject.catalog.MondrianXMLA:" + olaptype + " - " + dbtype
								+ " - " + " - " + host + " - " + port + " - " + database + " - " + catalog + " - "
								+ user + " - " + passwd + " - ");
			}

			mdxConn = new MondrianXmlaConnection(olaptype, dbtype, host, port, database, catalog, user, passwd);
			if (DEBUG.IS_ON()) {
				DEBUG.OUT(
						"MdxlUtils.createConnectionObject.connectionObject.MondrianXMLA.Object:" + mdxConn.toString());
			}

		} else {
			throw GamaRuntimeException.error("GAMA does not support: " + olaptype, scope);
		}
		if (DEBUG.IS_ON()) {
			DEBUG.OUT("MdxUtils.createConnection:" + mdxConn.toString());
		}
		return mdxConn;
	}

	public static MdxConnection createConnectionObject(final Map<String, Object> params) throws GamaRuntimeException {
		final String olaptype = (String) params.get("olaptype");
		final String dbtype = (String) params.get("dbtype");
		final String host = (String) params.get("host");
		final String port = (String) params.get("port");
		final String database = (String) params.get("database");
		final String catalog = (String) params.get("catalog");
		final String user = (String) params.get("user");
		final String passwd = (String) params.get("passwd");
		// if ( DEBUG.IS_ON() ) {
		// DEBUG.OUT("MdxlUtils.createConnectionObject:" + olaptype + " - " + dbtype + " - " + " - " + host +
		// " - " + port + " - " + database + " - " + catalog + " - " + user + " - " + passwd);
		// }
		MdxConnection mdxConn;
		// create connection
		if (olaptype.equalsIgnoreCase(MdxConnection.MSAS)) {

			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		} else if (olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			mdxConn = new MondrianConnection(olaptype, dbtype, host, port, database, catalog, user, passwd);
		} else if (dbtype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			mdxConn = new MondrianXmlaConnection(olaptype, host, port, database, catalog, user, passwd);
		} else {
			throw GamaRuntimeException.error("GAMA does not support: " + olaptype, null);
		}
		// if ( DEBUG.IS_ON() ) {
		// DEBUG.OUT("MdxUtils.createConnection:" + mdxConn.toString());
		// }
		return mdxConn;
	}

	public static MdxConnection createConnectionObject(final IScope scope, final String olaptype, final String host,
			final String port, final String database, final String user, final String passwd)
			throws GamaRuntimeException {
		// if ( DEBUG.IS_ON() ) {
		// DEBUG.OUT("MdxlUtils.createConnectionObject:" + olaptype + " - " + host + " - " + port + " - " +
		// database + " - ");
		// }
		MdxConnection mdxConn;
		// create connection
		if (olaptype.equalsIgnoreCase(MdxConnection.MSAS)) {

			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		} else if (olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			mdxConn = new MondrianConnection(olaptype, host, port, database, user, passwd);
		} else if (olaptype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			mdxConn = new MondrianXmlaConnection(olaptype, host, port, database, user, passwd);
		} else {
			throw GamaRuntimeException.error("GAMA does not support: " + olaptype, scope);
		}
		// if ( DEBUG.IS_ON() ) {
		// DEBUG.OUT("MdxUtils.createConnection:" + mdxConn.toString());
		// }
		return mdxConn;
	}

	public static MdxConnection createConnectionObject(final IScope scope, final String olaptype, final String host,
			final String port, final String database, final String catalog, final String user, final String passwd)
			throws GamaRuntimeException {
		// if ( DEBUG.IS_ON() ) {
		// DEBUG.OUT("MdxlUtils.createConnectionObject:" + olaptype + " - " + host + " - " + port + " - " +
		// database + " - " + catalog + " - " + user + " - " + passwd);
		// }
		MdxConnection mdxConn;
		// create connection
		if (olaptype.equalsIgnoreCase(MdxConnection.MSAS)) {

			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		} else if (olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			mdxConn = new MondrianConnection(olaptype, host, port, database, user, passwd, catalog);
		} else if (olaptype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			mdxConn = new MondrianXmlaConnection(olaptype, host, port, database, catalog, user, passwd);
		} else {
			throw GamaRuntimeException.error("GAMA does not support: " + olaptype, scope);
		}
		// if ( DEBUG.IS_ON() ) {
		// DEBUG.OUT("MdxUtils.createConnection:" + mdxConn.toString());
		// }
		return mdxConn;
	}

	public static MdxConnection createConnectionObject(final IScope scope, final String olaptype, final String dbtype,
			final String host, final String port, final String database, final String catalog, final String user,
			final String passwd) throws GamaRuntimeException {
		// if ( DEBUG ) {
		// DEBUG.OUT("MdxlUtils.createConnectionObject:" + olaptype + " - " + host + " - " + port + " - " +
		// database + " - " + catalog + " - " + user + " - " + passwd);
		// }
		MdxConnection mdxConn;
		// create connection
		if (olaptype.equalsIgnoreCase(MdxConnection.MSAS)) {

			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		} else if (olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN)) {
			mdxConn = new MondrianConnection(olaptype, dbtype, host, port, database, catalog, user, passwd);
		} else if (olaptype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA)) {
			mdxConn = new MondrianXmlaConnection(olaptype, host, port, database, catalog, user, passwd);
		} else {
			throw GamaRuntimeException.error("GAMA does not support: " + dbtype, scope);
		}
		// if ( DEBUG.IS_ON() ) {
		// DEBUG.OUT("MdxUtils.createConnection:" + mdxConn.toString());
		// }
		return mdxConn;
	}

}
