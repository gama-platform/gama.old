package ummisco.gama.ui.navigator;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import msi.gama.common.interfaces.IKeyword;

public class NavigatorSorter extends ViewerSorter {

	public NavigatorSorter() {
	}

	public NavigatorSorter(final Collator collator) {
		super(collator);
	}

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		// if (e1 instanceof WrappedSyntacticContent && e2 instanceof
		// WrappedSyntacticContent) {
		// return ((WrappedSyntacticContent)
		// e1).compareTo((WrappedSyntacticContent) e2);
		// }
		// if (e1 instanceof WrappedGamlObject && e2 instanceof
		// WrappedGamlObject) {
		// return ((WrappedGamlObject) e1).compareTo((WrappedGamlObject) e2);
		// }
		return super.compare(viewer, e1, e2);
	}

	@Override
	public int category(final Object e2) {
		if (e2 instanceof WrappedFolder)
			return -1;
		if (e2 instanceof WrappedExperiment)
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
