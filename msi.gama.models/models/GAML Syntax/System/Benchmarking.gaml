/***
* Name: Benchmark 
* Author: Benoit Gaudou
* Description: This model illustrates the possibility of GAMA in terms of benchmarking and profiling of the code.
*   In order to optimize a model that begins to become big and slow to run, 
*   it is necessary to identify the pieces of code that are the longest to execute.
*   This is the purpose of the benchmark statement and facet.
* 
*   GAML provides 2 ways of doing benchmarking:
*    - the statement benchmark, that benchmarks a specific block of statements (and shows information in the console)
*    - the facet benchmark of the statement experiment that benchmark the whole execution of the code (and write results in a csv file)
* Tags: benchmark, experiment
***/

model Benchmarking



global {
	init {
		create people number: 300;
	}
	
	reflex neighboorhood {
		// benchmark statement will compute the time spent to execute the block of code its embeds.
		// To get more reliable results, the inner statements can be executed several times (specified by the repeat: facet).
		benchmark "Benchmark of closest_to operator" repeat: 100 {
			ask people {
				do get_closest_people;
			}
		}
	}
}

species people {
	action get_closest_people {
		people neigh <- people closest_to self;
	} 
}

// When the facet benchmark: is used in an experiment, it will produces step after step a csv file
// that summarizes the time spent in block and the number of executions of each statement.
experiment Benchmarking type: gui benchmark: true { }
