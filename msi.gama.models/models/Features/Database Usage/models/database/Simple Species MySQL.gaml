/**
 *  Author: Truong Minh Thai (thai.truongminh@gmail.com)
 *  Description:
 *  This model illustrates the use of the AgentDB species (instead of the SQLSKILL), and in particular following actions:
 *    - testConection
 *    - isConnected
 *    - close 
 *    - executeUpdate
 *    - insert
 *    - select
 *    - getParameter 
 *  This model does SQl query commands:
 * 		- Create table 
 * 		- Insert data
 *      - Select data
 * 	    - Delete data
 *      - Drop table 
 * 
 *  NOTE: YOU SHOULD HAVE ALREADY CREATED YOUR DATABASE (meteo_DB here) AND IMPORTED THE FILE (../../includes/meteo_DB_dump.sql)
 *        IN ORDER THAT THE MODEL CAN RUN PROPERLY.
 */
model simpleSQL_DBSpecies_MySQL

global {
	map<string, string> PARAMS <- ['host'::'localhost', 'dbtype'::'MySQL', 'database'::'meteo_DB', 'port'::'8889', 'user'::'root', 'passwd'::'root'];
	init {
		create DB_Accessor number: 1 {
			if (self testConnection (params::PARAMS) = false) {
				write "Impossible connection";
			} else {
				write "Connection of " + self;
				do connect params: PARAMS;
			}

		}

		if (first(DB_Accessor) isConnected [] = false) {
			write "No connection.";
			ask (AgentDB) {
				do close;
			}

			do halt;
		} else {
			write "" + first(DB_Accessor) getParameter ();
		}

		ask (DB_Accessor) {
			do executeUpdate updateComm: "CREATE TABLE registration" + "(id INTEGER PRIMARY KEY, " + " first TEXT NOT NULL, " + " last TEXT NOT NULL, " + " age INTEGER);";
			write "REGISTRATION table has been created.";
			do executeUpdate updateComm: "INSERT INTO registration " + "VALUES(100, 'Zara', 'Ali', 18);";
			do executeUpdate updateComm: "INSERT INTO registration " + "VALUES(?, ?, ?, ?);" values: [101, 'Mr', 'Mme', 45];
			do insert into: "registration" values: [102, 'Mahnaz', 'Fatma', 25];
			do insert into: "registration" columns: ["id", "first", "last"] values: [103, 'Zaid tim', 'Kha'];
			do insert into: "registration" columns: ["id", "first", "last"] values: [104, 'Bill', 'Clark'];
			write "Five records have been inserted.";
			write "Click on <<Step>> button to view selected data";
		}

	}

}

species DB_Accessor parent: AgentDB {
	reflex select {
		list<list> t <- list<list> (self select (select::"SELECT * FROM registration"));
		write "Select before updated " + t;
	}

	reflex update {
		do executeUpdate updateComm: "UPDATE registration SET age = 30 WHERE id IN (100, 101)";
		do executeUpdate updateComm: "DELETE FROM registration where id=103 ";
		list<list> t <- list<list> (self select (select::"SELECT * FROM registration"));
		write "Select after updated " + t;
	}

	reflex drop {
		do executeUpdate updateComm: "DROP TABLE registration";
		write "Registration table has been dropped.";
	}

}

experiment simple_SQL_exp type: gui {
}     