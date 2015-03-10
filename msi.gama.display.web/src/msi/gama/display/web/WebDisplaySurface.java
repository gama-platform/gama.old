/*********************************************************************************************
 * 
 * 
 * 'WebDisplaySurface.java', in plugin 'msi.gama.display.web', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.display.web;


// @display("web")
public class WebDisplaySurface /* implements IDisplaySurface */{
	//
	// private static final String TAG = WebDisplaySurface.class.getName();
	//
	// public WebDisplaySurface(final Object ... args) {
	// Logger.mlog(TAG, "constructor");
	// }
	//
	// @Override
	// public IScope getDisplayScope() {
	// return null;
	// }
	//
	// /**
	// * Method getImage()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getImage()
	// */
	// @Override
	// public BufferedImage getImage() {
	// return null;
	// }
	//
	// /**
	// * Method dispose()
	// * @see msi.gama.common.interfaces.IDisplaySurface#dispose()
	// */
	// @Override
	// public void dispose() {}
	//
	// private boolean isJavascriptInitialized = false;
	//
	// /**
	// * Method updateDisplay()
	// * @see msi.gama.common.interfaces.IDisplaySurface#updateDisplay()
	// */
	// @Override
	// public void updateDisplay(final boolean force) {
	// Logger.mlog(TAG, "updateDisplay");
	//
	// if ( isWebDisplayViewNotFound() ) {
	// Logger.mlog("Finding view...");
	// findWebDisplayView();
	// }
	//
	// if ( false == isJavascriptInitialized ) {
	// isJavascriptInitialized = true;
	//
	// if ( internalBrowser != null ) {
	// Logger.elog("NO ERROR HERE!");
	// testInternalBrowser();
	// } else {
	// Logger.elog("Internal Browser error!");
	// }
	// }
	// }
	//
	// private void testInternalBrowser() {
	//
	// SwtGui.getDisplay().syncExec(new Runnable() {
	//
	// static final String webUrl = "http://get.webgl.org/";
	//
	// @Override
	// public void run() {
	// try {
	// internalBrowser.setUrl(webUrl);
	// } catch (final Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	// });
	// }
	//
	// /**
	// * Method computeBoundsFrom()
	// * @see msi.gama.common.interfaces.IDisplaySurface#computeBoundsFrom(int, int)
	// */
	// @Override
	// public int[] computeBoundsFrom(final int width, final int height) {
	// return null;
	// }
	//
	// /**
	// * Method resizeImage()
	// * @see msi.gama.common.interfaces.IDisplaySurface#resizeImage(int, int)
	// */
	// @Override
	// public boolean resizeImage(final int width, final int height, final boolean force) {
	// return false;
	// }
	//
	// /**
	// * Method zoomIn()
	// * @see msi.gama.common.interfaces.IDisplaySurface#zoomIn()
	// */
	// @Override
	// public void zoomIn() {}
	//
	// /**
	// * Method zoomOut()
	// * @see msi.gama.common.interfaces.IDisplaySurface#zoomOut()
	// */
	// @Override
	// public void zoomOut() {}
	//
	// /**
	// * Method zoomFit()
	// * @see msi.gama.common.interfaces.IDisplaySurface#zoomFit()
	// */
	// @Override
	// public void zoomFit() {}
	//
	// /**
	// * Method getManager()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getManager()
	// */
	// @Override
	// public ILayerManager getManager() {
	// return new LayerManager(this);
	// }
	//
	// /**
	// * Method focusOn()
	// * @see msi.gama.common.interfaces.IDisplaySurface#focusOn(msi.gama.metamodel.shape.IShape, msi.gama.common.interfaces.ILayer)
	// */
	// @Override
	// public void focusOn(final IShape geometry) {
	// Logger.mlog(TAG, "focusOn event");
	// }
	//
	// /**
	// * Method canBeUpdated()
	// * @see msi.gama.common.interfaces.IDisplaySurface#canBeUpdated(boolean)
	// */
	// @Override
	// public void canBeUpdated(final boolean ok) {}
	//
	// /**
	// * Method setBackgroundColor()
	// * @see msi.gama.common.interfaces.IDisplaySurface#setBackgroundColor(java.awt.Color)
	// */
	// @Override
	// public void setBackground(final Color background) {}
	//
	// /**
	// * Method setQualityRendering()
	// * @see msi.gama.common.interfaces.IDisplaySurface#setQualityRendering(boolean)
	// */
	// @Override
	// public void setQualityRendering(final boolean quality) {}
	//
	// /**
	// * Method snapshot()
	// * @see msi.gama.common.interfaces.IDisplaySurface#snapshot()
	// */
	// @Override
	// public void snapshot() {}
	//
	// /**
	// * Method getWidth()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getWidth()
	// */
	// @Override
	// public int getWidth() {
	// return 0;
	// }
	//
	// /**
	// * Method getHeight()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getHeight()
	// */
	// @Override
	// public int getHeight() {
	// return 0;
	// }
	//
	// /**
	// * Method getImageWidth()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getImageWidth()
	// */
	// @Override
	// public int getImageWidth() {
	// return 0;
	// }
	//
	// /**
	// * Method getImageHeight()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getImageHeight()
	// */
	// @Override
	// public int getImageHeight() {
	// return 0;
	// }
	//
	// /**
	// * Method setOrigin()
	// * @see msi.gama.common.interfaces.IDisplaySurface#setOrigin(int, int)
	// */
	// @Override
	// public void setOrigin(final int i, final int j) {}
	//
	// /**
	// * Method getOriginX()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getOriginX()
	// */
	// @Override
	// public int getOriginX() {
	// return 0;
	// }
	//
	// /**
	// * Method getOriginY()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getOriginY()
	// */
	// @Override
	// public int getOriginY() {
	// return 0;
	// }
	//
	// /**
	// * Method initialize()
	// * @see msi.gama.common.interfaces.IDisplaySurface#initialize(double, double, msi.gama.outputs.LayeredDisplayOutput)
	// */
	// @Override
	// public void initialize(final IScope scope, final LayeredDisplayOutput output) {
	//
	// final String viewId = output.getViewId();
	// String viewName = output.getViewName();
	// Logger.mlog(TAG, "initialize", viewId, viewName);
	//
	// debugViewId();
	//
	// // SwtGui.getDisplay().syncExec(new Runnable() {
	// // @Override
	// // public void run() {
	// // try {
	// // IViewReference[] result = SwtGui.getPage()
	// // .getViewReferences();
	// //
	// // for (IViewReference view : result) {
	// // Logger.mlog(view.getId());
	// // }
	// // } catch (final Exception e) {
	// // e.printStackTrace();
	// // }
	// //
	// // }
	// // });
	//
	// // final IViewReference ref = SwtGui.getPage(
	// // SwtGui.PERSPECTIVE_SIMULATION_ID)
	// // .findViewReference(output.getViewId(),
	// // output.isUnique() ? null : output.getName());
	// // if (ref == null) {
	// // System.err.println("MinhVV get ViewReference null");
	// // return;
	// // }
	// // final IViewPart part = ref.getView(true);
	// // if (!(part instanceof IGamaView)) {
	// // System.out.println("MinhVV get view false");
	// // return;
	// // }
	// //
	// // final IGamaView view = (IGamaView) part;
	//
	// // WebDisplayView view = WebDisplayView.getWebDisplayView();
	// //
	// // if (null == view) {
	// // Logger.elog("getWebDisplayView null");
	// // }
	//
	// // WebDisplayView webDisplayView = WebDisplayView.this;
	// // Browser browser = (Browser) WebDisplayView.this.getComponent();
	// // if (null != browser) {
	// // browser.setUrl("google.com");
	// // } else {
	// // Logger.elog(TAG, "can not find browser");
	// // }
	//
	// }
	//
	// /**
	// * Method outputChanged()
	// * @see msi.gama.common.interfaces.IDisplaySurface#outputChanged(double, double, msi.gama.outputs.LayeredDisplayOutput)
	// */
	// @Override
	// public void outputChanged(final IScope scope, final LayeredDisplayOutput output) {}
	//
	// /**
	// * Method getHighlightColor()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getHighlightColor()
	// */
	// @Override
	// public int[] getHighlightColor() {
	// return null;
	// }
	//
	// /**
	// * Method setHighlightColor()
	// * @see msi.gama.common.interfaces.IDisplaySurface#setHighlightColor(int[])
	// */
	// @Override
	// public void setHighlightColor(final int[] rgb) {}
	//
	// /**
	// * Method addMouseListener()
	// * @see msi.gama.common.interfaces.IDisplaySurface#addMouseListener(java.awt.event.MouseListener)
	// */
	// @Override
	// public void addMouseListener(final MouseListener e) {}
	//
	// /**
	// * Method getEnvWidth()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getEnvWidth()
	// */
	// @Override
	// public double getEnvWidth() {
	// return 0;
	// }
	//
	// /**
	// * Method getEnvHeight()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getEnvHeight()
	// */
	// @Override
	// public double getEnvHeight() {
	// return 0;
	// }
	//
	// /**
	// * Method getDisplayWidth()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getDisplayWidth()
	// */
	// @Override
	// public int getDisplayWidth() {
	// return 0;
	// }
	//
	// /**
	// * Method getDisplayHeight()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getDisplayHeight()
	// */
	// @Override
	// public int getDisplayHeight() {
	// return 0;
	// }
	//
	// /**
	// * Method setZoomListener()
	// * @see msi.gama.common.interfaces.IDisplaySurface#setZoomListener(msi.gama.common.interfaces.IDisplaySurface.IZoomListener)
	// */
	// @Override
	// public void setZoomListener(final IZoomListener listener) {}
	//
	// /**
	// * Method getModelCoordinates()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getModelCoordinates()
	// */
	// @Override
	// public GamaPoint getModelCoordinates() {
	// return null;
	// }
	//
	// /**
	// * Method followAgent()
	// * @see msi.gama.common.interfaces.IDisplaySurface#followAgent(msi.gama.metamodel.agent.IAgent)
	// */
	// @Override
	// public void followAgent(final IAgent a) {}
	//
	// /**
	// * Method getZoomLevel()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getZoomLevel()
	// */
	// @Override
	// public double getZoomLevel() {
	// return 0;
	// }
	//
	// /**
	// * Method setSize()
	// * @see msi.gama.common.interfaces.IDisplaySurface#setSize(int, int)
	// */
	// @Override
	// public void setSize(final int x, final int y) {}
	//
	// /**
	// * Method getQualityRendering()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getQualityRendering()
	// */
	// @Override
	// public boolean getQualityRendering() {
	// return false;
	// }
	//
	// public void debugViewId() {
	// traceViews();
	//
	// final IWorkbenchPage page = SwtGui.getPage();
	// if ( page == null ) {
	// Logger.elog(TAG, "Page null");
	// return;
	// } // Closing the workbench
	// final IViewReference ref = page.findViewReference(WebDisplayView.ID);
	// if ( ref == null ) {
	// Logger.elog(TAG, "View Ref null");
	// return;
	// }
	// final IViewPart part = ref.getView(true);
	// if ( !(part instanceof IGamaView) ) {
	// Logger.elog(TAG, "Not view display");
	// return;
	// }
	// final IGamaView view = (IGamaView) part;
	// Logger.elog(TAG, "View OK");
	// }
	//
	// public void traceViews() {
	// SwtGui.getDisplay().syncExec(new Runnable() {
	//
	// @Override
	// public void run() {
	// try {
	// IViewReference[] result = SwtGui.getPage().getViewReferences();
	//
	// for ( IViewReference view : result ) {
	// Logger.mlog(view.getId());
	// }
	// } catch (final Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	// });
	// }
	//
	// private WebDisplayView webDisplayView = null;
	// private final Browser internalBrowser = null;
	//
	// private boolean isWebDisplayViewNotFound() {
	// return null == this.webDisplayView;
	// }
	//
	// public void findWebDisplayView() {
	// SwtGui.getDisplay().syncExec(new Runnable() {
	//
	// @Override
	// public void run() {
	// try {
	// IViewReference[] listViews = SwtGui.getPage().getViewReferences();
	//
	// for ( IViewReference viewRef : listViews ) {
	// if ( viewRef.getId().equals(WebDisplayView.ID) ) {
	// webDisplayView = (WebDisplayView) viewRef.getView(true);
	// // internalBrowser = (Browser) webDisplayView.getComponent();
	// Logger.mlog("Create browser OK grace a View found: ", viewRef.getId());
	// }
	// }
	// } catch (final Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	// });
	// }
	//
	// /**
	// * Method removeMouseListener()
	// * @see msi.gama.common.interfaces.IDisplaySurface#removeMouseListener(java.awt.event.MouseListener)
	// */
	// @Override
	// public void removeMouseListener(final MouseListener e) {}
	//
	// @Override
	// public GamaPoint getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final Point sizeInPixels,
	// final Point positionInPixels) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public IList<IAgent> selectAgent(final int x, final int y) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// /**
	// * Method getOutput()
	// * @see msi.gama.common.interfaces.IDisplaySurface#getOutput()
	// */
	// @Override
	// public IDisplayOutput getOutput() {
	// return null;
	// }
	//
	// /**
	// * Method waitForUpdateAndRun()
	// * @see msi.gama.common.interfaces.IDisplaySurface#waitForUpdateAndRun(java.lang.Runnable)
	// */
	// @Override
	// public void waitForUpdateAndRun(final Runnable r) {}
	//
}
