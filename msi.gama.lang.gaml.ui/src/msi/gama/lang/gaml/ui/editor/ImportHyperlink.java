/*********************************************************************************************
 * 
 *
 * 'ImportHyperlink.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import msi.gama.lang.gaml.ui.FileOpener;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.PartInitException;

/**
 * A hyperlink for imported .proto files.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
class ImportHyperlink implements IHyperlink {

	private static Logger logger = Logger.getLogger(ImportHyperlink.class);

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
			if ( importUri.isPlatformResource() ) {
				fileOpener.openFileInWorkspace(importUri);
				return;
			}

		} catch (PartInitException e) {
			logger.error("Unable to open " + importUri.toString(), e);
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
