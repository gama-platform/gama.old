package msi.gama.common.interfaces;

import java.util.List;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gaml.types.Types;

public interface IColored {

	GamaColor getColor(IScope scope);

	default List<GamaColor> getColors(final IScope scope) {
		return GamaListFactory.wrap(Types.COLOR, getColor(scope));
	}

}
