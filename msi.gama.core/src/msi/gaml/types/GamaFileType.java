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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.types;

import java.io.File;
import java.util.*;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.IGamaFile;
import msi.gaml.operators.Files;

/**
 * Written by drogoul Modified on 1 aožt 2010
 * 
 * @todo Description
 * 
 */
@type(name = IType.FILE_STR, id = IType.FILE, wraps = { IGamaFile.class, File.class }, kind = ISymbolKind.Variable.CONTAINER /* ? */)
public class GamaFileType extends GamaType<IGamaFile> {

	/** Constant field textSuffixes. */
	private static final List<String> textSuffixes = Arrays.asList(".txt", ".data",
		".text", ".tsv", ".asc");

	private static final String csvSuffix = ".csv";
	private static final String shpSuffix = ".shp";
	// To be used later, to load geometries ?
	private static final String propertiesSuffix = ".properties";
	/** Constant field imageSuffixes. */
	private static final List<String> imageSuffixes = Arrays.asList(".pgm", ".tif", ".csv", ".tiff",
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

	@Override
	public IGamaFile getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.get(NONE);
	}

	@operator(value = "is_text")
	@doc(
		value =	"the operator tests whether the operand represents the name of a supported text file",
		comment = "cf. file type definition for supported (espacially image) file extensions.",
		examples = {"is_text(\"../includes/Stupid_Cell.Data\")    --:  true;",
					"is_text(\"../includes/test.png\")            --:  false;", 
					"is_text(\"../includes/test.properties\")     --:  false;",
					"is_text(\"../includes/test.shp\")            --:  false;"},
		see = {"text", "is_properties", "is_shape", "is_image"})	
	public static Boolean isTextFile(final String f) {
		final String fn = f.toLowerCase();
		for ( final String s : textSuffixes ) {
			if ( fn.contains(s) ) { return true; }
		}
		return false;
	}

	@operator(value = "is_csv")
	@doc(
		value =	"the operator tests whether the operand represents the name of a supported csv file",
		comment = "cf. file type definition for supported (espacially image) file extensions.",
		examples = {"is_csv(\"../includes/test.csv\")    --:  true;",
					"is_csv(\"../includes/test.png\")            --:  false;", 
					"is_csv(\"../includes/test.properties\")     --:  false;",
					"is_csv(\"../includes/test.shp\")            --:  false;"},
		see = {"is_text", "is_properties", "is_shape", "is_image"})	
	public static Boolean isCSVFile(final String f) {
		return f.toLowerCase().contains(csvSuffix);
	}

	@operator(value = "is_properties")
	@doc(
		value ="the operator tests whether the operand represents the name of a supported properties file",
		comment = "cf. file type definition for supported (espacially image) file extensions.",
		examples = {"is_properties(\"../includes/Stupid_Cell.Data\")    --:  false;",
					"is_properties(\"../includes/test.png\")            --:  false;", 
					"is_properties(\"../includes/test.properties\")     --:  true;",
					"is_properties(\"../includes/test.shp\")            --:  false;"},
		see = {"properties", "is_text", "is_shape", "is_image"})		
	public static Boolean isProperties(final String f) {
		return f.toLowerCase().contains(propertiesSuffix);
	}

	@operator(value = "is_shape")
	@doc(
		value ="the operator tests whether the operand represents the name of a supported shapefile",
		comment = "cf. file type definition for supported (espacially image) file extensions.",
		examples = {"is_shape(\"../includes/Stupid_Cell.Data\")    --:  false;",
					"is_shape(\"../includes/test.png\")            --:  false;", 
					"is_shape(\"../includes/test.properties\")     --:  false;",
					"is_shape(\"../includes/test.shp\")            --:  true;"},
		see = {"image", "is_text", "is_properties", "is_image"})	
	public static Boolean isShape(final String f) {
		return f.toLowerCase().contains(shpSuffix);
	}

	@operator(value = "is_image")
	@doc(
		value ="the operator tests whether the operand represents the name of a supported image file",
		comment = "cf. file type definition for supported (espacially image) file extensions.",
		examples = {"is_image(\"../includes/Stupid_Cell.Data\")    --:  false;",
					"is_image(\"../includes/test.png\")            --:  true;", 
					"is_image(\"../includes/test.properties\")     --:  false;",
					"is_image(\"../includes/test.shp\")            --:  false;"},
		see = {"image", "is_text", "is_properties", "is_shape"})
	public static Boolean isImageFile(final String f) {
		final String fn = f.toLowerCase();
		for ( final String s : imageSuffixes ) {
			if ( fn.contains(s) ) { return true; }
		}
		return false;
	}

}
