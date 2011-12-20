/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.views;

import java.awt.Color;
import javax.swing.JComponent;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.*;
import msi.gama.gui.parameters.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.swing.EmbeddedSwingComposite;
import msi.gama.outputs.LayerDisplayOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class LayeredDisplayView extends ExpandableItemsView<IDisplay> {

	public static final String ID = GuiUtils.LAYER_VIEW_ID;

	private Composite swingCompo;

	@Override
	public void setFocus() {
		swingCompo.setFocus();
	}

	protected IDisplayManager getDisplayManager() {
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
		label.setFont(SwtGui.labelFont);
		label.setLayoutData(SwtGui.labelData);
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
	public boolean addItem(final IDisplay d) {
		createItem(d, false);
		return true;
	}

	@Override
	protected Composite createItemContentsFor(final IDisplay d) {
		Composite compo = new Composite(getViewer(), SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 5;
		compo.setLayout(layout);
		try {
			if ( d instanceof AbstractDisplay ) {
				((AbstractDisplay) d).fillComposite(compo,
					((LayerDisplayOutput) getOutput()).getSurface());
			}
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
	public String getItemDisplayName(final IDisplay obj, final String previousName) {
		return getDisplayManager().getItemDisplayName(obj, previousName);
	}

	@Override
	public java.util.List<IDisplay> getItems() {
		return getDisplayManager().getItems();
	}

	@Override
	public void updateItemValues() {}
}
