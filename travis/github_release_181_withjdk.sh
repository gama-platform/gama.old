#!/bin/bash




function update_tag() {
	echo "update tag " $1 
	git config --global user.email "my.gama.bot@gmail.com"
	git config --global user.name "GAMA Bot"
	git remote rm origin
	git remote add origin https://gama-bot:$BOT_TOKEN@github.com/gama-platform/gama.git
	git config remote.origin.fetch "+refs/heads/*:refs/remotes/origin/*"
	git fetch origin
	git checkout --track origin/master
	git pull
	git status
	git push origin :refs/tags/$1
	git tag -d $1
	git tag -fa $1 -m "$1"
	git push --tags -f
	git ls-remote --tags origin
	git show-ref --tags
}


set -e
echo "github_release_181_withjdk"		
COMMIT=$@

REPO="gama-platform/gama"
RELEASE="1.8.1"
thePATH="/home/travis/build/gama-platform/gama/ummisco.gama.product/target/products/Gama1.7"















COMMIT="${COMMIT:0:7}"

timestamp=$(date '+_%D')

SUFFIX=$timestamp'_'$COMMIT'.zip'
echo $SUFFIX



n=0
RELEASEFILES[$n]="$thePATH-linux.gtk.x86_64.zip"
NEWFILES[$n]='GAMA1.8_Continuous_Linux'$SUFFIX 
n=1
RELEASEFILES[$n]="$thePATH-macosx.cocoa.x86_64.zip"
NEWFILES[$n]='GAMA1.8_Continuous_Mac'$SUFFIX
n=2
RELEASEFILES[$n]="$thePATH-win32.win32.x86_64.zip" 
NEWFILES[$n]='GAMA1.8_Continuous_Win'$SUFFIX
n=3
RELEASEFILES[$n]="$thePATH-linux.gtk.x86_64_withJDK.zip"
NEWFILES[$n]='GAMA1.8_Continuous_withJDK_Linux'$SUFFIX
n=4
RELEASEFILES[$n]="$thePATH-win32.win32.x86_64_withJDK.zip" 
NEWFILES[$n]='GAMA1.8_Continuous_withJDK_Win'$SUFFIX
n=5
RELEASEFILES[$n]="$thePATH-macosx.cocoa.x86_64_withJDK.zip"
NEWFILES[$n]='GAMA1.8_Continuous_withJDK_Mac'$SUFFIX
 

i=0
for (( i=0; i<6; i++ ))
do
	FILE="${RELEASEFILES[$i]}"
	NFILE="${NEWFILES[$i]}"
	ls -sh $FILE
	echo $NFILE
done





LK1="https://api.github.com/repos/gama-platform/gama/releases/tags/$RELEASE"

echo   "Getting info of release Continuous...  "
RESULT1=`curl  -s -X GET \
-H "Authorization: token $BOT_TOKEN"   \
"$LK1"`	
echo $RESULT1

	json=$RESULT1
	prop='id'
	
    temp=`echo $json | sed 's/\\\\\//\//g' | sed 's/[{}]//g' | awk -v k="text" '{n=split($0,a,","); for (i=1; i<=n; i++) print a[i]}' | sed 's/\"\:\"/\|/g' | sed 's/[\,]/ /g' | sed 's/\"//g' | grep -w $prop`
    
	assets=`echo ${temp##*|}`

	for theid in $assets; do
		if [ "$theid" != "id:" ]; then
	LK1="https://api.github.com/repos/gama-platform/gama/releases/$theid"

	echo   "Deleting release Continuous...  "
	RESULT1=`curl  -s -X DELETE \
	-H "Authorization: token $BOT_TOKEN"   \
	"$LK1"`	
	echo $RESULT1
	break
		fi
	done 


	#update_tag $RELEASE

	echo   "Creating release Continuous...  "
