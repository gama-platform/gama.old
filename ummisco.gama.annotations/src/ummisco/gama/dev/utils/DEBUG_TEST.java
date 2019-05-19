package ummisco.gama.dev.utils;

public class DEBUG_TEST {

	public static void main(String[] args) {
		new DEBUG_TEST().run();
	}

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
