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
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaList;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Written by drogoul Modified on 18 mai 2011
 * 
 * @todo Description
 * 
 */
public class TableAgentInspectView extends GamaViewPart {

	public static final String ID = GuiUtils.TABLE_VIEW_ID;

	// List<IAgent> currentAgents = new GamaList();
	String speciesName = "";
	final List<String> columnNames = new GamaList();
	boolean locked;
	TableViewer viewer;
	org.eclipse.swt.widgets.List speciesMenu, attributesMenu;

	@Override
	public void update(final IDisplayOutput output) {
		if ( speciesName != null ) {
			viewer.setInput(GAMA.getSimulation().getPopulationFor(speciesName));
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
		}
		Collections.sort(columnNames);
		if ( columnNames.remove(IKeyword.NAME) ) {
			columnNames.add(0, IKeyword.NAME);
		}

	}

	private void createMenus(final Composite parent) {
		final Composite menuComposite = new Composite(parent, SWT.BORDER_SOLID);
		menuComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
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
		speciesMenu.setItems(strings);
		speciesMenu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final String[] strings = speciesMenu.getSelection();
				if ( strings.length > 0 && !speciesName.equals(strings[0]) ) {
					setSpeciesName(strings[0]);
					fillAttributeMenu();
					recreateViewer();
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
		// getSite().setSelectionProvider(viewer);
		// Set the sorter for the table

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

	private TableViewerColumn createTableViewerColumn(final String title, final int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	public TableViewer getViewer() {
		return viewer;
	}

	@Override
	protected Integer[] getToolbarActionsId() {
		// TODO Need to be usable (not the case now)
		return new Integer[] { PAUSE, REFRESH };
	}

}
