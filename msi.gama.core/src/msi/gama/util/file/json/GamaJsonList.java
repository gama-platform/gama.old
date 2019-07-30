/*
 * Copyright 2016-2017 Clifton Labs Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package msi.gama.util.file.json;

import java.util.Collection;
import java.util.Iterator;

import msi.gama.util.GamaList;
import msi.gaml.types.Types;

/**
 * GamaJsonList is a GAMA compatible implementation of a json array
 *
 * @author A. Drogoul, adapted from json-simple library
 */
public class GamaJsonList extends GamaList<Object> implements Jsonable {
	public GamaJsonList() {
		super();
	}

	public GamaJsonList(final Collection<?> collection) {
		super(collection.size(), Types.NO_TYPE);
		addAll(collection);
	}

	@Override
	public String toJson() {
		final StringBuilder writable = new StringBuilder();
		boolean isFirstElement = true;
		final Iterator<Object> elements = this.iterator();
		writable.append('[');
		while (elements.hasNext()) {
			if (isFirstElement) {
				isFirstElement = false;
			} else {
				writable.append(',');
			}
			writable.append(Jsoner.serialize(elements.next()));
		}
		writable.append(']');
		return writable.toString();
	}
}
