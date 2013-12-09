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

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.*;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.CreateStatement.CreateValidator;
import msi.gaml.types.*;
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
 * 
 * Creation of agents from CSV files: create toto from: "toto.csv" header: true
 * with:[att1::read("NAME"), att2::read("TYPE")];
 * or, without header: create toto from: "toto.csv"with:[att1::read(0), att2::read(1)]; //with the
 * read(int), the index of the column.
 */
@symbol(name = IKeyword.CREATE, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, with_args = true, remote_context = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = { @facet(name = IKeyword.SPECIES, type = IType.SPECIES, optional = true),
	@facet(name = IKeyword.RETURNS, type = IType.NEW_TEMP_ID, optional = true),
	@facet(name = IKeyword.FROM, type = IType.NONE, optional = true),
	@facet(name = IKeyword.NUMBER, type = IType.INT, optional = true),
	@facet(name = IKeyword.AS, type = { IType.SPECIES }, optional = true),
	@facet(name = IKeyword.WITH, type = { IType.MAP }, optional = true),
	@facet(name = IKeyword.SIZE, type = { IType.FLOAT }, optional = true),
	@facet(name = IKeyword.HEADER, type = { IType.BOOL }, optional = true),
	@facet(name = IKeyword.TYPE, type = { IType.STRING }, optional = true) }, omissible = IKeyword.SPECIES)
@validator(CreateValidator.class)
public class CreateStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	public static class CreateValidator implements IDescriptionValidator<StatementDescription> {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final StatementDescription cd) {

