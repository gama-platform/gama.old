package msi.gama.outputs.layers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import msi.gama.common.interfaces.IEventLayerDelegate;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;

public class MouseEventLayerDelegate implements IEventLayerDelegate {

	public static final Set<String> EVENTS =
			new HashSet<>(Arrays.asList("mouse_up", "mouse_down", "mouse_move", "mouse_enter", "mouse_exit"));

	@Override
	public boolean acceptSource(final IScope scope, final Object source) {
		return Objects.equals(source, IKeyword.DEFAULT);
	}

	@Override
	public boolean createFrom(final IScope scope, final Object source, final EventLayerStatement statement) {
		return true;
	}

//	@Override
//	public boolean acceptEvent(final String event) {
//		return EVENTS.contains(event);
//	}
//
//	@Override
//	public void enableOn(final IScope scope, final String event, final EventLayer layer) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void disableOn(final IScope scope, final String event, final EventLayer layer) {
//		// TODO Auto-generated method stub
//
//	}

}