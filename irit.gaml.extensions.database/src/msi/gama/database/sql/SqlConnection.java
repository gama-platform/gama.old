/*********************************************************************************************
 *
 *
 * 'SqlConnection.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.opengis.referencing.FactoryException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.GamaPreferences;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.metamodel.topology.projection.Projection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;

/*
 * @Author
 * TRUONG Minh Thai
 * Fredric AMBLARD
 * Benoit GAUDOU
 * Christophe Sibertin-BLANC
 * Created date: 19-Apr-2013
 * Modified:
 * 26-Apr-2013:
 * Remove driver msi.gama.ext/sqljdbc4.jar
 * add driver msi.gama.ext/jtds-1.2.6.jar
 * Change driver name for MSSQL from com.microsoft.sqlserver.jdbc.SQLServerDriver to net.sourceforge.jtds.jdbc.Driver
 * 18-July-2013:
 * Add load extension library for SQLITE case.
 * 15-Jan-2014:
 * Add datetime type.
 * Add NULL VALUE
 * Last Modified: 15-Jan-2014
 */
public abstract class SqlConnection {

	protected static final boolean DEBUG = false; // Change DEBUG = false for
													// release version
	public static final String MYSQL = "mysql";
	public static final String POSTGRES = "postgres";
	public static final String POSTGIS = "postgis";
	public static final String MSSQL = "sqlserver";
	public static final String SQLITE = "sqlite";
	public static final String GEOMETRYTYPE = "GEOMETRY";
	public static final String CHAR = "CHAR";
	public static final String VARCHAR = "VARCHAR";
	public static final String NVARCHAR = "NVARCHAR";
	public static final String TEXT = "TEXT";
	public static final String BLOB = "BLOB";
	public static final String TIMESTAMP = "TIMESTAMP"; // MSSQL
														// (number);Postgres,MySQL
														// ('YYYY-MM-DD
														// HH:MM:SS')
	public static final String DATETIME = "DATETIME"; // MSSQL,Postgres, MySQL,
														// SQLite ( "YYYY-MM-DD
														// HH:MM:SS.SSS")
	public static final String SMALLDATETIME = "SMALLDATETIME"; // MSSQL
	public static final String DATE = "DATE"; // MSSQL,Postgres, MySQL, SQlite
	public static final String YEAR = "YEAR"; // Postgres, MySQL(yyyy)
	public static final String TIME = "TIME"; // MySQL ('00:00:00')
	public static final String NULLVALUE = "NULL";

	static final String MYSQLDriver = new String("com.mysql.jdbc.Driver");
	// static final String MSSQLDriver = new
	// String("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	static final String MSSQLDriver = new String("net.sourceforge.jtds.jdbc.Driver");
	static final String SQLITEDriver = new String("org.sqlite.JDBC");
	static final String POSTGRESDriver = new String("org.postgresql.Driver");

	protected String vender = "";
	protected String dbtype = "";
	protected String url = "";
	protected String port = "";
	protected String dbName = "";
	protected String userName = "";
	protected String password = "";
	protected Boolean transformed = false;
	protected String extension = null;
	protected boolean loadExt = false;

	// AD: Added to be sure that SQL connections use a correct projection when
	// they load/save geometries
	private IProjection gis = null;
	// AD: Added to be sure to remember the parameters (which can contain other
	// informations about GIS data
	protected Map<String, Object> params;

	public void setGis(final Projection gis) {
		this.gis = gis;
	}

	public IProjection getGis() {
		return this.gis;
	}

	public boolean getTransform() {
		return transformed;
	}

