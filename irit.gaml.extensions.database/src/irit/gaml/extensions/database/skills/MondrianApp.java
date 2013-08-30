package irit.gaml.extensions.database.skills;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.List;

import org.olap4j.Axis;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.CellSetMetaData;
import org.olap4j.OlapConnection;
import org.olap4j.Position;
import org.olap4j.layout.RectangularCellSetFormatter;
import org.olap4j.metadata.Member;

import msi.gama.database.mdx.MdxConnection;
import msi.gama.database.mdx.MdxUtils;
import msi.gama.util.GamaList;


public class MondrianApp {
	public static void main(String [] args) throws Exception{
		OlapConnection olapConnection;			 
		// Mondrian with MySQL
//		MdxConnection mdxConnection = MdxUtils.createConnectionObject("MONDRIAN","mysql","localhost","3306","foodmart"
//					 ,"C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\mondrian\\WEB-INF\\queries\\FoodMart.xml"
//					 ,"root","root"
//					 );
		// Mondrian with postgreSQL
		MdxConnection mdxConnection = MdxUtils.createConnectionObject("MONDRIAN","postgres","localhost","5432","foodmart"
				 ,"C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\mondrian\\WEB-INF\\queries\\FoodMart.xml"
//				 ,"FoodMart.xml"
				 
				 ,"postgres","tmt"
				 );
		
			 olapConnection = (OlapConnection) mdxConnection.connectMDB();
			 System.out.println("----Connection:"+olapConnection.toString());
//		        // Check if it's all groovy
			 ResultSet databases = olapConnection.getMetaData().getDatabases();
		      databases.first();
		     System.out.println("Database: "+
		                olapConnection.getMetaData().getDriverName()
		                + " -> "
		                + databases.getString(1));
//
//		        // Done
//     olapConnection.close();
//			 OlapStatement statement = (OlapStatement) conn.createStatement();
//			 System.out.println("OK");
			// olapConnection.setRoleName("Admin");
//			 System.out.println(olapConnection.getCatalog());
//			 System.out.println("Namelist cube:");
			 //mdxConnection.prinCubesName(mdxConnection.getCubes(olapConnection));

			 System.out.println();

			//------------------------------------------------------------------------------------
//			 String selectStr=
//					 "select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns" 
//					+", {([Promotion Media].[All Media], [Product].[All Products])} ON rows"
//							 +" from Sales where ([Time].[1997]) ";

//			 String selectStr=
//					 "select "
//					+"    {[Measures].[Unit Sales]} on columns,"
//					+"    order(except([Promotion Media].[Media Type].members,{[Promotion Media].[Media Type].[No Media]}),[Measures].[Unit Sales],DESC) on rows"
//					+" from Sales";

//			 String selectStr=
//					" select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} ON COLUMNS,"
//					+"   {([Promotion Media].[All Media], [Product].[All Products])} ON ROWS "
//					+" from [Sales] "
//					+" where [Time].[1997]";
			 
			 String selectStr=
			  " select {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} ON COLUMNS,"
			 +"     Hierarchize(Union(Union(Union({([Promotion Media].[All Media], [Product].[All Products])}, Crossjoin([Promotion Media].[All Media].Children, {[Product].[All Products]})), "
			 +"		Crossjoin({[Promotion Media].[Daily Paper, Radio, TV]}, [Product].[All Products].Children)), Crossjoin({[Promotion Media].[Street Handout]}, [Product].[All Products].Children))) ON ROWS "
			 +" from [Sales] "
			 +" where [Time].[1997] ";
			
//			 String selectStr=
//			 " select "
//			 +" CrossJoin("
//			 +"   {[Measures].[Unit Sales], [Measures].[Store Sales]},"
//			 +"   {[Time].[1997].[Q2].children}) on columns, "
//			 +" CrossJoin("
//			 +"   CrossJoin("
//			 +"     [Gender].members,"
//			 +"     [Marital Status].members),"
//			 +"  {[Store], [Store].children}) on rows"
//			 +" from [Sales]"
//			 +" where ("
//			 +" [Product].[Food],"
//			 +" [Education Level].[High School Degree],"
//			 +" [Promotions].DefaultMember)";


			 
			 CellSet cellSet= mdxConnection.select(olapConnection,selectStr);
			 System.out.println("MDX OK");
			 //System.out.println("Meta data"+cellSet.getMetaData().toString());
			 mdxConnection.getCellSetMetaData(cellSet);
			 List<CellSetAxis> cellSetAxes = cellSet.getAxes();
			  // Print headings.
		        System.out.print("\t\t");
		        CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.axisOrdinal());

		        
		        System.out.print("\nKiem tra lai2----------------------------------------------------------\n");
				GamaList<Object> olapResult = mdxConnection.selectMDB(olapConnection,selectStr);
				mdxConnection.prinColumnsName(olapResult);
				System.out.println();
				mdxConnection.printRowsData(olapResult);
		        System.out.print("\nKiem tra lai3----------------------------------------------------------\n");
				
