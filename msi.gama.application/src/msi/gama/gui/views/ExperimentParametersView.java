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

import java.util.Collection;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.*;
import msi.gama.runtime.*;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.statements.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class ExperimentParametersView extends AttributesEditorsView<String> {

	public static final String ID = GuiUtils.PARAMETER_VIEW_ID;

	private IExperimentSpecies experiment;
	private Composite commands;

	@Override
	public void ownCreatePartControl(final Composite view) {
		super.ownCreatePartControl(view);
		final Composite intermediate = new Composite(view, SWT.NONE);
		final GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.marginWidth = 0;
		parentLayout.marginHeight = 0;
		parentLayout.verticalSpacing = 0;
		intermediate.setLayout(parentLayout);
		commands = new Composite(intermediate, SWT.BORDER_SOLID);
		commands.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		commands.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		commands.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 0;
		commands.setLayout(layout);
		commands.pack();
		view.pack();
		view.layout();
		parent = intermediate;
	}

	public void addItem(final IExperimentSpecies exp) {
		if ( exp != null && exp != experiment ) {
			experiment = exp;
			reset();
			editors = (EditorsList<String>) exp.getParametersEditors();
			if ( editors == null ) { return; }
			displayItems();
		} else {
			experiment = null;
		}
	}

	@Override
	public void displayItems() {
		super.displayItems();
		for ( final Control c : commands.getChildren() ) {
			c.dispose();
		}
		final Collection<UserCommandStatement> userCommands = experiment.getUserCommands();
		for ( final IStatement command : userCommands ) {
			final Button b = new Button(commands, SWT.PUSH);
			b.setText(command.getName());
			b.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					GAMA.getSimulation().getScheduler().executeOneAction(new GamaHelper() {

						@Override
						public Object run(final IScope scope) {
							final Object result = command.executeOn(scope);
							experiment.getSimulationOutputs().forceUpdateOutputs();
							return result;
						}

					});
				}

			});
		}
		commands.pack();
		commands.update();
	}

	/**
	 * @see msi.gama.gui.views.GamaVie@Override
	 *      wPart#getToolbarActionsId()
	 */
	@Override
	protected Integer[] getToolbarActionsId() {
		return new Integer[] { /* SAVE, */REVERT };
	}

	@Override
	public boolean addItem(final String object) {
		createItem(object, true);
		return true;
	}

	public IExperimentSpecies getExperiment() {
		return experiment;
	}

}
