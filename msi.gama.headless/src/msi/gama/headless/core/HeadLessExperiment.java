package msi.gama.headless.core;

import java.util.Collection;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IValue;
import msi.gama.common.interfaces.ItemList;
import msi.gama.kernel.batch.IExploration;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.FileOutput;
import msi.gama.outputs.IOutputManager;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.ActionStatement;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.IStatement.WithArgs;
import msi.gaml.statements.UserCommandStatement;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;
  
@symbol(name = { IKeyword.HEADLESS_UI }, kind = ISymbolKind.EXPERIMENT, with_sequence = true)
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.LABEL, values = { IKeyword.HEADLESS_UI }, optional = false) }, omissible = IKeyword.NAME)
@inside(symbols = IKeyword.MODEL)
public class HeadLessExperiment  implements IHeadLessExperiment {
	private ExperimentSpecies myExperiment;
	
	public HeadLessExperiment(ExperimentSpecies exp) {
		myExperiment = exp; 
	}

	@Override
	public void setSeed(final double seed) {
		// this.seed = seed;
	}

	@Override
	public void start(final int nbStep) {
		for ( int i = 0; i < nbStep; i++ ) {
			GAMA.controller.userStep();
		}

	}

		@Override
	public boolean isGui() {
		return false;
	}

		@Override
		public IModel getModel() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setModel(IModel model) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public IOutputManager getSimulationOutputs() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IOutputManager getExperimentOutputs() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ItemList getParametersEditors() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasParameter(String name) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ExperimentAgent getAgent() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IScope getExperimentScope() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void open() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void reload() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public SimulationAgent getCurrentSimulation() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, IParameter> getParameters() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IExploration getExplorationAlgorithm() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public FileOutput getLog() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isBatch() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Map<String, Batch> getExplorableParameters() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IExpression getFrequency() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IExpression getSchedule() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean extendsSpecies(ISpecies s) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isGrid() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isGraph() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public IList<ISpecies> getMicroSpecies() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ISpecies getMicroSpecies(String microSpeciesName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasMicroSpecies() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containMicroSpecies(ISpecies species) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ISpecies getParentSpecies() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isPeer(ISpecies other) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public List<ISpecies> getSelfWithParents() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<UserCommandStatement> getUserCommands() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T extends IStatement> T getStatement(Class<T> clazz, String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public WithArgs getAction(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IList<ActionStatement> getActions() {
			// TODO Auto-generated method stub
			return null;
		}

	
		@Override
		public IList<String> getAspectNames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IList<IStatement> getBehaviors() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IArchitecture getArchitecture() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getArchitectureName() {
			// TODO Auto-generated method stub
			return this.myExperiment.getArchitectureName();
		}

		@Override
		public ISpecies getMacroSpecies() {
			// TODO Auto-generated method stub
			return this.myExperiment.getMacroSpecies();
		}

		@Override
		public String getParentName() {
			// TODO Auto-generated method stub
			return this.myExperiment.getParentName();
		}

		@Override
		public IVariable getVar(String n) {
			// TODO Auto-generated method stub
			return this.myExperiment.getVar(n);
		}

		@Override
		public IList<String> getVarNames() {
			// TODO Auto-generated method stub
			return this.myExperiment.getVarNames();
		}

		@Override
		public Collection<IVariable> getVars() {
			// TODO Auto-generated method stub
			return this.myExperiment.getVars();
		}

		@Override
		public boolean hasAspect(String n) {
			// TODO Auto-generated method stub
			return this.myExperiment.hasAspect(n);
		}

		@Override
		public boolean hasVar(String name) {
			// TODO Auto-generated method stub
			return this.myExperiment.hasVar(name);
		}

		@Override
		public void setMacroSpecies(ISpecies macroSpecies) {
			// TODO Auto-generated method stub
			this.myExperiment.setMacroSpecies(macroSpecies);
		}

		@Override
		public boolean isMirror() {
			// TODO Auto-generated method stub
			return this.myExperiment.isMirror();
		}

		@Override
		public Boolean implementsSkill(String skill) {
			// TODO Auto-generated method stub
			return this.myExperiment.implementsSkill(skill);
		}

		@Override
		public Collection<String> getMicroSpeciesNames() {
			// TODO Auto-generated method stub
			return this.myExperiment.getMicroSpeciesNames();
		}

		@Override
		public void dispose() {
			this.myExperiment.dispose();
		}

		@Override
		public IDescription getDescription() {
			// TODO Auto-generated method stub
			return this.myExperiment.getDescription();
		}

		@Override
		public IExpression getFacet(String key) {
			// TODO Auto-generated method stub
			return this.myExperiment.getFacet(key);
		}

		@Override
		public boolean hasFacet(String key) {
			// TODO Auto-generated method stub
			return this.myExperiment.hasFacet(key);
		}

		@Override
		public void setChildren(List<? extends ISymbol> children) {
			// TODO Auto-generated method stub
			this.myExperiment.setChildren(children);
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return this.myExperiment.getName();
		}

		@Override
		public void setName(String newName) {
			// TODO Auto-generated method stub
			this.myExperiment.setName(newName);
		}

		@Override
		public IAgent get(IScope scope, Integer index)
				throws GamaRuntimeException {
			// TODO Auto-generated method stub
			return this.myExperiment.get(scope, index);
		}

		@Override
		public Object getFromIndicesList(IScope scope, IList indices)
				throws GamaRuntimeException {
			// TODO Auto-generated method stub
			return this.myExperiment.getFromIndicesList(scope, indices);
		}

		@Override
		public boolean contains(IScope scope, Object o)
				throws GamaRuntimeException {
			// TODO Auto-generated method stub
			return this.myExperiment.contains(scope, o);
		}

		@Override
		public IAgent first(IScope scope) throws GamaRuntimeException {
			// TODO Auto-generated method stub
			return this.myExperiment.first(scope);
		}

		@Override
		public IAgent last(IScope scope) throws GamaRuntimeException {
			// TODO Auto-generated method stub
			return this.myExperiment.last(scope);
		}

		@Override
		public int length(IScope scope) {
			// TODO Auto-generated method stub
			return this.myExperiment.length(scope);
		}

		@Override
		public boolean isEmpty(IScope scope) {
			// TODO Auto-generated method stub
			return this.myExperiment.isEmpty(scope);
		}

		@Override
		public IContainer<Integer, IAgent> reverse(IScope scope)
				throws GamaRuntimeException {
			// TODO Auto-generated method stub
			return this.myExperiment.reverse(scope);
		}

		@Override
		public IAgent any(IScope scope) {
			// TODO Auto-generated method stub
			return this.myExperiment.any(scope);
		}

		@Override
		public boolean checkBounds(Integer index, boolean forAdding) {
			return this.myExperiment.checkBounds(index, forAdding);
		}

		@Override
		public void add(IScope scope, Integer index, Object value,
				Object parameter, boolean all, boolean add) {
			this.myExperiment.add(scope, index, value, parameter, all, add);
			
		}

		@Override
		public void remove(IScope scope, Object index, Object value, boolean all) {
			this.myExperiment.remove(scope, index, value, all);
			
		}

		@Override
		public IList listValue(IScope scope) throws GamaRuntimeException {
			// TODO Auto-generated method stub
			return this.myExperiment.listValue(scope);
		}

		@Override
		public IMatrix matrixValue(IScope scope) throws GamaRuntimeException {
			// TODO Auto-generated method stub
			return this.myExperiment.matrixValue(scope);
		}

		@Override
		public IMatrix matrixValue(IScope scope, ILocation preferredSize)
				throws GamaRuntimeException {
			// TODO Auto-generated method stub
			return this.myExperiment.matrixValue(scope, preferredSize);
		}

		@Override
		public GamaMap mapValue(IScope scope) throws GamaRuntimeException {
			// TODO Auto-generated method stub
			return this.myExperiment.mapValue(scope);
		}

		@Override
		public Iterable<? extends IAgent> iterable(IScope scope) {
			// TODO Auto-generated method stub
			return this.myExperiment.iterable(scope);
		}

		@Override
		public String stringValue(IScope scope) throws GamaRuntimeException {
			// TODO Auto-generated method stub
			return this.myExperiment.stringValue(scope);
		}

		@Override
		public IValue copy(IScope scope) throws GamaRuntimeException {
			// TODO Auto-generated method stub
			return this.myExperiment.copy(scope);
		}

		@Override
		public String toGaml() {
			// TODO Auto-generated method stub
			return this.myExperiment.toGaml();
		}

		@Override
		public Iterator<IAgent> iterator() {
			// TODO Auto-generated method stub
			return this.myExperiment.iterator();
		}

		@Override
		public void setParameterValue(String name, Object value) {
			this.myExperiment.setParameterValue(name, value);
		}

		@Override
		public IExecutable getAspect(String n) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IList<IExecutable> getAspects() {
			// TODO Auto-generated method stub
			return null;
		}

/*	public void setChildren(final List<? extends ISymbol> children) {
		super.setChildren(children);
		for ( ISymbol s : children ) {
			if ( s instanceof IParameter ) {
				IParameter p = (IParameter) s;
				final String name = p.getName();
				boolean already = parameters.containsKey(name);
				if ( !already ) {
					parameters.put(name, p);
				}
				boolean registerParameter = !already;
			}
		}
	}
	*/
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

}
