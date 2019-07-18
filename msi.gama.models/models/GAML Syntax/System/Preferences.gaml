// GAMA 1.8 Preferences saved on 2018-08-03T10:54:14.609

model preferences

experiment 'Display Preferences' type: gui {
init {
	//Append the name of simulations to their outputs
	write sample(gama.pref_append_simulation_name);

	//Display grid lines
	write sample(gama.pref_chart_display_gridlines);

	//Monitor memory and emit a warning if it is low
	write sample(gama.pref_check_memory);

	//Max. number of characters to keep when paused (-1 = unlimited)
	write sample(gama.pref_console_buffer);

	//Max. number of characters to display (-1 = unlimited)
	write sample(gama.pref_console_size);

	//Wrap long lines (can slow down output)
	write sample(gama.pref_console_wrap);

	//Custom date pattern (https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns)
	write sample(gama.pref_date_custom_formatter);

	//Default date pattern for writing dates (i.e. string(date1))
	write sample(gama.pref_date_default_formatter);

	//Default starting date of models
	write sample(gama.pref_date_starting_date);

	//Default time step of models
	write sample(gama.pref_date_time_step);

	//Apply antialiasing
	write sample(gama.pref_display_antialias);

	//Default background color ('background' facet of 'display')
	write sample(gama.pref_display_background_color);

	//Continue to draw displays when in Modeling perspective
	write sample(gama.pref_display_continue_drawing);

	//Default rendering method (Java2D for 2D, OpenGL for 3D)
	write sample(gama.pref_display_default);

	//Default color of agents
	write sample(gama.pref_display_default_color);

	//Default font to use in 'draw'
	write sample(gama.pref_display_default_font);

	//Defaut shape of agents
	write sample(gama.pref_display_default_shape);

	//Default size of agents
	write sample(gama.pref_display_default_size);

	//Enable fast snapshots (uncomplete when the display is obscured by others but much faster)
	write sample(gama.pref_display_fast_snapshot);

	//Display 'flat' histograms
	write sample(gama.pref_display_flat_charts);

	//Default highlight color
	write sample(gama.pref_display_highlight_color);

	//Default line width (facet 'width' of 'draw')
	write sample(gama.pref_display_line_width);

	//Max. number of frames per second
	write sample(gama.pref_display_max_fps);

	//Keep values in memory (to save them as CSV)
	write sample(gama.pref_display_memorize_charts);

	//Disable acceleration for Java2D (necessary on some configurations)
	write sample(gama.pref_display_no_java2d_acceleration);

	//Forces textures dimensions to a power of 2 (e.g. 16x16. Necessary on some configurations)
	write sample(gama.pref_display_power_of_2);

	//Display a border around display views
	write sample(gama.pref_display_show_border);

	//Show errors thrown in displays and outputs
	write sample(gama.pref_display_show_errors);

	//Show the display bottom overlay
	write sample(gama.pref_display_show_overlay);

	//Draw 3D axes
	write sample(gama.pref_display_show_referential);

	//Draw rotation axes
	write sample(gama.pref_display_show_rotation);

	//Show the display top toolbar
	write sample(gama.pref_display_show_toolbar);

	//Number of slices of circular geometries
	write sample(gama.pref_display_slice_number);

	//Synchronize outputs with the simulation
	write sample(gama.pref_display_synchronized);

	//Use OpenGL tesselator (false is more precise, but more CPU intensive)
	write sample(gama.pref_display_triangulator);

	//Default layout of display views
	write sample(gama.pref_display_view_layout);

	//Only display visible agents (faster, may create visual oddities)
	write sample(gama.pref_display_visible_agents);

	//Set the zoom factor (0 for slow, 1 fast)
	write sample(gama.pref_display_zoom_factor);

	//Text color of errors
	write sample(gama.pref_error_text_color);

	//Show execution errors
	write sample(gama.pref_errors_display);

	//Number of errors to display
	write sample(gama.pref_errors_number);

	//Display most recent first
	write sample(gama.pref_errors_recent_first);

	//Stop simulation at first error
	write sample(gama.pref_errors_stop);

	//Treat warnings as errors
	write sample(gama.pref_errors_warnings_errors);

	//Ask to close the previous experiment when launching a new one
	write sample(gama.pref_experiment_ask_closing);

	//Auto-run experiments when they are launched
	write sample(gama.pref_experiment_auto_run);

	//Only display (in the UI and in headless runs) failed and aborted tests
	write sample(gama.pref_failed_tests);

	//Let GAMA decide which CRS to use to project GIS data
	write sample(gama.pref_gis_auto_crs);

	//...or use the following CRS (EPSG code)
	write sample(gama.pref_gis_default_crs);

	//...or use the following CRS (EPSG code)
	write sample(gama.pref_gis_initial_crs);

	//... or use this following CRS (EPSG code)
	write sample(gama.pref_gis_output_crs);

	//When no .prj file or CRS is supplied, consider GIS data to be already projected in this CRS
	write sample(gama.pref_gis_same_crs);

	//When no CRS is provided, save the GIS data with the current CRS
	write sample(gama.pref_gis_save_crs);

	//Connection timeout (in ms)
	write sample(gama.pref_http_connect_timeout);

	//Empty the local cache of files downloaded from the web
	write sample(gama.pref_http_empty_cache);

	//Read timeout (in ms)
	write sample(gama.pref_http_read_timeout);

	//Number of times to retry if connection cannot be established
	write sample(gama.pref_http_retry_number);

	//Path to JRI library ($R_HOME/library/rJava/jri/libjri.jnilib) (http://www.r-project.org)
	write sample(gama.pref_lib_r);

	//Path to Spatialite library (http://www.gaia-gis.it/gaia-sins/)
	write sample(gama.pref_lib_spatialite);

	//If true, when running out of memory, GAMA will try to close the experiment, otherwise it exits
	write sample(gama.pref_memory_action);

	//Interval (in seconds) at which memory should be monitored
	write sample(gama.pref_memory_frequency);

	//Trigger warnings when the percentage of available memory is below
	write sample(gama.pref_memory_threshold);

	//Optimize agents memory
	write sample(gama.pref_optimize_agent_memory);

	//Optimize the 'at_distance' operator
	write sample(gama.pref_optimize_at_distance);

	//Optimize constant expressions (experimental)
	write sample(gama.pref_optimize_constant_expressions);

	//Optimize the path computation operators and goto action (but with possible 'jump' issues)
	write sample(gama.pref_optimize_path_computation);

	//Optimize spatial queries: add agents only when necessary in the quadtree (still experimental)
	write sample(gama.pref_optimize_quadtree);

	//Make grids schedule their agents in parallel
	write sample(gama.pref_parallel_grids);

	//Make experiments run simulations in parallel
	write sample(gama.pref_parallel_simulations);

	//Make species schedule their agents in parallel
	write sample(gama.pref_parallel_species);

	//Max. number of threads to use (available processors: 8)
	write sample(gama.pref_parallel_threads);

	//Number under which agents are executed sequentially
	write sample(gama.pref_parallel_threshold);

	//Tolerance for the comparison of points
	write sample(gama.pref_point_tolerance);

	//Default seed value (0 is undefined)
	write sample(gama.pref_rng_default_seed);

	//Define a default seed
	write sample(gama.pref_rng_define_seed);

	//Include in the parameters
	write sample(gama.pref_rng_in_parameters);

	//Default random number generator
	write sample(gama.pref_rng_name);

	//Color of Simulation 0 in the UI (console, view tabs) 
	write sample(gama.pref_simulation_color_0);

	//Color of Simulation 1 in the UI (console, view tabs) 
	write sample(gama.pref_simulation_color_1);

	//Color of Simulation 2 in the UI (console, view tabs) 
	write sample(gama.pref_simulation_color_2);

	//Color of Simulation 3 in the UI (console, view tabs) 
	write sample(gama.pref_simulation_color_3);

	//Color of Simulation 4 in the UI (console, view tabs) 
	write sample(gama.pref_simulation_color_4);

	//Text color of warnings
	write sample(gama.pref_warning_text_color);

}
}


