package ummisco.gama.ui.views.toolbar;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;

import ummisco.gama.ui.resources.GamaIcons;

public class GamaCommand {

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

		result.setImageDescriptor(GamaIcons.create(image).descriptor());
		result.setToolTipText(tooltip);
		result.setId(image);
		return result;

	}

	public Action toCheckAction() {
		final Action result = new Action(text, Action.AS_CHECK_BOX) {

			@Override
			public void runWithEvent(final Event e) {
				selector.widgetSelected(new SelectionEvent(e));
			}
		};

		result.setImageDescriptor(GamaIcons.create(image).descriptor());
		result.setToolTipText(tooltip);
		result.setId(image);
		return result;

	}

}
