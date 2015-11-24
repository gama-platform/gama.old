/*********************************************************************************************
 *
 *
 * 'PopulationInspectView.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.views;

import java.io.IOException;
import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.*;
import msi.gama.gui.parameters.ExpressionControl;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.commands.*;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.CsvWriter;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.*;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;
import msi.gaml.variables.IVariable;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Written by drogoul Modified on 18 mai 2011
 *
 * @todo Description
 *
 */
public class PopulationInspectView extends GamaViewPart implements IToolbarDecoratedView.Sizable, IToolbarDecoratedView.Pausable {

	protected static String exportFolder = "exports";
	public static final String ID = GuiUtils.TABLE_VIEW_ID;
	public static final String ID_ATTRIBUTE = "#";
	public static final String CUSTOM = "custom";

	public final static int SAVE = 0;
	public final static int LOCK = 1;
	public final static int POP = 2;
	public final static int EXPR = 3;
	public static final List<String> DONT_INSPECT_BY_DEFAULT = Arrays.asList(IKeyword.PEERS, IKeyword.MEMBERS,
		IKeyword.AGENTS, IKeyword.SHAPE, IKeyword.HOST);
	IScope scope;
	volatile boolean locked;
	// volatile boolean refreshing;
	ToolItem populationMenu;
	TableViewer viewer;
	org.eclipse.swt.widgets.List attributesMenu;
	private AgentComparator comparator;
	private ExpressionControl editor;
	private String speciesName;

	IAgent[] elements = new IAgent[0];
	Font currentFont = new Font(SwtGui.getDisplay(), SwtGui.getSmallFont().getFontData());
	Map<String, List<String>> selectedColumns = new HashMap();

	class AgentContentProvider implements ILazyContentProvider {

		@Override
		public void dispose() {
			elements = new IAgent[0];
		}

		@Override
		public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
			IAgent[] list = newInput == null ? new IAgent[0] : (IAgent[]) newInput;
			elements = list;
			if ( elements.length > 1 && comparator != null ) {
				Arrays.sort(elements, comparator);
			}
			viewer.setItemCount(elements.length);
		}

