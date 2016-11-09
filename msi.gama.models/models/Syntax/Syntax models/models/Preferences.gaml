/**
* Name: preferences
* Author: drogoul
* Description: Shows how to gain read / write access to the preferences of the plaform. This model is generated automatically by the "Export..." command in the Preferences view. 
* When you run it, be aware that some of your preferences might be changed !
* Tags: Gama, Preferences
*/
model preferences

experiment 'Display Preferences' type: gui
{
	init
	{
	//Break down agents in menus every
		write sample(gama.pref_menu_size);

		//Max. number of characters to display in the console (-1 means no limit) 
		write sample(gama.pref_console_size);

		//Max. number of characters to keep in memory when console is paused (-1 means no limit)
		write sample(gama.pref_console_buffer);

		//Automatically wrap long lines in the console (can slow down output)
		write sample(gama.pref_console_wrap);

		//Automatically switch to modeling perspective when editing a model
		write sample(gama.pref_switch_perspective);

		//Append the name of the simulation to the title of its outputs
		write sample(gama.pref_append_simulation_name);

		//Show warning markers when editing a model
		write sample(gama.pref_editor_enable_warnings);

		//Show information markers when editing a model
		write sample(gama.pref_editor_enable_infos);

		//Random number generator
		write sample(gama.pref_rng_name);

		//Define a default seed
		write sample(gama.pref_rng_define_seed);

		//Default seed value (0 means undefined)
		write sample(gama.pref_rng_default_seed);

		//Include in the parameters of models
		write sample(gama.pref_rng_in_parameters);

		//Display errors
		write sample(gama.pref_errors_display);

		//Number of errors to display
		write sample(gama.pref_errors_number);

		//Display most recent first
		write sample(gama.pref_errors_recent_first);

		//Stop simulation at first error
		write sample(gama.pref_errors_stop);

		//Treat warnings as errors
		write sample(gama.pref_errors_warnings_errors);

		//Display Welcome page at startup
		write sample(gama.pref_show_welcome_page);

		//Maximize GAMA windows at startup
		write sample(gama.pref_show_maximized);

		//Default step for delay slider (in sec.)
		write sample(gama.pref_experiment_default_step);

		//Auto-run experiments when they are launched
		write sample(gama.pref_experiment_auto_run);

		//Ask to close the previous experiment before launching a new one ?
		write sample(gama.pref_experiment_ask_closing);

		//Color of Simulation 0
		write sample(gama.pref_simulation_color_0);

		//Color of Simulation 1
		write sample(gama.pref_simulation_color_1);

		//Color of Simulation 2
		write sample(gama.pref_simulation_color_2);

		//Color of Simulation 3
		write sample(gama.pref_simulation_color_3);

		//Color of Simulation 4
		write sample(gama.pref_simulation_color_4);

		//Continue to draw displays (in the background) when in Modeling perspective
		write sample(gama.pref_display_continue_drawing);

		//Disable the menu bar when displays are turned full-screen
		write sample(gama.pref_display_fullscreen_menu);

		//Default display method when none is specified
		write sample(gama.pref_display_default);

		//Synchronize displays with simulations
		write sample(gama.pref_display_synchronized);

		//Show display overlay
		write sample(gama.pref_display_show_overlay);

		//Show scale bar in overlay
		write sample(gama.pref_display_show_scale);

		//Show number of frames per second in overlay
		write sample(gama.pref_display_show_fps);

		//Apply antialiasing
		write sample(gama.pref_display_antialias);

		//Default background color
		write sample(gama.pref_display_background_color);

		//Default highlight color
		write sample(gama.pref_display_highlight_color);

		//Stack displays on screen in the order defined by the model
		write sample(gama.pref_display_same_order);

		//Display a border around the view
		write sample(gama.pref_display_show_border);

		//Default layout of display views
		write sample(gama.pref_display_view_layout);

		//Defaut shape to use for agents
		write sample(gama.pref_display_default_shape);

		//Default size to use for agents
		write sample(gama.pref_display_default_size);

		//Default color to use for agents
		write sample(gama.pref_display_default_color);

		//Draw 3D referential
		write sample(gama.pref_display_show_referential);

		//Draw rotation helper
		write sample(gama.pref_display_show_rotation);

		//Set the zoom factor to use (from 0 for a slow zoom to 1 for a fast one)
		write sample(gama.pref_display_zoom_factor);

		//Set the maximum number of frames per second to display
		write sample(gama.pref_display_max_fps);

		//Set the width of lines drawn on displays
		write sample(gama.pref_display_line_width);

		//Path to the Spatialite (see http://www.gaia-gis.it/gaia-sins/) library
		write sample(gama.pref_lib_spatialite);

		//Path to the RScript (see http://www.r-project.org) library
		write sample(gama.pref_lib_r);

		//Let GAMA decide which CRS to use to project GIS data
		write sample(gama.pref_gis_auto_crs);

		//...or use the following CRS (EPSG code)
		write sample(gama.pref_gis_default_crs);

		//When no .prj file or CRS is supplied, consider GIS data to be already projected in this CRS
		write sample(gama.pref_gis_same_crs);

		//...or use the following CRS (EPSG code)
		write sample(gama.pref_gis_initial_crs);

		//When no CRS is provided, save the GIS data with the current CRS
		write sample(gama.pref_gis_save_crs);

		//... or use this following CRS (EPSG code)
		write sample(gama.pref_gis_output_crs);

		//Show errors in displays and outputs
		write sample(gama.pref_display_show_errors);

		//Only process for display the agents that are visible
		write sample(gama.pref_display_visible_agents);

		//Enable OpenGL background loading of textures (can cause problems with some graphics cards on Linux and Windows)
		write sample(gama.pref_display_shared_cache);

		//Enable fast snapshots of displays (uncomplete when the display is obscured by other views but much faster than normal snapshots)
		write sample(gama.pref_display_fast_snapshot);

		//Forces the dimensions of OpenGL textures to be power of 2 (e.g. 8x8, 16x16, etc.). Necessary on some graphic cards
		write sample(gama.pref_display_power_of_2);

		//Disable graphics hardware acceleration for Java2D displays (can be necessary on some configurations)
		write sample(gama.pref_display_no_java2d_acceleration);

		//Delay (in ms) between the opening of display views (increase if you experience freezes when opening displays, esp. Java2D displays)
		write sample(gama.pref_display_delay_views);

		//Automatically optimize constant expressions
		write sample(gama.pref_optimize_constant_expressions);

		//Automatically optimize the memory used by agents
		write sample(gama.pref_optimize_agent_memory);

		//Use optimized (but less accurate) arithmetic and trigonometric functions
		write sample(gama.pref_optimize_math_functions);

		//Automatically optimize the at_distance operator
		write sample(gama.pref_optimize_at_distance);

		//Default shapefile viewer fill color
		write sample(gama.pref_shapefile_background_color);

		//Default shapefile viewer line color
		write sample(gama.pref_shapefile_line_color);

		//Text color of errors in error view
		write sample(gama.pref_error_text_color);

		//Text color of warnings in error view
		write sample(gama.pref_warning_text_color);

		//Default image viewer background color
		write sample(gama.pref_image_background_color);

		//Font of buttons (applies to new buttons)
		write sample(gama.pref_button_font);

		//Default font to use in text layers or draw statements when none is specified
		write sample(gama.pref_display_default_font);

		//Sort colors menu by
		write sample(gama.pref_menu_colors_sort);

		//Reverse order
		write sample(gama.pref_menu_colors_reverse);

		//Group colors
		write sample(gama.pref_menu_colors_group);

		//Display metadata of data and GAML files in navigator
		write sample(gama.pref_navigator_display_metadata);

		//Allow experiments to run multiple simulations in parallel
		write sample(gama.pref_parallel_simulations);

		//Make grids schedule their agents in parallel by default
		write sample(gama.pref_parallel_grids);

		//Make regular species schedule their agents in parallel by default
		write sample(gama.pref_parallel_species);

		//Number under which agents will always be executed sequentially
		write sample(gama.pref_parallel_threshold);

		//Max. number of threads to use for parallel operations (available processors: 8)
		write sample(gama.pref_parallel_threads);

		//Custom date pattern (see https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns)
		write sample(gama.pref_date_custom_formatter);

		//Default date pattern for writing dates if none is specified (i.e. string(date1))
		write sample(gama.pref_date_default_formatter);

		//Default starting date of models when it is not set
		write sample(gama.pref_date_starting_date);

		//Default time step of models when it is not set
		write sample(gama.pref_date_time_step);

		//Sort operators menu by
		write sample(gama.pref_menu_operators_sort);

		//Automatically close curly brackets ( { )
		write sample(gama.pref_editor_close_curly);

		//Automatically close square brackets ( [ )
		write sample(gama.pref_editor_close_square);

		//Automatically close parentheses
		write sample(gama.pref_editor_close_parentheses);

		//Apply formatting to models on save
		write sample(gama.pref_editor_save_format);

		//Save all model files before lauching an experiment
		write sample(gama.pref_editor_save_all);

		//Ask before saving each file
		write sample(gama.pref_editor_ask_save);

		//Turn on colorization of code sections by default
		write sample(gama.pref_editor_editbox_on);

		//Show edition toolbar by default
		write sample(gama.pref_editor_show_toolbar);

		//Font of editors
		write sample(gama.pref_editor_font);

		//Background color of editors
		write sample(gama.pref_editor_background_color);

		//Show other models' experiments in toolbar
		write sample(gama.pref_editor_other_experiments);

		//Enable faster compilation (but less accurate error reporting in nagivator)
		write sample(gama.pref_optimize_fast_compilation);

		//Mark occurrences of symbols in models
		write sample(gama.pref_editor_mark_occurrences);
	}

}

