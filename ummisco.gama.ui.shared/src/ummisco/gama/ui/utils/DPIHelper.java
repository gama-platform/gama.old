/*******************************************************************************************************
 *
 * DPIHelper.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.utils;

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
