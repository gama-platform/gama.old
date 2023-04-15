/*******************************************************************************************************
 *
 * Activator.java, in irit.gaml.extensions.database, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package irit.gaml.extensions.database;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import irit.gaml.extensions.database.utils.sql.SqlConnection;
import irit.gaml.extensions.database.utils.sql.SqlUtils;
import irit.gaml.extensions.database.utils.sql.mysql.MySqlConnection;
import irit.gaml.extensions.database.utils.sql.mysql.MySqlConnector;
import irit.gaml.extensions.database.utils.sql.postgres.PostgresConnection;
import irit.gaml.extensions.database.utils.sql.postgres.PostgresConnector;
import irit.gaml.extensions.database.utils.sql.sqlite.SqliteConnection;
import irit.gaml.extensions.database.utils.sql.sqlite.SqliteConnector;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.IMap;

/**
 * The Class Activator.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class Activator implements BundleActivator {

	@Override
	public void start(final BundleContext context) throws Exception {
		SqlUtils.externalConnectors.put(MySqlConnection.MYSQL,new MySqlConnector());		
		SqlUtils.externalConnectors.put(PostgresConnection.POSTGRES,new PostgresConnector());		
		SqlUtils.externalConnectors.put(PostgresConnection.POSTGIS,new PostgresConnector());				
		SqlUtils.externalConnectors.put(SqliteConnection.SQLITE,new SqliteConnector());		

		
		GeometryUtils.addEnvelopeComputer((scope, obj) -> {

			if (!(obj instanceof IMap)) { return null; }
			final IMap<String, Object> params = (IMap<String, Object>) obj;
			
			Envelope3D env = null;
			try(SqlConnection sqlConn = SqlUtils.createConnectionObject(scope, params)) {
			 // create connection
			 // sqlConn = SqlUtils.createConnectionObject(scope, params);
			 // get data
				final IList gamaList = sqlConn.selectDB(scope, (String) params.get("select"));
				env = SqlConnection.getBounds(gamaList);

				IProjection gis;
				gis = scope.getSimulation().getProjectionFactory().fromParams(scope, params, env);
				env = gis.getProjectedEnvelope();
			} catch(Exception e ) {
				throw GamaRuntimeException.error("Error in creating the world envelope from DataBase.",scope);				
			}
			return env;
			// ----------------------------------------------------------------------------------------------------

		});

	}

	@Override
	public void stop(final BundleContext context) throws Exception {}

}
