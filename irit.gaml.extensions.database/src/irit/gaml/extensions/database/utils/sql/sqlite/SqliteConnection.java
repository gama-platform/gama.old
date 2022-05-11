/*******************************************************************************************************
 *
 * SqliteConnection.java, in irit.gaml.extensions.database, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package irit.gaml.extensions.database.utils.sql.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.sqlite.SQLiteConfig;

import irit.gaml.extensions.database.utils.sql.SqlConnection;
import irit.gaml.extensions.database.utils.sql.SqlUtils;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class SqliteConnection.
 */
/*
 * @Author TRUONG Minh Thai Fredric AMBLARD Benoit GAUDOU Christophe Sibertin-BLANC Created date: 19-Apr-2013 Modified:
 * 18-July-2013: Add load extension library for SQLITE case. Correct error getColumnTypeName when return null value
 * 23-July-2013 Modify connectDB() method: - Add load Extention. - Clean memory(garbage collection) after load.
 * 15-Jan-2014 Fix null error of getInsertString method
 *
 *
 * Last Modified: 15-Jan-2014
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SqliteConnection extends SqlConnection {

	/** The Constant SQLITE. */
	public static final String SQLITE = "sqlite";	

	/** The Constant SQLITE. */
	public static final String SPATIALITE = "spatialite";
	
	/** The Constant WKT2GEO. */
	private static final String WKT2GEO = "GeomFromText";

	/**
	 * Instantiates a new sqlite connection.
	 *
	 * @param venderName
	 *            the vender name
	 * @param database
	 *            the database
	 * @param transformed
	 *            the transformed
	 */
	public SqliteConnection(final String venderName, final String database, final Boolean transformed) {
		super(venderName, database, transformed);
	}

	/**
	 * Instantiates a new sqlite connection.
	 *
	 * @param venderName
	 *            the vender name
	 * @param database
	 *            the database
	 * @param extension
	 *            the extension
	 * @param transformed
	 *            the transformed
	 */
	public SqliteConnection(final String venderName, final String database, final String extension,
			final Boolean transformed) {
		super(venderName, database, transformed);
		this.extension = extension;
	}

	@Override
	public Connection connectDB()
			throws ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException {
		Connection conn = null;
		try {
			if (!SQLITE.equalsIgnoreCase(vender))
				throw new ClassNotFoundException("SqliteConnection.connectSQL: The " + vender + " is not supported!");
			Class.forName(SQLITEDriver).newInstance();
			final SQLiteConfig config = new SQLiteConfig();
			config.enableLoadExtension(true);
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbName, config.toProperties());
			// load Spatialite extension library
			if (extension != null && new File(extension).exists()) { load_extension(conn, extension); }
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			throw new ClassNotFoundException(e.toString());
		} catch (final InstantiationException e) {
			e.printStackTrace();
			throw new InstantiationException(e.toString());
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
			throw new IllegalAccessException(e.toString());
		} catch (final SQLException e) {
			e.printStackTrace();
			throw new SQLException(e.toString());
		}
		return conn;

	}

	@Override
	protected IList<IList<Object>> resultSet2GamaList(final ResultSetMetaData rsmd, final ResultSet rs) {
		// convert Geometry in SQL to Geometry type in GeoTool

		final IList<IList<Object>> repRequest =
				GamaListFactory.create(msi.gaml.types.Types.LIST.of(msi.gaml.types.Types.LIST));
		try {
			final List<Integer> geoColumn = getGeometryColumns(rsmd);
			final int nbCol = rsmd.getColumnCount();
			while (rs.next()) {
				final IList<Object> rowList = GamaListFactory.create();
				for (int j = 1; j <= nbCol; j++) {
					// check column is geometry column?
					if (geoColumn.contains(j)) {
						rowList.add(SqlUtils.read(rs.getBytes(j)));
					} else {
						rowList.add(rs.getObject(j));
					}
				}
				repRequest.add(rowList);
			}
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, null);
		}
		return repRequest;

	}

	@Override
	protected List<Integer> getGeometryColumns(final ResultSetMetaData rsmd) throws SQLException {
		final int numberOfColumns = rsmd.getColumnCount();
		final List<Integer> geoColumn = new ArrayList<>();
		for (int i = 1; i <= numberOfColumns; i++) {
			// Search column with Geometry type
			if (SQLITE.equalsIgnoreCase(vender) && rsmd.getColumnType(i) == 2004) { geoColumn.add(i); }
		}
		return geoColumn;

	}

	@Override
	protected IList<Object> getColumnTypeName(final ResultSetMetaData rsmd) throws SQLException {
		final int numberOfColumns = rsmd.getColumnCount();
		final IList<Object> columnType = GamaListFactory.create();
		for (int i = 1; i <= numberOfColumns; i++) {
			// Search column with Geometry type
			if (SQLITE.equalsIgnoreCase(vender) && rsmd.getColumnType(i) == 2004) {
				columnType.add(GEOMETRYTYPE);
			} else {
				columnType.add(rsmd.getColumnTypeName(i).toUpperCase());
			}
		}
		return columnType;

	}

	@Override
	protected String getInsertString(final IScope scope, final Connection conn, final String table_name,
			final IList<Object> cols, final IList<Object> values) throws GamaRuntimeException {
		final int col_no = cols.size();
		String insertStr = "INSERT INTO ";
		StringBuilder selectStr = new StringBuilder("SELECT ");
		String colStr = "";
		String valueStr = "";
		// Check size of parameters
		if (values.size() != col_no)
			throw new IndexOutOfBoundsException("Size of columns list and values list are not equal");
		// Get column name
		for (int i = 0; i < col_no; i++) {
			if (i == col_no - 1) {
				colStr = colStr + (String) cols.get(i);
			} else {
				colStr = colStr + (String) cols.get(i) + ",";
			}
		}
		// create SELECT statement string
		selectStr.append(colStr).append(" FROM ").append(table_name).append(" LIMIT 1 ;");

		if (DEBUG.IS_ON()) { DEBUG.OUT("SqliteConnection.getInsertString.select command:" + selectStr.toString()); }

		try {
			final IList<Object> col_Types = getColumnTypeName(scope, conn, table_name, cols);

			// Insert command
			// set parameter value
			valueStr = "";
			final IProjection saveGis = getSavingGisProjection(scope);
			for (int i = 0; i < col_no; i++) {
				// Value list begin-------------------------------------------
				if (values.get(i) == null) {
					valueStr = valueStr + NULLVALUE;
				} else if (GEOMETRYTYPE.equalsIgnoreCase((String) col_Types.get(i))) { // for

					final WKTReader wkt = new WKTReader();
					Geometry geo = wkt.read(values.get(i).toString());
					// DEBUG.LOG(geo.toString());
					if (transformed) { geo = saveGis.inverseTransform(geo); }
					valueStr = valueStr + WKT2GEO + "('" + geo.toString() + "')";
				} else if (CHAR.equalsIgnoreCase((String) col_Types.get(i))
						|| VARCHAR.equalsIgnoreCase((String) col_Types.get(i))
						|| NVARCHAR.equalsIgnoreCase((String) col_Types.get(i))
						|| TEXT.equalsIgnoreCase((String) col_Types.get(i))) { // for
																					// String
																					// type
					// Correct error string
					String temp = values.get(i).toString();
					temp = temp.replace("'", "''");
					// Add to value:
					valueStr = valueStr + "'" + temp + "'";
				} else { // For other type
					valueStr = valueStr + values.get(i).toString();
				}
				if (i != col_no - 1) { // Add delimiter of each value
					valueStr = valueStr + ",";
				}
				// Value list
				// end--------------------------------------------------------

			}
			insertStr = insertStr + table_name + "(" + colStr + ") " + "VALUES(" + valueStr + ")";

			if (DEBUG.IS_ON()) { DEBUG.OUT("SqliteConnection.getInsertString:" + insertStr); }

		} catch (final SQLException | ParseException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("SqliteConnection.insertBD " + e.toString(), scope);
		}

		return insertStr;
	}

	@Override
	protected String getInsertString(final IScope scope, final Connection conn, final String table_name,
			final IList<Object> values) throws GamaRuntimeException {
		StringBuilder insertStr = new StringBuilder("INSERT INTO ");
		String selectStr = "SELECT ";
		String colStr = "";
		String valueStr = "";

		// Get column name
		// create SELECT statement string
		selectStr = selectStr + " * " + " FROM " + table_name + " LIMIT 1 ;";

		try {
			// get column type;
			final Statement st = conn.createStatement();
			final ResultSet rs = st.executeQuery(selectStr);
			final ResultSetMetaData rsmd = rs.getMetaData();
			final IList<Object> col_Names = getColumnName(rsmd);
			final IList<Object> col_Types = getColumnTypeName(scope, conn, table_name, col_Names);

			final int col_no = col_Names.size();
			// Check size of parameters
			if (values.size() != col_Names.size())
				throw new IndexOutOfBoundsException("Size of columns list and values list are not equal");

			if (DEBUG.IS_ON()) {
				DEBUG.OUT("list of column Name:" + col_Names);
				DEBUG.OUT("list of column type:" + col_Types);
			}
			// Insert command
			// set parameter value
			colStr = "";
			valueStr = "";
			for (int i = 0; i < col_no; i++) {
				// Value list begin-------------------------------------------
				if (values.get(i) == null) {
					valueStr = valueStr + NULLVALUE;
				} else if (GEOMETRYTYPE.equalsIgnoreCase((String) col_Types.get(i))) { // for
					final WKTReader wkt = new WKTReader();
					Geometry geo = wkt.read(values.get(i).toString());
					if (transformed) { geo = getSavingGisProjection(scope).inverseTransform(geo); }
					valueStr = valueStr + WKT2GEO + "('" + geo.toString() + "')";

				} else if (CHAR.equalsIgnoreCase((String) col_Types.get(i))
						|| VARCHAR.equalsIgnoreCase((String) col_Types.get(i))
						|| NVARCHAR.equalsIgnoreCase((String) col_Types.get(i))
						|| TEXT.equalsIgnoreCase((String) col_Types.get(i))) {
					String temp = values.get(i).toString();
					temp = temp.replace("'", "''");
					// Add to value:
					valueStr = valueStr + "'" + temp + "'";
				} else { // For other type
					valueStr = valueStr + values.get(i).toString();
				}
				colStr = colStr + col_Names.get(i).toString();

				if (i != col_no - 1) { // Add delimiter of each value
					colStr = colStr + ",";
					valueStr = valueStr + ",";
				}
			}

			insertStr.append(table_name).append("(").append(colStr).append(") ").append("VALUES(").append(valueStr)
					.append(")");

		} catch (final SQLException | ParseException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("SqliteConnection.getInsertString:" + e.toString(), scope);
		}

		return insertStr.toString();
	}

	/**
	 * Gets the column type name.
	 *
	 * @param scope
	 *            the scope
	 * @param conn
	 *            the conn
	 * @param tableName
	 *            the table name
	 * @param columns
	 *            the columns
	 * @return the column type name
	 * @throws SQLException
	 *             the SQL exception
	 */
	// 18/July/2013
	private IList<Object> getColumnTypeName(final IScope scope, final Connection conn, final String tableName,
			final IList<Object> columns) throws SQLException {
		final int numberOfColumns = columns.size();
		final IList<Object> columnType = GamaListFactory.create();
		final String sqlStr = "PRAGMA table_info(" + tableName + ");";
		final IList<? super IList<? super IList>> result = selectDB(scope, conn, sqlStr);
		final IList<? extends IList<Object>> data = (IList<? extends IList<Object>>) result.get(2);

		try (final Statement st = conn.createStatement()) {
			// st.executeQuery(sqlStr);
			final int numRows = data.size();
			for (int i = 0; i < numberOfColumns; i++) {
				final String colName = ((String) columns.get(i)).trim();
				for (int j = 0; j < numRows; ++j) {
					final IList<Object> row = data.get(j);
					final String name = ((String) row.get(1)).trim();
					final String type = ((String) row.get(2)).trim();
					if (colName.equalsIgnoreCase(name)) {
						if (BLOB.equalsIgnoreCase(type) || "GEOMETRY".equalsIgnoreCase(type)
								|| "POINT".equalsIgnoreCase(type) || "LINESTRING".equalsIgnoreCase(type)
								|| "POLYGON".equalsIgnoreCase(type) || "MULTIPOINT".equalsIgnoreCase(type)
								|| "MULTILINESTRING".equalsIgnoreCase(type) || "MULTIPOLYGON".equalsIgnoreCase(type)
								|| "GEOMETRYCOLLECTION".equalsIgnoreCase(type)) {
							columnType.add(GEOMETRYTYPE);
						} else {
							columnType.add(type);
						}

					}
				}
			}
		}
		return columnType;
	}

	/**
	 * Load extension.
	 *
	 * @param conn
	 *            the conn
	 * @param extension
	 *            the extension
	 * @throws SQLException
	 *             the SQL exception
	 */
	// 23-July-2013
	private void load_extension(final Connection conn, final String extension) throws SQLException {
		// load Spatialite extension library
		try (final Statement stmt = conn.createStatement();) {
			stmt.setQueryTimeout(30); // set timeout to 30 sec.
			stmt.execute("SELECT load_extension('" + extension.replace('\\', '/') + "')");
		} catch (final SQLException e) {
			throw e;
		}
	}
	
	@Override
	public void close() throws Exception {
//		throw new NotImplementedException("Close not implemented");
	}
	
	

}