LK="https://api.github.com/repos/gama-platform/gama/releases"

  RESULT=` curl -s -X POST \
  -H "X-Parse-Application-Id: sensitive" \
  -H "X-Parse-REST-API-Key: sensitive" \
  -H "Authorization: token $BOT_TOKEN"   \
  -H "Content-Type: application/json" \
  -d '{"tag_name": "'$RELEASE'", "name":"Continuous build","body":"# BUG FIXES\n
\n
<details>\n
<summary>View contents</summary>\n
\n
- **Charts** - d99c03370, 5e41f06ad - #2921, correct colors in histograms and radars.\n
- **Displays** - 0fffc807b - #2854, takes orientation into account in the camera settings.\n
- **Displays** - 512942c8f - #2853, improves numbers input in the z-axis rotation field\n
- **Displays** - 903c64dd2 - #2897, adds a preference for copying Camera parameters.\n
- **Displays** - 09df6fa99 - #2971, harmonizes the meaning of the \`transparency\` facet\n
- **Displays** - 3f3563350 - #2835, take correct snapshots of zoomed displays\n
- **Displays** - 80fa60de2, ee1281a52 - #2851, better handles Java2D displays when simulation is added dynamically from the Parameters view\n
- **Displays** - c4fde1666 - #2913. #2902, better computes boolean attributes in layers.\n
- **Editor** - 0b5288062 - #2850, removes empty error messages from the editor\n
- **Editor** - a1246f7d8 - #2969, reenables correct operations of &quot;Find References...&quot;\n
- **Editor -** cc2b564cf - #2833, more robust highlighting of boolean operators\n
- **Files** - 66f73efb1 - #2860, updates the CSV metadata when edited in GAMA\n
- **Files** - 9a52ccf31 - #2929, replaces \`:\` in filenames when saving benchmark results\n
- **Files** - ab00eea0a - #2901, corrects \`.asc\` file headers created by QGis.\n
- **Files** - ae9efc1d7 - #2988, does not escape \`/\` anymore in strings\n
- **Files** - d6cf13ca0 – Fixes an issue with OSM data and projection\n
- **FSM** - 5229a7415, 3198351c7 - #2865, run the \`exit\` section of a state, even when it is marked as \`final: true\` (could happen when dying).\n
- **FSM** - e55831c20 - #2866, emits an error when several states are marked \`final\`\n
- **GAML** - 075b1a282, 6f40de575, 169ddb61e, 81f6d2f1c - #2869, #2874 and [COMOKIT/COMOKIT-Model/issues/21](https://github.com/COMOKIT/COMOKIT-Model/issues/21), checks dependencies between attributes\n
- **GAML** - 67021601e - #2922, better computes the variable scope of recursive actions\n
- **GAML** - 698b2f577 - #2932, provides the right context to the \`agents\` layer.\n
- **GAML** - 6f40de575 - #2875, checks the syntax of generic types more carefully\n
- **GAML** - 7c2c653e7 - #2873, disables the use of \`at\` on any type but containers\n
- **GAML** - a92f3b0f2 - warnings for expressions in the interactive console or monitors\n
- **GAML -** bd48567a4 - #2995, makes sure that \`font\` can be called with 2 arguments.\n
- **GAML** - d9a459d2c - #2910, computes constant string values used in file statements\n
- **GAML** - df346c9cc - #2605, sets the correct compilation and execution scopes when building attributes to save in the \`save\` statement\n
- **GAML** - e10938c14 - #2836, better serializes color constants\n
- **GAML** - e17800f51 - Fixes an annoying compilation bug for virtual actions\n
- **Geometries -** 47518a11a, 35d96ce01 – Fixes issues with \`pathBetween\` and \`masked_by\` operators\n
- **Goto -** 44d205977 – Make sure that the \`on:\` facet can accept a \`species\`.\n
- **Graphs** - 8963a3f62 - #2916, better splits some T-shaped geometries when cleaning road networks\n
- **Graphs** - 048512f64, 472b1891a - #2861, stops losing the geometrical information when applying layouts to graphs.\n
- **Graphs** - 187848edf - Fixes a bug occurring when filtering road type when creating a network and then using \`all_pairs_shortest_path\`\n
- **Graphs** - 540a5e137 - #2839, prevents an NPE in the update of a graph vertices\n
- **Graphs** - ba85b59f4 - #2940, removes an NPE when cleaning a road network built from OSM data using ArcMap\n
- **Headless** - 005d1fa4c - #2890, points to the embedded JDK in the headless scripts\n
- **Headless** - 0da04afdd - #2983, correctly sets \`project_path\` in headless mode\n
- **Headless** - aa74dbbf1, d4f7751d4, 47c131a29, cdba1b740 - #296, improves the robustness of the headless script and its handling of paths\n
- **Headless** - caaf8781c – #2976, removes virtual outputs when generating XML files\n
- **Hexagonal grids** - 23e0cf28f - #2894, returns the correct cell at a given location\n
- **Hexagonal grids** - 474cfbcd1 - #2892, allows spatial queries to operate again\n
- **Maths plugin** - 04af42dca - #2867, sets the value of \`t\`(time) after \`solve\` is over.\n
- **Maths plugin** - 468720f92 - #2868, deprecates \`integrated_times\` and \`integrated_values\` in the \`solve\` statement.\n
- **Navigator** - fd90fa5d8 - #2846, displays all types of files in &quot;Uses&quot; in the navigator.\n
- **Network plugin** a8d53f6f9 - #2906, adds a new attribute to the network \`connect\` action, in order to set the max size of packets (default is still 1024)\n
- **Operators** - af254f9fe - #2858, correctly documents the \`-\` operator for lists\n
- **Parameters view** - 5cdc90aa7 - #2834, allows labels of parameter to wrap.\n
- **Random** - 1a3b78fc7 - #2974, stops filling lists with random values in parallel\n
- **Random** - 7a29e0a8c, ac17bc332 - #2930 and [COMOKIT/COMOKIT-Model/issues/30](https://github.com/COMOKIT/COMOKIT-Model/issues/30), removes uses of HashSet in spatial operators and indexes\n
- **Save statement** - 187ece66f - #2857, allows to save one shape at a time.\n
- **Scheduling** - 4ffc81989 - #2927, #2933, schedules mirror species after their target species\n
- **Scheduling** - a1fd69ea2 - #2952, removes an NPE when using \`schedules\` in \`global\` and documents it.\n
- **Species browser** - d97481245 - #2852, correctly restores the attributes list in the species browser\n
- **Template editor** - 87d420345 - #2849, restores the ability to add templates again\n
- **Tests** - c61ba75c7 - #2881, builds a dynamic correspondence between class names and types\n
- **User input** - 89ad7c806 - #2978, makes \`init:\` / \`\&lt;-\` mandatory in \`user_input\`.\n
- **Views** - 903c64dd2 - #2856, removes the useless preference on view tabs height.\n
- **Wizards** - 3d94861ce - #2882, checks badly named experiments/models in wizards\n
- **Workbench** - d34a3d664 - #2848, prevents infinite loop when creating workspaces\n
\n
</details>\n
\n
# ENHANCEMENTS\n
\n
<details>\n
<summary>View contents</summary>\n
\n
- **Charts** - 3dc0d1156 - #2864, deprecates the xxx\_font\_size / xxx\_font\_style facets in chart layers and allows to use the font(...) operator instead\n
- **Displays** - 344894ea1 - #2127, #2380 and #2928, Improves the display of rasters\n
- **Displays** - 136f6e931 - #2891, synchronizes displays when one is set to autosave.\n
- **Displays** - 76a2902f4 - #1918, Addition of \`mouse_menu\` (ctrl-click) for event layers\n
- **Displays** - ed233b5d9 – Allows to display graphics w/o respecting world proportions\n
- **Documentation** - 94ad24936 – Encodes the docGAMA.xml in UTF-8\n
- **Files** - 831688854 - #2830, #2870, improves the handling of complex CSV files by adding 2 new constructors to \`csv_file\`.\n
- **Files** - 86a64de67, 9429902f9 – Adds an understandable error message for DXF files when the unit is less than or equal to 0. – improves their parsing (elliptical arc)\n
- **Files** - d21eaac44 –#2939, adds the new \`folder_exists\` operator.\n
- **GAML** - 2b5705d0e - #2883, documents the fact that \`model:\` needs to be the first facet of \`experiment\`.\n
- **GAML** - 57e07c2a4 –#2931, emits a warning when comparing different types\n
- **GAML** - 6310cb8df - #2872, enhanced information on the redefinition of reflexes and the order in which they will be executed\n
- **GAML** - 834da4446 – Transforms \`#min_int\` and \`#max_int\` into... \`int\` constants.\n
- **Geometries** - 984fb5c5e - Improves the building of paths in 3D graphs\n
- **Geometries** - ab1e403a4 – #2896, ensures cells&#39; geometries share common points\n
- **Graphs** - 950c78c4b – Add a new operator: \`as_edge_graph(list edges, list nodes)\`\n
- **Headless** - a92ff24b5 - Auto display error log if error in java runtime\n
- **Models library** - ecbbd444e - Addition of the Luneray&#39; Flu tutorial\n
- **Models library** - 1bc931619 - Addition of traffic models.\n
- **Models library** - 2e948dd40 - Adds models of \`CRS_transform\` and \`to_GAMA_CRS\`\n
- **Models library** - ae1286d03 - Addition of models for the BDI tutorial\n
- **Models library** - 735bf366a – Adds a simple example of XY charts\n
- **Models library** - 110733fc3 - Adds a model demonstrating simplification and buffer\n
- **Models library** - 6fd7ee88a – Adds models of the new statistical distributions\n
- **Models library** - 5c0221098 – Removes the \`init\` in &quot;Long Series.gaml&quot;\n
- **Navigator** - 53a9dbee9 - #2989 does not display experiments in the navigator when \`@no_experiment\` is defined\n
- **Operators** - bfc39b460 – Adds an operator to transform a geometry from a CRS to a target CRS.\n
- **Operators** - d8154ffda – Adds \`all_indexes_of\` operator\n
- **Parameters view** - b618d5ba8 - Adds collapse/expand all for parameter view\n
- **Parameters view** - d8402f462 - Add preferences to auto-expand all parameters in categories\n
- **Random -** 0d4a5557d, 58df2c36d, 629480343 - addition of rnd operators with weibull, lognormal and gauss distributions, various parameters + probability density functions\n
- **Random -** 6643eef5c - Adds \`rnd_choice\` with map argument, keys return type and values gives the distribution\n
- **Save statement -** 2d421ef67 - Adds the export of gis data with GAMA CRS\n
- **Save statement** - 3aabeae90 - Save geotiff files in float format (and not byte).\n
- **Save statement -** 731ff61ad – Saves geometries with GAMA CRS and scaling\n
- **Save statement -** 91ccdf80d – Adds .prj support file when saving shapefiles\n
- **Save statement** – b5582127f, afe6da45a, saves geometries with a list of attributes\n
- **Simulation** - 32eb57a24 – updates the state of the simulation after UI is created.\n
- **Simulation** - 66bb1f856 – improves the addition of simulations (splits the views)\n
- **Simulations** - 40d4dac49 – #2904, adds a &#39;resume&#39; action to simulations and an example in the Models Library as well.\n
- **User input** - 974e55200 - #2979, improves \`user_input()\` by allowing to choose values among a list\n
- **Wizards** - 11aba31c8 - Proposes templates in projects in the &quot;new model&quot; wizard\n
- **Workbench** - 1ec1cb1a9 - Adds preferences for asking users about a workspace at startup\n
\n
</details>","draft": false,"prerelease": true}' \
    "$LK"`
