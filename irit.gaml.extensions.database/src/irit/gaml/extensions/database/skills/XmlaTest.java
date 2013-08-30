package irit.gaml.extensions.database.skills;

import java.io.PrintWriter;
//import com.tonbeller.jpivot.chart.ChartComponent.jpivotCategoryURLGenerator;
//import com.tonbeller.jpivot.chart.ChartComponent.jpivotPieURLGenerator;
import mondrian.olap.Connection;
import mondrian.olap.DriverManager;
import mondrian.olap.Query;
import mondrian.olap.Result;

public class XmlaTest {


public void requete1 (){

//String connectString = "Provider=mondrian;" + 
//"Jdbc=jdbc:mysql://localhost:3306/foodmart?user=root&password=root;" +
//"Catalog=file:C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\mondrian\\WEB-INF\\queries\\FoodMart.xml;"+ 
//"JdbcDrivers=com.mysql.jdbc.Driver";
String connectString = "Provider=mondrian;" + 
"Jdbc=jdbc:postgresql://localhost:5432/foodmart?user=postgres&password=tmt;" +
"Catalog=file:C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\mondrian\\WEB-INF\\queries\\FoodMart.xml;"+ 
"JdbcDrivers=org.postgresql.Driver";
System.out.println(connectString);
Connection connection = null;
connection = DriverManager.getConnection(connectString, null);
Query query = connection.parseQuery(
//"SELECT {[Measures].[Unit Sales], [Measures].[Store Cost], [Measures].[Store Sales]} on columns," +
//"{([Promotion Media].[All Media], [Product].[All Products])} ON rows "+
//"FROM Sales " +
//"WHERE ([Time].[1997])"); 
" select "
+" CrossJoin("
+"   {[Measures].[Unit Sales], [Measures].[Store Sales]},"
+"   {[Time].[1997].[Q2].children}) on columns, "
+" CrossJoin("
+"   CrossJoin("
+"     [Gender].members,"
+"     [Marital Status].members),"
+"  {[Store], [Store].children}) on rows"
+" from [Sales]"
+" where ("
+" [Product].[Food],"
+" [Education Level].[High School Degree],"
+" [Promotions].DefaultMember)");

Result result = connection.execute(query);
result.print(new PrintWriter(System.out,true)); 
}


public static void main (String[] args){
new XmlaTest().requete1();

}
}