/*********************************************************************************************
 * 
 * 
 * 'GamlProposalProvider.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.contentassist;

import java.util.*;
import msi.gama.lang.gaml.services.GamlGrammarAccess;
import msi.gama.lang.gaml.ui.contentassist.AbstractGamlProposalProvider;
import msi.gama.lang.gaml.ui.labeling.GamlLabelProvider;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.factories.DescriptionFactory;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.contentassist.*;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.ui.editor.contentassist.*;
import com.google.common.base.Function;
import com.google.inject.Inject;

/**
 * see
 * http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on
 * how to customize content assistant
 */
public class GamlProposalProvider extends AbstractGamlProposalProvider {

	// private static Set<String> typeList;
	// private static GamlProperties allowedFacets;
	// private static Image rgbImage = ImageDescriptor.createFromFile(GamlProposalProvider.class, "/icons/_rgb.png")
	// .createImage();
	// private static Image facetImage = ImageDescriptor.createFromFile(GamlProposalProvider.class, "/icons/_facet.png")
	// .createImage();
	private static Image typeImage = ImageDescriptor.createFromFile(GamlProposalProvider.class, "/icons/_type.png")
		.createImage();
	private static Image varImage = ImageDescriptor.createFromFile(GamlProposalProvider.class, "/icons/_var.png")
		.createImage();
	private static Image actionImage = ImageDescriptor.createFromFile(GamlProposalProvider.class, "/icons/_action.png")
		.createImage();

	// private static Image skillImage = ImageDescriptor.createFromFile(GamlProposalProvider.class,
	// "/icons/_skills.png")
	// .createImage();

	class GamlProposalCreator extends DefaultProposalCreator {

		ContentAssistContext context;

		/**
		 * @param contentAssistContext
		 * @param ruleName
		 * @param qualifiedNameConverter
		 */
		public GamlProposalCreator(final ContentAssistContext contentAssistContext, final String ruleName,
			final IQualifiedNameConverter qualifiedNameConverter) {
			super(contentAssistContext, ruleName, qualifiedNameConverter);
			context = contentAssistContext;
		}

