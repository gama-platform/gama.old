/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.commands;

import java.io.*;
import java.util.*;
import msi.gama.common.interfaces.*;

import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.*;
import msi.gama.util.*;
import msi.gaml.compilation.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.ModelFactory;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.*;
import org.geotools.feature.*;
import org.geotools.geometry.jts.JTS;
import org.opengis.feature.simple.*;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.*;
import com.vividsolutions.jts.geom.Geometry;

/**
 * This command is used to create agents.
 * 
 * Considering the invoking agent as the execution context, species of the created agents can be
 * 1. The same species of the invoking agent or any peer species of the invoking agent's species.
 * The newly created agent(s) will take the invoking agent's macro-agent as its/their macro-agent.
 * 
 * 2. The direct micro-species of the invoking agent's species.
 * The newly create agent(s) will take the invoking agent as its/their macro-agent.
 * 
 * 3. The direct macro-species of the invoking agent's species or any peer species of this direct
 * macro-species.
 * The newly created agent(s) will take the macro-agent of invoking agent's macro-agent as its/their
 * macro-agent.
 */
@symbol(name = IKeyword.CREATE, kind = ISymbolKind.SEQUENCE_COMMAND)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_COMMAND })
@facets({ @facet(name = IKeyword.SPECIES, type = IType.SPECIES_STR, optional = true),
	@facet(name = IKeyword.RETURNS, type = IType.NEW_TEMP_ID, optional = true),
	@facet(name = IKeyword.FROM, type = IType.NONE_STR, optional = true),
	@facet(name = IKeyword.NUMBER, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.AS, type = { IType.SPECIES_STR }, optional = true),
	@facet(name = IKeyword.WITH, type = { IType.MAP_STR }, optional = true),
	@facet(name = IKeyword.SIZE, type = { IType.FLOAT_STR }, optional = true),
	@facet(name = IKeyword.TYPE, type = { IType.STRING_STR }, optional = true) })
@remote_context
@with_args
public class CreateCommand extends AbstractCommandSequence implements ICommand.WithArgs {

	private Arguments init;
	private final IExpression from;
	private final IExpression number;
	private final IExpression speciesExpr;
	private final String returnString;
	private double sizeSquare = -1;
	private String typeDiscretisation = "";

	private AbstractCommandSequence sequence;

	/** allows to project geometries in a new projection */
	private MathTransform transformCRS = null;

	public CreateCommand(final IDescription desc) {
		super(desc);
		returnString = getLiteral(IKeyword.RETURNS);
		from = getFacet(IKeyword.FROM);
		number = getFacet(IKeyword.NUMBER);
		speciesExpr = getFacet(IKeyword.SPECIES);
		// asExpr = getFacet(ISymbol.AS);
		setName("create " + speciesExpr == null ? description.getSpeciesContext().getName()
			: speciesExpr.toGaml());
	}

	public CreateCommand() {
		super(null);
		returnString = null;
		from = null;
		number = null;
		speciesExpr = null;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> com) {
		sequence = new AbstractCommandSequence(description);
		sequence.setName("commands of create ");
		sequence.setChildren(com);
	}

	@Override
	public void enterScope(final IScope scope) {
		if ( returnString != null ) {
			scope.addVarWithValue(returnString, null);
		}
		super.enterScope(scope);
	}

