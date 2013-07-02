package msi.gama.database.mdx;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;

import org.olap4j.Axis;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.CellSetAxisMetaData;
import org.olap4j.CellSetMetaData;
import org.olap4j.OlapConnection;
import org.olap4j.OlapDatabaseMetaData;
import org.olap4j.OlapException;
import org.olap4j.OlapStatement;
import org.olap4j.Position;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.NamedList;
import org.olap4j.metadata.Property;

/*
 * @Author  
 *     TRUONG Minh Thai
 *     Fredric AMBLARD
 *     Benoit GAUDOU
 *     Christophe Sibertin-BLANC
 * 
 * 
 * SQLConnection:   supports the method
 * - connectDB: make a connection to DBMS.
 * - selectDB: connect to DBMS and run executeQuery to select data from DBMS.
 * - executeUpdateDB: connect to DBMS and run executeUpdate to update/insert/delete/drop/create data
 * on DBMS.
 * 
 * Created date: 18-Jan-2013
 * Modified:
 *     03-05-2013: add selectMDB methods
 *         
 * Last Modified: 02-07-2013
 */
public abstract class MdxConnection {
	private static final boolean DEBUG = false; // Change DEBUG = false for release version
	protected static final String MONDRIAN ="mondrian";
	protected static final String MONDRIANXMLA ="mondrian/xmla";
	protected static final String MSAS ="msas"; //Micrsoft SQL Server Analysis Services
	protected static final String MYSQL = "mysql";
	protected static final String POSTGRES = "postgres";
	protected static final String POSTGIS = "postgis";
	protected static final String MSSQL = "sqlserver";
	protected static final String SQLITE = "sqlite";

	protected static final String GEOMETRYTYPE="GEOMETRY";
	protected static final String MYSQLDriver = new String("com.mysql.jdbc.Driver");
	//static final String MSSQLDriver = new String("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	protected static final String MSSQLDriver = new String("net.sourceforge.jtds.jdbc.Driver");
	protected static final String SQLITEDriver = new String("org.sqlite.JDBC");
	protected static final String POSTGRESDriver = new String("org.postgresql.Driver");
		
	protected String vender="";
	protected String url="";
	protected String port="";
	protected String dbName="";
	protected String userName="";
	protected String password="";
	
	protected OlapConnection olapConnection;
	protected Connection connection;
	
	public MdxConnection(String vender)
	{
		this.vender=vender;
	}
	public MdxConnection(String venderName,String database)
	{
		this.vender=venderName;
		this.dbName=database;
	}
	public MdxConnection()
	{
	}

	public MdxConnection(String venderName,String url,String port,
			String dbName, String userName,String password)  
	{
		this.vender=venderName;
		this.url=url;
		this.port=port;
		this.dbName=dbName;
		this.userName=userName;
		this.password=password;	
	}
	
	public void setConnection(){
		this.connection=connectMDB();
		
	}
	public Connection getConnection(){
		return this.connection;
	}
	public boolean isConnected(){
		if (this.connection!=null){
			return true;
		}else {
			return false;
		}
	}
	
	public String getDatabase() throws GamaRuntimeException
	{
		try {
			return olapConnection.getDatabase();
		} catch (OlapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		}
	}
	
	public OlapDatabaseMetaData getMetaData() throws GamaRuntimeException
	{
		try {
			return olapConnection.getMetaData();
		} catch (OlapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		}
	}
	/*
	 * Make a connection to Multidimensional Database Server
	 */
	public abstract Connection connectMDB() throws GamaRuntimeException ;

	/*
	 * Select data source with connection was established
	 */
	
	public CellSet select(String selectComm)
	{
		CellSet resultCellSet=null;
		Connection conn=null;
		try {
			conn = connectMDB();
			resultCellSet = select(conn, selectComm);
			conn.close();
		} catch (SQLException e) {

		}
		return resultCellSet;
	}
	public CellSet select(Connection connection, String selectComm) throws GamaRuntimeException 
	{
		 CellSet resultCellSet=null;
		 OlapStatement statement;
		try {
			statement = (OlapStatement) connection.createStatement();
			resultCellSet=statement.executeOlapQuery(selectComm);
	        statement.close();
	        //connection.close();
		}catch (OlapException e){
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		}catch (SQLException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error(e.toString());
		}
		 return resultCellSet;
	}
	

	/*
	 * Select data source with connection was established
	 */
	public GamaList<Object> selectMDB(String selectComm)  
	{
		 CellSet cellSet=select(selectComm);
		 return cellSet2List(cellSet);
	}

