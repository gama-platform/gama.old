/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */

package msi.gama.lang.gaml.services;

import com.google.inject.Singleton;
import com.google.inject.Inject;

import org.eclipse.xtext.*;
import org.eclipse.xtext.service.GrammarProvider;
import org.eclipse.xtext.service.AbstractElementFinder.*;


@Singleton
public class GamlGrammarAccess extends AbstractGrammarElementFinder {
	
	
	public class ModelElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Model");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword cModelKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cNameFQNParserRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		private final Assignment cImportsAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cImportsImportParserRuleCall_2_0 = (RuleCall)cImportsAssignment_2.eContents().get(0);
		private final Assignment cGamlAssignment_3 = (Assignment)cGroup.eContents().get(3);
		private final RuleCall cGamlGamlLangDefParserRuleCall_3_0 = (RuleCall)cGamlAssignment_3.eContents().get(0);
		private final Assignment cStatementsAssignment_4 = (Assignment)cGroup.eContents().get(4);
		private final RuleCall cStatementsStatementParserRuleCall_4_0 = (RuleCall)cStatementsAssignment_4.eContents().get(0);
		
		//Model:
		//	"model" name=FQN imports+=Import* gaml=GamlLangDef? statements+=Statement*;
		public ParserRule getRule() { return rule; }

		//"model" name=FQN imports+=Import* gaml=GamlLangDef? statements+=Statement*
		public Group getGroup() { return cGroup; }

		//"model"
		public Keyword getModelKeyword_0() { return cModelKeyword_0; }