echo $RESULT	

















echo
echo "Getting info of $RELEASE tag..."
echo 
LK="https://api.github.com/repos/gama-platform/gama/releases/tags/$RELEASE"

  RESULT=` curl -s -X GET \
  -H "X-Parse-Application-Id: sensitive" \
  -H "X-Parse-REST-API-Key: sensitive" \
  -H "Authorization: token $BOT_TOKEN"   \
  -H "Content-Type: application/json" \
  -d '{"name":"value"}' \
    "$LK"`
echo $RESULT	
RELEASEID=`echo "$RESULT" | sed -ne 's/^  "id": \(.*\),$/\1/p'`
echo $RELEASEID


echo 
echo "Upload new files..."
echo

for (( i=0; i<6; i++ ))
do     
	FILE="${RELEASEFILES[$i]}"
	NFILE="${NEWFILES[$i]}"

  FILENAME=`basename $FILE`
  echo   "Uploading $NFILE...  "
  LK="https://uploads.github.com/repos/gama-platform/gama/releases/$RELEASEID/assets?name=$NFILE"
  
  RESULT=`curl -s -w  "\n%{http_code}\n"                   \
    -H "Authorization: token $BOT_TOKEN"                \
    -H "Accept: application/vnd.github.manifold-preview"  \
    -H "Content-Type: application/zip"                    \
    --data-binary "@$FILE"                                \
    "$LK"`
	echo $RESULT
done 
 
echo DONE