	protected IProjection getSavingGisProjection(final IScope scope) {
		final Boolean longitudeFirst = params.containsKey("longitudeFirst") ? (Boolean) params.get("longitudeFirst")
				: true;
		final String crs = (String) params.get("crs");
		if (crs != null) {
			try {
				return scope.getSimulation().getProjectionFactory().forSavingWith(crs);
			} catch (final FactoryException e) {

				throw GamaRuntimeException.error("No factory found for decoding the EPSG " + crs
						+ " code. GAMA may be unable to save any GIS data", scope);

			}
		}
		final String srid = (String) params.get("srid");
		if (srid != null) {
			try {
				return scope.getSimulation().getProjectionFactory().forSavingWith(Cast.asInt(scope, srid),
						longitudeFirst);
			} catch (final FactoryException e) {

				throw GamaRuntimeException.error("No factory found for decoding the EPSG " + srid
						+ " code. GAMA may be unable to save any GIS data", scope);

			}
			// return
			// scope.getSimulationScope().getProjectionFactory().forSavingWith(srid,
			// longitudeFirst);
		} else {
			// return
			// scope.getSimulationScope().getProjectionFactory().forSavingWith((Integer)
			// null);
			try {
				return scope.getSimulation().getProjectionFactory()
						.forSavingWith(GamaPreferences.LIB_OUTPUT_CRS.getValue());
			} catch (final FactoryException e) {

				throw GamaRuntimeException.error("No factory found for decoding the EPSG "
						+ GamaPreferences.LIB_OUTPUT_CRS.getValue() + " code. GAMA may be unable to save any GIS data",
						scope);

			}
		}

	}

	public void setParams(final Map<String, Object> params) {
		this.params = params;
	}

	public SqlConnection(final String dbName) {
		this.dbName = dbName;
	}

	public SqlConnection(final String venderName, final String database) {
		this.vender = venderName;
		this.dbName = database;
		this.dbtype = venderName;
	}

	public SqlConnection(final String venderName, final String database, final Boolean transformed) {
		this.vender = venderName;
		this.dbName = database;
		this.transformed = transformed;
		this.dbtype = venderName;
	}

	public SqlConnection() {
	}

	public SqlConnection(final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password) {
		this.vender = venderName;
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
		this.dbtype = venderName;
	}

	public SqlConnection(final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password, final Boolean transformed) {
		this.vender = venderName;
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
		this.dbtype = venderName;
		this.transformed = transformed;
	}

	/*
	 * Make a connection to BDMS
	 */
	public abstract Connection connectDB()
			throws ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException;

	/*
	 * @Method:resultSet2GamaList(ResultSetMetaData rsmd, ResultSet rs)
	 *
	 * @Description: Convert RecordSet to GamaList
	 *
	 * @param ResultSetMetaData,ResultSet
	 *
	 * @return GamaList<GamaList<Object>>
	 */
	protected abstract IList<IList<Object>> resultSet2GamaList(ResultSetMetaData rsmd, ResultSet rs);

	/*
	 * @Meththod: getGeometryColumns(ResultSetMetaData rsmd)
	 *
	 * @Description: Get columns id of field with geometry type
	 *
	 * @param ResultSetMetaData
	 *
	 * @return List<Integer>
	 *
	 * @throws SQLException
	 */
	protected abstract List<Integer> getGeometryColumns(ResultSetMetaData rsmd) throws SQLException;

	/*
	 * @Method: getColumnTypeName
	 *
	 * @Description: Get columns type name
	 *
	 * @param ResultSetMetaData
	 *
	 * @return GamaList<String>
	 *
	 * @throws SQLException
	 */
	protected abstract IList<Object> getColumnTypeName(ResultSetMetaData rsmd) throws SQLException;

	/*
	 * Make insert command string with columns and values
	 */
	protected abstract String getInsertString(IScope scope, Connection conn, String table_name, IList<Object> cols,
			IList<Object> values) throws GamaRuntimeException;

	/*
	 * Make insert command string for all columns with values
	 */
	protected abstract String getInsertString(IScope scope, Connection conn, String table_name, IList<Object> values)
			throws GamaRuntimeException;

