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
package msi.gaml.statements;

import java.io.*;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GisUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.GamaFile;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.Facets.Facet;
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
@symbol(name = IKeyword.CREATE, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, with_args = true, remote_context = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = { @facet(name = IKeyword.SPECIES, type = IType.SPECIES_STR, optional = true),
	@facet(name = IKeyword.RETURNS, type = IType.NEW_TEMP_ID, optional = true),
	@facet(name = IKeyword.FROM, type = IType.NONE_STR, optional = true),
	@facet(name = IKeyword.NUMBER, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.AS, type = { IType.SPECIES_STR }, optional = true),
	@facet(name = IKeyword.WITH, type = { IType.MAP_STR }, optional = true),
	@facet(name = IKeyword.SIZE, type = { IType.FLOAT_STR }, optional = true),
	@facet(name = IKeyword.TYPE, type = { IType.STRING_STR }, optional = true) }, omissible = IKeyword.SPECIES)
public class CreateStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	private Arguments init;
	private final IExpression from;
	private final IExpression number;
	private final IExpression speciesExpr;
	private final String returnString;
	// private final double sizeSquare = -1;
	// private final String typeDiscretisation = "";
	
	private static final boolean DEBUG=false;
	
	private AbstractStatementSequence sequence;

	/** allows to project geometries in a new projection */
	private MathTransform transformCRS = null;
	
	public CreateStatement(final IDescription desc) {
		super(desc);
		returnString = getLiteral(IKeyword.RETURNS);
		from = getFacet(IKeyword.FROM);
		number = getFacet(IKeyword.NUMBER);
		speciesExpr = getFacet(IKeyword.SPECIES);
		setName("create");
	}

	public CreateStatement() {
		super(null);
		returnString = null;
		from = null;
		number = null;
		speciesExpr = null;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> com) {
		sequence = new AbstractStatementSequence(description);
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
			if ( AbstractGamlAdditions.isBuiltIn(m.getName()) ) {
				continue;
			}

			if ( !firstSpec ) {
				firstSpec = true;
				retVal.append(m.getName());
			} else {
				retVal.append(", " + m.getName());
			}
		}

		while (!currentSpec.getName().equals(IKeyword.WORLD_SPECIES)) {
			if ( !AbstractGamlAdditions.isBuiltIn(currentSpec.getName()) ) {
				if ( !firstSpec ) {
					firstSpec = true;
					retVal.append(currentSpec.getName());
				} else {
					retVal.append(", " + currentSpec.getName());
				}
			}

			for ( ISpecies p : currentSpec.getPeersSpecies() ) {
				if ( AbstractGamlAdditions.isBuiltIn(p.getName()) ) {
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

				throw new GamaRuntimeException("Population of " + speciesExpr.literalValue() +
					" species is not accessible from the context of " + executor.getName() +
					" agent. Available populations are [" + availableSpecies + "] ");
			}

			thePopulation = executor.getPopulationFor(targetSpecies);
		}

		scope.addVarWithValue(IKeyword.MYSELF, scope.getAgentScope());

		if ( from != null ) {
			IType type = from.getType();
			//begin ---------------------------------------------------------------------------------------------
			//Thai.truongminh@gmail.com 
			// 04-sep-2012: for create agen from:list
			// from: alist with: attribute1:: num1, attribute2: num2...;
			if (type.id()==IType.LIST)	
			{
				final List<Map<String, Object>> initialValues = gamaList2ListMap(scope,(GamaList<Object>) from.value(scope));
				createAgents(scope, agents, thePopulation, initialValues,initialValues.size() );
			} 
			else
			//--------------------------------------------------------------------------------------------- end
			
			if ( type.id() == IType.STRING || type.id() == IType.FILE ) {
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
		String s = returnString;
		if ( s != null ) {
			scope.setVarValue(s, agents);
		}
		if ( agents.size() == 1 ) { return agents.get(0); }

		return agents;
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
				if ( GisUtils.transformCRS == null ) {
					ShpFiles shpf = new ShpFiles(shpFile);
					double latitude = featureShp.getBounds().centre().x;
					double longitude = featureShp.getBounds().centre().y;
					transformCRS = GisUtils.getTransformCRS(shpf, latitude, longitude);
				} else {
					transformCRS = GisUtils.transformCRS;
				}
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
			Object shfile = from.value(scope);
			shapeFile =
				shfile instanceof String ? scope.getSimulationScope().getModel()
					.getRelativeFilePath(Cast.asString(scope, shfile), true) : ((GamaFile) shfile)
					.getPath();

		} catch (GamaRuntimeException e) {
			e.printStackTrace();
		}
		return getFeatureIterator(shapeFile);
	}

	private void computeInits(final IScope scope, final Map<String, Object> values)
		throws GamaRuntimeException {
		if ( init == null ) { return; }
		for ( Facet f : init.entrySet() ) {
			if ( f != null ) {
				IExpression valueExpr = f.getValue().getExpression();
				Object val = valueExpr.value(scope);
				values.put(f.getKey(), val);
			}
		}
	}

	private void computeInits(final IScope scope, final List<Map<String, Object>> inits,
		final int numberOfAgents) throws GamaRuntimeException {
		if ( init == null ) { return; }
		for ( int i = 0; i < numberOfAgents; i++ ) {
			Map<String, Object> initialValues = new HashMap();
			inits.add(initialValues);
		}
		for ( Facet f : init.entrySet() ) {
			if ( f != null ) {
				String name = f.getKey();
				IExpression valueExpr = f.getValue().getExpression();
				Object val = valueExpr.value(scope);
				boolean multiple = val instanceof List && ((List) val).size() == numberOfAgents;
				for ( int i = 0; i < numberOfAgents; i++ ) {
					inits.get(i).put(name, multiple ? ((List) val).get(i) : val);
				}
			}
		}
	}

	@Override
	public IType getReturnType() {
		return Types.get(IType.LIST);
	}

	@Override
	public void setFormalArgs(final Arguments args) {
		init = args;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {
		// TODO Auto-generated method stub
	}

	
	/*
	 * thai.truongminh@gmail.com
	 * Method: GamaList2ListMap
	 * Description: 
	 * created date : 03-09-2012
	 * Modified date: 
	 *    04-09-2012
	 *    13-09-2012: change columnTypeName - colType
	 * 
	 */		
	private void computeInits(final IScope scope, final Map<String, Object> values, GamaList<Object> rowList, GamaList<Object> colType)
				throws GamaRuntimeException {
				if ( init == null ) { return; }
				for ( Facet f : init.entrySet() ) {
					if ( f != null ) {
						IExpression valueExpr = f.getValue().getExpression();
						int val = new Integer(valueExpr.value(scope).toString());
						
						if (((String)colType.get(val)).equalsIgnoreCase("GEOMETRY"))
						{
							Geometry geom=(Geometry) rowList.get(val);
							
							
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
							
							values.put(f.getKey(), new GamaShape(geom));
						}	
						else
						   values.put(f.getKey(), rowList.get(val));
						
					}
				}
			}	

		/*
		 * thai.truongminh@gmail.com
		 * Method: GamaList2ListMap
		 * Description: 
		 * created date : 13-09-2012
		 * 
		 */		
	 private void computeInits(final IScope scope, final Map<String, Object> values, GamaList<Object> rowList,GamaList<Object> colTypes, GamaList<Object> colNames)
					throws GamaRuntimeException 
			{
					if ( init == null ) { return; }
					for ( Facet f : init.entrySet() ) {
						if ( f != null ) {
							IExpression valueExpr = f.getValue().getExpression();
							//get parameter
							String columnName = valueExpr.value(scope).toString().toUpperCase();
							// get column number of parameter 
							int val = colNames.indexOf(columnName);
							if (DEBUG){
								System.out.println("Create.computeInit.collist"+colNames);
								System.out.println("Create.computeInit.colType"+colTypes);
								System.out.println("Create.computeInit.colName"+columnName);
								System.out.println("Create.computeInit.colIndex"+val);
							}
							if (((String)colTypes.get(val)).equalsIgnoreCase("GEOMETRY"))
							{
								Geometry geom=(Geometry) rowList.get(val);
								
								
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
								if (DEBUG){
									System.out.println("Create.computeInit.geometry"+geom);
								}
								values.put(f.getKey(), new GamaShape(geom));
							}	
							else
							   values.put(f.getKey(), rowList.get(val));
							
						}
					}
				}	


	/*
	 * thai.truongminh@gmail.com
	 * Method: GamaList2ListMap
	 * Description: transform GamaList of data to List of Map in Java. this function repair initial data
	 *              for creating agents.
	 * created date : 04-09-2012
	 * Modified date:
	 */
	private List<Map<String, Object>> gamaList2ListMap(final IScope scope, GamaList<Object> gamaList) throws GamaRuntimeException 
	{
		//Get List from Gama
		//GamaList<GamaList<Object>> initValue = (GamaList<GamaList<Object>>) from.value(scope);
		//GamaList<Object> gamaList= (GamaList<Object>) from.value(scope);
		
		//get Metadata
		//ResultSetMetaData rsmd=(ResultSetMetaData) gamaList.get(0);
		
		//get Column name
		GamaList<Object> colNames=(GamaList<Object>) gamaList.get(0);
		//get Column type
		GamaList<Object> colTypes=(GamaList<Object>) gamaList.get(1);
		//Get ResultSet 
		GamaList<GamaList<Object>> initValue = (GamaList<GamaList<Object>>) gamaList.get(2);
		
		//set initialValues to generate species
		final List<Map<String, Object>> initialValues = new GamaList();
		int n=initValue.length();
		//int max = number == null ? Integer.MAX_VALUE : numberOfAgents;
		int index=0;
		for (int i=0; i<n && i<Integer.MAX_VALUE; i++)
		{
			index++;
			GamaList<Object> rowList = initValue.get(i);
			Map<String, Object> map = new GamaMap();
			//computeInits(scope, map,rowList,colTypes);
			computeInits(scope, map,rowList,colTypes,colNames);
			initialValues.add(map);
		}
		//createAgents(scope, agents, thePopulation, initialValues, index);
		return initialValues;
	} 
	
	
}