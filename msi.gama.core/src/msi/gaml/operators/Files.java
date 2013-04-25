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
package msi.gaml.operators;

import java.io.File;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.*;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 20 dec. 2010
 * 
 * @todo Description
 * 
 */
public class Files {

	public static final String IMAGE = "image";
	public static final String TEXT = "text";
	public static final String PROPERTIES = "properties";
	public static final String FOLDER = "folder";
	public static final String SHAPE = "shapefile";
	public static final String GRID = "gridfile";
	public static final String READ = "read";
	public static final String WRITE = "write";
	public static Deque<Map> tempAttributes = new LinkedList();

	@operator(value = IKeyword.FILE, can_be_const = true)
	@doc(value = "opens a file in read only mode, creates a GAML file object, and tries to determine and store the file content in the contents attribute.", comment = "The file should have a supported extension, see file type deifnition for supported file extensions.", special_cases = "If the specified string does not refer to an existing file, an exception is risen when the variable is used.", examples = {
		"let fileT type: file value: file(\"../includes/Stupid_Cell.Data\"); ",
		"			// fileT represents the file \"../includes/Stupid_Cell.Data\"",
		"			// fileT.contents here contains a matrix storing all the data of the text file" }, see = { "folder",
		"new_folder" })
	public static IGamaFile from(final IScope scope, final String s) throws GamaRuntimeException {
		if ( GamaFileType.isImageFile(s) ) { return imageFile(scope, s); }
		// if ( GamaFileType.isCSVFile(s) ) { return textFile(scope, s); }
		if ( GamaFileType.isTextFile(s) ) { return textFile(scope, s); }
		if ( GamaFileType.isProperties(s) ) { return propertyFile(scope, s); }
		if ( GamaFileType.isShape(s) ) { return shapeFile(scope, s); }
		if ( GamaFileType.isGrid(s) ) { return gridFile(scope, s); }
		if ( new File(s).isDirectory() ) { return folderFile(scope, s); }
		throw new GamaRuntimeException("Unknown file type: " + s);
	}