experiment 'Set Preferences' type: gui
{
	init
	{
	//Break down agents in menus every
		gama.pref_menu_size <- 50;

		//Max. number of characters to display in the console (-1 means no limit) 
		gama.pref_console_size <- 20000;

		//Max. number of characters to keep in memory when console is paused (-1 means no limit)
		gama.pref_console_buffer <- 20000;

		//Automatically wrap long lines in the console (can slow down output)
		gama.pref_console_wrap <- false;

		//Automatically switch to modeling perspective when editing a model
		gama.pref_switch_perspective <- false;

		//Append the name of the simulation to the title of its outputs
		gama.pref_append_simulation_name <- false;

		//Show warning markers when editing a model
		gama.pref_editor_enable_warnings <- true;

		//Show information markers when editing a model
		gama.pref_editor_enable_infos <- true;

		//Random number generator
		gama.pref_rng_name <- 'mersenne';

		//Define a default seed
		gama.pref_rng_define_seed <- false;

		//Default seed value (0 means undefined)
		gama.pref_rng_default_seed <- 1.0;

		//Include in the parameters of models
		gama.pref_rng_in_parameters <- true;

		//Display errors
		gama.pref_errors_display <- true;

		//Number of errors to display
		gama.pref_errors_number <- 10;

		//Display most recent first
		gama.pref_errors_recent_first <- true;

		//Stop simulation at first error
		gama.pref_errors_stop <- true;

		//Treat warnings as errors
		gama.pref_errors_warnings_errors <- false;

		//Display Welcome page at startup
		gama.pref_show_welcome_page <- true;

		//Maximize GAMA windows at startup
		gama.pref_show_maximized <- true;

		//Default step for delay slider (in sec.)
		gama.pref_experiment_default_step <- 0.01;

		//Auto-run experiments when they are launched
		gama.pref_experiment_auto_run <- false;

		//Ask to close the previous experiment before launching a new one ?
		gama.pref_experiment_ask_closing <- true;

		//Color of Simulation 0
		gama.pref_simulation_color_0 <- rgb(74, 97, 144, 255);

		//Color of Simulation 1
		gama.pref_simulation_color_1 <- rgb(66, 119, 42, 255);

		//Color of Simulation 2
		gama.pref_simulation_color_2 <- rgb(83, 95, 107, 255);

		//Color of Simulation 3
		gama.pref_simulation_color_3 <- rgb(195, 98, 43, 255);

		//Color of Simulation 4
		gama.pref_simulation_color_4 <- rgb(150, 132, 106, 255);

		//Continue to draw displays (in the background) when in Modeling perspective
		gama.pref_display_continue_drawing <- false;

		//Disable the menu bar when displays are turned full-screen
		gama.pref_display_fullscreen_menu <- true;

		//Default display method when none is specified
		gama.pref_display_default <- 'Java2D';

		//Synchronize displays with simulations
		gama.pref_display_synchronized <- false;

		//Show display overlay
		gama.pref_display_show_overlay <- false;

		//Show scale bar in overlay
		gama.pref_display_show_scale <- false;

		//Show number of frames per second in overlay
		gama.pref_display_show_fps <- false;

		//Apply antialiasing
		gama.pref_display_antialias <- false;

		//Default background color
		gama.pref_display_background_color <- #white;

		//Default highlight color
		gama.pref_display_highlight_color <- rgb(0, 200, 200, 255);

		//Stack displays on screen in the order defined by the model
		gama.pref_display_same_order <- true;

		//Display a border around the view
		gama.pref_display_show_border <- true;

		//Default layout of display views
		gama.pref_display_view_layout <- 'None';

		//Defaut shape to use for agents
		gama.pref_display_default_shape <- 'shape';

		//Default size to use for agents
		gama.pref_display_default_size <- 1.0;

		//Default color to use for agents
		gama.pref_display_default_color <- #yellow;

		//Draw 3D referential
		gama.pref_display_show_referential <- true;

		//Draw rotation helper
		gama.pref_display_show_rotation <- true;

		//Set the zoom factor to use (from 0 for a slow zoom to 1 for a fast one)
		gama.pref_display_zoom_factor <- 0.5;

		//Set the maximum number of frames per second to display
		gama.pref_display_max_fps <- 20;

		//Set the width of lines drawn on displays
		gama.pref_display_line_width <- 1.2;

		//Path to the Spatialite (see http://www.gaia-gis.it/gaia-sins/) library
		gama.pref_lib_spatialite <- file('Enter path');

		//Path to the RScript (see http://www.r-project.org) library
		gama.pref_lib_r <- file('/Library/Frameworks/R.framework/Versions/2.15/Resources/bin/exec/x86_64/RScript');

		//Let GAMA decide which CRS to use to project GIS data
		gama.pref_gis_auto_crs <- true;

		//...or use the following CRS (EPSG code)
		gama.pref_gis_default_crs <- 32648;

		//When no .prj file or CRS is supplied, consider GIS data to be already projected in this CRS
		gama.pref_gis_same_crs <- true;

		//...or use the following CRS (EPSG code)
		gama.pref_gis_initial_crs <- 4326;

		//When no CRS is provided, save the GIS data with the current CRS
		gama.pref_gis_save_crs <- true;

		//... or use this following CRS (EPSG code)
		gama.pref_gis_output_crs <- 4326;

		//Show errors in displays and outputs
		gama.pref_display_show_errors <- false;

		//Only process for display the agents that are visible
		gama.pref_display_visible_agents <- false;

		//Enable OpenGL background loading of textures (can cause problems with some graphics cards on Linux and Windows)
		gama.pref_display_shared_cache <- false;

		//Enable fast snapshots of displays (uncomplete when the display is obscured by other views but much faster than normal snapshots)
		gama.pref_display_fast_snapshot <- false;

		//Forces the dimensions of OpenGL textures to be power of 2 (e.g. 8x8, 16x16, etc.). Necessary on some graphic cards
		gama.pref_display_power_of_2 <- false;

		//Disable graphics hardware acceleration for Java2D displays (can be necessary on some configurations)
		gama.pref_display_no_java2d_acceleration <- false;

		//Delay (in ms) between the opening of display views (increase if you experience freezes when opening displays, esp. Java2D displays)
		gama.pref_display_delay_views <- 200;

		//Automatically optimize constant expressions
		gama.pref_optimize_constant_expressions <- false;

		//Automatically optimize the memory used by agents
		gama.pref_optimize_agent_memory <- false;

		//Use optimized (but less accurate) arithmetic and trigonometric functions
		gama.pref_optimize_math_functions <- false;

		//Automatically optimize the at_distance operator
		gama.pref_optimize_at_distance <- true;

		//Default shapefile viewer fill color
		gama.pref_shapefile_background_color <- #silver;

		//Default shapefile viewer line color
		gama.pref_shapefile_line_color <- #black;

		//Text color of errors in error view
		gama.pref_error_text_color <- rgb(210, 155, 156, 255);

		//Text color of warnings in error view
		gama.pref_warning_text_color <- rgb(255, 201, 162, 255);

		//Default image viewer background color
		gama.pref_image_background_color <- #white;

		//Font of buttons (applies to new buttons)
		gama.pref_button_font <- font('.SF NS Text', 11.0, # bold);

		//Default font to use in text layers or draw statements when none is specified
		gama.pref_display_default_font <- font('Helvetica', 12.0, # plain);

		//Sort colors menu by
		gama.pref_menu_colors_sort <- 'RGB value';

		//Reverse order
		gama.pref_menu_colors_reverse <- false;

		//Group colors
		gama.pref_menu_colors_group <- false;

		//Display metadata of data and GAML files in navigator
		gama.pref_navigator_display_metadata <- true;

		//Allow experiments to run multiple simulations in parallel
		gama.pref_parallel_simulations <- true;

		//Make grids schedule their agents in parallel by default
		gama.pref_parallel_grids <- false;

		//Make regular species schedule their agents in parallel by default
		gama.pref_parallel_species <- false;

		//Number under which agents will always be executed sequentially
		gama.pref_parallel_threshold <- 20;

		//Max. number of threads to use for parallel operations (available processors: 8)
		gama.pref_parallel_threads <- 4;

		//Custom date pattern (see https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns)
		gama.pref_date_custom_formatter <- 'yyyy-MM-dd HH:mm:ss';

		//Default date pattern for writing dates if none is specified (i.e. string(date1))
		gama.pref_date_default_formatter <- 'CUSTOM';

		//Default starting date of models when it is not set
		gama.pref_date_starting_date <- date('1970-01-01 07:00:00');

		//Default time step of models when it is not set
		gama.pref_date_time_step <- 1.0;

		//Sort operators menu by
		gama.pref_menu_operators_sort <- 'Category';

		//Automatically close curly brackets ( { )
		gama.pref_editor_close_curly <- true;

		//Automatically close square brackets ( [ )
		gama.pref_editor_close_square <- true;

		//Automatically close parentheses
		gama.pref_editor_close_parentheses <- true;

		//Apply formatting to models on save
		gama.pref_editor_save_format <- false;

		//Save all model files before lauching an experiment
		gama.pref_editor_save_all <- true;

		//Ask before saving each file
		gama.pref_editor_ask_save <- false;

		//Turn on colorization of code sections by default
		gama.pref_editor_editbox_on <- false;

		//Show edition toolbar by default
		gama.pref_editor_show_toolbar <- true;

		//Font of editors
		gama.pref_editor_font <- font('.SF NS Text', 11.0, # plain);

		//Background color of editors
		gama.pref_editor_background_color <- rgb(255, 255, 255, 255);

		//Show other models' experiments in toolbar
		gama.pref_editor_other_experiments <- false;

		//Enable faster compilation (but less accurate error reporting in nagivator)
		gama.pref_optimize_fast_compilation <- false;

		//Mark occurrences of symbols in models
		gama.pref_editor_mark_occurrences <- true;
	}

}

