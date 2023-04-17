/*******************************************************************************************************
 *
 * NavigatorSearchControl.java, in ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.navigator;

import static msi.gama.application.workbench.ThemeHelper.isDark;
import static ummisco.gama.ui.resources.IGamaColors.VERY_DARK_GRAY;
import static ummisco.gama.ui.resources.IGamaColors.VERY_LIGHT_GRAY;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.swt.IFocusService;

import msi.gama.runtime.PlatformHelper;
import one.util.streamex.StreamEx;
import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.navigator.contents.VirtualContent;
import ummisco.gama.ui.navigator.contents.WrappedGamaFile;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.views.toolbar.GamaToolbarSimple;

/**
 * The class EditToolbarFindControls.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */
public class NavigatorSearchControl {

	/**
	 * Should select.
	 *
	 * @param o
	 *            the o
	 * @return true, if successful
	 */
	boolean shouldSelect(final Object o) {
		if (!(o instanceof WrappedGamaFile file)) return false;
		if (file.getName().toLowerCase().contains(pattern) || file.hasTag(pattern)) return true;
		return false;
	}

	/**
	 * The Class NamePatternFilter.
	 */
	protected class NamePatternFilter extends ViewerFilter {

		/** The already selected. */
		@SuppressWarnings ("rawtypes") Set alreadySelected = new HashSet<>();

		/**
		 * Reset.
		 */
		public void reset() {
			alreadySelected = StreamEx.ofValues(ResourceManager.cache.asMap()).filter(r -> shouldSelect(r)).toSet();
		}

		/**
		 * Instantiates a new name pattern filter.
		 */
		public NamePatternFilter() {}

		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			return select((VirtualContent<?>) element, true);
		}

		/**
		 * Select.
		 *
		 * @param element
		 *            the element
		 * @param b
		 *            the b
		 * @return true, if successful
		 */
		@SuppressWarnings ("unchecked")
		private boolean select(final VirtualContent<?> element, final boolean b) {
			if (alreadySelected.contains(element)) return true;
			if (internalSelect(element, b)) {
				alreadySelected.add(element);
				return true;
			}
			return false;
		}

		/**
		 * Internal select.
		 *
		 * @param element
		 *            the element
		 * @param considerVirtualContent
		 *            the consider virtual content
		 * @return true, if successful
		 */
		private boolean internalSelect(final VirtualContent<?> element, final boolean considerVirtualContent) {
			if (pattern.isEmpty()) return true;
			switch (element.getType()) {
				case FILE:
					return shouldSelect(element);
				case CATEGORY:
				case FILE_REFERENCE:
				case GAML_ELEMENT:
					return considerVirtualContent;
				case FOLDER:
				case PROJECT:
				case ROOT:
				case VIRTUAL_FOLDER:
				default:
					final Object[] children = element.getNavigatorChildren();
					for (final Object element2 : children) {
						if (select((VirtualContent<?>) element2, false)) return true;
					}
					return false;
			}
		}
	}

	/** The find. */
	Text find;

	/** The Constant EMPTY. */
	private static final String EMPTY = "Find model..."; //$NON-NLS-1$

	/** The pattern. */
	String pattern;

	/** The navigator. */
	GamaNavigator navigator;

	/** The tree viewer. */
	CommonViewer treeViewer;

	/**
	 * List of expanded elements at the start of a search used internally to restore the state before search
	 **/
	private Object[] expandedElementsBeforeSearch = null;

	/** The filter. */
	final NamePatternFilter filter = new NamePatternFilter();

	/**
	 * Instantiates a new navigator search control.
	 *
	 * @param navigator
	 *            the navigator
	 */
	public NavigatorSearchControl(final GamaNavigator navigator) {
		this.navigator = navigator;
	}

	/**
	 * Initialize.
	 */
	public void initialize() {
		treeViewer = navigator.getCommonViewer();
	}

	/**
	 * Fill.
	 *
	 * @param toolbar
	 *            the toolbar
	 * @return the navigator search control
	 */
	public NavigatorSearchControl fill(final GamaToolbarSimple toolbar) {
		Composite parent = toolbar;
		Color c = parent.getBackground();
		if (PlatformHelper.isWindows()) {
			parent = new Composite(toolbar, SWT.NONE);
			final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			data.heightHint = 24;
			data.widthHint = 100;
			parent.setLayoutData(data);
			final GridLayout layout = new GridLayout();
			parent.setLayout(layout);
			GamaColors.setBackground(c, parent);
		}

		find = new Text(parent, SWT.SEARCH | SWT.ICON_SEARCH);
		final IFocusService focusService = navigator.getSite().getService(IFocusService.class);
		focusService.addFocusTracker(find, "search");
		final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.heightHint = 16;
		data.widthHint = 100;
		find.setLayoutData(data);
		find.setMessage(EMPTY);

		toolbar.control(parent == toolbar ? find : parent, 100);
		GamaColors.setBackAndForeground(c, isDark() ? VERY_LIGHT_GRAY.color() : VERY_DARK_GRAY.color(), find);
		find.addModifyListener(modifyListener);
		find.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(final KeyEvent e) {}

			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.character == SWT.ESC) {
					find.setText("");
					navigator.setFocus();
				}
			}
		});

		return this;
	}

	/** The reset job. */
	UIJob resetJob = new UIJob("Reset") {

		@Override
		public IStatus runInUIThread(final IProgressMonitor monitor) {
			resetSearch();
			return Status.OK_STATUS;
		}
	};

	/** The search job. */
	UIJob searchJob = new UIJob("Search") {

		@Override
		public IStatus runInUIThread(final IProgressMonitor monitor) {
			doSearch();
			return Status.OK_STATUS;
		}
	};

	/** The modify listener. */
	private final ModifyListener modifyListener = e -> {
		pattern = ((Text) e.widget).getText().toLowerCase();
		if (pattern.isEmpty()) {
			searchJob.cancel();
			resetJob.schedule(200);
		} else {
			if (searchJob.getState() == Job.SLEEPING || searchJob.getState() == Job.WAITING) { searchJob.cancel(); }
			searchJob.schedule(200);

		}
	};

	/**
	 * Do search.
	 */
	public void doSearch() {
		if (expandedElementsBeforeSearch == null) { expandedElementsBeforeSearch = treeViewer.getExpandedElements(); }
		treeViewer.getControl().setRedraw(false);
		filter.reset();
		if (!Arrays.asList(treeViewer.getFilters()).contains(filter)) {
			treeViewer.addFilter(filter);
		} else {
			treeViewer.refresh(false);
		}
		treeViewer.expandAll();
		treeViewer.getControl().setRedraw(true);
	}

	/**
	 * Reset search.
	 */
	public void resetSearch() {
		treeViewer.getControl().setRedraw(false);
		if (Arrays.asList(treeViewer.getFilters()).contains(filter)) {
			treeViewer.removeFilter(filter);
		} else {
			treeViewer.refresh(false);
		}
		if (expandedElementsBeforeSearch != null) {
			treeViewer.collapseAll();
			treeViewer.setExpandedElements(expandedElementsBeforeSearch);
			expandedElementsBeforeSearch = null;
		}
		treeViewer.getControl().setRedraw(true);
	}

	/**
	 * Search for.
	 *
	 * @param name
	 *            the name
	 */
	public void searchFor(final String name) {
		find.setText(name);
		pattern = name;
		doSearch();

	}

}