		@Override
		public void updateElement(final int index) {
			if ( index > elements.length - 1 ) { return; }
			viewer.replace(elements[index], index);
		}

	}

	final private AgentContentProvider provider = new AgentContentProvider();

	@Override
	protected GamaUIJob createUpdateJob() {
		return new GamaUIJob() {

			@Override
			protected UpdatePriority jobPriority() {
				return UpdatePriority.LOW;
			}

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				TableViewer v = viewer;
				if ( !locked ) {
					IAgent[] agents = getOutput().getLastValue();
					if ( Arrays.equals(elements, agents) ) {
						if ( v != null && v.getTable() != null && !v.getTable().isDisposed() ) {
							v.refresh();
						}
					} else {
						if ( v != null && v.getTable() != null && !v.getTable().isDisposed() ) {
							viewer.setInput(agents);
						}
					}
				} else {
					if ( v != null && v.getTable() != null && !v.getTable().isDisposed() ) {
						viewer.refresh();
					}
				}

				return Status.OK_STATUS;
			}
		};
	}

	@Override
	public InspectDisplayOutput getOutput() {
		return (InspectDisplayOutput) super.getOutput();
	}

	@Override
	public void addOutput(final IDisplayOutput output) {
		super.addOutput(output);
		scope = null;
		selectedColumns.clear();
		final IExpression expr = getOutput().getValue();
		if ( expr != null ) {
			final String name = expr.getType().getContentType().getSpeciesName();
			if ( expr.literalValue().equals(name) ) {
				setSpeciesName(name, true);
			} else {
				setSpeciesName(CUSTOM, true);
			}
		}
		comparator = new AgentComparator();

		recreateViewer();
	}

	private void setSpeciesName(final String name, final boolean fromMenu) {
		speciesName = name;
		if ( fromMenu && editor != null ) {
			editor.getControl().setText(name);
		}
		if ( !selectedColumns.containsKey(name) ) {
			selectedColumns.put(name, new ArrayList());
			final List<String> names = getOutput().getAttributes();
			if ( names != null ) {
				selectedColumns.get(name).addAll(names);
			} else if ( getOutput().getValue() != null ) {
				final IExpression expr = getOutput().getValue();
				final SpeciesDescription realSpecies = expr.getType().getContentType().getSpecies();
				// final ISpecies species = GAMA.getModel().getSpecies(realSpecies);
				if ( realSpecies == null ) { return; }
				selectedColumns.get(name).addAll(realSpecies.getVarNames());
				selectedColumns.get(name).removeAll(DONT_INSPECT_BY_DEFAULT);
			}
			Collections.sort(selectedColumns.get(name));
			selectedColumns.get(name).remove(ID_ATTRIBUTE);
			selectedColumns.get(name).add(0, ID_ATTRIBUTE);

		}
		changePartName(name);

	}

	// @Override
	// protected void setContentDescription(final String description) {
	// if ( toolbar == null ) { return; }
	// toolbar.status((Image) null, description, IGamaColors.BLUE);
	// }

	private void changePartName(final String name) {
		if ( name == null ) { return; }
		// this.setContentDescription(StringUtils.capitalize(name) + " population in macro-agent " +
		// getOutput().getRootAgent().getName());
		if ( name.equals(CUSTOM) ) {
			setPartName("Custom population");
		} else {
			setPartName("Population of " + name);
		}
	}

	private void createMenus(final Composite parent) {
		final Composite menuComposite = new Composite(parent, SWT.NONE);
		menuComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
		final GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 1;
		menuComposite.setLayout(layout);
		Label attributesLabel = new Label(menuComposite, SWT.NONE);
		attributesLabel.setText("Attributes");
		attributesLabel.setFont(SwtGui.getLabelfont());
		attributesMenu = new org.eclipse.swt.widgets.List(menuComposite, SWT.V_SCROLL | SWT.MULTI);
		attributesMenu.setBackground(parent.getBackground());
		attributesMenu.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fillAttributeMenu();
		menuComposite.pack(true);
	}

	private void createExpressionComposite() {
		Composite compo = new Composite(toolbar.getToolbar(SWT.RIGHT), SWT.None);
		compo.setSize(new Point(150, 30));
		compo.setBackground(IGamaColors.WHITE.color());
		compo.setLayout(new GridLayout(1, false));
		editor =
			new ExpressionControl(compo, null, getScope().getAgentScope(), Types.CONTAINER.of(Types.AGENT), SWT.BORDER,
				false) {

			@Override
			public void modifyValue() {
				Object oldVal = getCurrentValue();
				super.modifyValue();
				if ( oldVal == null ? getCurrentValue() == null : oldVal.equals(getCurrentValue()) ) {
					if ( outputs.isEmpty() ) { return; }
					try {
						getOutput().setNewExpression((IExpression) getCurrentValue());
					} catch (final GamaRuntimeException e) {
						e.printStackTrace();
					}
					final ISpecies species = getOutput().getSpecies();
					setSpeciesName(species == null ? null : species.getName(), false);
					fillAttributeMenu();
					// TODO Make a test on the columns.
					recreateViewer();
					update(getOutput());

				}
			}
		};
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.minimumHeight = 16;
		data.heightHint = 16;
		if ( speciesName != null ) {
			editor.getControl().setText(speciesName);
		}
		editor.getControl().setLayoutData(data);
		editor.getControl().setToolTipText("Enter a GAML expression returning one or several agents ");
		toolbar.control(compo, 150, SWT.RIGHT);
		toolbar.refresh(true);
	}

	private final SelectionAdapter attributeAdapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			selectedColumns.put(speciesName, Arrays.asList(attributesMenu.getSelection()));
			recreateViewer();
			update(getOutput());
		}

	};

	private void fillAttributeMenu() {
		// Not yet declared or already disposed
		if ( attributesMenu == null || attributesMenu.isDisposed() ) { return; }
		attributesMenu.removeAll();
		String tooltipText;
		if ( CUSTOM.equals(speciesName) ) {
			tooltipText = "A list of the attributes common to the agents returned by the custom expression";
		} else {
			tooltipText =
				"A list of the attributes defined in species " + speciesName +
				". Select the ones you want to display in the table";
		}
		attributesMenu.setToolTipText(tooltipText);
		final IExpression expr = getOutput().getValue();
		if ( expr != null ) {
			final SpeciesDescription realSpecies = expr.getType().getContentType().getSpecies();
			// final ISpecies species = GAMA.getModel().getSpecies(realSpecies);
			if ( realSpecies != null ) {
				final List<String> names = new ArrayList(realSpecies.getVarNames());
				Collections.sort(names);
				attributesMenu.setItems(names.toArray(new String[0]));
				for ( int i = 0; i < names.size(); i++ ) {
					if ( selectedColumns.get(speciesName) != null &&
						selectedColumns.get(speciesName).contains(names.get(i)) ) {
						attributesMenu.select(i);
					}
				}
				attributesMenu.addSelectionListener(attributeAdapter);
			}
		}
	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		final Composite view = new Composite(c, SWT.None);
		speciesName = getOutput().getExpressionText();
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
		intermediate.layout(true);
		parent = intermediate;
	}

	private void createViewer(final Composite parent) {
		viewer = new TableViewer(parent, SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		createColumns();
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setFont(currentFont);
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(provider);
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(final DoubleClickEvent event) {
				final IStructuredSelection s = (IStructuredSelection) viewer.getSelection();
				final Object o = s.getFirstElement();
				if ( o instanceof IAgent ) {
					GuiUtils.setHighlightedAgent((IAgent) o);
					GAMA.getExperiment().getSimulationOutputs().forceUpdateOutputs();
				}
			}
		});

		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(false);
		menuMgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(final IMenuManager manager) {
				IAgent agent = null;
				final IStructuredSelection s = (IStructuredSelection) viewer.getSelection();
				final Object o = s.getFirstElement();
				if ( o instanceof IAgent ) {
					agent = (IAgent) o;
				}
				if ( agent != null ) {
					manager.removeAll();
					manager.update(true);
					AgentsMenu.createMenuForAgent(viewer.getControl().getMenu(), agent, null, false);
				}
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		// Layout the viewer
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 1;
		viewer.getControl().setLayoutData(gridData);
		// comparator = new AgentComparator();
		// viewer.setComparator(comparator);
	}

	private void recreateViewer() {
		if ( viewer == null ) { return; }
		final Table table = viewer.getTable();
		if ( table.isDisposed() ) { return; }
		table.dispose();
		createViewer(parent);
		parent.layout(true);
	}

	private void createColumns() {
		final List<String> selection = new ArrayList(Arrays.asList(attributesMenu.getSelection()));
		selection.remove(ID_ATTRIBUTE);
		selection.add(0, ID_ATTRIBUTE);
		for ( final String title : selection ) {
			createTableViewerColumn(title, 100, 0);
		}
	}

	private ColumnLabelProvider getColumnLabelProvider(final String title) {
		return new ColumnLabelProvider() {

			@Override
			public String getText(final Object element) {
				final IAgent agent = (IAgent) element;
				if ( agent.dead() && !title.equals(ID_ATTRIBUTE) ) { return "N/A"; }
				if ( title.equals(ID_ATTRIBUTE) ) { return String.valueOf(agent.getIndex()); }
				return Cast.toGaml(getScope().getAgentVarValue(agent, title));
			}
		};
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final String name) {
		final SelectionAdapter columnSortAdapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				comparator.setColumn(name);
				final int dir = comparator.getDirection();
				viewer.getTable().setSortDirection(dir);
				viewer.getTable().setSortColumn(column);
				if ( GAMA.isPaused() || getOutput().isPaused() ) {
					Arrays.sort(elements, comparator);
				}
				viewer.refresh();
			}
		};
		return columnSortAdapter;
	}

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

	public class AgentComparator extends ViewerComparator implements Comparator {

		private String attribute = null;
		private int direction = SWT.UP;
		private final NaturalOrderComparator stringComparator = new NaturalOrderComparator();

		public int getDirection() {
			return direction;
		}

		public void setColumn(final String column) {
			if ( column.equals(attribute) ) {
				// Same column as last sort; toggle the direction
				direction = direction == SWT.UP ? SWT.DOWN : SWT.UP;
			} else {
				// New column; do an ascending sort
				attribute = column;
				direction = SWT.UP;
			}
		}

		@Override
		public int compare(final Viewer viewer, final Object e1, final Object e2) {
			return compare(e1, e2);
		}

		@Override
		public int compare(final Object e1, final Object e2) {
			final IAgent p1 = (IAgent) e1;
			final IAgent p2 = (IAgent) e2;
			final IScope scope = getScope();
			int rc = 0;
			if ( attribute == null || attribute.equals(ID_ATTRIBUTE) ) {
				rc = p1.compareTo(p2);
			} else {
				try {
					final Object v1 = scope.getAgentVarValue(p1, attribute);
					if ( v1 == null ) {
						rc = -1;
					} else {
						final Object v2 = scope.getAgentVarValue(p2, attribute);
						if ( v2 == null ) {
							rc = 1;
						} else {
							final IVariable v = getOutput().getSpecies().getVar(attribute);
							final int id = v.getType().id();
							switch (id) {
								case IType.INT:
									rc = ((Integer) v1).compareTo((Integer) v2);
									break;
								case IType.FLOAT:
									rc = ((Double) v1).compareTo((Double) v2);
									break;
								case IType.STRING:
									rc = stringComparator.compare(v1, v2);
									break;
								case IType.POINT:
									rc = ((ILocation) v1).compareTo(v2);
									break;
								default:
									rc = Cast.toGaml(v1).compareTo(Cast.toGaml(v2));
							}
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			// If descending order, flip the direction
			if ( direction == SWT.DOWN ) {
				rc = -rc;
			}
			return rc;
		}

		/**
		 * @return
		 */

	}

	private IScope getScope() {
		if ( scope == null ) {
			scope = getOutput().getScope().copy();
		}
		return scope;
	}

	public static class NaturalOrderComparator implements Comparator {

		int compareRight(final String a, final String b) {
			int bias = 0;
			int ia = 0;
			int ib = 0;
			for ( ;; ia++, ib++ ) {
				final char ca = charAt(a, ia);
				final char cb = charAt(b, ib);

				if ( !Character.isDigit(ca) && !Character.isDigit(cb) ) {
					return bias;
				} else if ( !Character.isDigit(ca) ) {
					return -1;
				} else if ( !Character.isDigit(cb) ) {
					return +1;
				} else if ( ca < cb ) {
					if ( bias == 0 ) {
						bias = -1;
					}
				} else if ( ca > cb ) {
					if ( bias == 0 ) {
						bias = +1;
					}
				} else if ( ca == 0 && cb == 0 ) { return bias; }
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
				// only count the number of zeroes leading the last number compared
				nza = nzb = 0;

				ca = charAt(a, ia);
				cb = charAt(b, ib);

				// skip over leading spaces or zeros
				while (Character.isSpaceChar(ca) || ca == '0') {
					if ( ca == '0' ) {
						nza++;
					} else {
						// only count consecutive zeroes
						nza = 0;
					}

					ca = charAt(a, ++ia);
				}

				while (Character.isSpaceChar(cb) || cb == '0') {
					if ( cb == '0' ) {
						nzb++;
					} else {
						// only count consecutive zeroes
						nzb = 0;
					}

					cb = charAt(b, ++ib);
				}

				// process run of digits
				if ( Character.isDigit(ca) && Character.isDigit(cb) ) {
					if ( (result = compareRight(a.substring(ia), b.substring(ib))) != 0 ) { return result; }
				}

				if ( ca == 0 && cb == 0 ) {
					// The strings compare the same. Perhaps the caller
					// will want to call strcmp to break the tie.
					return nza - nzb;
				}

				if ( ca < cb ) {
					return -1;
				} else if ( ca > cb ) { return +1; }

				++ia;
				++ib;
			}

		}

		char charAt(final String s, final int i) {
			if ( i >= s.length() ) { return 0; }
			return s.charAt(i);
		}
	}

	/**
	 *
	 */
	public void saveAsCSV() {
		try {
			Files.newFolder(getScope(), exportFolder);
		} catch (GamaRuntimeException e1) {
			e1.addContext("Impossible to create folder " + exportFolder);
			GAMA.reportError(getScope(), e1, false);
			e1.printStackTrace();
			return;
		}

		String exportFileName =
			FileUtils.constructAbsoluteFilePath(getScope(), exportFolder + "/" + speciesName + "_population" +
				getScope().getClock().getCycle() + ".csv", false);
		// File file = new File(exportFileName);
		// FileWriter fileWriter = null;
		// try {
		// file.createNewFile();
		// fileWriter = new FileWriter(file, false);
		// } catch (final IOException e) {
		// throw GamaRuntimeException.create(e);
		// }
		Table table = viewer.getTable();
		TableColumn[] columns = table.getColumns();
		CsvWriter writer = new CsvWriter(exportFileName);

		List<String[]> contents = new ArrayList();
		String[] headers = new String[columns.length];
		int columnIndex = 0;
		for ( TableColumn column : columns ) {
			headers[columnIndex++] = column.getText();
		}
		contents.add(headers);
		TableItem[] items = table.getItems();
		for ( TableItem item : items ) {
			String[] row = new String[columns.length];
			for ( int i = 0; i < columns.length; i++ ) {
				row[i] = item.getText(i);
			}
			contents.add(row);
		}
		try {
			for ( String[] ss : contents ) {
				writer.writeRecord(ss);
			}

			writer.close();
		} catch (IOException e) {
			throw GamaRuntimeException.create(e);
		}
	}

	@Override
	public Control getSizableFontControl() {
		if ( viewer == null ) { return null; }
		return viewer.getTable();
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		tb.check("population.lock2", "", "Lock the current population (prevents editing it)", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				locked = !locked;
				editor.getControl().setEnabled(!locked);
				populationMenu.setEnabled(!locked);

				// TODO let the list of agents remain the same ??
			}

		}, SWT.RIGHT);
		createExpressionComposite();
		populationMenu = tb.menu("population.list2", "", "Browse a species", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent trigger) {
				if ( locked ) { return; }
				GamaMenu menu = new GamaMenu() {

					@Override
					protected void fillMenu() {
						IPopulation[] pops = getOutput().getRootAgent().getMicroPopulations();
						for ( final IPopulation p : pops ) {
							action(p.getName(), new SelectionAdapter() {

								@Override
								public void widgetSelected(final SelectionEvent e) {
									setSpeciesName(p.getName(), true);
								}

							}, GamaIcons.create("display.agents2").image());
						}
					}
				};
				menu.open(toolbar, trigger);
			}

		}, SWT.RIGHT);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button("menu.saveas2", "Save as CSV", "Save the attributes of agents into a CSV file",
			new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				saveAsCSV();
			}

		}, SWT.RIGHT);
	}

	@Override
	public void dispose() {
		super.dispose();
		if ( viewer != null && viewer.getTable() != null && !viewer.getTable().isDisposed() ) {
			viewer.getTable().dispose();
		}
		if ( currentFont != null && !currentFont.isDisposed() ) {
			currentFont.dispose();
		}
	}

	@Override
	public void close() {
		attributesMenu.removeAll();
		provider.dispose();
		super.close();
	}

}
