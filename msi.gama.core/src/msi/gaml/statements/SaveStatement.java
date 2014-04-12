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
package msi.gaml.statements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gama.util.graph.IGraph;
import msi.gama.util.graph.writer.AvailableGraphWriters;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

@symbol(name = IKeyword.SAVE, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, with_args = true, remote_context = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.ACTION })
@facets(value = { @facet(name = IKeyword.TYPE, type = IType.ID, optional = true, doc = @doc("an expression that evaluates to an string, the type of the output file (it can be only \"shp\", \"text\" or \"csv\") ")),
	@facet(name = IKeyword.DATA, type = IType.NONE, optional = true, doc = @doc("any expression, that will be saved in the file")),
	@facet(name = IKeyword.REWRITE, type = IType.BOOL, optional = true, doc = @doc("an expression that evaluates to a boolean, specifying whether the save will ecrase the file or append data at the end of it")),
	@facet(name = IKeyword.TO, type = IType.STRING, optional = false, doc = @doc("an expression that evaluates to an string, the path to the file")),
	@facet(name = "crs", type = IType.NONE, optional = true, doc = @doc("the name of the projectsion, e.g. crs:\"EPSG:4326\" or its EPSG id, e.g. crs:4326. Here a list of the CRS codes (and EPSG id): http://spatialreference.org")),
	@facet(name = IKeyword.WITH, type = { IType.MAP }, optional = true, doc = @doc("")) }, omissible = IKeyword.DATA)
