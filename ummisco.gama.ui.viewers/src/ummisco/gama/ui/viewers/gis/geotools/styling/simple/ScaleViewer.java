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
 */
package ummisco.gama.ui.viewers.gis.geotools.styling.simple;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * Allows editing/viewing of a scale. Used to build the min/max scale editors for a rule.
 * <p>
 * Here is the pretty picture:
 * 
 * <pre>
 * <code>
 *          +-+ +-------------+             
 *    Scale:|x| | 90%       \/| 
 *          +-+ +-------------+
 * </code>
 * </pre>
 * </p>
 * <p>
 * Workflow:
 * <ol>
 * <li>createControl( parent ) - set up controls
 * <li>setFill( stroke, mode ) - provide content from SimpleStyleConfigurator/SimpleRasterConfigurator
 * <ol>
 * <li>scale values got from rules
 * <li>values copied into controls
 * <li>controls enabled based on mode & fields
 * </ol>
 * <li>Listener.widgetSelected/modifyText - User performs an "edit"
 * <li>Listener.sync( SelectionEvent ) - update fields with values of controls
 * <li>fire( SelectionSevent ) - notify SimpleStyleConfigurator/SimpleRasterConfigurator of change
 * <li>getScale( ) - returns the specified scale
 * </ul>
 * </p>
 * @author Andrea Aime
 * @since 1.1
 *
 *
 *
 * @source $URL$
 */
public class ScaleViewer {

	public static final int MIN = 0;
	public static final int MAX = 1;

	boolean enabled;
	double scale;
	int type;

	Button on;
	Combo scaleEditor;

	private class Listener implements SelectionListener, ModifyListener {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			sync(e);
		};

		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
			sync(e);
		};

		@Override
		public void modifyText(final ModifyEvent e) {
			sync(AbstractSimpleConfigurator.selectionEvent(e));
		};

		private void sync(final SelectionEvent selectionEvent) {
			try {
				ScaleViewer.this.enabled = ScaleViewer.this.on.getSelection();
				final String ptext = ScaleViewer.this.scaleEditor.getText();
				ScaleViewer.this.scale = Double.parseDouble(ptext);
				fire(selectionEvent); // everything worked
			} catch (final Throwable t) {
				t.printStackTrace();
			} finally {
				ScaleViewer.this.scaleEditor.setEnabled(ScaleViewer.this.enabled);
			}
		}

	};

	Listener sync = new Listener();
	private SelectionListener listener;

	public ScaleViewer(final int type) {
		if ( type != MIN && type != MAX )
			throw new IllegalArgumentException("Type should be either MIN or MAX"); //$NON-NLS-1$
		this.type = type;
		this.scale = type == MIN ? 0 : Double.MAX_VALUE;
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
	 * TODO summary sentence for createControl ...
	 * 
	 * @param parent
	 * @param kListener
	 * @return Generated composite
	 */
	public Composite createControl(final Composite parent, final KeyListener kListener) {
		final String labelId = type == MIN ? "Min scale:" : "Max scale:";
		final Composite part = AbstractSimpleConfigurator.subpart(parent, labelId);

		this.on = new Button(part, SWT.CHECK);
		this.on.addSelectionListener(this.sync);

		this.scaleEditor = new Combo(part, SWT.DROP_DOWN);
		this.scaleEditor.setItems(new String[] { "100", "1000", "10000", "100000", "1000000", "10000000" }); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		this.scaleEditor.setTextLimit(10);
		this.scaleEditor.addKeyListener(kListener);
		final String tooltip = type == MIN ? "Minimum scale denominator" : "Maximum scale denominator";
		this.scaleEditor.setToolTipText(tooltip);
		return part;
	}

	/**
	 * Gets the scale denominator chosen by the user, or the default value for this type if none was selected.
	 * Default values are 0 for MIN type, {@linkplain Double#MAX_VALUE} for the MAX type
	 * @param build
	 * 
	 * @return Fill defined by this model
	 */
	public double getScale() {
		if ( !this.enabled )
			return type == MIN ? 0 : Double.MAX_VALUE;
		else return scale;
	}

	/**
	 * Sets the scale denominator, or disables the component if the provided scale is not a positive number
	 * @param scale
	 */
	public void setScale(final double scale2, final long defaultScale) {
		listen(false);

		this.scale = scale2;
		this.enabled = true;
		if ( Double.isNaN(scale) || Double.isInfinite(scale) || scale <= Double.MIN_VALUE ||
			scale >= Double.MAX_VALUE ) {
			this.scale = defaultScale;
			this.enabled = false;
		}

		scaleEditor.setText(Double.toString(scale));
		this.on.setSelection(this.enabled);
		this.scaleEditor.setEnabled(this.enabled);
		listen(true);
	}

	void listen(final boolean listen) {
		if ( listen ) {
			this.on.addSelectionListener(this.sync);
			this.scaleEditor.addSelectionListener(this.sync);
			this.scaleEditor.addModifyListener(this.sync);
		} else {
			this.on.removeSelectionListener(this.sync);
			this.scaleEditor.removeSelectionListener(this.sync);
			this.scaleEditor.removeModifyListener(this.sync);
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

}
