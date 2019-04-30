/**
* Name:  CreateBuildingTableMSSQL
* Author: Truong Minh Thai
* Description: This model shows how to create a database and a table in MSSQL using GAMA
 * Tags: database
 */
model CreateBuildingTable_MSSQL


global
{
	map<string, string> PARAMS <- ['host'::'127.0.0.1', 'dbtype'::'sqlserver', 'database'::'', 'port'::'1433', 'user'::'sa', 'passwd'::'tmt'];
	init
	{
		write "This model will work only if the corresponding database is installed" color: #red;

		create dummy;
		ask dummy
		{
			if (testConnection(PARAMS))
			{
				do executeUpdate params: PARAMS updateComm: "CREATE DATABASE spatial_DB";
				write "spatial_BD database was created ";
				remove  "database" from: PARAMS;
				put "spatial_DB" key: "database" in: PARAMS;
				do executeUpdate params: PARAMS updateComm: "CREATE TABLE bounds" + "( " + " geom GEOMETRY " + ")";
				write "bounds table was created ";
				do executeUpdate params: PARAMS updateComm: "CREATE TABLE buildings " + "( " + " name VARCHAR(255), " + " type VARCHAR(255), " + " geom GEOMETRY " + ")";
				write "buildings table was created ";
			} else
			{
				write "Connection to MySQL can not be established ";
			}

		}

	}

}

species dummy skills: [SQLSKILL]
{
}

experiment default_expr type: gui
{
}