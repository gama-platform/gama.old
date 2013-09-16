/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml.ui.contentassist;

import java.util.*;
import msi.gama.lang.gaml.ui.labeling.GamlLabelProvider;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.operators.IUnits;
import msi.gaml.types.Types;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.contentassist.*;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.ui.IImageHelper;
import org.eclipse.xtext.ui.editor.contentassist.*;
import com.google.common.base.Function;
import com.google.inject.Inject;

/**
 * see
 * http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on
 * how to customize content assistant
 */
public class GamlProposalProvider extends AbstractGamlProposalProvider {

	private static Set<String> typeList;
	private static GamlProperties allowedFacets;
	private static Image rgbImage = ImageDescriptor.createFromFile(GamlProposalProvider.class, "/icons/_rgb.png")
		.createImage();
	private static Image facetImage = ImageDescriptor.createFromFile(GamlProposalProvider.class, "/icons/_facet.png")
		.createImage();
	private static Image typeImage = ImageDescriptor.createFromFile(GamlProposalProvider.class, "/icons/_type.png")
		.createImage();
	private static Image varImage = ImageDescriptor.createFromFile(GamlProposalProvider.class, "/icons/_var.png")
		.createImage();
	private static Image actionImage = ImageDescriptor.createFromFile(GamlProposalProvider.class, "/icons/_action.png")
		.createImage();
	private static Image skillImage = ImageDescriptor.createFromFile(GamlProposalProvider.class, "/icons/_skills.png")
		.createImage();

	class GamlProposalCreator extends DefaultProposalCreator {

		/**
		 * @param contentAssistContext
		 * @param ruleName
		 * @param qualifiedNameConverter
		 */
		public GamlProposalCreator(final ContentAssistContext contentAssistContext, final String ruleName,
			final IQualifiedNameConverter qualifiedNameConverter) {
			super(contentAssistContext, ruleName, qualifiedNameConverter);
		}

		@Override
		public ICompletionProposal apply(final IEObjectDescription candidate) {
			ConfigurableCompletionProposal cp = (ConfigurableCompletionProposal) super.apply(candidate);
			String doc = candidate.getUserData("doc");
			if ( cp != null ) {
				if ( doc != null ) {
					String title = candidate.getUserData("title");
					cp.setAdditionalProposalInfo("<b>" + title + "</b><p/><p>" + doc + "</p>");

				}
				String type = candidate.getUserData("type");
				if ( type != null ) {
					if ( type.equals("operator") ) {
						cp.setDisplayString(cp.getDisplayString().concat(" (Built-in operator) "));
						cp.setImage(actionImage);
					}
				}
			}
			return cp;
		}

	};

	class GamlCompletionProposal extends ConfigurableCompletionProposal {

		/**
		 * @param replacementString
		 * @param replacementOffset
		 * @param replacementLength
		 * @param cursorPosition
		 * @param image
		 * @param displayString
		 * @param contextInformation
		 * @param additionalProposalInfo
		 */
		public GamlCompletionProposal(final String replacementString, final int replacementOffset,
			final int replacementLength, final int cursorPosition, final Image image, final StyledString displayString,
			final IContextInformation contextInformation, final String additionalProposalInfo) {
			super(replacementString, replacementOffset, replacementLength, cursorPosition, image, displayString,
				contextInformation, additionalProposalInfo);
		}

		@Override
		public IInformationControlCreator getInformationControlCreator() {
			return new IInformationControlCreator() {

				@Override
				public IInformationControl createInformationControl(final Shell parent) {
					IInformationControl control = new DefaultInformationControl(parent, true);
					return control;

				}
			};
		}

	}

	private DefaultProposalCreator creator;

	static class BuiltInProposal {

		String name;
		StyledString title;
		Image image;

		public BuiltInProposal(final String name, final StyledString title, final Image image) {
			super();
			this.name = name;
			this.title = title;
			this.image = image;
		}
	}

	@Override
	protected String getDisplayString(final EObject element, String qualifiedNameAsString, final String shortName) {
		if ( qualifiedNameAsString == null ) {
			qualifiedNameAsString = shortName;
		}
		if ( qualifiedNameAsString == null ) {
			if ( element != null ) {
				qualifiedNameAsString = provider.getText(element);
			} else {
				return null;
			}
		}
		return qualifiedNameAsString;
	}

