package msi.gama.gui.views;

/**
 * A view that may be zoomed.
 * Zooms are made in an asynchronous way (that is, not blocking call).
 * 
 * @author Samuel Thiriot
 *
 */
public interface IViewWithZoom {

	public void zoomToFit();
	public void zoomIn();
	public void zoomOut();
	public void toggleView();
	
	public void snapshot();
	
	/**
	 * If true, the view should block the simulation when refresh is called.
	 * Else it may work in a "best-effort" way (display as soon as possible) 
	 * @param synchro
	 */
	public void setSynchronized(boolean synchro);
	
}
