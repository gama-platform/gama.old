/*********************************************************************************************
 *
 * 'FileFolderSorter.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import java.text.Collator;
import org.eclipse.core.resources.*;
import org.eclipse.jface.viewers.*;

public class FileFolderSorter extends ViewerSorter {

	public static boolean BY_DATE = false;

	public FileFolderSorter() {}

	public FileFolderSorter(final Collator collator) {
		super(collator);
	}

	@Override
	public int category(final Object element) {
		if ( element instanceof IContainer ) { return 0; }
		if ( element instanceof IFile ) { return 1; }
		return 2;
	}

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);

		if ( cat1 != cat2 ) { return cat1 - cat2; }
		if ( BY_DATE ) {
			Long date1 = getDate(e1);
			Long date2 = getDate(e2);
			return date2.compareTo(date1);
		} else {
			String name1 = getLabel(viewer, e1);
			String name2 = getLabel(viewer, e2);

			// use the comparator to compare the strings
			return getComparator().compare(name1, name2);
		}
	}

	private Long getDate(final Object e) {
		if ( e instanceof IResource ) {
			return ((IResource) e).getLocalTimeStamp();
		} else {
			return Long.MAX_VALUE;
		}
	}

	private String getLabel(final Viewer viewer, final Object e1) {
		String name1;
		if ( viewer == null || !(viewer instanceof ContentViewer) ) {
			name1 = e1.toString();
		} else {
			IBaseLabelProvider prov = ((ContentViewer) viewer).getLabelProvider();
			if ( prov instanceof ILabelProvider ) {
				ILabelProvider lprov = (ILabelProvider) prov;
				name1 = lprov.getText(e1);
			} else {
				name1 = e1.toString();
			}
		}
		if ( name1 == null ) {
			name1 = "";//$NON-NLS-1$
		}
		return name1;
	}

}
