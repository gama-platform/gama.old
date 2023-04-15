/*******************************************************************************************************
 *
 * SaveStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ISaveDelegate;
import msi.gama.common.util.FileUtils;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IModifiableContainer;
import msi.gama.util.file.GamaFile.FlushBufferException;
import msi.gama.util.file.IGamaFile;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.data.MapExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.SaveStatement.SaveValidator;
import msi.gaml.statements.save.ImageSaver;
import msi.gaml.types.GamaFileType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class SaveStatement.
 */

/**
 * The Class SaveStatement.
 */
@symbol (
		name = IKeyword.SAVE,
		kind = ISymbolKind.SINGLE_STATEMENT,
		concept = { IConcept.FILE, IConcept.SAVE_FILE },
		with_sequence = true, // necessary to allow declaring the attributes facet as remote itself
		// with_args = true,
		remote_context = true)
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.ACTION })
@facets (
		value = { @facet (
				name = IKeyword.FORMAT,
				type = IType.ID,
				optional = true,
				doc = @doc (
						value = "a string representing the format of the output file (e.g. \"shp\", \"asc\", \"geotiff\", \"png\", \"text\", \"csv\"). If the file extension is non ambiguous in facet 'to:', this format does not need to be specified. However, in many cases, it can be useful to do it (for instance, when saving a string to a .pgw file, it is always better to clearly indicate that the expected format is 'text'). ")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.ID,
						optional = true,

						doc = @doc (
								deprecated = "Use 'format' instead",
								value = "a string representing the type of the output file (e.g. \"shp\", \"asc\", \"geotiff\", \"png\", \"text\", \"csv\") ")),
				@facet (
						name = IKeyword.DATA,
						type = IType.NONE,
						optional = true,
						doc = @doc ("the data that will be saved to the file or the file itself to save when data is used in its simplest form")),
				@facet (
						name = IKeyword.REWRITE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("a boolean expression specifying whether to erase the file if it exists or append data at the end of it. Only applicable to \"text\" or \"csv\" files. Default is true")),
				@facet (
						name = IKeyword.HEADER,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("an expression that evaluates to a boolean, specifying whether the save will write a header if the file does not exist")),
				@facet (
						name = IKeyword.TO,
						type = IType.STRING,
						optional = true,
						doc = @doc ("an expression that evaluates to an string, the path to the file, or directly to a file")),
				@facet (
						name = "crs",
						type = IType.NONE,
						optional = true,
						doc = @doc ("the name of the projection, e.g. crs:\"EPSG:4326\" or its EPSG id, e.g. crs:4326. Here a list of the CRS codes (and EPSG id): http://spatialreference.org")),
				@facet (
						name = IKeyword.ATTRIBUTES,
						type = { IType.MAP, IType.LIST },
						remote_context = true,
						optional = true,
						doc = @doc (
								value = "Allows to specify the attributes of a shape file or GeoJson file where agents are saved. Can be expressed as a list of string or as a literal map. When expressed as a list, each value should represent the name of an attribute of the shape or agent. The keys of the map are the names of the attributes that will be present in the file, the values are whatever expressions neeeded to define their value. ")),
				@facet (
						name = IKeyword.WITH,
						type = { IType.MAP },
						optional = true,
						doc = @doc (
								deprecated = "Please use 'attributes:' instead",
								value = "Allows to define the attributes of a shape file. Keys of the map are the attributes of agents to save, values are the names of attributes in the shape file")) },
		omissible = IKeyword.DATA)
