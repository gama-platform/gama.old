/*********************************************************************************************
 *
 * 'GraphicViewer.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
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
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.StyleBuilder;
import org.opengis.style.GraphicalSymbol;

/**
 * Allows editing/viewing of a Style Layer Descriptor "Graphic".
 * <p>
 * Here is the pretty picture:
 * 
 * <pre>
 * <code>
 *          +-+ +-------+ +------+
 *   Point: |x| | star\/| |size\/|
 *          +-+ +-------+ +------+
 * </code>
 * </pre>
 * </p>
 * <p>
 * Workflow:
 * <ol>
 * <li>createControl( parent ) - set up controls
 * <li>setGraphic( graphic, mode ) - provide content from SimpleStyleConfigurator
 * <ol>
 * <li>Symbolizer values copied into fields based on mode
 * <li>fields copied into controls
 * <li>controls enabled based on mode & fields
 * </ol>
 * <li>Listener.widgetSelected/modifyText - User performs an "edit"
 * <li>Listener.sync( SelectionEvent ) - update fields with values of controls
 * <li>fire( SelectionSevent ) - notify SimpleStyleConfigurator of change
 * <li>getGraphic( Fill, Stroke, StyleBuilder ) - construct a Graphic based on fields
 * </ul>
 * </p>
 * @author Jody Garnett
 * @since 1.0.0
 *
 *
 *
 * @source $URL$
 */
public class GraphicViewer {

	boolean enabled;
	String type;
	double width;

	Button on;
	Combo name;
	Combo size;

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
				GraphicViewer.this.enabled = GraphicViewer.this.on.getSelection();
				GraphicViewer.this.type = GraphicViewer.this.name.getText();
				try {
					GraphicViewer.this.width = Integer.parseInt(GraphicViewer.this.size.getText());
				} catch (final NumberFormatException nan) {
					// well lets just leave width alone
				}
				fire(selectionEvent); // everything worked
			} catch (final Throwable t) {
				// meh
			} finally {
				GraphicViewer.this.name.setEnabled(GraphicViewer.this.enabled);
				GraphicViewer.this.size.setEnabled(GraphicViewer.this.enabled);
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
	 * @param klisten
	 * @param build
	 * @return Generated composite
	 */
	public Composite createControl(final Composite parent, final KeyListener klisten, final StyleBuilder build) {
		final Composite part = AbstractSimpleConfigurator.subpart(parent, "Point");

		this.on = new Button(part, SWT.CHECK);
		// this.on.addSelectionListener( this.sync );

		this.size = new Combo(part, SWT.DROP_DOWN);
		this.size.setItems(new String[] { "1", "2", "3", "5", "10", "15" }); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		this.size.setTextLimit(2);
		this.size.addKeyListener(klisten);
		this.size.setToolTipText("Graphic size");

		this.name = new Combo(part, SWT.DROP_DOWN);
		this.name.setItems(build.getWellKnownMarkNames());
		this.name.setTextLimit(9);
		this.name.addKeyListener(klisten);
		this.name.setToolTipText("Shape type");
		return part;
	}

	/**
	 * TODO summary sentence for getGraphic ...
	 * 
	 * @param filll
	 * @param stroke
	 * @param build
	 * @return Graphic defined by this model
	 */
	public Graphic getGraphic(final Fill filll, final Stroke stroke, final StyleBuilder build) {
		if ( !this.enabled ) {
			Mark mark = null;
			if ( this.type == null ) {
				build.createMark("square", null, null); //$NON-NLS-1$
			} else {
				mark = build.createMark(this.type, (Fill) null, (Stroke) null);
			}
			return build.createGraphic(null, mark, null);
		}
		final Mark mark = build.createMark(this.type, filll, stroke);
		final Graphic graphic = build.createGraphic(null, mark, null);
		graphic.setSize(build.literalExpression(this.width));
		return graphic;
	}

	/**
	 * TODO summary sentence for setGraphic ...
	 * 
	 * @param graphic
	 * @param mode
	 * @param enabled
	 */
	public void setGraphic(Graphic graphic, final Mode mode, final Color defaultColor) {
		boolean enabled = true;
		if ( graphic == null ) {
			final StyleBuilder builder = new StyleBuilder();
			graphic = builder.createGraphic(null, builder.createMark(StyleBuilder.MARK_SQUARE, defaultColor), null);
			enabled = true;
		}
		this.width = SLDs.size(graphic);
		final String text = MessageFormat.format("{0,number,#0}", this.width); //$NON-NLS-1$
		if ( text != null ) {
			this.size.setText(text);
			this.size.select(this.size.indexOf(text));
		}

		boolean marked = false;
		if ( graphic != null && graphic.graphicalSymbols() != null && !graphic.graphicalSymbols().isEmpty() ) {

			for ( final GraphicalSymbol symbol : graphic.graphicalSymbols() ) {
				if ( symbol instanceof Mark ) {
					final Mark mark = (Mark) symbol;
					setMark(mark, mode);
					marked = true;
					break;
				}
			}
		}
		if ( !marked ) {
			setMark(null, mode);
		}
		this.enabled = this.enabled && enabled;
	}

	private void setMark(final Mark mark, final Mode mode) {
		listen(false);
		try {
			this.enabled = mode == Mode.POINT && mark != null;
			this.type = SLD.wellKnownName(mark);

			// Stroke is used in line, point and polygon
			this.on.setEnabled(mode == Mode.POINT || mode == Mode.ALL);

			if ( this.type != null ) {
				this.name.setText(this.type);
				this.name.select(this.name.indexOf(this.type));
			}

			this.on.setSelection(this.enabled);
			this.size.setEnabled(this.enabled);
			this.name.setEnabled(this.enabled);
		} finally {
			listen(true); // listen to user now
		}
	}

	void listen(final boolean listen) {
		if ( listen ) {
			this.on.addSelectionListener(this.sync);
			this.size.addSelectionListener(this.sync);
			this.size.addModifyListener(this.sync);
			this.name.addSelectionListener(this.sync);
			this.name.addModifyListener(this.sync);
		} else {
			this.on.removeSelectionListener(this.sync);
			this.size.removeSelectionListener(this.sync);
			this.size.removeModifyListener(this.sync);
			this.name.removeSelectionListener(this.sync);
			this.name.removeModifyListener(this.sync);
		}
	}
}
