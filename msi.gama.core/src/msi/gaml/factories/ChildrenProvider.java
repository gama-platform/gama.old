package msi.gaml.factories;

import java.util.*;
import msi.gaml.descriptions.IDescription;

public class ChildrenProvider {

	private final List<IDescription> children;
	public static final ChildrenProvider NONE = new ChildrenProvider(null);

	public ChildrenProvider(final List descs) {
		children = descs;
	}

	public List<IDescription> getChildren() {
		return children == null ? Collections.EMPTY_LIST : children;
	}

}