	public GamaList<Object> selectMDB(Connection connection, String selectComm)  
	{
		 CellSet cellSet=select(connection,selectComm);
		 return cellSet2List(cellSet);
	}
	/*
	 *  Format of Olap query result (GamaList<Object>:
	 *      Result of OLAP query is transformed to Gamalist<Object> with order:
	 *      (0): GamaList<String>: List of column names.
	 *      (1): GamaList<Object>: Row data. it contains List of list and it look like a matrix with structure:
	 *          (0): the first row data
	 *          (1): the second row data 
	 *          ...
	 *          Each row data contains two element:
	 *           (0): rowMembers (GamaList<String>: this is a list of members in the row. 
	 *           (1): cellValues (Gamalist<Object>): This is a list of values in cell column or (we can call measures) 
	 *      
	 */
	public GamaList<Object> cellSet2List(CellSet cellSet){
		 GamaList<Object> olapResult = new GamaList<Object>();
		 olapResult.add(this.getColumnsName(cellSet));
		 olapResult.add(this.getRowsData(cellSet));
		 return olapResult;
	}
	
	public GamaList<Object> selectMBD(String onColumns, String onRows, String from){
		String mdxStr = "SELECT " + onColumns + " ON COLUMNS, " + onRows + " ON ROWS " 
				        + " FROM " + from ;
		return  selectMDB(mdxStr);
	}
	
	public GamaList<Object> selectMDB(Connection connection, String onColumns, String onRows, String from){
		String mdxStr = "SELECT " + onColumns + " ON COLUMNS, " + onRows + " ON ROWS " 
				        + " FROM " + from ;
		return  selectMDB(connection, mdxStr);
	}
	
	public GamaList<Object> selectMDB(String onColumns, String onRows, String from, String where){
		String mdxStr = "SELECT " + onColumns + " ON COLUMNS, " + onRows + " ON ROWS " 
				        + " FROM " + from + " WHERE ( " + where  + " )";
		return  selectMDB(mdxStr);
	}
	
	public GamaList<Object> selectMDB(Connection connection, String onColumns, String onRows, String from, String where){
		String mdxStr = "SELECT " + onColumns + " ON COLUMNS, " + onRows + " ON ROWS " 
				        + " FROM " + from + " WHERE ( " + where  + " )";
		return  selectMDB(connection, mdxStr);
	}
	

