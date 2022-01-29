/*******************************************************************************************************
 *
 * AbstractGamlProposalProvider.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.Alternatives;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ui.editor.contentassist.AbstractJavaBasedContentProposalProvider;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

/**
 * Represents a generated, default implementation of superclass {@link AbstractJavaBasedContentProposalProvider}.
 * Methods are dynamically dispatched on the first parameter, i.e., you can override them 
 * with a more concrete subtype. 
 */
public abstract class AbstractGamlProposalProvider extends AbstractJavaBasedContentProposalProvider {

	/**
	 * Complete standalone block block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeStandaloneBlock_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete string evaluator toto.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeStringEvaluator_Toto(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete string evaluator expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeStringEvaluator_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete model pragmas.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeModel_Pragmas(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete model name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeModel_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete model imports.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeModel_Imports(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete model block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeModel_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete model block statements.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeModelBlock_Statements(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete import import URI.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeImport_ImportURI(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete import name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeImport_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete pragma name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completePragma_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete experiment file structure exp.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeExperimentFileStructure_Exp(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete headless experiment key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeHeadlessExperiment_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete headless experiment first facet.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeHeadlessExperiment_FirstFacet(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete headless experiment name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeHeadlessExperiment_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(0)), context, acceptor);
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(1)), context, acceptor);
	}
	
	/**
	 * Complete headless experiment import URI.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeHeadlessExperiment_ImportURI(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete headless experiment facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeHeadlessExperiment_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete headless experiment block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeHeadlessExperiment_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S global key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Global_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S global facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Global_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S global block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Global_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S species key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Species_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S species first facet.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Species_FirstFacet(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S species name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Species_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S species facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Species_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S species block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Species_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S experiment key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Experiment_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S experiment first facet.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Experiment_FirstFacet(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S experiment name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Experiment_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(0)), context, acceptor);
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(1)), context, acceptor);
	}
	
	/**
	 * Complete S experiment facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Experiment_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S experiment block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Experiment_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S 1 expr facets block or end key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_1Expr_Facets_BlockOrEnd_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S 1 expr facets block or end first facet.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_1Expr_Facets_BlockOrEnd_FirstFacet(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S 1 expr facets block or end expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_1Expr_Facets_BlockOrEnd_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S 1 expr facets block or end facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_1Expr_Facets_BlockOrEnd_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S 1 expr facets block or end block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_1Expr_Facets_BlockOrEnd_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S do key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Do_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S do first facet.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Do_FirstFacet(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S do expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Do_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S do facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Do_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S do block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Do_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S loop key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Loop_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S loop name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Loop_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S loop facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Loop_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S loop block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Loop_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S if key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_If_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S if first facet.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_If_FirstFacet(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S if expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_If_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S if block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_If_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S if else.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_If_Else(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(0)), context, acceptor);
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(1)), context, acceptor);
	}
	
	/**
	 * Complete S try key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Try_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S try block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Try_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S try catch.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Try_Catch(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S other key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Other_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S other facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Other_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S other block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Other_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S return key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Return_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S return first facet.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Return_FirstFacet(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S return expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Return_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S reflex key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Reflex_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S reflex first facet.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Reflex_FirstFacet(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S reflex name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Reflex_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S reflex expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Reflex_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S reflex block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Reflex_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S definition tkey.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Definition_Tkey(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S definition first facet.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Definition_FirstFacet(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S definition name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Definition_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(0)), context, acceptor);
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(1)), context, acceptor);
	}
	
	/**
	 * Complete S definition args.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Definition_Args(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S definition facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Definition_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S definition block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Definition_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S action key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Action_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S action first facet.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Action_FirstFacet(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S action name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Action_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S action args.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Action_Args(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S action facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Action_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S action block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Action_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S var key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Var_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S var first facet.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Var_FirstFacet(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S var name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Var_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S var facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Var_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S direct assignment expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_DirectAssignment_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S direct assignment key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_DirectAssignment_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S direct assignment value.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_DirectAssignment_Value(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S direct assignment facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_DirectAssignment_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S set key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Set_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S set expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Set_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S set value.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Set_Value(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S equations key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Equations_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S equations name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Equations_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S equations facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Equations_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S equations equations.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Equations_Equations(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S equation expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Equation_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(0)), context, acceptor);
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(1)), context, acceptor);
	}
	
	/**
	 * Complete S equation key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Equation_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S equation value.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Equation_Value(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S solve key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Solve_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S solve first facet.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Solve_FirstFacet(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S solve expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Solve_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S solve facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Solve_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S solve block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Solve_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S display key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Display_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S display first facet.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Display_FirstFacet(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S display name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Display_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(0)), context, acceptor);
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(1)), context, acceptor);
	}
	
	/**
	 * Complete S display facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Display_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete S display block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeS_Display_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete display block statements.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeDisplayBlock_Statements(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete species or grid display statement key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeSpeciesOrGridDisplayStatement_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete species or grid display statement expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeSpeciesOrGridDisplayStatement_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete species or grid display statement facets.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeSpeciesOrGridDisplayStatement_Facets(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete species or grid display statement block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeSpeciesOrGridDisplayStatement_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete action arguments args.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeActionArguments_Args(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete argument definition type.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeArgumentDefinition_Type(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete argument definition name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeArgumentDefinition_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete argument definition default.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeArgumentDefinition_Default(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete classic facet key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeClassicFacet_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		if (assignment.getTerminal() instanceof RuleCall) {
			completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
		}
		if (assignment.getTerminal() instanceof Keyword) {
			// subclasses may override
		}
	}
	
	/**
	 * Complete classic facet expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeClassicFacet_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete definition facet key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeDefinitionFacet_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete definition facet name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeDefinitionFacet_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(0)), context, acceptor);
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(1)), context, acceptor);
	}
	
	/**
	 * Complete function facet key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeFunctionFacet_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete function facet expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeFunctionFacet_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete type facet key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeTypeFacet_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete type facet expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeTypeFacet_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete action facet key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeActionFacet_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete action facet expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeActionFacet_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete action facet block.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeActionFacet_Block(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete var facet key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeVarFacet_Key(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete var facet expr.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeVarFacet_Expr(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete block statements.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeBlock_Statements(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete argument pair op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeArgumentPair_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		if (assignment.getTerminal() instanceof RuleCall) {
			completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
		}
		if (assignment.getTerminal() instanceof Alternatives) {
			completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(0)), context, acceptor);
			completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(1)), context, acceptor);
			completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(2)), context, acceptor);
			completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(3)), context, acceptor);
			completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(4)), context, acceptor);
		}
	}
	
	/**
	 * Complete argument pair right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeArgumentPair_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete pair op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completePair_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete pair right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completePair_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete if op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeIf_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete if right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeIf_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete if if false.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeIf_IfFalse(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete or op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeOr_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete or right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeOr_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete and op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeAnd_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete and right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeAnd_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete cast op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeCast_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete cast right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeCast_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete comparison op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeComparison_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
		// subclasses may override
		// subclasses may override
		// subclasses may override
		// subclasses may override
		// subclasses may override
	}
	
	/**
	 * Complete comparison right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeComparison_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete addition op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeAddition_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
		// subclasses may override
	}
	
	/**
	 * Complete addition right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeAddition_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete multiplication op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeMultiplication_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
		// subclasses may override
	}
	
	/**
	 * Complete multiplication right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeMultiplication_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete exponentiation op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeExponentiation_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete exponentiation right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeExponentiation_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete binary op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeBinary_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete binary right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeBinary_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete unit op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeUnit_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
		// subclasses may override
	}
	
	/**
	 * Complete unit right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeUnit_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete unary op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeUnary_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
		// subclasses may override
	}
	
	/**
	 * Complete unary right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeUnary_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete access op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeAccess_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete access right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeAccess_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		if (assignment.getTerminal() instanceof RuleCall) {
			completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
		}
		if (assignment.getTerminal() instanceof Alternatives) {
			completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(0)), context, acceptor);
			completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(1)), context, acceptor);
		}
	}
	
	/**
	 * Complete primary exprs.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completePrimary_Exprs(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete primary left.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completePrimary_Left(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete primary op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completePrimary_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete primary right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completePrimary_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete primary Z.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completePrimary_Z(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete function left.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeFunction_Left(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete function type.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeFunction_Type(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete function right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeFunction_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete expression list exprs.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeExpressionList_Exprs(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete parameter built in facet key.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeParameter_BuiltInFacetKey(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(0)), context, acceptor);
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(1)), context, acceptor);
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(2)), context, acceptor);
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(3)), context, acceptor);
		completeRuleCall(((RuleCall)((Alternatives)assignment.getTerminal()).getElements().get(4)), context, acceptor);
	}
	
	/**
	 * Complete parameter left.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeParameter_Left(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete parameter right.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeParameter_Right(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete unit ref ref.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeUnitRef_Ref(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		lookupCrossReference(((CrossReference)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete variable ref ref.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeVariableRef_Ref(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		lookupCrossReference(((CrossReference)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete type ref ref.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeTypeRef_Ref(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		lookupCrossReference(((CrossReference)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete type ref parameter.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeTypeRef_Parameter(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete type info first.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeTypeInfo_First(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete type info second.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeTypeInfo_Second(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete skill ref ref.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeSkillRef_Ref(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		lookupCrossReference(((CrossReference)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete action ref ref.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeActionRef_Ref(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		lookupCrossReference(((CrossReference)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete equation ref ref.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeEquationRef_Ref(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		lookupCrossReference(((CrossReference)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete unit fake definition name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeUnitFakeDefinition_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete type fake definition name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeTypeFakeDefinition_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete action fake definition name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeActionFakeDefinition_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete skill fake definition name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeSkillFakeDefinition_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete var fake definition name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeVarFakeDefinition_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete equation fake definition name.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeEquationFakeDefinition_Name(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete terminal expression op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeTerminalExpression_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}
	
	/**
	 * Complete string literal op.
	 *
	 * @param model the model
	 * @param assignment the assignment
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void completeStringLiteral_Op(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		completeRuleCall(((RuleCall)assignment.getTerminal()), context, acceptor);
	}

	/**
	 * Complete entry.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Entry(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete standalone block.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_StandaloneBlock(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete string evaluator.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_StringEvaluator(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete model.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Model(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete model block.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ModelBlock(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete import.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Import(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete pragma.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Pragma(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete experiment file structure.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ExperimentFileStructure(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete headless experiment.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_HeadlessExperiment(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S section.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Section(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S global.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Global(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S species.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Species(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S experiment.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Experiment(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete statement.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Statement(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S 1 expr facets block or end.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_1Expr_Facets_BlockOrEnd(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S do.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Do(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S loop.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Loop(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S if.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_If(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S try.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Try(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S other.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Other(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S return.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Return(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S declaration.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Declaration(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S reflex.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Reflex(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S definition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Definition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S action.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Action(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S var.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Var(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S assignment.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Assignment(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S direct assignment.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_DirectAssignment(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S set.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Set(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S equations.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Equations(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S equation.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Equation(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S solve.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Solve(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S display.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_S_Display(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete display block.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_displayBlock(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete display statement.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_displayStatement(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete species or grid display statement.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_speciesOrGridDisplayStatement(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete equations key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete__EquationsKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete solve key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete__SolveKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete species key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete__SpeciesKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete experiment key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete__ExperimentKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete 1 expr facets block or end key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete__1Expr_Facets_BlockOrEnd_Key(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete layer key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete__LayerKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete do key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete__DoKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete var or const key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete__VarOrConstKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete reflex key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete__ReflexKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete assignment key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete__AssignmentKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete action arguments.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ActionArguments(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete argument definition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ArgumentDefinition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete facet.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Facet(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete first facet key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_FirstFacetKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete classic facet key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ClassicFacetKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete definition facet key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_DefinitionFacetKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete type facet key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_TypeFacetKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete special facet key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_SpecialFacetKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete action facet key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ActionFacetKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete var facet key.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_VarFacetKey(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete classic facet.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ClassicFacet(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete definition facet.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_DefinitionFacet(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete function facet.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_FunctionFacet(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete type facet.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_TypeFacet(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete action facet.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ActionFacet(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete var facet.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_VarFacet(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete block.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Block(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete expression.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Expression(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete binary operator.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_BinaryOperator(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete argument pair.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ArgumentPair(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete pair.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Pair(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete if.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_If(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete or.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Or(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete and.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_And(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete cast.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Cast(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete comparison.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Comparison(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete addition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Addition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete multiplication.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Multiplication(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete exponentiation.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Exponentiation(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete binary.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Binary(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete unit.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Unit(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete unary.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Unary(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete access.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Access(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete primary.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Primary(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete abstract ref.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_AbstractRef(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete function.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Function(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete expression list.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ExpressionList(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete parameter.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Parameter(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete unit ref.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_UnitRef(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete variable ref.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_VariableRef(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete type ref.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_TypeRef(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete type info.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_TypeInfo(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete skill ref.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_SkillRef(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete action ref.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ActionRef(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete equation ref.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_EquationRef(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete gaml definition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_GamlDefinition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete equation definition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_EquationDefinition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete type definition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_TypeDefinition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete var definition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_VarDefinition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete action definition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ActionDefinition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete unit fake definition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_UnitFakeDefinition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete type fake definition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_TypeFakeDefinition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete action fake definition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ActionFakeDefinition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete skill fake definition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_SkillFakeDefinition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete var fake definition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_VarFakeDefinition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete equation fake definition.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_EquationFakeDefinition(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete valid ID.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_Valid_ID(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete terminal expression.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_TerminalExpression(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete string literal.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_StringLiteral(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete KEYWORD.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_KEYWORD(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete INTEGER.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_INTEGER(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete BOOLEAN.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_BOOLEAN(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete ID.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ID(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete DOUBLE.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_DOUBLE(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete STRING.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_STRING(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete M L COMMENT.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ML_COMMENT(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete S L COMMENT.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_SL_COMMENT(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete WS.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_WS(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
	
	/**
	 * Complete AN Y OTHER.
	 *
	 * @param model the model
	 * @param ruleCall the rule call
	 * @param context the context
	 * @param acceptor the acceptor
	 */
	public void complete_ANY_OTHER(EObject model, RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// subclasses may override
	}
}
