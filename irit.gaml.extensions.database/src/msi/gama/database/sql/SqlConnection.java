/*******************************************************************************************************
 *
 * SqlConnection.java, in irit.gaml.extensions.database, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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

/**
 * The Class SqlConnection.
 */
/*
 * @Author TRUONG Minh Thai Fredric AMBLARD Benoit GAUDOU Christophe Sibertin-BLANC Created date: 19-Apr-2013 Modified:
 * 26-Apr-2013: Remove driver msi.gama.ext/sqljdbc4.jar add driver msi.gama.ext/jtds-1.2.6.jar Change driver name for
 * MSSQL from com.microsoft.sqlserver.jdbc.SQLServerDriver to net.sourceforge.jtds.jdbc.Driver 18-July-2013: Add load
 * extension library for SQLITE case. 15-Jan-2014: Add datetime type. Add NULL VALUE Last Modified: 15-Jan-2014
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public abstract class SqlConnection {

	/** The Constant MYSQL. */
	static final String MYSQL = "mysql";
	
	/** The Constant POSTGRES. */
	static final String POSTGRES = "postgres";
	
	/** The Constant POSTGIS. */
	static final String POSTGIS = "postgis";
	
	/** The Constant MSSQL. */
	static final String MSSQL = "sqlserver";
	
	/** The Constant SQLITE. */
	public static final String SQLITE = "sqlite";
	
	/** The Constant GEOMETRYTYPE. */
	public static final String GEOMETRYTYPE = "GEOMETRY";
	
	/** The Constant CHAR. */
	static final String CHAR = "CHAR";
	
	/** The Constant VARCHAR. */
	static final String VARCHAR = "VARCHAR";
	
	/** The Constant NVARCHAR. */
	static final String NVARCHAR = "NVARCHAR";
	
	/** The Constant TEXT. */
	static final String TEXT = "TEXT";
	
	/** The Constant BLOB. */
	static final String BLOB = "BLOB";
	
	/** The Constant TIMESTAMP. */
	static final String TIMESTAMP = "TIMESTAMP";
	
	/** The Constant DATETIME. */
	static final String DATETIME = "DATETIME"; // MSSQL,Postgres, MySQL,

	/** The Constant DATE. */
	static final String DATE = "DATE"; // MSSQL,Postgres, MySQL, SQlite
	
	/** The Constant YEAR. */
	static final String YEAR = "YEAR"; // Postgres, MySQL(yyyy)
	
	/** The Constant TIME. */
	static final String TIME = "TIME"; // MySQL ('00:00:00')
	
	/** The Constant NULLVALUE. */
	static final String NULLVALUE = "NULL";

	/** The Constant MYSQLDriver. */
	static final String MYSQLDriver = "com.mysql.jdbc.Driver";
	
	/** The Constant MSSQLDriver. */
	static final String MSSQLDriver = "net.sourceforge.jtds.jdbc.Driver";
	
	/** The Constant SQLITEDriver. */
	static final String SQLITEDriver = "org.sqlite.JDBC";
	
	/** The Constant POSTGRESDriver. */
	static final String POSTGRESDriver = "org.postgresql.Driver";

	/** The vender. */
	protected String vender = "";

	/** The url. */
	protected String url = "";
	
	/** The port. */
	protected String port = "";
	
	/** The db name. */
	protected String dbName = "";
	
	/** The user name. */
	protected String userName = "";
	
	/** The password. */
	protected String password = "";
	
	/** The transformed. */
	protected Boolean transformed = false;
	
	/** The extension. */
	protected String extension = null;

	// AD: Added to be sure that SQL connections use a correct projection when
	/** The gis. */
	// they load/save geometries
	private IProjection gis = null;
	// AD: Added to be sure to remember the parameters (which can contain other
	/** The params. */
	// informations about GIS data
	private Map<String, Object> params;

	/**
	 * Sets the gis.
	 *
	 * @param gis the new gis
	 */
	public void setGis(final Projection gis) {
		this.gis = gis;
	}

	/**
	 * Gets the gis.
	 *
	 * @return the gis
	 */
	public IProjection getGis() {
		return this.gis;
	}

	/**
	 * Gets the transform.
	 *
	 * @return the transform
	 */
	public boolean getTransform() {
		return transformed;
	}

	/**
	 * Gets the saving gis projection.
	 *
	 * @param scope the scope
	 * @return the saving gis projection
	 */
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

	/**
	 * Sets the params.
	 *
	 * @param params the params
	 */
	public void setParams(final Map<String, Object> params) {
		this.params = params;
	}

	/**
	 * Instantiates a new sql connection.
	 *
	 * @param dbName the db name
	 */
	SqlConnection(final String dbName) {
		this.dbName = dbName;
	}

	/**
	 * Instantiates a new sql connection.
	 *
	 * @param venderName the vender name
	 * @param database the database
	 */
	SqlConnection(final String venderName, final String database) {
		this.vender = venderName;
		this.dbName = database;
	}

	/**
	 * Instantiates a new sql connection.
	 *
	 * @param venderName the vender name
	 * @param database the database
	 * @param transformed the transformed
	 */
	SqlConnection(final String venderName, final String database, final Boolean transformed) {
		this.vender = venderName;
		this.dbName = database;
		this.transformed = transformed;
	}

	/**
	 * Instantiates a new sql connection.
	 *
	 * @param venderName the vender name
	 * @param url the url
	 * @param port the port
	 * @param dbName the db name
	 * @param userName the user name
	 * @param password the password
	 */
	SqlConnection(final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password) {
		this.vender = venderName;
		this.url = url;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
	}

	/**
	 * Instantiates a new sql connection.
	 *
	 * @param venderName the vender name
	 * @param url the url
	 * @param port the port
	 * @param dbName the db name
	 * @param userName the user name
	 * @param password the password
	 * @param transformed the transformed
	 */
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

	/**
	 * Connect DB.
	 *
	 * @return the connection
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InstantiationException the instantiation exception
	 * @throws SQLException the SQL exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	/*
	 * Make a connection to BDMS
	 */
	public abstract Connection connectDB()
			throws ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException;

	/**
	 * Result set 2 gama list.
	 *
	 * @param rsmd the rsmd
	 * @param rs the rs
	 * @return the i list
	 */
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

	/**
	 * Gets the geometry columns.
	 *
	 * @param rsmd the rsmd
	 * @return the geometry columns
	 * @throws SQLException the SQL exception
	 */
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

	/**
	 * Gets the column type name.
	 *
	 * @param rsmd the rsmd
	 * @return the column type name
	 * @throws SQLException the SQL exception
	 */
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

	/**
	 * Gets the insert string.
	 *
	 * @param scope the scope
	 * @param conn the conn
	 * @param table_name the table name
	 * @param cols the cols
	 * @param values the values
	 * @return the insert string
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	/*
	 * Make insert command string with columns and values
	 */
	protected abstract String getInsertString(IScope scope, Connection conn, String table_name, IList<Object> cols,
			IList<Object> values) throws GamaRuntimeException;

	/**
	 * Gets the insert string.
	 *
	 * @param scope the scope
	 * @param conn the conn
	 * @param table_name the table name
	 * @param values the values
	 * @return the insert string
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	/*
	 * Make insert command string for all columns with values
	 */
	protected abstract String getInsertString(IScope scope, Connection conn, String table_name, IList<Object> values)
			throws GamaRuntimeException;

	/**
	 * Select DB.
	 *
	 * @param scope the scope
	 * @param selectComm the select comm
	 * @return the i list<? super I list<? super I list>>
	 */
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
	/**
	 * Select DB.
	 *
	 * @param scope the scope
	 * @param conn the conn
	 * @param selectComm the select comm
	 * @return the i list<? super I list<? super I list>>
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

	/**
	 * Execute update DB.
	 *
	 * @param scope the scope
	 * @param updateComm the update comm
	 * @return the int
	 * @throws GamaRuntimeException the gama runtime exception
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

	/**
	 * Execute update DB.
	 *
	 * @param scope the scope
	 * @param conn the conn
	 * @param updateComm the update comm
	 * @return the int
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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

	/**
	 * Result set 2 gama list.
	 *
	 * @param rs the rs
	 * @return the i list
	 * @throws SQLException the SQL exception
	 */
	private IList<IList<Object>> resultSet2GamaList(final ResultSet rs) throws SQLException {
		final ResultSetMetaData rsmd = rs.getMetaData();
		return resultSet2GamaList(rsmd, rs);
	}

	/**
	 * Gets the column name.
	 *
	 * @param rsmd the rsmd
	 * @return the column name
	 * @throws SQLException the SQL exception
	 */
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

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getURL() {
		return url;
	}

	/**
	 * Gets the vendor.
	 *
	 * @return the vendor
	 */
	public String getVendor() {
		return vender;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
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
	/**
	 * Gets the bounds.
	 *
	 * @param IList the i list
	 * @return the bounds
	 */
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

	/**
	 * Insert DB.
	 *
	 * @param scope the scope
	 * @param conn the conn
	 * @param table_name the table name
	 * @param cols the cols
	 * @param values the values
	 * @return the int
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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

	/**
	 * Insert DB.
	 *
	 * @param scope the scope
	 * @param table_name the table name
	 * @param cols the cols
	 * @param values the values
	 * @return the int
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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

	/**
	 * Insert DB.
	 *
	 * @param scope the scope
	 * @param conn the conn
	 * @param table_name the table name
	 * @param values the values
	 * @return the int
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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

	/**
	 * Insert DB.
	 *
	 * @param scope the scope
	 * @param table_name the table name
	 * @param values the values
	 * @return the int
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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

	/**
	 * Execute query DB.
	 *
	 * @param scope the scope
	 * @param conn the conn
	 * @param queryStr the query str
	 * @param condition_values the condition values
	 * @return the i list
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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

	/**
	 * Execute query DB.
	 *
	 * @param scope the scope
	 * @param queryStr the query str
	 * @param condition_values the condition values
	 * @return the i list
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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

	/**
	 * Execute update DB.
	 *
	 * @param scope the scope
	 * @param conn the conn
	 * @param queryStr the query str
	 * @param condition_values the condition values
	 * @return the int
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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

	/**
	 * Execute update DB.
	 *
	 * @param scope the scope
	 * @param queryStr the query str
	 * @param condition_values the condition values
	 * @return the int
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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

	/**
	 * Sets the transformed.
	 *
	 * @param tranformed the new transformed
	 */
	public void setTransformed(final boolean tranformed) {
		this.transformed = tranformed;
	}

}// end of class
