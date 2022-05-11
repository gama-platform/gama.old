/*******************************************************************************************************
 *
 * SqlUtils.java, in irit.gaml.extensions.database, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package irit.gaml.extensions.database.utils.sql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;

import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.types.IType;
import ummisco.gama.dev.utils.DEBUG;


/**
 * The Class SqlUtils.
 */
/*
 * @Author TRUONG Minh Thai Fredric AMBLARD Benoit GAUDOU Christophe Sibertin-BLANC Created date: 19-Apr-2013 Modified:
 * 18-July-2013: Add load extension library for SQLITE case. Last Modified: 18-July-2013
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SqlUtils {

	public static Map<String,ISqlConnector> externalConnectors = new HashMap<>();
	
	/**
	 * Creates the connection object.
	 *
	 * @param scope the scope
	 * @param params the params
	 * @return the sql connection
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public static SqlConnection createConnectionObject(final IScope scope, final Map<String, Object> params)
			throws GamaRuntimeException {
		final String dbtype = ((String) params.get("dbtype")).toLowerCase();
		final String host = (String) params.get("host");
		final String port = (String) params.get("port");
		final String database = (String) params.get("database");
		final String user = (String) params.get("user");
		final String passwd = (String) params.get("passwd");
		final boolean transform = params.containsKey("transform") ? (Boolean) params.get("transform") : true;

		SqlConnection sqlConn = null;
		// create connection
		ISqlConnector connector = externalConnectors.get(dbtype);
		if(connector == null) {
			throw GamaRuntimeException.error("GAMA does not support databases of type: " + dbtype, scope);				
		} else {
			sqlConn = connector.connection(scope, dbtype, host, port, database, user, passwd, transform);				
		}
			
		if (DEBUG.IS_ON()) {
			DEBUG.OUT("SqlUtils.createConnection:" + sqlConn.toString());
		}
		// AD: Added to be sure to remember the parameters
		sqlConn.setParams(params);
		return sqlConn;
	}

	/**
	 * Creates the connection object.
	 *
	 * @param scope the scope
	 * @return the sql connection
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public static SqlConnection createConnectionObject(final IScope scope) throws GamaRuntimeException {
		final java.util.Map params = (java.util.Map) scope.getArg("params", IType.MAP);
		return createConnectionObject(scope, params);
	}

	/**
	 * Read.
	 *
	 * @param b the b
	 * @return the geometry
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
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

	/**
	 * Input stream 2 geometry.
	 *
	 * @param inputStream the input stream
	 * @return the geometry
	 * @throws Exception the exception
	 */
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

			if (geometryAsBytes.length < 5) {
				throw new Exception("Invalid geometry inputStream - less than five bytes");
			}

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
	// public static IList<Object> transform(final GisUtils gis, final
	// IList<? extends IList<Object>> dataset,
	/**
	 * Transform.
	 *
	 * @param scope the scope
	 * @param gis the gis
	 * @param dataset the dataset
	 * @param fromAbsoluteToGis the from absolute to gis
	 * @return the i list
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	// final boolean fromAbsoluteToGis) throws GamaRuntimeException {
	static IList<Object> transform(final IScope scope, final IProjection gis,
			final IList<? super IList<Object>> dataset, final boolean fromAbsoluteToGis) throws GamaRuntimeException {

		try {
			final IList<Object> response = GamaListFactory.create();
			final IList<Object> records_new = GamaListFactory.create();
			// IList<Object> columnNames = dataset.get(0);
			// IList<Object> columnTypes = dataset.get(1);
			// IList<Object> records = dataset.get(2);

			final IList<Object> columnNames = (IList<Object>) dataset.get(0);
			final IList<Object> columnTypes = (IList<Object>) dataset.get(1);
			final IList<Object> records = (IList<Object>) dataset.get(2);

			final int columnSize = columnNames.size();
			final int lineSize = records.size();

			response.add(columnNames);
			response.add(columnTypes);

			// transform
			for (int i = 0; i < lineSize; i++) {
				final IList<Object> rec_old = (IList<Object>) records.get(i);
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
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.Gis2Absolute: " + e.toString(), scope);
		}
	}

}
