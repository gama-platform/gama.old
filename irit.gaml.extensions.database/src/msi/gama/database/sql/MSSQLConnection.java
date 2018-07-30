/*********************************************************************************************
 *
 *
 * 'MSSQLConnection.java', in plugin 'msi.gama.core', is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.database.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;

/*
 * @Author TRUONG Minh Thai Fredric AMBLARD Benoit GAUDOU Christophe Sibertin-BLANC Created date: 19-Apr-2013 Modified:
 * * 26-Apr-2013: Remove driver msi.gama.ext/sqljdbc4.jar add driver msi.gama.ext/jtds-1.2.6.jar Change driver name for
 * MSSQL from com.microsoft.sqlserver.jdbc.SQLServerDriver to net.sourceforge.jtds.jdbc.Driver Edit ConnectDB for new
 * driver Add new condition for geometry type 2004 (it look like postgres) 15-Jan-2014 Fix null error of getInsertString
 * methods Fix date/time error of getInsertString methods
 *
 * Last Modified: 15-Jan-2014
 */
public class MSSQLConnection extends SqlConnection {

	private static final boolean DEBUG = false; // Change DEBUG = false for
												// release version
	private static final String WKT2GEO = "geometry::STGeomFromText";
	private static final String SRID = "0"; // must solve later
	private static final String PREFIX_TIMESTAMP = "cast('";
	private static final String MID_TIMESTAMP = "' as ";
	private static final String SUPFIX_TIMESTAMP = ")";

	public MSSQLConnection(final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password) {
		super(venderName, url, port, dbName, userName, password);
	}

	public MSSQLConnection(final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password, final Boolean transformed) {
		super(venderName, url, port, dbName, userName, password, transformed);
	}

