/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.types;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.IGamaFile;
import msi.gaml.operators.Files;

/**
 * Written by drogoul Modified on 1 ao�t 2010
 * 
 * @todo Description
 * 
 */
@type(name = IKeyword.FILE, id = IType.FILE, wraps = { IGamaFile.class }, kind = ISymbolKind.Variable.CONTAINER /* ? */)
public class GamaFileType extends GamaContainerType<IGamaFile> {

	private static final List<String> textSuffixes = Arrays.asList(".txt", ".data", ".csv",
		".text", ".tsv", "xml");
	private static final String shpSuffix = ".shp";
	private static final String gamlSuffix = ".gaml";
	private static final List<String>  osmSuffix = Arrays.asList(".osm", ".pbf", ".bz2",
			".gz");
	private static final List<String> gridSuffixes = Arrays.asList(".asc");

	private static final String propertiesSuffix = ".properties";
	private static final List<String> imageSuffixes = Arrays.asList(".pgm", ".tif", ".tiff",
		".jpg", ".jpeg", ".png", ".gif", ".pict", ".bmp");

	/** Constant field pgmSuffix. */
	public static final String pgmSuffix = ".pgm";

	@Override
	public IGamaFile cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj == null ) { return getDefault(); }
		if ( obj instanceof IGamaFile ) { return (IGamaFile) obj; }
		if ( obj instanceof String ) { return Files.from(scope, (String) obj); }
		return getDefault();
	}

	@operator(value = "is_text")
	@doc(value = "the operator tests whether the operand represents the name of a supported text file", comment = "cf. file type definition for supported (espacially image) file extensions.", examples = {
		"is_text(\"../includes/Stupid_Cell.Data\")    --:  true;",
		"is_text(\"../includes/test.png\")            --:  false;",
		"is_text(\"../includes/test.properties\")     --:  false;",
		"is_text(\"../includes/test.shp\")            --:  false;" }, see = { "text",
		"is_properties", "is_shape", "is_image" })
	public static Boolean isTextFile(final String f) {
		final String fn = f.toLowerCase();
		for ( final String s : textSuffixes ) {
			if ( fn.endsWith(s) ) { return true; }
		}
		return false;
	}

	/*
	 * @operator(value = "is_csv")
	 * 
	 * @doc(
	 * value = "the operator tests whether the operand represents the name of a supported csv file",
	 * comment = "cf. file type definition for supported (espacially image) file extensions.",
	 * examples = {"is_csv(\"../includes/test.csv\")    --:  true;",
	 * "is_csv(\"../includes/test.png\")            --:  false;",
	 * "is_csv(\"../includes/test.properties\")     --:  false;",
	 * "is_csv(\"../includes/test.shp\")            --:  false;"},
	 * see = {"is_text", "is_properties", "is_shape", "is_image"})
	 * public static Boolean isCSVFile(final String f) {
	 * return f.toLowerCase().contains(csvSuffix);
	 * }
	 */

	@operator(value = "is_properties")
	@doc(value = "the operator tests whether the operand represents the name of a supported properties file", comment = "cf. file type definition for supported (espacially image) file extensions.", examples = {
		"is_properties(\"../includes/Stupid_Cell.Data\")    --:  false;",
		"is_properties(\"../includes/test.png\")            --:  false;",
		"is_properties(\"../includes/test.properties\")     --:  true;",
		"is_properties(\"../includes/test.shp\")            --:  false;" }, see = { "properties",
		"is_text", "is_shape", "is_image" })
	public static Boolean isProperties(final String f) {
		return f.toLowerCase().endsWith(propertiesSuffix);
	}

	@operator(value = "is_shape")
	@doc(value = "the operator tests whether the operand represents the name of a supported shapefile", comment = "cf. file type definition for supported (espacially image) file extensions.", examples = {
		"is_shape(\"../includes/Stupid_Cell.Data\")    --:  false;",
		"is_shape(\"../includes/test.png\")            --:  false;",
		"is_shape(\"../includes/test.properties\")     --:  false;",
		"is_shape(\"../includes/test.shp\")            --:  true;" }, see = { "image", "is_text",
		"is_properties", "is_image" })
	public static Boolean isShape(final String f) {
		return f.toLowerCase().endsWith(shpSuffix);
	}
	
	@operator(value = "is_GAML")
	@doc(value = "the operator tests whether the operand represents the name of a supported gamlfile", comment = "cf. file type definition for supported (espacially model) file extensions.", examples = {
			"is_shape(\"../includes/Stupid_Cell.Data\")    --:  false;",
			"is_shape(\"../includes/test.png\")            --:  false;",
			"is_shape(\"../includes/test.properties\")     --:  false;",
			"is_shape(\"../includes/test.gaml\")            --:  true;" }, see = {
			"image", "is_text", "is_properties", "is_image" })
	public static Boolean isGAML(final String f) {
		return f.toLowerCase().endsWith(gamlSuffix);
	}

	@operator(value = "is_osm")
	@doc(value = "the operator tests whether the operand represents the name of a supported osm file", comment = "cf. file type definition for supported (espacially image) file extensions.", examples = {
		"is_osm(\"../includes/Stupid_Cell.Data\")    --:  false;",
		"is_osm(\"../includes/test.png\")            --:  false;",
		"is_osm(\"../includes/test.properties\")     --:  false;",
		"is_osm(\"../includes/test.osm\")            --:  true;" }, see = { "image", "is_text",
		"is_properties", "is_image" })
	public static Boolean isOsm(final String f) {
		final String fn = f.toLowerCase();
		for ( final String s : osmSuffix ) {
			if ( fn.endsWith(s) ) { return true; }
		}
		return false;
	}
	
	@operator(value = "is_grid")
	@doc(value = "the operator tests whether the operand represents the name of a supported gridfile", comment = "cf. file type definition for supported (espacially image) file extensions.", examples = {
		"is_grid(\"../includes/Stupid_Cell.Data\")    --:  false;",
		"is_grid(\"../includes/test.png\")            --:  false;",
		"is_grid(\"../includes/test.properties\")     --:  false;",
		"is_grid(\"../includes/test.asc\")            --:  true;" }, see = { "image", "is_text",
		"is_properties", "is_image", "is_shape" })
	public static Boolean isGrid(final String f) {
		final String fn = f.toLowerCase();
		for ( final String s : gridSuffixes ) {
			if ( fn.endsWith(s) ) { return true; }
		}
		return false;
	}

	@operator(value = "is_image")
	@doc(value = "the operator tests whether the operand represents the name of a supported image file", comment = "cf. file type definition for supported (espacially image) file extensions.", examples = {
		"is_image(\"../includes/Stupid_Cell.Data\")    --:  false;",
		"is_image(\"../includes/test.png\")            --:  true;",
		"is_image(\"../includes/test.properties\")     --:  false;",
		"is_image(\"../includes/test.shp\")            --:  false;" }, see = { "image", "is_text",
		"is_properties", "is_shape" })
	public static Boolean isImageFile(final String f) {
		final String fn = f.toLowerCase();
		for ( final String s : imageSuffixes ) {
			if ( fn.endsWith(s) ) { return true; }
		}
		return false;
	}

}
