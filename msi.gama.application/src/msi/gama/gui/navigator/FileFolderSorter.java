package msi.gama.gui.navigator;

import java.text.Collator;
import org.eclipse.core.resources.*;
import org.eclipse.jface.viewers.*;

public class FileFolderSorter extends ViewerSorter {

	public FileFolderSorter() {}

	public FileFolderSorter(final Collator collator) {
		super(collator);
	}

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		if ( e1 instanceof IContainer && e2 instanceof IFile ) { return -1; }
		if ( e2 instanceof IContainer && e1 instanceof IFile ) { return 1; }
		return super.compare(viewer, e1, e2);
	}

}
