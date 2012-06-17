package msi.gaml.factories;

import java.util.*;
import msi.gaml.descriptions.IDescription;

public class ChildrenProvider implements IChildrenProvider {

	private final List<IDescription> children;

	public ChildrenProvider(final List descs) {
		children = descs;
	}

	@Override
	public List<IDescription> getChildren() {
		return children == null ? Collections.EMPTY_LIST : children;
	}

}
