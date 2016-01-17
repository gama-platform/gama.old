package msi.gama.database.geosql;

import java.io.IOException;
import java.util.*;
import javax.swing.JOptionPane;
import org.geotools.data.*;
import org.geotools.data.mysql.MySQLDataStoreFactory;
import org.geotools.data.simple.*;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.type.*;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import com.vividsolutions.jts.geom.*;
import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.GamaGisFile;
import msi.gaml.types.*;

// DataStore.dispose(); //close connection;
public class GamaSqlConnection extends GamaGisFile {

	protected static final boolean DEBUG = false; // Change DEBUG = false for release version
	public static final String MYSQL = "mysql";
	public static final String POSTGRES = "postgres";
	public static final String POSTGIS = "postgis";
	public static final String MSSQL = "sqlserver";
	public static final String SQLITE = "spatialite";
	public static final String GEOMETRYTYPE = "GEOMETRY";
	public static final String CHAR = "CHAR";
	public static final String VARCHAR = "VARCHAR";
	public static final String NVARCHAR = "NVARCHAR";
	public static final String TEXT = "TEXT";
	public static final String BLOB = "BLOB";
	public static final String TIMESTAMP = "TIMESTAMP"; // MSSQL (number);Postgres,MySQL ('YYYY-MM-DD HH:MM:SS')
	public static final String DATETIME = "DATETIME"; // MSSQL,Postgres, MySQL, SQLite ( "YYYY-MM-DD HH:MM:SS.SSS")
	public static final String SMALLDATETIME = "SMALLDATETIME"; // MSSQL
	public static final String DATE = "DATE"; // MSSQL,Postgres, MySQL, SQlite
	public static final String YEAR = "YEAR"; // Postgres, MySQL(yyyy)
	public static final String TIME = "TIME"; // MySQL ('00:00:00')
	public static final String NULLVALUE = "NULL";

	static final String MYSQLDriver = new String("com.mysql.jdbc.Driver");
	// static final String MSSQLDriver = new String("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	static final String MSSQLDriver = new String("net.sourceforge.jtds.jdbc.Driver");
	static final String SQLITEDriver = new String("org.sqlite.JDBC");
	static final String POSTGRESDriver = new String("org.postgresql.Driver");

	protected String vender = "";
	protected String dbtype = "";
	protected String host = "";
	protected String port = "";
	protected String database = "";
	protected String user = "";
	protected String passwd = "";
	protected Boolean transformed = false;
	protected String extension = null;
	protected boolean loadExt = false;

	// Database object/ Database connection
	protected DataStore dataStore;
	protected String table = null;
	protected String filter = null;

	// public GamaSqlConnection(IScope scope, String localhost, Integer code) {
	// super(scope, localhost, code);
	// // TODO Auto-generated constructor stub
	// }

	public GamaSqlConnection(final IScope scope) {
		super(scope, FileUtils.constructAbsoluteFilePath(scope, "getfun.shp", false), 0);
		setParams(scope);
	}

	public GamaSqlConnection(final IScope scope, final Map<String, Object> params) {
		// super(scope,"F://GAMA_MODELS//GEODB//includes//bounds.shp",0);
		super(scope, FileUtils.constructAbsoluteFilePath(scope, "getfun.shp", false), 0);

		setParams(params);
	}

	private void setParams(final IScope scope) {
		setConnectionParameters(scope);
		setTableName(scope);
		setFilter(scope);
	}

	// private void setParams(final Map<String, Object> params, final String table, final String filter) {
	// setConnectionParameters(params);
	// setTable(table);
	// setFilter(filter);
	// }

	private void setParams(final Map<String, Object> params) {
		setConnectionParameters(params);
	}

	private void setTable(final String table) {
		this.table = table;
	}
	//
	// private void setFilter(final String filter) {
	// this.filter = filter;
	// }

	private void setConnectionParameters(final IScope scope) {
		Map<String, Object> params = (Map<String, Object>) scope.getArg("params", IType.MAP);
		this.dbtype = (String) params.get("dbtype");
		this.host = (String) params.get("host");
		this.port = (String) params.get("port");
		this.database = (String) params.get("database");
		this.user = (String) params.get("user");
		this.passwd = (String) params.get("passwd");
	}