		@Override
		public ICompletionProposal apply(final IEObjectDescription candidate) {

			ConfigurableCompletionProposal cp = (ConfigurableCompletionProposal) super.apply(candidate);
			boolean isOperator = false;
			String doc = candidate.getUserData("doc");
			String title = candidate.getUserData("title");
			if ( doc == null ) {
				doc = "Not documented yet";
			}
			if ( cp != null ) {
				cp.setAdditionalProposalInfo("<b>" + title + "</b><p/><p>" + doc + "</p>");

				String type = candidate.getUserData("type");
				if ( type != null ) {
					if ( type.equals("operator") ) {
						isOperator = true;
						cp.setDisplayString(cp.getDisplayString().concat(" (Built-in operator) "));
						cp.setImage(actionImage);
					} else if ( type.equals("variable") ) {
						cp.setDisplayString(cp.getDisplayString().concat(" (Built-in variable) "));
						cp.setImage(varImage);
					} else if ( type.equals("field") ) {
						cp.setDisplayString(cp.getDisplayString().concat(" (Built-in field) "));
						cp.setImage(varImage);
					} else if ( type.equals("action") ) {
						cp.setDisplayString(cp.getDisplayString().concat(" (Built-in action) "));
						cp.setImage(actionImage);
					} else if ( type.equals("unit") ) {
						isOperator = true;
						cp.setDisplayString(cp.getDisplayString().concat(" (Built-in unit) "));
						cp.setImage(null);
					} else if ( type.equals("type") ) {
						isOperator = true;
						cp.setDisplayString(cp.getDisplayString().concat(" (Built-in type) "));
						cp.setImage(typeImage);
					}
					cp.setPriority(1000);
				}
			}

			if ( context.getPrefix().equals(".") ) {
				if ( isOperator ) { return null; }
				if ( cp != null && cp.getPriority() > 500 ) {
					cp.setPriority(200);
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

	// private DefaultProposalCreator creator;

	static class BuiltInProposal {

		String name;
		StyledString title;
		Image image;
		private String documentation;

		public BuiltInProposal(final String name, final StyledString title, final Image image) {
			super();
			this.name = name;
			this.title = title;
			this.image = image;
		}

		/**
		 * @param documentation
		 */
		public void setDoc(final String documentation) {
			this.documentation = documentation;
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
	static final Set<String> fields = new HashSet(), vars = new HashSet(), actions = new HashSet(),
		types = new HashSet(), skills = new HashSet(), constants = new HashSet(), units = new HashSet(),
		statements = new HashSet(), facets = new HashSet();

	@Inject
	GamlLabelProvider provider;

	// @Inject
	// private IImageHelper imageHelper;
	//
	// @Inject
	// private GamlJavaValidator validator;

	@Inject
	private GamlGrammarAccess ga;

	@Override
	public void createProposals(final ContentAssistContext context, final ICompletionProposalAcceptor acceptor) {
		// Disabling for comments (see Issue 786)
		EObject grammarElement = context.getCurrentNode().getGrammarElement();
		if ( grammarElement == ga.getML_COMMENTRule() ) { return; }
		if ( grammarElement == ga.getSL_COMMENTRule() ) { return; }
		//
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
			// for ( String t : Types.getTypeNames() ) {
			// types.add(t);
			// Image image = imageHelper.getImage(provider.typeImage(t));
			// if ( image == null ) {
			// image = image = imageHelper.getImage(provider.typeImage("gaml_facet.png"));
			// }
			// BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + " (Built-in type)"), image);
			// proposals.add(cp);
			// }

			// for ( String t : AbstractGamlAdditions.CONSTANTS ) {
			// constants.add(t);
			// BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + " (Built-in constant)"), null);
			// proposals.add(cp);
			// }
			// for ( String t : IUnits.UNITS.keySet() ) {
			// units.add(t);
			// BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + " (Built-in unit)"), null);
			// proposals.add(cp);
			// }
			for ( String t : DescriptionFactory.getStatementProtoNames() ) {
				SymbolProto s = DescriptionFactory.getProto(t, null);
				statements.add(t);
				String title = " (Statement)";
				BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + title), null);
				proposals.add(cp);
				cp.setDoc(s.getDocumentation());
			}

			for ( String t : DescriptionFactory.getVarProtoNames() ) {
				SymbolProto s = DescriptionFactory.getVarProto(t, null);
				statements.add(t);
				String title = " (Declaration)";
				BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + title), null);
				proposals.add(cp);
				cp.setDoc(s.getDocumentation());

			}
			// for ( String t : AbstractGamlAdditions.getAllSkills() ) {
			// skills.add(t);
			// BuiltInProposal cp = new BuiltInProposal(t, new StyledString(t + ": (Built-in skill)"), skillImage);
			// proposals.add(cp);
			// }

		}
		for ( BuiltInProposal bi : proposals ) {
			ICompletionProposal cp =
				createCompletionProposal(bi.name, bi.title, bi.image, 1000, context.getPrefix(), context);
			if ( cp == null ) {
				// scope.getGui().debug("GamlProposalProvider.addBuiltInElements null for " + t);
			} else {
				if ( bi.documentation != null ) {
					((ConfigurableCompletionProposal) cp).setAdditionalProposalInfo(bi.documentation);
				}
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

	@Override
	protected boolean isValidProposal(final String proposal, final String prefix, final ContentAssistContext context) {
		if ( prefix.equals(".") ) { return !types.contains(proposal) && !units.contains(proposal) &&
			!constants.contains(proposal) && !skills.contains(proposal) && isValidProposal(proposal, "", context); }
		return super.isValidProposal(proposal, prefix, context);
	}

}
