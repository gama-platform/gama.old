/*******************************************************************************************************
 *
 * Files.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.operators;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.utils.IOUtils;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.file.GamaFolderFile;
import msi.gama.util.file.IGamaFile;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 20 dec. 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "rawtypes", "restriction" })
public class Files {

	/** The Constant WRITE. */
	public static final String WRITE = "write";

	// @operator (
	// value = IKeyword.FILE,
	// can_be_const = true,
	// category = IOperatorCategory.FILE,
	// concept = { IConcept.FILE })
	// @doc (
	// value = "Creates a file in read/write mode, setting its contents to the container passed in parameter",
	// comment = "The type of container to pass will depend on the type of file (see the management of files in the
	// documentation). Can be used to copy files since files are considered as containers. For example: save
	// file('image_copy.png', file('image.png')); will copy image.png to image_copy.png")
	/**
	 * From.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @param container
	 *            the container
	 * @return the i gama file
	 */
	// @no_test
	public static IGamaFile from(final IScope scope, final String s, final IContainer container) {
		// WARNING Casting to Modifiable is not safe
		// TODO: Add a method toModifiableVersion() to IContainer
		final IType key = container == null ? Types.NO_TYPE : container.getGamlType().getKeyType();
		final IType content = container == null ? Types.NO_TYPE : container.getGamlType().getContentType();
		return (IGamaFile) Types.FILE.cast(scope, s, container, key, content, false);
	}

	//
	// @operator (
	// value = IKeyword.FILE,
	// can_be_const = true,
	// category = IOperatorCategory.FILE,
	// concept = { IConcept.FILE })
	// @doc (
	// value = "opens a file in read only mode, creates a GAML file object, and tries to determine and store the file
	// content in the contents attribute.",
	// comment = "The file should have a supported extension, see file type definition for supported file extensions.",
	// usages = @usage ("If the specified string does not refer to an existing file, an exception is risen when the
	// variable is used."),
	// examples = { @example (
	// value = "let fileT type: file value: file(\"../includes/Stupid_Cell.Data\"); "),
	// @example (
	// value = " // fileT represents the file \"../includes/Stupid_Cell.Data\""),
	// @example (
	// value = " // fileT.contents here contains a matrix storing all the data of the text file") },
	/**
	 * From.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @return the i gama file
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	// see = { "folder", "new_folder" })
	public static IGamaFile from(final IScope scope, final String s) throws GamaRuntimeException {
		return from(scope, s, null);
	}

	/**
	 * Exist file.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @return true, if successful
	 */
	@operator (
			value = "file_exists",
			can_be_const = false,
			category = IOperatorCategory.FILE,
			concept = { IConcept.FILE })
	@doc (
			value = "Test whether the parameter is the path to an existing file. False if it does not exist of if it is a folder",
			examples = { @example (
					value = "string file_name <-\"../includes/buildings.shp\";",
					isExecutable = false),
					@example (
							value = "		if file_exists(file_name){",
							isExecutable = false),
					@example (
							value = "			write \"File exists in the computer\";",
							isExecutable = false),
					@example (
							value = "	}",
							isExecutable = false) })
	@no_test
	public static boolean exist_file(final IScope scope, final String s) {
		if (s == null || scope == null) return false;
		final String path = FileUtils.constructAbsoluteFilePath(scope, s, false);
		final File f = new File(path);

		return f.exists() && !f.isDirectory();
	}

	/**
	 * Zip files.
	 *
	 * @param scope
	 *            the scope
	 * @param zipfile
	 *            the zipfile
	 * @param s
	 *            the s
	 * @return true, if successful
	 */
	@operator (
			value = "folder_exists",
			can_be_const = false,
			category = IOperatorCategory.FILE,
			concept = { IConcept.FILE })
	@doc (
			value = "Test whether the parameter is the path to an existing folder. False if it doesnt exist or if it is a file",
			examples = { @example (
					value = "string file_name <-\"../includes/\";",
					isExecutable = false),
					@example (
							value = "		if folder_exists(file_name){",
							isExecutable = false),
					@example (
							value = "			write \"Folder exists in the computer\";",
							isExecutable = false),
					@example (
							value = "	}",
							isExecutable = false) })
	@no_test
	public static boolean zip_files(final IScope scope, final String zipfile, final IList<String> s) {
		if (s == null || s.isEmpty() || scope == null) return false;

		return true;
	}

	/**
	 * Extract folder.
	 *
	 * @param scope
	 *            the scope
	 * @param zipFile
	 *            the zip file
	 * @param extractFolder
	 *            the extract folder
	 */
	public static void extractFolder(final IScope scope, final String zipFile, final String extractFolder) {
		try {
			int BUFFER = 2048;
			File file = new File(zipFile);

			try (ZipFile zip = new ZipFile(file)) {
				String newPath = extractFolder;

				new File(newPath).mkdir();
				Enumeration zipFileEntries = zip.entries();

				// Process each entry
				while (zipFileEntries.hasMoreElements()) {
					// grab a zip file entry
					ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
					String currentEntry = entry.getName();

					File destFile = new File(newPath, currentEntry);
					// destFile = new File(newPath, destFile.getName());
					File destinationParent = destFile.getParentFile();

					// create the parent directory structure if needed
					destinationParent.mkdirs();

					if (!entry.isDirectory()) {
						try (BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry))) {
							int currentByte;
							// establish buffer for writing file
							byte data[] = new byte[BUFFER];

							try (// write the current file to disk
									FileOutputStream fos = new FileOutputStream(destFile)) {
								try (BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER)) {
									// read and write until last byte is encountered
									while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
										dest.write(data, 0, currentByte);
									}
									dest.flush();
								}
							}
						}
					}

				}
			}
		} catch (Exception e) {
			GamaRuntimeException.error("ERROR: " + e.getMessage(), scope);
		}

	}

	/**
	 * Adds the folder to zip.
	 *
	 * @param folderOrFile
	 *            the folder or file
	 * @param zip
	 *            the zip
	 * @param baseName
	 *            the base name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void addFolderToZip(final File folderOrFile, final ZipOutputStream zip, final String baseName)
			throws IOException {
		File[] files;
		if (folderOrFile.isDirectory()) {
			files = folderOrFile.listFiles();
			if (files == null) return;
		} else {
			files = new File[1];
			files[0] = folderOrFile;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				addFolderToZip(file, zip, baseName);
			} else {
				String name = file.getAbsolutePath().substring(baseName.length());
				ZipEntry zipEntry = new ZipEntry(name);
				zip.putNextEntry(zipEntry);
				IOUtils.copy(new FileInputStream(file), zip);
				zip.closeEntry();
			}
		}
	}

	/**
	 * Delete.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @return true, if successful
	 */
	@operator (
			value = "delete_file",
			can_be_const = false,
			category = IOperatorCategory.FILE,
			concept = { IConcept.FILE })
	@doc (
			value = "delete a file or a folder",
			examples = { @example (
					value = "bool delete_file_ok <- delete_file([\"../includes/my_folder\"];",
					isExecutable = false) })
	@no_test
	public static boolean delete(final IScope scope, final String source) {
		if (source == null || scope == null) return false;
		final String pathSource = FileUtils.constructAbsoluteFilePath(scope, source, false);
		File file = new File(pathSource);
		if (!file.isDirectory()) return file.delete();
		deleteDir(file);
		return !exist_folder(scope, source);
	}
	
	
	/**
	 * Delete.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @return true, if successful
	 */
	@operator (
			value = "rename_file",
			can_be_const = false,
			category = IOperatorCategory.FILE,
			concept = { IConcept.FILE })
	@doc (
			value = "rename/move a file or a folder",
			examples = { @example (
					value = "bool rename_file_ok <- rename_file(\"../includes/my_folder\",\"../includes/my_new_folder\");",
					isExecutable = false) })
	@no_test
	public static boolean rename(final IScope scope, final String source, final String destination) {
		if (source == null || scope == null || destination == null) return false;
		final String pathSource = FileUtils.constructAbsoluteFilePath(scope, source, false);
		final String pathDest = FileUtils.constructAbsoluteFilePath(scope, destination, false);
		File sourceFile = new File(pathSource);
		File destFile = new File(pathDest);
		return sourceFile.renameTo(destFile) ;
	}

	/**
	 * Delete.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @return true, if successful
	 */
	@operator (
			value = "copy_file",
			can_be_const = false,
			category = IOperatorCategory.FILE,
			concept = { IConcept.FILE })
	@doc (
			value = "copy a file or a folder",
			examples = { @example (
					value = "bool copy_file_ok <- copy_file(\"../includes/my_folder\",\"../includes/my_new_folder\",true);",
					isExecutable = false),
			})
	@no_test
	public static boolean copy(final IScope scope, final String source, final String destination, final boolean replace) {
		if (source == null || scope == null || destination == null) return false;
		final String pathSource = FileUtils.constructAbsoluteFilePath(scope, source, false);
		final String pathDest = FileUtils.constructAbsoluteFilePath(scope, destination, false);
		File file = new File(pathSource);
		if (!file.isDirectory()) {
			Path dest = null;
			try {
				if (replace){
					dest = java.nio.file.Files.copy(Paths.get(pathSource), Paths.get(pathDest),java.nio.file.StandardCopyOption.REPLACE_EXISTING);
				}else {
					dest = java.nio.file.Files.copy(Paths.get(pathSource), Paths.get(pathDest));					
				}
			} catch (IOException e) {
				GamaRuntimeException.error("Error when copying the file " + e.getMessage(), scope);
			}
			return (dest != null) && (dest.toFile().exists());
		}
		 try {
			java.nio.file.Files.walk(Paths.get(pathSource))
			  .forEach(s -> {
			      Path dest = Paths.get(pathDest, s.toString()
			        .substring(pathSource.length()));
			      try {
			    	  if (replace) {
			    		  java.nio.file.Files.copy(s, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			    	  } else {
			    		  java.nio.file.Files.copy(s, dest);			    		  
			    	  }
			      } catch (IOException e) {
			          e.printStackTrace();
			      }
			  });
		} catch (IOException e) {
			GamaRuntimeException.error("Error when copying the folder " + e.getMessage(), scope);
		}
		return !exist_folder(scope, destination);
	}
	
	@operator (
			value = "copy_file",
			can_be_const = false,
			category = IOperatorCategory.FILE,
			concept = { IConcept.FILE })
	@doc (
			value = "copy a file or a folder",
			examples = { 
						@example (
						value = "bool copy_file_ok <- copy_file(\"../includes/my_folder\",\"../includes/my_new_folder\");",
						isExecutable = false) 
			})
	
	public static boolean copy(final IScope scope, final String source, final String destination) {
		return copy(scope, source, destination, false);
	}


	/**
	 * Delete dir.
	 *
	 * @param file
	 *            the file
	 */
	static void deleteDir(final File file) {
		if (file.isDirectory()) {
			File[] contents = file.listFiles();
			if (contents != null) {
				for (File f : contents) { if (!java.nio.file.Files.isSymbolicLink(f.toPath())) { deleteDir(f); } }
			}
		}
		file.delete();
	}

	/**
	 * Unzip.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param destination
	 *            the destination
	 * @return true, if successful
	 */
	@operator (
			value = "unzip",
			can_be_const = false,
			category = IOperatorCategory.FILE,
			concept = { IConcept.FILE })
	@doc (
			value = "Unzip a given zip file into a given folder. Returns true if the file is well unzipped",
			examples = { @example (
					value = "bool unzip_ok <- unzip([\"../includes/my_folder\"], \"folder.zip\";",
					isExecutable = false) })
	@no_test
	public static boolean unzip(final IScope scope, final String source, final String destination) {
		if (source == null || !exist_file(scope, source) || destination == null || scope == null) return false;
		final String pathDestination = FileUtils.constructAbsoluteFilePath(scope, destination, false);
		final String pathSource = FileUtils.constructAbsoluteFilePath(scope, source, false);

		extractFolder(scope, pathSource, pathDestination);
		return true;
	}

	/**
	 * Zip.
	 *
	 * @param scope
	 *            the scope
	 * @param sources
	 *            the sources
	 * @param destination
	 *            the destination
	 * @return true, if successful
	 */
	@operator (
			value = "zip",
			can_be_const = false,
			category = IOperatorCategory.FILE,
			concept = { IConcept.FILE })
	@doc (
			value = "Zip a given list of files or folders. Returns true if the files are well zipped",
			examples = { @example (
					value = "bool zip_ok <- zip([\"../includes/my_folder\"], \"folder.zip\";",
					isExecutable = false) })
	@no_test
	public static boolean zip(final IScope scope, final IList<String> sources, final String destination) {
		if (sources == null || sources.isEmpty() || destination == null || scope == null) return false;
		final String pathDestination = FileUtils.constructAbsoluteFilePath(scope, destination, false);
		try (FileOutputStream fos = new FileOutputStream(pathDestination)) {
			try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(fos))) {
				for (String source : sources) {
					final String pathSource = FileUtils.constructAbsoluteFilePath(scope, source, false);
					File f = new File(pathSource);
					addFolderToZip(f, zip, f.getParentFile().getAbsolutePath());
				}

			}
		} catch (IOException e) {
			GamaRuntimeException.error(e.getMessage(), scope);
		}
		return true;
	}

	/**
	 * Exist folder.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @return true, if successful
	 */
	@operator (
			value = "folder_exists",
			can_be_const = false,
			category = IOperatorCategory.FILE,
			concept = { IConcept.FILE })
	@doc (
			value = "Test whether the parameter is the path to an existing folder. False if it doesnt exist or if it is a file",
			examples = { @example (
					value = "string file_name <-\"../includes/\";",
					isExecutable = false),
					@example (
							value = "		if folder_exists(file_name){",
							isExecutable = false),
					@example (
							value = "			write \"Folder exists in the computer\";",
							isExecutable = false),
					@example (
							value = "	}",
							isExecutable = false) })
	@no_test
	public static boolean exist_folder(final IScope scope, final String s) {
		if (s == null || scope == null) return false;
		final String path = FileUtils.constructAbsoluteFilePath(scope, s, false);
		final File f = new File(path);

		return f.exists() && f.isDirectory();
	}

	/**
	 * Folder file.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @return the i gama file
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = IKeyword.FOLDER,
			can_be_const = false,
			index_type = IType.INT,
			category = IOperatorCategory.FILE,
			concept = { IConcept.FILE },
			content_type = IType.STRING)
	@doc (
			value = "opens an existing repository",
			usages = @usage ("If the specified string does not refer to an existing repository, an exception is risen."),
			examples = {
					// @example(value = "folder(\"../includes/\")", raises = "error"),
					@example (
							value = "file dirT <- folder(\"../includes/\");",
							isExecutable = false),
					@example (
							value = "				// dirT represents the repository \"../includes/\""),
					@example (
							value = "				// dirT.contents here contains the list of the names of included files") },
			see = { "file", "new_folder" })
	@no_test
	public static IGamaFile folderFile(final IScope scope, final String s) throws GamaRuntimeException {
		return new GamaFolderFile(scope, s);
	}

	/**
	 * Folder file.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @param modify
	 *            the modify
	 * @return the i gama file
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static IGamaFile folderFile(final IScope scope, final String s, final boolean modify)
			throws GamaRuntimeException {
		return new GamaFolderFile(scope, s, modify);
	}

	/**
	 * Writable.
	 *
	 * @param scope
	 *            the scope
	 * @param s
	 *            the s
	 * @param writable
	 *            the writable
	 * @return the i gama file
	 */
	@operator (
			value = "writable",
			category = IOperatorCategory.FILE,
			concept = { IConcept.FILE },
			index_type = ITypeProvider.KEY_TYPE_AT_INDEX + 1,
			content_type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1)
	@doc (
			value = "Marks the file as read-only or not, depending on the second boolean argument, and returns the first argument",
			comment = "A file is created using its native flags. This operator can change them. Beware that this change is system-wide (and not only restrained to GAMA): changing a file to read-only mode (e.g. \"writable(f, false)\")",
			examples = { @example (
					value = "shape_file(\"../images/point_eau.shp\") writable false",
					equals = "returns a file in read-only mode",
					test = false) },
			see = "file")
	@no_test
	public static IGamaFile writable(final IScope scope, final IGamaFile s, final Boolean writable) {
		if (s == null) throw GamaRuntimeException.error("Attempt to change the mode of a non-existent file", scope);
		final boolean b = writable == null ? false : writable;
		s.setWritable(scope, b);
		return s;
	}

	/**
	 * Allows to read the value of an attribute stored in a GIS if the agent has been created from this GIS. Values are
	 * either conserved in a special subclass of GamaGeometry or available during creation time in the flow of features.
	 *
	 * @param scope
	 *            the current execution stack
	 * @param s
	 *            the name of the attribute to read
	 * @return
	 */
	@operator (
			value = { "read", "get" },
			category = IOperatorCategory.FILE,
			concept = { IConcept.ATTRIBUTE, IConcept.FILE })
	@doc (
			value = "Reads an attribute of the agent. The attribute's name is specified by the operand.",
			masterDoc = true,
			examples = { @example (
					var = "agent_name",
					value = "read ('name')",
					equals = "reads the 'name' variable of agent then assigns the returned value to the 'agent_name' variable. ",
					test = false) })
	@no_test
	public static Object opRead(final IScope scope, final String s) throws GamaRuntimeException {
		// First try to read in the temp attributes
		final Map attributes = scope.peekReadAttributes();
		if (attributes != null) return attributes.get(s);
		// Then try to read in the agent, if it has been created from a GIS/CSV
		// file.
		return opRead(scope, scope.getAgent(), s);
	}

	/**
	 * Op read.
	 *
	 * @param scope
	 *            the scope
	 * @param g
	 *            the g
	 * @param s
	 *            the s
	 * @return the object
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "get",
			category = IOperatorCategory.CONTAINER,
			concept = { IConcept.CONTAINER, IConcept.SPECIES, IConcept.ATTRIBUTE })
	// @doc(examples = {
	// "let agent_name value: an_agent get ('name'); --: reads the 'name'
	// variable of agent then assigns the returned value to the
	// 'second_variable' variable."
	// })
	@doc (
			value = "Reads an attribute of the specified agent (or geometry) (left operand). The attribute name is specified by the right operand.",
			masterDoc = true,
			usages = { @usage (
					value = "Reading the attribute of another agent",
					examples = @example (
							value = "string agent_name <- an_agent get('name');     // reads then 'name' attribute of an_agent then assigns the returned value to the agent_name variable",
							isExecutable = false)) })
	@no_test
	public static Object opRead(final IScope scope, final IAgent g, final String s) throws GamaRuntimeException {
		if (g == null) return null;
		return g.get(scope, s);
	}

	/**
	 * Op read.
	 *
	 * @param scope
	 *            the scope
	 * @param g
	 *            the g
	 * @param s
	 *            the s
	 * @return the object
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = "get",
			category = IOperatorCategory.FILE,
			concept = { IConcept.GEOMETRY })
	// @doc(examples = {
	// "let geom_area value: a_geometry get ('area'); --: reads the 'area'
	// attribute of the 'a_geometry' geometry then assigns the returned value to
	// the 'geom_area' variable."
	// })
	@doc (
			value = "Reads an attribute of the specified geometry (left operand). The attribute name is specified by the right operand.",
			usages = { @usage (
					value = "Reading the attribute of a geometry",
					examples = @example (
							value = "string geom_area <- a_geometry get('area');     // reads then 'area' attribute of 'a_geometry' variable then assigns the returned value to the geom_area variable",
							isExecutable = false)) })
	@no_test
	public static Object opRead(final IScope scope, final IShape g, final String s) throws GamaRuntimeException {
		if (g == null) return null;
		return ((GamaShape) g.getGeometry()).getAttribute(s);
	}

	/**
	 * New folder.
	 *
	 * @param scope
	 *            the scope
	 * @param folder
	 *            the folder
	 * @return the i gama file
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = { "new_folder" },
			index_type = IType.INT,
			content_type = IType.STRING,
			category = IOperatorCategory.FILE,
			concept = { IConcept.FILE })
	@doc (
			value = "opens an existing repository or create a new folder if it does not exist.",
			comment = "",
			usages = {
					@usage ("If the specified string does not refer to an existing repository, the repository is created."),
					@usage ("If the string refers to an existing file, an exception is risen.") },
			examples = {
					@example ("file dirNewT <- new_folder(\"incl/\");   	// dirNewT represents the repository \"../incl/\""),
					@example ("															// eventually creates the directory ../incl") },
			see = { "folder", "file", "folder_exists" })
	public static IGamaFile newFolder(final IScope scope, final String folder) throws GamaRuntimeException {
		String theName;
		theName = FileUtils.constructAbsoluteFilePath(scope, folder, false);

		final File file = new File(theName);
		if (file.exists() && !file.isDirectory()) throw GamaRuntimeException
				.error("The folder " + folder + " can not overwrite a file with the same name", scope);
		if (!file.exists()) { file.mkdirs(); }
		return new GamaFolderFile(scope, folder);

	}

}
