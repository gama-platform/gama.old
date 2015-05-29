package msi.gama.database.geosql;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.mysql.MySQLDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.GamaPreferences;
import msi.gama.common.util.FileUtils;
import msi.gama.common.util.GuiUtils;
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

//DataStore.dispose(); //close connection;
public  class GamaSqlConnection extends GamaGisFile {
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

	public GamaSqlConnection(IScope scope, String localhost, Integer code) {
		super(scope, localhost, code);
		// TODO Auto-generated constructor stub
	}
	
	public GamaSqlConnection(IScope scope){
		super(scope,
			  FileUtils.constructAbsoluteFilePath(scope, "getfun.shp", false),0);
		setParams(scope);		
	}
	
	public GamaSqlConnection(IScope scope,  Map<String, Object> params) {
//		super(scope,"F://GAMA_MODELS//GEODB//includes//bounds.shp",0);
		super(scope,
				  FileUtils.constructAbsoluteFilePath(scope, "getfun.shp", false),0);

		setParams(params);
	}
	
	private void setParams(IScope scope){
		Map<String, Object> params = (Map<String, Object>) scope.getArg("params", IType.MAP);
		 this.dbtype = (String) params.get("dbtype");
		 this.host = (String) params.get("host");
		 this.port = (String) params.get("port");
		 this.database = (String) params.get("database");
		 this.user = (String) params.get("user");
		 this.passwd = (String) params.get("passwd");
//		 extension = (String) params.get("extension");
//		 transformed = params.containsKey("transform") ? (Boolean) params.get("transform") : true;

	}
	private void setParams(Map<String, Object> params){
		 this.dbtype = (String) params.get("dbtype");
		 this.host = (String) params.get("host");
		 this.port = (String) params.get("port");
		 this.database = (String) params.get("database");
		 this.user = (String) params.get("user");
		 this.passwd = (String) params.get("passwd");
//		 extension = (String) params.get("extension");
//		 transformed = params.containsKey("transform") ? (Boolean) params.get("transform") : true;

	}

	public static GamaSqlConnection createConnectionObject( IScope scope)
			throws GamaRuntimeException, Exception {
		Map<String, Object> params = (Map<String, Object>) scope.getArg("params", IType.MAP);
		return createConnectionObject(scope, params);
	
	}

