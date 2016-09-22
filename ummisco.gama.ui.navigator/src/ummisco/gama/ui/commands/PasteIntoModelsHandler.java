package ummisco.gama.ui.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CopyFilesAndFoldersOperation;

import msi.gama.application.workspace.WorkspaceModelsManager;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class PasteIntoModelsHandler extends AbstractHandler {

	public static void handlePaste(final String[] selection) {
		for (final String name : selection) {
			final File f = new File(name);
			IContainer container;
			if (f.isDirectory()) {
				try {
					if (WorkspaceModelsManager.instance.isGamaProject(f)) {
						container = WorkspaceModelsManager.createOrUpdateProject(f.getName());
						final CopyFilesAndFoldersOperation op = new CopyFilesAndFoldersOperation(
								WorkbenchHelper.getShell());
						op.setVirtualFolders(false);
						final List<File> files = Arrays.asList(f.listFiles());
						final List<String> names = new ArrayList();
						for (final File toCopy : files) {
							if (toCopy.getName().equals(".project"))
								continue;
							names.add(toCopy.getAbsolutePath());
						}
						op.copyFiles(names.toArray(new String[0]), container);
					} else {
						container = WorkspaceModelsManager.instance.createUnclassifiedModelsProject(new Path(name));
						final CopyFilesAndFoldersOperation op = new CopyFilesAndFoldersOperation(
								WorkbenchHelper.getShell());
						op.setVirtualFolders(false);
						op.copyFiles(new String[] { name }, container);
					}
				} catch (final CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					container = WorkspaceModelsManager.instance.createUnclassifiedModelsProject(new Path(name));
					final CopyFilesAndFoldersOperation op = new CopyFilesAndFoldersOperation(
							WorkbenchHelper.getShell());
					op.setVirtualFolders(false);
					op.copyFiles(new String[] { name }, container);
				} catch (final CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static void handlePaste() {
		final Clipboard clipBoard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
		final FileTransfer transfer = FileTransfer.getInstance();
		final String[] selection = (String[]) clipBoard.getContents(transfer);
		if (selection != null && selection.length != 0)
			handlePaste(selection);
		clipBoard.dispose();
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Clipboard clipBoard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
		final FileTransfer transfer = FileTransfer.getInstance();
		final String[] selection = (String[]) clipBoard.getContents(transfer);
		handlePaste(selection);
		return this;
	}

}
