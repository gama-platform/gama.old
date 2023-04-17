/*******************************************************************************************************
 *
 * KeyboardEventLayerDelegate.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import msi.gama.common.interfaces.IEventLayerDelegate;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;

/**
 * The Class MouseEventLayerDelegate.
 */
public class KeyboardEventLayerDelegate implements IEventLayerDelegate {

	/** The Constant SHIFT_MODIFIER. */
	@constant (
			value = "shift",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents the shift key modifier")) final static public String SHIFT_MODIFIER = "|shift|";

	/** The Constant CONTROL_MODIFIER. */
	@constant (
			value = "ctrl",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents the control key modifier")) final static public String CONTROL_MODIFIER = "|ctrl|";

	/** The Constant ALT_MODIFIER. */
	@constant (
			value = "alt",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents the alt key modifier")) final static public String ALT_MODIFIER = "|alt|";

	/** The Constant CMD_MODIFIER. */
	@constant (
			value = "cmd",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents the command key modifier")) final static public String CMD_MODIFIER = "|cmd|";
	/** The mouse press const. */
	@constant (
			value = "arrow_down",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user presses the arrow down key. Defining an event layer with this event will deactivate the default navigation using arrow keys in the display. Use shift+arrow to override")) final static public String ARROW_DOWN =
					"arrow_down";

	/** The mouse press const. */
	@constant (
			value = "arrow_up",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user presses the arrow up key. Defining an event layer with this event will deactivate the default navigation using arrow keys in the display. Use shift+arrow to override")) final static public String ARROW_UP =
					"arrow_up";

	/** The mouse press const. */
	@constant (
			value = "arrow_left",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user presses the arrow left key. Defining an event layer with this event will deactivate the default navigation using arrow keys in the display. Use shift+arrow to override")) final static public String ARROW_LEFT =
					"arrow_left";

	/** The mouse press const. */
	@constant (
			value = "arrow_right",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user presses the arrow right key.Defining an event layer with this event will deactivate the default navigation using arrow keys in the display. Use shift+arrow to override")) final static public String ARROW_RIGHT =
					"arrow_right";

	/** The mouse press const. */
	@constant (
			value = "escape",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user presses the ESC key. Defining an event layer with this event will deactivate the fullscreen shortcut in the display. Use the toolbar/menu command to go fullscreen")) final static public String KEY_ESC =
					"escape";

	/** The mouse press const. */
	@constant (
			value = "page_down",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user presses the page down key")) final static public String KEY_PAGE_DOWN =
					"page_down";

	/** The mouse press const. */
	@constant (
			value = "page_up",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user presses the page up key")) final static public String KEY_PAGE_UP =
					"page_up";

	/** The mouse press const. */
	@constant (
			value = "enter",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user presses the enter/return key")) final static public String KEY_ENTER =
					"enter";

	/** The mouse press const. */
	@constant (
			value = "tab",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user presses the tab key")) final static public String KEY_TAB =
					"tab";

	/** The Constant EVENTS. */
	public static final Set<String> EVENTS = new HashSet<>(Arrays.asList(KEY_ENTER, KEY_ESC, KEY_PAGE_DOWN, KEY_PAGE_UP,
			KEY_TAB, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT, ARROW_UP));

	@Override
	public boolean acceptSource(final IScope scope, final Object source) {
		return Objects.equals(source, IKeyword.DEFAULT);
	}

	@Override
	public boolean createFrom(final IScope scope, final Object source, final EventLayerStatement statement) {
		return true;
	}

	@Override
	public Set<String> getEvents() { return EVENTS; }

}