	private String accumulateAvailableSpecs(final IAgent executor) {
		StringBuffer retVal = new StringBuffer();
		boolean firstSpec = false;
		ISpecies currentSpec = executor.getSpecies();
		for ( ISpecies m : currentSpec.getMicroSpecies() ) {
			if ( ModelFactory.isBuiltIn(m.getName()) ) {
				continue;
			}

			if ( !firstSpec ) {
				firstSpec = true;
				retVal.append(m.getName());
			} else {
				retVal.append(", " + m.getName());
			}
		}

		while (!currentSpec.getName().equals(IKeyword.WORLD_SPECIES_NAME)) {
			if ( !ModelFactory.isBuiltIn(currentSpec.getName()) ) {
				if ( !firstSpec ) {
					firstSpec = true;
					retVal.append(currentSpec.getName());
				} else {
					retVal.append(", " + currentSpec.getName());
				}
			}

			for ( ISpecies p : currentSpec.getPeersSpecies() ) {
				if ( ModelFactory.isBuiltIn(p.getName()) ) {
					continue;
				}
				retVal.append(", " + p.getName());
			}

			currentSpec = currentSpec.getMacroSpecies();
		}

		return retVal.toString();
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		// First, we compute the number of agents to create
		// If we read from a shape file, we do not take it into account

		final IAgent executor = scope.getAgentScope();
		final int numberOfAgents = number == null ? 1 : Cast.asInt(scope, number.value(scope));
		if ( from == null && numberOfAgents <= 0 ) { return new GamaList(); }
		final GamaList<IAgent> agents = new GamaList<IAgent>(numberOfAgents);

		// Next, we compute the species to instantiate

		IPopulation thePopulation;
		if ( speciesExpr == null ) {
			thePopulation = executor.getPopulationFor(description.getSpeciesContext().getName());
		} else {
			ISpecies targetSpecies = (ISpecies) speciesExpr.value(scope);
			if ( targetSpecies == null ) {
				String availableSpecies = accumulateAvailableSpecs(executor);

				throw new GamaRuntimeException(new GamlException("Species: " +
					speciesExpr.value(scope) + " is unknown or is not visible in the context of " +
					executor.getSpecies() + " species. Visible species are [" + availableSpecies +
					"] ", this.getDescription().getSourceInformation()));
			}

			thePopulation = executor.getPopulationFor(targetSpecies);
		}

		// "myself" is added to the temporary variables
		scope.addVarWithValue(IKeyword.MYSELF, scope.getAgentScope());

		if ( from != null ) {
			IExpression size = getFacet(IKeyword.SIZE);
			if ( size != null ) {
				double ss = Cast.asFloat(scope, size.value(scope));
				if ( ss > 0 ) {
					sizeSquare = ss;
				}

			}
			IExpression typeDisc = getFacet(IKeyword.TYPE);
			if ( typeDisc != null ) {
				typeDiscretisation = Cast.asString(scope,typeDisc.value(scope));
				if ( typeDiscretisation == null ) {
					typeDiscretisation = "";
				}
			}
			IType type = from.type();
			if ( type.isSpeciesType() ) {
				IAgent ff = (IAgent) from.value(scope);
				if ( ff != null ) {

					final List<Map<String, Object>> initialValues = new GamaList();
					computeInits(scope, initialValues, numberOfAgents);
					createAgentsFromAgent(scope, ff, agents, thePopulation, initialValues);
				}
			} else if ( type.id() == IType.LIST ) {
				IList ags = Cast.asList(scope, from.value(scope));
				if ( ags != null && from.getContentType().isSpeciesType() ) {
					for ( Object entity : ags ) {
						if ( entity instanceof IAgent ) {

							final List<Map<String, Object>> initialValues = new GamaList();
							computeInits(scope, initialValues, numberOfAgents);
							createAgentsFromAgent(scope, (IAgent) entity, agents, thePopulation,
								initialValues);
						}
					}
				}
			} else if ( type.id() == IType.STRING ) {
				FeatureIterator<SimpleFeature> it3 = getFeatureIterator(scope);
				final List<Map<String, Object>> initialValues = new GamaList();
				if ( it3 != null ) {
					int index = 0;
					int max = number == null ? Integer.MAX_VALUE : numberOfAgents;
					while (it3.hasNext() && index <= max) {
						index++;
						SimpleFeature fact = it3.next();
						GisUtils.setCurrentGisReader(fact);
						Geometry geom = (Geometry) fact.getDefaultGeometry();

						// if a transform function is defined, computation of the
						// geometry coordinates in the new projection
						if ( transformCRS != null ) {
							try {
								geom = JTS.transform(geom, transformCRS);
							} catch (MismatchedDimensionException e) {
								e.printStackTrace();
							} catch (TransformException e) {
								e.printStackTrace();
							}
						}

						geom = GisUtils.fromGISToAbsolute(geom);

						Map<String, Object> map = new GamaMap();
						computeInits(scope, map);
						map.put(IKeyword.SHAPE, new GamaShape(geom));
						initialValues.add(map);
					}
					it3.close();
					createAgents(scope, agents, thePopulation, initialValues, index);
					GisUtils.setCurrentGisReader(null);
				}
			}

		}

		// else we create numberOfAgents agents

		else {
			final List<Map<String, Object>> initialValues = new GamaList();
			computeInits(scope, initialValues, numberOfAgents);
			createAgents(scope, agents, thePopulation, initialValues, numberOfAgents);
		}

		// and we return the agent(s
		String s = getLiteral(IKeyword.RETURNS);
		if ( s != null ) {
			scope.setVarValue(s, agents);
		}
		if ( agents.size() == 1 ) { return agents.get(0); }

		return agents;
	}

