/*******************************************************************************************************
 *
 * msi.gama.util.graph.IGraphEventProvider.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph;

import msi.gama.runtime.IScope;

public interface IGraphEventProvider {

	public void addListener(IGraphEventListener listener);

	public void removeListener(IGraphEventListener listener);

	public void dispatchEvent(final IScope scope, GraphEvent event);

}
