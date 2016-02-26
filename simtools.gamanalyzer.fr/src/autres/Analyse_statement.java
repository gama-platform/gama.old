package autres;

import gama_analyzer.AgentGroupFollower;

import java.util.List;
import java.util.Map;
import weka.clusterers.Cobweb;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation.GamlGridAgent;
import msi.gama.metamodel.topology.grid.GridTopology;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaMatrix;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.species.GamlSpecies;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;
import msi.gama.util.*;

@facets (value = {@facet(name = Analyse_statement.ANALYSE_STATEMENT_VARIABLE, type = { IType.STRING, IType.STRING}, optional = false),
		//@facet(name = Analyse_statement.ANALYSE_STATEMENT_VAR, type = { IType.STRING, IType.STRING}, optional = false),// facets = paramètres
		@facet(name = Analyse_statement.ANALYSE_STATEMENT_CONSTRAINT, type = { IType.STRING, IType.STRING}, optional = false)}, 
		omissible = Analyse_statement.ANALYSE_STATEMENT_VARIABLE) 
@symbol (name = {"analyse"}, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false,
concept = {} )
@inside (symbols = IKeyword.EXPERIMENT)

public class Analyse_statement extends AbstractStatement {

	public static final String ANALYSE_STATEMENT_VARIABLE = "species_to_analyse"; 
	//public static final String ANALYSE_STATEMENT_VAR = "list_to_analyse";
	public static final String ANALYSE_STATEMENT_CONSTRAINT = "with_constraint";

	public static String getAnalyseStatementVariable() { return ANALYSE_STATEMENT_VARIABLE; }
	public static String getAnalyseStatementConstraint() { return ANALYSE_STATEMENT_CONSTRAINT; }

	public Analyse_statement(IDescription desc) {
		super(desc);

		desc.getFacets().get(ANALYSE_STATEMENT_VARIABLE);
		desc.getFacets().get(ANALYSE_STATEMENT_CONSTRAINT);
	}
	
	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		String analysedSpecies = (String) this.getFacet(ANALYSE_STATEMENT_VARIABLE).value(scope);
		String constraint = (String) this.getFacet(ANALYSE_STATEMENT_CONSTRAINT).value(scope);
		return null;
	}
}