experiment 'Set Preferences' type: gui {
init {
	//Append the name of simulations to their outputs
	gama.pref_append_simulation_name <- false;

	//Display grid lines
	gama.pref_chart_display_gridlines <- true;

	//Monitor memory and emit a warning if it is low
	gama.pref_check_memory <- true;

	//Max. number of characters to keep when paused (-1 = unlimited)
	gama.pref_console_buffer <- 20000;

	//Max. number of characters to display (-1 = unlimited)
	gama.pref_console_size <- 20000;

	//Wrap long lines (can slow down output)
	gama.pref_console_wrap <- false;

	//Custom date pattern (https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns)
	gama.pref_date_custom_formatter <- 'yyyy-MM-dd HH:mm:ss';

	//Default date pattern for writing dates (i.e. string(date1))
	gama.pref_date_default_formatter <- 'CUSTOM';

	//Default starting date of models
	gama.pref_date_starting_date <- date ('1970-01-01 07:00:00');

	//Default time step of models
	gama.pref_date_time_step <- 1.0;

	//Apply antialiasing
	gama.pref_display_antialias <- false;

	//Default background color ('background' facet of 'display')
	gama.pref_display_background_color <- °white;

	//Continue to draw displays when in Modeling perspective
	gama.pref_display_continue_drawing <- false;

	//Default rendering method (Java2D for 2D, OpenGL for 3D)
	gama.pref_display_default <- 'Java2D';

	//Default color of agents
	gama.pref_display_default_color <- °yellow;

	//Default font to use in 'draw'
	gama.pref_display_default_font <- font('Helvetica',12.0,#plain);

	//Defaut shape of agents
	gama.pref_display_default_shape <- 'shape';

	//Default size of agents
	gama.pref_display_default_size <- 1.0;

	//Enable fast snapshots (uncomplete when the display is obscured by others but much faster)
	gama.pref_display_fast_snapshot <- false;

	//Display 'flat' histograms
	gama.pref_display_flat_charts <- false;

	//Default highlight color
	gama.pref_display_highlight_color <- rgb (0, 200, 200,255);

	//Default line width (facet 'width' of 'draw')
	gama.pref_display_line_width <- 1.2;

	//Max. number of frames per second
	gama.pref_display_max_fps <- 20;

	//Keep values in memory (to save them as CSV)
	gama.pref_display_memorize_charts <- true;

	//Disable acceleration for Java2D (necessary on some configurations)
	gama.pref_display_no_java2d_acceleration <- false;

	//Forces textures dimensions to a power of 2 (e.g. 16x16. Necessary on some configurations)
	gama.pref_display_power_of_2 <- false;

	//Display a border around display views
	gama.pref_display_show_border <- false;

	//Show errors thrown in displays and outputs
	gama.pref_display_show_errors <- false;

	//Show the display bottom overlay
	gama.pref_display_show_overlay <- false;

	//Draw 3D axes
	gama.pref_display_show_referential <- true;

	//Draw rotation axes
	gama.pref_display_show_rotation <- true;

	//Show the display top toolbar
	gama.pref_display_show_toolbar <- true;

	//Number of slices of circular geometries
	gama.pref_display_slice_number <- 16;

	//Synchronize outputs with the simulation
	gama.pref_display_synchronized <- false;

	//Use OpenGL tesselator (false is more precise, but more CPU intensive)
	gama.pref_display_triangulator <- true;

	//Default layout of display views
	gama.pref_display_view_layout <- 'None';

	//Only display visible agents (faster, may create visual oddities)
	gama.pref_display_visible_agents <- false;

	//Set the zoom factor (0 for slow, 1 fast)
	gama.pref_display_zoom_factor <- 0.5;

	//Text color of errors
	gama.pref_error_text_color <- rgb (210, 155, 156,255);

	//Show execution errors
	gama.pref_errors_display <- true;

	//Number of errors to display
	gama.pref_errors_number <- 10;

	//Display most recent first
	gama.pref_errors_recent_first <- true;

	//Stop simulation at first error
	gama.pref_errors_stop <- true;

	//Treat warnings as errors
	gama.pref_errors_warnings_errors <- false;

	//Ask to close the previous experiment when launching a new one
	gama.pref_experiment_ask_closing <- true;

	//Auto-run experiments when they are launched
	gama.pref_experiment_auto_run <- false;

	//Only display (in the UI and in headless runs) failed and aborted tests
	gama.pref_failed_tests <- false;

	//Let GAMA decide which CRS to use to project GIS data
	gama.pref_gis_auto_crs <- true;

	//...or use the following CRS (EPSG code)
	gama.pref_gis_default_crs <- 32648;

	//...or use the following CRS (EPSG code)
	gama.pref_gis_initial_crs <- 4326;

	//... or use this following CRS (EPSG code)
	gama.pref_gis_output_crs <- 4326;

	//When no .prj file or CRS is supplied, consider GIS data to be already projected in this CRS
	gama.pref_gis_same_crs <- true;

	//When no CRS is provided, save the GIS data with the current CRS
	gama.pref_gis_save_crs <- true;

	//Connection timeout (in ms)
	gama.pref_http_connect_timeout <- 20000;

	//Empty the local cache of files downloaded from the web
	gama.pref_http_empty_cache <- false;

	//Read timeout (in ms)
	gama.pref_http_read_timeout <- 20000;

	//Number of times to retry if connection cannot be established
	gama.pref_http_retry_number <- 3;

	//Path to JRI library ($R_HOME/library/rJava/jri/libjri.jnilib) (http://www.r-project.org)
	gama.pref_lib_r <- file('/Library/Frameworks/R.framework/Resources/library/rJava/jri/libjri.jnilib');

	//Path to Spatialite library (http://www.gaia-gis.it/gaia-sins/)
	gama.pref_lib_spatialite <- file('/Users/drogoul/Documents/Git/gama/msi.gama.models/models/Features/Data Importation/models/Enter path');

	//If true, when running out of memory, GAMA will try to close the experiment, otherwise it exits
	gama.pref_memory_action <- true;

	//Interval (in seconds) at which memory should be monitored
	gama.pref_memory_frequency <- 2;

	//Trigger warnings when the percentage of available memory is below
	gama.pref_memory_threshold <- 20;

	//Optimize agents memory
	gama.pref_optimize_agent_memory <- true;

	//Optimize the 'at_distance' operator
	gama.pref_optimize_at_distance <- true;

	//Optimize constant expressions (experimental)
	gama.pref_optimize_constant_expressions <- false;

	//Optimize the path computation operators and goto action (but with possible 'jump' issues)
	gama.pref_optimize_path_computation <- false;

	//Optimize spatial queries: add agents only when necessary in the quadtree (still experimental)
	gama.pref_optimize_quadtree <- false;

	//Make grids schedule their agents in parallel
	gama.pref_parallel_grids <- false;

	//Make experiments run simulations in parallel
	gama.pref_parallel_simulations <- true;

	//Make species schedule their agents in parallel
	gama.pref_parallel_species <- false;

	//Max. number of threads to use (available processors: 8)
	gama.pref_parallel_threads <- 4;

	//Number under which agents are executed sequentially
	gama.pref_parallel_threshold <- 20;

	//Tolerance for the comparison of points
	gama.pref_point_tolerance <- 0.0;

	//Default seed value (0 is undefined)
	gama.pref_rng_default_seed <- 1.0;

	//Define a default seed
	gama.pref_rng_define_seed <- false;

	//Include in the parameters
	gama.pref_rng_in_parameters <- false;

	//Default random number generator
	gama.pref_rng_name <- 'mersenne';

	//Color of Simulation 0 in the UI (console, view tabs) 
	gama.pref_simulation_color_0 <- rgb (74, 97, 144,255);

	//Color of Simulation 1 in the UI (console, view tabs) 
	gama.pref_simulation_color_1 <- rgb (66, 119, 42,255);

	//Color of Simulation 2 in the UI (console, view tabs) 
	gama.pref_simulation_color_2 <- rgb (83, 95, 107,255);

	//Color of Simulation 3 in the UI (console, view tabs) 
	gama.pref_simulation_color_3 <- rgb (195, 98, 43,255);

	//Color of Simulation 4 in the UI (console, view tabs) 
	gama.pref_simulation_color_4 <- rgb (150, 132, 106,255);

	//Text color of warnings
	gama.pref_warning_text_color <- rgb (255, 201, 162,255);

}
}
