/*******************************************************************************************************
 *
 * DEBUG_TEST.java, in ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.dev.utils;

/**
 * The Class DEBUG_TEST.
 */
public class DEBUG_TEST {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		new DEBUG_TEST().run();
	}

	/**
	 * Run.
	 */
	public void run() {

		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			DEBUG.findCallingClassName();
		}
		DEBUG.LOG("Security manager caller: " + (System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			DEBUG.findCallingClassNameOld();
		}
		DEBUG.LOG("Stack trace caller: " + (System.currentTimeMillis() - start) + "ms");

	}
}
