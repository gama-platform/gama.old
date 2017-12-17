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

import static msi.gama.common.preferences.GamaPreferences.Runtime.FAILED_TESTS;
import static msi.gama.common.preferences.GamaPreferences.Runtime.TESTS_SORTED;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import com.google.common.primitives.Ints;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.ItemList;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;
import msi.gaml.statements.test.AbstractSummary;
import msi.gaml.statements.test.CompoundSummary;
import msi.gaml.statements.test.TestExperimentSummary;
import msi.gaml.statements.test.TestState;
import ummisco.gama.ui.controls.ParameterExpandItem;
import ummisco.gama.ui.parameters.AbstractEditor;
import ummisco.gama.ui.parameters.AssertEditor;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;

public class TestView extends ExpandableItemsView<AbstractSummary<?>> implements IGamaView.Test {

	static final Comparator<AbstractSummary<?>> BY_ORDER = (o1, o2) -> Ints.compare(o1.getIndex(), o2.getIndex());
	static final Comparator<AbstractSummary<?>> BY_SEVERITY = (o1, o2) -> {
		final TestState s1 = o1.getState();
		final TestState s2 = o2.getState();
		if (s1 == s2)
			return BY_ORDER.compare(o1, o2);
		else
			return s1.compareTo(s2);
	};
	public final List<AbstractSummary<?>> experiments = new ArrayList<>();
	private boolean runningAllTests;
	public static final GridLayout layout = new GridLayout(2, false);
	static {
		layout.verticalSpacing = 0;
	}

