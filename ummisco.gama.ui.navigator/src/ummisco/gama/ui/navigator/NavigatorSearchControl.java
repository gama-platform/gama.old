/*********************************************************************************************
 *
 * 'EditorSearchControls.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.swt.IFocusService;

import one.util.streamex.StreamEx;
import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.navigator.contents.VirtualContent;
import ummisco.gama.ui.navigator.contents.VirtualContent.VirtualContentType;
import ummisco.gama.ui.navigator.contents.WrappedFile;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.views.toolbar.GamaToolbarSimple;

/**
 * The class EditToolbarFindControls.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */
public class NavigatorSearchControl {

	protected class NamePatternFilter extends ViewerFilter {

		@SuppressWarnings ("rawtypes") Set alreadySelected = new HashSet<>();

		public void reset() {
			alreadySelected =
					StreamEx.ofValues(ResourceManager.cache.asMap()).filter(r -> r.getType() == VirtualContentType.FILE
							&& ((WrappedFile) r).isGamaFile() && r.getName().toLowerCase().contains(pattern)).toSet();
		}

		public NamePatternFilter() {}

		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			return select((VirtualContent) element, true);
		}

		@SuppressWarnings ("unchecked")
		private boolean select(final VirtualContent element, final boolean b) {
			if (alreadySelected.contains(element))
				return true;
			if (internalSelect(element, b)) {
				alreadySelected.add(element);
				return true;
			}
			return false;
		}

		private boolean internalSelect(final VirtualContent element, final boolean considerVirtualContent) {
			if (pattern.isEmpty())
				return true;
			switch (element.getType()) {
				case FILE:
					return ((WrappedFile) element).isGamaFile() && element.getName().toLowerCase().contains(pattern);
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
					for (int i = 0; i < children.length; i++)
						if (select((VirtualContent) children[i], false))
							return true;
					return false;
			}
		}
	}

	private static final String EMPTY = "Find model..."; //$NON-NLS-1$
	private String pattern;
	GamaNavigator navigator;
	CommonViewer treeViewer;
	final NamePatternFilter filter = new NamePatternFilter();

	public NavigatorSearchControl(final GamaNavigator navigator) {
		this.navigator = navigator;
	}

	public void initialize() {
		treeViewer = navigator.getCommonViewer();
	}

	public NavigatorSearchControl fill(final GamaToolbarSimple toolbar) {

		final Text find = new Text(toolbar, SWT.SEARCH | SWT.ICON_SEARCH);
		final IFocusService focusService = navigator.getSite().getService(IFocusService.class);
		focusService.addFocusTracker(find, "search");
		final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.heightHint = 24;
		data.widthHint = 100;
		find.setLayoutData(data);
		find.setBackground(IGamaColors.WHITE.color());
		find.setForeground(IGamaColors.BLACK.color());
		find.setMessage(EMPTY);
		toolbar.control(find, 100);
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

	UIJob resetJob = new UIJob("Reset") {

		@Override
		public IStatus runInUIThread(final IProgressMonitor monitor) {
			resetSearch();
			return Status.OK_STATUS;
		}
	};

	UIJob searchJob = new UIJob("Search") {

		@Override
		public IStatus runInUIThread(final IProgressMonitor monitor) {
			doSearch();
			return Status.OK_STATUS;
		}
	};

	private final ModifyListener modifyListener = e -> {
		pattern = ((Text) e.widget).getText().toLowerCase();
		if (pattern.isEmpty()) {
			searchJob.cancel();
			resetJob.schedule(200);
		} else {
			if (searchJob.getState() == Job.SLEEPING || searchJob.getState() == Job.WAITING)
				searchJob.cancel();
			searchJob.schedule(200);

		}
	};

	public void doSearch() {
		treeViewer.getControl().setRedraw(false);
		filter.reset();
		if (!Arrays.asList(treeViewer.getFilters()).contains(filter))
			treeViewer.addFilter(filter);
		else
			treeViewer.refresh(false);
		treeViewer.expandAll();
		treeViewer.getControl().setRedraw(true);
	}

	public void resetSearch() {
		treeViewer.getControl().setRedraw(false);
		if (Arrays.asList(treeViewer.getFilters()).contains(filter))
			treeViewer.removeFilter(filter);
		else
			treeViewer.refresh(false);
		treeViewer.getControl().setRedraw(true);
	}

}
