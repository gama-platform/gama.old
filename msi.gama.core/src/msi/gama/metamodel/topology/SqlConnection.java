package msi.gama.metamodel.topology;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//import org.geotools.data.teradata.WKBAttributeIO;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

import msi.gama.common.util.GisUtils;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaPair;
import msi.gaml.types.IType;

/*
 * @Author TRUONG Minh Thai 
 * @Supervisors:
 *     Christophe Sibertin-BLANC
 *     Fredric AMBLARD
 *     Benoit GAUDOU
 * 
 * 
 * SQLConnection:   supports the method
 * - connectDB: make a connection to DBMS.
 * - selectDB: connect to DBMS and run executeQuery to select data from DBMS.
 * - executeUpdateDB: connect to DBMS and run executeUpdate to update/insert/delete/drop/create data
 * on DBMS.
 * 
 * Created date: 17-Feb-2012
 * Modified:
 *     20-Jun-2012: Change the Skill class to class library for Skill and Agent class
 *     19-Sep-2012: Change const MSSQL to SQLSERVER for SQL Server 
 *     20-Sep-2012: Add SQLITE to all methods
 *     24-Sep-2012: Add methods
 *                    - SqlConnection()
 *                    - select(Connection conn, String select)
 *                    - executeUpdate(Connection conn, String updateConn)
 *    25-Sep-2012:  
 *        Add case: geometry of SQLITE into methods:
 *    			- List<Integer> getGeometryColumns(ResultSetMetaData rsmd)
 *              - GamaList<Object> getColumnTypeName(ResultSetMetaData rsmd)       
 * Last Modified: 25-Sep-2012
 */
public class SqlConnection {
		static final boolean DEBUG = false; // Change DEBUG = false for release version
		public static final String MYSQL ="MySQL";
		public static final String POSTGRES="postgres";
		//static final String MSSQL ="MsSQL";
		public static final String MSSQL ="sqlserver";
		public static final String SQLITE="sqlite";
		static final String MYSQLDriver = new String("com.mysql.jdbc.Driver");
		static final String MSSQLDriver = new String("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		static final String SQLITEDriver = new String("org.sqlite.JDBC");
		static final String POSTGRESDriver= new String("org.postgresql.Driver");
		static String vender="";
		static String url="";
		static String port="";
		static String dbName="";
		static String userName="";
		static String password="";
		
		public SqlConnection(String dbName)
		{
			this.dbName=dbName;
		}
		public SqlConnection(String venderName,String database)
		{
			this.vender=venderName;
			this.dbName=database;
		}
		public SqlConnection()
		{
		}

		public SqlConnection(String venderName,String url,String port,
				String dbName, String userName,String password)
		{
			this.vender=venderName;
			this.url=url;
			this.port=port;
			this.dbName=dbName;
			this.userName=userName;
			this.password=password;	
			//conn=connectDB();
		}
		
		/*
		 * Make a connection to BDMS
		 * 
		 * @syntax: do action: connectDB {
		 * arg vendorName value: vendorName; //MySQL/MSSQL
		 * arg url value: urlvalue;
		 * arg port value: portvaluse;
		 * arg dbName value: dbnamevalue;
		 * arg usrName value: usrnamevalue;
		 * arg password value: pwvaluse;
		 * }
		 */
		public Connection connectDB()
				throws ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException 
		{
				Connection conn = null;
				try {
					if ( vender.equalsIgnoreCase(MYSQL) ) {
						Class.forName(MYSQLDriver).newInstance();
						conn =
							DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + dbName,
								userName, password);
					} else if ( vender.equalsIgnoreCase(MSSQL) ) {
						Class.forName(MSSQLDriver).newInstance();
						conn =
							DriverManager.getConnection("jdbc:sqlserver://" + url + ":" + port +
								";databaseName=" + dbName + ";user=" + userName + ";password=" + password +
								";");
					} else if ( vender.equalsIgnoreCase(SQLITE) ) {
						Class.forName(SQLITEDriver).newInstance();
						conn =
							DriverManager.getConnection("jdbc:sqlite:" + dbName);
						if (DEBUG){
							System.out.println("SQLlite:"+conn.toString());
						}
					} else if ( vender.equalsIgnoreCase(POSTGRES) ) {
						//Class.forName(POSTGRESDriver).newInstance();
						Class.forName("org.postgresql.Driver");
						//System.out.println("Postgres:OK");
						conn =
							DriverManager.getConnection("jdbc:postgresql://"+url+":"+port+"/"+ dbName + "?user="+ userName + "&password=" + password);
					}  else {
						throw new ClassNotFoundException("SQLConnection.connectSQL: The " 
					                                       + vender
					                                       + " is not supported!");
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					throw new ClassNotFoundException(e.toString());
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new InstantiationException(e.toString());
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new IllegalAccessException(e.toString());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new SQLException(e.toString());
				}
				return conn;
		}

		/*
		 * Make a connection to BDMS and execute the select statement
		 * 
		 * @return GamaList<GamaList<Object>>
		 */
		//public GamaList<GamaList<Object>> selectDB(String selectComm) 
		public GamaList<Object> selectDB(String selectComm) 
		{
			ResultSet rs;
			GamaList<Object> result=new GamaList<Object>();
			//GamaList<Object> rowList = new GamaList<Object>();
			GamaList<GamaList<Object>> repRequest = new GamaList<GamaList<Object>>();
			Connection conn=null;
			try {
				conn=connectDB();
				if (DEBUG){
					System.out.println("Select Command:"+ selectComm);
				}
				Statement st = conn.createStatement(); 
				rs = st.executeQuery(selectComm);
				ResultSetMetaData rsmd = rs.getMetaData();
				if (DEBUG){
					System.out.println("MetaData:"+rsmd.toString());
				}
				//repRequest=resultSet2GamaList(rs);
				
				//result.add(rsmd);
				result.add(getColumnName(rsmd));
				result.add(getColumnTypeName(rsmd));
				
				repRequest=resultSet2GamaList(rs);
				result.add(repRequest);
				
				if (DEBUG){
					System.out.println("list of column name:" + result.get(0) );
					System.out.println("list of column type:" + result.get(1) );
					System.out.println("list of data:" + result.get(2) );
				}
				rs.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.selectDB: " + e.toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.selectDB: " + e.toString());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.selectDB: " + e.toString());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.selectDB: " + e.toString());
			}

