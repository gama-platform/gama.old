// GAMA 1.8 Preferences saved on 2018-07-25T21:28:42.549

model preferences

experiment 'Display Preferences' type: gui {
init {
	//Display welcome page
	write sample(gama.pref_show_welcome_page);

	//Maximize GAMA window
	write sample(gama.pref_show_maximized);

	//Break down agents in menus every
	write sample(gama.pref_menu_size);

	//Max. number of characters to display (-1 = unlimited)
	write sample(gama.pref_console_size);

	//Max. number of characters to keep when paused (-1 = unlimited)
	write sample(gama.pref_console_buffer);

	//Wrap long lines (can slow down output)
	write sample(gama.pref_console_wrap);

	//Append the name of simulations to their outputs
	write sample(gama.pref_append_simulation_name);

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

	//Switch to modeling perspective when editing a model
	write sample(gama.pref_switch_perspective);

	//Show warning markers in the editor
	write sample(gama.pref_editor_enable_warnings);

	//Show information markers in the editor
	write sample(gama.pref_editor_enable_infos);

	//Auto-run experiments when they are launched
	write sample(gama.pref_experiment_auto_run);

	//Ask to close the previous experiment when launching a new one
	write sample(gama.pref_experiment_ask_closing);

	//Default step for the delay slider (in sec.)
	write sample(gama.pref_experiment_default_step);

	//Synchronize outputs with the simulation
	write sample(gama.pref_display_synchronized);

	//Sorts the results of tests by severity
	write sample(gama.pref_tests_sorted);

	//Run tests after each update of the platform
	write sample(gama.pref_run_tests);

	//Run tests at each start of the platform
	write sample(gama.pref_start_tests);

	//Include user-defined tests in the tests suite
	write sample(gama.pref_user_tests);

	//Only display (in the UI and in headless runs) failed and aborted tests
	write sample(gama.pref_failed_tests);

	//Show execution errors
	write sample(gama.pref_errors_display);

	//Show errors thrown in displays and outputs
	write sample(gama.pref_display_show_errors);

	//Number of errors to display
	write sample(gama.pref_errors_number);

	//Display most recent first
	write sample(gama.pref_errors_recent_first);

	//Stop simulation at first error
	write sample(gama.pref_errors_stop);

	//Treat warnings as errors
	write sample(gama.pref_errors_warnings_errors);

	//Default layout of display views
	write sample(gama.pref_display_view_layout);

	//Stack displays in the order defined in the model
	write sample(gama.pref_display_same_order);

	//Delay in ms between the opening of views (increase to avoid freezes of Java2D displays)
	write sample(gama.pref_display_delay_views);

	//Display a border around display views
	write sample(gama.pref_display_show_border);

	//Continue to draw displays when in Modeling perspective
	write sample(gama.pref_display_continue_drawing);

	//Use the native mode for full-screen (experimental)
	write sample(gama.pref_display_fullscreen_native);

	//Disable the OS menu bar when a display is full-screen
	write sample(gama.pref_display_fullscreen_menu);

	//Show the toolbar when a display is full-screen
	write sample(gama.pref_display_fullscreen_toolbar);

	//Enable fast snapshots (uncomplete when the display is obscured by others but much faster)
	write sample(gama.pref_display_fast_snapshot);

	//Show the display top toolbar
	write sample(gama.pref_display_show_toolbar);

	//Show the display bottom overlay
	write sample(gama.pref_display_show_overlay);

	//Show scale bar
	write sample(gama.pref_display_show_scale);

	//Show number of frames per second
	write sample(gama.pref_display_show_fps);

	//Display 'flat' histograms
	write sample(gama.pref_display_flat_charts);

	//Keep values in memory (to save them as CSV)
	write sample(gama.pref_display_memorize_charts);

	//Display grid lines
	write sample(gama.pref_chart_display_gridlines);

	//Default rendering method (Java2D for 2D, OpenGL for 3D)
	write sample(gama.pref_display_default);

	//Apply antialiasing
	write sample(gama.pref_display_antialias);

	//Default background color ('background' facet of 'display')
	write sample(gama.pref_display_background_color);

	//Default highlight color
	write sample(gama.pref_display_highlight_color);

	//Defaut shape of agents
	write sample(gama.pref_display_default_shape);

	//Default size of agents
	write sample(gama.pref_display_default_size);

	//Default color of agents
	write sample(gama.pref_display_default_color);

	//Only display visible agents (faster, may create visual oddities)
	write sample(gama.pref_display_visible_agents);

	//Disable acceleration for Java2D (necessary on some configurations)
	write sample(gama.pref_display_no_java2d_acceleration);

	//Draw 3D axes
	write sample(gama.pref_display_show_referential);

	//Draw rotation axes
	write sample(gama.pref_display_show_rotation);

	//Default line width (facet 'width' of 'draw')
	write sample(gama.pref_display_line_width);

	//Draw only the 'external' faces of objects
	write sample(gama.pref_display_visible_faces);

	//Number of slices of circular geometries
	write sample(gama.pref_display_slice_number);

	//Set the zoom factor (0 for slow, 1 fast)
	write sample(gama.pref_display_zoom_factor);

	//Max. number of frames per second
	write sample(gama.pref_display_max_fps);

	//Forces textures dimensions to a power of 2 (e.g. 16x16. Necessary on some configurations)
	write sample(gama.pref_display_power_of_2);

	//Use OpenGL tesselator (false is more precise, but more CPU intensive)
	write sample(gama.pref_display_triangulator);

	//Default random number generator
	write sample(gama.pref_rng_name);

	//Define a default seed
	write sample(gama.pref_rng_define_seed);

	//Default seed value (0 is undefined)
	write sample(gama.pref_rng_default_seed);

	//Include in the parameters
	write sample(gama.pref_rng_in_parameters);

	//Optimize constant expressions (experimental)
	write sample(gama.pref_optimize_constant_expressions);

	//Optimize agents memory
	write sample(gama.pref_optimize_agent_memory);

	//Use faster (but less accurate) arithmetic functions
	write sample(gama.pref_optimize_math_functions);

	//Optimize the 'at_distance' operator
	write sample(gama.pref_optimize_at_distance);

	//Optimize the path computation operators and goto action (but with possible 'jump' issues)
	write sample(gama.pref_optimize_path_computation);

	//Optimize spatial queries: add agents only when necessary in the quadtree (still experimental)
	write sample(gama.pref_optimize_quadtree);

	//Tolerance for the comparison of points
	write sample(gama.pref_point_tolerance);

	//Path to Spatialite library (http://www.gaia-gis.it/gaia-sins/)
	write sample(gama.pref_lib_spatialite);

	//Path to JRI library ($R_HOME/library/rJava/jri/libjri.jnilib) (http://www.r-project.org)
	write sample(gama.pref_lib_r);

	//Let GAMA decide which CRS to use to project GIS data
	write sample(gama.pref_gis_auto_crs);

	//When no .prj file or CRS is supplied, consider GIS data to be already projected in this CRS
	write sample(gama.pref_gis_same_crs);

	//When no CRS is provided, save the GIS data with the current CRS
	write sample(gama.pref_gis_save_crs);

	//...or use the following CRS (EPSG code)
	write sample(gama.pref_gis_default_crs);

	//...or use the following CRS (EPSG code)
	write sample(gama.pref_gis_initial_crs);

	//... or use this following CRS (EPSG code)
	write sample(gama.pref_gis_output_crs);

	//Default font to use in 'draw'
	write sample(gama.pref_display_default_font);

	//Make experiments run simulations in parallel
	write sample(gama.pref_parallel_simulations);

	//Make grids schedule their agents in parallel
	write sample(gama.pref_parallel_grids);

	//Make species schedule their agents in parallel
	write sample(gama.pref_parallel_species);

	//Number under which agents are executed sequentially
	write sample(gama.pref_parallel_threshold);

	//Max. number of threads to use (available processors: 8)
	write sample(gama.pref_parallel_threads);

	//Custom date pattern (https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns)
	write sample(gama.pref_date_custom_formatter);

	//Default date pattern for writing dates (i.e. string(date1))
	write sample(gama.pref_date_default_formatter);

	//Default starting date of models
	write sample(gama.pref_date_starting_date);

	//Default time step of models
	write sample(gama.pref_date_time_step);

	//Shapefile viewer fill color
	write sample(gama.pref_shapefile_background_color);

	//Shapefile viewer line color
	write sample(gama.pref_shapefile_line_color);

	//Text color of errors
	write sample(gama.pref_error_text_color);

	//Text color of warnings
	write sample(gama.pref_warning_text_color);

	//Image viewer background color
	write sample(gama.pref_image_background_color);

	//Font of buttons and dialogs
	write sample(gama.pref_button_font);

	//Sort colors menu by
	write sample(gama.pref_menu_colors_sort);

	//Reverse order
	write sample(gama.pref_menu_colors_reverse);

	//Group colors
	write sample(gama.pref_menu_colors_group);

	//Display metadata in navigator
	write sample(gama.pref_navigator_display_metadata);

	//Save all editors when switching perspectives
	write sample(gama.pref_editor_perspective_save);

	//Maintain the state of the navigator across sessions
	write sample(gama.pref_keep_navigator_state);

	//Reduce the height of views' toolbars
	write sample(gama.pref_view_toolbar_height);

	//Enable faster validation (but less accurate error reporting in nagivator)
	write sample(gama.pref_optimize_fast_compilation);

	//Mark occurrences of symbols
	write sample(gama.pref_editor_mark_occurrences);

	//Sort operators menu by
	write sample(gama.pref_menu_operators_sort);

	//Close curly brackets ( { )
	write sample(gama.pref_editor_close_curly);

	//Close square brackets ( [ )
	write sample(gama.pref_editor_close_square);

	//Close parentheses
	write sample(gama.pref_editor_close_parentheses);

	//Apply formatting on save
	write sample(gama.pref_editor_save_format);

	//Save all editors before lauching an experiment
	write sample(gama.pref_editor_save_all);

	//Drag files and resources as references in GAML files
	write sample(gama.pref_editor_drag_resources);

	//Ask before saving each file
	write sample(gama.pref_editor_ask_save);

	//Turn on colorization of code sections
	write sample(gama.pref_editor_editbox_on);

	//Font of editors
	write sample(gama.pref_editor_font);

	//Background color of editors
	write sample(gama.pref_editor_background_color);

	//Statement keywords font
	write sample(gama.pref_keyword_font);

	//Punctuation characters font
	write sample(gama.pref_punctuation_font);

	//Operators & action calls font
	write sample(gama.pref_binary_font);

	//Reserved symbols font
	write sample(gama.pref_reserved_font);

	//Comments font
	write sample(gama.pref_comment_font);

	//Strings font
	write sample(gama.pref_string_font);

	//Literal constants font
	write sample(gama.pref_number_font);

	//Default font
	write sample(gama.pref_default_font);

	//Facet keys font
	write sample(gama.pref_facet_font);

	//Variables used in expressions font
	write sample(gama.pref_variable_font);

	//Variables definitions font
	write sample(gama.pref_varDef_font);

	//Type font
	write sample(gama.pref_typeDef_font);

	//Assignment signs font
	write sample(gama.pref_assignment_font);

	//Unit names font
	write sample(gama.pref_unit_font);

	//Tasks font
	write sample(gama.pref_task_font);

	//Pragma font
	write sample(gama.pref_pragma_font);

	//Statement keywords color
	write sample(gama.pref_keyword_color);

	//Punctuation characters color
	write sample(gama.pref_punctuation_color);

	//Operators & action calls color
	write sample(gama.pref_binary_color);

	//Reserved symbols color
	write sample(gama.pref_reserved_color);

	//Comments color
	write sample(gama.pref_comment_color);

	//Strings color
	write sample(gama.pref_string_color);

	//Literal constants color
	write sample(gama.pref_number_color);

	//Default color
	write sample(gama.pref_default_color);

	//Facet keys color
	write sample(gama.pref_facet_color);

	//Variables used in expressions color
	write sample(gama.pref_variable_color);

	//Variables definitions color
	write sample(gama.pref_varDef_color);

	//Type color
	write sample(gama.pref_typeDef_color);

	//Assignment signs color
	write sample(gama.pref_assignment_color);

	//Unit names color
	write sample(gama.pref_unit_color);

	//Tasks color
	write sample(gama.pref_task_color);

	//Pragma color
	write sample(gama.pref_pragma_color);

	//Location of the preferences dialog on screen
	write sample(gama.dialog_location);

	//Size of the preferences dialog on screen
	write sample(gama.dialog_size);

	//Tab selected in the preferences dialog
	write sample(gama.dialog_tab);

}
}