@doc (
		value = "Allows to save data in a file. The type of file can be \"shp\", \"asc\", \"geotiff\", \"text\" or \"csv\".",
		usages = { @usage (
				value = "Its simple syntax is:",
				examples = { @example (
						value = "save data to: output_file type: a_type_file;",
						isExecutable = false) }),
				@usage (
						value = "To save data in a text file:",
						examples = { @example (
								value = "save (string(cycle) + \"->\"  + name + \":\" + location) to: \"save_data.txt\" type: \"text\";") }),
				@usage (
						value = "To save the values of some attributes of the current agent in csv file:",
						examples = { @example (
								value = "save [name, location, host] to: \"save_data.csv\" type: \"csv\";") }),
				@usage (
						value = "To save the values of all attributes of all the agents of a species into a csv (with optional attributes):",
						examples = { @example (
								value = "save species_of(self) to: \"save_csvfile.csv\" type: \"csv\" header: false;") }),
				@usage (
						value = "To save the geometries of all the agents of a species into a shapefile (with optional attributes):",
						examples = { @example (
								value = "save species_of(self) to: \"save_shapefile.shp\" type: \"shp\" attributes: ['nameAgent'::name, 'locationAgent'::location] crs: \"EPSG:4326\";") }),
				@usage (
						value = "To save the grid_value attributes of all the cells of a grid into an ESRI ASCII Raster file:",
						examples = { @example (
								value = "save grid to: \"save_grid.asc\" type: \"asc\";") }),
				@usage (
						value = "To save the grid_value attributes of all the cells of a grid into geotiff:",
						examples = { @example (
								value = "save grid to: \"save_grid.tif\" type: \"geotiff\";") }),
				@usage (
						value = "To save the grid_value attributes of all the cells of a grid into png (with a worldfile):",
						examples = { @example (
								value = "save grid to: \"save_grid.png\" type: \"image\";") }),
				@usage (
						value = "The save statement can be use in an init block, a reflex, an action or in a user command. Do not use it in experiments.") })
