package msi.gama.lang.gaml.serializer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import msi.gama.lang.gaml.gaml.ActionFacetExpr;
import msi.gama.lang.gaml.gaml.ArgPairExpr;
import msi.gama.lang.gaml.gaml.Array;
import msi.gama.lang.gaml.gaml.Block;
import msi.gama.lang.gaml.gaml.BooleanLiteral;
import msi.gama.lang.gaml.gaml.ColorLiteral;
import msi.gama.lang.gaml.gaml.DefBinaryOp;
import msi.gama.lang.gaml.gaml.DefReserved;
import msi.gama.lang.gaml.gaml.DefUnary;
import msi.gama.lang.gaml.gaml.Definition;
import msi.gama.lang.gaml.gaml.DoubleLiteral;
import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.lang.gaml.gaml.FacetExpr;
import msi.gama.lang.gaml.gaml.Function;
import msi.gama.lang.gaml.gaml.FunctionFacetExpr;
import msi.gama.lang.gaml.gaml.FunctionGamlFacetRef;
import msi.gama.lang.gaml.gaml.GamlBinaryExpr;
import msi.gama.lang.gaml.gaml.GamlFacetRef;
import msi.gama.lang.gaml.gaml.GamlLangDef;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.GamlUnaryExpr;
import msi.gama.lang.gaml.gaml.GamlUnitExpr;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.IntLiteral;
import msi.gama.lang.gaml.gaml.MemberRef;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.NameFacetExpr;
import msi.gama.lang.gaml.gaml.PairExpr;
import msi.gama.lang.gaml.gaml.Point;
import msi.gama.lang.gaml.gaml.ReturnsFacetExpr;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.gaml.TernExp;
import msi.gama.lang.gaml.gaml.UnitName;
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
			case GamlPackage.ACTION_FACET_EXPR:
				if(context == grammarAccess.getActionFacetExprRule() ||
				   context == grammarAccess.getDefinitionFacetExprRule() ||
				   context == grammarAccess.getFacetExprRule() ||
				   context == grammarAccess.getGamlVarRefRule()) {
					sequence_ActionFacetExpr(context, (ActionFacetExpr) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ARG_PAIR_EXPR:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getArgPairExprRule() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_ArgPairExpr(context, (ArgPairExpr) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ARRAY:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_PrimaryExpression(context, (Array) semanticObject); 
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
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_TerminalExpression(context, (BooleanLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.COLOR_LITERAL:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_TerminalExpression(context, (ColorLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DEF_BINARY_OP:
				if(context == grammarAccess.getDefBinaryOpRule()) {
					sequence_DefBinaryOp(context, (DefBinaryOp) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DEF_RESERVED:
				if(context == grammarAccess.getDefReservedRule() ||
				   context == grammarAccess.getGamlVarRefRule()) {
					sequence_DefReserved(context, (DefReserved) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DEF_UNARY:
				if(context == grammarAccess.getDefUnaryRule()) {
					sequence_DefUnary(context, (DefUnary) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DEFINITION:
				if(context == grammarAccess.getDefinitionRule() ||
				   context == grammarAccess.getGamlVarRefRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_Definition(context, (Definition) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DOUBLE_LITERAL:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_TerminalExpression(context, (DoubleLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.EXPRESSION:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_Addition(context, (Expression) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.FACET_EXPR:
				if(context == grammarAccess.getFacetExprRule()) {
					sequence_FacetExpr(context, (FacetExpr) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.FUNCTION:
				if(context == grammarAccess.getAbstractRefRule() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getFunctionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_Function(context, (Function) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.FUNCTION_FACET_EXPR:
				if(context == grammarAccess.getFacetExprRule() ||
				   context == grammarAccess.getFunctionFacetExprRule()) {
					sequence_FunctionFacetExpr(context, (FunctionFacetExpr) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.FUNCTION_GAML_FACET_REF:
				if(context == grammarAccess.getFacetRefRule() ||
				   context == grammarAccess.getFunctionGamlFacetRefRule()) {
					sequence_FunctionGamlFacetRef(context, (FunctionGamlFacetRef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_BINARY_EXPR:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_GamlBinaryExpr(context, (GamlBinaryExpr) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_FACET_REF:
				if(context == grammarAccess.getFacetRefRule() ||
				   context == grammarAccess.getGamlFacetRefRule()) {
					sequence_GamlFacetRef(context, (GamlFacetRef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_LANG_DEF:
				if(context == grammarAccess.getGamlLangDefRule()) {
					sequence_GamlLangDef(context, (GamlLangDef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_UNARY_EXPR:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_GamlUnaryExpr(context, (GamlUnaryExpr) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getGamlUnaryExprAccess().getGamlUnaryExprLeftAction_1_1_0()) {
					sequence_GamlUnaryExpr_GamlUnaryExpr_1_1_0(context, (GamlUnaryExpr) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_UNIT_EXPR:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_GamlUnitExpr(context, (GamlUnitExpr) semanticObject); 
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
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_TerminalExpression(context, (IntLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.MEMBER_REF:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_MemberRef(context, (MemberRef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.MODEL:
				if(context == grammarAccess.getModelRule()) {
					sequence_Model(context, (Model) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.NAME_FACET_EXPR:
				if(context == grammarAccess.getDefinitionFacetExprRule() ||
				   context == grammarAccess.getFacetExprRule() ||
				   context == grammarAccess.getGamlVarRefRule() ||
				   context == grammarAccess.getNameFacetExprRule()) {
					sequence_NameFacetExpr(context, (NameFacetExpr) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.PAIR_EXPR:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_PairExpr(context, (PairExpr) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.POINT:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_PrimaryExpression(context, (Point) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.RETURNS_FACET_EXPR:
				if(context == grammarAccess.getDefinitionFacetExprRule() ||
				   context == grammarAccess.getFacetExprRule() ||
				   context == grammarAccess.getGamlVarRefRule() ||
				   context == grammarAccess.getReturnsFacetExprRule()) {
					sequence_ReturnsFacetExpr(context, (ReturnsFacetExpr) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.STATEMENT:
				if(context == grammarAccess.getClassicStatementRule()) {
					sequence_ClassicStatement(context, (Statement) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getIfEvalRule()) {
					sequence_IfEval(context, (Statement) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getStatementRule()) {
					sequence_Statement(context, (Statement) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.STRING_LITERAL:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_TerminalExpression(context, (StringLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.TERN_EXP:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_TernExp(context, (TernExp) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.UNIT_NAME:
				if(context == grammarAccess.getUnitNameRule()) {
					sequence_UnitName(context, (UnitName) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.VARIABLE_REF:
				if(context == grammarAccess.getAbstractRefRule() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinaryExprRule() ||
				   context == grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0() ||
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
	 *     (name=ID | name=STRING | name=BuiltIn)
	 */
	protected void sequence_ActionFacetExpr(EObject context, ActionFacetExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (
	 *         (left=Addition_Expression_1_0_0 (op='+' | op='-') right=Multiplication) | 
	 *         (left=Multiplication_Expression_1_0_0 (op='*' | op='/' | op='^') right=GamlBinaryExpr) | 
	 *         (
	 *             left=Relational_Expression_1_0_0 
	 *             (
	 *                 op='!=' | 
	 *                 op='=' | 
	 *                 op='>=' | 
	 *                 op='<=' | 
	 *                 op='<' | 
	 *                 op='>'
	 *             ) 
	 *             right=PairExpr
	 *         ) | 
	 *         (left=AndExp_Expression_1_0 op='and' right=Relational) | 
	 *         (left=OrExp_Expression_1_0 op='or' right=AndExp)
	 *     )
	 */
	protected void sequence_Addition(EObject context, Expression semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (arg=ID op='::' right=Addition)
	 */
	protected void sequence_ArgPairExpr(EObject context, ArgPairExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (statements+=Statement*)
	 */
	protected void sequence_Block(EObject context, Block semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=BuiltIn ref=GamlFacetRef? expr=Expression facets+=FacetExpr* block=Block?)
	 */
	protected void sequence_ClassicStatement(EObject context, Statement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     name=ID
	 */
	protected void sequence_DefBinaryOp(EObject context, DefBinaryOp semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.DEF_BINARY_OP__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.DEF_BINARY_OP__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getDefBinaryOpAccess().getNameIDTerminalRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     name=ID
	 */
	protected void sequence_DefReserved(EObject context, DefReserved semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_VAR_REF__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_VAR_REF__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getDefReservedAccess().getNameIDTerminalRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     name=ID
	 */
	protected void sequence_DefUnary(EObject context, DefUnary semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.DEF_UNARY__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.DEF_UNARY__NAME));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getDefUnaryAccess().getNameIDTerminalRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (key=ID (name=ID | name=STRING | name=BuiltIn)? facets+=FacetExpr* block=Block?)
	 */
	protected void sequence_Definition(EObject context, Definition semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=GamlFacetRef expr=Expression)
	 */
	protected void sequence_FacetExpr(EObject context, FacetExpr semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.FACET_EXPR__KEY) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.FACET_EXPR__KEY));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.FACET_EXPR__EXPR) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.FACET_EXPR__EXPR));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getFacetExprAccess().getKeyGamlFacetRefParserRuleCall_2_0_0(), semanticObject.getKey());
		feeder.accept(grammarAccess.getFacetExprAccess().getExprExpressionParserRuleCall_2_1_0(), semanticObject.getExpr());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (key=FunctionGamlFacetRef expr=Expression)
	 */
	protected void sequence_FunctionFacetExpr(EObject context, FunctionFacetExpr semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.FACET_EXPR__KEY) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.FACET_EXPR__KEY));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.FACET_EXPR__EXPR) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.FACET_EXPR__EXPR));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getFunctionFacetExprAccess().getKeyFunctionGamlFacetRefParserRuleCall_0_0(), semanticObject.getKey());
		feeder.accept(grammarAccess.getFunctionFacetExprAccess().getExprExpressionParserRuleCall_2_0(), semanticObject.getExpr());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (ref='function' | ref='->')
	 */
	protected void sequence_FunctionGamlFacetRef(EObject context, FunctionGamlFacetRef semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (op=ID args+=Expression args+=Expression*)
	 */
	protected void sequence_Function(EObject context, Function semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=GamlBinaryExpr_GamlBinaryExpr_1_0_0 op=ID right=GamlUnitExpr)
	 */
	protected void sequence_GamlBinaryExpr(EObject context, GamlBinaryExpr semanticObject) {
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
		feeder.accept(grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getGamlBinaryExprAccess().getOpIDTerminalRuleCall_1_0_1_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getGamlBinaryExprAccess().getRightGamlUnitExprParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (ref=ID | ref='<-')
	 */
	protected void sequence_GamlFacetRef(EObject context, GamlFacetRef semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (b+=DefBinaryOp | r+=DefReserved | unaries+=DefUnary)+
	 */
	protected void sequence_GamlLangDef(EObject context, GamlLangDef semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=GamlUnaryExpr_GamlUnaryExpr_1_1_0 (op='-' | op='!' | op='my' | op='the' | op='not') right=GamlUnaryExpr)
	 */
	protected void sequence_GamlUnaryExpr(EObject context, GamlUnaryExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     {GamlUnaryExpr}
	 */
	protected void sequence_GamlUnaryExpr_GamlUnaryExpr_1_1_0(EObject context, GamlUnaryExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=GamlUnitExpr_GamlUnitExpr_1_0_0 op='#' right=UnitName)
	 */
	protected void sequence_GamlUnitExpr(EObject context, GamlUnitExpr semanticObject) {
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
		feeder.accept(grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getGamlUnitExprAccess().getOpNumberSignKeyword_1_0_1_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getGamlUnitExprAccess().getRightUnitNameParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (key='if' ref=GamlFacetRef? expr=Expression block=Block (else=Statement | else=Block)?)
	 */
	protected void sequence_IfEval(EObject context, Statement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
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
	 *     (left=MemberRef_MemberRef_1_0 op='.' right=VariableRef)
	 */
	protected void sequence_MemberRef(EObject context, MemberRef semanticObject) {
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
		feeder.accept(grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getMemberRefAccess().getOpFullStopKeyword_1_1_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getMemberRefAccess().getRightVariableRefParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID imports+=Import* gaml=GamlLangDef? statements+=Statement*)
	 */
	protected void sequence_Model(EObject context, Model semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID | name=STRING | name=BuiltIn)
	 */
	protected void sequence_NameFacetExpr(EObject context, NameFacetExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=PairExpr_PairExpr_1_1_0_0 op='::' right=Addition)
	 */
	protected void sequence_PairExpr(EObject context, PairExpr semanticObject) {
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
		feeder.accept(grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getPairExprAccess().getOpColonColonKeyword_1_1_0_1_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getPairExprAccess().getRightAdditionParserRuleCall_1_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     ((exprs+=Expression exprs+=Expression*)?)
	 */
	protected void sequence_PrimaryExpression(EObject context, Array semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=Expression op=',' right=Expression)
	 */
	protected void sequence_PrimaryExpression(EObject context, Point semanticObject) {
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
		feeder.accept(grammarAccess.getPrimaryExpressionAccess().getLeftExpressionParserRuleCall_3_2_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getPrimaryExpressionAccess().getOpCommaKeyword_3_2_1_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getPrimaryExpressionAccess().getRightExpressionParserRuleCall_3_2_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     name=ID
	 */
	protected void sequence_ReturnsFacetExpr(EObject context, ReturnsFacetExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (
	 *         (key='if' ref=GamlFacetRef? expr=Expression block=Block (else=Statement | else=Block)?) | 
	 *         (key=BuiltIn ref=GamlFacetRef? expr=Expression facets+=FacetExpr* block=Block?)
	 *     )
	 */
	protected void sequence_Statement(EObject context, Statement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     value=BOOLEAN
	 */
	protected void sequence_TerminalExpression(EObject context, BooleanLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     value=COLOR
	 */
	protected void sequence_TerminalExpression(EObject context, ColorLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     value=DOUBLE
	 */
	protected void sequence_TerminalExpression(EObject context, DoubleLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     value=INTEGER
	 */
	protected void sequence_TerminalExpression(EObject context, IntLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     value=STRING
	 */
	protected void sequence_TerminalExpression(EObject context, StringLiteral semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=TernExp_TernExp_1_0 op='?' right=OrExp ifFalse=OrExp)
	 */
	protected void sequence_TernExp(EObject context, TernExp semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__OP) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__OP));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.EXPRESSION__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.EXPRESSION__RIGHT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.TERN_EXP__IF_FALSE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.TERN_EXP__IF_FALSE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getTernExpAccess().getOpQuestionMarkKeyword_1_1_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getTernExpAccess().getRightOrExpParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.accept(grammarAccess.getTernExpAccess().getIfFalseOrExpParserRuleCall_1_4_0(), semanticObject.getIfFalse());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     op=ID
	 */
	protected void sequence_UnitName(EObject context, UnitName semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     ref=[GamlVarRef|ID]
	 */
	protected void sequence_VariableRef(EObject context, VariableRef semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
}