	//Connect connection parameters with the new parameter
	public Map<String, Object>  createConnectionParams(IScope scope, Map<String, Object> params){
		Map<String, Object> connectionParameters = new HashMap<String,Object>();

		String dbtype = (String) params.get("dbtype");
		String host = (String) params.get("host");
		String port = (String) params.get("port");
		String database = (String) params.get("database");
		String user = (String) params.get("user");
		String passwd = (String) params.get("passwd");

		if (dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGRES) || dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGIS))
		{
		      connectionParameters.put("host",host);
		      connectionParameters.put("dbtype",dbtype);
		      connectionParameters.put("port", port);
		      connectionParameters.put("database",database);
		      connectionParameters.put("user",user);
		      connectionParameters.put("passwd",passwd);
		      // advanced
//		      params.put(PostgisDataStoreFactory.LOOSEBBOX, true );
//		      params.put(PostgisDataStoreFactory.PREPARED_STATEMENTS, true );
			
		}else if (dbtype.equalsIgnoreCase(GamaSqlConnection.MYSQL))
		{
//			java.util.Map params = new java.util.HashMap();
			connectionParameters.put(MySQLDataStoreFactory.DBTYPE.key, dbtype);
			connectionParameters.put(MySQLDataStoreFactory.HOST.key, host);
			connectionParameters.put(MySQLDataStoreFactory.PORT.key, new Integer(port));
			connectionParameters.put(MySQLDataStoreFactory.DATABASE.key, database);
			connectionParameters.put(MySQLDataStoreFactory.USER.key, user);
			connectionParameters.put(MySQLDataStoreFactory.PASSWD.key, passwd);
		}else if (dbtype.equalsIgnoreCase(GamaSqlConnection.MSSQL))
		{
		      connectionParameters.put("host",host);
		      connectionParameters.put("dbtype",dbtype);
		      connectionParameters.put("port", port);
		      connectionParameters.put("database",database);
		      connectionParameters.put("user",user);
		      connectionParameters.put("passwd",passwd);
				
		}else if (dbtype.equalsIgnoreCase(GamaSqlConnection.SQLITE))
		{
			String DBRelativeLocation = FileUtils.constructAbsoluteFilePath(scope, database, true);
			//String EXTRelativeLocation = GamaPreferences.LIB_SPATIALITE.value(scope).getPath();
			connectionParameters.put("dbtype", dbtype);
			connectionParameters.put("database", DBRelativeLocation );
			
		}
		return connectionParameters;
	}

	// Connect connection parameters with connection attributes of the object 
	public Map<String, Object>  createConnectionParams(IScope scope){
		Map<String, Object> connectionParameters = new HashMap<String,Object>();
		
		if (dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGRES) || dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGIS))
		{
		      connectionParameters.put("host",host);
		      connectionParameters.put("dbtype",dbtype);
		      connectionParameters.put("port", port);
		      connectionParameters.put("database",database);
		      connectionParameters.put("user",user);
		      connectionParameters.put("passwd",passwd);
		      // advanced
//		      params.put(PostgisDataStoreFactory.LOOSEBBOX, true );
//		      params.put(PostgisDataStoreFactory.PREPARED_STATEMENTS, true );
			
		}else if (dbtype.equalsIgnoreCase(GamaSqlConnection.MYSQL))
		{
//			java.util.Map params = new java.util.HashMap();
			connectionParameters.put(MySQLDataStoreFactory.DBTYPE.key, dbtype);
			connectionParameters.put(MySQLDataStoreFactory.HOST.key, host);
			connectionParameters.put(MySQLDataStoreFactory.PORT.key, new Integer(port));
			connectionParameters.put(MySQLDataStoreFactory.DATABASE.key, database);
			connectionParameters.put(MySQLDataStoreFactory.USER.key, user);
			connectionParameters.put(MySQLDataStoreFactory.PASSWD.key, passwd);
		}else if (dbtype.equalsIgnoreCase(GamaSqlConnection.MSSQL))
		{
		      connectionParameters.put("host",host);
		      connectionParameters.put("dbtype",dbtype);
		      connectionParameters.put("port", port);
		      connectionParameters.put("database",database);
		      connectionParameters.put("user",user);
		      connectionParameters.put("passwd",passwd);
				
		}else if (dbtype.equalsIgnoreCase(GamaSqlConnection.SQLITE))
		{
			String DBRelativeLocation = FileUtils.constructAbsoluteFilePath(scope, database, true);
			//String EXTRelativeLocation = GamaPreferences.LIB_SPATIALITE.value(scope).getPath();
			connectionParameters.put("dbtype", dbtype);
			connectionParameters.put("database", DBRelativeLocation );
			
		}
		return connectionParameters;
	}
	
    public DataStore Connect(IScope scope) throws Exception{
	  Map<String, Object> connectionParameters = new HashMap<String,Object>();
//      connectionParameters.put("host",host);
//      connectionParameters.put("dbtype",dbtype);
//      connectionParameters.put("port",port);
//      connectionParameters.put("database",database);
//      connectionParameters.put("user",user);
//      connectionParameters.put("passwd",passwd);
	  connectionParameters = createConnectionParams(scope);
   	  DataStore dStore;
      dStore = DataStoreFinder.getDataStore(connectionParameters); //get connection
      System.out.println("data store postgress:"+dStore);
      if (dStore == null) {
          throw new IOException("Can't connect to " + database);
      }
      return dStore;	  
    }
    
    
    public DataStore Connect(IScope scope, Map<String, Object> params) throws IOException{
  	  Map<String, Object> connectionParameters = new HashMap<String,Object>();
	  connectionParameters = createConnectionParams(scope,params);

	  DataStore dStore;
      dStore = DataStoreFinder.getDataStore(connectionParameters); //get connection
      if (dStore == null) {
    	  throw new IOException("Can't connect to " + database);
      }
      return dStore;	  
    }
    
    public void close() throws GamaRuntimeException{
    	if (dataStore != null){
    		dataStore.dispose();
    	}else {
			throw GamaRuntimeException.error("The connection to "+ this.database + " is not opened ");
    	}
    }
	public static GamaSqlConnection createConnectionObject( IScope scope, Map<String, Object> params)
			throws GamaRuntimeException, Exception {
		
			String dbtype = (String) params.get("dbtype");
			String host = (String) params.get("host");
			String port = (String) params.get("port");
			String database = (String) params.get("database");
			String user = (String) params.get("user");
			String passwd = (String) params.get("passwd");
           
			Map<String, Object> connectionParameters = new HashMap<String,Object>();
            connectionParameters.put("host",host);
            connectionParameters.put("dbtype",dbtype);
            connectionParameters.put("port",port);
            connectionParameters.put("database",database);
            connectionParameters.put("user",user);
            connectionParameters.put("passwd",passwd);

			if ( dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGRES) || dbtype.equalsIgnoreCase(GamaSqlConnection.POSTGIS) ) {
				DataStore dStore;
			    dStore = DataStoreFinder.getDataStore(connectionParameters); //get connection
			    if (dStore == null) {
		            JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
		        }
			    GamaSqlConnection sqlConn;
				sqlConn = new GamaSqlConnection(scope,connectionParameters);
		        sqlConn.setDataStore(dStore);// Create connection an get data store (database) for query
		        return sqlConn;
					
			} else {
				throw GamaRuntimeException.error("GAMA does not support databases of type: " + dbtype);
			}

		}
	
	public void setDataStore(DataStore dataStore){
		this.dataStore = dataStore;
	}
	
	public DataStore getDataStore(DataStore dstore){
		return this.dataStore;
	}
	protected void readTable(final IScope scope) {
		String tableName = (String) scope.getArg("table", IType.STRING);
		String filterStr = (String) scope.getArg("filter", IType.STRING);
		readTable(scope, tableName, filterStr);
	}

	protected void readTable(final IScope scope, String tableName, String filterStr) {
		
		SimpleFeatureIterator reader = null;
		IList list = getBuffer();
		try {
			QueryInfo queryInfo= new QueryInfo(this.dataStore, tableName, filterStr);
			int size = queryInfo.getSize();
			Envelope env= queryInfo.getEnvelope();
			int index = 0;
			computeProjection(scope, env);  //??
			//reader = store.getFeatureReader();
			reader =queryInfo.getRecordSet().features();
			int i=0;
			while (reader.hasNext()) {
				GuiUtils.updateSubStatusCompletion(index++ / size);
				Feature feature = reader.next();
				
				System.out.println("Record "+ i++ +": "+feature.getValue().toString());
				
				Geometry g = (Geometry) feature.getDefaultGeometryProperty().getValue();
				if ( g != null && !g.isEmpty() ) {
					g = gis.transform(g);
					list.add(new GamaGisGeometry(g, feature));
				} else {
					GAMA.reportError(
						scope,
						GamaRuntimeException.warning("GamaSqlConnection.fillBuffer; geometry could not be added : " +
							feature.getIdentifier(), scope), false);
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
			GuiUtils.endSubStatus("Reading table " + tableName);
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
	public void read(IScope scope){
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

//_________________________________________________________________	
	public static class QueryInfo  {

		final int itemNumber; // Number of records
		final CoordinateReferenceSystem crs;
		final double width;
		final double height;
		final Envelope env;
		final Map<String, String> attributes;   //meta data of query
		final SimpleFeatureCollection features;  // data/recordsets

		public QueryInfo(DataStore dStore, String tableName, String filterStr) {
			
			SimpleFeatureSource source = null;   
			SimpleFeatureCollection sfeatures=null;  // Query data
			Map<String, String> attributes = new LinkedHashMap(); // Meta data
			ReferencedEnvelope env = new ReferencedEnvelope();
			CoordinateReferenceSystem crs = null;
			int number = 0;
			try {

				source = dStore.getFeatureSource(tableName);  // table
				Filter filter = ECQL.toFilter(filterStr);     // create filter for read data
				sfeatures = source.getFeatures(filter);        //read data
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
				number = sfeatures.size();   // get number of records
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
				this.itemNumber = number;      // number of records
				this.crs = crs;
				this.features=sfeatures;       //  data
				this.attributes = attributes;  //  attributes
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
