/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.commands;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import msi.gama.gui.application.GUI;
import org.eclipse.core.commands.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.IProgressService;

public abstract class ConvertHandler extends AbstractHandler {

	private IWorkbenchPage activePage;
	private IWorkbenchWindow activeWindow;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		activePage = activeWindow.getActivePage();
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		try {
			service.runInUI(activeWindow, new IRunnableWithProgress() {

				@Override
				public void run(final IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
					monitor.beginTask("Converting", 3);
					try {
						IFile file = convert(monitor);
						monitor.worked(1);
						monitor.setTaskName("Opening the new file");
						IDE.openEditor(activePage, file);
						monitor.done();
					} catch (Exception e) {
						throw new InvocationTargetException(e, e.getMessage());
					}
				}
			}, null);
		} catch (Exception e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		return null;
	}

	private IFile convert(final IProgressMonitor monitor) throws Exception {
		IFile oFile = getFilePath();

		IPath oPath = oFile.getFullPath();
		IFile nFile =
			ResourcesPlugin.getWorkspace().getRoot().getFile(oPath.addFileExtension(getNewExt()));

		if ( nFile.exists() ) {
			// add date to the end
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.");
			String date = sdf.format(Calendar.getInstance().getTime());
			nFile =
				ResourcesPlugin.getWorkspace().getRoot()
					.getFile(oPath.addFileExtension(date + getNewExt()));
			if ( nFile.exists() ) { throw new Exception("file exists: " + oFile.getFullPath()); }
		}
		GUI.info("writing: " + nFile.getFullPath());
		monitor.worked(1);
		monitor.setTaskName("Writing: " + nFile.getFullPath());
		// InputStream is = getConvertedInputStream(oFile); try {
		nFile.create(getConvertedInputStream(oFile), true, monitor);
		// } finally { is.close(); }
		return nFile;
	}

	private IFile getFilePath() {
		/* Get the selection */
		ISelection selection = activeWindow.getSelectionService().getSelection();
		IFile fileToRun = null;

		/* If run is asked from the editor context menu */
		if ( selection instanceof TextSelection ) {
			fileToRun =
				((IFileEditorInput) activePage.getActiveEditor().getEditorInput()).getFile();
		}
		/* Run is asked from the navigator context menu */
		else {
			fileToRun = (IFile) ((IStructuredSelection) selection).getFirstElement();
		}
		return fileToRun;
	}

	/**
	 * get the result of the conversion in an InputStream
	 * @param source the source file to convert
	 * @return the result of the conversion in an InputStream
	 * @see ByteArrayInputStream#ByteArrayInputStream(byte[])
	 * @see String#getBytes()
	 */
	protected abstract InputStream getConvertedInputStream(IFile source);

	protected abstract String getNewExt();
}