@validator (SaveValidator.class)
@SuppressWarnings ({ "rawtypes" })
public class SaveStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	/** The Constant NON_SAVEABLE_ATTRIBUTE_NAMES. */
	public static final Set<String> NON_SAVEABLE_ATTRIBUTE_NAMES =
			Set.of(IKeyword.PEERS, IKeyword.LOCATION, IKeyword.HOST, IKeyword.AGENTS, IKeyword.MEMBERS, IKeyword.SHAPE);

	/** The Constant EPSG_LABEL. */
	private static final String EPSG_LABEL = "EPSG:";

	/** The Constant DELEGATES_BY_GAML_TYPE. */
	private static final Map<String, Map<IType, ISaveDelegate>> DELEGATES = new HashMap<>();

	/**
	 * @param createExecutableExtension
	 */
	public static void addDelegate(final ISaveDelegate delegate) {
		Set<String> files = delegate.getFileTypes();
		final IType t = delegate.getDataType();
		for (String f : files) {
			Map<IType, ISaveDelegate> map = DELEGATES.get(f);
			if (map == null) {
				map = new HashMap<>();
				DELEGATES.put(f, map);
			}
			if (map.containsKey(t)) {
				DEBUG.LOG("WARNING: Extensions to SaveStatement already registered for file type " + f
						+ " and data type " + t);
			}
			map.put(t, delegate);

		}

	}

	/**
	 * The Class SaveValidator.
	 */
	public static class SaveValidator implements IDescriptionValidator<StatementDescription> {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final StatementDescription description) {

			final StatementDescription desc = description;
			final Facets with = desc.getPassedArgs();
			final IExpression att = desc.getFacetExpr(ATTRIBUTES);
			final IExpressionDescription type = desc.getFacet(FORMAT, TYPE);
			desc.removeFacets(TYPE);
			if (type != null) { desc.setFacet(FORMAT, type); }
			final IExpression format = type == null ? null : type.getExpression();

			final IExpression data = desc.getFacetExpr(DATA);
			if (data == null) return;
			final IType<?> dataType = data.getGamlType();
			final IExpression to = desc.getFacetExpr(TO);

			boolean isAFile = Types.FILE.isAssignableFrom(dataType);
			boolean hasTo = to != null;
			String ext = null;
			if (hasTo && to.isConst()) { ext = com.google.common.io.Files.getFileExtension(to.literalValue()); }
			boolean hasFormat = format != null;

			if (isAFile && hasTo) {
				desc.warning("The destination will not be taking into account when saving an already existing file",
						IGamlIssue.UNMATCHED_OPERANDS);
			}
			if (isAFile && hasFormat) {
				desc.warning("The file format will not be taken into account when saving an already existing file ",
						IGamlIssue.CONFLICTING_FACETS, FORMAT);
			}

			if (!isAFile && !hasTo) {
				desc.error("No file specified", IGamlIssue.MISSING_FACET);
				return;
			}

			if (!isAFile && !hasFormat && hasTo && ext != null && !DELEGATES.containsKey(ext)) {
				if (dataType != Types.STRING && dataType != Types.INT && dataType != Types.FLOAT) {
					desc.error("Unknown file extension. Accepted formats are: "
							+ DELEGATES.keySet().stream().sorted().toList(), IGamlIssue.UNKNOWN_ARGUMENT, TO);
					return;
				}
				desc.warning("Unknown file format, will default to 'text'. Accepted formats are: "
						+ DELEGATES.keySet().stream().sorted().toList(), IGamlIssue.UNKNOWN_ARGUMENT, TO);
			}

			if (!isAFile && !hasFormat && hasTo) {
				desc.info(
						"'save' will use the extension of the file to determine its format. If you are unsure about this, please specify the format of the file using the 'format:' facet",
						IGamlIssue.UNKNOWN_ARGUMENT);
			}

			if (!isAFile && hasFormat && hasTo) {
				String id = format.literalValue();
				if (!DELEGATES.containsKey(id)) {
					desc.error(
							"Unknown file format. Accepted formats are: "
									+ DELEGATES.keySet().stream().sorted().toList(),
							IGamlIssue.UNKNOWN_ARGUMENT, FORMAT);
					return;
				}
				if (ext != null && !id.equals(ext) && (!IMAGE.equals(id) || !ImageSaver.FILE_FORMATS.contains(ext))) {
					desc.info("The extension of the file and the format differ. Make sure they are compatible",
							IGamlIssue.CONFLICTING_FACETS);
				}

			}

			final boolean isMap = att instanceof MapExpression;
			if (att != null) {
				if (!isMap && !att.getGamlType().isTranslatableInto(Types.LIST.of(Types.STRING))) {
					desc.error("attributes must be expressed as a map<string, unknown> or as a list<string>",
							IGamlIssue.WRONG_TYPE, ATTRIBUTES);
					return;
				}
				if (isMap) {
					final MapExpression map = (MapExpression) att;
					if (map.getGamlType().getKeyType() != Types.STRING) {
						desc.error(
								"The type of the keys of the attributes map must be string. These will be used for naming the attributes in the file",
								IGamlIssue.WRONG_TYPE, ATTRIBUTES);
						return;
					}
				}

				if (with.exists()) {
					desc.warning(
							"'with' and 'attributes' are mutually exclusive. Only the first one will be considered",
							IGamlIssue.CONFLICTING_FACETS, ATTRIBUTES, WITH);
				}

				if (ext != null && format == null && !"shp".equals(ext) && !"json".equals(ext) || format != null
						&& !"shp".equals(format.literalValue()) && !"json".equals(format.literalValue())) {
					desc.warning("Attributes can only be defined for shape or json files", IGamlIssue.WRONG_TYPE,
							ATTRIBUTES);
				}

			}

			/** The t. */
			final IType<?> t = dataType.getContentType();

			/** The species. */
			final SpeciesDescription species = t.getSpecies();

			if (att == null && !with.exists()) return;

			if (species == null) {
				if (with.exists() || isMap) {
					desc.error("Attributes of geometries can only be specified with a list of attribute names",
							IGamlIssue.UNKNOWN_FACET, att == null ? WITH : ATTRIBUTES);
				}
				// Error deactivated for fixing #2982.
				// desc.error("Attributes can only be saved for agents", IGamlIssue.UNKNOWN_FACET,
				// att == null ? WITH : ATTRIBUTES);
			} else {
				with.forEachFacet((name, exp) -> {
					if (!species.hasAttribute(name)) {
						desc.error("Attribute " + name + " is not defined for the agents of " + data.serialize(false),
								IGamlIssue.UNKNOWN_VAR, WITH);
						return false;
					}
					return true;
				});
			}
		}

	}

	/** The with facet. */
	private Arguments withFacet;

	/** The attributes facet. */
	private final IExpression attributesFacet;

	/** The item. */
	private final IExpression item;

	/** The file. */
	private final IExpression file;

	/** The rewrite expr. */
	private final IExpression rewriteExpr;

	/**
	 * Instantiates a new save statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public SaveStatement(final IDescription desc) {
		super(desc);
		item = desc.getFacetExpr(IKeyword.DATA);
		file = getFacet(IKeyword.TO);
		rewriteExpr = getFacet(IKeyword.REWRITE);
		attributesFacet = getFacet(IKeyword.ATTRIBUTES);
	}

	/**
	 * Should overwrite.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 */
	private boolean shouldOverwrite(final IScope scope) {
		if (rewriteExpr == null) return true;
		return Cast.asBool(scope, rewriteExpr.value(scope));
	}

	@SuppressWarnings ("unchecked")
	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if (item == null) return null;
		// First case: we have a file as item;
		if (file == null) {
			if (!Types.FILE.isAssignableFrom(item.getGamlType())) return null;
			final IGamaFile theFile = (IGamaFile) item.value(scope);
			if (theFile != null) {
				// Passes directly the facets of the statement, like crs, etc.
				theFile.save(scope, description.getFacets());
			}
			return theFile;
		}
		final String fileName = Cast.asString(scope, file.value(scope));
		final String filePath = FileUtils.constructAbsoluteFilePath(scope, fileName, false);
		if (filePath == null || "".equals(filePath)) return null;
		final File fileToSave = new File(filePath);
		String typeExp = getLiteral(IKeyword.FORMAT);
		// Second case: a filename is indicated but not the type. In that case,
		// we try to build a new GamaFile from it and save it
		if (typeExp == null) {
			final Object contents = item.value(scope);
			if (contents instanceof IModifiableContainer mc) {
				try {
					// We set a temporary flag to the scope, which should be readable by the GamaFile and indicate that
					// the file is created "for saving" (and not reading). Otherwise it might create an exception if the
					// file does not exist already (see #3684)
					scope.setData(IGamaFile.KEY_TEMPORARY_OUTPUT, true);
					final IGamaFile f = GamaFileType.createFile(scope, fileName, mc);
					f.save(scope, description.getFacets());
					return f;
				} catch (FlushBufferException e) {
					// Nothing to do : the corresponding GamaFile does not implement flushBuffer
					// Not really clean but well... see #3684. We silently log the error and continue with the format
					DEBUG.OUT(e.getMessage());
				} finally {
					// We remove the temporary flag
					scope.setData(IGamaFile.KEY_TEMPORARY_OUTPUT, null);
				}
			}
			typeExp = com.google.common.io.Files.getFileExtension(fileName);

		}

		try {
			Files.createDirectories(fileToSave.toPath().getParent());
			boolean exists = fileToSave.exists();
			final boolean rewrite = shouldOverwrite(scope);
			if (rewrite && exists) {
				fileToSave.delete();
				exists = false;
			}
			IExpression header = getFacet(IKeyword.HEADER);
			final boolean addHeader = !exists && (header == null || Cast.asBool(scope, header.value(scope)));
			final String type = (typeExp != null ? typeExp : "text").trim().toLowerCase();
			String code = null;
			IExpression crsCode = getFacet("crs");
			if (crsCode != null) {
				final IType tt = crsCode.getGamlType();
				if (tt.id() == IType.INT || tt.id() == IType.FLOAT) {
					code = EPSG_LABEL + Cast.asInt(scope, crsCode.value(scope));
				} else if (tt.id() == IType.STRING) { code = (String) crsCode.value(scope); }
			}
			Object attributesToSave = attributesFacet == null ? withFacet : attributesFacet;
			//
			IType itemType = item.getGamlType();
			ISaveDelegate delegate = findDelegate(itemType, type);
			if (delegate != null) {
				delegate.save(scope, item, fileToSave, code, addHeader, type, attributesToSave);
				return Cast.asString(scope, file.value(scope));
			}
			throw GamaRuntimeException.error("Format not recognized: " + type, scope);
		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	/**
	 * Find delegate.
	 *
	 * @param dataType
	 *            the data type
	 * @param fileFormat
	 *            the file type
	 * @return the i save delegate
	 */
	private ISaveDelegate findDelegate(final IType dataType, final String fileFormat) {
		Map<IType, ISaveDelegate> map = DELEGATES.get(fileFormat);
		if (map == null) return null;
		int distance = Integer.MAX_VALUE;
		ISaveDelegate closest = null;
		for (Entry<IType, ISaveDelegate> entry : map.entrySet()) {
			if (entry.getKey().isAssignableFrom(dataType)) {
				@SuppressWarnings ("unchecked") int d = dataType.distanceTo(entry.getKey());
				if (d < distance) {
					distance = d;
					closest = entry.getValue();
				}
			}
		}
		return closest;
	}

	@Override
	public void setFormalArgs(final Arguments args) { withFacet = args; }

	@Override
	public void setRuntimeArgs(final IScope scope, final Arguments args) {
		//
	}
}