		        // Print rows.
		        CellSetAxis rowsAxis = cellSetAxes.get(Axis.ROWS.axisOrdinal());
		        int cellOrdinal = 0;
		        for (Position rowPosition : rowsAxis.getPositions()) {
		            boolean first = true;
		            // print member on each row
		            for (Member member : rowPosition.getMembers()) {
		                if (first) {
		                    first = false;
		                } else {
		                    System.out.print("\t");
		                }
		                System.out.print("ROW["+rowPosition.getOrdinal()+","+member.getOrdinal()+"]:"+member.getName());
		            }
		            System.out.print("\t\t");
		            // Print the value of the cell in each column.
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
 
		                System.out.print('\t');
		                System.out.print(cell.getFormattedValue());
		            }
		            System.out.println();
		        }
		     // Now, nicely formatted.
		        System.out.println();
		        final PrintWriter pw = new PrintWriter(System.out);
		        new RectangularCellSetFormatter(false).format(cellSet, pw);
		        pw.flush();
		     // Iteration over a two-axis query
		        List<CellSetAxis> cellSetAxes1 = cellSet.getAxes();
		        //System.out.println("size"+cellSetAxes1.size());
		        //Meta Data---------------------------------------------------------------------------
		        CellSetMetaData cellSetMetaData=cellSet.getMetaData();
		       // System.out.println("Column Count:"+cellSetMetaData.getColumnCount());
		        System.out.println("Number of Axes:"+cellSetMetaData.getAxesMetaData().size());
		        System.out.println("Cube Name:"+cellSetMetaData.getCube().getName());
		        
		        //------------------------------------------------------------------------------------
//		        System.out.println(cellSetAxes1.get( Axis.ROWS.axisOrdinal() ));
//		        for (
//		          Position axis_0 
//		          : cellSet.getAxes().get( Axis.ROWS.axisOrdinal() ).getPositions()) 
//		        {
//		          for (
//		            Position axis_1
//		            : cellSet.getAxes().get(Axis.COLUMNS.axisOrdinal()).getPositions())
//		          {
//		            Object value = cellSet.getCell(axis_0, axis_1).getValue();
//		            System.out.print("("+ axis_0.getOrdinal() + " ; "+  axis_1.getOrdinal() +")" );	System.out.print('\t');	
//		            //System.out.print(value.toString()+'\t');	
//		          }
//		          System.out.println();
//		        }
		        // Close the statement and connection.
//		        statement.close();
//		        connection.close();
			 //CellSetMetaData cellMetaData = cellSet.getMetaData();
			 //System.out.println("Column count:"+ Integer.toString(cellMetaData.getColumnCount()));
			 //System.out.println( (cellMetaData).toString());
//		}catch (Exception e){
//			System.err.println("Error:"+e);
//		}
		
	}


}
 