	/*
	 * Make a connection to BDMS and execute the select statement
	 *
	 * @return GamaList<GamaList<Object>>
	 */
	public IList<? super IList<? super IList>> selectDB(final IScope scope, final String selectComm) {
		IList<? super IList<? super IList>> result = GamaListFactory
				.create(msi.gaml.types.Types.LIST.of(msi.gaml.types.Types.LIST));
		Connection conn = null;
		try {
			conn = connectDB();
			result = selectDB(scope, conn, selectComm);
			conn.close();
		} catch (final Exception e) {
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString(), scope);
		}

		// return repRequest;
		return result;
	}

	/*
	 * Make a connection to BDMS and execute the select statement
	 *
	 * @return GamaList<GamaList<Object>>
	 */
	// public GamaList<GamaList<Object>> selectDB(String selectComm)
	public IList<? super IList<? super IList>> selectDB(final IScope scope, final Connection conn,
			final String selectComm) {
		;
		ResultSet rs;
		IList<? super IList<? super IList>> result = GamaListFactory
				.create(msi.gaml.types.Types.LIST.of(msi.gaml.types.Types.LIST));
		// GamaList<? extends GamaList<? super GamaList>> result = new
		// GamaList();

		// GamaList<Object> rowList = new GamaList<Object>();
		IList repRequest = GamaListFactory.create(msi.gaml.types.Types.NO_TYPE);
		try {
			final Statement st = conn.createStatement();
			rs = st.executeQuery(selectComm);

			final ResultSetMetaData rsmd = rs.getMetaData();
			if (DEBUG) {
				scope.getGui().debug("MetaData:" + rsmd.toString());
			}
			result.add(getColumnName(rsmd));
			final IList<Object> columns = getColumnTypeName(rsmd);
			result.add(columns);
			repRequest = resultSet2GamaList(rs);
			result.add(repRequest);

			/**
			 * AD: Added to transform Geometries
			 */
			// if ( columns.contains(GEOMETRYTYPE) && transformed) {
			// if ( gis == null ) {
			// // we have at least one geometry type and we compute the envelope
			// if no gis is present
			// // Envelope env = getBounds(repRequest);
			// Envelope env = getBounds(result);
			// // we now compute the GisUtils instance for our case (based on
			// params and env)
			// gis =
			// scope.getSimulationScope().getProjectionFactory().fromParams(params,
			// env);
			// }
			// // and we transform the geometries using its projection
			// // repRequest = SqlUtils.transform(gis, repRequest, false);
			// result = SqlUtils.transform(gis, result, false);
			// }

			if (columns.contains(GEOMETRYTYPE) && transformed) {
				gis = scope.getSimulation().getProjectionFactory().getWorld();
				if (gis != null) // create envelope for environment
				{
					final Envelope env = scope.getSimulation().getEnvelope();
					gis = scope.getSimulation().getProjectionFactory().fromParams(params, env);
					result = SqlUtils.transform(scope, gis, result, false);
				}
			}
			/**
			 * AD
			 */

			// result.add(repRequest);

			if (DEBUG) {
				scope.getGui().debug("list of column name:" + result.get(0));
				scope.getGui().debug("list of column type:" + result.get(1));
				scope.getGui().debug("list of data:" + result.get(2));
			}

			st.close();

			rs.close();
		} catch (final SQLException e) {
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString(), scope);
		}
		// return repRequest;
		return result;
	}

	/*
	 * Make a connection to BDMS and execute the update statement
	 * (update/insert/delete/create/drop)
	 */

	public int executeUpdateDB(final IScope scope, final String updateComm) throws GamaRuntimeException {
		Connection conn = null;
		int n = 0;
		try {
			conn = connectDB();
			// if ( DEBUG ) {
			// scope.getGui().debug("Update Command:" + updateComm);
			// }
			final Statement st = conn.createStatement();
			n = st.executeUpdate(updateComm);
			// if ( DEBUG ) {
			// scope.getGui().debug("Updated records :" + n);
			// }

			st.close();
			// st=null;
			// System.gc();

			conn.close();
		} catch (final Exception e) {
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString(), scope);
		}

		// if ( DEBUG ) {
		// scope.getGui().informConsole(updateComm + " was run");
		// }

		return n;

	}

	/*
	 * execute the update statement with current
	 * connection(update/insert/delete/create/drop)
	 */
	public int executeUpdateDB(final IScope scope, final Connection conn, final String updateComm)
			throws GamaRuntimeException {
		int n = 0;
		try {
			// if ( DEBUG ) {
			// scope.getGui().debug("Update Command:" + updateComm);
			// }
			final Statement st = conn.createStatement();
			n = st.executeUpdate(updateComm);
			st.close();

			// if ( DEBUG ) {
			// scope.getGui().debug("Updated records :" + n);
			// }
		} catch (final SQLException e) {
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString(), scope);
		}

		return n;
	}

	protected IList<IList<Object>> resultSet2GamaList(final ResultSet rs) throws SQLException {
		final ResultSetMetaData rsmd = rs.getMetaData();
		return resultSet2GamaList(rsmd, rs);
	}

	/*
	 * @Method: getColumnName
	 *
	 * @Description: Get columns name
	 *
	 * @param ResultSetMetaData
	 *
	 * @return GamaList<String>
	 *
	 * @throws SQLException
	 */
	protected IList<Object> getColumnName(final ResultSetMetaData rsmd) throws SQLException {
		final int numberOfColumns = rsmd.getColumnCount();
		final IList<Object> columnType = GamaListFactory.create();
		for (int i = 1; i <= numberOfColumns; i++) {
			columnType.add(rsmd.getColumnName(i).toUpperCase());
		}
		return columnType;
	}

	public String getURL() {
		return url;
	}

	public String getVendor() {
		return vender;
	}

	public String getUser() {
		return userName;
	}

	/*
	 * @Method: getBounds( GamaList<Object> gamaList)
	 *
	 * @Description: Get Envelope of a set of geometry
	 *
	 * @param GamaList<Object> gamaList: gamalist is a set of geometry type
	 *
	 * @return Envelope: Envelope/boundary of the geometry set.
	 *
	 * @throws Exception
	 */

	// public static Envelope getBounds(final GamaList<? extends GamaList<?
	// super GamaList>> gamaList) {
	public static Envelope getBounds(final IList<? super IList<? super IList>> gamaList) {

		Envelope envelope;
		// get Column name
		final IList colNames = (IList) gamaList.get(0);
		// get Column type
		final IList colTypes = (IList) gamaList.get(1);
		final int index = colTypes.indexOf(GEOMETRYTYPE);
		if (index < 0) {
			return null;
		} else {
			// Get ResultSet
			final GamaList initValue = (GamaList) gamaList.get(2);
			final int n = initValue.size();
			// int max = number == null ? Integer.MAX_VALUE : numberOfAgents;
			if (n == 0) {
				return null;
			} else {
				GamaList<Object> rowList = (GamaList<Object>) initValue.get(0);
				Geometry geo = (Geometry) rowList.get(index);
				envelope = geo.getEnvelopeInternal();
				double maxX = envelope.getMaxX();
				double maxY = envelope.getMaxY();
				double minX = envelope.getMinX();
				double minY = envelope.getMinY();
				for (int i = 1; i < n && i < Integer.MAX_VALUE; i++) {
					rowList = (GamaList<Object>) initValue.get(i);
					geo = (Geometry) rowList.get(index);
					envelope = geo.getEnvelopeInternal();
					final double maxX1 = envelope.getMaxX();
					final double maxY1 = envelope.getMaxY();
					final double minX1 = envelope.getMinX();
					final double minY1 = envelope.getMinY();

					maxX = maxX > maxX1 ? maxX : maxX1;
					maxY = maxY > maxY1 ? maxY : maxY1;
					minX = minX < minX1 ? minX : minX1;
					minY = minY < minY1 ? minY : minY1;
					envelope.init(minX, maxX, minY, maxY);

				}
				return envelope;
			}
		}
	}

	/*-------------------------------------------------------------------------------------------------------------------------
	 * Make Insert a reccord into table
	 *
	 */
	public int insertDB(final IScope scope, final Connection conn, final String table_name, final GamaList<Object> cols,
			final GamaList<Object> values) throws GamaRuntimeException {
		int rec_no = -1;
		if (values.size() != cols.size()) {
			throw new IndexOutOfBoundsException("Size of columns list and values list are not equal");
		}
		try {
			// Get Insert command
			final Statement st = conn.createStatement();
			// String sqlStr = getInsertString(gis, conn, table_name, cols,
			// values);
			final String sqlStr = getInsertString(scope, conn, table_name, cols, values);
			if (DEBUG) {
				scope.getGui().debug("SQLConnection.insertBD.STR:" + sqlStr);
			}
			// rec_no = st.executeUpdate(getInsertString(scope, conn,
			// table_name, cols, values));
			rec_no = st.executeUpdate(sqlStr);
			st.close();
			// st=null;
			// System.gc();

			if (DEBUG) {
				scope.getGui().debug("SQLConnection.insertBD.rec_no:" + rec_no);
			}

		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertBD " + e.toString(), scope);
		}

		return rec_no;
	}

	/*-------------------------------------------------------------------------------------------------------------------------
	 *  Insert a reccord into table
	 *
	 */
	public int insertDB(final IScope scope, final Connection conn, final String table_name, final GamaList<Object> cols,
			final GamaList<Object> values, final Boolean transformed) throws GamaRuntimeException {
		this.transformed = transformed;
		int rec_no = -1;
		// Get Insert command
		rec_no = insertDB(scope, conn, table_name, cols, values);
		return rec_no;
	}

	/*-------------------------------------------------------------------------------------------------------------------------
	 *  Insert a reccord into table
	 *
	 */
	public int insertDB(final IScope scope, final String table_name, final GamaList<Object> cols,
			final GamaList<Object> values) throws GamaRuntimeException {
		Connection conn;
		int rec_no = -1;
		try {
			conn = connectDB();
			rec_no = insertDB(scope, conn, table_name, cols, values);
			conn.close();
		} catch (final Exception e) {
			throw GamaRuntimeException.error("SQLConnection.insertBD " + e.toString(), scope);
		}
		return rec_no;
	}

	/*
	 * Insert a reccord into table
	 */
	public int insertDB(final IScope scope, final String table_name, final GamaList<Object> cols,
			final GamaList<Object> values, final Boolean transformed) throws GamaRuntimeException {
		this.transformed = transformed;
		int rec_no = -1;
		rec_no = insertDB(scope, table_name, cols, values);
		return rec_no;
	}

	/*
	 * Insert a reccord into table
	 */
	public int insertDB(final IScope scope, final Connection conn, final String table_name,
			final GamaList<Object> values) throws GamaRuntimeException {
		int rec_no = -1;
		try {
			// Get Insert command
			final Statement st = conn.createStatement();

			rec_no = st.executeUpdate(getInsertString(scope, conn, table_name, values));
			st.close();
			// st=null;
			// System.gc();
			if (DEBUG) {
				scope.getGui().debug("SQLConnection.insertBD.rec_no:" + rec_no);
			}

		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertBD " + e.toString(), scope);
		}
		return rec_no;
	}

	public int insertDB(final IScope scope, final Connection conn, final String table_name,
			final GamaList<Object> values, final Boolean transformed) throws GamaRuntimeException {
		this.transformed = transformed;
		return insertDB(scope, conn, table_name, values);
	}

	/*
	 * Insert a reccord into table
	 */
	public int insertDB(final IScope scope, final String table_name, final GamaList<Object> values)
			throws GamaRuntimeException {
		Connection conn;
		int rec_no = -1;
		try {
			conn = connectDB();
			rec_no = insertDB(scope, conn, table_name, values);
			conn.close();
		} catch (final Exception e) {
			throw GamaRuntimeException.error("SQLConnection.insertBD " + e.toString(), scope);
		}
		return rec_no;
	}

	/*
	 * Insert a reccord into table
	 */
	public int insertDB(final IScope scope, final String table_name, final GamaList<Object> values,
			final Boolean transformed) throws GamaRuntimeException {
		this.transformed = transformed;
		return insertDB(scope, table_name, values);
	}

	/*
	 * @Method: executeQueryDB(Connection conn,String queryStr, GamaList<Object>
	 * condition_value)
	 *
	 * @Description: Executes the SQL query in this PreparedStatement object and
	 * returns the ResultSet object generated by the query
	 *
	 * @param queryStr: SQL query string with question mark (?).
	 *
	 * @param condition_value:List of values that are used to assign into
	 * conditions of queryStr
	 *
	 * @return ResultSet:returns the ResultSet object generated by the query.
	 *
	 * @throws GamaRuntimeException: if a database access error occurs or the
	 * SQL statement does not return a ResultSet object
	 */
	public IList<Object> executeQueryDB(final IScope scope, final Connection conn, final String queryStr,
			final IList<Object> condition_values) throws GamaRuntimeException {
		PreparedStatement pstmt = null;
		ResultSet rs;
		IList<Object> result = GamaListFactory.create();
		IList repRequest = GamaListFactory.create();
		final int condition_count = condition_values.size();
		try {

			pstmt = conn.prepareStatement(queryStr);

			// set value for each condition
			for (int i = 0; i < condition_count; i++) {
				pstmt.setObject(i + 1, condition_values.get(i));
			}
			rs = pstmt.executeQuery();

			final ResultSetMetaData rsmd = rs.getMetaData();
			if (DEBUG) {
				scope.getGui().debug("MetaData:" + rsmd.toString());
			}
			result.add(getColumnName(rsmd));
			final IList columns = getColumnTypeName(rsmd);
			result.add(columns);

			repRequest = resultSet2GamaList(rs);

			result.add(repRequest);

			/**
			 * AD: Added to transform Geometries
			 */
			// if ( columns.contains(GEOMETRYTYPE) && transformed) {
			// if ( gis == null ) {
			// // we have at least one geometry type and we compute the envelope
			// if no gis is present
			// // Envelope env = getBounds(repRequest);
			// Envelope env = getBounds(result);
			// // we now compute the GisUtils instance for our case (based on
			// params and env)
			// gis =
			// scope.getSimulationScope().getProjectionFactory().fromParams(params,
			// env);
			// }
			// // and we transform the geometries using its projection
			// // repRequest = SqlUtils.transform(gis, repRequest, false);
			// result = SqlUtils.transform(gis, result, false);
			// }
			if (columns.contains(GEOMETRYTYPE) && transformed) {
				gis = scope.getSimulation().getProjectionFactory().getWorld();
				if (gis != null) {
					final Envelope env = scope.getSimulation().getEnvelope();
					gis = scope.getSimulation().getProjectionFactory().fromParams(params, env);
					result = SqlUtils.transform(scope, gis, result, false);
				}
			}

			/**
			 * AD
			 */

			// result.add(repRequest);

			if (DEBUG) {
				scope.getGui().debug("list of column name:" + result.get(0));
				scope.getGui().debug("list of column type:" + result.get(1));
				scope.getGui().debug("list of data:" + result.get(2));
			}

			pstmt.close();
			// pstmt=null;
			// System.gc();

			rs.close();
		} catch (final SQLException e) {
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString(), scope);
		}
		// return repRequest;
		return result;

	}

	/*
	 * @Method: ExecuteQueryDB(Connection conn,String queryStr, GamaList<Object>
	 * condition_values)
	 *
	 * @Description: Executes the SQL query in this PreparedStatement object and
	 * returns the ResultSet object generated by the query
	 *
	 * @param conn: MAP of Connection parameters to RDBM
	 *
	 * @param queryStr: SQL query (select) string with question mark (?).
	 *
	 * @param condition_value:List of values that are used to assign into
	 * conditions of queryStr
	 *
	 * @return ResultSet:returns the ResultSet object generated by the query.
	 *
	 * @throws GamaRuntimeException: if a database access error occurs or the
	 * SQL statement does not return a ResultSet object
	 */
	public IList<Object> executeQueryDB(final IScope scope, final String queryStr, final IList<Object> condition_values)
			throws GamaRuntimeException {
		IList<Object> result = GamaListFactory.create();
		Connection conn;
		try {
			conn = connectDB();
			result = executeQueryDB(scope, conn, queryStr, condition_values);
			conn.close();
			// set value for each condition
		} catch (final Exception e) {
			throw GamaRuntimeException.error("SQLConnection.executeQuery: " + e.toString(), scope);
		}
		return result;

	}

	/*
	 * @Method: executeUpdateDB(Connection conn,String queryStr,
	 * GamaList<Object> condition_value)
	 *
	 * @Description: Executes the SQL statement in this PreparedStatement
	 * object, which must be an SQL INSERT, UPDATE or DELETE statement; or an
	 * SQL statement that returns nothing, such as a DDL statement.
	 *
	 * @param conn: MAP of Connection parameters to RDBM
	 *
	 * @param queryStr: an SQL INSERT, UPDATE or DELETE statement with question
	 * mark (?).
	 *
	 * @param condition_values: List of values that are used to assign into
	 * conditions of queryStr.
	 *
	 * @return row_count:either (1) the row count for INSERT, UPDATE, or DELETE
	 * statements or (2) 0 for SQL statements that return nothing
	 *
	 * @throws GamaRuntimeException
	 */
	public int executeUpdateDB(final IScope scope, final Connection conn, final String queryStr,
			final GamaList<Object> condition_values) throws GamaRuntimeException {
		PreparedStatement pstmt = null;
		int row_count = -1;
		final int condition_count = condition_values.size();
		try {
			pstmt = conn.prepareStatement(queryStr);
			// set value for each condition
			// if ( DEBUG ) {
			// scope.getGui().debug("SqlConnection.ExecuteUpdateDB.values.size:"
			// + condition_count);
			// scope.getGui().debug("SqlConnection.ExecuteUpdateDB.values.size:"
			// + condition_values.serialize(false));
			// }

			for (int i = 0; i < condition_count; i++) {
				pstmt.setObject(i + 1, condition_values.get(i));
			}
			row_count = pstmt.executeUpdate();

			pstmt.close();

		} catch (final SQLException e) {
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString(), scope);
		}
		return row_count;
	}

	/*
	 * @Method: executeUpdateDB(Connection conn,String queryStr,
	 * GamaList<Object> condition_value)
	 *
	 * @Description: Executes the SQL statement in this PreparedStatement
	 * object, which must be an SQL INSERT, UPDATE or DELETE statement; or an
	 * SQL statement that returns nothing, such as a DDL statement.
	 *
	 * @param queryStr: an SQL INSERT, UPDATE or DELETE statement with question
	 * mark (?).
	 *
	 * @param condition_values:
	 *
	 * @return row_count:either (1) the row count for INSERT, UPDATE, or DELETE
	 * statements or (2) 0 for SQL statements that return nothing
	 *
	 * @throws GamaRuntimeException
	 */
	public int executeUpdateDB(final IScope scope, final String queryStr, final GamaList<Object> condition_values)
			throws GamaRuntimeException {
		int row_count = -1;
		Connection conn;
		try {
			conn = connectDB();
			row_count = executeUpdateDB(scope, conn, queryStr, condition_values);
			conn.close();
			// set value for each condition
		} catch (final Exception e) {
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString(), scope);
		}
		return row_count;
	}

	public void setTransformed(final boolean tranformed) {
		this.transformed = tranformed;
	}

}// end of class
