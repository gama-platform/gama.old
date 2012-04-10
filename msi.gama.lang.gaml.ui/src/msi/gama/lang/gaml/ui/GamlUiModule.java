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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml.ui;

import msi.gama.lang.gaml.ui.highlight.*;
import msi.gama.lang.gaml.ui.hover.*;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.*;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.contentassist.XtextContentAssistProcessor;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.syntaxcoloring.*;
import org.eclipse.xtext.ui.editor.validation.ValidatingEditorCallback;
import com.google.inject.*;

/**
 * Use this class to register components to be used within the IDE.
 */
public class GamlUiModule extends msi.gama.lang.gaml.ui.AbstractGamlUiModule {

	public GamlUiModule(final AbstractUIPlugin plugin) {
		super(plugin);
		setValidationTrigger(activeWorkbenchWindow(), plugin);
	}

	@Override
	public void configure(final Binder binder) {
		super.configure(binder);
		binder
			.bind(String.class)
			.annotatedWith(
				com.google.inject.name.Names
					.named(XtextContentAssistProcessor.COMPLETION_AUTO_ACTIVATION_CHARS))
			.toInstance(".:,");
	}

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
		return ValidatingEditorCallback.class;
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

	public static IWorkbenchWindow activeWorkbenchWindow() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		return workbench == null ? null : workbench.getActiveWorkbenchWindow();
	}

	@Override
	public Class<? extends IHyperlinkDetector> bindIHyperlinkDetector() {
		return GamlHyperlinkDetector.class;
	}

	private void setValidationTrigger(final IWorkbenchWindow w, final AbstractUIPlugin plugin) {
		// TODO reenable this for having the generation made everytime an editor is selected.
		if ( w == null || !(plugin instanceof msi.gama.lang.gaml.ui.internal.GamlActivator) ) { return; }
		w.getPartService().addPartListener(new ValidateFileOnActivation());
	}
}
