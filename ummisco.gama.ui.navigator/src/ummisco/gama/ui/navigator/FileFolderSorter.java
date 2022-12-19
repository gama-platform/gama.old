/*******************************************************************************************************
 *
 * FileFolderSorter.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator;

import java.text.Collator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.navigator.contents.WrappedContainer;

/**
 * The Class FileFolderSorter.
 */
public class FileFolderSorter extends ViewerComparator {

	/** The by date. */
	public static boolean BY_DATE = false;

	/**
	 * Instantiates a new file folder sorter.
	 */
	public FileFolderSorter() {}

	/**
	 * Instantiates a new file folder sorter.
	 *
	 * @param collator the collator
	 */
	public FileFolderSorter(final Collator collator) { // NO_UCD (unused code)
		super(collator);
	}

	@Override
	public int category(final Object element) {
		if (element instanceof WrappedContainer) { return 0; }
		if (element instanceof IFile) { return 1; }
		return 2;
	}

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		final int cat1 = category(e1);
		final int cat2 = category(e2);

		if (cat1 != cat2) { return cat1 - cat2; }
		if (BY_DATE) {
			final Long date1 = getDate(e1);
			final Long date2 = getDate(e2);
			return date2.compareTo(date1);
		} else {
			final String name1 = getLabel(viewer, e1);
			final String name2 = getLabel(viewer, e2);

			// use the comparator to compare the strings
			return getComparator().compare(name1, name2);
		}
	}

	/**
	 * Gets the date.
	 *
	 * @param e the e
	 * @return the date
	 */
	private Long getDate(final Object e) {
		final IResource r = ResourceManager.getResource(e);
		if (r != null) {
			return r.getLocalTimeStamp();
		} else {
			return Long.MAX_VALUE;
		}
	}

	/**
	 * Gets the label.
	 *
	 * @param viewer the viewer
	 * @param e1 the e 1
	 * @return the label
	 */
	private String getLabel(final Viewer viewer, final Object e1) {
		String name1;
		if (viewer == null || !(viewer instanceof ContentViewer)) {
			name1 = e1.toString();
		} else {
			final IBaseLabelProvider prov = ((ContentViewer) viewer).getLabelProvider();
			if (prov instanceof ILabelProvider) {
				final ILabelProvider lprov = (ILabelProvider) prov;
				name1 = lprov.getText(e1);
			} else {
				name1 = e1.toString();
			}
		}
		if (name1 == null) {
			name1 = "";//$NON-NLS-1$
		}
		return name1;
	}

}
