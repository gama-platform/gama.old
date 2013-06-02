/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.views;

import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.outputs.*;
import msi.gama.runtime.*;
import msi.gama.util.GamaList;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Written by drogoul Modified on 18 mai 2011
 * 
 * @todo Description
 * 
 */
public class PopulationInspectView extends GamaViewPart {

	public static final String ID = GuiUtils.TABLE_VIEW_ID;
	public static final List<String> DONT_INSPECT = Arrays.asList(IKeyword.PEERS, IKeyword.MEMBERS, IKeyword.AGENTS);

	String speciesName = "";
	final List<String> columnNames = new GamaList();
	boolean locked;
	TableViewer viewer;
	org.eclipse.swt.widgets.List speciesMenu, attributesMenu;
	private AgentComparator comparator;

	@Override
	public void update(final IDisplayOutput output) {
		if ( speciesName != null ) {
			final List list = new ArrayList(GAMA.getSimulation().getPopulationFor(speciesName));
			// Collections.sort(list, comparator);
			viewer.setInput(list);
		} else {
			viewer.setInput(null);
		}
		viewer.refresh();
	}

	@Override
	public InspectDisplayOutput getOutput() {
		return (InspectDisplayOutput) super.getOutput();
	}

	@Override
	public void setOutput(final IDisplayOutput output) {
		super.setOutput(output);
		if ( getOutput().getValue() != null ) {
			setSpeciesName(getOutput().getValue().getContentType().getSpeciesName());
		}
	}

	private void setSpeciesName(final String name) {
		speciesName = name;
		final List<String> names = getOutput().getAttributes();
		columnNames.clear();
		if ( names != null ) {
			columnNames.addAll(names);
		} else if ( getOutput().getValue() != null ) {
			final ISpecies species = GAMA.getModel().getSpecies(speciesName);
			if ( species == null ) { return; }
			columnNames.addAll(species.getVarNames());
			columnNames.removeAll(DONT_INSPECT);
		}
		Collections.sort(columnNames);
		if ( columnNames.remove(IKeyword.NAME) ) {
			columnNames.add(0, IKeyword.NAME);
		}

	}

