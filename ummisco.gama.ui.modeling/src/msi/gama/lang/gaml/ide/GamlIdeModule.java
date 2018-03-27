/*********************************************************************************************
 *
 * 'GamlUiModule.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ide;

import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.xtext.builder.builderState.IMarkerUpdater;
import org.eclipse.xtext.builder.resourceloader.IResourceLoader;
import org.eclipse.xtext.builder.resourceloader.ResourceLoaderProviders;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.ide.editor.contentassist.FQNPrefixMatcher;
import org.eclipse.xtext.ide.editor.contentassist.IPrefixMatcher;
import org.eclipse.xtext.ide.editor.contentassist.IProposalConflictHelper;
import org.eclipse.xtext.ide.editor.contentassist.antlr.AntlrProposalConflictHelper;
import org.eclipse.xtext.ide.editor.contentassist.antlr.IContentAssistParser;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.parser.IEncodingProvider;
import org.eclipse.xtext.parser.antlr.ISyntaxErrorMessageProvider;
import org.eclipse.xtext.resource.clustering.DynamicResourceClusteringPolicy;
import org.eclipse.xtext.resource.clustering.IResourceClusteringPolicy;
import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.eclipse.xtext.service.DispatchingProvider;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.ui.IImageHelper;
import org.eclipse.xtext.ui.IImageHelper.IImageDescriptorHelper;
import org.eclipse.xtext.ui.editor.IXtextEditorCallback;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.XtextSourceViewer;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;
import org.eclipse.xtext.ui.editor.actions.IActionContributor;
import org.eclipse.xtext.ui.editor.autoedit.AbstractEditStrategyProvider;
import org.eclipse.xtext.ui.editor.contentassist.ITemplateProposalProvider;
import org.eclipse.xtext.ui.editor.contentassist.XtextContentAssistProcessor;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.model.IResourceForEditorInputFactory;
import org.eclipse.xtext.ui.editor.model.ResourceForIEditorInputFactory;
import org.eclipse.xtext.ui.editor.outline.actions.IOutlineContribution;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.ui.resource.SimpleResourceSetProvider;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.name.Names;

import msi.gama.common.interfaces.IGamlLabelProvider;
import msi.gama.lang.gaml.ide.contentassist.antlr.GamlParser;
import msi.gama.lang.gaml.parsing.GamlSyntaxErrorMessageProvider;
import msi.gama.lang.gaml.resource.GamlEncodingProvider;
import msi.gama.lang.gaml.ui.contentassist.GamlTemplateProposalProvider;
import msi.gama.lang.gaml.ui.decorators.GamlImageHelper;
import msi.gama.lang.gaml.ui.decorators.GamlMarkerUpdater;
import msi.gama.lang.gaml.ui.editor.GamaAutoEditStrategyProvider;
import msi.gama.lang.gaml.ui.editor.GamaSourceViewerFactory;
import msi.gama.lang.gaml.ui.editor.GamlEditor;
import msi.gama.lang.gaml.ui.editor.GamlEditor.GamaSourceViewerConfiguration;
import msi.gama.lang.gaml.ui.editor.GamlEditorTickUpdater;
import msi.gama.lang.gaml.ui.editor.GamlHyperlinkDetector;
import msi.gama.lang.gaml.ui.editor.GamlMarkOccurrenceActionContributor;
import msi.gama.lang.gaml.ui.editor.folding.GamaFoldingActionContributor;
import msi.gama.lang.gaml.ui.editor.folding.GamaFoldingRegionProvider;
import msi.gama.lang.gaml.ui.highlight.GamlHighlightingConfiguration;
import msi.gama.lang.gaml.ui.highlight.GamlReconciler;
import msi.gama.lang.gaml.ui.highlight.GamlSemanticHighlightingCalculator;
import msi.gama.lang.gaml.ui.hover.GamlDocumentationProvider;
import msi.gama.lang.gaml.ui.hover.GamlHoverProvider;
import msi.gama.lang.gaml.ui.hover.GamlHoverProvider.GamlDispatchingEObjectTextHover;
import msi.gama.lang.gaml.ui.labeling.GamlLabelProvider;
import msi.gama.lang.gaml.ui.outline.GamlLinkWithEditorOutlineContribution;
import msi.gama.lang.gaml.ui.outline.GamlOutlinePage;
import msi.gama.lang.gaml.ui.outline.GamlSortOutlineContribution;
import msi.gama.lang.gaml.ui.templates.GamlTemplateStore;
import msi.gama.lang.gaml.ui.utils.ModelRunner;
import ummisco.gama.ui.interfaces.IModelRunner;

/**
 * Use this class to register components to be used within the IDE.
 */
public class GamlIdeModule extends AbstractGamlIdeModule {

	/**
	 * @see org.eclipse.xtext.service.AbstractGenericModule#configure(com.google.inject.Binder)
	 */
	@Override
	public void configure(Binder binder) {
		super.configure(binder);
		configureContentAssistLexer(binder);
		binder.bind(IContentAssistParser.class).to(GamlParser.class);
		binder.bind(IProposalConflictHelper.class).to(AntlrProposalConflictHelper.class);
		binder.bind(IPrefixMatcher.class).to(FQNPrefixMatcher.class);


	}
	
}