	@operator(value = IMAGE, can_be_const = true, index_type = IType.POINT)
	@doc(value = "opens a file that is a kind of image.", comment = "The file should have an image extension, cf. file type deifnition for supported file extensions.", special_cases = "If the specified string does not refer to an existing image file, an exception is risen.", examples = { "let fileT type: file value: image(\"../includes/testImage.png\");  // fileT represents the file \"../includes/testShape.png\"" }, see = {
		"file", "shapefile", "properties", "text" })
	public static IGamaFile imageFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GamaImageFile(scope, s);
	}

	@operator(value = TEXT, can_be_const = true, index_type = IType.INT)
	@doc(value = "opens a file that a is a kind of text.", comment = "The file should have a text extension, cf. file type definition for supported file extensions.", special_cases = "If the specified string does not refer to an existing text file, an exception is risen.", examples = {
		"let fileT type: file value: text(\"../includes/Stupid_Cell.Data\");",
		"				// fileT represents the text file \"../includes/Stupid_Cell.Data\"" }, see = { "file", "properties",
		"image", "shapefile" })
	public static IGamaFile textFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GamaTextFile(scope, s);
	}

	@operator(value = PROPERTIES, can_be_const = true, index_type = IType.STRING)
	@doc(value = "opens a file that is a kind of properties.", comment = "The file should have a properties extension, cf. type file definition for supported file extensions.", special_cases = "If the specified string does not refer to an existing propserites file, an exception is risen.", examples = { "let fileT type: file value: properties(\"../includes/testProperties.properties\");  // fileT represents the properties file \"../includes/testProperties.properties\"" }, see = {
		"file", "shapefile", "image", "text" })
	public static IGamaFile propertyFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GamaPropertyFile(scope, s);
	}

	@operator(value = SHAPE, can_be_const = true, index_type = IType.INT)
	@doc(value = "opens a file that a is a kind of shapefile.", comment = "The file should have a shapefile extension, cf. file type definition for supported file extensions.", special_cases = "If the specified string does not refer to an existing shapefile file, an exception is risen.", examples = {
		"let fileT type: file value: shapefile(\"../includes/testProperties.shp\");",
		"            // fileT represents the shapefile file \"../includes/testProperties.shp\"" }, see = { "file",
		"properties", "image", "text" })
	public static IGamaFile shapeFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GamaShapeFile(scope, s);
	}

	@operator(value = GRID, can_be_const = true, index_type = IType.INT)
	@doc(value = "opens a file that a is a kind of shapefile.", comment = "The file should have a gridfile extension, cf. file type definition for supported file extensions.", special_cases = "If the specified string does not refer to an existing gridfile file, an exception is risen.", examples = {
		"file fileT <- gridfile(\"../includes/testProperties.asc\");",
		"            // fileT represents the gridfile file \"../includes/testProperties.asc\"" }, see = { "file",
		"properties", "image", "text", "shapefile" })
	public static IGamaFile gridFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GamaGridFile(scope, s);
	}

	@operator(value = FOLDER, can_be_const = true, index_type = IType.INT)
	@doc(value = "opens an existing repository", special_cases = " If the specified string does not refer to an existing repository, an exception is risen.", examples = {
		"let dirT type: file value: folder(\"../includes/\");",
		"				// dirT represents the repository \"../includes/\"",
		"				// dirT.contents here contains the list of the names of included files" }, see = { "file", "new_folder" })
	public static IGamaFile folderFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GamaFolderFile(scope, s);
	}

	@operator(value = READ, type = ITypeProvider.FIRST_TYPE, content_type = ITypeProvider.FIRST_CONTENT_TYPE, index_type = ITypeProvider.FIRST_KEY_TYPE)
	@doc(value = "marks the file so that only read operations are allowed.", comment = "A file is created by default in read-only mode. The operator write can change the mode.", examples = { "read(shapefile(\"../images/point_eau.shp\"))  --:  returns a file in read-only mode representing \"../images/point_eau.shp\"" }, see = {
		"file", "write" })
	public static IGamaFile opRead(final IScope scope, final IGamaFile s) {
		s.setWritable(false);
		return s;
	}

	@operator(value = WRITE, type = ITypeProvider.FIRST_TYPE, content_type = ITypeProvider.FIRST_CONTENT_TYPE, index_type = ITypeProvider.FIRST_KEY_TYPE)
	@doc(value = "marks the file so that read and write operations are allowed.", comment = "A file is created by default in read-only mode.", examples = { "write(shapefile(\"../images/point_eau.shp\"))   --: returns a file in read-write mode representing \"../images/point_eau.shp\"" }, see = {
		"file", "read" })
	public static IGamaFile opWrite(final IScope scope, final IGamaFile s) {
		s.setWritable(true);
		return s;
	}

	/**
	 * Allows to read the value of an attribute stored in a GIS if the agent has been
	 * created from this GIS. Values are either conserved in a special subclass of GamaGeometry or
	 * available during creation time in the flow of features.
	 * 
	 * @param scope the current execution stack
	 * @param s the name of the attribute to read
	 * @return
	 */
	@operator(value = { READ, "get" })
	// @doc(
	// value = "",
	// comment = "",
	// special_cases = "",
	// examples = {""},
	// see = {""})
	public static Object opRead(final IScope scope, final String s) throws GamaRuntimeException {
		// First try to read in the temp attributes
		Map attributes = tempAttributes.peek();
		if ( attributes != null ) { return attributes.get(s); }
		// Then try to read in the agent, if it has been created from a GIS/CSV file.
		return opRead(scope, scope.getAgentScope(), s);
	}

	@operator(value = { READ, "get" })
	// @doc(
	// value = "",
	// comment = "",
	// special_cases = "",
	// examples = {""},
	// see = {""})
	public static Object opRead(final IScope scope, final Integer index) throws GamaRuntimeException {
		// First try to read in the temp attributes
		Map attributes = tempAttributes.peek();
		if ( attributes != null ) { return attributes.get(index); }
		// Try to read in the agent, if it has been created from a GIS/CSV file.
		IAgent g = scope.getAgentScope();
		return g.getAttribute(index);
	}

	@operator(value = "get")
	public static Object opRead(final IScope scope, final IAgent g, final String s) throws GamaRuntimeException {
		if ( g == null ) { return null; }
		return g.getAttribute(s);
	}

	@operator(value = "get")
	public static Object opRead(final IScope scope, final IShape g, final String s) throws GamaRuntimeException {
		if ( g == null ) { return null; }
		return ((GamaShape) g.getGeometry()).getAttribute(s);
	}

	@operator(value = { "new_folder" }, index_type = IType.INT, content_type = IType.STRING)
	@doc(value = "opens an existing repository or create a new folder if it does not exist.", comment = "", special_cases = " If the specified string does not refer to an existing repository, the repository is created. If the string refers to an existing file, an exception is risen.", examples = {
		"let dirNewT type: file value: new_folder(\"../incl/\");   	// dirNewT represents the repository \"../incl/\"",
		"															// eventually creates the directory ../incl" }, see = { "folder", "file" })
	public static IGamaFile newFolder(final IScope scope, final String folder) throws GamaRuntimeException {
		IModel model = scope.getSimulationScope().getModel();
		String theName;
		theName = model.getRelativeFilePath(folder, false);

		final File file = new File(theName);
		if ( file.exists() && !file.isDirectory() ) { throw new GamaRuntimeException("The folder " + folder +
			" can not overwrite a file with the same name"); }
		if ( !file.exists() ) {
			file.mkdir();
		}
		return new GamaFolderFile(scope, folder);

	}

}
