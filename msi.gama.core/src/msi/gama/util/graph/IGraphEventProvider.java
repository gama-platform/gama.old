/*********************************************************************************************
 *
 * 'IGraphEventProvider.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph;

public interface IGraphEventProvider {

	public void addListener(IGraphEventListener listener);
	public void removeListener(IGraphEventListener listener);

	public void dispatchEvent(GraphEvent event);
	
}
