package msi.gaml.factories;

import java.util.*;
import msi.gaml.descriptions.IDescription;

public class ChildrenProvider implements IChildrenProvider {

	private final List<IDescription> children;
	private Class javaBase;

	public ChildrenProvider(final List<IDescription> descs) {
		children = descs;
	}

	public ChildrenProvider(final List<IDescription> descs, final Class base) {
		children = descs;
		javaBase = base;
	}

	@Override
	public List<IDescription> getChildren() {
		return children == null ? Collections.EMPTY_LIST : children;
	}

	@Override
	public Class getJavaBase() {
		return javaBase;
	}

}
