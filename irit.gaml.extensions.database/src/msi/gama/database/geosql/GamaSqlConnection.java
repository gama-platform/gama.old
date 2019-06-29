package msi.gama.database.geosql;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.mysql.MySQLDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.shape.GamaGisGeometry;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.file.GamaGisFile;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

// DataStore.dispose(); //close connection;
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class GamaSqlConnection extends GamaGisFile {

	private static final String MYSQL = "mysql";
	private static final String POSTGRES = "postgres";
	private static final String POSTGIS = "postgis";
	private static final String MSSQL = "sqlserver";
	private static final String SQLITE = "spatialite";
	private String dbtype = "";
	private String host = "";
	private String port = "";
	private String database = "";
	private String user = "";
	private String passwd = "";

	// Database object/ Database connection
	private DataStore dataStore;

	public GamaSqlConnection(final IScope scope) {
		super(scope, FileUtils.constructAbsoluteFilePath(scope, "getfun.shp", false), 0);
		setParams(scope);
	}

	private GamaSqlConnection(final IScope scope, final Map<String, Object> params) {
		super(scope, FileUtils.constructAbsoluteFilePath(scope, "getfun.shp", false), 0);

		setParams(params);
	}

	private void setParams(final IScope scope) {
		setConnectionParameters(scope);
	}

	private void setParams(final Map<String, Object> params) {
		setConnectionParameters(params);
	}

	private void setConnectionParameters(final IScope scope) {
		final Map<String, Object> params = (Map<String, Object>) scope.getArg("params", IType.MAP);
		this.dbtype = (String) params.get("dbtype");
		this.host = (String) params.get("host");
		this.port = (String) params.get("port");
		this.database = (String) params.get("database");
		this.user = (String) params.get("user");
		this.passwd = (String) params.get("passwd");
	}

	private void setConnectionParameters(final Map<String, Object> params) {
		this.dbtype = (String) params.get("dbtype");
		this.host = (String) params.get("host");
		this.port = (String) params.get("port");
		this.database = (String) params.get("database");
		this.user = (String) params.get("user");
		this.passwd = (String) params.get("passwd");
	}

	// Connect connection parameters with connection attributes of the object
	private Map<String, Object> createConnectionParams(final IScope scope) {
		final Map<String, Object> connectionParameters = new HashMap<>();

		if (dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGRES) || dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGIS)) {
			connectionParameters.put("host", host);
			connectionParameters.put("dbtype", dbtype);
			connectionParameters.put("port", port);
			connectionParameters.put("database", database);
			connectionParameters.put("user", user);
			connectionParameters.put("passwd", passwd);
			// advanced

		} else if (dbtype.equalsIgnoreCase(GamaSqlConnection.MYSQL)) {
			connectionParameters.put(MySQLDataStoreFactory.DBTYPE.key, dbtype);
			connectionParameters.put(JDBCDataStoreFactory.HOST.key, host);
			connectionParameters.put(MySQLDataStoreFactory.PORT.key, Integer.valueOf(port));
			connectionParameters.put(JDBCDataStoreFactory.DATABASE.key, database);
			connectionParameters.put(JDBCDataStoreFactory.USER.key, user);
			connectionParameters.put(JDBCDataStoreFactory.PASSWD.key, passwd);
		} else if (dbtype.equalsIgnoreCase(GamaSqlConnection.MSSQL)) {
			connectionParameters.put("host", host);
			connectionParameters.put("dbtype", dbtype);
			connectionParameters.put("port", port);
			connectionParameters.put("database", database);
			connectionParameters.put("user", user);
			connectionParameters.put("passwd", passwd);

		} else if (dbtype.equalsIgnoreCase(GamaSqlConnection.SQLITE)) {
			final String DBRelativeLocation = FileUtils.constructAbsoluteFilePath(scope, database, true);
			// String EXTRelativeLocation =
			// GamaPreferences.LIB_SPATIALITE.value(scope).getPath();
			connectionParameters.put("dbtype", dbtype);
			connectionParameters.put("database", DBRelativeLocation);

		}
		return connectionParameters;
	}

	/*
	 * Create a connection to database with current connection parameter of the GamaSqlConnection object
	 */
	public DataStore Connect(final IScope scope) throws Exception {
		final Map<String, Object> connectionParameters = createConnectionParams(scope);
		DataStore dStore;
		dStore = DataStoreFinder.getDataStore(connectionParameters); // get
																		// connection
		// DEBUG.LOG("data store postgress:" + dStore);
		if (dStore == null) { throw new IOException("Can't connect to " + database); }
		return dStore;
	}

	/*
	 * Close the current connection of of the GamaSqlConnection object
	 */
	public void close(final IScope scope) throws GamaRuntimeException {
		if (dataStore != null) {
			dataStore.dispose();
		} else {
			throw GamaRuntimeException.error("The connection to " + this.database + " is not opened ", scope);
		}
	}

	public void setDataStore(final DataStore dataStore) {
		this.dataStore = dataStore;
	}

	private void readTable(final IScope scope) {
		final String tableName = (String) scope.getArg("table", IType.STRING);
		final String filterStr = (String) scope.getArg("filter", IType.STRING);
		readTable(scope, tableName, filterStr);
	}

	private void readTable(final IScope scope, final String tableName, final String filterStr) {

		final IList list = getBuffer();
		final QueryInfo queryInfo = new QueryInfo(scope, this.dataStore, tableName, filterStr);
		try (SimpleFeatureIterator reader = queryInfo.getRecordSet().features()) {

			final int size = queryInfo.getSize();
			final Envelope3D env = queryInfo.getEnvelope();
			int index = 0;
			computeProjection(scope, env); // ??
			// reader = store.getFeatureReader();
			// final int i = 0;
			while (reader.hasNext()) {
				scope.getGui().getStatus(scope).setSubStatusCompletion(index++ / (double) size);
				final Feature feature = reader.next();

				// DEBUG.LOG("Record " + i++ + ": " +
				// feature.getValue().toString());

				Geometry g = (Geometry) feature.getDefaultGeometryProperty().getValue();
				if (g != null && !g.isEmpty()) {
					g = gis.transform(g);
					list.add(new GamaGisGeometry(g, feature));
				} else {
					GAMA.reportError(scope, GamaRuntimeException.warning(
							"GamaSqlConnection.fillBuffer; geometry could not be added : " + feature.getIdentifier(),
							scope), false);
				}
			}
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			scope.getGui().getStatus(scope).endSubStatus("Reading table " + tableName);
		}
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		readTable(scope);
	}

	public void read(final IScope scope) {
		fillBuffer(scope);
	}

	@Override
	protected CoordinateReferenceSystem getOwnCRS(final IScope scope) {
		return null;
	}

	// _________________________________________________________________
	private static class QueryInfo {

		private final int itemNumber; // Number of records
		private final Envelope3D env;
		private final SimpleFeatureCollection features; // data/recordsets

		QueryInfo(final IScope scope, final DataStore dStore, final String tableName, final String filterStr) {

			SimpleFeatureSource source = null;
			SimpleFeatureCollection sfeatures = null; // Query data
			final Map<String, String> attributes = new LinkedHashMap(); // Meta
																		// data
			ReferencedEnvelope env = new ReferencedEnvelope();
			CoordinateReferenceSystem crs = null;
			int number = 0;
			try {

				source = dStore.getFeatureSource(tableName); // table
				final Filter filter = ECQL.toFilter(filterStr); // create filter
																// for read data
				sfeatures = source.getFeatures(filter); // read data
				try {
					crs = source.getInfo().getCRS();
				} catch (final Exception e) {
					DEBUG.ERR("Ignored exception in ShapeInfo getCRS:" + e.getMessage());
				}
				env = source.getBounds();
				if (crs != null) {
					try {
						env = env.transform(new ProjectionFactory().getTargetCRS(scope), true);
					} catch (final Exception e) {}
				}
				number = sfeatures.size(); // get number of records
				// get meta data
				final java.util.List<AttributeDescriptor> att_list = source.getSchema().getAttributeDescriptors();
				for (final AttributeDescriptor desc : att_list) {
					String type;
					if (desc.getType() instanceof GeometryType) {
						type = "geometry";
					} else {
						type = Types.get(desc.getType().getBinding()).toString();
					}
					attributes.put(desc.getName().getLocalPart(), type);
				}
			} catch (final Exception e) {
				DEBUG.ERR("Error in reading metadata of " + tableName);

			} finally {
				this.env = Envelope3D.of(env);
				this.itemNumber = number; // number of records
				this.features = sfeatures; // data
			}

		}

		public int getSize() {
			return itemNumber;
		}

		public Envelope3D getEnvelope() {
			return env;
		}

		public SimpleFeatureCollection getRecordSet() {
			return features;
		}
	}// end of class QueryInfo

}
