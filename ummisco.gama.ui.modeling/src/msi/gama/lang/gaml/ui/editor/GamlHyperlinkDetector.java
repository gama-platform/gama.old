/*********************************************************************************************
 *
 * 'GamlHyperlinkDetector.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.hyperlinking.DefaultHyperlinkDetector;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.concurrent.CancelableUnitOfWork;

import com.google.inject.Inject;

import msi.gama.common.util.FileUtils;
import msi.gama.lang.gaml.gaml.HeadlessExperiment;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.StringLiteral;
import ummisco.gama.ui.commands.FileOpener;

/**
 * Represents an implementation of interface <code>{@link IHyperlinkDetector}</code> to find and convert
 * {@link CrossReference elements}, at a given location, to {@code IHyperlink}.
 * 
 * @author Alexis Drogoul
 */
public class GamlHyperlinkDetector extends DefaultHyperlinkDetector {

	class ImportHyperlink implements IHyperlink {

		private final URI importUri;
		private final IRegion region;

		ImportHyperlink(final URI importUri, final IRegion region) {
			this.importUri = importUri;
			this.region = region;
		}

		@Override
		public void open() {
			FileOpener.openFile(importUri);
		}

		@Override
		public String getTypeLabel() {
			return null;
		}

		@Override
		public IRegion getHyperlinkRegion() {
			return region;
		}

		@Override
		public String getHyperlinkText() {
			return null;
		}
	}

	private static final IHyperlink[] NO_HYPERLINKS = null;

	@Inject private EObjectAtOffsetHelper eObjectAtOffsetHelper;

	@Override
	public IHyperlink[] detectHyperlinks(final ITextViewer textViewer, final IRegion region,
			final boolean canShowMultipleHyperlinks) {
		final IXtextDocument document = (IXtextDocument) textViewer.getDocument();
		final IHyperlink[] importHyperlinks = importHyperlinks(document, region);
		if (importHyperlinks != NO_HYPERLINKS) { return importHyperlinks; }
		return document.readOnly(new CancelableUnitOfWork<IHyperlink[], XtextResource>() {

			@Override
			public IHyperlink[] exec(final XtextResource resource, final CancelIndicator c) {
				return getHelper().createHyperlinksByOffset(resource, region.getOffset(), canShowMultipleHyperlinks);
			}
		});
	}

	public URI getURI(final StringLiteral resolved) {
		return FileUtils.getURI(resolved.getOp(), resolved.eResource().getURI());
	}

	private IHyperlink[] importHyperlinks(final IXtextDocument document, final IRegion region) {
		return document.readOnly(resource -> {
			final EObject resolved = eObjectAtOffsetHelper.resolveElementAt(resource, region.getOffset());

			if (resolved instanceof StringLiteral) {
				final URI iu1 = getURI((StringLiteral) resolved);
				if (iu1 != null) {
					IRegion hRegion;
					try {
						hRegion = importUriRegion(document, region.getOffset(), ((StringLiteral) resolved).getOp());
					} catch (final BadLocationException e1) {
						return NO_HYPERLINKS;
					}
					if (hRegion == null) { return NO_HYPERLINKS; }
					final IHyperlink hyperlink1 = new ImportHyperlink(iu1, hRegion);
					return new IHyperlink[] { hyperlink1 };
				}
			}
			String importUri = null;
			if (resolved instanceof Import) {
				importUri = ((Import) resolved).getImportURI();
			} else if (resolved instanceof HeadlessExperiment) {
				importUri = ((HeadlessExperiment) resolved).getImportURI();
			}
			if (importUri == null) { return NO_HYPERLINKS; }
			final URI iu2 = URI.createURI(importUri, false).resolve(resource.getURI());
			IRegion importUriRegion;
			try {
				importUriRegion = importUriRegion(document, region.getOffset(), importUri);
			} catch (final BadLocationException e2) {
				return NO_HYPERLINKS;
			}
			if (importUriRegion == null) { return NO_HYPERLINKS; }
			final IHyperlink hyperlink2 = new ImportHyperlink(iu2, importUriRegion);
			return new IHyperlink[] { hyperlink2 };
		});
	}

	private IRegion importUriRegion(final IXtextDocument document, final int offset, final String importUri)
			throws BadLocationException {
		final int lineNumber = document.getLineOfOffset(offset);
		final int lineLength = document.getLineLength(lineNumber);
		final int lineOffset = document.getLineOffset(lineNumber);
		final String line = document.get(lineOffset, lineLength);
		final int uriIndex = line.indexOf(importUri);
		return new Region(lineOffset + uriIndex, importUri.length());
	}
}
