/*******************************************************************************************************
 *
 * MouseEventLayerDelegate.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
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
public class MouseEventLayerDelegate implements IEventLayerDelegate {

	/** The Constant MOUSE_DOWN. */
	@constant (
			value = "mouse_down",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user presses the mouse button")) final static public String MOUSE_DOWN =
					"mouse_down";

	/** The Constant MOUSE_UP. */
	@constant (
			value = "mouse_up",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user releases the mouse button")) final static public String MOUSE_UP =
					"mouse_up";

	/** The Constant MOUSE_MOVED. */
	@constant (
			value = "mouse_move",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user moves the mouse")) final static public String MOUSE_MOVED =
					"mouse_move";

	/** The Constant MOUSE_CLICKED. */
	@constant (
			value = "mouse_click",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user presses and releases the mouse button immediately")) final static public String MOUSE_CLICKED =
					"mouse_click";

	/** The Constant MOUSE_ENTERED. */
	@constant (
			value = "mouse_enter",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the mouse enters the display")) final static public String MOUSE_ENTERED =
					"mouse_enter";

	/** The Constant MOUSE_EXITED. */
	@constant (
			value = "mouse_exit",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the mouse leaves the display")) final static public String MOUSE_EXITED =
					"mouse_exit";

	/** The Constant MOUSE_MENU. */
	@constant (
			value = "mouse_menu",
			category = IOperatorCategory.USER_CONTROL,
			doc = @doc ("Represents an event emitted when the user invokes the contextual menu")) final static public String MOUSE_MENU =
					"mouse_menu";

	/** The Constant EVENTS. */
	public static final Set<String> EVENTS =
			new HashSet<>(Arrays.asList(MOUSE_UP, MOUSE_DOWN, MOUSE_MOVED, MOUSE_ENTERED, MOUSE_EXITED, MOUSE_MENU));

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