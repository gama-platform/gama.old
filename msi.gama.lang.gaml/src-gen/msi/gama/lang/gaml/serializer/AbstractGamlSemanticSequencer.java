package msi.gama.lang.gaml.serializer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import msi.gama.lang.gaml.gaml.Access;
import msi.gama.lang.gaml.gaml.ArgPairExpr;
import msi.gama.lang.gaml.gaml.Array;
import msi.gama.lang.gaml.gaml.Block;
import msi.gama.lang.gaml.gaml.BooleanLiteral;
import msi.gama.lang.gaml.gaml.ColorLiteral;
import msi.gama.lang.gaml.gaml.Contents;
import msi.gama.lang.gaml.gaml.DoubleLiteral;
import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.Function;
import msi.gama.lang.gaml.gaml.GamlBinaryExpr;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.GamlUnaryExpr;
import msi.gama.lang.gaml.gaml.GamlUnitExpr;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.IntLiteral;
import msi.gama.lang.gaml.gaml.MemberRef;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.PairExpr;
import msi.gama.lang.gaml.gaml.Point;
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
			case GamlPackage.ACCESS:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_Access(context, (Access) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.ARG_PAIR_EXPR:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
				   context == grammarAccess.getAdditionRule() ||
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
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
			case GamlPackage.CONTENTS:
				if(context == grammarAccess.getContentsRule()) {
					sequence_Contents(context, (Contents) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.DOUBLE_LITERAL:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_Addition(context, (Expression) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.FACET:
				if(context == grammarAccess.getClassicFacetRule()) {
					sequence_ClassicFacet(context, (Facet) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getDefinitionFacetRule() ||
				   context == grammarAccess.getGamlVarRefRule()) {
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
				else break;
			case GamlPackage.FUNCTION:
				if(context == grammarAccess.getAbstractRefRule() ||
				   context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_Function(context, (Function) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_BINARY_EXPR:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_GamlBinaryExpr(context, (GamlBinaryExpr) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_UNARY_EXPR:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_GamlUnaryExpr(context, (GamlUnaryExpr) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getGamlUnaryExprAccess().getGamlUnaryExprLeftAction_1_1_0_0()) {
					sequence_GamlUnaryExpr_GamlUnaryExpr_1_1_0_0(context, (GamlUnaryExpr) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.GAML_UNIT_EXPR:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
			case GamlPackage.PAIR_EXPR:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				   context == grammarAccess.getPrimaryExpressionRule() ||
				   context == grammarAccess.getRelationalRule() ||
				   context == grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0() ||
				   context == grammarAccess.getTernExpRule() ||
				   context == grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0()) {
					sequence_PrimaryExpression(context, (Point) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.STATEMENT:
				if(context == grammarAccess.getAssignmentStatementRule()) {
					sequence_AssignmentStatement(context, (Statement) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getClassicStatementRule()) {
					sequence_ClassicStatement(context, (Statement) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getDefinitionStatementRule() ||
				   context == grammarAccess.getGamlVarRefRule()) {
					sequence_DefinitionStatement(context, (Statement) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getIfStatementRule()) {
					sequence_IfStatement(context, (Statement) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getReturnStatementRule()) {
					sequence_ReturnStatement(context, (Statement) semanticObject); 
					return; 
				}
				else if(context == grammarAccess.getStatementRule()) {
					sequence_Statement(context, (Statement) semanticObject); 
					return; 
				}
				else break;
			case GamlPackage.STRING_LITERAL:
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				if(context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
				   context == grammarAccess.getAccessRule() ||
				   context == grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0() ||
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
	 *     (left=Access_Access_1_0_0 args+=Expression args+=Expression*)
	 */
	protected void sequence_Access(EObject context, Access semanticObject) {
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
	 *     (((arg=ID op='::') | (arg=DefinitionFacetKey op=':')) right=Addition)
	 */
	protected void sequence_ArgPairExpr(EObject context, ArgPairExpr semanticObject) {
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
	 *             key='--' | 
	 *             key=':='
	 *         ) 
	 *         value=Expression 
	 *         facets+=Facet*
	 *     )
	 */
	protected void sequence_AssignmentStatement(EObject context, Statement semanticObject) {
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
	 *     ((key=ID | key='<-') expr=Expression)
	 */
	protected void sequence_ClassicFacet(EObject context, Facet semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=BuiltInStatementKey expr=Expression facets+=Facet* block=Block?)
	 */
	protected void sequence_ClassicStatement(EObject context, Statement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (type=ID type2=ID?)
	 */
	protected void sequence_Contents(EObject context, Contents semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=DefinitionFacetKey (name=ID | name=STRING | name=BuiltInStatementKey))
	 */
	protected void sequence_DefinitionFacet(EObject context, Facet semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key=ID of=Contents? (name=ID | name=STRING | name=BuiltInStatementKey)? facets+=Facet* block=Block?)
	 */
	protected void sequence_DefinitionStatement(EObject context, Statement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (
	 *         ((key='function:' | key='->') expr=Expression) | 
	 *         (key=DefinitionFacetKey (name=ID | name=STRING | name=BuiltInStatementKey)) | 
	 *         ((key=ID | key='<-') expr=Expression)
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
	 *     ((left=GamlUnaryExpr_GamlUnaryExpr_1_1_0_0 (op='¡' right=UnitName)) | ((op='-' | op='!' | op='my' | op='the' | op='not') right=GamlUnaryExpr))
	 */
	protected void sequence_GamlUnaryExpr(EObject context, GamlUnaryExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     {GamlUnaryExpr}
	 */
	protected void sequence_GamlUnaryExpr_GamlUnaryExpr_1_1_0_0(EObject context, GamlUnaryExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (left=GamlUnitExpr_GamlUnitExpr_1_0_0 (op='#' | op='¡') right=UnitName)
	 */
	protected void sequence_GamlUnitExpr(EObject context, GamlUnitExpr semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key='if' expr=Expression block=Block (else=IfStatement | else=Block)?)
	 */
	protected void sequence_IfStatement(EObject context, Statement semanticObject) {
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
	 *     (left=MemberRef_MemberRef_1_0 (op='.' right=PrimaryExpression))
	 */
	protected void sequence_MemberRef(EObject context, MemberRef semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
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
	 *     (left=Expression op=',' right=Expression z=Expression?)
	 */
	protected void sequence_PrimaryExpression(EObject context, Point semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (key='return' expr=Expression?)
	 */
	protected void sequence_ReturnStatement(EObject context, Statement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Constraint:
	 *     (
	 *         (
	 *             expr=Expression 
	 *             (
	 *                 key='<-' | 
	 *                 key='<<' | 
	 *                 key='>>' | 
	 *                 key='+=' | 
	 *                 key='-=' | 
	 *                 key='++' | 
	 *                 key='--' | 
	 *                 key=':='
	 *             ) 
	 *             value=Expression 
	 *             facets+=Facet*
	 *         ) | 
	 *         (key='return' expr=Expression?) | 
	 *         (key='if' expr=Expression block=Block (else=IfStatement | else=Block)?) | 
	 *         (key=BuiltInStatementKey expr=Expression facets+=Facet* block=Block?) | 
	 *         (key=ID of=Contents? (name=ID | name=STRING | name=BuiltInStatementKey)? facets+=Facet* block=Block?)
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
