/*******************************************************************************
 * Copyright (c) 2007-2008 SAS Institute Inc., ILOG S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * SAS Institute Inc. - initial API and implementation
 * ILOG S.A. - initial API and implementation
 *******************************************************************************/
package msi.gama.gui.swt.swing;

import java.util.EventListener;

/**
 * A listener that is notified on events related to
 * the size of the embedded Swing control
 */
public interface SizeListener extends EventListener {

	/**
	 * See {@link SwingControl#preferredSizeChanged(org.eclipse.swt.graphics.Point, org.eclipse.swt.graphics.Point, org.eclipse.swt.graphics.Point)}
	 * for information on when this method is called.
	 * 
	 * @param event
	 */
	void preferredSizeChanged(SizeEvent event);
}
