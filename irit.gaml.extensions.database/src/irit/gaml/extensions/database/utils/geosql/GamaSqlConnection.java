/*******************************************************************************************************
 *
 * GamaSqlConnection.java, in irit.gaml.extensions.database, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package irit.gaml.extensions.database.utils.geosql;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import irit.gaml.extensions.database.utils.sql.ISqlConnector;
import irit.gaml.extensions.database.utils.sql.SqlUtils;
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

/**
 * The Class GamaSqlConnection.
 */
// DataStore.dispose(); //close connection;
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class GamaSqlConnection extends GamaGisFile {

	/** The dbtype. */
	private String dbtype = "";

	/** The host. */
	private String host = "";

	/** The port. */
	private String port = "";

	/** The database. */
	private String database = "";

	/** The user. */
	private String user = "";

	/** The passwd. */
	private String passwd = "";

	/** The data store. */
	// Database object/ Database connection
	private DataStore dataStore;

	/**
	 * Instantiates a new gama sql connection.
	 *
	 * @param scope
	 *            the scope
	 */
	public GamaSqlConnection(final IScope scope) {
		super(scope, FileUtils.constructAbsoluteFilePath(scope, "getfun.shp", false), 0);
		setParams(scope);
	}

	/**
	 * Instantiates a new gama sql connection.
	 *
	 * @param scope
	 *            the scope
	 * @param params
	 *            the params
	 */
	private GamaSqlConnection(final IScope scope, final Map<String, Object> params) {
		super(scope, FileUtils.constructAbsoluteFilePath(scope, "getfun.shp", false), 0);

		setParams(params);
	}

	/**
	 * Sets the params.
	 *
	 * @param scope
	 *            the new params
	 */
	private void setParams(final IScope scope) {
		setConnectionParameters(scope);
	}

	/**
	 * Sets the params.
	 *
	 * @param params
	 *            the params
	 */
	private void setParams(final Map<String, Object> params) {
		setConnectionParameters(params);
	}

	/**
	 * Sets the connection parameters.
	 *
	 * @param scope
	 *            the new connection parameters
	 */
	private void setConnectionParameters(final IScope scope) {
		final Map<String, Object> params = (Map<String, Object>) scope.getArg("params", IType.MAP);
		this.dbtype = (String) params.get("dbtype");
		this.host = (String) params.get("host");
		this.port = (String) params.get("port");
		this.database = (String) params.get("database");
		this.user = (String) params.get("user");
		this.passwd = (String) params.get("passwd");
	}

	/**
	 * Sets the connection parameters.
	 *
	 * @param params
	 *            the params
	 */
	private void setConnectionParameters(final Map<String, Object> params) {
		this.dbtype = (String) params.get("dbtype");
		this.host = (String) params.get("host");
		this.port = (String) params.get("port");
		this.database = (String) params.get("database");
		this.user = (String) params.get("user");
		this.passwd = (String) params.get("passwd");
	}

	/**
	 * Creates the connection params.
	 *
	 * @param scope
	 *            the scope
	 * @return the map
	 */
	// Connect connection parameters with connection attributes of the object
	private Map<String, Object> createConnectionParams(final IScope scope) {
		ISqlConnector connector = SqlUtils.externalConnectors.get(dbtype);
		return connector.getConnectionParameters(scope, host, dbtype, port, database, user, passwd);
	}

	/**
	 * Connect.
	 *
	 * @param scope
	 *            the scope
	 * @return the data store
	 * @throws Exception
	 *             the exception
	 */
	/*
	 * Create a connection to database with current connection parameter of the GamaSqlConnection object
	 */
	public DataStore Connect(final IScope scope) throws Exception {
		final Map<String, Object> connectionParameters = createConnectionParams(scope);
		DataStore dStore;
		dStore = DataStoreFinder.getDataStore(connectionParameters); // get
																		// connection
		// DEBUG.LOG("data store postgress:" + dStore);
		if (dStore == null) throw new IOException("Can't connect to " + database);
		return dStore;
	}

	/**
	 * Close.
	 *
	 * @param scope
	 *            the scope
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	/*
	 * Close the current connection of of the GamaSqlConnection object
	 */
	public void close(final IScope scope) throws GamaRuntimeException {
		if (dataStore == null)
			throw GamaRuntimeException.error("The connection to " + this.database + " is not opened ", scope);
		dataStore.dispose();
	}

	/**
	 * Sets the data store.
	 *
	 * @param dataStore
	 *            the new data store
	 */
	public void setDataStore(final DataStore dataStore) { this.dataStore = dataStore; }

	/**
	 * Read table.
	 *
	 * @param scope
	 *            the scope
	 */
	private void readTable(final IScope scope) {
		final String tableName = (String) scope.getArg("table", IType.STRING);
		final String filterStr = (String) scope.getArg("filter", IType.STRING);
		readTable(scope, tableName, filterStr);
	}

	/**
	 * Read table.
	 *
	 * @param scope
	 *            the scope
	 * @param tableName
	 *            the table name
	 * @param filterStr
	 *            the filter str
	 */
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
				scope.getGui().getStatus().setSubStatusCompletion(index++ / (double) size, scope);
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
			scope.getGui().getStatus().endSubStatus("Reading table " + tableName, scope);
		}
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) return;
		setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
		readTable(scope);
	}

	/**
	 * Read.
	 *
	 * @param scope
	 *            the scope
	 */
	public void read(final IScope scope) {
		fillBuffer(scope);
	}

	@Override
	protected CoordinateReferenceSystem getOwnCRS(final IScope scope) {
		return null;
	}

	/**
	 * The Class QueryInfo.
	 */
	// _________________________________________________________________
	private static class QueryInfo {

		/** The item number. */
		private final int itemNumber; // Number of records

		/** The env. */
		private final Envelope3D env;

		/** The features. */
		private final SimpleFeatureCollection features; // data/recordsets

		/**
		 * Instantiates a new query info.
		 *
		 * @param scope
		 *            the scope
		 * @param dStore
		 *            the d store
		 * @param tableName
		 *            the table name
		 * @param filterStr
		 *            the filter str
		 */
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

		/**
		 * Gets the size.
		 *
		 * @return the size
		 */
		public int getSize() { return itemNumber; }

		/**
		 * Gets the envelope.
		 *
		 * @return the envelope
		 */
		public Envelope3D getEnvelope() { return env; }

		/**
		 * Gets the record set.
		 *
		 * @return the record set
		 */
		public SimpleFeatureCollection getRecordSet() { return features; }
	}// end of class QueryInfo

	@Override
	protected SimpleFeatureCollection getFeatureCollection(final IScope scope) {
		return null;
	}

}
