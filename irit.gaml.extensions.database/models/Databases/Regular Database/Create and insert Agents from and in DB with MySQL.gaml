/**
* Name:  create_agents_Insert_result_MySQL
* Author: Benoit Gaudou
* Description: This model illustrates the use of the MySQL DBMS to: 
* 
 *     - create agents from a database
 * 
 *     - store every cycle some results into a database
 * 
 * 
 *  Note: this model could be used with any DBMS just by changing the PARAMS variable.
 * 
 * 
 *  NOTE: YOU SHOULD HAVE ALREADY CREATED YOUR DATABASE (meteo_DB here) AND IMPORTED THE FILE (../includes/meteo_DB_dump.sql)
 *        IN ORDER THAT THE MODEL CAN RUN PROPERLY.
* Tags: database
 */
model create_agents_Insert_result_MySQL 

global {
	string res_DB <- '`result_DB`';
	map<string, string> PARAMS <- ['host'::'localhost', 'dbtype'::'MySQL', 'database'::'meteo_DB', 'port'::'8889', 'user'::'root', 'passwd'::'root'];
	string SQLquery_idPoint <- "SELECT `idPointgrille`, AVG(`RRmm`) AS RR, AVG(`Tmin`) AS Tmin, AVG(`Tmax`) AS Tmax, AVG(`Rglot`) AS Rglot, AVG(`ETPmm`) AS ETPmm
    			FROM meteo_table GROUP BY `idPointgrille`";
	init {
		write "This model will work only if the MySQL database server is installed." color: #red;
		write "In addition, the database \"meteo_db\" should have be created and the data imported inside. The SQL queries are available in the file ../includes/meteo_DB_dump.sql.";
		write "";
		
		create DB_accessor;
		ask DB_accessor {
			do executeUpdate params: PARAMS updateComm: "DROP TABLE IF EXISTS `result_DB`";
			do executeUpdate params: PARAMS updateComm: "CREATE TABLE `result_DB` (
										  `idPoint` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
										  `valRnd` float NOT NULL DEFAULT '0',
										  `cycle` int(16) NOT NULL DEFAULT '0'
										) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		}

		write first(DB_accessor).select (PARAMS, SQLquery_idPoint);

		create idPoint from: first(DB_accessor).select(PARAMS, SQLquery_idPoint)
		with: [name:: "idPointgrille", RRmm::"RR", Tmin::"Tmin", Tmax::"Tmax", Rglot::"Rglot", ETPmm::"ETPmm"];
	}

	reflex endSimu when: (cycle = 10) {
		ask DB_accessor {
			write "Data: " + (select(PARAMS, "select * FROM "));
			do executeUpdate params: PARAMS updateComm: "DROP TABLE " + res_DB;
		}

		write "DROP the result table";
		do pause; 
	}

}

species idPoint {
	float RRmm;
	float Tmin;
	float Tmax;
	float Rglot;
	float ETPmm;
	float valRnd;
	
	reflex compute_new_random_value {
		valRnd <- float(rnd(RRmm + Tmin + Tmax + Rglot + ETPmm));
	}

	reflex store_valRnd {
		ask (first(DB_accessor)) {
			do executeUpdate params: PARAMS updateComm: "INSERT INTO " + res_DB + " VALUES(?, ?, ?);" values: [myself.name, myself.valRnd, cycle];
		}

		write " " + self + " inserts value " + valRnd;
	}
}

species DB_accessor skills: [SQLSKILL] {
	list listRes <- [];
	
	init {
		// Test of the connection to the database
		if (!testConnection(PARAMS)) {
			write "Connection impossible";
			ask (world) {
				do pause;
			}

		} else {
			write "Connection Database OK.";
		}

		write "" + (select(PARAMS,"SELECT * FROM meteo_table"));
		write "" + (select(PARAMS, SQLquery_idPoint));
	}

}

experiment createInsertMySQL type: gui {
}
   