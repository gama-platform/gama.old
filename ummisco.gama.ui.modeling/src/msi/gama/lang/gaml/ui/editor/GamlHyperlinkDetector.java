/*********************************************************************************************
 * 
 * 
 * 'GamlHyperlinkDetector.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.ui.FileOpener;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.PartInitException;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.ui.editor.hyperlinking.DefaultHyperlinkDetector;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import com.google.inject.Inject;

/**
 * Represents an implementation of interface
 * <code>{@link IHyperlinkDetector}</code> to find and convert
 * {@link CrossReference elements}, at a given location, to {@code IHyperlink}.
 * 
 * @author Alexis Drogoul
 */
public class GamlHyperlinkDetector extends DefaultHyperlinkDetector {

	class ImportHyperlink implements IHyperlink {

		private final URI importUri;
		private final IRegion region;
		private final FileOpener fileOpener;

		ImportHyperlink(final URI importUri, final IRegion region,
				final FileOpener fileOpener) {
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
				}

			} catch (PartInitException e) {
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

	@Inject
	private EObjectAtOffsetHelper eObjectAtOffsetHelper;
	@Inject
	private FileOpener fileOpener;

	@Override
	public IHyperlink[] detectHyperlinks(final ITextViewer textViewer,
			final IRegion region, final boolean canShowMultipleHyperlinks) {
		IXtextDocument document = (IXtextDocument) textViewer.getDocument();
		IHyperlink[] importHyperlinks = importHyperlinks(document, region);
		if (importHyperlinks != NO_HYPERLINKS) {
			return importHyperlinks;
		}
		return document
				.readOnly(new IUnitOfWork<IHyperlink[], XtextResource>() {

					@Override
					public IHyperlink[] exec(final XtextResource resource) {
						return getHelper().createHyperlinksByOffset(resource,
								region.getOffset(), canShowMultipleHyperlinks);
					}
				});
	}

	public URI getURI(final StringLiteral resolved) {
		String target = resolved.getOp();
		if (target == null) {
			return null;
		}
		URI iu = URI.createURI(target, false).resolve(
				resolved.eResource().getURI());
		if (isFileExisting(iu)) {
			return iu;
		}
		return null;
	}

	private IHyperlink[] importHyperlinks(final IXtextDocument document,
			final IRegion region) {
		return document
				.readOnly(new IUnitOfWork<IHyperlink[], XtextResource>() {

					@Override
					public IHyperlink[] exec(final XtextResource resource) {
						EObject resolved = eObjectAtOffsetHelper
								.resolveElementAt(resource, region.getOffset());

						// System.out.println("Hyperlink target:" + resolved ==
						// null ? null : resolved.getClass().getSimpleName());
						if (resolved instanceof StringLiteral) {
							URI iu = getURI((StringLiteral) resolved);
							if (iu != null) {
								IRegion hRegion;
								try {
									hRegion = importUriRegion(document,
											region.getOffset(),
											((StringLiteral) resolved).getOp());
								} catch (BadLocationException e) {
									return NO_HYPERLINKS;
								}
								if (hRegion == null) {
									return NO_HYPERLINKS;
								}
								IHyperlink hyperlink = new ImportHyperlink(iu,
										hRegion, fileOpener);
								return new IHyperlink[] { hyperlink };
							}
						}
						if (!(resolved instanceof Import)) {
							return NO_HYPERLINKS;
						}
						Import anImport = (Import) resolved;
						// if ( !imports.isResolved(anImport) ) { return
						// NO_HYPERLINKS; }
						String importUri = anImport.getImportURI();
						if (importUri == null) {
							return NO_HYPERLINKS;
						}
						URI iu = URI.createURI(importUri, false).resolve(
								resource.getURI());
						IRegion importUriRegion;
						try {
							importUriRegion = importUriRegion(document,
									region.getOffset(), importUri);
						} catch (BadLocationException e) {
							return NO_HYPERLINKS;
						}
						if (importUriRegion == null) {
							return NO_HYPERLINKS;
						}
						IHyperlink hyperlink = new ImportHyperlink(iu,
								importUriRegion, fileOpener);
						return new IHyperlink[] { hyperlink };
					}
				});
	}

	public boolean isFileExisting(final URI uri) {
		IFile file = getFile(uri);
		if (file != null)
			return file.exists();
		return false;
	}

	public IFile getFile(final URI uri) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		String uriAsText = uri.toPlatformString(true);
		IPath path = uriAsText != null ? new Path(uriAsText) : null;
		if (path == null) {
			return null;
		}
		IFile file = root.getFile(path);
		return file;
	}

	private IRegion importUriRegion(final IXtextDocument document,
			final int offset, final String importUri)
			throws BadLocationException {
		int lineNumber = document.getLineOfOffset(offset);
		int lineLength = document.getLineLength(lineNumber);
		int lineOffset = document.getLineOffset(lineNumber);
		String line = document.get(lineOffset, lineLength);
		int uriIndex = line.indexOf(importUri);
		return new Region(lineOffset + uriIndex, importUri.length());
	}
}