@doc(value="Allows to save data in a file. The type of file can be \"shp\", \"text\" or \"csv\".", usages = { 
	@usage(value="Its simple syntax is:", examples = {@example(value="save data to: output_file type: a_type_file;",isExecutable=false)}),
	@usage(value="To save data in a text file:", examples = {
		@example(value="save (string(cycle) + \"->\"  + name + \":\" + location) to: \"save_data.txt\" type: \"text\";")}),
	@usage(value="To save the values of some attributes of the current agent in csv file:", examples = {
		@example(value="save [name, location, host] to: \"save_data.csv\" type: \"csv\";")}),	
	@usage(value="To save the geometries of all the agents of a species into a shapefile (with optional attributes):", examples = {
		@example(value="save species_of(self) to: \"save_shapefile.shp\" type: \"shp\" with: [name::\"nameAgent\", location::\"locationAgent\"] crs: \"EPSG:4326\";")}),
	@usage(value="The save statement can be use in an init block, a reflex, an action or in a user command. Do not use it in experiments.")
})
public class SaveStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	private Arguments init;
	private final IExpression crsCode;

	public SaveStatement(final IDescription desc) {
		super(desc);
		crsCode = desc.getFacets().getExpr("crs");

	}

	// TODO rewrite this with the GamaFile framework

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final String typeExp = getLiteral(IKeyword.TYPE);
		final IExpression file = getFacet(IKeyword.TO);
		final IExpression rewriteExp = getFacet(IKeyword.REWRITE);

		String path = "";
		if ( file == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		path =
			scope.getSimulationScope().getModel().getRelativeFilePath(Cast.asString(scope, file.value(scope)), false);
		if ( path.equals("") ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}
		String type = "text";
		if ( typeExp != null ) {
			type = typeExp;
		}
		if ( type.equals("shp") ) {
			final IExpression item = getFacet(IKeyword.DATA);
			List<? extends IAgent> agents;
			if ( item == null ) {
				// scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			agents = Cast.asList(scope, item.value(scope));
			if ( agents == null ) {
				// scope.setStatus(ExecutionStatus.failure);
				return null;
			}
			if ( agents.isEmpty() ) {}
			saveShape(agents, path, scope);
		} else if ( type.equals("text") || type.equals("csv") ) {
			final File fileTxt = new File(path);
			if ( rewriteExp != null ) {
				final boolean rewrite = Cast.asBool(scope, rewriteExp.value(scope));
				if ( rewrite ) {
					if ( fileTxt.exists() ) {
						fileTxt.delete();
					}
				}
			}
			try {
				fileTxt.createNewFile();
			} catch (final GamaRuntimeException e) {
				throw e;
			} catch (final IOException e) {
				throw GamaRuntimeException.create(e);
			}
			final IExpression item = getFacet(IKeyword.DATA);
			saveText(type, item, fileTxt, scope);

		} else if ( AvailableGraphWriters.getAvailableWriters().contains(type.trim().toLowerCase()) ) {

			final IExpression item = getFacet(IKeyword.DATA);
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

			throw GamaRuntimeException.error("Unable to save, because this format is not recognized ('" + type + "')");
		}
		return Cast.asString(scope, file.value(scope));
	}

	public void saveShape(final List<? extends IAgent> agents, final String path, final IScope scope)
		throws GamaRuntimeException {
		final Map<String, String> attributes = new GamaMap();
		computeInits(scope, attributes);
		final StringBuilder specs = new StringBuilder(agents.size() * 20);
		for ( final IAgent be : agents ) {
			if ( be.getGeometry() != null ) {
				final IAgent ag = be;
				specs.append("geom:" + ag.getInnerGeometry().getClass().getSimpleName());
				break;
			}
		}
		try {
			ISpecies species = null;
			if ( agents instanceof IPopulation ) {
				species = ((IPopulation) agents).getSpecies();
			} else {
				species = agents.get(0).getSpecies();
			}
			for ( final String e : attributes.keySet() ) {
				String var = attributes.get(e);
				String name = e.replaceAll("\"", "");
				name = name.replaceAll("'", "");
				specs.append(',').append(name).append(':').append(type(species.getVar(var).getType()));
			}
			final String featureTypeName = species.toString();

			saveShapeFile(scope, path, agents, featureTypeName, specs.toString(), attributes);
		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e);
		}

	}

	public void saveText(final String type, final IExpression item, final File fileTxt, final IScope scope)
		throws GamaRuntimeException {
		try {
			final FileWriter fw = new FileWriter(fileTxt, true);

			if ( item != null ) {
				if ( type.equals("text") ) {
					fw.write(Cast.asString(scope, item.value(scope)) + System.getProperty("line.separator"));
				} else if ( type.equals("csv") ) {
					// item.getContentType();
					if ( item.getType().id() == IType.LIST ) {
						final IList values = Cast.asList(scope, item.value(scope));
						for ( int i = 0; i < values.size() - 1; i++ ) {
							fw.write(Cast.asString(scope, values.get(i)) + ",");
						}
						fw.write(Cast.asString(scope, values.lastValue(scope)) + System.getProperty("line.separator"));
					} else {
						fw.write(Cast.asString(scope, item.value(scope)) + System.getProperty("line.separator"));
					}
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

	public void saveShapeFile(final IScope scope, final String path, final List<? extends IAgent> agents,
		final String featureTypeName, final String specs, final Map<String, String> attributes) throws IOException,
		SchemaException, GamaRuntimeException {
		String code = null;
		if (crsCode != null) {
			IType type = crsCode.getType();
			if (type.id() == type.INT || type.id() == type.FLOAT) {
				code = "EPSG:" + Cast.asInt(scope, crsCode.value(scope));
			} else if (type.id() == type.STRING){
				code = (String) crsCode.value(scope);
			}
		}
		IProjection gis;
		if (code == null) {
			gis = scope.getSimulationScope().getProjectionFactory().getWorld();
		} else {
			try {
				gis = scope.getSimulationScope().getProjectionFactory().forSavingWith(code);
			} catch (FactoryException e1) {
				throw GamaRuntimeException.error("The code " + code +
					" does not correspond to a known EPSG code. GAMA is unable to save " + path, scope);
			}
		}
		final ShapefileDataStore store = new ShapefileDataStore(new File(path).toURI().toURL());
		final SimpleFeatureType type = DataUtilities.createType(featureTypeName, specs);

		store.createSchema(type);
		final FeatureStore<FeatureType, Feature> featureStore = (FeatureStore) store.getFeatureSource(featureTypeName);
		final Transaction t = new DefaultTransaction();
		final FeatureCollection collection = FeatureCollections.newCollection();

		int i = 1;

		for ( final IAgent ag : agents ) {
			final List<Object> liste = new GamaList<Object>();
			Geometry geom = (Geometry) ag.getInnerGeometry().clone();
			// TODO Pr�voir un locationConverter pour passer d'un environnement � l'autre
			if (gis != null)
				geom = gis.inverseTransform(geom);
			liste.add(geom);
			if ( attributes != null ) {
				for ( final Object e : attributes.values() ) {
					liste.add(ag.getDirectVarValue(scope, e.toString()));
				}
			}

			final SimpleFeature simpleFeature = SimpleFeatureBuilder.build(type, liste.toArray(), String.valueOf(i++));
			collection.add(simpleFeature);

		}

		featureStore.addFeatures(collection);
		t.commit();
		t.close();
		store.dispose();
		if (gis != null)
			writePRJ(scope, path, gis);
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
