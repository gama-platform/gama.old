/*********************************************************************************************
 *
 *
 * 'SqlConnection.java', in plugin 'msi.gama.core', is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
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

import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.metamodel.topology.projection.Projection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import ummisco.gama.dev.utils.DEBUG;

/*
 * @Author TRUONG Minh Thai Fredric AMBLARD Benoit GAUDOU Christophe Sibertin-BLANC Created date: 19-Apr-2013 Modified:
 * 26-Apr-2013: Remove driver msi.gama.ext/sqljdbc4.jar add driver msi.gama.ext/jtds-1.2.6.jar Change driver name for
 * MSSQL from com.microsoft.sqlserver.jdbc.SQLServerDriver to net.sourceforge.jtds.jdbc.Driver 18-July-2013: Add load
 * extension library for SQLITE case. 15-Jan-2014: Add datetime type. Add NULL VALUE Last Modified: 15-Jan-2014
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public abstract class SqlConnection {

	static final String MYSQL = "mysql";
	static final String POSTGRES = "postgres";
	static final String POSTGIS = "postgis";
	static final String MSSQL = "sqlserver";
	public static final String SQLITE = "sqlite";
	public static final String GEOMETRYTYPE = "GEOMETRY";
	static final String CHAR = "CHAR";
	static final String VARCHAR = "VARCHAR";
	static final String NVARCHAR = "NVARCHAR";
	static final String TEXT = "TEXT";
	static final String BLOB = "BLOB";
	static final String TIMESTAMP = "TIMESTAMP";
	static final String DATETIME = "DATETIME"; // MSSQL,Postgres, MySQL,

	static final String DATE = "DATE"; // MSSQL,Postgres, MySQL, SQlite
	static final String YEAR = "YEAR"; // Postgres, MySQL(yyyy)
	static final String TIME = "TIME"; // MySQL ('00:00:00')
	static final String NULLVALUE = "NULL";

	static final String MYSQLDriver = "com.mysql.jdbc.Driver";
	static final String MSSQLDriver = "net.sourceforge.jtds.jdbc.Driver";
	static final String SQLITEDriver = "org.sqlite.JDBC";
	static final String POSTGRESDriver = "org.postgresql.Driver";

	protected String vender = "";

	protected String url = "";
	protected String port = "";
	protected String dbName = "";
	protected String userName = "";
	protected String password = "";
	protected Boolean transformed = false;
	protected String extension = null;

	// AD: Added to be sure that SQL connections use a correct projection when
	// they load/save geometries
	private IProjection gis = null;
	// AD: Added to be sure to remember the parameters (which can contain other
	// informations about GIS data
	private Map<String, Object> params;

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
		final Boolean longitudeFirst =
				params.containsKey("longitudeFirst") ? (Boolean) params.get("longitudeFirst") : true;
		final String crs = (String) params.get("crs");
		if (crs != null) {
			try {
				return scope.getSimulation().getProjectionFactory().forSavingWith(scope, crs);
			} catch (final FactoryException e) {

				throw GamaRuntimeException.error("No factory found for decoding the EPSG " + crs
						+ " code. GAMA may be unable to save any GIS data", scope);

			}
		}
		final String srid = (String) params.get("srid");
		if (srid != null) {
			try {
				return scope.getSimulation().getProjectionFactory().forSavingWith(scope, Cast.asInt(scope, srid),
						longitudeFirst);
			} catch (final FactoryException e) {

				throw GamaRuntimeException.error("No factory found for decoding the EPSG " + srid
						+ " code. GAMA may be unable to save any GIS data", scope);

			}
		} else {
			try {
				return scope.getSimulation().getProjectionFactory().forSavingWith(scope,
						GamaPreferences.External.LIB_OUTPUT_CRS.getValue());
			} catch (final FactoryException e) {

				throw GamaRuntimeException.error(
						"No factory found for decoding the EPSG " + GamaPreferences.External.LIB_OUTPUT_CRS.getValue()
								+ " code. GAMA may be unable to save any GIS data",
						scope);

			}
		}

	}

	public void setParams(final Map<String, Object> params) {
		this.params = params;
	}

	SqlConnection(final String dbName) {
		this.dbName = dbName;
	}

	SqlConnection(final String venderName, final String database) {
		this.vender = venderName;
		this.dbName = database;
	}

	SqlConnection(final String venderName, final String database, final Boolean transformed) {
		this.vender = venderName;
		this.dbName = database;
		this.transformed = transformed;
	}

	SqlConnection(final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password) {
		this.vender = venderName;
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
	}

	SqlConnection(final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password, final Boolean transformed) {
		this.vender = venderName;
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
		this.transformed = transformed;
	}

	/*
	 * Make a connection to BDMS
	 */
	public abstract Connection connectDB()
			throws ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException;

	/*
	 * @Method:resultSet2IList(ResultSetMetaData rsmd, ResultSet rs)
	 *
	 * @Description: Convert RecordSet to IList
	 *
	 * @param ResultSetMetaData,ResultSet
	 *
	 * @return IList<IList<Object>>
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
	 * @return IList<String>
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
	 * @return IList<IList<Object>>
	 */
	public IList<? super IList<? super IList>> selectDB(final IScope scope, final String selectComm) {
		try (Connection conn = connectDB();) {
			return selectDB(scope, conn, selectComm);
		} catch (final Exception e) {
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString(), scope);
		}

	}

	/*
	 * Make a connection to BDMS and execute the select statement
	 *
	 * @return IList<IList<Object>>
	 */
	// public IList<IList<Object>> selectDB(String selectComm)
	public IList<? super IList<? super IList>> selectDB(final IScope scope, final Connection conn,
			final String selectComm) {
		;
		// ResultSet rs;
		IList<? super IList<? super IList>> result =
				GamaListFactory.create(msi.gaml.types.Types.LIST.of(msi.gaml.types.Types.LIST));
		// IList<? extends IList<? super IList>> result = new
		// IList();

		// IList<Object> rowList = new IList<Object>();
		IList repRequest;
		try (final Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(selectComm);) {

			final ResultSetMetaData rsmd = rs.getMetaData();
			if (DEBUG.IS_ON()) {
				DEBUG.OUT("MetaData:" + rsmd.toString());
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
					final Envelope3D env = scope.getSimulation().getEnvelope();
					gis = scope.getSimulation().getProjectionFactory().fromParams(scope, params, env);
					result = SqlUtils.transform(scope, gis, result, false);
				}
			}
			/**
			 * AD
			 */

			// result.add(repRequest);

			if (DEBUG.IS_ON()) {
				DEBUG.OUT("list of column name:" + result.get(0));
				DEBUG.OUT("list of column type:" + result.get(1));
				DEBUG.OUT("list of data:" + result.get(2));
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
	 * Make a connection to BDMS and execute the update statement (update/insert/delete/create/drop)
	 */

	public int executeUpdateDB(final IScope scope, final String updateComm) throws GamaRuntimeException {

		int n = 0;
		try (Connection conn = connectDB(); final Statement st = conn.createStatement();) {

			// if ( DEBUG ) {
			// DEBUG.OUT("Update Command:" + updateComm);
			// }

			n = st.executeUpdate(updateComm);
			// if ( DEBUG ) {
			// DEBUG.OUT("Updated records :" + n);
			// }

			// st=null;
			// System.gc();

		} catch (final Exception e) {
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString(), scope);
		}

		// if ( DEBUG ) {
		// scope.getGui().informConsole(updateComm + " was run");
		// }

		return n;

	}

	/*
	 * execute the update statement with current connection(update/insert/delete/create/drop)
	 */
	public int executeUpdateDB(final IScope scope, final Connection conn, final String updateComm)
			throws GamaRuntimeException {
		int n = 0;
		try (final Statement st = conn.createStatement();) {
			n = st.executeUpdate(updateComm);
		} catch (

		final SQLException e) {
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString(), scope);
		}

		return n;
	}

	private IList<IList<Object>> resultSet2GamaList(final ResultSet rs) throws SQLException {
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
	 * @return IList<String>
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
	 * @Method: getBounds( IList<Object> IList)
	 *
	 * @Description: Get Envelope of a set of geometry
	 *
	 * @param IList<Object> IList: IList is a set of geometry type
	 *
	 * @return Envelope: Envelope/boundary of the geometry set.
	 *
	 * @throws Exception
	 */

	// public static Envelope getBounds(final IList<? extends IList<?
	// super IList>> IList) {
	public static Envelope3D getBounds(final IList<? super IList<? super IList>> IList) {

		Envelope3D envelope;
		// get Column name
		// final IList colNames = (IList) IList.get(0);
		// get Column type
		final IList colTypes = (IList) IList.get(1);
		final int index = colTypes.indexOf(GEOMETRYTYPE);
		if (index < 0) {
			return null;
		} else {
			// Get ResultSet
			final IList initValue = (IList) IList.get(2);
			final int n = initValue.size();
			// int max = number == null ? Integer.MAX_VALUE : numberOfAgents;
			if (n == 0) {
				return null;
			} else {
				IList<Object> rowList = (IList<Object>) initValue.get(0);
				Geometry geo = (Geometry) rowList.get(index);
				envelope = Envelope3D.of(geo);
				double maxX = envelope.getMaxX();
				double maxY = envelope.getMaxY();
				double minX = envelope.getMinX();
				double minY = envelope.getMinY();
				for (int i = 1; i < n && i < Integer.MAX_VALUE; i++) {
					rowList = (IList<Object>) initValue.get(i);
					geo = (Geometry) rowList.get(index);
					envelope = Envelope3D.of(geo);
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
	public int insertDB(final IScope scope, final Connection conn, final String table_name, final IList<Object> cols,
			final IList<Object> values) throws GamaRuntimeException {
		int rec_no = -1;
		if (values.size() != cols.size()) {
			throw new IndexOutOfBoundsException("Size of columns list and values list are not equal");
		}
		try (final Statement st = conn.createStatement();) {
			// String sqlStr = getInsertString(gis, conn, table_name, cols,
			// values);
			final String sqlStr = getInsertString(scope, conn, table_name, cols, values);
			if (DEBUG.IS_ON()) {
				DEBUG.OUT("SQLConnection.insertBD.STR:" + sqlStr);
			}
			// rec_no = st.executeUpdate(getInsertString(scope, conn,
			// table_name, cols, values));
			rec_no = st.executeUpdate(sqlStr);
			// st=null;
			// System.gc();

			if (DEBUG.IS_ON()) {
				DEBUG.OUT("SQLConnection.insertBD.rec_no:" + rec_no);
			}

		} catch (final SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertBD " + e.toString(), scope);
		}

		return rec_no;
	}

	/*-------------------------------------------------------------------------------------------------------------------------
	 *  Insert a reccord into table
	 *
	 */
	public int insertDB(final IScope scope, final String table_name, final IList<Object> cols,
			final IList<Object> values) throws GamaRuntimeException {
		int rec_no = -1;
		try (Connection conn = connectDB();) {
			rec_no = insertDB(scope, conn, table_name, cols, values);
		} catch (final Exception e) {
			throw GamaRuntimeException.error("SQLConnection.insertBD " + e.toString(), scope);
		}
		return rec_no;
	}

	/*
	 * Insert a reccord into table
	 */
	public int insertDB(final IScope scope, final Connection conn, final String table_name, final IList<Object> values)
			throws GamaRuntimeException {
		int rec_no = -1;
		try (final Statement st = conn.createStatement();) {
			// Get Insert command

			rec_no = st.executeUpdate(getInsertString(scope, conn, table_name, values));
			// st=null;
			// System.gc();
			if (DEBUG.IS_ON()) {
				DEBUG.OUT("SQLConnection.insertBD.rec_no:" + rec_no);
			}

		} catch (final SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("SQLConnection.insertBD " + e.toString(), scope);
		}
		return rec_no;
	}

	/*
	 * Insert a reccord into table
	 */
	public int insertDB(final IScope scope, final String table_name, final IList<Object> values)
			throws GamaRuntimeException {
		int rec_no = -1;
		try (Connection conn = connectDB();) {
			rec_no = insertDB(scope, conn, table_name, values);
		} catch (final Exception e) {
			throw GamaRuntimeException.error("SQLConnection.insertBD " + e.toString(), scope);
		}
		return rec_no;
	}

	/*
	 * @Method: executeQueryDB(Connection conn,String queryStr, IList<Object> condition_value)
	 *
	 * @Description: Executes the SQL query in this PreparedStatement object and returns the ResultSet object generated
	 * by the query
	 *
	 * @param queryStr: SQL query string with question mark (?).
	 *
	 * @param condition_value:List of values that are used to assign into conditions of queryStr
	 *
	 * @return ResultSet:returns the ResultSet object generated by the query.
	 *
	 * @throws GamaRuntimeException: if a database access error occurs or the SQL statement does not return a ResultSet
	 * object
	 */
	public IList<Object> executeQueryDB(final IScope scope, final Connection conn, final String queryStr,
			final IList<Object> condition_values) throws GamaRuntimeException {

		IList<Object> result = GamaListFactory.create();
		IList repRequest;
		final int condition_count = condition_values.size();
		try (PreparedStatement pstmt = conn.prepareStatement(queryStr);) {

			// set value for each condition
			for (int i = 0; i < condition_count; i++) {
				pstmt.setObject(i + 1, condition_values.get(i));
			}
			try (ResultSet rs = pstmt.executeQuery();) {

				final ResultSetMetaData rsmd = rs.getMetaData();
				if (DEBUG.IS_ON()) {
					DEBUG.OUT("MetaData:" + rsmd.toString());
				}
				result.add(getColumnName(rsmd));
				final IList columns = getColumnTypeName(rsmd);
				result.add(columns);

				repRequest = resultSet2GamaList(rs);
				if (columns.contains(GEOMETRYTYPE) && transformed) {
					gis = scope.getSimulation().getProjectionFactory().getWorld();
					if (gis != null) {
						final Envelope3D env = scope.getSimulation().getEnvelope();
						gis = scope.getSimulation().getProjectionFactory().fromParams(scope, params, env);
						result = SqlUtils.transform(scope, gis, result, false);
					}
				}
			}

			result.add(repRequest);

		} catch (final SQLException e) {
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString(), scope);
		}
		return result;

	}

	/*
	 * @Method: ExecuteQueryDB(Connection conn,String queryStr, IList<Object> condition_values)
	 *
	 * @Description: Executes the SQL query in this PreparedStatement object and returns the ResultSet object generated
	 * by the query
	 *
	 * @param conn: MAP of Connection parameters to RDBM
	 *
	 * @param queryStr: SQL query (select) string with question mark (?).
	 *
	 * @param condition_value:List of values that are used to assign into conditions of queryStr
	 *
	 * @return ResultSet:returns the ResultSet object generated by the query.
	 *
	 * @throws GamaRuntimeException: if a database access error occurs or the SQL statement does not return a ResultSet
	 * object
	 */
	public IList<Object> executeQueryDB(final IScope scope, final String queryStr, final IList<Object> condition_values)
			throws GamaRuntimeException {
		IList<Object> result;
		try (Connection conn = connectDB();) {
			result = executeQueryDB(scope, conn, queryStr, condition_values);
			// set value for each condition
		} catch (final Exception e) {
			throw GamaRuntimeException.error("SQLConnection.executeQuery: " + e.toString(), scope);
		}
		return result;

	}

	/*
	 * @Method: executeUpdateDB(Connection conn,String queryStr, IList<Object> condition_value)
	 *
	 * @Description: Executes the SQL statement in this PreparedStatement object, which must be an SQL INSERT, UPDATE or
	 * DELETE statement; or an SQL statement that returns nothing, such as a DDL statement.
	 *
	 * @param conn: MAP of Connection parameters to RDBM
	 *
	 * @param queryStr: an SQL INSERT, UPDATE or DELETE statement with question mark (?).
	 *
	 * @param condition_values: List of values that are used to assign into conditions of queryStr.
	 *
	 * @return row_count:either (1) the row count for INSERT, UPDATE, or DELETE statements or (2) 0 for SQL statements
	 * that return nothing
	 *
	 * @throws GamaRuntimeException
	 */
	public int executeUpdateDB(final IScope scope, final Connection conn, final String queryStr,
			final IList<Object> condition_values) throws GamaRuntimeException {
		int row_count = -1;
		final int condition_count = condition_values.size();
		try (final PreparedStatement pstmt = conn.prepareStatement(queryStr);) {
			// set value for each condition
			// if ( DEBUG ) {
			// DEBUG.OUT("SqlConnection.ExecuteUpdateDB.values.size:"
			// + condition_count);
			// DEBUG.OUT("SqlConnection.ExecuteUpdateDB.values.size:"
			// + condition_values.serialize(false));
			// }

			for (int i = 0; i < condition_count; i++) {
				pstmt.setObject(i + 1, condition_values.get(i));
			}
			row_count = pstmt.executeUpdate();

		} catch (final SQLException e) {
			throw GamaRuntimeException.error("SQLConnection.selectDB: " + e.toString(), scope);
		}
		return row_count;
	}

	/*
	 * @Method: executeUpdateDB(Connection conn,String queryStr, IList<Object> condition_value)
	 *
	 * @Description: Executes the SQL statement in this PreparedStatement object, which must be an SQL INSERT, UPDATE or
	 * DELETE statement; or an SQL statement that returns nothing, such as a DDL statement.
	 *
	 * @param queryStr: an SQL INSERT, UPDATE or DELETE statement with question mark (?).
	 *
	 * @param condition_values:
	 *
	 * @return row_count:either (1) the row count for INSERT, UPDATE, or DELETE statements or (2) 0 for SQL statements
	 * that return nothing
	 *
	 * @throws GamaRuntimeException
	 */
	public int executeUpdateDB(final IScope scope, final String queryStr, final IList<Object> condition_values)
			throws GamaRuntimeException {
		int row_count = -1;
		try (Connection conn = connectDB();) {
			row_count = executeUpdateDB(scope, conn, queryStr, condition_values);

			// set value for each condition
		} catch (

		final Exception e) {
			throw GamaRuntimeException.error("SQLConnection.executeUpdateDB: " + e.toString(), scope);
		}
		return row_count;
	}

	public void setTransformed(final boolean tranformed) {
		this.transformed = tranformed;
	}

}// end of class
