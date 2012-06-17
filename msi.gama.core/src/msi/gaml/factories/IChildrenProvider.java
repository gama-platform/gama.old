package msi.gaml.factories;

import java.util.*;
import msi.gaml.descriptions.IDescription;

public interface IChildrenProvider {

	public List<IDescription> getChildren();

	// public Class getJavaBase();

	public static IChildrenProvider NONE = new IChildrenProvider() {

		@Override
		public List<IDescription> getChildren() {
			return Collections.EMPTY_LIST;
		}

		// @Override
		// public Class getJavaBase() {
		// return null;
		// }

	};

}
