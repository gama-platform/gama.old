# Major changes from 1.9.1 to 1.9.2

**The GAMA development team is pleased to announce the release of GAMA 1.9.2**. This is a maintenance release, aiming at fixing bugs observed in 1.9.1 and clarifying some of its concepts. No new features have been added, except the possibility to define `#mouse_drag` `event`s. 

# Detailed changes.

This release of GAMA contains new features and fixes, including:

* 2D displays can now also be locked (only from the user interface currently)
* A new event has been introduced: `mouse_drag`, an example is available in the model library: [msi.gama.models/models/Visualization and User Interaction/User Interaction/models/Mouse Drag.gaml](https://github.com/gama-platform/gama/blob/GAMA_1.9.2/msi.gama.models/models/Visualization%20and%20User%20Interaction/User%20Interaction/models/Mouse%20Drag.gaml)
* Fixes potential issues with `user_location` on touch screens
* Fixes many issues on keyboard events (see issue [#3770](https://github.com/gama-platform/gama/issues/3770), and [this commit](https://github.com/gama-platform/gama/commit/48973746ba47191f0aac92fff1908a950ae07d3c))
* Fixes control buttons not updating while in fullscreen ([#3769](https://github.com/gama-platform/gama/issues/3769))
* Fixes the saving of matrices using the `save` statement that was faulty for non-square matrices
* Fixes the casting from `matrix` to `string` that was faulty for non-square matrices
* Adds the operator `exp_rnd` to generate a random number following an exponential distribution (example in [msi.gama.models/models/Visualization and User Interaction/Visualization/Charts/models/Distribution.gaml](https://github.com/gama-platform/gama/blob/bc6dd960f608af2a61b358cfbb1eba0d89329d05/msi.gama.models/models/Visualization%20and%20User%20Interaction/Visualization/Charts/models/Distribution.gaml))
* Various fixes and improvements for the reading/writing of `csv` files (see this [commit](https://github.com/gama-platform/gama/commit/ead1fd816bf55b1f6838127122750959fc33b999), [this one](https://github.com/gama-platform/gama/commit/fbe60ca9a72f5b2be322d5bfe1b7ac179079ffcd), [this one](https://github.com/gama-platform/gama/commit/c4eb5023019a8f360d29870c7c3b7d6f425f4a8f) and this [issue](https://github.com/gama-platform/gama/issues/3817))
* Fixes bugs in the gaml editor when a display didn't contain any code
* Improves the display of fields/mesh ([#3796](https://github.com/gama-platform/gama/issues/3796))
* Fixes runtime error happening in torus models in certain cases ([#3783](https://github.com/gama-platform/gama/issues/3783))
* Improvement of the type inference system for matrices ([#3792](https://github.com/gama-platform/gama/issues/3792))
* Fixes `hpc` flag being ignored in some cases in headless mode ([#3687](https://github.com/gama-platform/gama/issues/3687))
* Fixes a bug in `save_simulation` where simulations with variables of type font couldn't be saved ([#3815](https://github.com/gama-platform/gama/issues/3815))
* Fixes default camera in 3d displays not being applied unless explicitly written ([#3811](https://github.com/gama-platform/gama/issues/3811))
* Enables steps in `loop` statement to be of float type instead of silently casting it to int ([#3810](https://github.com/gama-platform/gama/issues/3810))
* Type casting has been made more consistent (see [#3809 for colors](https://github.com/gama-platform/gama/issues/3809), [#3803](https://github.com/gama-platform/gama/issues/3803) for lists and [#3806](https://github.com/gama-platform/gama/issues/3806) for pairs )
* Improves memory management when drawing images (see [this commit](https://github.com/gama-platform/gama/commit/7839e38a71694621fab9174b1f8a8e5e81f866ec))
* Fixes rendering issues in documentation navigation within gama on windows ([#3804](https://github.com/gama-platform/gama/issues/3804))
* Better handling of `HTTP` responses (see this [commit](https://github.com/gama-platform/gama/commit/9f95125c85a3d63fe69e7c6697c3f48c1aa8e841))
* Fixes runtime errors happening in some models with dynamic cameras ([#3821](https://github.com/gama-platform/gama/issues/3821))
* Fixes issues related to search dialog in the help menu for macOS ([#3829](https://github.com/gama-platform/gama/issues/3829) and [#3828](https://github.com/gama-platform/gama/issues/3828))
* Fixes some issues leading Morris exploration not being run (see this [commit](https://github.com/gama-platform/gama/commit/faa37a417e54e45d9ee305a89ed618f4ee10dd09))
* Makes gama-server able to execute multiple commands at the same time in parallel for each client (see this [commit](https://github.com/gama-platform/gama/commit/e7abe5c69bed37472bb631aada11b88c33ee0716))
* Adds an optional keepalive function to gama-server (see this [commit](https://github.com/gama-platform/gama/commit/e7abe5c69bed37472bb631aada11b88c33ee0716))
* Fixes exception raising sometimes when clients disconnect from gama-server (see this [commit](https://github.com/gama-platform/gama/commit/30ab9f193dbd8fe7747d140744badc27b1351e9c))
* Splitting the gama-server command `fetch` into two commands: `upload` and `download`
* Adding some verification on the format and types for the `parameters` option of the `load` and `reload` commands of gama-server
* Renaming the `memorize` type into `record` 
* Introducing `compress` facet to indicate if a `memorize`/`record` experiment should use compression (reduces memory usage but increase computation time)
* Introducing different formats used internally for `memorize`/`record` experiments that can be: `xml`, the legacy one, or two new ones: `json` and `binary` (both faster and more memory efficient) and can be set with the `format` facet of the experiment.
* Fixing issues in the script `gama-headless.sh` used in macOS ([#3766](https://github.com/gama-platform/gama/issues/3766) and this [commit](https://github.com/gama-platform/gama/commit/1f0436ea9f668283f4824aefba5d0f1b13274318))
* Multiple fixes on the `moran` operator (see this [issue](https://github.com/gama-platform/gama/issues/3848), this [commit](https://github.com/gama-platform/gama/commit/a4f3aad5d4dc9dfa56d5b5e4a9a8dda671f249b5) and [this one](https://github.com/gama-platform/gama/commit/01fa686a905bb2e37314496ab14a2b4a24d8ea07))
* Fixing the shortcut for code suggestion in macOS ([#3852](https://github.com/gama-platform/gama/issues/3852))
* ASC file using dx/dy format can now be read too
* Big memory leaks have been fixed for operations on images
* Miscellaneous internal/architecture improvements
* Fixes exceptions raised in tabu searches in some cases
* Fixes the `copy` operator on `shape` variables
* New splash screen
* Improves the navigation in the parameter by not refreshing it completely when one parameter changes (for example in batch mode)
* Fixes the initialisation order for experiment parameters
* Various general fixes in the display of svg
* All the geometries defined in an svg file are now accessible separately (allowing to draw only some of them, or to have different color for each for example)
* Adds a parameter to set an automatic `z` increment in between layers in opengl
 


***

## 

***

## Changes that can impact models
### 🔴 Errors 🔴: concepts that cannot be used anymore 


### 🔴 Errors 🔴: concepts that need to be written differently

* All skills belonging to the "driving" skill have been renamed for more intuitive names, the skill `advanced_driving` has been replaced by `driving`, the skill `skill_road` is replaced by `road_skill` and `skill_road_node` is replaced by `intersection_skill`

* The `loop` statements using a `step` facet are not casting the `step` value into an `int` anymore which means that if you had loops that used `float` variables as a step, they may behave differently.

* casting colors (`rgb` type) into different types changed in some cases:
  * casting an `rgb` into a `float` now returns the same result as casting to an `int` instead of returning `0`
  * casting an `rgb` into a `point` now returns a point formed like this `{red, green, blue}` instead of `{0,0,0}`
  * casting an `rgb` into a `list` now returns a list of its four components: red, green, blue and alpha instead of just red, green and blue

* casting a `string` into a `list` now returns a list of string composed of all the letters of the original `string`. For example: `list("some string")` will return this list: `["s", "o", "m", "e", " ", "s", "t", "r", "i", "n", "g"]`

* casting into a `pair` has been homogenized, overall most cases are kept unchanged but some fringe cases are eliminated. The general rule is that casting anything into a `pair` will now result in a pair where the first and the second elements are the same initial object. For example: `pair([1,2,3])` will return this pair: `[1,2,3]::[1,2,3]`. The only exceptions are casting a `pair` into a `pair` which will result in no change, and casting a `map` into a `pair` that will result in a pair where the first element is the list of keys of the map and the second is the list of values of the map.

* The gama-server command `fetch` has been split into two different commands: `upload` and `download` and cannot be used anymore. See [here](https://gama-platform.org/wiki/HeadlessServer#the-download-command) the documentation on how to use those commands.

* The type of experiment `memorize` should be renamed `record`. In addition there's now two additional facets you can set for `memorize`/`record` experiments: `format` and `compress`. The `format` facet indicates the internal format used to save each step, and can be `"xml"`, `"binary"` or `"json"`. The `compress` facet indicates whether or not the saved step should be compressed or not. Compressed ones will take less memory in the long run, but will take more time to save/load.

### 🟠 Warnings 🟠: 


***

## Preferences
The description of all preferences can be found at this [page](https://gama-platform.org/wiki/next/Preferences). A number of new preferences have been added to cover existing or new aspects of the platform. They are summarised below.
### New preferences

***

## Bug fixes
You can also check the complete list of the closed issues on the [github repository](https://github.com/gama-platform/gama/issues?q=created%3A%3E%3D2023-04-13+is%3Aclosed). Keep in mind that this list is incomplete as a lot of problems where solved without being linked to any issue on github (via the mailing list or internally for example).

***

## Added models
The library of models has undergone some changes. Besides making sure all the models compile and run fine under the new version of GAMA, it also brings some new models, which are listed below:

* A model to showcase the use of SVG files: [ummisco.gaml.extensions.image/models/Images/models/SVG Manipulation.gaml](https://github.com/gama-platform/gama/blob/cfda0e49894472cd1475b055f886715d056222f4/ummisco.gaml.extensions.image/models/Images/models/SVG%20Manipulation.gaml)
* An example model to test the new `mouse_drag` event: [msi.gama.models/models/Visualization and User Interaction/User Interaction/models/Mouse Drag.gaml](https://github.com/gama-platform/gama/blob/GAMA_1.9.2/msi.gama.models/models/Visualization%20and%20User%20Interaction/User%20Interaction/models/Mouse%20Drag.gaml)
* Pedestrian movement following Mehdi Moussaid's model: [msi.gama.models/models/Toy Models/Pedestrian/models/Moussaid model.gaml](https://github.com/gama-platform/gama/blob/3192728f30e9ee89245ca89b1cfad4bd5e75f8ce/msi.gama.models/models/Toy%20Models/Pedestrian/models/Moussaid%20model.gaml) 
* A new example of data importation to get a mapbox image as a background of the simulation: [msi.gama.models/models/Data/Data Importation/models/MapBox Image Import As Background Image.gaml](https://github.com/gama-platform/gama/blob/cfda0e49894472cd1475b055f886715d056222f4/msi.gama.models/models/Data/Data%20Importation/models/MapBox%20Image%20Import%20As%20Background%20Image.gaml)

### --- 
