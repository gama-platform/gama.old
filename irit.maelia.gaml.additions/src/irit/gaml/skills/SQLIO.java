package irit.gaml.skills;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.ConnectionPoolDataSource;

import org.geotools.data.jdbc.ConnectionPool;
import org.geotools.data.mysql.MySQLConnectionFactory;
import org.geotools.data.mysql.MySQLDataStore;
import org.geotools.data.mysql.MysqlConnection;
import org.geotools.data.sqlserver.SQLServerDataStoreFactory;

import com.mysql.jdbc.MySQLConnection;

public class SQLIO {
	SQLServerDataStoreFactory sqlDataStoreFactory;
	
	public static void main(String[] args) throws Exception {
		//MySQLConnectionFactory connFactory=new MySQLConnectionFactory("127.0.0.1",3306,"BPH");
		 //connFactory.setLogin("root","");
		 //ConnectionPool connPool=connFactory.getConnectionPool(); 
		 //Connection conn = qs.connectMySQL("MySQL","127.0.0.1","3306","BPH","root","");
		 //rs = (ResultSet) conn.createStatement().executeQuery("select *  from VNM_ADM1");
		 //Connection conn = connectMySQL("MSSQL","193.49.54.165","1433","BPH","sa","tmt");
		 //rs = (ResultSet) conn.createStatement().executeQuery("select varname_1,AsWKB(geometry) as geo  from VNM_ADM1");
		 //rs = (ResultSet) conn.createStatement().executeQuery("select id_1, name_1,geom.STAsBinary()  from VNM_ADM1");
		MySQLDataStore mySQLDataStore= MySQLDataStore.getInstance("127.0.0.1",3306,"BPH","root","");
		//System.out.print(mySQLDataStore.getView(query)("BPH","Geometry"));
	}
	
	public static Connection connectMySQL(final String vendorName, final String url, final String port,
			final String dbName, final String usrName, final String password)
			throws ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException {
			Connection conn = null;
			String mySQLDriver = new String("com.mysql.jdbc.Driver");
			String msSQLDriver = new String("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			try {
				if ( vendorName.equalsIgnoreCase("MySQL") ) {
					Class.forName(mySQLDriver).newInstance();
					conn =
						(Connection) DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + dbName,
							usrName, password);
				} else if ( vendorName.equalsIgnoreCase("MSSQL") ) {
					Class.forName(msSQLDriver).newInstance();
					conn =
						(Connection) DriverManager.getConnection("jdbc:sqlserver://" + url + ":" + port +
							";databaseName=" + dbName + ";user=" + usrName + ";password=" + password +
							";");
				} else {
					throw new ClassNotFoundException("SQLConnection.connectSQL: The " + vendorName +
						"is not supported!");
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

}