	@Override
	public Connection connectDB()
			throws ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			if (vender.equalsIgnoreCase(MSSQL)) {
				// Class.forName(MSSQLDriver).newInstance();
				// conn =
				// DriverManager.getConnection("jdbc:sqlserver://" + url + ":" +
				// port + ";databaseName=" + dbName +
				// ";user=" + userName + ";password=" + password + ";");
				Class.forName(MSSQLDriver).newInstance();
				conn = DriverManager.getConnection("jdbc:jtds:sqlserver://" + url + ":" + port + "/" + dbName, userName,
						password);
			} else {
				throw new ClassNotFoundException("MSSQLConnection.connectDB: The " + vender + " is not supported!");
			}
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			throw new ClassNotFoundException(e.toString());
		} catch (final InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new InstantiationException(e.toString());
		} catch (final IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalAccessException(e.toString());
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SQLException(e.toString());
		}
		return conn;

	}

	@Override
	protected IList<IList<Object>> resultSet2GamaList(final ResultSetMetaData rsmd, final ResultSet rs) {
		// TODO Auto-generated method stub
		// convert Geometry in SQL to Geometry type in GeoTool
		final IList<IList<Object>> repRequest = GamaListFactory.create(msi.gaml.types.Types.LIST);
		try {
			final List<Integer> geoColumn = getGeometryColumns(rsmd);
			final int nbCol = rsmd.getColumnCount();
			// int i = 1;
			// if ( DEBUG ) {
			// scope.getGui().debug("Number of col:" + nbCol);
			// }
			// if ( DEBUG ) {
			// scope.getGui().debug("Number of row:" + rs.getFetchSize());
			// }
			while (rs.next()) {
				// InputStream inputStream = rs.getBinaryStream(i);
				// if ( DEBUG ) {
				// scope.getGui().debug("processing at row:" + i);
				// }

				final IList<Object> rowList = GamaListFactory.create();
				for (int j = 1; j <= nbCol; j++) {
					// check column is geometry column?
					// if ( DEBUG ) {
					// scope.getGui().debug("col " + j + ": " +
					// rs.getObject(j));
					// }
					if (geoColumn.contains(j)) {
						// if ( DEBUG ) {
						// scope.getGui().debug("convert at [" + i + "," + j +
						// "]: ");
						// }
						rowList.add(SqlUtils.read(rs.getBytes(j)));
					} else {
						rowList.add(rs.getObject(j));
					}
				}
				repRequest.add(rowList);
				// i++;
			}
			// if ( DEBUG ) {
			// scope.getGui().debug("Number of row:" + i);
			// }
		} catch (final Exception e) {

		}
		return repRequest;

	}

	@Override
	protected List<Integer> getGeometryColumns(final ResultSetMetaData rsmd) throws SQLException {
		// TODO Auto-generated method stub
		final int numberOfColumns = rsmd.getColumnCount();
		final List<Integer> geoColumn = new ArrayList<>();
		for (int i = 1; i <= numberOfColumns; i++) {

			// if ( DEBUG ) {
			// scope.getGui().debug("col " + i + ": " + rsmd.getColumnName(i));
			// scope.getGui().debug(" - Type: " + rsmd.getColumnType(i));
			// scope.getGui().debug(" - TypeName: " +
			// rsmd.getColumnTypeName(i));
			// scope.getGui().debug(" - size: " + rsmd.getColumnDisplaySize(i));
			//
			// }

			/*
			 * for Geometry - in MySQL Type: -2/-4 - TypeName: UNKNOWN - size: 2147483647 - In MSSQL with sqljdbc4
			 * driver Type: -3/ with jdts driver type=2004 - TypeName: geometry - size: 2147483647 - In SQLITE Type:
			 * 2004 - TypeName: BLOB - size: 2147483647 - In PostGIS/PostGresSQL Type: 1111 - TypeName: geometry - size:
			 * 2147483647 st_asbinary(geom): - Type: -2 - TypeName: bytea - size: 2147483647
			 */
			// Search column with Geometry type
			// if ( vender.equalsIgnoreCase(MSSQL) & rsmd.getColumnType(i) == -3
			// ) {
			if (vender.equalsIgnoreCase(MSSQL) && rsmd.getColumnType(i) == 2004) {
				geoColumn.add(i);
			}
		}
		return geoColumn;

	}

	@Override
	protected IList<Object> getColumnTypeName(final ResultSetMetaData rsmd) throws SQLException {
		// TODO Auto-generated method stub
		final int numberOfColumns = rsmd.getColumnCount();
		final IList<Object> columnType = GamaListFactory.create();
		for (int i = 1; i <= numberOfColumns; i++) {
			/*
			 * for Geometry - in MySQL Type: -2/-4 - TypeName: UNKNOWN - size: 2147483647 - In MSSQL with sqljdbc4
			 * driver Type: -3/ with jdts driver type=2004 - TypeName: geometry - size: 2147483647 - In SQLITE Type:
			 * 2004 - TypeName: BLOB - size: 2147483647 - In PostGIS/PostGresSQL Type: 1111 - TypeName: geometry - size:
			 * 2147483647
			 */
			// Search column with Geometry type
			// if ( vender.equalsIgnoreCase(MSSQL) & rsmd.getColumnType(i) == -3
			// ) {
			if (vender.equalsIgnoreCase(MSSQL) && rsmd.getColumnType(i) == 2004) {
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
		// TODO Auto-generated method stub
		final int col_no = cols.size();
		String insertStr = "INSERT INTO ";
		String selectStr = "SELECT ";
		String colStr = "";
		String valueStr = "";
		// Check size of parameters
		if (values.size() != col_no) { throw new IndexOutOfBoundsException(
				"Size of columns list and values list are not equal"); }
		// Get column name
		for (int i = 0; i < col_no; i++) {
			if (i == col_no - 1) {
				colStr = colStr + (String) cols.get(i);
			} else {
				colStr = colStr + (String) cols.get(i) + ",";
			}
		}
		// create SELECT statement string
		selectStr = selectStr + " TOP 1 " + colStr + " FROM " + table_name + " ;";

		if (DEBUG) {
			scope.getGui().debug("MSSQLConnection.getInsertString.select command:" + selectStr);
		}

		try {
			// get column type;
			final Statement st = conn.createStatement();
			final ResultSet rs = st.executeQuery(selectStr);
			final ResultSetMetaData rsmd = rs.getMetaData();
			final IList<Object> col_Names = getColumnName(rsmd);
			final IList<Object> col_Types = getColumnTypeName(rsmd);

			if (DEBUG) {
				scope.getGui().debug("list of column Name:" + col_Names);
				scope.getGui().debug("list of column type:" + col_Types);
			}
			// Insert command
			// set parameter value
			valueStr = "";
			final IProjection saveGis = getSavingGisProjection(scope);
			for (int i = 0; i < col_no; i++) {
				// Value list begin-------------------------------------------
				if (values.get(i) == null) {
					valueStr = valueStr + NULLVALUE;
				} else if (((String) col_Types.get(i)).equalsIgnoreCase(GEOMETRYTYPE)) { // for
																							// GEOMETRY
																							// type
					// // Transform GAMA GIS TO NORMAL
					// if ( transformed ) {
					// WKTReader wkt = new WKTReader();
					// Geometry geo2 =
					// scope.getTopology().getGisUtils()
					// .inverseTransform(wkt.read(values.get(i).toString()));
					// valueStr = valueStr + WKT2GEO + "('" + geo2.toString() +
					// "')";
					// } else {
					// valueStr = valueStr + WKT2GEO + "('" +
					// values.get(i).toString() + "')";
					// }

					// 23/Jul/2013 - Transform GAMA GIS TO NORMAL
					final WKTReader wkt = new WKTReader();
					Geometry geo = wkt.read(values.get(i).toString());
					// System.out.println(geo.toString());
					if (transformed) {
						geo = saveGis.inverseTransform(geo);
					}
					// System.out.println(geo.toString());
					valueStr = valueStr + WKT2GEO + "('" + geo.toString() + "', " + SRID + ")";

				} else if (((String) col_Types.get(i)).equalsIgnoreCase(CHAR)
						|| ((String) col_Types.get(i)).equalsIgnoreCase(VARCHAR)
						|| ((String) col_Types.get(i)).equalsIgnoreCase(NVARCHAR)
						|| ((String) col_Types.get(i)).equalsIgnoreCase(TEXT)) { // for
																					// String
																					// type
					// Correct error string
					String temp = values.get(i).toString();
					temp = temp.replaceAll("'", "''");
					// Add to value:
					valueStr = valueStr + "'" + temp + "'";
				} else if (((String) col_Types.get(i)).equalsIgnoreCase(TIMESTAMP)) { // For
																						// timestamp
					valueStr = valueStr + PREFIX_TIMESTAMP + values.get(i).toString() + MID_TIMESTAMP + TIMESTAMP
							+ SUPFIX_TIMESTAMP;
				} else if (((String) col_Types.get(i)).equalsIgnoreCase(DATETIME)) { // For
																						// datetime
					valueStr = valueStr + PREFIX_TIMESTAMP + values.get(i).toString() + MID_TIMESTAMP + DATETIME
							+ SUPFIX_TIMESTAMP;
				} else if (((String) col_Types.get(i)).equalsIgnoreCase(DATE)) { // For
																					// datetime
					valueStr = valueStr + PREFIX_TIMESTAMP + values.get(i).toString() + MID_TIMESTAMP + DATE
							+ SUPFIX_TIMESTAMP;
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

			if (DEBUG) {
				scope.getGui().debug("MSSQLConnection.getInsertString:" + insertStr);
			}

		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("MSSQLConnection.getInsertString " + e.toString(), scope);
		} catch (final ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("MSSQLConnection.getInsertString " + e.toString(), scope);
		}

		return insertStr;
	}

	@Override
	protected String getInsertString(final IScope scope, final Connection conn, final String table_name,
			final IList<Object> values) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		String insertStr = "INSERT INTO ";
		String selectStr = "SELECT ";
		String colStr = "";
		String valueStr = "";

		// Get column name
		// create SELECT statement string
		selectStr = selectStr + " TOP 1 * " + " FROM " + table_name + " ;";
		try {
			// get column type;
			final Statement st = conn.createStatement();
			final ResultSet rs = st.executeQuery(selectStr);
			final ResultSetMetaData rsmd = rs.getMetaData();
			final IList<Object> col_Names = getColumnName(rsmd);
			final IList<Object> col_Types = getColumnTypeName(rsmd);
			final int col_no = col_Names.size();
			// Check size of parameters
			if (values.size() != col_Names.size()) { throw new IndexOutOfBoundsException(
					"Size of columns list and values list are not equal"); }

			// Insert command
			// set parameter value
			colStr = "";
			valueStr = "";
			for (int i = 0; i < col_no; i++) {
				// Value list begin-------------------------------------------
				if (values.get(i) == null) {
					valueStr = valueStr + NULLVALUE;
				} else if (((String) col_Types.get(i)).equalsIgnoreCase(GEOMETRYTYPE)) { // for
																							// GEOMETRY
																							// type
					// // Transform GAMA GIS TO NORMAL
					// if ( transformed ) {
					// WKTReader wkt = new WKTReader();
					// Geometry geo2 =
					// scope.getTopology().getGisUtils()
					// .inverseTransform(wkt.read(values.get(i).toString()));
					// valueStr = valueStr + WKT2GEO + "('" + geo2.toString() +
					// "')";
					// } else {
					// valueStr = valueStr + WKT2GEO + "('" +
					// values.get(i).toString() + "')";
					// }

					// 23/Jul/2013 - Transform GAMA GIS TO NORMAL
					final WKTReader wkt = new WKTReader();
					Geometry geo = wkt.read(values.get(i).toString());
					// System.out.println(geo.toString());

					if (transformed) {
						geo = getSavingGisProjection(scope).inverseTransform(geo);
					}
					// System.out.println(geo.toString());
					valueStr = valueStr + WKT2GEO + "('" + geo.toString() + "', " + SRID + ")";

				} else if (((String) col_Types.get(i)).equalsIgnoreCase(CHAR)
						|| ((String) col_Types.get(i)).equalsIgnoreCase(VARCHAR)
						|| ((String) col_Types.get(i)).equalsIgnoreCase(NVARCHAR)
						|| ((String) col_Types.get(i)).equalsIgnoreCase(TEXT)) { // for
																					// String
																					// type
																					// Correct
																					// error
																					// string
					String temp = values.get(i).toString();
					temp = temp.replaceAll("'", "''");
					// Add to value:
					valueStr = valueStr + "'" + temp + "'";
				} else if (((String) col_Types.get(i)).equalsIgnoreCase(TIMESTAMP)) { // For
																						// timestamp
					valueStr = valueStr + PREFIX_TIMESTAMP + values.get(i).toString() + MID_TIMESTAMP + TIMESTAMP
							+ SUPFIX_TIMESTAMP;
				} else if (((String) col_Types.get(i)).equalsIgnoreCase(DATETIME)) { // For
																						// datetime
					valueStr = valueStr + PREFIX_TIMESTAMP + values.get(i).toString() + MID_TIMESTAMP + DATETIME
							+ SUPFIX_TIMESTAMP;
				} else if (((String) col_Types.get(i)).equalsIgnoreCase(DATE)) { // For
																					// datetime
					valueStr = valueStr + PREFIX_TIMESTAMP + values.get(i).toString() + MID_TIMESTAMP + DATE
							+ SUPFIX_TIMESTAMP;
				} else { // For other type
					valueStr = valueStr + values.get(i).toString();
				}
				// Value list
				// end--------------------------------------------------------
				// column list
				colStr = colStr + col_Names.get(i).toString();

				if (i != col_no - 1) { // Add delimiter of each value
					colStr = colStr + ",";
					valueStr = valueStr + ",";
				}
			}

			insertStr = insertStr + table_name + "(" + colStr + ") " + "VALUES(" + valueStr + ")";

			if (DEBUG) {
				scope.getGui().debug("SqlConection.getInsertString:" + insertStr);
			}

		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("MSSQLConnection.insertBD " + e.toString(), scope);
		} catch (final ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error("MSSQLConnection.insertBD " + e.toString(), scope);
		}

		return insertStr;
	}
}
