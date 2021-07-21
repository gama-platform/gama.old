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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

public class GamaCommand {

	public static GamaCommand build(final String image, final String text, final String tooltip,
			final Selector runner) {
		return new GamaCommand(image, text, tooltip, runner);
	}

	public static GamaCommand build(final String image, final String text, final Selector runner) {
		return new GamaCommand(image, text, runner);
	}

	String text, tooltip, image;
	Selector selector;

	public GamaCommand(final String image, final String text, final String tooltip, final Selector runner) {
		this.text = text;
		this.tooltip = tooltip;
		this.image = image;
		this.selector = runner;
	}

	public GamaCommand(final String image, final String text, final Selector runner) {
		this(image, text, text, runner);
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(final String tooltip) {
		this.tooltip = tooltip;
	}

	public String getImage() {
		return image;
	}

	public void setImage(final String image) {
		this.image = image;
	}

	public Selector getListener() {
		return selector;
	}

	public void setSelector(final Selector event) {
		this.selector = event;
	}

	public String getId() {
		return image;
	}

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

	public ToolItem toItem(final ToolBar t) {
		return toItem(t, selector);
	}

	public ToolItem toItem(final ToolBar t, final Selector sel) {
		final var i = new ToolItem(t, SWT.FLAT | SWT.PUSH);
		if (text != null) { i.setText(text); }
		i.setToolTipText(tooltip);
		if (image != null) { i.setImage(create(image).image()); }
		i.addSelectionListener(sel);
		return i;
	}

	public Button toButton(final Composite t, final Selector selector2) {
		final Button i = new Button(t, SWT.FLAT | SWT.TRANSPARENT | SWT.PUSH);

		if (text != null) { i.setText(text); }
		i.setToolTipText(tooltip);
		if (image != null) { i.setImage(create(image).image()); }
		i.addSelectionListener(selector2);
		return i;
	}

	public Label toLabel(final Composite t, final MouseListener selector2) {
		final Label i = new Label(t, SWT.NONE);
		if (text != null) { i.setText(text); }
		i.setToolTipText(tooltip);
		if (image != null) { i.setImage(create(image).image()); }
		i.addMouseListener(selector2);
		return i;
	}

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
