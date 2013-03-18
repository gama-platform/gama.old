package msi.gama.lang.gaml.serializer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import msi.gama.lang.gaml.gaml.Access;
import msi.gama.lang.gaml.gaml.ActionArguments;
import msi.gama.lang.gaml.gaml.ActionFakeDefinition;
import msi.gama.lang.gaml.gaml.ActionRef;
import msi.gama.lang.gaml.gaml.ArgumentDefinition;
import msi.gama.lang.gaml.gaml.ArgumentPair;
import msi.gama.lang.gaml.gaml.Array;
import msi.gama.lang.gaml.gaml.Binary;
import msi.gama.lang.gaml.gaml.Block;
import msi.gama.lang.gaml.gaml.BooleanLiteral;
import msi.gama.lang.gaml.gaml.ColorLiteral;
import msi.gama.lang.gaml.gaml.Dot;
import msi.gama.lang.gaml.gaml.DoubleLiteral;
import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.lang.gaml.gaml.ExpressionList;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.Function;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.If;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.IntLiteral;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.Pair;
import msi.gama.lang.gaml.gaml.Parameter;
import msi.gama.lang.gaml.gaml.Parameters;
import msi.gama.lang.gaml.gaml.Point;
import msi.gama.lang.gaml.gaml.ReservedLiteral;
import msi.gama.lang.gaml.gaml.S_Action;
import msi.gama.lang.gaml.gaml.S_Definition;
import msi.gama.lang.gaml.gaml.S_DirectAssignment;
import msi.gama.lang.gaml.gaml.S_Do;
import msi.gama.lang.gaml.gaml.S_Equation;
import msi.gama.lang.gaml.gaml.S_Experiment;
import msi.gama.lang.gaml.gaml.S_If;
import msi.gama.lang.gaml.gaml.S_Loop;
import msi.gama.lang.gaml.gaml.S_Other;
import msi.gama.lang.gaml.gaml.S_Reflex;
import msi.gama.lang.gaml.gaml.S_Return;
import msi.gama.lang.gaml.gaml.S_Set;
import msi.gama.lang.gaml.gaml.S_Species;
import msi.gama.lang.gaml.gaml.S_Var;
import msi.gama.lang.gaml.gaml.SkillFakeDefinition;
import msi.gama.lang.gaml.gaml.SkillRef;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.StringEvaluator;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.gaml.TypeFakeDefinition;
import msi.gama.lang.gaml.gaml.TypeRef;
import msi.gama.lang.gaml.gaml.Unary;
import msi.gama.lang.gaml.gaml.Unit;
import msi.gama.lang.gaml.gaml.UnitFakeDefinition;
import msi.gama.lang.gaml.gaml.UnitName;
import msi.gama.lang.gaml.gaml.VarFakeDefinition;
import msi.gama.lang.gaml.gaml.VariableRef;
import msi.gama.lang.gaml.services.GamlGrammarAccess;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.serializer.acceptor.ISemanticSequenceAcceptor;
import org.eclipse.xtext.serializer.acceptor.SequenceFeeder;
import org.eclipse.xtext.serializer.diagnostic.ISemanticSequencerDiagnosticProvider;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic.Acceptor;
import org.eclipse.xtext.serializer.sequencer.AbstractSemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.GenericSequencer;
import org.eclipse.xtext.serializer.sequencer.ISemanticNodeProvider.INodesForEObjectProvider;
import org.eclipse.xtext.serializer.sequencer.ISemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService.ValueTransient;

@SuppressWarnings("restriction")
public class AbstractGamlSemanticSequencer extends AbstractSemanticSequencer {

	@Inject
	protected GamlGrammarAccess grammarAccess;
	
	@Inject
	protected ISemanticSequencerDiagnosticProvider diagnosticProvider;
	
	@Inject
	protected ITransientValueService transientValues;
	
	@Inject
	@GenericSequencer
	protected Provider<ISemanticSequencer> genericSequencerProvider;
	
	protected ISemanticSequencer genericSequencer;
	
	
	@Override
	public void init(ISemanticSequencer sequencer, ISemanticSequenceAcceptor sequenceAcceptor, Acceptor errorAcceptor) {
		super.init(sequencer, sequenceAcceptor, errorAcceptor);
		this.genericSequencer = genericSequencerProvider.get();
		this.genericSequencer.init(sequencer, sequenceAcceptor, errorAcceptor);
	}
	
