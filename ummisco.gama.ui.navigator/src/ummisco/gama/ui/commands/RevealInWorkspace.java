/*******************************************************************************************************
 *
 * RevealInWorkspace.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import ummisco.gama.ui.navigator.GamaNavigator;
import ummisco.gama.ui.navigator.contents.LinkedFile;
import ummisco.gama.ui.navigator.contents.WrappedFile;

/**
 * The Class RevealInWorkspace.
 */
public class RevealInWorkspace extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IStructuredSelection sel = HandlerUtil.getCurrentStructuredSelection(event);
		if (sel.isEmpty()) { return null; }
		final IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (!(part instanceof GamaNavigator)) { return null; }
		final GamaNavigator nav = (GamaNavigator) part;
		final List<Object> selection = sel.toList();
		final List<WrappedFile> newSelection = new ArrayList<>();
		for (final Object o : selection) {
			if (o instanceof LinkedFile) {
				newSelection.add(((LinkedFile) o).getTarget());
			}
		}
		if (newSelection.isEmpty()) { return null; }
		nav.selectReveal(new StructuredSelection(newSelection));
		return this;
	}

}