	static final List<BuiltInProposal> proposals = new ArrayList();

	@Inject
	GamlLabelProvider provider;

	@Inject
	private IImageHelper imageHelper;

	@Inject
	private GamlJavaValidator validator;

	@Override
	public void createProposals(final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		addBuiltInElements(context, acceptor);
		super.createProposals(context, acceptor);

	}

	/**
	 * @see org.eclipse.xtext.ui.editor.contentassist.AbstractContentProposalProvider#doCreateProposal(java.lang.String,
	 *      org.eclipse.jface.viewers.StyledString, org.eclipse.swt.graphics.Image, int,
	 *      org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext)
	 */
	@Override
	protected ConfigurableCompletionProposal doCreateProposal(final String proposal, final StyledString displayString,
		final Image image, final int priority, final ContentAssistContext context) {
		ConfigurableCompletionProposal cp = super.doCreateProposal(proposal, displayString, image, priority, context);
		return cp;
	}

	/**
	 * @param context
	 * @param acceptor
	 * 
	 *            TODO Filter the proposals (passing an argument ?) depending on the context in the dispatcher (see
	 *            commented methods below).
	 *            TODO Build this list at once instead of recomputing it everytime (might be done in a dedicated data
	 *            structure somewhere) and separate it by types (vars, units, etc.)
	 */
	private void addBuiltInElements(final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		if ( proposals.isEmpty() ) {
			for ( String t : Types.getTypeNames() ) {
				Image image = imageHelper.getImage(provider.typeImage(t));
				if ( image == null ) {
					image = image = imageHelper.getImage(provider.typeImage("gaml_facet.png"));
				}
				BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + " (Built-in type)"), image);
				proposals.add(cp);
			}