	protected GamaList<Object> getColumnsName(CellSet cellSet){
		GamaList<Object> columnsName = new GamaList<Object>();
		 List<CellSetAxis> cellSetAxes = cellSet.getAxes();
		  // get headings.
		 CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.axisOrdinal());
	     for (Position position : columnsAxis.getPositions()) {
	            Member measure = position.getMembers().get(0);
	            columnsName.add(measure.getName());
	      }
		return columnsName;
	     
	}
	protected GamaList<Object> getRowsData(CellSet cellSet){
		GamaList<Object> rowsData = new GamaList<Object>();
		
		List<CellSetAxis> cellSetAxes = cellSet.getAxes();
        CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.axisOrdinal());
        CellSetAxis rowsAxis = cellSetAxes.get(Axis.ROWS.axisOrdinal());
        int cellOrdinal = 0;
        for (Position rowPosition : rowsAxis.getPositions()) {
        	GamaList<Object> row = new GamaList<Object>();
        	GamaList<Object> rowMembers = new GamaList<Object>();
            // get member on each row
            for (Member member : rowPosition.getMembers()) {
            	rowMembers.add(member.getName());
            }
            // get value of the cell in each column.
            GamaList<Object> cellValues = new GamaList<Object>();
            for (Position columnPosition : columnsAxis.getPositions()) {
                // Access the cell via its ordinal. The ordinal is kept in step
                // because we increment the ordinal once for each row and
                // column.
                Cell cell = cellSet.getCell(cellOrdinal);
                // Just for kicks, convert the ordinal to a list of coordinates.
                // The list matches the row and column positions.
                List<Integer> coordList =
                    cellSet.ordinalToCoordinates(cellOrdinal);
                assert coordList.get(0) == rowPosition.getOrdinal();
                assert coordList.get(1) == columnPosition.getOrdinal();

                ++cellOrdinal;
                cellValues.add(cell.getFormattedValue());
            }
            // Add member and value to row 
            row.add(rowMembers);
            row.add(cellValues);
            // Add row to rowsData
            rowsData.add(row);
        }
        return rowsData;
	     
	}
	/*
	 *  Get all column names of OLAP query
	 */
	public GamaList<Object> getAllColummsName(GamaList<Object> olapResult){
		return (GamaList<Object>) olapResult.get(0);
	}
	/*
	 *  Get all column names of OLAP query
	 */
	public Object getColummNameAt(GamaList<Object> olapResult, int cIndex){
		return this.getAllColummsName(olapResult).get(cIndex);
	}

	/*
	 * Get  all rows data 
	 */
	public GamaList<Object> getAllRowsData(GamaList<Object> olapResult){
		return (GamaList<Object>) olapResult.get(1);
	}	
	/*
	 * Get  row data (row members + cell values)  at row index(rIndex) 
	 */
	public GamaList<Object> getRowDataAt(GamaList<Object> olapResult, int rIndex){
		return (GamaList<Object>) getAllRowsData(olapResult).get(rIndex);
	}

	/*
	 * Get all row members at row(index) 
	 */
	public GamaList<Object> getAllMembersAt(GamaList<Object> olapResult, int rIndex){
		return (GamaList<Object>) getRowDataAt(olapResult,rIndex).get(0);
	}
	/*
	 * Get  row member at  row index:rIndex ,member index:mIndex) 
	 */
	public Object getRowMemberAt(GamaList<Object> olapResult, int rIndex, int mIndex){
		return (Object) getAllMembersAt(olapResult,rIndex).get(mIndex);
	}
	
	/*
	 * Get all cell values at index row 
	 */
	public GamaList<Object> getAllCellValuesAt(GamaList<Object> olapResult, int rIndex){
		return (GamaList<Object>) getRowDataAt(olapResult,rIndex).get(1);
	}
	/*
	 * Get  cell value at  row index:rIndex ,cell index:cIndex) 
	 */
	public Object getCellValueAt(GamaList<Object> olapResult, int rIndex, int cIndex){
		return (Object) getAllCellValuesAt(olapResult,rIndex).get(cIndex);
	}
	
	/*
	 * Get cubes of OlapConnection
	 */
	public NamedList<Cube> getCubes(OlapConnection connection){
		NamedList<Cube> cubes=null;
		try {
			 cubes = connection.getOlapSchema().getCubes();
			
		} catch (OlapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cubes;
	}

	public void getCellSetMetaData(CellSet cellSet){
		CellSetMetaData cellSetMD=null;
		NamedList<CellSetAxisMetaData> cellSetAxisMD=null;
		NamedList<Property> properties =null;
		Cube cube=null;
		CellSetAxisMetaData filterAxisMD=null;
		try {
			cellSetMD= cellSet.getMetaData();
			cellSetAxisMD=cellSetMD.getAxesMetaData(); //MAP<K,V>
			properties=cellSetMD.getCellProperties();
			cube=cellSetMD.getCube();
			// print 
			System.out.println("CellSetAxis Meta Data");
			int m=cellSetAxisMD.size();
			for (int i=0;i<m;i++){
				CellSetAxisMetaData cellMD=cellSetAxisMD.get(i);
				List<Hierarchy> hierarchy =cellMD.getHierarchies();
				List<Property> property=cellMD.getProperties();
				System.out.println("Hierarchy");
				int n=hierarchy.size();
				for (int j=0;j<n;++j){
					System.out.print(hierarchy.get(j).getName()+"\t");
				}
				System.out.println("\n Properties");
				n=property.size();
				for (int j=0;j<n;++j){
					System.out.print(property.get(j).getName()+"\t");
				}

				
			}
			System.out.println("\n End Cell Set Meta Data ------------");
			System.out.println("Cell Set Axis Meta Data:"+cellSetAxisMD.iterator().toString());
			System.out.println("propertis Meta Data:"+properties.iterator().toString());
			System.out.println("cubes Meta Data:"+cube.toString());
		} catch (OlapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 *  print all row data
	 */
//	public void printOlapResul(GamaList<Object> rowsData){
//        int m=rowsData.size();
//        for (int i=0; i<m;++i){
//       		System.out.print("row"+ i+":\t");
//       		GamaList<Object> row= (GamaList<Object>) rowsData.get(i);
//       		GamaList<Object> members= (GamaList<Object>) row.get(0);
//       		GamaList<Object> values= (GamaList<Object>) row.get(1);
//       		// print member
//       		int k = members.size();
//       		for (int j=0;j<k;j++){
//       			System.out.print(members.get(j).toString()+"\t");
//       		}
//       		//print value
//       		int l = values.size();
//       		for (int j=0;j<l;j++){
//       			System.out.print(values.get(j).toString()+"\t");
//       		}
//       		System.out.println();
//       	}
//	}
//	
	/*
	 *  print all row data
	 */
	public void printRowsData(GamaList<Object> olapResult){
		
        int m=this.getAllRowsData(olapResult).size();
        for (int rIndex=0; rIndex<m;++rIndex){
       		System.out.print("row"+ rIndex+":\t");
       		// print member
       		int k = this.getAllMembersAt(olapResult, rIndex).size();
       		for (int mIndex=0;mIndex<k;mIndex++){
       			System.out.print(this.getRowMemberAt(olapResult, rIndex, mIndex).toString()+"\t");
       		}
       		//print value
       		int l = this.getAllCellValuesAt(olapResult, rIndex).size();
       		for (int cIndex=0;cIndex<l;++cIndex){
       			System.out.print(this.getCellValueAt(olapResult, rIndex, cIndex).toString()+"\t");
       		}
       		System.out.println();
       	}
	}
	/*
	 *  print all column names
	 */
	
	public void prinColumnsName(GamaList<Object> olapResult){
		int m=this.getAllColummsName(olapResult).size();
		for (int cIndex=0; cIndex<m; ++ cIndex){
			System.out.print(this.getColummNameAt(olapResult, cIndex).toString()+"\t");
		}
		
	}
	
	
	
	public void prinCubesName(NamedList<Cube> cubes){
		int m=cubes.size();
		for (int i=0; i<m; ++ i){
			List<Cube> cube=(List<Cube>) cubes.get(i);
			int n=cube.size();
			System.out.print(cube.get(i).toString()+"\t");
		}
		
	}

}// end class
