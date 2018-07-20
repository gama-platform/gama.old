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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.PartInitException;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.hyperlinking.DefaultHyperlinkDetector;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.concurrent.CancelableUnitOfWork;

import com.google.inject.Inject;

import msi.gama.lang.gaml.gaml.HeadlessExperiment;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.ui.utils.FileOpener;

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
		private final FileOpener fileOpener;

		ImportHyperlink(final URI importUri, final IRegion region, final FileOpener fileOpener) {
			this.importUri = importUri;
			this.region = region;
			this.fileOpener = fileOpener;
		}

		@Override
		public void open() {
			try {
				if (importUri.isPlatformResource()) {
					fileOpener.openFileInWorkspace(importUri);
					return;
				} else if (importUri.isFile()) {
					fileOpener.openFileInFileSystem(importUri);
				}

			} catch (final PartInitException e) {
				System.out.println("Unable to open " + importUri.toString());
			}
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
	@Inject private FileOpener fileOpener;

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
		final String target = resolved.getOp();
		if (target == null) { return null; }
		final java.io.File f = new java.io.File(target);
		if (f.exists()) {
			// We have an absolute file
			final URI fileURI = URI.createFileURI(target);
			final URI platformURI = URI.createFileURI(Platform.getInstanceLocation().getURL().toString());

			System.out.println(platformURI);
			return fileURI;
		} else {
			final URI first = URI.createURI(target, false);
			if (!first.isRelative() && isFileExisting(first)) { return first; }
			final URI iu = first.resolve(resolved.eResource().getURI());
			if (isFileExisting(iu)) { return iu; }
			return null;
		}
	}

	private IHyperlink[] importHyperlinks(final IXtextDocument document, final IRegion region) {
		return document.readOnly(resource -> {
			final EObject resolved = eObjectAtOffsetHelper.resolveElementAt(resource, region.getOffset());

			// System.out.println("Hyperlink target:" + resolved ==
			// null ? null : resolved.getClass().getSimpleName());
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
					final IHyperlink hyperlink1 = new ImportHyperlink(iu1, hRegion, fileOpener);
					return new IHyperlink[] { hyperlink1 };
				}
			}
			String importUri = null;
			if (resolved instanceof Import) {
				final Import anImport = (Import) resolved;
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
			final IHyperlink hyperlink2 = new ImportHyperlink(iu2, importUriRegion, fileOpener);
			return new IHyperlink[] { hyperlink2 };
		});
	}

	public boolean isFileExisting(final URI uri) {
		if (uri.isFile()) {
			if (new java.io.File(java.net.URI.create(uri.toString())).exists()) { return true; }
		}
		final IFile file = getFile(uri);
		if (file != null) { return file.exists(); }
		return false;
	}

	public IFile getFile(final URI uri) {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final String uriAsText = uri.toPlatformString(true);
		final IPath path = uriAsText != null ? new Path(uriAsText) : null;
		if (path == null) { return null; }
		final IFile file = root.getFile(path);
		return file;
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
