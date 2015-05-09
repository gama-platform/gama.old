/*********************************************************************************************
 * 
 * 
 * 'LayersOverlay.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt.controls;

import msi.gama.common.interfaces.*;
import msi.gama.gui.displays.layers.LayerSideControls;
import msi.gama.gui.swt.IGamaColors;
import msi.gama.gui.views.LayeredDisplayView;
import msi.gama.outputs.layers.AbstractLayer;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * The class Popup.
 * 
 * @author drogoul
 * @since 19 aug. 2013
 * 
 */
public class LayersOverlay2 extends PopupDialog {

	LayeredDisplayView view;
	Composite content;

	public LayersOverlay2(final LayeredDisplayView parent, final int shellStyle) {
		super(parent.getSite().getShell(), shellStyle, true, true, true, true, true, null, null);
		view = parent;
	}

	@Override
	protected Point getDefaultLocation(final Point initialSize) {
		return view.getSurfaceComposite().toDisplay(new Point(0, 0));
	}

	@Override
	protected Point getInitialLocation(final Point initialSize) {
		return view.getSurfaceComposite().toDisplay(new Point(0, 0));
	}

	@Override
	protected Point getInitialSize() {
		Point size = view.getSurfaceComposite().getSize();
		return new Point(size.x, size.y / 2);
	}

	@Override
	protected Point getDefaultSize() {
		Point size = view.getSurfaceComposite().getSize();
		return new Point(size.x, size.y / 2);
	}

	@Override
	protected Color getBackground() {
		return IGamaColors.WHITE.color();
	}

	@Override
	protected Color getForeground() {
		return IGamaColors.BLACK.color();
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		getShell().setAlpha(220);
		content = new Composite(parent, SWT.None);
		content.setLayout(new GridLayout());
		ItemList<ILayer> list = view.getDisplayManager();
		ParameterExpandBar viewer = new ParameterExpandBar(content, SWT.V_SCROLL, true, true, true, true, list);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.setLayoutData(data);
		viewer.setSpacing(5);
		for ( ILayer layer : list.getItems() ) {
			createItem("Layer " + layer.getName(), layer, createItemContentsFor(viewer, layer), viewer, false);
		}
		content.layout();
		viewer.addListener(SWT.Collapse, new Listener() {

			@Override
			public void handleEvent(final Event e) {
				content.redraw();
			}

		});
		viewer.addListener(SWT.Expand, new Listener() {

			@Override
			public void handleEvent(final Event e) {
				content.redraw();
			}

		});
		return content;
	}

	protected Composite createItemContentsFor(final Composite viewer, final ILayer d) {
		final Composite compo = new Composite(viewer, SWT.NONE);
		final GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 5;
		compo.setLayout(layout);
		if ( d instanceof AbstractLayer ) {
			LayerSideControls.fill(compo, d, view.getOutput().getSurface());
		}
		return compo;
	}

	protected ParameterExpandItem createItem(final String name, final ILayer data, final Composite control,
		final ParameterExpandBar bar, final boolean expanded) {
		ParameterExpandItem i = new ParameterExpandItem(bar, data, SWT.None);
		if ( name != null ) {
			i.setText(name);
		}
		control.pack(true);
		control.layout();
		i.setControl(control);
		i.setHeight(control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		i.setExpanded(expanded);
		return i;
	}

}
