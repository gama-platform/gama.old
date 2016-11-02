/*********************************************************************************************
 *
 * 'NavigatorSorter.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import java.text.Collator;

import org.eclipse.jface.viewers.ViewerSorter;

import msi.gama.common.interfaces.IKeyword;
import ummisco.gama.ui.navigator.WrappedSyntacticContent.WrappedExperimentContent;

public class NavigatorSorter extends ViewerSorter {

	public NavigatorSorter() {
	}

	public NavigatorSorter(final Collator collator) {
		super(collator);
	}

	@Override
	public int category(final Object e2) {
		if (e2 instanceof WrappedFolder)
			return -1;
		if (e2 instanceof WrappedExperimentContent)
			return 100;
		if (e2 instanceof WrappedSyntacticContent) {
			final WrappedSyntacticContent w = (WrappedSyntacticContent) e2;
			if (w.element.isSpecies() && w.element.getKeyword().equals(IKeyword.GRID))
				return 0;
			if (w.element.isSpecies())
				return 1;
			if (!w.element.hasChildren())
				return 2;
			if (w.element.hasChildren() && w.element.getKeyword().equals(IKeyword.ACTION))
				return 3;
			return 4;
		}
		return 0;
	}

}