	public static String ID = IGui.TEST_VIEW_ID;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		// if (!SwtGui.ALL_TESTS_RUNNING) {
		experiments.clear();
		super.reset();
		// }
	}

	@Override
	protected boolean areItemsClosable() {
		return false;
	}

	protected void resortTests() {
		final Comparator<AbstractSummary<?>> comp = TESTS_SORTED.getValue() ? BY_SEVERITY : BY_ORDER;
		experiments.sort(comp);
	}

	@Override
	public void startNewTestSequence(final boolean all) {
		runningAllTests = all;
		experiments.clear();
		WorkbenchHelper.run(() -> {
			if (toolbar != null)
				toolbar.status(null, "Run experiment to see the tests results", e -> {
					GAMA.startFrontmostExperiment();
				}, IGamaColors.BLUE, SWT.LEFT);
		});
		super.reset();
	}

	@Override
	public void finishTestSequence() {
		super.reset();
		reset();
	}

	@Override
	public void addTestResult(final CompoundSummary<?, ?> summary) {
		if (summary instanceof TestExperimentSummary) {
			if (!experiments.contains(summary)) {
				experiments.add(summary);
			}
		} else
			for (final AbstractSummary<?> s : summary.getSummaries().values()) {
				if (!experiments.contains(s)) {
					experiments.add(s);
				}
			}
	}

	@Override
	public boolean addItem(final AbstractSummary<?> experiment) {
		final boolean onlyFailed = GamaPreferences.Runtime.FAILED_TESTS.getValue();
		ParameterExpandItem item = getViewer() == null ? null : getViewer().getItem(experiment);
		if (item != null)
			item.dispose();
		if (onlyFailed) {
			final TestState state = experiment.getState();
			if (state != TestState.FAILED && state != TestState.ABORTED)
				return false;
		}
		item = createItem(getParentComposite(), experiment, !runningAllTests,
				GamaColors.get(getItemDisplayColor(experiment)));
		return true;
	}

	@Override
	public void ownCreatePartControl(final Composite view) {
		view.setBackground(IGamaColors.WHITE.color());
	}

	// Experimental: creates a deferred item
	@Override
	protected ParameterExpandItem createItem(final Composite parent, final AbstractSummary<?> data,
			final boolean expanded, final GamaUIColor color) {
		createViewer(parent);
		if (getViewer() == null) { return null; }
		final Composite control = createItemContentsFor(data);
		ParameterExpandItem item;
		if (expanded) {
			createEditors(control, data);
			item = createItem(parent, data, control, expanded, color);
		} else {
			item = createItem(parent, data, control, expanded, color);
			item.onExpand(() -> createEditors(control, data));
		}
		return item;
	}

	@Override
	protected Composite createItemContentsFor(final AbstractSummary<?> experiment) {
		final Composite compo = new Composite(getViewer(), SWT.NONE);
		compo.setLayout(layout);
		compo.setBackground(getViewer().getBackground());
		return compo;
	}

	public void createEditors(final Composite compo, final AbstractSummary<?> test) {
		Map<String, ? extends AbstractSummary<?>> assertions = test.getSummaries();
		for (final Map.Entry<String, ? extends AbstractSummary<?>> assertion : assertions.entrySet()) {
			final AbstractSummary<?> summary = assertion.getValue();
			final String name = assertion.getKey();
			createEditor(compo, test, summary, name);
			if (summary instanceof CompoundSummary) {
				assertions = summary.getSummaries();
				for (final Map.Entry<String, ? extends AbstractSummary<?>> aa : assertions.entrySet()) {
					createEditor(compo, test, aa.getValue(), aa.getKey());
				}
			}
		}
	}

	public void createEditor(final Composite compo, final AbstractSummary<?> globalTest,
			final AbstractSummary<?> subTest, final String name) {
		if (GamaPreferences.Runtime.FAILED_TESTS.getValue()) {
			final TestState state = subTest.getState();
			if (state != TestState.FAILED && state != TestState.ABORTED)
				return;
		}
		final AssertEditor ed = new AssertEditor(GAMA.getRuntimeScope(), subTest);
		// editorsByExperiment.get(globalTest).put(name, ed);
		((AbstractEditor<?>) ed).createComposite(compo);
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		TESTS_SORTED.removeChangeListeners();
		FAILED_TESTS.removeChangeListeners();
		final ToolItem t = tb.check(GamaIcons.create("test.sort2").getCode(), "Sort by severity",
				"When checked, sort the tests by their decreasing state severity (i.e. errored > failed > warning > passed > not run). Otherwise they are sorted by their order of execution.",
				e -> {
					TESTS_SORTED.set(!TESTS_SORTED.getValue());
					TestView.super.reset();
					reset();
				}, SWT.RIGHT);
		t.setSelection(TESTS_SORTED.getValue());
		TESTS_SORTED.onChange(v -> t.setSelection(v));

		final ToolItem t2 = tb.check(GamaIcons.create("test.filter2").getCode(), "Filter tests",
				"When checked, show only errored and failed tests and assertions", e -> {
					FAILED_TESTS.set(!FAILED_TESTS.getValue());
					TestView.super.reset();
					reset();
				}, SWT.RIGHT);
		t2.setSelection(FAILED_TESTS.getValue());
		FAILED_TESTS.onChange(v -> t2.setSelection(v));

	}

	@Override
	public void setFocus() {}

	@Override
	public void removeItem(final AbstractSummary<?> obj) {}

	@Override
	public void pauseItem(final AbstractSummary<?> obj) {}

	@Override
	public void resumeItem(final AbstractSummary<?> obj) {}

	@Override
	public String getItemDisplayName(final AbstractSummary<?> obj, final String previousName) {
		final StringBuilder sb = new StringBuilder(300);
		final String name = obj.getTitle();
		sb.append(obj.getState()).append(ItemList.SEPARATION_CODE).append(name).append(" ");
		return sb.toString();
	}

	@Override
	protected boolean shouldBeClosedWhenNoExperiments() {
		return !runningAllTests;
	}

	@Override
	public GamaColor getItemDisplayColor(final AbstractSummary<?> t) {
		return t.getColor();
	}

	@Override
	public void focusItem(final AbstractSummary<?> data) {}

	@Override
	public List<AbstractSummary<?>> getItems() {
		return experiments;
	}

	@Override
	public void updateItemValues() {}

	@Override
	public void reset() {
		WorkbenchHelper.run(() -> {
			if (!getParentComposite().isDisposed()) {
				resortTests();
				displayItems();
				getParentComposite().layout(true, false);
				if (toolbar != null)
					toolbar.status(null, new CompoundSummary(experiments).getStringSummary(), null, IGamaColors.BLUE,
							SWT.LEFT);
			}
		});

	}

	/**
	 * Method handleMenu()
	 * 
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final AbstractSummary<?> item, final int x, final int y) {
		final Map<String, Runnable> result = new HashMap<>();
		result.put("Copy summary to clipboard", () -> {
			WorkbenchHelper.copy(item.toString());
		});
		result.put("Show in editor", () -> GAMA.getGui().editModel(null, item.getURI()));
		return result;
	}

	@Override
	protected boolean needsOutput() {
		return false;
	}

}
