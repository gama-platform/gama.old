/*********************************************************************************************
 *
 * 'rtest.java, in plugin ummisco.gaml.extensions.rjava, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.rjava.skill;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;

class TextConsole implements RMainLoopCallbacks {
	@Override
	public void rWriteConsole(final Rengine re, final String text, final int oType) {
		System.out.print(text);
	}

	@Override
	public void rBusy(final Rengine re, final int which) {
		System.out.println("rBusy(" + which + ")");
	}

	@Override
	public String rReadConsole(final Rengine re, final String prompt, final int addToHistory) {
		System.out.print(prompt);
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			final String s = br.readLine();
			return s == null || s.length() == 0 ? s : s + "\n";
		} catch (final Exception e) {
			System.out.println("jriReadConsole exception: " + e.getMessage());
		}
		return null;
	}

	@Override
	public void rShowMessage(final Rengine re, final String message) {
		System.out.println("rShowMessage \"" + message + "\"");
	}

	@Override
	public String rChooseFile(final Rengine re, final int newFile) {
		final FileDialog fd = new FileDialog(new Frame(), newFile == 0 ? "Select a file" : "Select a new file",
				newFile == 0 ? FileDialog.LOAD : FileDialog.SAVE);
		fd.setVisible(true);
		String res = null;
		if (fd.getDirectory() != null)
			res = fd.getDirectory();
		if (fd.getFile() != null)
			res = res == null ? fd.getFile() : res + fd.getFile();
		return res;
	}

	@Override
	public void rFlushConsole(final Rengine re) {
	}

	@Override
	public void rLoadHistory(final Rengine re, final String filename) {
	}

	@Override
	public void rSaveHistory(final Rengine re, final String filename) {
	}

	public long rExecJCommand(final Rengine re, final String commandId, final long argsExpr, final int options) {
		System.out.println("rExecJCommand \"" + commandId + "\"");
		return 0;
	}

	public void rProcessJEvents(final Rengine re) {
	}

}

public class rtest {
	@SuppressWarnings("rawtypes")
	public static void main(final String[] args) {
		// just making sure we have the right version of everything
		if (!Rengine.versionCheck()) {
			System.err.println("** Version mismatch - Java files don't match library version.");
			System.exit(1);
		}
		System.out.println("Creating Rengine (with arguments)");
		// 1) we pass the arguments from the command line
		// 2) we won't use the main loop at first, we'll start it later
		// (that's the "false" as second argument)
		// 3) the callbacks are implemented by the TextConsole class above
		final Rengine re = new Rengine(args, false, new TextConsole());
		System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's
		// ready
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}

		/*
		 * High-level API - do not use RNI methods unless there is no other way
		 * to accomplish what you want
		 */
		try {
			REXP x;
			re.eval("data(iris)", false);
			System.out.println(x = re.eval("iris"));
			// generic vectors are RVector to accomodate names
			final RVector v = x.asVector();
			if (v.getNames() != null) {
				System.out.println("has names:");
				for (final Enumeration e = v.getNames().elements(); e.hasMoreElements();) {
					System.out.println(e.nextElement());
				}
			}
			// for compatibility with Rserve we allow casting of vectors to
			// lists
			final RList vl = x.asList();
			final String[] k = vl.keys();
			if (k != null) {
				System.out.println("and once again from the list:");
				int i = 0;
				while (i < k.length)
					System.out.println(k[i++]);
			}

			// get boolean array
			System.out.println(x = re.eval("iris[[1]]>mean(iris[[1]])"));
			// R knows about TRUE/FALSE/NA, so we cannot use boolean[] this way
			// instead, we use int[] which is more convenient (and what R uses
			// internally anyway)
			final int[] bi = x.asIntArray();
			{
				int i = 0;
				while (i < bi.length) {
					System.out.print(bi[i] == 0 ? "F " : bi[i] == 1 ? "T " : "NA ");
					i++;
				}
				System.out.println("");
			}

			// push a boolean array
			final boolean by[] = { true, false, false };
			re.assign("bool", by);
			System.out.println(x = re.eval("bool"));
			// asBool returns the first element of the array as RBool
			// (mostly useful for boolean arrays of the length 1). is should
			// return true
			System.out.println("isTRUE? " + x.asBool().isTRUE());

			// now for a real dotted-pair list:
			System.out.println(x = re.eval("pairlist(a=1,b='foo',c=1:5)"));
			final RList l = x.asList();
			if (l != null) {
				int i = 0;
				final String[] a = l.keys();
				System.out.println("Keys:");
				while (i < a.length)
					System.out.println(a[i++]);
				System.out.println("Contents:");
				i = 0;
				while (i < a.length)
					System.out.println(l.at(i++));
			}
			System.out.println(re.eval("sqrt(36)"));
		} catch (final Exception e) {
			System.out.println("EX:" + e);
			e.printStackTrace();
		}

		// Part 2 - low-level API - for illustration purposes only!
		// System.exit(0);

		// simple assignment like a<-"hello" (env=0 means use R_GlobalEnv)
		final long xp1 = re.rniPutString("hello");
		re.rniAssign("a", xp1, 0);

		// Example: how to create a named list or data.frame
		final double da[] = { 1.2, 2.3, 4.5 };
		final double db[] = { 1.4, 2.6, 4.2 };
		final long xp3 = re.rniPutDoubleArray(da);
		final long xp4 = re.rniPutDoubleArray(db);

		// now build a list (generic vector is how that's called in R)
		final long la[] = { xp3, xp4 };
		final long xp5 = re.rniPutVector(la);

		// now let's add names
		final String sa[] = { "a", "b" };
		final long xp2 = re.rniPutStringArray(sa);
		re.rniSetAttr(xp5, "names", xp2);

		// ok, we have a proper list now
		// we could use assign and then eval "b<-data.frame(b)", but for now
		// let's build it by hand:
		final String rn[] = { "1", "2", "3" };
		final long xp7 = re.rniPutStringArray(rn);
		re.rniSetAttr(xp5, "row.names", xp7);

		final long xp6 = re.rniPutString("data.frame");
		re.rniSetAttr(xp5, "class", xp6);

		// assign the whole thing to the "b" variable
		re.rniAssign("b", xp5, 0);

		{
			System.out.println("Parsing");
			final long e = re.rniParse("data(iris)", 1);
			System.out.println("Result = " + e + ", running eval");
			final long r = re.rniEval(e, 0);
			System.out.println("Result = " + r + ", building REXP");
			final REXP x = new REXP(re, r);
			System.out.println("REXP result = " + x);
		}
		{
			System.out.println("Parsing");
			final long e = re.rniParse("iris", 1);
			System.out.println("Result = " + e + ", running eval");
			final long r = re.rniEval(e, 0);
			System.out.println("Result = " + r + ", building REXP");
			final REXP x = new REXP(re, r);
			System.out.println("REXP result = " + x);
		}
		{
			System.out.println("Parsing");
			final long e = re.rniParse("names(iris)", 1);
			System.out.println("Result = " + e + ", running eval");
			final long r = re.rniEval(e, 0);
			System.out.println("Result = " + r + ", building REXP");
			final REXP x = new REXP(re, r);
			System.out.println("REXP result = " + x);
			final String s[] = x.asStringArray();
			if (s != null) {
				int i = 0;
				while (i < s.length) {
					System.out.println("[" + i + "] \"" + s[i] + "\"");
					i++;
				}
			}
		}
		{
			System.out.println("Parsing");
			final long e = re.rniParse("rnorm(10)", 1);
			System.out.println("Result = " + e + ", running eval");
			final long r = re.rniEval(e, 0);
			System.out.println("Result = " + r + ", building REXP");
			final REXP x = new REXP(re, r);
			System.out.println("REXP result = " + x);
			final double d[] = x.asDoubleArray();
			if (d != null) {
				int i = 0;
				while (i < d.length) {
					System.out.print((i == 0 ? "" : ", ") + d[i]);
					i++;
				}
				System.out.println("");
			}
			System.out.println("");
		}
		{
			final REXP x = re.eval("1:10");
			System.out.println("REXP result = " + x);
			final int d[] = x.asIntArray();
			if (d != null) {
				int i = 0;
				while (i < d.length) {
					System.out.print((i == 0 ? "" : ", ") + d[i]);
					i++;
				}
				System.out.println("");
			}
		}

		re.eval("print(1:10/3)");

		if (true) {
			// so far we used R as a computational slave without REPL
			// now we start the loop, so the user can use the console
			System.out.println("Now the console is yours ... have fun");
			re.startMainLoop();
		} else {
			re.end();
			System.out.println("end");
		}
	}
}
