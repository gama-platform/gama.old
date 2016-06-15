/*
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package ummisco.gama.ui.viewers.gis.geotools.styling.simple;

import java.text.MessageFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.StyleBuilder;

/**
 * Allows editing/viewing of Opacity Element.
 * <p>
 * Here is the pretty picture:
 * 
 * <pre>
 * <code>
 *          +----------------+
 *   Label: |       opacity\/|
 *          +----------------+
 * </code>
 * </pre>
 * </p>
 * <p>
 * Workflow:
 * <ol>
 * <li>createControl( parent ) - set up controls
 * <li>set( raster, mode ) - provide content from SimpleStyleConfigurator
 * <ol>
 * <li>Symbolizer values copied into fields based on mode
 * <li>fields copied into controls
 * <li>controls enabled based on mode & fields
 * </ol>
 * <li>Listener.widgetSelected/modifyText - User performs an "edit"
 * <li>Listener.sync( SelectionEvent ) - update fields with values of controls
 * <li>fire( SelectionSevent ) - notify SimpleStyleConfigurator of change
 * <li>get( StyleBuilder ) - construct based on fields
 * </ul>
 * </p>
 * @author Emily Gouge (Refractions Research, Inc.)
 * @since 1.0.0
 *
 *
 *
 * @source $URL$
 */
public class OpacityViewer {

	double opacity = Double.NaN;

	private Combo percent;
	private SelectionListener listener;

	private class Listener implements ModifyListener {

		@Override
		public void modifyText(final ModifyEvent e) {
			sync(AbstractSimpleConfigurator.selectionEvent(e));
		};

		private void sync(final SelectionEvent e) {
			try {
				try {
					String ptext = OpacityViewer.this.percent.getText();
					if ( ptext.endsWith("%") ) { //$NON-NLS-1$
						ptext = ptext.substring(0, ptext.length() - 1);
						OpacityViewer.this.opacity = Double.parseDouble(ptext);
						OpacityViewer.this.opacity /= 100.0;
					} else {
						OpacityViewer.this.opacity = Double.parseDouble(ptext);
						if ( OpacityViewer.this.opacity > 1 ) {
							OpacityViewer.this.opacity /= 100.0;
						}
					}
				} catch (final NumberFormatException nan) {
					// well lets just leave opacity alone
				}
				fire(e); // everything worked
			} catch (final Throwable t) {}
		}

	};

	Listener sync = new Listener();

	/**
	 * TODO summary sentence for createControl ...
	 * 
	 * @param parent
	 * @param listener1
	 * @return Generated composite
	 */
	public Composite createControl(final Composite parent, final KeyListener listener1) {
		final Composite part = AbstractSimpleConfigurator.subpart(parent, "Raster");

		this.percent = new Combo(part, SWT.DROP_DOWN);
		this.percent.setItems(new String[] { "0%", "25%", "50%", "75%", "100%" }); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
		this.percent.setTextLimit(4);
		this.percent.addKeyListener(listener1);
		this.percent.setToolTipText("Percent opacity");
		return part;
	}

	void listen(final boolean listen) {
		if ( listen ) {
			this.percent.addModifyListener(this.sync);
		} else {
			this.percent.removeModifyListener(this.sync);
		}
	}

	/**
	 * Accepts a listener that will be notified when content changes.
	 * @param listener1
	 */
	public void addListener(final SelectionListener listener1) {
		this.listener = listener1;
	}

	/**
	 * Remove listener.
	 * @param listener1
	 */
	public void removeListener(final SelectionListener listener1) {
		if ( this.listener == listener1 )
			this.listener = null;
	}

	/**
	 * TODO summary sentence for fire ...
	 * 
	 * @param event
	 */
	protected void fire(final SelectionEvent event) {
		if ( this.listener == null )
			return;
		this.listener.widgetSelected(event);
	}

	/**
	 * Called to set up this "viewer" based on the provided symbolizer
	 * @param sym
	 */
	public void set(final RasterSymbolizer sym2) {
		listen(false); // don't sync when setting up
		try {
			final RasterSymbolizer sym = sym2;

			if ( sym == null ) {
				this.opacity = 1.0;
			} else {
				this.opacity = SLD.rasterOpacity(sym);
			}
			final String text = MessageFormat.format("{0,number,#0%}", this.opacity); //$NON-NLS-1$
			this.percent.setText(text);
			this.percent.select(this.percent.indexOf(text));

		} finally {
			listen(true); // listen to user now
		}
	}

	/**
	 * TODO summary sentence for getStroke ...
	 * @param build
	 * 
	 * @return Stroke defined by this model
	 */
	public RasterSymbolizer get(final StyleBuilder build) {
		return Double.isNaN(opacity) ? null : build.createRasterSymbolizer(null, this.opacity);
	}

	public double getValue() {
		return this.opacity;
	}
}
