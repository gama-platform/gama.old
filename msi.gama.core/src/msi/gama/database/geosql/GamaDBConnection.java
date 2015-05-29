package msi.gama.database.geosql;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public class GamaDBConnection {
	protected static final boolean DEBUG = false; // Change DEBUG = false for release version
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
	
	// Database object
    protected DataStore dataStore;

    public GamaDBConnection(){
    	
    }
    public GamaDBConnection(Map<String, Object> params){
		 this.dbtype = (String) params.get("dbtype");
		 this.host = (String) params.get("host");
		 this.port = (String) params.get("port");
		 this.database = (String) params.get("database");
		 this.user = (String) params.get("user");
		 this.passwd = (String) params.get("passwd");
    }

    public GamaDBConnection(IScope scope){
		Map<String, Object> params = (Map<String, Object>) scope.getArg("params", IType.MAP);
		 this.dbtype = (String) params.get("dbtype");
		 this.host = (String) params.get("host");
		 this.port = (String) params.get("port");
		 this.database = (String) params.get("database");
		 this.user = (String) params.get("user");
		 this.passwd = (String) params.get("passwd");
    	
    }
    public GamaDBConnection(IScope scope, Map<String, Object> params){
		 this.dbtype = (String) params.get("dbtype");
		 this.host = (String) params.get("host");
		 this.port = (String) params.get("port");
		 this.database = (String) params.get("database");
		 this.user = (String) params.get("user");
		 this.passwd = (String) params.get("passwd");
    }
    
    public DataStore Connect() throws Exception{
	  Map<String, Object> connectionParameters = new HashMap<String,Object>();
      connectionParameters.put("host",host);
      connectionParameters.put("dbtype",dbtype);
      connectionParameters.put("port",port);
      connectionParameters.put("database",database);
      connectionParameters.put("user",user);
      connectionParameters.put("passwd",passwd);
   	  DataStore dStore;
      dStore = DataStoreFinder.getDataStore(connectionParameters); //get connection
      System.out.println("data store postgress:"+dStore);
      if (dStore == null) {
//          JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
          throw new IOException("Can't connect to " + database);
      }
      return dStore;	  
    }
    public DataStore Connect(Map<String, Object> connectionParameters) throws IOException{
	  DataStore dStore;
      dStore = DataStoreFinder.getDataStore(connectionParameters); //get connection
      System.out.println("data store postgress:"+dStore);
      if (dStore == null) {
//          JOptionPane.showMessageDialog(null, "Could not connect - check parameters");
          throw new IOException("Can't connect to " + database);
      }
      return dStore;	  
    }
    
    public void setDataStore(DataStore dataStore){
    	this.dataStore = dataStore;
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
  				this.features=sfeatures;       // asign data
  				this.attributes = attributes;  // asign attributes
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
   
    public static void main(String[] args) throws Exception {
    	GamaDBConnection gamaDBConn = new GamaDBConnection();
        Map<String, Object> connectionParameters = new HashMap<String,Object>();
        //Postgres
        connectionParameters.put("host","localhost");
        connectionParameters.put("dbtype","postgis");
        connectionParameters.put("port","5433");
        connectionParameters.put("database","GAMADB");
        connectionParameters.put("user","postgres");
        connectionParameters.put("passwd","tmt");
   	    DataStore ds= gamaDBConn.Connect(connectionParameters);
        System.out.println("data store Postgres:"+ds);
        //MSSQL
        connectionParameters.put("host","localhost");
        connectionParameters.put("dbtype","sqlserver");
        connectionParameters.put("port","1433");
        connectionParameters.put("database","MultiScale");
        connectionParameters.put("user","sa");
        connectionParameters.put("passwd","tmt");
        ds= gamaDBConn.Connect(connectionParameters);
        System.out.println("data store MSSQL:"+ds);
    }

}
