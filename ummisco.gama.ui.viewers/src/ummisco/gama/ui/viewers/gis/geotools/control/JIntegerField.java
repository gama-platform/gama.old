/*********************************************************************************************
 *
 * 'JIntegerField.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.control;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

/**
 * A text field control to work with integer values. It can be constrained to positive values if desired.
 * <p>
 *
 * @author Michael Bedward
 * @author Andrea Antonello (www.hydrologis.com)
 *
 *
 *
 * @source $URL$
 */
public class JIntegerField extends JValueField implements ModifyListener {

	private boolean fireEvents;
	private final boolean allowNegative;

	/**
	 * Creates a new text field that allows negative values and has an initial value of 0.
	 */
	public JIntegerField(final Composite parent, final int style) {
		this(parent, style, 0, true);
	}

	/**
	 * Creates a new text field with an initial value of 0.
	 *
	 * @param allowNegative
	 *            true if this field should allow negative values to be entered; false if only positive values are
	 *            allowed
	 */
	public JIntegerField(final Composite parent, final int style, final boolean allowsNegative) {
		this(parent, style, 0, allowsNegative);
	}

	/**
	 * Creates a new text field that allows negative values and the given initial value.
	 *
	 * @param value
	 *            the initial value to display
	 */
	public JIntegerField(final Composite parent, final int style, final int value) {
		this(parent, style, value, true);
	}

	/**
	 * Creates a new text field with the given initial value.
	 *
	 * @param value
	 *            the initial value to display
	 * @param allowNegative
	 *            true if this field should allow negative values to be entered; false if only positive values are
	 *            allowed
	 */
	public JIntegerField(final Composite parent, final int style, final int value, final boolean allowNegative) {
		super(parent, style);

		addModifyListener(this);

		this.allowNegative = allowNegative;
		setValue(value);

	}

	/**
	 * Get the current value of this control.
	 *
	 * @return the current value
	 */
	public int getValue() {
		return Integer.parseInt(getText());
	}

	/**
	 * Set the integer value of this control. A {@code ValueChangedEvent} will be published to all
	 * {@code ValueChangedListeners}.
	 * 
	 * @param value
	 *            the value to set
	 *
	 * @throws IllegalArgumentException
	 *             if {@code value} is negative but the field only allows positive values
	 */
	public void setValue(final int value) {
		setValue(value, true);
	}

	/**
	 * Set the integer value of this control, optionally skipping notification of the change to listeners.
	 * <p>
	 * This version is useful when two or more controls are synchronized (ie. changes to the value of one control
	 * results in changes to the values of other controls). In such a setting, firing change events can result in an
	 * endless cycle or a mutex violation.
	 *
	 * @param value
	 *            the value to set
	 * @param publishEvent
	 *            true to notify listeners of this change; false to skip notification
	 *
	 * @throws IllegalArgumentException
	 *             if {@code value} is negative but the field only allows positive values
	 */
	public void setValue(final int value, final boolean publishEvent) {
		fireEvents = publishEvent;

		if (!allowNegative && value < 0) { throw new IllegalArgumentException(
				String.format("Negative value (%d) but text field set to only allow positive values", value)); }
		setText(String.valueOf(value));
		fireEvents = true;
	}

	@Override
	public void modifyText(final ModifyEvent arg0) {
		if (fireEvents) {
			final ValueChangedEvent<Integer> ev =
					new ValueChangedEvent<>(JIntegerField.this, Integer.valueOf(getText()));
			fireValueChangedEvent(ev);
		}

	}

}
