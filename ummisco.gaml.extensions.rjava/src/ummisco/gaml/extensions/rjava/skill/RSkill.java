/*********************************************************************************************
 * 
 * 
 * 'SimpleBdiArchitecture.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gaml.extensions.rjava.skill;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@skill(name = "RSkill", concept = { IConcept.STATISTIC, IConcept.SKILL })
public class RSkill extends Skill {

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

	private String[] args;
	private final Rengine re = new Rengine(args, false, new TextConsole());

	@action(name = "R_eval", args = {
			@arg(name = "command", type = IType.STRING, optional = true, doc = @doc("R command to be evalutated")) }, doc = @doc(value = "evaluate the R command", returns = "object in R.", examples = {
					@example(" R_eval(\"data(iris)\")") }))
	public String primREval(final IScope scope) throws GamaRuntimeException {
		// re = new Rengine(args, false, new TextConsole());

		final REXP x = re.eval((String) scope.getArg("command", IType.STRING));

		// TODO remove intension aussi
		return x.toString();
	}

}
