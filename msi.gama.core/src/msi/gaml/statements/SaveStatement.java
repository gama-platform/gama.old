/*******************************************************************************************************
 *
 * SaveStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import static msi.gama.common.geometry.GeometryUtils.cleanGeometryCollection;
import static msi.gama.common.geometry.GeometryUtils.fixesPolygonCWS;
import static msi.gama.common.util.FileUtils.constructAbsoluteFilePath;

import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.media.jai.RasterFactory;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.jgrapht.nio.GraphExporter;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.Envelope;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ITyped;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.projection.IProjection;
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
import msi.gama.util.file.IGamaFile;
import msi.gama.util.graph.IGraph;
import msi.gama.util.graph.writer.GraphExporters;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.data.MapExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.SaveStatement.SaveValidator;
import msi.gaml.statements.save.ASCSaver;
import msi.gaml.statements.save.CSVSaver;
import msi.gaml.statements.save.GeoJSonSaver;
import msi.gaml.statements.save.GeoTiffSaver;
import msi.gaml.statements.save.ImageSaver;
import msi.gaml.statements.save.ShapeSaver;
import msi.gaml.statements.save.TextSaver;
import msi.gaml.types.GamaFileType;
import msi.gaml.types.GamaKmlExport;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

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
				name = IKeyword.TYPE,
				type = IType.ID,
				optional = true,
				values = { "shp", "text", "csv", "asc", "geotiff", "image", "kml", "kmz", "json", "dimacs", "dot",
						"gexf", "graphml", "gml", "graph6" },
				doc = @doc ("an expression that evaluates to a string, the type of the output file (it can be only \"shp\", \"asc\", \"geotiff\", \"image\", \"text\" or \"csv\") ")),
				@facet (
						name = IKeyword.DATA,
						type = IType.NONE,
						optional = true,
						doc = @doc ("the data that will be saved to the file")),
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

	/** The Constant GEOTIFF. */
	private static final String GEOTIFF = "geotiff";

	/** The Constant DOES_NOT_CORRESPOND_TO_A_KNOWN_EPSG_CODE. */
	private static final String DOES_NOT_CORRESPOND_TO_A_KNOWN_EPSG_CODE =
			" does not correspond to a known EPSG code. GAMA is unable to save ";

	/** The Constant THE_CODE. */
	private static final String THE_CODE = "The code ";

	/** The Constant EPSG_LABEL. */
	private static final String EPSG_LABEL = "EPSG:";

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
				final IExpression type = desc.getFacetExpr(TYPE);
				if (type == null || !"shp".equals(type.literalValue()) && !"json".equals(type.literalValue())) {
					desc.warning("Attributes can only be defined for shape or json files", IGamlIssue.WRONG_TYPE,
							ATTRIBUTES);
				}

			}

			final IExpression data = desc.getFacetExpr(DATA);
			if (data == null) return;
			final IType<?> t = data.getGamlType().getContentType();
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

	/** The header. */
	private final IExpression crsCode;

	/** The item. */
	private final IExpression item;

	/** The file. */
	private final IExpression file;

	/** The rewrite expr. */
	private final IExpression rewriteExpr;

	/** The header. */
	private final IExpression header;

	/**
	 * Instantiates a new save statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public SaveStatement(final IDescription desc) {
		super(desc);
		crsCode = desc.getFacetExpr("crs");
		item = desc.getFacetExpr(IKeyword.DATA);
		file = getFacet(IKeyword.TO);
		rewriteExpr = getFacet(IKeyword.REWRITE);
		header = getFacet(IKeyword.HEADER);
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
		final String typeExp = getLiteral(IKeyword.TYPE);
		// Second case: a filename is indicated but not the type. In that case,
		// we try to build a new GamaFile from it and save it
		if (typeExp == null) {
			final String theName = Cast.asString(scope, file.value(scope));
			final Object contents = item.value(scope);
			if (contents instanceof IModifiableContainer mc) {
				final IGamaFile f = GamaFileType.createFile(scope, theName, mc);
				f.save(scope, description.getFacets());
				return f;
			}

		}

		// These statements will need to be completely rethought because of the
		// possibility to now use the GamaFile infrastructure for this.
		// For instance, TYPE is not needed anymore (the name of the file / its
		// inner type will be enough), like in save json_file("ddd.json",
		// my_map); which we can probably allow to be written save my_map to:
		// json_file("ddd.json"); see #1362

		try {
			final String path = constructAbsoluteFilePath(scope, Cast.asString(scope, file.value(scope)), false);
			if (path == null || "".equals(path)) return null;
			final File fileToSave = new File(path);
			Files.createDirectories(fileToSave.toPath().getParent());
			boolean exists = fileToSave.exists();
			final boolean rewrite = shouldOverwrite(scope);
			if (rewrite && exists) {
				fileToSave.delete();
				exists = false;
			}
			final boolean addHeader = !exists && (header == null || Cast.asBool(scope, header.value(scope)));
			final String type = (typeExp != null ? typeExp : "text").trim().toLowerCase();
			String code = null;
			if (crsCode != null) {
				final IType tt = crsCode.getGamlType();
				if (tt.id() == IType.INT || tt.id() == IType.FLOAT) {
					code = EPSG_LABEL + Cast.asInt(scope, crsCode.value(scope));
				} else if (tt.id() == IType.STRING) { code = (String) crsCode.value(scope); }
			}
			//
			switch (type) {
				case "json":
					new GeoJSonSaver().save(scope, item, fileToSave, code, withFacet, attributesFacet);
					break;
				case "shp":
					new ShapeSaver().save(scope, item, fileToSave, code, withFacet, attributesFacet);
					break;
				case "text":
					new TextSaver().save(scope, item, fileToSave, addHeader);
					break;
				case "csv":
					new CSVSaver().save(scope, item, fileToSave, addHeader);
					break;
				case "asc":
					new ASCSaver().save(scope, item, fileToSave);
					break;
				case "image":
					new ImageSaver().save(scope, item, fileToSave);
					break;
				case GEOTIFF:
					new GeoTiffSaver().save(scope, item, fileToSave);
					break;
				case "kml", "kmz":
					final Object kml = item.value(scope);
					if (!(kml instanceof GamaKmlExport export)) return null;
					if ("kml".equals(type)) {
						export.saveAsKml(scope, path);
					} else {
						export.saveAsKmz(scope, path);
					}
					break;
				default:
					GraphExporter<?, ?> exp = GraphExporters.getGraphWriter(type);
					if (exp == null)
						throw GamaRuntimeException.error("Format is not recognized ('" + type + "')", scope);
					final IGraph g = Cast.asGraph(scope, item);
					if (g == null) return null;
					exp.exportGraph(g, fileToSave.getAbsoluteFile());
			}
		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
		return Cast.asString(scope, file.value(scope));
	}

	/**
	 * Creates the coverage byte from float.
	 *
	 * @param name
	 *            the name
	 * @param matrix
	 *            the matrix
	 * @param envelope
	 *            the envelope
	 * @return the grid coverage 2 D
	 */
	// from org.geotools.coverage.grid.GridCoverageFactory
	public static GridCoverage2D createCoverageByteFromFloat(final CharSequence name, final float[][] matrix,
			final Envelope envelope) {

		int width = 0;
		final int height = matrix.length;
		for (int j = 0; j < height; j++) {
			final float[] row = matrix[j];
			if (row != null && row.length > width) { width = row.length; }
		}

		final WritableRaster raster;
		raster = RasterFactory.createBandedRaster(DataBuffer.TYPE_BYTE, width, height, 1, null);
		for (int j = 0; j < height; j++) {
			int i = 0;
			final float[] row = matrix[j];
			if (row != null) { for (; i < row.length; i++) { raster.setSample(i, j, 0, (byte) Math.round(row[i])); } }
			for (; i < width; i++) { raster.setSample(i, j, 0, (byte) 255); }
		}

		return new GridCoverageFactory().create(name, raster, envelope);
	}

	/**
	 * Type.
	 *
	 * @param theVar
	 *            the var
	 * @return the string
	 */
	public static String type(final ITyped theVar) {
		return switch (theVar.getGamlType().id()) {
			case IType.BOOL -> "Boolean";
			case IType.INT -> "Integer";
			case IType.FLOAT -> "Double";
			default -> "String";
		};
	}

	/** The Constant NON_SAVEABLE_ATTRIBUTE_NAMES. */
	public static final Set<String> NON_SAVEABLE_ATTRIBUTE_NAMES =
			Set.of(IKeyword.PEERS, IKeyword.LOCATION, IKeyword.HOST, IKeyword.AGENTS, IKeyword.MEMBERS, IKeyword.SHAPE);

	/**
	 * Builds the feature.
	 *
	 * @param scope
	 *            the scope
	 * @param ff
	 *            the ff
	 * @param ag
	 *            the ag
	 * @param gis
	 *            the gis
	 * @param attributeValues
	 *            the attribute values
	 * @return true, if successful
	 */
	public static boolean buildFeature(final IScope scope, final SimpleFeature ff, final IShape ag,
			final IProjection gis, final Collection<IExpression> attributeValues) {
		final List<Object> values = new ArrayList<>();
		// geometry is by convention (in specs) at position 0
		Geometry g = ag.getInnerGeometry();
		if (g == null) return false;
		if (gis != null) { g = gis.inverseTransform(g); }
		g = fixesPolygonCWS(cleanGeometryCollection(g));
		values.add(g);
		if (ag instanceof IAgent ia) {
			for (final IExpression variable : attributeValues) {
				Object val = scope.evaluate(variable, ia).getValue();
				if (variable.getGamlType().equals(Types.STRING)) {
					val = val == null ? "" : StringUtils.toJavaString(val.toString());
				}
				values.add(val);
			}
		} else {
			// see #2982. Assume it is an attribute of the shape
			for (final IExpression variable : attributeValues) {
				final Object val = variable.value(scope);
				if (val instanceof String s) {
					values.add(ag.getAttribute(s));
				} else {
					values.add("");
				}
			}
		}
		// AD Assumes that the type is ok.
		// AD WARNING Would require some sort of iterator operator that
		// would collect the values beforehand
		ff.setAttributes(values);
		return true;
	}

	@Override
	public void setFormalArgs(final Arguments args) { withFacet = args; }

	@Override
	public void setRuntimeArgs(final IScope scope, final Arguments args) {
		//
	}
}
