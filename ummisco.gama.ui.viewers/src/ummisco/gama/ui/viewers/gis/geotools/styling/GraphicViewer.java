/*******************************************************************************************************
 *
 * GraphicViewer.java, in ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.styling;

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
 *
 * @author Jody Garnett
 * @since 1.0.0
 *
 *
 *
 * @source $URL$
 */
public class GraphicViewer {

	/** The enabled. */
	boolean enabled;
	
	/** The type. */
	String type;
	
	/** The width. */
	double width;

	/** The on. */
	Button on;
	
	/** The name. */
	Combo name;
	
	/** The size. */
	Combo size;

	/**
	 * The Class Listener.
	 */
	class Listener implements SelectionListener, ModifyListener {

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
			sync(SimpleConfigurator.selectionEvent(e));
		};

		/**
		 * Sync.
		 *
		 * @param selectionEvent the selection event
		 */
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

	/** The sync. */
	Listener sync = new Listener();
	
	/** The listener. */
	private SelectionListener listener;

	/**
	 * Accepts a listener that will be notified when content changes.
	 *
	 * @param listener1
	 */
	public void addListener(final SelectionListener listener1) {
		this.listener = listener1;
	}

	/**
	 * TODO summary sentence for fire ...
	 *
	 * @param event
	 */
	protected void fire(final SelectionEvent event) {
		if (this.listener == null) { return; }
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
		final Composite part = SimpleConfigurator.subpart(parent, "Point");

		this.on = new Button(part, SWT.CHECK);

		this.size = new Combo(part, SWT.DROP_DOWN);
		this.size.setItems(new String[] { "1", "2", "3", "5", "10", "15" });
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
		if (!this.enabled) {
			Mark mark = null;
			if (this.type == null) {
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
	public void setGraphic(final Graphic g, final Mode mode, final Color defaultColor) {
		Graphic graphic = g;
		boolean enabled = true;
		if (graphic == null) {
			final StyleBuilder builder = new StyleBuilder();
			graphic = builder.createGraphic(null, builder.createMark(StyleBuilder.MARK_SQUARE, defaultColor), null);
			enabled = true;
		}
		this.width = SLDs.size(graphic);
		final String text = MessageFormat.format("{0,number,#0}", this.width); //$NON-NLS-1$
		if (text != null) {
			this.size.setText(text);
			this.size.select(this.size.indexOf(text));
		}

		boolean marked = false;
		if (graphic != null && graphic.graphicalSymbols() != null && !graphic.graphicalSymbols().isEmpty()) {

			for (final GraphicalSymbol symbol : graphic.graphicalSymbols()) {
				if (symbol instanceof Mark) {
					final Mark mark = (Mark) symbol;
					setMark(mark, mode);
					marked = true;
					break;
				}
			}
		}
		if (!marked) {
			setMark(null, mode);
		}
		this.enabled = this.enabled && enabled;
	}

	/**
	 * Sets the mark.
	 *
	 * @param mark the mark
	 * @param mode the mode
	 */
	private void setMark(final Mark mark, final Mode mode) {
		listen(false);
		try {
			this.enabled = mode == Mode.POINT && mark != null;
			this.type = SLD.wellKnownName(mark);

			// Stroke is used in line, point and polygon
			this.on.setEnabled(mode == Mode.POINT || mode == Mode.ALL);

			if (this.type != null) {
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

	/**
	 * Listen.
	 *
	 * @param listen the listen
	 */
	void listen(final boolean listen) {
		if (listen) {
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
