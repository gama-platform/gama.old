/*******************************************************************************************************
 *
 * DPIHelper.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.utils;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.widgets.Monitor;

/**
 * The Class DPIHelper.
 */
public class DPIHelper {

	/**
	 * Checks if is hi DPI.
	 *
	 * @return true, if is hi DPI
	 */
	public static boolean isHiDPI(final Monitor monitor) {
		final int deviceZoom = monitor == null ? DPIUtil.getDeviceZoom() : monitor.getZoom();

		return deviceZoom > 100;
	}

	/**
	 * Gets the device zoom.
	 *
	 * @return the device zoom
	 */
	public static int getDeviceZoom(final Monitor monitor) {
		return monitor == null ? DPIUtil.getDeviceZoom() : monitor.getZoom();

	}

	/**
	 * Returns SWT auto scaled-up value {@code v}, compatible with {@link DPIUtil#autoScaleUp(int)}
	 * <p>
	 * We need to keep track of SWT's implementation in this regard!
	 * </p>
	 */
	public static int autoScaleUp(final Monitor monitor, final int v) {
		// Temp !
		// if (true) return v;
		final int deviceZoom = monitor == null ? DPIUtil.getDeviceZoom() : monitor.getZoom();
		if (100 == deviceZoom || DPIUtil.useCairoAutoScale()) return v;
		final float scaleFactor = deviceZoom / 100f;
		return Math.round(v * scaleFactor);
	}

	/**
	 * Auto scale up.
	 *
	 * @param monitor
	 *
	 * @param v
	 *            the v
	 * @return the double
	 */
	public static double autoScaleUp(final Monitor monitor, final double v) {
		final int deviceZoom = monitor == null ? DPIUtil.getDeviceZoom() : monitor.getZoom();
		if (100 == deviceZoom || DPIUtil.useCairoAutoScale()) return v;
		final double scaleFactor = deviceZoom / 100d;
		return v * scaleFactor;
	}

	/**
	 * Returns SWT auto scaled-down value {@code v}, compatible with {@link DPIUtil#autoScaleDown(int)}
	 * <p>
	 * We need to keep track of SWT's implementation in this regard!
	 * </p>
	 */
	public static int autoScaleDown(final Monitor monitor, final int v) {
		// Temp !
		// if (true) return v;
		final int deviceZoom = monitor == null ? DPIUtil.getDeviceZoom() : monitor.getZoom();
		if (100 == deviceZoom || DPIUtil.useCairoAutoScale()) return v;
		final float scaleFactor = deviceZoom / 100f;
		return Math.round(v / scaleFactor);
	}

	/**
	 * Returns a new scaled up Rectangle.
	 */
	public static Rectangle autoScaleUp(final Monitor monitor, final Rectangle rect) {
		final int deviceZoom = monitor == null ? DPIUtil.getDeviceZoom() : monitor.getZoom();
		if (deviceZoom == 100 || rect == null) return rect;
		Rectangle scaledRect = new Rectangle(0, 0, 0, 0);
		Point scaledTopLeft = autoScaleUp(monitor, new Point(rect.x, rect.y));
		Point scaledBottomRight = autoScaleUp(monitor, new Point(rect.x + rect.width, rect.y + rect.height));
		scaledRect.x = scaledTopLeft.x;
		scaledRect.y = scaledTopLeft.y;
		scaledRect.width = scaledBottomRight.x - scaledTopLeft.x;
		scaledRect.height = scaledBottomRight.y - scaledTopLeft.y;
		return scaledRect;
	}

	/**
	 * Auto scale up.
	 *
	 * @param point
	 *            the point
	 * @return the point
	 */
	public static Point autoScaleUp(final Monitor monitor, final Point point) {
		final int deviceZoom = monitor == null ? DPIUtil.getDeviceZoom() : monitor.getZoom();
		if (deviceZoom == 100 || point == null) return point;
		final float scaleFactor = deviceZoom / 100f;
		Point scaledPoint = new Point(0, 0);
		scaledPoint.x = Math.round(point.x * scaleFactor);
		scaledPoint.y = Math.round(point.y * scaleFactor);
		return scaledPoint;
	}

	/**
	 * Auto scale down.
	 *
	 * @param v
	 *            the v
	 * @return the double
	 */
	public static double autoScaleDown(final Monitor monitor, final double v) {
		// Temp !
		// if (true) return v;
		final int deviceZoom = monitor == null ? DPIUtil.getDeviceZoom() : monitor.getZoom();
		if (100 == deviceZoom || DPIUtil.useCairoAutoScale()) return v;
		final double scaleFactor = deviceZoom / 100d;
		return v / scaleFactor;
	}

}
