/*********************************************************************************************
 *
 * 'FillViewer.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.styling.simple;

import java.awt.Color;
import java.text.MessageFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.geotools.styling.Fill;
import org.geotools.styling.SLD;
import org.geotools.styling.StyleBuilder;

/**
 * Allows editing/viewing of a Style Layer Descriptor "Stroke".
 * <p>
 * Here is the pretty picture:
 * 
 * <pre>
 * <code>
 *          +-+ +-------+ +------+             
 *    Fill: |x| | color | | 90%\/| 
 *          +-+ +-------+ +------+
 * </code>
 * </pre>
 * </p>
 * <p>
 * Workflow:
 * <ol>
 * <li>createControl( parent ) - set up controls
 * <li>setFill( stroke, mode ) - provide content from SimpleStyleConfigurator
 * <ol>
 * <li>Symbolizer values copied into fields based on mode
 * <li>fields copied into controls
 * <li>controls enabled based on mode & fields
 * </ol>
 * <li>Listener.widgetSelected/modifyText - User performs an "edit"
 * <li>Listener.sync( SelectionEvent ) - update fields with values of controls
 * <li>fire( SelectionSevent ) - notify SimpleStyleConfigurator of change
 * <li>getFill( StyleBuilder ) - construct a Fill based on fields
 * </ul>
 * </p>
 * @author Jody Garnett
 * @since 1.0.0
 *
 *
 *
 * @source $URL$
 */
public class FillViewer {

	boolean enabled;
	Color color;
	double opacity;

	Button on;
	StolenColorEditor chooser;
	Combo percent;

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
				FillViewer.this.enabled = FillViewer.this.on.getSelection();
				FillViewer.this.color = FillViewer.this.chooser.getColor();
				try {
					String ptext = FillViewer.this.percent.getText();
					if ( ptext.endsWith("%") ) { //$NON-NLS-1$
						ptext = ptext.substring(0, ptext.length() - 1);
						FillViewer.this.opacity = Double.parseDouble(ptext) / 100.0;
					} else {
						FillViewer.this.opacity = Double.parseDouble(ptext);
						if ( FillViewer.this.opacity > 1 ) {
							FillViewer.this.opacity /= 100.0;
						}
					}
				} catch (final NumberFormatException nan) {
					// well lets just leave opacity alone
					throw nan;
				}
				fire(selectionEvent); // everything worked
			} catch (final Throwable t) {
				t.printStackTrace();
			} finally {
				FillViewer.this.chooser.setEnabled(FillViewer.this.enabled);
				FillViewer.this.percent.setEnabled(FillViewer.this.enabled);
			}
		}

	};

	Listener sync = new Listener();
	private SelectionListener listener;

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
		final Composite part = AbstractSimpleConfigurator.subpart(parent, "Fill:");

		this.on = new Button(part, SWT.CHECK);
		this.on.addSelectionListener(this.sync);

		this.chooser = new StolenColorEditor(part, this.sync);

		this.percent = new Combo(part, SWT.DROP_DOWN);
		this.percent.setItems(new String[] { "0%", "25%", "50%", "75%", "100%" }); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		this.percent.setTextLimit(4);
		this.percent.addKeyListener(kListener);
		this.percent.setToolTipText("Fill opacity");
		return part;
	}

	/**
	 * TODO summary sentence for getFill ...
	 * @param build
	 * 
	 * @return Fill defined by this model
	 */
	public Fill getFill(final StyleBuilder build) {
		if ( !this.enabled )
			return null;
		if ( !Double.isNaN(this.opacity) ) { return build.createFill(this.color, this.opacity); }
		return build.createFill(this.color);
	}

	void listen(final boolean listen) {
		if ( listen ) {
			this.on.addSelectionListener(this.sync);
			this.chooser.setListener(this.sync);
			this.percent.addSelectionListener(this.sync);
			this.percent.addModifyListener(this.sync);
		} else {
			this.on.removeSelectionListener(this.sync);
			this.chooser.setListener(null);
			this.percent.removeSelectionListener(this.sync);
			this.percent.removeModifyListener(this.sync);
		}
	}

	/**
	 * TODO summary sentence for setFill ...
	 * 
	 * @param fill
	 * @param mode
	 * @param enabled
	 */
	public void setFill(final Fill fill2, final Mode mode, final Color defaultColor) {
		listen(false);
		try {

			boolean enabled = true;
			Fill fill = fill2;
			if ( fill == null ) {
				final StyleBuilder builder = new StyleBuilder();
				fill = builder.createFill(defaultColor, 0.5);
				enabled = false;
			}

			this.enabled = enabled && mode != Mode.NONE && mode != Mode.LINE && fill != null;
			this.color = SLD.color(fill);
			this.opacity = SLD.opacity(fill);

			// Fill is used in point and polygon
			this.on.setEnabled(mode != Mode.NONE && mode != Mode.LINE);
			this.chooser.setColor(this.color);

			final String text = MessageFormat.format("{0,number,#0%}", this.opacity); //$NON-NLS-1$
			this.percent.setText(text);
			this.percent.select(this.percent.indexOf(text));

			this.on.setSelection(this.enabled);
			this.chooser.setEnabled(this.enabled);
			this.percent.setEnabled(this.enabled);
		} finally {
			listen(true);
		}
	}
}
