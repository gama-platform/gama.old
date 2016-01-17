/*********************************************************************************************
 *
 *
 * 'MdxUtils.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
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

public class MdxUtils {

	private static final boolean DEBUG = false;

	public static MdxConnection createConnectionObject(final IScope scope, final Map<String, Object> params)
		throws GamaRuntimeException {
		String olaptype = (String) params.get("olaptype");
		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String catalog = (String) params.get("catalog");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		if ( DEBUG ) {
			scope.getGui().debug("MdxlUtils.createConnectionObject:" + olaptype + " - " + dbtype + " - " + host +
				" - " + port + " - " + database + " - " + catalog + " - " + user + " - " + passwd);
		}
		MdxConnection mdxConn;
		// create connection
		if ( olaptype.equalsIgnoreCase(MdxConnection.MSAS) ) {

			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		} else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN) ) {
			String fullPath = FileUtils.constructAbsoluteFilePath(scope, catalog, true);
			// catalog=fullPath.replace('\\', '/');
			catalog = fullPath;
			if ( DEBUG ) {
				scope.getGui().debug("MdxlUtils.createConnectionObject- full path:" + fullPath);
				scope.getGui().debug("MdxlUtils.createConnectionObject- Catalog path:" + catalog);
			}

			if ( DEBUG ) {
				scope.getGui()
					.debug("MdxlUtils.createConnectionObject.catalog.Mondrian:" + olaptype + " - " + dbtype + " - " +
						" - " + host + " - " + port + " - " + database + " - " + catalog + " - " + user + " - " +
						passwd + " - ");
			}

			mdxConn = new MondrianConnection(olaptype, dbtype, host, port, database, catalog, user, passwd);
			if ( DEBUG ) {
				scope.getGui()
					.debug("MdxlUtils.createConnectionObject.connectionObject.Mondrian.Object:" + mdxConn.toString());
			}

		} else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA) ) {
			if ( DEBUG ) {
				scope.getGui()
					.debug("MdxlUtils.createConnectionObject.catalog.MondrianXMLA:" + olaptype + " - " + dbtype +
						" - " + " - " + host + " - " + port + " - " + database + " - " + catalog + " - " + user +
						" - " + passwd + " - ");
			}

			mdxConn = new MondrianXmlaConnection(olaptype, dbtype, host, port, database, catalog, user, passwd);
			if ( DEBUG ) {
				scope.getGui().debug(
					"MdxlUtils.createConnectionObject.connectionObject.MondrianXMLA.Object:" + mdxConn.toString());
			}

		} else {
			throw GamaRuntimeException.error("GAMA does not support: " + olaptype, scope);
		}
		if ( DEBUG ) {
			scope.getGui().debug("MdxUtils.createConnection:" + mdxConn.toString());
		}
		return mdxConn;
	}

	public static MdxConnection createConnectionObject(final Map<String, Object> params) throws GamaRuntimeException {
		String olaptype = (String) params.get("olaptype");
		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String catalog = (String) params.get("catalog");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		// if ( DEBUG ) {
		// scope.getGui().debug("MdxlUtils.createConnectionObject:" + olaptype + " - " + dbtype + " - " + " - " + host +
		// " - " + port + " - " + database + " - " + catalog + " - " + user + " - " + passwd);
		// }
		MdxConnection mdxConn;
		// create connection
		if ( olaptype.equalsIgnoreCase(MdxConnection.MSAS) ) {

			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		} else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN) ) {
			mdxConn = new MondrianConnection(olaptype, dbtype, host, port, database, catalog, user, passwd);
		} else if ( dbtype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA) ) {
			mdxConn = new MondrianXmlaConnection(olaptype, host, port, database, catalog, user, passwd);
		} else {
			throw GamaRuntimeException.error("GAMA does not support: " + olaptype, null);
		}
		// if ( DEBUG ) {
		// scope.getGui().debug("MdxUtils.createConnection:" + mdxConn.toString());
		// }
		return mdxConn;
	}

	public static MdxConnection createConnectionObject(final String olaptype, final String host, final String port,
		final String database, final String user, final String passwd) throws GamaRuntimeException {
		// if ( DEBUG ) {
		// scope.getGui().debug("MdxlUtils.createConnectionObject:" + olaptype + " - " + host + " - " + port + " - " +
		// database + " - ");
		// }
		MdxConnection mdxConn;
		// create connection
		if ( olaptype.equalsIgnoreCase(MdxConnection.MSAS) ) {

			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		} else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN) ) {
			mdxConn = new MondrianConnection(olaptype, host, port, database, user, passwd);
		} else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA) ) {
			mdxConn = new MondrianXmlaConnection(olaptype, host, port, database, user, passwd);
		} else {
			throw GamaRuntimeException.error("GAMA does not support: " + olaptype);
		}
		// if ( DEBUG ) {
		// scope.getGui().debug("MdxUtils.createConnection:" + mdxConn.toString());
		// }
		return mdxConn;
	}

	public static MdxConnection createConnectionObject(final String olaptype, final String host, final String port,
		final String database, final String catalog, final String user, final String passwd)
			throws GamaRuntimeException {
		// if ( DEBUG ) {
		// scope.getGui().debug("MdxlUtils.createConnectionObject:" + olaptype + " - " + host + " - " + port + " - " +
		// database + " - " + catalog + " - " + user + " - " + passwd);
		// }
		MdxConnection mdxConn;
		// create connection
		if ( olaptype.equalsIgnoreCase(MdxConnection.MSAS) ) {

			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		} else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN) ) {
			mdxConn = new MondrianConnection(olaptype, host, port, database, user, passwd, catalog);
		} else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA) ) {
			mdxConn = new MondrianXmlaConnection(olaptype, host, port, database, catalog, user, passwd);
		} else {
			throw GamaRuntimeException.error("GAMA does not support: " + olaptype);
		}
		// if ( DEBUG ) {
		// scope.getGui().debug("MdxUtils.createConnection:" + mdxConn.toString());
		// }
		return mdxConn;
	}

	public static MdxConnection createConnectionObject(final String olaptype, final String dbtype, final String host,
		final String port, final String database, final String catalog, final String user, final String passwd)
			throws GamaRuntimeException {
		// if ( DEBUG ) {
		// scope.getGui().debug("MdxlUtils.createConnectionObject:" + olaptype + " - " + host + " - " + port + " - " +
		// database + " - " + catalog + " - " + user + " - " + passwd);
		// }
		MdxConnection mdxConn;
		// create connection
		if ( olaptype.equalsIgnoreCase(MdxConnection.MSAS) ) {

			mdxConn = new MSASConnection(olaptype, host, port, database, user, passwd);
		} else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIAN) ) {
			mdxConn = new MondrianConnection(olaptype, dbtype, host, port, database, catalog, user, passwd);
		} else if ( olaptype.equalsIgnoreCase(MdxConnection.MONDRIANXMLA) ) {
			mdxConn = new MondrianXmlaConnection(olaptype, host, port, database, catalog, user, passwd);
		} else {
			throw GamaRuntimeException.error("GAMA does not support: " + dbtype);
		}
		// if ( DEBUG ) {
		// scope.getGui().debug("MdxUtils.createConnection:" + mdxConn.toString());
		// }
		return mdxConn;
	}

}
