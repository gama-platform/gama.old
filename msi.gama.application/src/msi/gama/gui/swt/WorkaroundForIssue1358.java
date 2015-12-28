/**
 * Created by drogoul, 29 déc. 2015
 *
 */
package msi.gama.gui.swt;

import java.lang.Thread.UncaughtExceptionHandler;
import msi.gama.gui.swt.swing.Platform;

/**
 * Class WorkaroundForIssue1358. Only for MacOS X -- to be removed when Eclipse 4.5.2 will be out.
 *
 * @author drogoul
 * @since 29 déc. 2015
 *
 */
public class WorkaroundForIssue1358 {

	public static void install() {
		if ( Platform.isCocoa() ) {
			Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

				@Override
				public void uncaughtException(final Thread t, final Throwable e) {
					if ( e instanceof NullPointerException ) {
						StackTraceElement[] traces = e.getStackTrace();
						for ( StackTraceElement s : traces ) {
							if ( s.getMethodName().contains("internal_new_GC") ) {
								System.out.println(
									"Harmless exception caught in Control.internal_new_GC() -- waiting from the fix in Eclipse 4.5.2");
								return;
							}
						}
					}
					e.printStackTrace();
				}
			});
		}
	}
}
