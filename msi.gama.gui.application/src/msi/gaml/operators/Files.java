/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import java.io.File;
import msi.gama.environment.GisUtil;
import msi.gama.interfaces.*;
import msi.gama.internal.types.GamaFileType;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.util.*;
import msi.gama.util.file.*;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Written by drogoul Modified on 20 déc. 2010
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
	public static final String READ = "read";
	public static final String WRITE = "write";

	@operator(value = ISymbol.FILE)
	public static IGamaFile from(final IScope scope, final String s) throws GamaRuntimeException {
		if ( GamaFileType.isImageFile(s) ) { return imageFile(scope, s); }
		if ( GamaFileType.isTextFile(s) ) { return textFile(scope, s); }
		if ( GamaFileType.isProperties(s) ) { return propertyFile(scope, s); }
		if ( GamaFileType.isShape(s) ) { return shapeFile(scope, s); }
		if ( new File(s).isDirectory() ) { return folderFile(scope, s); }
		throw new GamaRuntimeException("Unknown file type: " + s);
	}

	@operator(value = IMAGE)
	public static IGamaFile imageFile(final IScope scope, final String s)
		throws GamaRuntimeException {
		return new GamaImageFile(scope, s);
	}

	@operator(value = TEXT)
	public static IGamaFile textFile(final IScope scope, final String s)
		throws GamaRuntimeException {
		return new GamaTextFile(scope, s);
	}

	@operator(value = PROPERTIES)
	public static IGamaFile propertyFile(final IScope scope, final String s)
		throws GamaRuntimeException {
		return new GamaPropertyFile(scope, s);
	}

	@operator(value = SHAPE)
	public static IGamaFile shapeFile(final IScope scope, final String s)
		throws GamaRuntimeException {
		return new GamaShapeFile(scope, s);
	}

	@operator(value = FOLDER)
	public static IGamaFile folderFile(final IScope scope, final String s)
		throws GamaRuntimeException {
		return new GamaFolderFile(scope, s);
	}

	@operator(value = READ)
	public static Object opRead(final IScope scope, final IGamaFile s) {
		s.setWritable(false);
		return s;
	}

	@operator(value = WRITE)
	public static Object opWrite(final IScope scope, final IGamaFile s) {
		s.setWritable(true);
		return s;
	}

	/**
	 * Allows to read the value of an attribute stored in a GIS if the agent has been created from
	 * this GIS. Values are either conserved in a special subclass of GamaGeometry or available
	 * during creation time in the flow of features.
	 * 
	 * @param scope the current execution stack
	 * @param s the name of the attribute to read
	 * @return
	 */
	@operator(value = READ)
	public static Object opRead(final IScope scope, final String s) {
		// First try to read in the geometry of the agent, if it has been created from a GIS file.
		GamaGeometry g = scope.getAgentScope().getGeometry();
		if ( g instanceof GamaGisGeometry ) { return ((GamaGisGeometry) g).getAttribute(s); }
		// Otherwise, we may be in a process of creating agents, which requires directly reading the
		// feature. Both processes will be unified later.
		SimpleFeature gisReader = GisUtil.getCurrentGisReader();
		if ( gisReader != null ) { return gisReader.getAttribute(s); }
		return null;
	}

	@operator(value = { "new_folder" })
	public static IGamaFile newFolder(final IScope scope, final String folder)
		throws GamaRuntimeException {
		IModel model = scope.getSimulationScope().getModel();
		String theName;
		theName = model.getRelativeFilePath(folder, false);

		final File file = new File(theName);
		if ( file.exists() && !file.isDirectory() ) { throw new GamaRuntimeException("The folder " +
			folder + " can not overwrite a file with the same name"); }
		if ( !file.exists() ) {
			file.mkdir();
		}
		return new GamaFolderFile(scope, folder);

	}

}
