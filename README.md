# GAMA 1.8.1
[![Build Status](https://travis-ci.org/gama-platform/gama.svg?branch=master)](https://travis-ci.org/gama-platform/gama)
[![Language](http://img.shields.io/badge/language-java-brightgreen.svg)](https://www.java.com/)
[![GitHub issues](https://img.shields.io/github/issues/gama-platform/gama.svg)](https://github.com/gama-platform/gama/issues)
[![Github Releases](https://img.shields.io/github/release/gama-platform/gama.svg)](https://github.com/gama-platform/gama/releases)
[![Documentation](https://img.shields.io/badge/documentation-web-brightgreen.svg)](https://gama-platform.github.io)
[![Documentation](https://img.shields.io/badge/documentation-pdf-brightgreen.svg)](https://github.com/gama-platform/gama/wiki/resources/pdf/docGAMAv17.pdf)

[![Sonarcloud](https://sonarcloud.io/api/project_badges/quality_gate?project=gama-platform_gamamsi.gama.parent)](https://sonarcloud.io/organizations/gama-platform/projects)

**Change log since version 1.8**
# BUG FIXES

- **Charts** - d99c03370, 5e41f06ad - #2921, correct colors in histograms and radars.
- **Displays** - 0fffc807b - #2854, takes orientation into account in the camera settings.
- **Displays** - 512942c8f - #2853, improves numbers input in the z-axis rotation field
- **Displays** - 903c64dd2 - #2897, adds a preference for copying Camera parameters.
- **Displays** - 09df6fa99 - #2971, harmonizes the meaning of the `transparency` facet
- **Displays** - 3f3563350 - #2835, take correct snapshots of zoomed displays
- **Displays** - 80fa60de2, ee1281a52 - #2851, better handles Java2D displays when simulation is added dynamically from the Parameters view
- **Displays** - c4fde1666 - #2913. #2902, better computes boolean attributes in layers.
- **Editor** - 0b5288062 - #2850, removes empty error messages from the editor
- **Editor** - a1246f7d8 - #2969, reenables correct operations of &quot;Find References...&quot;
- **Editor -** cc2b564cf - #2833, more robust highlighting of boolean operators
- **Files** - 66f73efb1 - #2860, updates the CSV metadata when edited in GAMA
- **Files** - 9a52ccf31 - #2929, replaces `:` in filenames when saving benchmark results
- **Files** - ab00eea0a - #2901, corrects `.asc` file headers created by QGis.
- **Files** - ae9efc1d7 - #2988, does not escape `/` anymore in strings
- **Files** - d6cf13ca0 – Fixes an issue with OSM data and projection
- **FSM** - 5229a7415, 3198351c7 - #2865, run the `exit` section of a state, even when it is marked as `final: true` (could happen when dying).
- **FSM** - e55831c20 - #2866, emits an error when several states are marked `final`
- **GAML** - 075b1a282, 6f40de575, 169ddb61e, 81f6d2f1c - #2869, #2874 and [COMOKIT/COMOKIT-Model/issues/21](https://github.com/COMOKIT/COMOKIT-Model/issues/21), checks dependencies between attributes
- **GAML** - 67021601e - #2922, better computes the variable scope of recursive actions
- **GAML** - 698b2f577 - #2932, provides the right context to the `agents` layer.
- **GAML** - 6f40de575 - #2875, checks the syntax of generic types more carefully
- **GAML** - 7c2c653e7 - #2873, disables the use of `at` on any type but containers
- **GAML** - a92f3b0f2 - warnings for expressions in the interactive console or monitors
- **GAML -** bd48567a4 - #2995, makes sure that `font` can be called with 2 arguments.
- **GAML** - d9a459d2c - #2910, computes constant string values used in file statements
- **GAML** - df346c9cc - #2605, sets the correct compilation and execution scopes when building attributes to save in the `save` statement
- **GAML** - e10938c14 - #2836, better serializes color constants
- **GAML** - e17800f51 - Fixes an annoying compilation bug for virtual actions
- **Geometries -** 47518a11a, 35d96ce01 – Fixes issues with `pathBetween` and `masked_by` operators
- **Goto -** 44d205977 – Make sure that the `on:` facet can accept a `species`.
- **Graphs** - 8963a3f62 - #2916, better splits some T-shaped geometries when cleaning road networks
- **Graphs** - 048512f64, 472b1891a - #2861, stops losing the geometrical information when applying layouts to graphs.
- **Graphs** - 187848edf - Fixes a bug occurring when filtering road type when creating a network and then using `all_pairs_shortest_path`
- **Graphs** - 540a5e137 - #2839, prevents an NPE in the update of a graph vertices
- **Graphs** - ba85b59f4 - #2940, removes an NPE when cleaning a road network built from OSM data using ArcMap
- **Headless** - 005d1fa4c - #2890, points to the embedded JDK in the headless scripts
- **Headless** - 0da04afdd - #2983, correctly sets `project_path` in headless mode
- **Headless** - aa74dbbf1, d4f7751d4, 47c131a29, cdba1b740 - #296, improves the robustness of the headless script and its handling of paths
- **Headless** - caaf8781c – #2976, removes virtual outputs when generating XML files
- **Hexagonal grids** - 23e0cf28f - #2894, returns the correct cell at a given location
- **Hexagonal grids** - 474cfbcd1 - #2892, allows spatial queries to operate again
- **Maths plugin** - 04af42dca - #2867, sets the value of `t`(time) after `solve` is over.
- **Maths plugin** - 468720f92 - #2868, deprecates `integrated_times` and `integrated_values` in the `solve` statement.
- **Navigator** - fd90fa5d8 - #2846, displays all types of files in &quot;Uses&quot; in the navigator.
- **Network plugin** a8d53f6f9 - #2906, adds a new attribute to the network `connect` action, in order to set the max size of packets (default is still 1024)
- **Operators** - af254f9fe - #2858, correctly documents the `-` operator for lists
- **Parameters view** - 5cdc90aa7 - #2834, allows labels of parameter to wrap.
- **Random** - 1a3b78fc7 - #2974, stops filling lists with random values in parallel
- **Random** - 7a29e0a8c, ac17bc332 - #2930 and [COMOKIT/COMOKIT-Model/issues/30](https://github.com/COMOKIT/COMOKIT-Model/issues/30), removes uses of HashSet in spatial operators and indexes
- **Save statement** - 187ece66f - #2857, allows to save one shape at a time.
- **Scheduling** - 4ffc81989 - #2927, #2933, schedules mirror species after their target species
- **Scheduling** - a1fd69ea2 - #2952, removes an NPE when using `schedules` in `global` and documents it.
- **Species browser** - d97481245 - #2852, correctly restores the attributes list in the species browser
- **Template editor** - 87d420345 - #2849, restores the ability to add templates again
- **Tests** - c61ba75c7 - #2881, builds a dynamic correspondence between class names and types
- **User input** - 89ad7c806 - #2978, makes `init:` / `\&lt;-` mandatory in `user_input`.
- **Views** - 903c64dd2 - #2856, removes the useless preference on view tabs height.
- **Wizards** - 3d94861ce - #2882, checks badly named experiments/models in wizards
- **Workbench** - d34a3d664 - #2848, prevents infinite loop when creating workspaces

# ENHANCEMENTS

- **Charts** - 3dc0d1156 - #2864, deprecates the xxx\_font\_size / xxx\_font\_style facets in chart layers and allows to use the font(...) operator instead
- **Displays** - 344894ea1 - #2127, #2380 and #2928, Improves the display of rasters
- **Displays** - 136f6e931 - #2891, synchronizes displays when one is set to autosave.
- **Displays** - 76a2902f4 - #1918, Addition of `mouse_menu` (ctrl-click) for event layers
- **Displays** - ed233b5d9 – Allows to display graphics w/o respecting world proportions
- **Documentation** - 94ad24936 – Encodes the docGAMA.xml in UTF-8
- **Files** - 831688854 - #2830, #2870, improves the handling of complex CSV files by adding 2 new constructors to `csv_file`.
- **Files** - 86a64de67, 9429902f9 – Adds an understandable error message for DXF files when the unit is less than or equal to 0. – improves their parsing (elliptical arc)
- **Files** - d21eaac44 –#2939, adds the new `folder_exists` operator.
- **GAML** - 2b5705d0e - #2883, documents the fact that `model:` needs to be the first facet of `experiment`.
- **GAML** - 57e07c2a4 –#2931, emits a warning when comparing different types
- **GAML** - 6310cb8df - #2872, enhanced information on the redefinition of reflexes and the order in which they will be executed
- **GAML** - 834da4446 – Transforms `#min_int` and `#max_int` into... `int` constants.
- **Geometries** - 984fb5c5e - Improves the building of paths in 3D graphs
- **Geometries** - ab1e403a4 – #2896, ensures cells&#39; geometries share common points
- **Graphs** - 950c78c4b – Add a new operator: `as_edge_graph(list edges, list nodes)`
- **Headless** - a92ff24b5 - Auto display error log if error in java runtime
- **Models library** - ecbbd444e - Addition of the Luneray&#39; Flu tutorial
- **Models library** - 1bc931619 - Addition of traffic models.
- **Models library** - 2e948dd40 - Adds models of `CRS_transform` and `to_GAMA_CRS`
- **Models library** - ae1286d03 - Addition of models for the BDI tutorial
- **Models library** - 735bf366a – Adds a simple example of XY charts
- **Models library** - 110733fc3 - Adds a model demonstrating simplification and buffer
- **Models library** - 6fd7ee88a – Adds models of the new statistical distributions
- **Models library** - 5c0221098 – Removes the `init` in &quot;Long Series.gaml&quot;
- **Navigator** - 53a9dbee9 - #2989 does not display experiments in the navigator when `@no_experiment` is defined
- **Operators** - bfc39b460 – Adds an operator to transform a geometry from a CRS to a target CRS.
- **Operators** - d8154ffda – Adds `all_indexes_of` operator
- **Parameters view** - b618d5ba8 - Adds collapse/expand all for parameter view
- **Parameters view** - d8402f462 - Add preferences to auto-expand all parameters in categories
- **Random -** 0d4a5557d, 58df2c36d, 629480343 - addition of rnd operators with weibull, lognormal and gauss distributions, various parameters + probability density functions
- **Random -** 6643eef5c - Adds `rnd_choice` with map argument, keys return type and values gives the distribution
- **Save statement -** 2d421ef67 - Adds the export of gis data with GAMA CRS
- **Save statement** - 3aabeae90 - Save geotiff files in float format (and not byte).
- **Save statement -** 731ff61ad – Saves geometries with GAMA CRS and scaling
- **Save statement -** 91ccdf80d – Adds .prj support file when saving shapefiles
- **Save statement** – b5582127f, afe6da45a, saves geometries with a list of attributes
- **Simulation** - 32eb57a24 – updates the state of the simulation after UI is created.
- **Simulation** - 66bb1f856 – improves the addition of simulations (splits the views)
- **Simulations** - 40d4dac49 – #2904, adds a &#39;resume&#39; action to simulations and an example in the Models Library as well.
- **User input** - 974e55200 - #2979, improves `user_input()` by allowing to choose values among a list
- **Wizards** - 11aba31c8 - Proposes templates in projects in the &quot;new model&quot; wizard
- **Workbench** - 1ec1cb1a9 - Adds preferences for asking users about a workspace at startup
 


 


  
  
  
