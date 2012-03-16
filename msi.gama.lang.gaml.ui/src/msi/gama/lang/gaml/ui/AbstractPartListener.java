/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package msi.gama.lang.gaml.ui;

import org.eclipse.ui.*;

/**
 * Base class for implementations of <code>{@link IPartListener2}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public abstract class AbstractPartListener implements IPartListener2 {
  @Override public void partActivated(IWorkbenchPartReference ref) {}
  @Override public void partDeactivated(IWorkbenchPartReference ref) {}

  @Override public void partBroughtToTop(IWorkbenchPartReference ref) {}

  @Override public void partOpened(IWorkbenchPartReference ref) {}
  @Override public void partClosed(IWorkbenchPartReference ref) {}

  @Override public void partVisible(IWorkbenchPartReference ref) {}
  @Override public void partHidden(IWorkbenchPartReference ref) {}

  @Override public void partInputChanged(IWorkbenchPartReference ref) {}
}