	private void createMenus(final Composite parent) {
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		final Composite menuComposite = new Composite(sc, SWT.NONE);
		sc.setContent(menuComposite);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		final GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 5;
		menuComposite.setLayout(layout);
		Label title = new Label(menuComposite, SWT.NONE);
		title.setText("Species:");
		title.setFont(SwtGui.getLabelfont());
		speciesMenu = new org.eclipse.swt.widgets.List(menuComposite, SWT.BORDER);
		speciesMenu.setToolTipText("The list of species used in the simulation. Select the one you want to inspect");
		final List<IPopulation> populations = GAMA.getModelPopulations();
		final List<String> names = new ArrayList();
		for ( final IPopulation pop : populations ) {
			names.add(pop.getName());
		}
		final String[] strings = names.toArray(new String[0]);
		speciesMenu.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		speciesMenu.setItems(strings);
		speciesMenu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final String[] strings = speciesMenu.getSelection();
				if ( strings.length > 0 && !speciesName.equals(strings[0]) ) {
					setSpeciesName(strings[0]);
					fillAttributeMenu();
					recreateViewer();
					update(getOutput());
				}

			}

		});
		speciesMenu.select(names.indexOf(speciesName));
		title = new Label(menuComposite, SWT.NONE);
		title.setText(""); // Spacer
		title = new Label(menuComposite, SWT.NONE);
		title.setText("Attributes:");
		title.setFont(SwtGui.getLabelfont());
		attributesMenu = new org.eclipse.swt.widgets.List(menuComposite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		fillAttributeMenu();
	}

	private void fillAttributeMenu() {
		attributesMenu.removeAll();
		attributesMenu.setToolTipText("A list of all the attributes defined in species " + speciesName +
			". Select the ones you want to display in the table");
		final ISpecies species = GAMA.getModel().getSpecies(speciesName);
		if ( species == null ) { return; }
		final List<String> names = species.getVarNames();
		Collections.sort(names);
		attributesMenu.setItems(names.toArray(new String[0]));
		for ( int i = 0; i < names.size(); i++ ) {
			if ( columnNames.contains(names.get(i)) ) {
				attributesMenu.select(i);
			}
		}
		attributesMenu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				recreateViewer();
			}

		});
		attributesMenu.pack(true);
		attributesMenu.getParent().setSize(attributesMenu.getParent().computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	@Override
	public void ownCreatePartControl(final Composite view) {
		final Composite intermediate = new Composite(view, SWT.NONE);
		final GridLayout parentLayout = new GridLayout(2, false);
		parentLayout.marginWidth = 0;
		parentLayout.marginHeight = 0;
		parentLayout.verticalSpacing = 0;

		intermediate.setLayout(parentLayout);
		createMenus(intermediate);
		createViewer(intermediate);
		comparator = new AgentComparator();
		viewer.setComparator(comparator);
		view.pack();
		view.layout();
		parent = intermediate;
	}

	private void createViewer(final Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns();
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setFont(SwtGui.getSmallFont());
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(final DoubleClickEvent event) {
				final IStructuredSelection s = (IStructuredSelection) viewer.getSelection();
				final Object o = s.getFirstElement();
				if ( o instanceof IAgent ) {
					GuiUtils.setHighlightedAgent((IAgent) o);
				}
			}
		});

		// Layout the viewer
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 1;
		viewer.getControl().setLayoutData(gridData);
	}

	private void recreateViewer() {
		final Table table = viewer.getTable();
		table.dispose();
		createViewer(parent);
		parent.layout(true);
	}

	private void createColumns() {
		final List<String> selection = new GamaList(attributesMenu.getSelection());
		selection.remove(IKeyword.NAME);
		selection.add(0, IKeyword.NAME);
		for ( final String title : selection ) {
			final TableViewerColumn col = createTableViewerColumn(title, 100, 0);
			col.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(final Object element) {
					final IAgent agent = (IAgent) element;
					if ( agent.dead() && !title.equals(IKeyword.NAME) ) { return "N/A"; }
					return Cast.toGaml(GAMA.getSimulation().getScope().getAgentVarValue(agent, title));
				}
			});
		}

	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final String name) {
		final SelectionAdapter selectionAdapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				comparator.setColumn(name);
				final int dir = comparator.getDirection();
				viewer.getTable().setSortDirection(dir);
				viewer.getTable().setSortColumn(column);
				viewer.refresh();
			}
		};
		return selectionAdapter;
	}

	private TableViewerColumn createTableViewerColumn(final String title, final int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getSelectionAdapter(column, title));
		return viewerColumn;
	}

	public TableViewer getViewer() {
		return viewer;
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	protected Integer[] getToolbarActionsId() {
		// TODO Need to be usable (not the case now)
		return new Integer[] { PAUSE, REFRESH };
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
			int rc = 0;
			if ( attribute == null ) {
				rc = p1.compareTo(p2);
			} else {
				final IScope scope = GAMA.obtainNewScope();
				try {
					final Object v1 = scope.getAgentVarValue(p1, attribute);
					if ( v1 == null ) {
						rc = -1;
					} else {
						final Object v2 = scope.getAgentVarValue(p2, attribute);
						if ( v2 == null ) {
							rc = 1;
						} else {
							final IVariable v = GAMA.getModel().getSpecies(speciesName).getVar(attribute);
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
								default:
									rc = Cast.asFloat(scope, v1).compareTo(Cast.asFloat(scope, v2));
							}
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				} finally {
					GAMA.releaseScope(scope);
				}
			}

			// If descending order, flip the direction
			if ( direction == SWT.DOWN ) {
				rc = -rc;
			}
			return rc;
		}

	}

	public class NaturalOrderComparator implements Comparator {

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

}
