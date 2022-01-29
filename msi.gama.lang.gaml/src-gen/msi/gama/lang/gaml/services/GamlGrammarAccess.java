/*******************************************************************************************************
 *
 * GamlGrammarAccess.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import org.eclipse.xtext.Action;
import org.eclipse.xtext.Alternatives;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.Group;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.service.AbstractElementFinder;
import org.eclipse.xtext.service.GrammarProvider;

/**
 * The Class GamlGrammarAccess.
 */
@Singleton
public class GamlGrammarAccess extends AbstractElementFinder.AbstractGrammarElementFinder {
	
	/**
	 * The Class EntryElements.
	 */
	public class EntryElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Entry");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c model parser rule call 0. */
		private final RuleCall cModelParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c string evaluator parser rule call 1. */
		private final RuleCall cStringEvaluatorParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		/** The c standalone block parser rule call 2. */
		private final RuleCall cStandaloneBlockParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		/** The c experiment file structure parser rule call 3. */
		private final RuleCall cExperimentFileStructureParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		
		//Entry:
		//    ->Model | StringEvaluator | StandaloneBlock | ExperimentFileStructure;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//->Model | StringEvaluator | StandaloneBlock | ExperimentFileStructure
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the model parser rule call 0.
		 *
		 * @return the model parser rule call 0
		 */
		//->Model
		public RuleCall getModelParserRuleCall_0() { return cModelParserRuleCall_0; }
		
		/**
		 * Gets the string evaluator parser rule call 1.
		 *
		 * @return the string evaluator parser rule call 1
		 */
		//StringEvaluator
		public RuleCall getStringEvaluatorParserRuleCall_1() { return cStringEvaluatorParserRuleCall_1; }
		
		/**
		 * Gets the standalone block parser rule call 2.
		 *
		 * @return the standalone block parser rule call 2
		 */
		//StandaloneBlock
		public RuleCall getStandaloneBlockParserRuleCall_2() { return cStandaloneBlockParserRuleCall_2; }
		
		/**
		 * Gets the experiment file structure parser rule call 3.
		 *
		 * @return the experiment file structure parser rule call 3
		 */
		//ExperimentFileStructure
		public RuleCall getExperimentFileStructureParserRuleCall_3() { return cExperimentFileStructureParserRuleCall_3; }
	}
	
	/**
	 * The Class StandaloneBlockElements.
	 */
	public class StandaloneBlockElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.StandaloneBlock");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c synthetic keyword 0. */
		private final Keyword c__synthetic__Keyword_0 = (Keyword)cGroup.eContents().get(0);
		
		/** The c block assignment 1. */
		private final Assignment cBlockAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c block block parser rule call 1 0. */
		private final RuleCall cBlockBlockParserRuleCall_1_0 = (RuleCall)cBlockAssignment_1.eContents().get(0);
		
		//StandaloneBlock:
		//    '__synthetic__' block=Block
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//'__synthetic__' block=Block
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the synthetic keyword 0.
		 *
		 * @return the synthetic keyword 0
		 */
		//'__synthetic__'
		public Keyword get__synthetic__Keyword_0() { return c__synthetic__Keyword_0; }
		
		/**
		 * Gets the block assignment 1.
		 *
		 * @return the block assignment 1
		 */
		//block=Block
		public Assignment getBlockAssignment_1() { return cBlockAssignment_1; }
		
		/**
		 * Gets the block block parser rule call 1 0.
		 *
		 * @return the block block parser rule call 1 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_1_0() { return cBlockBlockParserRuleCall_1_0; }
	}
	
	/**
	 * The Class StringEvaluatorElements.
	 */
	public class StringEvaluatorElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.StringEvaluator");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c toto assignment 0. */
		private final Assignment cTotoAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c toto ID terminal rule call 0 0. */
		private final RuleCall cTotoIDTerminalRuleCall_0_0 = (RuleCall)cTotoAssignment_0.eContents().get(0);
		
		/** The c less than sign hyphen minus keyword 1. */
		private final Keyword cLessThanSignHyphenMinusKeyword_1 = (Keyword)cGroup.eContents().get(1);
		
		/** The c expr assignment 2. */
		private final Assignment cExprAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c expr expression parser rule call 2 0. */
		private final RuleCall cExprExpressionParserRuleCall_2_0 = (RuleCall)cExprAssignment_2.eContents().get(0);
		
		//StringEvaluator:
		//    toto=ID "<-" expr=Expression;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//toto=ID "<-" expr=Expression
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the toto assignment 0.
		 *
		 * @return the toto assignment 0
		 */
		//toto=ID
		public Assignment getTotoAssignment_0() { return cTotoAssignment_0; }
		
		/**
		 * Gets the toto ID terminal rule call 0 0.
		 *
		 * @return the toto ID terminal rule call 0 0
		 */
		//ID
		public RuleCall getTotoIDTerminalRuleCall_0_0() { return cTotoIDTerminalRuleCall_0_0; }
		
		/**
		 * Gets the less than sign hyphen minus keyword 1.
		 *
		 * @return the less than sign hyphen minus keyword 1
		 */
		//"<-"
		public Keyword getLessThanSignHyphenMinusKeyword_1() { return cLessThanSignHyphenMinusKeyword_1; }
		
		/**
		 * Gets the expr assignment 2.
		 *
		 * @return the expr assignment 2
		 */
		//expr=Expression
		public Assignment getExprAssignment_2() { return cExprAssignment_2; }
		
		/**
		 * Gets the expr expression parser rule call 2 0.
		 *
		 * @return the expr expression parser rule call 2 0
		 */
		//Expression
		public RuleCall getExprExpressionParserRuleCall_2_0() { return cExprExpressionParserRuleCall_2_0; }
	}
	
	/**
	 * The Class ModelElements.
	 */
	public class ModelElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Model");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c pragmas assignment 0. */
		private final Assignment cPragmasAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c pragmas pragma parser rule call 0 0. */
		private final RuleCall cPragmasPragmaParserRuleCall_0_0 = (RuleCall)cPragmasAssignment_0.eContents().get(0);
		
		/** The c model keyword 1. */
		private final Keyword cModelKeyword_1 = (Keyword)cGroup.eContents().get(1);
		
		/** The c name assignment 2. */
		private final Assignment cNameAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c name ID terminal rule call 2 0. */
		private final RuleCall cNameIDTerminalRuleCall_2_0 = (RuleCall)cNameAssignment_2.eContents().get(0);
		
		/** The c imports assignment 3. */
		private final Assignment cImportsAssignment_3 = (Assignment)cGroup.eContents().get(3);
		
		/** The c imports import parser rule call 3 0. */
		private final RuleCall cImportsImportParserRuleCall_3_0 = (RuleCall)cImportsAssignment_3.eContents().get(0);
		
		/** The c block assignment 4. */
		private final Assignment cBlockAssignment_4 = (Assignment)cGroup.eContents().get(4);
		
		/** The c block model block parser rule call 4 0. */
		private final RuleCall cBlockModelBlockParserRuleCall_4_0 = (RuleCall)cBlockAssignment_4.eContents().get(0);
		
		//Model:
		//    (pragmas +=Pragma)* 'model' name=ID  (imports+=Import)* block=ModelBlock;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//(pragmas +=Pragma)* 'model' name=ID  (imports+=Import)* block=ModelBlock
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the pragmas assignment 0.
		 *
		 * @return the pragmas assignment 0
		 */
		//(pragmas +=Pragma)*
		public Assignment getPragmasAssignment_0() { return cPragmasAssignment_0; }
		
		/**
		 * Gets the pragmas pragma parser rule call 0 0.
		 *
		 * @return the pragmas pragma parser rule call 0 0
		 */
		//Pragma
		public RuleCall getPragmasPragmaParserRuleCall_0_0() { return cPragmasPragmaParserRuleCall_0_0; }
		
		/**
		 * Gets the model keyword 1.
		 *
		 * @return the model keyword 1
		 */
		//'model'
		public Keyword getModelKeyword_1() { return cModelKeyword_1; }
		
		/**
		 * Gets the name assignment 2.
		 *
		 * @return the name assignment 2
		 */
		//name=ID
		public Assignment getNameAssignment_2() { return cNameAssignment_2; }
		
		/**
		 * Gets the name ID terminal rule call 2 0.
		 *
		 * @return the name ID terminal rule call 2 0
		 */
		//ID
		public RuleCall getNameIDTerminalRuleCall_2_0() { return cNameIDTerminalRuleCall_2_0; }
		
		/**
		 * Gets the imports assignment 3.
		 *
		 * @return the imports assignment 3
		 */
		//(imports+=Import)*
		public Assignment getImportsAssignment_3() { return cImportsAssignment_3; }
		
		/**
		 * Gets the imports import parser rule call 3 0.
		 *
		 * @return the imports import parser rule call 3 0
		 */
		//Import
		public RuleCall getImportsImportParserRuleCall_3_0() { return cImportsImportParserRuleCall_3_0; }
		
		/**
		 * Gets the block assignment 4.
		 *
		 * @return the block assignment 4
		 */
		//block=ModelBlock
		public Assignment getBlockAssignment_4() { return cBlockAssignment_4; }
		
		/**
		 * Gets the block model block parser rule call 4 0.
		 *
		 * @return the block model block parser rule call 4 0
		 */
		//ModelBlock
		public RuleCall getBlockModelBlockParserRuleCall_4_0() { return cBlockModelBlockParserRuleCall_4_0; }
	}
	
	/**
	 * The Class ModelBlockElements.
	 */
	public class ModelBlockElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ModelBlock");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c block action 0. */
		private final Action cBlockAction_0 = (Action)cGroup.eContents().get(0);
		
		/** The c statements assignment 1. */
		private final Assignment cStatementsAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c statements S section parser rule call 1 0. */
		private final RuleCall cStatementsS_SectionParserRuleCall_1_0 = (RuleCall)cStatementsAssignment_1.eContents().get(0);
		
		//ModelBlock returns Block:
		//    {Block} (statements+=(S_Section))*
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//{Block} (statements+=(S_Section))*
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the block action 0.
		 *
		 * @return the block action 0
		 */
		//{Block}
		public Action getBlockAction_0() { return cBlockAction_0; }
		
		/**
		 * Gets the statements assignment 1.
		 *
		 * @return the statements assignment 1
		 */
		//(statements+=(S_Section))*
		public Assignment getStatementsAssignment_1() { return cStatementsAssignment_1; }
		
		/**
		 * Gets the statements S section parser rule call 1 0.
		 *
		 * @return the statements S section parser rule call 1 0
		 */
		//(S_Section)
		public RuleCall getStatementsS_SectionParserRuleCall_1_0() { return cStatementsS_SectionParserRuleCall_1_0; }
	}
	
	/**
	 * The Class ImportElements.
	 */
	public class ImportElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Import");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c import keyword 0. */
		private final Keyword cImportKeyword_0 = (Keyword)cGroup.eContents().get(0);
		
		/** The c import URI assignment 1. */
		private final Assignment cImportURIAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c import URISTRING terminal rule call 1 0. */
		private final RuleCall cImportURISTRINGTerminalRuleCall_1_0 = (RuleCall)cImportURIAssignment_1.eContents().get(0);
		
		/** The c group 2. */
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		
		/** The c as keyword 2 0. */
		private final Keyword cAsKeyword_2_0 = (Keyword)cGroup_2.eContents().get(0);
		
		/** The c name assignment 2 1. */
		private final Assignment cNameAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		
		/** The c name valid ID parser rule call 2 1 0. */
		private final RuleCall cNameValid_IDParserRuleCall_2_1_0 = (RuleCall)cNameAssignment_2_1.eContents().get(0);
		
		//Import:
		//    'import' importURI=STRING ("as" name=Valid_ID)?;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//'import' importURI=STRING ("as" name=Valid_ID)?
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the import keyword 0.
		 *
		 * @return the import keyword 0
		 */
		//'import'
		public Keyword getImportKeyword_0() { return cImportKeyword_0; }
		
		/**
		 * Gets the import URI assignment 1.
		 *
		 * @return the import URI assignment 1
		 */
		//importURI=STRING
		public Assignment getImportURIAssignment_1() { return cImportURIAssignment_1; }
		
		/**
		 * Gets the import URISTRING terminal rule call 1 0.
		 *
		 * @return the import URISTRING terminal rule call 1 0
		 */
		//STRING
		public RuleCall getImportURISTRINGTerminalRuleCall_1_0() { return cImportURISTRINGTerminalRuleCall_1_0; }
		
		/**
		 * Gets the group 2.
		 *
		 * @return the group 2
		 */
		//("as" name=Valid_ID)?
		public Group getGroup_2() { return cGroup_2; }
		
		/**
		 * Gets the as keyword 2 0.
		 *
		 * @return the as keyword 2 0
		 */
		//"as"
		public Keyword getAsKeyword_2_0() { return cAsKeyword_2_0; }
		
		/**
		 * Gets the name assignment 2 1.
		 *
		 * @return the name assignment 2 1
		 */
		//name=Valid_ID
		public Assignment getNameAssignment_2_1() { return cNameAssignment_2_1; }
		
		/**
		 * Gets the name valid ID parser rule call 2 1 0.
		 *
		 * @return the name valid ID parser rule call 2 1 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_2_1_0() { return cNameValid_IDParserRuleCall_2_1_0; }
	}
	
	/**
	 * The Class PragmaElements.
	 */
	public class PragmaElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Pragma");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c commercial at keyword 0. */
		private final Keyword cCommercialAtKeyword_0 = (Keyword)cGroup.eContents().get(0);
		
		/** The c name assignment 1. */
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c name ID terminal rule call 1 0. */
		private final RuleCall cNameIDTerminalRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		
		// // must be named importURI
		//Pragma:
		//    '@' name=ID
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//'@' name=ID
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the commercial at keyword 0.
		 *
		 * @return the commercial at keyword 0
		 */
		//'@'
		public Keyword getCommercialAtKeyword_0() { return cCommercialAtKeyword_0; }
		
		/**
		 * Gets the name assignment 1.
		 *
		 * @return the name assignment 1
		 */
		//name=ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		/**
		 * Gets the name ID terminal rule call 1 0.
		 *
		 * @return the name ID terminal rule call 1 0
		 */
		//ID
		public RuleCall getNameIDTerminalRuleCall_1_0() { return cNameIDTerminalRuleCall_1_0; }
	}
	
	/**
	 * The Class ExperimentFileStructureElements.
	 */
	public class ExperimentFileStructureElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ExperimentFileStructure");
		
		/** The c exp assignment. */
		private final Assignment cExpAssignment = (Assignment)rule.eContents().get(1);
		
		/** The c exp headless experiment parser rule call 0. */
		private final RuleCall cExpHeadlessExperimentParserRuleCall_0 = (RuleCall)cExpAssignment.eContents().get(0);
		
		///**
		// * Experiment files
		// */
		//ExperimentFileStructure:
		//    exp=HeadlessExperiment
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the exp assignment.
		 *
		 * @return the exp assignment
		 */
		//exp=HeadlessExperiment
		public Assignment getExpAssignment() { return cExpAssignment; }
		
		/**
		 * Gets the exp headless experiment parser rule call 0.
		 *
		 * @return the exp headless experiment parser rule call 0
		 */
		//HeadlessExperiment
		public RuleCall getExpHeadlessExperimentParserRuleCall_0() { return cExpHeadlessExperimentParserRuleCall_0; }
	}
	
	/**
	 * The Class HeadlessExperimentElements.
	 */
	public class HeadlessExperimentElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.HeadlessExperiment");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key experiment key parser rule call 0 0. */
		private final RuleCall cKey_ExperimentKeyParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c first facet assignment 1. */
		private final Assignment cFirstFacetAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c first facet name keyword 1 0. */
		private final Keyword cFirstFacetNameKeyword_1_0 = (Keyword)cFirstFacetAssignment_1.eContents().get(0);
		
		/** The c name assignment 2. */
		private final Assignment cNameAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c name alternatives 2 0. */
		private final Alternatives cNameAlternatives_2_0 = (Alternatives)cNameAssignment_2.eContents().get(0);
		
		/** The c name valid ID parser rule call 2 0 0. */
		private final RuleCall cNameValid_IDParserRuleCall_2_0_0 = (RuleCall)cNameAlternatives_2_0.eContents().get(0);
		
		/** The c name STRING terminal rule call 2 0 1. */
		private final RuleCall cNameSTRINGTerminalRuleCall_2_0_1 = (RuleCall)cNameAlternatives_2_0.eContents().get(1);
		
		/** The c group 3. */
		private final Group cGroup_3 = (Group)cGroup.eContents().get(3);
		
		/** The c model keyword 3 0. */
		private final Keyword cModelKeyword_3_0 = (Keyword)cGroup_3.eContents().get(0);
		
		/** The c import URI assignment 3 1. */
		private final Assignment cImportURIAssignment_3_1 = (Assignment)cGroup_3.eContents().get(1);
		
		/** The c import URISTRING terminal rule call 3 1 0. */
		private final RuleCall cImportURISTRINGTerminalRuleCall_3_1_0 = (RuleCall)cImportURIAssignment_3_1.eContents().get(0);
		
		/** The c facets assignment 4. */
		private final Assignment cFacetsAssignment_4 = (Assignment)cGroup.eContents().get(4);
		
		/** The c facets facet parser rule call 4 0. */
		private final RuleCall cFacetsFacetParserRuleCall_4_0 = (RuleCall)cFacetsAssignment_4.eContents().get(0);
		
		/** The c alternatives 5. */
		private final Alternatives cAlternatives_5 = (Alternatives)cGroup.eContents().get(5);
		
		/** The c block assignment 5 0. */
		private final Assignment cBlockAssignment_5_0 = (Assignment)cAlternatives_5.eContents().get(0);
		
		/** The c block block parser rule call 5 0 0. */
		private final RuleCall cBlockBlockParserRuleCall_5_0_0 = (RuleCall)cBlockAssignment_5_0.eContents().get(0);
		
		/** The c semicolon keyword 5 1. */
		private final Keyword cSemicolonKeyword_5_1 = (Keyword)cAlternatives_5.eContents().get(1);
		
		//HeadlessExperiment :
		//    key=_ExperimentKey (firstFacet="name:")? name=(Valid_ID | STRING) ('model:' importURI=STRING) ? (facets+=Facet)* (block=Block | ';');
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key=_ExperimentKey (firstFacet="name:")? name=(Valid_ID | STRING) ('model:' importURI=STRING) ? (facets+=Facet)* (block=Block | ';')
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key=_ExperimentKey
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key experiment key parser rule call 0 0.
		 *
		 * @return the key experiment key parser rule call 0 0
		 */
		//_ExperimentKey
		public RuleCall getKey_ExperimentKeyParserRuleCall_0_0() { return cKey_ExperimentKeyParserRuleCall_0_0; }
		
		/**
		 * Gets the first facet assignment 1.
		 *
		 * @return the first facet assignment 1
		 */
		//(firstFacet="name:")?
		public Assignment getFirstFacetAssignment_1() { return cFirstFacetAssignment_1; }
		
		/**
		 * Gets the first facet name keyword 1 0.
		 *
		 * @return the first facet name keyword 1 0
		 */
		//"name:"
		public Keyword getFirstFacetNameKeyword_1_0() { return cFirstFacetNameKeyword_1_0; }
		
		/**
		 * Gets the name assignment 2.
		 *
		 * @return the name assignment 2
		 */
		//name=(Valid_ID | STRING)
		public Assignment getNameAssignment_2() { return cNameAssignment_2; }
		
		/**
		 * Gets the name alternatives 2 0.
		 *
		 * @return the name alternatives 2 0
		 */
		//(Valid_ID | STRING)
		public Alternatives getNameAlternatives_2_0() { return cNameAlternatives_2_0; }
		
		/**
		 * Gets the name valid ID parser rule call 2 0 0.
		 *
		 * @return the name valid ID parser rule call 2 0 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_2_0_0() { return cNameValid_IDParserRuleCall_2_0_0; }
		
		/**
		 * Gets the name STRING terminal rule call 2 0 1.
		 *
		 * @return the name STRING terminal rule call 2 0 1
		 */
		//STRING
		public RuleCall getNameSTRINGTerminalRuleCall_2_0_1() { return cNameSTRINGTerminalRuleCall_2_0_1; }
		
		/**
		 * Gets the group 3.
		 *
		 * @return the group 3
		 */
		//('model:' importURI=STRING) ?
		public Group getGroup_3() { return cGroup_3; }
		
		/**
		 * Gets the model keyword 3 0.
		 *
		 * @return the model keyword 3 0
		 */
		//'model:'
		public Keyword getModelKeyword_3_0() { return cModelKeyword_3_0; }
		
		/**
		 * Gets the import URI assignment 3 1.
		 *
		 * @return the import URI assignment 3 1
		 */
		//importURI=STRING
		public Assignment getImportURIAssignment_3_1() { return cImportURIAssignment_3_1; }
		
		/**
		 * Gets the import URISTRING terminal rule call 3 1 0.
		 *
		 * @return the import URISTRING terminal rule call 3 1 0
		 */
		//STRING
		public RuleCall getImportURISTRINGTerminalRuleCall_3_1_0() { return cImportURISTRINGTerminalRuleCall_3_1_0; }
		
		/**
		 * Gets the facets assignment 4.
		 *
		 * @return the facets assignment 4
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_4() { return cFacetsAssignment_4; }
		
		/**
		 * Gets the facets facet parser rule call 4 0.
		 *
		 * @return the facets facet parser rule call 4 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_4_0() { return cFacetsFacetParserRuleCall_4_0; }
		
		/**
		 * Gets the alternatives 5.
		 *
		 * @return the alternatives 5
		 */
		//(block=Block | ';')
		public Alternatives getAlternatives_5() { return cAlternatives_5; }
		
		/**
		 * Gets the block assignment 5 0.
		 *
		 * @return the block assignment 5 0
		 */
		//block=Block
		public Assignment getBlockAssignment_5_0() { return cBlockAssignment_5_0; }
		
		/**
		 * Gets the block block parser rule call 5 0 0.
		 *
		 * @return the block block parser rule call 5 0 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_5_0_0() { return cBlockBlockParserRuleCall_5_0_0; }
		
		/**
		 * Gets the semicolon keyword 5 1.
		 *
		 * @return the semicolon keyword 5 1
		 */
		//';'
		public Keyword getSemicolonKeyword_5_1() { return cSemicolonKeyword_5_1; }
	}
	
	/**
	 * The Class S_SectionElements.
	 */
	public class S_SectionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Section");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c S global parser rule call 0. */
		private final RuleCall cS_GlobalParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c S species parser rule call 1. */
		private final RuleCall cS_SpeciesParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		/** The c S experiment parser rule call 2. */
		private final RuleCall cS_ExperimentParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		///**
		// * Global statements
		// */
		//S_Section returns Statement:
		//     S_Global | S_Species | S_Experiment
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//S_Global | S_Species | S_Experiment
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the s global parser rule call 0.
		 *
		 * @return the s global parser rule call 0
		 */
		//S_Global
		public RuleCall getS_GlobalParserRuleCall_0() { return cS_GlobalParserRuleCall_0; }
		
		/**
		 * Gets the s species parser rule call 1.
		 *
		 * @return the s species parser rule call 1
		 */
		//S_Species
		public RuleCall getS_SpeciesParserRuleCall_1() { return cS_SpeciesParserRuleCall_1; }
		
		/**
		 * Gets the s experiment parser rule call 2.
		 *
		 * @return the s experiment parser rule call 2
		 */
		//S_Experiment
		public RuleCall getS_ExperimentParserRuleCall_2() { return cS_ExperimentParserRuleCall_2; }
	}
	
	/**
	 * The Class S_GlobalElements.
	 */
	public class S_GlobalElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Global");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key global keyword 0 0. */
		private final Keyword cKeyGlobalKeyword_0_0 = (Keyword)cKeyAssignment_0.eContents().get(0);
		
		/** The c facets assignment 1. */
		private final Assignment cFacetsAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c facets facet parser rule call 1 0. */
		private final RuleCall cFacetsFacetParserRuleCall_1_0 = (RuleCall)cFacetsAssignment_1.eContents().get(0);
		
		/** The c alternatives 2. */
		private final Alternatives cAlternatives_2 = (Alternatives)cGroup.eContents().get(2);
		
		/** The c block assignment 2 0. */
		private final Assignment cBlockAssignment_2_0 = (Assignment)cAlternatives_2.eContents().get(0);
		
		/** The c block block parser rule call 2 0 0. */
		private final RuleCall cBlockBlockParserRuleCall_2_0_0 = (RuleCall)cBlockAssignment_2_0.eContents().get(0);
		
		/** The c semicolon keyword 2 1. */
		private final Keyword cSemicolonKeyword_2_1 = (Keyword)cAlternatives_2.eContents().get(1);
		
		//S_Global :
		//    key="global" (facets+=Facet)* (block=Block | ';')
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key="global" (facets+=Facet)* (block=Block | ';')
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key="global"
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key global keyword 0 0.
		 *
		 * @return the key global keyword 0 0
		 */
		//"global"
		public Keyword getKeyGlobalKeyword_0_0() { return cKeyGlobalKeyword_0_0; }
		
		/**
		 * Gets the facets assignment 1.
		 *
		 * @return the facets assignment 1
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_1() { return cFacetsAssignment_1; }
		
		/**
		 * Gets the facets facet parser rule call 1 0.
		 *
		 * @return the facets facet parser rule call 1 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_1_0() { return cFacetsFacetParserRuleCall_1_0; }
		
		/**
		 * Gets the alternatives 2.
		 *
		 * @return the alternatives 2
		 */
		//(block=Block | ';')
		public Alternatives getAlternatives_2() { return cAlternatives_2; }
		
		/**
		 * Gets the block assignment 2 0.
		 *
		 * @return the block assignment 2 0
		 */
		//block=Block
		public Assignment getBlockAssignment_2_0() { return cBlockAssignment_2_0; }
		
		/**
		 * Gets the block block parser rule call 2 0 0.
		 *
		 * @return the block block parser rule call 2 0 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_2_0_0() { return cBlockBlockParserRuleCall_2_0_0; }
		
		/**
		 * Gets the semicolon keyword 2 1.
		 *
		 * @return the semicolon keyword 2 1
		 */
		//';'
		public Keyword getSemicolonKeyword_2_1() { return cSemicolonKeyword_2_1; }
	}
	
	/**
	 * The Class S_SpeciesElements.
	 */
	public class S_SpeciesElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Species");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key species key parser rule call 0 0. */
		private final RuleCall cKey_SpeciesKeyParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c first facet assignment 1. */
		private final Assignment cFirstFacetAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c first facet name keyword 1 0. */
		private final Keyword cFirstFacetNameKeyword_1_0 = (Keyword)cFirstFacetAssignment_1.eContents().get(0);
		
		/** The c name assignment 2. */
		private final Assignment cNameAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c name ID terminal rule call 2 0. */
		private final RuleCall cNameIDTerminalRuleCall_2_0 = (RuleCall)cNameAssignment_2.eContents().get(0);
		
		/** The c facets assignment 3. */
		private final Assignment cFacetsAssignment_3 = (Assignment)cGroup.eContents().get(3);
		
		/** The c facets facet parser rule call 3 0. */
		private final RuleCall cFacetsFacetParserRuleCall_3_0 = (RuleCall)cFacetsAssignment_3.eContents().get(0);
		
		/** The c alternatives 4. */
		private final Alternatives cAlternatives_4 = (Alternatives)cGroup.eContents().get(4);
		
		/** The c block assignment 4 0. */
		private final Assignment cBlockAssignment_4_0 = (Assignment)cAlternatives_4.eContents().get(0);
		
		/** The c block block parser rule call 4 0 0. */
		private final RuleCall cBlockBlockParserRuleCall_4_0_0 = (RuleCall)cBlockAssignment_4_0.eContents().get(0);
		
		/** The c semicolon keyword 4 1. */
		private final Keyword cSemicolonKeyword_4_1 = (Keyword)cAlternatives_4.eContents().get(1);
		
		//S_Species :
		//    key=_SpeciesKey (firstFacet='name:')? name=ID (facets+=Facet)* (block=Block | ';');
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key=_SpeciesKey (firstFacet='name:')? name=ID (facets+=Facet)* (block=Block | ';')
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key=_SpeciesKey
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key species key parser rule call 0 0.
		 *
		 * @return the key species key parser rule call 0 0
		 */
		//_SpeciesKey
		public RuleCall getKey_SpeciesKeyParserRuleCall_0_0() { return cKey_SpeciesKeyParserRuleCall_0_0; }
		
		/**
		 * Gets the first facet assignment 1.
		 *
		 * @return the first facet assignment 1
		 */
		//(firstFacet='name:')?
		public Assignment getFirstFacetAssignment_1() { return cFirstFacetAssignment_1; }
		
		/**
		 * Gets the first facet name keyword 1 0.
		 *
		 * @return the first facet name keyword 1 0
		 */
		//'name:'
		public Keyword getFirstFacetNameKeyword_1_0() { return cFirstFacetNameKeyword_1_0; }
		
		/**
		 * Gets the name assignment 2.
		 *
		 * @return the name assignment 2
		 */
		//name=ID
		public Assignment getNameAssignment_2() { return cNameAssignment_2; }
		
		/**
		 * Gets the name ID terminal rule call 2 0.
		 *
		 * @return the name ID terminal rule call 2 0
		 */
		//ID
		public RuleCall getNameIDTerminalRuleCall_2_0() { return cNameIDTerminalRuleCall_2_0; }
		
		/**
		 * Gets the facets assignment 3.
		 *
		 * @return the facets assignment 3
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_3() { return cFacetsAssignment_3; }
		
		/**
		 * Gets the facets facet parser rule call 3 0.
		 *
		 * @return the facets facet parser rule call 3 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_3_0() { return cFacetsFacetParserRuleCall_3_0; }
		
		/**
		 * Gets the alternatives 4.
		 *
		 * @return the alternatives 4
		 */
		//(block=Block | ';')
		public Alternatives getAlternatives_4() { return cAlternatives_4; }
		
		/**
		 * Gets the block assignment 4 0.
		 *
		 * @return the block assignment 4 0
		 */
		//block=Block
		public Assignment getBlockAssignment_4_0() { return cBlockAssignment_4_0; }
		
		/**
		 * Gets the block block parser rule call 4 0 0.
		 *
		 * @return the block block parser rule call 4 0 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_4_0_0() { return cBlockBlockParserRuleCall_4_0_0; }
		
		/**
		 * Gets the semicolon keyword 4 1.
		 *
		 * @return the semicolon keyword 4 1
		 */
		//';'
		public Keyword getSemicolonKeyword_4_1() { return cSemicolonKeyword_4_1; }
	}
	
	/**
	 * The Class S_ExperimentElements.
	 */
	public class S_ExperimentElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Experiment");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key experiment key parser rule call 0 0. */
		private final RuleCall cKey_ExperimentKeyParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c first facet assignment 1. */
		private final Assignment cFirstFacetAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c first facet name keyword 1 0. */
		private final Keyword cFirstFacetNameKeyword_1_0 = (Keyword)cFirstFacetAssignment_1.eContents().get(0);
		
		/** The c name assignment 2. */
		private final Assignment cNameAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c name alternatives 2 0. */
		private final Alternatives cNameAlternatives_2_0 = (Alternatives)cNameAssignment_2.eContents().get(0);
		
		/** The c name valid ID parser rule call 2 0 0. */
		private final RuleCall cNameValid_IDParserRuleCall_2_0_0 = (RuleCall)cNameAlternatives_2_0.eContents().get(0);
		
		/** The c name STRING terminal rule call 2 0 1. */
		private final RuleCall cNameSTRINGTerminalRuleCall_2_0_1 = (RuleCall)cNameAlternatives_2_0.eContents().get(1);
		
		/** The c facets assignment 3. */
		private final Assignment cFacetsAssignment_3 = (Assignment)cGroup.eContents().get(3);
		
		/** The c facets facet parser rule call 3 0. */
		private final RuleCall cFacetsFacetParserRuleCall_3_0 = (RuleCall)cFacetsAssignment_3.eContents().get(0);
		
		/** The c alternatives 4. */
		private final Alternatives cAlternatives_4 = (Alternatives)cGroup.eContents().get(4);
		
		/** The c block assignment 4 0. */
		private final Assignment cBlockAssignment_4_0 = (Assignment)cAlternatives_4.eContents().get(0);
		
		/** The c block block parser rule call 4 0 0. */
		private final RuleCall cBlockBlockParserRuleCall_4_0_0 = (RuleCall)cBlockAssignment_4_0.eContents().get(0);
		
		/** The c semicolon keyword 4 1. */
		private final Keyword cSemicolonKeyword_4_1 = (Keyword)cAlternatives_4.eContents().get(1);
		
		//S_Experiment :
		//    key=_ExperimentKey (firstFacet="name:")? name=(Valid_ID | STRING) (facets+=Facet)* (block=Block | ';');
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key=_ExperimentKey (firstFacet="name:")? name=(Valid_ID | STRING) (facets+=Facet)* (block=Block | ';')
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key=_ExperimentKey
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key experiment key parser rule call 0 0.
		 *
		 * @return the key experiment key parser rule call 0 0
		 */
		//_ExperimentKey
		public RuleCall getKey_ExperimentKeyParserRuleCall_0_0() { return cKey_ExperimentKeyParserRuleCall_0_0; }
		
		/**
		 * Gets the first facet assignment 1.
		 *
		 * @return the first facet assignment 1
		 */
		//(firstFacet="name:")?
		public Assignment getFirstFacetAssignment_1() { return cFirstFacetAssignment_1; }
		
		/**
		 * Gets the first facet name keyword 1 0.
		 *
		 * @return the first facet name keyword 1 0
		 */
		//"name:"
		public Keyword getFirstFacetNameKeyword_1_0() { return cFirstFacetNameKeyword_1_0; }
		
		/**
		 * Gets the name assignment 2.
		 *
		 * @return the name assignment 2
		 */
		//name=(Valid_ID | STRING)
		public Assignment getNameAssignment_2() { return cNameAssignment_2; }
		
		/**
		 * Gets the name alternatives 2 0.
		 *
		 * @return the name alternatives 2 0
		 */
		//(Valid_ID | STRING)
		public Alternatives getNameAlternatives_2_0() { return cNameAlternatives_2_0; }
		
		/**
		 * Gets the name valid ID parser rule call 2 0 0.
		 *
		 * @return the name valid ID parser rule call 2 0 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_2_0_0() { return cNameValid_IDParserRuleCall_2_0_0; }
		
		/**
		 * Gets the name STRING terminal rule call 2 0 1.
		 *
		 * @return the name STRING terminal rule call 2 0 1
		 */
		//STRING
		public RuleCall getNameSTRINGTerminalRuleCall_2_0_1() { return cNameSTRINGTerminalRuleCall_2_0_1; }
		
		/**
		 * Gets the facets assignment 3.
		 *
		 * @return the facets assignment 3
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_3() { return cFacetsAssignment_3; }
		
		/**
		 * Gets the facets facet parser rule call 3 0.
		 *
		 * @return the facets facet parser rule call 3 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_3_0() { return cFacetsFacetParserRuleCall_3_0; }
		
		/**
		 * Gets the alternatives 4.
		 *
		 * @return the alternatives 4
		 */
		//(block=Block | ';')
		public Alternatives getAlternatives_4() { return cAlternatives_4; }
		
		/**
		 * Gets the block assignment 4 0.
		 *
		 * @return the block assignment 4 0
		 */
		//block=Block
		public Assignment getBlockAssignment_4_0() { return cBlockAssignment_4_0; }
		
		/**
		 * Gets the block block parser rule call 4 0 0.
		 *
		 * @return the block block parser rule call 4 0 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_4_0_0() { return cBlockBlockParserRuleCall_4_0_0; }
		
		/**
		 * Gets the semicolon keyword 4 1.
		 *
		 * @return the semicolon keyword 4 1
		 */
		//';'
		public Keyword getSemicolonKeyword_4_1() { return cSemicolonKeyword_4_1; }
	}
	
	/**
	 * The Class StatementElements.
	 */
	public class StatementElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Statement");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c alternatives 0. */
		private final Alternatives cAlternatives_0 = (Alternatives)cAlternatives.eContents().get(0);
		
		/** The c S declaration parser rule call 0 0. */
		private final RuleCall cS_DeclarationParserRuleCall_0_0 = (RuleCall)cAlternatives_0.eContents().get(0);
		
		/** The c alternatives 0 1. */
		private final Alternatives cAlternatives_0_1 = (Alternatives)cAlternatives_0.eContents().get(1);
		
		/** The c S assignment parser rule call 0 1 0. */
		private final RuleCall cS_AssignmentParserRuleCall_0_1_0 = (RuleCall)cAlternatives_0_1.eContents().get(0);
		
		/** The c S 1 expr facets block or end parser rule call 0 1 1. */
		private final RuleCall cS_1Expr_Facets_BlockOrEndParserRuleCall_0_1_1 = (RuleCall)cAlternatives_0_1.eContents().get(1);
		
		/** The c S other parser rule call 0 1 2. */
		private final RuleCall cS_OtherParserRuleCall_0_1_2 = (RuleCall)cAlternatives_0_1.eContents().get(2);
		
		/** The c S do parser rule call 0 1 3. */
		private final RuleCall cS_DoParserRuleCall_0_1_3 = (RuleCall)cAlternatives_0_1.eContents().get(3);
		
		/** The c S return parser rule call 0 1 4. */
		private final RuleCall cS_ReturnParserRuleCall_0_1_4 = (RuleCall)cAlternatives_0_1.eContents().get(4);
		
		/** The c S solve parser rule call 0 1 5. */
		private final RuleCall cS_SolveParserRuleCall_0_1_5 = (RuleCall)cAlternatives_0_1.eContents().get(5);
		
		/** The c S if parser rule call 0 1 6. */
		private final RuleCall cS_IfParserRuleCall_0_1_6 = (RuleCall)cAlternatives_0_1.eContents().get(6);
		
		/** The c S try parser rule call 0 1 7. */
		private final RuleCall cS_TryParserRuleCall_0_1_7 = (RuleCall)cAlternatives_0_1.eContents().get(7);
		
		/** The c S equations parser rule call 0 1 8. */
		private final RuleCall cS_EquationsParserRuleCall_0_1_8 = (RuleCall)cAlternatives_0_1.eContents().get(8);
		
		/** The c S display parser rule call 1. */
		private final RuleCall cS_DisplayParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		///**
		// * Statements
		// */
		//Statement:
		//    (=> S_Declaration |
		//    ((=> S_Assignment | S_1Expr_Facets_BlockOrEnd | S_Other | S_Do | S_Return | S_Solve | S_If | S_Try | S_Equations))) | S_Display ;
		@Override public ParserRule getRule() { return rule; }
		
		//(=> S_Declaration |
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//((=> S_Assignment | S_1Expr_Facets_BlockOrEnd | S_Other | S_Do | S_Return | S_Solve | S_If | S_Try | S_Equations))) | S_Display
		public Alternatives getAlternatives() { return cAlternatives; }
		
		//(=> S_Declaration |
		/**
		 * Gets the alternatives 0.
		 *
		 * @return the alternatives 0
		 */
		//((=> S_Assignment | S_1Expr_Facets_BlockOrEnd | S_Other | S_Do | S_Return | S_Solve | S_If | S_Try | S_Equations)))
		public Alternatives getAlternatives_0() { return cAlternatives_0; }
		
		/**
		 * Gets the s declaration parser rule call 0 0.
		 *
		 * @return the s declaration parser rule call 0 0
		 */
		//=> S_Declaration
		public RuleCall getS_DeclarationParserRuleCall_0_0() { return cS_DeclarationParserRuleCall_0_0; }
		
		/**
		 * Gets the alternatives 0 1.
		 *
		 * @return the alternatives 0 1
		 */
		//((=> S_Assignment | S_1Expr_Facets_BlockOrEnd | S_Other | S_Do | S_Return | S_Solve | S_If | S_Try | S_Equations))
		public Alternatives getAlternatives_0_1() { return cAlternatives_0_1; }
		
		/**
		 * Gets the s assignment parser rule call 0 1 0.
		 *
		 * @return the s assignment parser rule call 0 1 0
		 */
		//=> S_Assignment
		public RuleCall getS_AssignmentParserRuleCall_0_1_0() { return cS_AssignmentParserRuleCall_0_1_0; }
		
		/**
		 * Gets the s 1 expr facets block or end parser rule call 0 1 1.
		 *
		 * @return the s 1 expr facets block or end parser rule call 0 1 1
		 */
		//S_1Expr_Facets_BlockOrEnd
		public RuleCall getS_1Expr_Facets_BlockOrEndParserRuleCall_0_1_1() { return cS_1Expr_Facets_BlockOrEndParserRuleCall_0_1_1; }
		
		/**
		 * Gets the s other parser rule call 0 1 2.
		 *
		 * @return the s other parser rule call 0 1 2
		 */
		//S_Other
		public RuleCall getS_OtherParserRuleCall_0_1_2() { return cS_OtherParserRuleCall_0_1_2; }
		
		/**
		 * Gets the s do parser rule call 0 1 3.
		 *
		 * @return the s do parser rule call 0 1 3
		 */
		//S_Do
		public RuleCall getS_DoParserRuleCall_0_1_3() { return cS_DoParserRuleCall_0_1_3; }
		
		/**
		 * Gets the s return parser rule call 0 1 4.
		 *
		 * @return the s return parser rule call 0 1 4
		 */
		//S_Return
		public RuleCall getS_ReturnParserRuleCall_0_1_4() { return cS_ReturnParserRuleCall_0_1_4; }
		
		/**
		 * Gets the s solve parser rule call 0 1 5.
		 *
		 * @return the s solve parser rule call 0 1 5
		 */
		//S_Solve
		public RuleCall getS_SolveParserRuleCall_0_1_5() { return cS_SolveParserRuleCall_0_1_5; }
		
		/**
		 * Gets the s if parser rule call 0 1 6.
		 *
		 * @return the s if parser rule call 0 1 6
		 */
		//S_If
		public RuleCall getS_IfParserRuleCall_0_1_6() { return cS_IfParserRuleCall_0_1_6; }
		
		/**
		 * Gets the s try parser rule call 0 1 7.
		 *
		 * @return the s try parser rule call 0 1 7
		 */
		//S_Try
		public RuleCall getS_TryParserRuleCall_0_1_7() { return cS_TryParserRuleCall_0_1_7; }
		
		/**
		 * Gets the s equations parser rule call 0 1 8.
		 *
		 * @return the s equations parser rule call 0 1 8
		 */
		//S_Equations
		public RuleCall getS_EquationsParserRuleCall_0_1_8() { return cS_EquationsParserRuleCall_0_1_8; }
		
		/**
		 * Gets the s display parser rule call 1.
		 *
		 * @return the s display parser rule call 1
		 */
		//S_Display
		public RuleCall getS_DisplayParserRuleCall_1() { return cS_DisplayParserRuleCall_1; }
	}
	
	/**
	 * The Class S_1Expr_Facets_BlockOrEndElements.
	 */
	public class S_1Expr_Facets_BlockOrEndElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_1Expr_Facets_BlockOrEnd");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key 1 expr facets block or end key parser rule call 0 0. */
		private final RuleCall cKey_1Expr_Facets_BlockOrEnd_KeyParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c first facet assignment 1. */
		private final Assignment cFirstFacetAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c first facet first facet key parser rule call 1 0. */
		private final RuleCall cFirstFacetFirstFacetKeyParserRuleCall_1_0 = (RuleCall)cFirstFacetAssignment_1.eContents().get(0);
		
		/** The c expr assignment 2. */
		private final Assignment cExprAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c expr expression parser rule call 2 0. */
		private final RuleCall cExprExpressionParserRuleCall_2_0 = (RuleCall)cExprAssignment_2.eContents().get(0);
		
		/** The c facets assignment 3. */
		private final Assignment cFacetsAssignment_3 = (Assignment)cGroup.eContents().get(3);
		
		/** The c facets facet parser rule call 3 0. */
		private final RuleCall cFacetsFacetParserRuleCall_3_0 = (RuleCall)cFacetsAssignment_3.eContents().get(0);
		
		/** The c alternatives 4. */
		private final Alternatives cAlternatives_4 = (Alternatives)cGroup.eContents().get(4);
		
		/** The c block assignment 4 0. */
		private final Assignment cBlockAssignment_4_0 = (Assignment)cAlternatives_4.eContents().get(0);
		
		/** The c block block parser rule call 4 0 0. */
		private final RuleCall cBlockBlockParserRuleCall_4_0_0 = (RuleCall)cBlockAssignment_4_0.eContents().get(0);
		
		/** The c semicolon keyword 4 1. */
		private final Keyword cSemicolonKeyword_4_1 = (Keyword)cAlternatives_4.eContents().get(1);
		
		//S_1Expr_Facets_BlockOrEnd returns Statement:
		//    key=_1Expr_Facets_BlockOrEnd_Key (firstFacet=FirstFacetKey)? (expr=Expression) (facets+=Facet)* (block=Block | ";");
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key=_1Expr_Facets_BlockOrEnd_Key (firstFacet=FirstFacetKey)? (expr=Expression) (facets+=Facet)* (block=Block | ";")
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key=_1Expr_Facets_BlockOrEnd_Key
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key 1 expr facets block or end key parser rule call 0 0.
		 *
		 * @return the key 1 expr facets block or end key parser rule call 0 0
		 */
		//_1Expr_Facets_BlockOrEnd_Key
		public RuleCall getKey_1Expr_Facets_BlockOrEnd_KeyParserRuleCall_0_0() { return cKey_1Expr_Facets_BlockOrEnd_KeyParserRuleCall_0_0; }
		
		/**
		 * Gets the first facet assignment 1.
		 *
		 * @return the first facet assignment 1
		 */
		//(firstFacet=FirstFacetKey)?
		public Assignment getFirstFacetAssignment_1() { return cFirstFacetAssignment_1; }
		
		/**
		 * Gets the first facet first facet key parser rule call 1 0.
		 *
		 * @return the first facet first facet key parser rule call 1 0
		 */
		//FirstFacetKey
		public RuleCall getFirstFacetFirstFacetKeyParserRuleCall_1_0() { return cFirstFacetFirstFacetKeyParserRuleCall_1_0; }
		
		/**
		 * Gets the expr assignment 2.
		 *
		 * @return the expr assignment 2
		 */
		//(expr=Expression)
		public Assignment getExprAssignment_2() { return cExprAssignment_2; }
		
		/**
		 * Gets the expr expression parser rule call 2 0.
		 *
		 * @return the expr expression parser rule call 2 0
		 */
		//Expression
		public RuleCall getExprExpressionParserRuleCall_2_0() { return cExprExpressionParserRuleCall_2_0; }
		
		/**
		 * Gets the facets assignment 3.
		 *
		 * @return the facets assignment 3
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_3() { return cFacetsAssignment_3; }
		
		/**
		 * Gets the facets facet parser rule call 3 0.
		 *
		 * @return the facets facet parser rule call 3 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_3_0() { return cFacetsFacetParserRuleCall_3_0; }
		
		/**
		 * Gets the alternatives 4.
		 *
		 * @return the alternatives 4
		 */
		//(block=Block | ";")
		public Alternatives getAlternatives_4() { return cAlternatives_4; }
		
		/**
		 * Gets the block assignment 4 0.
		 *
		 * @return the block assignment 4 0
		 */
		//block=Block
		public Assignment getBlockAssignment_4_0() { return cBlockAssignment_4_0; }
		
		/**
		 * Gets the block block parser rule call 4 0 0.
		 *
		 * @return the block block parser rule call 4 0 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_4_0_0() { return cBlockBlockParserRuleCall_4_0_0; }
		
		/**
		 * Gets the semicolon keyword 4 1.
		 *
		 * @return the semicolon keyword 4 1
		 */
		//";"
		public Keyword getSemicolonKeyword_4_1() { return cSemicolonKeyword_4_1; }
	}
	
	/**
	 * The Class S_DoElements.
	 */
	public class S_DoElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Do");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key do key parser rule call 0 0. */
		private final RuleCall cKey_DoKeyParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c first facet assignment 1. */
		private final Assignment cFirstFacetAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c first facet action keyword 1 0. */
		private final Keyword cFirstFacetActionKeyword_1_0 = (Keyword)cFirstFacetAssignment_1.eContents().get(0);
		
		/** The c expr assignment 2. */
		private final Assignment cExprAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c expr abstract ref parser rule call 2 0. */
		private final RuleCall cExprAbstractRefParserRuleCall_2_0 = (RuleCall)cExprAssignment_2.eContents().get(0);
		
		/** The c facets assignment 3. */
		private final Assignment cFacetsAssignment_3 = (Assignment)cGroup.eContents().get(3);
		
		/** The c facets facet parser rule call 3 0. */
		private final RuleCall cFacetsFacetParserRuleCall_3_0 = (RuleCall)cFacetsAssignment_3.eContents().get(0);
		
		/** The c alternatives 4. */
		private final Alternatives cAlternatives_4 = (Alternatives)cGroup.eContents().get(4);
		
		/** The c block assignment 4 0. */
		private final Assignment cBlockAssignment_4_0 = (Assignment)cAlternatives_4.eContents().get(0);
		
		/** The c block block parser rule call 4 0 0. */
		private final RuleCall cBlockBlockParserRuleCall_4_0_0 = (RuleCall)cBlockAssignment_4_0.eContents().get(0);
		
		/** The c semicolon keyword 4 1. */
		private final Keyword cSemicolonKeyword_4_1 = (Keyword)cAlternatives_4.eContents().get(1);
		
		//S_Do:
		//    key=_DoKey (firstFacet="action:")? expr=AbstractRef (facets+=Facet)* (block=Block | ';');
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key=_DoKey (firstFacet="action:")? expr=AbstractRef (facets+=Facet)* (block=Block | ';')
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key=_DoKey
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key do key parser rule call 0 0.
		 *
		 * @return the key do key parser rule call 0 0
		 */
		//_DoKey
		public RuleCall getKey_DoKeyParserRuleCall_0_0() { return cKey_DoKeyParserRuleCall_0_0; }
		
		/**
		 * Gets the first facet assignment 1.
		 *
		 * @return the first facet assignment 1
		 */
		//(firstFacet="action:")?
		public Assignment getFirstFacetAssignment_1() { return cFirstFacetAssignment_1; }
		
		/**
		 * Gets the first facet action keyword 1 0.
		 *
		 * @return the first facet action keyword 1 0
		 */
		//"action:"
		public Keyword getFirstFacetActionKeyword_1_0() { return cFirstFacetActionKeyword_1_0; }
		
		/**
		 * Gets the expr assignment 2.
		 *
		 * @return the expr assignment 2
		 */
		//expr=AbstractRef
		public Assignment getExprAssignment_2() { return cExprAssignment_2; }
		
		/**
		 * Gets the expr abstract ref parser rule call 2 0.
		 *
		 * @return the expr abstract ref parser rule call 2 0
		 */
		//AbstractRef
		public RuleCall getExprAbstractRefParserRuleCall_2_0() { return cExprAbstractRefParserRuleCall_2_0; }
		
		/**
		 * Gets the facets assignment 3.
		 *
		 * @return the facets assignment 3
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_3() { return cFacetsAssignment_3; }
		
		/**
		 * Gets the facets facet parser rule call 3 0.
		 *
		 * @return the facets facet parser rule call 3 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_3_0() { return cFacetsFacetParserRuleCall_3_0; }
		
		/**
		 * Gets the alternatives 4.
		 *
		 * @return the alternatives 4
		 */
		//(block=Block | ';')
		public Alternatives getAlternatives_4() { return cAlternatives_4; }
		
		/**
		 * Gets the block assignment 4 0.
		 *
		 * @return the block assignment 4 0
		 */
		//block=Block
		public Assignment getBlockAssignment_4_0() { return cBlockAssignment_4_0; }
		
		/**
		 * Gets the block block parser rule call 4 0 0.
		 *
		 * @return the block block parser rule call 4 0 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_4_0_0() { return cBlockBlockParserRuleCall_4_0_0; }
		
		/**
		 * Gets the semicolon keyword 4 1.
		 *
		 * @return the semicolon keyword 4 1
		 */
		//';'
		public Keyword getSemicolonKeyword_4_1() { return cSemicolonKeyword_4_1; }
	}
	
	/**
	 * The Class S_LoopElements.
	 */
	public class S_LoopElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Loop");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key loop keyword 0 0. */
		private final Keyword cKeyLoopKeyword_0_0 = (Keyword)cKeyAssignment_0.eContents().get(0);
		
		/** The c name assignment 1. */
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c name ID terminal rule call 1 0. */
		private final RuleCall cNameIDTerminalRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		
		/** The c facets assignment 2. */
		private final Assignment cFacetsAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c facets facet parser rule call 2 0. */
		private final RuleCall cFacetsFacetParserRuleCall_2_0 = (RuleCall)cFacetsAssignment_2.eContents().get(0);
		
		/** The c block assignment 3. */
		private final Assignment cBlockAssignment_3 = (Assignment)cGroup.eContents().get(3);
		
		/** The c block block parser rule call 3 0. */
		private final RuleCall cBlockBlockParserRuleCall_3_0 = (RuleCall)cBlockAssignment_3.eContents().get(0);
		
		//S_Loop:
		//    key="loop" (name=ID)? (facets+=Facet)* block=Block;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key="loop" (name=ID)? (facets+=Facet)* block=Block
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key="loop"
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key loop keyword 0 0.
		 *
		 * @return the key loop keyword 0 0
		 */
		//"loop"
		public Keyword getKeyLoopKeyword_0_0() { return cKeyLoopKeyword_0_0; }
		
		/**
		 * Gets the name assignment 1.
		 *
		 * @return the name assignment 1
		 */
		//(name=ID)?
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		/**
		 * Gets the name ID terminal rule call 1 0.
		 *
		 * @return the name ID terminal rule call 1 0
		 */
		//ID
		public RuleCall getNameIDTerminalRuleCall_1_0() { return cNameIDTerminalRuleCall_1_0; }
		
		/**
		 * Gets the facets assignment 2.
		 *
		 * @return the facets assignment 2
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_2() { return cFacetsAssignment_2; }
		
		/**
		 * Gets the facets facet parser rule call 2 0.
		 *
		 * @return the facets facet parser rule call 2 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_2_0() { return cFacetsFacetParserRuleCall_2_0; }
		
		/**
		 * Gets the block assignment 3.
		 *
		 * @return the block assignment 3
		 */
		//block=Block
		public Assignment getBlockAssignment_3() { return cBlockAssignment_3; }
		
		/**
		 * Gets the block block parser rule call 3 0.
		 *
		 * @return the block block parser rule call 3 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_3_0() { return cBlockBlockParserRuleCall_3_0; }
	}
	
	/**
	 * The Class S_IfElements.
	 */
	public class S_IfElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_If");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key if keyword 0 0. */
		private final Keyword cKeyIfKeyword_0_0 = (Keyword)cKeyAssignment_0.eContents().get(0);
		
		/** The c first facet assignment 1. */
		private final Assignment cFirstFacetAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c first facet condition keyword 1 0. */
		private final Keyword cFirstFacetConditionKeyword_1_0 = (Keyword)cFirstFacetAssignment_1.eContents().get(0);
		
		/** The c expr assignment 2. */
		private final Assignment cExprAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c expr expression parser rule call 2 0. */
		private final RuleCall cExprExpressionParserRuleCall_2_0 = (RuleCall)cExprAssignment_2.eContents().get(0);
		
		/** The c block assignment 3. */
		private final Assignment cBlockAssignment_3 = (Assignment)cGroup.eContents().get(3);
		
		/** The c block block parser rule call 3 0. */
		private final RuleCall cBlockBlockParserRuleCall_3_0 = (RuleCall)cBlockAssignment_3.eContents().get(0);
		
		/** The c group 4. */
		private final Group cGroup_4 = (Group)cGroup.eContents().get(4);
		
		/** The c else keyword 4 0. */
		private final Keyword cElseKeyword_4_0 = (Keyword)cGroup_4.eContents().get(0);
		
		/** The c else assignment 4 1. */
		private final Assignment cElseAssignment_4_1 = (Assignment)cGroup_4.eContents().get(1);
		
		/** The c else alternatives 4 1 0. */
		private final Alternatives cElseAlternatives_4_1_0 = (Alternatives)cElseAssignment_4_1.eContents().get(0);
		
		/** The c else S if parser rule call 4 1 0 0. */
		private final RuleCall cElseS_IfParserRuleCall_4_1_0_0 = (RuleCall)cElseAlternatives_4_1_0.eContents().get(0);
		
		/** The c else block parser rule call 4 1 0 1. */
		private final RuleCall cElseBlockParserRuleCall_4_1_0_1 = (RuleCall)cElseAlternatives_4_1_0.eContents().get(1);
		
		//S_If:
		//    key='if' (firstFacet="condition:")? expr=Expression block=Block (-> 'else' else=(S_If | Block))?;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key='if' (firstFacet="condition:")? expr=Expression block=Block (-> 'else' else=(S_If | Block))?
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key='if'
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key if keyword 0 0.
		 *
		 * @return the key if keyword 0 0
		 */
		//'if'
		public Keyword getKeyIfKeyword_0_0() { return cKeyIfKeyword_0_0; }
		
		/**
		 * Gets the first facet assignment 1.
		 *
		 * @return the first facet assignment 1
		 */
		//(firstFacet="condition:")?
		public Assignment getFirstFacetAssignment_1() { return cFirstFacetAssignment_1; }
		
		/**
		 * Gets the first facet condition keyword 1 0.
		 *
		 * @return the first facet condition keyword 1 0
		 */
		//"condition:"
		public Keyword getFirstFacetConditionKeyword_1_0() { return cFirstFacetConditionKeyword_1_0; }
		
		/**
		 * Gets the expr assignment 2.
		 *
		 * @return the expr assignment 2
		 */
		//expr=Expression
		public Assignment getExprAssignment_2() { return cExprAssignment_2; }
		
		/**
		 * Gets the expr expression parser rule call 2 0.
		 *
		 * @return the expr expression parser rule call 2 0
		 */
		//Expression
		public RuleCall getExprExpressionParserRuleCall_2_0() { return cExprExpressionParserRuleCall_2_0; }
		
		/**
		 * Gets the block assignment 3.
		 *
		 * @return the block assignment 3
		 */
		//block=Block
		public Assignment getBlockAssignment_3() { return cBlockAssignment_3; }
		
		/**
		 * Gets the block block parser rule call 3 0.
		 *
		 * @return the block block parser rule call 3 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_3_0() { return cBlockBlockParserRuleCall_3_0; }
		
		/**
		 * Gets the group 4.
		 *
		 * @return the group 4
		 */
		//(-> 'else' else=(S_If | Block))?
		public Group getGroup_4() { return cGroup_4; }
		
		/**
		 * Gets the else keyword 4 0.
		 *
		 * @return the else keyword 4 0
		 */
		//-> 'else'
		public Keyword getElseKeyword_4_0() { return cElseKeyword_4_0; }
		
		/**
		 * Gets the else assignment 4 1.
		 *
		 * @return the else assignment 4 1
		 */
		//else=(S_If | Block)
		public Assignment getElseAssignment_4_1() { return cElseAssignment_4_1; }
		
		/**
		 * Gets the else alternatives 4 1 0.
		 *
		 * @return the else alternatives 4 1 0
		 */
		//(S_If | Block)
		public Alternatives getElseAlternatives_4_1_0() { return cElseAlternatives_4_1_0; }
		
		/**
		 * Gets the else S if parser rule call 4 1 0 0.
		 *
		 * @return the else S if parser rule call 4 1 0 0
		 */
		//S_If
		public RuleCall getElseS_IfParserRuleCall_4_1_0_0() { return cElseS_IfParserRuleCall_4_1_0_0; }
		
		/**
		 * Gets the else block parser rule call 4 1 0 1.
		 *
		 * @return the else block parser rule call 4 1 0 1
		 */
		//Block
		public RuleCall getElseBlockParserRuleCall_4_1_0_1() { return cElseBlockParserRuleCall_4_1_0_1; }
	}
	
	/**
	 * The Class S_TryElements.
	 */
	public class S_TryElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Try");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key try keyword 0 0. */
		private final Keyword cKeyTryKeyword_0_0 = (Keyword)cKeyAssignment_0.eContents().get(0);
		
		/** The c block assignment 1. */
		private final Assignment cBlockAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c block block parser rule call 1 0. */
		private final RuleCall cBlockBlockParserRuleCall_1_0 = (RuleCall)cBlockAssignment_1.eContents().get(0);
		
		/** The c group 2. */
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		
		/** The c catch keyword 2 0. */
		private final Keyword cCatchKeyword_2_0 = (Keyword)cGroup_2.eContents().get(0);
		
		/** The c catch assignment 2 1. */
		private final Assignment cCatchAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		
		/** The c catch block parser rule call 2 1 0. */
		private final RuleCall cCatchBlockParserRuleCall_2_1_0 = (RuleCall)cCatchAssignment_2_1.eContents().get(0);
		
		//S_Try:
		//    key='try' block=Block (-> 'catch' catch=Block) ?
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key='try' block=Block (-> 'catch' catch=Block) ?
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key='try'
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key try keyword 0 0.
		 *
		 * @return the key try keyword 0 0
		 */
		//'try'
		public Keyword getKeyTryKeyword_0_0() { return cKeyTryKeyword_0_0; }
		
		/**
		 * Gets the block assignment 1.
		 *
		 * @return the block assignment 1
		 */
		//block=Block
		public Assignment getBlockAssignment_1() { return cBlockAssignment_1; }
		
		/**
		 * Gets the block block parser rule call 1 0.
		 *
		 * @return the block block parser rule call 1 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_1_0() { return cBlockBlockParserRuleCall_1_0; }
		
		/**
		 * Gets the group 2.
		 *
		 * @return the group 2
		 */
		//(-> 'catch' catch=Block) ?
		public Group getGroup_2() { return cGroup_2; }
		
		/**
		 * Gets the catch keyword 2 0.
		 *
		 * @return the catch keyword 2 0
		 */
		//-> 'catch'
		public Keyword getCatchKeyword_2_0() { return cCatchKeyword_2_0; }
		
		/**
		 * Gets the catch assignment 2 1.
		 *
		 * @return the catch assignment 2 1
		 */
		//catch=Block
		public Assignment getCatchAssignment_2_1() { return cCatchAssignment_2_1; }
		
		/**
		 * Gets the catch block parser rule call 2 1 0.
		 *
		 * @return the catch block parser rule call 2 1 0
		 */
		//Block
		public RuleCall getCatchBlockParserRuleCall_2_1_0() { return cCatchBlockParserRuleCall_2_1_0; }
	}
	
	/**
	 * The Class S_OtherElements.
	 */
	public class S_OtherElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Other");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key ID terminal rule call 0 0. */
		private final RuleCall cKeyIDTerminalRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c facets assignment 1. */
		private final Assignment cFacetsAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c facets facet parser rule call 1 0. */
		private final RuleCall cFacetsFacetParserRuleCall_1_0 = (RuleCall)cFacetsAssignment_1.eContents().get(0);
		
		/** The c alternatives 2. */
		private final Alternatives cAlternatives_2 = (Alternatives)cGroup.eContents().get(2);
		
		/** The c block assignment 2 0. */
		private final Assignment cBlockAssignment_2_0 = (Assignment)cAlternatives_2.eContents().get(0);
		
		/** The c block block parser rule call 2 0 0. */
		private final RuleCall cBlockBlockParserRuleCall_2_0_0 = (RuleCall)cBlockAssignment_2_0.eContents().get(0);
		
		/** The c semicolon keyword 2 1. */
		private final Keyword cSemicolonKeyword_2_1 = (Keyword)cAlternatives_2.eContents().get(1);
		
		//S_Other:
		//    key=ID (facets+=Facet)* (block=Block | ';');
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key=ID (facets+=Facet)* (block=Block | ';')
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key=ID
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key ID terminal rule call 0 0.
		 *
		 * @return the key ID terminal rule call 0 0
		 */
		//ID
		public RuleCall getKeyIDTerminalRuleCall_0_0() { return cKeyIDTerminalRuleCall_0_0; }
		
		/**
		 * Gets the facets assignment 1.
		 *
		 * @return the facets assignment 1
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_1() { return cFacetsAssignment_1; }
		
		/**
		 * Gets the facets facet parser rule call 1 0.
		 *
		 * @return the facets facet parser rule call 1 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_1_0() { return cFacetsFacetParserRuleCall_1_0; }
		
		/**
		 * Gets the alternatives 2.
		 *
		 * @return the alternatives 2
		 */
		//(block=Block | ';')
		public Alternatives getAlternatives_2() { return cAlternatives_2; }
		
		/**
		 * Gets the block assignment 2 0.
		 *
		 * @return the block assignment 2 0
		 */
		//block=Block
		public Assignment getBlockAssignment_2_0() { return cBlockAssignment_2_0; }
		
		/**
		 * Gets the block block parser rule call 2 0 0.
		 *
		 * @return the block block parser rule call 2 0 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_2_0_0() { return cBlockBlockParserRuleCall_2_0_0; }
		
		/**
		 * Gets the semicolon keyword 2 1.
		 *
		 * @return the semicolon keyword 2 1
		 */
		//';'
		public Keyword getSemicolonKeyword_2_1() { return cSemicolonKeyword_2_1; }
	}
	
	/**
	 * The Class S_ReturnElements.
	 */
	public class S_ReturnElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Return");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key return keyword 0 0. */
		private final Keyword cKeyReturnKeyword_0_0 = (Keyword)cKeyAssignment_0.eContents().get(0);
		
		/** The c first facet assignment 1. */
		private final Assignment cFirstFacetAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c first facet value keyword 1 0. */
		private final Keyword cFirstFacetValueKeyword_1_0 = (Keyword)cFirstFacetAssignment_1.eContents().get(0);
		
		/** The c expr assignment 2. */
		private final Assignment cExprAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c expr expression parser rule call 2 0. */
		private final RuleCall cExprExpressionParserRuleCall_2_0 = (RuleCall)cExprAssignment_2.eContents().get(0);
		
		/** The c semicolon keyword 3. */
		private final Keyword cSemicolonKeyword_3 = (Keyword)cGroup.eContents().get(3);
		
		//S_Return:
		//    key='return' (firstFacet="value:")? expr=Expression? ';';
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key='return' (firstFacet="value:")? expr=Expression? ';'
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key='return'
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key return keyword 0 0.
		 *
		 * @return the key return keyword 0 0
		 */
		//'return'
		public Keyword getKeyReturnKeyword_0_0() { return cKeyReturnKeyword_0_0; }
		
		/**
		 * Gets the first facet assignment 1.
		 *
		 * @return the first facet assignment 1
		 */
		//(firstFacet="value:")?
		public Assignment getFirstFacetAssignment_1() { return cFirstFacetAssignment_1; }
		
		/**
		 * Gets the first facet value keyword 1 0.
		 *
		 * @return the first facet value keyword 1 0
		 */
		//"value:"
		public Keyword getFirstFacetValueKeyword_1_0() { return cFirstFacetValueKeyword_1_0; }
		
		/**
		 * Gets the expr assignment 2.
		 *
		 * @return the expr assignment 2
		 */
		//expr=Expression?
		public Assignment getExprAssignment_2() { return cExprAssignment_2; }
		
		/**
		 * Gets the expr expression parser rule call 2 0.
		 *
		 * @return the expr expression parser rule call 2 0
		 */
		//Expression
		public RuleCall getExprExpressionParserRuleCall_2_0() { return cExprExpressionParserRuleCall_2_0; }
		
		/**
		 * Gets the semicolon keyword 3.
		 *
		 * @return the semicolon keyword 3
		 */
		//';'
		public Keyword getSemicolonKeyword_3() { return cSemicolonKeyword_3; }
	}
	
	/**
	 * The Class S_DeclarationElements.
	 */
	public class S_DeclarationElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Declaration");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c S definition parser rule call 0. */
		private final RuleCall cS_DefinitionParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c S species parser rule call 1. */
		private final RuleCall cS_SpeciesParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		/** The c S reflex parser rule call 2. */
		private final RuleCall cS_ReflexParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		/** The c S action parser rule call 3. */
		private final RuleCall cS_ActionParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		
		/** The c S var parser rule call 4. */
		private final RuleCall cS_VarParserRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		
		/** The c S loop parser rule call 5. */
		private final RuleCall cS_LoopParserRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		
		//    /*
		// * DECLARATIONS
		// */
		//S_Declaration:
		//    ->S_Definition | S_Species | S_Reflex | S_Action | S_Var | S_Loop ;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//->S_Definition | S_Species | S_Reflex | S_Action | S_Var | S_Loop
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the s definition parser rule call 0.
		 *
		 * @return the s definition parser rule call 0
		 */
		//->S_Definition
		public RuleCall getS_DefinitionParserRuleCall_0() { return cS_DefinitionParserRuleCall_0; }
		
		/**
		 * Gets the s species parser rule call 1.
		 *
		 * @return the s species parser rule call 1
		 */
		//S_Species
		public RuleCall getS_SpeciesParserRuleCall_1() { return cS_SpeciesParserRuleCall_1; }
		
		/**
		 * Gets the s reflex parser rule call 2.
		 *
		 * @return the s reflex parser rule call 2
		 */
		//S_Reflex
		public RuleCall getS_ReflexParserRuleCall_2() { return cS_ReflexParserRuleCall_2; }
		
		/**
		 * Gets the s action parser rule call 3.
		 *
		 * @return the s action parser rule call 3
		 */
		//S_Action
		public RuleCall getS_ActionParserRuleCall_3() { return cS_ActionParserRuleCall_3; }
		
		/**
		 * Gets the s var parser rule call 4.
		 *
		 * @return the s var parser rule call 4
		 */
		//S_Var
		public RuleCall getS_VarParserRuleCall_4() { return cS_VarParserRuleCall_4; }
		
		/**
		 * Gets the s loop parser rule call 5.
		 *
		 * @return the s loop parser rule call 5
		 */
		//S_Loop
		public RuleCall getS_LoopParserRuleCall_5() { return cS_LoopParserRuleCall_5; }
	}
	
	/**
	 * The Class S_ReflexElements.
	 */
	public class S_ReflexElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Reflex");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key reflex key parser rule call 0 0. */
		private final RuleCall cKey_ReflexKeyParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c first facet assignment 1 0. */
		private final Assignment cFirstFacetAssignment_1_0 = (Assignment)cGroup_1.eContents().get(0);
		
		/** The c first facet name keyword 1 0 0. */
		private final Keyword cFirstFacetNameKeyword_1_0_0 = (Keyword)cFirstFacetAssignment_1_0.eContents().get(0);
		
		/** The c name assignment 1 1. */
		private final Assignment cNameAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		
		/** The c name valid ID parser rule call 1 1 0. */
		private final RuleCall cNameValid_IDParserRuleCall_1_1_0 = (RuleCall)cNameAssignment_1_1.eContents().get(0);
		
		/** The c group 2. */
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		
		/** The c when keyword 2 0. */
		private final Keyword cWhenKeyword_2_0 = (Keyword)cGroup_2.eContents().get(0);
		
		/** The c colon keyword 2 1. */
		private final Keyword cColonKeyword_2_1 = (Keyword)cGroup_2.eContents().get(1);
		
		/** The c expr assignment 2 2. */
		private final Assignment cExprAssignment_2_2 = (Assignment)cGroup_2.eContents().get(2);
		
		/** The c expr expression parser rule call 2 2 0. */
		private final RuleCall cExprExpressionParserRuleCall_2_2_0 = (RuleCall)cExprAssignment_2_2.eContents().get(0);
		
		/** The c block assignment 3. */
		private final Assignment cBlockAssignment_3 = (Assignment)cGroup.eContents().get(3);
		
		/** The c block block parser rule call 3 0. */
		private final RuleCall cBlockBlockParserRuleCall_3_0 = (RuleCall)cBlockAssignment_3.eContents().get(0);
		
		//S_Reflex:
		//    key=_ReflexKey ((firstFacet="name:")? name=Valid_ID)? ("when"":" expr=Expression)? block=Block;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key=_ReflexKey ((firstFacet="name:")? name=Valid_ID)? ("when"":" expr=Expression)? block=Block
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key=_ReflexKey
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key reflex key parser rule call 0 0.
		 *
		 * @return the key reflex key parser rule call 0 0
		 */
		//_ReflexKey
		public RuleCall getKey_ReflexKeyParserRuleCall_0_0() { return cKey_ReflexKeyParserRuleCall_0_0; }
		
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//((firstFacet="name:")? name=Valid_ID)?
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the first facet assignment 1 0.
		 *
		 * @return the first facet assignment 1 0
		 */
		//(firstFacet="name:")?
		public Assignment getFirstFacetAssignment_1_0() { return cFirstFacetAssignment_1_0; }
		
		/**
		 * Gets the first facet name keyword 1 0 0.
		 *
		 * @return the first facet name keyword 1 0 0
		 */
		//"name:"
		public Keyword getFirstFacetNameKeyword_1_0_0() { return cFirstFacetNameKeyword_1_0_0; }
		
		/**
		 * Gets the name assignment 1 1.
		 *
		 * @return the name assignment 1 1
		 */
		//name=Valid_ID
		public Assignment getNameAssignment_1_1() { return cNameAssignment_1_1; }
		
		/**
		 * Gets the name valid ID parser rule call 1 1 0.
		 *
		 * @return the name valid ID parser rule call 1 1 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_1_1_0() { return cNameValid_IDParserRuleCall_1_1_0; }
		
		/**
		 * Gets the group 2.
		 *
		 * @return the group 2
		 */
		//("when"":" expr=Expression)?
		public Group getGroup_2() { return cGroup_2; }
		
		/**
		 * Gets the when keyword 2 0.
		 *
		 * @return the when keyword 2 0
		 */
		//"when"
		public Keyword getWhenKeyword_2_0() { return cWhenKeyword_2_0; }
		
		/**
		 * Gets the colon keyword 2 1.
		 *
		 * @return the colon keyword 2 1
		 */
		//":"
		public Keyword getColonKeyword_2_1() { return cColonKeyword_2_1; }
		
		/**
		 * Gets the expr assignment 2 2.
		 *
		 * @return the expr assignment 2 2
		 */
		//expr=Expression
		public Assignment getExprAssignment_2_2() { return cExprAssignment_2_2; }
		
		/**
		 * Gets the expr expression parser rule call 2 2 0.
		 *
		 * @return the expr expression parser rule call 2 2 0
		 */
		//Expression
		public RuleCall getExprExpressionParserRuleCall_2_2_0() { return cExprExpressionParserRuleCall_2_2_0; }
		
		/**
		 * Gets the block assignment 3.
		 *
		 * @return the block assignment 3
		 */
		//block=Block
		public Assignment getBlockAssignment_3() { return cBlockAssignment_3; }
		
		/**
		 * Gets the block block parser rule call 3 0.
		 *
		 * @return the block block parser rule call 3 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_3_0() { return cBlockBlockParserRuleCall_3_0; }
	}
	
	/**
	 * The Class S_DefinitionElements.
	 */
	public class S_DefinitionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Definition");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c tkey assignment 0. */
		private final Assignment cTkeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c tkey type ref parser rule call 0 0. */
		private final RuleCall cTkeyTypeRefParserRuleCall_0_0 = (RuleCall)cTkeyAssignment_0.eContents().get(0);
		
		/** The c first facet assignment 1. */
		private final Assignment cFirstFacetAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c first facet name keyword 1 0. */
		private final Keyword cFirstFacetNameKeyword_1_0 = (Keyword)cFirstFacetAssignment_1.eContents().get(0);
		
		/** The c name assignment 2. */
		private final Assignment cNameAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c name alternatives 2 0. */
		private final Alternatives cNameAlternatives_2_0 = (Alternatives)cNameAssignment_2.eContents().get(0);
		
		/** The c name valid ID parser rule call 2 0 0. */
		private final RuleCall cNameValid_IDParserRuleCall_2_0_0 = (RuleCall)cNameAlternatives_2_0.eContents().get(0);
		
		/** The c name STRING terminal rule call 2 0 1. */
		private final RuleCall cNameSTRINGTerminalRuleCall_2_0_1 = (RuleCall)cNameAlternatives_2_0.eContents().get(1);
		
		/** The c group 3. */
		private final Group cGroup_3 = (Group)cGroup.eContents().get(3);
		
		/** The c left parenthesis keyword 3 0. */
		private final Keyword cLeftParenthesisKeyword_3_0 = (Keyword)cGroup_3.eContents().get(0);
		
		/** The c args assignment 3 1. */
		private final Assignment cArgsAssignment_3_1 = (Assignment)cGroup_3.eContents().get(1);
		
		/** The c args action arguments parser rule call 3 1 0. */
		private final RuleCall cArgsActionArgumentsParserRuleCall_3_1_0 = (RuleCall)cArgsAssignment_3_1.eContents().get(0);
		
		/** The c right parenthesis keyword 3 2. */
		private final Keyword cRightParenthesisKeyword_3_2 = (Keyword)cGroup_3.eContents().get(2);
		
		/** The c facets assignment 4. */
		private final Assignment cFacetsAssignment_4 = (Assignment)cGroup.eContents().get(4);
		
		/** The c facets facet parser rule call 4 0. */
		private final RuleCall cFacetsFacetParserRuleCall_4_0 = (RuleCall)cFacetsAssignment_4.eContents().get(0);
		
		/** The c alternatives 5. */
		private final Alternatives cAlternatives_5 = (Alternatives)cGroup.eContents().get(5);
		
		/** The c block assignment 5 0. */
		private final Assignment cBlockAssignment_5_0 = (Assignment)cAlternatives_5.eContents().get(0);
		
		/** The c block block parser rule call 5 0 0. */
		private final RuleCall cBlockBlockParserRuleCall_5_0_0 = (RuleCall)cBlockAssignment_5_0.eContents().get(0);
		
		/** The c semicolon keyword 5 1. */
		private final Keyword cSemicolonKeyword_5_1 = (Keyword)cAlternatives_5.eContents().get(1);
		
		//S_Definition:
		//    tkey=(TypeRef) (firstFacet="name:")? name=(Valid_ID | STRING) ('(' (args=ActionArguments) ')')? (facets+=Facet)* (block=Block | ';');
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//tkey=(TypeRef) (firstFacet="name:")? name=(Valid_ID | STRING) ('(' (args=ActionArguments) ')')? (facets+=Facet)* (block=Block | ';')
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the tkey assignment 0.
		 *
		 * @return the tkey assignment 0
		 */
		//tkey=(TypeRef)
		public Assignment getTkeyAssignment_0() { return cTkeyAssignment_0; }
		
		/**
		 * Gets the tkey type ref parser rule call 0 0.
		 *
		 * @return the tkey type ref parser rule call 0 0
		 */
		//(TypeRef)
		public RuleCall getTkeyTypeRefParserRuleCall_0_0() { return cTkeyTypeRefParserRuleCall_0_0; }
		
		/**
		 * Gets the first facet assignment 1.
		 *
		 * @return the first facet assignment 1
		 */
		//(firstFacet="name:")?
		public Assignment getFirstFacetAssignment_1() { return cFirstFacetAssignment_1; }
		
		/**
		 * Gets the first facet name keyword 1 0.
		 *
		 * @return the first facet name keyword 1 0
		 */
		//"name:"
		public Keyword getFirstFacetNameKeyword_1_0() { return cFirstFacetNameKeyword_1_0; }
		
		/**
		 * Gets the name assignment 2.
		 *
		 * @return the name assignment 2
		 */
		//name=(Valid_ID | STRING)
		public Assignment getNameAssignment_2() { return cNameAssignment_2; }
		
		/**
		 * Gets the name alternatives 2 0.
		 *
		 * @return the name alternatives 2 0
		 */
		//(Valid_ID | STRING)
		public Alternatives getNameAlternatives_2_0() { return cNameAlternatives_2_0; }
		
		/**
		 * Gets the name valid ID parser rule call 2 0 0.
		 *
		 * @return the name valid ID parser rule call 2 0 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_2_0_0() { return cNameValid_IDParserRuleCall_2_0_0; }
		
		/**
		 * Gets the name STRING terminal rule call 2 0 1.
		 *
		 * @return the name STRING terminal rule call 2 0 1
		 */
		//STRING
		public RuleCall getNameSTRINGTerminalRuleCall_2_0_1() { return cNameSTRINGTerminalRuleCall_2_0_1; }
		
		/**
		 * Gets the group 3.
		 *
		 * @return the group 3
		 */
		//('(' (args=ActionArguments) ')')?
		public Group getGroup_3() { return cGroup_3; }
		
		/**
		 * Gets the left parenthesis keyword 3 0.
		 *
		 * @return the left parenthesis keyword 3 0
		 */
		//'('
		public Keyword getLeftParenthesisKeyword_3_0() { return cLeftParenthesisKeyword_3_0; }
		
		/**
		 * Gets the args assignment 3 1.
		 *
		 * @return the args assignment 3 1
		 */
		//(args=ActionArguments)
		public Assignment getArgsAssignment_3_1() { return cArgsAssignment_3_1; }
		
		/**
		 * Gets the args action arguments parser rule call 3 1 0.
		 *
		 * @return the args action arguments parser rule call 3 1 0
		 */
		//ActionArguments
		public RuleCall getArgsActionArgumentsParserRuleCall_3_1_0() { return cArgsActionArgumentsParserRuleCall_3_1_0; }
		
		/**
		 * Gets the right parenthesis keyword 3 2.
		 *
		 * @return the right parenthesis keyword 3 2
		 */
		//')'
		public Keyword getRightParenthesisKeyword_3_2() { return cRightParenthesisKeyword_3_2; }
		
		/**
		 * Gets the facets assignment 4.
		 *
		 * @return the facets assignment 4
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_4() { return cFacetsAssignment_4; }
		
		/**
		 * Gets the facets facet parser rule call 4 0.
		 *
		 * @return the facets facet parser rule call 4 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_4_0() { return cFacetsFacetParserRuleCall_4_0; }
		
		/**
		 * Gets the alternatives 5.
		 *
		 * @return the alternatives 5
		 */
		//(block=Block | ';')
		public Alternatives getAlternatives_5() { return cAlternatives_5; }
		
		/**
		 * Gets the block assignment 5 0.
		 *
		 * @return the block assignment 5 0
		 */
		//block=Block
		public Assignment getBlockAssignment_5_0() { return cBlockAssignment_5_0; }
		
		/**
		 * Gets the block block parser rule call 5 0 0.
		 *
		 * @return the block block parser rule call 5 0 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_5_0_0() { return cBlockBlockParserRuleCall_5_0_0; }
		
		/**
		 * Gets the semicolon keyword 5 1.
		 *
		 * @return the semicolon keyword 5 1
		 */
		//';'
		public Keyword getSemicolonKeyword_5_1() { return cSemicolonKeyword_5_1; }
	}
	
	/**
	 * The Class S_ActionElements.
	 */
	public class S_ActionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Action");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c S action action 0. */
		private final Action cS_ActionAction_0 = (Action)cGroup.eContents().get(0);
		
		/** The c key assignment 1. */
		private final Assignment cKeyAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c key action keyword 1 0. */
		private final Keyword cKeyActionKeyword_1_0 = (Keyword)cKeyAssignment_1.eContents().get(0);
		
		/** The c first facet assignment 2. */
		private final Assignment cFirstFacetAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c first facet name keyword 2 0. */
		private final Keyword cFirstFacetNameKeyword_2_0 = (Keyword)cFirstFacetAssignment_2.eContents().get(0);
		
		/** The c name assignment 3. */
		private final Assignment cNameAssignment_3 = (Assignment)cGroup.eContents().get(3);
		
		/** The c name valid ID parser rule call 3 0. */
		private final RuleCall cNameValid_IDParserRuleCall_3_0 = (RuleCall)cNameAssignment_3.eContents().get(0);
		
		/** The c group 4. */
		private final Group cGroup_4 = (Group)cGroup.eContents().get(4);
		
		/** The c left parenthesis keyword 4 0. */
		private final Keyword cLeftParenthesisKeyword_4_0 = (Keyword)cGroup_4.eContents().get(0);
		
		/** The c args assignment 4 1. */
		private final Assignment cArgsAssignment_4_1 = (Assignment)cGroup_4.eContents().get(1);
		
		/** The c args action arguments parser rule call 4 1 0. */
		private final RuleCall cArgsActionArgumentsParserRuleCall_4_1_0 = (RuleCall)cArgsAssignment_4_1.eContents().get(0);
		
		/** The c right parenthesis keyword 4 2. */
		private final Keyword cRightParenthesisKeyword_4_2 = (Keyword)cGroup_4.eContents().get(2);
		
		/** The c facets assignment 5. */
		private final Assignment cFacetsAssignment_5 = (Assignment)cGroup.eContents().get(5);
		
		/** The c facets facet parser rule call 5 0. */
		private final RuleCall cFacetsFacetParserRuleCall_5_0 = (RuleCall)cFacetsAssignment_5.eContents().get(0);
		
		/** The c alternatives 6. */
		private final Alternatives cAlternatives_6 = (Alternatives)cGroup.eContents().get(6);
		
		/** The c block assignment 6 0. */
		private final Assignment cBlockAssignment_6_0 = (Assignment)cAlternatives_6.eContents().get(0);
		
		/** The c block block parser rule call 6 0 0. */
		private final RuleCall cBlockBlockParserRuleCall_6_0_0 = (RuleCall)cBlockAssignment_6_0.eContents().get(0);
		
		/** The c semicolon keyword 6 1. */
		private final Keyword cSemicolonKeyword_6_1 = (Keyword)cAlternatives_6.eContents().get(1);
		
		//S_Action returns S_Definition:
		//    {S_Action} key="action" (firstFacet='name:')? name=Valid_ID ('(' (args=ActionArguments) ')')? (facets+=Facet)* (block=Block | ';');
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//{S_Action} key="action" (firstFacet='name:')? name=Valid_ID ('(' (args=ActionArguments) ')')? (facets+=Facet)* (block=Block | ';')
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the s action action 0.
		 *
		 * @return the s action action 0
		 */
		//{S_Action}
		public Action getS_ActionAction_0() { return cS_ActionAction_0; }
		
		/**
		 * Gets the key assignment 1.
		 *
		 * @return the key assignment 1
		 */
		//key="action"
		public Assignment getKeyAssignment_1() { return cKeyAssignment_1; }
		
		/**
		 * Gets the key action keyword 1 0.
		 *
		 * @return the key action keyword 1 0
		 */
		//"action"
		public Keyword getKeyActionKeyword_1_0() { return cKeyActionKeyword_1_0; }
		
		/**
		 * Gets the first facet assignment 2.
		 *
		 * @return the first facet assignment 2
		 */
		//(firstFacet='name:')?
		public Assignment getFirstFacetAssignment_2() { return cFirstFacetAssignment_2; }
		
		/**
		 * Gets the first facet name keyword 2 0.
		 *
		 * @return the first facet name keyword 2 0
		 */
		//'name:'
		public Keyword getFirstFacetNameKeyword_2_0() { return cFirstFacetNameKeyword_2_0; }
		
		/**
		 * Gets the name assignment 3.
		 *
		 * @return the name assignment 3
		 */
		//name=Valid_ID
		public Assignment getNameAssignment_3() { return cNameAssignment_3; }
		
		/**
		 * Gets the name valid ID parser rule call 3 0.
		 *
		 * @return the name valid ID parser rule call 3 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_3_0() { return cNameValid_IDParserRuleCall_3_0; }
		
		/**
		 * Gets the group 4.
		 *
		 * @return the group 4
		 */
		//('(' (args=ActionArguments) ')')?
		public Group getGroup_4() { return cGroup_4; }
		
		/**
		 * Gets the left parenthesis keyword 4 0.
		 *
		 * @return the left parenthesis keyword 4 0
		 */
		//'('
		public Keyword getLeftParenthesisKeyword_4_0() { return cLeftParenthesisKeyword_4_0; }
		
		/**
		 * Gets the args assignment 4 1.
		 *
		 * @return the args assignment 4 1
		 */
		//(args=ActionArguments)
		public Assignment getArgsAssignment_4_1() { return cArgsAssignment_4_1; }
		
		/**
		 * Gets the args action arguments parser rule call 4 1 0.
		 *
		 * @return the args action arguments parser rule call 4 1 0
		 */
		//ActionArguments
		public RuleCall getArgsActionArgumentsParserRuleCall_4_1_0() { return cArgsActionArgumentsParserRuleCall_4_1_0; }
		
		/**
		 * Gets the right parenthesis keyword 4 2.
		 *
		 * @return the right parenthesis keyword 4 2
		 */
		//')'
		public Keyword getRightParenthesisKeyword_4_2() { return cRightParenthesisKeyword_4_2; }
		
		/**
		 * Gets the facets assignment 5.
		 *
		 * @return the facets assignment 5
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_5() { return cFacetsAssignment_5; }
		
		/**
		 * Gets the facets facet parser rule call 5 0.
		 *
		 * @return the facets facet parser rule call 5 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_5_0() { return cFacetsFacetParserRuleCall_5_0; }
		
		/**
		 * Gets the alternatives 6.
		 *
		 * @return the alternatives 6
		 */
		//(block=Block | ';')
		public Alternatives getAlternatives_6() { return cAlternatives_6; }
		
		/**
		 * Gets the block assignment 6 0.
		 *
		 * @return the block assignment 6 0
		 */
		//block=Block
		public Assignment getBlockAssignment_6_0() { return cBlockAssignment_6_0; }
		
		/**
		 * Gets the block block parser rule call 6 0 0.
		 *
		 * @return the block block parser rule call 6 0 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_6_0_0() { return cBlockBlockParserRuleCall_6_0_0; }
		
		/**
		 * Gets the semicolon keyword 6 1.
		 *
		 * @return the semicolon keyword 6 1
		 */
		//';'
		public Keyword getSemicolonKeyword_6_1() { return cSemicolonKeyword_6_1; }
	}
	
	/**
	 * The Class S_VarElements.
	 */
	public class S_VarElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Var");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c S var action 0. */
		private final Action cS_VarAction_0 = (Action)cGroup.eContents().get(0);
		
		/** The c key assignment 1. */
		private final Assignment cKeyAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c key var or const key parser rule call 1 0. */
		private final RuleCall cKey_VarOrConstKeyParserRuleCall_1_0 = (RuleCall)cKeyAssignment_1.eContents().get(0);
		
		/** The c first facet assignment 2. */
		private final Assignment cFirstFacetAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c first facet name keyword 2 0. */
		private final Keyword cFirstFacetNameKeyword_2_0 = (Keyword)cFirstFacetAssignment_2.eContents().get(0);
		
		/** The c name assignment 3. */
		private final Assignment cNameAssignment_3 = (Assignment)cGroup.eContents().get(3);
		
		/** The c name valid ID parser rule call 3 0. */
		private final RuleCall cNameValid_IDParserRuleCall_3_0 = (RuleCall)cNameAssignment_3.eContents().get(0);
		
		/** The c facets assignment 4. */
		private final Assignment cFacetsAssignment_4 = (Assignment)cGroup.eContents().get(4);
		
		/** The c facets facet parser rule call 4 0. */
		private final RuleCall cFacetsFacetParserRuleCall_4_0 = (RuleCall)cFacetsAssignment_4.eContents().get(0);
		
		/** The c semicolon keyword 5. */
		private final Keyword cSemicolonKeyword_5 = (Keyword)cGroup.eContents().get(5);
		
		//S_Var returns S_Definition:
		//    {S_Var} key=_VarOrConstKey (firstFacet="name:")? name=Valid_ID (facets+=Facet)* ';';
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//{S_Var} key=_VarOrConstKey (firstFacet="name:")? name=Valid_ID (facets+=Facet)* ';'
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the s var action 0.
		 *
		 * @return the s var action 0
		 */
		//{S_Var}
		public Action getS_VarAction_0() { return cS_VarAction_0; }
		
		/**
		 * Gets the key assignment 1.
		 *
		 * @return the key assignment 1
		 */
		//key=_VarOrConstKey
		public Assignment getKeyAssignment_1() { return cKeyAssignment_1; }
		
		/**
		 * Gets the key var or const key parser rule call 1 0.
		 *
		 * @return the key var or const key parser rule call 1 0
		 */
		//_VarOrConstKey
		public RuleCall getKey_VarOrConstKeyParserRuleCall_1_0() { return cKey_VarOrConstKeyParserRuleCall_1_0; }
		
		/**
		 * Gets the first facet assignment 2.
		 *
		 * @return the first facet assignment 2
		 */
		//(firstFacet="name:")?
		public Assignment getFirstFacetAssignment_2() { return cFirstFacetAssignment_2; }
		
		/**
		 * Gets the first facet name keyword 2 0.
		 *
		 * @return the first facet name keyword 2 0
		 */
		//"name:"
		public Keyword getFirstFacetNameKeyword_2_0() { return cFirstFacetNameKeyword_2_0; }
		
		/**
		 * Gets the name assignment 3.
		 *
		 * @return the name assignment 3
		 */
		//name=Valid_ID
		public Assignment getNameAssignment_3() { return cNameAssignment_3; }
		
		/**
		 * Gets the name valid ID parser rule call 3 0.
		 *
		 * @return the name valid ID parser rule call 3 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_3_0() { return cNameValid_IDParserRuleCall_3_0; }
		
		/**
		 * Gets the facets assignment 4.
		 *
		 * @return the facets assignment 4
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_4() { return cFacetsAssignment_4; }
		
		/**
		 * Gets the facets facet parser rule call 4 0.
		 *
		 * @return the facets facet parser rule call 4 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_4_0() { return cFacetsFacetParserRuleCall_4_0; }
		
		/**
		 * Gets the semicolon keyword 5.
		 *
		 * @return the semicolon keyword 5
		 */
		//';'
		public Keyword getSemicolonKeyword_5() { return cSemicolonKeyword_5; }
	}
	
	/**
	 * The Class S_AssignmentElements.
	 */
	public class S_AssignmentElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Assignment");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c S direct assignment parser rule call 0. */
		private final RuleCall cS_DirectAssignmentParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c S set parser rule call 1. */
		private final RuleCall cS_SetParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//    /*
		// * ASSIGNMENTS
		// */
		//S_Assignment:
		//    S_DirectAssignment | S_Set;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//S_DirectAssignment | S_Set
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the s direct assignment parser rule call 0.
		 *
		 * @return the s direct assignment parser rule call 0
		 */
		//S_DirectAssignment
		public RuleCall getS_DirectAssignmentParserRuleCall_0() { return cS_DirectAssignmentParserRuleCall_0; }
		
		/**
		 * Gets the s set parser rule call 1.
		 *
		 * @return the s set parser rule call 1
		 */
		//S_Set
		public RuleCall getS_SetParserRuleCall_1() { return cS_SetParserRuleCall_1; }
	}
	
	/**
	 * The Class S_DirectAssignmentElements.
	 */
	public class S_DirectAssignmentElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_DirectAssignment");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c group 0. */
		private final Group cGroup_0 = (Group)cGroup.eContents().get(0);
		
		/** The c expr assignment 0 0. */
		private final Assignment cExprAssignment_0_0 = (Assignment)cGroup_0.eContents().get(0);
		
		/** The c expr expression parser rule call 0 0 0. */
		private final RuleCall cExprExpressionParserRuleCall_0_0_0 = (RuleCall)cExprAssignment_0_0.eContents().get(0);
		
		/** The c key assignment 0 1. */
		private final Assignment cKeyAssignment_0_1 = (Assignment)cGroup_0.eContents().get(1);
		
		/** The c key assignment key parser rule call 0 1 0. */
		private final RuleCall cKey_AssignmentKeyParserRuleCall_0_1_0 = (RuleCall)cKeyAssignment_0_1.eContents().get(0);
		
		/** The c value assignment 0 2. */
		private final Assignment cValueAssignment_0_2 = (Assignment)cGroup_0.eContents().get(2);
		
		/** The c value expression parser rule call 0 2 0. */
		private final RuleCall cValueExpressionParserRuleCall_0_2_0 = (RuleCall)cValueAssignment_0_2.eContents().get(0);
		
		/** The c facets assignment 0 3. */
		private final Assignment cFacetsAssignment_0_3 = (Assignment)cGroup_0.eContents().get(3);
		
		/** The c facets facet parser rule call 0 3 0. */
		private final RuleCall cFacetsFacetParserRuleCall_0_3_0 = (RuleCall)cFacetsAssignment_0_3.eContents().get(0);
		
		/** The c semicolon keyword 1. */
		private final Keyword cSemicolonKeyword_1 = (Keyword)cGroup.eContents().get(1);
		
		//S_DirectAssignment:
		//    (expr=Expression key=(_AssignmentKey) value=Expression (facets+=Facet)*) ';';
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//(expr=Expression key=(_AssignmentKey) value=Expression (facets+=Facet)*) ';'
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the group 0.
		 *
		 * @return the group 0
		 */
		//(expr=Expression key=(_AssignmentKey) value=Expression (facets+=Facet)*)
		public Group getGroup_0() { return cGroup_0; }
		
		/**
		 * Gets the expr assignment 0 0.
		 *
		 * @return the expr assignment 0 0
		 */
		//expr=Expression
		public Assignment getExprAssignment_0_0() { return cExprAssignment_0_0; }
		
		/**
		 * Gets the expr expression parser rule call 0 0 0.
		 *
		 * @return the expr expression parser rule call 0 0 0
		 */
		//Expression
		public RuleCall getExprExpressionParserRuleCall_0_0_0() { return cExprExpressionParserRuleCall_0_0_0; }
		
		/**
		 * Gets the key assignment 0 1.
		 *
		 * @return the key assignment 0 1
		 */
		//key=(_AssignmentKey)
		public Assignment getKeyAssignment_0_1() { return cKeyAssignment_0_1; }
		
		/**
		 * Gets the key assignment key parser rule call 0 1 0.
		 *
		 * @return the key assignment key parser rule call 0 1 0
		 */
		//(_AssignmentKey)
		public RuleCall getKey_AssignmentKeyParserRuleCall_0_1_0() { return cKey_AssignmentKeyParserRuleCall_0_1_0; }
		
		/**
		 * Gets the value assignment 0 2.
		 *
		 * @return the value assignment 0 2
		 */
		//value=Expression
		public Assignment getValueAssignment_0_2() { return cValueAssignment_0_2; }
		
		/**
		 * Gets the value expression parser rule call 0 2 0.
		 *
		 * @return the value expression parser rule call 0 2 0
		 */
		//Expression
		public RuleCall getValueExpressionParserRuleCall_0_2_0() { return cValueExpressionParserRuleCall_0_2_0; }
		
		/**
		 * Gets the facets assignment 0 3.
		 *
		 * @return the facets assignment 0 3
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_0_3() { return cFacetsAssignment_0_3; }
		
		/**
		 * Gets the facets facet parser rule call 0 3 0.
		 *
		 * @return the facets facet parser rule call 0 3 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_0_3_0() { return cFacetsFacetParserRuleCall_0_3_0; }
		
		/**
		 * Gets the semicolon keyword 1.
		 *
		 * @return the semicolon keyword 1
		 */
		//';'
		public Keyword getSemicolonKeyword_1() { return cSemicolonKeyword_1; }
	}
	
	/**
	 * The Class S_SetElements.
	 */
	public class S_SetElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Set");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key set keyword 0 0. */
		private final Keyword cKeySetKeyword_0_0 = (Keyword)cKeyAssignment_0.eContents().get(0);
		
		/** The c expr assignment 1. */
		private final Assignment cExprAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c expr expression parser rule call 1 0. */
		private final RuleCall cExprExpressionParserRuleCall_1_0 = (RuleCall)cExprAssignment_1.eContents().get(0);
		
		/** The c alternatives 2. */
		private final Alternatives cAlternatives_2 = (Alternatives)cGroup.eContents().get(2);
		
		/** The c value keyword 2 0. */
		private final Keyword cValueKeyword_2_0 = (Keyword)cAlternatives_2.eContents().get(0);
		
		/** The c less than sign hyphen minus keyword 2 1. */
		private final Keyword cLessThanSignHyphenMinusKeyword_2_1 = (Keyword)cAlternatives_2.eContents().get(1);
		
		/** The c value assignment 3. */
		private final Assignment cValueAssignment_3 = (Assignment)cGroup.eContents().get(3);
		
		/** The c value expression parser rule call 3 0. */
		private final RuleCall cValueExpressionParserRuleCall_3_0 = (RuleCall)cValueAssignment_3.eContents().get(0);
		
		/** The c semicolon keyword 4. */
		private final Keyword cSemicolonKeyword_4 = (Keyword)cGroup.eContents().get(4);
		
		//S_Set:
		//    key="set" expr=Expression ("value:" | "<-") value=Expression ";";
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key="set" expr=Expression ("value:" | "<-") value=Expression ";"
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key="set"
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key set keyword 0 0.
		 *
		 * @return the key set keyword 0 0
		 */
		//"set"
		public Keyword getKeySetKeyword_0_0() { return cKeySetKeyword_0_0; }
		
		/**
		 * Gets the expr assignment 1.
		 *
		 * @return the expr assignment 1
		 */
		//expr=Expression
		public Assignment getExprAssignment_1() { return cExprAssignment_1; }
		
		/**
		 * Gets the expr expression parser rule call 1 0.
		 *
		 * @return the expr expression parser rule call 1 0
		 */
		//Expression
		public RuleCall getExprExpressionParserRuleCall_1_0() { return cExprExpressionParserRuleCall_1_0; }
		
		/**
		 * Gets the alternatives 2.
		 *
		 * @return the alternatives 2
		 */
		//("value:" | "<-")
		public Alternatives getAlternatives_2() { return cAlternatives_2; }
		
		/**
		 * Gets the value keyword 2 0.
		 *
		 * @return the value keyword 2 0
		 */
		//"value:"
		public Keyword getValueKeyword_2_0() { return cValueKeyword_2_0; }
		
		/**
		 * Gets the less than sign hyphen minus keyword 2 1.
		 *
		 * @return the less than sign hyphen minus keyword 2 1
		 */
		//"<-"
		public Keyword getLessThanSignHyphenMinusKeyword_2_1() { return cLessThanSignHyphenMinusKeyword_2_1; }
		
		/**
		 * Gets the value assignment 3.
		 *
		 * @return the value assignment 3
		 */
		//value=Expression
		public Assignment getValueAssignment_3() { return cValueAssignment_3; }
		
		/**
		 * Gets the value expression parser rule call 3 0.
		 *
		 * @return the value expression parser rule call 3 0
		 */
		//Expression
		public RuleCall getValueExpressionParserRuleCall_3_0() { return cValueExpressionParserRuleCall_3_0; }
		
		/**
		 * Gets the semicolon keyword 4.
		 *
		 * @return the semicolon keyword 4
		 */
		//";"
		public Keyword getSemicolonKeyword_4() { return cSemicolonKeyword_4; }
	}
	
	/**
	 * The Class S_EquationsElements.
	 */
	public class S_EquationsElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Equations");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key equations key parser rule call 0 0. */
		private final RuleCall cKey_EquationsKeyParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c name assignment 1. */
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c name valid ID parser rule call 1 0. */
		private final RuleCall cNameValid_IDParserRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		
		/** The c facets assignment 2. */
		private final Assignment cFacetsAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c facets facet parser rule call 2 0. */
		private final RuleCall cFacetsFacetParserRuleCall_2_0 = (RuleCall)cFacetsAssignment_2.eContents().get(0);
		
		/** The c alternatives 3. */
		private final Alternatives cAlternatives_3 = (Alternatives)cGroup.eContents().get(3);
		
		/** The c group 3 0. */
		private final Group cGroup_3_0 = (Group)cAlternatives_3.eContents().get(0);
		
		/** The c left curly bracket keyword 3 0 0. */
		private final Keyword cLeftCurlyBracketKeyword_3_0_0 = (Keyword)cGroup_3_0.eContents().get(0);
		
		/** The c group 3 0 1. */
		private final Group cGroup_3_0_1 = (Group)cGroup_3_0.eContents().get(1);
		
		/** The c equations assignment 3 0 1 0. */
		private final Assignment cEquationsAssignment_3_0_1_0 = (Assignment)cGroup_3_0_1.eContents().get(0);
		
		/** The c equations S equation parser rule call 3 0 1 0 0. */
		private final RuleCall cEquationsS_EquationParserRuleCall_3_0_1_0_0 = (RuleCall)cEquationsAssignment_3_0_1_0.eContents().get(0);
		
		/** The c semicolon keyword 3 0 1 1. */
		private final Keyword cSemicolonKeyword_3_0_1_1 = (Keyword)cGroup_3_0_1.eContents().get(1);
		
		/** The c right curly bracket keyword 3 0 2. */
		private final Keyword cRightCurlyBracketKeyword_3_0_2 = (Keyword)cGroup_3_0.eContents().get(2);
		
		/** The c semicolon keyword 3 1. */
		private final Keyword cSemicolonKeyword_3_1 = (Keyword)cAlternatives_3.eContents().get(1);
		
		//S_Equations:
		//    key=_EquationsKey name=Valid_ID (facets+=Facet)* ('{' (equations+=S_Equation ';')* '}' | ';');
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key=_EquationsKey name=Valid_ID (facets+=Facet)* ('{' (equations+=S_Equation ';')* '}' | ';')
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key=_EquationsKey
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key equations key parser rule call 0 0.
		 *
		 * @return the key equations key parser rule call 0 0
		 */
		//_EquationsKey
		public RuleCall getKey_EquationsKeyParserRuleCall_0_0() { return cKey_EquationsKeyParserRuleCall_0_0; }
		
		/**
		 * Gets the name assignment 1.
		 *
		 * @return the name assignment 1
		 */
		//name=Valid_ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		/**
		 * Gets the name valid ID parser rule call 1 0.
		 *
		 * @return the name valid ID parser rule call 1 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_1_0() { return cNameValid_IDParserRuleCall_1_0; }
		
		/**
		 * Gets the facets assignment 2.
		 *
		 * @return the facets assignment 2
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_2() { return cFacetsAssignment_2; }
		
		/**
		 * Gets the facets facet parser rule call 2 0.
		 *
		 * @return the facets facet parser rule call 2 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_2_0() { return cFacetsFacetParserRuleCall_2_0; }
		
		/**
		 * Gets the alternatives 3.
		 *
		 * @return the alternatives 3
		 */
		//('{' (equations+=S_Equation ';')* '}' | ';')
		public Alternatives getAlternatives_3() { return cAlternatives_3; }
		
		/**
		 * Gets the group 3 0.
		 *
		 * @return the group 3 0
		 */
		//'{' (equations+=S_Equation ';')* '}'
		public Group getGroup_3_0() { return cGroup_3_0; }
		
		/**
		 * Gets the left curly bracket keyword 3 0 0.
		 *
		 * @return the left curly bracket keyword 3 0 0
		 */
		//'{'
		public Keyword getLeftCurlyBracketKeyword_3_0_0() { return cLeftCurlyBracketKeyword_3_0_0; }
		
		/**
		 * Gets the group 3 0 1.
		 *
		 * @return the group 3 0 1
		 */
		//(equations+=S_Equation ';')*
		public Group getGroup_3_0_1() { return cGroup_3_0_1; }
		
		/**
		 * Gets the equations assignment 3 0 1 0.
		 *
		 * @return the equations assignment 3 0 1 0
		 */
		//equations+=S_Equation
		public Assignment getEquationsAssignment_3_0_1_0() { return cEquationsAssignment_3_0_1_0; }
		
		/**
		 * Gets the equations S equation parser rule call 3 0 1 0 0.
		 *
		 * @return the equations S equation parser rule call 3 0 1 0 0
		 */
		//S_Equation
		public RuleCall getEquationsS_EquationParserRuleCall_3_0_1_0_0() { return cEquationsS_EquationParserRuleCall_3_0_1_0_0; }
		
		/**
		 * Gets the semicolon keyword 3 0 1 1.
		 *
		 * @return the semicolon keyword 3 0 1 1
		 */
		//';'
		public Keyword getSemicolonKeyword_3_0_1_1() { return cSemicolonKeyword_3_0_1_1; }
		
		/**
		 * Gets the right curly bracket keyword 3 0 2.
		 *
		 * @return the right curly bracket keyword 3 0 2
		 */
		//'}'
		public Keyword getRightCurlyBracketKeyword_3_0_2() { return cRightCurlyBracketKeyword_3_0_2; }
		
		/**
		 * Gets the semicolon keyword 3 1.
		 *
		 * @return the semicolon keyword 3 1
		 */
		//';'
		public Keyword getSemicolonKeyword_3_1() { return cSemicolonKeyword_3_1; }
	}
	
	/**
	 * The Class S_EquationElements.
	 */
	public class S_EquationElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Equation");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c expr assignment 0. */
		private final Assignment cExprAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c expr alternatives 0 0. */
		private final Alternatives cExprAlternatives_0_0 = (Alternatives)cExprAssignment_0.eContents().get(0);
		
		/** The c expr function parser rule call 0 0 0. */
		private final RuleCall cExprFunctionParserRuleCall_0_0_0 = (RuleCall)cExprAlternatives_0_0.eContents().get(0);
		
		/** The c expr variable ref parser rule call 0 0 1. */
		private final RuleCall cExprVariableRefParserRuleCall_0_0_1 = (RuleCall)cExprAlternatives_0_0.eContents().get(1);
		
		/** The c key assignment 1. */
		private final Assignment cKeyAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c key equals sign keyword 1 0. */
		private final Keyword cKeyEqualsSignKeyword_1_0 = (Keyword)cKeyAssignment_1.eContents().get(0);
		
		/** The c value assignment 2. */
		private final Assignment cValueAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c value expression parser rule call 2 0. */
		private final RuleCall cValueExpressionParserRuleCall_2_0 = (RuleCall)cValueAssignment_2.eContents().get(0);
		
		//S_Equation returns S_Assignment:
		//    expr=(Function|VariableRef) key="=" value=Expression;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//expr=(Function|VariableRef) key="=" value=Expression
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the expr assignment 0.
		 *
		 * @return the expr assignment 0
		 */
		//expr=(Function|VariableRef)
		public Assignment getExprAssignment_0() { return cExprAssignment_0; }
		
		/**
		 * Gets the expr alternatives 0 0.
		 *
		 * @return the expr alternatives 0 0
		 */
		//(Function|VariableRef)
		public Alternatives getExprAlternatives_0_0() { return cExprAlternatives_0_0; }
		
		/**
		 * Gets the expr function parser rule call 0 0 0.
		 *
		 * @return the expr function parser rule call 0 0 0
		 */
		//Function
		public RuleCall getExprFunctionParserRuleCall_0_0_0() { return cExprFunctionParserRuleCall_0_0_0; }
		
		/**
		 * Gets the expr variable ref parser rule call 0 0 1.
		 *
		 * @return the expr variable ref parser rule call 0 0 1
		 */
		//VariableRef
		public RuleCall getExprVariableRefParserRuleCall_0_0_1() { return cExprVariableRefParserRuleCall_0_0_1; }
		
		/**
		 * Gets the key assignment 1.
		 *
		 * @return the key assignment 1
		 */
		//key="="
		public Assignment getKeyAssignment_1() { return cKeyAssignment_1; }
		
		/**
		 * Gets the key equals sign keyword 1 0.
		 *
		 * @return the key equals sign keyword 1 0
		 */
		//"="
		public Keyword getKeyEqualsSignKeyword_1_0() { return cKeyEqualsSignKeyword_1_0; }
		
		/**
		 * Gets the value assignment 2.
		 *
		 * @return the value assignment 2
		 */
		//value=Expression
		public Assignment getValueAssignment_2() { return cValueAssignment_2; }
		
		/**
		 * Gets the value expression parser rule call 2 0.
		 *
		 * @return the value expression parser rule call 2 0
		 */
		//Expression
		public RuleCall getValueExpressionParserRuleCall_2_0() { return cValueExpressionParserRuleCall_2_0; }
	}
	
	/**
	 * The Class S_SolveElements.
	 */
	public class S_SolveElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Solve");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key solve key parser rule call 0 0. */
		private final RuleCall cKey_SolveKeyParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c first facet assignment 1. */
		private final Assignment cFirstFacetAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c first facet equation keyword 1 0. */
		private final Keyword cFirstFacetEquationKeyword_1_0 = (Keyword)cFirstFacetAssignment_1.eContents().get(0);
		
		/** The c expr assignment 2. */
		private final Assignment cExprAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c expr equation ref parser rule call 2 0. */
		private final RuleCall cExprEquationRefParserRuleCall_2_0 = (RuleCall)cExprAssignment_2.eContents().get(0);
		
		/** The c facets assignment 3. */
		private final Assignment cFacetsAssignment_3 = (Assignment)cGroup.eContents().get(3);
		
		/** The c facets facet parser rule call 3 0. */
		private final RuleCall cFacetsFacetParserRuleCall_3_0 = (RuleCall)cFacetsAssignment_3.eContents().get(0);
		
		/** The c alternatives 4. */
		private final Alternatives cAlternatives_4 = (Alternatives)cGroup.eContents().get(4);
		
		/** The c block assignment 4 0. */
		private final Assignment cBlockAssignment_4_0 = (Assignment)cAlternatives_4.eContents().get(0);
		
		/** The c block block parser rule call 4 0 0. */
		private final RuleCall cBlockBlockParserRuleCall_4_0_0 = (RuleCall)cBlockAssignment_4_0.eContents().get(0);
		
		/** The c semicolon keyword 4 1. */
		private final Keyword cSemicolonKeyword_4_1 = (Keyword)cAlternatives_4.eContents().get(1);
		
		//S_Solve:
		//    key=_SolveKey (firstFacet="equation:")? expr=EquationRef (facets+=Facet)* (block=Block | ';');
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key=_SolveKey (firstFacet="equation:")? expr=EquationRef (facets+=Facet)* (block=Block | ';')
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key=_SolveKey
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key solve key parser rule call 0 0.
		 *
		 * @return the key solve key parser rule call 0 0
		 */
		//_SolveKey
		public RuleCall getKey_SolveKeyParserRuleCall_0_0() { return cKey_SolveKeyParserRuleCall_0_0; }
		
		/**
		 * Gets the first facet assignment 1.
		 *
		 * @return the first facet assignment 1
		 */
		//(firstFacet="equation:")?
		public Assignment getFirstFacetAssignment_1() { return cFirstFacetAssignment_1; }
		
		/**
		 * Gets the first facet equation keyword 1 0.
		 *
		 * @return the first facet equation keyword 1 0
		 */
		//"equation:"
		public Keyword getFirstFacetEquationKeyword_1_0() { return cFirstFacetEquationKeyword_1_0; }
		
		/**
		 * Gets the expr assignment 2.
		 *
		 * @return the expr assignment 2
		 */
		//expr=EquationRef
		public Assignment getExprAssignment_2() { return cExprAssignment_2; }
		
		/**
		 * Gets the expr equation ref parser rule call 2 0.
		 *
		 * @return the expr equation ref parser rule call 2 0
		 */
		//EquationRef
		public RuleCall getExprEquationRefParserRuleCall_2_0() { return cExprEquationRefParserRuleCall_2_0; }
		
		/**
		 * Gets the facets assignment 3.
		 *
		 * @return the facets assignment 3
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_3() { return cFacetsAssignment_3; }
		
		/**
		 * Gets the facets facet parser rule call 3 0.
		 *
		 * @return the facets facet parser rule call 3 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_3_0() { return cFacetsFacetParserRuleCall_3_0; }
		
		/**
		 * Gets the alternatives 4.
		 *
		 * @return the alternatives 4
		 */
		//(block=Block | ';')
		public Alternatives getAlternatives_4() { return cAlternatives_4; }
		
		/**
		 * Gets the block assignment 4 0.
		 *
		 * @return the block assignment 4 0
		 */
		//block=Block
		public Assignment getBlockAssignment_4_0() { return cBlockAssignment_4_0; }
		
		/**
		 * Gets the block block parser rule call 4 0 0.
		 *
		 * @return the block block parser rule call 4 0 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_4_0_0() { return cBlockBlockParserRuleCall_4_0_0; }
		
		/**
		 * Gets the semicolon keyword 4 1.
		 *
		 * @return the semicolon keyword 4 1
		 */
		//';'
		public Keyword getSemicolonKeyword_4_1() { return cSemicolonKeyword_4_1; }
	}
	
	/**
	 * The Class S_DisplayElements.
	 */
	public class S_DisplayElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.S_Display");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key display keyword 0 0. */
		private final Keyword cKeyDisplayKeyword_0_0 = (Keyword)cKeyAssignment_0.eContents().get(0);
		
		/** The c first facet assignment 1. */
		private final Assignment cFirstFacetAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c first facet name keyword 1 0. */
		private final Keyword cFirstFacetNameKeyword_1_0 = (Keyword)cFirstFacetAssignment_1.eContents().get(0);
		
		/** The c name assignment 2. */
		private final Assignment cNameAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c name alternatives 2 0. */
		private final Alternatives cNameAlternatives_2_0 = (Alternatives)cNameAssignment_2.eContents().get(0);
		
		/** The c name valid ID parser rule call 2 0 0. */
		private final RuleCall cNameValid_IDParserRuleCall_2_0_0 = (RuleCall)cNameAlternatives_2_0.eContents().get(0);
		
		/** The c name STRING terminal rule call 2 0 1. */
		private final RuleCall cNameSTRINGTerminalRuleCall_2_0_1 = (RuleCall)cNameAlternatives_2_0.eContents().get(1);
		
		/** The c facets assignment 3. */
		private final Assignment cFacetsAssignment_3 = (Assignment)cGroup.eContents().get(3);
		
		/** The c facets facet parser rule call 3 0. */
		private final RuleCall cFacetsFacetParserRuleCall_3_0 = (RuleCall)cFacetsAssignment_3.eContents().get(0);
		
		/** The c block assignment 4. */
		private final Assignment cBlockAssignment_4 = (Assignment)cGroup.eContents().get(4);
		
		/** The c block display block parser rule call 4 0. */
		private final RuleCall cBlockDisplayBlockParserRuleCall_4_0 = (RuleCall)cBlockAssignment_4.eContents().get(0);
		
		///**
		// * DISPLAYS
		// */
		//S_Display:
		//    key="display" (firstFacet="name:")? name=(Valid_ID|STRING) (facets+=Facet)* block=displayBlock
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key="display" (firstFacet="name:")? name=(Valid_ID|STRING) (facets+=Facet)* block=displayBlock
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key="display"
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key display keyword 0 0.
		 *
		 * @return the key display keyword 0 0
		 */
		//"display"
		public Keyword getKeyDisplayKeyword_0_0() { return cKeyDisplayKeyword_0_0; }
		
		/**
		 * Gets the first facet assignment 1.
		 *
		 * @return the first facet assignment 1
		 */
		//(firstFacet="name:")?
		public Assignment getFirstFacetAssignment_1() { return cFirstFacetAssignment_1; }
		
		/**
		 * Gets the first facet name keyword 1 0.
		 *
		 * @return the first facet name keyword 1 0
		 */
		//"name:"
		public Keyword getFirstFacetNameKeyword_1_0() { return cFirstFacetNameKeyword_1_0; }
		
		/**
		 * Gets the name assignment 2.
		 *
		 * @return the name assignment 2
		 */
		//name=(Valid_ID|STRING)
		public Assignment getNameAssignment_2() { return cNameAssignment_2; }
		
		/**
		 * Gets the name alternatives 2 0.
		 *
		 * @return the name alternatives 2 0
		 */
		//(Valid_ID|STRING)
		public Alternatives getNameAlternatives_2_0() { return cNameAlternatives_2_0; }
		
		/**
		 * Gets the name valid ID parser rule call 2 0 0.
		 *
		 * @return the name valid ID parser rule call 2 0 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_2_0_0() { return cNameValid_IDParserRuleCall_2_0_0; }
		
		/**
		 * Gets the name STRING terminal rule call 2 0 1.
		 *
		 * @return the name STRING terminal rule call 2 0 1
		 */
		//STRING
		public RuleCall getNameSTRINGTerminalRuleCall_2_0_1() { return cNameSTRINGTerminalRuleCall_2_0_1; }
		
		/**
		 * Gets the facets assignment 3.
		 *
		 * @return the facets assignment 3
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_3() { return cFacetsAssignment_3; }
		
		/**
		 * Gets the facets facet parser rule call 3 0.
		 *
		 * @return the facets facet parser rule call 3 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_3_0() { return cFacetsFacetParserRuleCall_3_0; }
		
		/**
		 * Gets the block assignment 4.
		 *
		 * @return the block assignment 4
		 */
		//block=displayBlock
		public Assignment getBlockAssignment_4() { return cBlockAssignment_4; }
		
		/**
		 * Gets the block display block parser rule call 4 0.
		 *
		 * @return the block display block parser rule call 4 0
		 */
		//displayBlock
		public RuleCall getBlockDisplayBlockParserRuleCall_4_0() { return cBlockDisplayBlockParserRuleCall_4_0; }
	}
	
	/**
	 * The Class DisplayBlockElements.
	 */
	public class DisplayBlockElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.displayBlock");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c block action 0. */
		private final Action cBlockAction_0 = (Action)cGroup.eContents().get(0);
		
		/** The c left curly bracket keyword 1. */
		private final Keyword cLeftCurlyBracketKeyword_1 = (Keyword)cGroup.eContents().get(1);
		
		/** The c statements assignment 2. */
		private final Assignment cStatementsAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c statements display statement parser rule call 2 0. */
		private final RuleCall cStatementsDisplayStatementParserRuleCall_2_0 = (RuleCall)cStatementsAssignment_2.eContents().get(0);
		
		/** The c right curly bracket keyword 3. */
		private final Keyword cRightCurlyBracketKeyword_3 = (Keyword)cGroup.eContents().get(3);
		
		//displayBlock returns Block:
		//    {Block} '{' (statements+=displayStatement)* '}'
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//{Block} '{' (statements+=displayStatement)* '}'
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the block action 0.
		 *
		 * @return the block action 0
		 */
		//{Block}
		public Action getBlockAction_0() { return cBlockAction_0; }
		
		/**
		 * Gets the left curly bracket keyword 1.
		 *
		 * @return the left curly bracket keyword 1
		 */
		//'{'
		public Keyword getLeftCurlyBracketKeyword_1() { return cLeftCurlyBracketKeyword_1; }
		
		/**
		 * Gets the statements assignment 2.
		 *
		 * @return the statements assignment 2
		 */
		//(statements+=displayStatement)*
		public Assignment getStatementsAssignment_2() { return cStatementsAssignment_2; }
		
		/**
		 * Gets the statements display statement parser rule call 2 0.
		 *
		 * @return the statements display statement parser rule call 2 0
		 */
		//displayStatement
		public RuleCall getStatementsDisplayStatementParserRuleCall_2_0() { return cStatementsDisplayStatementParserRuleCall_2_0; }
		
		/**
		 * Gets the right curly bracket keyword 3.
		 *
		 * @return the right curly bracket keyword 3
		 */
		//'}'
		public Keyword getRightCurlyBracketKeyword_3() { return cRightCurlyBracketKeyword_3; }
	}
	
	/**
	 * The Class DisplayStatementElements.
	 */
	public class DisplayStatementElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.displayStatement");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c species or grid display statement parser rule call 0. */
		private final RuleCall cSpeciesOrGridDisplayStatementParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c statement parser rule call 1. */
		private final RuleCall cStatementParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//displayStatement returns Statement:
		//    =>speciesOrGridDisplayStatement | Statement
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//=>speciesOrGridDisplayStatement | Statement
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the species or grid display statement parser rule call 0.
		 *
		 * @return the species or grid display statement parser rule call 0
		 */
		//=>speciesOrGridDisplayStatement
		public RuleCall getSpeciesOrGridDisplayStatementParserRuleCall_0() { return cSpeciesOrGridDisplayStatementParserRuleCall_0; }
		
		/**
		 * Gets the statement parser rule call 1.
		 *
		 * @return the statement parser rule call 1
		 */
		//Statement
		public RuleCall getStatementParserRuleCall_1() { return cStatementParserRuleCall_1; }
	}
	
	/**
	 * The Class SpeciesOrGridDisplayStatementElements.
	 */
	public class SpeciesOrGridDisplayStatementElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.speciesOrGridDisplayStatement");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key species key parser rule call 0 0. */
		private final RuleCall cKey_SpeciesKeyParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c expr assignment 1. */
		private final Assignment cExprAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c expr expression parser rule call 1 0. */
		private final RuleCall cExprExpressionParserRuleCall_1_0 = (RuleCall)cExprAssignment_1.eContents().get(0);
		
		/** The c facets assignment 2. */
		private final Assignment cFacetsAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c facets facet parser rule call 2 0. */
		private final RuleCall cFacetsFacetParserRuleCall_2_0 = (RuleCall)cFacetsAssignment_2.eContents().get(0);
		
		/** The c alternatives 3. */
		private final Alternatives cAlternatives_3 = (Alternatives)cGroup.eContents().get(3);
		
		/** The c block assignment 3 0. */
		private final Assignment cBlockAssignment_3_0 = (Assignment)cAlternatives_3.eContents().get(0);
		
		/** The c block display block parser rule call 3 0 0. */
		private final RuleCall cBlockDisplayBlockParserRuleCall_3_0_0 = (RuleCall)cBlockAssignment_3_0.eContents().get(0);
		
		/** The c semicolon keyword 3 1. */
		private final Keyword cSemicolonKeyword_3_1 = (Keyword)cAlternatives_3.eContents().get(1);
		
		//speciesOrGridDisplayStatement:
		//    key=_SpeciesKey expr=Expression (facets+=Facet)* (block=displayBlock | ";")
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key=_SpeciesKey expr=Expression (facets+=Facet)* (block=displayBlock | ";")
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key=_SpeciesKey
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key species key parser rule call 0 0.
		 *
		 * @return the key species key parser rule call 0 0
		 */
		//_SpeciesKey
		public RuleCall getKey_SpeciesKeyParserRuleCall_0_0() { return cKey_SpeciesKeyParserRuleCall_0_0; }
		
		/**
		 * Gets the expr assignment 1.
		 *
		 * @return the expr assignment 1
		 */
		//expr=Expression
		public Assignment getExprAssignment_1() { return cExprAssignment_1; }
		
		/**
		 * Gets the expr expression parser rule call 1 0.
		 *
		 * @return the expr expression parser rule call 1 0
		 */
		//Expression
		public RuleCall getExprExpressionParserRuleCall_1_0() { return cExprExpressionParserRuleCall_1_0; }
		
		/**
		 * Gets the facets assignment 2.
		 *
		 * @return the facets assignment 2
		 */
		//(facets+=Facet)*
		public Assignment getFacetsAssignment_2() { return cFacetsAssignment_2; }
		
		/**
		 * Gets the facets facet parser rule call 2 0.
		 *
		 * @return the facets facet parser rule call 2 0
		 */
		//Facet
		public RuleCall getFacetsFacetParserRuleCall_2_0() { return cFacetsFacetParserRuleCall_2_0; }
		
		/**
		 * Gets the alternatives 3.
		 *
		 * @return the alternatives 3
		 */
		//(block=displayBlock | ";")
		public Alternatives getAlternatives_3() { return cAlternatives_3; }
		
		/**
		 * Gets the block assignment 3 0.
		 *
		 * @return the block assignment 3 0
		 */
		//block=displayBlock
		public Assignment getBlockAssignment_3_0() { return cBlockAssignment_3_0; }
		
		/**
		 * Gets the block display block parser rule call 3 0 0.
		 *
		 * @return the block display block parser rule call 3 0 0
		 */
		//displayBlock
		public RuleCall getBlockDisplayBlockParserRuleCall_3_0_0() { return cBlockDisplayBlockParserRuleCall_3_0_0; }
		
		/**
		 * Gets the semicolon keyword 3 1.
		 *
		 * @return the semicolon keyword 3 1
		 */
		//";"
		public Keyword getSemicolonKeyword_3_1() { return cSemicolonKeyword_3_1; }
	}
	
	/**
	 * The Class _EquationsKeyElements.
	 */
	public class _EquationsKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml._EquationsKey");
		
		/** The c equation keyword. */
		private final Keyword cEquationKeyword = (Keyword)rule.eContents().get(1);
		
		//    /**
		// * Statement keys
		// */
		//_EquationsKey:
		//    "equation";
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the equation keyword.
		 *
		 * @return the equation keyword
		 */
		//"equation"
		public Keyword getEquationKeyword() { return cEquationKeyword; }
	}
	
	/**
	 * The Class _SolveKeyElements.
	 */
	public class _SolveKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml._SolveKey");
		
		/** The c solve keyword. */
		private final Keyword cSolveKeyword = (Keyword)rule.eContents().get(1);
		
		//_SolveKey:
		//    "solve";
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the solve keyword.
		 *
		 * @return the solve keyword
		 */
		//"solve"
		public Keyword getSolveKeyword() { return cSolveKeyword; }
	}
	
	/**
	 * The Class _SpeciesKeyElements.
	 */
	public class _SpeciesKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml._SpeciesKey");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c species keyword 0. */
		private final Keyword cSpeciesKeyword_0 = (Keyword)cAlternatives.eContents().get(0);
		
		/** The c grid keyword 1. */
		private final Keyword cGridKeyword_1 = (Keyword)cAlternatives.eContents().get(1);
		
		//_SpeciesKey:
		//    "species" | "grid";
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//"species" | "grid"
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the species keyword 0.
		 *
		 * @return the species keyword 0
		 */
		//"species"
		public Keyword getSpeciesKeyword_0() { return cSpeciesKeyword_0; }
		
		/**
		 * Gets the grid keyword 1.
		 *
		 * @return the grid keyword 1
		 */
		//"grid"
		public Keyword getGridKeyword_1() { return cGridKeyword_1; }
	}
	
	/**
	 * The Class _ExperimentKeyElements.
	 */
	public class _ExperimentKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml._ExperimentKey");
		
		/** The c experiment keyword. */
		private final Keyword cExperimentKeyword = (Keyword)rule.eContents().get(1);
		
		//_ExperimentKey:
		//    "experiment"
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the experiment keyword.
		 *
		 * @return the experiment keyword
		 */
		//"experiment"
		public Keyword getExperimentKeyword() { return cExperimentKeyword; }
	}
	
	/**
	 * The Class _1Expr_Facets_BlockOrEnd_KeyElements.
	 */
	public class _1Expr_Facets_BlockOrEnd_KeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml._1Expr_Facets_BlockOrEnd_Key");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c layer key parser rule call 0. */
		private final RuleCall c_LayerKeyParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c ask keyword 1. */
		private final Keyword cAskKeyword_1 = (Keyword)cAlternatives.eContents().get(1);
		
		/** The c release keyword 2. */
		private final Keyword cReleaseKeyword_2 = (Keyword)cAlternatives.eContents().get(2);
		
		/** The c capture keyword 3. */
		private final Keyword cCaptureKeyword_3 = (Keyword)cAlternatives.eContents().get(3);
		
		/** The c create keyword 4. */
		private final Keyword cCreateKeyword_4 = (Keyword)cAlternatives.eContents().get(4);
		
		/** The c write keyword 5. */
		private final Keyword cWriteKeyword_5 = (Keyword)cAlternatives.eContents().get(5);
		
		/** The c error keyword 6. */
		private final Keyword cErrorKeyword_6 = (Keyword)cAlternatives.eContents().get(6);
		
		/** The c warn keyword 7. */
		private final Keyword cWarnKeyword_7 = (Keyword)cAlternatives.eContents().get(7);
		
		/** The c exception keyword 8. */
		private final Keyword cExceptionKeyword_8 = (Keyword)cAlternatives.eContents().get(8);
		
		/** The c save keyword 9. */
		private final Keyword cSaveKeyword_9 = (Keyword)cAlternatives.eContents().get(9);
		
		/** The c assert keyword 10. */
		private final Keyword cAssertKeyword_10 = (Keyword)cAlternatives.eContents().get(10);
		
		/** The c inspect keyword 11. */
		private final Keyword cInspectKeyword_11 = (Keyword)cAlternatives.eContents().get(11);
		
		/** The c browse keyword 12. */
		private final Keyword cBrowseKeyword_12 = (Keyword)cAlternatives.eContents().get(12);
		
		/** The c draw keyword 13. */
		private final Keyword cDrawKeyword_13 = (Keyword)cAlternatives.eContents().get(13);
		
		/** The c using keyword 14. */
		private final Keyword cUsingKeyword_14 = (Keyword)cAlternatives.eContents().get(14);
		
		/** The c switch keyword 15. */
		private final Keyword cSwitchKeyword_15 = (Keyword)cAlternatives.eContents().get(15);
		
		/** The c put keyword 16. */
		private final Keyword cPutKeyword_16 = (Keyword)cAlternatives.eContents().get(16);
		
		/** The c add keyword 17. */
		private final Keyword cAddKeyword_17 = (Keyword)cAlternatives.eContents().get(17);
		
		/** The c remove keyword 18. */
		private final Keyword cRemoveKeyword_18 = (Keyword)cAlternatives.eContents().get(18);
		
		/** The c match keyword 19. */
		private final Keyword cMatchKeyword_19 = (Keyword)cAlternatives.eContents().get(19);
		
		/** The c match between keyword 20. */
		private final Keyword cMatch_betweenKeyword_20 = (Keyword)cAlternatives.eContents().get(20);
		
		/** The c match one keyword 21. */
		private final Keyword cMatch_oneKeyword_21 = (Keyword)cAlternatives.eContents().get(21);
		
		/** The c parameter keyword 22. */
		private final Keyword cParameterKeyword_22 = (Keyword)cAlternatives.eContents().get(22);
		
		/** The c status keyword 23. */
		private final Keyword cStatusKeyword_23 = (Keyword)cAlternatives.eContents().get(23);
		
		/** The c highlight keyword 24. */
		private final Keyword cHighlightKeyword_24 = (Keyword)cAlternatives.eContents().get(24);
		
		/** The c focus on keyword 25. */
		private final Keyword cFocus_onKeyword_25 = (Keyword)cAlternatives.eContents().get(25);
		
		/** The c layout keyword 26. */
		private final Keyword cLayoutKeyword_26 = (Keyword)cAlternatives.eContents().get(26);
		
		//_1Expr_Facets_BlockOrEnd_Key:
		//    _LayerKey | "ask" | "release" | "capture" | "create" | "write" | "error" | "warn" | "exception" | "save" | "assert" | "inspect" | "browse" |
		//    "draw"  | "using" | "switch" | "put" | "add" | "remove" | "match" | "match_between" | "match_one" | "parameter" | "status" | "highlight" | "focus_on" | "layout" ;
		@Override public ParserRule getRule() { return rule; }
		
		//_LayerKey | "ask" | "release" | "capture" | "create" | "write" | "error" | "warn" | "exception" | "save" | "assert" | "inspect" | "browse" |
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//"draw"  | "using" | "switch" | "put" | "add" | "remove" | "match" | "match_between" | "match_one" | "parameter" | "status" | "highlight" | "focus_on" | "layout"
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the layer key parser rule call 0.
		 *
		 * @return the layer key parser rule call 0
		 */
		//_LayerKey
		public RuleCall get_LayerKeyParserRuleCall_0() { return c_LayerKeyParserRuleCall_0; }
		
		/**
		 * Gets the ask keyword 1.
		 *
		 * @return the ask keyword 1
		 */
		//"ask"
		public Keyword getAskKeyword_1() { return cAskKeyword_1; }
		
		/**
		 * Gets the release keyword 2.
		 *
		 * @return the release keyword 2
		 */
		//"release"
		public Keyword getReleaseKeyword_2() { return cReleaseKeyword_2; }
		
		/**
		 * Gets the capture keyword 3.
		 *
		 * @return the capture keyword 3
		 */
		//"capture"
		public Keyword getCaptureKeyword_3() { return cCaptureKeyword_3; }
		
		/**
		 * Gets the creates the keyword 4.
		 *
		 * @return the creates the keyword 4
		 */
		//"create"
		public Keyword getCreateKeyword_4() { return cCreateKeyword_4; }
		
		/**
		 * Gets the write keyword 5.
		 *
		 * @return the write keyword 5
		 */
		//"write"
		public Keyword getWriteKeyword_5() { return cWriteKeyword_5; }
		
		/**
		 * Gets the error keyword 6.
		 *
		 * @return the error keyword 6
		 */
		//"error"
		public Keyword getErrorKeyword_6() { return cErrorKeyword_6; }
		
		/**
		 * Gets the warn keyword 7.
		 *
		 * @return the warn keyword 7
		 */
		//"warn"
		public Keyword getWarnKeyword_7() { return cWarnKeyword_7; }
		
		/**
		 * Gets the exception keyword 8.
		 *
		 * @return the exception keyword 8
		 */
		//"exception"
		public Keyword getExceptionKeyword_8() { return cExceptionKeyword_8; }
		
		/**
		 * Gets the save keyword 9.
		 *
		 * @return the save keyword 9
		 */
		//"save"
		public Keyword getSaveKeyword_9() { return cSaveKeyword_9; }
		
		/**
		 * Gets the assert keyword 10.
		 *
		 * @return the assert keyword 10
		 */
		//"assert"
		public Keyword getAssertKeyword_10() { return cAssertKeyword_10; }
		
		/**
		 * Gets the inspect keyword 11.
		 *
		 * @return the inspect keyword 11
		 */
		//"inspect"
		public Keyword getInspectKeyword_11() { return cInspectKeyword_11; }
		
		/**
		 * Gets the browse keyword 12.
		 *
		 * @return the browse keyword 12
		 */
		//"browse"
		public Keyword getBrowseKeyword_12() { return cBrowseKeyword_12; }
		
		/**
		 * Gets the draw keyword 13.
		 *
		 * @return the draw keyword 13
		 */
		//"draw"
		public Keyword getDrawKeyword_13() { return cDrawKeyword_13; }
		
		/**
		 * Gets the using keyword 14.
		 *
		 * @return the using keyword 14
		 */
		//"using"
		public Keyword getUsingKeyword_14() { return cUsingKeyword_14; }
		
		/**
		 * Gets the switch keyword 15.
		 *
		 * @return the switch keyword 15
		 */
		//"switch"
		public Keyword getSwitchKeyword_15() { return cSwitchKeyword_15; }
		
		/**
		 * Gets the put keyword 16.
		 *
		 * @return the put keyword 16
		 */
		//"put"
		public Keyword getPutKeyword_16() { return cPutKeyword_16; }
		
		/**
		 * Gets the adds the keyword 17.
		 *
		 * @return the adds the keyword 17
		 */
		//"add"
		public Keyword getAddKeyword_17() { return cAddKeyword_17; }
		
		/**
		 * Gets the removes the keyword 18.
		 *
		 * @return the removes the keyword 18
		 */
		//"remove"
		public Keyword getRemoveKeyword_18() { return cRemoveKeyword_18; }
		
		/**
		 * Gets the match keyword 19.
		 *
		 * @return the match keyword 19
		 */
		//"match"
		public Keyword getMatchKeyword_19() { return cMatchKeyword_19; }
		
		/**
		 * Gets the match between keyword 20.
		 *
		 * @return the match between keyword 20
		 */
		//"match_between"
		public Keyword getMatch_betweenKeyword_20() { return cMatch_betweenKeyword_20; }
		
		/**
		 * Gets the match one keyword 21.
		 *
		 * @return the match one keyword 21
		 */
		//"match_one"
		public Keyword getMatch_oneKeyword_21() { return cMatch_oneKeyword_21; }
		
		/**
		 * Gets the parameter keyword 22.
		 *
		 * @return the parameter keyword 22
		 */
		//"parameter"
		public Keyword getParameterKeyword_22() { return cParameterKeyword_22; }
		
		/**
		 * Gets the status keyword 23.
		 *
		 * @return the status keyword 23
		 */
		//"status"
		public Keyword getStatusKeyword_23() { return cStatusKeyword_23; }
		
		/**
		 * Gets the highlight keyword 24.
		 *
		 * @return the highlight keyword 24
		 */
		//"highlight"
		public Keyword getHighlightKeyword_24() { return cHighlightKeyword_24; }
		
		/**
		 * Gets the focus on keyword 25.
		 *
		 * @return the focus on keyword 25
		 */
		//"focus_on"
		public Keyword getFocus_onKeyword_25() { return cFocus_onKeyword_25; }
		
		/**
		 * Gets the layout keyword 26.
		 *
		 * @return the layout keyword 26
		 */
		//"layout"
		public Keyword getLayoutKeyword_26() { return cLayoutKeyword_26; }
	}
	
	/**
	 * The Class _LayerKeyElements.
	 */
	public class _LayerKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml._LayerKey");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c light keyword 0. */
		private final Keyword cLightKeyword_0 = (Keyword)cAlternatives.eContents().get(0);
		
		/** The c camera keyword 1. */
		private final Keyword cCameraKeyword_1 = (Keyword)cAlternatives.eContents().get(1);
		
		/** The c text keyword 2. */
		private final Keyword cTextKeyword_2 = (Keyword)cAlternatives.eContents().get(2);
		
		/** The c image keyword 3. */
		private final Keyword cImageKeyword_3 = (Keyword)cAlternatives.eContents().get(3);
		
		/** The c data keyword 4. */
		private final Keyword cDataKeyword_4 = (Keyword)cAlternatives.eContents().get(4);
		
		/** The c chart keyword 5. */
		private final Keyword cChartKeyword_5 = (Keyword)cAlternatives.eContents().get(5);
		
		/** The c agents keyword 6. */
		private final Keyword cAgentsKeyword_6 = (Keyword)cAlternatives.eContents().get(6);
		
		/** The c graphics keyword 7. */
		private final Keyword cGraphicsKeyword_7 = (Keyword)cAlternatives.eContents().get(7);
		
		/** The c display population keyword 8. */
		private final Keyword cDisplay_populationKeyword_8 = (Keyword)cAlternatives.eContents().get(8);
		
		/** The c display grid keyword 9. */
		private final Keyword cDisplay_gridKeyword_9 = (Keyword)cAlternatives.eContents().get(9);
		
		/** The c quadtree keyword 10. */
		private final Keyword cQuadtreeKeyword_10 = (Keyword)cAlternatives.eContents().get(10);
		
		/** The c event keyword 11. */
		private final Keyword cEventKeyword_11 = (Keyword)cAlternatives.eContents().get(11);
		
		/** The c overlay keyword 12. */
		private final Keyword cOverlayKeyword_12 = (Keyword)cAlternatives.eContents().get(12);
		
		/** The c datalist keyword 13. */
		private final Keyword cDatalistKeyword_13 = (Keyword)cAlternatives.eContents().get(13);
		
		/** The c mesh keyword 14. */
		private final Keyword cMeshKeyword_14 = (Keyword)cAlternatives.eContents().get(14);
		
		//_LayerKey:
		//    "light" | "camera" | "text" | "image" | "data" | "chart" | "agents" | "graphics" | "display_population" | "display_grid" | "quadtree" | "event" | "overlay" | "datalist" | "mesh"
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//"light" | "camera" | "text" | "image" | "data" | "chart" | "agents" | "graphics" | "display_population" | "display_grid" | "quadtree" | "event" | "overlay" | "datalist" | "mesh"
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the light keyword 0.
		 *
		 * @return the light keyword 0
		 */
		//"light"
		public Keyword getLightKeyword_0() { return cLightKeyword_0; }
		
		/**
		 * Gets the camera keyword 1.
		 *
		 * @return the camera keyword 1
		 */
		//"camera"
		public Keyword getCameraKeyword_1() { return cCameraKeyword_1; }
		
		/**
		 * Gets the text keyword 2.
		 *
		 * @return the text keyword 2
		 */
		//"text"
		public Keyword getTextKeyword_2() { return cTextKeyword_2; }
		
		/**
		 * Gets the image keyword 3.
		 *
		 * @return the image keyword 3
		 */
		//"image"
		public Keyword getImageKeyword_3() { return cImageKeyword_3; }
		
		/**
		 * Gets the data keyword 4.
		 *
		 * @return the data keyword 4
		 */
		//"data"
		public Keyword getDataKeyword_4() { return cDataKeyword_4; }
		
		/**
		 * Gets the chart keyword 5.
		 *
		 * @return the chart keyword 5
		 */
		//"chart"
		public Keyword getChartKeyword_5() { return cChartKeyword_5; }
		
		/**
		 * Gets the agents keyword 6.
		 *
		 * @return the agents keyword 6
		 */
		//"agents"
		public Keyword getAgentsKeyword_6() { return cAgentsKeyword_6; }
		
		/**
		 * Gets the graphics keyword 7.
		 *
		 * @return the graphics keyword 7
		 */
		//"graphics"
		public Keyword getGraphicsKeyword_7() { return cGraphicsKeyword_7; }
		
		/**
		 * Gets the display population keyword 8.
		 *
		 * @return the display population keyword 8
		 */
		//"display_population"
		public Keyword getDisplay_populationKeyword_8() { return cDisplay_populationKeyword_8; }
		
		/**
		 * Gets the display grid keyword 9.
		 *
		 * @return the display grid keyword 9
		 */
		//"display_grid"
		public Keyword getDisplay_gridKeyword_9() { return cDisplay_gridKeyword_9; }
		
		/**
		 * Gets the quadtree keyword 10.
		 *
		 * @return the quadtree keyword 10
		 */
		//"quadtree"
		public Keyword getQuadtreeKeyword_10() { return cQuadtreeKeyword_10; }
		
		/**
		 * Gets the event keyword 11.
		 *
		 * @return the event keyword 11
		 */
		//"event"
		public Keyword getEventKeyword_11() { return cEventKeyword_11; }
		
		/**
		 * Gets the overlay keyword 12.
		 *
		 * @return the overlay keyword 12
		 */
		//"overlay"
		public Keyword getOverlayKeyword_12() { return cOverlayKeyword_12; }
		
		/**
		 * Gets the datalist keyword 13.
		 *
		 * @return the datalist keyword 13
		 */
		//"datalist"
		public Keyword getDatalistKeyword_13() { return cDatalistKeyword_13; }
		
		/**
		 * Gets the mesh keyword 14.
		 *
		 * @return the mesh keyword 14
		 */
		//"mesh"
		public Keyword getMeshKeyword_14() { return cMeshKeyword_14; }
	}
	
	/**
	 * The Class _DoKeyElements.
	 */
	public class _DoKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml._DoKey");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c do keyword 0. */
		private final Keyword cDoKeyword_0 = (Keyword)cAlternatives.eContents().get(0);
		
		/** The c invoke keyword 1. */
		private final Keyword cInvokeKeyword_1 = (Keyword)cAlternatives.eContents().get(1);
		
		//_DoKey:
		//    "do" | "invoke";
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//"do" | "invoke"
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the do keyword 0.
		 *
		 * @return the do keyword 0
		 */
		//"do"
		public Keyword getDoKeyword_0() { return cDoKeyword_0; }
		
		/**
		 * Gets the invoke keyword 1.
		 *
		 * @return the invoke keyword 1
		 */
		//"invoke"
		public Keyword getInvokeKeyword_1() { return cInvokeKeyword_1; }
	}
	
	/**
	 * The Class _VarOrConstKeyElements.
	 */
	public class _VarOrConstKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml._VarOrConstKey");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c var keyword 0. */
		private final Keyword cVarKeyword_0 = (Keyword)cAlternatives.eContents().get(0);
		
		/** The c const keyword 1. */
		private final Keyword cConstKeyword_1 = (Keyword)cAlternatives.eContents().get(1);
		
		/** The c let keyword 2. */
		private final Keyword cLetKeyword_2 = (Keyword)cAlternatives.eContents().get(2);
		
		/** The c arg keyword 3. */
		private final Keyword cArgKeyword_3 = (Keyword)cAlternatives.eContents().get(3);
		
		//_VarOrConstKey:
		//    "var" | "const" | "let" | "arg" ;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//"var" | "const" | "let" | "arg"
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the var keyword 0.
		 *
		 * @return the var keyword 0
		 */
		//"var"
		public Keyword getVarKeyword_0() { return cVarKeyword_0; }
		
		/**
		 * Gets the const keyword 1.
		 *
		 * @return the const keyword 1
		 */
		//"const"
		public Keyword getConstKeyword_1() { return cConstKeyword_1; }
		
		/**
		 * Gets the let keyword 2.
		 *
		 * @return the let keyword 2
		 */
		//"let"
		public Keyword getLetKeyword_2() { return cLetKeyword_2; }
		
		/**
		 * Gets the arg keyword 3.
		 *
		 * @return the arg keyword 3
		 */
		//"arg"
		public Keyword getArgKeyword_3() { return cArgKeyword_3; }
	}
	
	/**
	 * The Class _ReflexKeyElements.
	 */
	public class _ReflexKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml._ReflexKey");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c init keyword 0. */
		private final Keyword cInitKeyword_0 = (Keyword)cAlternatives.eContents().get(0);
		
		/** The c reflex keyword 1. */
		private final Keyword cReflexKeyword_1 = (Keyword)cAlternatives.eContents().get(1);
		
		/** The c aspect keyword 2. */
		private final Keyword cAspectKeyword_2 = (Keyword)cAlternatives.eContents().get(2);
		
		//_ReflexKey:
		//    "init" | "reflex" | "aspect";
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//"init" | "reflex" | "aspect"
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the inits the keyword 0.
		 *
		 * @return the inits the keyword 0
		 */
		//"init"
		public Keyword getInitKeyword_0() { return cInitKeyword_0; }
		
		/**
		 * Gets the reflex keyword 1.
		 *
		 * @return the reflex keyword 1
		 */
		//"reflex"
		public Keyword getReflexKeyword_1() { return cReflexKeyword_1; }
		
		/**
		 * Gets the aspect keyword 2.
		 *
		 * @return the aspect keyword 2
		 */
		//"aspect"
		public Keyword getAspectKeyword_2() { return cAspectKeyword_2; }
	}
	
	/**
	 * The Class _AssignmentKeyElements.
	 */
	public class _AssignmentKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml._AssignmentKey");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c less than sign hyphen minus keyword 0. */
		private final Keyword cLessThanSignHyphenMinusKeyword_0 = (Keyword)cAlternatives.eContents().get(0);
		
		/** The c less than sign less than sign keyword 1. */
		private final Keyword cLessThanSignLessThanSignKeyword_1 = (Keyword)cAlternatives.eContents().get(1);
		
		/** The c group 2. */
		private final Group cGroup_2 = (Group)cAlternatives.eContents().get(2);
		
		/** The c greater than sign keyword 2 0. */
		private final Keyword cGreaterThanSignKeyword_2_0 = (Keyword)cGroup_2.eContents().get(0);
		
		/** The c greater than sign keyword 2 1. */
		private final Keyword cGreaterThanSignKeyword_2_1 = (Keyword)cGroup_2.eContents().get(1);
		
		/** The c less than sign less than sign plus sign keyword 3. */
		private final Keyword cLessThanSignLessThanSignPlusSignKeyword_3 = (Keyword)cAlternatives.eContents().get(3);
		
		/** The c group 4. */
		private final Group cGroup_4 = (Group)cAlternatives.eContents().get(4);
		
		/** The c greater than sign keyword 4 0. */
		private final Keyword cGreaterThanSignKeyword_4_0 = (Keyword)cGroup_4.eContents().get(0);
		
		/** The c greater than sign hyphen minus keyword 4 1. */
		private final Keyword cGreaterThanSignHyphenMinusKeyword_4_1 = (Keyword)cGroup_4.eContents().get(1);
		
		/** The c plus sign less than sign hyphen minus keyword 5. */
		private final Keyword cPlusSignLessThanSignHyphenMinusKeyword_5 = (Keyword)cAlternatives.eContents().get(5);
		
		/** The c less than sign plus sign keyword 6. */
		private final Keyword cLessThanSignPlusSignKeyword_6 = (Keyword)cAlternatives.eContents().get(6);
		
		/** The c greater than sign hyphen minus keyword 7. */
		private final Keyword cGreaterThanSignHyphenMinusKeyword_7 = (Keyword)cAlternatives.eContents().get(7);
		
		//_AssignmentKey:
		//    "<-" | "<<" | '>' '>' | "<<+" | '>''>-' | "+<-" | "<+" | ">-" ;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//"<-" | "<<" | '>' '>' | "<<+" | '>''>-' | "+<-" | "<+" | ">-"
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the less than sign hyphen minus keyword 0.
		 *
		 * @return the less than sign hyphen minus keyword 0
		 */
		//"<-"
		public Keyword getLessThanSignHyphenMinusKeyword_0() { return cLessThanSignHyphenMinusKeyword_0; }
		
		/**
		 * Gets the less than sign less than sign keyword 1.
		 *
		 * @return the less than sign less than sign keyword 1
		 */
		//"<<"
		public Keyword getLessThanSignLessThanSignKeyword_1() { return cLessThanSignLessThanSignKeyword_1; }
		
		/**
		 * Gets the group 2.
		 *
		 * @return the group 2
		 */
		//'>' '>'
		public Group getGroup_2() { return cGroup_2; }
		
		/**
		 * Gets the greater than sign keyword 2 0.
		 *
		 * @return the greater than sign keyword 2 0
		 */
		//'>'
		public Keyword getGreaterThanSignKeyword_2_0() { return cGreaterThanSignKeyword_2_0; }
		
		/**
		 * Gets the greater than sign keyword 2 1.
		 *
		 * @return the greater than sign keyword 2 1
		 */
		//'>'
		public Keyword getGreaterThanSignKeyword_2_1() { return cGreaterThanSignKeyword_2_1; }
		
		/**
		 * Gets the less than sign less than sign plus sign keyword 3.
		 *
		 * @return the less than sign less than sign plus sign keyword 3
		 */
		//"<<+"
		public Keyword getLessThanSignLessThanSignPlusSignKeyword_3() { return cLessThanSignLessThanSignPlusSignKeyword_3; }
		
		/**
		 * Gets the group 4.
		 *
		 * @return the group 4
		 */
		//'>''>-'
		public Group getGroup_4() { return cGroup_4; }
		
		/**
		 * Gets the greater than sign keyword 4 0.
		 *
		 * @return the greater than sign keyword 4 0
		 */
		//'>'
		public Keyword getGreaterThanSignKeyword_4_0() { return cGreaterThanSignKeyword_4_0; }
		
		/**
		 * Gets the greater than sign hyphen minus keyword 4 1.
		 *
		 * @return the greater than sign hyphen minus keyword 4 1
		 */
		//'>-'
		public Keyword getGreaterThanSignHyphenMinusKeyword_4_1() { return cGreaterThanSignHyphenMinusKeyword_4_1; }
		
		/**
		 * Gets the plus sign less than sign hyphen minus keyword 5.
		 *
		 * @return the plus sign less than sign hyphen minus keyword 5
		 */
		//"+<-"
		public Keyword getPlusSignLessThanSignHyphenMinusKeyword_5() { return cPlusSignLessThanSignHyphenMinusKeyword_5; }
		
		/**
		 * Gets the less than sign plus sign keyword 6.
		 *
		 * @return the less than sign plus sign keyword 6
		 */
		//"<+"
		public Keyword getLessThanSignPlusSignKeyword_6() { return cLessThanSignPlusSignKeyword_6; }
		
		/**
		 * Gets the greater than sign hyphen minus keyword 7.
		 *
		 * @return the greater than sign hyphen minus keyword 7
		 */
		//">-"
		public Keyword getGreaterThanSignHyphenMinusKeyword_7() { return cGreaterThanSignHyphenMinusKeyword_7; }
	}
	
	/**
	 * The Class ActionArgumentsElements.
	 */
	public class ActionArgumentsElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ActionArguments");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c args assignment 0. */
		private final Assignment cArgsAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c args argument definition parser rule call 0 0. */
		private final RuleCall cArgsArgumentDefinitionParserRuleCall_0_0 = (RuleCall)cArgsAssignment_0.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c comma keyword 1 0. */
		private final Keyword cCommaKeyword_1_0 = (Keyword)cGroup_1.eContents().get(0);
		
		/** The c args assignment 1 1. */
		private final Assignment cArgsAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		
		/** The c args argument definition parser rule call 1 1 0. */
		private final RuleCall cArgsArgumentDefinitionParserRuleCall_1_1_0 = (RuleCall)cArgsAssignment_1_1.eContents().get(0);
		
		//    /**
		// * Parameters and arguments
		// */
		////Parameters:
		////    {Parameters} (params=ParameterList)?;
		//ActionArguments:
		//    args+=ArgumentDefinition (',' args+=ArgumentDefinition)*;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//args+=ArgumentDefinition (',' args+=ArgumentDefinition)*
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the args assignment 0.
		 *
		 * @return the args assignment 0
		 */
		//args+=ArgumentDefinition
		public Assignment getArgsAssignment_0() { return cArgsAssignment_0; }
		
		/**
		 * Gets the args argument definition parser rule call 0 0.
		 *
		 * @return the args argument definition parser rule call 0 0
		 */
		//ArgumentDefinition
		public RuleCall getArgsArgumentDefinitionParserRuleCall_0_0() { return cArgsArgumentDefinitionParserRuleCall_0_0; }
		
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//(',' args+=ArgumentDefinition)*
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the comma keyword 1 0.
		 *
		 * @return the comma keyword 1 0
		 */
		//','
		public Keyword getCommaKeyword_1_0() { return cCommaKeyword_1_0; }
		
		/**
		 * Gets the args assignment 1 1.
		 *
		 * @return the args assignment 1 1
		 */
		//args+=ArgumentDefinition
		public Assignment getArgsAssignment_1_1() { return cArgsAssignment_1_1; }
		
		/**
		 * Gets the args argument definition parser rule call 1 1 0.
		 *
		 * @return the args argument definition parser rule call 1 1 0
		 */
		//ArgumentDefinition
		public RuleCall getArgsArgumentDefinitionParserRuleCall_1_1_0() { return cArgsArgumentDefinitionParserRuleCall_1_1_0; }
	}
	
	/**
	 * The Class ArgumentDefinitionElements.
	 */
	public class ArgumentDefinitionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ArgumentDefinition");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c type assignment 0. */
		private final Assignment cTypeAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c type type ref parser rule call 0 0. */
		private final RuleCall cTypeTypeRefParserRuleCall_0_0 = (RuleCall)cTypeAssignment_0.eContents().get(0);
		
		/** The c name assignment 1. */
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c name valid ID parser rule call 1 0. */
		private final RuleCall cNameValid_IDParserRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		
		/** The c group 2. */
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		
		/** The c less than sign hyphen minus keyword 2 0. */
		private final Keyword cLessThanSignHyphenMinusKeyword_2_0 = (Keyword)cGroup_2.eContents().get(0);
		
		/** The c default assignment 2 1. */
		private final Assignment cDefaultAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		
		/** The c default expression parser rule call 2 1 0. */
		private final RuleCall cDefaultExpressionParserRuleCall_2_1_0 = (RuleCall)cDefaultAssignment_2_1.eContents().get(0);
		
		//ArgumentDefinition:
		//    type=(TypeRef) name=Valid_ID ('<-' default=Expression)?;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//type=(TypeRef) name=Valid_ID ('<-' default=Expression)?
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the type assignment 0.
		 *
		 * @return the type assignment 0
		 */
		//type=(TypeRef)
		public Assignment getTypeAssignment_0() { return cTypeAssignment_0; }
		
		/**
		 * Gets the type type ref parser rule call 0 0.
		 *
		 * @return the type type ref parser rule call 0 0
		 */
		//(TypeRef)
		public RuleCall getTypeTypeRefParserRuleCall_0_0() { return cTypeTypeRefParserRuleCall_0_0; }
		
		/**
		 * Gets the name assignment 1.
		 *
		 * @return the name assignment 1
		 */
		//name=Valid_ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		/**
		 * Gets the name valid ID parser rule call 1 0.
		 *
		 * @return the name valid ID parser rule call 1 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_1_0() { return cNameValid_IDParserRuleCall_1_0; }
		
		/**
		 * Gets the group 2.
		 *
		 * @return the group 2
		 */
		//('<-' default=Expression)?
		public Group getGroup_2() { return cGroup_2; }
		
		/**
		 * Gets the less than sign hyphen minus keyword 2 0.
		 *
		 * @return the less than sign hyphen minus keyword 2 0
		 */
		//'<-'
		public Keyword getLessThanSignHyphenMinusKeyword_2_0() { return cLessThanSignHyphenMinusKeyword_2_0; }
		
		/**
		 * Gets the default assignment 2 1.
		 *
		 * @return the default assignment 2 1
		 */
		//default=Expression
		public Assignment getDefaultAssignment_2_1() { return cDefaultAssignment_2_1; }
		
		/**
		 * Gets the default expression parser rule call 2 1 0.
		 *
		 * @return the default expression parser rule call 2 1 0
		 */
		//Expression
		public RuleCall getDefaultExpressionParserRuleCall_2_1_0() { return cDefaultExpressionParserRuleCall_2_1_0; }
	}
	
	/**
	 * The Class FacetElements.
	 */
	public class FacetElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Facet");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c action facet parser rule call 0. */
		private final RuleCall cActionFacetParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c definition facet parser rule call 1. */
		private final RuleCall cDefinitionFacetParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		/** The c classic facet parser rule call 2. */
		private final RuleCall cClassicFacetParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		/** The c type facet parser rule call 3. */
		private final RuleCall cTypeFacetParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		
		/** The c var facet parser rule call 4. */
		private final RuleCall cVarFacetParserRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		
		/** The c function facet parser rule call 5. */
		private final RuleCall cFunctionFacetParserRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		
		//    /**
		// * Facets
		// */
		//Facet:
		//    ActionFacet | DefinitionFacet  | ClassicFacet | TypeFacet | VarFacet | FunctionFacet  ;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//ActionFacet | DefinitionFacet  | ClassicFacet | TypeFacet | VarFacet | FunctionFacet
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the action facet parser rule call 0.
		 *
		 * @return the action facet parser rule call 0
		 */
		//ActionFacet
		public RuleCall getActionFacetParserRuleCall_0() { return cActionFacetParserRuleCall_0; }
		
		/**
		 * Gets the definition facet parser rule call 1.
		 *
		 * @return the definition facet parser rule call 1
		 */
		//DefinitionFacet
		public RuleCall getDefinitionFacetParserRuleCall_1() { return cDefinitionFacetParserRuleCall_1; }
		
		/**
		 * Gets the classic facet parser rule call 2.
		 *
		 * @return the classic facet parser rule call 2
		 */
		//ClassicFacet
		public RuleCall getClassicFacetParserRuleCall_2() { return cClassicFacetParserRuleCall_2; }
		
		/**
		 * Gets the type facet parser rule call 3.
		 *
		 * @return the type facet parser rule call 3
		 */
		//TypeFacet
		public RuleCall getTypeFacetParserRuleCall_3() { return cTypeFacetParserRuleCall_3; }
		
		/**
		 * Gets the var facet parser rule call 4.
		 *
		 * @return the var facet parser rule call 4
		 */
		//VarFacet
		public RuleCall getVarFacetParserRuleCall_4() { return cVarFacetParserRuleCall_4; }
		
		/**
		 * Gets the function facet parser rule call 5.
		 *
		 * @return the function facet parser rule call 5
		 */
		//FunctionFacet
		public RuleCall getFunctionFacetParserRuleCall_5() { return cFunctionFacetParserRuleCall_5; }
	}
	
	/**
	 * The Class FirstFacetKeyElements.
	 */
	public class FirstFacetKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.FirstFacetKey");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c definition facet key parser rule call 0. */
		private final RuleCall cDefinitionFacetKeyParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c type facet key parser rule call 1. */
		private final RuleCall cTypeFacetKeyParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		/** The c special facet key parser rule call 2. */
		private final RuleCall cSpecialFacetKeyParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		/** The c var facet key parser rule call 3. */
		private final RuleCall cVarFacetKeyParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		
		/** The c action facet key parser rule call 4. */
		private final RuleCall cActionFacetKeyParserRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		
		/** The c classic facet key parser rule call 5. */
		private final RuleCall cClassicFacetKeyParserRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		
		//FirstFacetKey:
		//    DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | VarFacetKey | ActionFacetKey | ClassicFacetKey ;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | VarFacetKey | ActionFacetKey | ClassicFacetKey
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the definition facet key parser rule call 0.
		 *
		 * @return the definition facet key parser rule call 0
		 */
		//DefinitionFacetKey
		public RuleCall getDefinitionFacetKeyParserRuleCall_0() { return cDefinitionFacetKeyParserRuleCall_0; }
		
		/**
		 * Gets the type facet key parser rule call 1.
		 *
		 * @return the type facet key parser rule call 1
		 */
		//TypeFacetKey
		public RuleCall getTypeFacetKeyParserRuleCall_1() { return cTypeFacetKeyParserRuleCall_1; }
		
		/**
		 * Gets the special facet key parser rule call 2.
		 *
		 * @return the special facet key parser rule call 2
		 */
		//SpecialFacetKey
		public RuleCall getSpecialFacetKeyParserRuleCall_2() { return cSpecialFacetKeyParserRuleCall_2; }
		
		/**
		 * Gets the var facet key parser rule call 3.
		 *
		 * @return the var facet key parser rule call 3
		 */
		//VarFacetKey
		public RuleCall getVarFacetKeyParserRuleCall_3() { return cVarFacetKeyParserRuleCall_3; }
		
		/**
		 * Gets the action facet key parser rule call 4.
		 *
		 * @return the action facet key parser rule call 4
		 */
		//ActionFacetKey
		public RuleCall getActionFacetKeyParserRuleCall_4() { return cActionFacetKeyParserRuleCall_4; }
		
		/**
		 * Gets the classic facet key parser rule call 5.
		 *
		 * @return the classic facet key parser rule call 5
		 */
		//ClassicFacetKey
		public RuleCall getClassicFacetKeyParserRuleCall_5() { return cClassicFacetKeyParserRuleCall_5; }
	}
	
	/**
	 * The Class ClassicFacetKeyElements.
	 */
	public class ClassicFacetKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ClassicFacetKey");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c ID terminal rule call 0. */
		private final RuleCall cIDTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		
		/** The c colon keyword 1. */
		private final Keyword cColonKeyword_1 = (Keyword)cGroup.eContents().get(1);
		
		//ClassicFacetKey: (ID ':');
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//(ID ':')
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the ID terminal rule call 0.
		 *
		 * @return the ID terminal rule call 0
		 */
		//ID
		public RuleCall getIDTerminalRuleCall_0() { return cIDTerminalRuleCall_0; }
		
		/**
		 * Gets the colon keyword 1.
		 *
		 * @return the colon keyword 1
		 */
		//':'
		public Keyword getColonKeyword_1() { return cColonKeyword_1; }
	}
	
	/**
	 * The Class DefinitionFacetKeyElements.
	 */
	public class DefinitionFacetKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.DefinitionFacetKey");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c name keyword 0. */
		private final Keyword cNameKeyword_0 = (Keyword)cAlternatives.eContents().get(0);
		
		/** The c returns keyword 1. */
		private final Keyword cReturnsKeyword_1 = (Keyword)cAlternatives.eContents().get(1);
		
		//DefinitionFacetKey:
		//    "name:" | "returns:" ;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//"name:" | "returns:"
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the name keyword 0.
		 *
		 * @return the name keyword 0
		 */
		//"name:"
		public Keyword getNameKeyword_0() { return cNameKeyword_0; }
		
		/**
		 * Gets the returns keyword 1.
		 *
		 * @return the returns keyword 1
		 */
		//"returns:"
		public Keyword getReturnsKeyword_1() { return cReturnsKeyword_1; }
	}
	
	/**
	 * The Class TypeFacetKeyElements.
	 */
	public class TypeFacetKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.TypeFacetKey");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c as keyword 0. */
		private final Keyword cAsKeyword_0 = (Keyword)cAlternatives.eContents().get(0);
		
		/** The c of keyword 1. */
		private final Keyword cOfKeyword_1 = (Keyword)cAlternatives.eContents().get(1);
		
		/** The c parent keyword 2. */
		private final Keyword cParentKeyword_2 = (Keyword)cAlternatives.eContents().get(2);
		
		/** The c species keyword 3. */
		private final Keyword cSpeciesKeyword_3 = (Keyword)cAlternatives.eContents().get(3);
		
		/** The c type keyword 4. */
		private final Keyword cTypeKeyword_4 = (Keyword)cAlternatives.eContents().get(4);
		
		// /*| "var:" */
		//TypeFacetKey:
		//    ("as:" | "of:" | "parent:" | "species:"|"type:");
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//("as:" | "of:" | "parent:" | "species:"|"type:")
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the as keyword 0.
		 *
		 * @return the as keyword 0
		 */
		//"as:"
		public Keyword getAsKeyword_0() { return cAsKeyword_0; }
		
		/**
		 * Gets the of keyword 1.
		 *
		 * @return the of keyword 1
		 */
		//"of:"
		public Keyword getOfKeyword_1() { return cOfKeyword_1; }
		
		/**
		 * Gets the parent keyword 2.
		 *
		 * @return the parent keyword 2
		 */
		//"parent:"
		public Keyword getParentKeyword_2() { return cParentKeyword_2; }
		
		/**
		 * Gets the species keyword 3.
		 *
		 * @return the species keyword 3
		 */
		//"species:"
		public Keyword getSpeciesKeyword_3() { return cSpeciesKeyword_3; }
		
		/**
		 * Gets the type keyword 4.
		 *
		 * @return the type keyword 4
		 */
		//"type:"
		public Keyword getTypeKeyword_4() { return cTypeKeyword_4; }
	}
	
	/**
	 * The Class SpecialFacetKeyElements.
	 */
	public class SpecialFacetKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.SpecialFacetKey");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c data keyword 0. */
		private final Keyword cDataKeyword_0 = (Keyword)cAlternatives.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		
		/** The c when keyword 1 0. */
		private final Keyword cWhenKeyword_1_0 = (Keyword)cGroup_1.eContents().get(0);
		
		/** The c colon keyword 1 1. */
		private final Keyword cColonKeyword_1_1 = (Keyword)cGroup_1.eContents().get(1);
		
		/** The c const keyword 2. */
		private final Keyword cConstKeyword_2 = (Keyword)cAlternatives.eContents().get(2);
		
		/** The c value keyword 3. */
		private final Keyword cValueKeyword_3 = (Keyword)cAlternatives.eContents().get(3);
		
		/** The c topology keyword 4. */
		private final Keyword cTopologyKeyword_4 = (Keyword)cAlternatives.eContents().get(4);
		
		/** The c item keyword 5. */
		private final Keyword cItemKeyword_5 = (Keyword)cAlternatives.eContents().get(5);
		
		/** The c init keyword 6. */
		private final Keyword cInitKeyword_6 = (Keyword)cAlternatives.eContents().get(6);
		
		/** The c message keyword 7. */
		private final Keyword cMessageKeyword_7 = (Keyword)cAlternatives.eContents().get(7);
		
		/** The c control keyword 8. */
		private final Keyword cControlKeyword_8 = (Keyword)cAlternatives.eContents().get(8);
		
		/** The c layout keyword 9. */
		private final Keyword cLayoutKeyword_9 = (Keyword)cAlternatives.eContents().get(9);
		
		/** The c environment keyword 10. */
		private final Keyword cEnvironmentKeyword_10 = (Keyword)cAlternatives.eContents().get(10);
		
		/** The c text keyword 11. */
		private final Keyword cTextKeyword_11 = (Keyword)cAlternatives.eContents().get(11);
		
		/** The c image keyword 12. */
		private final Keyword cImageKeyword_12 = (Keyword)cAlternatives.eContents().get(12);
		
		/** The c using keyword 13. */
		private final Keyword cUsingKeyword_13 = (Keyword)cAlternatives.eContents().get(13);
		
		/** The c parameter keyword 14. */
		private final Keyword cParameterKeyword_14 = (Keyword)cAlternatives.eContents().get(14);
		
		/** The c aspect keyword 15. */
		private final Keyword cAspectKeyword_15 = (Keyword)cAlternatives.eContents().get(15);
		
		/** The c light keyword 16. */
		private final Keyword cLightKeyword_16 = (Keyword)cAlternatives.eContents().get(16);
		
		//SpecialFacetKey:
		//    'data:' | 'when'':' | "const:" | "value:" | "topology:" | "item:" | "init:" | "message:" | "control:" | "layout:" |
		//    "environment:" | 'text:' | 'image:' | 'using:' | "parameter:" | "aspect:" | "light:";
		@Override public ParserRule getRule() { return rule; }
		
		//'data:' | 'when'':' | "const:" | "value:" | "topology:" | "item:" | "init:" | "message:" | "control:" | "layout:" |
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//"environment:" | 'text:' | 'image:' | 'using:' | "parameter:" | "aspect:" | "light:"
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the data keyword 0.
		 *
		 * @return the data keyword 0
		 */
		//'data:'
		public Keyword getDataKeyword_0() { return cDataKeyword_0; }
		
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//'when'':'
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the when keyword 1 0.
		 *
		 * @return the when keyword 1 0
		 */
		//'when'
		public Keyword getWhenKeyword_1_0() { return cWhenKeyword_1_0; }
		
		/**
		 * Gets the colon keyword 1 1.
		 *
		 * @return the colon keyword 1 1
		 */
		//':'
		public Keyword getColonKeyword_1_1() { return cColonKeyword_1_1; }
		
		/**
		 * Gets the const keyword 2.
		 *
		 * @return the const keyword 2
		 */
		//"const:"
		public Keyword getConstKeyword_2() { return cConstKeyword_2; }
		
		/**
		 * Gets the value keyword 3.
		 *
		 * @return the value keyword 3
		 */
		//"value:"
		public Keyword getValueKeyword_3() { return cValueKeyword_3; }
		
		/**
		 * Gets the topology keyword 4.
		 *
		 * @return the topology keyword 4
		 */
		//"topology:"
		public Keyword getTopologyKeyword_4() { return cTopologyKeyword_4; }
		
		/**
		 * Gets the item keyword 5.
		 *
		 * @return the item keyword 5
		 */
		//"item:"
		public Keyword getItemKeyword_5() { return cItemKeyword_5; }
		
		/**
		 * Gets the inits the keyword 6.
		 *
		 * @return the inits the keyword 6
		 */
		//"init:"
		public Keyword getInitKeyword_6() { return cInitKeyword_6; }
		
		/**
		 * Gets the message keyword 7.
		 *
		 * @return the message keyword 7
		 */
		//"message:"
		public Keyword getMessageKeyword_7() { return cMessageKeyword_7; }
		
		/**
		 * Gets the control keyword 8.
		 *
		 * @return the control keyword 8
		 */
		//"control:"
		public Keyword getControlKeyword_8() { return cControlKeyword_8; }
		
		/**
		 * Gets the layout keyword 9.
		 *
		 * @return the layout keyword 9
		 */
		//"layout:"
		public Keyword getLayoutKeyword_9() { return cLayoutKeyword_9; }
		
		/**
		 * Gets the environment keyword 10.
		 *
		 * @return the environment keyword 10
		 */
		//"environment:"
		public Keyword getEnvironmentKeyword_10() { return cEnvironmentKeyword_10; }
		
		/**
		 * Gets the text keyword 11.
		 *
		 * @return the text keyword 11
		 */
		//'text:'
		public Keyword getTextKeyword_11() { return cTextKeyword_11; }
		
		/**
		 * Gets the image keyword 12.
		 *
		 * @return the image keyword 12
		 */
		//'image:'
		public Keyword getImageKeyword_12() { return cImageKeyword_12; }
		
		/**
		 * Gets the using keyword 13.
		 *
		 * @return the using keyword 13
		 */
		//'using:'
		public Keyword getUsingKeyword_13() { return cUsingKeyword_13; }
		
		/**
		 * Gets the parameter keyword 14.
		 *
		 * @return the parameter keyword 14
		 */
		//"parameter:"
		public Keyword getParameterKeyword_14() { return cParameterKeyword_14; }
		
		/**
		 * Gets the aspect keyword 15.
		 *
		 * @return the aspect keyword 15
		 */
		//"aspect:"
		public Keyword getAspectKeyword_15() { return cAspectKeyword_15; }
		
		/**
		 * Gets the light keyword 16.
		 *
		 * @return the light keyword 16
		 */
		//"light:"
		public Keyword getLightKeyword_16() { return cLightKeyword_16; }
	}
	
	/**
	 * The Class ActionFacetKeyElements.
	 */
	public class ActionFacetKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ActionFacetKey");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c action keyword 0. */
		private final Keyword cActionKeyword_0 = (Keyword)cAlternatives.eContents().get(0);
		
		/** The c on change keyword 1. */
		private final Keyword cOn_changeKeyword_1 = (Keyword)cAlternatives.eContents().get(1);
		
		//ActionFacetKey:
		//    "action:" | "on_change:";
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//"action:" | "on_change:"
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the action keyword 0.
		 *
		 * @return the action keyword 0
		 */
		//"action:"
		public Keyword getActionKeyword_0() { return cActionKeyword_0; }
		
		/**
		 * Gets the on change keyword 1.
		 *
		 * @return the on change keyword 1
		 */
		//"on_change:"
		public Keyword getOn_changeKeyword_1() { return cOn_changeKeyword_1; }
	}
	
	/**
	 * The Class VarFacetKeyElements.
	 */
	public class VarFacetKeyElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.VarFacetKey");
		
		/** The c var keyword. */
		private final Keyword cVarKeyword = (Keyword)rule.eContents().get(1);
		
		//VarFacetKey:
		//    "var:";
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the var keyword.
		 *
		 * @return the var keyword
		 */
		//"var:"
		public Keyword getVarKeyword() { return cVarKeyword; }
	}
	
	/**
	 * The Class ClassicFacetElements.
	 */
	public class ClassicFacetElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ClassicFacet");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c alternatives 0. */
		private final Alternatives cAlternatives_0 = (Alternatives)cGroup.eContents().get(0);
		
		/** The c key assignment 0 0. */
		private final Assignment cKeyAssignment_0_0 = (Assignment)cAlternatives_0.eContents().get(0);
		
		/** The c key classic facet key parser rule call 0 0 0. */
		private final RuleCall cKeyClassicFacetKeyParserRuleCall_0_0_0 = (RuleCall)cKeyAssignment_0_0.eContents().get(0);
		
		/** The c key assignment 0 1. */
		private final Assignment cKeyAssignment_0_1 = (Assignment)cAlternatives_0.eContents().get(1);
		
		/** The c key less than sign hyphen minus keyword 0 1 0. */
		private final Keyword cKeyLessThanSignHyphenMinusKeyword_0_1_0 = (Keyword)cKeyAssignment_0_1.eContents().get(0);
		
		/** The c key assignment 0 2. */
		private final Assignment cKeyAssignment_0_2 = (Assignment)cAlternatives_0.eContents().get(2);
		
		/** The c key special facet key parser rule call 0 2 0. */
		private final RuleCall cKeySpecialFacetKeyParserRuleCall_0_2_0 = (RuleCall)cKeyAssignment_0_2.eContents().get(0);
		
		/** The c expr assignment 1. */
		private final Assignment cExprAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c expr expression parser rule call 1 0. */
		private final RuleCall cExprExpressionParserRuleCall_1_0 = (RuleCall)cExprAssignment_1.eContents().get(0);
		
		//ClassicFacet returns Facet:
		//    (key=ClassicFacetKey | key='<-' | key=SpecialFacetKey) expr=Expression;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//(key=ClassicFacetKey | key='<-' | key=SpecialFacetKey) expr=Expression
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the alternatives 0.
		 *
		 * @return the alternatives 0
		 */
		//(key=ClassicFacetKey | key='<-' | key=SpecialFacetKey)
		public Alternatives getAlternatives_0() { return cAlternatives_0; }
		
		/**
		 * Gets the key assignment 0 0.
		 *
		 * @return the key assignment 0 0
		 */
		//key=ClassicFacetKey
		public Assignment getKeyAssignment_0_0() { return cKeyAssignment_0_0; }
		
		/**
		 * Gets the key classic facet key parser rule call 0 0 0.
		 *
		 * @return the key classic facet key parser rule call 0 0 0
		 */
		//ClassicFacetKey
		public RuleCall getKeyClassicFacetKeyParserRuleCall_0_0_0() { return cKeyClassicFacetKeyParserRuleCall_0_0_0; }
		
		/**
		 * Gets the key assignment 0 1.
		 *
		 * @return the key assignment 0 1
		 */
		//key='<-'
		public Assignment getKeyAssignment_0_1() { return cKeyAssignment_0_1; }
		
		/**
		 * Gets the key less than sign hyphen minus keyword 0 1 0.
		 *
		 * @return the key less than sign hyphen minus keyword 0 1 0
		 */
		//'<-'
		public Keyword getKeyLessThanSignHyphenMinusKeyword_0_1_0() { return cKeyLessThanSignHyphenMinusKeyword_0_1_0; }
		
		/**
		 * Gets the key assignment 0 2.
		 *
		 * @return the key assignment 0 2
		 */
		//key=SpecialFacetKey
		public Assignment getKeyAssignment_0_2() { return cKeyAssignment_0_2; }
		
		/**
		 * Gets the key special facet key parser rule call 0 2 0.
		 *
		 * @return the key special facet key parser rule call 0 2 0
		 */
		//SpecialFacetKey
		public RuleCall getKeySpecialFacetKeyParserRuleCall_0_2_0() { return cKeySpecialFacetKeyParserRuleCall_0_2_0; }
		
		/**
		 * Gets the expr assignment 1.
		 *
		 * @return the expr assignment 1
		 */
		//expr=Expression
		public Assignment getExprAssignment_1() { return cExprAssignment_1; }
		
		/**
		 * Gets the expr expression parser rule call 1 0.
		 *
		 * @return the expr expression parser rule call 1 0
		 */
		//Expression
		public RuleCall getExprExpressionParserRuleCall_1_0() { return cExprExpressionParserRuleCall_1_0; }
	}
	
	/**
	 * The Class DefinitionFacetElements.
	 */
	public class DefinitionFacetElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.DefinitionFacet");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key definition facet key parser rule call 0 0. */
		private final RuleCall cKeyDefinitionFacetKeyParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c name assignment 1. */
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c name alternatives 1 0. */
		private final Alternatives cNameAlternatives_1_0 = (Alternatives)cNameAssignment_1.eContents().get(0);
		
		/** The c name valid ID parser rule call 1 0 0. */
		private final RuleCall cNameValid_IDParserRuleCall_1_0_0 = (RuleCall)cNameAlternatives_1_0.eContents().get(0);
		
		/** The c name STRING terminal rule call 1 0 1. */
		private final RuleCall cNameSTRINGTerminalRuleCall_1_0_1 = (RuleCall)cNameAlternatives_1_0.eContents().get(1);
		
		//DefinitionFacet returns Facet:
		//    ((-> key=DefinitionFacetKey) name=(Valid_ID | STRING));
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//((-> key=DefinitionFacetKey) name=(Valid_ID | STRING))
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//(-> key=DefinitionFacetKey)
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key definition facet key parser rule call 0 0.
		 *
		 * @return the key definition facet key parser rule call 0 0
		 */
		//DefinitionFacetKey
		public RuleCall getKeyDefinitionFacetKeyParserRuleCall_0_0() { return cKeyDefinitionFacetKeyParserRuleCall_0_0; }
		
		/**
		 * Gets the name assignment 1.
		 *
		 * @return the name assignment 1
		 */
		//name=(Valid_ID | STRING)
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		/**
		 * Gets the name alternatives 1 0.
		 *
		 * @return the name alternatives 1 0
		 */
		//(Valid_ID | STRING)
		public Alternatives getNameAlternatives_1_0() { return cNameAlternatives_1_0; }
		
		/**
		 * Gets the name valid ID parser rule call 1 0 0.
		 *
		 * @return the name valid ID parser rule call 1 0 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_1_0_0() { return cNameValid_IDParserRuleCall_1_0_0; }
		
		/**
		 * Gets the name STRING terminal rule call 1 0 1.
		 *
		 * @return the name STRING terminal rule call 1 0 1
		 */
		//STRING
		public RuleCall getNameSTRINGTerminalRuleCall_1_0_1() { return cNameSTRINGTerminalRuleCall_1_0_1; }
	}
	
	/**
	 * The Class FunctionFacetElements.
	 */
	public class FunctionFacetElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.FunctionFacet");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key hyphen minus greater than sign keyword 0 0. */
		private final Keyword cKeyHyphenMinusGreaterThanSignKeyword_0_0 = (Keyword)cKeyAssignment_0.eContents().get(0);
		
		/** The c alternatives 1. */
		private final Alternatives cAlternatives_1 = (Alternatives)cGroup.eContents().get(1);
		
		/** The c group 1 0. */
		private final Group cGroup_1_0 = (Group)cAlternatives_1.eContents().get(0);
		
		/** The c expr assignment 1 0 0. */
		private final Assignment cExprAssignment_1_0_0 = (Assignment)cGroup_1_0.eContents().get(0);
		
		/** The c expr expression parser rule call 1 0 0 0. */
		private final RuleCall cExprExpressionParserRuleCall_1_0_0_0 = (RuleCall)cExprAssignment_1_0_0.eContents().get(0);
		
		/** The c group 1 1. */
		private final Group cGroup_1_1 = (Group)cAlternatives_1.eContents().get(1);
		
		/** The c left curly bracket keyword 1 1 0. */
		private final Keyword cLeftCurlyBracketKeyword_1_1_0 = (Keyword)cGroup_1_1.eContents().get(0);
		
		/** The c expr assignment 1 1 1. */
		private final Assignment cExprAssignment_1_1_1 = (Assignment)cGroup_1_1.eContents().get(1);
		
		/** The c expr expression parser rule call 1 1 1 0. */
		private final RuleCall cExprExpressionParserRuleCall_1_1_1_0 = (RuleCall)cExprAssignment_1_1_1.eContents().get(0);
		
		/** The c right curly bracket keyword 1 1 2. */
		private final Keyword cRightCurlyBracketKeyword_1_1_2 = (Keyword)cGroup_1_1.eContents().get(2);
		
		//FunctionFacet returns Facet:
		//    key = '->' (=>(expr= Expression) | ('{' expr=Expression '}'))
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key = '->' (=>(expr= Expression) | ('{' expr=Expression '}'))
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key = '->'
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key hyphen minus greater than sign keyword 0 0.
		 *
		 * @return the key hyphen minus greater than sign keyword 0 0
		 */
		//'->'
		public Keyword getKeyHyphenMinusGreaterThanSignKeyword_0_0() { return cKeyHyphenMinusGreaterThanSignKeyword_0_0; }
		
		/**
		 * Gets the alternatives 1.
		 *
		 * @return the alternatives 1
		 */
		//(=>(expr= Expression) | ('{' expr=Expression '}'))
		public Alternatives getAlternatives_1() { return cAlternatives_1; }
		
		/**
		 * Gets the group 1 0.
		 *
		 * @return the group 1 0
		 */
		//=>(expr= Expression)
		public Group getGroup_1_0() { return cGroup_1_0; }
		
		/**
		 * Gets the expr assignment 1 0 0.
		 *
		 * @return the expr assignment 1 0 0
		 */
		//expr= Expression
		public Assignment getExprAssignment_1_0_0() { return cExprAssignment_1_0_0; }
		
		/**
		 * Gets the expr expression parser rule call 1 0 0 0.
		 *
		 * @return the expr expression parser rule call 1 0 0 0
		 */
		//Expression
		public RuleCall getExprExpressionParserRuleCall_1_0_0_0() { return cExprExpressionParserRuleCall_1_0_0_0; }
		
		/**
		 * Gets the group 1 1.
		 *
		 * @return the group 1 1
		 */
		//('{' expr=Expression '}')
		public Group getGroup_1_1() { return cGroup_1_1; }
		
		/**
		 * Gets the left curly bracket keyword 1 1 0.
		 *
		 * @return the left curly bracket keyword 1 1 0
		 */
		//'{'
		public Keyword getLeftCurlyBracketKeyword_1_1_0() { return cLeftCurlyBracketKeyword_1_1_0; }
		
		/**
		 * Gets the expr assignment 1 1 1.
		 *
		 * @return the expr assignment 1 1 1
		 */
		//expr=Expression
		public Assignment getExprAssignment_1_1_1() { return cExprAssignment_1_1_1; }
		
		/**
		 * Gets the expr expression parser rule call 1 1 1 0.
		 *
		 * @return the expr expression parser rule call 1 1 1 0
		 */
		//Expression
		public RuleCall getExprExpressionParserRuleCall_1_1_1_0() { return cExprExpressionParserRuleCall_1_1_1_0; }
		
		/**
		 * Gets the right curly bracket keyword 1 1 2.
		 *
		 * @return the right curly bracket keyword 1 1 2
		 */
		//'}'
		public Keyword getRightCurlyBracketKeyword_1_1_2() { return cRightCurlyBracketKeyword_1_1_2; }
	}
	
	/**
	 * The Class TypeFacetElements.
	 */
	public class TypeFacetElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.TypeFacet");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key type facet key parser rule call 0 0. */
		private final RuleCall cKeyTypeFacetKeyParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c alternatives 1. */
		private final Alternatives cAlternatives_1 = (Alternatives)cGroup.eContents().get(1);
		
		/** The c group 1 0. */
		private final Group cGroup_1_0 = (Group)cAlternatives_1.eContents().get(0);
		
		/** The c expr assignment 1 0 0. */
		private final Assignment cExprAssignment_1_0_0 = (Assignment)cGroup_1_0.eContents().get(0);
		
		/** The c expr type ref parser rule call 1 0 0 0. */
		private final RuleCall cExprTypeRefParserRuleCall_1_0_0_0 = (RuleCall)cExprAssignment_1_0_0.eContents().get(0);
		
		/** The c expr assignment 1 1. */
		private final Assignment cExprAssignment_1_1 = (Assignment)cAlternatives_1.eContents().get(1);
		
		/** The c expr expression parser rule call 1 1 0. */
		private final RuleCall cExprExpressionParserRuleCall_1_1_0 = (RuleCall)cExprAssignment_1_1.eContents().get(0);
		
		//TypeFacet returns Facet:
		//    key=TypeFacetKey (->(expr=TypeRef) | expr= Expression);
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key=TypeFacetKey (->(expr=TypeRef) | expr= Expression)
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key=TypeFacetKey
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key type facet key parser rule call 0 0.
		 *
		 * @return the key type facet key parser rule call 0 0
		 */
		//TypeFacetKey
		public RuleCall getKeyTypeFacetKeyParserRuleCall_0_0() { return cKeyTypeFacetKeyParserRuleCall_0_0; }
		
		/**
		 * Gets the alternatives 1.
		 *
		 * @return the alternatives 1
		 */
		//(->(expr=TypeRef) | expr= Expression)
		public Alternatives getAlternatives_1() { return cAlternatives_1; }
		
		/**
		 * Gets the group 1 0.
		 *
		 * @return the group 1 0
		 */
		//->(expr=TypeRef)
		public Group getGroup_1_0() { return cGroup_1_0; }
		
		/**
		 * Gets the expr assignment 1 0 0.
		 *
		 * @return the expr assignment 1 0 0
		 */
		//expr=TypeRef
		public Assignment getExprAssignment_1_0_0() { return cExprAssignment_1_0_0; }
		
		/**
		 * Gets the expr type ref parser rule call 1 0 0 0.
		 *
		 * @return the expr type ref parser rule call 1 0 0 0
		 */
		//TypeRef
		public RuleCall getExprTypeRefParserRuleCall_1_0_0_0() { return cExprTypeRefParserRuleCall_1_0_0_0; }
		
		/**
		 * Gets the expr assignment 1 1.
		 *
		 * @return the expr assignment 1 1
		 */
		//expr= Expression
		public Assignment getExprAssignment_1_1() { return cExprAssignment_1_1; }
		
		/**
		 * Gets the expr expression parser rule call 1 1 0.
		 *
		 * @return the expr expression parser rule call 1 1 0
		 */
		//Expression
		public RuleCall getExprExpressionParserRuleCall_1_1_0() { return cExprExpressionParserRuleCall_1_1_0; }
	}
	
	/**
	 * The Class ActionFacetElements.
	 */
	public class ActionFacetElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ActionFacet");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key action facet key parser rule call 0 0. */
		private final RuleCall cKeyActionFacetKeyParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c alternatives 1. */
		private final Alternatives cAlternatives_1 = (Alternatives)cGroup.eContents().get(1);
		
		/** The c expr assignment 1 0. */
		private final Assignment cExprAssignment_1_0 = (Assignment)cAlternatives_1.eContents().get(0);
		
		/** The c expr action ref parser rule call 1 0 0. */
		private final RuleCall cExprActionRefParserRuleCall_1_0_0 = (RuleCall)cExprAssignment_1_0.eContents().get(0);
		
		/** The c block assignment 1 1. */
		private final Assignment cBlockAssignment_1_1 = (Assignment)cAlternatives_1.eContents().get(1);
		
		/** The c block block parser rule call 1 1 0. */
		private final RuleCall cBlockBlockParserRuleCall_1_1_0 = (RuleCall)cBlockAssignment_1_1.eContents().get(0);
		
		//ActionFacet returns Facet:
		//    key=ActionFacetKey (expr=ActionRef | block=Block);
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key=ActionFacetKey (expr=ActionRef | block=Block)
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key=ActionFacetKey
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key action facet key parser rule call 0 0.
		 *
		 * @return the key action facet key parser rule call 0 0
		 */
		//ActionFacetKey
		public RuleCall getKeyActionFacetKeyParserRuleCall_0_0() { return cKeyActionFacetKeyParserRuleCall_0_0; }
		
		/**
		 * Gets the alternatives 1.
		 *
		 * @return the alternatives 1
		 */
		//(expr=ActionRef | block=Block)
		public Alternatives getAlternatives_1() { return cAlternatives_1; }
		
		/**
		 * Gets the expr assignment 1 0.
		 *
		 * @return the expr assignment 1 0
		 */
		//expr=ActionRef
		public Assignment getExprAssignment_1_0() { return cExprAssignment_1_0; }
		
		/**
		 * Gets the expr action ref parser rule call 1 0 0.
		 *
		 * @return the expr action ref parser rule call 1 0 0
		 */
		//ActionRef
		public RuleCall getExprActionRefParserRuleCall_1_0_0() { return cExprActionRefParserRuleCall_1_0_0; }
		
		/**
		 * Gets the block assignment 1 1.
		 *
		 * @return the block assignment 1 1
		 */
		//block=Block
		public Assignment getBlockAssignment_1_1() { return cBlockAssignment_1_1; }
		
		/**
		 * Gets the block block parser rule call 1 1 0.
		 *
		 * @return the block block parser rule call 1 1 0
		 */
		//Block
		public RuleCall getBlockBlockParserRuleCall_1_1_0() { return cBlockBlockParserRuleCall_1_1_0; }
	}
	
	/**
	 * The Class VarFacetElements.
	 */
	public class VarFacetElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.VarFacet");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c key assignment 0. */
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		
		/** The c key var facet key parser rule call 0 0. */
		private final RuleCall cKeyVarFacetKeyParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		
		/** The c expr assignment 1. */
		private final Assignment cExprAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c expr variable ref parser rule call 1 0. */
		private final RuleCall cExprVariableRefParserRuleCall_1_0 = (RuleCall)cExprAssignment_1.eContents().get(0);
		
		//VarFacet returns Facet:
		//    key= VarFacetKey expr=VariableRef;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//key= VarFacetKey expr=VariableRef
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the key assignment 0.
		 *
		 * @return the key assignment 0
		 */
		//key= VarFacetKey
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }
		
		/**
		 * Gets the key var facet key parser rule call 0 0.
		 *
		 * @return the key var facet key parser rule call 0 0
		 */
		//VarFacetKey
		public RuleCall getKeyVarFacetKeyParserRuleCall_0_0() { return cKeyVarFacetKeyParserRuleCall_0_0; }
		
		/**
		 * Gets the expr assignment 1.
		 *
		 * @return the expr assignment 1
		 */
		//expr=VariableRef
		public Assignment getExprAssignment_1() { return cExprAssignment_1; }
		
		/**
		 * Gets the expr variable ref parser rule call 1 0.
		 *
		 * @return the expr variable ref parser rule call 1 0
		 */
		//VariableRef
		public RuleCall getExprVariableRefParserRuleCall_1_0() { return cExprVariableRefParserRuleCall_1_0; }
	}
	
	/**
	 * The Class BlockElements.
	 */
	public class BlockElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Block");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c block action 0. */
		private final Action cBlockAction_0 = (Action)cGroup.eContents().get(0);
		
		/** The c left curly bracket keyword 1. */
		private final Keyword cLeftCurlyBracketKeyword_1 = (Keyword)cGroup.eContents().get(1);
		
		/** The c group 2. */
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		
		/** The c statements assignment 2 0. */
		private final Assignment cStatementsAssignment_2_0 = (Assignment)cGroup_2.eContents().get(0);
		
		/** The c statements statement parser rule call 2 0 0. */
		private final RuleCall cStatementsStatementParserRuleCall_2_0_0 = (RuleCall)cStatementsAssignment_2_0.eContents().get(0);
		
		/** The c right curly bracket keyword 2 1. */
		private final Keyword cRightCurlyBracketKeyword_2_1 = (Keyword)cGroup_2.eContents().get(1);
		
		//    /**
		// * Blocks. An ordered list of statements inside curly brackets
		// */
		//Block:
		//    {Block} '{' ((statements+=Statement)* '}');
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//{Block} '{' ((statements+=Statement)* '}')
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the block action 0.
		 *
		 * @return the block action 0
		 */
		//{Block}
		public Action getBlockAction_0() { return cBlockAction_0; }
		
		/**
		 * Gets the left curly bracket keyword 1.
		 *
		 * @return the left curly bracket keyword 1
		 */
		//'{'
		public Keyword getLeftCurlyBracketKeyword_1() { return cLeftCurlyBracketKeyword_1; }
		
		/**
		 * Gets the group 2.
		 *
		 * @return the group 2
		 */
		//((statements+=Statement)* '}')
		public Group getGroup_2() { return cGroup_2; }
		
		/**
		 * Gets the statements assignment 2 0.
		 *
		 * @return the statements assignment 2 0
		 */
		//(statements+=Statement)*
		public Assignment getStatementsAssignment_2_0() { return cStatementsAssignment_2_0; }
		
		/**
		 * Gets the statements statement parser rule call 2 0 0.
		 *
		 * @return the statements statement parser rule call 2 0 0
		 */
		//Statement
		public RuleCall getStatementsStatementParserRuleCall_2_0_0() { return cStatementsStatementParserRuleCall_2_0_0; }
		
		/**
		 * Gets the right curly bracket keyword 2 1.
		 *
		 * @return the right curly bracket keyword 2 1
		 */
		//'}'
		public Keyword getRightCurlyBracketKeyword_2_1() { return cRightCurlyBracketKeyword_2_1; }
	}
	
	/**
	 * The Class ExpressionElements.
	 */
	public class ExpressionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Expression");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c argument pair parser rule call 0. */
		private final RuleCall cArgumentPairParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c pair parser rule call 1. */
		private final RuleCall cPairParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//    /**
		// * Expressions
		// */
		//Expression:
		//    ArgumentPair | Pair ;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//ArgumentPair | Pair
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the argument pair parser rule call 0.
		 *
		 * @return the argument pair parser rule call 0
		 */
		//ArgumentPair
		public RuleCall getArgumentPairParserRuleCall_0() { return cArgumentPairParserRuleCall_0; }
		
		/**
		 * Gets the pair parser rule call 1.
		 *
		 * @return the pair parser rule call 1
		 */
		//Pair
		public RuleCall getPairParserRuleCall_1() { return cPairParserRuleCall_1; }
	}
	
	/**
	 * The Class BinaryOperatorElements.
	 */
	public class BinaryOperatorElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.BinaryOperator");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c or parser rule call 0. */
		private final RuleCall cOrParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c and parser rule call 1. */
		private final RuleCall cAndParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		/** The c cast parser rule call 2. */
		private final RuleCall cCastParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		/** The c comparison parser rule call 3. */
		private final RuleCall cComparisonParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		
		/** The c addition parser rule call 4. */
		private final RuleCall cAdditionParserRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		
		/** The c multiplication parser rule call 5. */
		private final RuleCall cMultiplicationParserRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		
		/** The c exponentiation parser rule call 6. */
		private final RuleCall cExponentiationParserRuleCall_6 = (RuleCall)cAlternatives.eContents().get(6);
		
		/** The c binary parser rule call 7. */
		private final RuleCall cBinaryParserRuleCall_7 = (RuleCall)cAlternatives.eContents().get(7);
		
		/** The c pair parser rule call 8. */
		private final RuleCall cPairParserRuleCall_8 = (RuleCall)cAlternatives.eContents().get(8);
		
		/** The c unit parser rule call 9. */
		private final RuleCall cUnitParserRuleCall_9 = (RuleCall)cAlternatives.eContents().get(9);
		
		//BinaryOperator returns Expression:
		//    Or | And | Cast | Comparison | Addition | Multiplication | Exponentiation | Binary | Pair | Unit
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//Or | And | Cast | Comparison | Addition | Multiplication | Exponentiation | Binary | Pair | Unit
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the or parser rule call 0.
		 *
		 * @return the or parser rule call 0
		 */
		//Or
		public RuleCall getOrParserRuleCall_0() { return cOrParserRuleCall_0; }
		
		/**
		 * Gets the and parser rule call 1.
		 *
		 * @return the and parser rule call 1
		 */
		//And
		public RuleCall getAndParserRuleCall_1() { return cAndParserRuleCall_1; }
		
		/**
		 * Gets the cast parser rule call 2.
		 *
		 * @return the cast parser rule call 2
		 */
		//Cast
		public RuleCall getCastParserRuleCall_2() { return cCastParserRuleCall_2; }
		
		/**
		 * Gets the comparison parser rule call 3.
		 *
		 * @return the comparison parser rule call 3
		 */
		//Comparison
		public RuleCall getComparisonParserRuleCall_3() { return cComparisonParserRuleCall_3; }
		
		/**
		 * Gets the addition parser rule call 4.
		 *
		 * @return the addition parser rule call 4
		 */
		//Addition
		public RuleCall getAdditionParserRuleCall_4() { return cAdditionParserRuleCall_4; }
		
		/**
		 * Gets the multiplication parser rule call 5.
		 *
		 * @return the multiplication parser rule call 5
		 */
		//Multiplication
		public RuleCall getMultiplicationParserRuleCall_5() { return cMultiplicationParserRuleCall_5; }
		
		/**
		 * Gets the exponentiation parser rule call 6.
		 *
		 * @return the exponentiation parser rule call 6
		 */
		//Exponentiation
		public RuleCall getExponentiationParserRuleCall_6() { return cExponentiationParserRuleCall_6; }
		
		/**
		 * Gets the binary parser rule call 7.
		 *
		 * @return the binary parser rule call 7
		 */
		//Binary
		public RuleCall getBinaryParserRuleCall_7() { return cBinaryParserRuleCall_7; }
		
		/**
		 * Gets the pair parser rule call 8.
		 *
		 * @return the pair parser rule call 8
		 */
		//Pair
		public RuleCall getPairParserRuleCall_8() { return cPairParserRuleCall_8; }
		
		/**
		 * Gets the unit parser rule call 9.
		 *
		 * @return the unit parser rule call 9
		 */
		//Unit
		public RuleCall getUnitParserRuleCall_9() { return cUnitParserRuleCall_9; }
	}
	
	/**
	 * The Class ArgumentPairElements.
	 */
	public class ArgumentPairElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ArgumentPair");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c group 0. */
		private final Group cGroup_0 = (Group)cGroup.eContents().get(0);
		
		/** The c alternatives 0 0. */
		private final Alternatives cAlternatives_0_0 = (Alternatives)cGroup_0.eContents().get(0);
		
		/** The c group 0 0 0. */
		private final Group cGroup_0_0_0 = (Group)cAlternatives_0_0.eContents().get(0);
		
		/** The c op assignment 0 0 0 0. */
		private final Assignment cOpAssignment_0_0_0_0 = (Assignment)cGroup_0_0_0.eContents().get(0);
		
		/** The c op valid ID parser rule call 0 0 0 0 0. */
		private final RuleCall cOpValid_IDParserRuleCall_0_0_0_0_0 = (RuleCall)cOpAssignment_0_0_0_0.eContents().get(0);
		
		/** The c colon colon keyword 0 0 0 1. */
		private final Keyword cColonColonKeyword_0_0_0_1 = (Keyword)cGroup_0_0_0.eContents().get(1);
		
		/** The c group 0 0 1. */
		private final Group cGroup_0_0_1 = (Group)cAlternatives_0_0.eContents().get(1);
		
		/** The c op assignment 0 0 1 0. */
		private final Assignment cOpAssignment_0_0_1_0 = (Assignment)cGroup_0_0_1.eContents().get(0);
		
		/** The c op alternatives 0 0 1 0 0. */
		private final Alternatives cOpAlternatives_0_0_1_0_0 = (Alternatives)cOpAssignment_0_0_1_0.eContents().get(0);
		
		/** The c op definition facet key parser rule call 0 0 1 0 0 0. */
		private final RuleCall cOpDefinitionFacetKeyParserRuleCall_0_0_1_0_0_0 = (RuleCall)cOpAlternatives_0_0_1_0_0.eContents().get(0);
		
		/** The c op type facet key parser rule call 0 0 1 0 0 1. */
		private final RuleCall cOpTypeFacetKeyParserRuleCall_0_0_1_0_0_1 = (RuleCall)cOpAlternatives_0_0_1_0_0.eContents().get(1);
		
		/** The c op special facet key parser rule call 0 0 1 0 0 2. */
		private final RuleCall cOpSpecialFacetKeyParserRuleCall_0_0_1_0_0_2 = (RuleCall)cOpAlternatives_0_0_1_0_0.eContents().get(2);
		
		/** The c op action facet key parser rule call 0 0 1 0 0 3. */
		private final RuleCall cOpActionFacetKeyParserRuleCall_0_0_1_0_0_3 = (RuleCall)cOpAlternatives_0_0_1_0_0.eContents().get(3);
		
		/** The c op var facet key parser rule call 0 0 1 0 0 4. */
		private final RuleCall cOpVarFacetKeyParserRuleCall_0_0_1_0_0_4 = (RuleCall)cOpAlternatives_0_0_1_0_0.eContents().get(4);
		
		/** The c colon keyword 0 0 1 1. */
		private final Keyword cColonKeyword_0_0_1_1 = (Keyword)cGroup_0_0_1.eContents().get(1);
		
		/** The c right assignment 1. */
		private final Assignment cRightAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c right pair parser rule call 1 0. */
		private final RuleCall cRightPairParserRuleCall_1_0 = (RuleCall)cRightAssignment_1.eContents().get(0);
		
		//ArgumentPair:
		//    => (op=(Valid_ID) '::' | op=(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey) ':')? right=Pair;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//=> (op=(Valid_ID) '::' | op=(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey) ':')? right=Pair
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the group 0.
		 *
		 * @return the group 0
		 */
		//=> (op=(Valid_ID) '::' | op=(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey) ':')?
		public Group getGroup_0() { return cGroup_0; }
		
		/**
		 * Gets the alternatives 0 0.
		 *
		 * @return the alternatives 0 0
		 */
		//op=(Valid_ID) '::' | op=(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey) ':'
		public Alternatives getAlternatives_0_0() { return cAlternatives_0_0; }
		
		/**
		 * Gets the group 0 0 0.
		 *
		 * @return the group 0 0 0
		 */
		//op=(Valid_ID) '::'
		public Group getGroup_0_0_0() { return cGroup_0_0_0; }
		
		/**
		 * Gets the op assignment 0 0 0 0.
		 *
		 * @return the op assignment 0 0 0 0
		 */
		//op=(Valid_ID)
		public Assignment getOpAssignment_0_0_0_0() { return cOpAssignment_0_0_0_0; }
		
		/**
		 * Gets the op valid ID parser rule call 0 0 0 0 0.
		 *
		 * @return the op valid ID parser rule call 0 0 0 0 0
		 */
		//(Valid_ID)
		public RuleCall getOpValid_IDParserRuleCall_0_0_0_0_0() { return cOpValid_IDParserRuleCall_0_0_0_0_0; }
		
		/**
		 * Gets the colon colon keyword 0 0 0 1.
		 *
		 * @return the colon colon keyword 0 0 0 1
		 */
		//'::'
		public Keyword getColonColonKeyword_0_0_0_1() { return cColonColonKeyword_0_0_0_1; }
		
		/**
		 * Gets the group 0 0 1.
		 *
		 * @return the group 0 0 1
		 */
		//op=(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey) ':'
		public Group getGroup_0_0_1() { return cGroup_0_0_1; }
		
		/**
		 * Gets the op assignment 0 0 1 0.
		 *
		 * @return the op assignment 0 0 1 0
		 */
		//op=(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey)
		public Assignment getOpAssignment_0_0_1_0() { return cOpAssignment_0_0_1_0; }
		
		/**
		 * Gets the op alternatives 0 0 1 0 0.
		 *
		 * @return the op alternatives 0 0 1 0 0
		 */
		//(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey)
		public Alternatives getOpAlternatives_0_0_1_0_0() { return cOpAlternatives_0_0_1_0_0; }
		
		/**
		 * Gets the op definition facet key parser rule call 0 0 1 0 0 0.
		 *
		 * @return the op definition facet key parser rule call 0 0 1 0 0 0
		 */
		//DefinitionFacetKey
		public RuleCall getOpDefinitionFacetKeyParserRuleCall_0_0_1_0_0_0() { return cOpDefinitionFacetKeyParserRuleCall_0_0_1_0_0_0; }
		
		/**
		 * Gets the op type facet key parser rule call 0 0 1 0 0 1.
		 *
		 * @return the op type facet key parser rule call 0 0 1 0 0 1
		 */
		//TypeFacetKey
		public RuleCall getOpTypeFacetKeyParserRuleCall_0_0_1_0_0_1() { return cOpTypeFacetKeyParserRuleCall_0_0_1_0_0_1; }
		
		/**
		 * Gets the op special facet key parser rule call 0 0 1 0 0 2.
		 *
		 * @return the op special facet key parser rule call 0 0 1 0 0 2
		 */
		//SpecialFacetKey
		public RuleCall getOpSpecialFacetKeyParserRuleCall_0_0_1_0_0_2() { return cOpSpecialFacetKeyParserRuleCall_0_0_1_0_0_2; }
		
		/**
		 * Gets the op action facet key parser rule call 0 0 1 0 0 3.
		 *
		 * @return the op action facet key parser rule call 0 0 1 0 0 3
		 */
		//ActionFacetKey
		public RuleCall getOpActionFacetKeyParserRuleCall_0_0_1_0_0_3() { return cOpActionFacetKeyParserRuleCall_0_0_1_0_0_3; }
		
		/**
		 * Gets the op var facet key parser rule call 0 0 1 0 0 4.
		 *
		 * @return the op var facet key parser rule call 0 0 1 0 0 4
		 */
		//VarFacetKey
		public RuleCall getOpVarFacetKeyParserRuleCall_0_0_1_0_0_4() { return cOpVarFacetKeyParserRuleCall_0_0_1_0_0_4; }
		
		/**
		 * Gets the colon keyword 0 0 1 1.
		 *
		 * @return the colon keyword 0 0 1 1
		 */
		//':'
		public Keyword getColonKeyword_0_0_1_1() { return cColonKeyword_0_0_1_1; }
		
		/**
		 * Gets the right assignment 1.
		 *
		 * @return the right assignment 1
		 */
		//right=Pair
		public Assignment getRightAssignment_1() { return cRightAssignment_1; }
		
		/**
		 * Gets the right pair parser rule call 1 0.
		 *
		 * @return the right pair parser rule call 1 0
		 */
		//Pair
		public RuleCall getRightPairParserRuleCall_1_0() { return cRightPairParserRuleCall_1_0; }
	}
	
	/**
	 * The Class PairElements.
	 */
	public class PairElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Pair");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c if parser rule call 0. */
		private final RuleCall cIfParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c binary operator left action 1 0. */
		private final Action cBinaryOperatorLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		
		/** The c op assignment 1 1. */
		private final Assignment cOpAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		
		/** The c op colon colon keyword 1 1 0. */
		private final Keyword cOpColonColonKeyword_1_1_0 = (Keyword)cOpAssignment_1_1.eContents().get(0);
		
		/** The c right assignment 1 2. */
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		
		/** The c right if parser rule call 1 2 0. */
		private final RuleCall cRightIfParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//Pair returns Expression:
		//    If
		//    ({BinaryOperator.left=current}
		//    op='::'
		//    right=If)?;
		@Override public ParserRule getRule() { return rule; }
		
		//If
		//({BinaryOperator.left=current}
		//op='::'
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//right=If)?
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the if parser rule call 0.
		 *
		 * @return the if parser rule call 0
		 */
		//If
		public RuleCall getIfParserRuleCall_0() { return cIfParserRuleCall_0; }
		
		//({BinaryOperator.left=current}
		//op='::'
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//right=If)?
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the binary operator left action 1 0.
		 *
		 * @return the binary operator left action 1 0
		 */
		//{BinaryOperator.left=current}
		public Action getBinaryOperatorLeftAction_1_0() { return cBinaryOperatorLeftAction_1_0; }
		
		/**
		 * Gets the op assignment 1 1.
		 *
		 * @return the op assignment 1 1
		 */
		//op='::'
		public Assignment getOpAssignment_1_1() { return cOpAssignment_1_1; }
		
		/**
		 * Gets the op colon colon keyword 1 1 0.
		 *
		 * @return the op colon colon keyword 1 1 0
		 */
		//'::'
		public Keyword getOpColonColonKeyword_1_1_0() { return cOpColonColonKeyword_1_1_0; }
		
		/**
		 * Gets the right assignment 1 2.
		 *
		 * @return the right assignment 1 2
		 */
		//right=If
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }
		
		/**
		 * Gets the right if parser rule call 1 2 0.
		 *
		 * @return the right if parser rule call 1 2 0
		 */
		//If
		public RuleCall getRightIfParserRuleCall_1_2_0() { return cRightIfParserRuleCall_1_2_0; }
	}
	
	/**
	 * The Class IfElements.
	 */
	public class IfElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.If");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c or parser rule call 0. */
		private final RuleCall cOrParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c if left action 1 0. */
		private final Action cIfLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		
		/** The c op assignment 1 1. */
		private final Assignment cOpAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		
		/** The c op question mark keyword 1 1 0. */
		private final Keyword cOpQuestionMarkKeyword_1_1_0 = (Keyword)cOpAssignment_1_1.eContents().get(0);
		
		/** The c right assignment 1 2. */
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		
		/** The c right or parser rule call 1 2 0. */
		private final RuleCall cRightOrParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		/** The c group 1 3. */
		private final Group cGroup_1_3 = (Group)cGroup_1.eContents().get(3);
		
		/** The c colon keyword 1 3 0. */
		private final Keyword cColonKeyword_1_3_0 = (Keyword)cGroup_1_3.eContents().get(0);
		
		/** The c if false assignment 1 3 1. */
		private final Assignment cIfFalseAssignment_1_3_1 = (Assignment)cGroup_1_3.eContents().get(1);
		
		/** The c if false or parser rule call 1 3 1 0. */
		private final RuleCall cIfFalseOrParserRuleCall_1_3_1_0 = (RuleCall)cIfFalseAssignment_1_3_1.eContents().get(0);
		
		//If returns Expression:
		//    Or
		//    ({If.left=current}
		//    op='?'
		//    right=Or
		//    (':'
		//    ifFalse=Or))?;
		@Override public ParserRule getRule() { return rule; }
		
		//Or
		//({If.left=current}
		//op='?'
		//right=Or
		//(':'
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//ifFalse=Or))?
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the or parser rule call 0.
		 *
		 * @return the or parser rule call 0
		 */
		//Or
		public RuleCall getOrParserRuleCall_0() { return cOrParserRuleCall_0; }
		
		//({If.left=current}
		//op='?'
		//right=Or
		//(':'
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//ifFalse=Or))?
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the if left action 1 0.
		 *
		 * @return the if left action 1 0
		 */
		//{If.left=current}
		public Action getIfLeftAction_1_0() { return cIfLeftAction_1_0; }
		
		/**
		 * Gets the op assignment 1 1.
		 *
		 * @return the op assignment 1 1
		 */
		//op='?'
		public Assignment getOpAssignment_1_1() { return cOpAssignment_1_1; }
		
		/**
		 * Gets the op question mark keyword 1 1 0.
		 *
		 * @return the op question mark keyword 1 1 0
		 */
		//'?'
		public Keyword getOpQuestionMarkKeyword_1_1_0() { return cOpQuestionMarkKeyword_1_1_0; }
		
		/**
		 * Gets the right assignment 1 2.
		 *
		 * @return the right assignment 1 2
		 */
		//right=Or
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }
		
		/**
		 * Gets the right or parser rule call 1 2 0.
		 *
		 * @return the right or parser rule call 1 2 0
		 */
		//Or
		public RuleCall getRightOrParserRuleCall_1_2_0() { return cRightOrParserRuleCall_1_2_0; }
		
		//(':'
		/**
		 * Gets the group 1 3.
		 *
		 * @return the group 1 3
		 */
		//ifFalse=Or)
		public Group getGroup_1_3() { return cGroup_1_3; }
		
		/**
		 * Gets the colon keyword 1 3 0.
		 *
		 * @return the colon keyword 1 3 0
		 */
		//':'
		public Keyword getColonKeyword_1_3_0() { return cColonKeyword_1_3_0; }
		
		/**
		 * Gets the if false assignment 1 3 1.
		 *
		 * @return the if false assignment 1 3 1
		 */
		//ifFalse=Or
		public Assignment getIfFalseAssignment_1_3_1() { return cIfFalseAssignment_1_3_1; }
		
		/**
		 * Gets the if false or parser rule call 1 3 1 0.
		 *
		 * @return the if false or parser rule call 1 3 1 0
		 */
		//Or
		public RuleCall getIfFalseOrParserRuleCall_1_3_1_0() { return cIfFalseOrParserRuleCall_1_3_1_0; }
	}
	
	/**
	 * The Class OrElements.
	 */
	public class OrElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Or");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c and parser rule call 0. */
		private final RuleCall cAndParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c binary operator left action 1 0. */
		private final Action cBinaryOperatorLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		
		/** The c op assignment 1 1. */
		private final Assignment cOpAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		
		/** The c op or keyword 1 1 0. */
		private final Keyword cOpOrKeyword_1_1_0 = (Keyword)cOpAssignment_1_1.eContents().get(0);
		
		/** The c right assignment 1 2. */
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		
		/** The c right and parser rule call 1 2 0. */
		private final RuleCall cRightAndParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//Or returns Expression:
		//    And
		//    ({BinaryOperator.left=current}
		//    op='or'
		//    right=And)*;
		@Override public ParserRule getRule() { return rule; }
		
		//And
		//({BinaryOperator.left=current}
		//op='or'
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//right=And)*
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the and parser rule call 0.
		 *
		 * @return the and parser rule call 0
		 */
		//And
		public RuleCall getAndParserRuleCall_0() { return cAndParserRuleCall_0; }
		
		//({BinaryOperator.left=current}
		//op='or'
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//right=And)*
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the binary operator left action 1 0.
		 *
		 * @return the binary operator left action 1 0
		 */
		//{BinaryOperator.left=current}
		public Action getBinaryOperatorLeftAction_1_0() { return cBinaryOperatorLeftAction_1_0; }
		
		/**
		 * Gets the op assignment 1 1.
		 *
		 * @return the op assignment 1 1
		 */
		//op='or'
		public Assignment getOpAssignment_1_1() { return cOpAssignment_1_1; }
		
		/**
		 * Gets the op or keyword 1 1 0.
		 *
		 * @return the op or keyword 1 1 0
		 */
		//'or'
		public Keyword getOpOrKeyword_1_1_0() { return cOpOrKeyword_1_1_0; }
		
		/**
		 * Gets the right assignment 1 2.
		 *
		 * @return the right assignment 1 2
		 */
		//right=And
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }
		
		/**
		 * Gets the right and parser rule call 1 2 0.
		 *
		 * @return the right and parser rule call 1 2 0
		 */
		//And
		public RuleCall getRightAndParserRuleCall_1_2_0() { return cRightAndParserRuleCall_1_2_0; }
	}
	
	/**
	 * The Class AndElements.
	 */
	public class AndElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.And");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c cast parser rule call 0. */
		private final RuleCall cCastParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c binary operator left action 1 0. */
		private final Action cBinaryOperatorLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		
		/** The c op assignment 1 1. */
		private final Assignment cOpAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		
		/** The c op and keyword 1 1 0. */
		private final Keyword cOpAndKeyword_1_1_0 = (Keyword)cOpAssignment_1_1.eContents().get(0);
		
		/** The c right assignment 1 2. */
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		
		/** The c right cast parser rule call 1 2 0. */
		private final RuleCall cRightCastParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//And returns Expression:
		//    Cast
		//    ({BinaryOperator.left=current}
		//    op='and'
		//    right=Cast)*;
		@Override public ParserRule getRule() { return rule; }
		
		//Cast
		//({BinaryOperator.left=current}
		//op='and'
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//right=Cast)*
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the cast parser rule call 0.
		 *
		 * @return the cast parser rule call 0
		 */
		//Cast
		public RuleCall getCastParserRuleCall_0() { return cCastParserRuleCall_0; }
		
		//({BinaryOperator.left=current}
		//op='and'
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//right=Cast)*
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the binary operator left action 1 0.
		 *
		 * @return the binary operator left action 1 0
		 */
		//{BinaryOperator.left=current}
		public Action getBinaryOperatorLeftAction_1_0() { return cBinaryOperatorLeftAction_1_0; }
		
		/**
		 * Gets the op assignment 1 1.
		 *
		 * @return the op assignment 1 1
		 */
		//op='and'
		public Assignment getOpAssignment_1_1() { return cOpAssignment_1_1; }
		
		/**
		 * Gets the op and keyword 1 1 0.
		 *
		 * @return the op and keyword 1 1 0
		 */
		//'and'
		public Keyword getOpAndKeyword_1_1_0() { return cOpAndKeyword_1_1_0; }
		
		/**
		 * Gets the right assignment 1 2.
		 *
		 * @return the right assignment 1 2
		 */
		//right=Cast
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }
		
		/**
		 * Gets the right cast parser rule call 1 2 0.
		 *
		 * @return the right cast parser rule call 1 2 0
		 */
		//Cast
		public RuleCall getRightCastParserRuleCall_1_2_0() { return cRightCastParserRuleCall_1_2_0; }
	}
	
	/**
	 * The Class CastElements.
	 */
	public class CastElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Cast");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c comparison parser rule call 0. */
		private final RuleCall cComparisonParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c group 1 0. */
		private final Group cGroup_1_0 = (Group)cGroup_1.eContents().get(0);
		
		/** The c binary operator left action 1 0 0. */
		private final Action cBinaryOperatorLeftAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		
		/** The c op assignment 1 0 1. */
		private final Assignment cOpAssignment_1_0_1 = (Assignment)cGroup_1_0.eContents().get(1);
		
		/** The c op as keyword 1 0 1 0. */
		private final Keyword cOpAsKeyword_1_0_1_0 = (Keyword)cOpAssignment_1_0_1.eContents().get(0);
		
		/** The c alternatives 1 1. */
		private final Alternatives cAlternatives_1_1 = (Alternatives)cGroup_1.eContents().get(1);
		
		/** The c right assignment 1 1 0. */
		private final Assignment cRightAssignment_1_1_0 = (Assignment)cAlternatives_1_1.eContents().get(0);
		
		/** The c right type ref parser rule call 1 1 0 0. */
		private final RuleCall cRightTypeRefParserRuleCall_1_1_0_0 = (RuleCall)cRightAssignment_1_1_0.eContents().get(0);
		
		/** The c group 1 1 1. */
		private final Group cGroup_1_1_1 = (Group)cAlternatives_1_1.eContents().get(1);
		
		/** The c left parenthesis keyword 1 1 1 0. */
		private final Keyword cLeftParenthesisKeyword_1_1_1_0 = (Keyword)cGroup_1_1_1.eContents().get(0);
		
		/** The c right assignment 1 1 1 1. */
		private final Assignment cRightAssignment_1_1_1_1 = (Assignment)cGroup_1_1_1.eContents().get(1);
		
		/** The c right type ref parser rule call 1 1 1 1 0. */
		private final RuleCall cRightTypeRefParserRuleCall_1_1_1_1_0 = (RuleCall)cRightAssignment_1_1_1_1.eContents().get(0);
		
		/** The c right parenthesis keyword 1 1 1 2. */
		private final Keyword cRightParenthesisKeyword_1_1_1_2 = (Keyword)cGroup_1_1_1.eContents().get(2);
		
		//Cast returns Expression:
		//    Comparison
		//    (({BinaryOperator.left = current}
		//        op='as'
		//) ((right= TypeRef) | ('(' right=TypeRef ')') ))?
		//;
		@Override public ParserRule getRule() { return rule; }
		
		//    Comparison
		//    (({BinaryOperator.left = current}
		//        op='as'
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//) ((right= TypeRef) | ('(' right=TypeRef ')') ))?
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the comparison parser rule call 0.
		 *
		 * @return the comparison parser rule call 0
		 */
		//Comparison
		public RuleCall getComparisonParserRuleCall_0() { return cComparisonParserRuleCall_0; }
		
		//    (({BinaryOperator.left = current}
		//        op='as'
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//) ((right= TypeRef) | ('(' right=TypeRef ')') ))?
		public Group getGroup_1() { return cGroup_1; }
		
		//({BinaryOperator.left = current}
		//        op='as'
		/**
		 * Gets the group 1 0.
		 *
		 * @return the group 1 0
		 */
		//)
		public Group getGroup_1_0() { return cGroup_1_0; }
		
		/**
		 * Gets the binary operator left action 1 0 0.
		 *
		 * @return the binary operator left action 1 0 0
		 */
		//{BinaryOperator.left = current}
		public Action getBinaryOperatorLeftAction_1_0_0() { return cBinaryOperatorLeftAction_1_0_0; }
		
		/**
		 * Gets the op assignment 1 0 1.
		 *
		 * @return the op assignment 1 0 1
		 */
		//op='as'
		public Assignment getOpAssignment_1_0_1() { return cOpAssignment_1_0_1; }
		
		/**
		 * Gets the op as keyword 1 0 1 0.
		 *
		 * @return the op as keyword 1 0 1 0
		 */
		//'as'
		public Keyword getOpAsKeyword_1_0_1_0() { return cOpAsKeyword_1_0_1_0; }
		
		/**
		 * Gets the alternatives 1 1.
		 *
		 * @return the alternatives 1 1
		 */
		//((right= TypeRef) | ('(' right=TypeRef ')') )
		public Alternatives getAlternatives_1_1() { return cAlternatives_1_1; }
		
		/**
		 * Gets the right assignment 1 1 0.
		 *
		 * @return the right assignment 1 1 0
		 */
		//(right= TypeRef)
		public Assignment getRightAssignment_1_1_0() { return cRightAssignment_1_1_0; }
		
		/**
		 * Gets the right type ref parser rule call 1 1 0 0.
		 *
		 * @return the right type ref parser rule call 1 1 0 0
		 */
		//TypeRef
		public RuleCall getRightTypeRefParserRuleCall_1_1_0_0() { return cRightTypeRefParserRuleCall_1_1_0_0; }
		
		/**
		 * Gets the group 1 1 1.
		 *
		 * @return the group 1 1 1
		 */
		//('(' right=TypeRef ')')
		public Group getGroup_1_1_1() { return cGroup_1_1_1; }
		
		/**
		 * Gets the left parenthesis keyword 1 1 1 0.
		 *
		 * @return the left parenthesis keyword 1 1 1 0
		 */
		//'('
		public Keyword getLeftParenthesisKeyword_1_1_1_0() { return cLeftParenthesisKeyword_1_1_1_0; }
		
		/**
		 * Gets the right assignment 1 1 1 1.
		 *
		 * @return the right assignment 1 1 1 1
		 */
		//right=TypeRef
		public Assignment getRightAssignment_1_1_1_1() { return cRightAssignment_1_1_1_1; }
		
		/**
		 * Gets the right type ref parser rule call 1 1 1 1 0.
		 *
		 * @return the right type ref parser rule call 1 1 1 1 0
		 */
		//TypeRef
		public RuleCall getRightTypeRefParserRuleCall_1_1_1_1_0() { return cRightTypeRefParserRuleCall_1_1_1_1_0; }
		
		/**
		 * Gets the right parenthesis keyword 1 1 1 2.
		 *
		 * @return the right parenthesis keyword 1 1 1 2
		 */
		//')'
		public Keyword getRightParenthesisKeyword_1_1_1_2() { return cRightParenthesisKeyword_1_1_1_2; }
	}
	
	/**
	 * The Class ComparisonElements.
	 */
	public class ComparisonElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Comparison");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c addition parser rule call 0. */
		private final RuleCall cAdditionParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c group 1 0. */
		private final Group cGroup_1_0 = (Group)cGroup_1.eContents().get(0);
		
		/** The c binary operator left action 1 0 0. */
		private final Action cBinaryOperatorLeftAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		
		/** The c op assignment 1 0 1. */
		private final Assignment cOpAssignment_1_0_1 = (Assignment)cGroup_1_0.eContents().get(1);
		
		/** The c op alternatives 1 0 1 0. */
		private final Alternatives cOpAlternatives_1_0_1_0 = (Alternatives)cOpAssignment_1_0_1.eContents().get(0);
		
		/** The c op exclamation mark equals sign keyword 1 0 1 0 0. */
		private final Keyword cOpExclamationMarkEqualsSignKeyword_1_0_1_0_0 = (Keyword)cOpAlternatives_1_0_1_0.eContents().get(0);
		
		/** The c op equals sign keyword 1 0 1 0 1. */
		private final Keyword cOpEqualsSignKeyword_1_0_1_0_1 = (Keyword)cOpAlternatives_1_0_1_0.eContents().get(1);
		
		/** The c op greater than sign equals sign keyword 1 0 1 0 2. */
		private final Keyword cOpGreaterThanSignEqualsSignKeyword_1_0_1_0_2 = (Keyword)cOpAlternatives_1_0_1_0.eContents().get(2);
		
		/** The c op less than sign equals sign keyword 1 0 1 0 3. */
		private final Keyword cOpLessThanSignEqualsSignKeyword_1_0_1_0_3 = (Keyword)cOpAlternatives_1_0_1_0.eContents().get(3);
		
		/** The c op less than sign keyword 1 0 1 0 4. */
		private final Keyword cOpLessThanSignKeyword_1_0_1_0_4 = (Keyword)cOpAlternatives_1_0_1_0.eContents().get(4);
		
		/** The c op greater than sign keyword 1 0 1 0 5. */
		private final Keyword cOpGreaterThanSignKeyword_1_0_1_0_5 = (Keyword)cOpAlternatives_1_0_1_0.eContents().get(5);
		
		/** The c right assignment 1 1. */
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		
		/** The c right addition parser rule call 1 1 0. */
		private final RuleCall cRightAdditionParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//Comparison returns Expression:
		//    Addition
		//    (({BinaryOperator.left=current}
		//    op=('!=' | '=' | '>=' | '<=' | '<' | '>'))
		//    right=Addition)?;
		@Override public ParserRule getRule() { return rule; }
		
		//Addition
		//(({BinaryOperator.left=current}
		//op=('!=' | '=' | '>=' | '<=' | '<' | '>'))
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//right=Addition)?
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the addition parser rule call 0.
		 *
		 * @return the addition parser rule call 0
		 */
		//Addition
		public RuleCall getAdditionParserRuleCall_0() { return cAdditionParserRuleCall_0; }
		
		//(({BinaryOperator.left=current}
		//op=('!=' | '=' | '>=' | '<=' | '<' | '>'))
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//right=Addition)?
		public Group getGroup_1() { return cGroup_1; }
		
		//({BinaryOperator.left=current}
		/**
		 * Gets the group 1 0.
		 *
		 * @return the group 1 0
		 */
		//    op=('!=' | '=' | '>=' | '<=' | '<' | '>'))
		public Group getGroup_1_0() { return cGroup_1_0; }
		
		/**
		 * Gets the binary operator left action 1 0 0.
		 *
		 * @return the binary operator left action 1 0 0
		 */
		//{BinaryOperator.left=current}
		public Action getBinaryOperatorLeftAction_1_0_0() { return cBinaryOperatorLeftAction_1_0_0; }
		
		/**
		 * Gets the op assignment 1 0 1.
		 *
		 * @return the op assignment 1 0 1
		 */
		//op=('!=' | '=' | '>=' | '<=' | '<' | '>')
		public Assignment getOpAssignment_1_0_1() { return cOpAssignment_1_0_1; }
		
		/**
		 * Gets the op alternatives 1 0 1 0.
		 *
		 * @return the op alternatives 1 0 1 0
		 */
		//('!=' | '=' | '>=' | '<=' | '<' | '>')
		public Alternatives getOpAlternatives_1_0_1_0() { return cOpAlternatives_1_0_1_0; }
		
		/**
		 * Gets the op exclamation mark equals sign keyword 1 0 1 0 0.
		 *
		 * @return the op exclamation mark equals sign keyword 1 0 1 0 0
		 */
		//'!='
		public Keyword getOpExclamationMarkEqualsSignKeyword_1_0_1_0_0() { return cOpExclamationMarkEqualsSignKeyword_1_0_1_0_0; }
		
		/**
		 * Gets the op equals sign keyword 1 0 1 0 1.
		 *
		 * @return the op equals sign keyword 1 0 1 0 1
		 */
		//'='
		public Keyword getOpEqualsSignKeyword_1_0_1_0_1() { return cOpEqualsSignKeyword_1_0_1_0_1; }
		
		/**
		 * Gets the op greater than sign equals sign keyword 1 0 1 0 2.
		 *
		 * @return the op greater than sign equals sign keyword 1 0 1 0 2
		 */
		//'>='
		public Keyword getOpGreaterThanSignEqualsSignKeyword_1_0_1_0_2() { return cOpGreaterThanSignEqualsSignKeyword_1_0_1_0_2; }
		
		/**
		 * Gets the op less than sign equals sign keyword 1 0 1 0 3.
		 *
		 * @return the op less than sign equals sign keyword 1 0 1 0 3
		 */
		//'<='
		public Keyword getOpLessThanSignEqualsSignKeyword_1_0_1_0_3() { return cOpLessThanSignEqualsSignKeyword_1_0_1_0_3; }
		
		/**
		 * Gets the op less than sign keyword 1 0 1 0 4.
		 *
		 * @return the op less than sign keyword 1 0 1 0 4
		 */
		//'<'
		public Keyword getOpLessThanSignKeyword_1_0_1_0_4() { return cOpLessThanSignKeyword_1_0_1_0_4; }
		
		/**
		 * Gets the op greater than sign keyword 1 0 1 0 5.
		 *
		 * @return the op greater than sign keyword 1 0 1 0 5
		 */
		//'>'
		public Keyword getOpGreaterThanSignKeyword_1_0_1_0_5() { return cOpGreaterThanSignKeyword_1_0_1_0_5; }
		
		/**
		 * Gets the right assignment 1 1.
		 *
		 * @return the right assignment 1 1
		 */
		//right=Addition
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }
		
		/**
		 * Gets the right addition parser rule call 1 1 0.
		 *
		 * @return the right addition parser rule call 1 1 0
		 */
		//Addition
		public RuleCall getRightAdditionParserRuleCall_1_1_0() { return cRightAdditionParserRuleCall_1_1_0; }
	}
	
	/**
	 * The Class AdditionElements.
	 */
	public class AdditionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Addition");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c multiplication parser rule call 0. */
		private final RuleCall cMultiplicationParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c group 1 0. */
		private final Group cGroup_1_0 = (Group)cGroup_1.eContents().get(0);
		
		/** The c binary operator left action 1 0 0. */
		private final Action cBinaryOperatorLeftAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		
		/** The c op assignment 1 0 1. */
		private final Assignment cOpAssignment_1_0_1 = (Assignment)cGroup_1_0.eContents().get(1);
		
		/** The c op alternatives 1 0 1 0. */
		private final Alternatives cOpAlternatives_1_0_1_0 = (Alternatives)cOpAssignment_1_0_1.eContents().get(0);
		
		/** The c op plus sign keyword 1 0 1 0 0. */
		private final Keyword cOpPlusSignKeyword_1_0_1_0_0 = (Keyword)cOpAlternatives_1_0_1_0.eContents().get(0);
		
		/** The c op hyphen minus keyword 1 0 1 0 1. */
		private final Keyword cOpHyphenMinusKeyword_1_0_1_0_1 = (Keyword)cOpAlternatives_1_0_1_0.eContents().get(1);
		
		/** The c right assignment 1 1. */
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		
		/** The c right multiplication parser rule call 1 1 0. */
		private final RuleCall cRightMultiplicationParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//Addition returns Expression:
		//    Multiplication
		//    (({BinaryOperator.left=current} op=('+' | '-'))
		//    right=Multiplication)*;
		@Override public ParserRule getRule() { return rule; }
		
		//Multiplication
		//(({BinaryOperator.left=current} op=('+' | '-'))
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//right=Multiplication)*
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the multiplication parser rule call 0.
		 *
		 * @return the multiplication parser rule call 0
		 */
		//Multiplication
		public RuleCall getMultiplicationParserRuleCall_0() { return cMultiplicationParserRuleCall_0; }
		
		//(({BinaryOperator.left=current} op=('+' | '-'))
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//right=Multiplication)*
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the group 1 0.
		 *
		 * @return the group 1 0
		 */
		//({BinaryOperator.left=current} op=('+' | '-'))
		public Group getGroup_1_0() { return cGroup_1_0; }
		
		/**
		 * Gets the binary operator left action 1 0 0.
		 *
		 * @return the binary operator left action 1 0 0
		 */
		//{BinaryOperator.left=current}
		public Action getBinaryOperatorLeftAction_1_0_0() { return cBinaryOperatorLeftAction_1_0_0; }
		
		/**
		 * Gets the op assignment 1 0 1.
		 *
		 * @return the op assignment 1 0 1
		 */
		//op=('+' | '-')
		public Assignment getOpAssignment_1_0_1() { return cOpAssignment_1_0_1; }
		
		/**
		 * Gets the op alternatives 1 0 1 0.
		 *
		 * @return the op alternatives 1 0 1 0
		 */
		//('+' | '-')
		public Alternatives getOpAlternatives_1_0_1_0() { return cOpAlternatives_1_0_1_0; }
		
		/**
		 * Gets the op plus sign keyword 1 0 1 0 0.
		 *
		 * @return the op plus sign keyword 1 0 1 0 0
		 */
		//'+'
		public Keyword getOpPlusSignKeyword_1_0_1_0_0() { return cOpPlusSignKeyword_1_0_1_0_0; }
		
		/**
		 * Gets the op hyphen minus keyword 1 0 1 0 1.
		 *
		 * @return the op hyphen minus keyword 1 0 1 0 1
		 */
		//'-'
		public Keyword getOpHyphenMinusKeyword_1_0_1_0_1() { return cOpHyphenMinusKeyword_1_0_1_0_1; }
		
		/**
		 * Gets the right assignment 1 1.
		 *
		 * @return the right assignment 1 1
		 */
		//right=Multiplication
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }
		
		/**
		 * Gets the right multiplication parser rule call 1 1 0.
		 *
		 * @return the right multiplication parser rule call 1 1 0
		 */
		//Multiplication
		public RuleCall getRightMultiplicationParserRuleCall_1_1_0() { return cRightMultiplicationParserRuleCall_1_1_0; }
	}
	
	/**
	 * The Class MultiplicationElements.
	 */
	public class MultiplicationElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Multiplication");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c exponentiation parser rule call 0. */
		private final RuleCall cExponentiationParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c group 1 0. */
		private final Group cGroup_1_0 = (Group)cGroup_1.eContents().get(0);
		
		/** The c binary operator left action 1 0 0. */
		private final Action cBinaryOperatorLeftAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		
		/** The c op assignment 1 0 1. */
		private final Assignment cOpAssignment_1_0_1 = (Assignment)cGroup_1_0.eContents().get(1);
		
		/** The c op alternatives 1 0 1 0. */
		private final Alternatives cOpAlternatives_1_0_1_0 = (Alternatives)cOpAssignment_1_0_1.eContents().get(0);
		
		/** The c op asterisk keyword 1 0 1 0 0. */
		private final Keyword cOpAsteriskKeyword_1_0_1_0_0 = (Keyword)cOpAlternatives_1_0_1_0.eContents().get(0);
		
		/** The c op solidus keyword 1 0 1 0 1. */
		private final Keyword cOpSolidusKeyword_1_0_1_0_1 = (Keyword)cOpAlternatives_1_0_1_0.eContents().get(1);
		
		/** The c right assignment 1 1. */
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		
		/** The c right exponentiation parser rule call 1 1 0. */
		private final RuleCall cRightExponentiationParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//Multiplication returns Expression:
		//    Exponentiation
		//    (({BinaryOperator.left=current} op=('*' | '/' )) right=Exponentiation)*;
		@Override public ParserRule getRule() { return rule; }
		
		//Exponentiation
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//(({BinaryOperator.left=current} op=('*' | '/' )) right=Exponentiation)*
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the exponentiation parser rule call 0.
		 *
		 * @return the exponentiation parser rule call 0
		 */
		//Exponentiation
		public RuleCall getExponentiationParserRuleCall_0() { return cExponentiationParserRuleCall_0; }
		
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//(({BinaryOperator.left=current} op=('*' | '/' )) right=Exponentiation)*
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the group 1 0.
		 *
		 * @return the group 1 0
		 */
		//({BinaryOperator.left=current} op=('*' | '/' ))
		public Group getGroup_1_0() { return cGroup_1_0; }
		
		/**
		 * Gets the binary operator left action 1 0 0.
		 *
		 * @return the binary operator left action 1 0 0
		 */
		//{BinaryOperator.left=current}
		public Action getBinaryOperatorLeftAction_1_0_0() { return cBinaryOperatorLeftAction_1_0_0; }
		
		/**
		 * Gets the op assignment 1 0 1.
		 *
		 * @return the op assignment 1 0 1
		 */
		//op=('*' | '/' )
		public Assignment getOpAssignment_1_0_1() { return cOpAssignment_1_0_1; }
		
		/**
		 * Gets the op alternatives 1 0 1 0.
		 *
		 * @return the op alternatives 1 0 1 0
		 */
		//('*' | '/' )
		public Alternatives getOpAlternatives_1_0_1_0() { return cOpAlternatives_1_0_1_0; }
		
		/**
		 * Gets the op asterisk keyword 1 0 1 0 0.
		 *
		 * @return the op asterisk keyword 1 0 1 0 0
		 */
		//'*'
		public Keyword getOpAsteriskKeyword_1_0_1_0_0() { return cOpAsteriskKeyword_1_0_1_0_0; }
		
		/**
		 * Gets the op solidus keyword 1 0 1 0 1.
		 *
		 * @return the op solidus keyword 1 0 1 0 1
		 */
		//'/'
		public Keyword getOpSolidusKeyword_1_0_1_0_1() { return cOpSolidusKeyword_1_0_1_0_1; }
		
		/**
		 * Gets the right assignment 1 1.
		 *
		 * @return the right assignment 1 1
		 */
		//right=Exponentiation
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }
		
		/**
		 * Gets the right exponentiation parser rule call 1 1 0.
		 *
		 * @return the right exponentiation parser rule call 1 1 0
		 */
		//Exponentiation
		public RuleCall getRightExponentiationParserRuleCall_1_1_0() { return cRightExponentiationParserRuleCall_1_1_0; }
	}
	
	/**
	 * The Class ExponentiationElements.
	 */
	public class ExponentiationElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Exponentiation");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c binary parser rule call 0. */
		private final RuleCall cBinaryParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c group 1 0. */
		private final Group cGroup_1_0 = (Group)cGroup_1.eContents().get(0);
		
		/** The c binary operator left action 1 0 0. */
		private final Action cBinaryOperatorLeftAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		
		/** The c op assignment 1 0 1. */
		private final Assignment cOpAssignment_1_0_1 = (Assignment)cGroup_1_0.eContents().get(1);
		
		/** The c op circumflex accent keyword 1 0 1 0. */
		private final Keyword cOpCircumflexAccentKeyword_1_0_1_0 = (Keyword)cOpAssignment_1_0_1.eContents().get(0);
		
		/** The c right assignment 1 1. */
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		
		/** The c right binary parser rule call 1 1 0. */
		private final RuleCall cRightBinaryParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//Exponentiation returns Expression:
		//    Binary
		//    (({BinaryOperator.left=current} op=('^')) right=Binary)*;
		@Override public ParserRule getRule() { return rule; }
		
		//Binary
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//(({BinaryOperator.left=current} op=('^')) right=Binary)*
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the binary parser rule call 0.
		 *
		 * @return the binary parser rule call 0
		 */
		//Binary
		public RuleCall getBinaryParserRuleCall_0() { return cBinaryParserRuleCall_0; }
		
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//(({BinaryOperator.left=current} op=('^')) right=Binary)*
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the group 1 0.
		 *
		 * @return the group 1 0
		 */
		//({BinaryOperator.left=current} op=('^'))
		public Group getGroup_1_0() { return cGroup_1_0; }
		
		/**
		 * Gets the binary operator left action 1 0 0.
		 *
		 * @return the binary operator left action 1 0 0
		 */
		//{BinaryOperator.left=current}
		public Action getBinaryOperatorLeftAction_1_0_0() { return cBinaryOperatorLeftAction_1_0_0; }
		
		/**
		 * Gets the op assignment 1 0 1.
		 *
		 * @return the op assignment 1 0 1
		 */
		//op=('^')
		public Assignment getOpAssignment_1_0_1() { return cOpAssignment_1_0_1; }
		
		/**
		 * Gets the op circumflex accent keyword 1 0 1 0.
		 *
		 * @return the op circumflex accent keyword 1 0 1 0
		 */
		//('^')
		public Keyword getOpCircumflexAccentKeyword_1_0_1_0() { return cOpCircumflexAccentKeyword_1_0_1_0; }
		
		/**
		 * Gets the right assignment 1 1.
		 *
		 * @return the right assignment 1 1
		 */
		//right=Binary
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }
		
		/**
		 * Gets the right binary parser rule call 1 1 0.
		 *
		 * @return the right binary parser rule call 1 1 0
		 */
		//Binary
		public RuleCall getRightBinaryParserRuleCall_1_1_0() { return cRightBinaryParserRuleCall_1_1_0; }
	}
	
	/**
	 * The Class BinaryElements.
	 */
	public class BinaryElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Binary");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c unit parser rule call 0. */
		private final RuleCall cUnitParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c group 1 0. */
		private final Group cGroup_1_0 = (Group)cGroup_1.eContents().get(0);
		
		/** The c binary operator left action 1 0 0. */
		private final Action cBinaryOperatorLeftAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		
		/** The c op assignment 1 0 1. */
		private final Assignment cOpAssignment_1_0_1 = (Assignment)cGroup_1_0.eContents().get(1);
		
		/** The c op valid ID parser rule call 1 0 1 0. */
		private final RuleCall cOpValid_IDParserRuleCall_1_0_1_0 = (RuleCall)cOpAssignment_1_0_1.eContents().get(0);
		
		/** The c right assignment 1 1. */
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		
		/** The c right unit parser rule call 1 1 0. */
		private final RuleCall cRightUnitParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//Binary returns Expression:
		//    Unit
		//    (({BinaryOperator.left=current} op=(Valid_ID)) right=Unit)*;
		@Override public ParserRule getRule() { return rule; }
		
		//Unit
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//(({BinaryOperator.left=current} op=(Valid_ID)) right=Unit)*
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the unit parser rule call 0.
		 *
		 * @return the unit parser rule call 0
		 */
		//Unit
		public RuleCall getUnitParserRuleCall_0() { return cUnitParserRuleCall_0; }
		
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//(({BinaryOperator.left=current} op=(Valid_ID)) right=Unit)*
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the group 1 0.
		 *
		 * @return the group 1 0
		 */
		//({BinaryOperator.left=current} op=(Valid_ID))
		public Group getGroup_1_0() { return cGroup_1_0; }
		
		/**
		 * Gets the binary operator left action 1 0 0.
		 *
		 * @return the binary operator left action 1 0 0
		 */
		//{BinaryOperator.left=current}
		public Action getBinaryOperatorLeftAction_1_0_0() { return cBinaryOperatorLeftAction_1_0_0; }
		
		/**
		 * Gets the op assignment 1 0 1.
		 *
		 * @return the op assignment 1 0 1
		 */
		//op=(Valid_ID)
		public Assignment getOpAssignment_1_0_1() { return cOpAssignment_1_0_1; }
		
		/**
		 * Gets the op valid ID parser rule call 1 0 1 0.
		 *
		 * @return the op valid ID parser rule call 1 0 1 0
		 */
		//(Valid_ID)
		public RuleCall getOpValid_IDParserRuleCall_1_0_1_0() { return cOpValid_IDParserRuleCall_1_0_1_0; }
		
		/**
		 * Gets the right assignment 1 1.
		 *
		 * @return the right assignment 1 1
		 */
		//right=Unit
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }
		
		/**
		 * Gets the right unit parser rule call 1 1 0.
		 *
		 * @return the right unit parser rule call 1 1 0
		 */
		//Unit
		public RuleCall getRightUnitParserRuleCall_1_1_0() { return cRightUnitParserRuleCall_1_1_0; }
	}
	
	/**
	 * The Class UnitElements.
	 */
	public class UnitElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Unit");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c unary parser rule call 0. */
		private final RuleCall cUnaryParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c group 1 0. */
		private final Group cGroup_1_0 = (Group)cGroup_1.eContents().get(0);
		
		/** The c unit left action 1 0 0. */
		private final Action cUnitLeftAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		
		/** The c op assignment 1 0 1. */
		private final Assignment cOpAssignment_1_0_1 = (Assignment)cGroup_1_0.eContents().get(1);
		
		/** The c op alternatives 1 0 1 0. */
		private final Alternatives cOpAlternatives_1_0_1_0 = (Alternatives)cOpAssignment_1_0_1.eContents().get(0);
		
		/** The c op degree sign keyword 1 0 1 0 0. */
		private final Keyword cOpDegreeSignKeyword_1_0_1_0_0 = (Keyword)cOpAlternatives_1_0_1_0.eContents().get(0);
		
		/** The c op number sign keyword 1 0 1 0 1. */
		private final Keyword cOpNumberSignKeyword_1_0_1_0_1 = (Keyword)cOpAlternatives_1_0_1_0.eContents().get(1);
		
		/** The c right assignment 1 1. */
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		
		/** The c right unit ref parser rule call 1 1 0. */
		private final RuleCall cRightUnitRefParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//Unit returns Expression:
		//    Unary
		//    (({Unit.left=current} op=('°'|"#")) right=UnitRef)?;
		@Override public ParserRule getRule() { return rule; }
		
		//Unary
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//(({Unit.left=current} op=('°'|"#")) right=UnitRef)?
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the unary parser rule call 0.
		 *
		 * @return the unary parser rule call 0
		 */
		//Unary
		public RuleCall getUnaryParserRuleCall_0() { return cUnaryParserRuleCall_0; }
		
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//(({Unit.left=current} op=('°'|"#")) right=UnitRef)?
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the group 1 0.
		 *
		 * @return the group 1 0
		 */
		//({Unit.left=current} op=('°'|"#"))
		public Group getGroup_1_0() { return cGroup_1_0; }
		
		/**
		 * Gets the unit left action 1 0 0.
		 *
		 * @return the unit left action 1 0 0
		 */
		//{Unit.left=current}
		public Action getUnitLeftAction_1_0_0() { return cUnitLeftAction_1_0_0; }
		
		/**
		 * Gets the op assignment 1 0 1.
		 *
		 * @return the op assignment 1 0 1
		 */
		//op=('°'|"#")
		public Assignment getOpAssignment_1_0_1() { return cOpAssignment_1_0_1; }
		
		/**
		 * Gets the op alternatives 1 0 1 0.
		 *
		 * @return the op alternatives 1 0 1 0
		 */
		//('°'|"#")
		public Alternatives getOpAlternatives_1_0_1_0() { return cOpAlternatives_1_0_1_0; }
		
		/**
		 * Gets the op degree sign keyword 1 0 1 0 0.
		 *
		 * @return the op degree sign keyword 1 0 1 0 0
		 */
		//'°'
		public Keyword getOpDegreeSignKeyword_1_0_1_0_0() { return cOpDegreeSignKeyword_1_0_1_0_0; }
		
		/**
		 * Gets the op number sign keyword 1 0 1 0 1.
		 *
		 * @return the op number sign keyword 1 0 1 0 1
		 */
		//"#"
		public Keyword getOpNumberSignKeyword_1_0_1_0_1() { return cOpNumberSignKeyword_1_0_1_0_1; }
		
		/**
		 * Gets the right assignment 1 1.
		 *
		 * @return the right assignment 1 1
		 */
		//right=UnitRef
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }
		
		/**
		 * Gets the right unit ref parser rule call 1 1 0.
		 *
		 * @return the right unit ref parser rule call 1 1 0
		 */
		//UnitRef
		public RuleCall getRightUnitRefParserRuleCall_1_1_0() { return cRightUnitRefParserRuleCall_1_1_0; }
	}
	
	/**
	 * The Class UnaryElements.
	 */
	public class UnaryElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Unary");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c access parser rule call 0. */
		private final RuleCall cAccessParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		
		/** The c unary action 1 0. */
		private final Action cUnaryAction_1_0 = (Action)cGroup_1.eContents().get(0);
		
		/** The c alternatives 1 1. */
		private final Alternatives cAlternatives_1_1 = (Alternatives)cGroup_1.eContents().get(1);
		
		/** The c group 1 1 0. */
		private final Group cGroup_1_1_0 = (Group)cAlternatives_1_1.eContents().get(0);
		
		/** The c op assignment 1 1 0 0. */
		private final Assignment cOpAssignment_1_1_0_0 = (Assignment)cGroup_1_1_0.eContents().get(0);
		
		/** The c op alternatives 1 1 0 0 0. */
		private final Alternatives cOpAlternatives_1_1_0_0_0 = (Alternatives)cOpAssignment_1_1_0_0.eContents().get(0);
		
		/** The c op degree sign keyword 1 1 0 0 0 0. */
		private final Keyword cOpDegreeSignKeyword_1_1_0_0_0_0 = (Keyword)cOpAlternatives_1_1_0_0_0.eContents().get(0);
		
		/** The c op number sign keyword 1 1 0 0 0 1. */
		private final Keyword cOpNumberSignKeyword_1_1_0_0_0_1 = (Keyword)cOpAlternatives_1_1_0_0_0.eContents().get(1);
		
		/** The c right assignment 1 1 0 1. */
		private final Assignment cRightAssignment_1_1_0_1 = (Assignment)cGroup_1_1_0.eContents().get(1);
		
		/** The c right unit ref parser rule call 1 1 0 1 0. */
		private final RuleCall cRightUnitRefParserRuleCall_1_1_0_1_0 = (RuleCall)cRightAssignment_1_1_0_1.eContents().get(0);
		
		/** The c group 1 1 1. */
		private final Group cGroup_1_1_1 = (Group)cAlternatives_1_1.eContents().get(1);
		
		/** The c op assignment 1 1 1 0. */
		private final Assignment cOpAssignment_1_1_1_0 = (Assignment)cGroup_1_1_1.eContents().get(0);
		
		/** The c op alternatives 1 1 1 0 0. */
		private final Alternatives cOpAlternatives_1_1_1_0_0 = (Alternatives)cOpAssignment_1_1_1_0.eContents().get(0);
		
		/** The c op hyphen minus keyword 1 1 1 0 0 0. */
		private final Keyword cOpHyphenMinusKeyword_1_1_1_0_0_0 = (Keyword)cOpAlternatives_1_1_1_0_0.eContents().get(0);
		
		/** The c op exclamation mark keyword 1 1 1 0 0 1. */
		private final Keyword cOpExclamationMarkKeyword_1_1_1_0_0_1 = (Keyword)cOpAlternatives_1_1_1_0_0.eContents().get(1);
		
		/** The c op my keyword 1 1 1 0 0 2. */
		private final Keyword cOpMyKeyword_1_1_1_0_0_2 = (Keyword)cOpAlternatives_1_1_1_0_0.eContents().get(2);
		
		/** The c op the keyword 1 1 1 0 0 3. */
		private final Keyword cOpTheKeyword_1_1_1_0_0_3 = (Keyword)cOpAlternatives_1_1_1_0_0.eContents().get(3);
		
		/** The c op not keyword 1 1 1 0 0 4. */
		private final Keyword cOpNotKeyword_1_1_1_0_0_4 = (Keyword)cOpAlternatives_1_1_1_0_0.eContents().get(4);
		
		/** The c right assignment 1 1 1 1. */
		private final Assignment cRightAssignment_1_1_1_1 = (Assignment)cGroup_1_1_1.eContents().get(1);
		
		/** The c right unary parser rule call 1 1 1 1 0. */
		private final RuleCall cRightUnaryParserRuleCall_1_1_1_1_0 = (RuleCall)cRightAssignment_1_1_1_1.eContents().get(0);
		
		//Unary returns Expression:
		//    Access |
		//    {Unary} ((op=('°'|'#') right=UnitRef) | (op=('-' | '!' | 'my' | 'the' | 'not')
		//    right=Unary));
		@Override public ParserRule getRule() { return rule; }
		
		//Access |
		//{Unary} ((op=('°'|'#') right=UnitRef) | (op=('-' | '!' | 'my' | 'the' | 'not')
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//right=Unary))
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the access parser rule call 0.
		 *
		 * @return the access parser rule call 0
		 */
		//Access
		public RuleCall getAccessParserRuleCall_0() { return cAccessParserRuleCall_0; }
		
		//{Unary} ((op=('°'|'#') right=UnitRef) | (op=('-' | '!' | 'my' | 'the' | 'not')
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//right=Unary))
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the unary action 1 0.
		 *
		 * @return the unary action 1 0
		 */
		//{Unary}
		public Action getUnaryAction_1_0() { return cUnaryAction_1_0; }
		
		//((op=('°'|'#') right=UnitRef) | (op=('-' | '!' | 'my' | 'the' | 'not')
		/**
		 * Gets the alternatives 1 1.
		 *
		 * @return the alternatives 1 1
		 */
		//   right=Unary))
		public Alternatives getAlternatives_1_1() { return cAlternatives_1_1; }
		
		/**
		 * Gets the group 1 1 0.
		 *
		 * @return the group 1 1 0
		 */
		//(op=('°'|'#') right=UnitRef)
		public Group getGroup_1_1_0() { return cGroup_1_1_0; }
		
		/**
		 * Gets the op assignment 1 1 0 0.
		 *
		 * @return the op assignment 1 1 0 0
		 */
		//op=('°'|'#')
		public Assignment getOpAssignment_1_1_0_0() { return cOpAssignment_1_1_0_0; }
		
		/**
		 * Gets the op alternatives 1 1 0 0 0.
		 *
		 * @return the op alternatives 1 1 0 0 0
		 */
		//('°'|'#')
		public Alternatives getOpAlternatives_1_1_0_0_0() { return cOpAlternatives_1_1_0_0_0; }
		
		/**
		 * Gets the op degree sign keyword 1 1 0 0 0 0.
		 *
		 * @return the op degree sign keyword 1 1 0 0 0 0
		 */
		//'°'
		public Keyword getOpDegreeSignKeyword_1_1_0_0_0_0() { return cOpDegreeSignKeyword_1_1_0_0_0_0; }
		
		/**
		 * Gets the op number sign keyword 1 1 0 0 0 1.
		 *
		 * @return the op number sign keyword 1 1 0 0 0 1
		 */
		//'#'
		public Keyword getOpNumberSignKeyword_1_1_0_0_0_1() { return cOpNumberSignKeyword_1_1_0_0_0_1; }
		
		/**
		 * Gets the right assignment 1 1 0 1.
		 *
		 * @return the right assignment 1 1 0 1
		 */
		//right=UnitRef
		public Assignment getRightAssignment_1_1_0_1() { return cRightAssignment_1_1_0_1; }
		
		/**
		 * Gets the right unit ref parser rule call 1 1 0 1 0.
		 *
		 * @return the right unit ref parser rule call 1 1 0 1 0
		 */
		//UnitRef
		public RuleCall getRightUnitRefParserRuleCall_1_1_0_1_0() { return cRightUnitRefParserRuleCall_1_1_0_1_0; }
		
		//(op=('-' | '!' | 'my' | 'the' | 'not')
		/**
		 * Gets the group 1 1 1.
		 *
		 * @return the group 1 1 1
		 */
		//   right=Unary)
		public Group getGroup_1_1_1() { return cGroup_1_1_1; }
		
		/**
		 * Gets the op assignment 1 1 1 0.
		 *
		 * @return the op assignment 1 1 1 0
		 */
		//op=('-' | '!' | 'my' | 'the' | 'not')
		public Assignment getOpAssignment_1_1_1_0() { return cOpAssignment_1_1_1_0; }
		
		/**
		 * Gets the op alternatives 1 1 1 0 0.
		 *
		 * @return the op alternatives 1 1 1 0 0
		 */
		//('-' | '!' | 'my' | 'the' | 'not')
		public Alternatives getOpAlternatives_1_1_1_0_0() { return cOpAlternatives_1_1_1_0_0; }
		
		/**
		 * Gets the op hyphen minus keyword 1 1 1 0 0 0.
		 *
		 * @return the op hyphen minus keyword 1 1 1 0 0 0
		 */
		//'-'
		public Keyword getOpHyphenMinusKeyword_1_1_1_0_0_0() { return cOpHyphenMinusKeyword_1_1_1_0_0_0; }
		
		/**
		 * Gets the op exclamation mark keyword 1 1 1 0 0 1.
		 *
		 * @return the op exclamation mark keyword 1 1 1 0 0 1
		 */
		//'!'
		public Keyword getOpExclamationMarkKeyword_1_1_1_0_0_1() { return cOpExclamationMarkKeyword_1_1_1_0_0_1; }
		
		/**
		 * Gets the op my keyword 1 1 1 0 0 2.
		 *
		 * @return the op my keyword 1 1 1 0 0 2
		 */
		//'my'
		public Keyword getOpMyKeyword_1_1_1_0_0_2() { return cOpMyKeyword_1_1_1_0_0_2; }
		
		/**
		 * Gets the op the keyword 1 1 1 0 0 3.
		 *
		 * @return the op the keyword 1 1 1 0 0 3
		 */
		//'the'
		public Keyword getOpTheKeyword_1_1_1_0_0_3() { return cOpTheKeyword_1_1_1_0_0_3; }
		
		/**
		 * Gets the op not keyword 1 1 1 0 0 4.
		 *
		 * @return the op not keyword 1 1 1 0 0 4
		 */
		//'not'
		public Keyword getOpNotKeyword_1_1_1_0_0_4() { return cOpNotKeyword_1_1_1_0_0_4; }
		
		/**
		 * Gets the right assignment 1 1 1 1.
		 *
		 * @return the right assignment 1 1 1 1
		 */
		//right=Unary
		public Assignment getRightAssignment_1_1_1_1() { return cRightAssignment_1_1_1_1; }
		
		/**
		 * Gets the right unary parser rule call 1 1 1 1 0.
		 *
		 * @return the right unary parser rule call 1 1 1 1 0
		 */
		//Unary
		public RuleCall getRightUnaryParserRuleCall_1_1_1_1_0() { return cRightUnaryParserRuleCall_1_1_1_1_0; }
	}
	
	/**
	 * The Class AccessElements.
	 */
	public class AccessElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Access");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c primary parser rule call 0. */
		private final RuleCall cPrimaryParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		
		/** The c access left action 1 0. */
		private final Action cAccessLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		
		/** The c alternatives 1 1. */
		private final Alternatives cAlternatives_1_1 = (Alternatives)cGroup_1.eContents().get(1);
		
		/** The c group 1 1 0. */
		private final Group cGroup_1_1_0 = (Group)cAlternatives_1_1.eContents().get(0);
		
		/** The c op assignment 1 1 0 0. */
		private final Assignment cOpAssignment_1_1_0_0 = (Assignment)cGroup_1_1_0.eContents().get(0);
		
		/** The c op left square bracket keyword 1 1 0 0 0. */
		private final Keyword cOpLeftSquareBracketKeyword_1_1_0_0_0 = (Keyword)cOpAssignment_1_1_0_0.eContents().get(0);
		
		/** The c right assignment 1 1 0 1. */
		private final Assignment cRightAssignment_1_1_0_1 = (Assignment)cGroup_1_1_0.eContents().get(1);
		
		/** The c right expression list parser rule call 1 1 0 1 0. */
		private final RuleCall cRightExpressionListParserRuleCall_1_1_0_1_0 = (RuleCall)cRightAssignment_1_1_0_1.eContents().get(0);
		
		/** The c right square bracket keyword 1 1 0 2. */
		private final Keyword cRightSquareBracketKeyword_1_1_0_2 = (Keyword)cGroup_1_1_0.eContents().get(2);
		
		/** The c group 1 1 1. */
		private final Group cGroup_1_1_1 = (Group)cAlternatives_1_1.eContents().get(1);
		
		/** The c op assignment 1 1 1 0. */
		private final Assignment cOpAssignment_1_1_1_0 = (Assignment)cGroup_1_1_1.eContents().get(0);
		
		/** The c op full stop keyword 1 1 1 0 0. */
		private final Keyword cOpFullStopKeyword_1_1_1_0_0 = (Keyword)cOpAssignment_1_1_1_0.eContents().get(0);
		
		/** The c right assignment 1 1 1 1. */
		private final Assignment cRightAssignment_1_1_1_1 = (Assignment)cGroup_1_1_1.eContents().get(1);
		
		/** The c right alternatives 1 1 1 1 0. */
		private final Alternatives cRightAlternatives_1_1_1_1_0 = (Alternatives)cRightAssignment_1_1_1_1.eContents().get(0);
		
		/** The c right abstract ref parser rule call 1 1 1 1 0 0. */
		private final RuleCall cRightAbstractRefParserRuleCall_1_1_1_1_0_0 = (RuleCall)cRightAlternatives_1_1_1_1_0.eContents().get(0);
		
		/** The c right string literal parser rule call 1 1 1 1 0 1. */
		private final RuleCall cRightStringLiteralParserRuleCall_1_1_1_1_0_1 = (RuleCall)cRightAlternatives_1_1_1_1_0.eContents().get(1);
		
		//Access returns Expression:
		//    Primary ({Access.left = current} (
		//        (op='[' right=ExpressionList? ']') | (op="." right=(AbstractRef|StringLiteral))
		//    ))*;
		@Override public ParserRule getRule() { return rule; }
		
		//Primary ({Access.left = current} (
		//    (op='[' right=ExpressionList? ']') | (op="." right=(AbstractRef|StringLiteral))
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//))*
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the primary parser rule call 0.
		 *
		 * @return the primary parser rule call 0
		 */
		//Primary
		public RuleCall getPrimaryParserRuleCall_0() { return cPrimaryParserRuleCall_0; }
		
		//({Access.left = current} (
		//       (op='[' right=ExpressionList? ']') | (op="." right=(AbstractRef|StringLiteral))
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//   ))*
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the access left action 1 0.
		 *
		 * @return the access left action 1 0
		 */
		//{Access.left = current}
		public Action getAccessLeftAction_1_0() { return cAccessLeftAction_1_0; }
		
		//(
		//       (op='[' right=ExpressionList? ']') | (op="." right=(AbstractRef|StringLiteral))
		/**
		 * Gets the alternatives 1 1.
		 *
		 * @return the alternatives 1 1
		 */
		//   )
		public Alternatives getAlternatives_1_1() { return cAlternatives_1_1; }
		
		/**
		 * Gets the group 1 1 0.
		 *
		 * @return the group 1 1 0
		 */
		//(op='[' right=ExpressionList? ']')
		public Group getGroup_1_1_0() { return cGroup_1_1_0; }
		
		/**
		 * Gets the op assignment 1 1 0 0.
		 *
		 * @return the op assignment 1 1 0 0
		 */
		//op='['
		public Assignment getOpAssignment_1_1_0_0() { return cOpAssignment_1_1_0_0; }
		
		/**
		 * Gets the op left square bracket keyword 1 1 0 0 0.
		 *
		 * @return the op left square bracket keyword 1 1 0 0 0
		 */
		//'['
		public Keyword getOpLeftSquareBracketKeyword_1_1_0_0_0() { return cOpLeftSquareBracketKeyword_1_1_0_0_0; }
		
		/**
		 * Gets the right assignment 1 1 0 1.
		 *
		 * @return the right assignment 1 1 0 1
		 */
		//right=ExpressionList?
		public Assignment getRightAssignment_1_1_0_1() { return cRightAssignment_1_1_0_1; }
		
		/**
		 * Gets the right expression list parser rule call 1 1 0 1 0.
		 *
		 * @return the right expression list parser rule call 1 1 0 1 0
		 */
		//ExpressionList
		public RuleCall getRightExpressionListParserRuleCall_1_1_0_1_0() { return cRightExpressionListParserRuleCall_1_1_0_1_0; }
		
		/**
		 * Gets the right square bracket keyword 1 1 0 2.
		 *
		 * @return the right square bracket keyword 1 1 0 2
		 */
		//']'
		public Keyword getRightSquareBracketKeyword_1_1_0_2() { return cRightSquareBracketKeyword_1_1_0_2; }
		
		/**
		 * Gets the group 1 1 1.
		 *
		 * @return the group 1 1 1
		 */
		//(op="." right=(AbstractRef|StringLiteral))
		public Group getGroup_1_1_1() { return cGroup_1_1_1; }
		
		/**
		 * Gets the op assignment 1 1 1 0.
		 *
		 * @return the op assignment 1 1 1 0
		 */
		//op="."
		public Assignment getOpAssignment_1_1_1_0() { return cOpAssignment_1_1_1_0; }
		
		/**
		 * Gets the op full stop keyword 1 1 1 0 0.
		 *
		 * @return the op full stop keyword 1 1 1 0 0
		 */
		//"."
		public Keyword getOpFullStopKeyword_1_1_1_0_0() { return cOpFullStopKeyword_1_1_1_0_0; }
		
		/**
		 * Gets the right assignment 1 1 1 1.
		 *
		 * @return the right assignment 1 1 1 1
		 */
		//right=(AbstractRef|StringLiteral)
		public Assignment getRightAssignment_1_1_1_1() { return cRightAssignment_1_1_1_1; }
		
		/**
		 * Gets the right alternatives 1 1 1 1 0.
		 *
		 * @return the right alternatives 1 1 1 1 0
		 */
		//(AbstractRef|StringLiteral)
		public Alternatives getRightAlternatives_1_1_1_1_0() { return cRightAlternatives_1_1_1_1_0; }
		
		/**
		 * Gets the right abstract ref parser rule call 1 1 1 1 0 0.
		 *
		 * @return the right abstract ref parser rule call 1 1 1 1 0 0
		 */
		//AbstractRef
		public RuleCall getRightAbstractRefParserRuleCall_1_1_1_1_0_0() { return cRightAbstractRefParserRuleCall_1_1_1_1_0_0; }
		
		/**
		 * Gets the right string literal parser rule call 1 1 1 1 0 1.
		 *
		 * @return the right string literal parser rule call 1 1 1 1 0 1
		 */
		//StringLiteral
		public RuleCall getRightStringLiteralParserRuleCall_1_1_1_1_0_1() { return cRightStringLiteralParserRuleCall_1_1_1_1_0_1; }
	}
	
	/**
	 * The Class PrimaryElements.
	 */
	public class PrimaryElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Primary");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c terminal expression parser rule call 0. */
		private final RuleCall cTerminalExpressionParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c abstract ref parser rule call 1. */
		private final RuleCall cAbstractRefParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		/** The c group 2. */
		private final Group cGroup_2 = (Group)cAlternatives.eContents().get(2);
		
		/** The c left parenthesis keyword 2 0. */
		private final Keyword cLeftParenthesisKeyword_2_0 = (Keyword)cGroup_2.eContents().get(0);
		
		/** The c expression list parser rule call 2 1. */
		private final RuleCall cExpressionListParserRuleCall_2_1 = (RuleCall)cGroup_2.eContents().get(1);
		
		/** The c right parenthesis keyword 2 2. */
		private final Keyword cRightParenthesisKeyword_2_2 = (Keyword)cGroup_2.eContents().get(2);
		
		/** The c group 3. */
		private final Group cGroup_3 = (Group)cAlternatives.eContents().get(3);
		
		/** The c left square bracket keyword 3 0. */
		private final Keyword cLeftSquareBracketKeyword_3_0 = (Keyword)cGroup_3.eContents().get(0);
		
		/** The c array action 3 1. */
		private final Action cArrayAction_3_1 = (Action)cGroup_3.eContents().get(1);
		
		/** The c exprs assignment 3 2. */
		private final Assignment cExprsAssignment_3_2 = (Assignment)cGroup_3.eContents().get(2);
		
		/** The c exprs expression list parser rule call 3 2 0. */
		private final RuleCall cExprsExpressionListParserRuleCall_3_2_0 = (RuleCall)cExprsAssignment_3_2.eContents().get(0);
		
		/** The c right square bracket keyword 3 3. */
		private final Keyword cRightSquareBracketKeyword_3_3 = (Keyword)cGroup_3.eContents().get(3);
		
		/** The c group 4. */
		private final Group cGroup_4 = (Group)cAlternatives.eContents().get(4);
		
		/** The c left curly bracket keyword 4 0. */
		private final Keyword cLeftCurlyBracketKeyword_4_0 = (Keyword)cGroup_4.eContents().get(0);
		
		/** The c point action 4 1. */
		private final Action cPointAction_4_1 = (Action)cGroup_4.eContents().get(1);
		
		/** The c left assignment 4 2. */
		private final Assignment cLeftAssignment_4_2 = (Assignment)cGroup_4.eContents().get(2);
		
		/** The c left expression parser rule call 4 2 0. */
		private final RuleCall cLeftExpressionParserRuleCall_4_2_0 = (RuleCall)cLeftAssignment_4_2.eContents().get(0);
		
		/** The c op assignment 4 3. */
		private final Assignment cOpAssignment_4_3 = (Assignment)cGroup_4.eContents().get(3);
		
		/** The c op comma keyword 4 3 0. */
		private final Keyword cOpCommaKeyword_4_3_0 = (Keyword)cOpAssignment_4_3.eContents().get(0);
		
		/** The c right assignment 4 4. */
		private final Assignment cRightAssignment_4_4 = (Assignment)cGroup_4.eContents().get(4);
		
		/** The c right expression parser rule call 4 4 0. */
		private final RuleCall cRightExpressionParserRuleCall_4_4_0 = (RuleCall)cRightAssignment_4_4.eContents().get(0);
		
		/** The c group 4 5. */
		private final Group cGroup_4_5 = (Group)cGroup_4.eContents().get(5);
		
		/** The c comma keyword 4 5 0. */
		private final Keyword cCommaKeyword_4_5_0 = (Keyword)cGroup_4_5.eContents().get(0);
		
		/** The c Z assignment 4 5 1. */
		private final Assignment cZAssignment_4_5_1 = (Assignment)cGroup_4_5.eContents().get(1);
		
		/** The c Z expression parser rule call 4 5 1 0. */
		private final RuleCall cZExpressionParserRuleCall_4_5_1_0 = (RuleCall)cZAssignment_4_5_1.eContents().get(0);
		
		/** The c right curly bracket keyword 4 6. */
		private final Keyword cRightCurlyBracketKeyword_4_6 = (Keyword)cGroup_4.eContents().get(6);
		
		//Primary returns Expression:
		//    TerminalExpression |
		//    AbstractRef |
		//    '(' ExpressionList ')' |
		//    '[' {Array} exprs=ExpressionList? ']' |
		//    '{' {Point} left=Expression op=',' right=Expression (',' z=Expression)? '}';
		@Override public ParserRule getRule() { return rule; }
		
		//TerminalExpression |
		//AbstractRef |
		//'(' ExpressionList ')' |
		//'[' {Array} exprs=ExpressionList? ']' |
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//'{' {Point} left=Expression op=',' right=Expression (',' z=Expression)? '}'
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the terminal expression parser rule call 0.
		 *
		 * @return the terminal expression parser rule call 0
		 */
		//TerminalExpression
		public RuleCall getTerminalExpressionParserRuleCall_0() { return cTerminalExpressionParserRuleCall_0; }
		
		/**
		 * Gets the abstract ref parser rule call 1.
		 *
		 * @return the abstract ref parser rule call 1
		 */
		//AbstractRef
		public RuleCall getAbstractRefParserRuleCall_1() { return cAbstractRefParserRuleCall_1; }
		
		/**
		 * Gets the group 2.
		 *
		 * @return the group 2
		 */
		//'(' ExpressionList ')'
		public Group getGroup_2() { return cGroup_2; }
		
		/**
		 * Gets the left parenthesis keyword 2 0.
		 *
		 * @return the left parenthesis keyword 2 0
		 */
		//'('
		public Keyword getLeftParenthesisKeyword_2_0() { return cLeftParenthesisKeyword_2_0; }
		
		/**
		 * Gets the expression list parser rule call 2 1.
		 *
		 * @return the expression list parser rule call 2 1
		 */
		//ExpressionList
		public RuleCall getExpressionListParserRuleCall_2_1() { return cExpressionListParserRuleCall_2_1; }
		
		/**
		 * Gets the right parenthesis keyword 2 2.
		 *
		 * @return the right parenthesis keyword 2 2
		 */
		//')'
		public Keyword getRightParenthesisKeyword_2_2() { return cRightParenthesisKeyword_2_2; }
		
		/**
		 * Gets the group 3.
		 *
		 * @return the group 3
		 */
		//'[' {Array} exprs=ExpressionList? ']'
		public Group getGroup_3() { return cGroup_3; }
		
		/**
		 * Gets the left square bracket keyword 3 0.
		 *
		 * @return the left square bracket keyword 3 0
		 */
		//'['
		public Keyword getLeftSquareBracketKeyword_3_0() { return cLeftSquareBracketKeyword_3_0; }
		
		/**
		 * Gets the array action 3 1.
		 *
		 * @return the array action 3 1
		 */
		//{Array}
		public Action getArrayAction_3_1() { return cArrayAction_3_1; }
		
		/**
		 * Gets the exprs assignment 3 2.
		 *
		 * @return the exprs assignment 3 2
		 */
		//exprs=ExpressionList?
		public Assignment getExprsAssignment_3_2() { return cExprsAssignment_3_2; }
		
		/**
		 * Gets the exprs expression list parser rule call 3 2 0.
		 *
		 * @return the exprs expression list parser rule call 3 2 0
		 */
		//ExpressionList
		public RuleCall getExprsExpressionListParserRuleCall_3_2_0() { return cExprsExpressionListParserRuleCall_3_2_0; }
		
		/**
		 * Gets the right square bracket keyword 3 3.
		 *
		 * @return the right square bracket keyword 3 3
		 */
		//']'
		public Keyword getRightSquareBracketKeyword_3_3() { return cRightSquareBracketKeyword_3_3; }
		
		/**
		 * Gets the group 4.
		 *
		 * @return the group 4
		 */
		//'{' {Point} left=Expression op=',' right=Expression (',' z=Expression)? '}'
		public Group getGroup_4() { return cGroup_4; }
		
		/**
		 * Gets the left curly bracket keyword 4 0.
		 *
		 * @return the left curly bracket keyword 4 0
		 */
		//'{'
		public Keyword getLeftCurlyBracketKeyword_4_0() { return cLeftCurlyBracketKeyword_4_0; }
		
		/**
		 * Gets the point action 4 1.
		 *
		 * @return the point action 4 1
		 */
		//{Point}
		public Action getPointAction_4_1() { return cPointAction_4_1; }
		
		/**
		 * Gets the left assignment 4 2.
		 *
		 * @return the left assignment 4 2
		 */
		//left=Expression
		public Assignment getLeftAssignment_4_2() { return cLeftAssignment_4_2; }
		
		/**
		 * Gets the left expression parser rule call 4 2 0.
		 *
		 * @return the left expression parser rule call 4 2 0
		 */
		//Expression
		public RuleCall getLeftExpressionParserRuleCall_4_2_0() { return cLeftExpressionParserRuleCall_4_2_0; }
		
		/**
		 * Gets the op assignment 4 3.
		 *
		 * @return the op assignment 4 3
		 */
		//op=','
		public Assignment getOpAssignment_4_3() { return cOpAssignment_4_3; }
		
		/**
		 * Gets the op comma keyword 4 3 0.
		 *
		 * @return the op comma keyword 4 3 0
		 */
		//','
		public Keyword getOpCommaKeyword_4_3_0() { return cOpCommaKeyword_4_3_0; }
		
		/**
		 * Gets the right assignment 4 4.
		 *
		 * @return the right assignment 4 4
		 */
		//right=Expression
		public Assignment getRightAssignment_4_4() { return cRightAssignment_4_4; }
		
		/**
		 * Gets the right expression parser rule call 4 4 0.
		 *
		 * @return the right expression parser rule call 4 4 0
		 */
		//Expression
		public RuleCall getRightExpressionParserRuleCall_4_4_0() { return cRightExpressionParserRuleCall_4_4_0; }
		
		/**
		 * Gets the group 4 5.
		 *
		 * @return the group 4 5
		 */
		//(',' z=Expression)?
		public Group getGroup_4_5() { return cGroup_4_5; }
		
		/**
		 * Gets the comma keyword 4 5 0.
		 *
		 * @return the comma keyword 4 5 0
		 */
		//','
		public Keyword getCommaKeyword_4_5_0() { return cCommaKeyword_4_5_0; }
		
		/**
		 * Gets the z assignment 4 5 1.
		 *
		 * @return the z assignment 4 5 1
		 */
		//z=Expression
		public Assignment getZAssignment_4_5_1() { return cZAssignment_4_5_1; }
		
		/**
		 * Gets the z expression parser rule call 4 5 1 0.
		 *
		 * @return the z expression parser rule call 4 5 1 0
		 */
		//Expression
		public RuleCall getZExpressionParserRuleCall_4_5_1_0() { return cZExpressionParserRuleCall_4_5_1_0; }
		
		/**
		 * Gets the right curly bracket keyword 4 6.
		 *
		 * @return the right curly bracket keyword 4 6
		 */
		//'}'
		public Keyword getRightCurlyBracketKeyword_4_6() { return cRightCurlyBracketKeyword_4_6; }
	}
	
	/**
	 * The Class AbstractRefElements.
	 */
	public class AbstractRefElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.AbstractRef");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c function parser rule call 0. */
		private final RuleCall cFunctionParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c variable ref parser rule call 1. */
		private final RuleCall cVariableRefParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//AbstractRef returns Expression:
		//    =>Function | VariableRef;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//=>Function | VariableRef
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the function parser rule call 0.
		 *
		 * @return the function parser rule call 0
		 */
		//=>Function
		public RuleCall getFunctionParserRuleCall_0() { return cFunctionParserRuleCall_0; }
		
		/**
		 * Gets the variable ref parser rule call 1.
		 *
		 * @return the variable ref parser rule call 1
		 */
		//VariableRef
		public RuleCall getVariableRefParserRuleCall_1() { return cVariableRefParserRuleCall_1; }
	}
	
	/**
	 * The Class FunctionElements.
	 */
	public class FunctionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Function");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c function action 0. */
		private final Action cFunctionAction_0 = (Action)cGroup.eContents().get(0);
		
		/** The c left assignment 1. */
		private final Assignment cLeftAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c left action ref parser rule call 1 0. */
		private final RuleCall cLeftActionRefParserRuleCall_1_0 = (RuleCall)cLeftAssignment_1.eContents().get(0);
		
		/** The c type assignment 2. */
		private final Assignment cTypeAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c type type info parser rule call 2 0. */
		private final RuleCall cTypeTypeInfoParserRuleCall_2_0 = (RuleCall)cTypeAssignment_2.eContents().get(0);
		
		/** The c left parenthesis keyword 3. */
		private final Keyword cLeftParenthesisKeyword_3 = (Keyword)cGroup.eContents().get(3);
		
		/** The c right assignment 4. */
		private final Assignment cRightAssignment_4 = (Assignment)cGroup.eContents().get(4);
		
		/** The c right expression list parser rule call 4 0. */
		private final RuleCall cRightExpressionListParserRuleCall_4_0 = (RuleCall)cRightAssignment_4.eContents().get(0);
		
		/** The c right parenthesis keyword 5. */
		private final Keyword cRightParenthesisKeyword_5 = (Keyword)cGroup.eContents().get(5);
		
		//Function returns Expression:
		//    {Function} (left=ActionRef) (type=TypeInfo)? '(' right=ExpressionList? ')';
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//{Function} (left=ActionRef) (type=TypeInfo)? '(' right=ExpressionList? ')'
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the function action 0.
		 *
		 * @return the function action 0
		 */
		//{Function}
		public Action getFunctionAction_0() { return cFunctionAction_0; }
		
		/**
		 * Gets the left assignment 1.
		 *
		 * @return the left assignment 1
		 */
		//(left=ActionRef)
		public Assignment getLeftAssignment_1() { return cLeftAssignment_1; }
		
		/**
		 * Gets the left action ref parser rule call 1 0.
		 *
		 * @return the left action ref parser rule call 1 0
		 */
		//ActionRef
		public RuleCall getLeftActionRefParserRuleCall_1_0() { return cLeftActionRefParserRuleCall_1_0; }
		
		/**
		 * Gets the type assignment 2.
		 *
		 * @return the type assignment 2
		 */
		//(type=TypeInfo)?
		public Assignment getTypeAssignment_2() { return cTypeAssignment_2; }
		
		/**
		 * Gets the type type info parser rule call 2 0.
		 *
		 * @return the type type info parser rule call 2 0
		 */
		//TypeInfo
		public RuleCall getTypeTypeInfoParserRuleCall_2_0() { return cTypeTypeInfoParserRuleCall_2_0; }
		
		/**
		 * Gets the left parenthesis keyword 3.
		 *
		 * @return the left parenthesis keyword 3
		 */
		//'('
		public Keyword getLeftParenthesisKeyword_3() { return cLeftParenthesisKeyword_3; }
		
		/**
		 * Gets the right assignment 4.
		 *
		 * @return the right assignment 4
		 */
		//right=ExpressionList?
		public Assignment getRightAssignment_4() { return cRightAssignment_4; }
		
		/**
		 * Gets the right expression list parser rule call 4 0.
		 *
		 * @return the right expression list parser rule call 4 0
		 */
		//ExpressionList
		public RuleCall getRightExpressionListParserRuleCall_4_0() { return cRightExpressionListParserRuleCall_4_0; }
		
		/**
		 * Gets the right parenthesis keyword 5.
		 *
		 * @return the right parenthesis keyword 5
		 */
		//')'
		public Keyword getRightParenthesisKeyword_5() { return cRightParenthesisKeyword_5; }
	}
	
	/**
	 * The Class ExpressionListElements.
	 */
	public class ExpressionListElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ExpressionList");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c group 0. */
		private final Group cGroup_0 = (Group)cAlternatives.eContents().get(0);
		
		/** The c exprs assignment 0 0. */
		private final Assignment cExprsAssignment_0_0 = (Assignment)cGroup_0.eContents().get(0);
		
		/** The c exprs expression parser rule call 0 0 0. */
		private final RuleCall cExprsExpressionParserRuleCall_0_0_0 = (RuleCall)cExprsAssignment_0_0.eContents().get(0);
		
		/** The c group 0 1. */
		private final Group cGroup_0_1 = (Group)cGroup_0.eContents().get(1);
		
		/** The c comma keyword 0 1 0. */
		private final Keyword cCommaKeyword_0_1_0 = (Keyword)cGroup_0_1.eContents().get(0);
		
		/** The c exprs assignment 0 1 1. */
		private final Assignment cExprsAssignment_0_1_1 = (Assignment)cGroup_0_1.eContents().get(1);
		
		/** The c exprs expression parser rule call 0 1 1 0. */
		private final RuleCall cExprsExpressionParserRuleCall_0_1_1_0 = (RuleCall)cExprsAssignment_0_1_1.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		
		/** The c exprs assignment 1 0. */
		private final Assignment cExprsAssignment_1_0 = (Assignment)cGroup_1.eContents().get(0);
		
		/** The c exprs parameter parser rule call 1 0 0. */
		private final RuleCall cExprsParameterParserRuleCall_1_0_0 = (RuleCall)cExprsAssignment_1_0.eContents().get(0);
		
		/** The c group 1 1. */
		private final Group cGroup_1_1 = (Group)cGroup_1.eContents().get(1);
		
		/** The c comma keyword 1 1 0. */
		private final Keyword cCommaKeyword_1_1_0 = (Keyword)cGroup_1_1.eContents().get(0);
		
		/** The c exprs assignment 1 1 1. */
		private final Assignment cExprsAssignment_1_1_1 = (Assignment)cGroup_1_1.eContents().get(1);
		
		/** The c exprs parameter parser rule call 1 1 1 0. */
		private final RuleCall cExprsParameterParserRuleCall_1_1_1_0 = (RuleCall)cExprsAssignment_1_1_1.eContents().get(0);
		
		//ExpressionList:
		//    (exprs+=Expression (',' exprs+=Expression)*) | (exprs+=Parameter (',' exprs+=Parameter)*);
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//(exprs+=Expression (',' exprs+=Expression)*) | (exprs+=Parameter (',' exprs+=Parameter)*)
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the group 0.
		 *
		 * @return the group 0
		 */
		//(exprs+=Expression (',' exprs+=Expression)*)
		public Group getGroup_0() { return cGroup_0; }
		
		/**
		 * Gets the exprs assignment 0 0.
		 *
		 * @return the exprs assignment 0 0
		 */
		//exprs+=Expression
		public Assignment getExprsAssignment_0_0() { return cExprsAssignment_0_0; }
		
		/**
		 * Gets the exprs expression parser rule call 0 0 0.
		 *
		 * @return the exprs expression parser rule call 0 0 0
		 */
		//Expression
		public RuleCall getExprsExpressionParserRuleCall_0_0_0() { return cExprsExpressionParserRuleCall_0_0_0; }
		
		/**
		 * Gets the group 0 1.
		 *
		 * @return the group 0 1
		 */
		//(',' exprs+=Expression)*
		public Group getGroup_0_1() { return cGroup_0_1; }
		
		/**
		 * Gets the comma keyword 0 1 0.
		 *
		 * @return the comma keyword 0 1 0
		 */
		//','
		public Keyword getCommaKeyword_0_1_0() { return cCommaKeyword_0_1_0; }
		
		/**
		 * Gets the exprs assignment 0 1 1.
		 *
		 * @return the exprs assignment 0 1 1
		 */
		//exprs+=Expression
		public Assignment getExprsAssignment_0_1_1() { return cExprsAssignment_0_1_1; }
		
		/**
		 * Gets the exprs expression parser rule call 0 1 1 0.
		 *
		 * @return the exprs expression parser rule call 0 1 1 0
		 */
		//Expression
		public RuleCall getExprsExpressionParserRuleCall_0_1_1_0() { return cExprsExpressionParserRuleCall_0_1_1_0; }
		
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//(exprs+=Parameter (',' exprs+=Parameter)*)
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the exprs assignment 1 0.
		 *
		 * @return the exprs assignment 1 0
		 */
		//exprs+=Parameter
		public Assignment getExprsAssignment_1_0() { return cExprsAssignment_1_0; }
		
		/**
		 * Gets the exprs parameter parser rule call 1 0 0.
		 *
		 * @return the exprs parameter parser rule call 1 0 0
		 */
		//Parameter
		public RuleCall getExprsParameterParserRuleCall_1_0_0() { return cExprsParameterParserRuleCall_1_0_0; }
		
		/**
		 * Gets the group 1 1.
		 *
		 * @return the group 1 1
		 */
		//(',' exprs+=Parameter)*
		public Group getGroup_1_1() { return cGroup_1_1; }
		
		/**
		 * Gets the comma keyword 1 1 0.
		 *
		 * @return the comma keyword 1 1 0
		 */
		//','
		public Keyword getCommaKeyword_1_1_0() { return cCommaKeyword_1_1_0; }
		
		/**
		 * Gets the exprs assignment 1 1 1.
		 *
		 * @return the exprs assignment 1 1 1
		 */
		//exprs+=Parameter
		public Assignment getExprsAssignment_1_1_1() { return cExprsAssignment_1_1_1; }
		
		/**
		 * Gets the exprs parameter parser rule call 1 1 1 0.
		 *
		 * @return the exprs parameter parser rule call 1 1 1 0
		 */
		//Parameter
		public RuleCall getExprsParameterParserRuleCall_1_1_1_0() { return cExprsParameterParserRuleCall_1_1_1_0; }
	}
	
	/**
	 * The Class ParameterElements.
	 */
	public class ParameterElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Parameter");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c parameter action 0. */
		private final Action cParameterAction_0 = (Action)cGroup.eContents().get(0);
		
		/** The c alternatives 1. */
		private final Alternatives cAlternatives_1 = (Alternatives)cGroup.eContents().get(1);
		
		/** The c built in facet key assignment 1 0. */
		private final Assignment cBuiltInFacetKeyAssignment_1_0 = (Assignment)cAlternatives_1.eContents().get(0);
		
		/** The c built in facet key alternatives 1 0 0. */
		private final Alternatives cBuiltInFacetKeyAlternatives_1_0_0 = (Alternatives)cBuiltInFacetKeyAssignment_1_0.eContents().get(0);
		
		/** The c built in facet key definition facet key parser rule call 1 0 0 0. */
		private final RuleCall cBuiltInFacetKeyDefinitionFacetKeyParserRuleCall_1_0_0_0 = (RuleCall)cBuiltInFacetKeyAlternatives_1_0_0.eContents().get(0);
		
		/** The c built in facet key type facet key parser rule call 1 0 0 1. */
		private final RuleCall cBuiltInFacetKeyTypeFacetKeyParserRuleCall_1_0_0_1 = (RuleCall)cBuiltInFacetKeyAlternatives_1_0_0.eContents().get(1);
		
		/** The c built in facet key special facet key parser rule call 1 0 0 2. */
		private final RuleCall cBuiltInFacetKeySpecialFacetKeyParserRuleCall_1_0_0_2 = (RuleCall)cBuiltInFacetKeyAlternatives_1_0_0.eContents().get(2);
		
		/** The c built in facet key action facet key parser rule call 1 0 0 3. */
		private final RuleCall cBuiltInFacetKeyActionFacetKeyParserRuleCall_1_0_0_3 = (RuleCall)cBuiltInFacetKeyAlternatives_1_0_0.eContents().get(3);
		
		/** The c built in facet key var facet key parser rule call 1 0 0 4. */
		private final RuleCall cBuiltInFacetKeyVarFacetKeyParserRuleCall_1_0_0_4 = (RuleCall)cBuiltInFacetKeyAlternatives_1_0_0.eContents().get(4);
		
		/** The c group 1 1. */
		private final Group cGroup_1_1 = (Group)cAlternatives_1.eContents().get(1);
		
		/** The c left assignment 1 1 0. */
		private final Assignment cLeftAssignment_1_1_0 = (Assignment)cGroup_1_1.eContents().get(0);
		
		/** The c left variable ref parser rule call 1 1 0 0. */
		private final RuleCall cLeftVariableRefParserRuleCall_1_1_0_0 = (RuleCall)cLeftAssignment_1_1_0.eContents().get(0);
		
		/** The c colon keyword 1 1 1. */
		private final Keyword cColonKeyword_1_1_1 = (Keyword)cGroup_1_1.eContents().get(1);
		
		/** The c right assignment 2. */
		private final Assignment cRightAssignment_2 = (Assignment)cGroup.eContents().get(2);
		
		/** The c right expression parser rule call 2 0. */
		private final RuleCall cRightExpressionParserRuleCall_2_0 = (RuleCall)cRightAssignment_2.eContents().get(0);
		
		//Parameter returns Expression:
		//    {Parameter} ((builtInFacetKey=(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey)) |
		//    (left=VariableRef ':')) right=Expression;
		@Override public ParserRule getRule() { return rule; }
		
		//{Parameter} ((builtInFacetKey=(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey)) |
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//(left=VariableRef ':')) right=Expression
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the parameter action 0.
		 *
		 * @return the parameter action 0
		 */
		//{Parameter}
		public Action getParameterAction_0() { return cParameterAction_0; }
		
		//((builtInFacetKey=(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey)) |
		/**
		 * Gets the alternatives 1.
		 *
		 * @return the alternatives 1
		 */
		//   (left=VariableRef ':'))
		public Alternatives getAlternatives_1() { return cAlternatives_1; }
		
		/**
		 * Gets the built in facet key assignment 1 0.
		 *
		 * @return the built in facet key assignment 1 0
		 */
		//(builtInFacetKey=(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey))
		public Assignment getBuiltInFacetKeyAssignment_1_0() { return cBuiltInFacetKeyAssignment_1_0; }
		
		/**
		 * Gets the built in facet key alternatives 1 0 0.
		 *
		 * @return the built in facet key alternatives 1 0 0
		 */
		//(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey)
		public Alternatives getBuiltInFacetKeyAlternatives_1_0_0() { return cBuiltInFacetKeyAlternatives_1_0_0; }
		
		/**
		 * Gets the built in facet key definition facet key parser rule call 1 0 0 0.
		 *
		 * @return the built in facet key definition facet key parser rule call 1 0 0 0
		 */
		//DefinitionFacetKey
		public RuleCall getBuiltInFacetKeyDefinitionFacetKeyParserRuleCall_1_0_0_0() { return cBuiltInFacetKeyDefinitionFacetKeyParserRuleCall_1_0_0_0; }
		
		/**
		 * Gets the built in facet key type facet key parser rule call 1 0 0 1.
		 *
		 * @return the built in facet key type facet key parser rule call 1 0 0 1
		 */
		//TypeFacetKey
		public RuleCall getBuiltInFacetKeyTypeFacetKeyParserRuleCall_1_0_0_1() { return cBuiltInFacetKeyTypeFacetKeyParserRuleCall_1_0_0_1; }
		
		/**
		 * Gets the built in facet key special facet key parser rule call 1 0 0 2.
		 *
		 * @return the built in facet key special facet key parser rule call 1 0 0 2
		 */
		//SpecialFacetKey
		public RuleCall getBuiltInFacetKeySpecialFacetKeyParserRuleCall_1_0_0_2() { return cBuiltInFacetKeySpecialFacetKeyParserRuleCall_1_0_0_2; }
		
		/**
		 * Gets the built in facet key action facet key parser rule call 1 0 0 3.
		 *
		 * @return the built in facet key action facet key parser rule call 1 0 0 3
		 */
		//ActionFacetKey
		public RuleCall getBuiltInFacetKeyActionFacetKeyParserRuleCall_1_0_0_3() { return cBuiltInFacetKeyActionFacetKeyParserRuleCall_1_0_0_3; }
		
		/**
		 * Gets the built in facet key var facet key parser rule call 1 0 0 4.
		 *
		 * @return the built in facet key var facet key parser rule call 1 0 0 4
		 */
		//VarFacetKey
		public RuleCall getBuiltInFacetKeyVarFacetKeyParserRuleCall_1_0_0_4() { return cBuiltInFacetKeyVarFacetKeyParserRuleCall_1_0_0_4; }
		
		/**
		 * Gets the group 1 1.
		 *
		 * @return the group 1 1
		 */
		//(left=VariableRef ':')
		public Group getGroup_1_1() { return cGroup_1_1; }
		
		/**
		 * Gets the left assignment 1 1 0.
		 *
		 * @return the left assignment 1 1 0
		 */
		//left=VariableRef
		public Assignment getLeftAssignment_1_1_0() { return cLeftAssignment_1_1_0; }
		
		/**
		 * Gets the left variable ref parser rule call 1 1 0 0.
		 *
		 * @return the left variable ref parser rule call 1 1 0 0
		 */
		//VariableRef
		public RuleCall getLeftVariableRefParserRuleCall_1_1_0_0() { return cLeftVariableRefParserRuleCall_1_1_0_0; }
		
		/**
		 * Gets the colon keyword 1 1 1.
		 *
		 * @return the colon keyword 1 1 1
		 */
		//':'
		public Keyword getColonKeyword_1_1_1() { return cColonKeyword_1_1_1; }
		
		/**
		 * Gets the right assignment 2.
		 *
		 * @return the right assignment 2
		 */
		//right=Expression
		public Assignment getRightAssignment_2() { return cRightAssignment_2; }
		
		/**
		 * Gets the right expression parser rule call 2 0.
		 *
		 * @return the right expression parser rule call 2 0
		 */
		//Expression
		public RuleCall getRightExpressionParserRuleCall_2_0() { return cRightExpressionParserRuleCall_2_0; }
	}
	
	/**
	 * The Class UnitRefElements.
	 */
	public class UnitRefElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.UnitRef");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c unit name action 0. */
		private final Action cUnitNameAction_0 = (Action)cGroup.eContents().get(0);
		
		/** The c ref assignment 1. */
		private final Assignment cRefAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c ref unit fake definition cross reference 1 0. */
		private final CrossReference cRefUnitFakeDefinitionCrossReference_1_0 = (CrossReference)cRefAssignment_1.eContents().get(0);
		
		/** The c ref unit fake definition ID terminal rule call 1 0 1. */
		private final RuleCall cRefUnitFakeDefinitionIDTerminalRuleCall_1_0_1 = (RuleCall)cRefUnitFakeDefinitionCrossReference_1_0.eContents().get(1);
		
		//UnitRef returns Expression:
		//    {UnitName} ref=[UnitFakeDefinition|ID];
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//{UnitName} ref=[UnitFakeDefinition|ID]
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the unit name action 0.
		 *
		 * @return the unit name action 0
		 */
		//{UnitName}
		public Action getUnitNameAction_0() { return cUnitNameAction_0; }
		
		/**
		 * Gets the ref assignment 1.
		 *
		 * @return the ref assignment 1
		 */
		//ref=[UnitFakeDefinition|ID]
		public Assignment getRefAssignment_1() { return cRefAssignment_1; }
		
		/**
		 * Gets the ref unit fake definition cross reference 1 0.
		 *
		 * @return the ref unit fake definition cross reference 1 0
		 */
		//[UnitFakeDefinition|ID]
		public CrossReference getRefUnitFakeDefinitionCrossReference_1_0() { return cRefUnitFakeDefinitionCrossReference_1_0; }
		
		/**
		 * Gets the ref unit fake definition ID terminal rule call 1 0 1.
		 *
		 * @return the ref unit fake definition ID terminal rule call 1 0 1
		 */
		//ID
		public RuleCall getRefUnitFakeDefinitionIDTerminalRuleCall_1_0_1() { return cRefUnitFakeDefinitionIDTerminalRuleCall_1_0_1; }
	}
	
	/**
	 * The Class VariableRefElements.
	 */
	public class VariableRefElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.VariableRef");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c variable ref action 0. */
		private final Action cVariableRefAction_0 = (Action)cGroup.eContents().get(0);
		
		/** The c ref assignment 1. */
		private final Assignment cRefAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c ref var definition cross reference 1 0. */
		private final CrossReference cRefVarDefinitionCrossReference_1_0 = (CrossReference)cRefAssignment_1.eContents().get(0);
		
		/** The c ref var definition valid ID parser rule call 1 0 1. */
		private final RuleCall cRefVarDefinitionValid_IDParserRuleCall_1_0_1 = (RuleCall)cRefVarDefinitionCrossReference_1_0.eContents().get(1);
		
		//VariableRef:
		//    {VariableRef} ref=[VarDefinition|Valid_ID];
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//{VariableRef} ref=[VarDefinition|Valid_ID]
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the variable ref action 0.
		 *
		 * @return the variable ref action 0
		 */
		//{VariableRef}
		public Action getVariableRefAction_0() { return cVariableRefAction_0; }
		
		/**
		 * Gets the ref assignment 1.
		 *
		 * @return the ref assignment 1
		 */
		//ref=[VarDefinition|Valid_ID]
		public Assignment getRefAssignment_1() { return cRefAssignment_1; }
		
		/**
		 * Gets the ref var definition cross reference 1 0.
		 *
		 * @return the ref var definition cross reference 1 0
		 */
		//[VarDefinition|Valid_ID]
		public CrossReference getRefVarDefinitionCrossReference_1_0() { return cRefVarDefinitionCrossReference_1_0; }
		
		/**
		 * Gets the ref var definition valid ID parser rule call 1 0 1.
		 *
		 * @return the ref var definition valid ID parser rule call 1 0 1
		 */
		//Valid_ID
		public RuleCall getRefVarDefinitionValid_IDParserRuleCall_1_0_1() { return cRefVarDefinitionValid_IDParserRuleCall_1_0_1; }
	}
	
	/**
	 * The Class TypeRefElements.
	 */
	public class TypeRefElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.TypeRef");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c group 0. */
		private final Group cGroup_0 = (Group)cAlternatives.eContents().get(0);
		
		/** The c type ref action 0 0. */
		private final Action cTypeRefAction_0_0 = (Action)cGroup_0.eContents().get(0);
		
		/** The c group 0 1. */
		private final Group cGroup_0_1 = (Group)cGroup_0.eContents().get(1);
		
		/** The c ref assignment 0 1 0. */
		private final Assignment cRefAssignment_0_1_0 = (Assignment)cGroup_0_1.eContents().get(0);
		
		/** The c ref type definition cross reference 0 1 0 0. */
		private final CrossReference cRefTypeDefinitionCrossReference_0_1_0_0 = (CrossReference)cRefAssignment_0_1_0.eContents().get(0);
		
		/** The c ref type definition ID terminal rule call 0 1 0 0 1. */
		private final RuleCall cRefTypeDefinitionIDTerminalRuleCall_0_1_0_0_1 = (RuleCall)cRefTypeDefinitionCrossReference_0_1_0_0.eContents().get(1);
		
		/** The c parameter assignment 0 1 1. */
		private final Assignment cParameterAssignment_0_1_1 = (Assignment)cGroup_0_1.eContents().get(1);
		
		/** The c parameter type info parser rule call 0 1 1 0. */
		private final RuleCall cParameterTypeInfoParserRuleCall_0_1_1_0 = (RuleCall)cParameterAssignment_0_1_1.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		
		/** The c type ref action 1 0. */
		private final Action cTypeRefAction_1_0 = (Action)cGroup_1.eContents().get(0);
		
		/** The c group 1 1. */
		private final Group cGroup_1_1 = (Group)cGroup_1.eContents().get(1);
		
		/** The c species keyword 1 1 0. */
		private final Keyword cSpeciesKeyword_1_1_0 = (Keyword)cGroup_1_1.eContents().get(0);
		
		/** The c parameter assignment 1 1 1. */
		private final Assignment cParameterAssignment_1_1_1 = (Assignment)cGroup_1_1.eContents().get(1);
		
		/** The c parameter type info parser rule call 1 1 1 0. */
		private final RuleCall cParameterTypeInfoParserRuleCall_1_1_1_0 = (RuleCall)cParameterAssignment_1_1_1.eContents().get(0);
		
		//TypeRef returns Expression:
		//     {TypeRef} (ref=[TypeDefinition|ID] parameter=TypeInfo?) | {TypeRef} ("species" parameter=TypeInfo) ;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//{TypeRef} (ref=[TypeDefinition|ID] parameter=TypeInfo?) | {TypeRef} ("species" parameter=TypeInfo)
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the group 0.
		 *
		 * @return the group 0
		 */
		//{TypeRef} (ref=[TypeDefinition|ID] parameter=TypeInfo?)
		public Group getGroup_0() { return cGroup_0; }
		
		/**
		 * Gets the type ref action 0 0.
		 *
		 * @return the type ref action 0 0
		 */
		//{TypeRef}
		public Action getTypeRefAction_0_0() { return cTypeRefAction_0_0; }
		
		/**
		 * Gets the group 0 1.
		 *
		 * @return the group 0 1
		 */
		//(ref=[TypeDefinition|ID] parameter=TypeInfo?)
		public Group getGroup_0_1() { return cGroup_0_1; }
		
		/**
		 * Gets the ref assignment 0 1 0.
		 *
		 * @return the ref assignment 0 1 0
		 */
		//ref=[TypeDefinition|ID]
		public Assignment getRefAssignment_0_1_0() { return cRefAssignment_0_1_0; }
		
		/**
		 * Gets the ref type definition cross reference 0 1 0 0.
		 *
		 * @return the ref type definition cross reference 0 1 0 0
		 */
		//[TypeDefinition|ID]
		public CrossReference getRefTypeDefinitionCrossReference_0_1_0_0() { return cRefTypeDefinitionCrossReference_0_1_0_0; }
		
		/**
		 * Gets the ref type definition ID terminal rule call 0 1 0 0 1.
		 *
		 * @return the ref type definition ID terminal rule call 0 1 0 0 1
		 */
		//ID
		public RuleCall getRefTypeDefinitionIDTerminalRuleCall_0_1_0_0_1() { return cRefTypeDefinitionIDTerminalRuleCall_0_1_0_0_1; }
		
		/**
		 * Gets the parameter assignment 0 1 1.
		 *
		 * @return the parameter assignment 0 1 1
		 */
		//parameter=TypeInfo?
		public Assignment getParameterAssignment_0_1_1() { return cParameterAssignment_0_1_1; }
		
		/**
		 * Gets the parameter type info parser rule call 0 1 1 0.
		 *
		 * @return the parameter type info parser rule call 0 1 1 0
		 */
		//TypeInfo
		public RuleCall getParameterTypeInfoParserRuleCall_0_1_1_0() { return cParameterTypeInfoParserRuleCall_0_1_1_0; }
		
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//{TypeRef} ("species" parameter=TypeInfo)
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the type ref action 1 0.
		 *
		 * @return the type ref action 1 0
		 */
		//{TypeRef}
		public Action getTypeRefAction_1_0() { return cTypeRefAction_1_0; }
		
		/**
		 * Gets the group 1 1.
		 *
		 * @return the group 1 1
		 */
		//("species" parameter=TypeInfo)
		public Group getGroup_1_1() { return cGroup_1_1; }
		
		/**
		 * Gets the species keyword 1 1 0.
		 *
		 * @return the species keyword 1 1 0
		 */
		//"species"
		public Keyword getSpeciesKeyword_1_1_0() { return cSpeciesKeyword_1_1_0; }
		
		/**
		 * Gets the parameter assignment 1 1 1.
		 *
		 * @return the parameter assignment 1 1 1
		 */
		//parameter=TypeInfo
		public Assignment getParameterAssignment_1_1_1() { return cParameterAssignment_1_1_1; }
		
		/**
		 * Gets the parameter type info parser rule call 1 1 1 0.
		 *
		 * @return the parameter type info parser rule call 1 1 1 0
		 */
		//TypeInfo
		public RuleCall getParameterTypeInfoParserRuleCall_1_1_1_0() { return cParameterTypeInfoParserRuleCall_1_1_1_0; }
	}
	
	/**
	 * The Class TypeInfoElements.
	 */
	public class TypeInfoElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.TypeInfo");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c less than sign keyword 0. */
		private final Keyword cLessThanSignKeyword_0 = (Keyword)cGroup.eContents().get(0);
		
		/** The c first assignment 1. */
		private final Assignment cFirstAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c first type ref parser rule call 1 0. */
		private final RuleCall cFirstTypeRefParserRuleCall_1_0 = (RuleCall)cFirstAssignment_1.eContents().get(0);
		
		/** The c group 2. */
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		
		/** The c comma keyword 2 0. */
		private final Keyword cCommaKeyword_2_0 = (Keyword)cGroup_2.eContents().get(0);
		
		/** The c second assignment 2 1. */
		private final Assignment cSecondAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		
		/** The c second type ref parser rule call 2 1 0. */
		private final RuleCall cSecondTypeRefParserRuleCall_2_1_0 = (RuleCall)cSecondAssignment_2_1.eContents().get(0);
		
		/** The c greater than sign keyword 3. */
		private final Keyword cGreaterThanSignKeyword_3 = (Keyword)cGroup.eContents().get(3);
		
		//TypeInfo:
		//    ('<' first=TypeRef ("," second=TypeRef)? ->'>')
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//('<' first=TypeRef ("," second=TypeRef)? ->'>')
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the less than sign keyword 0.
		 *
		 * @return the less than sign keyword 0
		 */
		//'<'
		public Keyword getLessThanSignKeyword_0() { return cLessThanSignKeyword_0; }
		
		/**
		 * Gets the first assignment 1.
		 *
		 * @return the first assignment 1
		 */
		//first=TypeRef
		public Assignment getFirstAssignment_1() { return cFirstAssignment_1; }
		
		/**
		 * Gets the first type ref parser rule call 1 0.
		 *
		 * @return the first type ref parser rule call 1 0
		 */
		//TypeRef
		public RuleCall getFirstTypeRefParserRuleCall_1_0() { return cFirstTypeRefParserRuleCall_1_0; }
		
		/**
		 * Gets the group 2.
		 *
		 * @return the group 2
		 */
		//("," second=TypeRef)?
		public Group getGroup_2() { return cGroup_2; }
		
		/**
		 * Gets the comma keyword 2 0.
		 *
		 * @return the comma keyword 2 0
		 */
		//","
		public Keyword getCommaKeyword_2_0() { return cCommaKeyword_2_0; }
		
		/**
		 * Gets the second assignment 2 1.
		 *
		 * @return the second assignment 2 1
		 */
		//second=TypeRef
		public Assignment getSecondAssignment_2_1() { return cSecondAssignment_2_1; }
		
		/**
		 * Gets the second type ref parser rule call 2 1 0.
		 *
		 * @return the second type ref parser rule call 2 1 0
		 */
		//TypeRef
		public RuleCall getSecondTypeRefParserRuleCall_2_1_0() { return cSecondTypeRefParserRuleCall_2_1_0; }
		
		/**
		 * Gets the greater than sign keyword 3.
		 *
		 * @return the greater than sign keyword 3
		 */
		//->'>'
		public Keyword getGreaterThanSignKeyword_3() { return cGreaterThanSignKeyword_3; }
	}
	
	/**
	 * The Class SkillRefElements.
	 */
	public class SkillRefElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.SkillRef");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c skill ref action 0. */
		private final Action cSkillRefAction_0 = (Action)cGroup.eContents().get(0);
		
		/** The c ref assignment 1. */
		private final Assignment cRefAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c ref skill fake definition cross reference 1 0. */
		private final CrossReference cRefSkillFakeDefinitionCrossReference_1_0 = (CrossReference)cRefAssignment_1.eContents().get(0);
		
		/** The c ref skill fake definition ID terminal rule call 1 0 1. */
		private final RuleCall cRefSkillFakeDefinitionIDTerminalRuleCall_1_0_1 = (RuleCall)cRefSkillFakeDefinitionCrossReference_1_0.eContents().get(1);
		
		//SkillRef returns Expression:
		//    {SkillRef} ref=[SkillFakeDefinition|ID];
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//{SkillRef} ref=[SkillFakeDefinition|ID]
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the skill ref action 0.
		 *
		 * @return the skill ref action 0
		 */
		//{SkillRef}
		public Action getSkillRefAction_0() { return cSkillRefAction_0; }
		
		/**
		 * Gets the ref assignment 1.
		 *
		 * @return the ref assignment 1
		 */
		//ref=[SkillFakeDefinition|ID]
		public Assignment getRefAssignment_1() { return cRefAssignment_1; }
		
		/**
		 * Gets the ref skill fake definition cross reference 1 0.
		 *
		 * @return the ref skill fake definition cross reference 1 0
		 */
		//[SkillFakeDefinition|ID]
		public CrossReference getRefSkillFakeDefinitionCrossReference_1_0() { return cRefSkillFakeDefinitionCrossReference_1_0; }
		
		/**
		 * Gets the ref skill fake definition ID terminal rule call 1 0 1.
		 *
		 * @return the ref skill fake definition ID terminal rule call 1 0 1
		 */
		//ID
		public RuleCall getRefSkillFakeDefinitionIDTerminalRuleCall_1_0_1() { return cRefSkillFakeDefinitionIDTerminalRuleCall_1_0_1; }
	}
	
	/**
	 * The Class ActionRefElements.
	 */
	public class ActionRefElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ActionRef");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c action ref action 0. */
		private final Action cActionRefAction_0 = (Action)cGroup.eContents().get(0);
		
		/** The c ref assignment 1. */
		private final Assignment cRefAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c ref action definition cross reference 1 0. */
		private final CrossReference cRefActionDefinitionCrossReference_1_0 = (CrossReference)cRefAssignment_1.eContents().get(0);
		
		/** The c ref action definition valid ID parser rule call 1 0 1. */
		private final RuleCall cRefActionDefinitionValid_IDParserRuleCall_1_0_1 = (RuleCall)cRefActionDefinitionCrossReference_1_0.eContents().get(1);
		
		//ActionRef returns Expression:
		//    {ActionRef} ref=[ActionDefinition|Valid_ID];
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//{ActionRef} ref=[ActionDefinition|Valid_ID]
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the action ref action 0.
		 *
		 * @return the action ref action 0
		 */
		//{ActionRef}
		public Action getActionRefAction_0() { return cActionRefAction_0; }
		
		/**
		 * Gets the ref assignment 1.
		 *
		 * @return the ref assignment 1
		 */
		//ref=[ActionDefinition|Valid_ID]
		public Assignment getRefAssignment_1() { return cRefAssignment_1; }
		
		/**
		 * Gets the ref action definition cross reference 1 0.
		 *
		 * @return the ref action definition cross reference 1 0
		 */
		//[ActionDefinition|Valid_ID]
		public CrossReference getRefActionDefinitionCrossReference_1_0() { return cRefActionDefinitionCrossReference_1_0; }
		
		/**
		 * Gets the ref action definition valid ID parser rule call 1 0 1.
		 *
		 * @return the ref action definition valid ID parser rule call 1 0 1
		 */
		//Valid_ID
		public RuleCall getRefActionDefinitionValid_IDParserRuleCall_1_0_1() { return cRefActionDefinitionValid_IDParserRuleCall_1_0_1; }
	}
	
	/**
	 * The Class EquationRefElements.
	 */
	public class EquationRefElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.EquationRef");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c equation ref action 0. */
		private final Action cEquationRefAction_0 = (Action)cGroup.eContents().get(0);
		
		/** The c ref assignment 1. */
		private final Assignment cRefAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c ref equation definition cross reference 1 0. */
		private final CrossReference cRefEquationDefinitionCrossReference_1_0 = (CrossReference)cRefAssignment_1.eContents().get(0);
		
		/** The c ref equation definition valid ID parser rule call 1 0 1. */
		private final RuleCall cRefEquationDefinitionValid_IDParserRuleCall_1_0_1 = (RuleCall)cRefEquationDefinitionCrossReference_1_0.eContents().get(1);
		
		//EquationRef returns Expression:
		//    {EquationRef} ref=[EquationDefinition|Valid_ID];
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//{EquationRef} ref=[EquationDefinition|Valid_ID]
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the equation ref action 0.
		 *
		 * @return the equation ref action 0
		 */
		//{EquationRef}
		public Action getEquationRefAction_0() { return cEquationRefAction_0; }
		
		/**
		 * Gets the ref assignment 1.
		 *
		 * @return the ref assignment 1
		 */
		//ref=[EquationDefinition|Valid_ID]
		public Assignment getRefAssignment_1() { return cRefAssignment_1; }
		
		/**
		 * Gets the ref equation definition cross reference 1 0.
		 *
		 * @return the ref equation definition cross reference 1 0
		 */
		//[EquationDefinition|Valid_ID]
		public CrossReference getRefEquationDefinitionCrossReference_1_0() { return cRefEquationDefinitionCrossReference_1_0; }
		
		/**
		 * Gets the ref equation definition valid ID parser rule call 1 0 1.
		 *
		 * @return the ref equation definition valid ID parser rule call 1 0 1
		 */
		//Valid_ID
		public RuleCall getRefEquationDefinitionValid_IDParserRuleCall_1_0_1() { return cRefEquationDefinitionValid_IDParserRuleCall_1_0_1; }
	}
	
	/**
	 * The Class GamlDefinitionElements.
	 */
	public class GamlDefinitionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.GamlDefinition");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c type definition parser rule call 0. */
		private final RuleCall cTypeDefinitionParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c var definition parser rule call 1. */
		private final RuleCall cVarDefinitionParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		/** The c unit fake definition parser rule call 2. */
		private final RuleCall cUnitFakeDefinitionParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		/** The c skill fake definition parser rule call 3. */
		private final RuleCall cSkillFakeDefinitionParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		
		/** The c action definition parser rule call 4. */
		private final RuleCall cActionDefinitionParserRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		
		/** The c equation definition parser rule call 5. */
		private final RuleCall cEquationDefinitionParserRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		
		//GamlDefinition:
		//    TypeDefinition | VarDefinition | UnitFakeDefinition | SkillFakeDefinition | ActionDefinition | EquationDefinition;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//TypeDefinition | VarDefinition | UnitFakeDefinition | SkillFakeDefinition | ActionDefinition | EquationDefinition
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the type definition parser rule call 0.
		 *
		 * @return the type definition parser rule call 0
		 */
		//TypeDefinition
		public RuleCall getTypeDefinitionParserRuleCall_0() { return cTypeDefinitionParserRuleCall_0; }
		
		/**
		 * Gets the var definition parser rule call 1.
		 *
		 * @return the var definition parser rule call 1
		 */
		//VarDefinition
		public RuleCall getVarDefinitionParserRuleCall_1() { return cVarDefinitionParserRuleCall_1; }
		
		/**
		 * Gets the unit fake definition parser rule call 2.
		 *
		 * @return the unit fake definition parser rule call 2
		 */
		//UnitFakeDefinition
		public RuleCall getUnitFakeDefinitionParserRuleCall_2() { return cUnitFakeDefinitionParserRuleCall_2; }
		
		/**
		 * Gets the skill fake definition parser rule call 3.
		 *
		 * @return the skill fake definition parser rule call 3
		 */
		//SkillFakeDefinition
		public RuleCall getSkillFakeDefinitionParserRuleCall_3() { return cSkillFakeDefinitionParserRuleCall_3; }
		
		/**
		 * Gets the action definition parser rule call 4.
		 *
		 * @return the action definition parser rule call 4
		 */
		//ActionDefinition
		public RuleCall getActionDefinitionParserRuleCall_4() { return cActionDefinitionParserRuleCall_4; }
		
		/**
		 * Gets the equation definition parser rule call 5.
		 *
		 * @return the equation definition parser rule call 5
		 */
		//EquationDefinition
		public RuleCall getEquationDefinitionParserRuleCall_5() { return cEquationDefinitionParserRuleCall_5; }
	}
	
	/**
	 * The Class EquationDefinitionElements.
	 */
	public class EquationDefinitionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.EquationDefinition");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c S equations parser rule call 0. */
		private final RuleCall cS_EquationsParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c equation fake definition parser rule call 1. */
		private final RuleCall cEquationFakeDefinitionParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//EquationDefinition:
		//    S_Equations | EquationFakeDefinition;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//S_Equations | EquationFakeDefinition
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the s equations parser rule call 0.
		 *
		 * @return the s equations parser rule call 0
		 */
		//S_Equations
		public RuleCall getS_EquationsParserRuleCall_0() { return cS_EquationsParserRuleCall_0; }
		
		/**
		 * Gets the equation fake definition parser rule call 1.
		 *
		 * @return the equation fake definition parser rule call 1
		 */
		//EquationFakeDefinition
		public RuleCall getEquationFakeDefinitionParserRuleCall_1() { return cEquationFakeDefinitionParserRuleCall_1; }
	}
	
	/**
	 * The Class TypeDefinitionElements.
	 */
	public class TypeDefinitionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.TypeDefinition");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c S species parser rule call 0. */
		private final RuleCall cS_SpeciesParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c type fake definition parser rule call 1. */
		private final RuleCall cTypeFakeDefinitionParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//TypeDefinition:
		//    S_Species | TypeFakeDefinition;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//S_Species | TypeFakeDefinition
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the s species parser rule call 0.
		 *
		 * @return the s species parser rule call 0
		 */
		//S_Species
		public RuleCall getS_SpeciesParserRuleCall_0() { return cS_SpeciesParserRuleCall_0; }
		
		/**
		 * Gets the type fake definition parser rule call 1.
		 *
		 * @return the type fake definition parser rule call 1
		 */
		//TypeFakeDefinition
		public RuleCall getTypeFakeDefinitionParserRuleCall_1() { return cTypeFakeDefinitionParserRuleCall_1; }
	}
	
	/**
	 * The Class VarDefinitionElements.
	 */
	public class VarDefinitionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.VarDefinition");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c S declaration parser rule call 0. */
		private final RuleCall cS_DeclarationParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c alternatives 1. */
		private final Alternatives cAlternatives_1 = (Alternatives)cAlternatives.eContents().get(1);
		
		/** The c model parser rule call 1 0. */
		private final RuleCall cModelParserRuleCall_1_0 = (RuleCall)cAlternatives_1.eContents().get(0);
		
		/** The c argument definition parser rule call 1 1. */
		private final RuleCall cArgumentDefinitionParserRuleCall_1_1 = (RuleCall)cAlternatives_1.eContents().get(1);
		
		/** The c definition facet parser rule call 1 2. */
		private final RuleCall cDefinitionFacetParserRuleCall_1_2 = (RuleCall)cAlternatives_1.eContents().get(2);
		
		/** The c var fake definition parser rule call 1 3. */
		private final RuleCall cVarFakeDefinitionParserRuleCall_1_3 = (RuleCall)cAlternatives_1.eContents().get(3);
		
		/** The c import parser rule call 1 4. */
		private final RuleCall cImportParserRuleCall_1_4 = (RuleCall)cAlternatives_1.eContents().get(4);
		
		/** The c S experiment parser rule call 1 5. */
		private final RuleCall cS_ExperimentParserRuleCall_1_5 = (RuleCall)cAlternatives_1.eContents().get(5);
		
		//VarDefinition:
		//    =>S_Declaration | (Model | ArgumentDefinition | DefinitionFacet | VarFakeDefinition | Import | S_Experiment);
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//=>S_Declaration | (Model | ArgumentDefinition | DefinitionFacet | VarFakeDefinition | Import | S_Experiment)
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the s declaration parser rule call 0.
		 *
		 * @return the s declaration parser rule call 0
		 */
		//=>S_Declaration
		public RuleCall getS_DeclarationParserRuleCall_0() { return cS_DeclarationParserRuleCall_0; }
		
		/**
		 * Gets the alternatives 1.
		 *
		 * @return the alternatives 1
		 */
		//(Model | ArgumentDefinition | DefinitionFacet | VarFakeDefinition | Import | S_Experiment)
		public Alternatives getAlternatives_1() { return cAlternatives_1; }
		
		/**
		 * Gets the model parser rule call 1 0.
		 *
		 * @return the model parser rule call 1 0
		 */
		//Model
		public RuleCall getModelParserRuleCall_1_0() { return cModelParserRuleCall_1_0; }
		
		/**
		 * Gets the argument definition parser rule call 1 1.
		 *
		 * @return the argument definition parser rule call 1 1
		 */
		//ArgumentDefinition
		public RuleCall getArgumentDefinitionParserRuleCall_1_1() { return cArgumentDefinitionParserRuleCall_1_1; }
		
		/**
		 * Gets the definition facet parser rule call 1 2.
		 *
		 * @return the definition facet parser rule call 1 2
		 */
		//DefinitionFacet
		public RuleCall getDefinitionFacetParserRuleCall_1_2() { return cDefinitionFacetParserRuleCall_1_2; }
		
		/**
		 * Gets the var fake definition parser rule call 1 3.
		 *
		 * @return the var fake definition parser rule call 1 3
		 */
		//VarFakeDefinition
		public RuleCall getVarFakeDefinitionParserRuleCall_1_3() { return cVarFakeDefinitionParserRuleCall_1_3; }
		
		/**
		 * Gets the import parser rule call 1 4.
		 *
		 * @return the import parser rule call 1 4
		 */
		//Import
		public RuleCall getImportParserRuleCall_1_4() { return cImportParserRuleCall_1_4; }
		
		/**
		 * Gets the s experiment parser rule call 1 5.
		 *
		 * @return the s experiment parser rule call 1 5
		 */
		//S_Experiment
		public RuleCall getS_ExperimentParserRuleCall_1_5() { return cS_ExperimentParserRuleCall_1_5; }
	}
	
	/**
	 * The Class ActionDefinitionElements.
	 */
	public class ActionDefinitionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ActionDefinition");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c S action parser rule call 0. */
		private final RuleCall cS_ActionParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c action fake definition parser rule call 1. */
		private final RuleCall cActionFakeDefinitionParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		/** The c S definition parser rule call 2. */
		private final RuleCall cS_DefinitionParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		/** The c type definition parser rule call 3. */
		private final RuleCall cTypeDefinitionParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		
		//ActionDefinition:
		//    S_Action | ActionFakeDefinition | S_Definition | TypeDefinition;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//S_Action | ActionFakeDefinition | S_Definition | TypeDefinition
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the s action parser rule call 0.
		 *
		 * @return the s action parser rule call 0
		 */
		//S_Action
		public RuleCall getS_ActionParserRuleCall_0() { return cS_ActionParserRuleCall_0; }
		
		/**
		 * Gets the action fake definition parser rule call 1.
		 *
		 * @return the action fake definition parser rule call 1
		 */
		//ActionFakeDefinition
		public RuleCall getActionFakeDefinitionParserRuleCall_1() { return cActionFakeDefinitionParserRuleCall_1; }
		
		/**
		 * Gets the s definition parser rule call 2.
		 *
		 * @return the s definition parser rule call 2
		 */
		//S_Definition
		public RuleCall getS_DefinitionParserRuleCall_2() { return cS_DefinitionParserRuleCall_2; }
		
		/**
		 * Gets the type definition parser rule call 3.
		 *
		 * @return the type definition parser rule call 3
		 */
		//TypeDefinition
		public RuleCall getTypeDefinitionParserRuleCall_3() { return cTypeDefinitionParserRuleCall_3; }
	}
	
	/**
	 * The Class UnitFakeDefinitionElements.
	 */
	public class UnitFakeDefinitionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.UnitFakeDefinition");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c unit keyword 0. */
		private final Keyword cUnitKeyword_0 = (Keyword)cGroup.eContents().get(0);
		
		/** The c name assignment 1. */
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c name ID terminal rule call 1 0. */
		private final RuleCall cNameIDTerminalRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		
		//    // Fake Definitions produced by the global scope provider
		//UnitFakeDefinition:
		//    '**unit*' name=ID;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//'**unit*' name=ID
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the unit keyword 0.
		 *
		 * @return the unit keyword 0
		 */
		//'**unit*'
		public Keyword getUnitKeyword_0() { return cUnitKeyword_0; }
		
		/**
		 * Gets the name assignment 1.
		 *
		 * @return the name assignment 1
		 */
		//name=ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		/**
		 * Gets the name ID terminal rule call 1 0.
		 *
		 * @return the name ID terminal rule call 1 0
		 */
		//ID
		public RuleCall getNameIDTerminalRuleCall_1_0() { return cNameIDTerminalRuleCall_1_0; }
	}
	
	/**
	 * The Class TypeFakeDefinitionElements.
	 */
	public class TypeFakeDefinitionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.TypeFakeDefinition");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c type keyword 0. */
		private final Keyword cTypeKeyword_0 = (Keyword)cGroup.eContents().get(0);
		
		/** The c name assignment 1. */
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c name ID terminal rule call 1 0. */
		private final RuleCall cNameIDTerminalRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		
		//TypeFakeDefinition:
		//    '**type*' name=ID;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//'**type*' name=ID
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the type keyword 0.
		 *
		 * @return the type keyword 0
		 */
		//'**type*'
		public Keyword getTypeKeyword_0() { return cTypeKeyword_0; }
		
		/**
		 * Gets the name assignment 1.
		 *
		 * @return the name assignment 1
		 */
		//name=ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		/**
		 * Gets the name ID terminal rule call 1 0.
		 *
		 * @return the name ID terminal rule call 1 0
		 */
		//ID
		public RuleCall getNameIDTerminalRuleCall_1_0() { return cNameIDTerminalRuleCall_1_0; }
	}
	
	/**
	 * The Class ActionFakeDefinitionElements.
	 */
	public class ActionFakeDefinitionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ActionFakeDefinition");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c action keyword 0. */
		private final Keyword cActionKeyword_0 = (Keyword)cGroup.eContents().get(0);
		
		/** The c name assignment 1. */
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c name valid ID parser rule call 1 0. */
		private final RuleCall cNameValid_IDParserRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		
		//ActionFakeDefinition:
		//    '**action*' name=Valid_ID;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//'**action*' name=Valid_ID
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the action keyword 0.
		 *
		 * @return the action keyword 0
		 */
		//'**action*'
		public Keyword getActionKeyword_0() { return cActionKeyword_0; }
		
		/**
		 * Gets the name assignment 1.
		 *
		 * @return the name assignment 1
		 */
		//name=Valid_ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		/**
		 * Gets the name valid ID parser rule call 1 0.
		 *
		 * @return the name valid ID parser rule call 1 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_1_0() { return cNameValid_IDParserRuleCall_1_0; }
	}
	
	/**
	 * The Class SkillFakeDefinitionElements.
	 */
	public class SkillFakeDefinitionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.SkillFakeDefinition");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c skill keyword 0. */
		private final Keyword cSkillKeyword_0 = (Keyword)cGroup.eContents().get(0);
		
		/** The c name assignment 1. */
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c name ID terminal rule call 1 0. */
		private final RuleCall cNameIDTerminalRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		
		//SkillFakeDefinition:
		//    '**skill*' name=ID;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//'**skill*' name=ID
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the skill keyword 0.
		 *
		 * @return the skill keyword 0
		 */
		//'**skill*'
		public Keyword getSkillKeyword_0() { return cSkillKeyword_0; }
		
		/**
		 * Gets the name assignment 1.
		 *
		 * @return the name assignment 1
		 */
		//name=ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		/**
		 * Gets the name ID terminal rule call 1 0.
		 *
		 * @return the name ID terminal rule call 1 0
		 */
		//ID
		public RuleCall getNameIDTerminalRuleCall_1_0() { return cNameIDTerminalRuleCall_1_0; }
	}
	
	/**
	 * The Class VarFakeDefinitionElements.
	 */
	public class VarFakeDefinitionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.VarFakeDefinition");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c var keyword 0. */
		private final Keyword cVarKeyword_0 = (Keyword)cGroup.eContents().get(0);
		
		/** The c name assignment 1. */
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c name valid ID parser rule call 1 0. */
		private final RuleCall cNameValid_IDParserRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		
		//VarFakeDefinition:
		//    '**var*' name=Valid_ID;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//'**var*' name=Valid_ID
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the var keyword 0.
		 *
		 * @return the var keyword 0
		 */
		//'**var*'
		public Keyword getVarKeyword_0() { return cVarKeyword_0; }
		
		/**
		 * Gets the name assignment 1.
		 *
		 * @return the name assignment 1
		 */
		//name=Valid_ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		/**
		 * Gets the name valid ID parser rule call 1 0.
		 *
		 * @return the name valid ID parser rule call 1 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_1_0() { return cNameValid_IDParserRuleCall_1_0; }
	}
	
	/**
	 * The Class EquationFakeDefinitionElements.
	 */
	public class EquationFakeDefinitionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.EquationFakeDefinition");
		
		/** The c group. */
		private final Group cGroup = (Group)rule.eContents().get(1);
		
		/** The c equation keyword 0. */
		private final Keyword cEquationKeyword_0 = (Keyword)cGroup.eContents().get(0);
		
		/** The c name assignment 1. */
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		
		/** The c name valid ID parser rule call 1 0. */
		private final RuleCall cNameValid_IDParserRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		
		//EquationFakeDefinition:
		//    '**equation*' name=Valid_ID;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the group.
		 *
		 * @return the group
		 */
		//'**equation*' name=Valid_ID
		public Group getGroup() { return cGroup; }
		
		/**
		 * Gets the equation keyword 0.
		 *
		 * @return the equation keyword 0
		 */
		//'**equation*'
		public Keyword getEquationKeyword_0() { return cEquationKeyword_0; }
		
		/**
		 * Gets the name assignment 1.
		 *
		 * @return the name assignment 1
		 */
		//name=Valid_ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }
		
		/**
		 * Gets the name valid ID parser rule call 1 0.
		 *
		 * @return the name valid ID parser rule call 1 0
		 */
		//Valid_ID
		public RuleCall getNameValid_IDParserRuleCall_1_0() { return cNameValid_IDParserRuleCall_1_0; }
	}
	
	/**
	 * The Class Valid_IDElements.
	 */
	public class Valid_IDElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.Valid_ID");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c species key parser rule call 0. */
		private final RuleCall c_SpeciesKeyParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c do key parser rule call 1. */
		private final RuleCall c_DoKeyParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		/** The c reflex key parser rule call 2. */
		private final RuleCall c_ReflexKeyParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		/** The c var or const key parser rule call 3. */
		private final RuleCall c_VarOrConstKeyParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		
		/** The c 1 expr facets block or end key parser rule call 4. */
		private final RuleCall c_1Expr_Facets_BlockOrEnd_KeyParserRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		
		/** The c equations key parser rule call 5. */
		private final RuleCall c_EquationsKeyParserRuleCall_5 = (RuleCall)cAlternatives.eContents().get(5);
		
		/** The c ID terminal rule call 6. */
		private final RuleCall cIDTerminalRuleCall_6 = (RuleCall)cAlternatives.eContents().get(6);
		
		/** The c experiment key parser rule call 7. */
		private final RuleCall c_ExperimentKeyParserRuleCall_7 = (RuleCall)cAlternatives.eContents().get(7);
		
		//Valid_ID:
		//    _SpeciesKey | _DoKey | _ReflexKey | _VarOrConstKey | _1Expr_Facets_BlockOrEnd_Key | _EquationsKey | ID | _ExperimentKey;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//_SpeciesKey | _DoKey | _ReflexKey | _VarOrConstKey | _1Expr_Facets_BlockOrEnd_Key | _EquationsKey | ID | _ExperimentKey
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the species key parser rule call 0.
		 *
		 * @return the species key parser rule call 0
		 */
		//_SpeciesKey
		public RuleCall get_SpeciesKeyParserRuleCall_0() { return c_SpeciesKeyParserRuleCall_0; }
		
		/**
		 * Gets the do key parser rule call 1.
		 *
		 * @return the do key parser rule call 1
		 */
		//_DoKey
		public RuleCall get_DoKeyParserRuleCall_1() { return c_DoKeyParserRuleCall_1; }
		
		/**
		 * Gets the reflex key parser rule call 2.
		 *
		 * @return the reflex key parser rule call 2
		 */
		//_ReflexKey
		public RuleCall get_ReflexKeyParserRuleCall_2() { return c_ReflexKeyParserRuleCall_2; }
		
		/**
		 * Gets the var or const key parser rule call 3.
		 *
		 * @return the var or const key parser rule call 3
		 */
		//_VarOrConstKey
		public RuleCall get_VarOrConstKeyParserRuleCall_3() { return c_VarOrConstKeyParserRuleCall_3; }
		
		/**
		 * Gets the 1 expr facets block or end key parser rule call 4.
		 *
		 * @return the 1 expr facets block or end key parser rule call 4
		 */
		//_1Expr_Facets_BlockOrEnd_Key
		public RuleCall get_1Expr_Facets_BlockOrEnd_KeyParserRuleCall_4() { return c_1Expr_Facets_BlockOrEnd_KeyParserRuleCall_4; }
		
		/**
		 * Gets the equations key parser rule call 5.
		 *
		 * @return the equations key parser rule call 5
		 */
		//_EquationsKey
		public RuleCall get_EquationsKeyParserRuleCall_5() { return c_EquationsKeyParserRuleCall_5; }
		
		/**
		 * Gets the ID terminal rule call 6.
		 *
		 * @return the ID terminal rule call 6
		 */
		//ID
		public RuleCall getIDTerminalRuleCall_6() { return cIDTerminalRuleCall_6; }
		
		/**
		 * Gets the experiment key parser rule call 7.
		 *
		 * @return the experiment key parser rule call 7
		 */
		//_ExperimentKey
		public RuleCall get_ExperimentKeyParserRuleCall_7() { return c_ExperimentKeyParserRuleCall_7; }
	}
	
	/**
	 * The Class TerminalExpressionElements.
	 */
	public class TerminalExpressionElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.TerminalExpression");
		
		/** The c alternatives. */
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		
		/** The c string literal parser rule call 0. */
		private final RuleCall cStringLiteralParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		
		/** The c group 1. */
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		
		/** The c int literal action 1 0. */
		private final Action cIntLiteralAction_1_0 = (Action)cGroup_1.eContents().get(0);
		
		/** The c op assignment 1 1. */
		private final Assignment cOpAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		
		/** The c op INTEGER terminal rule call 1 1 0. */
		private final RuleCall cOpINTEGERTerminalRuleCall_1_1_0 = (RuleCall)cOpAssignment_1_1.eContents().get(0);
		
		/** The c group 2. */
		private final Group cGroup_2 = (Group)cAlternatives.eContents().get(2);
		
		/** The c double literal action 2 0. */
		private final Action cDoubleLiteralAction_2_0 = (Action)cGroup_2.eContents().get(0);
		
		/** The c op assignment 2 1. */
		private final Assignment cOpAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		
		/** The c op DOUBLE terminal rule call 2 1 0. */
		private final RuleCall cOpDOUBLETerminalRuleCall_2_1_0 = (RuleCall)cOpAssignment_2_1.eContents().get(0);
		
		/** The c group 3. */
		private final Group cGroup_3 = (Group)cAlternatives.eContents().get(3);
		
		/** The c boolean literal action 3 0. */
		private final Action cBooleanLiteralAction_3_0 = (Action)cGroup_3.eContents().get(0);
		
		/** The c op assignment 3 1. */
		private final Assignment cOpAssignment_3_1 = (Assignment)cGroup_3.eContents().get(1);
		
		/** The c op BOOLEAN terminal rule call 3 1 0. */
		private final RuleCall cOpBOOLEANTerminalRuleCall_3_1_0 = (RuleCall)cOpAssignment_3_1.eContents().get(0);
		
		/** The c group 4. */
		private final Group cGroup_4 = (Group)cAlternatives.eContents().get(4);
		
		/** The c reserved literal action 4 0. */
		private final Action cReservedLiteralAction_4_0 = (Action)cGroup_4.eContents().get(0);
		
		/** The c op assignment 4 1. */
		private final Assignment cOpAssignment_4_1 = (Assignment)cGroup_4.eContents().get(1);
		
		/** The c op KEYWORD terminal rule call 4 1 0. */
		private final RuleCall cOpKEYWORDTerminalRuleCall_4_1_0 = (RuleCall)cOpAssignment_4_1.eContents().get(0);
		
		//    /**
		// * Terminals
		// */
		//TerminalExpression:
		//    StringLiteral |
		//    {IntLiteral} op=INTEGER |
		//    {DoubleLiteral} op=DOUBLE |
		//    /*{ColorLiteral} op=COLOR |*/
		//    {BooleanLiteral} op=BOOLEAN |
		//    {ReservedLiteral} op=KEYWORD;
		@Override public ParserRule getRule() { return rule; }
		
		//StringLiteral |
		//{IntLiteral} op=INTEGER |
		//{DoubleLiteral} op=DOUBLE |
		///*{ColorLiteral} op=COLOR |*/
		//{BooleanLiteral} op=BOOLEAN |
		/**
		 * Gets the alternatives.
		 *
		 * @return the alternatives
		 */
		//{ReservedLiteral} op=KEYWORD
		public Alternatives getAlternatives() { return cAlternatives; }
		
		/**
		 * Gets the string literal parser rule call 0.
		 *
		 * @return the string literal parser rule call 0
		 */
		//StringLiteral
		public RuleCall getStringLiteralParserRuleCall_0() { return cStringLiteralParserRuleCall_0; }
		
		/**
		 * Gets the group 1.
		 *
		 * @return the group 1
		 */
		//{IntLiteral} op=INTEGER
		public Group getGroup_1() { return cGroup_1; }
		
		/**
		 * Gets the int literal action 1 0.
		 *
		 * @return the int literal action 1 0
		 */
		//{IntLiteral}
		public Action getIntLiteralAction_1_0() { return cIntLiteralAction_1_0; }
		
		/**
		 * Gets the op assignment 1 1.
		 *
		 * @return the op assignment 1 1
		 */
		//op=INTEGER
		public Assignment getOpAssignment_1_1() { return cOpAssignment_1_1; }
		
		/**
		 * Gets the op INTEGER terminal rule call 1 1 0.
		 *
		 * @return the op INTEGER terminal rule call 1 1 0
		 */
		//INTEGER
		public RuleCall getOpINTEGERTerminalRuleCall_1_1_0() { return cOpINTEGERTerminalRuleCall_1_1_0; }
		
		/**
		 * Gets the group 2.
		 *
		 * @return the group 2
		 */
		//{DoubleLiteral} op=DOUBLE
		public Group getGroup_2() { return cGroup_2; }
		
		/**
		 * Gets the double literal action 2 0.
		 *
		 * @return the double literal action 2 0
		 */
		//{DoubleLiteral}
		public Action getDoubleLiteralAction_2_0() { return cDoubleLiteralAction_2_0; }
		
		/**
		 * Gets the op assignment 2 1.
		 *
		 * @return the op assignment 2 1
		 */
		//op=DOUBLE
		public Assignment getOpAssignment_2_1() { return cOpAssignment_2_1; }
		
		/**
		 * Gets the op DOUBLE terminal rule call 2 1 0.
		 *
		 * @return the op DOUBLE terminal rule call 2 1 0
		 */
		//DOUBLE
		public RuleCall getOpDOUBLETerminalRuleCall_2_1_0() { return cOpDOUBLETerminalRuleCall_2_1_0; }
		
		///*{ColorLiteral} op=COLOR |*/
		/**
		 * Gets the group 3.
		 *
		 * @return the group 3
		 */
		//{BooleanLiteral} op=BOOLEAN
		public Group getGroup_3() { return cGroup_3; }
		
		///*{ColorLiteral} op=COLOR |*/
		/**
		 * Gets the boolean literal action 3 0.
		 *
		 * @return the boolean literal action 3 0
		 */
		//{BooleanLiteral}
		public Action getBooleanLiteralAction_3_0() { return cBooleanLiteralAction_3_0; }
		
		/**
		 * Gets the op assignment 3 1.
		 *
		 * @return the op assignment 3 1
		 */
		//op=BOOLEAN
		public Assignment getOpAssignment_3_1() { return cOpAssignment_3_1; }
		
		/**
		 * Gets the op BOOLEAN terminal rule call 3 1 0.
		 *
		 * @return the op BOOLEAN terminal rule call 3 1 0
		 */
		//BOOLEAN
		public RuleCall getOpBOOLEANTerminalRuleCall_3_1_0() { return cOpBOOLEANTerminalRuleCall_3_1_0; }
		
		/**
		 * Gets the group 4.
		 *
		 * @return the group 4
		 */
		//{ReservedLiteral} op=KEYWORD
		public Group getGroup_4() { return cGroup_4; }
		
		/**
		 * Gets the reserved literal action 4 0.
		 *
		 * @return the reserved literal action 4 0
		 */
		//{ReservedLiteral}
		public Action getReservedLiteralAction_4_0() { return cReservedLiteralAction_4_0; }
		
		/**
		 * Gets the op assignment 4 1.
		 *
		 * @return the op assignment 4 1
		 */
		//op=KEYWORD
		public Assignment getOpAssignment_4_1() { return cOpAssignment_4_1; }
		
		/**
		 * Gets the op KEYWORD terminal rule call 4 1 0.
		 *
		 * @return the op KEYWORD terminal rule call 4 1 0
		 */
		//KEYWORD
		public RuleCall getOpKEYWORDTerminalRuleCall_4_1_0() { return cOpKEYWORDTerminalRuleCall_4_1_0; }
	}
	
	/**
	 * The Class StringLiteralElements.
	 */
	public class StringLiteralElements extends AbstractParserRuleElementFinder {
		
		/** The rule. */
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.StringLiteral");
		
		/** The c op assignment. */
		private final Assignment cOpAssignment = (Assignment)rule.eContents().get(1);
		
		/** The c op STRING terminal rule call 0. */
		private final RuleCall cOpSTRINGTerminalRuleCall_0 = (RuleCall)cOpAssignment.eContents().get(0);
		
		//StringLiteral:
		//    op=STRING
		//;
		@Override public ParserRule getRule() { return rule; }
		
		/**
		 * Gets the op assignment.
		 *
		 * @return the op assignment
		 */
		//op=STRING
		public Assignment getOpAssignment() { return cOpAssignment; }
		
		/**
		 * Gets the op STRING terminal rule call 0.
		 *
		 * @return the op STRING terminal rule call 0
		 */
		//STRING
		public RuleCall getOpSTRINGTerminalRuleCall_0() { return cOpSTRINGTerminalRuleCall_0; }
	}
	
	
	/** The p entry. */
	private final EntryElements pEntry;
	
	/** The p standalone block. */
	private final StandaloneBlockElements pStandaloneBlock;
	
	/** The p string evaluator. */
	private final StringEvaluatorElements pStringEvaluator;
	
	/** The p model. */
	private final ModelElements pModel;
	
	/** The p model block. */
	private final ModelBlockElements pModelBlock;
	
	/** The p import. */
	private final ImportElements pImport;
	
	/** The p pragma. */
	private final PragmaElements pPragma;
	
	/** The p experiment file structure. */
	private final ExperimentFileStructureElements pExperimentFileStructure;
	
	/** The p headless experiment. */
	private final HeadlessExperimentElements pHeadlessExperiment;
	
	/** The p S section. */
	private final S_SectionElements pS_Section;
	
	/** The p S global. */
	private final S_GlobalElements pS_Global;
	
	/** The p S species. */
	private final S_SpeciesElements pS_Species;
	
	/** The p S experiment. */
	private final S_ExperimentElements pS_Experiment;
	
	/** The p statement. */
	private final StatementElements pStatement;
	
	/** The p S 1 expr facets block or end. */
	private final S_1Expr_Facets_BlockOrEndElements pS_1Expr_Facets_BlockOrEnd;
	
	/** The p S do. */
	private final S_DoElements pS_Do;
	
	/** The p S loop. */
	private final S_LoopElements pS_Loop;
	
	/** The p S if. */
	private final S_IfElements pS_If;
	
	/** The p S try. */
	private final S_TryElements pS_Try;
	
	/** The p S other. */
	private final S_OtherElements pS_Other;
	
	/** The p S return. */
	private final S_ReturnElements pS_Return;
	
	/** The p S declaration. */
	private final S_DeclarationElements pS_Declaration;
	
	/** The p S reflex. */
	private final S_ReflexElements pS_Reflex;
	
	/** The p S definition. */
	private final S_DefinitionElements pS_Definition;
	
	/** The p S action. */
	private final S_ActionElements pS_Action;
	
	/** The p S var. */
	private final S_VarElements pS_Var;
	
	/** The p S assignment. */
	private final S_AssignmentElements pS_Assignment;
	
	/** The p S direct assignment. */
	private final S_DirectAssignmentElements pS_DirectAssignment;
	
	/** The p S set. */
	private final S_SetElements pS_Set;
	
	/** The p S equations. */
	private final S_EquationsElements pS_Equations;
	
	/** The p S equation. */
	private final S_EquationElements pS_Equation;
	
	/** The p S solve. */
	private final S_SolveElements pS_Solve;
	
	/** The p S display. */
	private final S_DisplayElements pS_Display;
	
	/** The p display block. */
	private final DisplayBlockElements pDisplayBlock;
	
	/** The p display statement. */
	private final DisplayStatementElements pDisplayStatement;
	
	/** The p species or grid display statement. */
	private final SpeciesOrGridDisplayStatementElements pSpeciesOrGridDisplayStatement;
	
	/** The p equations key. */
	private final _EquationsKeyElements p_EquationsKey;
	
	/** The p solve key. */
	private final _SolveKeyElements p_SolveKey;
	
	/** The p species key. */
	private final _SpeciesKeyElements p_SpeciesKey;
	
	/** The p experiment key. */
	private final _ExperimentKeyElements p_ExperimentKey;
	
	/** The p 1 expr facets block or end key. */
	private final _1Expr_Facets_BlockOrEnd_KeyElements p_1Expr_Facets_BlockOrEnd_Key;
	
	/** The p layer key. */
	private final _LayerKeyElements p_LayerKey;
	
	/** The p do key. */
	private final _DoKeyElements p_DoKey;
	
	/** The p var or const key. */
	private final _VarOrConstKeyElements p_VarOrConstKey;
	
	/** The p reflex key. */
	private final _ReflexKeyElements p_ReflexKey;
	
	/** The p assignment key. */
	private final _AssignmentKeyElements p_AssignmentKey;
	
	/** The p action arguments. */
	private final ActionArgumentsElements pActionArguments;
	
	/** The p argument definition. */
	private final ArgumentDefinitionElements pArgumentDefinition;
	
	/** The p facet. */
	private final FacetElements pFacet;
	
	/** The p first facet key. */
	private final FirstFacetKeyElements pFirstFacetKey;
	
	/** The p classic facet key. */
	private final ClassicFacetKeyElements pClassicFacetKey;
	
	/** The p definition facet key. */
	private final DefinitionFacetKeyElements pDefinitionFacetKey;
	
	/** The p type facet key. */
	private final TypeFacetKeyElements pTypeFacetKey;
	
	/** The p special facet key. */
	private final SpecialFacetKeyElements pSpecialFacetKey;
	
	/** The p action facet key. */
	private final ActionFacetKeyElements pActionFacetKey;
	
	/** The p var facet key. */
	private final VarFacetKeyElements pVarFacetKey;
	
	/** The p classic facet. */
	private final ClassicFacetElements pClassicFacet;
	
	/** The p definition facet. */
	private final DefinitionFacetElements pDefinitionFacet;
	
	/** The p function facet. */
	private final FunctionFacetElements pFunctionFacet;
	
	/** The p type facet. */
	private final TypeFacetElements pTypeFacet;
	
	/** The p action facet. */
	private final ActionFacetElements pActionFacet;
	
	/** The p var facet. */
	private final VarFacetElements pVarFacet;
	
	/** The p block. */
	private final BlockElements pBlock;
	
	/** The p expression. */
	private final ExpressionElements pExpression;
	
	/** The p binary operator. */
	private final BinaryOperatorElements pBinaryOperator;
	
	/** The p argument pair. */
	private final ArgumentPairElements pArgumentPair;
	
	/** The p pair. */
	private final PairElements pPair;
	
	/** The p if. */
	private final IfElements pIf;
	
	/** The p or. */
	private final OrElements pOr;
	
	/** The p and. */
	private final AndElements pAnd;
	
	/** The p cast. */
	private final CastElements pCast;
	
	/** The p comparison. */
	private final ComparisonElements pComparison;
	
	/** The p addition. */
	private final AdditionElements pAddition;
	
	/** The p multiplication. */
	private final MultiplicationElements pMultiplication;
	
	/** The p exponentiation. */
	private final ExponentiationElements pExponentiation;
	
	/** The p binary. */
	private final BinaryElements pBinary;
	
	/** The p unit. */
	private final UnitElements pUnit;
	
	/** The p unary. */
	private final UnaryElements pUnary;
	
	/** The p access. */
	private final AccessElements pAccess;
	
	/** The p primary. */
	private final PrimaryElements pPrimary;
	
	/** The p abstract ref. */
	private final AbstractRefElements pAbstractRef;
	
	/** The p function. */
	private final FunctionElements pFunction;
	
	/** The p expression list. */
	private final ExpressionListElements pExpressionList;
	
	/** The p parameter. */
	private final ParameterElements pParameter;
	
	/** The p unit ref. */
	private final UnitRefElements pUnitRef;
	
	/** The p variable ref. */
	private final VariableRefElements pVariableRef;
	
	/** The p type ref. */
	private final TypeRefElements pTypeRef;
	
	/** The p type info. */
	private final TypeInfoElements pTypeInfo;
	
	/** The p skill ref. */
	private final SkillRefElements pSkillRef;
	
	/** The p action ref. */
	private final ActionRefElements pActionRef;
	
	/** The p equation ref. */
	private final EquationRefElements pEquationRef;
	
	/** The p gaml definition. */
	private final GamlDefinitionElements pGamlDefinition;
	
	/** The p equation definition. */
	private final EquationDefinitionElements pEquationDefinition;
	
	/** The p type definition. */
	private final TypeDefinitionElements pTypeDefinition;
	
	/** The p var definition. */
	private final VarDefinitionElements pVarDefinition;
	
	/** The p action definition. */
	private final ActionDefinitionElements pActionDefinition;
	
	/** The p unit fake definition. */
	private final UnitFakeDefinitionElements pUnitFakeDefinition;
	
	/** The p type fake definition. */
	private final TypeFakeDefinitionElements pTypeFakeDefinition;
	
	/** The p action fake definition. */
	private final ActionFakeDefinitionElements pActionFakeDefinition;
	
	/** The p skill fake definition. */
	private final SkillFakeDefinitionElements pSkillFakeDefinition;
	
	/** The p var fake definition. */
	private final VarFakeDefinitionElements pVarFakeDefinition;
	
	/** The p equation fake definition. */
	private final EquationFakeDefinitionElements pEquationFakeDefinition;
	
	/** The p valid ID. */
	private final Valid_IDElements pValid_ID;
	
	/** The p terminal expression. */
	private final TerminalExpressionElements pTerminalExpression;
	
	/** The p string literal. */
	private final StringLiteralElements pStringLiteral;
	
	/** The t KEYWORD. */
	private final TerminalRule tKEYWORD;
	
	/** The t INTEGER. */
	private final TerminalRule tINTEGER;
	
	/** The t BOOLEAN. */
	private final TerminalRule tBOOLEAN;
	
	/** The t ID. */
	private final TerminalRule tID;
	
	/** The t DOUBLE. */
	private final TerminalRule tDOUBLE;
	
	/** The t STRING. */
	private final TerminalRule tSTRING;
	
	/** The t M L COMMENT. */
	private final TerminalRule tML_COMMENT;
	
	/** The t S L COMMENT. */
	private final TerminalRule tSL_COMMENT;
	
	/** The t WS. */
	private final TerminalRule tWS;
	
	/** The t AN Y OTHER. */
	private final TerminalRule tANY_OTHER;
	
	/** The grammar. */
	private final Grammar grammar;

	/**
	 * Instantiates a new gaml grammar access.
	 *
	 * @param grammarProvider the grammar provider
	 */
	@Inject
	public GamlGrammarAccess(GrammarProvider grammarProvider) {
		this.grammar = internalFindGrammar(grammarProvider);
		this.pEntry = new EntryElements();
		this.pStandaloneBlock = new StandaloneBlockElements();
		this.pStringEvaluator = new StringEvaluatorElements();
		this.pModel = new ModelElements();
		this.pModelBlock = new ModelBlockElements();
		this.pImport = new ImportElements();
		this.pPragma = new PragmaElements();
		this.pExperimentFileStructure = new ExperimentFileStructureElements();
		this.pHeadlessExperiment = new HeadlessExperimentElements();
		this.pS_Section = new S_SectionElements();
		this.pS_Global = new S_GlobalElements();
		this.pS_Species = new S_SpeciesElements();
		this.pS_Experiment = new S_ExperimentElements();
		this.pStatement = new StatementElements();
		this.pS_1Expr_Facets_BlockOrEnd = new S_1Expr_Facets_BlockOrEndElements();
		this.pS_Do = new S_DoElements();
		this.pS_Loop = new S_LoopElements();
		this.pS_If = new S_IfElements();
		this.pS_Try = new S_TryElements();
		this.pS_Other = new S_OtherElements();
		this.pS_Return = new S_ReturnElements();
		this.pS_Declaration = new S_DeclarationElements();
		this.pS_Reflex = new S_ReflexElements();
		this.pS_Definition = new S_DefinitionElements();
		this.pS_Action = new S_ActionElements();
		this.pS_Var = new S_VarElements();
		this.pS_Assignment = new S_AssignmentElements();
		this.pS_DirectAssignment = new S_DirectAssignmentElements();
		this.pS_Set = new S_SetElements();
		this.pS_Equations = new S_EquationsElements();
		this.pS_Equation = new S_EquationElements();
		this.pS_Solve = new S_SolveElements();
		this.pS_Display = new S_DisplayElements();
		this.pDisplayBlock = new DisplayBlockElements();
		this.pDisplayStatement = new DisplayStatementElements();
		this.pSpeciesOrGridDisplayStatement = new SpeciesOrGridDisplayStatementElements();
		this.p_EquationsKey = new _EquationsKeyElements();
		this.p_SolveKey = new _SolveKeyElements();
		this.p_SpeciesKey = new _SpeciesKeyElements();
		this.p_ExperimentKey = new _ExperimentKeyElements();
		this.p_1Expr_Facets_BlockOrEnd_Key = new _1Expr_Facets_BlockOrEnd_KeyElements();
		this.p_LayerKey = new _LayerKeyElements();
		this.p_DoKey = new _DoKeyElements();
		this.p_VarOrConstKey = new _VarOrConstKeyElements();
		this.p_ReflexKey = new _ReflexKeyElements();
		this.p_AssignmentKey = new _AssignmentKeyElements();
		this.pActionArguments = new ActionArgumentsElements();
		this.pArgumentDefinition = new ArgumentDefinitionElements();
		this.pFacet = new FacetElements();
		this.pFirstFacetKey = new FirstFacetKeyElements();
		this.pClassicFacetKey = new ClassicFacetKeyElements();
		this.pDefinitionFacetKey = new DefinitionFacetKeyElements();
		this.pTypeFacetKey = new TypeFacetKeyElements();
		this.pSpecialFacetKey = new SpecialFacetKeyElements();
		this.pActionFacetKey = new ActionFacetKeyElements();
		this.pVarFacetKey = new VarFacetKeyElements();
		this.pClassicFacet = new ClassicFacetElements();
		this.pDefinitionFacet = new DefinitionFacetElements();
		this.pFunctionFacet = new FunctionFacetElements();
		this.pTypeFacet = new TypeFacetElements();
		this.pActionFacet = new ActionFacetElements();
		this.pVarFacet = new VarFacetElements();
		this.pBlock = new BlockElements();
		this.pExpression = new ExpressionElements();
		this.pBinaryOperator = new BinaryOperatorElements();
		this.pArgumentPair = new ArgumentPairElements();
		this.pPair = new PairElements();
		this.pIf = new IfElements();
		this.pOr = new OrElements();
		this.pAnd = new AndElements();
		this.pCast = new CastElements();
		this.pComparison = new ComparisonElements();
		this.pAddition = new AdditionElements();
		this.pMultiplication = new MultiplicationElements();
		this.pExponentiation = new ExponentiationElements();
		this.pBinary = new BinaryElements();
		this.pUnit = new UnitElements();
		this.pUnary = new UnaryElements();
		this.pAccess = new AccessElements();
		this.pPrimary = new PrimaryElements();
		this.pAbstractRef = new AbstractRefElements();
		this.pFunction = new FunctionElements();
		this.pExpressionList = new ExpressionListElements();
		this.pParameter = new ParameterElements();
		this.pUnitRef = new UnitRefElements();
		this.pVariableRef = new VariableRefElements();
		this.pTypeRef = new TypeRefElements();
		this.pTypeInfo = new TypeInfoElements();
		this.pSkillRef = new SkillRefElements();
		this.pActionRef = new ActionRefElements();
		this.pEquationRef = new EquationRefElements();
		this.pGamlDefinition = new GamlDefinitionElements();
		this.pEquationDefinition = new EquationDefinitionElements();
		this.pTypeDefinition = new TypeDefinitionElements();
		this.pVarDefinition = new VarDefinitionElements();
		this.pActionDefinition = new ActionDefinitionElements();
		this.pUnitFakeDefinition = new UnitFakeDefinitionElements();
		this.pTypeFakeDefinition = new TypeFakeDefinitionElements();
		this.pActionFakeDefinition = new ActionFakeDefinitionElements();
		this.pSkillFakeDefinition = new SkillFakeDefinitionElements();
		this.pVarFakeDefinition = new VarFakeDefinitionElements();
		this.pEquationFakeDefinition = new EquationFakeDefinitionElements();
		this.pValid_ID = new Valid_IDElements();
		this.pTerminalExpression = new TerminalExpressionElements();
		this.pStringLiteral = new StringLiteralElements();
		this.tKEYWORD = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.KEYWORD");
		this.tINTEGER = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.INTEGER");
		this.tBOOLEAN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.BOOLEAN");
		this.tID = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ID");
		this.tDOUBLE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.DOUBLE");
		this.tSTRING = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.STRING");
		this.tML_COMMENT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ML_COMMENT");
		this.tSL_COMMENT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.SL_COMMENT");
		this.tWS = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.WS");
		this.tANY_OTHER = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "msi.gama.lang.gaml.Gaml.ANY_OTHER");
	}
	
	/**
	 * Internal find grammar.
	 *
	 * @param grammarProvider the grammar provider
	 * @return the grammar
	 */
	protected Grammar internalFindGrammar(GrammarProvider grammarProvider) {
		Grammar grammar = grammarProvider.getGrammar(this);
		while (grammar != null) {
			if ("msi.gama.lang.gaml.Gaml".equals(grammar.getName())) {
				return grammar;
			}
			List<Grammar> grammars = grammar.getUsedGrammars();
			if (!grammars.isEmpty()) {
				grammar = grammars.iterator().next();
			} else {
				return null;
			}
		}
		return grammar;
	}
	
	@Override
	public Grammar getGrammar() {
		return grammar;
	}
	

	
	//Entry:
	/**
	 * Gets the entry access.
	 *
	 * @return the entry access
	 */
	//    ->Model | StringEvaluator | StandaloneBlock | ExperimentFileStructure;
	public EntryElements getEntryAccess() {
		return pEntry;
	}
	
	/**
	 * Gets the entry rule.
	 *
	 * @return the entry rule
	 */
	public ParserRule getEntryRule() {
		return getEntryAccess().getRule();
	}
	
	//StandaloneBlock:
	//    '__synthetic__' block=Block
	/**
	 * Gets the standalone block access.
	 *
	 * @return the standalone block access
	 */
	//;
	public StandaloneBlockElements getStandaloneBlockAccess() {
		return pStandaloneBlock;
	}
	
	/**
	 * Gets the standalone block rule.
	 *
	 * @return the standalone block rule
	 */
	public ParserRule getStandaloneBlockRule() {
		return getStandaloneBlockAccess().getRule();
	}
	
	//StringEvaluator:
	/**
	 * Gets the string evaluator access.
	 *
	 * @return the string evaluator access
	 */
	//    toto=ID "<-" expr=Expression;
	public StringEvaluatorElements getStringEvaluatorAccess() {
		return pStringEvaluator;
	}
	
	/**
	 * Gets the string evaluator rule.
	 *
	 * @return the string evaluator rule
	 */
	public ParserRule getStringEvaluatorRule() {
		return getStringEvaluatorAccess().getRule();
	}
	
	//Model:
	/**
	 * Gets the model access.
	 *
	 * @return the model access
	 */
	//    (pragmas +=Pragma)* 'model' name=ID  (imports+=Import)* block=ModelBlock;
	public ModelElements getModelAccess() {
		return pModel;
	}
	
	/**
	 * Gets the model rule.
	 *
	 * @return the model rule
	 */
	public ParserRule getModelRule() {
		return getModelAccess().getRule();
	}
	
	//ModelBlock returns Block:
	//    {Block} (statements+=(S_Section))*
	/**
	 * Gets the model block access.
	 *
	 * @return the model block access
	 */
	//;
	public ModelBlockElements getModelBlockAccess() {
		return pModelBlock;
	}
	
	/**
	 * Gets the model block rule.
	 *
	 * @return the model block rule
	 */
	public ParserRule getModelBlockRule() {
		return getModelBlockAccess().getRule();
	}
	
	//Import:
	/**
	 * Gets the import access.
	 *
	 * @return the import access
	 */
	//    'import' importURI=STRING ("as" name=Valid_ID)?;
	public ImportElements getImportAccess() {
		return pImport;
	}
	
	/**
	 * Gets the import rule.
	 *
	 * @return the import rule
	 */
	public ParserRule getImportRule() {
		return getImportAccess().getRule();
	}
	
	// // must be named importURI
	//Pragma:
	//    '@' name=ID
	/**
	 * Gets the pragma access.
	 *
	 * @return the pragma access
	 */
	//;
	public PragmaElements getPragmaAccess() {
		return pPragma;
	}
	
	/**
	 * Gets the pragma rule.
	 *
	 * @return the pragma rule
	 */
	public ParserRule getPragmaRule() {
		return getPragmaAccess().getRule();
	}
	
	///**
	// * Experiment files
	// */
	//ExperimentFileStructure:
	//    exp=HeadlessExperiment
	/**
	 * Gets the experiment file structure access.
	 *
	 * @return the experiment file structure access
	 */
	//;
	public ExperimentFileStructureElements getExperimentFileStructureAccess() {
		return pExperimentFileStructure;
	}
	
	/**
	 * Gets the experiment file structure rule.
	 *
	 * @return the experiment file structure rule
	 */
	public ParserRule getExperimentFileStructureRule() {
		return getExperimentFileStructureAccess().getRule();
	}
	
	//HeadlessExperiment :
	/**
	 * Gets the headless experiment access.
	 *
	 * @return the headless experiment access
	 */
	//    key=_ExperimentKey (firstFacet="name:")? name=(Valid_ID | STRING) ('model:' importURI=STRING) ? (facets+=Facet)* (block=Block | ';');
	public HeadlessExperimentElements getHeadlessExperimentAccess() {
		return pHeadlessExperiment;
	}
	
	/**
	 * Gets the headless experiment rule.
	 *
	 * @return the headless experiment rule
	 */
	public ParserRule getHeadlessExperimentRule() {
		return getHeadlessExperimentAccess().getRule();
	}
	
	///**
	// * Global statements
	// */
	//S_Section returns Statement:
	//     S_Global | S_Species | S_Experiment
	/**
	 * Gets the s section access.
	 *
	 * @return the s section access
	 */
	//;
	public S_SectionElements getS_SectionAccess() {
		return pS_Section;
	}
	
	/**
	 * Gets the s section rule.
	 *
	 * @return the s section rule
	 */
	public ParserRule getS_SectionRule() {
		return getS_SectionAccess().getRule();
	}
	
	//S_Global :
	//    key="global" (facets+=Facet)* (block=Block | ';')
	/**
	 * Gets the s global access.
	 *
	 * @return the s global access
	 */
	//;
	public S_GlobalElements getS_GlobalAccess() {
		return pS_Global;
	}
	
	/**
	 * Gets the s global rule.
	 *
	 * @return the s global rule
	 */
	public ParserRule getS_GlobalRule() {
		return getS_GlobalAccess().getRule();
	}
	
	//S_Species :
	/**
	 * Gets the s species access.
	 *
	 * @return the s species access
	 */
	//    key=_SpeciesKey (firstFacet='name:')? name=ID (facets+=Facet)* (block=Block | ';');
	public S_SpeciesElements getS_SpeciesAccess() {
		return pS_Species;
	}
	
	/**
	 * Gets the s species rule.
	 *
	 * @return the s species rule
	 */
	public ParserRule getS_SpeciesRule() {
		return getS_SpeciesAccess().getRule();
	}
	
	//S_Experiment :
	/**
	 * Gets the s experiment access.
	 *
	 * @return the s experiment access
	 */
	//    key=_ExperimentKey (firstFacet="name:")? name=(Valid_ID | STRING) (facets+=Facet)* (block=Block | ';');
	public S_ExperimentElements getS_ExperimentAccess() {
		return pS_Experiment;
	}
	
	/**
	 * Gets the s experiment rule.
	 *
	 * @return the s experiment rule
	 */
	public ParserRule getS_ExperimentRule() {
		return getS_ExperimentAccess().getRule();
	}
	
	///**
	// * Statements
	// */
	//Statement:
	//    (=> S_Declaration |
	/**
	 * Gets the statement access.
	 *
	 * @return the statement access
	 */
	//    ((=> S_Assignment | S_1Expr_Facets_BlockOrEnd | S_Other | S_Do | S_Return | S_Solve | S_If | S_Try | S_Equations))) | S_Display ;
	public StatementElements getStatementAccess() {
		return pStatement;
	}
	
	/**
	 * Gets the statement rule.
	 *
	 * @return the statement rule
	 */
	public ParserRule getStatementRule() {
		return getStatementAccess().getRule();
	}
	
	//S_1Expr_Facets_BlockOrEnd returns Statement:
	/**
	 * Gets the s 1 expr facets block or end access.
	 *
	 * @return the s 1 expr facets block or end access
	 */
	//    key=_1Expr_Facets_BlockOrEnd_Key (firstFacet=FirstFacetKey)? (expr=Expression) (facets+=Facet)* (block=Block | ";");
	public S_1Expr_Facets_BlockOrEndElements getS_1Expr_Facets_BlockOrEndAccess() {
		return pS_1Expr_Facets_BlockOrEnd;
	}
	
	/**
	 * Gets the s 1 expr facets block or end rule.
	 *
	 * @return the s 1 expr facets block or end rule
	 */
	public ParserRule getS_1Expr_Facets_BlockOrEndRule() {
		return getS_1Expr_Facets_BlockOrEndAccess().getRule();
	}
	
	//S_Do:
	/**
	 * Gets the s do access.
	 *
	 * @return the s do access
	 */
	//    key=_DoKey (firstFacet="action:")? expr=AbstractRef (facets+=Facet)* (block=Block | ';');
	public S_DoElements getS_DoAccess() {
		return pS_Do;
	}
	
	/**
	 * Gets the s do rule.
	 *
	 * @return the s do rule
	 */
	public ParserRule getS_DoRule() {
		return getS_DoAccess().getRule();
	}
	
	//S_Loop:
	/**
	 * Gets the s loop access.
	 *
	 * @return the s loop access
	 */
	//    key="loop" (name=ID)? (facets+=Facet)* block=Block;
	public S_LoopElements getS_LoopAccess() {
		return pS_Loop;
	}
	
	/**
	 * Gets the s loop rule.
	 *
	 * @return the s loop rule
	 */
	public ParserRule getS_LoopRule() {
		return getS_LoopAccess().getRule();
	}
	
	//S_If:
	/**
	 * Gets the s if access.
	 *
	 * @return the s if access
	 */
	//    key='if' (firstFacet="condition:")? expr=Expression block=Block (-> 'else' else=(S_If | Block))?;
	public S_IfElements getS_IfAccess() {
		return pS_If;
	}
	
	/**
	 * Gets the s if rule.
	 *
	 * @return the s if rule
	 */
	public ParserRule getS_IfRule() {
		return getS_IfAccess().getRule();
	}
	
	//S_Try:
	//    key='try' block=Block (-> 'catch' catch=Block) ?
	/**
	 * Gets the s try access.
	 *
	 * @return the s try access
	 */
	//;
	public S_TryElements getS_TryAccess() {
		return pS_Try;
	}
	
	/**
	 * Gets the s try rule.
	 *
	 * @return the s try rule
	 */
	public ParserRule getS_TryRule() {
		return getS_TryAccess().getRule();
	}
	
	//S_Other:
	/**
	 * Gets the s other access.
	 *
	 * @return the s other access
	 */
	//    key=ID (facets+=Facet)* (block=Block | ';');
	public S_OtherElements getS_OtherAccess() {
		return pS_Other;
	}
	
	/**
	 * Gets the s other rule.
	 *
	 * @return the s other rule
	 */
	public ParserRule getS_OtherRule() {
		return getS_OtherAccess().getRule();
	}
	
	//S_Return:
	/**
	 * Gets the s return access.
	 *
	 * @return the s return access
	 */
	//    key='return' (firstFacet="value:")? expr=Expression? ';';
	public S_ReturnElements getS_ReturnAccess() {
		return pS_Return;
	}
	
	/**
	 * Gets the s return rule.
	 *
	 * @return the s return rule
	 */
	public ParserRule getS_ReturnRule() {
		return getS_ReturnAccess().getRule();
	}
	
	//    /*
	// * DECLARATIONS
	// */
	//S_Declaration:
	/**
	 * Gets the s declaration access.
	 *
	 * @return the s declaration access
	 */
	//    ->S_Definition | S_Species | S_Reflex | S_Action | S_Var | S_Loop ;
	public S_DeclarationElements getS_DeclarationAccess() {
		return pS_Declaration;
	}
	
	/**
	 * Gets the s declaration rule.
	 *
	 * @return the s declaration rule
	 */
	public ParserRule getS_DeclarationRule() {
		return getS_DeclarationAccess().getRule();
	}
	
	//S_Reflex:
	/**
	 * Gets the s reflex access.
	 *
	 * @return the s reflex access
	 */
	//    key=_ReflexKey ((firstFacet="name:")? name=Valid_ID)? ("when"":" expr=Expression)? block=Block;
	public S_ReflexElements getS_ReflexAccess() {
		return pS_Reflex;
	}
	
	/**
	 * Gets the s reflex rule.
	 *
	 * @return the s reflex rule
	 */
	public ParserRule getS_ReflexRule() {
		return getS_ReflexAccess().getRule();
	}
	
	//S_Definition:
	/**
	 * Gets the s definition access.
	 *
	 * @return the s definition access
	 */
	//    tkey=(TypeRef) (firstFacet="name:")? name=(Valid_ID | STRING) ('(' (args=ActionArguments) ')')? (facets+=Facet)* (block=Block | ';');
	public S_DefinitionElements getS_DefinitionAccess() {
		return pS_Definition;
	}
	
	/**
	 * Gets the s definition rule.
	 *
	 * @return the s definition rule
	 */
	public ParserRule getS_DefinitionRule() {
		return getS_DefinitionAccess().getRule();
	}
	
	//S_Action returns S_Definition:
	/**
	 * Gets the s action access.
	 *
	 * @return the s action access
	 */
	//    {S_Action} key="action" (firstFacet='name:')? name=Valid_ID ('(' (args=ActionArguments) ')')? (facets+=Facet)* (block=Block | ';');
	public S_ActionElements getS_ActionAccess() {
		return pS_Action;
	}
	
	/**
	 * Gets the s action rule.
	 *
	 * @return the s action rule
	 */
	public ParserRule getS_ActionRule() {
		return getS_ActionAccess().getRule();
	}
	
	//S_Var returns S_Definition:
	/**
	 * Gets the s var access.
	 *
	 * @return the s var access
	 */
	//    {S_Var} key=_VarOrConstKey (firstFacet="name:")? name=Valid_ID (facets+=Facet)* ';';
	public S_VarElements getS_VarAccess() {
		return pS_Var;
	}
	
	/**
	 * Gets the s var rule.
	 *
	 * @return the s var rule
	 */
	public ParserRule getS_VarRule() {
		return getS_VarAccess().getRule();
	}
	
	//    /*
	// * ASSIGNMENTS
	// */
	//S_Assignment:
	/**
	 * Gets the s assignment access.
	 *
	 * @return the s assignment access
	 */
	//    S_DirectAssignment | S_Set;
	public S_AssignmentElements getS_AssignmentAccess() {
		return pS_Assignment;
	}
	
	/**
	 * Gets the s assignment rule.
	 *
	 * @return the s assignment rule
	 */
	public ParserRule getS_AssignmentRule() {
		return getS_AssignmentAccess().getRule();
	}
	
	//S_DirectAssignment:
	/**
	 * Gets the s direct assignment access.
	 *
	 * @return the s direct assignment access
	 */
	//    (expr=Expression key=(_AssignmentKey) value=Expression (facets+=Facet)*) ';';
	public S_DirectAssignmentElements getS_DirectAssignmentAccess() {
		return pS_DirectAssignment;
	}
	
	/**
	 * Gets the s direct assignment rule.
	 *
	 * @return the s direct assignment rule
	 */
	public ParserRule getS_DirectAssignmentRule() {
		return getS_DirectAssignmentAccess().getRule();
	}
	
	//S_Set:
	/**
	 * Gets the s set access.
	 *
	 * @return the s set access
	 */
	//    key="set" expr=Expression ("value:" | "<-") value=Expression ";";
	public S_SetElements getS_SetAccess() {
		return pS_Set;
	}
	
	/**
	 * Gets the s set rule.
	 *
	 * @return the s set rule
	 */
	public ParserRule getS_SetRule() {
		return getS_SetAccess().getRule();
	}
	
	//S_Equations:
	/**
	 * Gets the s equations access.
	 *
	 * @return the s equations access
	 */
	//    key=_EquationsKey name=Valid_ID (facets+=Facet)* ('{' (equations+=S_Equation ';')* '}' | ';');
	public S_EquationsElements getS_EquationsAccess() {
		return pS_Equations;
	}
	
	/**
	 * Gets the s equations rule.
	 *
	 * @return the s equations rule
	 */
	public ParserRule getS_EquationsRule() {
		return getS_EquationsAccess().getRule();
	}
	
	//S_Equation returns S_Assignment:
	/**
	 * Gets the s equation access.
	 *
	 * @return the s equation access
	 */
	//    expr=(Function|VariableRef) key="=" value=Expression;
	public S_EquationElements getS_EquationAccess() {
		return pS_Equation;
	}
	
	/**
	 * Gets the s equation rule.
	 *
	 * @return the s equation rule
	 */
	public ParserRule getS_EquationRule() {
		return getS_EquationAccess().getRule();
	}
	
	//S_Solve:
	/**
	 * Gets the s solve access.
	 *
	 * @return the s solve access
	 */
	//    key=_SolveKey (firstFacet="equation:")? expr=EquationRef (facets+=Facet)* (block=Block | ';');
	public S_SolveElements getS_SolveAccess() {
		return pS_Solve;
	}
	
	/**
	 * Gets the s solve rule.
	 *
	 * @return the s solve rule
	 */
	public ParserRule getS_SolveRule() {
		return getS_SolveAccess().getRule();
	}
	
	///**
	// * DISPLAYS
	// */
	//S_Display:
	//    key="display" (firstFacet="name:")? name=(Valid_ID|STRING) (facets+=Facet)* block=displayBlock
	/**
	 * Gets the s display access.
	 *
	 * @return the s display access
	 */
	//;
	public S_DisplayElements getS_DisplayAccess() {
		return pS_Display;
	}
	
	/**
	 * Gets the s display rule.
	 *
	 * @return the s display rule
	 */
	public ParserRule getS_DisplayRule() {
		return getS_DisplayAccess().getRule();
	}
	
	//displayBlock returns Block:
	//    {Block} '{' (statements+=displayStatement)* '}'
	/**
	 * Gets the display block access.
	 *
	 * @return the display block access
	 */
	//;
	public DisplayBlockElements getDisplayBlockAccess() {
		return pDisplayBlock;
	}
	
	/**
	 * Gets the display block rule.
	 *
	 * @return the display block rule
	 */
	public ParserRule getDisplayBlockRule() {
		return getDisplayBlockAccess().getRule();
	}
	
	//displayStatement returns Statement:
	//    =>speciesOrGridDisplayStatement | Statement
	/**
	 * Gets the display statement access.
	 *
	 * @return the display statement access
	 */
	//;
	public DisplayStatementElements getDisplayStatementAccess() {
		return pDisplayStatement;
	}
	
	/**
	 * Gets the display statement rule.
	 *
	 * @return the display statement rule
	 */
	public ParserRule getDisplayStatementRule() {
		return getDisplayStatementAccess().getRule();
	}
	
	//speciesOrGridDisplayStatement:
	//    key=_SpeciesKey expr=Expression (facets+=Facet)* (block=displayBlock | ";")
	/**
	 * Gets the species or grid display statement access.
	 *
	 * @return the species or grid display statement access
	 */
	//;
	public SpeciesOrGridDisplayStatementElements getSpeciesOrGridDisplayStatementAccess() {
		return pSpeciesOrGridDisplayStatement;
	}
	
	/**
	 * Gets the species or grid display statement rule.
	 *
	 * @return the species or grid display statement rule
	 */
	public ParserRule getSpeciesOrGridDisplayStatementRule() {
		return getSpeciesOrGridDisplayStatementAccess().getRule();
	}
	
	//    /**
	// * Statement keys
	// */
	//_EquationsKey:
	/**
	 * Gets the equations key access.
	 *
	 * @return the equations key access
	 */
	//    "equation";
	public _EquationsKeyElements get_EquationsKeyAccess() {
		return p_EquationsKey;
	}
	
	/**
	 * Gets the equations key rule.
	 *
	 * @return the equations key rule
	 */
	public ParserRule get_EquationsKeyRule() {
		return get_EquationsKeyAccess().getRule();
	}
	
	//_SolveKey:
	/**
	 * Gets the solve key access.
	 *
	 * @return the solve key access
	 */
	//    "solve";
	public _SolveKeyElements get_SolveKeyAccess() {
		return p_SolveKey;
	}
	
	/**
	 * Gets the solve key rule.
	 *
	 * @return the solve key rule
	 */
	public ParserRule get_SolveKeyRule() {
		return get_SolveKeyAccess().getRule();
	}
	
	//_SpeciesKey:
	/**
	 * Gets the species key access.
	 *
	 * @return the species key access
	 */
	//    "species" | "grid";
	public _SpeciesKeyElements get_SpeciesKeyAccess() {
		return p_SpeciesKey;
	}
	
	/**
	 * Gets the species key rule.
	 *
	 * @return the species key rule
	 */
	public ParserRule get_SpeciesKeyRule() {
		return get_SpeciesKeyAccess().getRule();
	}
	
	//_ExperimentKey:
	//    "experiment"
	/**
	 * Gets the experiment key access.
	 *
	 * @return the experiment key access
	 */
	//;
	public _ExperimentKeyElements get_ExperimentKeyAccess() {
		return p_ExperimentKey;
	}
	
	/**
	 * Gets the experiment key rule.
	 *
	 * @return the experiment key rule
	 */
	public ParserRule get_ExperimentKeyRule() {
		return get_ExperimentKeyAccess().getRule();
	}
	
	//_1Expr_Facets_BlockOrEnd_Key:
	//    _LayerKey | "ask" | "release" | "capture" | "create" | "write" | "error" | "warn" | "exception" | "save" | "assert" | "inspect" | "browse" |
	/**
	 * Gets the 1 expr facets block or end key access.
	 *
	 * @return the 1 expr facets block or end key access
	 */
	//    "draw"  | "using" | "switch" | "put" | "add" | "remove" | "match" | "match_between" | "match_one" | "parameter" | "status" | "highlight" | "focus_on" | "layout" ;
	public _1Expr_Facets_BlockOrEnd_KeyElements get_1Expr_Facets_BlockOrEnd_KeyAccess() {
		return p_1Expr_Facets_BlockOrEnd_Key;
	}
	
	/**
	 * Gets the 1 expr facets block or end key rule.
	 *
	 * @return the 1 expr facets block or end key rule
	 */
	public ParserRule get_1Expr_Facets_BlockOrEnd_KeyRule() {
		return get_1Expr_Facets_BlockOrEnd_KeyAccess().getRule();
	}
	
	//_LayerKey:
	//    "light" | "camera" | "text" | "image" | "data" | "chart" | "agents" | "graphics" | "display_population" | "display_grid" | "quadtree" | "event" | "overlay" | "datalist" | "mesh"
	/**
	 * Gets the layer key access.
	 *
	 * @return the layer key access
	 */
	//;
	public _LayerKeyElements get_LayerKeyAccess() {
		return p_LayerKey;
	}
	
	/**
	 * Gets the layer key rule.
	 *
	 * @return the layer key rule
	 */
	public ParserRule get_LayerKeyRule() {
		return get_LayerKeyAccess().getRule();
	}
	
	//_DoKey:
	/**
	 * Gets the do key access.
	 *
	 * @return the do key access
	 */
	//    "do" | "invoke";
	public _DoKeyElements get_DoKeyAccess() {
		return p_DoKey;
	}
	
	/**
	 * Gets the do key rule.
	 *
	 * @return the do key rule
	 */
	public ParserRule get_DoKeyRule() {
		return get_DoKeyAccess().getRule();
	}
	
	//_VarOrConstKey:
	/**
	 * Gets the var or const key access.
	 *
	 * @return the var or const key access
	 */
	//    "var" | "const" | "let" | "arg" ;
	public _VarOrConstKeyElements get_VarOrConstKeyAccess() {
		return p_VarOrConstKey;
	}
	
	/**
	 * Gets the var or const key rule.
	 *
	 * @return the var or const key rule
	 */
	public ParserRule get_VarOrConstKeyRule() {
		return get_VarOrConstKeyAccess().getRule();
	}
	
	//_ReflexKey:
	/**
	 * Gets the reflex key access.
	 *
	 * @return the reflex key access
	 */
	//    "init" | "reflex" | "aspect";
	public _ReflexKeyElements get_ReflexKeyAccess() {
		return p_ReflexKey;
	}
	
	/**
	 * Gets the reflex key rule.
	 *
	 * @return the reflex key rule
	 */
	public ParserRule get_ReflexKeyRule() {
		return get_ReflexKeyAccess().getRule();
	}
	
	//_AssignmentKey:
	/**
	 * Gets the assignment key access.
	 *
	 * @return the assignment key access
	 */
	//    "<-" | "<<" | '>' '>' | "<<+" | '>''>-' | "+<-" | "<+" | ">-" ;
	public _AssignmentKeyElements get_AssignmentKeyAccess() {
		return p_AssignmentKey;
	}
	
	/**
	 * Gets the assignment key rule.
	 *
	 * @return the assignment key rule
	 */
	public ParserRule get_AssignmentKeyRule() {
		return get_AssignmentKeyAccess().getRule();
	}
	
	//    /**
	// * Parameters and arguments
	// */
	////Parameters:
	////    {Parameters} (params=ParameterList)?;
	//ActionArguments:
	/**
	 * Gets the action arguments access.
	 *
	 * @return the action arguments access
	 */
	//    args+=ArgumentDefinition (',' args+=ArgumentDefinition)*;
	public ActionArgumentsElements getActionArgumentsAccess() {
		return pActionArguments;
	}
	
	/**
	 * Gets the action arguments rule.
	 *
	 * @return the action arguments rule
	 */
	public ParserRule getActionArgumentsRule() {
		return getActionArgumentsAccess().getRule();
	}
	
	//ArgumentDefinition:
	/**
	 * Gets the argument definition access.
	 *
	 * @return the argument definition access
	 */
	//    type=(TypeRef) name=Valid_ID ('<-' default=Expression)?;
	public ArgumentDefinitionElements getArgumentDefinitionAccess() {
		return pArgumentDefinition;
	}
	
	/**
	 * Gets the argument definition rule.
	 *
	 * @return the argument definition rule
	 */
	public ParserRule getArgumentDefinitionRule() {
		return getArgumentDefinitionAccess().getRule();
	}
	
	//    /**
	// * Facets
	// */
	//Facet:
	/**
	 * Gets the facet access.
	 *
	 * @return the facet access
	 */
	//    ActionFacet | DefinitionFacet  | ClassicFacet | TypeFacet | VarFacet | FunctionFacet  ;
	public FacetElements getFacetAccess() {
		return pFacet;
	}
	
	/**
	 * Gets the facet rule.
	 *
	 * @return the facet rule
	 */
	public ParserRule getFacetRule() {
		return getFacetAccess().getRule();
	}
	
	//FirstFacetKey:
	/**
	 * Gets the first facet key access.
	 *
	 * @return the first facet key access
	 */
	//    DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | VarFacetKey | ActionFacetKey | ClassicFacetKey ;
	public FirstFacetKeyElements getFirstFacetKeyAccess() {
		return pFirstFacetKey;
	}
	
	/**
	 * Gets the first facet key rule.
	 *
	 * @return the first facet key rule
	 */
	public ParserRule getFirstFacetKeyRule() {
		return getFirstFacetKeyAccess().getRule();
	}
	
	/**
	 * Gets the classic facet key access.
	 *
	 * @return the classic facet key access
	 */
	//ClassicFacetKey: (ID ':');
	public ClassicFacetKeyElements getClassicFacetKeyAccess() {
		return pClassicFacetKey;
	}
	
	/**
	 * Gets the classic facet key rule.
	 *
	 * @return the classic facet key rule
	 */
	public ParserRule getClassicFacetKeyRule() {
		return getClassicFacetKeyAccess().getRule();
	}
	
	//DefinitionFacetKey:
	/**
	 * Gets the definition facet key access.
	 *
	 * @return the definition facet key access
	 */
	//    "name:" | "returns:" ;
	public DefinitionFacetKeyElements getDefinitionFacetKeyAccess() {
		return pDefinitionFacetKey;
	}
	
	/**
	 * Gets the definition facet key rule.
	 *
	 * @return the definition facet key rule
	 */
	public ParserRule getDefinitionFacetKeyRule() {
		return getDefinitionFacetKeyAccess().getRule();
	}
	
	// /*| "var:" */
	//TypeFacetKey:
	/**
	 * Gets the type facet key access.
	 *
	 * @return the type facet key access
	 */
	//    ("as:" | "of:" | "parent:" | "species:"|"type:");
	public TypeFacetKeyElements getTypeFacetKeyAccess() {
		return pTypeFacetKey;
	}
	
	/**
	 * Gets the type facet key rule.
	 *
	 * @return the type facet key rule
	 */
	public ParserRule getTypeFacetKeyRule() {
		return getTypeFacetKeyAccess().getRule();
	}
	
	//SpecialFacetKey:
	//    'data:' | 'when'':' | "const:" | "value:" | "topology:" | "item:" | "init:" | "message:" | "control:" | "layout:" |
	/**
	 * Gets the special facet key access.
	 *
	 * @return the special facet key access
	 */
	//    "environment:" | 'text:' | 'image:' | 'using:' | "parameter:" | "aspect:" | "light:";
	public SpecialFacetKeyElements getSpecialFacetKeyAccess() {
		return pSpecialFacetKey;
	}
	
	/**
	 * Gets the special facet key rule.
	 *
	 * @return the special facet key rule
	 */
	public ParserRule getSpecialFacetKeyRule() {
		return getSpecialFacetKeyAccess().getRule();
	}
	
	//ActionFacetKey:
	/**
	 * Gets the action facet key access.
	 *
	 * @return the action facet key access
	 */
	//    "action:" | "on_change:";
	public ActionFacetKeyElements getActionFacetKeyAccess() {
		return pActionFacetKey;
	}
	
	/**
	 * Gets the action facet key rule.
	 *
	 * @return the action facet key rule
	 */
	public ParserRule getActionFacetKeyRule() {
		return getActionFacetKeyAccess().getRule();
	}
	
	//VarFacetKey:
	/**
	 * Gets the var facet key access.
	 *
	 * @return the var facet key access
	 */
	//    "var:";
	public VarFacetKeyElements getVarFacetKeyAccess() {
		return pVarFacetKey;
	}
	
	/**
	 * Gets the var facet key rule.
	 *
	 * @return the var facet key rule
	 */
	public ParserRule getVarFacetKeyRule() {
		return getVarFacetKeyAccess().getRule();
	}
	
	//ClassicFacet returns Facet:
	/**
	 * Gets the classic facet access.
	 *
	 * @return the classic facet access
	 */
	//    (key=ClassicFacetKey | key='<-' | key=SpecialFacetKey) expr=Expression;
	public ClassicFacetElements getClassicFacetAccess() {
		return pClassicFacet;
	}
	
	/**
	 * Gets the classic facet rule.
	 *
	 * @return the classic facet rule
	 */
	public ParserRule getClassicFacetRule() {
		return getClassicFacetAccess().getRule();
	}
	
	//DefinitionFacet returns Facet:
	/**
	 * Gets the definition facet access.
	 *
	 * @return the definition facet access
	 */
	//    ((-> key=DefinitionFacetKey) name=(Valid_ID | STRING));
	public DefinitionFacetElements getDefinitionFacetAccess() {
		return pDefinitionFacet;
	}
	
	/**
	 * Gets the definition facet rule.
	 *
	 * @return the definition facet rule
	 */
	public ParserRule getDefinitionFacetRule() {
		return getDefinitionFacetAccess().getRule();
	}
	
	//FunctionFacet returns Facet:
	//    key = '->' (=>(expr= Expression) | ('{' expr=Expression '}'))
	/**
	 * Gets the function facet access.
	 *
	 * @return the function facet access
	 */
	//;
	public FunctionFacetElements getFunctionFacetAccess() {
		return pFunctionFacet;
	}
	
	/**
	 * Gets the function facet rule.
	 *
	 * @return the function facet rule
	 */
	public ParserRule getFunctionFacetRule() {
		return getFunctionFacetAccess().getRule();
	}
	
	//TypeFacet returns Facet:
	/**
	 * Gets the type facet access.
	 *
	 * @return the type facet access
	 */
	//    key=TypeFacetKey (->(expr=TypeRef) | expr= Expression);
	public TypeFacetElements getTypeFacetAccess() {
		return pTypeFacet;
	}
	
	/**
	 * Gets the type facet rule.
	 *
	 * @return the type facet rule
	 */
	public ParserRule getTypeFacetRule() {
		return getTypeFacetAccess().getRule();
	}
	
	//ActionFacet returns Facet:
	/**
	 * Gets the action facet access.
	 *
	 * @return the action facet access
	 */
	//    key=ActionFacetKey (expr=ActionRef | block=Block);
	public ActionFacetElements getActionFacetAccess() {
		return pActionFacet;
	}
	
	/**
	 * Gets the action facet rule.
	 *
	 * @return the action facet rule
	 */
	public ParserRule getActionFacetRule() {
		return getActionFacetAccess().getRule();
	}
	
	//VarFacet returns Facet:
	/**
	 * Gets the var facet access.
	 *
	 * @return the var facet access
	 */
	//    key= VarFacetKey expr=VariableRef;
	public VarFacetElements getVarFacetAccess() {
		return pVarFacet;
	}
	
	/**
	 * Gets the var facet rule.
	 *
	 * @return the var facet rule
	 */
	public ParserRule getVarFacetRule() {
		return getVarFacetAccess().getRule();
	}
	
	//    /**
	// * Blocks. An ordered list of statements inside curly brackets
	// */
	//Block:
	/**
	 * Gets the block access.
	 *
	 * @return the block access
	 */
	//    {Block} '{' ((statements+=Statement)* '}');
	public BlockElements getBlockAccess() {
		return pBlock;
	}
	
	/**
	 * Gets the block rule.
	 *
	 * @return the block rule
	 */
	public ParserRule getBlockRule() {
		return getBlockAccess().getRule();
	}
	
	//    /**
	// * Expressions
	// */
	//Expression:
	/**
	 * Gets the expression access.
	 *
	 * @return the expression access
	 */
	//    ArgumentPair | Pair ;
	public ExpressionElements getExpressionAccess() {
		return pExpression;
	}
	
	/**
	 * Gets the expression rule.
	 *
	 * @return the expression rule
	 */
	public ParserRule getExpressionRule() {
		return getExpressionAccess().getRule();
	}
	
	//BinaryOperator returns Expression:
	//    Or | And | Cast | Comparison | Addition | Multiplication | Exponentiation | Binary | Pair | Unit
	/**
	 * Gets the binary operator access.
	 *
	 * @return the binary operator access
	 */
	//;
	public BinaryOperatorElements getBinaryOperatorAccess() {
		return pBinaryOperator;
	}
	
	/**
	 * Gets the binary operator rule.
	 *
	 * @return the binary operator rule
	 */
	public ParserRule getBinaryOperatorRule() {
		return getBinaryOperatorAccess().getRule();
	}
	
	//ArgumentPair:
	/**
	 * Gets the argument pair access.
	 *
	 * @return the argument pair access
	 */
	//    => (op=(Valid_ID) '::' | op=(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey) ':')? right=Pair;
	public ArgumentPairElements getArgumentPairAccess() {
		return pArgumentPair;
	}
	
	/**
	 * Gets the argument pair rule.
	 *
	 * @return the argument pair rule
	 */
	public ParserRule getArgumentPairRule() {
		return getArgumentPairAccess().getRule();
	}
	
	//Pair returns Expression:
	//    If
	//    ({BinaryOperator.left=current}
	//    op='::'
	/**
	 * Gets the pair access.
	 *
	 * @return the pair access
	 */
	//    right=If)?;
	public PairElements getPairAccess() {
		return pPair;
	}
	
	/**
	 * Gets the pair rule.
	 *
	 * @return the pair rule
	 */
	public ParserRule getPairRule() {
		return getPairAccess().getRule();
	}
	
	//If returns Expression:
	//    Or
	//    ({If.left=current}
	//    op='?'
	//    right=Or
	//    (':'
	/**
	 * Gets the if access.
	 *
	 * @return the if access
	 */
	//    ifFalse=Or))?;
	public IfElements getIfAccess() {
		return pIf;
	}
	
	/**
	 * Gets the if rule.
	 *
	 * @return the if rule
	 */
	public ParserRule getIfRule() {
		return getIfAccess().getRule();
	}
	
	//Or returns Expression:
	//    And
	//    ({BinaryOperator.left=current}
	//    op='or'
	/**
	 * Gets the or access.
	 *
	 * @return the or access
	 */
	//    right=And)*;
	public OrElements getOrAccess() {
		return pOr;
	}
	
	/**
	 * Gets the or rule.
	 *
	 * @return the or rule
	 */
	public ParserRule getOrRule() {
		return getOrAccess().getRule();
	}
	
	//And returns Expression:
	//    Cast
	//    ({BinaryOperator.left=current}
	//    op='and'
	/**
	 * Gets the and access.
	 *
	 * @return the and access
	 */
	//    right=Cast)*;
	public AndElements getAndAccess() {
		return pAnd;
	}
	
	/**
	 * Gets the and rule.
	 *
	 * @return the and rule
	 */
	public ParserRule getAndRule() {
		return getAndAccess().getRule();
	}
	
	//Cast returns Expression:
	//    Comparison
	//    (({BinaryOperator.left = current}
	//        op='as'
	//) ((right= TypeRef) | ('(' right=TypeRef ')') ))?
	/**
	 * Gets the cast access.
	 *
	 * @return the cast access
	 */
	//;
	public CastElements getCastAccess() {
		return pCast;
	}
	
	/**
	 * Gets the cast rule.
	 *
	 * @return the cast rule
	 */
	public ParserRule getCastRule() {
		return getCastAccess().getRule();
	}
	
	//Comparison returns Expression:
	//    Addition
	//    (({BinaryOperator.left=current}
	//    op=('!=' | '=' | '>=' | '<=' | '<' | '>'))
	/**
	 * Gets the comparison access.
	 *
	 * @return the comparison access
	 */
	//    right=Addition)?;
	public ComparisonElements getComparisonAccess() {
		return pComparison;
	}
	
	/**
	 * Gets the comparison rule.
	 *
	 * @return the comparison rule
	 */
	public ParserRule getComparisonRule() {
		return getComparisonAccess().getRule();
	}
	
	//Addition returns Expression:
	//    Multiplication
	//    (({BinaryOperator.left=current} op=('+' | '-'))
	/**
	 * Gets the addition access.
	 *
	 * @return the addition access
	 */
	//    right=Multiplication)*;
	public AdditionElements getAdditionAccess() {
		return pAddition;
	}
	
	/**
	 * Gets the addition rule.
	 *
	 * @return the addition rule
	 */
	public ParserRule getAdditionRule() {
		return getAdditionAccess().getRule();
	}
	
	//Multiplication returns Expression:
	//    Exponentiation
	/**
	 * Gets the multiplication access.
	 *
	 * @return the multiplication access
	 */
	//    (({BinaryOperator.left=current} op=('*' | '/' )) right=Exponentiation)*;
	public MultiplicationElements getMultiplicationAccess() {
		return pMultiplication;
	}
	
	/**
	 * Gets the multiplication rule.
	 *
	 * @return the multiplication rule
	 */
	public ParserRule getMultiplicationRule() {
		return getMultiplicationAccess().getRule();
	}
	
	//Exponentiation returns Expression:
	//    Binary
	/**
	 * Gets the exponentiation access.
	 *
	 * @return the exponentiation access
	 */
	//    (({BinaryOperator.left=current} op=('^')) right=Binary)*;
	public ExponentiationElements getExponentiationAccess() {
		return pExponentiation;
	}
	
	/**
	 * Gets the exponentiation rule.
	 *
	 * @return the exponentiation rule
	 */
	public ParserRule getExponentiationRule() {
		return getExponentiationAccess().getRule();
	}
	
	//Binary returns Expression:
	//    Unit
	/**
	 * Gets the binary access.
	 *
	 * @return the binary access
	 */
	//    (({BinaryOperator.left=current} op=(Valid_ID)) right=Unit)*;
	public BinaryElements getBinaryAccess() {
		return pBinary;
	}
	
	/**
	 * Gets the binary rule.
	 *
	 * @return the binary rule
	 */
	public ParserRule getBinaryRule() {
		return getBinaryAccess().getRule();
	}
	
	//Unit returns Expression:
	//    Unary
	/**
	 * Gets the unit access.
	 *
	 * @return the unit access
	 */
	//    (({Unit.left=current} op=('°'|"#")) right=UnitRef)?;
	public UnitElements getUnitAccess() {
		return pUnit;
	}
	
	/**
	 * Gets the unit rule.
	 *
	 * @return the unit rule
	 */
	public ParserRule getUnitRule() {
		return getUnitAccess().getRule();
	}
	
	//Unary returns Expression:
	//    Access |
	//    {Unary} ((op=('°'|'#') right=UnitRef) | (op=('-' | '!' | 'my' | 'the' | 'not')
	/**
	 * Gets the unary access.
	 *
	 * @return the unary access
	 */
	//    right=Unary));
	public UnaryElements getUnaryAccess() {
		return pUnary;
	}
	
	/**
	 * Gets the unary rule.
	 *
	 * @return the unary rule
	 */
	public ParserRule getUnaryRule() {
		return getUnaryAccess().getRule();
	}
	
	//Access returns Expression:
	//    Primary ({Access.left = current} (
	//        (op='[' right=ExpressionList? ']') | (op="." right=(AbstractRef|StringLiteral))
	/**
	 * Gets the access access.
	 *
	 * @return the access access
	 */
	//    ))*;
	public AccessElements getAccessAccess() {
		return pAccess;
	}
	
	/**
	 * Gets the access rule.
	 *
	 * @return the access rule
	 */
	public ParserRule getAccessRule() {
		return getAccessAccess().getRule();
	}
	
	//Primary returns Expression:
	//    TerminalExpression |
	//    AbstractRef |
	//    '(' ExpressionList ')' |
	//    '[' {Array} exprs=ExpressionList? ']' |
	/**
	 * Gets the primary access.
	 *
	 * @return the primary access
	 */
	//    '{' {Point} left=Expression op=',' right=Expression (',' z=Expression)? '}';
	public PrimaryElements getPrimaryAccess() {
		return pPrimary;
	}
	
	/**
	 * Gets the primary rule.
	 *
	 * @return the primary rule
	 */
	public ParserRule getPrimaryRule() {
		return getPrimaryAccess().getRule();
	}
	
	//AbstractRef returns Expression:
	/**
	 * Gets the abstract ref access.
	 *
	 * @return the abstract ref access
	 */
	//    =>Function | VariableRef;
	public AbstractRefElements getAbstractRefAccess() {
		return pAbstractRef;
	}
	
	/**
	 * Gets the abstract ref rule.
	 *
	 * @return the abstract ref rule
	 */
	public ParserRule getAbstractRefRule() {
		return getAbstractRefAccess().getRule();
	}
	
	//Function returns Expression:
	/**
	 * Gets the function access.
	 *
	 * @return the function access
	 */
	//    {Function} (left=ActionRef) (type=TypeInfo)? '(' right=ExpressionList? ')';
	public FunctionElements getFunctionAccess() {
		return pFunction;
	}
	
	/**
	 * Gets the function rule.
	 *
	 * @return the function rule
	 */
	public ParserRule getFunctionRule() {
		return getFunctionAccess().getRule();
	}
	
	//ExpressionList:
	/**
	 * Gets the expression list access.
	 *
	 * @return the expression list access
	 */
	//    (exprs+=Expression (',' exprs+=Expression)*) | (exprs+=Parameter (',' exprs+=Parameter)*);
	public ExpressionListElements getExpressionListAccess() {
		return pExpressionList;
	}
	
	/**
	 * Gets the expression list rule.
	 *
	 * @return the expression list rule
	 */
	public ParserRule getExpressionListRule() {
		return getExpressionListAccess().getRule();
	}
	
	//Parameter returns Expression:
	//    {Parameter} ((builtInFacetKey=(DefinitionFacetKey | TypeFacetKey  | SpecialFacetKey | ActionFacetKey | VarFacetKey)) |
	/**
	 * Gets the parameter access.
	 *
	 * @return the parameter access
	 */
	//    (left=VariableRef ':')) right=Expression;
	public ParameterElements getParameterAccess() {
		return pParameter;
	}
	
	/**
	 * Gets the parameter rule.
	 *
	 * @return the parameter rule
	 */
	public ParserRule getParameterRule() {
		return getParameterAccess().getRule();
	}
	
	//UnitRef returns Expression:
	/**
	 * Gets the unit ref access.
	 *
	 * @return the unit ref access
	 */
	//    {UnitName} ref=[UnitFakeDefinition|ID];
	public UnitRefElements getUnitRefAccess() {
		return pUnitRef;
	}
	
	/**
	 * Gets the unit ref rule.
	 *
	 * @return the unit ref rule
	 */
	public ParserRule getUnitRefRule() {
		return getUnitRefAccess().getRule();
	}
	
	//VariableRef:
	/**
	 * Gets the variable ref access.
	 *
	 * @return the variable ref access
	 */
	//    {VariableRef} ref=[VarDefinition|Valid_ID];
	public VariableRefElements getVariableRefAccess() {
		return pVariableRef;
	}
	
	/**
	 * Gets the variable ref rule.
	 *
	 * @return the variable ref rule
	 */
	public ParserRule getVariableRefRule() {
		return getVariableRefAccess().getRule();
	}
	
	//TypeRef returns Expression:
	/**
	 * Gets the type ref access.
	 *
	 * @return the type ref access
	 */
	//     {TypeRef} (ref=[TypeDefinition|ID] parameter=TypeInfo?) | {TypeRef} ("species" parameter=TypeInfo) ;
	public TypeRefElements getTypeRefAccess() {
		return pTypeRef;
	}
	
	/**
	 * Gets the type ref rule.
	 *
	 * @return the type ref rule
	 */
	public ParserRule getTypeRefRule() {
		return getTypeRefAccess().getRule();
	}
	
	//TypeInfo:
	//    ('<' first=TypeRef ("," second=TypeRef)? ->'>')
	/**
	 * Gets the type info access.
	 *
	 * @return the type info access
	 */
	//;
	public TypeInfoElements getTypeInfoAccess() {
		return pTypeInfo;
	}
	
	/**
	 * Gets the type info rule.
	 *
	 * @return the type info rule
	 */
	public ParserRule getTypeInfoRule() {
		return getTypeInfoAccess().getRule();
	}
	
	//SkillRef returns Expression:
	/**
	 * Gets the skill ref access.
	 *
	 * @return the skill ref access
	 */
	//    {SkillRef} ref=[SkillFakeDefinition|ID];
	public SkillRefElements getSkillRefAccess() {
		return pSkillRef;
	}
	
	/**
	 * Gets the skill ref rule.
	 *
	 * @return the skill ref rule
	 */
	public ParserRule getSkillRefRule() {
		return getSkillRefAccess().getRule();
	}
	
	//ActionRef returns Expression:
	/**
	 * Gets the action ref access.
	 *
	 * @return the action ref access
	 */
	//    {ActionRef} ref=[ActionDefinition|Valid_ID];
	public ActionRefElements getActionRefAccess() {
		return pActionRef;
	}
	
	/**
	 * Gets the action ref rule.
	 *
	 * @return the action ref rule
	 */
	public ParserRule getActionRefRule() {
		return getActionRefAccess().getRule();
	}
	
	//EquationRef returns Expression:
	/**
	 * Gets the equation ref access.
	 *
	 * @return the equation ref access
	 */
	//    {EquationRef} ref=[EquationDefinition|Valid_ID];
	public EquationRefElements getEquationRefAccess() {
		return pEquationRef;
	}
	
	/**
	 * Gets the equation ref rule.
	 *
	 * @return the equation ref rule
	 */
	public ParserRule getEquationRefRule() {
		return getEquationRefAccess().getRule();
	}
	
	//GamlDefinition:
	/**
	 * Gets the gaml definition access.
	 *
	 * @return the gaml definition access
	 */
	//    TypeDefinition | VarDefinition | UnitFakeDefinition | SkillFakeDefinition | ActionDefinition | EquationDefinition;
	public GamlDefinitionElements getGamlDefinitionAccess() {
		return pGamlDefinition;
	}
	
	/**
	 * Gets the gaml definition rule.
	 *
	 * @return the gaml definition rule
	 */
	public ParserRule getGamlDefinitionRule() {
		return getGamlDefinitionAccess().getRule();
	}
	
	//EquationDefinition:
	/**
	 * Gets the equation definition access.
	 *
	 * @return the equation definition access
	 */
	//    S_Equations | EquationFakeDefinition;
	public EquationDefinitionElements getEquationDefinitionAccess() {
		return pEquationDefinition;
	}
	
	/**
	 * Gets the equation definition rule.
	 *
	 * @return the equation definition rule
	 */
	public ParserRule getEquationDefinitionRule() {
		return getEquationDefinitionAccess().getRule();
	}
	
	//TypeDefinition:
	/**
	 * Gets the type definition access.
	 *
	 * @return the type definition access
	 */
	//    S_Species | TypeFakeDefinition;
	public TypeDefinitionElements getTypeDefinitionAccess() {
		return pTypeDefinition;
	}
	
	/**
	 * Gets the type definition rule.
	 *
	 * @return the type definition rule
	 */
	public ParserRule getTypeDefinitionRule() {
		return getTypeDefinitionAccess().getRule();
	}
	
	//VarDefinition:
	/**
	 * Gets the var definition access.
	 *
	 * @return the var definition access
	 */
	//    =>S_Declaration | (Model | ArgumentDefinition | DefinitionFacet | VarFakeDefinition | Import | S_Experiment);
	public VarDefinitionElements getVarDefinitionAccess() {
		return pVarDefinition;
	}
	
	/**
	 * Gets the var definition rule.
	 *
	 * @return the var definition rule
	 */
	public ParserRule getVarDefinitionRule() {
		return getVarDefinitionAccess().getRule();
	}
	
	//ActionDefinition:
	/**
	 * Gets the action definition access.
	 *
	 * @return the action definition access
	 */
	//    S_Action | ActionFakeDefinition | S_Definition | TypeDefinition;
	public ActionDefinitionElements getActionDefinitionAccess() {
		return pActionDefinition;
	}
	
	/**
	 * Gets the action definition rule.
	 *
	 * @return the action definition rule
	 */
	public ParserRule getActionDefinitionRule() {
		return getActionDefinitionAccess().getRule();
	}
	
	//    // Fake Definitions produced by the global scope provider
	//UnitFakeDefinition:
	/**
	 * Gets the unit fake definition access.
	 *
	 * @return the unit fake definition access
	 */
	//    '**unit*' name=ID;
	public UnitFakeDefinitionElements getUnitFakeDefinitionAccess() {
		return pUnitFakeDefinition;
	}
	
	/**
	 * Gets the unit fake definition rule.
	 *
	 * @return the unit fake definition rule
	 */
	public ParserRule getUnitFakeDefinitionRule() {
		return getUnitFakeDefinitionAccess().getRule();
	}
	
	//TypeFakeDefinition:
	/**
	 * Gets the type fake definition access.
	 *
	 * @return the type fake definition access
	 */
	//    '**type*' name=ID;
	public TypeFakeDefinitionElements getTypeFakeDefinitionAccess() {
		return pTypeFakeDefinition;
	}
	
	/**
	 * Gets the type fake definition rule.
	 *
	 * @return the type fake definition rule
	 */
	public ParserRule getTypeFakeDefinitionRule() {
		return getTypeFakeDefinitionAccess().getRule();
	}
	
	//ActionFakeDefinition:
	/**
	 * Gets the action fake definition access.
	 *
	 * @return the action fake definition access
	 */
	//    '**action*' name=Valid_ID;
	public ActionFakeDefinitionElements getActionFakeDefinitionAccess() {
		return pActionFakeDefinition;
	}
	
	/**
	 * Gets the action fake definition rule.
	 *
	 * @return the action fake definition rule
	 */
	public ParserRule getActionFakeDefinitionRule() {
		return getActionFakeDefinitionAccess().getRule();
	}
	
	//SkillFakeDefinition:
	/**
	 * Gets the skill fake definition access.
	 *
	 * @return the skill fake definition access
	 */
	//    '**skill*' name=ID;
	public SkillFakeDefinitionElements getSkillFakeDefinitionAccess() {
		return pSkillFakeDefinition;
	}
	
	/**
	 * Gets the skill fake definition rule.
	 *
	 * @return the skill fake definition rule
	 */
	public ParserRule getSkillFakeDefinitionRule() {
		return getSkillFakeDefinitionAccess().getRule();
	}
	
	//VarFakeDefinition:
	/**
	 * Gets the var fake definition access.
	 *
	 * @return the var fake definition access
	 */
	//    '**var*' name=Valid_ID;
	public VarFakeDefinitionElements getVarFakeDefinitionAccess() {
		return pVarFakeDefinition;
	}
	
	/**
	 * Gets the var fake definition rule.
	 *
	 * @return the var fake definition rule
	 */
	public ParserRule getVarFakeDefinitionRule() {
		return getVarFakeDefinitionAccess().getRule();
	}
	
	//EquationFakeDefinition:
	/**
	 * Gets the equation fake definition access.
	 *
	 * @return the equation fake definition access
	 */
	//    '**equation*' name=Valid_ID;
	public EquationFakeDefinitionElements getEquationFakeDefinitionAccess() {
		return pEquationFakeDefinition;
	}
	
	/**
	 * Gets the equation fake definition rule.
	 *
	 * @return the equation fake definition rule
	 */
	public ParserRule getEquationFakeDefinitionRule() {
		return getEquationFakeDefinitionAccess().getRule();
	}
	
	//Valid_ID:
	/**
	 * Gets the valid ID access.
	 *
	 * @return the valid ID access
	 */
	//    _SpeciesKey | _DoKey | _ReflexKey | _VarOrConstKey | _1Expr_Facets_BlockOrEnd_Key | _EquationsKey | ID | _ExperimentKey;
	public Valid_IDElements getValid_IDAccess() {
		return pValid_ID;
	}
	
	/**
	 * Gets the valid ID rule.
	 *
	 * @return the valid ID rule
	 */
	public ParserRule getValid_IDRule() {
		return getValid_IDAccess().getRule();
	}
	
	//    /**
	// * Terminals
	// */
	//TerminalExpression:
	//    StringLiteral |
	//    {IntLiteral} op=INTEGER |
	//    {DoubleLiteral} op=DOUBLE |
	//    /*{ColorLiteral} op=COLOR |*/
	//    {BooleanLiteral} op=BOOLEAN |
	/**
	 * Gets the terminal expression access.
	 *
	 * @return the terminal expression access
	 */
	//    {ReservedLiteral} op=KEYWORD;
	public TerminalExpressionElements getTerminalExpressionAccess() {
		return pTerminalExpression;
	}
	
	/**
	 * Gets the terminal expression rule.
	 *
	 * @return the terminal expression rule
	 */
	public ParserRule getTerminalExpressionRule() {
		return getTerminalExpressionAccess().getRule();
	}
	
	//StringLiteral:
	//    op=STRING
	/**
	 * Gets the string literal access.
	 *
	 * @return the string literal access
	 */
	//;
	public StringLiteralElements getStringLiteralAccess() {
		return pStringLiteral;
	}
	
	/**
	 * Gets the string literal rule.
	 *
	 * @return the string literal rule
	 */
	public ParserRule getStringLiteralRule() {
		return getStringLiteralAccess().getRule();
	}
	
	//terminal KEYWORD:
	/**
	 * Gets the KEYWORD rule.
	 *
	 * @return the KEYWORD rule
	 */
	//    'each' | 'self' | 'myself' | 'nil' | 'super' ;
	public TerminalRule getKEYWORDRule() {
		return tKEYWORD;
	}
	
	//terminal INTEGER:
	/**
	 * Gets the INTEGER rule.
	 *
	 * @return the INTEGER rule
	 */
	//    '0' | ('1'..'9' ('0'..'9')*);
	public TerminalRule getINTEGERRule() {
		return tINTEGER;
	}
	
	//terminal BOOLEAN:
	/**
	 * Gets the BOOLEAN rule.
	 *
	 * @return the BOOLEAN rule
	 */
	//    'true' | 'false';
	public TerminalRule getBOOLEANRule() {
		return tBOOLEAN;
	}
	
	//terminal ID:
	/**
	 * Gets the ID rule.
	 *
	 * @return the ID rule
	 */
	//    ('a'..'z' | 'A'..'Z' | '_' | '$') ('a'..'z' | 'A'..'Z' | '_' | '$' | '0'..'9')*;
	public TerminalRule getIDRule() {
		return tID;
	}
	
	////terminal COLOR:
	////    '#' ('0'..'9' | 'A'..'F')+;
	//terminal DOUBLE:
	//    '1'..'9' ('0'..'9')* ('.' '0'..'9'+)? (('E' | 'e') ('+' | '-')? '0'..'9'+)? | '0' ('.' '0'..'9'+)? (('E' | 'e') ('+' |
	/**
	 * Gets the DOUBLE rule.
	 *
	 * @return the DOUBLE rule
	 */
	//'-')? '0'..'9'+)?;
	public TerminalRule getDOUBLERule() {
		return tDOUBLE;
	}
	
	//terminal STRING:
	//    '"' ('\\' ('b' | 't' | 'n' | 'f' | 'r' | 'u' | '"' | '\\') | !('\\' | '"'))* '"' | "'" ('\\' ('b' | 't' | 'n' | 'f' |
	/**
	 * Gets the STRING rule.
	 *
	 * @return the STRING rule
	 */
	//    'r' | 'u' | "'" | '\\') | !('\\' | "'"))* "'";
	public TerminalRule getSTRINGRule() {
		return tSTRING;
	}
	
	//terminal ML_COMMENT:
	/**
	 * Gets the m L COMMENT rule.
	 *
	 * @return the m L COMMENT rule
	 */
	//    '/*'->'*/';
	public TerminalRule getML_COMMENTRule() {
		return tML_COMMENT;
	}
	
	//terminal SL_COMMENT:
	/**
	 * Gets the s L COMMENT rule.
	 *
	 * @return the s L COMMENT rule
	 */
	//    '//' !('\n' | '\r')* ('\r'? '\n')?;
	public TerminalRule getSL_COMMENTRule() {
		return tSL_COMMENT;
	}
	
	//terminal WS:
	/**
	 * Gets the WS rule.
	 *
	 * @return the WS rule
	 */
	//    (' ' | '\t' | '\r' | '\n')+;
	public TerminalRule getWSRule() {
		return tWS;
	}
	
	//terminal ANY_OTHER:
	/**
	 * Gets the AN Y OTHER rule.
	 *
	 * @return the AN Y OTHER rule
	 */
	//    .;
	public TerminalRule getANY_OTHERRule() {
		return tANY_OTHER;
	}
}
