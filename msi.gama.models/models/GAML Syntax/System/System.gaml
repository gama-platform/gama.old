/**
* Name: System
* Author: A. Drogoul
* Description: Describes the syntax used to get different system properties. These can be used to ensure, for instance, that a minimum memory is available, or that a minimum version of GAMA is used to run a model
* Tags: system, platform
*/
model System


global
{
	init
	{
		// The version of the current GAMA installation
		write sample(gama.version);
		// The list of plugins loaded in the current GAMA installation
		write gama.plugins;
		// The current time since epoch day (i.e. UNIX time)
		write sample(gama.machine_time) + " milliseconds since epoch day";
		// The current path to the workspace
		write gama.workspace_path;
		// The memory still available to be allocated to GAMA
		write sample(gama.free_memory) + " bytes" ;
		// The maximum amount of memory GAMA can be allocated
		write sample(gama.max_memory) + " bytes";
	}

}

experiment Run;