	public void createSequence(EObject context, EObject semanticObject) {
		if(semanticObject.eClass().getEPackage() == GamlPackage.eINSTANCE) switch(semanticObject.eClass().getClassifierID()) {
			case GamlPackage.ACCESS:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_Access(context, (Access) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ACTION_ARGUMENTS:
				if(context == grammarAccess.getActionArgumentsRule()) {
					sequence_ActionArguments(context, (ActionArguments) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ACTION_FAKE_DEFINITION:
				if(context == grammarAccess.getActionDefinitionRule() ||
				   context == grammarAccess.getActionFakeDefinitionRule() ||
				   context == grammarAccess.getGamlDefinitionRule()) {
					sequence_ActionFakeDefinition(context, (ActionFakeDefinition) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ACTION_REF:
				if(context == grammarAccess.getActionRefRule()) {
					sequence_ActionRef(context, (ActionRef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ARGUMENT_DEFINITION:
				if(context == grammarAccess.getArgumentDefinitionRule() ||
				   context == grammarAccess.getGamlDefinitionRule() ||
				   context == grammarAccess.getVarDefinitionRule()) {
					sequence_ArgumentDefinition(context, (ArgumentDefinition) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ARGUMENT_PAIR:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getArgumentPairRule() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_ArgumentPair(context, (ArgumentPair) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ARRAY:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_Primary(context, (Array) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.BINARY:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_Binary(context, (Binary) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.BLOCK:
				if(context == grammarAccess.getBlockRule()) {
					sequence_Block(context, (Block) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.BOOLEAN_LITERAL:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_TerminalExpression(context, (BooleanLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.COLOR_LITERAL:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_TerminalExpression(context, (ColorLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DOT:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_Dot(context, (Dot) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DOUBLE_LITERAL:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_TerminalExpression(context, (DoubleLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.EXPRESSION:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_Addition(context, (Expression) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.EXPRESSION_LIST:
				if(context == grammarAccess.getExpressionListRule()) {
					sequence_ExpressionList(context, (ExpressionList) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getParameterListRule()) {
					sequence_ParameterList(context, (ExpressionList) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.FACET:
				if(context == grammarAccess.getActionFacetRule()) {
					sequence_ActionFacet(context, (Facet) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getClassicFacetRule()) {
					sequence_ClassicFacet(context, (Facet) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getDefinitionFacetRule() ||
				   context == grammarAccess.getGamlDefinitionRule() ||
				   context == grammarAccess.getVarDefinitionRule()) {
					sequence_DefinitionFacet(context, (Facet) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getFacetRule()) {
					sequence_Facet(context, (Facet) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getFunctionFacetRule()) {
					sequence_FunctionFacet(context, (Facet) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getTypeFacetRule()) {
					sequence_TypeFacet(context, (Facet) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.FUNCTION:
				if(context == grammarAccess.getAbstractRefRule() ||
				   context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getFunctionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_Function(context, (Function) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.IF:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_If(context, (If) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.IMPORT:
				if(context == grammarAccess.getImportRule()) {
					sequence_Import(context, (Import) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.INT_LITERAL:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_TerminalExpression(context, (IntLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.MODEL:
				if(context == grammarAccess.getModelRule()) {
					sequence_Model(context, (Model) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.PAIR:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_Pair(context, (Pair) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.PARAMETER:
				if(context == grammarAccess.getParameterRule()) {
					sequence_Parameter(context, (Parameter) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.PARAMETERS:
				if(context == grammarAccess.getParametersRule()) {
					sequence_Parameters(context, (Parameters) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_Primary(context, (Parameters) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.POINT:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_Primary(context, (Point) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.RESERVED_LITERAL:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_TerminalExpression(context, (ReservedLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SACTION:
				if(context == grammarAccess.getActionDefinitionRule() ||
				   context == grammarAccess.getGamlDefinitionRule() ||
				   context == grammarAccess.getS_ActionRule() ||
				   context == grammarAccess.getS_DeclarationRule() ||
				   context == grammarAccess.getStatementRule() ||
				   context == grammarAccess.getVarDefinitionRule()) {
					sequence_S_Action(context, (S_Action) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SDEFINITION:
				if(context == grammarAccess.getActionDefinitionRule() ||
				   context == grammarAccess.getGamlDefinitionRule() ||
				   context == grammarAccess.getS_DeclarationRule() ||
				   context == grammarAccess.getS_DefinitionRule() ||
				   context == grammarAccess.getStatementRule() ||
				   context == grammarAccess.getVarDefinitionRule()) {
					sequence_S_Definition(context, (S_Definition) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SDIRECT_ASSIGNMENT:
				if(context == grammarAccess.getS_AssignmentRule() ||
				   context == grammarAccess.getS_DirectAssignmentRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_S_DirectAssignment(context, (S_DirectAssignment) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SDO:
				if(context == grammarAccess.getS_DoRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_S_Do(context, (S_Do) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SEQUATION:
				if(context == grammarAccess.getS_AssignmentRule() ||
				   context == grammarAccess.getS_EquationRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_S_Equation(context, (S_Equation) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SEXPERIMENT:
				if(context == grammarAccess.getGamlDefinitionRule() ||
				   context == grammarAccess.getS_DeclarationRule() ||
				   context == grammarAccess.getS_ExperimentRule() ||
				   context == grammarAccess.getStatementRule() ||
				   context == grammarAccess.getVarDefinitionRule()) {
					sequence_S_Experiment(context, (S_Experiment) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SIF:
				if(context == grammarAccess.getS_IfRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_S_If(context, (S_If) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SLOOP:
				if(context == grammarAccess.getGamlDefinitionRule() ||
				   context == grammarAccess.getS_DeclarationRule() ||
				   context == grammarAccess.getS_LoopRule() ||
				   context == grammarAccess.getStatementRule() ||
				   context == grammarAccess.getVarDefinitionRule()) {
					sequence_S_Loop(context, (S_Loop) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SOTHER:
				if(context == grammarAccess.getS_OtherRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_S_Other(context, (S_Other) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SREFLEX:
				if(context == grammarAccess.getGamlDefinitionRule() ||
				   context == grammarAccess.getS_DeclarationRule() ||
				   context == grammarAccess.getS_ReflexRule() ||
				   context == grammarAccess.getStatementRule() ||
				   context == grammarAccess.getVarDefinitionRule()) {
					sequence_S_Reflex(context, (S_Reflex) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SRETURN:
				if(context == grammarAccess.getS_ReturnRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_S_Return(context, (S_Return) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SSET:
				if(context == grammarAccess.getS_AssignmentRule() ||
				   context == grammarAccess.getS_SetRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_S_Set(context, (S_Set) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SSPECIES:
				if(context == grammarAccess.getGamlDefinitionRule() ||
				   context == grammarAccess.getS_DeclarationRule() ||
				   context == grammarAccess.getS_SpeciesRule() ||
				   context == grammarAccess.getStatementRule() ||
				   context == grammarAccess.getTypeDefinitionRule() ||
				   context == grammarAccess.getVarDefinitionRule()) {
					sequence_S_Species(context, (S_Species) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SVAR:
				if(context == grammarAccess.getGamlDefinitionRule() ||
				   context == grammarAccess.getS_DeclarationRule() ||
				   context == grammarAccess.getS_VarRule() ||
				   context == grammarAccess.getStatementRule() ||
				   context == grammarAccess.getVarDefinitionRule()) {
					sequence_S_Var(context, (S_Var) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SKILL_FAKE_DEFINITION:
				if(context == grammarAccess.getGamlDefinitionRule() ||
				   context == grammarAccess.getSkillFakeDefinitionRule()) {
					sequence_SkillFakeDefinition(context, (SkillFakeDefinition) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SKILL_REF:
				if(context == grammarAccess.getSkillRefRule()) {
					sequence_SkillRef(context, (SkillRef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.STATEMENT:
				if(context == grammarAccess.getS_1Expr_Facets_BlockOrEndRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_S_1Expr_Facets_BlockOrEnd(context, (Statement) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.STRING_EVALUATOR:
				if(context == grammarAccess.getModelRule()) {
					sequence_Model(context, (StringEvaluator) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.STRING_LITERAL:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_TerminalExpression(context, (StringLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.TYPE_FAKE_DEFINITION:
				if(context == grammarAccess.getGamlDefinitionRule() ||
				   context == grammarAccess.getTypeDefinitionRule() ||
				   context == grammarAccess.getTypeFakeDefinitionRule()) {
					sequence_TypeFakeDefinition(context, (TypeFakeDefinition) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.TYPE_REF:
				if(context == grammarAccess.getTypeRefRule()) {
					sequence_TypeRef(context, (TypeRef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.UNARY:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_Unary(context, (Unary) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.UNIT:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0()) {
					sequence_Unit(context, (Unit) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.UNIT_FAKE_DEFINITION:
				if(context == grammarAccess.getGamlDefinitionRule() ||
				   context == grammarAccess.getUnitFakeDefinitionRule()) {
					sequence_UnitFakeDefinition(context, (UnitFakeDefinition) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.UNIT_NAME:
				if(context == grammarAccess.getUnitRefRule()) {
					sequence_UnitRef(context, (UnitName) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.VAR_FAKE_DEFINITION:
				if(context == grammarAccess.getGamlDefinitionRule() ||
				   context == grammarAccess.getVarDefinitionRule() ||
				   context == grammarAccess.getVarFakeDefinitionRule()) {
					sequence_VarFakeDefinition(context, (VarFakeDefinition) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.VARIABLE_REF:
				if(context == grammarAccess.getAbstractRefRule() ||
				   context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndRule() ||
				   context == grammarAccess.getAndAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getBinaryRule() ||
				   context == grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getComparisonRule() ||
				   context == grammarAccess.getComparisonAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getDotRule() ||
				   context == grammarAccess.getDotAccess().getDotLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getIfRule() ||
				   context == grammarAccess.getIfAccess().getIfLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrRule() ||
				   context == grammarAccess.getOrAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairRule() ||
				   context == grammarAccess.getPairAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrimaryRule() ||
				   context == grammarAccess.getUnaryRule() ||
				   context == grammarAccess.getUnitRule() ||
				   context == grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getVariableRefRule()) {
					sequence_VariableRef(context, (VariableRef) semanticObject); 
					return; 
				}
				else break;
			}
		if (errorAcceptor != null) errorAcceptor.accept(diagnosticProvider.createInvalidContextOrTypeDiagnostic(semanticObject, context));
	}
	
	/**
	 * Constraint:
	 *     (left=Access_Access_1_0_0 args=ExpressionList)
	 */
	protected void sequence_Access(EObject context, Access semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (args+=ArgumentDefinition args+=ArgumentDefinition*)
	 */
	protected void sequence_ActionArguments(EObject context, ActionArguments semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=ActionFacetKey expr=ActionRef)
	 */
	protected void sequence_ActionFacet(EObject context, Facet semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     name=Valid_ID
	 */
	protected void sequence_ActionFakeDefinition(EObject context, ActionFakeDefinition semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_DEFINITION__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_DEFINITION__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getActionFakeDefinitionAccess().getNameValid_IDParserRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     ref=[ActionDefinition|Valid_ID]
	 */
	protected void sequence_ActionRef(EObject context, ActionRef semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (
	 *         (left=Addition_Expression_1_0_0 (op='+' | op='-') right=Multiplication) | 
	 *         (left=Multiplication_Expression_1_0_0 (op='*' | op='/' | op='^') right=Binary) | 
	 *         (
	 *             left=Comparison_Expression_1_0_0 
	 *             (
	 *                 op='!=' | 
	 *                 op='=' | 
	 *                 op='>=' | 
	 *                 op='<=' | 
	 *                 op='<' | 
	 *                 op='>'
	 *             ) 
	 *             right=Addition
	 *         ) | 
	 *         (left=And_Expression_1_0 op='and' right=Comparison) | 
	 *         (left=Or_Expression_1_0 op='or' right=And)
	 *     )
	 */
	protected void sequence_Addition(EObject context, Expression semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (type=TypeRef name=Valid_ID default=Expression?)
	 */
	protected void sequence_ArgumentDefinition(EObject context, ArgumentDefinition semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     ((op=Valid_ID | op=DefinitionFacetKey | op=TypeFacetKey | op=SpecialFacetKey | op=ActionFacetKey)? right=If)
	 */
	protected void sequence_ArgumentPair(EObject context, ArgumentPair semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=Binary_Binary_1_0_0 op=Valid_ID right=Unit)
	 */
	protected void sequence_Binary(EObject context, Binary semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__OP) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__OP));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getBinaryAccess().getBinaryLeftAction_1_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getBinaryAccess().getOpValid_IDParserRuleCall_1_0_1_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getBinaryAccess().getRightUnitParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (function=Expression | statements+=Statement*)
	 */
	protected void sequence_Block(EObject context, Block semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     ((key=ID | key='<-' | key=SpecialFacetKey) expr=Expression)
	 */
	protected void sequence_ClassicFacet(EObject context, Facet semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=DefinitionFacetKey (name=Valid_ID | name=STRING))
	 */
	protected void sequence_DefinitionFacet(EObject context, Facet semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=Dot_Dot_1_0 (op='.' right=Primary))
	 */
	protected void sequence_Dot(EObject context, Dot semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (exprs+=Expression exprs+=Expression*)
	 */
	protected void sequence_ExpressionList(EObject context, ExpressionList semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (
	 *         (key=DefinitionFacetKey (name=Valid_ID | name=STRING)) | 
	 *         ((key='function:' | key='->') expr=Expression) | 
	 *         ((key=ID | key='<-' | key=SpecialFacetKey) expr=Expression) | 
	 *         (key=TypeFacetKey expr=TypeRef) | 
	 *         (key=ActionFacetKey expr=ActionRef)
	 *     )
	 */
	protected void sequence_Facet(EObject context, Facet semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     ((key='function:' | key='->') expr=Expression)
	 */
	protected void sequence_FunctionFacet(EObject context, Facet semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (op=Valid_ID args=ExpressionList)
	 */
	protected void sequence_Function(EObject context, Function semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=If_If_1_0 op='?' right=Or ifFalse=Or)
	 */
	protected void sequence_If(EObject context, If semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__OP) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__OP));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__RIGHT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.IF__IF_FALSE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.IF__IF_FALSE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getIfAccess().getIfLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getIfAccess().getOpQuestionMarkKeyword_1_1_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getIfAccess().getRightOrParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.accept(grammarAccess.getIfAccess().getIfFalseOrParserRuleCall_1_4_0(), semanticObject.getIfFalse());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     importURI=STRING
	 */
	protected void sequence_Import(EObject context, Import semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.IMPORT__IMPORT_URI) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.IMPORT__IMPORT_URI));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0(), semanticObject.getImportURI());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID imports+=Import* statements+=Statement*)
	 */
	protected void sequence_Model(EObject context, Model semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID expr=Expression)
	 */
	protected void sequence_Model(EObject context, StringEvaluator semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=Pair_Pair_1_0_0 op='::' right=If)
	 */
	protected void sequence_Pair(EObject context, Pair semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__OP) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__OP));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getPairAccess().getPairLeftAction_1_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getPairAccess().getOpColonColonKeyword_1_0_1_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getPairAccess().getRightIfParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (exprs+=Parameter exprs+=Parameter*)
	 */
	protected void sequence_ParameterList(EObject context, ExpressionList semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (
	 *         (builtInFacetKey=DefinitionFacetKey | builtInFacetKey=TypeFacetKey | builtInFacetKey=SpecialFacetKey | builtInFacetKey=ActionFacetKey | left=VariableRef) 
	 *         right=Expression
	 *     )
	 */
	protected void sequence_Parameter(EObject context, Parameter semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (params=ParameterList?)
	 */
	protected void sequence_Parameters(EObject context, Parameters semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (exprs=ExpressionList?)
	 */
	protected void sequence_Primary(EObject context, Array semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (params=ParameterList?)
	 */
	protected void sequence_Primary(EObject context, Parameters semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=Expression op=',' right=Expression z=Expression?)
	 */
	protected void sequence_Primary(EObject context, Point semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=_1Expr_Facets_BlockOrEnd_Key expr=Expression facets+=Facet* block=Block?)
	 */
	protected void sequence_S_1Expr_Facets_BlockOrEnd(EObject context, Statement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key='action' name=Valid_ID args=ActionArguments? facets+=Facet* block=Block?)
	 */
	protected void sequence_S_Action(EObject context, S_Action semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (tkey=TypeRef (name=Valid_ID | name=STRING) args=ActionArguments? facets+=Facet* block=Block?)
	 */
	protected void sequence_S_Definition(EObject context, S_Definition semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (
	 *         expr=Expression 
	 *         (
	 *             key='<-' | 
	 *             key='<<' | 
	 *             key='>>' | 
	 *             key='+=' | 
	 *             key='-=' | 
	 *             key='++' | 
	 *             key='--'
	 *         ) 
	 *         value=Expression 
	 *         facets+=Facet*
	 *     )
	 */
	protected void sequence_S_DirectAssignment(EObject context, S_DirectAssignment semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=_DoKey expr=ActionRef params=Parameters? facets+=Facet* block=Block?)
	 */
	protected void sequence_S_Do(EObject context, S_Do semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (expr=Function key='=' value=Expression)
	 */
	protected void sequence_S_Equation(EObject context, S_Equation semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key='experiment' (name=Valid_ID | name=STRING) facets+=Facet* block=Block)
	 */
	protected void sequence_S_Experiment(EObject context, S_Experiment semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key='if' expr=Expression block=Block (else=S_If | else=Block)?)
	 */
	protected void sequence_S_If(EObject context, S_If semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key='loop' name=ID? facets+=Facet* block=Block)
	 */
	protected void sequence_S_Loop(EObject context, S_Loop semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=ID facets+=Facet* block=Block?)
	 */
	protected void sequence_S_Other(EObject context, S_Other semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=_ReflexKey name=Valid_ID? expr=Expression? block=Block)
	 */
	protected void sequence_S_Reflex(EObject context, S_Reflex semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key='return' expr=Expression?)
	 */
	protected void sequence_S_Return(EObject context, S_Return semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key='set' expr=Expression value=Expression)
	 */
	protected void sequence_S_Set(EObject context, S_Set semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=_SpeciesKey name=ID facets+=Facet* block=Block?)
	 */
	protected void sequence_S_Species(EObject context, S_Species semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=_VarOrConstKey name=Valid_ID facets+=Facet*)
	 */
	protected void sequence_S_Var(EObject context, S_Var semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     name=ID
	 */
	protected void sequence_SkillFakeDefinition(EObject context, SkillFakeDefinition semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_DEFINITION__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_DEFINITION__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getSkillFakeDefinitionAccess().getNameIDTerminalRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     ref=[SkillFakeDefinition|ID]
	 */
	protected void sequence_SkillRef(EObject context, SkillRef semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     op=BOOLEAN
	 */
	protected void sequence_TerminalExpression(EObject context, BooleanLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     op=COLOR
	 */
	protected void sequence_TerminalExpression(EObject context, ColorLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     op=DOUBLE
	 */
	protected void sequence_TerminalExpression(EObject context, DoubleLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     op=INTEGER
	 */
	protected void sequence_TerminalExpression(EObject context, IntLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     op=KEYWORD
	 */
	protected void sequence_TerminalExpression(EObject context, ReservedLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     op=STRING
	 */
	protected void sequence_TerminalExpression(EObject context, StringLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=TypeFacetKey expr=TypeRef)
	 */
	protected void sequence_TypeFacet(EObject context, Facet semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     name=ID
	 */
	protected void sequence_TypeFakeDefinition(EObject context, TypeFakeDefinition semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_DEFINITION__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_DEFINITION__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getTypeFakeDefinitionAccess().getNameIDTerminalRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (ref=[TypeDefinition|ID] of=TypeRef?)
	 */
	protected void sequence_TypeRef(EObject context, TypeRef semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     ((op='¡' right=UnitRef) | ((op='-' | op='!' | op='my' | op='the' | op='not') right=Unary))
	 */
	protected void sequence_Unary(EObject context, Unary semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     name=ID
	 */
	protected void sequence_UnitFakeDefinition(EObject context, UnitFakeDefinition semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_DEFINITION__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_DEFINITION__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getUnitFakeDefinitionAccess().getNameIDTerminalRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     ref=[UnitFakeDefinition|ID]
	 */
	protected void sequence_UnitRef(EObject context, UnitName semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=Unit_Unit_1_0_0 op='¡' right=UnitRef)
	 */
	protected void sequence_Unit(EObject context, Unit semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__OP) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__OP));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getUnitAccess().getUnitLeftAction_1_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getUnitAccess().getOpDegreeSignKeyword_1_0_1_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getUnitAccess().getRightUnitRefParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     name=Valid_ID
	 */
	protected void sequence_VarFakeDefinition(EObject context, VarFakeDefinition semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_DEFINITION__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_DEFINITION__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getVarFakeDefinitionAccess().getNameValid_IDParserRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     ref=[VarDefinition|Valid_ID]
	 */
	protected void sequence_VariableRef(EObject context, VariableRef semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
}
