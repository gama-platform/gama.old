/*********************************************************************************************
 *
 * 'ErrorView.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.ItemList;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.TestAgent;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;
import msi.gaml.statements.test.AssertStatement;
import msi.gaml.statements.test.TestStatement;
import ummisco.gama.ui.parameters.AbstractEditor;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.inspectors.ExpandableItemsView;

public class TestView extends ExpandableItemsView<TestStatement> {

	public final static int EDITORS_SPACING = 0;
	public final List<AssertEditor> editors = new ArrayList<>();

	class ItemComposite extends Composite {

		public ItemComposite(final Composite parent, final int style) {
			super(parent, style);
			final GridLayout layout = new GridLayout(2, false);
			layout.verticalSpacing = EDITORS_SPACING;
			setLayout(layout);
		}

		public void addEditor(final AbstractEditor<?> editor) {
			editor.createComposite(this);
		}

	}

	public static String ID = IGui.TEST_VIEW_ID;

	@Override
	protected boolean areItemsClosable() {
		return false;
	}

	@Override
	public boolean addItem(final TestStatement e) {
		createItem(parent, e, false, GamaColors.get(getItemDisplayColor(e)));
		return true;
	}

	@Override
	public void ownCreatePartControl(final Composite view) {}

	@Override
	protected Composite createItemContentsFor(final TestStatement test) {
		final Collection<AssertStatement> assertions = test.getAssertions();
		final ItemComposite compo = new ItemComposite(getViewer(), SWT.NONE);
		compo.setBackground(getViewer().getBackground());
		for (final AssertStatement assertion : assertions) {
			final AssertEditor ed = new AssertEditor(GAMA.getRuntimeScope(), assertion);
			editors.add(ed);
			compo.addEditor(ed);
		}
		return compo;
	}

	@Override
	public void setFocus() {}

	@Override
	public void removeItem(final TestStatement obj) {}

	@Override
	public void pauseItem(final TestStatement obj) {}

	@Override
	public void resumeItem(final TestStatement obj) {}

	@Override
	public String getItemDisplayName(final TestStatement obj, final String previousName) {
		final StringBuilder sb = new StringBuilder(300);
		final String a = obj.getName();
		sb.append(obj.getName()).append(" in ").append(obj.getDescription().getEnclosingDescription().getTitle())
				.append(" ").append(ItemList.SEPARATION_CODE).append(obj.getState());
		return sb.toString();
	}

	@Override
	public GamaColor getItemDisplayColor(final TestStatement t) {
		return getItemDisplayColor(t.getState());
	}

	public static GamaColor getItemDisplayColor(final TestStatement.State s) {
		switch (s) {
			case FAILED:
				return GamaColor.getNamed("gamared");
			case NOT_RUN:
				return GamaColors.toGamaColor(IGamaColors.NEUTRAL.color());
			case WARNING:
				return GamaColor.getNamed("gamaorange");
			case PASSED:
				return GamaColor.getNamed("gamagreen");
			default:
				return null;
		}
	}

	@Override
	public void focusItem(final TestStatement data) {}

	@Override
	public List<TestStatement> getItems() {
		final IExperimentPlan expe = GAMA.getExperiment();
		if (expe == null)
			return Collections.EMPTY_LIST;
		final ExperimentAgent agent = expe.getAgent();
		if (!(agent instanceof TestAgent))
			return Collections.EMPTY_LIST;
		return ((TestAgent) agent).getAllTests();
	}

	@Override
	public void updateItemValues() {
		this.getViewer().updateItemNames();
		this.getViewer().updateItemColors();
	}

	@Override
	public void reset() {
		WorkbenchHelper.run(() -> {
			TestView.super.reset();
			displayItems();
			parent.layout(true, true);
		});

	}

	/**
	 * Method handleMenu()
	 * 
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final TestStatement item, final int x, final int y) {
		final Map<String, Runnable> result = new HashMap<>();
		result.put("Copy summary to clipboard", () -> {
			final Clipboard clipboard = new Clipboard(parent.getDisplay());
			final String data = item.getSummary();
			clipboard.setContents(new Object[] { data }, new Transfer[] { TextTransfer.getInstance() });
			clipboard.dispose();
		});
		result.put("Show in editor",
				() -> GAMA.getGui().editModel(null, item.getDescription().getUnderlyingElement(null)));
		return result;
	}

	@Override
	protected boolean needsOutput() {
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}
