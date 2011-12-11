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
package msi.gama.internal.types;

import java.io.*;
import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.util.file.IGamaFile;
import msi.gaml.operators.Files;

/**
 * Written by drogoul Modified on 1 aožt 2010
 * 
 * @todo Description
 * 
 */
@type(value = IType.FILE_STR, id = IType.FILE, wraps = { IGamaFile.class, File.class })
public class GamaFileType extends GamaType<IGamaFile> {

	/** Constant field textSuffixes. */
	private static final List<String> textSuffixes = Arrays.asList(".txt", ".data", ".csv",
		".text", ".tsv", ".asc");
	private static final String shpSuffix = ".shp";
	// To be used later, to load geometries ?
	private static final String propertiesSuffix = ".properties";
	/** Constant field imageSuffixes. */
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

	@Override
	public IGamaFile getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.get(NONE);
	}

	/**
	 * Checks if is absolute path.
	 * 
	 * @param filePath the file path
	 * 
	 * @return true, if is absolute path
	 */
	static boolean isAbsolutePath(final String filePath) {
		final File[] roots = File.listRoots();
		for ( int i = 0; i < roots.length; i++ ) {
			if ( filePath.startsWith(roots[i].getAbsolutePath()) ) { return true; }
		}
		return false;
	}

	/**
	 * Removes a root.
	 * 
	 * @param absoluteFilePath the absolute file path
	 * 
	 * @return the string
	 */
	static String removeRoot(final String absoluteFilePath) {
		// OutputManager.debug("absoluteFilePath before = " + absoluteFilePath);

		final File[] roots = File.listRoots();
		for ( int i = 0; i < roots.length; i++ ) {
			if ( absoluteFilePath.startsWith(roots[i].getAbsolutePath()) ) { return absoluteFilePath
				.substring(roots[i].getAbsolutePath().length(), absoluteFilePath.length()); }
		}
		return absoluteFilePath;
	}

	/**
	 * Construct absolute file path.
	 * 
	 * @param filePath the file path
	 * @param mustExist the must exist
	 * 
	 * @return the string
	 * 
	 * @throws GamlException the gaml exception
	 */
	static public String constructAbsoluteFilePath(final String filePath,
		final String referenceFile, final boolean mustExist) throws GamlException {
		String baseDirectory = new File(referenceFile).getParent();
		final GamlException ex =
			new GamlException("File denoted by " + filePath +
				" not found! Tried the following paths : ");
		File file = null;
		if ( isAbsolutePath(filePath) ) {
			file = new File(filePath);
			if ( file.exists() || !mustExist ) {
				try {
					return file.getCanonicalPath();
				} catch (final IOException e) {
					e.printStackTrace();
					return file.getAbsolutePath();
				}
			}
			ex.addContext(file.getAbsolutePath());
			file = new File(baseDirectory + File.separator + removeRoot(filePath));

			if ( file.exists() ) {
				try {
					return file.getCanonicalPath();
				} catch (final IOException e) {
					e.printStackTrace();
					return file.getAbsolutePath();
				}
			}
			ex.addContext(file.getAbsolutePath());
		} else {
			file = new File(baseDirectory + File.separatorChar + filePath);
			if ( file.exists() || !mustExist ) {
				try {
					return file.getCanonicalPath();
				} catch (final IOException e) {
					e.printStackTrace();
					return file.getAbsolutePath();
				}
			}
			ex.addContext(file.getAbsolutePath());
		}

		throw ex;
	}

	@operator(value = "is_text")
	public static Boolean isTextFile(final String f) {
		final String fn = f.toLowerCase();
		for ( final String s : textSuffixes ) {
			if ( fn.contains(s) ) { return true; }
		}
		return false;
	}

	@operator(value = "is_properties")
	public static Boolean isProperties(final String f) {
		return f.toLowerCase().contains(propertiesSuffix);
	}

	@operator(value = "is_shape")
	public static Boolean isShape(final String f) {
		return f.toLowerCase().contains(shpSuffix);
	}

	@operator(value = "is_image")
	public static Boolean isImageFile(final String f) {
		final String fn = f.toLowerCase();
		for ( final String s : imageSuffixes ) {
			if ( fn.contains(s) ) { return true; }
		}
		return false;
	}

}
