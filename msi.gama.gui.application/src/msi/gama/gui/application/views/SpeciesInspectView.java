/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.views;

import java.util.*;
import java.util.List;
import msi.gama.factories.ModelFactory;
import msi.gama.gui.application.GUI;
import msi.gama.gui.application.commands.AgentsMenu;
import msi.gama.gui.parameters.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.experiment.ParameterAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * The SpeciesView.
 */
// TODO Adapt to Multi-scale model
public class SpeciesInspectView extends ExpandableItemsView<IPopulation> {

	public static final String			ID		=
													"msi.gama.gui.application.view.SpeciesInspectView";
	public final List<AbstractEditor>	editors	= new ArrayList();

	@Override
	public void setOutput(final IDisplayOutput out) {
		super.setOutput(out);
		if ( parent != null ) {
			reset();
			displayItems();
		}
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
		Composite compo = new Composite(getViewer(), SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		compo.setLayout(layout);
		String cat = getItemDisplayName(species, null);
		boolean isBuiltIn = ModelFactory.isBuiltIn(species.getName());
		boolean hasParent = species.getSpecies().getParentName() != null;
		boolean hasAgents = species.size() != 0;
		boolean hasAspects = !species.getAspectNames().isEmpty();
		boolean hasBehaviors = species.getSpecies().getBehaviors().size() != 0;

		if ( !hasParent && !hasAgents && !hasBehaviors ) { return compo; }

		if ( hasParent ) {
			editors.add(EditorFactory.create(compo, new ParameterAdapter("Parent: ", cat,
				IType.STRING) {

				@Override
				public String value() {
					return species.getSpecies().getParentName();
				}

			}));
		}
		if ( !isBuiltIn || hasAgents ) {
			final AbstractEditor agentsEditor =
				EditorFactory.create(compo, new ParameterAdapter("Population: ", cat, " ",
					IType.STRING) {

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
			Label label = agentsEditor.getUnitLabel();
			Composite p = label.getParent();
			label.dispose();
			final Button button = new Button(p, SWT.FLAT | SWT.PUSH);
			button.setImage(GUI.speciesImage);
			button.setText("Inspect");
			button.setToolTipText("Click to select an agent from the drop-down menu");
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					Menu old = button.getMenu();
					button.setMenu(null);
					if ( old != null ) {
						old.dispose();
					}
					agentsEditor.updateValue();
					getViewer().updateItemNames();
					Menu dropMenu = AgentsMenu.createSpeciesSubMenu(button, species); // TODO adapt
																						// to
																						// multi-scale
																						// model
					button.setMenu(dropMenu);
					dropMenu.setVisible(true);
				}

			});

		}
		if ( hasBehaviors ) {
			editors.add(EditorFactory.create(compo, new ParameterAdapter("Behaviors: ", cat,
				IType.INT) {

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

		editors.add(EditorFactory.create(compo, new ParameterAdapter("Attributes: ", cat,
			IType.STRING) {

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
			editors.add(EditorFactory.create(compo, new ParameterAdapter("Aspects: ", cat,
				IType.INT) {

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
		final ISimulation sim = GAMA.getFrontmostSimulation();
		List<IPopulation> finalSpeciesList;
		final List<IPopulation> allSpeciesList = sim.getWorld().getMicroPopulations(); // TODO adapt
																						// to
																						// multi-scale
																						// model
		Collections.sort(allSpeciesList);
		final IPopulation worldSpecies = sim.getWorldPopulation();
		final List<IPopulation> builtInSpeciesList = new ArrayList();
		for ( IPopulation m : allSpeciesList ) {
			if ( ModelFactory.isBuiltIn(m.getName()) ) {
				builtInSpeciesList.add(m);
			}
		}
		allSpeciesList.removeAll(builtInSpeciesList);
		finalSpeciesList = new ArrayList(builtInSpeciesList);
		finalSpeciesList.addAll(allSpeciesList);
		finalSpeciesList.remove(worldSpecies);
		finalSpeciesList.add(0, worldSpecies);
		return finalSpeciesList;
	}

	@Override
	public String getItemDisplayName(final IPopulation obj, final String previousName) {
		boolean isBuiltIn = ModelFactory.isBuiltIn(obj.getName());
		int size = obj.size();
		return "Species" + ItemList.SEPARATION_CODE +
			(isBuiltIn ? ItemList.ERROR_CODE : ItemList.INFO_CODE) + obj.getName() +
			(isBuiltIn ? " (built-in)" : "") + " - " + size + (size < 2 ? " agent" : " agents");
	}

	@Override
	public void updateItemValues() {
		for ( AbstractEditor ed : editors ) {
			ed.updateValue();
		}

	}

}
