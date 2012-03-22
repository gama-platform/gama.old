package msi.gama.util.graph;

public interface IGraphEventProvider {

	public void addListener(IGraphEventListener listener);
	public void removeListener(IGraphEventListener listener);

	public void dispatchEvent(GraphEvent event);
	
}
