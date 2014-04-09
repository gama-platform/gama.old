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
import msi.gama.database.sql.SqlConnection;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gama.util.matrix.IMatrix;
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
@facets(value = { @facet(name = IKeyword.SPECIES, type = IType.SPECIES, optional = true, doc = @doc("an expression that evaluates to a species, the species of created agents")),
	@facet(name = IKeyword.RETURNS, type = IType.NEW_TEMP_ID, optional = true, doc = @doc("a new temporary variable name containing the list of created agents (a lsit even if only one agent has been created)")),
	@facet(name = IKeyword.FROM, type = IType.NONE, optional = true, doc = @doc("an expression that evaluates to a localized entity, a list of localized entities, a string (the path of a shapefile, a .csv, a .asc or a OSM file) or a container returned by a request to a database")),
	@facet(name = IKeyword.NUMBER, type = IType.INT, optional = true, doc = @doc("an expression that evaluates to an int, the number of created agents")),
	@facet(name = IKeyword.AS, type = { IType.SPECIES }, optional = true, doc = @doc("")),
	@facet(name = IKeyword.WITH, type = { IType.MAP }, optional = true, doc = @doc("an expression that evaluates to a map, for each pair the key is a species attribute and the value the assigned value")),
	@facet(name = IKeyword.HEADER, type = { IType.BOOL }, optional = true, doc = @doc("an expression that evaluates to a boolean, when creating agents from csv file, specify whether the file header is loaded"))}, omissible = IKeyword.SPECIES)
@doc(value="Allows an agent to create `number` agents of species `species`, to create agents of species `species` from a shapefile or to create agents of species `species` from one or several localized entities (discretization of the localized entity geometries).", usages={
	@usage(value="Its simple syntax to create `an_int` agents of species `a_species` is:", examples ={@example(value="create a_species number: an_int;",isExecutable=false)}),
	@usage("If `number` equals 0 or species is not a species, the statement is ignored."),
	@usage(value="In GAML modelers can create agents of species `a_species  (with two attributes `type` and `nature` with types corresponding to the types of the shapefile attributes) from a shapefile `the_shapefile` while reading attributes 'TYPE_OCC' and 'NATURE' of the shapefile. One agent will be created by object contained in the shapefile:",examples=@example(value="create a_species from: the_shapefile with: [type:: 'TYPE_OCC', nature::'NATURE'];",isExecutable=false)),
	@usage(value="In order to create agents from a .csv file, facet `header` can be used to specified whether we can use columns header:", examples={@example(value="create toto from: \"toto.csv\" header: true with:[att1::read(\"NAME\"), att2::read(\"TYPE\")];",isExecutable=false),@example(value="or",isExecutable=false),@example(value="create toto from: \"toto.csv\" with:[att1::read(0), att2::read(1)]; //with read(int), the index of the column",isExecutable=false)}),
	@usage(value="Created agents are initialized following the rules of their species. If one wants to refer to them after the statement is executed, the returns keyword has to be defined: the agents created will then be referred to by the temporary variable it declares. For instance, the following statement creates 0 to 4 agents of the same species as the sender, and puts them in the temporary variable children for later use.",examples={@example(value="create species (self) number: rnd (4) returns: children;",test=false),@example(value="ask children {",test=true),@example(value="        // ...",test=false),@example(value="}",test=false)}),
	@usage(value="If one wants to specify a special initialization sequence for the agents created, create provides the same possibilities as ask. This extended syntax is:",examples={@example(value="create a_species number: an_int {",isExecutable=false),@example(value="     [statements]",isExecutable=false),@example(value="}",isExecutable=false)}),
	@usage(value="The same rules as in ask apply. The only difference is that, for the agents created, the assignments of variables will bypass the initialization defined in species. For instance:",examples={@example(value="create species(self) number: rnd (4) returns: children {",isExecutable=false),@example(value="     set location <- myself.location + {rnd (2), rnd (2)}; // tells the children to be initially located close to me",isExecutable=false),@example(value="     set parent <- myself; // tells the children that their parent is me (provided the variable parent is declared in this species) ",isExecutable=false),@example(value="}",isExecutable=false)}),	
	@usage(value="Desprecated uses: ", examples={@example(value="// Simple syntax",isExecutable=false),@example(value="create species: a_species number: an_int;",isExecutable=false),@example(value="",isExecutable=false)})
})
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
			if ( s == null ) { throw GamaRuntimeException.error("No population of " + species.toGaml() +
				" is accessible in the context of " + executor + "."); }
			pop = executor.getPopulationFor(s);
		}
		scope.addVarWithValue(IKeyword.MYSELF, executor);
		// We grab whatever initial values are defined (from CSV, GIS, or user)
		final List<Map> inits = new GamaList(max == null ? 10 : max);
		final Object source = getSource(scope);
		if ( source instanceof GamaCSVFile ) {
			fillInits(scope, inits, max, (GamaCSVFile) source);
		}
		else if ( source instanceof IList && ((IList) source).get(0) instanceof List ) {
			// DBAccess
			fillInitsWithDBResults(scope, inits, max, (IList) source);
		} else if ( source instanceof IList && ((IList) source).get(0) instanceof GamaShape ||
			source instanceof GamaShapeFile || source instanceof GamaOsmFile ) {
			fillInits(scope, inits, max, (IAddressableContainer) source);
		} else if ( source instanceof GamaGridFile ) {
			fillInits(scope, inits, max, (GamaGridFile) source);
		} else if ( source instanceof GamaTextFile ||  source instanceof GamaCSVFile) {
			fillInits(scope, inits, max, (GamaFile) source);
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

	private void fillInits(IScope scope, List<Map> inits, Integer max,
			GamaCSVFile source) {
		final boolean hasHeader = header == null ? false : Cast.asBool(scope, header.value(scope));
		IMatrix mat = source.getContents(scope);
		if (mat == null || mat.isEmpty(scope))
			return;
		int rows = mat.getRows(scope);
		final int num = max == null ? rows : Math.min(rows, max);
		
		IList<String> headers = new GamaList<String>();
		if (hasHeader) {
			for (Object obj : mat.getRow(scope, 0)) {
				headers.add(Cast.asString(scope,obj));
			}
		}
		for ( int i = hasHeader ? 1 : 0; i < num; i++ ) {
			final GamaMap map = new GamaMap();
			final IList vals = mat.getRow(scope, i);
			for ( int j = 0; j < vals.size(); j++ ) {
				map.put(hasHeader ?headers.get(j) : j, vals.get(j));
			}
			// CSV attributes are mixed with the attributes of agents
			fillWithUserInit(scope, map);
			inits.add(map);
		}
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
	private void fillInits(final IScope scope, final List<Map> inits, final Integer max,
		final IAddressableContainer<Integer, GamaShape, Integer, GamaShape> file) {
		final int num = max == null ? file.length(scope) : Math.min(file.length(scope), max);
		for ( int i = 0; i < num; i++ ) {
			final GamaShape g = file.get(scope, i);
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
			final IShape g = file.get(scope, i);
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
	private void fillInitsWithDBResults(final IScope scope, final List<Map> initialValues, final Integer max,
		final IList list) throws GamaRuntimeException {
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
				if ( ((String) colTypes.get(val)).equalsIgnoreCase(SqlConnection.GEOMETRYTYPE) ) {
					final Geometry geom = (Geometry) rowList.get(val);
					values.put(f.getKey(), new GamaShape(geom));
				} else {
					values.put(f.getKey(), rowList.get(val));
				}

			}
		}
	}

}