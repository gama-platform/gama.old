/*******************************************************************************************************
 *
 * AbstractGamlSyntacticSequencer.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.serializer;

import com.google.inject.Inject;
import java.util.List;
import msi.gama.lang.gaml.services.GamlGrammarAccess;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.AbstractElementAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.AlternativeAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.GroupAlias;
import org.eclipse.xtext.serializer.analysis.GrammarAlias.TokenAlias;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynNavigable;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynTransition;
import org.eclipse.xtext.serializer.sequencer.AbstractSyntacticSequencer;

/**
 * The Class AbstractGamlSyntacticSequencer.
 */
@SuppressWarnings("all")
public abstract class AbstractGamlSyntacticSequencer extends AbstractSyntacticSequencer {

	/** The grammar access. */
	protected GamlGrammarAccess grammarAccess;
	
	/** The match S equations semicolon keyword 3 1 or left curly bracket keyword 3 0 0 right curly bracket keyword 3 0 2. */
	protected AbstractElementAlias match_S_Equations_SemicolonKeyword_3_1_or___LeftCurlyBracketKeyword_3_0_0_RightCurlyBracketKeyword_3_0_2__;
	
	/** The match S set less than sign hyphen minus keyword 2 1 or value keyword 2 0. */
	protected AbstractElementAlias match_S_Set_LessThanSignHyphenMinusKeyword_2_1_or_ValueKeyword_2_0;
	
	/**
	 * Inits the.
	 *
	 * @param access the access
	 */
	@Inject
	protected void init(IGrammarAccess access) {
		grammarAccess = (GamlGrammarAccess) access;
		match_S_Equations_SemicolonKeyword_3_1_or___LeftCurlyBracketKeyword_3_0_0_RightCurlyBracketKeyword_3_0_2__ = new AlternativeAlias(false, false, new GroupAlias(false, false, new TokenAlias(false, false, grammarAccess.getS_EquationsAccess().getLeftCurlyBracketKeyword_3_0_0()), new TokenAlias(false, false, grammarAccess.getS_EquationsAccess().getRightCurlyBracketKeyword_3_0_2())), new TokenAlias(false, false, grammarAccess.getS_EquationsAccess().getSemicolonKeyword_3_1()));
		match_S_Set_LessThanSignHyphenMinusKeyword_2_1_or_ValueKeyword_2_0 = new AlternativeAlias(false, false, new TokenAlias(false, false, grammarAccess.getS_SetAccess().getLessThanSignHyphenMinusKeyword_2_1()), new TokenAlias(false, false, grammarAccess.getS_SetAccess().getValueKeyword_2_0()));
	}
	
	@Override
	protected String getUnassignedRuleCallToken(EObject semanticObject, RuleCall ruleCall, INode node) {
		return "";
	}
	
	
	@Override
	protected void emitUnassignedTokens(EObject semanticObject, ISynTransition transition, INode fromNode, INode toNode) {
		if (transition.getAmbiguousSyntaxes().isEmpty()) return;
		List<INode> transitionNodes = collectNodes(fromNode, toNode);
		for (AbstractElementAlias syntax : transition.getAmbiguousSyntaxes()) {
			List<INode> syntaxNodes = getNodesFor(transitionNodes, syntax);
			if (match_S_Equations_SemicolonKeyword_3_1_or___LeftCurlyBracketKeyword_3_0_0_RightCurlyBracketKeyword_3_0_2__.equals(syntax))
				emit_S_Equations_SemicolonKeyword_3_1_or___LeftCurlyBracketKeyword_3_0_0_RightCurlyBracketKeyword_3_0_2__(semanticObject, getLastNavigableState(), syntaxNodes);
			else if (match_S_Set_LessThanSignHyphenMinusKeyword_2_1_or_ValueKeyword_2_0.equals(syntax))
				emit_S_Set_LessThanSignHyphenMinusKeyword_2_1_or_ValueKeyword_2_0(semanticObject, getLastNavigableState(), syntaxNodes);
			else acceptNodes(getLastNavigableState(), syntaxNodes);
		}
	}

	/**
	 * Ambiguous syntax:
	 *     ('{' '}') | ';'
	 *
	 * This ambiguous syntax occurs at:
	 *     facets+=Facet (ambiguity) (rule end)
	 *     name=Valid_ID (ambiguity) (rule end)
	 */
	protected void emit_S_Equations_SemicolonKeyword_3_1_or___LeftCurlyBracketKeyword_3_0_0_RightCurlyBracketKeyword_3_0_2__(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
	/**
	 * Ambiguous syntax:
	 *     'value:' | '<-'
	 *
	 * This ambiguous syntax occurs at:
	 *     expr=Expression (ambiguity) value=Expression
	 */
	protected void emit_S_Set_LessThanSignHyphenMinusKeyword_2_1_or_ValueKeyword_2_0(EObject semanticObject, ISynNavigable transition, List<INode> nodes) {
		acceptNodes(transition, nodes);
	}
	
}
