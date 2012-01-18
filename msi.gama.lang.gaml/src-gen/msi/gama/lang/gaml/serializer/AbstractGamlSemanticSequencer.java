package msi.gama.lang.gaml.serializer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import msi.gama.lang.gaml.gaml.And;
import msi.gama.lang.gaml.gaml.ArrayRef;
import msi.gama.lang.gaml.gaml.AssignDiv;
import msi.gama.lang.gaml.gaml.AssignMin;
import msi.gama.lang.gaml.gaml.AssignMult;
import msi.gama.lang.gaml.gaml.AssignPlus;
import msi.gama.lang.gaml.gaml.Block;
import msi.gama.lang.gaml.gaml.BooleanLiteral;
import msi.gama.lang.gaml.gaml.ColorLiteral;
import msi.gama.lang.gaml.gaml.DefBinaryOp;
import msi.gama.lang.gaml.gaml.DefFacet;
import msi.gama.lang.gaml.gaml.DefKeyword;
import msi.gama.lang.gaml.gaml.DefReserved;
import msi.gama.lang.gaml.gaml.DefUnit;
import msi.gama.lang.gaml.gaml.Definition;
import msi.gama.lang.gaml.gaml.Div;
import msi.gama.lang.gaml.gaml.DoubleLiteral;
import msi.gama.lang.gaml.gaml.Evaluation;
import msi.gama.lang.gaml.gaml.FacetExpr;
import msi.gama.lang.gaml.gaml.FunctionRef;
import msi.gama.lang.gaml.gaml.GamlBinarOpRef;
import msi.gama.lang.gaml.gaml.GamlBinary;
import msi.gama.lang.gaml.gaml.GamlBlock;
import msi.gama.lang.gaml.gaml.GamlFacetRef;
import msi.gama.lang.gaml.gaml.GamlKeywordRef;
import msi.gama.lang.gaml.gaml.GamlLangDef;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.GamlReservedRef;
import msi.gama.lang.gaml.gaml.GamlUnary;
import msi.gama.lang.gaml.gaml.GamlUnitRef;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.IntLiteral;
import msi.gama.lang.gaml.gaml.Matrix;
import msi.gama.lang.gaml.gaml.MemberRefP;
import msi.gama.lang.gaml.gaml.MemberRefR;
import msi.gama.lang.gaml.gaml.Minus;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.Multi;
import msi.gama.lang.gaml.gaml.Or;
import msi.gama.lang.gaml.gaml.Pair;
import msi.gama.lang.gaml.gaml.Plus;
import msi.gama.lang.gaml.gaml.Point;
import msi.gama.lang.gaml.gaml.Pow;
import msi.gama.lang.gaml.gaml.RelEq;
import msi.gama.lang.gaml.gaml.RelEqEq;
import msi.gama.lang.gaml.gaml.RelGt;
import msi.gama.lang.gaml.gaml.RelGtEq;
import msi.gama.lang.gaml.gaml.RelLt;
import msi.gama.lang.gaml.gaml.RelLtEq;
import msi.gama.lang.gaml.gaml.RelNotEq;
import msi.gama.lang.gaml.gaml.Row;
import msi.gama.lang.gaml.gaml.SetEval;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.gaml.Ternary;
import msi.gama.lang.gaml.gaml.Unit;
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
			case GamlPackage.AND:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_AndExp(context, (And) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ARRAY_REF:
				if(context == grammarAccess.getAbrstractRefRule() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getRightMemberRefRule() ||
				   context == grammarAccess.getRightMemberRefAccess().getMemberRefRLeftAction_1_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_AbrstractRef(context, (ArrayRef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ASSIGN_DIV:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_AssignmentOp(context, (AssignDiv) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ASSIGN_MIN:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_AssignmentOp(context, (AssignMin) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ASSIGN_MULT:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_AssignmentOp(context, (AssignMult) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ASSIGN_PLUS:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_AssignmentOp(context, (AssignPlus) semanticObject); 
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
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_TerminalExpression(context, (BooleanLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.COLOR_LITERAL:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
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
			case GamlPackage.DEF_FACET:
				if(context == grammarAccess.getDefFacetRule()) {
					sequence_DefFacet(context, (DefFacet) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DEF_KEYWORD:
				if(context == grammarAccess.getDefKeywordRule()) {
					sequence_DefKeyword(context, (DefKeyword) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DEF_RESERVED:
				if(context == grammarAccess.getAbstractDefinitionRule() ||
				   context == grammarAccess.getDefReservedRule()) {
					sequence_DefReserved(context, (DefReserved) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DEF_UNIT:
				if(context == grammarAccess.getDefUnitRule()) {
					sequence_DefUnit(context, (DefUnit) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DEFINITION:
				if(context == grammarAccess.getAbstractDefinitionRule() ||
				   context == grammarAccess.getDefinitionRule() ||
				   context == grammarAccess.getStatementRule() ||
				   context == grammarAccess.getSubStatementRule()) {
					sequence_Definition(context, (Definition) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DIV:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Multiplication(context, (Div) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DOUBLE_LITERAL:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_TerminalExpression(context, (DoubleLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.EVALUATION:
				if(context == grammarAccess.getEvaluationRule() ||
				   context == grammarAccess.getStatementRule() ||
				   context == grammarAccess.getSubStatementRule()) {
					sequence_Evaluation(context, (Evaluation) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.FACET_EXPR:
				if(context == grammarAccess.getAbstractDefinitionRule() ||
				   context == grammarAccess.getFacetExprRule()) {
					sequence_FacetExpr(context, (FacetExpr) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.FUNCTION_REF:
				if(context == grammarAccess.getAbrstractRefRule() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getRightMemberRefRule() ||
				   context == grammarAccess.getRightMemberRefAccess().getMemberRefRLeftAction_1_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_AbrstractRef(context, (FunctionRef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_BINAR_OP_REF:
				if(context == grammarAccess.getAbstractGamlRefRule() ||
				   context == grammarAccess.getGamlBinarOpRefRule()) {
					sequence_GamlBinarOpRef(context, (GamlBinarOpRef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_BINARY:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_GamlBinExpr(context, (GamlBinary) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_BLOCK:
				if(context == grammarAccess.getGamlBlockRule()) {
					sequence_GamlBlock(context, (GamlBlock) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_FACET_REF:
				if(context == grammarAccess.getAbstractGamlRefRule() ||
				   context == grammarAccess.getGamlFacetRefRule()) {
					sequence_GamlFacetRef(context, (GamlFacetRef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_KEYWORD_REF:
				if(context == grammarAccess.getAbstractGamlRefRule() ||
				   context == grammarAccess.getGamlKeywordRefRule()) {
					sequence_GamlKeywordRef(context, (GamlKeywordRef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_LANG_DEF:
				if(context == grammarAccess.getGamlLangDefRule()) {
					sequence_GamlLangDef(context, (GamlLangDef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_RESERVED_REF:
				if(context == grammarAccess.getAbstractGamlRefRule() ||
				   context == grammarAccess.getGamlReservedRefRule()) {
					sequence_GamlReservedRef(context, (GamlReservedRef) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_UNARY:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_GamlUnaryExpr(context, (GamlUnary) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_UNIT_REF:
				if(context == grammarAccess.getAbstractGamlRefRule() ||
				   context == grammarAccess.getGamlUnitRefRule()) {
					sequence_GamlUnitRef(context, (GamlUnitRef) semanticObject); 
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
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_TerminalExpression(context, (IntLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.MATRIX:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMatrixRule() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Matrix(context, (Matrix) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.MEMBER_REF_P:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_MemberRef(context, (MemberRefP) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.MEMBER_REF_R:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getRightMemberRefRule() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_RightMemberRef(context, (MemberRefR) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.MINUS:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Addition(context, (Minus) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.MODEL:
				if(context == grammarAccess.getModelRule()) {
					sequence_Model(context, (Model) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.MULTI:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Multiplication(context, (Multi) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.OR:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_OrExp(context, (Or) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.PAIR:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_PairExpr(context, (Pair) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.PLUS:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Addition(context, (Plus) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.POINT:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPointRule() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Point(context, (Point) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.POW:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Multiplication(context, (Pow) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.REL_EQ:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Relational(context, (RelEq) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.REL_EQ_EQ:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Relational(context, (RelEqEq) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.REL_GT:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Relational(context, (RelGt) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.REL_GT_EQ:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Relational(context, (RelGtEq) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.REL_LT:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Relational(context, (RelLt) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.REL_LT_EQ:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Relational(context, (RelLtEq) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.REL_NOT_EQ:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_Relational(context, (RelNotEq) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ROW:
				if(context == grammarAccess.getRowRule()) {
					sequence_Row(context, (Row) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.SET_EVAL:
				if(context == grammarAccess.getSetEvalRule() ||
				   context == grammarAccess.getStatementRule()) {
					sequence_SetEval(context, (SetEval) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.STRING_LITERAL:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTerminalExpressionRule() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_TerminalExpression(context, (StringLiteral) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.TERNARY:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_TernExp(context, (Ternary) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.UNIT:
				if(context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0()) {
					sequence_GamlUnitExpr(context, (Unit) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.VARIABLE_REF:
				if(context == grammarAccess.getAbrstractRefRule() ||
				   context == grammarAccess.getAbrstractRefAccess().getArrayRefArrayAction_1_1_0() ||
				   context == grammarAccess.getAbrstractRefAccess().getFunctionRefFuncAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
				   context == grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getAndExpRule() ||
				   context == grammarAccess.getAndExpAccess().getAndLeftAction_1_0() ||
				   context == grammarAccess.getAssignmentOpRule() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0() ||
				   context == grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0() ||
				   context == grammarAccess.getExpressionRule() ||
				   context == grammarAccess.getGamlBinExprRule() ||
				   context == grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0() ||
				   context == grammarAccess.getGamlUnaryExprRule() ||
				   context == grammarAccess.getGamlUnitExprRule() ||
				   context == grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0() ||
				   context == grammarAccess.getMemberRefRule() ||
				   context == grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0() ||
				   context == grammarAccess.getMultiplicationRule() ||
				   context == grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0() ||
				   context == grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0() ||
				   context == grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0() ||
				   context == grammarAccess.getOrExpRule() ||
				   context == grammarAccess.getOrExpAccess().getOrLeftAction_1_0() ||
				   context == grammarAccess.getPairExprRule() ||
				   context == grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0() ||
				   context == grammarAccess.getPrePrimaryExprRule() ||
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0() ||
				   context == grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0() ||
				   context == grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0() ||
				   context == grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0() ||
				   context == grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0() ||
				   context == grammarAccess.getRightMemberRefRule() ||
				   context == grammarAccess.getRightMemberRefAccess().getMemberRefRLeftAction_1_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0() ||
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
	 *     ((array=AbrstractRef_ArrayRef_1_1_0 (args+=Expression args+=Expression*)?) | array=AbrstractRef_ArrayRef_1_1_0)
	 *
	 * Features:
	 *    array[0, 2]
	 *    args[0, *]
	 *         EXCLUDE_IF_UNSET array
	 *         EXCLUDE_IF_SET array
	 */
	protected void sequence_AbrstractRef(EObject context, ArrayRef semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     ((func=AbrstractRef_FunctionRef_1_0_0 (args+=Expression args+=Expression*)?) | func=AbrstractRef_FunctionRef_1_0_0)
	 *
	 * Features:
	 *    func[0, 2]
	 *    args[0, *]
	 *         EXCLUDE_IF_UNSET func
	 *         EXCLUDE_IF_SET func
	 */
	protected void sequence_AbrstractRef(EObject context, FunctionRef semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=Addition_Minus_1_0_1_0 right=Multiplication)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_Addition(EObject context, Minus semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.MINUS__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.MINUS__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.MINUS__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.MINUS__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getAdditionAccess().getMinusLeftAction_1_0_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getAdditionAccess().getRightMultiplicationParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=Addition_Plus_1_0_0_0 right=Multiplication)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_Addition(EObject context, Plus semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.PLUS__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.PLUS__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.PLUS__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.PLUS__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getAdditionAccess().getPlusLeftAction_1_0_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getAdditionAccess().getRightMultiplicationParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=AndExp_And_1_0 right=Relational)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_AndExp(EObject context, And semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.AND__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.AND__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.AND__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.AND__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getAndExpAccess().getAndLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getAndExpAccess().getRightRelationalParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=AssignmentOp_AssignDiv_1_0_3_0 right=TernExp)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_AssignmentOp(EObject context, AssignDiv semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.ASSIGN_DIV__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.ASSIGN_DIV__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.ASSIGN_DIV__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.ASSIGN_DIV__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getAssignmentOpAccess().getAssignDivLeftAction_1_0_3_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getAssignmentOpAccess().getRightTernExpParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=AssignmentOp_AssignMin_1_0_1_0 right=TernExp)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_AssignmentOp(EObject context, AssignMin semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.ASSIGN_MIN__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.ASSIGN_MIN__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.ASSIGN_MIN__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.ASSIGN_MIN__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getAssignmentOpAccess().getAssignMinLeftAction_1_0_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getAssignmentOpAccess().getRightTernExpParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=AssignmentOp_AssignMult_1_0_2_0 right=TernExp)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_AssignmentOp(EObject context, AssignMult semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.ASSIGN_MULT__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.ASSIGN_MULT__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.ASSIGN_MULT__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.ASSIGN_MULT__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getAssignmentOpAccess().getAssignMultLeftAction_1_0_2_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getAssignmentOpAccess().getRightTernExpParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=AssignmentOp_AssignPlus_1_0_0_0 right=TernExp)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_AssignmentOp(EObject context, AssignPlus semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.ASSIGN_PLUS__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.ASSIGN_PLUS__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.ASSIGN_PLUS__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.ASSIGN_PLUS__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getAssignmentOpAccess().getAssignPlusLeftAction_1_0_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getAssignmentOpAccess().getRightTernExpParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (statements+=Statement*)
	 *
	 * Features:
	 *    statements[0, *]
	 */
	protected void sequence_Block(EObject context, Block semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     name=ID
	 *
	 * Features:
	 *    name[1, 1]
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
	 *     (name=ID type=[DefReserved|ID]? default=TerminalExpression?)
	 *
	 * Features:
	 *    name[1, 1]
	 *    type[0, 1]
	 *    default[0, 1]
	 */
	protected void sequence_DefFacet(EObject context, DefFacet semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID block=GamlBlock?)
	 *
	 * Features:
	 *    name[1, 1]
	 *    block[0, 1]
	 */
	protected void sequence_DefKeyword(EObject context, DefKeyword semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID type=[DefReserved|ID]? value=TerminalExpression?)
	 *
	 * Features:
	 *    name[1, 1]
	 *    type[0, 1]
	 *    value[0, 1]
	 */
	protected void sequence_DefReserved(EObject context, DefReserved semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID coef=DOUBLE?)
	 *
	 * Features:
	 *    name[1, 1]
	 *    coef[0, 1]
	 */
	protected void sequence_DefUnit(EObject context, DefUnit semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=GamlKeywordRef name=ID facets+=FacetExpr* block=Block?)
	 *
	 * Features:
	 *    facets[0, *]
	 *    block[0, 1]
	 *    key[1, 1]
	 *    name[1, 1]
	 */
	protected void sequence_Definition(EObject context, Definition semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=GamlKeywordRef var=Expression? facets+=FacetExpr* block=Block?)
	 *
	 * Features:
	 *    facets[0, *]
	 *    block[0, 1]
	 *    key[1, 1]
	 *    var[0, 1]
	 */
	protected void sequence_Evaluation(EObject context, Evaluation semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (name=ID | (key=GamlFacetRef expr=Expression))
	 *
	 * Features:
	 *    name[0, 1]
	 *         EXCLUDE_IF_SET key
	 *         EXCLUDE_IF_SET expr
	 *    key[0, 1]
	 *         EXCLUDE_IF_UNSET expr
	 *         MANDATORY_IF_SET expr
	 *         EXCLUDE_IF_SET name
	 *    expr[0, 1]
	 *         EXCLUDE_IF_UNSET key
	 *         MANDATORY_IF_SET key
	 *         EXCLUDE_IF_SET name
	 */
	protected void sequence_FacetExpr(EObject context, FacetExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=GamlBinExpr_GamlBinary_1_0_0 op=GamlBinarOpRef right=GamlUnitExpr)
	 *
	 * Features:
	 *    left[1, 1]
	 *    op[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_GamlBinExpr(EObject context, GamlBinary semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_BINARY__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_BINARY__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_BINARY__OP) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_BINARY__OP));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_BINARY__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_BINARY__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getGamlBinExprAccess().getGamlBinaryLeftAction_1_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getGamlBinExprAccess().getOpGamlBinarOpRefParserRuleCall_1_0_1_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getGamlBinExprAccess().getRightGamlUnitExprParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     ref=[DefBinaryOp|ID]
	 *
	 * Features:
	 *    ref[1, 1]
	 */
	protected void sequence_GamlBinarOpRef(EObject context, GamlBinarOpRef semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_BINAR_OP_REF__REF) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_BINAR_OP_REF__REF));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getGamlBinarOpRefAccess().getRefDefBinaryOpIDTerminalRuleCall_0_1(), semanticObject.getRef());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     ((facets+=[DefFacet|ID] facets+=[DefFacet|ID]*)? (childs+=[DefKeyword|ID] childs+=[DefKeyword|ID]*)?)
	 *
	 * Features:
	 *    facets[0, *]
	 *    childs[0, *]
	 */
	protected void sequence_GamlBlock(EObject context, GamlBlock semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     ref=[DefFacet|ID]
	 *
	 * Features:
	 *    ref[1, 1]
	 */
	protected void sequence_GamlFacetRef(EObject context, GamlFacetRef semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_FACET_REF__REF) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_FACET_REF__REF));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getGamlFacetRefAccess().getRefDefFacetIDTerminalRuleCall_0_0_1(), semanticObject.getRef());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     ref=[DefKeyword|ID]
	 *
	 * Features:
	 *    ref[1, 1]
	 */
	protected void sequence_GamlKeywordRef(EObject context, GamlKeywordRef semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_KEYWORD_REF__REF) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_KEYWORD_REF__REF));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getGamlKeywordRefAccess().getRefDefKeywordIDTerminalRuleCall_0_1(), semanticObject.getRef());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (k+=DefKeyword | f+=DefFacet | b+=DefBinaryOp | r+=DefReserved | u+=DefUnit)+
	 *
	 * Features:
	 *    k[0, *]
	 *    f[0, *]
	 *    b[0, *]
	 *    r[0, *]
	 *    u[0, *]
	 */
	protected void sequence_GamlLangDef(EObject context, GamlLangDef semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     ref=[DefReserved|ID]
	 *
	 * Features:
	 *    ref[1, 1]
	 */
	protected void sequence_GamlReservedRef(EObject context, GamlReservedRef semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_RESERVED_REF__REF) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_RESERVED_REF__REF));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getGamlReservedRefAccess().getRefDefReservedIDTerminalRuleCall_0_1(), semanticObject.getRef());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (op=UnarOp right=GamlUnaryExpr)
	 *
	 * Features:
	 *    op[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_GamlUnaryExpr(EObject context, GamlUnary semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_UNARY__OP) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_UNARY__OP));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_UNARY__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_UNARY__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getGamlUnaryExprAccess().getOpUnarOpParserRuleCall_1_1_0_0(), semanticObject.getOp());
		feeder.accept(grammarAccess.getGamlUnaryExprAccess().getRightGamlUnaryExprParserRuleCall_1_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=GamlUnitExpr_Unit_1_0_0 right=GamlUnitRef)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_GamlUnitExpr(EObject context, Unit semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.UNIT__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.UNIT__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.UNIT__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.UNIT__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getGamlUnitExprAccess().getUnitLeftAction_1_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getGamlUnitExprAccess().getRightGamlUnitRefParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     ref=[DefUnit|ID]
	 *
	 * Features:
	 *    ref[1, 1]
	 */
	protected void sequence_GamlUnitRef(EObject context, GamlUnitRef semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.GAML_UNIT_REF__REF) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.GAML_UNIT_REF__REF));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getGamlUnitRefAccess().getRefDefUnitIDTerminalRuleCall_0_1(), semanticObject.getRef());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     importURI=STRING
	 *
	 * Features:
	 *    importURI[1, 1]
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
	 *     ((rows+=Row rows+=Row*)?)
	 *
	 * Features:
	 *    rows[0, *]
	 */
	protected void sequence_Matrix(EObject context, Matrix semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=MemberRef_MemberRefP_1_0 right=RightMemberRef)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_MemberRef(EObject context, MemberRefP semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.MEMBER_REF_P__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.MEMBER_REF_P__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.MEMBER_REF_P__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.MEMBER_REF_P__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getMemberRefAccess().getMemberRefPLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getMemberRefAccess().getRightRightMemberRefParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (name=FQN imports+=Import* gaml=GamlLangDef? statements+=Statement*)
	 *
	 * Features:
	 *    name[1, 1]
	 *    imports[0, *]
	 *    gaml[0, 1]
	 *    statements[0, *]
	 */
	protected void sequence_Model(EObject context, Model semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=Multiplication_Div_1_0_1_0 right=GamlBinExpr)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_Multiplication(EObject context, Div semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.DIV__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.DIV__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.DIV__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.DIV__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getMultiplicationAccess().getDivLeftAction_1_0_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getMultiplicationAccess().getRightGamlBinExprParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=Multiplication_Multi_1_0_0_0 right=GamlBinExpr)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_Multiplication(EObject context, Multi semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.MULTI__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.MULTI__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.MULTI__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.MULTI__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getMultiplicationAccess().getMultiLeftAction_1_0_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getMultiplicationAccess().getRightGamlBinExprParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=Multiplication_Pow_1_0_2_0 right=GamlBinExpr)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_Multiplication(EObject context, Pow semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.POW__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.POW__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.POW__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.POW__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getMultiplicationAccess().getPowLeftAction_1_0_2_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getMultiplicationAccess().getRightGamlBinExprParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=OrExp_Or_1_0 right=AndExp)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_OrExp(EObject context, Or semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.OR__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.OR__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.OR__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.OR__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getOrExpAccess().getOrLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getOrExpAccess().getRightAndExpParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=PairExpr_Pair_1_0_0 right=Addition)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_PairExpr(EObject context, Pair semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.PAIR__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.PAIR__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.PAIR__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.PAIR__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getPairExprAccess().getPairLeftAction_1_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getPairExprAccess().getRightAdditionParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (x=Expression y=Expression)
	 *
	 * Features:
	 *    x[1, 1]
	 *    y[1, 1]
	 */
	protected void sequence_Point(EObject context, Point semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.POINT__X) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.POINT__X));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.POINT__Y) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.POINT__Y));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getPointAccess().getXExpressionParserRuleCall_0_0(), semanticObject.getX());
		feeder.accept(grammarAccess.getPointAccess().getYExpressionParserRuleCall_2_0(), semanticObject.getY());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=Relational_RelEq_1_0_1_0 right=PairExpr)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_Relational(EObject context, RelEq semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_EQ__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_EQ__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_EQ__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_EQ__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getRelationalAccess().getRelEqLeftAction_1_0_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getRelationalAccess().getRightPairExprParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=Relational_RelEqEq_1_0_2_0 right=PairExpr)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_Relational(EObject context, RelEqEq semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_EQ_EQ__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_EQ_EQ__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_EQ_EQ__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_EQ_EQ__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getRelationalAccess().getRelEqEqLeftAction_1_0_2_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getRelationalAccess().getRightPairExprParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=Relational_RelGt_1_0_6_0 right=PairExpr)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_Relational(EObject context, RelGt semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_GT__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_GT__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_GT__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_GT__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getRelationalAccess().getRelGtLeftAction_1_0_6_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getRelationalAccess().getRightPairExprParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=Relational_RelGtEq_1_0_4_0 right=PairExpr)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_Relational(EObject context, RelGtEq semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_GT_EQ__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_GT_EQ__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_GT_EQ__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_GT_EQ__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getRelationalAccess().getRelGtEqLeftAction_1_0_4_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getRelationalAccess().getRightPairExprParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=Relational_RelLt_1_0_5_0 right=PairExpr)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_Relational(EObject context, RelLt semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_LT__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_LT__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_LT__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_LT__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getRelationalAccess().getRelLtLeftAction_1_0_5_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getRelationalAccess().getRightPairExprParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=Relational_RelLtEq_1_0_3_0 right=PairExpr)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_Relational(EObject context, RelLtEq semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_LT_EQ__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_LT_EQ__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_LT_EQ__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_LT_EQ__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getRelationalAccess().getRelLtEqLeftAction_1_0_3_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getRelationalAccess().getRightPairExprParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=Relational_RelNotEq_1_0_0_0 right=PairExpr)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_Relational(EObject context, RelNotEq semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_NOT_EQ__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_NOT_EQ__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.REL_NOT_EQ__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.REL_NOT_EQ__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getRelationalAccess().getRelNotEqLeftAction_1_0_0_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getRelationalAccess().getRightPairExprParserRuleCall_1_1_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (left=RightMemberRef_MemberRefR_1_0 right=RightMemberRef)
	 *
	 * Features:
	 *    left[1, 1]
	 *    right[1, 1]
	 */
	protected void sequence_RightMemberRef(EObject context, MemberRefR semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.MEMBER_REF_R__LEFT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.MEMBER_REF_R__LEFT));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.MEMBER_REF_R__RIGHT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.MEMBER_REF_R__RIGHT));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getRightMemberRefAccess().getMemberRefRLeftAction_1_0(), semanticObject.getLeft());
		feeder.accept(grammarAccess.getRightMemberRefAccess().getRightRightMemberRefParserRuleCall_1_2_0(), semanticObject.getRight());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (exprs+=Expression exprs+=Expression*)
	 *
	 * Features:
	 *    exprs[1, *]
	 */
	protected void sequence_Row(EObject context, Row semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (var=Expression facets+=FacetExpr* block=Block?)
	 *
	 * Features:
	 *    facets[0, *]
	 *    block[0, 1]
	 *    var[1, 1]
	 */
	protected void sequence_SetEval(EObject context, SetEval semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     value=BOOLEAN
	 *
	 * Features:
	 *    value[1, 1]
	 */
	protected void sequence_TerminalExpression(EObject context, BooleanLiteral semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.BOOLEAN_LITERAL__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.BOOLEAN_LITERAL__VALUE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getTerminalExpressionAccess().getValueBOOLEANTerminalRuleCall_4_1_0(), semanticObject.isValue());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     value=COLOR
	 *
	 * Features:
	 *    value[1, 1]
	 */
	protected void sequence_TerminalExpression(EObject context, ColorLiteral semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.COLOR_LITERAL__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.COLOR_LITERAL__VALUE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getTerminalExpressionAccess().getValueCOLORTerminalRuleCall_2_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     value=DOUBLE
	 *
	 * Features:
	 *    value[1, 1]
	 */
	protected void sequence_TerminalExpression(EObject context, DoubleLiteral semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.DOUBLE_LITERAL__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.DOUBLE_LITERAL__VALUE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getTerminalExpressionAccess().getValueDOUBLETerminalRuleCall_1_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     value=INT
	 *
	 * Features:
	 *    value[1, 1]
	 */
	protected void sequence_TerminalExpression(EObject context, IntLiteral semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.INT_LITERAL__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.INT_LITERAL__VALUE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getTerminalExpressionAccess().getValueINTTerminalRuleCall_0_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     value=STRING
	 *
	 * Features:
	 *    value[1, 1]
	 */
	protected void sequence_TerminalExpression(EObject context, StringLiteral semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.STRING_LITERAL__VALUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.STRING_LITERAL__VALUE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getTerminalExpressionAccess().getValueSTRINGTerminalRuleCall_3_1_0(), semanticObject.getValue());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     (condition=TernExp_Ternary_1_0 ifTrue=OrExp ifFalse=OrExp)
	 *
	 * Features:
	 *    condition[1, 1]
	 *    ifTrue[1, 1]
	 *    ifFalse[1, 1]
	 */
	protected void sequence_TernExp(EObject context, Ternary semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.TERNARY__CONDITION) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.TERNARY__CONDITION));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.TERNARY__IF_TRUE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.TERNARY__IF_TRUE));
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.TERNARY__IF_FALSE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.TERNARY__IF_FALSE));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getTernExpAccess().getTernaryConditionAction_1_0(), semanticObject.getCondition());
		feeder.accept(grammarAccess.getTernExpAccess().getIfTrueOrExpParserRuleCall_1_2_0(), semanticObject.getIfTrue());
		feeder.accept(grammarAccess.getTernExpAccess().getIfFalseOrExpParserRuleCall_1_4_0(), semanticObject.getIfFalse());
		feeder.finish();
	}
	
	
	/**
	 * Constraint:
	 *     ref=[AbstractDefinition|ID]
	 *
	 * Features:
	 *    ref[1, 1]
	 */
	protected void sequence_VariableRef(EObject context, VariableRef semanticObject) {
		if(errorAcceptor != null) {
			if(transientValues.isValueTransient(semanticObject, GamlPackage.Literals.VARIABLE_REF__REF) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, GamlPackage.Literals.VARIABLE_REF__REF));
		}
		INodesForEObjectProvider nodes = createNodeProvider(semanticObject);
		SequenceFeeder feeder = createSequencerFeeder(semanticObject, nodes);
		feeder.accept(grammarAccess.getVariableRefAccess().getRefAbstractDefinitionIDTerminalRuleCall_0_1(), semanticObject.getRef());
		feeder.finish();
	}
}
