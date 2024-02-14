/*******************************************************************************************************
 *
 * GamlCoreConstants.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.constants;

import java.awt.Font;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IConstantCategory;

/**
 * The Interface IUnits.
 */
public interface GamlCoreConstants {

	/** The current error. */
	@constant (
			value = "current_error",
			altNames = {},
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.ACTION },
			doc = { @doc ("The text of the last error thrown during the current execution") }) String current_error =
					"";

	/**
	 * Mathematical constants
	 *
	 */
	@constant (
			value = "pi",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT, IConcept.MATH },
			doc = @doc ("The PI constant")) double pi = Math.PI;

	/** The e. */
	@constant (
			value = "e",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT, IConcept.MATH },
			doc = @doc ("The e constant")) double e = Math.E;

	/** The to deg. */
	@constant (
			value = "to_deg",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the value to convert radians into degrees")) double to_deg = 180d / Math.PI;

	/** The to rad. */
	@constant (
			value = "to_rad",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the value to convert degrees into radians")) double to_rad = Math.PI / 180d;

	/** The nan. */
	@constant (
			value = "nan",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding a Not-a-Number (NaN) value of type float (Java Double.POSITIVE_INFINITY)")) double nan =
					Double.NaN;

	/** The infinity. */
	@constant (
			value = "infinity",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the positive infinity of type float (Java Double.POSITIVE_INFINITY)")) double infinity =
					Double.POSITIVE_INFINITY;

	/** The min float. */
	@constant (
			value = "min_float",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the smallest positive nonzero value of type float (Java Double.MIN_VALUE)")) double min_float =
					Double.MIN_VALUE;

	/** The max float. */
	@constant (
			value = "max_float",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the largest positive finite value of type float (Java Double.MAX_VALUE)")) double max_float =
					Double.MAX_VALUE;

	/** The min int. */
	@constant (
			value = "min_int",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the minimum value an int can have (Java Integer.MIN_VALUE)")) int min_int =
					Integer.MIN_VALUE;

	/** The max int. */
	@constant (
			value = "max_int",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.CONSTANT },
			doc = @doc ("A constant holding the maximum value an int can have (Java Integer.MAX_VALUE)")) int max_int =
					Integer.MAX_VALUE;

	/**
	 * Shortest Path algorithm constants
	 */
	@constant (
			value = "FloydWarshall",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.EQUATION, IConcept.CONSTANT },
			doc = @doc ("FloydWarshall shortest path computation algorithm")) String FloydWarshall = "FloydWarshall";

	/** The Bellmann ford. */
	@constant (
			value = "BellmannFord",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.GRAPH, IConcept.CONSTANT },
			doc = @doc ("BellmannFord shortest path computation algorithm")) String BellmannFord = "BellmannFord";

	/** The Dijkstra. */
	@constant (
			value = "Dijkstra",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.GRAPH, IConcept.CONSTANT },
			doc = @doc ("Dijkstra shortest path computation algorithm")) String Dijkstra = "Dijkstra";

	/** The A star. */
	@constant (
			value = "AStar",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.GRAPH, IConcept.CONSTANT },
			doc = @doc ("AStar shortest path computation algorithm")) String AStar = "AStar";

	/** The NBA star. */
	@constant (
			value = "NBAStar",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.GRAPH, IConcept.CONSTANT },
			doc = @doc ("NBAStar shortest path computation algorithm")) String NBAStar = "NBAStar";

	/** The NBA star approx. */
	@constant (
			value = "NBAStarApprox",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.GRAPH, IConcept.CONSTANT },
			doc = @doc ("NBAStarApprox shortest path computation algorithm")) String NBAStarApprox = "NBAStarApprox";

	/** The Delta stepping. */
	@constant (
			value = "DeltaStepping",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.GRAPH, IConcept.CONSTANT },
			doc = @doc ("DeltaStepping shortest path computation algorithm")) String DeltaStepping = "DeltaStepping";

	/** The CH bidirectional dijkstra. */
	@constant (
			value = "CHBidirectionalDijkstra",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.GRAPH, IConcept.CONSTANT },
			doc = @doc ("CHBidirectionalDijkstra shortest path computation algorithm")) String CHBidirectionalDijkstra =
					"CHBidirectionalDijkstra";

	/** The Bidirectional dijkstra. */
	@constant (
			value = "BidirectionalDijkstra",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.GRAPH, IConcept.CONSTANT },
			doc = @doc ("BidirectionalDijkstra shortest path computation algorithm")) String BidirectionalDijkstra =
					"BidirectionalDijkstra";

	/** The Transit node routing. */
	@constant (
			value = "TransitNodeRouting",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.GRAPH, IConcept.CONSTANT },
			doc = @doc ("TransitNodeRouting shortest path computation algorithm")) String TransitNodeRouting =
					"TransitNodeRouting";

	/** The Yen. */
	@constant (
			value = "Yen",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.GRAPH, IConcept.CONSTANT },
			doc = @doc ("Yen K shortest paths computation algorithm")) String Yen = "Yen";

	/** The Bhandari. */
	@constant (
			value = "Bhandari",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.GRAPH, IConcept.CONSTANT },
			doc = @doc ("Bhandari K shortest paths computation algorithm")) String Bhandari = "Bhandari";

	/** The Eppstein. */
	@constant (
			value = "Eppstein",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.GRAPH, IConcept.CONSTANT },
			doc = @doc ("Eppstein K shortest paths computation algorithm")) String Eppstein = "Eppstein";

	/** The Suurballe. */
	@constant (
			value = "Suurballe",
			category = { IConstantCategory.CONSTANT },
			concept = { IConcept.GRAPH, IConcept.CONSTANT },
			doc = @doc ("Suurballe K shortest paths computation algorithm")) String Suurballe = "Suurballe";

	/**
	 * Buffer constants
	 */
	@constant (
			value = "round",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GEOMETRY, IConcept.CONSTANT },
			doc = @doc ("This constant represents a round line buffer end cap style")) int round = 1;

	/** The flat. */
	@constant (
			value = "flat",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GEOMETRY, IConcept.CONSTANT },
			doc = @doc ("This constant represents a flat line buffer end cap style")) int flat = 2;

	/** The square. */
	@constant (
			value = "square",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GEOMETRY, IConcept.CONSTANT },
			doc = @doc ("This constant represents a square line buffer end cap style")) int square = 3;

	/**
	 * Anchor constants
	 */
	@constant (
			value = "center",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("Represents an anchor situated at the center of the text to draw")) GamaPoint center =
					new GamaPoint(0.5, 0.5);

	/** The top left. */
	@constant (
			value = "top_left",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("Represents an anchor situated at the top left corner of the text to draw")) GamaPoint top_left =
					new GamaPoint(0, 1);

	/** The left center. */
	@constant (
			value = "left_center",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("Represents an anchor situated at the center of the left side of the text to draw")) GamaPoint left_center =
					new GamaPoint(0, 0.5);

	/** The bottom left. */
	@constant (
			value = "bottom_left",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("Represents an anchor situated at the bottom left corner of the text to draw")) GamaPoint bottom_left =
					new GamaPoint(0, 0);

	/** The bottom center. */
	@constant (
			value = "bottom_center",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			altNames = { "bottom" },
			doc = @doc ("Represents an anchor situated at the center of the bottom side of the text to draw")) GamaPoint bottom_center =
					new GamaPoint(0.5, 0);

	/** The bottom right. */
	@constant (
			value = "bottom_right",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("Represents an anchor situated at the bottom right corner of the text to draw")) GamaPoint bottom_right =
					new GamaPoint(1, 0);

	/** The right center. */
	@constant (
			value = "right_center",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("Represents an anchor situated at the center of the right side of the text to draw")) GamaPoint right_center =
					new GamaPoint(1, 0.5);

	/** The top right. */
	@constant (
			value = "top_right",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("Represents an anchor situated at the top right corner of the text to draw")) GamaPoint top_right =
					new GamaPoint(1, 1);

	/** The top center. */
	@constant (
			value = "top_center",
			category = { IConstantCategory.GRAPHIC },
			altNames = { "top" },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("Represents an anchor situated at the center of the top side of the text to draw")) GamaPoint top_center =
					new GamaPoint(0.5, 1);

	/**
	 * Layout constants
	 *
	 */
	@constant (
			value = "none",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("This constant represents the absence of a predefined layout")) int none = 0;

	/** The stack. */
	@constant (
			value = "stack",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("This constant represents a layout where all display views are stacked")) int stack = 1;

	/** The split. */
	@constant (
			value = "split",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("This constant represents a layout where all display views are split in a grid-like structure")) int split =
					2;

	/** The horizontal. */
	@constant (
			value = "horizontal",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("This constant represents a layout where all display views are aligned horizontally")) int horizontal =
					3;

	/** The vertical. */
	@constant (
			value = "vertical",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.DISPLAY, IConcept.OUTPUT },
			doc = @doc ("This constant represents a layout where all display views are aligned vertically")) int vertical =
					4;

	/**
	 * Font style constants
	 */

	@constant (
			value = "bold",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GRAPHIC, IConcept.TEXT },
			doc = @doc ("This constant allows to build a font with a bold face. Can be combined with #italic")) int bold =
					Font.BOLD; /* 1 */

	/** The italic. */
	@constant (
			value = "italic",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GRAPHIC, IConcept.TEXT },
			doc = @doc ("This constant allows to build a font with an italic face. Can be combined with #bold")) int italic =
					Font.ITALIC; /* 2 */

	/** The plain. */
	@constant (
			value = "plain",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GRAPHIC, IConcept.TEXT },
			doc = @doc ("This constant allows to build a font with a plain face")) int plain = Font.PLAIN;
	/**
	 * Special units
	 */

	@constant (
			value = "user_location",
			altNames = { "user_location_in_world" },
			category = IConstantCategory.GRAPHIC,
			concept = { IConcept.DISPLAY },
			doc = @doc ("This unit permanently holds the mouse's location in the world's coordinates. If it is outside a display window, its last position is used.")) GamaPoint user_location =
					new GamaPoint();

	/** The user location in display. */
	@constant (
			value = "user_location_in_display",
			category = IConstantCategory.GRAPHIC,
			concept = { IConcept.DISPLAY },
			doc = @doc ("This unit permanently holds the mouse's location in the display's coordinates. If it is outside a display window, its last position is used.")) GamaPoint user_location_in_display =
					new GamaPoint();

	/** The camera location. */
	@constant (
			value = "camera_location",
			category = IConstantCategory.GRAPHIC,
			concept = { IConcept.GRAPHIC, IConcept.GRAPHIC_UNIT, IConcept.THREED },
			doc = @doc ("This unit, only available when running aspects or declaring displays, returns the current position of the camera as a point")) GamaPoint camera_location =
					new GamaPoint();

	/** The camera target. */
	@constant (
			value = "camera_target",
			category = IConstantCategory.GRAPHIC,
			concept = { IConcept.GRAPHIC, IConcept.GRAPHIC_UNIT, IConcept.THREED },
			doc = @doc ("This unit, only available when running aspects or declaring displays, returns the current target of the camera as a point")) GamaPoint camera_target =
					new GamaPoint();

	/** The camera orientation. */
	@constant (
			value = "camera_orientation",
			category = IConstantCategory.GRAPHIC,
			concept = { IConcept.GRAPHIC, IConcept.GRAPHIC_UNIT, IConcept.THREED },
			doc = @doc ("This unit, only available when running aspects or declaring displays, returns the current orientation of the camera as a point")) GamaPoint camera_orientation =
					new GamaPoint();

	/** The zoom. */
	@constant (
			value = "zoom",
			category = IConstantCategory.GRAPHIC,
			concept = { IConcept.GRAPHIC, IConcept.DISPLAY },
			doc = @doc ("This unit, only available when running aspects or declaring displays, returns the current zoom level of the display as a positive float, where 1.0 represent the neutral zoom (100%)")) double zoom =
					1;

	/** The fullscreen. */
	@constant (
			value = "fullscreen",
			category = IConstantCategory.GRAPHIC,
			concept = { IConcept.GRAPHIC, IConcept.DISPLAY },
			doc = @doc ("This unit, only available when running aspects or declaring displays, returns whether the display is currently fullscreen or not")) boolean fullscreen =
					false;

	/** The hidpi. */
	@constant (
			value = "hidpi",
			category = IConstantCategory.GRAPHIC,
			concept = { IConcept.GRAPHIC, IConcept.DISPLAY },
			doc = @doc ("This unit, only available when running aspects or declaring displays, returns whether the display is currently in HiDPI mode or not")) boolean hidpi =
					false;

	/** The px. */
	@constant (
			value = "pixels",
			altNames = { "px" },
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GRAPHIC, IConcept.GRAPHIC_UNIT },
			doc = @doc ("This unit, only available when running aspects or declaring displays,  returns a dynamic value instead of a fixed one. px (or pixels), returns the value of one pixel on the current view in terms of model units.")) double pixels =
					1d, px = pixels;

	/** The display width. */
	@constant (
			value = "display_width",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GRAPHIC, IConcept.GRAPHIC_UNIT },
			doc = @doc ("This constant is only accessible in a graphical context: display, graphics...")) double display_width =
					1;

	/** The display height. */
	@constant (
			value = "display_height",
			category = { IConstantCategory.GRAPHIC },
			concept = { IConcept.GRAPHIC, IConcept.GRAPHIC_UNIT },
			doc = @doc ("This constant is only accessible in a graphical context: display, graphics...")) double display_height =
					1;

	/** The now. */
	@constant (
			value = "now",
			category = { IConstantCategory.TIME },
			concept = { IConcept.DATE, IConcept.TIME },
			doc = @doc ("This value represents the current date")) double now = 1;

}