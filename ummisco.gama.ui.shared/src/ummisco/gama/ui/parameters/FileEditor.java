/*******************************************************************************************************
 *
 * FileEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.NewFolderDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

import msi.gama.common.util.FileUtils;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFolderFile;
import msi.gama.util.file.IGamaFile;
import msi.gaml.operators.Files;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import one.util.streamex.StreamEx;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class FileEditor.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class FileEditor extends AbstractEditor<IGamaFile> {

	/** The file extensions. */
	private final Set<String> fileExtensions;

	/** The text box. */
	private FlatButton textBox;

	/** The is folder. */
	private final boolean isFolder;

	/** The is workspace. */
	private final boolean isWorkspace;

	/**
	 * Instantiates a new file editor.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 * @param param
	 *            the param
	 * @param l
	 *            the l
	 * @param isFolder
	 *            the is folder
	 */
	FileEditor(final IAgent agent, final IParameter param, final EditorListener l, final boolean isFolder) {
		super(agent, param, l);
		this.isFolder = isFolder;
		this.isWorkspace = param != null && param.isWorkspace();
		this.fileExtensions =
				param == null ? null : param.getFileExtensions() == null || param.getFileExtensions().length == 0 ? null
						: new HashSet(Arrays.asList(param.getFileExtensions()));
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		textBox = FlatButton.menu(comp, null, "").light().small();
		textBox.setText("No " + (isFolder ? "folder" : "file"));
		textBox.addSelectionListener(this);
		return textBox;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		IGamaFile file = currentValue;
		if (isWorkspace) {
			if (!isFolder) {
				IFile result = WorkspaceResourceDialog.openFileSelection(null, "Choose file",
						"Choose a file for parameter '" + param.getTitle() + "'", false,
						file == null ? null : new IFile[] { FileUtils.getFile(file.getPath(getScope()), null, true) },
						Arrays.asList(new ViewerFilter() {

							@Override
							public boolean select(final Viewer viewer, final Object parentElement,
									final Object element) {
								IResource r = (IResource) element;
								return r instanceof IContainer || fileExtensions == null
										|| fileExtensions != null && fileExtensions.contains(r.getFileExtension());
							}
						}));
				if (result == null) return;
				String path = FileUtils.constructAbsoluteFilePath(getScope(), result.getLocation().toOSString(), true);
				if (path != null) { file = Files.from(getScope(), path); }
			} else {
				IContainer result = WorkspaceResourceDialog.openFolderSelection(null, "Choose folder",
						"Choose a folder for parameter '" + param.getTitle() + "'", false,
						file == null ? null
								: new IContainer[] { FileUtils.getFolder(file.getPath(getScope()), null, true) },
						Arrays.asList(new ViewerFilter() {

							@Override
							public boolean select(final Viewer viewer, final Object parentElement,
									final Object element) {
								IResource r = (IResource) element;
								return r instanceof IContainer || fileExtensions == null
										|| fileExtensions != null && fileExtensions.contains(r.getFileExtension());
							}
						}));
				if (result == null) return;
				String path = FileUtils.constructAbsoluteFilePath(getScope(), result.getLocation().toOSString(), true);
				if (path != null) { file = Files.from(getScope(), path); }
			}
		} else if (isFolder) {
			final DirectoryDialog dialog = new DirectoryDialog(WorkbenchHelper.getDisplay().getActiveShell(), SWT.NULL);
			if (!(file instanceof GamaFolderFile)) { file = null; }
			dialog.setFilterPath(file != null ? file.getPath(getScope())
					: GAMA.getModel() == null ? ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString()
					: GAMA.getModel().getFilePath());
			dialog.setText("Choose a folder for parameter '" + param.getTitle() + "'");
			final String path = dialog.open();
			if (path != null) { file = Files.folderFile(getScope(), path, false); }
		} else {
			final FileDialog dialog = new FileDialog(WorkbenchHelper.getDisplay().getActiveShell(), SWT.NULL);
			dialog.setFilterPath(file != null ? file.getPath(getScope())
					: GAMA.getModel().getFilePath().isBlank()
							? ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString()
					: GAMA.getModel().getFilePath());
			dialog.setFileName(file != null ? file.getPath(getScope())
					: GAMA.getModel().getFilePath().isBlank()
							? ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString()
					: GAMA.getModel().getFilePath());
			dialog.setText("Choose a file for parameter '" + param.getTitle() + "'");
			if (fileExtensions != null) {
				dialog.setFilterExtensions(StreamEx.of(fileExtensions).map(s -> ("*." + s)).toArray(String.class));
			}
			final String path = dialog.open();
			if (path != null) { file = Files.from(getScope(), path); }
		}
		modifyAndDisplayValue(file);
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		if (currentValue == null) {
			textBox.setText("No " + (isFolder ? "folder" : "file"));
		} else {
			final IGamaFile file = currentValue;
			String path;
			try {
				path = file.getPath(getScope());
			} catch (final GamaRuntimeException e) {
				path = file.getOriginalPath();
			}

			textBox.setToolTipText(path);
			textBox.setText(path);
		}
		internalModification = false;
	}

	@Override
	public IType getExpectedType() { return Types.FILE; }

	@Override
	protected void applyEdit() {
		widgetSelected(null);
	}

	@Override
	protected int[] getToolItems() { return new int[] { EDIT, REVERT }; }

	/**
	 * The Class WorkspaceResourceDialog.
	 */
	static public class WorkspaceResourceDialog extends ElementTreeSelectionDialog
			implements ISelectionStatusValidator {

		/**
		 * Open folder selection.
		 *
		 * @param parent
		 *            the parent
		 * @param title
		 *            the title
		 * @param message
		 *            the message
		 * @param allowMultipleSelection
		 *            the allow multiple selection
		 * @param initialSelection
		 *            the initial selection
		 * @param viewerFilters
		 *            the viewer filters
		 * @return the i container[]
		 */
		public static IContainer openFolderSelection(final Shell parent, final String title, final String message,
				final boolean allowMultipleSelection, final Object[] initialSelection,
				final List<ViewerFilter> viewerFilters) {
			WorkspaceResourceDialog dialog =
					new WorkspaceResourceDialog(parent, new WorkbenchLabelProvider(), new WorkbenchContentProvider());
			dialog.setAllowMultiple(allowMultipleSelection);
			dialog.setTitle(title != null ? title : "Choose folder");
			dialog.setMessage(message);
			dialog.showNewFolderControl = true;
			dialog.addFilter(dialog.createDefaultViewerFilter(false));
			if (viewerFilters != null) {
				for (ViewerFilter viewerFilter : viewerFilters) { dialog.addFilter(viewerFilter); }
			}
			if (initialSelection != null) { dialog.setInitialSelections(initialSelection); }
			dialog.loadContents();
			return dialog.open() == Window.OK ? dialog.getSelectedContainer() : null;
		}

		/**
		 * Open file selection.
		 *
		 * @param parent
		 *            the parent
		 * @param title
		 *            the title
		 * @param message
		 *            the message
		 * @param allowMultipleSelection
		 *            the allow multiple selection
		 * @param initialSelection
		 *            the initial selection
		 * @param viewerFilters
		 *            the viewer filters
		 * @return the i file[]
		 */
		public static IFile openFileSelection(final Shell parent, final String title, final String message,
				final boolean allowMultipleSelection, final Object[] initialSelection,
				final List<ViewerFilter> viewerFilters) {
			WorkspaceResourceDialog dialog =
					new WorkspaceResourceDialog(parent, new WorkbenchLabelProvider(), new WorkbenchContentProvider());
			dialog.setAllowMultiple(allowMultipleSelection);
			dialog.setTitle(title != null ? title : "Choose file");
			dialog.setMessage(message);

			dialog.addFilter(dialog.createDefaultViewerFilter(true));
			if (viewerFilters != null) {
				for (ViewerFilter viewerFilter : viewerFilters) { dialog.addFilter(viewerFilter); }
			}

			if (initialSelection != null) { dialog.setInitialSelections(initialSelection); }

			dialog.loadContents();
			return dialog.open() == Window.OK ? dialog.getSelectedFile() : null;
		}

		/** The show new folder control. */
		protected boolean showNewFolderControl = false;

		/** The show file control. */
		protected boolean showFileControl = false;

		/** The show files. */
		protected boolean showFiles = true;

		/** The new folder button. */
		protected Button newFolderButton;

		/** The file text. */
		protected Text fileText;

		/** The file text content. */
		protected String fileTextContent = "";

		/** The selected container. */
		protected IContainer selectedContainer;

		/**
		 * Instantiates a new workspace resource dialog.
		 *
		 * @param parent
		 *            the parent
		 * @param labelProvider
		 *            the label provider
		 * @param contentProvider
		 *            the content provider
		 */
		public WorkspaceResourceDialog(final Shell parent, final ILabelProvider labelProvider,
				final ITreeContentProvider contentProvider) {
			super(parent, labelProvider, contentProvider);
			setComparator(new ResourceComparator(ResourceComparator.NAME));
			setValidator(this);
		}

		/**
		 * Load contents.
		 */
		public void loadContents() {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			setInput(root);
		}

		/**
		 * Creates the default viewer filter.
		 *
		 * @param showFiles
		 *            the show files
		 * @return the viewer filter
		 */
		public ViewerFilter createDefaultViewerFilter(final boolean showFiles) {
			this.showFiles = showFiles;
			return new ViewerFilter() {
				@Override
				public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
					if (element instanceof IResource workspaceResource)
						return workspaceResource.isAccessible() && (WorkspaceResourceDialog.this.showFiles
								|| workspaceResource.getType() != IResource.FILE);
					return false;
				}
			};
		}

		@Override
		protected Control createDialogArea(final Composite parent) {
			Composite composite = (Composite) super.createDialogArea(parent);

			if (showNewFolderControl) { createNewFolderControl(composite); }
			if (showFileControl) { createFileControl(composite); }

			applyDialogFont(composite);
			return composite;
		}

		/**
		 * Creates the new folder control.
		 *
		 * @param parent
		 *            the parent
		 */
		protected void createNewFolderControl(final Composite parent) {
			newFolderButton = new Button(parent, SWT.PUSH);
			newFolderButton.setText("New Folder");
			newFolderButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					newFolderButtonPressed();
				}
			});
			newFolderButton.setFont(parent.getFont());
			updateNewFolderButtonState();
		}

		/**
		 * Update new folder button state.
		 */
		protected void updateNewFolderButtonState() {
			IStructuredSelection selection = (IStructuredSelection) getTreeViewer().getSelection();
			selectedContainer = null;
			if (selection.size() == 1) {
				Object first = selection.getFirstElement();
				if (first instanceof IContainer) { selectedContainer = (IContainer) first; }
			}
			newFolderButton.setEnabled(selectedContainer != null);
		}

		/**
		 * New folder button pressed.
		 */
		protected void newFolderButtonPressed() {
			NewFolderDialog dialog = new NewFolderDialog(getShell(), selectedContainer);
			if (dialog.open() == Window.OK) {
				TreeViewer treeViewer = getTreeViewer();
				treeViewer.refresh(selectedContainer);
				Object createdFolder = dialog.getResult()[0];
				treeViewer.reveal(createdFolder);
				treeViewer.setSelection(new StructuredSelection(createdFolder));
			}
		}

		/**
		 * Creates the file control.
		 *
		 * @param parent
		 *            the parent
		 */
		protected void createFileControl(final Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			{
				GridLayout layout = new GridLayout(2, false);
				layout.marginLeft = -5;
				layout.marginRight = -5;
				layout.marginTop = -5;
				layout.marginBottom = -5;
				composite.setLayout(layout);
			}

			Label fileLabel = new Label(composite, SWT.NONE);
			fileLabel.setText("File name");

			fileText = new Text(composite, SWT.BORDER);
			fileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fileText.addModifyListener(e -> fileTextModified(fileText.getText()));

			if (fileTextContent != null) { fileText.setText(fileTextContent); }
		}

		/**
		 * File text modified.
		 *
		 * @param text
		 *            the text
		 */
		protected void fileTextModified(final String text) {
			fileTextContent = text;
			updateOKStatus();
		}

		@Override
		public IStatus validate(final Object[] selectedElements) {
			if (showNewFolderControl) { updateNewFolderButtonState(); }

			boolean enableOK = false;
			for (Object selectedElement : selectedElements) {
				if (selectedElement instanceof IContainer) {
					enableOK = !showFiles || showFileControl && fileText.getText().trim().length() > 0;
				} else if (selectedElement instanceof IFile) {
					if (showFileControl) { fileText.setText(((IFile) selectedElement).getName()); }
					enableOK = true;
				}
				if (enableOK) { break; }
			}

			return enableOK ? new Status(IStatus.OK, "org.eclipse.emf.common.ui", 0, "", null)
					: new Status(IStatus.ERROR, "org.eclipse.emf.common.ui", 0, "", null);
		}

		/**
		 * Gets the selected containers.
		 *
		 * @return the selected containers
		 */
		public IContainer getSelectedContainer() {
			for (Object element : getResult()) { if (element instanceof IContainer) return (IContainer) element; }
			return null;
		}

		/**
		 * Gets the selected files.
		 *
		 * @return the selected files
		 */
		public IFile getSelectedFile() {
			for (Object element : getResult()) { if (element instanceof IFile) return (IFile) element; }
			return null;
		}

		/**
		 * Sets the file text.
		 *
		 * @param text
		 *            the new file text
		 */
		public void setFileText(String text) {
			if (text == null) { text = ""; }

			if (fileText != null && !fileText.isDisposed()) {
				fileText.setText(text);
			} else {
				fileTextContent = text;
			}
		}

		/**
		 * Gets the file text.
		 *
		 * @return the file text
		 */
		public String getFileText() {
			return fileText != null && !fileText.isDisposed() ? fileText.getText() : fileTextContent;
		}

	}

}
