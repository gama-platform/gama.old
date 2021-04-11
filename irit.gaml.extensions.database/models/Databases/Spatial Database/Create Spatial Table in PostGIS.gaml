/**
* Name:  CreateBuildingTablePostGIS
* Author: Truong Minh Thai
* Description: This model shows how to create a database and a table in PostGIS using GAMA
 * Tags: database
 */
model CreateBuildingTablePostGIS

global {
	map<string, string> PARAMS <- ['host'::'localhost', 'dbtype'::'Postgres', 'database'::'', 'port'::'5432', 'user'::'postgres', 'passwd'::''];

	init {
		write "This model will work only if the corresponding database is installed." color: #red;
		write "Note that for postgresql/postgis, a template database with postgis extension should be created previously.";
		write "With Postgresql 10, with pgAdmin 4: ";
		write "   - create a database named `template_postgis`,";
		write "   - open the Query tool (by right-clicking on the template_postgis database),";
		write "   - execute the code:  `CREATE EXTENSION postgis;`";
		write " pgAdmin 4 should be closed before trying to connect to the database from GAMA.";
		write "";
		create dummy;
		ask dummy {
			if (testConnection(PARAMS)) {
				do executeUpdate params: PARAMS updateComm: "DROP DATABASE IF EXISTS spatial_db ;";
				do executeUpdate params: PARAMS updateComm: "CREATE DATABASE spatial_db with TEMPLATE = template_postgis;";
				write "spatial_BD database has been created. ";

				// remove "database" from: PARAMS;
				put "spatial_db" key: "database" in: PARAMS;
				do executeUpdate params: PARAMS updateComm: "CREATE TABLE bounds" + "( " + " geom GEOMETRY " + ")";
				write "bounds table has been created.";
				do executeUpdate params: PARAMS updateComm: "CREATE TABLE buildings " + "( " + " name character varying(255), " + " type character varying(255), " + " geom GEOMETRY " + ")";
				write "buildings table has been created. ";
			} else {
				write "Connection to MySQL cannot be established ";
			}

		}

	}

}

species dummy skills: [SQLSKILL] {
}

experiment default_expr type: gui {
}