	private void setTableName(final IScope scope) {
		this.table = (String) scope.getArg("table", IType.STRING);
	}

	private void setFilter(final IScope scope) {
		this.filter = (String) scope.getArg("filter", IType.STRING);
	}

	private void setConnectionParameters(final Map<String, Object> params) {
		this.dbtype = (String) params.get("dbtype");
		this.host = (String) params.get("host");
		this.port = (String) params.get("port");
		this.database = (String) params.get("database");
		this.user = (String) params.get("user");
		this.passwd = (String) params.get("passwd");
		// this.table = (String) params.get("table");
		// this.filter = (String) params.get("filter");
	}

	public static GamaSqlConnection createConnectionObject(final IScope scope) throws GamaRuntimeException, Exception {
		Map<String, Object> params = (Map<String, Object>) scope.getArg("params", IType.MAP);
		return createConnectionObject(scope, params);

	}

	// Connect connection parameters with the new parameter
	public Map<String, Object> createConnectionParams(final IScope scope, final Map<String, Object> params) {
		Map<String, Object> connectionParameters = new HashMap<String, Object>();

		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		// String table = (String) params.get("table");
		// String filter = (String) params.get("filter");

		if ( dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGRES) ||
			dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGIS) ) {
			connectionParameters.put("host", host);
			connectionParameters.put("dbtype", dbtype);
			connectionParameters.put("port", port);
			connectionParameters.put("database", database);
			connectionParameters.put("user", user);
			connectionParameters.put("passwd", passwd);
			// advanced
			// params.put(PostgisDataStoreFactory.LOOSEBBOX, true );
			// params.put(PostgisDataStoreFactory.PREPARED_STATEMENTS, true );

		} else if ( dbtype.equalsIgnoreCase(GamaSqlConnection.MYSQL) ) {
			// java.util.Map params = new java.util.HashMap();
			connectionParameters.put(MySQLDataStoreFactory.DBTYPE.key, dbtype);
			connectionParameters.put(JDBCDataStoreFactory.HOST.key, host);
			connectionParameters.put(MySQLDataStoreFactory.PORT.key, new Integer(port));
			connectionParameters.put(JDBCDataStoreFactory.DATABASE.key, database);
			connectionParameters.put(JDBCDataStoreFactory.USER.key, user);
			connectionParameters.put(JDBCDataStoreFactory.PASSWD.key, passwd);
		} else if ( dbtype.equalsIgnoreCase(GamaSqlConnection.MSSQL) ) {
			connectionParameters.put("host", host);
			connectionParameters.put("dbtype", dbtype);
			connectionParameters.put("port", port);
			connectionParameters.put("database", database);
			connectionParameters.put("user", user);
			connectionParameters.put("passwd", passwd);

		} else if ( dbtype.equalsIgnoreCase(GamaSqlConnection.SQLITE) ) {
			String DBRelativeLocation = FileUtils.constructAbsoluteFilePath(scope, database, true);
			// String EXTRelativeLocation = GamaPreferences.LIB_SPATIALITE.value(scope).getPath();
			connectionParameters.put("dbtype", dbtype);
			connectionParameters.put("database", DBRelativeLocation);

		}
		return connectionParameters;
	}

	// Connect connection parameters with connection attributes of the object
	public Map<String, Object> createConnectionParams(final IScope scope) {
		Map<String, Object> connectionParameters = new HashMap<String, Object>();

		if ( dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGRES) ||
			dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGIS) ) {
			connectionParameters.put("host", host);
			connectionParameters.put("dbtype", dbtype);
			connectionParameters.put("port", port);
			connectionParameters.put("database", database);
			connectionParameters.put("user", user);
			connectionParameters.put("passwd", passwd);
			// advanced
			// params.put(PostgisDataStoreFactory.LOOSEBBOX, true );
			// params.put(PostgisDataStoreFactory.PREPARED_STATEMENTS, true );

		} else if ( dbtype.equalsIgnoreCase(GamaSqlConnection.MYSQL) ) {
			// java.util.Map params = new java.util.HashMap();
			connectionParameters.put(MySQLDataStoreFactory.DBTYPE.key, dbtype);
			connectionParameters.put(JDBCDataStoreFactory.HOST.key, host);
			connectionParameters.put(MySQLDataStoreFactory.PORT.key, new Integer(port));
			connectionParameters.put(JDBCDataStoreFactory.DATABASE.key, database);
			connectionParameters.put(JDBCDataStoreFactory.USER.key, user);
			connectionParameters.put(JDBCDataStoreFactory.PASSWD.key, passwd);
		} else if ( dbtype.equalsIgnoreCase(GamaSqlConnection.MSSQL) ) {
			connectionParameters.put("host", host);
			connectionParameters.put("dbtype", dbtype);
			connectionParameters.put("port", port);
			connectionParameters.put("database", database);
			connectionParameters.put("user", user);
			connectionParameters.put("passwd", passwd);

		} else if ( dbtype.equalsIgnoreCase(GamaSqlConnection.SQLITE) ) {
			String DBRelativeLocation = FileUtils.constructAbsoluteFilePath(scope, database, true);
			// String EXTRelativeLocation = GamaPreferences.LIB_SPATIALITE.value(scope).getPath();
			connectionParameters.put("dbtype", dbtype);
			connectionParameters.put("database", DBRelativeLocation);

		}
		return connectionParameters;
	}

	/*
	 * Create a connection to database with current connection parameter of the GamaSqlConnection object
	 */
	public DataStore Connect(final IScope scope) throws Exception {
		Map<String, Object> connectionParameters = new HashMap<String, Object>();
		connectionParameters = createConnectionParams(scope);
		DataStore dStore;
		dStore = DataStoreFinder.getDataStore(connectionParameters); // get connection
		System.out.println("data store postgress:" + dStore);
		if ( dStore == null ) { throw new IOException("Can't connect to " + database); }
		return dStore;
	}

	/*
	 * Create a connection to database with the connection parameter params
	 */
	public DataStore Connect(final IScope scope, final Map<String, Object> params) throws IOException {
		Map<String, Object> connectionParameters = new HashMap<String, Object>();
		connectionParameters = createConnectionParams(scope, params);

		DataStore dStore;
		dStore = DataStoreFinder.getDataStore(connectionParameters); // get connection
		if ( dStore == null ) { throw new IOException("Can't connect to " + database); }
		return dStore;
	}

	/*
	 * Close the current connection of of the GamaSqlConnection object
	 */
	public void close(final IScope scope) throws GamaRuntimeException {
		if ( dataStore != null ) {
			dataStore.dispose();
		} else {
			throw GamaRuntimeException.error("The connection to " + this.database + " is not opened ", scope);
		}
	}

	public static GamaSqlConnection createConnectionObject(final IScope scope, final Map<String, Object> params)
		throws GamaRuntimeException, Exception {

		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");
		String table = (String) params.get("table");
		String filter = (String) params.get("filter");

		Map<String, Object> connectionParameters = new HashMap<String, Object>();
		connectionParameters.put("host", host);
		connectionParameters.put("dbtype", dbtype);
		connectionParameters.put("port", port);
		connectionParameters.put("database", database);
		connectionParameters.put("user", user);
		connectionParameters.put("passwd", passwd);

		if ( dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGRES) ||
			dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGIS) ) {
			DataStore dStore;
			dStore = DataStoreFinder.getDataStore(connectionParameters); // get connection
			if ( dStore == null ) {
				JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
			}
			GamaSqlConnection sqlConn;
			sqlConn = new GamaSqlConnection(scope, connectionParameters);
			sqlConn.setTable(table);
			sqlConn.setFilter(scope);
			sqlConn.setDataStore(dStore);// Create connection an get data store (database) for query

			return sqlConn;

		} else {
			throw GamaRuntimeException.error("GAMA does not support databases of type: " + dbtype, scope);
		}

	}

	public void setDataStore(final DataStore dataStore) {
		this.dataStore = dataStore;
	}

	public DataStore getDataStore(final DataStore dstore) {
		return this.dataStore;
	}

	protected void readTable(final IScope scope) {
		String tableName = (String) scope.getArg("table", IType.STRING);
		String filterStr = (String) scope.getArg("filter", IType.STRING);
		readTable(scope, tableName, filterStr);
	}

	protected void readTable(final IScope scope, final String tableName, final String filterStr) {

		SimpleFeatureIterator reader = null;
		IList list = getBuffer();
		try {
			QueryInfo queryInfo = new QueryInfo(this.dataStore, tableName, filterStr);
			int size = queryInfo.getSize();
			Envelope env = queryInfo.getEnvelope();
			int index = 0;
			computeProjection(scope, env); // ??
			// reader = store.getFeatureReader();
			reader = queryInfo.getRecordSet().features();
			int i = 0;
			while (reader.hasNext()) {
				scope.getGui().setSubStatusCompletion(index++ / size);
				Feature feature = reader.next();

				System.out.println("Record " + i++ + ": " + feature.getValue().toString());

				Geometry g = (Geometry) feature.getDefaultGeometryProperty().getValue();
				if ( g != null && !g.isEmpty() ) {
					g = gis.transform(g);
					list.add(new GamaGisGeometry(g, feature));
				} else {
					GAMA.reportError(scope,
						GamaRuntimeException.warning(
							"GamaSqlConnection.fillBuffer; geometry could not be added : " + feature.getIdentifier(),
							scope),
						false);
				}
			}
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			if ( reader != null ) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			scope.getGui().endSubStatus("Reading table " + tableName);
		}
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( getBuffer() != null ) { return; }
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		readTable(scope);
	}

	public void read(final IScope scope) {
		fillBuffer(scope);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {

	}

	@Override
	protected CoordinateReferenceSystem getOwnCRS() {
		return null;
	}

	// _________________________________________________________________
	public static class QueryInfo {

		final int itemNumber; // Number of records
		final CoordinateReferenceSystem crs;
		final double width;
		final double height;
		final Envelope env;
		final Map<String, String> attributes; // meta data of query
		final SimpleFeatureCollection features; // data/recordsets

		public QueryInfo(final DataStore dStore, final String tableName, final String filterStr) {

			SimpleFeatureSource source = null;
			SimpleFeatureCollection sfeatures = null; // Query data
			Map<String, String> attributes = new LinkedHashMap(); // Meta data
			ReferencedEnvelope env = new ReferencedEnvelope();
			CoordinateReferenceSystem crs = null;
			int number = 0;
			try {

				source = dStore.getFeatureSource(tableName); // table
				Filter filter = ECQL.toFilter(filterStr); // create filter for read data
				sfeatures = source.getFeatures(filter); // read data
				try {
					crs = source.getInfo().getCRS();
				} catch (Exception e) {
					System.out.println("Ignored exception in ShapeInfo getCRS:" + e.getMessage());
				}
				env = source.getBounds();
				if ( crs != null ) {
					try {
						env = env.transform(new ProjectionFactory().getTargetCRS(), true);
					} catch (Exception e) {}
				}
				number = sfeatures.size(); // get number of records
				// get meta data
				java.util.List<AttributeDescriptor> att_list = source.getSchema().getAttributeDescriptors();
				for ( AttributeDescriptor desc : att_list ) {
					String type;
					if ( desc.getType() instanceof GeometryType ) {
						type = "geometry";
					} else {
						type = Types.get(desc.getType().getBinding()).toString();
					}
					attributes.put(desc.getName().getLocalPart(), type);
				}
			} catch (Exception e) {
				System.out.println("Error in reading metadata of " + tableName);

			} finally {
				this.width = env.getWidth();
				this.height = env.getHeight();
				this.env = env;
				this.itemNumber = number; // number of records
				this.crs = crs;
				this.features = sfeatures; // data
				this.attributes = attributes; // attributes
			}

		}

		public CoordinateReferenceSystem getCRS() {
			return crs;
		}

		public int getSize() {
			return itemNumber;
		}

		public Envelope getEnvelope() {
			return env;
		}

		public double getwidth() {
			return width;
		}

		public double getheight() {
			return height;
		}

		public Map<String, String> getAttributes() {
			return attributes;
		}

		public SimpleFeatureCollection getRecordSet() {
			return features;
		}
	}// end of class QueryInfo

}
