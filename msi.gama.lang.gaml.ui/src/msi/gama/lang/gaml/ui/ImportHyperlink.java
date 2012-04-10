/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package msi.gama.lang.gaml.ui;

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
				fileOpener.openProtoFileInWorkspace(importUri);
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
