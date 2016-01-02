/*********************************************************************************************
 *
 *
 * 'SaveStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.statements;

import java.io.*;
import java.util.*;
import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.SchemaException;
import org.opengis.feature.simple.*;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import com.vividsolutions.jts.geom.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.IGraph;
import msi.gama.util.graph.writer.AvailableGraphWriters;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.*;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.SaveStatement.SaveValidator;
import msi.gaml.types.*;

@symbol(name = IKeyword.SAVE,
	kind = ISymbolKind.SINGLE_STATEMENT,
	with_sequence = false,
	with_args = true,
	remote_context = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.ACTION })
@facets(value = {
	@facet(name = IKeyword.TYPE,
		type = IType.ID,
		optional = true,
		doc = @doc("an expression that evaluates to an string, the type of the output file (it can be only \"shp\", \"text\" or \"csv\") ") ),
	@facet(name = IKeyword.DATA,
		type = IType.NONE,
		optional = true,
		doc = @doc("any expression, that will be saved in the file") ),
	@facet(name = IKeyword.REWRITE,
		type = IType.BOOL,
		optional = true,
		doc = @doc("an expression that evaluates to a boolean, specifying whether the save will ecrase the file or append data at the end of it") ),
	@facet(name = IKeyword.HEADER,
		type = IType.BOOL,
		optional = true,
		doc = @doc("an expression that evaluates to a boolean, specifying whether the save will write a header if the file does not exist") ),
	@facet(name = IKeyword.TO,
		type = IType.STRING,
		optional = false,
		doc = @doc("an expression that evaluates to an string, the path to the file") ),
	@facet(name = "crs",
		type = IType.NONE,
		optional = true,
		doc = @doc("the name of the projection, e.g. crs:\"EPSG:4326\" or its EPSG id, e.g. crs:4326. Here a list of the CRS codes (and EPSG id): http://spatialreference.org") ),
	@facet(name = IKeyword.WITH, type = { IType.MAP }, optional = true, doc = @doc("") ) }, omissible = IKeyword.DATA)
@doc(value = "Allows to save data in a file. The type of file can be \"shp\", \"text\" or \"csv\".",
	usages = {
		@usage(value = "Its simple syntax is:",
			examples = { @example(value = "save data to: output_file type: a_type_file;", isExecutable = false) }),
		@usage(value = "To save data in a text file:",
			examples = { @example(
				value = "save (string(cycle) + \"->\"  + name + \":\" + location) to: \"save_data.txt\" type: \"text\";") }),
		@usage(value = "To save the values of some attributes of the current agent in csv file:",
			examples = { @example(value = "save [name, location, host] to: \"save_data.csv\" type: \"csv\";") }),
		@usage(
			value = "To save the values of all attributes of all the agents of a species into a csv (with optional attributes):",
			examples = {
				@example(value = "save species_of(self) to: \"save_csvfile.csv\" type: \"csv\" header: false;") }),
		@usage(
			value = "To save the geometries of all the agents of a species into a shapefile (with optional attributes):",
			examples = { @example(
				value = "save species_of(self) to: \"save_shapefile.shp\" type: \"shp\" with: [name::\"nameAgent\", location::\"locationAgent\"] crs: \"EPSG:4326\";") }),
		@usage(
			value = "The save statement can be use in an init block, a reflex, an action or in a user command. Do not use it in experiments.") })
@validator(SaveValidator.class)
public class SaveStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	public static class SaveValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription description) {
			StatementDescription desc = (StatementDescription) description;
			IExpression data = desc.getFacets().getExpr(DATA);
			if ( data == null ) { return; }
			IType t = data.getType().getContentType();
			SpeciesDescription species = t.getSpecies();
			Collection<StatementDescription> args = desc.getArgs();
			if ( args == null || args.isEmpty() ) { return; }
			if ( species == null ) {
				desc.error("No attributes can be saved for geometries", IGamlIssue.UNKNOWN_VAR, WITH);
			} else {
				for ( StatementDescription arg : args ) {
					if ( !species.hasVar(arg.getName()) ) {
						desc.error(
							"Attribute " + arg.getName() + " is not defined for the agents of " + data.serialize(false),
							IGamlIssue.UNKNOWN_VAR, WITH);
					}
				}
			}
		}

	}

	private Arguments init;
	private final IExpression crsCode, item, file, rewriteExpr, header;

	public SaveStatement(final IDescription desc) {
		super(desc);
		crsCode = desc.getFacets().getExpr("crs");
		item = desc.getFacets().getExpr(IKeyword.DATA);
		file = getFacet(IKeyword.TO);
		rewriteExpr = getFacet(IKeyword.REWRITE);
		header = getFacet(IKeyword.HEADER);
	}

	// TODO rewrite this with the GamaFile framework

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final String typeExp = getLiteral(IKeyword.TYPE);

		String path = "";
		if ( file == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		path = FileUtils.constructAbsoluteFilePath(scope, Cast.asString(scope, file.value(scope)), false);
		if ( path.equals("") ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		String type = "text";
		if ( typeExp != null ) {
			type = typeExp;
		}
		if ( type.equals("shp") ) {
			if ( item == null ) { return null; }
			IList<? extends IShape> agents = Cast.asList(scope, item.value(scope));
			if ( agents == null || agents.isEmpty() ) { return null; }
			saveShape(agents, path, scope);
		} else if ( type.equals("text") || type.equals("csv") ) {
			final File fileTxt = new File(path);
			boolean exists = fileTxt.exists();
			if ( rewriteExpr != null ) {
				final boolean rewrite = Cast.asBool(scope, rewriteExpr.value(scope));
				if ( rewrite ) {
					if ( fileTxt.exists() ) {
						fileTxt.delete();
						exists = false;
					}
				}
			}
			try {
				createParents(fileTxt);
				fileTxt.createNewFile();
			} catch (final GamaRuntimeException e) {
				throw e;
			} catch (final IOException e) {
				throw GamaRuntimeException.create(e);
			}

			final boolean addHeader = !exists && (header == null || Cast.asBool(scope, header.value(scope)));

			saveText(type, fileTxt, addHeader, scope);

		} else if ( type.equals("asc") ) {
			ISpecies species;
			if ( item == null ) { return null; }
			species = Cast.asSpecies(scope, item.value(scope));
			if ( species == null || !species.isGrid() ) { return null; }

			saveAsc(species, path, scope);
		} else if ( AvailableGraphWriters.getAvailableWriters().contains(type.trim().toLowerCase()) ) {

			IGraph g;
			if ( item == null ) {
				// scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			g = Cast.asGraph(scope, item);
			if ( g == null ) {
				// scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			AvailableGraphWriters.getGraphWriter(type.trim().toLowerCase()).writeGraph(scope, g, null, path);

		} else {

			throw GamaRuntimeException.error("Unable to save, because this format is not recognized ('" + type + "')",
				scope);
		}
		return Cast.asString(scope, file.value(scope));
	}

	private static void createParents(final File outputFile) {
		File parent = outputFile.getParentFile();
		if ( !parent.exists() ) {
			parent.mkdirs();
		}

	}

	public void saveAsc(final ISpecies species, final String path, final IScope scope) {
		File f = new File(path);
		if ( f.exists() ) {
			f.delete();
		}
		try {
			FileWriter fw = new FileWriter(f);
			String header = "";
			GridPopulation gp = (GridPopulation) species.getPopulation(scope);
			int nbCols = gp.getNbCols();
			int nbRows = gp.getNbRows();
			header += "ncols         " + nbCols + Strings.LN;
			header += "nrows         " + nbRows + Strings.LN;
			boolean nullProjection = scope.getSimulationScope().getProjectionFactory().getWorld() == null;
			header += "xllcorner     " +
				(nullProjection ? "0"
					: scope.getSimulationScope().getProjectionFactory().getWorld().getProjectedEnvelope().getMinX()) +
				Strings.LN;
			header += "yllcorner     " +
				(nullProjection ? "0"
					: scope.getSimulationScope().getProjectionFactory().getWorld().getProjectedEnvelope().getMinY()) +
				Strings.LN;
			double dx = scope.getSimulationScope().getEnvelope().getWidth() / nbCols;
			double dy = scope.getSimulationScope().getEnvelope().getHeight() / nbRows;
			if ( dx == dy ) {
				header += "cellsize      " + dx + Strings.LN;
			} else {
				header += "dx            " + dx + Strings.LN;
				header += "dy            " + dy + Strings.LN;
			}
			fw.write(header);

			for ( int i = 0; i < nbRows; i++ ) {
				String val = "";
				for ( int j = 0; j < nbCols; j++ ) {
					val += gp.getGridValue(j, i) + " ";
				}
				fw.write(val + Strings.LN);
			}
			fw.close();
		} catch (IOException e) {
			return;
		}

	}

	public void saveShape(final IList<? extends IShape> agents, final String path, final IScope scope)
		throws GamaRuntimeException {

		final StringBuilder specs = new StringBuilder(agents.size() * 20);
		String geomType = "";
		for ( final IShape be : agents ) {
			if ( be.getGeometry() != null ) {
				final IShape ag = be;
				geomType = ag.getInnerGeometry().getClass().getSimpleName();
				if ( ag.getInnerGeometry().getNumGeometries() > 1 ) {
					if ( ag.getInnerGeometry().getGeometryN(0).getClass() == Point.class ) {
						geomType = MultiPoint.class.getSimpleName();
					} else if ( ag.getInnerGeometry().getGeometryN(0).getClass() == LineString.class ) {
						geomType = MultiLineString.class.getSimpleName();
					} else if ( ag.getInnerGeometry().getGeometryN(0).getClass() == Polygon.class ) {
						geomType = MultiPolygon.class.getSimpleName();
					}
					break;
				}
			}
		}
		specs.append("geometry:" + geomType);
		try {
			SpeciesDescription species = agents.getType().getContentType().getSpecies();
			Map<String, String> attributes = GamaMapFactory.create(Types.STRING, Types.STRING);
			if ( species != null ) {
				computeInits(scope, attributes);
				for ( final String e : attributes.keySet() ) {
					String var = attributes.get(e);
					String name = e.replaceAll("\"", "");
					name = name.replaceAll("'", "");
					specs.append(',').append(name).append(':').append(type(species.getVariable(var).getType()));
				}
			}
			saveShapeFile(scope, path, agents, /* featureTypeName, */specs.toString(), attributes);
		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}

	}

	public void saveText(final String type, final File fileTxt, final boolean header, final IScope scope)
		throws GamaRuntimeException {
		try {
			if ( item == null ) { return; }
			final FileWriter fw = new FileWriter(fileTxt, true);
			if ( type.equals("text") ) {
				fw.write(Cast.asString(scope, item.value(scope)) + Strings.LN);
			} else if ( type.equals("csv") ) {
				IType itemType = item.getType();
				boolean isAgent = itemType.isAgentType() || itemType.getContentType().isAgentType();
				Object value = item.value(scope);
				IList values =
					itemType.isContainer() ? Cast.asList(scope, value) : GamaListFactory.create(scope, itemType, value);
				if ( values.isEmpty() ) {
					fw.close();
					return;
				}
				if ( isAgent ) {
					Collection<String> attributeNames = values.getType().getContentType().getSpecies().getVarNames();
					attributeNames.remove(IKeyword.NAME);
					attributeNames.remove(IKeyword.LOCATION);
					attributeNames.remove(IKeyword.PEERS);
					attributeNames.remove(IKeyword.HOST);
					attributeNames.remove(IKeyword.AGENTS);
					attributeNames.remove(IKeyword.MEMBERS);
					if ( header ) {
						// final IAgent ag0 = Cast.asAgent(scope, values.get(0));
						fw.write("cycle;name;location.x;location.y;location.z");
						for ( String v : attributeNames ) {
							fw.write(";" + v);
						}
						fw.write(Strings.LN);
					}
					for ( Object obj : values ) {
						if ( obj instanceof IAgent ) {
							final IAgent ag = Cast.asAgent(scope, obj);
							fw.write(scope.getClock().getCycle() + ";" + ag.getName() + ";" + ag.getLocation().getX() +
								";" + ag.getLocation().getY() + ";" + ag.getLocation().getZ());
							for ( String v : attributeNames ) {
								fw.write(";" + Cast.toGaml(ag.getDirectVarValue(scope, v)));
							}
							fw.write(Strings.LN);
						}

					}
				} else {
					for ( int i = 0; i < values.size() - 1; i++ ) {
						fw.write(Cast.toGaml(values.get(i)) + ",");
					}
					fw.write(Cast.toGaml(values.lastValue(scope)) + Strings.LN);
				}

			}

			fw.close();
		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e);
		}

	}

	public String type(final IType gamaName) {
		if ( gamaName.id() == IType.BOOL ) { return "Boolean"; }
		if ( gamaName.id() == IType.FLOAT ) { return "Double"; }
		if ( gamaName.id() == IType.INT ) { return "Integer"; }
		return "String";
	}

	private void computeInits(final IScope scope, final Map<String, String> values) throws GamaRuntimeException {
		if ( init == null ) { return; }
		for ( final Map.Entry<String, IExpressionDescription> f : init.entrySet() ) {
			if ( f != null ) {
				values.put(f.getValue().toString(), f.getKey());
			}
		}
	}

	// AD 2/1/16 Replace IAgent by IShape so as to be able to save geometries
	public void saveShapeFile(final IScope scope, final String path, final List<? extends IShape> agents,
		/* final String featureTypeName, */final String specs, final Map<String, String> attributes)
			throws IOException, SchemaException, GamaRuntimeException {

		String code = null;
		if ( crsCode != null ) {
			IType type = crsCode.getType();
			if ( type.id() == IType.INT || type.id() == IType.FLOAT ) {
				code = "EPSG:" + Cast.asInt(scope, crsCode.value(scope));
			} else if ( type.id() == IType.STRING ) {
				code = (String) crsCode.value(scope);
			}
		}
		IProjection gis;
		if ( code == null ) {
			gis = scope.getSimulationScope().getProjectionFactory().getWorld();
		} else {
			try {
				gis = scope.getSimulationScope().getProjectionFactory().forSavingWith(code);
			} catch (FactoryException e1) {
				throw GamaRuntimeException.error(
					"The code " + code + " does not correspond to a known EPSG code. GAMA is unable to save " + path,
					scope);
			}
		}

		// AD 11/02/15 Added to allow saving to new directories
		File f = new File(path);
		createParents(f);

		final ShapefileDataStore store = new ShapefileDataStore(f.toURI().toURL());

		// The name of the type and the name of the feature source shoud now be the same.
		SimpleFeatureType type = DataUtilities.createType(store.getFeatureSource().getEntry().getTypeName(), specs);
		store.createSchema(type);
		// AD: creation of a FeatureWriter on the store.
		FeatureWriter fw = store.getFeatureWriter(Transaction.AUTO_COMMIT);

		// AD Builds once the list of agent attributes to evaluate
		Collection<String> attributeValues = attributes == null ? Collections.EMPTY_LIST : attributes.values();
		List<Object> values = new ArrayList();
		for ( final IShape ag : agents ) {
			values.clear();
			SimpleFeature ff = (SimpleFeature) fw.next();
			// geometry is by convention (in specs) at position 0
			values.add(gis == null ? ag.getInnerGeometry() : gis.inverseTransform(ag.getInnerGeometry()));
			if ( ag instanceof IAgent ) {
				for ( final String variable : attributeValues ) {
					values.add(((IAgent) ag).getDirectVarValue(scope, variable));
				}
			}
			// AD Assumes that the type is ok.
			// AD TODO replace this list of variable names by expressions (to be evaluated by agents), so that dynamic values can be passed
			// AD WARNING Would require some sort of iterator operator that would collect the values beforehand
			ff.setAttributes(values);
			fw.write();
		}
		fw.close();
		store.dispose();
		if ( gis != null ) {
			writePRJ(scope, path, gis);
		}
	}

	private void writePRJ(final IScope scope, final String path, final IProjection gis) {
		final CoordinateReferenceSystem crs = gis.getInitialCRS();
		if ( crs != null ) {
			try {
				final FileWriter fw = new FileWriter(path.replace(".shp", ".prj"));
				fw.write(crs.toString());
				fw.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setFormalArgs(final Arguments args) {
		init = args;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {
		// TODO Auto-generated method stub
	}
}