		//name=FQN
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }

		//FQN
		public RuleCall getNameFQNParserRuleCall_1_0() { return cNameFQNParserRuleCall_1_0; }

		//imports+=Import*
		public Assignment getImportsAssignment_2() { return cImportsAssignment_2; }

		//Import
		public RuleCall getImportsImportParserRuleCall_2_0() { return cImportsImportParserRuleCall_2_0; }

		//gaml=GamlLangDef?
		public Assignment getGamlAssignment_3() { return cGamlAssignment_3; }

		//GamlLangDef
		public RuleCall getGamlGamlLangDefParserRuleCall_3_0() { return cGamlGamlLangDefParserRuleCall_3_0; }

		//statements+=Statement*
		public Assignment getStatementsAssignment_4() { return cStatementsAssignment_4; }

		//Statement
		public RuleCall getStatementsStatementParserRuleCall_4_0() { return cStatementsStatementParserRuleCall_4_0; }
	}

	public class ImportElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Import");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword cImportKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cImportURIAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cImportURISTRINGTerminalRuleCall_1_0 = (RuleCall)cImportURIAssignment_1.eContents().get(0);
		
		//// feature must be named importURI
		//Import:
		//	"import" importURI=STRING;
		public ParserRule getRule() { return rule; }

		//"import" importURI=STRING
		public Group getGroup() { return cGroup; }

		//"import"
		public Keyword getImportKeyword_0() { return cImportKeyword_0; }

		//importURI=STRING
		public Assignment getImportURIAssignment_1() { return cImportURIAssignment_1; }

		//STRING
		public RuleCall getImportURISTRINGTerminalRuleCall_1_0() { return cImportURISTRINGTerminalRuleCall_1_0; }
	}

	public class ImportedFQNElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "ImportedFQN");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cFQNParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Keyword cFullStopKeyword_1_0 = (Keyword)cGroup_1.eContents().get(0);
		private final Keyword cAsteriskKeyword_1_1 = (Keyword)cGroup_1.eContents().get(1);
		
		//ImportedFQN:
		//	FQN ("." "*")?;
		public ParserRule getRule() { return rule; }

		//FQN ("." "*")?
		public Group getGroup() { return cGroup; }

		//FQN
		public RuleCall getFQNParserRuleCall_0() { return cFQNParserRuleCall_0; }

		//("." "*")?
		public Group getGroup_1() { return cGroup_1; }

		//"."
		public Keyword getFullStopKeyword_1_0() { return cFullStopKeyword_1_0; }

		//"*"
		public Keyword getAsteriskKeyword_1_1() { return cAsteriskKeyword_1_1; }
	}

	public class GamlLangDefElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "GamlLangDef");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword c_gamlKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Keyword cLeftCurlyBracketKeyword_1 = (Keyword)cGroup.eContents().get(1);
		private final Alternatives cAlternatives_2 = (Alternatives)cGroup.eContents().get(2);
		private final Assignment cKAssignment_2_0 = (Assignment)cAlternatives_2.eContents().get(0);
		private final RuleCall cKDefKeywordParserRuleCall_2_0_0 = (RuleCall)cKAssignment_2_0.eContents().get(0);
		private final Assignment cFAssignment_2_1 = (Assignment)cAlternatives_2.eContents().get(1);
		private final RuleCall cFDefFacetParserRuleCall_2_1_0 = (RuleCall)cFAssignment_2_1.eContents().get(0);
		private final Assignment cBAssignment_2_2 = (Assignment)cAlternatives_2.eContents().get(2);
		private final RuleCall cBDefBinaryOpParserRuleCall_2_2_0 = (RuleCall)cBAssignment_2_2.eContents().get(0);
		private final Assignment cRAssignment_2_3 = (Assignment)cAlternatives_2.eContents().get(3);
		private final RuleCall cRDefReservedParserRuleCall_2_3_0 = (RuleCall)cRAssignment_2_3.eContents().get(0);
		private final Assignment cUAssignment_2_4 = (Assignment)cAlternatives_2.eContents().get(4);
		private final RuleCall cUDefUnitParserRuleCall_2_4_0 = (RuleCall)cUAssignment_2_4.eContents().get(0);
		private final Keyword cRightCurlyBracketKeyword_3 = (Keyword)cGroup.eContents().get(3);
		
		//GamlLangDef:
		//	"_gaml" "{" (k+=DefKeyword | f+=DefFacet | b+=DefBinaryOp | r+=DefReserved | u+=DefUnit)+ "}";
		public ParserRule getRule() { return rule; }

		//"_gaml" "{" (k+=DefKeyword | f+=DefFacet | b+=DefBinaryOp | r+=DefReserved | u+=DefUnit)+ "}"
		public Group getGroup() { return cGroup; }

		//"_gaml"
		public Keyword get_gamlKeyword_0() { return c_gamlKeyword_0; }

		//"{"
		public Keyword getLeftCurlyBracketKeyword_1() { return cLeftCurlyBracketKeyword_1; }

		//(k+=DefKeyword | f+=DefFacet | b+=DefBinaryOp | r+=DefReserved | u+=DefUnit)+
		public Alternatives getAlternatives_2() { return cAlternatives_2; }

		//k+=DefKeyword
		public Assignment getKAssignment_2_0() { return cKAssignment_2_0; }

		//DefKeyword
		public RuleCall getKDefKeywordParserRuleCall_2_0_0() { return cKDefKeywordParserRuleCall_2_0_0; }

		//f+=DefFacet
		public Assignment getFAssignment_2_1() { return cFAssignment_2_1; }

		//DefFacet
		public RuleCall getFDefFacetParserRuleCall_2_1_0() { return cFDefFacetParserRuleCall_2_1_0; }

		//b+=DefBinaryOp
		public Assignment getBAssignment_2_2() { return cBAssignment_2_2; }

		//DefBinaryOp
		public RuleCall getBDefBinaryOpParserRuleCall_2_2_0() { return cBDefBinaryOpParserRuleCall_2_2_0; }

		//r+=DefReserved
		public Assignment getRAssignment_2_3() { return cRAssignment_2_3; }

		//DefReserved
		public RuleCall getRDefReservedParserRuleCall_2_3_0() { return cRDefReservedParserRuleCall_2_3_0; }

		//u+=DefUnit
		public Assignment getUAssignment_2_4() { return cUAssignment_2_4; }

		//DefUnit
		public RuleCall getUDefUnitParserRuleCall_2_4_0() { return cUDefUnitParserRuleCall_2_4_0; }

		//"}"
		public Keyword getRightCurlyBracketKeyword_3() { return cRightCurlyBracketKeyword_3; }
	}

	public class DefKeywordElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "DefKeyword");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword c_keywordKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cNameIDTerminalRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		private final Alternatives cAlternatives_2 = (Alternatives)cGroup.eContents().get(2);
		private final Assignment cBlockAssignment_2_0 = (Assignment)cAlternatives_2.eContents().get(0);
		private final RuleCall cBlockGamlBlockParserRuleCall_2_0_0 = (RuleCall)cBlockAssignment_2_0.eContents().get(0);
		private final Keyword cSemicolonKeyword_2_1 = (Keyword)cAlternatives_2.eContents().get(1);
		
		//DefKeyword:
		//	"_keyword" name=ID (block=GamlBlock | ";");
		public ParserRule getRule() { return rule; }

		//"_keyword" name=ID (block=GamlBlock | ";")
		public Group getGroup() { return cGroup; }

		//"_keyword"
		public Keyword get_keywordKeyword_0() { return c_keywordKeyword_0; }

		//name=ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }

		//ID
		public RuleCall getNameIDTerminalRuleCall_1_0() { return cNameIDTerminalRuleCall_1_0; }

		//block=GamlBlock | ";"
		public Alternatives getAlternatives_2() { return cAlternatives_2; }

		//block=GamlBlock
		public Assignment getBlockAssignment_2_0() { return cBlockAssignment_2_0; }

		//GamlBlock
		public RuleCall getBlockGamlBlockParserRuleCall_2_0_0() { return cBlockGamlBlockParserRuleCall_2_0_0; }

		//";"
		public Keyword getSemicolonKeyword_2_1() { return cSemicolonKeyword_2_1; }
	}

	public class GamlBlockElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "GamlBlock");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword cLeftCurlyBracketKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Action cGamlBlockAction_1 = (Action)cGroup.eContents().get(1);
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		private final Keyword c_facetsKeyword_2_0 = (Keyword)cGroup_2.eContents().get(0);
		private final Keyword cLeftSquareBracketKeyword_2_1 = (Keyword)cGroup_2.eContents().get(1);
		private final Assignment cFacetsAssignment_2_2 = (Assignment)cGroup_2.eContents().get(2);
		private final CrossReference cFacetsDefFacetCrossReference_2_2_0 = (CrossReference)cFacetsAssignment_2_2.eContents().get(0);
		private final RuleCall cFacetsDefFacetIDTerminalRuleCall_2_2_0_1 = (RuleCall)cFacetsDefFacetCrossReference_2_2_0.eContents().get(1);
		private final Group cGroup_2_3 = (Group)cGroup_2.eContents().get(3);
		private final Keyword cCommaKeyword_2_3_0 = (Keyword)cGroup_2_3.eContents().get(0);
		private final Assignment cFacetsAssignment_2_3_1 = (Assignment)cGroup_2_3.eContents().get(1);
		private final CrossReference cFacetsDefFacetCrossReference_2_3_1_0 = (CrossReference)cFacetsAssignment_2_3_1.eContents().get(0);
		private final RuleCall cFacetsDefFacetIDTerminalRuleCall_2_3_1_0_1 = (RuleCall)cFacetsDefFacetCrossReference_2_3_1_0.eContents().get(1);
		private final Keyword cRightSquareBracketKeyword_2_4 = (Keyword)cGroup_2.eContents().get(4);
		private final Group cGroup_3 = (Group)cGroup.eContents().get(3);
		private final Keyword c_childrenKeyword_3_0 = (Keyword)cGroup_3.eContents().get(0);
		private final Keyword cLeftSquareBracketKeyword_3_1 = (Keyword)cGroup_3.eContents().get(1);
		private final Assignment cChildsAssignment_3_2 = (Assignment)cGroup_3.eContents().get(2);
		private final CrossReference cChildsDefKeywordCrossReference_3_2_0 = (CrossReference)cChildsAssignment_3_2.eContents().get(0);
		private final RuleCall cChildsDefKeywordIDTerminalRuleCall_3_2_0_1 = (RuleCall)cChildsDefKeywordCrossReference_3_2_0.eContents().get(1);
		private final Group cGroup_3_3 = (Group)cGroup_3.eContents().get(3);
		private final Keyword cCommaKeyword_3_3_0 = (Keyword)cGroup_3_3.eContents().get(0);
		private final Assignment cChildsAssignment_3_3_1 = (Assignment)cGroup_3_3.eContents().get(1);
		private final CrossReference cChildsDefKeywordCrossReference_3_3_1_0 = (CrossReference)cChildsAssignment_3_3_1.eContents().get(0);
		private final RuleCall cChildsDefKeywordIDTerminalRuleCall_3_3_1_0_1 = (RuleCall)cChildsDefKeywordCrossReference_3_3_1_0.eContents().get(1);
		private final Keyword cRightSquareBracketKeyword_3_4 = (Keyword)cGroup_3.eContents().get(4);
		private final Keyword cRightCurlyBracketKeyword_4 = (Keyword)cGroup.eContents().get(4);
		
		//GamlBlock:
		//	"{" {GamlBlock} ("_facets" "[" facets+=[DefFacet] ("," facets+=[DefFacet])* "]")? ("_children" "["
		//	childs+=[DefKeyword] ("," childs+=[DefKeyword])* "]")? "}";
		public ParserRule getRule() { return rule; }

		//"{" {GamlBlock} ("_facets" "[" facets+=[DefFacet] ("," facets+=[DefFacet])* "]")? ("_children" "[" childs+=[DefKeyword]
		//("," childs+=[DefKeyword])* "]")? "}"
		public Group getGroup() { return cGroup; }

		//"{"
		public Keyword getLeftCurlyBracketKeyword_0() { return cLeftCurlyBracketKeyword_0; }

		//{GamlBlock}
		public Action getGamlBlockAction_1() { return cGamlBlockAction_1; }

		//("_facets" "[" facets+=[DefFacet] ("," facets+=[DefFacet])* "]")?
		public Group getGroup_2() { return cGroup_2; }

		//"_facets"
		public Keyword get_facetsKeyword_2_0() { return c_facetsKeyword_2_0; }

		//"["
		public Keyword getLeftSquareBracketKeyword_2_1() { return cLeftSquareBracketKeyword_2_1; }

		//facets+=[DefFacet]
		public Assignment getFacetsAssignment_2_2() { return cFacetsAssignment_2_2; }

		//[DefFacet]
		public CrossReference getFacetsDefFacetCrossReference_2_2_0() { return cFacetsDefFacetCrossReference_2_2_0; }

		//ID
		public RuleCall getFacetsDefFacetIDTerminalRuleCall_2_2_0_1() { return cFacetsDefFacetIDTerminalRuleCall_2_2_0_1; }

		//("," facets+=[DefFacet])*
		public Group getGroup_2_3() { return cGroup_2_3; }

		//","
		public Keyword getCommaKeyword_2_3_0() { return cCommaKeyword_2_3_0; }

		//facets+=[DefFacet]
		public Assignment getFacetsAssignment_2_3_1() { return cFacetsAssignment_2_3_1; }

		//[DefFacet]
		public CrossReference getFacetsDefFacetCrossReference_2_3_1_0() { return cFacetsDefFacetCrossReference_2_3_1_0; }

		//ID
		public RuleCall getFacetsDefFacetIDTerminalRuleCall_2_3_1_0_1() { return cFacetsDefFacetIDTerminalRuleCall_2_3_1_0_1; }

		//"]"
		public Keyword getRightSquareBracketKeyword_2_4() { return cRightSquareBracketKeyword_2_4; }

		//("_children" "[" childs+=[DefKeyword] ("," childs+=[DefKeyword])* "]")?
		public Group getGroup_3() { return cGroup_3; }

		//"_children"
		public Keyword get_childrenKeyword_3_0() { return c_childrenKeyword_3_0; }

		//"["
		public Keyword getLeftSquareBracketKeyword_3_1() { return cLeftSquareBracketKeyword_3_1; }

		//childs+=[DefKeyword]
		public Assignment getChildsAssignment_3_2() { return cChildsAssignment_3_2; }

		//[DefKeyword]
		public CrossReference getChildsDefKeywordCrossReference_3_2_0() { return cChildsDefKeywordCrossReference_3_2_0; }

		//ID
		public RuleCall getChildsDefKeywordIDTerminalRuleCall_3_2_0_1() { return cChildsDefKeywordIDTerminalRuleCall_3_2_0_1; }

		//("," childs+=[DefKeyword])*
		public Group getGroup_3_3() { return cGroup_3_3; }

		//","
		public Keyword getCommaKeyword_3_3_0() { return cCommaKeyword_3_3_0; }

		//childs+=[DefKeyword]
		public Assignment getChildsAssignment_3_3_1() { return cChildsAssignment_3_3_1; }

		//[DefKeyword]
		public CrossReference getChildsDefKeywordCrossReference_3_3_1_0() { return cChildsDefKeywordCrossReference_3_3_1_0; }

		//ID
		public RuleCall getChildsDefKeywordIDTerminalRuleCall_3_3_1_0_1() { return cChildsDefKeywordIDTerminalRuleCall_3_3_1_0_1; }

		//"]"
		public Keyword getRightSquareBracketKeyword_3_4() { return cRightSquareBracketKeyword_3_4; }

		//"}"
		public Keyword getRightCurlyBracketKeyword_4() { return cRightCurlyBracketKeyword_4; }
	}

	public class DefFacetElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "DefFacet");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword c_facetKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cNameIDTerminalRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		private final Keyword cColonKeyword_2_0 = (Keyword)cGroup_2.eContents().get(0);
		private final Assignment cTypeAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		private final CrossReference cTypeDefReservedCrossReference_2_1_0 = (CrossReference)cTypeAssignment_2_1.eContents().get(0);
		private final RuleCall cTypeDefReservedIDTerminalRuleCall_2_1_0_1 = (RuleCall)cTypeDefReservedCrossReference_2_1_0.eContents().get(1);
		private final Group cGroup_3 = (Group)cGroup.eContents().get(3);
		private final Keyword cEqualsSignKeyword_3_0 = (Keyword)cGroup_3.eContents().get(0);
		private final Assignment cDefaultAssignment_3_1 = (Assignment)cGroup_3.eContents().get(1);
		private final RuleCall cDefaultTerminalExpressionParserRuleCall_3_1_0 = (RuleCall)cDefaultAssignment_3_1.eContents().get(0);
		private final Keyword cSemicolonKeyword_4 = (Keyword)cGroup.eContents().get(4);
		
		//DefFacet:
		//	"_facet" name=ID (":" type=[DefReserved])? ("=" default=TerminalExpression)? ";";
		public ParserRule getRule() { return rule; }

		//"_facet" name=ID (":" type=[DefReserved])? ("=" default=TerminalExpression)? ";"
		public Group getGroup() { return cGroup; }

		//"_facet"
		public Keyword get_facetKeyword_0() { return c_facetKeyword_0; }

		//name=ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }

		//ID
		public RuleCall getNameIDTerminalRuleCall_1_0() { return cNameIDTerminalRuleCall_1_0; }

		//(":" type=[DefReserved])?
		public Group getGroup_2() { return cGroup_2; }

		//":"
		public Keyword getColonKeyword_2_0() { return cColonKeyword_2_0; }

		//type=[DefReserved]
		public Assignment getTypeAssignment_2_1() { return cTypeAssignment_2_1; }

		//[DefReserved]
		public CrossReference getTypeDefReservedCrossReference_2_1_0() { return cTypeDefReservedCrossReference_2_1_0; }

		//ID
		public RuleCall getTypeDefReservedIDTerminalRuleCall_2_1_0_1() { return cTypeDefReservedIDTerminalRuleCall_2_1_0_1; }

		//("=" default=TerminalExpression)?
		public Group getGroup_3() { return cGroup_3; }

		//"="
		public Keyword getEqualsSignKeyword_3_0() { return cEqualsSignKeyword_3_0; }

		//default=TerminalExpression
		public Assignment getDefaultAssignment_3_1() { return cDefaultAssignment_3_1; }

		//TerminalExpression
		public RuleCall getDefaultTerminalExpressionParserRuleCall_3_1_0() { return cDefaultTerminalExpressionParserRuleCall_3_1_0; }

		//";"
		public Keyword getSemicolonKeyword_4() { return cSemicolonKeyword_4; }
	}

	public class DefBinaryOpElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "DefBinaryOp");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword c_binaryKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cNameIDTerminalRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		private final Keyword cSemicolonKeyword_2 = (Keyword)cGroup.eContents().get(2);
		
		//DefBinaryOp:
		//	"_binary" name=ID ";";
		public ParserRule getRule() { return rule; }

		//"_binary" name=ID ";"
		public Group getGroup() { return cGroup; }

		//"_binary"
		public Keyword get_binaryKeyword_0() { return c_binaryKeyword_0; }

		//name=ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }

		//ID
		public RuleCall getNameIDTerminalRuleCall_1_0() { return cNameIDTerminalRuleCall_1_0; }

		//";"
		public Keyword getSemicolonKeyword_2() { return cSemicolonKeyword_2; }
	}

	public class DefReservedElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "DefReserved");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword c_reservedKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cNameIDTerminalRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		private final Keyword cColonKeyword_2_0 = (Keyword)cGroup_2.eContents().get(0);
		private final Assignment cTypeAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		private final CrossReference cTypeDefReservedCrossReference_2_1_0 = (CrossReference)cTypeAssignment_2_1.eContents().get(0);
		private final RuleCall cTypeDefReservedIDTerminalRuleCall_2_1_0_1 = (RuleCall)cTypeDefReservedCrossReference_2_1_0.eContents().get(1);
		private final Group cGroup_3 = (Group)cGroup.eContents().get(3);
		private final Keyword cEqualsSignKeyword_3_0 = (Keyword)cGroup_3.eContents().get(0);
		private final Assignment cValueAssignment_3_1 = (Assignment)cGroup_3.eContents().get(1);
		private final RuleCall cValueTerminalExpressionParserRuleCall_3_1_0 = (RuleCall)cValueAssignment_3_1.eContents().get(0);
		private final Keyword cSemicolonKeyword_4 = (Keyword)cGroup.eContents().get(4);
		
		//DefReserved:
		//	"_reserved" name=ID (":" type=[DefReserved])? ("=" value=TerminalExpression)? ";";
		public ParserRule getRule() { return rule; }

		//"_reserved" name=ID (":" type=[DefReserved])? ("=" value=TerminalExpression)? ";"
		public Group getGroup() { return cGroup; }

		//"_reserved"
		public Keyword get_reservedKeyword_0() { return c_reservedKeyword_0; }

		//name=ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }

		//ID
		public RuleCall getNameIDTerminalRuleCall_1_0() { return cNameIDTerminalRuleCall_1_0; }

		//(":" type=[DefReserved])?
		public Group getGroup_2() { return cGroup_2; }

		//":"
		public Keyword getColonKeyword_2_0() { return cColonKeyword_2_0; }

		//type=[DefReserved]
		public Assignment getTypeAssignment_2_1() { return cTypeAssignment_2_1; }

		//[DefReserved]
		public CrossReference getTypeDefReservedCrossReference_2_1_0() { return cTypeDefReservedCrossReference_2_1_0; }

		//ID
		public RuleCall getTypeDefReservedIDTerminalRuleCall_2_1_0_1() { return cTypeDefReservedIDTerminalRuleCall_2_1_0_1; }

		//("=" value=TerminalExpression)?
		public Group getGroup_3() { return cGroup_3; }

		//"="
		public Keyword getEqualsSignKeyword_3_0() { return cEqualsSignKeyword_3_0; }

		//value=TerminalExpression
		public Assignment getValueAssignment_3_1() { return cValueAssignment_3_1; }

		//TerminalExpression
		public RuleCall getValueTerminalExpressionParserRuleCall_3_1_0() { return cValueTerminalExpressionParserRuleCall_3_1_0; }

		//";"
		public Keyword getSemicolonKeyword_4() { return cSemicolonKeyword_4; }
	}

	public class DefUnitElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "DefUnit");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword c_unitKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cNameIDTerminalRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		private final Group cGroup_2 = (Group)cGroup.eContents().get(2);
		private final Keyword cEqualsSignKeyword_2_0 = (Keyword)cGroup_2.eContents().get(0);
		private final Assignment cCoefAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		private final RuleCall cCoefDOUBLETerminalRuleCall_2_1_0 = (RuleCall)cCoefAssignment_2_1.eContents().get(0);
		private final Keyword cSemicolonKeyword_3 = (Keyword)cGroup.eContents().get(3);
		
		//DefUnit:
		//	"_unit" name=ID ("=" coef=DOUBLE)? ";";
		public ParserRule getRule() { return rule; }

		//"_unit" name=ID ("=" coef=DOUBLE)? ";"
		public Group getGroup() { return cGroup; }

		//"_unit"
		public Keyword get_unitKeyword_0() { return c_unitKeyword_0; }

		//name=ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }

		//ID
		public RuleCall getNameIDTerminalRuleCall_1_0() { return cNameIDTerminalRuleCall_1_0; }

		//("=" coef=DOUBLE)?
		public Group getGroup_2() { return cGroup_2; }

		//"="
		public Keyword getEqualsSignKeyword_2_0() { return cEqualsSignKeyword_2_0; }

		//coef=DOUBLE
		public Assignment getCoefAssignment_2_1() { return cCoefAssignment_2_1; }

		//DOUBLE
		public RuleCall getCoefDOUBLETerminalRuleCall_2_1_0() { return cCoefDOUBLETerminalRuleCall_2_1_0; }

		//";"
		public Keyword getSemicolonKeyword_3() { return cSemicolonKeyword_3; }
	}

	public class AbstractGamlRefElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "AbstractGamlRef");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cGamlFacetRefParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cGamlKeywordRefParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cGamlBinarOpRefParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		private final RuleCall cGamlUnitRefParserRuleCall_3 = (RuleCall)cAlternatives.eContents().get(3);
		private final RuleCall cGamlReservedRefParserRuleCall_4 = (RuleCall)cAlternatives.eContents().get(4);
		
		//// for highlight
		//AbstractGamlRef:
		//	GamlFacetRef | GamlKeywordRef | GamlBinarOpRef | GamlUnitRef | GamlReservedRef;
		public ParserRule getRule() { return rule; }

		//GamlFacetRef | GamlKeywordRef | GamlBinarOpRef | GamlUnitRef | GamlReservedRef
		public Alternatives getAlternatives() { return cAlternatives; }

		//GamlFacetRef
		public RuleCall getGamlFacetRefParserRuleCall_0() { return cGamlFacetRefParserRuleCall_0; }

		//GamlKeywordRef
		public RuleCall getGamlKeywordRefParserRuleCall_1() { return cGamlKeywordRefParserRuleCall_1; }

		//GamlBinarOpRef
		public RuleCall getGamlBinarOpRefParserRuleCall_2() { return cGamlBinarOpRefParserRuleCall_2; }

		//GamlUnitRef
		public RuleCall getGamlUnitRefParserRuleCall_3() { return cGamlUnitRefParserRuleCall_3; }

		//GamlReservedRef
		public RuleCall getGamlReservedRefParserRuleCall_4() { return cGamlReservedRefParserRuleCall_4; }
	}

	public class GamlKeywordRefElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "GamlKeywordRef");
		private final Assignment cRefAssignment = (Assignment)rule.eContents().get(1);
		private final CrossReference cRefDefKeywordCrossReference_0 = (CrossReference)cRefAssignment.eContents().get(0);
		private final RuleCall cRefDefKeywordIDTerminalRuleCall_0_1 = (RuleCall)cRefDefKeywordCrossReference_0.eContents().get(1);
		
		//// for highlight
		//GamlKeywordRef:
		//	ref=[DefKeyword];
		public ParserRule getRule() { return rule; }

		//ref=[DefKeyword]
		public Assignment getRefAssignment() { return cRefAssignment; }

		//[DefKeyword]
		public CrossReference getRefDefKeywordCrossReference_0() { return cRefDefKeywordCrossReference_0; }

		//ID
		public RuleCall getRefDefKeywordIDTerminalRuleCall_0_1() { return cRefDefKeywordIDTerminalRuleCall_0_1; }
	}

	public class GamlFacetRefElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "GamlFacetRef");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cRefAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final CrossReference cRefDefFacetCrossReference_0_0 = (CrossReference)cRefAssignment_0.eContents().get(0);
		private final RuleCall cRefDefFacetIDTerminalRuleCall_0_0_1 = (RuleCall)cRefDefFacetCrossReference_0_0.eContents().get(1);
		private final Keyword cColonKeyword_1 = (Keyword)cGroup.eContents().get(1);
		
		//GamlFacetRef hidden():
		//	ref=[DefFacet] ":";
		public ParserRule getRule() { return rule; }

		//ref=[DefFacet] ":"
		public Group getGroup() { return cGroup; }

		//ref=[DefFacet]
		public Assignment getRefAssignment_0() { return cRefAssignment_0; }

		//[DefFacet]
		public CrossReference getRefDefFacetCrossReference_0_0() { return cRefDefFacetCrossReference_0_0; }

		//ID
		public RuleCall getRefDefFacetIDTerminalRuleCall_0_0_1() { return cRefDefFacetIDTerminalRuleCall_0_0_1; }

		//":"
		public Keyword getColonKeyword_1() { return cColonKeyword_1; }
	}

	public class GamlBinarOpRefElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "GamlBinarOpRef");
		private final Assignment cRefAssignment = (Assignment)rule.eContents().get(1);
		private final CrossReference cRefDefBinaryOpCrossReference_0 = (CrossReference)cRefAssignment.eContents().get(0);
		private final RuleCall cRefDefBinaryOpIDTerminalRuleCall_0_1 = (RuleCall)cRefDefBinaryOpCrossReference_0.eContents().get(1);
		
		//GamlBinarOpRef:
		//	ref=[DefBinaryOp];
		public ParserRule getRule() { return rule; }

		//ref=[DefBinaryOp]
		public Assignment getRefAssignment() { return cRefAssignment; }

		//[DefBinaryOp]
		public CrossReference getRefDefBinaryOpCrossReference_0() { return cRefDefBinaryOpCrossReference_0; }

		//ID
		public RuleCall getRefDefBinaryOpIDTerminalRuleCall_0_1() { return cRefDefBinaryOpIDTerminalRuleCall_0_1; }
	}

	public class GamlUnitRefElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "GamlUnitRef");
		private final Assignment cRefAssignment = (Assignment)rule.eContents().get(1);
		private final CrossReference cRefDefUnitCrossReference_0 = (CrossReference)cRefAssignment.eContents().get(0);
		private final RuleCall cRefDefUnitIDTerminalRuleCall_0_1 = (RuleCall)cRefDefUnitCrossReference_0.eContents().get(1);
		
		//GamlUnitRef:
		//	ref=[DefUnit];
		public ParserRule getRule() { return rule; }

		//ref=[DefUnit]
		public Assignment getRefAssignment() { return cRefAssignment; }

		//[DefUnit]
		public CrossReference getRefDefUnitCrossReference_0() { return cRefDefUnitCrossReference_0; }

		//ID
		public RuleCall getRefDefUnitIDTerminalRuleCall_0_1() { return cRefDefUnitIDTerminalRuleCall_0_1; }
	}

	public class GamlReservedRefElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "GamlReservedRef");
		private final Assignment cRefAssignment = (Assignment)rule.eContents().get(1);
		private final CrossReference cRefDefReservedCrossReference_0 = (CrossReference)cRefAssignment.eContents().get(0);
		private final RuleCall cRefDefReservedIDTerminalRuleCall_0_1 = (RuleCall)cRefDefReservedCrossReference_0.eContents().get(1);
		
		//GamlReservedRef:
		//	ref=[DefReserved];
		public ParserRule getRule() { return rule; }

		//ref=[DefReserved]
		public Assignment getRefAssignment() { return cRefAssignment; }

		//[DefReserved]
		public CrossReference getRefDefReservedCrossReference_0() { return cRefDefReservedCrossReference_0; }

		//ID
		public RuleCall getRefDefReservedIDTerminalRuleCall_0_1() { return cRefDefReservedIDTerminalRuleCall_0_1; }
	}

	public class StatementElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Statement");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cSetEvalParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cSubStatementParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//Statement:
		//	SetEval | SubStatement;
		public ParserRule getRule() { return rule; }

		//SetEval | SubStatement
		public Alternatives getAlternatives() { return cAlternatives; }

		//SetEval
		public RuleCall getSetEvalParserRuleCall_0() { return cSetEvalParserRuleCall_0; }

		//SubStatement
		public RuleCall getSubStatementParserRuleCall_1() { return cSubStatementParserRuleCall_1; }
	}

	public class SubStatementElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "SubStatement");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cDefinitionParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cEvaluationParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		
		//SubStatement:
		//	Definition | Evaluation;
		public ParserRule getRule() { return rule; }

		//Definition | Evaluation
		public Alternatives getAlternatives() { return cAlternatives; }

		//Definition
		public RuleCall getDefinitionParserRuleCall_0() { return cDefinitionParserRuleCall_0; }

		//Evaluation
		public RuleCall getEvaluationParserRuleCall_1() { return cEvaluationParserRuleCall_1; }
	}

	public class SetEvalElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "SetEval");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Keyword cSetKeyword_0 = (Keyword)cGroup.eContents().get(0);
		private final Assignment cVarAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cVarExpressionParserRuleCall_1_0 = (RuleCall)cVarAssignment_1.eContents().get(0);
		private final Assignment cFacetsAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cFacetsFacetExprParserRuleCall_2_0 = (RuleCall)cFacetsAssignment_2.eContents().get(0);
		private final Alternatives cAlternatives_3 = (Alternatives)cGroup.eContents().get(3);
		private final Assignment cBlockAssignment_3_0 = (Assignment)cAlternatives_3.eContents().get(0);
		private final RuleCall cBlockBlockParserRuleCall_3_0_0 = (RuleCall)cBlockAssignment_3_0.eContents().get(0);
		private final Keyword cSemicolonKeyword_3_1 = (Keyword)cAlternatives_3.eContents().get(1);
		
		//SetEval:
		//	"set" var=Expression facets+=FacetExpr* (block=Block | ";");
		public ParserRule getRule() { return rule; }

		//"set" var=Expression facets+=FacetExpr* (block=Block | ";")
		public Group getGroup() { return cGroup; }

		//"set"
		public Keyword getSetKeyword_0() { return cSetKeyword_0; }

		//var=Expression
		public Assignment getVarAssignment_1() { return cVarAssignment_1; }

		//Expression
		public RuleCall getVarExpressionParserRuleCall_1_0() { return cVarExpressionParserRuleCall_1_0; }

		//facets+=FacetExpr*
		public Assignment getFacetsAssignment_2() { return cFacetsAssignment_2; }

		//FacetExpr
		public RuleCall getFacetsFacetExprParserRuleCall_2_0() { return cFacetsFacetExprParserRuleCall_2_0; }

		//block=Block | ";"
		public Alternatives getAlternatives_3() { return cAlternatives_3; }

		//block=Block
		public Assignment getBlockAssignment_3_0() { return cBlockAssignment_3_0; }

		//Block
		public RuleCall getBlockBlockParserRuleCall_3_0_0() { return cBlockBlockParserRuleCall_3_0_0; }

		//";"
		public Keyword getSemicolonKeyword_3_1() { return cSemicolonKeyword_3_1; }
	}

	public class DefinitionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Definition");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cKeyGamlKeywordRefParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		private final Assignment cNameAssignment_1 = (Assignment)cGroup.eContents().get(1);
		private final RuleCall cNameIDTerminalRuleCall_1_0 = (RuleCall)cNameAssignment_1.eContents().get(0);
		private final Assignment cFacetsAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cFacetsFacetExprParserRuleCall_2_0 = (RuleCall)cFacetsAssignment_2.eContents().get(0);
		private final Alternatives cAlternatives_3 = (Alternatives)cGroup.eContents().get(3);
		private final Assignment cBlockAssignment_3_0 = (Assignment)cAlternatives_3.eContents().get(0);
		private final RuleCall cBlockBlockParserRuleCall_3_0_0 = (RuleCall)cBlockAssignment_3_0.eContents().get(0);
		private final Keyword cSemicolonKeyword_3_1 = (Keyword)cAlternatives_3.eContents().get(1);
		
		//// const/var/let/species/grid/gis/state/arg
		//Definition:
		//	key=GamlKeywordRef name=ID facets+=FacetExpr* (block=Block | ";");
		public ParserRule getRule() { return rule; }

		//key=GamlKeywordRef name=ID facets+=FacetExpr* (block=Block | ";")
		public Group getGroup() { return cGroup; }

		//key=GamlKeywordRef
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }

		//GamlKeywordRef
		public RuleCall getKeyGamlKeywordRefParserRuleCall_0_0() { return cKeyGamlKeywordRefParserRuleCall_0_0; }

		//name=ID
		public Assignment getNameAssignment_1() { return cNameAssignment_1; }

		//ID
		public RuleCall getNameIDTerminalRuleCall_1_0() { return cNameIDTerminalRuleCall_1_0; }

		//facets+=FacetExpr*
		public Assignment getFacetsAssignment_2() { return cFacetsAssignment_2; }

		//FacetExpr
		public RuleCall getFacetsFacetExprParserRuleCall_2_0() { return cFacetsFacetExprParserRuleCall_2_0; }

		//block=Block | ";"
		public Alternatives getAlternatives_3() { return cAlternatives_3; }

		//block=Block
		public Assignment getBlockAssignment_3_0() { return cBlockAssignment_3_0; }

		//Block
		public RuleCall getBlockBlockParserRuleCall_3_0_0() { return cBlockBlockParserRuleCall_3_0_0; }

		//";"
		public Keyword getSemicolonKeyword_3_1() { return cSemicolonKeyword_3_1; }
	}

	public class EvaluationElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Evaluation");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cKeyAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cKeyGamlKeywordRefParserRuleCall_0_0 = (RuleCall)cKeyAssignment_0.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Keyword cColonKeyword_1_0 = (Keyword)cGroup_1.eContents().get(0);
		private final Assignment cVarAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cVarExpressionParserRuleCall_1_1_0 = (RuleCall)cVarAssignment_1_1.eContents().get(0);
		private final Assignment cFacetsAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cFacetsFacetExprParserRuleCall_2_0 = (RuleCall)cFacetsAssignment_2.eContents().get(0);
		private final Alternatives cAlternatives_3 = (Alternatives)cGroup.eContents().get(3);
		private final Assignment cBlockAssignment_3_0 = (Assignment)cAlternatives_3.eContents().get(0);
		private final RuleCall cBlockBlockParserRuleCall_3_0_0 = (RuleCall)cBlockAssignment_3_0.eContents().get(0);
		private final Keyword cSemicolonKeyword_3_1 = (Keyword)cAlternatives_3.eContents().get(1);
		
		//Evaluation:
		//	key=GamlKeywordRef (":" var=Expression)? facets+=FacetExpr* (block=Block | ";");
		public ParserRule getRule() { return rule; }

		//key=GamlKeywordRef (":" var=Expression)? facets+=FacetExpr* (block=Block | ";")
		public Group getGroup() { return cGroup; }

		//key=GamlKeywordRef
		public Assignment getKeyAssignment_0() { return cKeyAssignment_0; }

		//GamlKeywordRef
		public RuleCall getKeyGamlKeywordRefParserRuleCall_0_0() { return cKeyGamlKeywordRefParserRuleCall_0_0; }

		//(":" var=Expression)?
		public Group getGroup_1() { return cGroup_1; }

		//":"
		public Keyword getColonKeyword_1_0() { return cColonKeyword_1_0; }

		//var=Expression
		public Assignment getVarAssignment_1_1() { return cVarAssignment_1_1; }

		//Expression
		public RuleCall getVarExpressionParserRuleCall_1_1_0() { return cVarExpressionParserRuleCall_1_1_0; }

		//facets+=FacetExpr*
		public Assignment getFacetsAssignment_2() { return cFacetsAssignment_2; }

		//FacetExpr
		public RuleCall getFacetsFacetExprParserRuleCall_2_0() { return cFacetsFacetExprParserRuleCall_2_0; }

		//block=Block | ";"
		public Alternatives getAlternatives_3() { return cAlternatives_3; }

		//block=Block
		public Assignment getBlockAssignment_3_0() { return cBlockAssignment_3_0; }

		//Block
		public RuleCall getBlockBlockParserRuleCall_3_0_0() { return cBlockBlockParserRuleCall_3_0_0; }

		//";"
		public Keyword getSemicolonKeyword_3_1() { return cSemicolonKeyword_3_1; }
	}

	public class FacetExprElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "FacetExpr");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final Group cGroup_0 = (Group)cAlternatives.eContents().get(0);
		private final Keyword cReturnsKeyword_0_0 = (Keyword)cGroup_0.eContents().get(0);
		private final Assignment cNameAssignment_0_1 = (Assignment)cGroup_0.eContents().get(1);
		private final RuleCall cNameIDTerminalRuleCall_0_1_0 = (RuleCall)cNameAssignment_0_1.eContents().get(0);
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		private final Assignment cKeyAssignment_1_0 = (Assignment)cGroup_1.eContents().get(0);
		private final RuleCall cKeyGamlFacetRefParserRuleCall_1_0_0 = (RuleCall)cKeyAssignment_1_0.eContents().get(0);
		private final Assignment cExprAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cExprExpressionParserRuleCall_1_1_0 = (RuleCall)cExprAssignment_1_1.eContents().get(0);
		
		//FacetExpr:
		//	"returns:" name=ID | key=GamlFacetRef expr=Expression;
		public ParserRule getRule() { return rule; }

		//"returns:" name=ID | key=GamlFacetRef expr=Expression
		public Alternatives getAlternatives() { return cAlternatives; }

		//"returns:" name=ID
		public Group getGroup_0() { return cGroup_0; }

		//"returns:"
		public Keyword getReturnsKeyword_0_0() { return cReturnsKeyword_0_0; }

		//name=ID
		public Assignment getNameAssignment_0_1() { return cNameAssignment_0_1; }

		//ID
		public RuleCall getNameIDTerminalRuleCall_0_1_0() { return cNameIDTerminalRuleCall_0_1_0; }

		//key=GamlFacetRef expr=Expression
		public Group getGroup_1() { return cGroup_1; }

		//key=GamlFacetRef
		public Assignment getKeyAssignment_1_0() { return cKeyAssignment_1_0; }

		//GamlFacetRef
		public RuleCall getKeyGamlFacetRefParserRuleCall_1_0_0() { return cKeyGamlFacetRefParserRuleCall_1_0_0; }

		//expr=Expression
		public Assignment getExprAssignment_1_1() { return cExprAssignment_1_1; }

		//Expression
		public RuleCall getExprExpressionParserRuleCall_1_1_0() { return cExprExpressionParserRuleCall_1_1_0; }
	}

	public class BlockElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Block");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cBlockAction_0 = (Action)cGroup.eContents().get(0);
		private final Keyword cLeftCurlyBracketKeyword_1 = (Keyword)cGroup.eContents().get(1);
		private final Assignment cStatementsAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cStatementsStatementParserRuleCall_2_0 = (RuleCall)cStatementsAssignment_2.eContents().get(0);
		private final Keyword cRightCurlyBracketKeyword_3 = (Keyword)cGroup.eContents().get(3);
		
		//Block:
		//	{Block} "{" statements+=Statement* "}";
		public ParserRule getRule() { return rule; }

		//{Block} "{" statements+=Statement* "}"
		public Group getGroup() { return cGroup; }

		//{Block}
		public Action getBlockAction_0() { return cBlockAction_0; }

		//"{"
		public Keyword getLeftCurlyBracketKeyword_1() { return cLeftCurlyBracketKeyword_1; }

		//statements+=Statement*
		public Assignment getStatementsAssignment_2() { return cStatementsAssignment_2; }

		//Statement
		public RuleCall getStatementsStatementParserRuleCall_2_0() { return cStatementsStatementParserRuleCall_2_0; }

		//"}"
		public Keyword getRightCurlyBracketKeyword_3() { return cRightCurlyBracketKeyword_3; }
	}

	public class AbstractDefinitionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "AbstractDefinition");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cDefinitionParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cDefReservedParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cFacetExprParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		//// for variable reference
		//AbstractDefinition:
		//	Definition | DefReserved | FacetExpr;
		public ParserRule getRule() { return rule; }

		//Definition | DefReserved | FacetExpr
		public Alternatives getAlternatives() { return cAlternatives; }

		//Definition
		public RuleCall getDefinitionParserRuleCall_0() { return cDefinitionParserRuleCall_0; }

		//DefReserved
		public RuleCall getDefReservedParserRuleCall_1() { return cDefReservedParserRuleCall_1; }

		//FacetExpr
		public RuleCall getFacetExprParserRuleCall_2() { return cFacetExprParserRuleCall_2; }
	}

	public class ExpressionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Expression");
		private final RuleCall cAssignmentOpParserRuleCall = (RuleCall)rule.eContents().get(1);
		
		//Expression:
		//	AssignmentOp;
		public ParserRule getRule() { return rule; }

		//AssignmentOp
		public RuleCall getAssignmentOpParserRuleCall() { return cAssignmentOpParserRuleCall; }
	}

	public class AssignmentOpElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "AssignmentOp");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cTernExpParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Alternatives cAlternatives_1_0 = (Alternatives)cGroup_1.eContents().get(0);
		private final Group cGroup_1_0_0 = (Group)cAlternatives_1_0.eContents().get(0);
		private final Action cAssignPlusLeftAction_1_0_0_0 = (Action)cGroup_1_0_0.eContents().get(0);
		private final Keyword cPlusSignEqualsSignKeyword_1_0_0_1 = (Keyword)cGroup_1_0_0.eContents().get(1);
		private final Group cGroup_1_0_1 = (Group)cAlternatives_1_0.eContents().get(1);
		private final Action cAssignMinLeftAction_1_0_1_0 = (Action)cGroup_1_0_1.eContents().get(0);
		private final Keyword cHyphenMinusEqualsSignKeyword_1_0_1_1 = (Keyword)cGroup_1_0_1.eContents().get(1);
		private final Group cGroup_1_0_2 = (Group)cAlternatives_1_0.eContents().get(2);
		private final Action cAssignMultLeftAction_1_0_2_0 = (Action)cGroup_1_0_2.eContents().get(0);
		private final Keyword cAsteriskEqualsSignKeyword_1_0_2_1 = (Keyword)cGroup_1_0_2.eContents().get(1);
		private final Group cGroup_1_0_3 = (Group)cAlternatives_1_0.eContents().get(3);
		private final Action cAssignDivLeftAction_1_0_3_0 = (Action)cGroup_1_0_3.eContents().get(0);
		private final Keyword cSolidusEqualsSignKeyword_1_0_3_1 = (Keyword)cGroup_1_0_3.eContents().get(1);
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cRightTernExpParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//AssignmentOp returns Expression:
		//	TernExp (({AssignPlus.left=current} "+=" | {AssignMin.left=current} "-=" | {AssignMult.left=current} "*=" |
		//	{AssignDiv.left=current} "/=") right=TernExp)?;
		public ParserRule getRule() { return rule; }

		//TernExp (({AssignPlus.left=current} "+=" | {AssignMin.left=current} "-=" | {AssignMult.left=current} "*=" |
		//{AssignDiv.left=current} "/=") right=TernExp)?
		public Group getGroup() { return cGroup; }

		//TernExp
		public RuleCall getTernExpParserRuleCall_0() { return cTernExpParserRuleCall_0; }

		//(({AssignPlus.left=current} "+=" | {AssignMin.left=current} "-=" | {AssignMult.left=current} "*=" |
		//{AssignDiv.left=current} "/=") right=TernExp)?
		public Group getGroup_1() { return cGroup_1; }

		//{AssignPlus.left=current} "+=" | {AssignMin.left=current} "-=" | {AssignMult.left=current} "*=" |
		//{AssignDiv.left=current} "/="
		public Alternatives getAlternatives_1_0() { return cAlternatives_1_0; }

		//{AssignPlus.left=current} "+="
		public Group getGroup_1_0_0() { return cGroup_1_0_0; }

		//{AssignPlus.left=current}
		public Action getAssignPlusLeftAction_1_0_0_0() { return cAssignPlusLeftAction_1_0_0_0; }

		//"+="
		public Keyword getPlusSignEqualsSignKeyword_1_0_0_1() { return cPlusSignEqualsSignKeyword_1_0_0_1; }

		//{AssignMin.left=current} "-="
		public Group getGroup_1_0_1() { return cGroup_1_0_1; }

		//{AssignMin.left=current}
		public Action getAssignMinLeftAction_1_0_1_0() { return cAssignMinLeftAction_1_0_1_0; }

		//"-="
		public Keyword getHyphenMinusEqualsSignKeyword_1_0_1_1() { return cHyphenMinusEqualsSignKeyword_1_0_1_1; }

		//{AssignMult.left=current} "*="
		public Group getGroup_1_0_2() { return cGroup_1_0_2; }

		//{AssignMult.left=current}
		public Action getAssignMultLeftAction_1_0_2_0() { return cAssignMultLeftAction_1_0_2_0; }

		//"*="
		public Keyword getAsteriskEqualsSignKeyword_1_0_2_1() { return cAsteriskEqualsSignKeyword_1_0_2_1; }

		//{AssignDiv.left=current} "/="
		public Group getGroup_1_0_3() { return cGroup_1_0_3; }

		//{AssignDiv.left=current}
		public Action getAssignDivLeftAction_1_0_3_0() { return cAssignDivLeftAction_1_0_3_0; }

		//"/="
		public Keyword getSolidusEqualsSignKeyword_1_0_3_1() { return cSolidusEqualsSignKeyword_1_0_3_1; }

		//right=TernExp
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }

		//TernExp
		public RuleCall getRightTernExpParserRuleCall_1_1_0() { return cRightTernExpParserRuleCall_1_1_0; }
	}

	public class TernExpElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "TernExp");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cOrExpParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cTernaryConditionAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final Keyword cQuestionMarkKeyword_1_1 = (Keyword)cGroup_1.eContents().get(1);
		private final Assignment cIfTrueAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cIfTrueOrExpParserRuleCall_1_2_0 = (RuleCall)cIfTrueAssignment_1_2.eContents().get(0);
		private final Keyword cColonKeyword_1_3 = (Keyword)cGroup_1.eContents().get(3);
		private final Assignment cIfFalseAssignment_1_4 = (Assignment)cGroup_1.eContents().get(4);
		private final RuleCall cIfFalseOrExpParserRuleCall_1_4_0 = (RuleCall)cIfFalseAssignment_1_4.eContents().get(0);
		
		//TernExp returns Expression:
		//	OrExp ({Ternary.condition=current} "?" ifTrue=OrExp ":" ifFalse=OrExp)?;
		public ParserRule getRule() { return rule; }

		//OrExp ({Ternary.condition=current} "?" ifTrue=OrExp ":" ifFalse=OrExp)?
		public Group getGroup() { return cGroup; }

		//OrExp
		public RuleCall getOrExpParserRuleCall_0() { return cOrExpParserRuleCall_0; }

		//({Ternary.condition=current} "?" ifTrue=OrExp ":" ifFalse=OrExp)?
		public Group getGroup_1() { return cGroup_1; }

		//{Ternary.condition=current}
		public Action getTernaryConditionAction_1_0() { return cTernaryConditionAction_1_0; }

		//"?"
		public Keyword getQuestionMarkKeyword_1_1() { return cQuestionMarkKeyword_1_1; }

		//ifTrue=OrExp
		public Assignment getIfTrueAssignment_1_2() { return cIfTrueAssignment_1_2; }

		//OrExp
		public RuleCall getIfTrueOrExpParserRuleCall_1_2_0() { return cIfTrueOrExpParserRuleCall_1_2_0; }

		//":"
		public Keyword getColonKeyword_1_3() { return cColonKeyword_1_3; }

		//ifFalse=OrExp
		public Assignment getIfFalseAssignment_1_4() { return cIfFalseAssignment_1_4; }

		//OrExp
		public RuleCall getIfFalseOrExpParserRuleCall_1_4_0() { return cIfFalseOrExpParserRuleCall_1_4_0; }
	}

	public class OrExpElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "OrExp");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cAndExpParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cOrLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final Keyword cOrKeyword_1_1 = (Keyword)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightAndExpParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//OrExp returns Expression:
		//	AndExp ({Or.left=current} "or" right=AndExp)*;
		public ParserRule getRule() { return rule; }

		//AndExp ({Or.left=current} "or" right=AndExp)*
		public Group getGroup() { return cGroup; }

		//AndExp
		public RuleCall getAndExpParserRuleCall_0() { return cAndExpParserRuleCall_0; }

		//({Or.left=current} "or" right=AndExp)*
		public Group getGroup_1() { return cGroup_1; }

		//{Or.left=current}
		public Action getOrLeftAction_1_0() { return cOrLeftAction_1_0; }

		//"or"
		public Keyword getOrKeyword_1_1() { return cOrKeyword_1_1; }

		//right=AndExp
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }

		//AndExp
		public RuleCall getRightAndExpParserRuleCall_1_2_0() { return cRightAndExpParserRuleCall_1_2_0; }
	}

	public class AndExpElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "AndExp");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cRelationalParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cAndLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final Keyword cAndKeyword_1_1 = (Keyword)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightRelationalParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//AndExp returns Expression:
		//	Relational ({And.left=current} "and" right=Relational)*;
		public ParserRule getRule() { return rule; }

		//Relational ({And.left=current} "and" right=Relational)*
		public Group getGroup() { return cGroup; }

		//Relational
		public RuleCall getRelationalParserRuleCall_0() { return cRelationalParserRuleCall_0; }

		//({And.left=current} "and" right=Relational)*
		public Group getGroup_1() { return cGroup_1; }

		//{And.left=current}
		public Action getAndLeftAction_1_0() { return cAndLeftAction_1_0; }

		//"and"
		public Keyword getAndKeyword_1_1() { return cAndKeyword_1_1; }

		//right=Relational
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }

		//Relational
		public RuleCall getRightRelationalParserRuleCall_1_2_0() { return cRightRelationalParserRuleCall_1_2_0; }
	}

	public class RelationalElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Relational");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cPairExprParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Alternatives cAlternatives_1_0 = (Alternatives)cGroup_1.eContents().get(0);
		private final Group cGroup_1_0_0 = (Group)cAlternatives_1_0.eContents().get(0);
		private final Action cRelNotEqLeftAction_1_0_0_0 = (Action)cGroup_1_0_0.eContents().get(0);
		private final Keyword cExclamationMarkEqualsSignKeyword_1_0_0_1 = (Keyword)cGroup_1_0_0.eContents().get(1);
		private final Group cGroup_1_0_1 = (Group)cAlternatives_1_0.eContents().get(1);
		private final Action cRelEqLeftAction_1_0_1_0 = (Action)cGroup_1_0_1.eContents().get(0);
		private final Keyword cEqualsSignKeyword_1_0_1_1 = (Keyword)cGroup_1_0_1.eContents().get(1);
		private final Group cGroup_1_0_2 = (Group)cAlternatives_1_0.eContents().get(2);
		private final Action cRelEqEqLeftAction_1_0_2_0 = (Action)cGroup_1_0_2.eContents().get(0);
		private final Keyword cEqualsSignEqualsSignKeyword_1_0_2_1 = (Keyword)cGroup_1_0_2.eContents().get(1);
		private final Group cGroup_1_0_3 = (Group)cAlternatives_1_0.eContents().get(3);
		private final Action cRelLtEqLeftAction_1_0_3_0 = (Action)cGroup_1_0_3.eContents().get(0);
		private final Keyword cGreaterThanSignEqualsSignKeyword_1_0_3_1 = (Keyword)cGroup_1_0_3.eContents().get(1);
		private final Group cGroup_1_0_4 = (Group)cAlternatives_1_0.eContents().get(4);
		private final Action cRelGtEqLeftAction_1_0_4_0 = (Action)cGroup_1_0_4.eContents().get(0);
		private final Keyword cLessThanSignEqualsSignKeyword_1_0_4_1 = (Keyword)cGroup_1_0_4.eContents().get(1);
		private final Group cGroup_1_0_5 = (Group)cAlternatives_1_0.eContents().get(5);
		private final Action cRelLtLeftAction_1_0_5_0 = (Action)cGroup_1_0_5.eContents().get(0);
		private final Keyword cLessThanSignKeyword_1_0_5_1 = (Keyword)cGroup_1_0_5.eContents().get(1);
		private final Group cGroup_1_0_6 = (Group)cAlternatives_1_0.eContents().get(6);
		private final Action cRelGtLeftAction_1_0_6_0 = (Action)cGroup_1_0_6.eContents().get(0);
		private final Keyword cGreaterThanSignKeyword_1_0_6_1 = (Keyword)cGroup_1_0_6.eContents().get(1);
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cRightPairExprParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//Relational returns Expression:
		//	PairExpr (({RelNotEq.left=current} "!=" | {RelEq.left=current} "=" | {RelEqEq.left=current} "==" |
		//	{RelLtEq.left=current} ">=" | {RelGtEq.left=current} "<=" | {RelLt.left=current} "<" | {RelGt.left=current} ">")
		//	right=PairExpr)?;
		public ParserRule getRule() { return rule; }

		//PairExpr (({RelNotEq.left=current} "!=" | {RelEq.left=current} "=" | {RelEqEq.left=current} "==" |
		//{RelLtEq.left=current} ">=" | {RelGtEq.left=current} "<=" | {RelLt.left=current} "<" | {RelGt.left=current} ">")
		//right=PairExpr)?
		public Group getGroup() { return cGroup; }

		//PairExpr
		public RuleCall getPairExprParserRuleCall_0() { return cPairExprParserRuleCall_0; }

		//(({RelNotEq.left=current} "!=" | {RelEq.left=current} "=" | {RelEqEq.left=current} "==" | {RelLtEq.left=current} ">=" |
		//{RelGtEq.left=current} "<=" | {RelLt.left=current} "<" | {RelGt.left=current} ">") right=PairExpr)?
		public Group getGroup_1() { return cGroup_1; }

		//{RelNotEq.left=current} "!=" | {RelEq.left=current} "=" | {RelEqEq.left=current} "==" | {RelLtEq.left=current} ">=" |
		//{RelGtEq.left=current} "<=" | {RelLt.left=current} "<" | {RelGt.left=current} ">"
		public Alternatives getAlternatives_1_0() { return cAlternatives_1_0; }

		//{RelNotEq.left=current} "!="
		public Group getGroup_1_0_0() { return cGroup_1_0_0; }

		//{RelNotEq.left=current}
		public Action getRelNotEqLeftAction_1_0_0_0() { return cRelNotEqLeftAction_1_0_0_0; }

		//"!="
		public Keyword getExclamationMarkEqualsSignKeyword_1_0_0_1() { return cExclamationMarkEqualsSignKeyword_1_0_0_1; }

		//{RelEq.left=current} "="
		public Group getGroup_1_0_1() { return cGroup_1_0_1; }

		//{RelEq.left=current}
		public Action getRelEqLeftAction_1_0_1_0() { return cRelEqLeftAction_1_0_1_0; }

		//"="
		public Keyword getEqualsSignKeyword_1_0_1_1() { return cEqualsSignKeyword_1_0_1_1; }

		//{RelEqEq.left=current} "=="
		public Group getGroup_1_0_2() { return cGroup_1_0_2; }

		//{RelEqEq.left=current}
		public Action getRelEqEqLeftAction_1_0_2_0() { return cRelEqEqLeftAction_1_0_2_0; }

		//"=="
		public Keyword getEqualsSignEqualsSignKeyword_1_0_2_1() { return cEqualsSignEqualsSignKeyword_1_0_2_1; }

		//{RelLtEq.left=current} ">="
		public Group getGroup_1_0_3() { return cGroup_1_0_3; }

		//{RelLtEq.left=current}
		public Action getRelLtEqLeftAction_1_0_3_0() { return cRelLtEqLeftAction_1_0_3_0; }

		//">="
		public Keyword getGreaterThanSignEqualsSignKeyword_1_0_3_1() { return cGreaterThanSignEqualsSignKeyword_1_0_3_1; }

		//{RelGtEq.left=current} "<="
		public Group getGroup_1_0_4() { return cGroup_1_0_4; }

		//{RelGtEq.left=current}
		public Action getRelGtEqLeftAction_1_0_4_0() { return cRelGtEqLeftAction_1_0_4_0; }

		//"<="
		public Keyword getLessThanSignEqualsSignKeyword_1_0_4_1() { return cLessThanSignEqualsSignKeyword_1_0_4_1; }

		//{RelLt.left=current} "<"
		public Group getGroup_1_0_5() { return cGroup_1_0_5; }

		//{RelLt.left=current}
		public Action getRelLtLeftAction_1_0_5_0() { return cRelLtLeftAction_1_0_5_0; }

		//"<"
		public Keyword getLessThanSignKeyword_1_0_5_1() { return cLessThanSignKeyword_1_0_5_1; }

		//{RelGt.left=current} ">"
		public Group getGroup_1_0_6() { return cGroup_1_0_6; }

		//{RelGt.left=current}
		public Action getRelGtLeftAction_1_0_6_0() { return cRelGtLeftAction_1_0_6_0; }

		//">"
		public Keyword getGreaterThanSignKeyword_1_0_6_1() { return cGreaterThanSignKeyword_1_0_6_1; }

		//right=PairExpr
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }

		//PairExpr
		public RuleCall getRightPairExprParserRuleCall_1_1_0() { return cRightPairExprParserRuleCall_1_1_0; }
	}

	public class PairExprElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "PairExpr");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cAdditionParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Group cGroup_1_0 = (Group)cGroup_1.eContents().get(0);
		private final Action cPairLeftAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		private final Keyword cColonColonKeyword_1_0_1 = (Keyword)cGroup_1_0.eContents().get(1);
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cRightAdditionParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//PairExpr returns Expression:
		//	Addition (({Pair.left=current} "::") right=Addition)?;
		public ParserRule getRule() { return rule; }

		//Addition (({Pair.left=current} "::") right=Addition)?
		public Group getGroup() { return cGroup; }

		//Addition
		public RuleCall getAdditionParserRuleCall_0() { return cAdditionParserRuleCall_0; }

		//(({Pair.left=current} "::") right=Addition)?
		public Group getGroup_1() { return cGroup_1; }

		//{Pair.left=current} "::"
		public Group getGroup_1_0() { return cGroup_1_0; }

		//{Pair.left=current}
		public Action getPairLeftAction_1_0_0() { return cPairLeftAction_1_0_0; }

		//"::"
		public Keyword getColonColonKeyword_1_0_1() { return cColonColonKeyword_1_0_1; }

		//right=Addition
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }

		//Addition
		public RuleCall getRightAdditionParserRuleCall_1_1_0() { return cRightAdditionParserRuleCall_1_1_0; }
	}

	public class AdditionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Addition");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cMultiplicationParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Alternatives cAlternatives_1_0 = (Alternatives)cGroup_1.eContents().get(0);
		private final Group cGroup_1_0_0 = (Group)cAlternatives_1_0.eContents().get(0);
		private final Action cPlusLeftAction_1_0_0_0 = (Action)cGroup_1_0_0.eContents().get(0);
		private final Keyword cPlusSignKeyword_1_0_0_1 = (Keyword)cGroup_1_0_0.eContents().get(1);
		private final Group cGroup_1_0_1 = (Group)cAlternatives_1_0.eContents().get(1);
		private final Action cMinusLeftAction_1_0_1_0 = (Action)cGroup_1_0_1.eContents().get(0);
		private final Keyword cHyphenMinusKeyword_1_0_1_1 = (Keyword)cGroup_1_0_1.eContents().get(1);
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cRightMultiplicationParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//Addition returns Expression:
		//	Multiplication (({Plus.left=current} "+" | {Minus.left=current} "-") right=Multiplication)*;
		public ParserRule getRule() { return rule; }

		//Multiplication (({Plus.left=current} "+" | {Minus.left=current} "-") right=Multiplication)*
		public Group getGroup() { return cGroup; }

		//Multiplication
		public RuleCall getMultiplicationParserRuleCall_0() { return cMultiplicationParserRuleCall_0; }

		//(({Plus.left=current} "+" | {Minus.left=current} "-") right=Multiplication)*
		public Group getGroup_1() { return cGroup_1; }

		//{Plus.left=current} "+" | {Minus.left=current} "-"
		public Alternatives getAlternatives_1_0() { return cAlternatives_1_0; }

		//{Plus.left=current} "+"
		public Group getGroup_1_0_0() { return cGroup_1_0_0; }

		//{Plus.left=current}
		public Action getPlusLeftAction_1_0_0_0() { return cPlusLeftAction_1_0_0_0; }

		//"+"
		public Keyword getPlusSignKeyword_1_0_0_1() { return cPlusSignKeyword_1_0_0_1; }

		//{Minus.left=current} "-"
		public Group getGroup_1_0_1() { return cGroup_1_0_1; }

		//{Minus.left=current}
		public Action getMinusLeftAction_1_0_1_0() { return cMinusLeftAction_1_0_1_0; }

		//"-"
		public Keyword getHyphenMinusKeyword_1_0_1_1() { return cHyphenMinusKeyword_1_0_1_1; }

		//right=Multiplication
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }

		//Multiplication
		public RuleCall getRightMultiplicationParserRuleCall_1_1_0() { return cRightMultiplicationParserRuleCall_1_1_0; }
	}

	public class MultiplicationElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Multiplication");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cGamlBinExprParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Alternatives cAlternatives_1_0 = (Alternatives)cGroup_1.eContents().get(0);
		private final Group cGroup_1_0_0 = (Group)cAlternatives_1_0.eContents().get(0);
		private final Action cMultiLeftAction_1_0_0_0 = (Action)cGroup_1_0_0.eContents().get(0);
		private final Keyword cAsteriskKeyword_1_0_0_1 = (Keyword)cGroup_1_0_0.eContents().get(1);
		private final Group cGroup_1_0_1 = (Group)cAlternatives_1_0.eContents().get(1);
		private final Action cDivLeftAction_1_0_1_0 = (Action)cGroup_1_0_1.eContents().get(0);
		private final Keyword cSolidusKeyword_1_0_1_1 = (Keyword)cGroup_1_0_1.eContents().get(1);
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cRightGamlBinExprParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//Multiplication returns Expression:
		//	GamlBinExpr (({Multi.left=current} "*" | {Div.left=current} "/") right=GamlBinExpr)*;
		public ParserRule getRule() { return rule; }

		//GamlBinExpr (({Multi.left=current} "*" | {Div.left=current} "/") right=GamlBinExpr)*
		public Group getGroup() { return cGroup; }

		//GamlBinExpr
		public RuleCall getGamlBinExprParserRuleCall_0() { return cGamlBinExprParserRuleCall_0; }

		//(({Multi.left=current} "*" | {Div.left=current} "/") right=GamlBinExpr)*
		public Group getGroup_1() { return cGroup_1; }

		//{Multi.left=current} "*" | {Div.left=current} "/"
		public Alternatives getAlternatives_1_0() { return cAlternatives_1_0; }

		//{Multi.left=current} "*"
		public Group getGroup_1_0_0() { return cGroup_1_0_0; }

		//{Multi.left=current}
		public Action getMultiLeftAction_1_0_0_0() { return cMultiLeftAction_1_0_0_0; }

		//"*"
		public Keyword getAsteriskKeyword_1_0_0_1() { return cAsteriskKeyword_1_0_0_1; }

		//{Div.left=current} "/"
		public Group getGroup_1_0_1() { return cGroup_1_0_1; }

		//{Div.left=current}
		public Action getDivLeftAction_1_0_1_0() { return cDivLeftAction_1_0_1_0; }

		//"/"
		public Keyword getSolidusKeyword_1_0_1_1() { return cSolidusKeyword_1_0_1_1; }

		//right=GamlBinExpr
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }

		//GamlBinExpr
		public RuleCall getRightGamlBinExprParserRuleCall_1_1_0() { return cRightGamlBinExprParserRuleCall_1_1_0; }
	}

	public class GamlBinExprElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "GamlBinExpr");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cPowerParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Group cGroup_1_0 = (Group)cGroup_1.eContents().get(0);
		private final Action cGamlBinaryLeftAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		private final Assignment cOpAssignment_1_0_1 = (Assignment)cGroup_1_0.eContents().get(1);
		private final RuleCall cOpGamlBinarOpRefParserRuleCall_1_0_1_0 = (RuleCall)cOpAssignment_1_0_1.eContents().get(0);
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cRightPowerParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//GamlBinExpr returns Expression:
		//	Power (({GamlBinary.left=current} op=GamlBinarOpRef) right=Power)*;
		public ParserRule getRule() { return rule; }

		//Power (({GamlBinary.left=current} op=GamlBinarOpRef) right=Power)*
		public Group getGroup() { return cGroup; }

		//Power
		public RuleCall getPowerParserRuleCall_0() { return cPowerParserRuleCall_0; }

		//(({GamlBinary.left=current} op=GamlBinarOpRef) right=Power)*
		public Group getGroup_1() { return cGroup_1; }

		//{GamlBinary.left=current} op=GamlBinarOpRef
		public Group getGroup_1_0() { return cGroup_1_0; }

		//{GamlBinary.left=current}
		public Action getGamlBinaryLeftAction_1_0_0() { return cGamlBinaryLeftAction_1_0_0; }

		//op=GamlBinarOpRef
		public Assignment getOpAssignment_1_0_1() { return cOpAssignment_1_0_1; }

		//GamlBinarOpRef
		public RuleCall getOpGamlBinarOpRefParserRuleCall_1_0_1_0() { return cOpGamlBinarOpRefParserRuleCall_1_0_1_0; }

		//right=Power
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }

		//Power
		public RuleCall getRightPowerParserRuleCall_1_1_0() { return cRightPowerParserRuleCall_1_1_0; }
	}

	public class PowerElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Power");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cGamlUnitExprParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Group cGroup_1_0 = (Group)cGroup_1.eContents().get(0);
		private final Action cPowLeftAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		private final Keyword cCircumflexAccentKeyword_1_0_1 = (Keyword)cGroup_1_0.eContents().get(1);
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cRightGamlUnitExprParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//Power returns Expression:
		//	GamlUnitExpr (({Pow.left=current} "^") right=GamlUnitExpr)*;
		public ParserRule getRule() { return rule; }

		//GamlUnitExpr (({Pow.left=current} "^") right=GamlUnitExpr)*
		public Group getGroup() { return cGroup; }

		//GamlUnitExpr
		public RuleCall getGamlUnitExprParserRuleCall_0() { return cGamlUnitExprParserRuleCall_0; }

		//(({Pow.left=current} "^") right=GamlUnitExpr)*
		public Group getGroup_1() { return cGroup_1; }

		//{Pow.left=current} "^"
		public Group getGroup_1_0() { return cGroup_1_0; }

		//{Pow.left=current}
		public Action getPowLeftAction_1_0_0() { return cPowLeftAction_1_0_0; }

		//"^"
		public Keyword getCircumflexAccentKeyword_1_0_1() { return cCircumflexAccentKeyword_1_0_1; }

		//right=GamlUnitExpr
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }

		//GamlUnitExpr
		public RuleCall getRightGamlUnitExprParserRuleCall_1_1_0() { return cRightGamlUnitExprParserRuleCall_1_1_0; }
	}

	public class GamlUnitExprElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "GamlUnitExpr");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cGamlUnaryExprParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Group cGroup_1_0 = (Group)cGroup_1.eContents().get(0);
		private final Action cUnitLeftAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		private final Keyword cNumberSignKeyword_1_0_1 = (Keyword)cGroup_1_0.eContents().get(1);
		private final Assignment cRightAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cRightGamlUnitRefParserRuleCall_1_1_0 = (RuleCall)cRightAssignment_1_1.eContents().get(0);
		
		//GamlUnitExpr returns Expression:
		//	GamlUnaryExpr (({Unit.left=current} "#") right=GamlUnitRef)?;
		public ParserRule getRule() { return rule; }

		//GamlUnaryExpr (({Unit.left=current} "#") right=GamlUnitRef)?
		public Group getGroup() { return cGroup; }

		//GamlUnaryExpr
		public RuleCall getGamlUnaryExprParserRuleCall_0() { return cGamlUnaryExprParserRuleCall_0; }

		//(({Unit.left=current} "#") right=GamlUnitRef)?
		public Group getGroup_1() { return cGroup_1; }

		//{Unit.left=current} "#"
		public Group getGroup_1_0() { return cGroup_1_0; }

		//{Unit.left=current}
		public Action getUnitLeftAction_1_0_0() { return cUnitLeftAction_1_0_0; }

		//"#"
		public Keyword getNumberSignKeyword_1_0_1() { return cNumberSignKeyword_1_0_1; }

		//right=GamlUnitRef
		public Assignment getRightAssignment_1_1() { return cRightAssignment_1_1; }

		//GamlUnitRef
		public RuleCall getRightGamlUnitRefParserRuleCall_1_1_0() { return cRightGamlUnitRefParserRuleCall_1_1_0; }
	}

	public class GamlUnaryExprElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "GamlUnaryExpr");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cPrePrimaryExprParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		private final Action cGamlUnaryAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final Group cGroup_1_1 = (Group)cGroup_1.eContents().get(1);
		private final Assignment cOpAssignment_1_1_0 = (Assignment)cGroup_1_1.eContents().get(0);
		private final RuleCall cOpUnarOpParserRuleCall_1_1_0_0 = (RuleCall)cOpAssignment_1_1_0.eContents().get(0);
		private final Assignment cRightAssignment_1_1_1 = (Assignment)cGroup_1_1.eContents().get(1);
		private final RuleCall cRightGamlUnaryExprParserRuleCall_1_1_1_0 = (RuleCall)cRightAssignment_1_1_1.eContents().get(0);
		
		//GamlUnaryExpr returns Expression:
		//	PrePrimaryExpr | {GamlUnary} (op=UnarOp right=GamlUnaryExpr);
		public ParserRule getRule() { return rule; }

		//PrePrimaryExpr | {GamlUnary} (op=UnarOp right=GamlUnaryExpr)
		public Alternatives getAlternatives() { return cAlternatives; }

		//PrePrimaryExpr
		public RuleCall getPrePrimaryExprParserRuleCall_0() { return cPrePrimaryExprParserRuleCall_0; }

		//{GamlUnary} (op=UnarOp right=GamlUnaryExpr)
		public Group getGroup_1() { return cGroup_1; }

		//{GamlUnary}
		public Action getGamlUnaryAction_1_0() { return cGamlUnaryAction_1_0; }

		//op=UnarOp right=GamlUnaryExpr
		public Group getGroup_1_1() { return cGroup_1_1; }

		//op=UnarOp
		public Assignment getOpAssignment_1_1_0() { return cOpAssignment_1_1_0; }

		//UnarOp
		public RuleCall getOpUnarOpParserRuleCall_1_1_0_0() { return cOpUnarOpParserRuleCall_1_1_0_0; }

		//right=GamlUnaryExpr
		public Assignment getRightAssignment_1_1_1() { return cRightAssignment_1_1_1; }

		//GamlUnaryExpr
		public RuleCall getRightGamlUnaryExprParserRuleCall_1_1_1_0() { return cRightGamlUnaryExprParserRuleCall_1_1_1_0; }
	}

	public class UnarOpElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "UnarOp");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final Keyword cHyphenMinusKeyword_0 = (Keyword)cAlternatives.eContents().get(0);
		private final Keyword cExclamationMarkKeyword_1 = (Keyword)cAlternatives.eContents().get(1);
		private final Keyword cMyKeyword_2 = (Keyword)cAlternatives.eContents().get(2);
		private final Keyword cTheKeyword_3 = (Keyword)cAlternatives.eContents().get(3);
		private final Keyword cNotKeyword_4 = (Keyword)cAlternatives.eContents().get(4);
		
		//UnarOp:
		//	"-" | "!" | "my" | "the" | "not";
		public ParserRule getRule() { return rule; }

		//"-" | "!" | "my" | "the" | "not"
		public Alternatives getAlternatives() { return cAlternatives; }

		//"-"
		public Keyword getHyphenMinusKeyword_0() { return cHyphenMinusKeyword_0; }

		//"!"
		public Keyword getExclamationMarkKeyword_1() { return cExclamationMarkKeyword_1; }

		//"my"
		public Keyword getMyKeyword_2() { return cMyKeyword_2; }

		//"the"
		public Keyword getTheKeyword_3() { return cTheKeyword_3; }

		//"not"
		public Keyword getNotKeyword_4() { return cNotKeyword_4; }
	}

	public class PrePrimaryExprElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "PrePrimaryExpr");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final RuleCall cTerminalExpressionParserRuleCall_0 = (RuleCall)cAlternatives.eContents().get(0);
		private final RuleCall cRightMemberRefParserRuleCall_1 = (RuleCall)cAlternatives.eContents().get(1);
		private final RuleCall cMemberRefParserRuleCall_2 = (RuleCall)cAlternatives.eContents().get(2);
		
		//// conflict between unary and binary
		//// see http://www.eclipse.org/forums/?t=msg&th=169692
		//PrePrimaryExpr returns Expression:
		//	TerminalExpression | RightMemberRef | MemberRef;
		public ParserRule getRule() { return rule; }

		//TerminalExpression | RightMemberRef | MemberRef
		public Alternatives getAlternatives() { return cAlternatives; }

		//TerminalExpression
		public RuleCall getTerminalExpressionParserRuleCall_0() { return cTerminalExpressionParserRuleCall_0; }

		//RightMemberRef
		public RuleCall getRightMemberRefParserRuleCall_1() { return cRightMemberRefParserRuleCall_1; }

		//MemberRef
		public RuleCall getMemberRefParserRuleCall_2() { return cMemberRefParserRuleCall_2; }
	}

	public class MemberRefElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "MemberRef");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cPrimaryExpressionParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cMemberRefPLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final Keyword cFullStopKeyword_1_1 = (Keyword)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightRightMemberRefParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//MemberRef returns Expression:
		//	PrimaryExpression ({MemberRefP.left=current} "." right=RightMemberRef)?;
		public ParserRule getRule() { return rule; }

		//PrimaryExpression ({MemberRefP.left=current} "." right=RightMemberRef)?
		public Group getGroup() { return cGroup; }

		//PrimaryExpression
		public RuleCall getPrimaryExpressionParserRuleCall_0() { return cPrimaryExpressionParserRuleCall_0; }

		//({MemberRefP.left=current} "." right=RightMemberRef)?
		public Group getGroup_1() { return cGroup_1; }

		//{MemberRefP.left=current}
		public Action getMemberRefPLeftAction_1_0() { return cMemberRefPLeftAction_1_0; }

		//"."
		public Keyword getFullStopKeyword_1_1() { return cFullStopKeyword_1_1; }

		//right=RightMemberRef
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }

		//RightMemberRef
		public RuleCall getRightRightMemberRefParserRuleCall_1_2_0() { return cRightRightMemberRefParserRuleCall_1_2_0; }
	}

	public class PrimaryExpressionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "PrimaryExpression");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final Group cGroup_0 = (Group)cAlternatives.eContents().get(0);
		private final Keyword cLeftParenthesisKeyword_0_0 = (Keyword)cGroup_0.eContents().get(0);
		private final RuleCall cExpressionParserRuleCall_0_1 = (RuleCall)cGroup_0.eContents().get(1);
		private final Keyword cRightParenthesisKeyword_0_2 = (Keyword)cGroup_0.eContents().get(2);
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		private final Keyword cLeftSquareBracketKeyword_1_0 = (Keyword)cGroup_1.eContents().get(0);
		private final RuleCall cMatrixParserRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		private final Keyword cRightSquareBracketKeyword_1_2 = (Keyword)cGroup_1.eContents().get(2);
		private final Group cGroup_2 = (Group)cAlternatives.eContents().get(2);
		private final Keyword cLeftCurlyBracketKeyword_2_0 = (Keyword)cGroup_2.eContents().get(0);
		private final RuleCall cPointParserRuleCall_2_1 = (RuleCall)cGroup_2.eContents().get(1);
		private final Keyword cRightCurlyBracketKeyword_2_2 = (Keyword)cGroup_2.eContents().get(2);
		
		//PrimaryExpression returns Expression:
		//	"(" Expression ")" | "[" Matrix "]" | "{" Point "}";
		public ParserRule getRule() { return rule; }

		//"(" Expression ")" | "[" Matrix "]" | "{" Point "}"
		public Alternatives getAlternatives() { return cAlternatives; }

		//"(" Expression ")"
		public Group getGroup_0() { return cGroup_0; }

		//"("
		public Keyword getLeftParenthesisKeyword_0_0() { return cLeftParenthesisKeyword_0_0; }

		//Expression
		public RuleCall getExpressionParserRuleCall_0_1() { return cExpressionParserRuleCall_0_1; }

		//")"
		public Keyword getRightParenthesisKeyword_0_2() { return cRightParenthesisKeyword_0_2; }

		//"[" Matrix "]"
		public Group getGroup_1() { return cGroup_1; }

		//"["
		public Keyword getLeftSquareBracketKeyword_1_0() { return cLeftSquareBracketKeyword_1_0; }

		//Matrix
		public RuleCall getMatrixParserRuleCall_1_1() { return cMatrixParserRuleCall_1_1; }

		//"]"
		public Keyword getRightSquareBracketKeyword_1_2() { return cRightSquareBracketKeyword_1_2; }

		//"{" Point "}"
		public Group getGroup_2() { return cGroup_2; }

		//"{"
		public Keyword getLeftCurlyBracketKeyword_2_0() { return cLeftCurlyBracketKeyword_2_0; }

		//Point
		public RuleCall getPointParserRuleCall_2_1() { return cPointParserRuleCall_2_1; }

		//"}"
		public Keyword getRightCurlyBracketKeyword_2_2() { return cRightCurlyBracketKeyword_2_2; }
	}

	public class PointElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Point");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cXAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cXExpressionParserRuleCall_0_0 = (RuleCall)cXAssignment_0.eContents().get(0);
		private final Keyword cCommaKeyword_1 = (Keyword)cGroup.eContents().get(1);
		private final Assignment cYAssignment_2 = (Assignment)cGroup.eContents().get(2);
		private final RuleCall cYExpressionParserRuleCall_2_0 = (RuleCall)cYAssignment_2.eContents().get(0);
		
		//Point:
		//	x=Expression "," y=Expression;
		public ParserRule getRule() { return rule; }

		//x=Expression "," y=Expression
		public Group getGroup() { return cGroup; }

		//x=Expression
		public Assignment getXAssignment_0() { return cXAssignment_0; }

		//Expression
		public RuleCall getXExpressionParserRuleCall_0_0() { return cXExpressionParserRuleCall_0_0; }

		//","
		public Keyword getCommaKeyword_1() { return cCommaKeyword_1; }

		//y=Expression
		public Assignment getYAssignment_2() { return cYAssignment_2; }

		//Expression
		public RuleCall getYExpressionParserRuleCall_2_0() { return cYExpressionParserRuleCall_2_0; }
	}

	public class MatrixElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Matrix");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Action cMatrixAction_0 = (Action)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Assignment cRowsAssignment_1_0 = (Assignment)cGroup_1.eContents().get(0);
		private final RuleCall cRowsRowParserRuleCall_1_0_0 = (RuleCall)cRowsAssignment_1_0.eContents().get(0);
		private final Group cGroup_1_1 = (Group)cGroup_1.eContents().get(1);
		private final Keyword cSemicolonKeyword_1_1_0 = (Keyword)cGroup_1_1.eContents().get(0);
		private final Assignment cRowsAssignment_1_1_1 = (Assignment)cGroup_1_1.eContents().get(1);
		private final RuleCall cRowsRowParserRuleCall_1_1_1_0 = (RuleCall)cRowsAssignment_1_1_1.eContents().get(0);
		
		//Matrix:
		//	{Matrix} (rows+=Row (";" rows+=Row)*)?;
		public ParserRule getRule() { return rule; }

		//{Matrix} (rows+=Row (";" rows+=Row)*)?
		public Group getGroup() { return cGroup; }

		//{Matrix}
		public Action getMatrixAction_0() { return cMatrixAction_0; }

		//(rows+=Row (";" rows+=Row)*)?
		public Group getGroup_1() { return cGroup_1; }

		//rows+=Row
		public Assignment getRowsAssignment_1_0() { return cRowsAssignment_1_0; }

		//Row
		public RuleCall getRowsRowParserRuleCall_1_0_0() { return cRowsRowParserRuleCall_1_0_0; }

		//(";" rows+=Row)*
		public Group getGroup_1_1() { return cGroup_1_1; }

		//";"
		public Keyword getSemicolonKeyword_1_1_0() { return cSemicolonKeyword_1_1_0; }

		//rows+=Row
		public Assignment getRowsAssignment_1_1_1() { return cRowsAssignment_1_1_1; }

		//Row
		public RuleCall getRowsRowParserRuleCall_1_1_1_0() { return cRowsRowParserRuleCall_1_1_1_0; }
	}

	public class RowElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "Row");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final Assignment cExprsAssignment_0 = (Assignment)cGroup.eContents().get(0);
		private final RuleCall cExprsExpressionParserRuleCall_0_0 = (RuleCall)cExprsAssignment_0.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Keyword cCommaKeyword_1_0 = (Keyword)cGroup_1.eContents().get(0);
		private final Assignment cExprsAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cExprsExpressionParserRuleCall_1_1_0 = (RuleCall)cExprsAssignment_1_1.eContents().get(0);
		
		//Row:
		//	exprs+=Expression ("," exprs+=Expression)*;
		public ParserRule getRule() { return rule; }

		//exprs+=Expression ("," exprs+=Expression)*
		public Group getGroup() { return cGroup; }

		//exprs+=Expression
		public Assignment getExprsAssignment_0() { return cExprsAssignment_0; }

		//Expression
		public RuleCall getExprsExpressionParserRuleCall_0_0() { return cExprsExpressionParserRuleCall_0_0; }

		//("," exprs+=Expression)*
		public Group getGroup_1() { return cGroup_1; }

		//","
		public Keyword getCommaKeyword_1_0() { return cCommaKeyword_1_0; }

		//exprs+=Expression
		public Assignment getExprsAssignment_1_1() { return cExprsAssignment_1_1; }

		//Expression
		public RuleCall getExprsExpressionParserRuleCall_1_1_0() { return cExprsExpressionParserRuleCall_1_1_0; }
	}

	public class RightMemberRefElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "RightMemberRef");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cAbrstractRefParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Action cMemberRefRLeftAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final Keyword cFullStopKeyword_1_1 = (Keyword)cGroup_1.eContents().get(1);
		private final Assignment cRightAssignment_1_2 = (Assignment)cGroup_1.eContents().get(2);
		private final RuleCall cRightRightMemberRefParserRuleCall_1_2_0 = (RuleCall)cRightAssignment_1_2.eContents().get(0);
		
		//RightMemberRef returns Expression:
		//	AbrstractRef ({MemberRefR.left=current} "." right=RightMemberRef)?;
		public ParserRule getRule() { return rule; }

		//AbrstractRef ({MemberRefR.left=current} "." right=RightMemberRef)?
		public Group getGroup() { return cGroup; }

		//AbrstractRef
		public RuleCall getAbrstractRefParserRuleCall_0() { return cAbrstractRefParserRuleCall_0; }

		//({MemberRefR.left=current} "." right=RightMemberRef)?
		public Group getGroup_1() { return cGroup_1; }

		//{MemberRefR.left=current}
		public Action getMemberRefRLeftAction_1_0() { return cMemberRefRLeftAction_1_0; }

		//"."
		public Keyword getFullStopKeyword_1_1() { return cFullStopKeyword_1_1; }

		//right=RightMemberRef
		public Assignment getRightAssignment_1_2() { return cRightAssignment_1_2; }

		//RightMemberRef
		public RuleCall getRightRightMemberRefParserRuleCall_1_2_0() { return cRightRightMemberRefParserRuleCall_1_2_0; }
	}

	public class AbrstractRefElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "AbrstractRef");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cVariableRefParserRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Alternatives cAlternatives_1 = (Alternatives)cGroup.eContents().get(1);
		private final Group cGroup_1_0 = (Group)cAlternatives_1.eContents().get(0);
		private final Action cFunctionRefFuncAction_1_0_0 = (Action)cGroup_1_0.eContents().get(0);
		private final Keyword cLeftParenthesisKeyword_1_0_1 = (Keyword)cGroup_1_0.eContents().get(1);
		private final Group cGroup_1_0_2 = (Group)cGroup_1_0.eContents().get(2);
		private final Assignment cArgsAssignment_1_0_2_0 = (Assignment)cGroup_1_0_2.eContents().get(0);
		private final RuleCall cArgsExpressionParserRuleCall_1_0_2_0_0 = (RuleCall)cArgsAssignment_1_0_2_0.eContents().get(0);
		private final Group cGroup_1_0_2_1 = (Group)cGroup_1_0_2.eContents().get(1);
		private final Keyword cCommaKeyword_1_0_2_1_0 = (Keyword)cGroup_1_0_2_1.eContents().get(0);
		private final Assignment cArgsAssignment_1_0_2_1_1 = (Assignment)cGroup_1_0_2_1.eContents().get(1);
		private final RuleCall cArgsExpressionParserRuleCall_1_0_2_1_1_0 = (RuleCall)cArgsAssignment_1_0_2_1_1.eContents().get(0);
		private final Keyword cRightParenthesisKeyword_1_0_3 = (Keyword)cGroup_1_0.eContents().get(3);
		private final Group cGroup_1_1 = (Group)cAlternatives_1.eContents().get(1);
		private final Action cArrayRefArrayAction_1_1_0 = (Action)cGroup_1_1.eContents().get(0);
		private final Keyword cLeftSquareBracketKeyword_1_1_1 = (Keyword)cGroup_1_1.eContents().get(1);
		private final Group cGroup_1_1_2 = (Group)cGroup_1_1.eContents().get(2);
		private final Assignment cArgsAssignment_1_1_2_0 = (Assignment)cGroup_1_1_2.eContents().get(0);
		private final RuleCall cArgsExpressionParserRuleCall_1_1_2_0_0 = (RuleCall)cArgsAssignment_1_1_2_0.eContents().get(0);
		private final Group cGroup_1_1_2_1 = (Group)cGroup_1_1_2.eContents().get(1);
		private final Keyword cCommaKeyword_1_1_2_1_0 = (Keyword)cGroup_1_1_2_1.eContents().get(0);
		private final Assignment cArgsAssignment_1_1_2_1_1 = (Assignment)cGroup_1_1_2_1.eContents().get(1);
		private final RuleCall cArgsExpressionParserRuleCall_1_1_2_1_1_0 = (RuleCall)cArgsAssignment_1_1_2_1_1.eContents().get(0);
		private final Keyword cRightSquareBracketKeyword_1_1_3 = (Keyword)cGroup_1_1.eContents().get(3);
		
		//AbrstractRef returns Expression:
		//	VariableRef ({FunctionRef.func=current} "(" (args+=Expression ("," args+=Expression)*)? ")" | {ArrayRef.array=current}
		//	"[" (args+=Expression ("," args+=Expression)*)? "]")?;
		public ParserRule getRule() { return rule; }

		//VariableRef ({FunctionRef.func=current} "(" (args+=Expression ("," args+=Expression)*)? ")" | {ArrayRef.array=current}
		//"[" (args+=Expression ("," args+=Expression)*)? "]")?
		public Group getGroup() { return cGroup; }

		//VariableRef
		public RuleCall getVariableRefParserRuleCall_0() { return cVariableRefParserRuleCall_0; }

		//({FunctionRef.func=current} "(" (args+=Expression ("," args+=Expression)*)? ")" | {ArrayRef.array=current} "["
		//(args+=Expression ("," args+=Expression)*)? "]")?
		public Alternatives getAlternatives_1() { return cAlternatives_1; }

		//{FunctionRef.func=current} "(" (args+=Expression ("," args+=Expression)*)? ")"
		public Group getGroup_1_0() { return cGroup_1_0; }

		//{FunctionRef.func=current}
		public Action getFunctionRefFuncAction_1_0_0() { return cFunctionRefFuncAction_1_0_0; }

		//"("
		public Keyword getLeftParenthesisKeyword_1_0_1() { return cLeftParenthesisKeyword_1_0_1; }

		//(args+=Expression ("," args+=Expression)*)?
		public Group getGroup_1_0_2() { return cGroup_1_0_2; }

		//args+=Expression
		public Assignment getArgsAssignment_1_0_2_0() { return cArgsAssignment_1_0_2_0; }

		//Expression
		public RuleCall getArgsExpressionParserRuleCall_1_0_2_0_0() { return cArgsExpressionParserRuleCall_1_0_2_0_0; }

		//("," args+=Expression)*
		public Group getGroup_1_0_2_1() { return cGroup_1_0_2_1; }

		//","
		public Keyword getCommaKeyword_1_0_2_1_0() { return cCommaKeyword_1_0_2_1_0; }

		//args+=Expression
		public Assignment getArgsAssignment_1_0_2_1_1() { return cArgsAssignment_1_0_2_1_1; }

		//Expression
		public RuleCall getArgsExpressionParserRuleCall_1_0_2_1_1_0() { return cArgsExpressionParserRuleCall_1_0_2_1_1_0; }

		//")"
		public Keyword getRightParenthesisKeyword_1_0_3() { return cRightParenthesisKeyword_1_0_3; }

		//{ArrayRef.array=current} "[" (args+=Expression ("," args+=Expression)*)? "]"
		public Group getGroup_1_1() { return cGroup_1_1; }

		//{ArrayRef.array=current}
		public Action getArrayRefArrayAction_1_1_0() { return cArrayRefArrayAction_1_1_0; }

		//"["
		public Keyword getLeftSquareBracketKeyword_1_1_1() { return cLeftSquareBracketKeyword_1_1_1; }

		//(args+=Expression ("," args+=Expression)*)?
		public Group getGroup_1_1_2() { return cGroup_1_1_2; }

		//args+=Expression
		public Assignment getArgsAssignment_1_1_2_0() { return cArgsAssignment_1_1_2_0; }

		//Expression
		public RuleCall getArgsExpressionParserRuleCall_1_1_2_0_0() { return cArgsExpressionParserRuleCall_1_1_2_0_0; }

		//("," args+=Expression)*
		public Group getGroup_1_1_2_1() { return cGroup_1_1_2_1; }

		//","
		public Keyword getCommaKeyword_1_1_2_1_0() { return cCommaKeyword_1_1_2_1_0; }

		//args+=Expression
		public Assignment getArgsAssignment_1_1_2_1_1() { return cArgsAssignment_1_1_2_1_1; }

		//Expression
		public RuleCall getArgsExpressionParserRuleCall_1_1_2_1_1_0() { return cArgsExpressionParserRuleCall_1_1_2_1_1_0; }

		//"]"
		public Keyword getRightSquareBracketKeyword_1_1_3() { return cRightSquareBracketKeyword_1_1_3; }
	}

	public class VariableRefElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "VariableRef");
		private final Assignment cRefAssignment = (Assignment)rule.eContents().get(1);
		private final CrossReference cRefAbstractDefinitionCrossReference_0 = (CrossReference)cRefAssignment.eContents().get(0);
		private final RuleCall cRefAbstractDefinitionIDTerminalRuleCall_0_1 = (RuleCall)cRefAbstractDefinitionCrossReference_0.eContents().get(1);
		
		//VariableRef:
		//	ref=[AbstractDefinition];
		public ParserRule getRule() { return rule; }

		//ref=[AbstractDefinition]
		public Assignment getRefAssignment() { return cRefAssignment; }

		//[AbstractDefinition]
		public CrossReference getRefAbstractDefinitionCrossReference_0() { return cRefAbstractDefinitionCrossReference_0; }

		//ID
		public RuleCall getRefAbstractDefinitionIDTerminalRuleCall_0_1() { return cRefAbstractDefinitionIDTerminalRuleCall_0_1; }
	}

	public class TerminalExpressionElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "TerminalExpression");
		private final Alternatives cAlternatives = (Alternatives)rule.eContents().get(1);
		private final Group cGroup_0 = (Group)cAlternatives.eContents().get(0);
		private final Action cIntLiteralAction_0_0 = (Action)cGroup_0.eContents().get(0);
		private final Assignment cValueAssignment_0_1 = (Assignment)cGroup_0.eContents().get(1);
		private final RuleCall cValueINTTerminalRuleCall_0_1_0 = (RuleCall)cValueAssignment_0_1.eContents().get(0);
		private final Group cGroup_1 = (Group)cAlternatives.eContents().get(1);
		private final Action cDoubleLiteralAction_1_0 = (Action)cGroup_1.eContents().get(0);
		private final Assignment cValueAssignment_1_1 = (Assignment)cGroup_1.eContents().get(1);
		private final RuleCall cValueDOUBLETerminalRuleCall_1_1_0 = (RuleCall)cValueAssignment_1_1.eContents().get(0);
		private final Group cGroup_2 = (Group)cAlternatives.eContents().get(2);
		private final Action cColorLiteralAction_2_0 = (Action)cGroup_2.eContents().get(0);
		private final Assignment cValueAssignment_2_1 = (Assignment)cGroup_2.eContents().get(1);
		private final RuleCall cValueCOLORTerminalRuleCall_2_1_0 = (RuleCall)cValueAssignment_2_1.eContents().get(0);
		private final Group cGroup_3 = (Group)cAlternatives.eContents().get(3);
		private final Action cStringLiteralAction_3_0 = (Action)cGroup_3.eContents().get(0);
		private final Assignment cValueAssignment_3_1 = (Assignment)cGroup_3.eContents().get(1);
		private final RuleCall cValueSTRINGTerminalRuleCall_3_1_0 = (RuleCall)cValueAssignment_3_1.eContents().get(0);
		private final Group cGroup_4 = (Group)cAlternatives.eContents().get(4);
		private final Action cBooleanLiteralAction_4_0 = (Action)cGroup_4.eContents().get(0);
		private final Assignment cValueAssignment_4_1 = (Assignment)cGroup_4.eContents().get(1);
		private final RuleCall cValueBOOLEANTerminalRuleCall_4_1_0 = (RuleCall)cValueAssignment_4_1.eContents().get(0);
		
		//TerminalExpression:
		//	{IntLiteral} value=INT | {DoubleLiteral} value=DOUBLE | {ColorLiteral} value=COLOR | {StringLiteral} value=STRING |
		//	{BooleanLiteral} value=BOOLEAN;
		public ParserRule getRule() { return rule; }

		//{IntLiteral} value=INT | {DoubleLiteral} value=DOUBLE | {ColorLiteral} value=COLOR | {StringLiteral} value=STRING |
		//{BooleanLiteral} value=BOOLEAN
		public Alternatives getAlternatives() { return cAlternatives; }

		//{IntLiteral} value=INT
		public Group getGroup_0() { return cGroup_0; }

		//{IntLiteral}
		public Action getIntLiteralAction_0_0() { return cIntLiteralAction_0_0; }

		//value=INT
		public Assignment getValueAssignment_0_1() { return cValueAssignment_0_1; }

		//INT
		public RuleCall getValueINTTerminalRuleCall_0_1_0() { return cValueINTTerminalRuleCall_0_1_0; }

		//{DoubleLiteral} value=DOUBLE
		public Group getGroup_1() { return cGroup_1; }

		//{DoubleLiteral}
		public Action getDoubleLiteralAction_1_0() { return cDoubleLiteralAction_1_0; }

		//value=DOUBLE
		public Assignment getValueAssignment_1_1() { return cValueAssignment_1_1; }

		//DOUBLE
		public RuleCall getValueDOUBLETerminalRuleCall_1_1_0() { return cValueDOUBLETerminalRuleCall_1_1_0; }

		//{ColorLiteral} value=COLOR
		public Group getGroup_2() { return cGroup_2; }

		//{ColorLiteral}
		public Action getColorLiteralAction_2_0() { return cColorLiteralAction_2_0; }

		//value=COLOR
		public Assignment getValueAssignment_2_1() { return cValueAssignment_2_1; }

		//COLOR
		public RuleCall getValueCOLORTerminalRuleCall_2_1_0() { return cValueCOLORTerminalRuleCall_2_1_0; }

		//{StringLiteral} value=STRING
		public Group getGroup_3() { return cGroup_3; }

		//{StringLiteral}
		public Action getStringLiteralAction_3_0() { return cStringLiteralAction_3_0; }

		//value=STRING
		public Assignment getValueAssignment_3_1() { return cValueAssignment_3_1; }

		//STRING
		public RuleCall getValueSTRINGTerminalRuleCall_3_1_0() { return cValueSTRINGTerminalRuleCall_3_1_0; }

		//{BooleanLiteral} value=BOOLEAN
		public Group getGroup_4() { return cGroup_4; }

		//{BooleanLiteral}
		public Action getBooleanLiteralAction_4_0() { return cBooleanLiteralAction_4_0; }

		//value=BOOLEAN
		public Assignment getValueAssignment_4_1() { return cValueAssignment_4_1; }

		//BOOLEAN
		public RuleCall getValueBOOLEANTerminalRuleCall_4_1_0() { return cValueBOOLEANTerminalRuleCall_4_1_0; }
	}

	public class FQNElements extends AbstractParserRuleElementFinder {
		private final ParserRule rule = (ParserRule) GrammarUtil.findRuleForName(getGrammar(), "FQN");
		private final Group cGroup = (Group)rule.eContents().get(1);
		private final RuleCall cIDTerminalRuleCall_0 = (RuleCall)cGroup.eContents().get(0);
		private final Group cGroup_1 = (Group)cGroup.eContents().get(1);
		private final Keyword cFullStopKeyword_1_0 = (Keyword)cGroup_1.eContents().get(0);
		private final RuleCall cIDTerminalRuleCall_1_1 = (RuleCall)cGroup_1.eContents().get(1);
		
		//// -----
		//FQN:
		//	ID ("." ID)*;
		public ParserRule getRule() { return rule; }

		//ID ("." ID)*
		public Group getGroup() { return cGroup; }

		//ID
		public RuleCall getIDTerminalRuleCall_0() { return cIDTerminalRuleCall_0; }

		//("." ID)*
		public Group getGroup_1() { return cGroup_1; }

		//"."
		public Keyword getFullStopKeyword_1_0() { return cFullStopKeyword_1_0; }

		//ID
		public RuleCall getIDTerminalRuleCall_1_1() { return cIDTerminalRuleCall_1_1; }
	}
	
	
	private ModelElements pModel;
	private ImportElements pImport;
	private ImportedFQNElements pImportedFQN;
	private GamlLangDefElements pGamlLangDef;
	private DefKeywordElements pDefKeyword;
	private GamlBlockElements pGamlBlock;
	private DefFacetElements pDefFacet;
	private DefBinaryOpElements pDefBinaryOp;
	private DefReservedElements pDefReserved;
	private DefUnitElements pDefUnit;
	private AbstractGamlRefElements pAbstractGamlRef;
	private GamlKeywordRefElements pGamlKeywordRef;
	private GamlFacetRefElements pGamlFacetRef;
	private GamlBinarOpRefElements pGamlBinarOpRef;
	private GamlUnitRefElements pGamlUnitRef;
	private GamlReservedRefElements pGamlReservedRef;
	private StatementElements pStatement;
	private SubStatementElements pSubStatement;
	private SetEvalElements pSetEval;
	private DefinitionElements pDefinition;
	private EvaluationElements pEvaluation;
	private FacetExprElements pFacetExpr;
	private BlockElements pBlock;
	private AbstractDefinitionElements pAbstractDefinition;
	private ExpressionElements pExpression;
	private AssignmentOpElements pAssignmentOp;
	private TernExpElements pTernExp;
	private OrExpElements pOrExp;
	private AndExpElements pAndExp;
	private RelationalElements pRelational;
	private PairExprElements pPairExpr;
	private AdditionElements pAddition;
	private MultiplicationElements pMultiplication;
	private GamlBinExprElements pGamlBinExpr;
	private PowerElements pPower;
	private GamlUnitExprElements pGamlUnitExpr;
	private GamlUnaryExprElements pGamlUnaryExpr;
	private UnarOpElements pUnarOp;
	private PrePrimaryExprElements pPrePrimaryExpr;
	private MemberRefElements pMemberRef;
	private PrimaryExpressionElements pPrimaryExpression;
	private PointElements pPoint;
	private MatrixElements pMatrix;
	private RowElements pRow;
	private RightMemberRefElements pRightMemberRef;
	private AbrstractRefElements pAbrstractRef;
	private VariableRefElements pVariableRef;
	private TerminalExpressionElements pTerminalExpression;
	private FQNElements pFQN;
	private TerminalRule tINT;
	private TerminalRule tBOOLEAN;
	private TerminalRule tID;
	private TerminalRule tCOLOR;
	private TerminalRule tDOUBLE;
	private TerminalRule tSTRING;
	private TerminalRule tML_COMMENT;
	private TerminalRule tSL_COMMENT;
	private TerminalRule tWS;
	private TerminalRule tANY_OTHER;
	
	private final GrammarProvider grammarProvider;

	@Inject
	public GamlGrammarAccess(GrammarProvider grammarProvider) {
		this.grammarProvider = grammarProvider;
	}
	
	public Grammar getGrammar() {	
		return grammarProvider.getGrammar(this);
	}
	

	
	//Model:
	//	"model" name=FQN imports+=Import* gaml=GamlLangDef? statements+=Statement*;
	public ModelElements getModelAccess() {
		return (pModel != null) ? pModel : (pModel = new ModelElements());
	}
	
	public ParserRule getModelRule() {
		return getModelAccess().getRule();
	}

	//// feature must be named importURI
	//Import:
	//	"import" importURI=STRING;
	public ImportElements getImportAccess() {
		return (pImport != null) ? pImport : (pImport = new ImportElements());
	}
	
	public ParserRule getImportRule() {
		return getImportAccess().getRule();
	}

	//ImportedFQN:
	//	FQN ("." "*")?;
	public ImportedFQNElements getImportedFQNAccess() {
		return (pImportedFQN != null) ? pImportedFQN : (pImportedFQN = new ImportedFQNElements());
	}
	
	public ParserRule getImportedFQNRule() {
		return getImportedFQNAccess().getRule();
	}

	//GamlLangDef:
	//	"_gaml" "{" (k+=DefKeyword | f+=DefFacet | b+=DefBinaryOp | r+=DefReserved | u+=DefUnit)+ "}";
	public GamlLangDefElements getGamlLangDefAccess() {
		return (pGamlLangDef != null) ? pGamlLangDef : (pGamlLangDef = new GamlLangDefElements());
	}
	
	public ParserRule getGamlLangDefRule() {
		return getGamlLangDefAccess().getRule();
	}

	//DefKeyword:
	//	"_keyword" name=ID (block=GamlBlock | ";");
	public DefKeywordElements getDefKeywordAccess() {
		return (pDefKeyword != null) ? pDefKeyword : (pDefKeyword = new DefKeywordElements());
	}
	
	public ParserRule getDefKeywordRule() {
		return getDefKeywordAccess().getRule();
	}

	//GamlBlock:
	//	"{" {GamlBlock} ("_facets" "[" facets+=[DefFacet] ("," facets+=[DefFacet])* "]")? ("_children" "["
	//	childs+=[DefKeyword] ("," childs+=[DefKeyword])* "]")? "}";
	public GamlBlockElements getGamlBlockAccess() {
		return (pGamlBlock != null) ? pGamlBlock : (pGamlBlock = new GamlBlockElements());
	}
	
	public ParserRule getGamlBlockRule() {
		return getGamlBlockAccess().getRule();
	}

	//DefFacet:
	//	"_facet" name=ID (":" type=[DefReserved])? ("=" default=TerminalExpression)? ";";
	public DefFacetElements getDefFacetAccess() {
		return (pDefFacet != null) ? pDefFacet : (pDefFacet = new DefFacetElements());
	}
	
	public ParserRule getDefFacetRule() {
		return getDefFacetAccess().getRule();
	}

	//DefBinaryOp:
	//	"_binary" name=ID ";";
	public DefBinaryOpElements getDefBinaryOpAccess() {
		return (pDefBinaryOp != null) ? pDefBinaryOp : (pDefBinaryOp = new DefBinaryOpElements());
	}
	
	public ParserRule getDefBinaryOpRule() {
		return getDefBinaryOpAccess().getRule();
	}

	//DefReserved:
	//	"_reserved" name=ID (":" type=[DefReserved])? ("=" value=TerminalExpression)? ";";
	public DefReservedElements getDefReservedAccess() {
		return (pDefReserved != null) ? pDefReserved : (pDefReserved = new DefReservedElements());
	}
	
	public ParserRule getDefReservedRule() {
		return getDefReservedAccess().getRule();
	}

	//DefUnit:
	//	"_unit" name=ID ("=" coef=DOUBLE)? ";";
	public DefUnitElements getDefUnitAccess() {
		return (pDefUnit != null) ? pDefUnit : (pDefUnit = new DefUnitElements());
	}
	
	public ParserRule getDefUnitRule() {
		return getDefUnitAccess().getRule();
	}

	//// for highlight
	//AbstractGamlRef:
	//	GamlFacetRef | GamlKeywordRef | GamlBinarOpRef | GamlUnitRef | GamlReservedRef;
	public AbstractGamlRefElements getAbstractGamlRefAccess() {
		return (pAbstractGamlRef != null) ? pAbstractGamlRef : (pAbstractGamlRef = new AbstractGamlRefElements());
	}
	
	public ParserRule getAbstractGamlRefRule() {
		return getAbstractGamlRefAccess().getRule();
	}

	//// for highlight
	//GamlKeywordRef:
	//	ref=[DefKeyword];
	public GamlKeywordRefElements getGamlKeywordRefAccess() {
		return (pGamlKeywordRef != null) ? pGamlKeywordRef : (pGamlKeywordRef = new GamlKeywordRefElements());
	}
	
	public ParserRule getGamlKeywordRefRule() {
		return getGamlKeywordRefAccess().getRule();
	}

	//GamlFacetRef hidden():
	//	ref=[DefFacet] ":";
	public GamlFacetRefElements getGamlFacetRefAccess() {
		return (pGamlFacetRef != null) ? pGamlFacetRef : (pGamlFacetRef = new GamlFacetRefElements());
	}
	
	public ParserRule getGamlFacetRefRule() {
		return getGamlFacetRefAccess().getRule();
	}

	//GamlBinarOpRef:
	//	ref=[DefBinaryOp];
	public GamlBinarOpRefElements getGamlBinarOpRefAccess() {
		return (pGamlBinarOpRef != null) ? pGamlBinarOpRef : (pGamlBinarOpRef = new GamlBinarOpRefElements());
	}
	
	public ParserRule getGamlBinarOpRefRule() {
		return getGamlBinarOpRefAccess().getRule();
	}

	//GamlUnitRef:
	//	ref=[DefUnit];
	public GamlUnitRefElements getGamlUnitRefAccess() {
		return (pGamlUnitRef != null) ? pGamlUnitRef : (pGamlUnitRef = new GamlUnitRefElements());
	}
	
	public ParserRule getGamlUnitRefRule() {
		return getGamlUnitRefAccess().getRule();
	}

	//GamlReservedRef:
	//	ref=[DefReserved];
	public GamlReservedRefElements getGamlReservedRefAccess() {
		return (pGamlReservedRef != null) ? pGamlReservedRef : (pGamlReservedRef = new GamlReservedRefElements());
	}
	
	public ParserRule getGamlReservedRefRule() {
		return getGamlReservedRefAccess().getRule();
	}

	//Statement:
	//	SetEval | SubStatement;
	public StatementElements getStatementAccess() {
		return (pStatement != null) ? pStatement : (pStatement = new StatementElements());
	}
	
	public ParserRule getStatementRule() {
		return getStatementAccess().getRule();
	}

	//SubStatement:
	//	Definition | Evaluation;
	public SubStatementElements getSubStatementAccess() {
		return (pSubStatement != null) ? pSubStatement : (pSubStatement = new SubStatementElements());
	}
	
	public ParserRule getSubStatementRule() {
		return getSubStatementAccess().getRule();
	}

	//SetEval:
	//	"set" var=Expression facets+=FacetExpr* (block=Block | ";");
	public SetEvalElements getSetEvalAccess() {
		return (pSetEval != null) ? pSetEval : (pSetEval = new SetEvalElements());
	}
	
	public ParserRule getSetEvalRule() {
		return getSetEvalAccess().getRule();
	}

	//// const/var/let/species/grid/gis/state/arg
	//Definition:
	//	key=GamlKeywordRef name=ID facets+=FacetExpr* (block=Block | ";");
	public DefinitionElements getDefinitionAccess() {
		return (pDefinition != null) ? pDefinition : (pDefinition = new DefinitionElements());
	}
	
	public ParserRule getDefinitionRule() {
		return getDefinitionAccess().getRule();
	}

	//Evaluation:
	//	key=GamlKeywordRef (":" var=Expression)? facets+=FacetExpr* (block=Block | ";");
	public EvaluationElements getEvaluationAccess() {
		return (pEvaluation != null) ? pEvaluation : (pEvaluation = new EvaluationElements());
	}
	
	public ParserRule getEvaluationRule() {
		return getEvaluationAccess().getRule();
	}

	//FacetExpr:
	//	"returns:" name=ID | key=GamlFacetRef expr=Expression;
	public FacetExprElements getFacetExprAccess() {
		return (pFacetExpr != null) ? pFacetExpr : (pFacetExpr = new FacetExprElements());
	}
	
	public ParserRule getFacetExprRule() {
		return getFacetExprAccess().getRule();
	}

	//Block:
	//	{Block} "{" statements+=Statement* "}";
	public BlockElements getBlockAccess() {
		return (pBlock != null) ? pBlock : (pBlock = new BlockElements());
	}
	
	public ParserRule getBlockRule() {
		return getBlockAccess().getRule();
	}

	//// for variable reference
	//AbstractDefinition:
	//	Definition | DefReserved | FacetExpr;
	public AbstractDefinitionElements getAbstractDefinitionAccess() {
		return (pAbstractDefinition != null) ? pAbstractDefinition : (pAbstractDefinition = new AbstractDefinitionElements());
	}
	
	public ParserRule getAbstractDefinitionRule() {
		return getAbstractDefinitionAccess().getRule();
	}

	//Expression:
	//	AssignmentOp;
	public ExpressionElements getExpressionAccess() {
		return (pExpression != null) ? pExpression : (pExpression = new ExpressionElements());
	}
	
	public ParserRule getExpressionRule() {
		return getExpressionAccess().getRule();
	}

	//AssignmentOp returns Expression:
	//	TernExp (({AssignPlus.left=current} "+=" | {AssignMin.left=current} "-=" | {AssignMult.left=current} "*=" |
	//	{AssignDiv.left=current} "/=") right=TernExp)?;
	public AssignmentOpElements getAssignmentOpAccess() {
		return (pAssignmentOp != null) ? pAssignmentOp : (pAssignmentOp = new AssignmentOpElements());
	}
	
	public ParserRule getAssignmentOpRule() {
		return getAssignmentOpAccess().getRule();
	}

	//TernExp returns Expression:
	//	OrExp ({Ternary.condition=current} "?" ifTrue=OrExp ":" ifFalse=OrExp)?;
	public TernExpElements getTernExpAccess() {
		return (pTernExp != null) ? pTernExp : (pTernExp = new TernExpElements());
	}
	
	public ParserRule getTernExpRule() {
		return getTernExpAccess().getRule();
	}

	//OrExp returns Expression:
	//	AndExp ({Or.left=current} "or" right=AndExp)*;
	public OrExpElements getOrExpAccess() {
		return (pOrExp != null) ? pOrExp : (pOrExp = new OrExpElements());
	}
	
	public ParserRule getOrExpRule() {
		return getOrExpAccess().getRule();
	}

	//AndExp returns Expression:
	//	Relational ({And.left=current} "and" right=Relational)*;
	public AndExpElements getAndExpAccess() {
		return (pAndExp != null) ? pAndExp : (pAndExp = new AndExpElements());
	}
	
	public ParserRule getAndExpRule() {
		return getAndExpAccess().getRule();
	}

	//Relational returns Expression:
	//	PairExpr (({RelNotEq.left=current} "!=" | {RelEq.left=current} "=" | {RelEqEq.left=current} "==" |
	//	{RelLtEq.left=current} ">=" | {RelGtEq.left=current} "<=" | {RelLt.left=current} "<" | {RelGt.left=current} ">")
	//	right=PairExpr)?;
	public RelationalElements getRelationalAccess() {
		return (pRelational != null) ? pRelational : (pRelational = new RelationalElements());
	}
	
	public ParserRule getRelationalRule() {
		return getRelationalAccess().getRule();
	}

	//PairExpr returns Expression:
	//	Addition (({Pair.left=current} "::") right=Addition)?;
	public PairExprElements getPairExprAccess() {
		return (pPairExpr != null) ? pPairExpr : (pPairExpr = new PairExprElements());
	}
	
	public ParserRule getPairExprRule() {
		return getPairExprAccess().getRule();
	}

	//Addition returns Expression:
	//	Multiplication (({Plus.left=current} "+" | {Minus.left=current} "-") right=Multiplication)*;
	public AdditionElements getAdditionAccess() {
		return (pAddition != null) ? pAddition : (pAddition = new AdditionElements());
	}
	
	public ParserRule getAdditionRule() {
		return getAdditionAccess().getRule();
	}

	//Multiplication returns Expression:
	//	GamlBinExpr (({Multi.left=current} "*" | {Div.left=current} "/") right=GamlBinExpr)*;
	public MultiplicationElements getMultiplicationAccess() {
		return (pMultiplication != null) ? pMultiplication : (pMultiplication = new MultiplicationElements());
	}
	
	public ParserRule getMultiplicationRule() {
		return getMultiplicationAccess().getRule();
	}

	//GamlBinExpr returns Expression:
	//	Power (({GamlBinary.left=current} op=GamlBinarOpRef) right=Power)*;
	public GamlBinExprElements getGamlBinExprAccess() {
		return (pGamlBinExpr != null) ? pGamlBinExpr : (pGamlBinExpr = new GamlBinExprElements());
	}
	
	public ParserRule getGamlBinExprRule() {
		return getGamlBinExprAccess().getRule();
	}

	//Power returns Expression:
	//	GamlUnitExpr (({Pow.left=current} "^") right=GamlUnitExpr)*;
	public PowerElements getPowerAccess() {
		return (pPower != null) ? pPower : (pPower = new PowerElements());
	}
	
	public ParserRule getPowerRule() {
		return getPowerAccess().getRule();
	}

	//GamlUnitExpr returns Expression:
	//	GamlUnaryExpr (({Unit.left=current} "#") right=GamlUnitRef)?;
	public GamlUnitExprElements getGamlUnitExprAccess() {
		return (pGamlUnitExpr != null) ? pGamlUnitExpr : (pGamlUnitExpr = new GamlUnitExprElements());
	}
	
	public ParserRule getGamlUnitExprRule() {
		return getGamlUnitExprAccess().getRule();
	}

	//GamlUnaryExpr returns Expression:
	//	PrePrimaryExpr | {GamlUnary} (op=UnarOp right=GamlUnaryExpr);
	public GamlUnaryExprElements getGamlUnaryExprAccess() {
		return (pGamlUnaryExpr != null) ? pGamlUnaryExpr : (pGamlUnaryExpr = new GamlUnaryExprElements());
	}
	
	public ParserRule getGamlUnaryExprRule() {
		return getGamlUnaryExprAccess().getRule();
	}

	//UnarOp:
	//	"-" | "!" | "my" | "the" | "not";
	public UnarOpElements getUnarOpAccess() {
		return (pUnarOp != null) ? pUnarOp : (pUnarOp = new UnarOpElements());
	}
	
	public ParserRule getUnarOpRule() {
		return getUnarOpAccess().getRule();
	}

	//// conflict between unary and binary
	//// see http://www.eclipse.org/forums/?t=msg&th=169692
	//PrePrimaryExpr returns Expression:
	//	TerminalExpression | RightMemberRef | MemberRef;
	public PrePrimaryExprElements getPrePrimaryExprAccess() {
		return (pPrePrimaryExpr != null) ? pPrePrimaryExpr : (pPrePrimaryExpr = new PrePrimaryExprElements());
	}
	
	public ParserRule getPrePrimaryExprRule() {
		return getPrePrimaryExprAccess().getRule();
	}

	//MemberRef returns Expression:
	//	PrimaryExpression ({MemberRefP.left=current} "." right=RightMemberRef)?;
	public MemberRefElements getMemberRefAccess() {
		return (pMemberRef != null) ? pMemberRef : (pMemberRef = new MemberRefElements());
	}
	
	public ParserRule getMemberRefRule() {
		return getMemberRefAccess().getRule();
	}

	//PrimaryExpression returns Expression:
	//	"(" Expression ")" | "[" Matrix "]" | "{" Point "}";
	public PrimaryExpressionElements getPrimaryExpressionAccess() {
		return (pPrimaryExpression != null) ? pPrimaryExpression : (pPrimaryExpression = new PrimaryExpressionElements());
	}
	
	public ParserRule getPrimaryExpressionRule() {
		return getPrimaryExpressionAccess().getRule();
	}

	//Point:
	//	x=Expression "," y=Expression;
	public PointElements getPointAccess() {
		return (pPoint != null) ? pPoint : (pPoint = new PointElements());
	}
	
	public ParserRule getPointRule() {
		return getPointAccess().getRule();
	}

	//Matrix:
	//	{Matrix} (rows+=Row (";" rows+=Row)*)?;
	public MatrixElements getMatrixAccess() {
		return (pMatrix != null) ? pMatrix : (pMatrix = new MatrixElements());
	}
	
	public ParserRule getMatrixRule() {
		return getMatrixAccess().getRule();
	}

	//Row:
	//	exprs+=Expression ("," exprs+=Expression)*;
	public RowElements getRowAccess() {
		return (pRow != null) ? pRow : (pRow = new RowElements());
	}
	
	public ParserRule getRowRule() {
		return getRowAccess().getRule();
	}

	//RightMemberRef returns Expression:
	//	AbrstractRef ({MemberRefR.left=current} "." right=RightMemberRef)?;
	public RightMemberRefElements getRightMemberRefAccess() {
		return (pRightMemberRef != null) ? pRightMemberRef : (pRightMemberRef = new RightMemberRefElements());
	}
	
	public ParserRule getRightMemberRefRule() {
		return getRightMemberRefAccess().getRule();
	}

	//AbrstractRef returns Expression:
	//	VariableRef ({FunctionRef.func=current} "(" (args+=Expression ("," args+=Expression)*)? ")" | {ArrayRef.array=current}
	//	"[" (args+=Expression ("," args+=Expression)*)? "]")?;
	public AbrstractRefElements getAbrstractRefAccess() {
		return (pAbrstractRef != null) ? pAbrstractRef : (pAbrstractRef = new AbrstractRefElements());
	}
	
	public ParserRule getAbrstractRefRule() {
		return getAbrstractRefAccess().getRule();
	}

	//VariableRef:
	//	ref=[AbstractDefinition];
	public VariableRefElements getVariableRefAccess() {
		return (pVariableRef != null) ? pVariableRef : (pVariableRef = new VariableRefElements());
	}
	
	public ParserRule getVariableRefRule() {
		return getVariableRefAccess().getRule();
	}

	//TerminalExpression:
	//	{IntLiteral} value=INT | {DoubleLiteral} value=DOUBLE | {ColorLiteral} value=COLOR | {StringLiteral} value=STRING |
	//	{BooleanLiteral} value=BOOLEAN;
	public TerminalExpressionElements getTerminalExpressionAccess() {
		return (pTerminalExpression != null) ? pTerminalExpression : (pTerminalExpression = new TerminalExpressionElements());
	}
	
	public ParserRule getTerminalExpressionRule() {
		return getTerminalExpressionAccess().getRule();
	}

	//// -----
	//FQN:
	//	ID ("." ID)*;
	public FQNElements getFQNAccess() {
		return (pFQN != null) ? pFQN : (pFQN = new FQNElements());
	}
	
	public ParserRule getFQNRule() {
		return getFQNAccess().getRule();
	}

	//terminal INT returns ecore::EInt:
	//	"0" | "1".."9" "0".."9"*;
	public TerminalRule getINTRule() {
		return (tINT != null) ? tINT : (tINT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "INT"));
	} 

	//terminal BOOLEAN returns ecore::EBoolean:
	//	"true" | "false";
	public TerminalRule getBOOLEANRule() {
		return (tBOOLEAN != null) ? tBOOLEAN : (tBOOLEAN = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "BOOLEAN"));
	} 

	//terminal ID:
	//	("a".."z" | "A".."Z" | "_" | "0".."9")+ ("$" ("a".."z" | "A".."Z" | "_" | "0".."9")+)?;
	public TerminalRule getIDRule() {
		return (tID != null) ? tID : (tID = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "ID"));
	} 

	/// *
	//terminal DELEGATION_ID : 
	//	ID ('$' ID)?;
	//	* / terminal COLOR:
	//	"#" ("0".."9" | "A".."F")+;
	public TerminalRule getCOLORRule() {
		return (tCOLOR != null) ? tCOLOR : (tCOLOR = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "COLOR"));
	} 

	//// @see http://java.sun.com/javase/6/docs/api/java/lang/Double.html#valueOf(java.lang.String)
	//// returns ecore::EDouble : 
	//terminal DOUBLE:
	//	"1".."9" "0".."9"* ("." "0".."9"+)? (("E" | "e") ("+" | "-")? "0".."9"+)? | "0" ("." "0".."9"+)? (("E" | "e") ("+" |
	//	"-")? "0".."9"+)?;
	public TerminalRule getDOUBLERule() {
		return (tDOUBLE != null) ? tDOUBLE : (tDOUBLE = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "DOUBLE"));
	} 

	//terminal STRING:
	//	"\"" ("\\" ("b" | "t" | "n" | "f" | "r" | "u" | "\"" | "\\") | !("\\" | "\""))* "\"" | "\'" ("\\" ("b" | "t" | "n" |
	//	"f" | "r" | "u" | "\'" | "\\") | !("\\" | "\'"))* "\'";
	public TerminalRule getSTRINGRule() {
		return (tSTRING != null) ? tSTRING : (tSTRING = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "STRING"));
	} 

	//terminal ML_COMMENT:
	//	"/ *"->"* /";
	public TerminalRule getML_COMMENTRule() {
		return (tML_COMMENT != null) ? tML_COMMENT : (tML_COMMENT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "ML_COMMENT"));
	} 

	//terminal SL_COMMENT:
	//	"//" !("\n" | "\r")* ("\r"? "\n")?;
	public TerminalRule getSL_COMMENTRule() {
		return (tSL_COMMENT != null) ? tSL_COMMENT : (tSL_COMMENT = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "SL_COMMENT"));
	} 

	//terminal WS:
	//	(" " | "\t" | "\r" | "\n")+;
	public TerminalRule getWSRule() {
		return (tWS != null) ? tWS : (tWS = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "WS"));
	} 

	//terminal ANY_OTHER:
	//	.;
	public TerminalRule getANY_OTHERRule() {
		return (tANY_OTHER != null) ? tANY_OTHER : (tANY_OTHER = (TerminalRule) GrammarUtil.findRuleForName(getGrammar(), "ANY_OTHER"));
	} 
}
