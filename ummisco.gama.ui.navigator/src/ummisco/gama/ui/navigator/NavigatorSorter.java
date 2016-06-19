package ummisco.gama.ui.navigator;

import java.text.Collator;
import org.eclipse.jface.viewers.*;

public class NavigatorSorter extends ViewerSorter {

	public NavigatorSorter() {}

	public NavigatorSorter(final Collator collator) {
		super(collator);
	}

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		if ( e1 instanceof WrappedGamlObject && e2 instanceof WrappedGamlObject ) { return ((WrappedGamlObject) e1)
			.compareTo((WrappedGamlObject) e2); }
		return super.compare(viewer, e1, e2);
	}

}
