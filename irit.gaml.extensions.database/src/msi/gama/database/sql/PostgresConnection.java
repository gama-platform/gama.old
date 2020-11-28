/*********************************************************************************************
 *
 *
 * 'PostgresConnection.java', in plugin 'msi.gama.core', is part of the source code of the GAMA modeling and simulation
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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import ummisco.gama.dev.utils.DEBUG;

/*
 * @Author TRUONG Minh Thai Fredric AMBLARD Benoit GAUDOU Christophe Sibertin-BLANC Created date: 19-Apr-2013 Modified:
 * 15-Jan-2014 Fix null error of getInsertString method
 *
 *
 * Last Modified: 15-Jan-2014
 */
class PostgresConnection extends SqlConnection {

	private static final String WKT2GEO = "ST_GeomFromText";

	PostgresConnection(final String venderName, final String url, final String port, final String dbName,
			final String userName, final String password, final Boolean transformed) {
		super(venderName, url, port, dbName, userName, password, transformed);
	}

	@Override
	public Connection connectDB()
			throws ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException {
		Connection conn = null;
		try {
			if (vender.equalsIgnoreCase(POSTGRES) || vender.equalsIgnoreCase(POSTGIS)) {
				Class.forName(POSTGRESDriver).newInstance();
				conn = DriverManager.getConnection("jdbc:postgresql://" + url + ":" + port + "/" + dbName + "?user="
						+ userName + "&password=" + password);
			} else {
				throw new ClassNotFoundException("PostgresConnection.connectSQL: The " + vender + " is not supported!");
			}
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

		final IList<IList<Object>> repRequest = GamaListFactory.create(msi.gaml.types.Types.LIST);
		try {
			final List<Integer> geoColumn = getGeometryColumns(rsmd);
			final int nbCol = rsmd.getColumnCount();
			while (rs.next()) {
				final IList<Object> rowList = GamaListFactory.create();
				for (int j = 1; j <= nbCol; j++) {
					if (geoColumn.contains(j)) {
						rowList.add(SqlUtils.read(rs.getBytes(j)));
					} else {
						rowList.add(rs.getObject(j));
					}
				}
				repRequest.add(rowList);
			}
		} catch (final Exception e) {

		}
		return repRequest;

	}

	@Override
	protected List<Integer> getGeometryColumns(final ResultSetMetaData rsmd) throws SQLException {
		final int numberOfColumns = rsmd.getColumnCount();
		final List<Integer> geoColumn = new ArrayList<>();
		for (int i = 1; i <= numberOfColumns; i++) {
			// Search column with Geometry type
			if (vender.equalsIgnoreCase(POSTGRES) && rsmd.getColumnType(i) == 1111
					|| vender.equalsIgnoreCase(POSTGRES) && rsmd.getColumnType(i) == -2
					|| vender.equalsIgnoreCase(POSTGIS) && rsmd.getColumnType(i) == 1111
					|| vender.equalsIgnoreCase(POSTGIS) && rsmd.getColumnType(i) == -2) {
				geoColumn.add(i);
			}
		}
		return geoColumn;

	}

	@Override
	protected IList<Object> getColumnTypeName(final ResultSetMetaData rsmd) throws SQLException {
		final int numberOfColumns = rsmd.getColumnCount();
		final IList<Object> columnType = GamaListFactory.create();
		for (int i = 1; i <= numberOfColumns; i++) {
			// Search column with Geometry type
			if (vender.equalsIgnoreCase(POSTGRES) && rsmd.getColumnType(i) == 1111
					|| vender.equalsIgnoreCase(POSTGRES) && rsmd.getColumnType(i) == -2
					|| vender.equalsIgnoreCase(POSTGIS) && rsmd.getColumnType(i) == 1111
					|| vender.equalsIgnoreCase(POSTGIS) && rsmd.getColumnType(i) == -2) {
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
		String selectStr = "SELECT ";
		String colStr = "";
		String valueStr = "";
		// Check size of parameters
		if (values.size() != col_no) {
			throw new IndexOutOfBoundsException("Size of columns list and values list are not equal");
		}
		// Get column name
		for (int i = 0; i < col_no; i++) {
			if (i == col_no - 1) {
				colStr = colStr + (String) cols.get(i);
			} else {
				colStr = colStr + (String) cols.get(i) + ",";
			}
		}
		// create SELECT statement string
		selectStr = selectStr + colStr + " FROM " + table_name + " LIMIT 1 ;";

		if (DEBUG.IS_ON()) {
			DEBUG.OUT("PostgresConnection.getInsertString.select command:" + selectStr);
		}

		try {
			// get column type;
			final Statement st = conn.createStatement();
			final ResultSet rs = st.executeQuery(selectStr);
			final ResultSetMetaData rsmd = rs.getMetaData();
			final IList<Object> col_Names = getColumnName(rsmd);
			final IList<Object> col_Types = getColumnTypeName(rsmd);

			if (DEBUG.IS_ON()) {
				DEBUG.OUT("list of column Name:" + col_Names);
				DEBUG.OUT("list of column type:" + col_Types);
			}
			// Insert command
			// set parameter value
			valueStr = "";
			final IProjection saveProj = getSavingGisProjection(scope);
			for (int i = 0; i < col_no; i++) {
				// Value list begin-------------------------------------------
				if (values.get(i) == null) {
					valueStr = valueStr + NULLVALUE;
				} else if (((String) col_Types.get(i)).equalsIgnoreCase(GEOMETRYTYPE)) {

					// 23/Jul/2013 - Transform GAMA GIS TO NORMAL
					final WKTReader wkt = new WKTReader();
					Geometry geo = wkt.read(values.get(i).toString());
					// DEBUG.LOG(geo.toString());
					if (transformed) {
						geo = saveProj.inverseTransform(geo); // have problem
																// here
					}
					valueStr = valueStr + WKT2GEO + "('" + geo.toString() + "')";

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

			if (DEBUG.IS_ON()) {
				DEBUG.OUT("PostgresConection.getInsertString:" + insertStr);
			}

		} catch (final SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("PostgresConnection.getInsertString:" + e.toString(), scope);
		} catch (final ParseException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("PostgresConnection.getInsertString:" + e.toString(), scope);
		}

		return insertStr;
	}

	@Override
	protected String getInsertString(final IScope scope, final Connection conn, final String table_name,
			final IList<Object> values) throws GamaRuntimeException {
		String insertStr = "INSERT INTO ";
		String selectStr = "SELECT ";
		String colStr = "";
		String valueStr = "";

		// Get column name
		// create SELECT statement string
		selectStr = selectStr + " * " + " FROM " + table_name + " LIMIT 1 ;";

		if (DEBUG.IS_ON()) {
			DEBUG.OUT("PostgresConnection.getInsertString.select command:" + selectStr);
		}

		try {
			// get column type;
			final Statement st = conn.createStatement();
			final ResultSet rs = st.executeQuery(selectStr);
			final ResultSetMetaData rsmd = rs.getMetaData();
			final IList<Object> col_Names = getColumnName(rsmd);
			final IList<Object> col_Types = getColumnTypeName(rsmd);
			final int col_no = col_Names.size();
			// Check size of parameters
			if (values.size() != col_Names.size()) {
				throw new IndexOutOfBoundsException("Size of columns list and values list are not equal");
			}

			// Insert command
			// set parameter value
			colStr = "";
			valueStr = "";
			for (int i = 0; i < col_no; i++) {
				// Value list begin-------------------------------------------
				if (values.get(i) == null) {
					valueStr = valueStr + NULLVALUE;
				} else if (((String) col_Types.get(i)).equalsIgnoreCase(GEOMETRYTYPE)) {
					// 23/Jul/2013 - Transform GAMA GIS TO NORMAL
					final WKTReader wkt = new WKTReader();
					Geometry geo = wkt.read(values.get(i).toString());
					// DEBUG.LOG(geo.toString());
					if (transformed) {
						geo = getSavingGisProjection(scope).inverseTransform(geo);
					}
					// DEBUG.LOG(geo.toString());
					valueStr = valueStr + WKT2GEO + "('" + geo.toString() + "')";

				} else if (((String) col_Types.get(i)).equalsIgnoreCase(CHAR)
						|| ((String) col_Types.get(i)).equalsIgnoreCase(VARCHAR)
						|| ((String) col_Types.get(i)).equalsIgnoreCase(NVARCHAR)
						|| ((String) col_Types.get(i)).equalsIgnoreCase(TEXT)) {
					String temp = values.get(i).toString();
					temp = temp.replaceAll("'", "''");
					// Add to value:
					valueStr = valueStr + "'" + temp + "'";
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

		} catch (final SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("PostgresConnection.getInsertString:" + e.toString(), scope);
		} catch (final ParseException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("PostgresConnection.getInsertString:" + e.toString(), scope);
		}

		return insertStr;
	}
}
