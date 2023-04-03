/*******************************************************************************************************
 *
 * PopulationInspectView.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.inspectors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.csv.CsvWriter;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.types.SpeciesConstantExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Files;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import msi.gaml.variables.IVariable;
import one.util.streamex.StreamEx;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.controls.SwitchButton;
import ummisco.gama.ui.menus.AgentsMenu;
import ummisco.gama.ui.menus.GamaMenu;
import ummisco.gama.ui.parameters.ExpressionControl;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.views.GamaViewPart;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

/**
 * Written by drogoul Modified on 18 mai 2011
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class PopulationInspectView extends GamaViewPart
		implements IToolbarDecoratedView.Sizable, IToolbarDecoratedView.Pausable {

	/** The export folder. */
	protected static String exportFolder = "exports";

	/** The Constant ID. */
	public static final String ID = IGui.TABLE_VIEW_ID;

	/** The Constant ID_ATTRIBUTE. */
	public static final String ID_ATTRIBUTE = "#";

	/** The Constant SAVE. */
	public final static int SAVE = 0;

	/** The Constant LOCK. */
	public final static int LOCK = 1;

	/** The Constant POP. */
	public final static int POP = 2;

	/** The Constant EXPR. */
	public final static int EXPR = 3;

	/** The Constant DONT_INSPECT_BY_DEFAULT. */
	public static final List<String> DONT_INSPECT_BY_DEFAULT =
			Arrays.asList(IKeyword.PEERS, IKeyword.MEMBERS, IKeyword.AGENTS, IKeyword.SHAPE, IKeyword.HOST);

	/** The scope. */
	private IScope scope;

	/** The locked. */
	volatile boolean locked;

	/** The population menu. */
	// volatile boolean refreshing;
	ToolItem populationMenu;

	/** The viewer. */
	TableViewer viewer;

	/** The attributes menu. */
	Composite attributesMenu;

	/** The comparator. */
	AgentComparator comparator;

	/** The editor. */
	ExpressionControl editor;
	// private String speciesName;

	/** The elements. */
	IAgent[] elements = {};

	/** The selected columns. */
	// Font currentFont = new Font(WorkbenchHelper.getDisplay(), GamaFonts.getSmallFont().getFontData());
	Map<String, List<String>> selectedColumns = new HashMap();

	/**
	 * The Class AgentContentProvider.
	 */
	class AgentContentProvider implements ILazyContentProvider {

		@Override
		public void dispose() {
			elements = new IAgent[0];
		}

		@Override
		public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
			final IAgent[] list = newInput == null ? new IAgent[0] : (IAgent[]) newInput;
			elements = list;
			if (elements.length > 1 && comparator != null) { Arrays.sort(elements, comparator); }
			viewer.setItemCount(elements.length);
		}

		@Override
		public void updateElement(final int index) {
			if (index > elements.length - 1) return;
			IAgent a = elements[index];
			if (a != null) {
				viewer.replace(a, index);
			} else {
				viewer.refresh();
			}
		}

	}

	/**
	 * Added to address Issue #2752
	 */
	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		this.setTitleImage(GamaIcon.named(IGamaIcons.VIEW_BROWSER).image());
	}

	/** The provider. */
	final private AgentContentProvider provider = new AgentContentProvider();

	@Override
	protected Job createUpdateJob() {
		return new GamaUIJob() {

			@Override
			protected UpdatePriority jobPriority() {
				return UpdatePriority.LOW;
			}

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				final TableViewer v = viewer;
				if (v == null || v.getTable() == null || v.getTable().isDisposed() || getOutput() == null)
					return Status.CANCEL_STATUS;
				if (!locked) {
					final IAgent[] agents = StreamEx.of(getOutput().getLastValue()).filter(a -> a != null && !a.dead())
							.toArray(IAgent.class);
					if (Arrays.equals(elements, agents)) {
						v.refresh();
					} else {
						viewer.setInput(agents);
					}
				} else {
					viewer.refresh();
				}
				return Status.OK_STATUS;
			}
		};
	}

	@Override
	public InspectDisplayOutput getOutput() { return (InspectDisplayOutput) super.getOutput(); }

	@Override
	public void addOutput(final IDisplayOutput output) {
		// Either both are null or they are equal
		if (getOutput() == output) return;
		// super.addOutput(output);
		// Only one output allowed for this view.
		if (getOutput() != null) {
			// We dont dispose the old one, as it would close the view...
			// Instead, we set it to paused, knowing
			// that the output manager has already removed it (otherwise, this
			// view would not have been
			// activated).
			getOutput().setPaused(true);
			// we release the scope
			GAMA.releaseScope(getScope());
			outputs.clear();
		}
		outputs.add(output);
		scope = output.getScope().copy("in population inspector");
		selectedColumns.clear();
		updateSpecies();
		comparator = new AgentComparator();

		recreateViewer();
	}

	/**
	 * Update species.
	 */
	void updateSpecies() {
		final ISpecies species = getOutput().getSpecies();
		final IExpression expr = getOutput().getValue();

		final String name = species == null ? IKeyword.AGENT : species.getName();
		final boolean isComplete = expr instanceof SpeciesConstantExpression;

		if (!selectedColumns.containsKey(name)) {
			selectedColumns.put(name, new ArrayList<>());
			final Map<String, String> attributes = getOutput().getAttributes();
			if (attributes != null) {
				selectedColumns.get(name).addAll(attributes.keySet());
			} else if (getOutput().getValue() != null) {
				if (species == null) return;
				selectedColumns.get(name).addAll(species.getVarNames());
				selectedColumns.get(name).removeAll(DONT_INSPECT_BY_DEFAULT);
			}
			Collections.sort(selectedColumns.get(name));
			selectedColumns.get(name).remove(ID_ATTRIBUTE);
			selectedColumns.get(name).add(0, ID_ATTRIBUTE);

		}

		changePartName(name, isComplete);

	}

	// @Override
	// protected void setContentDescription(final String description) {
	// if ( toolbar == null ) { return; }
	// toolbar.status((Image) null, description, IGamaColors.BLUE);
	// }

	/**
	 * Change part name.
	 *
	 * @param name
	 *            the name
	 * @param complete
	 *            the complete
	 */
	private void changePartName(final String name, final boolean complete) {
		if (name == null) return;
		// this.setContentDescription(StringUtils.capitalize(name) + "
		// population in macro-agent " +
		// getOutput().getRootAgent().getName());
		// WorkbenchHelper.runInUI("", 50, (m) -> {
		if (!complete) {
			setPartName(getOutput().getName() + ": set of " + name);
		} else {
			setPartName(getOutput().getName() + ": population of " + name);
		}
		// });

	}

	/**
	 * Creates the menus.
	 *
	 * @param parent
	 *            the parent
	 */
	private void createMenus(final Composite parent) {
		final ScrolledComposite scroll = new ScrolledComposite(parent, SWT.V_SCROLL);
		scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		scroll.setExpandHorizontal(true);
		scroll.setExpandVertical(true);
		attributesMenu = new Composite(scroll, SWT.NONE);
		scroll.setContent(attributesMenu);
		final GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 1;
		attributesMenu.setLayout(layout);
		// attributesMenu.setBackground(IGamaColors.WHITE.color());
		fillAttributeMenu();
	}

	/**
	 * Fill attribute menu.
	 */
	void fillAttributeMenu() {
		// Not yet declared or already disposed
		if (getOutput() == null || attributesMenu == null || attributesMenu.isDisposed()) return;
		for (final Control c : attributesMenu.getChildren()) { c.dispose(); }
		Label attributesLabel = new Label(attributesMenu, SWT.NONE);
		attributesLabel.setText("Attributes");
		// attributesLabel.setFont(GamaFonts.getNavigHeaderFont());
		attributesLabel = new Label(attributesMenu, SWT.None);
		attributesLabel.setText(" ");
		String tooltipText;
		final String speciesName = getSpeciesName();
		if (IKeyword.AGENT.equals(speciesName)) {
			tooltipText = "A list of the attributes common to the agents returned by the custom expression";
		} else {
			tooltipText = "A list of the attributes defined in species " + speciesName
					+ ". Select the ones you want to display in the table";
		}
		attributesMenu.setToolTipText(tooltipText);
		final boolean hasPreviousSelection = selectedColumns.get(speciesName) != null;
		final InspectDisplayOutput output = getOutput();
		final ISpecies species = output.getSpecies();
		final List<String> names = new ArrayList(
				getOutput().getAttributes() == null ? species.getVarNames() : getOutput().getAttributes().keySet());
		Collections.sort(names);
		DEBUG.OUT("" + names);
		for (final String name : names) {
			final SwitchButton b = new SwitchButton(attributesMenu, SWT.NONE, "   ", "   ", name);
			// b.setBackground(GamaColors.system(SWT.COLOR_WHITE));
			b.setSelection(hasPreviousSelection && selectedColumns.get(speciesName).contains(name));
			b.addSelectionListener(attributeAdapter);
		}
		final Point size = attributesMenu.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		attributesMenu.setSize(size);
		attributesMenu.layout(true, true);
		((ScrolledComposite) attributesMenu.getParent()).setMinSize(size);
	}

	/**
	 * Creates the expression composite.
	 */
	private void createExpressionComposite() {
		final Composite compo = new Composite(toolbar.getToolbar(SWT.RIGHT), SWT.None);
		compo.setSize(new Point(150, 30));
		// compo.setBackground(IGamaColors.WHITE.color());
		compo.setLayout(new GridLayout(1, false));
		editor = new ExpressionControl(getScope(), compo, null, getScope().getAgent(), Types.CONTAINER.of(Types.AGENT),
				SWT.BORDER, false) {

			@Override
			public void modifyValue() {
				final Object oldVal = getCurrentValue();
				super.modifyValue();
				if (oldVal == null ? getCurrentValue() != null : !oldVal.equals(getCurrentValue())) {
					if (outputs.isEmpty()) return;
					try {
						getOutput().setNewExpression((IExpression) getCurrentValue());

					} catch (final GamaRuntimeException e) {
						e.printStackTrace();
					}
					updateSpecies();
					fillAttributeMenu();
					// TODO Make a test on the columns.
					recreateViewer();
					update(getOutput());

				}
			}
		};
		final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.minimumHeight = 16;
		data.heightHint = 16;
		// if ( speciesName != null ) {
		editor.getControl().setText(getOutput().getExpressionText()); // TODO
																		// Output
																		// available
																		// ?
		// }
		editor.getControl().setLayoutData(data);
		editor.getControl().setToolTipText("Enter a GAML expression returning one or several agents ");
		toolbar.control(compo, 150, SWT.RIGHT);
		toolbar.refresh(true);
	}

	/**
	 * Gets the attributes selection.
	 *
	 * @return the attributes selection
	 */
	List<String> getAttributesSelection() {
		final ArrayList<String> result = new ArrayList<>();
		for (final Control c : attributesMenu.getChildren()) {
			if (c instanceof SwitchButton b && b.getSelection()) { result.add(b.getText()); }
		}
		return result;
	}

	/** The attribute adapter. */
	private final SelectionAdapter attributeAdapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			selectedColumns.put(getSpeciesName(), getAttributesSelection());
			recreateViewer();
			update(getOutput());
		}

	};

	/**
	 * Gets the species name.
	 *
	 * @return the species name
	 */
	String getSpeciesName() {
		if (getOutput() == null) return "";
		final ISpecies species = getOutput().getSpecies();
		if (species == null) return IKeyword.AGENT;
		return species.getName();
	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		final Composite view = new Composite(c, SWT.None);
		final GridLayout viewLayout = new GridLayout(1, false);
		viewLayout.marginWidth = 0;
		viewLayout.marginHeight = 0;
		viewLayout.verticalSpacing = 0;
		view.setLayout(viewLayout);
		final Composite intermediate = new Composite(view, SWT.NONE);
		intermediate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout intermediateLayout = new GridLayout(2, false);
		intermediateLayout.marginWidth = 0;
		intermediateLayout.marginHeight = 0;
		intermediateLayout.verticalSpacing = 0;
		intermediate.setLayout(intermediateLayout);
		createMenus(intermediate);
		createViewer(intermediate);
		intermediate.layout(true, true);
		setParentComposite(intermediate);
	}

	/**
	 * Creates the viewer.
	 *
	 * @param parent
	 *            the parent
	 */
	private void createViewer(final Composite parent) {
		viewer = new TableViewer(parent, SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		createColumns();
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		// table.setFont(currentFont);
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(provider);
		viewer.addDoubleClickListener(event -> {
			final IStructuredSelection s = (IStructuredSelection) viewer.getSelection();
			final Object o = s.getFirstElement();
			if (o instanceof IAgent) {
				getScope().getGui().setHighlightedAgent((IAgent) o);
				GAMA.getExperiment().refreshAllOutputs();

			}
		});
		viewer.setComparer(new IElementComparer() {

			@Override
			public int hashCode(final Object element) {
				return Objects.hashCode(element);
			}

			@Override
			public boolean equals(final Object a, final Object b) {
				return Objects.equals(a, b);
			}
		});

		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(false);
		menuMgr.addMenuListener(manager -> {
			IAgent agent = null;
			final IStructuredSelection s = (IStructuredSelection) viewer.getSelection();
			final Object o = s.getFirstElement();
			if (o instanceof IAgent) { agent = (IAgent) o; }
			if (agent != null) {
				manager.removeAll();
				manager.update(true);
				AgentsMenu.createMenuForAgent(viewer.getControl().getMenu(), agent, false, true);
			}
		});
		final Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		// Layout the viewer
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 1;
		viewer.getControl().setLayoutData(gridData);
		// comparator = new AgentComparator();
		// viewer.setComparator(comparator);
	}

	/**
	 * Recreate viewer.
	 */
	void recreateViewer() {
		if (viewer == null) return;
		final Table table = viewer.getTable();
		if (table.isDisposed()) return;
		table.dispose();
		createViewer(getParentComposite());
		getParentComposite().layout(true);
	}

	/**
	 * Creates the columns.
	 */
	private void createColumns() {
		final List<String> selection = new ArrayList(getAttributesSelection());
		selection.remove(ID_ATTRIBUTE);
		selection.add(0, ID_ATTRIBUTE);
		for (final String title : selection) { createTableViewerColumn(title, 100, 0); }
	}

	/**
	 * Gets the column label provider.
	 *
	 * @param title
	 *            the title
	 * @return the column label provider
	 */
	private ColumnLabelProvider getColumnLabelProvider(final String title) {
		return new ColumnLabelProvider() {

			@Override
			public String getText(final Object element) {
				final IAgent agent = (IAgent) element;
				if (agent.dead() && !ID_ATTRIBUTE.equals(title)) return "N/A";
				if (ID_ATTRIBUTE.equals(title)) return String.valueOf(agent.getIndex());
				// final Object value;
				if (agent.getSpecies().hasVar(title)) return Cast.toGaml(getScope().getAgentVarValue(agent, title));
				return Cast.toGaml(agent.getAttribute(title));
			}
		};
	}

	/**
	 * Gets the selection adapter.
	 *
	 * @param column
	 *            the column
	 * @param name
	 *            the name
	 * @return the selection adapter
	 */
	private SelectionAdapter getSelectionAdapter(final TableColumn column, final String name) {
		return new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				comparator.setColumn(name);
				final int dir = comparator.getDirection();
				viewer.getTable().setSortDirection(dir);
				viewer.getTable().setSortColumn(column);
				if (getScope().isPaused() || getOutput().isPaused()) { Arrays.sort(elements, comparator); }
				viewer.refresh();
			}
		};
	}

	/**
	 * Creates the table viewer column.
	 *
	 * @param title
	 *            the title
	 * @param bound
	 *            the bound
	 * @param colNumber
	 *            the col number
	 * @return the table viewer column
	 */
	private TableViewerColumn createTableViewerColumn(final String title, final int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getSelectionAdapter(column, title));
		viewerColumn.setLabelProvider(getColumnLabelProvider(title));
		return viewerColumn;
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * The Class AgentComparator.
	 */
	public class AgentComparator extends ViewerComparator implements Comparator {

		/** The attribute. */
		private String attribute = null;

		/** The direction. */
		private int direction = SWT.UP;

		/** The string comparator. */
		private final NaturalOrderComparator stringComparator = new NaturalOrderComparator();

		/**
		 * Gets the direction.
		 *
		 * @return the direction
		 */
		public int getDirection() { return direction; }

		/**
		 * Sets the column.
		 *
		 * @param column
		 *            the new column
		 */
		public void setColumn(final String column) {
			if (column.equals(attribute)) {
				// Same column as last sort; toggle the direction
				direction = direction == SWT.UP ? SWT.DOWN : SWT.UP;
			} else {
				// New column; do an ascending sort
				attribute = column;
				direction = SWT.UP;
			}
		}

		@Override
		public int compare(final Viewer v, final Object e1, final Object e2) {
			return compare(e1, e2);
		}

		@Override
		public int compare(final Object e1, final Object e2) {
			if (e1 == null) return -1;
			if (e2 == null) return 1;
			final IAgent p1 = (IAgent) e1;
			final IAgent p2 = (IAgent) e2;
			final IScope myScope = getScope();
			int rc = 0;
			if (attribute == null || ID_ATTRIBUTE.equals(attribute)) {
				rc = p1.compareTo(p2);
			} else {
				try {
					final Object v1 = myScope.getAgentVarValue(p1, attribute);
					if (v1 == null) {
						rc = -1;
					} else {
						final Object v2 = myScope.getAgentVarValue(p2, attribute);
						if (v2 == null) {
							rc = 1;
						} else {
							final IVariable v = getOutput().getSpecies().getVar(attribute);
							final int id = v.getType().id();
							rc = switch (id) {
								case IType.INT -> ((Integer) v1).compareTo((Integer) v2);
								case IType.FLOAT -> ((Double) v1).compareTo((Double) v2);
								case IType.STRING -> stringComparator.compare(v1, v2);
								case IType.POINT -> ((GamaPoint) v1).compareTo((GamaPoint) v2);
								default -> Cast.toGaml(v1).compareTo(Cast.toGaml(v2));
							};
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			// If descending order, flip the direction
			if (direction == SWT.DOWN) { rc = -rc; }
			return rc;
		}

		/**
		 * @return
		 */

	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	IScope getScope() { return scope; }

	/**
	 * The Class NaturalOrderComparator.
	 */
	public static class NaturalOrderComparator implements Comparator {

		/**
		 * Compare right.
		 *
		 * @param a
		 *            the a
		 * @param b
		 *            the b
		 * @return the int
		 */
		int compareRight(final String a, final String b) {
			int bias = 0;
			int ia = 0;
			int ib = 0;
			for (;; ia++, ib++) {
				final char ca = charAt(a, ia);
				final char cb = charAt(b, ib);

				if (!Character.isDigit(ca) && !Character.isDigit(cb)) return bias;
				if (!Character.isDigit(ca)) return -1;
				if (!Character.isDigit(cb)) return +1;
				if (ca < cb) {
					if (bias == 0) { bias = -1; }
				} else if (ca > cb) {
					if (bias == 0) { bias = +1; }
				} else if (ca == 0 && cb == 0) return bias;
			}
		}

		@Override
		public int compare(final Object o1, final Object o2) {
			final String a = o1.toString();
			final String b = o2.toString();

			int ia = 0, ib = 0;
			int nza = 0, nzb = 0;
			char ca, cb;
			int result;

			while (true) {
				// only count the number of zeroes leading the last number
				// compared
				nza = nzb = 0;

				ca = charAt(a, ia);
				cb = charAt(b, ib);

				// skip over leading spaces or zeros
				while (Character.isSpaceChar(ca) || ca == '0') {
					if (ca == '0') {
						nza++;
					} else {
						// only count consecutive zeroes
						nza = 0;
					}

					ca = charAt(a, ++ia);
				}

				while (Character.isSpaceChar(cb) || cb == '0') {
					if (cb == '0') {
						nzb++;
					} else {
						// only count consecutive zeroes
						nzb = 0;
					}

					cb = charAt(b, ++ib);
				}

				// process run of digits
				if (Character.isDigit(ca) && Character.isDigit(cb)
						&& (result = compareRight(a.substring(ia), b.substring(ib))) != 0)
					return result;

				if (ca == 0 && cb == 0) // The strings compare the same. Perhaps the caller
					// will want to call strcmp to break the tie.
					return nza - nzb;

				if (ca < cb) return -1;
				if (ca > cb) return +1;

				++ia;
				++ib;
			}

		}

		/**
		 * Char at.
		 *
		 * @param s
		 *            the s
		 * @param i
		 *            the i
		 * @return the char
		 */
		char charAt(final String s, final int i) {
			if (i >= s.length()) return 0;
			return s.charAt(i);
		}
	}

	/**
	 *
	 */
	public void saveAsCSV() {
		try {
			Files.newFolder(getScope(), exportFolder);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + exportFolder);
			GAMA.reportError(getScope(), e1, false);
			e1.printStackTrace();
			return;
		}

		final String exportFileName = FileUtils.constructAbsoluteFilePath(getScope(),
				exportFolder + "/" + getSpeciesName() + "_population" + getScope().getClock().getCycle() + ".csv",
				false);
		final Table table = viewer.getTable();
		final TableColumn[] columns = table.getColumns();
		try (final CsvWriter writer = new CsvWriter(exportFileName);) {
			// AD 2/1/16 Replaces the comma by ';' to properly output points and
			// lists
			writer.setDelimiter(';');
			writer.setUseTextQualifier(false);

			final List<String[]> contents = new ArrayList<>();
			final String[] headers = new String[columns.length];
			int columnIndex = 0;
			for (final TableColumn column : columns) { headers[columnIndex++] = column.getText(); }
			contents.add(headers);
			final TableItem[] items = table.getItems();
			for (final TableItem item : items) {
				final String[] row = new String[columns.length];
				for (int i = 0; i < columns.length; i++) { row[i] = item.getText(i); }
				contents.add(row);
			}
			try {
				for (final String[] ss : contents) { writer.writeRecord(ss); }

			} catch (final IOException e) {
				throw GamaRuntimeException.create(e, getScope());

			}
		}
	}

	@Override
	public Control getSizableFontControl() {
		if (viewer == null) return null;
		return viewer.getTable();
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		if (getOutput() == null) return;
		super.createToolItems(tb);
		tb.check(IGamaIcons.LOCK_POPULATION, "", "Lock the current population (prevents editing it)", e -> {
			locked = !locked;
			editor.getControl().setEnabled(!locked);
			populationMenu.setEnabled(!locked);

			// TODO let the list of agents remain the same ??
		}, SWT.RIGHT);
		createExpressionComposite();
		populationMenu = tb.menu(IGamaIcons.BROWSE_POPULATIONS, "", "Browse a species", trigger -> {
			if (locked) return;
			final GamaMenu menu = new GamaMenu() {

				@Override
				protected void fillMenu() {
					final IPopulation[] pops = getOutput().getRootAgent().getMicroPopulations();
					for (final IPopulation p : pops) {
						action(p.getName(), new SelectionAdapter() {

							@Override
							public void widgetSelected(final SelectionEvent e) {
								editor.getControl().setText(p.getName());
								editor.widgetDefaultSelected(null);
							}

						}, GamaIcon.named(IGamaIcons.MENU_POPULATION).image());
					}
				}
			};
			menu.open(toolbar.getToolbar(SWT.RIGHT), trigger);
		}, SWT.RIGHT);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button(IGamaIcons.SAVE_AS, "Save as CSV", "Save the agents and their attributes into a CSV file",
				e -> saveAsCSV(), SWT.RIGHT);
	}

	@Override
	public void widgetDisposed(final DisposeEvent e) {
		super.dispose();
		if (viewer != null && viewer.getTable() != null && !viewer.getTable().isDisposed()) {
			viewer.getTable().dispose();
		}
		if (attributesMenu != null && !attributesMenu.isDisposed()) { attributesMenu.dispose(); }
		// if (currentFont != null && !currentFont.isDisposed()) { currentFont.dispose(); }
		provider.dispose();
		GAMA.releaseScope(scope);
		scope = null;
	}

	/**
	 * Method pauseChanged()
	 *
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView.Pausable#pauseChanged()
	 */
	@Override
	public void pauseChanged() {}



}
