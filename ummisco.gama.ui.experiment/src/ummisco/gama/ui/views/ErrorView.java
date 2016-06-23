/*********************************************************************************************
 *
 *
 * 'ErrorView.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.ItemList;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.utils.PreferencesHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.inspectors.ExpandableItemsView;

public class ErrorView extends ExpandableItemsView<GamaRuntimeException> implements IGamaView.Error {

	public static String ID = IGui.ERROR_VIEW_ID;
	int numberOfDisplayedErrors = GamaPreferences.CORE_ERRORS_NUMBER.getValue();
	boolean mostRecentFirst = GamaPreferences.CORE_RECENT.getValue();
	private volatile List<GamaRuntimeException> exceptions = new ArrayList<>();
	private final ExceptionGatherer collector = new ExceptionGatherer();

	class ExceptionGatherer extends Job {

		public ExceptionGatherer() {
			super("Runtime error collector");
		}

		final BlockingQueue<GamaRuntimeException> queue = new LinkedBlockingQueue();
		volatile boolean running;

		void offer(final GamaRuntimeException ex) {
			queue.offer(ex);
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			while (running) {
				while (queue.isEmpty()) {
					try {
						Thread.sleep(500);
					} catch (final InterruptedException e) {
						return Status.OK_STATUS;
					}
				}
				if (!running)
					return Status.CANCEL_STATUS;
				process();
			}
			return Status.OK_STATUS;
		}

		public void stop() {
			running = false;
		}

		private synchronized void process() {
			final ArrayList<GamaRuntimeException> array = new ArrayList(queue);
			queue.clear();
			final GamaRuntimeException firstEx = array.get(0);
			if (GamaPreferences.CORE_REVEAL_AND_STOP.getValue()) {
				firstEx.setReported();
				gotoEditor(firstEx);
				if (GamaPreferences.CORE_SHOW_ERRORS.getValue()) {
					final List<GamaRuntimeException> newList = new ArrayList();
					newList.add(firstEx);
					updateUI(newList);
				}

			} else if (GamaPreferences.CORE_SHOW_ERRORS.getValue()) {
				final ArrayList<GamaRuntimeException> oldExcp = new ArrayList(exceptions);
				for (final GamaRuntimeException newEx : array) {
					if (oldExcp.size() == 0) {
						oldExcp.add(newEx);
					} else {
						boolean toAdd = true;
						for (final GamaRuntimeException oldEx : oldExcp.toArray(new GamaRuntimeException[0])) {
							if (oldEx.equivalentTo(newEx)) {
								if (oldEx != newEx)
									oldEx.addAgents(newEx.getAgentsNames());
								toAdd = false;
							}
						}
						if (toAdd)
							oldExcp.add(newEx);

					}
				}
				updateUI(oldExcp);
			}

		}

		public void updateUI(final List<GamaRuntimeException> newExceptions) {
			WorkbenchHelper.asyncRun(new Runnable() {

				@Override
				public void run() {
					exceptions = newExceptions;
					reset();
				}
			});
		}

		public void start() {
			running = true;
			schedule();

		}

	}

	@Override
	protected boolean areItemsClosable() {
		return true;
	}

	@Override
	public boolean addItem(final GamaRuntimeException e) {
		createItem(parent, e, false, null);

		return true;
	}

	@Override
	public void addNewError(final GamaRuntimeException ex) {
		if (!collector.running) {
			collector.start();
		}
		collector.offer(ex);
	}

	@Override
	public void ownCreatePartControl(final Composite view) {
	}

	private void gotoEditor(final GamaRuntimeException exception) {

		final EObject o = exception.getEditorContext();
		if (o != null) {
			WorkbenchHelper.asyncRun(new Runnable() {

				@Override
				public void run() {
					GAMA.getGui().editModel(o);
				}
			});
		}

	}

	@Override
	protected Composite createItemContentsFor(final GamaRuntimeException exception) {
		final ScrolledComposite compo = new ScrolledComposite(getViewer(), SWT.H_SCROLL);
		final GridLayout layout = new GridLayout(1, false);
		final GridData firstColData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layout.verticalSpacing = 5;
		compo.setLayout(layout);
		final Table t = new Table(compo, SWT.H_SCROLL);
		t.setFont(GamaFonts.getExpandfont());

		t.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				gotoEditor(exception);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		});
		t.setLayoutData(firstColData);
		final java.util.List<String> strings = exception.getContextAsList();
		t.setForeground(exception.isWarning() ? GamaColors.get(PreferencesHelper.WARNING_TEXT_COLOR.getValue()).color()
				: GamaColors.get(PreferencesHelper.ERROR_TEXT_COLOR.getValue()).color());
		final TableColumn c = new TableColumn(t, SWT.NONE);
		c.setResizable(true);
		final TableColumn column2 = new TableColumn(t, SWT.NONE);
		for (int i = 0; i < strings.size(); i++) {
			final TableItem item = new TableItem(t, SWT.NONE);
			item.setText(new String[] { String.valueOf(i), strings.get(i) });
		}
		c.pack();
		column2.pack();
		t.setSize(t.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		t.pack();
		compo.setContent(t);
		compo.pack();
		return compo;
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void removeItem(final GamaRuntimeException obj) {
		exceptions.remove(obj);
	}

	@Override
	public void pauseItem(final GamaRuntimeException obj) {
	}

	@Override
	public void resumeItem(final GamaRuntimeException obj) {
	}

	@Override
	public String getItemDisplayName(final GamaRuntimeException obj, final String previousName) {
		final StringBuilder sb = new StringBuilder(300);
		final String a = obj.getAgentSummary();
		if (a != null) {
			sb.append(a).append(" at ");
		}
		sb.append("cycle ").append(obj.getCycle()).append(ItemList.SEPARATION_CODE)
				.append(obj.isWarning() ? ItemList.WARNING_CODE : ItemList.ERROR_CODE).append(obj.getMessage());
		return sb.toString();
	}

	@Override
	public void focusItem(final GamaRuntimeException data) {
		// gotoEditor(data);
	}

	@Override
	public List<GamaRuntimeException> getItems() {
		final List<GamaRuntimeException> errors = new ArrayList();
		final int size = exceptions.size();
		if (size == 0) {
			return errors;
		}
		final int end = size;
		int begin = end - numberOfDisplayedErrors;
		begin = begin < 0 ? 0 : begin;
		final List<GamaRuntimeException> except = new ArrayList(exceptions);
		errors.addAll(except.subList(begin, end));
		if (mostRecentFirst) {
			Collections.reverse(errors);
		}
		return errors;
	}

	@Override
	public void updateItemValues() {
		this.getViewer().updateItemNames();
	}

	@Override
	public void reset() {
		super.reset();
		for (final GamaRuntimeException exception : new ArrayList<GamaRuntimeException>(exceptions)) {
			if (exception.isInvalid()) {
				exceptions.remove(exception);
			}
		}
		// exceptions.clear();
		displayItems();
	}

	/**
	 * Method handleMenu()
	 * 
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final GamaRuntimeException item, final int x, final int y) {
		final Map<String, Runnable> result = new HashMap();
		result.put("Copy text", new Runnable() {

			@Override
			public void run() {
				final Clipboard clipboard = new Clipboard(parent.getDisplay());
				final String data = item.getAllText();
				clipboard.setContents(new Object[] { data }, new Transfer[] { TextTransfer.getInstance() });
				clipboard.dispose();
			}
		});
		result.put("Show in editor", new Runnable() {

			@Override
			public void run() {
				gotoEditor(item);
			}
		});
		return result;
	}

	@Override
	protected boolean needsOutput() {
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();
		collector.stop();
		if (WorkbenchHelper.getWorkbench() != null && WorkbenchHelper.getWorkbench().isClosing())
			collector.cancel();

	}

}