	private void createAgentsFromAgent(final IScope scope, final IAgent agent,
		final GamaList<IAgent> agents, final IPopulation realSpecies,
		final List<Map<String, Object>> initialValues) throws GamaRuntimeException {
		IShape geom = agent.getGeometry();
		if ( geom == null ) { return; }
		List<Geometry> geoms = null;
		if ( sizeSquare > 0 && !typeDiscretisation.equals("Triangles") ) {
			typeDiscretisation = "Squares";
		} else {
			typeDiscretisation = "Triangles";
		}

		if ( typeDiscretisation.equals("Squares") ) {
			geoms = GeometryUtils.discretisation(geom.getInnerGeometry(), sizeSquare, false);
		} else {
			if ( sizeSquare > 0 ) {
				geoms = new GamaList<Geometry>();
				List<Geometry> parts =
					GeometryUtils.discretisation(geom.getInnerGeometry(), sizeSquare, true);
				for ( Geometry gg : parts ) {
					geoms.addAll(GeometryUtils.triangulation(gg));
				}
			} else {
				geoms =
					new GamaList<Geometry>(GeometryUtils.triangulation(geom.getInnerGeometry()));
			}
		}
		List<Geometry> finalGeoms = new GamaList(geoms.size());
		for ( Geometry g : geoms ) {
			if ( g != null && !g.isEmpty() ) {
				finalGeoms.add(g);
			}
		}
		createAgents(scope, agents, realSpecies, initialValues, finalGeoms.size());
		for ( int i = 0, n = agents.size(); i < n; i++ ) {
			((GamaShape) agents.get(i).getGeometry()).setInnerGeometry(finalGeoms.get(i));
		}
	}

	/**
	 * @throws GamaRuntimeException
	 * @param population
	 * @param isExecutable
	 * @param initialValues
	 */
	private void createAgents(final IScope scope, final GamaList<IAgent> agents,
		final IPopulation population, final List<Map<String, Object>> initialValues,
		final int number) throws GamaRuntimeException {
		if ( number == 0 ) { return; }
		List<? extends IAgent> list = population.createAgents(scope, number, initialValues, false);

		if ( !sequence.isEmpty() ) {
			for ( int i = 0; i < number; i++ ) {
				IAgent remoteAgent = list.get(i);
				scope.execute(sequence, remoteAgent);
			}
		}
		agents.addAll(list);
	}

	private FeatureIterator<SimpleFeature> getFeatureIterator(final String shapeFile) {
		try {
			File shpFile = new File(shapeFile);
			ShapefileDataStore store = new ShapefileDataStore(shpFile.toURI().toURL());
			String name = store.getTypeNames()[0];
			FeatureSource<SimpleFeatureType, SimpleFeature> source = store.getFeatureSource(name);
			FeatureCollection<SimpleFeatureType, SimpleFeature> featureShp = source.getFeatures();
			if ( store.getSchema().getCoordinateReferenceSystem() != null ) {
				ShpFiles shpf = new ShpFiles(shpFile);
				double latitude = featureShp.getBounds().centre().x;
				double longitude = featureShp.getBounds().centre().y;
				transformCRS = GisUtils.getTransformCRS(shpf, latitude, longitude);
			}
			return featureShp.features();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * @param macro
	 * @return
	 */
	private FeatureIterator<SimpleFeature> getFeatureIterator(final IScope scope) {
		String shapeFile = "";
		try {
			shapeFile =
				scope.getSimulationScope().getModel()
					.getRelativeFilePath(Cast.asString(scope,from.value(scope)), true);
		} catch (GamaRuntimeException e) {
			e.printStackTrace();
		}
		return getFeatureIterator(shapeFile);
	}

	private void computeInits(final IScope scope, final Map<String, Object> values)
		throws GamaRuntimeException {
		if ( init == null ) { return; }
		for ( String name : init.keySet() ) {
			IExpression valueExpr = init.getExpr(name);
			Object val = valueExpr.value(scope);
			values.put(name, val);
		}
	}

	private void computeInits(final IScope scope, final List<Map<String, Object>> inits,
		final int numberOfAgents) throws GamaRuntimeException {
		if ( init == null ) { return; }
		for ( int i = 0; i < numberOfAgents; i++ ) {
			Map<String, Object> initialValues = new HashMap();
			inits.add(initialValues);
		}
		for ( String name : init.keySet() ) {
			IExpression valueExpr = init.getExpr(name);
			Object val = valueExpr.value(scope);
			boolean multiple = val instanceof List && ((List) val).size() == numberOfAgents;
			for ( int i = 0; i < numberOfAgents; i++ ) {
				inits.get(i).put(name, multiple ? ((List) val).get(i) : val);
			}
		}
	}

	@Override
	public IType getReturnType() {
		return Types.get(IType.LIST);
	}

	@Override
	public void setFormalArgs(final Arguments args) throws GamlException {
		init = args;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {
		// TODO Auto-generated method stub
	}

}