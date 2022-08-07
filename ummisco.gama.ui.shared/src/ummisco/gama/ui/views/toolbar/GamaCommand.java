/*******************************************************************************************************
 *
 * GamaCommand.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.toolbar;

import static ummisco.gama.ui.resources.GamaIcons.create;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import ummisco.gama.ui.resources.GamaIcon;

/**
 * The Class GamaCommand.
 */
public class GamaCommand {

	/**
	 * Builds the.
	 *
	 * @param image
	 *            the image
	 * @param text
	 *            the text
	 * @param tooltip
	 *            the tooltip
	 * @param runner
	 *            the runner
	 * @return the gama command
	 */
	public static GamaCommand build(final String image, final String text, final String tooltip,
			final Selector runner) {
		return new GamaCommand(image, text, tooltip, runner);
	}

	/**
	 * Builds the.
	 *
	 * @param image
	 *            the image
	 * @param text
	 *            the text
	 * @param runner
	 *            the runner
	 * @return the gama command
	 */
	public static GamaCommand build(final String image, final String text, final Selector runner) {
		return new GamaCommand(image, text, runner);
	}

	/** The image. */
	String text, tooltip, image;

	/** The selector. */
	Selector selector;

	/**
	 * Instantiates a new gama command.
	 *
	 * @param image
	 *            the image
	 * @param text
	 *            the text
	 * @param tooltip
	 *            the tooltip
	 * @param runner
	 *            the runner
	 */
	public GamaCommand(final String image, final String text, final String tooltip, final Selector runner) {
		this.text = text;
		this.tooltip = tooltip;
		this.image = image;
		this.selector = runner;
	}

	/**
	 * Instantiates a new gama command.
	 *
	 * @param image
	 *            the image
	 * @param text
	 *            the text
	 * @param runner
	 *            the runner
	 */
	public GamaCommand(final String image, final String text, final Selector runner) {
		this(image, text, text, runner);
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() { return text; }

	/**
	 * Sets the text.
	 *
	 * @param text
	 *            the new text
	 */
	public void setText(final String text) { this.text = text; }

	/**
	 * Gets the tooltip.
	 *
	 * @return the tooltip
	 */
	public String getTooltip() { return tooltip; }

	/**
	 * Sets the tooltip.
	 *
	 * @param tooltip
	 *            the new tooltip
	 */
	public void setTooltip(final String tooltip) { this.tooltip = tooltip; }

	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public String getImage() { return image; }

	/**
	 * Sets the image.
	 *
	 * @param image
	 *            the new image
	 */
	public void setImage(final String image) { this.image = image; }

	/**
	 * Gets the listener.
	 *
	 * @return the listener
	 */
	public Selector getListener() { return selector; }

	/**
	 * Sets the selector.
	 *
	 * @param event
	 *            the new selector
	 */
	public void setSelector(final Selector event) { this.selector = event; }

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() { return image; }

	/**
	 * To action.
	 *
	 * @return the action
	 */
	public Action toAction() {
		final Action result = new Action(text) {

			@Override
			public void runWithEvent(final Event e) {
				selector.widgetSelected(new SelectionEvent(e));
			}
		};

		result.setImageDescriptor(create(image).descriptor());
		result.setToolTipText(tooltip);
		result.setId(image);
		return result;

	}

	/**
	 * To check action.
	 *
	 * @return the action
	 */
	public Action toCheckAction() {
		final Action result = new Action(text, IAction.AS_CHECK_BOX) {

			@Override
			public void runWithEvent(final Event e) {
				selector.widgetSelected(new SelectionEvent(e));
			}
		};

		result.setImageDescriptor(create(image).descriptor());
		result.setToolTipText(tooltip);
		result.setId(image);
		return result;

	}

	/**
	 * To item.
	 *
	 * @param t
	 *            the t
	 * @return the tool item
	 */
	public ToolItem toItem(final ToolBar t) {
		return toItem(t, selector);
	}

	/**
	 * To item.
	 *
	 * @param t
	 *            the t
	 * @param sel
	 *            the sel
	 * @return the tool item
	 */
	public ToolItem toItem(final ToolBar t, final Selector sel) {
		final var i = new ToolItem(t, SWT.FLAT | SWT.PUSH);
		if (text != null) { i.setText(text); }
		i.setToolTipText(tooltip);
		if (image != null) {
			GamaIcon icon = create(image);
			i.setImage(icon.image());
			i.setDisabledImage(icon.disabled());
		}
		i.addSelectionListener(sel);
		return i;
	}

	public MenuItem toItem(final Menu m) {
		final var i = new MenuItem(m, SWT.PUSH);
		if (text != null) { i.setText(text); }
		i.setToolTipText(tooltip);
		if (image != null) { i.setImage(create(image).image()); }
		i.addSelectionListener(selector);
		return i;
	}

	/**
	 * To button.
	 *
	 * @param t
	 *            the t
	 * @param selector2
	 *            the selector 2
	 * @return the button
	 */
	public Button toButton(final Composite t, final Selector selector2) {
		final Button i = new Button(t, SWT.FLAT | SWT.TRANSPARENT | SWT.PUSH);

		if (text != null) { i.setText(text); }
		i.setToolTipText(tooltip);
		if (image != null) { i.setImage(create(image).image()); }
		i.addSelectionListener(selector2);
		return i;
	}

	/**
	 * To label.
	 *
	 * @param t
	 *            the t
	 * @param selector2
	 *            the selector 2
	 * @return the label
	 */
	public Label toLabel(final Composite t, final MouseListener selector2) {
		final Label i = new Label(t, SWT.NONE);
		if (text != null) { i.setText(text); }
		i.setToolTipText(tooltip);
		if (image != null) { i.setImage(create(image).image()); }
		i.addMouseListener(selector2);
		return i;
	}

	/**
	 * To link.
	 *
	 * @param t
	 *            the t
	 * @param selector2
	 *            the selector 2
	 * @return the hyperlink
	 */
	public Hyperlink toLink(final Composite t, final Selector selector2) {
		if (image == null) {
			Hyperlink l = new Hyperlink(t, SWT.FLAT);
			l.setText(text);
			l.setToolTipText(tooltip);
			l.setUnderlined(false);
			return l;
		}
		final ImageHyperlink i = new ImageHyperlink(t, SWT.FLAT);
		if (text != null) { i.setText(text); }
		i.setToolTipText(tooltip);
		i.setImage(create(image).image());
		i.setUnderlined(false);
		return i;
	}

}