			final SpeciesDescription species = cd.computeSpecies();
			if ( species != null ) {
				if ( species.isAbstract() ) {
					cd.error("Species " + species.getName() + " is abstract and cannot be instantiated",
						IGamlIssue.WRONG_TYPE, IKeyword.SPECIES);
					return;
				} else if ( species.isMirror() ) {
					cd.error("Species " + species.getName() + " is a mirror and cannot be instantiated",
						IGamlIssue.WRONG_TYPE, IKeyword.SPECIES);
					return;
				}
				SpeciesDescription callerSpecies = cd.getSpeciesContext();
				SpeciesDescription macro = species.getMacroSpecies();
				if ( macro == null ) {
					cd.error("The macro-species of " + species + " cannot be determined");
				} else if ( callerSpecies != macro && !callerSpecies.hasMacroSpecies(macro) &&
					!callerSpecies.hasParent(macro) ) {
					cd.error("No instance of " + macro.getName() + " available for creating instances of " +
						species.getName());
				}

			} else {
				cd.error("Species cannot be determined");
			}

		}

	}

	private Arguments init;
	private final IExpression from, number, species, header;
	private final String returns;
	private final AbstractStatementSequence sequence;

	public CreateStatement(final IDescription desc) {
		super(desc);
		returns = getLiteral(IKeyword.RETURNS);
		from = getFacet(IKeyword.FROM);
		number = getFacet(IKeyword.NUMBER);
		species = getFacet(IKeyword.SPECIES);
		header = getFacet(IKeyword.HEADER);
		sequence = new AbstractStatementSequence(description);
		sequence.setName("commands of create ");
		setName("create");
	}

	@Override
	public void setChildren(final List<? extends ISymbol> com) {
		sequence.setChildren(com);
	}

	@Override
	public void enterScope(final IScope scope) {
		if ( returns != null ) {
			scope.addVarWithValue(returns, null);
		}
		super.enterScope(scope);
	}

	@Override
	public IList<? extends IAgent> privateExecuteIn(final IScope scope) {

		// First, we compute the number of agents to create
		final Integer max = number == null ? null : Cast.asInt(scope, number.value(scope));
		if ( from == null && max != null && max <= 0 ) { return GamaList.EMPTY_LIST; }

		// Next, we compute the species to instantiate
		IPopulation pop;
		final IAgent executor = scope.getAgentScope();
		if ( species == null ) {
			pop = executor.getPopulationFor(description.getSpeciesContext().getName());
		} else {
			final ISpecies s = Cast.asSpecies(scope, species.value(scope));
			if ( s == null ) { throw GamaRuntimeException.error("No population of " + species.literalValue() +
				" is accessible in the context of " + executor + "."); }
			pop = executor.getPopulationFor(s);
		}
		scope.addVarWithValue(IKeyword.MYSELF, executor);
		// We grab whatever initial values are defined (from CSV, GIS, or user)
		final List<Map> inits = new GamaList(max == null ? 10 : max);
		final Object source = getSource(scope);
		if ( source instanceof IList ) {
			fillInits(scope, inits, max, (IList) source);
		} else if ( source instanceof GamaShapeFile ) {
			fillInits(scope, inits, max, (GamaShapeFile) source);
		} else if ( source instanceof GamaGridFile ) {
			fillInits(scope, inits, max, (GamaGridFile) source);
		} else if ( source instanceof GamaTextFile ) {
			fillInits(scope, inits, max, (GamaTextFile) source);
		} else {
			fillInits(scope, inits, max);
		}
		// and we create and return the agent(s)
		final IList<? extends IAgent> agents = createAgents(scope, pop, inits);
		if ( returns != null ) {
			scope.setVarValue(returns, agents);
		}
		return agents;
	}

	private Object getSource(final IScope scope) {
		Object source = from == null ? null : from.value(scope);
		if ( source instanceof String ) {
			source = Files.from(scope, (String) source);
		}
		return source;
	}

	/**
	 * Method used to read initial values and attributes from a CSV file.
	 */
	private void fillInits(final IScope scope, final List<Map> inits, final Integer max, final GamaTextFile file) {
		final boolean hasHeader = header == null ? false : Cast.asBool(scope, header.value(scope));
		final GamaList<String[]> rows = new GamaList(file.length(scope));
		for ( final String str : file.iterable(scope) ) {
			rows.add(GamaMatrixType.csvPattern.split(str, -1));
		}
		final int num = max == null ? rows.size() : Math.min(rows.size(), max);
		final String[] headers = rows.get(0);
		for ( int i = hasHeader ? 1 : 0; i < num; i++ ) {
			final GamaMap map = new GamaMap();
			final String[] splitStr = rows.get(i);
			for ( int j = 0; j < splitStr.length; j++ ) {
				map.put(hasHeader ? headers[j] : j, splitStr[j]);
			}
			// CSV attributes are mixed with the attributes of agents
			fillWithUserInit(scope, map);
			inits.add(map);
		}
	}

	/**
	 * Method used to read initial values and attributes from a GIS file.
	 */
	private void fillInits(final IScope scope, final List<Map> inits, final Integer max, final GamaShapeFile file) {
		final int num = max == null ? file.length(scope) : Math.min(file.length(scope), max);
		for ( int i = 0; i < num; i++ ) {
			final GamaGisGeometry g = file.get(scope, i);
			final Map map = g.getOrCreateAttributes();
			// The shape is added to the initial values
			map.put(IKeyword.SHAPE, g);
			// GIS attributes are mixed with the attributes of agents
			fillWithUserInit(scope, map);
			inits.add(map);
		}
	}

	/**
	 * Method used to read initial values and attributes from a GRID file.
	 */
	private void fillInits(final IScope scope, final List<Map> inits, final Integer max, final GamaGridFile file) {
		final int num = max == null ? file.length(scope) : Math.min(file.length(scope), max);
		for ( int i = 0; i < num; i++ ) {
			final GamaGisGeometry g = file.get(scope, i);
			final Map map = g.getOrCreateAttributes();
			// The shape is added to the initial values
			map.put(IKeyword.SHAPE, g);
			// GIS attributes are mixed with the attributes of agents
			fillWithUserInit(scope, map);
			inits.add(map);
		}
	}

	/**
	 * Method used to read initial values decribed by the modeler (facet with)
	 */
	private void fillInits(final IScope scope, final List<Map> inits, final Integer max) {
		if ( init == null ) { return; }
		final int num = max == null ? 1 : max;
		for ( int i = 0; i < num; i++ ) {
			final Map map = new GamaMap();
			fillWithUserInit(scope, map);
			inits.add(map);
		}
	}

	/**
	 * Method used to read initial values and attributes from a list of values
	 * @author thai.truongminh@gmail.com
	 * @since 04-09-2012
	 */
	private void fillInits(final IScope scope, final List<Map> initialValues, final Integer max, final IList list)
		throws GamaRuntimeException {
		// get Column name
		final GamaList<Object> colNames = (GamaList<Object>) list.get(0);
		// get Column type
		final GamaList<Object> colTypes = (GamaList<Object>) list.get(1);
		// Get ResultSet
		final GamaList<GamaList<Object>> initValue = (GamaList<GamaList<Object>>) list.get(2);
		// set initialValues to generate species
		final int num = max == null ? initValue.length(scope) : Math.min(max, initValue.length(scope));
		for ( int i = 0; i < num; i++ ) {
			final GamaList<Object> rowList = initValue.get(i);
			final Map map = new GamaMap();
			computeInits(scope, map, rowList, colTypes, colNames);
			initialValues.add(map);
		}
	}

	private IList<? extends IAgent> createAgents(final IScope scope, final IPopulation population, final List<Map> inits) {
		final IList<? extends IAgent> list = population.createAgents(scope, inits.size(), inits, false);
		if ( !sequence.isEmpty() ) {
			for ( final IAgent remoteAgent : list.iterable(scope) ) {
				Object[] result = new Object[1];
				if ( !scope.execute(sequence, remoteAgent, null, result) ) {
					break;
				}
			}
		}
		return list;
	}

	private void fillWithUserInit(final IScope scope, final Map values) {
		if ( init == null ) { return; }
		Files.tempAttributes.push(values);
		try {
			for ( final Map.Entry<String, IExpressionDescription> f : init.entrySet() ) {
				if ( f != null ) {
					values.put(f.getKey(), f.getValue().getExpression().value(scope));
				}
			}
		} finally {
			Files.tempAttributes.pop();
		}
	}

	@Override
	public IType getType() {
		return Types.get(IType.LIST);
	}

	@Override
	public void setFormalArgs(final Arguments args) {
		init = args;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {}

	/*
	 * thai.truongminh@gmail.com
	 * Method: GamaList2ListMap
	 * Description:
	 * created date : 13-09-2012
	 * 25-Feb-2013:
	 * Add transformCRS from GisUtils.transformCRS
	 * Last Modified: 25-Feb-2013
	 */
	private void computeInits(final IScope scope, final Map values, final GamaList<Object> rowList,
		final GamaList<Object> colTypes, final GamaList<Object> colNames) throws GamaRuntimeException {
		if ( init == null ) { return; }
		for ( final Map.Entry<String, IExpressionDescription> f : init.entrySet() ) {
			if ( f != null ) {
				final IExpression valueExpr = f.getValue().getExpression();
				// get parameter
				final String columnName = valueExpr.value(scope).toString().toUpperCase();
				// get column number of parameter
				final int val = colNames.indexOf(columnName);
				if ( ((String) colTypes.get(val)).equalsIgnoreCase("GEOMETRY") ) {
					final Geometry geom = (Geometry) rowList.get(val);
					values.put(f.getKey(), new GamaShape(scope.getTopology().getGisUtils().transform(geom)));
				} else {
					values.put(f.getKey(), rowList.get(val));
				}

			}
		}
	}

}