experiment 'Set Preferences' type: gui {
init {
	//Display welcome page
	gama.pref_show_welcome_page <- false;

	//Maximize GAMA window
	gama.pref_show_maximized <- true;

	//Break down agents in menus every
	gama.pref_menu_size <- 50;

	//Max. number of characters to display (-1 = unlimited)
	gama.pref_console_size <- 20000;

	//Max. number of characters to keep when paused (-1 = unlimited)
	gama.pref_console_buffer <- 20000;

	//Wrap long lines (can slow down output)
	gama.pref_console_wrap <- false;

	//Append the name of simulations to their outputs
	gama.pref_append_simulation_name <- false;

	//Color of Simulation 0 in the UI (console, view tabs) 
	gama.pref_simulation_color_0 <- °navy;

	//Color of Simulation 1 in the UI (console, view tabs) 
	gama.pref_simulation_color_1 <- rgb (66, 119, 42,255);

	//Color of Simulation 2 in the UI (console, view tabs) 
	gama.pref_simulation_color_2 <- rgb (83, 95, 107,255);

	//Color of Simulation 3 in the UI (console, view tabs) 
	gama.pref_simulation_color_3 <- rgb (195, 98, 43,255);

	//Color of Simulation 4 in the UI (console, view tabs) 
	gama.pref_simulation_color_4 <- rgb (150, 132, 106,255);

	//Switch to modeling perspective when editing a model
	gama.pref_switch_perspective <- false;

	//Show warning markers in the editor
	gama.pref_editor_enable_warnings <- true;

	//Show information markers in the editor
	gama.pref_editor_enable_infos <- true;

	//Auto-run experiments when they are launched
	gama.pref_experiment_auto_run <- false;

	//Ask to close the previous experiment when launching a new one
	gama.pref_experiment_ask_closing <- true;

	//Default step for the delay slider (in sec.)
	gama.pref_experiment_default_step <- 0.01;

	//Synchronize outputs with the simulation
	gama.pref_display_synchronized <- false;

	//Sorts the results of tests by severity
	gama.pref_tests_sorted <- false;

	//Run tests after each update of the platform
	gama.pref_run_tests <- false;

	//Run tests at each start of the platform
	gama.pref_start_tests <- false;

	//Include user-defined tests in the tests suite
	gama.pref_user_tests <- false;

	//Only display (in the UI and in headless runs) failed and aborted tests
	gama.pref_failed_tests <- false;

	//Show execution errors
	gama.pref_errors_display <- true;

	//Show errors thrown in displays and outputs
	gama.pref_display_show_errors <- false;

	//Number of errors to display
	gama.pref_errors_number <- 10;

	//Display most recent first
	gama.pref_errors_recent_first <- true;

	//Stop simulation at first error
	gama.pref_errors_stop <- true;

	//Treat warnings as errors
	gama.pref_errors_warnings_errors <- false;

	//Default layout of display views
	gama.pref_display_view_layout <- 'None';

	//Stack displays in the order defined in the model
	gama.pref_display_same_order <- true;

	//Delay in ms between the opening of views (increase to avoid freezes of Java2D displays)
	gama.pref_display_delay_views <- 986;

	//Display a border around display views
	gama.pref_display_show_border <- false;

	//Continue to draw displays when in Modeling perspective
	gama.pref_display_continue_drawing <- false;

	//Use the native mode for full-screen (experimental)
	gama.pref_display_fullscreen_native <- false;

	//Disable the OS menu bar when a display is full-screen
	gama.pref_display_fullscreen_menu <- false;

	//Show the toolbar when a display is full-screen
	gama.pref_display_fullscreen_toolbar <- false;

	//Enable fast snapshots (uncomplete when the display is obscured by others but much faster)
	gama.pref_display_fast_snapshot <- false;

	//Show the display top toolbar
	gama.pref_display_show_toolbar <- true;

	//Show the display bottom overlay
	gama.pref_display_show_overlay <- false;

	//Show scale bar
	gama.pref_display_show_scale <- false;

	//Show number of frames per second
	gama.pref_display_show_fps <- false;

	//Display 'flat' histograms
	gama.pref_display_flat_charts <- true;

	//Keep values in memory (to save them as CSV)
	gama.pref_display_memorize_charts <- false;

	//Display grid lines
	gama.pref_chart_display_gridlines <- true;

	//Default rendering method (Java2D for 2D, OpenGL for 3D)
	gama.pref_display_default <- 'Java2D';

	//Apply antialiasing
	gama.pref_display_antialias <- false;

	//Default background color ('background' facet of 'display')
	gama.pref_display_background_color <- °white;

	//Default highlight color
	gama.pref_display_highlight_color <- rgb (0, 200, 200,255);

	//Defaut shape of agents
	gama.pref_display_default_shape <- 'shape';

	//Default size of agents
	gama.pref_display_default_size <- 1.0;

	//Default color of agents
	gama.pref_display_default_color <- °yellow;

	//Only display visible agents (faster, may create visual oddities)
	gama.pref_display_visible_agents <- false;

	//Disable acceleration for Java2D (necessary on some configurations)
	gama.pref_display_no_java2d_acceleration <- false;

	//Draw 3D axes
	gama.pref_display_show_referential <- true;

	//Draw rotation axes
	gama.pref_display_show_rotation <- true;

	//Default line width (facet 'width' of 'draw')
	gama.pref_display_line_width <- 1.2;

	//Draw only the 'external' faces of objects
	gama.pref_display_visible_faces <- false;

	//Number of slices of circular geometries
	gama.pref_display_slice_number <- 16;

	//Set the zoom factor (0 for slow, 1 fast)
	gama.pref_display_zoom_factor <- 0.5;

	//Max. number of frames per second
	gama.pref_display_max_fps <- 20;

	//Forces textures dimensions to a power of 2 (e.g. 16x16. Necessary on some configurations)
	gama.pref_display_power_of_2 <- true;

	//Use OpenGL tesselator (false is more precise, but more CPU intensive)
	gama.pref_display_triangulator <- true;

	//Default random number generator
	gama.pref_rng_name <- 'mersenne';

	//Define a default seed
	gama.pref_rng_define_seed <- false;

	//Default seed value (0 is undefined)
	gama.pref_rng_default_seed <- 1.0;

	//Include in the parameters
	gama.pref_rng_in_parameters <- false;

	//Optimize constant expressions (experimental)
	gama.pref_optimize_constant_expressions <- false;

	//Optimize agents memory
	gama.pref_optimize_agent_memory <- true;

	//Use faster (but less accurate) arithmetic functions
	gama.pref_optimize_math_functions <- false;

	//Optimize the 'at_distance' operator
	gama.pref_optimize_at_distance <- true;

	//Optimize the path computation operators and goto action (but with possible 'jump' issues)
	gama.pref_optimize_path_computation <- false;

	//Optimize spatial queries: add agents only when necessary in the quadtree (still experimental)
	gama.pref_optimize_quadtree <- false;

	//Tolerance for the comparison of points
	gama.pref_point_tolerance <- 0.0;

	//Path to Spatialite library (http://www.gaia-gis.it/gaia-sins/)
	gama.pref_lib_spatialite <- file('Enter path');

	//Path to JRI library ($R_HOME/library/rJava/jri/libjri.jnilib) (http://www.r-project.org)
	gama.pref_lib_r <- file('/Library/Frameworks/R.framework/Versions/2.15/Resources/bin/exec/x86_64/RScript');

	//Let GAMA decide which CRS to use to project GIS data
	gama.pref_gis_auto_crs <- true;

	//When no .prj file or CRS is supplied, consider GIS data to be already projected in this CRS
	gama.pref_gis_same_crs <- true;

	//When no CRS is provided, save the GIS data with the current CRS
	gama.pref_gis_save_crs <- true;

	//...or use the following CRS (EPSG code)
	gama.pref_gis_default_crs <- 32648;

	//...or use the following CRS (EPSG code)
	gama.pref_gis_initial_crs <- 4326;

	//... or use this following CRS (EPSG code)
	gama.pref_gis_output_crs <- 4326;

	//Default font to use in 'draw'
	gama.pref_display_default_font <- font('Helvetica',12.0,#plain);

	//Make experiments run simulations in parallel
	gama.pref_parallel_simulations <- true;

	//Make grids schedule their agents in parallel
	gama.pref_parallel_grids <- false;

	//Make species schedule their agents in parallel
	gama.pref_parallel_species <- false;

	//Number under which agents are executed sequentially
	gama.pref_parallel_threshold <- 20;

	//Max. number of threads to use (available processors: 8)
	gama.pref_parallel_threads <- 4;

	//Custom date pattern (https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#patterns)
	gama.pref_date_custom_formatter <- 'yyyy-MM-dd HH:mm:ss';

	//Default date pattern for writing dates (i.e. string(date1))
	gama.pref_date_default_formatter <- 'CUSTOM';

	//Default starting date of models
	gama.pref_date_starting_date <- date ('1970-01-01 07:00:00');

	//Default time step of models
	gama.pref_date_time_step <- 1.0;

	//Shapefile viewer fill color
	gama.pref_shapefile_background_color <- °lightgrey;

	//Shapefile viewer line color
	gama.pref_shapefile_line_color <- °black;

	//Text color of errors
	gama.pref_error_text_color <- rgb (210, 155, 156,255);

	//Text color of warnings
	gama.pref_warning_text_color <- rgb (255, 201, 162,255);

	//Image viewer background color
	gama.pref_image_background_color <- °white;

	//Font of buttons and dialogs
	gama.pref_button_font <- font('.SF NS Text',11.0,#bold);

	//Sort colors menu by
	gama.pref_menu_colors_sort <- 'RGB value';

	//Reverse order
	gama.pref_menu_colors_reverse <- false;

	//Group colors
	gama.pref_menu_colors_group <- false;

	//Display metadata in navigator
	gama.pref_navigator_display_metadata <- true;

	//Save all editors when switching perspectives
	gama.pref_editor_perspective_save <- true;

	//Maintain the state of the navigator across sessions
	gama.pref_keep_navigator_state <- true;

	//Reduce the height of views' toolbars
	gama.pref_view_toolbar_height <- true;

	//Enable faster validation (but less accurate error reporting in nagivator)
	gama.pref_optimize_fast_compilation <- false;

	//Mark occurrences of symbols
	gama.pref_editor_mark_occurrences <- true;

	//Sort operators menu by
	gama.pref_menu_operators_sort <- 'Category';

	//Close curly brackets ( { )
	gama.pref_editor_close_curly <- true;

	//Close square brackets ( [ )
	gama.pref_editor_close_square <- true;

	//Close parentheses
	gama.pref_editor_close_parentheses <- true;

	//Apply formatting on save
	gama.pref_editor_save_format <- false;

	//Save all editors before lauching an experiment
	gama.pref_editor_save_all <- true;

	//Drag files and resources as references in GAML files
	gama.pref_editor_drag_resources <- true;

	//Ask before saving each file
	gama.pref_editor_ask_save <- false;

	//Turn on colorization of code sections
	gama.pref_editor_editbox_on <- false;

	//Font of editors
	gama.pref_editor_font <- font('Anonymous Pro',12.0,#plain);

	//Background color of editors
	gama.pref_editor_background_color <- °white;

	//Statement keywords font
	gama.pref_keyword_font <- font('Anonymous Pro',12.0,#plain);

	//Punctuation characters font
	gama.pref_punctuation_font <- font('Anonymous Pro',12.0,#plain);

	//Operators & action calls font
	gama.pref_binary_font <- font('Anonymous Pro',12.0,#plain);

	//Reserved symbols font
	gama.pref_reserved_font <- font('Anonymous Pro',12.0,#plain);

	//Comments font
	gama.pref_comment_font <- font('Anonymous Pro',12.0,#plain);

	//Strings font
	gama.pref_string_font <- font('Anonymous Pro',12.0,#plain);

	//Literal constants font
	gama.pref_number_font <- font('Anonymous Pro',12.0,#plain);

	//Default font
	gama.pref_default_font <- font('Anonymous Pro',12.0,#plain);

	//Facet keys font
	gama.pref_facet_font <- font('Arial',12.0,#plain);

	//Variables used in expressions font
	gama.pref_variable_font <- font('Anonymous Pro',12.0,#plain);

	//Variables definitions font
	gama.pref_varDef_font <- font('Anonymous Pro',12.0,#plain);

	//Type font
	gama.pref_typeDef_font <- font('Anonymous Pro',12.0,#plain);

	//Assignment signs font
	gama.pref_assignment_font <- font('Anonymous Pro',12.0,#plain);

	//Unit names font
	gama.pref_unit_font <- font('Anonymous Pro',12.0,#plain);

	//Tasks font
	gama.pref_task_font <- font('Anonymous Pro',12.0,#plain);

	//Pragma font
	gama.pref_pragma_font <- font('Anonymous Pro',12.0,#plain);

	//Statement keywords color
	gama.pref_keyword_color <- rgb (127, 0, 85,255);

	//Punctuation characters color
	gama.pref_punctuation_color <- °transparent;

	//Operators & action calls color
	gama.pref_binary_color <- rgb (46, 93, 78,255);

	//Reserved symbols color
	gama.pref_reserved_color <- °black;

	//Comments color
	gama.pref_comment_color <- rgb (63, 127, 95,255);

	//Strings color
	gama.pref_string_color <- rgb (116, 167, 251,255);

	//Literal constants color
	gama.pref_number_color <- rgb (125, 125, 125,255);

	//Default color
	gama.pref_default_color <- °transparent;

	//Facet keys color
	gama.pref_facet_color <- rgb (154, 72, 71,255);

	//Variables used in expressions color
	gama.pref_variable_color <- rgb (28, 125, 180,255);

	//Variables definitions color
	gama.pref_varDef_color <- rgb (0, 0, 153,255);

	//Type color
	gama.pref_typeDef_color <- rgb (0, 79, 116,255);

	//Assignment signs color
	gama.pref_assignment_color <- rgb (50, 50, 50,255);

	//Unit names color
	gama.pref_unit_color <- °transparent;

	//Tasks color
	gama.pref_task_color <- rgb (150, 132, 106,255);

	//Pragma color
	gama.pref_pragma_color <- rgb (122, 122, 122,255);

	//Location of the preferences dialog on screen
	gama.dialog_location <- {435.0,163.0,0.0};

	//Size of the preferences dialog on screen
	gama.dialog_size <- {1385.0,970.0,0.0};

	//Tab selected in the preferences dialog
	gama.dialog_tab <- 0;

}
}
