/*********************************************************************************************
 *
 *
 * 'SqlUtils.java', in plugin 'msi.gama.core', is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.database.sql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.types.IType;

/*
 * @Author TRUONG Minh Thai Fredric AMBLARD Benoit GAUDOU Christophe Sibertin-BLANC Created date: 19-Apr-2013 Modified:
 * 18-July-2013: Add load extension library for SQLITE case. Last Modified: 18-July-2013
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SqlUtils {

	private static boolean DEBUG = false;

	public static SqlConnection createConnectionObject(final IScope scope, final Map<String, Object> params)
			throws GamaRuntimeException {
		final String dbtype = (String) params.get("dbtype");
		final String host = (String) params.get("host");
		final String port = (String) params.get("port");
		final String database = (String) params.get("database");
		final String user = (String) params.get("user");
		final String passwd = (String) params.get("passwd");
		// final String extension = (String) params.get("extension");
		// thai.truongminh@gmail.com
		// Move transform arg of select to a key in params
		// boolean transform = scope.hasArg("transform") ? (Boolean)
		// scope.getArg("transform", IType.BOOL) : true;
		final boolean transform = params.containsKey("transform") ? (Boolean) params.get("transform") : true;

		if (DEBUG) {
			scope.getGui().debug(
					"SqlUtils.createConnection:" + dbtype + " - " + host + " - " + port + " - " + database + " - ");
		}
		SqlConnection sqlConn;
		// create connection
		if (dbtype.equalsIgnoreCase(SqlConnection.SQLITE)) {
			final String DBRelativeLocation = FileUtils.constructAbsoluteFilePath(scope, database, true);
			final String EXTRelativeLocation = GamaPreferences.External.LIB_SPATIALITE.value(scope).getPath(scope);
			if (EXTRelativeLocation != null && !EXTRelativeLocation.equalsIgnoreCase("")) {
				sqlConn = new SqliteConnection(dbtype, DBRelativeLocation, EXTRelativeLocation, transform);

			} else {
				sqlConn = new SqliteConnection(dbtype, DBRelativeLocation, transform);
			}
		} else if (dbtype.equalsIgnoreCase(SqlConnection.MSSQL)) {
			sqlConn = new MSSQLConnection(dbtype, host, port, database, user, passwd, transform);
		} else if (dbtype.equalsIgnoreCase(SqlConnection.MYSQL)) {
			sqlConn = new MySqlConnection(dbtype, host, port, database, user, passwd, transform);
		} else if (dbtype.equalsIgnoreCase(SqlConnection.POSTGRES) || dbtype.equalsIgnoreCase(SqlConnection.POSTGIS)) {
			sqlConn = new PostgresConnection(dbtype, host, port, database, user, passwd, transform);
		} else {
			throw GamaRuntimeException.error("GAMA does not support databases of type: " + dbtype, scope);
		}
		if (DEBUG) {
			scope.getGui().debug("SqlUtils.createConnection:" + sqlConn.toString());
		}
		// AD: Added to be sure to remember the parameters
		sqlConn.setParams(params);
		return sqlConn;
	}

	public static SqlConnection createConnectionObject(final IScope scope) throws GamaRuntimeException {
		final java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
		return createConnectionObject(scope, params);
	}

	/*
	 * @Method: read(byte [] b)
	 *
	 * @Description: Convert Binary to Geometry (MSSQL,Sqlite, Postgres cases)
	 *
	 * @param byte [] b
	 *
	 * @return Geometry
	 *
	 * @throws IOException, ParseException
	 */
	public static Geometry read(final byte[] b) throws IOException, ParseException {
		final WKBReader wkb = new WKBReader();
		final Geometry geom = wkb.read(b);
		return geom;
	}

	/*
	 * @Method: Binary2Geometry(byte [] geometryAsBytes )
	 *
	 * @description: Convert binary to Geometry
	 *
	 * @param byte []
	 *
	 * @return Geometry
	 *
	 * @throws ParseException
	 */
	public static Geometry Binary2Geometry(final byte[] geometryAsBytes) throws ParseException {
		final byte[] wkb = new byte[geometryAsBytes.length - 4];
		System.arraycopy(geometryAsBytes, 4, wkb, 0, wkb.length);
		final WKBReader wkbReader = new WKBReader();
		final Geometry geom = wkbReader.read(wkb);
		return geom;
	}

	/*
	 * @Method: InputStream2Geometry(InputStream inputStream)
	 *
	 * @Description: Convert Binary to Geometry (MySQL case)
	 *
	 * @param InputStream inputStream
	 *
	 * @return Geometry
	 *
	 * @throws Exception
	 */
	public static Geometry InputStream2Geometry(final InputStream inputStream) throws Exception {
		Geometry dbGeometry = null;
		if (inputStream != null) {
			// convert the stream to a byte[] array
			// so it can be passed to the WKBReader
			final byte[] buffer = new byte[255];
			int bytesRead = 0;
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				baos.write(buffer, 0, bytesRead);
			}

			final byte[] geometryAsBytes = baos.toByteArray();

			if (geometryAsBytes.length < 5) { throw new Exception(
					"Invalid geometry inputStream - less than five bytes"); }

			// first four bytes of the geometry are the SRID,
			// followed by the actual WKB. Determine the SRID
			// here
			final byte[] sridBytes = new byte[4];
			System.arraycopy(geometryAsBytes, 0, sridBytes, 0, 4);
			final boolean bigEndian = geometryAsBytes[4] == 0x00;

			int srid = 0;
			if (bigEndian) {
				for (final byte sridByte : sridBytes) {
					srid = (srid << 8) + (sridByte & 0xff);
				}
			} else {
				for (int i = 0; i < sridBytes.length; i++) {
					srid += (sridBytes[i] & 0xff) << 8 * i;
				}
			}

			// use the JTS WKBReader for WKB parsing
			final WKBReader wkbReader = new WKBReader();

			// copy the byte array, removing the first four
			// SRID bytes
			final byte[] wkb = new byte[geometryAsBytes.length - 4];
			System.arraycopy(geometryAsBytes, 4, wkb, 0, wkb.length);
			dbGeometry = wkbReader.read(wkb);
			dbGeometry.setSRID(srid);
		}

		return dbGeometry;
	}

	/*
	 *
	 * Gis2Absolute: transform all absolute geometry values in GAMA to geometry
	 */
	// public static GamaList<Object> transform(final GisUtils gis, final
	// GamaList<? extends GamaList<Object>> dataset,
	// final boolean fromAbsoluteToGis) throws GamaRuntimeException {
	public static IList<Object> transform(final IScope scope, final IProjection gis,
			final IList<? super IList<Object>> dataset, final boolean fromAbsoluteToGis) throws GamaRuntimeException {

		try {
			final IList<Object> response = GamaListFactory.create();
			final IList<Object> records_new = GamaListFactory.create();
			// GamaList<Object> columnNames = dataset.get(0);
			// GamaList<Object> columnTypes = dataset.get(1);
			// GamaList<Object> records = dataset.get(2);

			final IList<Object> columnNames = (GamaList<Object>) dataset.get(0);
			final IList<Object> columnTypes = (GamaList<Object>) dataset.get(1);
			final IList<Object> records = (GamaList<Object>) dataset.get(2);

			final int columnSize = columnNames.size();
			final int lineSize = records.size();

			response.add(columnNames);
			response.add(columnTypes);

			// transform
			for (int i = 0; i < lineSize; i++) {
				final IList<Object> rec_old = (GamaList<Object>) records.get(i);
				final IList<Object> rec_new = GamaListFactory.create();
				for (int j = 0; j < columnSize; j++) {
					if (((String) columnTypes.get(j)).equalsIgnoreCase(SqlConnection.GEOMETRYTYPE)) {
						Geometry geo2 = (Geometry) rec_old.get(j);
						if (fromAbsoluteToGis) {
							geo2 = gis.inverseTransform(geo2);
						} else {
							geo2 = gis.transform(geo2);
						}
						rec_new.add(geo2);
					} else {
						rec_new.add(rec_old.get(j));
					}

				}
				records_new.add(rec_new);
			}
			response.add(records_new);
			return response;
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.Gis2Absolute: " + e.toString(), scope);
		}
	}

}