			for ( String t : AbstractGamlAdditions.CONSTANTS ) {
				BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + " (Built-in constant)"), null);
				proposals.add(cp);
			}
			for ( String t : IUnits.UNITS.keySet() ) {

				BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + " (Built-in unit)"), null);
				proposals.add(cp);
			}
			for ( String t : AbstractGamlAdditions.getAllFields() ) {

				BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + " (Built-in field)"), varImage);
			}
			for ( String t : AbstractGamlAdditions.getAllVars() ) {

				BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + " (Built-in variable)"), varImage);
				proposals.add(cp);
			}
			for ( String t : AbstractGamlAdditions.getAllSkills() ) {

				BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + ": (Built-in facet)"), skillImage);
				proposals.add(cp);
			}
			for ( String t : AbstractGamlAdditions.getAllActions() ) {

				BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + " (Built-in action)"), actionImage);
				proposals.add(cp);
			}
			// for ( String t : IExpressionCompiler.OPERATORS.keySet() ) {
			//
			// BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + " (Built-in operator)"), actionImage);
			// proposals.add(cp);
			// }
		}
		for ( BuiltInProposal bi : proposals ) {
			ICompletionProposal cp =
				createCompletionProposal(bi.name, bi.title, bi.image, 1000, context.getPrefix(), context);
			if ( cp == null ) {
				// GuiUtils.debug("GamlProposalProvider.addBuiltInElements null for " + t);
			} else {
				acceptor.accept(cp);
			}
		}

	}

	/**
	 * @see org.eclipse.xtext.ui.editor.contentassist.AbstractContentProposalProvider#doCreateProposal(java.lang.String,
	 *      org.eclipse.jface.viewers.StyledString, org.eclipse.swt.graphics.Image, int, int)
	 */
	@Override
	protected ConfigurableCompletionProposal doCreateProposal(final String proposal, final StyledString displayString,
		final Image image, final int replacementOffset, final int replacementLength) {
		return new GamlCompletionProposal(proposal, replacementOffset, replacementLength, proposal.length(), image,
			displayString, null, null);
	}

	@Override
	protected Function<IEObjectDescription, ICompletionProposal> getProposalFactory(final String ruleName,
		final ContentAssistContext contentAssistContext) {
		return new GamlProposalCreator(contentAssistContext, ruleName, getQualifiedNameConverter());
	}
	// @Override
	// public void completeKeyword(final Keyword keyword, final ContentAssistContext contentAssistContext,
	// final ICompletionProposalAcceptor acceptor) {
	// final ICompletionProposal proposal =
	// createCompletionProposal(keyword.getValue(), getKeywordDisplayString(keyword), getImage(keyword),
	// contentAssistContext);
	// getPriorityHelper().adjustKeywordPriority(proposal, contentAssistContext.getPrefix());
	// acceptor.accept(proposal);
	// }

	// @Override
	// public void completeRuleCall(final RuleCall ruleCall, final ContentAssistContext contentAssistContext,
	// final ICompletionProposalAcceptor acceptor) {
	// final AbstractRule calledRule = ruleCall.getRule();
	// final String methodName = "complete_" + calledRule.getName();
	// GuiUtils.debug("GamlProposalProvider.completeRuleCall " + methodName);
	// EObject e = contentAssistContext.getCurrentModel();
	// e = contentAssistContext.getPreviousModel();
	// invokeMethod(methodName, acceptor, contentAssistContext.getCurrentModel(), ruleCall, contentAssistContext);
	// }

	// @Override
	// public void completeAssignment(final Assignment assignment, final ContentAssistContext contentAssistContext,
	// final ICompletionProposalAcceptor acceptor) {
	// final ParserRule parserRule = GrammarUtil.containingParserRule(assignment);
	// final String methodName =
	// "complete" + Strings.toFirstUpper(parserRule.getName()) + "_" +
	// Strings.toFirstUpper(assignment.getFeature());
	// GuiUtils.debug("GamlProposalProvider.completeAssignment " + methodName);
	// EObject e = contentAssistContext.getCurrentModel();
	// e = contentAssistContext.getPreviousModel();
	//
	// invokeMethod(methodName, acceptor, contentAssistContext.getCurrentModel(), assignment, contentAssistContext);
	// }

	// @Override
	// public void completeDot_Op(final EObject model, final Assignment assignment,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// completeDot_Right(model, assignment, context, acceptor);
	// }
	//
	// @Override
	// public void completeModel_Name(final EObject model, final Assignment assignment,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {}
	//
	// @Override
	// public void completeModel_Imports(final EObject model, final Assignment assignment,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {}
	//
	// @Override
	// public void completeModel_Statements(final EObject model, final Assignment assignment,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {}
	//
	// @Override
	// public void completeImport_ImportURI(final EObject model, final Assignment assignment,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {}
	//
	// // @Override
	// // public void completeClassicStatement_Key(final EObject model, final Assignment assignment,
	// // final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// //
	// // }
	//
	// //
	// // @Override
	// // public void completeClassicStatement_Facets(final EObject model, final Assignment
	// assignment,
	// // final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// // GuiUtils.debug("Complete ClassicStatement facets");
	// // }
	//
	// // @Override
	// // public void completeClassicStatement_Block(final EObject model, final Assignment
	// assignment,
	// // final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// //
	// // }
	//
	// /*
	// * @Override
	// * public void completeIfEval_Key(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeIfEval_Ref(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeIfEval_Expr(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeIfEval_Block(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeIfEval_Else(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeDefinition_Name(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeGamlFacetRef_Ref(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * completeFacetExpr_Key(model, assignment, context, acceptor);
	// * }
	// *
	// * @Override
	// * public void completeFunctionGamlFacetRef_Ref(final EObject model, final Assignment
	// * assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete FunctionGamlFacetRef ref");
	// * }
	// */
	//
	// @Override
	// public void completeClassicFacet_Key(final EObject model, final Assignment assignment,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// if ( model instanceof Statement ) {
	// IGamlDescription gd = DescriptionFactory.getGamlDescription(model);
	// if ( gd instanceof IDescription ) {
	// IDescription desc = (IDescription) gd;
	// // System.out.println("after\n");
	//
	// String[][] ss = desc.getMeta().getPossibleCombinations();
	// for ( String s[] : ss ) {
	// if ( s != null ) {
	// String combination = "";
	// for ( String f : s ) {
	// combination += f + ": ";
	// }
	// acceptor.accept(createCompletionProposal(combination,
	// "Possible combination:(" + combination + ")", facetImage, context));
	// }
	// }
	//
	// Map<String, FacetProto> facets = desc.getMeta().getPossibleFacets();
	// for ( String s : facets.keySet() ) {
	// acceptor.accept(createCompletionProposal(s + ":",
	// "Facet " + s + ": (" + (facets.get(s).optional ? "optional" : "required") +
	// ") type:" + facets.get(s).types, facetImage, context));
	// }
	//
	// }
	// }
	// }
	//
	// /*
	// *
	// * @Override
	// * public void completeFacetExpr_Expr(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete facetExpr expr");
	// * }
	// *
	// * @Override
	// * public void completeNameFacetExpr_Name(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete facetExpr name");
	// * }
	// *
	// * @Override
	// * public void completeReturnsFacetExpr_Name(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete ReturnsFacetExpr name");
	// * }
	// *
	// * @Override
	// * public void completeActionFacetExpr_Name(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete ActionFacetExpr name");
	// * }
	// *
	// * @Override
	// * public void completeFunctionFacetExpr_Key(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete FunctionFacetExpr key");
	// * }
	// *
	// * @Override
	// * public void completeFunctionFacetExpr_Expr(final EObject model, final Assignment
	// assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete FunctionFacetExpr expr");
	// * }
	// *
	// * @Override
	// * public void completeBlock_Statements(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeTernExp_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeTernExp_Right(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeTernExp_IfFalse(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeOrExp_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeOrExp_Right(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeAndExp_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeAndExp_Right(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeRelational_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeRelational_Right(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeArgPairExpr_Arg(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeArgPairExpr_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeArgPairExpr_Right(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completePairExpr_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completePairExpr_Right(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeAddition_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeAddition_Right(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeMultiplication_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeMultiplication_Right(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeGamlBinaryExpr_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeGamlBinaryExpr_Right(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeGamlUnitExpr_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeGamlUnitExpr_Right(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeGamlUnaryExpr_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeGamlUnaryExpr_Right(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completePrimaryExpression_Exprs(final EObject model, final Assignment
	// assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completePrimaryExpression_Left(final EObject model, final Assignment
	// assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completePrimaryExpression_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completePrimaryExpression_Right(final EObject model, final Assignment
	// assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeFunction_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeFunction_Args(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeUnitName_Op(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeVariableRef_Ref(final EObject model, final Assignment assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void completeTerminalExpression_Value(final EObject model, final Assignment
	// * assignment,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void complete_Model(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void complete_Import(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void complete_Statement(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete Statement");
	// * }
	// *
	// * @Override
	// * public void complete_ClassicStatement(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete ClassicStatement");
	// * }
	// *
	// * @Override
	// * public void complete_IfEval(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void complete_Definition(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete Definition");
	// * }
	// *
	// * @Override
	// * public void complete_FacetRef(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete FacetRef");
	// * }
	// *
	// * @Override
	// * public void complete_GamlFacetRef(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// *
	// * }
	// *
	// * @Override
	// * public void complete_FunctionGamlFacetRef(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete FunctionGamlFacetRef");
	// * }
	// *
	// * @Override
	// * public void complete_FacetExpr(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete FacetExpr");
	// * }
	// *
	// * @Override
	// * public void complete_DefinitionFacetExpr(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete DefinitionFacetExpr");
	// * }
	// *
	// * @Override
	// * public void complete_NameFacetExpr(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete NameFacetExpr");
	// * }
	// *
	// * @Override
	// * public void complete_ReturnsFacetExpr(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete ReturnsFacetExpr");
	// * }
	// *
	// * @Override
	// * public void complete_ActionFacetExpr(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete ActionFacetExpr");
	// * }
	// *
	// * @Override
	// * public void complete_FunctionFacetExpr(final EObject model, final RuleCall ruleCall,
	// * final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// * GuiUtils.debug("Complete FunctionFacetExpr");
	// * }
	// */@Override
	// public void complete_Block(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_Expression(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_If(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_Or(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_And(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_Comparison(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_ArgumentPair(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_Pair(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_Addition(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_Multiplication(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_Binary(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_Unit(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_Unary(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// // @Override
	// // public void complete_PrePrimaryExpr(final EObject model, final RuleCall ruleCall,
	// // final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// //
	// // }
	//
	// @Override
	// public void complete_Dot(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_Primary(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// GuiUtils.debug("Completing PrimaryExpression");
	// }
	//
	// // @Override
	// // public void complete_AbstractRef(final EObject model, final RuleCall ruleCall,
	// // final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// // GuiUtils.debug("Completing AbstractRef");
	// // }
	//
	// @Override
	// public void complete_Function(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_UnitRef(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_VariableRef(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// GuiUtils.debug("Completing VariableRef");
	// }
	//
	// @Override
	// public void complete_VarDefinition(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_TerminalExpression(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_INTEGER(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_BOOLEAN(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_ID(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_COLOR(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_DOUBLE(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// /**
	// * @see
	// msi.gama.lang.gaml.ui.contentassist.AbstractGamlProposalProvider#completeDefinitionStatement_Facets(org.eclipse.emf.ecore.EObject,
	// * org.eclipse.xtext.Assignment,
	// * org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext,
	// * org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor)
	// */
	// @Override
	// public void completeS_Definition_Facets(EObject model, Assignment assignment,
	// ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
	// // super.completeDefinitionStatement_Facets(model, assignment, context, acceptor);
	// if ( model instanceof Facet ) {
	// completeClassicFacet_Key(model.eContainer(), assignment, context, acceptor);
	// }
	// }
	//
	// /**
	// * @see
	// org.eclipse.xtext.ui.editor.contentassist.AbstractJavaBasedContentProposalProvider#completeKeyword(org.eclipse.xtext.Keyword,
	// * org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext,
	// * org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor)
	// */
	// @Override
	// public void completeKeyword(Keyword keyword, ContentAssistContext contentAssistContext,
	// ICompletionProposalAcceptor acceptor) {
	// super.completeKeyword(keyword, contentAssistContext, acceptor);
	// }
	//
	// @Override
	// public void complete_STRING(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_ML_COMMENT(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_SL_COMMENT(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// }
	//
	// @Override
	// public void complete_WS(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// GuiUtils.debug("Completing WS");
	// }
	//
	// @Override
	// public void complete_ANY_OTHER(final EObject model, final RuleCall ruleCall,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	// GuiUtils.debug("Completing OTHER");
	// }
	//
	// @Override
	// public void completeDot_Right(final EObject model, final Assignment assignment,
	// final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
	//
	// if ( !(model instanceof Dot) ) { return; }
	// Expression left = ((Dot) model).getLeft();
	// EObject obj = model;
	// IDescription desc = null;
	// // Find the upper description
	// while (desc == null && obj != null) {
	// obj = obj.eContainer();
	// desc = DescriptionFactory.getGamlDescription(obj, IDescription.class);
	// }
	// if ( desc != null ) {
	// IExpressionDescription ed = new EcoreBasedExpressionDescription(left);
	// IExpression expression = GAMA.getExpressionFactory().createExpr(ed, desc);
	// if ( expression != null ) {
	// IType type = expression.getType();
	// Map<String, ? extends IGamlDescription> descs =
	// type.getFieldDescriptions(desc.getModelDescription());
	// for ( String s : descs.keySet() ) {
	// IGamlDescription d = descs.get(s);
	// String ss = provider.removeTags(d.getTitle()) + " (type: " + d.getType() + ")";
	// acceptor.accept(createCompletionProposal(s, ss,
	// imageHelper.getImage(provider.typeImage(d.getType().toString())), context));
	// }
	// }
	// }
	//
	// }

	// INTRODUCE THE FILTERING ON THE "FAKE" ELEMENTS SO THAT WE CAN GRAB THEIR DOCUMENTATION, IMAGE, NAME, TYPE, etc.

}
