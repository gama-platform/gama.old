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
package msi.gama.lang.gaml.ui;

import msi.gama.common.util.GuiUtils;
import msi.gama.lang.gaml.ui.GamlEditor.GamaSourceViewerConfiguration;
import msi.gama.lang.gaml.ui.contentassist.*;
import msi.gama.lang.gaml.ui.highlight.*;
import msi.gama.lang.gaml.ui.hover.*;
import msi.gama.lang.gaml.ui.hover.GamlHoverProvider.GamlDispatchingEObjectTextHover;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.parser.antlr.ISyntaxErrorMessageProvider;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.ui.editor.*;
import org.eclipse.xtext.ui.editor.actions.IActionContributor;
import org.eclipse.xtext.ui.editor.contentassist.XtextContentAssistProcessor;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;
import org.eclipse.xtext.ui.editor.syntaxcoloring.*;
import org.eclipse.xtext.ui.resource.*;
import com.google.inject.*;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used within the IDE.
 */
public class GamlUiModule extends msi.gama.lang.gaml.ui.AbstractGamlUiModule {

	public GamlUiModule(final AbstractUIPlugin plugin) {
		super(plugin);
		System.out.println("Configuring user interface access through SWT + XText");
		GuiUtils.setSwtGui(new XtextGui());
		// Logger.getLogger(GamlJavaValidator.class).setLevel(Level.DEBUG);
		// setValidationTrigger(activeWorkbenchWindow(), plugin);
	}

	@Override
	public void configure(final Binder binder) {
		super.configure(binder);
		binder.bind(ILocationInFileProvider.class).to(GamlLocationInFileProvider.class);
		binder
			.bind(String.class)
			.annotatedWith(
				com.google.inject.name.Names.named(XtextContentAssistProcessor.COMPLETION_AUTO_ACTIVATION_CHARS))
			.toInstance(".");
	}

	@SingletonBinding(eager = true)
	public Class<? extends msi.gama.lang.gaml.validation.GamlJavaValidator> bindGamlJavaValidator() {
		return GamlJavaValidator.class;
	}

	@Override
	public Class<? extends IContentAssistProcessor> bindIContentAssistProcessor() {
		return GamlContentAssistProcessor.class;
	}

	public Class<? extends org.eclipse.xtext.ui.editor.contentassist.antlr.ParserBasedContentAssistContextFactory.StatefulFactory> bindStatefulFactory() {
		return ContentAssistContextFactory.class;
	}

	@Override
	public Class<? extends org.eclipse.jface.text.ITextHover> bindITextHover() {
		return GamlDispatchingEObjectTextHover.class;
	}

	// For performance issues on opening files : see http://alexruiz.developerblogs.com/?p=2359
	@Override
	public Class<? extends IResourceSetProvider> bindIResourceSetProvider() {
		return SimpleResourceSetProvider.class;
	}

	@Override
	public void configureXtextEditorErrorTickUpdater(final com.google.inject.Binder binder) {
		binder.bind(IXtextEditorCallback.class).annotatedWith(Names.named("IXtextEditorCallBack")).to( //$NON-NLS-1$
			GamlEditorTickUpdater.class);
	}

	// public Class<? extends EObjectAtOffsetHelper> bindEObjectAtOffsetHelper() {
	// return NonXRefEObjectAtOffset.class;
	// }

	/**
	 * @author Pierrick
	 * @return GAMLSemanticHighlightingCalculator
	 */
	public Class<? extends ISemanticHighlightingCalculator> bindSemanticHighlightingCalculator() {
		return GamlSemanticHighlightingCalculator.class;
	}

	public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration() {
		return GamlHighlightingConfiguration.class;
	}

	@Override
	public Class<? extends org.eclipse.xtext.ui.editor.IXtextEditorCallback> bindIXtextEditorCallback() {
		// TODO Verify this as it is only needed, normally, for languages that do not use the builder infrastructure
		// (see http://www.eclipse.org/forums/index.php/mv/msg/167666/532239/)
		// return ValidatingEditorCallback.class;
		return GamlEditorCallback.class;
	}

	public Class<? extends ISyntaxErrorMessageProvider> bindISyntaxErrorMessageProvider() {
		return GamlSyntaxErrorMessageProvider.class;
	}

	public Class<? extends IEObjectHoverProvider> bindIEObjectHoverProvider() {
		return GamlHoverProvider.class;
	}

	public Class<? extends IEObjectDocumentationProvider> bindIEObjectDocumentationProviderr() {
		return GamlDocumentationProvider.class;
	}

	@Override
	public Provider<IAllContainersState> provideIAllContainersState() {
		return org.eclipse.xtext.ui.shared.Access.getWorkspaceProjectsState();
	}

	public Class<? extends XtextEditor> bindXtextEditor() {
		return GamlEditor.class;
	}

	public Class<? extends XtextSourceViewerConfiguration> bindXtextSourceViewerConfiguration() {
		return GamaSourceViewerConfiguration.class;
	}

	@Override
	public Class<? extends IHyperlinkDetector> bindIHyperlinkDetector() {
		return GamlHyperlinkDetector.class;
	}

	@Override
	public void configureMarkOccurrencesAction(final Binder binder) {
		binder.bind(IActionContributor.class).annotatedWith(Names.named("markOccurrences"))
			.to(GamlMarkOccurrenceActionContributor.class);
		binder.bind(IPreferenceStoreInitializer.class)
			.annotatedWith(Names.named("GamlMarkOccurrenceActionContributor")) //$NON-NLS-1$
			.to(GamlMarkOccurrenceActionContributor.class);
	}

}
