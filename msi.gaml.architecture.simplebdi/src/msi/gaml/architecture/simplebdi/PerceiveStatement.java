/*********************************************************************************************
 * 
 *
 * 'PerceiveStatement.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/

package msi.gaml.architecture.simplebdi;

import java.util.Iterator;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.statements.RemoteSequence;
import msi.gaml.types.IType;


@symbol(name={PerceiveStatement.PERCEIVE}, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, remote_context=true)
@inside(kinds = { ISymbolKind.SPECIES })
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.ID, optional = true, doc = @doc("the name of the perception")),
	@facet(name = IKeyword.AS, type = IType.SPECIES, optional = true, doc = @doc("an expression that evaluates to a species")),
	@facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true, doc = @doc("a boolean to tell when does the perceive is active" )),
	@facet(name = IKeyword.IN, type = {IType.FLOAT,IType.GEOMETRY}, optional = true, doc = @doc("a float or a geometry. If it is a float, it's a radius of a detection area. If it is a geometry, it is the area of detection of others species.")),
	@facet(name = IKeyword.TARGET, type = { IType.CONTAINER, IType.POINT }, optional=false, doc = @doc("the list of the agent you want to perceive"))
}, omissible = IKeyword.NAME)
@doc(value = "Allow the agent, with a bdi architecture, to perceive others agents" , usages = {
		@usage(value = "the basic syntax to perceive agents inside a circle of perception", examples = {
				@example(value = "perceive name_of-perception target: the_agents_you_want_to_perceive in: a_distance when: a_certain_condition {" , isExecutable = false),
				@example(value = "Here you are in the context of the perceived agents. To refer to the agent who does the perception, use myself.", isExecutable = false),
				@example(value = "If you want to make an action (such as adding a belief for example), use ask myself{ do the_action}", isExecutable=false),
				@example(value = "}", isExecutable = false)
		})
})

public class PerceiveStatement extends AbstractStatementSequence{

	public static final String PERCEIVE = "perceive";
	
	private RemoteSequence sequence = null;
	
	final IExpression _when;
	final IExpression _in;
	private final IExpression target = getFacet(IKeyword.TARGET);
	private final Object[] result = new Object[1];
	
	public IExpression getWhen(){
		return _when;
	}
	
	public IExpression getIn(){
		return _in;
	}
	
	@Override
	public void setChildren(final List<? extends ISymbol> com) {
		sequence = new RemoteSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}
	
	@Override
	public void leaveScope(final IScope scope) {
		scope.popLoop();
		super.leaveScope(scope);
	}
	
	public PerceiveStatement(IDescription desc) {
		super(desc);
		_when = getFacet(IKeyword.WHEN);
		if(hasFacet(IKeyword.IN)){
			_in = getFacet(IKeyword.IN);
		}else{
			_in=null;
		}
		if ( hasFacet(IKeyword.NAME) ) {
			setName(getLiteral(IKeyword.NAME));
		}
	}
	public Object privateExecuteIn(IScope scope) throws GamaRuntimeException{
		if ( _when == null || Cast.asBool(scope, _when.value(scope)) ){
			final Object obj = target.value(scope);
			Object inArg = null;
			if(_in!=null){
				inArg = _in.value(scope);
			}			
			
			if (inArg instanceof Float || inArg instanceof Integer || inArg instanceof Double){
				IList<IAgent> temp = msi.gaml.operators.Spatial.Queries.at_distance(scope, (IContainer)obj, Cast.asFloat(scope, inArg));
				final Iterator<IAgent> runners = ((IContainer) temp).iterable(scope).iterator();
				if(runners!=null){
					while (runners.hasNext() && scope.execute(sequence, runners.next(), null, result)) {}
				}
				return result[0];
					
			}else if(inArg instanceof msi.gaml.types.GamaGeometryType || inArg instanceof GamaShape){
				IList<IAgent> temp = msi.gaml.operators.Spatial.Queries.overlapping(scope, (IContainer)obj, Cast.asGeometry(scope, inArg));
				final Iterator<IAgent> runners = ((IContainer) temp).iterable(scope).iterator();
					if(runners!=null){
						while (runners.hasNext() && scope.execute(sequence, runners.next(), null, result)) {}
					}
				return result[0];
			}else{
			final Iterator<IAgent> runners =
					obj instanceof IContainer ? ((IContainer) obj).iterable(scope).iterator() : null;
			while (runners.hasNext() && scope.execute(sequence, runners.next(), null, result)) {}
			return result[0];
			}
		}
		return null;
	
	}
}