			if ( DEBUG ) {
				GuiUtils.informConsole(selectComm + " was run");
			}
			//return repRequest;
			return result;
		}
		/*
		 * Make a connection to BDMS and execute the select statement
		 * 
		 * @return GamaList<GamaList<Object>>
		 */
		//public GamaList<GamaList<Object>> selectDB(String selectComm) 
		public GamaList<Object> selectDB(Connection conn, String selectComm) 
		{
			ResultSet rs;
			GamaList<Object> result=new GamaList<Object>();
			//GamaList<Object> rowList = new GamaList<Object>();
			GamaList<GamaList<Object>> repRequest = new GamaList<GamaList<Object>>();
			try {
				Statement st = conn.createStatement(); 
				rs = st.executeQuery(selectComm);
				ResultSetMetaData rsmd = rs.getMetaData();
				if (DEBUG){
					System.out.println("MetaData:"+rsmd.toString());
				}
				//repRequest=resultSet2GamaList(rs);
				
				//result.add(rsmd);
				result.add(getColumnName(rsmd));
				result.add(getColumnTypeName(rsmd));
				
				repRequest=resultSet2GamaList(rs);
				result.add(repRequest);
				
				if (DEBUG){
					System.out.println("list of column name:" + result.get(0) );
					System.out.println("list of column type:" + result.get(1) );
					System.out.println("list of data:" + result.get(2) );
				}
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.selectDB: " + e.toString());
			}
			//return repRequest;
			return result;
		}

		/*
		 * Make a connection to BDMS and execute the update statement (update/insert/delete/create/drop)
		 * 
		 */

		public int executeUpdateDB(String updateComm) throws GamaRuntimeException {
			Connection conn = null;
			int n = 0;
			try {
				conn = connectDB();
				if (DEBUG) {
					System.out.println("Update Command:" +updateComm);
				}
				Statement st = conn.createStatement(); 
				n = st.executeUpdate(updateComm);
				if (DEBUG) {
					System.out.println("Updated records :" +n);
				}

				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
			}

			if ( DEBUG ) {
				GuiUtils.informConsole(updateComm + " was run");
			}

			return n;

		}

		/*
		 *  execute the update statement with current connection(update/insert/delete/create/drop)
		 * 
		 */

		public int executeUpdateDB(Connection conn, String updateComm) throws GamaRuntimeException {
			int n = 0;
			try {
				if (DEBUG) {
					System.out.println("Update Command:" +updateComm);
				}
				Statement st = conn.createStatement(); 
				n = st.executeUpdate(updateComm);
				if (DEBUG) {
					System.out.println("Updated records :" +n);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new GamaRuntimeException("SQLConnection.executeUpdateDB: " + e.toString());
			}
			if ( DEBUG ) {
				GuiUtils.informConsole(updateComm + " was run");
			}

			return n;
		}
		
		
		
		GamaList<GamaList<Object>> resultSet2GamaList(ResultSet rs) throws SQLException
		{
			ResultSetMetaData rsmd=rs.getMetaData();
			return resultSet2GamaList(rsmd,rs);
		}
		
		/*
		 * @Method:resultSet2GamaList(ResultSetMetaData rsmd, ResultSet rs)
	     * @Description: Convert RecordSet to GamaList
	     * 
	     * @param ResultSetMetaData,ResultSet
	     * 
	     * @return GamaList<GamaList<Object>>
	     * 
	 	 */
		GamaList<GamaList<Object>> resultSet2GamaList(ResultSetMetaData rsmd, ResultSet rs)
			{
				// convert Geometry in SQL to Geometry type in GeoTool
				
				GamaList<GamaList<Object>> repRequest = new GamaList<GamaList<Object>>();
				try{
					List<Integer>  geoColumn = getGeometryColumns(rsmd);
					int nbCol = rsmd.getColumnCount();
					int i=1;
					while (rs.next())
					{
						//InputStream inputStream = rs.getBinaryStream(i);
						GamaList<Object> rowList = new GamaList<Object>();
						for ( int j = 1; j <= nbCol; j++ ) 
						{
							// check column is geometry column?
							if (geoColumn.contains(j)){
								if (DEBUG) System.out.println("convert at ["+ i+","+j+"]: ");
								//rowList.add(Binary2Geometry(rs.getBytes(j)));
								//rowList.add(InputStream2Geometry(rs.getBinaryStream(j)));
								//rowList.add(read((byte[])rs.getObject(j)));
								//rowList.add(read(rs.getBytes(j)));
								//rowList.add(read(rs,j));
								if (vender.equalsIgnoreCase(MYSQL)){
									rowList.add(InputStream2Geometry(rs.getBinaryStream(j)));
								}
								else //for (vender.equalsIgnoreCase(MSSQL)/(vender.equalsIgnoreCase(SQLITE))
									rowList.add(read(rs.getBytes(j)));
								    //rowList.add(InputStream2Geometry( (InputStream) rs.getObject(j)));
							}
							else
								rowList.add(rs.getObject(j));
						}
						repRequest.add(rowList);
						i++;
					}
				}
				catch (Exception e)
				{
					
				}
				return repRequest;
							
			}
			
	

			/*
		     * @Meththo: getGeometryColumns(ResultSetMetaData rsmd)
		     * @Description: Get columns id of field with geometry type
		     * 
		     * @param ResultSetMetaData
		     * 
		     * @return List<Integer>
		     * 
		     * @throws SQLException
		 	 */
			List<Integer> getGeometryColumns(ResultSetMetaData rsmd)
			throws SQLException
			{
				 int numberOfColumns = rsmd.getColumnCount();
				 List<Integer>  geoColumn = new ArrayList<Integer>();
				 for (int i=1; i<=numberOfColumns; i++){
					 
					 if (DEBUG){
						 System.out.print("col "+ i + ": "+ rsmd.getColumnName(i));
						 System.out.print("   - Type: "+ rsmd.getColumnType(i));
						 System.out.print("   - TypeName: "+ rsmd.getColumnTypeName(i));
						 System.out.println("  - size: "+ rsmd.getColumnDisplaySize(i));
						 
					 }
					
					 /* for Geometry 
					 - in MySQL Type: -2/-4   - TypeName: UNKNOWN   - size: 2147483647
					 - In MSSQL Type: -3   - TypeName: geometry     - size: 2147483647
					 - In SQLITE Type: 2004   - TypeName: BLOB      - size: 2147483647
					 */
					 // Search column with Geometry type
					 if ((vender.equalsIgnoreCase(MYSQL) & rsmd.getColumnType(i)==-4 ) || 
							 (vender.equalsIgnoreCase(MYSQL) & rsmd.getColumnType(i)==-2 ) || 
							 (vender.equalsIgnoreCase(MSSQL) & rsmd.getColumnType(i)==-3 )||
							 (vender.equalsIgnoreCase(SQLITE) & rsmd.getColumnType(i)==2004) )
						 geoColumn.add(i);
				}		
				 return geoColumn;
			}

			/*
		     * @Method: getColumnName
		     * @Description: Get columns name
		     * 
		     * @param ResultSetMetaData
		     * 
		     * @return GamaList<String>
		     * 
		     * @throws SQLException
		 	 */
			
			GamaList<Object> getColumnName(ResultSetMetaData rsmd) throws SQLException
			{
				 int numberOfColumns = rsmd.getColumnCount();
				 GamaList<Object>  columnType = new GamaList<Object>();
				 for (int i=1; i<=numberOfColumns; i++){
						 columnType.add(rsmd.getColumnName(i).toUpperCase());
				}		
				 return columnType;				
			}

			/*
		     * @Method: getColumnTypeName
		     * @Description: Get columns type name
		     * 
		     * @param ResultSetMetaData
		     * 
		     * @return GamaList<String>
		     * 
		     * @throws SQLException
		 	 */
			
			GamaList<Object> getColumnTypeName(ResultSetMetaData rsmd) throws SQLException
			{
				 int numberOfColumns = rsmd.getColumnCount();
				 GamaList<Object>  columnType = new GamaList<Object>();
				 for (int i=1; i<=numberOfColumns; i++){
					 /* for Geometry 
					 - in MySQL Type: -2   - TypeName: UNKNOWN   - size: 2147483647
					 - In MSSQL Type: -3   - TypeName: geometry  - size: 2147483647
					 */
					 // Search column with Geometry type
					 if ((vender.equalsIgnoreCase(MYSQL) & rsmd.getColumnType(i)==-2) || 
							 (vender.equalsIgnoreCase(MSSQL) & rsmd.getColumnType(i)==-3)||
							 (vender.equalsIgnoreCase(SQLITE) & rsmd.getColumnType(i)==2004) )
						 columnType.add("GEOMETRY");
					 else
						 columnType.add(rsmd.getColumnTypeName(i).toUpperCase());
				}		
				 return columnType;				
			}
			
			/*
		     * @Method: read(byte [] b)
		     * @Description: Convert Binary to Geometry (sqlserver case)
		     * 
		     * @param byte [] b
		     * 
		     * @return Geometry
		     * 
		     * @throws IOException, ParseException
		 	 */
			static Geometry read(byte [] b) throws IOException, ParseException
			{
				//WKBAttributeIO wkb=new WKBAttributeIO();
				WKBReader wkb= new WKBReader();
				Geometry geom=wkb.read(b);
				return geom;
				//return GisUtils.fromGISToAbsolute(geom);
			}
			 /*
		     * @Method: Binary2Geometry(byte [] geometryAsBytes )
		     * @description: Convert binary to Geometry 
		     * @param byte []
		     * 
		     * @return Geometry
		     * 
		     * @throws ParseException
		 	 */
			static Geometry Binary2Geometry(byte [] geometryAsBytes ) throws ParseException
			{
				 byte[] wkb = new byte[geometryAsBytes.length - 4];
				 System.arraycopy(geometryAsBytes, 4, wkb, 0, wkb.length);
				 WKBReader wkbReader = new WKBReader();
				 Geometry geom=wkbReader.read(wkb);	
				
				 return geom;
			}

			/*
		     * @Method: InputStream2Geometry(InputStream inputStream)
		     * @Description: Convert Binary to Geometry (MySQL case)
		     * 
		     * @param InputStream inputStream
		     * 
		     * @return Geometry
		     * 
		     * @throws Exception
		 	 */
			
			public static Geometry InputStream2Geometry(InputStream inputStream) throws Exception {
				 
				 
			     Geometry dbGeometry = null;
			 
			     if (inputStream != null) {
			 
			         //convert the stream to a byte[] array
			         //so it can be passed to the WKBReader
			         byte[] buffer = new byte[255];
			 
			         int bytesRead = 0;
			         ByteArrayOutputStream baos = new ByteArrayOutputStream();
			         while ((bytesRead = inputStream.read(buffer)) != -1) {
			             baos.write(buffer, 0, bytesRead);
			         }
			 
			         byte[] geometryAsBytes = baos.toByteArray();
			 
			         if (geometryAsBytes.length < 5) {
			             throw new Exception("Invalid geometry inputStream - less than five bytes");
			         }
			 
			         //first four bytes of the geometry are the SRID,
			         //followed by the actual WKB.  Determine the SRID
			         //here
			         byte[] sridBytes = new byte[4];
			         System.arraycopy(geometryAsBytes, 0, sridBytes, 0, 4);
			         boolean bigEndian = (geometryAsBytes[4] == 0x00);
			 
			         int srid = 0;
			         if (bigEndian) {
			            for (int i = 0; i < sridBytes.length; i++) {
			               srid = (srid << 8) + (sridBytes[i] & 0xff);
			            }
			         } else {
			            for (int i = 0; i < sridBytes.length; i++) {
			              srid += (sridBytes[i] & 0xff) << (8 * i);
			            }
			         }
			 
			         //use the JTS WKBReader for WKB parsing
			         WKBReader wkbReader = new WKBReader();
			 
			         //copy the byte array, removing the first four
			         //SRID bytes
			         byte[] wkb = new byte[geometryAsBytes.length - 4];
			         System.arraycopy(geometryAsBytes, 4, wkb, 0, wkb.length);
			         dbGeometry = wkbReader.read(wkb);
			         dbGeometry.setSRID(srid);
			     }
			 
			     return dbGeometry;			 }
			
			
//			public Geometry read(ResultSet rs, int index) throws IOException
//			{
//				return new WKBAttributeIO().read(rs, index);
//			}
			public String getURL(){ return url; }
			public String getVendor(){ return vender; }
			public String getUser(){ return userName;}
			
			/*
		     * @Method: getBounds( GamaList<Object> gamaList)
		     * @Description: Get Envelope of a set of geometry
		     * 
		     * @param GamaList<Object> gamaList: gamalist is a set of geometry type
		     * 
		     * @return Envelope: Envelope/boundary of the geometry set.
		     * 
		     * @throws Exception
		 	 */

		public static Envelope getBounds( GamaList<Object> gamaList)throws IOException {
				   	Envelope envelope;
					//get Column name
					GamaList<Object> colNames=(GamaList<Object>) gamaList.get(0);
					//get Column type
					GamaList<Object> colTypes=(GamaList<Object>) gamaList.get(1);
					int index=colTypes.indexOf("GEOMETRY");
					if (index<0) return null;
					else {
						//Get ResultSet 
						GamaList<GamaList<Object>> initValue = (GamaList<GamaList<Object>>) gamaList.get(2);
						int n=initValue.length();
						//int max = number == null ? Integer.MAX_VALUE : numberOfAgents;
			            if (n<0) return null;
			            else {
			            	GamaList<Object> rowList = initValue.get(0);
			            	Geometry geo= (Geometry) rowList.get(index);
			            	envelope=geo.getEnvelopeInternal();
			            	double maxX=envelope.getMaxX();
			            	double maxY=envelope.getMaxY();
			            	double minX=envelope.getMinX();
			            	double minY=envelope.getMinY();
			            	

							for (int i=1; i<n && i<Integer.MAX_VALUE; i++)
							{
								
								rowList = initValue.get(i);
								geo= (Geometry) rowList.get(index);
								envelope=geo.getEnvelopeInternal();
				            	double maxX1=envelope.getMaxX();
				            	double maxY1=envelope.getMaxY();
				            	double minX1=envelope.getMinX();
				            	double minY1=envelope.getMinY();

								maxX= maxX>maxX1 ? maxX : maxX1 ;
								maxY= maxY>maxY1 ? maxY : maxY1 ;
								minX= minX<minX1 ? minX : minX1 ;
								minY= minY<minY1 ? minY : minY1 ;
								envelope.init(minX,maxX, minY, maxY);
								
							}
							return envelope;
			            }
					}

			   }
}
