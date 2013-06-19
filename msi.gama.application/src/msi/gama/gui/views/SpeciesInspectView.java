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
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.parameters.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.ParameterAdapter;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.outputs.*;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * The SpeciesView.
 */
// TODO Adapt to Multi-scale model
public class SpeciesInspectView extends ExpandableItemsView<IPopulation> {

	public static final String ID = GuiUtils.SPECIES_VIEW_ID;
	public final List<AbstractEditor> editors = new ArrayList();

	@Override
	public void setOutput(final IDisplayOutput out) {
		super.setOutput(out);
		if ( parent != null ) {
			reset();
			displayItems();
		}
	}

	/**
	 * @see msi.gama.gui.views.GamaViewPart#getToolbarActionsId()
	 */
	@Override
	protected Integer[] getToolbarActionsId() {
		// TODO Need to check if the output can support it
		return new Integer[] { PAUSE, REFRESH };
	}

	@Override
	public void reset() {
		super.reset();
		editors.clear();
	}

	@Override
	public boolean addItem(final IPopulation species) {
		createItem(species, false);
		return true;
	}

	@Override
	protected Composite createItemContentsFor(final IPopulation species) {
		final Composite compo = new Composite(getViewer(), SWT.NONE);
		final GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		compo.setLayout(layout);
		final String cat = getItemDisplayName(species, null);
		// boolean isBuiltIn = Types.isBuiltIn(species.getName());
		final boolean hasParent = species.getSpecies().getParentName() != null;
		// boolean hasAgents = species.size() != 0;
		final boolean hasAspects = !species.getAspectNames().isEmpty();
		final boolean hasBehaviors = species.getSpecies().getBehaviors().size() != 0;

		if ( !hasParent /* && !hasAgents */&& !hasBehaviors ) { return compo; }

		if ( hasParent ) {
			editors.add(EditorFactory.create(compo, new ParameterAdapter("Parent: ", cat, IType.STRING) {

				@Override
				public String value() {
					return species.getSpecies().getParentName();
				}

			}));
		}
		// if ( !isBuiltIn /* || hasAgents */) {}

		final AbstractEditor agentsEditor =
			EditorFactory.create(compo, new ParameterAdapter("Population: ", cat, " ", IType.STRING) {

				@Override
				public String value() {
					return "" + species.size() + " living agents";
				}

				@Override
				public boolean allowsTooltip() {
					return false;
				}

			});
		editors.add(agentsEditor);
		final Label label = agentsEditor.getUnitLabel();
		final Composite p = label.getParent();
		label.dispose();
		final Button button = new Button(p, SWT.FLAT | SWT.PUSH);
		button.setImage(SwtGui.gridImage);
		button.setText("Inspect");
		button.setToolTipText("Open an inspector for the population of this species");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Menu old = button.getMenu();
				button.setMenu(null);
				if ( old != null ) {
					old.dispose();
				}
				try {
					new InspectDisplayOutput(species.getHost(), species.getSpecies()).launch();
				} catch (final GamaRuntimeException ex) {
					GAMA.reportError(ex, false);
				}
				// agentsEditor.updateValue();
				// getViewer().updateItemNames();
				// final Menu dropMenu = AgentsMenu.createSpeciesSubMenu(button, species, null);
				// // TODO adapt to multi-scale model
				// button.setMenu(dropMenu);
				// dropMenu.setVisible(true);
			}

		});

		if ( hasBehaviors ) {
			editors.add(EditorFactory.create(compo, new ParameterAdapter("Behaviors: ", cat, IType.INT) {

				@Override
				public Object value() {
					return null;
				}

				@Override
				public boolean isEditable() {
					return true;
				}

				@Override
				public boolean isLabel() {
					return true;
				}

				@Override
				public boolean allowsTooltip() {
					return false;
				}

				@Override
				public List getAmongValue() {
					return species.getSpecies().getBehaviors();
				}

			}));
		}

		editors.add(EditorFactory.create(compo, new ParameterAdapter("Attributes: ", cat, IType.STRING) {

			@Override
			public Object value() {
				return null;
			}

			@Override
			public boolean isEditable() {
				return true;
			}

			@Override
			public boolean isLabel() {
				return true;
			}

			@Override
			public boolean allowsTooltip() {
				return false;
			}

			@Override
			public List getAmongValue() {
				return species.getSpecies().getVarNames();
			}

		}));

		if ( hasAspects ) {
			editors.add(EditorFactory.create(compo, new ParameterAdapter("Aspects: ", cat, IType.INT) {

				@Override
				public Object value() {
					return null;
				}

				@Override
				public boolean isEditable() {
					return true;
				}

				@Override
				public boolean isLabel() {
					return true;
				}

				@Override
				public boolean allowsTooltip() {
					return false;
				}

				@Override
				public List getAmongValue() {
					return species.getAspectNames();
				}

			}));
		}

		return compo;
	}

	@Override
	public List<IPopulation> getItems() {
		return GAMA.getModelPopulations();
	}

	@Override
	public String getItemDisplayName(final IPopulation obj, final String previousName) {
		// boolean isBuiltIn = ;
		final int size = obj.size();
		return "Species" + ItemList.SEPARATION_CODE + ItemList.INFO_CODE + obj.getName() + "" + " - " + size +
			(size < 2 ? " agent" : " agents");
	}

	@Override
	public void updateItemValues() {
		for ( final IParameterEditor ed : editors ) {
			ed.updateValue();
		}

	}

}
