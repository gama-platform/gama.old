/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.views;

import java.awt.Color;
import javax.swing.JComponent;
import msi.gama.gui.application.GUI;
import msi.gama.gui.graphics.*;
import msi.gama.gui.graphics.DisplayManager.DisplayItem;
import msi.gama.gui.parameters.*;
import msi.gama.gui.util.swing.EmbeddedSwingComposite;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.outputs.LayerDisplayOutput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class LayeredDisplayView extends ExpandableItemsView<DisplayItem> {

	public static final String ID = "msi.gama.gui.application.view.LayeredDisplayView";

	private Composite swingCompo;

	@Override
	public void setFocus() {
		swingCompo.setFocus();
	}

	protected DisplayManager getDisplayManager() {
		return ((LayerDisplayOutput) getOutput()).getSurface().getManager();
	}

	@Override
	public void ownCreatePartControl(final Composite c) {
		super.ownCreatePartControl(c);
		parent = new SashForm(c, SWT.HORIZONTAL | SWT.SMOOTH);
		createViewer();
		Composite general = new Composite(getViewer(), SWT.None);
		GridLayout layout = new GridLayout(2, false);

		general.setLayout(layout);
		final Button label = new Button(general, SWT.CHECK);
		label.setFont(GUI.labelFont);
		label.setLayoutData(GUI.labelData);
		label.setText("");
		label.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				((AWTDisplaySurface) ((LayerDisplayOutput) getOutput()).getSurface())
					.setNavigationImageEnabled(label.getSelection());
			}

		});
		label.setSelection(true);
		EmbeddedSwingComposite nav =
			new EmbeddedSwingComposite(general, SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND,
				getOutput()) {

				@Override
				protected JComponent createSwingComponent() {
					return ((AWTDisplaySurface) getOutput().getSurface()).getNavigator();
				}
			};
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumHeight = 200;
		nav.setLayoutData(data);
		EditorFactory.create(general, "Background:", output.getBackgroundColor(),
			new EditorListener<Color>() {

				@Override
				public void valueModified(final Color newValue) {
					output.setBackgroundColor(newValue);
				}
			});
		createItem("General", null, general, true);
		nav.populate();
		displayItems();
		swingCompo =
			new EmbeddedSwingComposite(parent, SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND,
				getOutput()) {

				@Override
				protected JComponent createSwingComponent() {
					return (AWTDisplaySurface) getOutput().getSurface();
				}
			};
		((EmbeddedSwingComposite) swingCompo).populate();
		((SashForm) parent).setWeights(new int[] { 1, 2 });
		((SashForm) parent).setMaximizedControl(swingCompo);
	}

	@Override
	public boolean addItem(final DisplayItem d) {
		createItem(d, false);
		return true;
	}

	@Override
	protected Composite createItemContentsFor(final DisplayItem d) {
		Composite compo = new Composite(getViewer(), SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 5;
		compo.setLayout(layout);
		try {
			d.display.fillComposite(compo, d, ((LayerDisplayOutput) getOutput()).getSurface());
		} catch (GamaRuntimeException e) {
			GAMA.reportError(e);
			return null;
		}
		return compo;
	}

	public IDisplaySurface getDisplaySurface() {
		return ((LayerDisplayOutput) getOutput()).getSurface();
	}

	public void toggleControls() {
		((SashForm) parent).setMaximizedControl(((SashForm) parent).getMaximizedControl() == null
			? swingCompo : null);
	}

	@Override
	public String getItemDisplayName(final DisplayItem obj, final String previousName) {
		return getDisplayManager().getItemDisplayName(obj, previousName);
	}

	@Override
	public java.util.List<DisplayItem> getItems() {
		return getDisplayManager().getItems();
	}

	@Override
	public void updateItemValues() {}
}
