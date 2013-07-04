package irit.gaml.skills;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import org.olap4j.*;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.NamedList;
import org.olap4j.layout.RectangularCellSetFormatter;

import msi.gama.database.mdx.MSASConnection;
import msi.gama.database.mdx.MdxConnection;
import msi.gama.database.mdx.MdxUtils;
import msi.gama.util.GamaList;

public class MDXApp { 

	public static void main(String [] args) throws Exception{
		Connection conn = null;
		OlapWrapper wrapper;
		OlapConnection olapConnection;
		//try{
			Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");
//			Connection connection =
//			    DriverManager.getConnection(
//			    			    "jdbc:xmla:Server=http://tmthai/olap/msmdpump.dll;"
//			    			    + "Cache=org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache;"
//			    			    + "Cache.Mode=LFU;Cache.Timeout=600;Cache.Size=100");	    		
//			    		"jdbc:xmla:Server=http://tmthai/olap/msmdpump.dll;Catalog=Northwind ASP");
//						"jdbc:xmla:Server=http://tmthai/olap/msmdpump.dll;");
			    		//"jdbc:xmla:Server=http://localhost/olap/msxisapi.dll;Catalog=Northwind ASP");
//			 wrapper = (OlapWrapper) connection;
//			 olapConnection = wrapper.unwrap(OlapConnection.class);	
			// OlapStatement statement =   olapConnection.createStatement();
			 MdxConnection mdxConnection = MdxUtils.createConnectionObject("SSAS/XMLA","localhost","80","olap","olapSA","olapSA");
			 olapConnection = (OlapConnection) mdxConnection.connectMDB();
//			 OlapStatement statement = (OlapStatement) conn.createStatement();
//			 System.out.println("OK");
			 System.out.println(olapConnection.getCatalog());
			 System.out.println("Namelist cube:");
			 //mdxConnection.prinCubesName(mdxConnection.getCubes(olapConnection));

			 System.out.println();
			//-------------------------------------------------------------------------------
//			 CellSet cellSet =
//					    statement.executeOlapQuery(
//					        //"SELECT {[Measures].[Price]} ON COLUMNS,"
//			"SELECT { [Measures].[unitprice], [Measures].[Quantity], [Measures].[Price] } ON COLUMNS,"
//					        + "  {[Product].[Product Category].[All].CHILDREN} ON ROWS"
//					        + " FROM [Northwind Star]");
			 //-------------------------------------------------------------------------------
//			 CellSet cellSet =
//					    statement.executeOlapQuery(
//			 "SELECT { [Product].[Product Category].[All].CHILDREN } ON COLUMNS ,"
//
//			 +"{ [Measures].[unitprice], [Measures].[Quantity], [Measures].[Price] } ON ROWS  "
//
//			+" FROM [Northwind Star]");
//			//-------------------------------------------------------------------------------
//			 CellSet cellSet =
//					    statement.executeOlapQuery(
//					 "SELECT { [Measures].[unitprice], [Measures].[Quantity], [Measures].[Price] } ON COLUMNS ,"
//
//					 +"{ { { [Time].[Year].[All].CHILDREN } * { [Product].[Product Category].[All].CHILDREN } } } ON ROWS " 
//
//					 + "  FROM [Northwind Star]");
			//-------------------------------------------------------------------------------		
			 
			 
//			 CellSet cellSet =
//					    statement.executeOlapQuery(			 
//			 "SELECT { [Measures].[Quantity], [Measures].[Price] } ON COLUMNS ,"
//
//			 +"{ { { [Time].[Year].[All].CHILDREN } * { [Product].[Product Category].[All].CHILDREN } } } ON ROWS  "
//
//			 +" FROM [Northwind Star] "
//
//			 +" WHERE ( [Customer].[Company Name].[All] ) ");
			 //------------------------------------------------------------------------------------
//			 CellSet cellSet =
//					    statement.executeOlapQuery(			 
//			 "SELECT { [Measures].[Quantity], [Measures].[Price] } ON COLUMNS ,"
//
//			+" { { { [Time].[Year].[All].CHILDREN } * "
//			+" { [Product].[Product Category].[All].CHILDREN } * "
//			+"{ [Customer].[Company Name].&[Alfreds Futterkiste], " +
//			"[Customer].[Company Name].&[Ana Trujillo Emparedados y helados], " +
//			"[Customer].[Company Name].&[Antonio Moreno Taquería] } } } ON ROWS " 
//
//			 +"FROM [Northwind Star] ");
			//------------------------------------------------------------------------------------
			 String selectStr=
					 "SELECT { [Measures].[Quantity], [Measures].[Price] } ON COLUMNS ,"
					 
					 			+ " { { { [Time].[Year].[All].CHILDREN } * "
					 			+ " { [Product].[Product Category].[All].CHILDREN } * "
					 			+ "{ [Customer].[Company Name].&[Alfreds Futterkiste], " 
					 			+ "[Customer].[Company Name].&[Ana Trujillo Emparedados y helados], " 
					 			+ "[Customer].[Company Name].&[Antonio Moreno Taquería] } } } ON ROWS " 
					 +"FROM [Northwind Star] ";

//			 String selectStr=
//			 "SELECT { [Product].[Product Category].[All].CHILDREN } ON COLUMNS ,"
//
//			 +"{ [Measures].[unitprice], [Measures].[Quantity], [Measures].[Price] } ON ROWS  "
//
//			+" FROM [Northwind Star]";

//			 String selectStr=
//					 "SELECT { [Product].[Product Category].[All].CHILDREN } ON COLUMNS ,"
//					 
//					 			 +"{ [Measures].[unitprice], [Measures].[Quantity], [Measures].[Price] } ON ROWS  "
//					 
//					 			+" FROM [Northwind Star]";


			 CellSet cellSet= mdxConnection.select(olapConnection,selectStr);
			 System.out.println("MDX OK");
			 //System.out.println("Meta data"+cellSet.getMetaData().toString());
			 mdxConnection.getCellSetMetaData(cellSet);
			 List<CellSetAxis> cellSetAxes = cellSet.getAxes();
			  // Print headings.
		        System.out.print("\t\t");
		        CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.axisOrdinal());
//		        for (Position position : columnsAxis.getPositions()) {
//		            Member measure = position.getMembers().get(0);
//		            System.out.print(measure.getName()+'\t');
//		        }
//		        GamaList<Object> cols=mdxConnection.getColumnName(cellSet);
//		        int n=cols.size();
//		        System.out.print("&&----------------------------------------------------------\n");
//		        for (int i=0; i<n;++i){
//		        	System.out.print(cols.get(i).toString()+";\t");
//		        }
//		        System.out.print("\n&&----------------------------------------------------------\n");
//		        GamaList<Object> rowsData=mdxConnection.getRowData(cellSet);
//		         int m=rowsData.size();
//		        for (int i=0; i<m;++i){
//		        	System.out.print("row"+ i+":\t");
//		        	GamaList<Object> row= (GamaList<Object>) rowsData.get(i);
//		        	GamaList<Object> members= (GamaList<Object>) row.get(0);
//		        	GamaList<Object> values= (GamaList<Object>) row.get(1);
//		        	// print member
//		        	int k = members.size();
//		        	for (int j=0;j<k;j++){
//		        		System.out.print(members.get(j).toString()+"\t");
//		        	}
//		        	//print value
//		        	int l = values.size();
//		        	for (int j=0;j<l;j++){
//		        		System.out.print(values.get(j).toString()+"\t");
//		        	}
//		        	System.out.println();
//
//		        }
		        
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
