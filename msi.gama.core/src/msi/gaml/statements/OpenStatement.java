/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.statements;

import java.awt.Desktop;
import java.io.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

/**
 * opens a file with a system editor / viewer
 * 
 * TODO change the keywork "type" to something else
 * 
 * @author Samuel Thiriot
 * 
 */
@symbol(name = IKeyword.OPEN, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, with_args = true, remote_context = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.ACTION })
@facets(value = { @facet(name = IKeyword.FILE, type = IType.STRING, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.ID, optional = true) }, omissible = IKeyword.FILE)
public class OpenStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	private Arguments init;

	public OpenStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {

		// retrieve the file to export
		final IExpression file = getFacet(IKeyword.FILE);

		String path = "";
		if ( file == null ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}

		path =
			scope.getSimulationScope().getModel().getRelativeFilePath(Cast.asString(scope, file.value(scope)), false);
		if ( path.equals("") ) {
			// scope.setStatus(ExecutionStatus.failure);
			return null;
		}

		// retrieve the optional "with" facet*
		final IExpression item = getFacet(IKeyword.TYPE);
		String withParam = null;
		if ( item != null ) {
			System.err.println("attempting to cast " + item);
			withParam = Cast.asString(scope, item.value(scope));
			// getLiteral(IKeyword.WITH);
		}
		if ( withParam == null ) {
			System.err.println("no program parameter, will open default editor");
			openFilename(scope, path);
		} else {
			System.err.println("with program: " + withParam + " " + path);
			openFilenameWithProgram(scope, path, withParam);
		}

		return null;
	}

	protected void openFilenameWithProgram(final IScope scope, final String filename, final String program) {

		try {
			final ProcessBuilder pb = new ProcessBuilder(program, filename);
			final Process p = pb.start();

			// Runtime.getRuntime().exec(program+" "+filename);
		} catch (final IOException e) {
			e.printStackTrace();
			throw GamaRuntimeException.error("error while attempting to open " + filename + " with program " + program +
				" (" + e.getMessage() + ")");
		}

	}

	/**
	 * Opens this filename with the default system editor.
	 * Attempts to use several methods: SWT, then Java awt Desktop.
	 * May fail on some platforms.
	 * Dies with a GamaException.
	 * @param scope
	 * @param filename
	 */
	protected void openFilename(final IScope scope, final String filename) {
		// for details: see
		// http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fswt%2Fprogram%2FProgram.html
		// http://stackoverflow.com/questions/526037/how-to-open-user-system-preferred-editor-for-given-file
		// http://www.rgagnon.com/javadetails/java-0014.html

		boolean opened = false;

		// open file
		final File f = new File(scope.getSimulationScope().getModel().getRelativeFilePath(filename, true));
		if ( !f.exists() ) { throw GamaRuntimeException.error("unable to open this file, which does not exists: " +
			filename); }
		if ( !f.canRead() ) { throw GamaRuntimeException.error("unable to open this file, which is not readable: " +
			filename); }

		if ( !f.isDirectory() && Display.getDefault() != null ) {

			// second first: open with SWT utils. Not working for directories. Provides a feedback on the result
			opened = true;
			Display.getDefault().syncExec(new Runnable() {

				@Override
				public void run() {
					Program.launch(f.getAbsolutePath());

				}
			});

		}

		if ( !opened && Desktop.isDesktopSupported() && Desktop.getDesktop() != null ) {
			// first option: recommanded solution in Java. Not supported everywhere, though.

			try {
				Desktop.getDesktop().open(f);
				opened = true;
			} catch (final IOException e) {
				e.printStackTrace();
				throw GamaRuntimeException.error("error while opening " + filename + " : " + e.getMessage());

			}

		}

		if ( !opened ) { throw GamaRuntimeException.error("Unable to open the file in this operating system " +
			filename); }

		// this solution is not multiplatform; let's avoid this kind of dangerous cooking ?
		// Process p = Runtime.getRuntime().exec("open "+f.getAbsolutePath());

	}

	@Override
	public void setFormalArgs(final Arguments args) {
		init = args;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {
		// TODO Auto-generated method stub
	}

}
