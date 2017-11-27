package ummisco.gama.ui.wizards;

/*******************************************************************************
 * Copyright (c) 2004, 2015 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation Red Hat, Inc - extensive changes to allow importing of
 * Archive Files Philippe Ombredanne (pombredanne@nexb.com) - Bug 101180 [Import/Export] Import Existing Project into
 * Workspace default widget is back button , should be text field Martin Oberhuber (martin.oberhuber@windriver.com) -
 * Bug 187318[Wizards] "Import Existing Project" loops forever with cyclic symbolic links Remy Chi Jian Suen
 * (remy.suen@gmail.com) - Bug 210568 [Import/Export] Refresh button does not update list of projects Matt Hurne
 * (matt@thehurnes.com) - Bug 144610 [Import/Export] Import existing projects does not search subdirectories of found
 * projects Christian Georgi (christian.georgi@sap.com) - Bug 400399 [Import/Export] Project import wizard does not
 * remember selected folder or archive Bob Meincke (bob.meincke@gmx.de) - Bug 394900 - [Import/Export] Import existing
 * projects broken by mal-formed .project file Manumitting Technologies Inc - improve operation in face of errors during
 * import (bug 463016)
 *******************************************************************************/

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.StatusUtil;
import org.eclipse.ui.internal.wizards.datatransfer.ArchiveFileManipulations;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;
import org.eclipse.ui.internal.wizards.datatransfer.ILeveledImportStructureProvider;
import org.eclipse.ui.internal.wizards.datatransfer.TarEntry;
import org.eclipse.ui.internal.wizards.datatransfer.TarException;
import org.eclipse.ui.internal.wizards.datatransfer.TarFile;
import org.eclipse.ui.internal.wizards.datatransfer.TarLeveledStructureProvider;
import org.eclipse.ui.internal.wizards.datatransfer.ZipLeveledStructureProvider;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

/**
 * The WizardProjectsImportPage is the page that allows the user to import projects from a particular location.
 */
public class ImportProjectWizardPage extends WizardDataTransferPage {

	/**
	 * The name of the folder containing metadata information for the workspace.
	 */
	public static final String METADATA_FOLDER = ".metadata"; //$NON-NLS-1$

	/**
	 * The import structure provider.
	 *
	 * @since 3.4
	 */
	private ILeveledImportStructureProvider structureProvider;

	/**
	 * @since 3.5
	 *
	 */
	private final class ProjectLabelProvider extends LabelProvider implements IColorProvider {

		@Override
		public String getText(final Object element) {
			return ((ProjectRecord) element).getProjectLabel();
		}

		@Override
		public Color getBackground(final Object element) {
			return null;
		}

		@Override
		public Color getForeground(final Object element) {
			final ProjectRecord projectRecord = (ProjectRecord) element;
			if (projectRecord.hasConflicts
					|| projectRecord.isInvalid) { return getShell().getDisplay().getSystemColor(SWT.COLOR_GRAY); }
			return null;
		}
	}

	/**
	 * Class declared public only for test suite.
	 *
	 */
	public class ProjectRecord {
		File projectSystemFile;

		Object projectArchiveFile;

		String projectName;

		Object parent;

		int level;

		boolean hasConflicts;

		boolean isInvalid = false;

		IProjectDescription description;

		/**
		 * Create a record for a project based on the info in the file.
		 *
		 * @param file
		 */
		ProjectRecord(final File file) {
			projectSystemFile = file;
			setProjectName();
		}

		/**
		 * @param file
		 *            The Object representing the .project file
		 * @param parent
		 *            The parent folder of the .project file
		 * @param level
		 *            The number of levels deep in the provider the file is
		 */
		ProjectRecord(final Object file, final Object parent, final int level) {
			this.projectArchiveFile = file;
			this.parent = parent;
			this.level = level;
			setProjectName();
		}

		/**
		 * Set the name of the project based on the projectFile.
		 */
		private void setProjectName() {
			try {
				if (projectArchiveFile != null) {
					final InputStream stream = structureProvider.getContents(projectArchiveFile);

					// If we can get a description pull the name from there
					if (stream == null) {
						if (projectArchiveFile instanceof ZipEntry) {
							final IPath path = new Path(((ZipEntry) projectArchiveFile).getName());
							projectName = path.segment(path.segmentCount() - 2);
						} else if (projectArchiveFile instanceof TarEntry) {
							final IPath path = new Path(((TarEntry) projectArchiveFile).getName());
							projectName = path.segment(path.segmentCount() - 2);
						}
					} else {
						description = IDEWorkbenchPlugin.getPluginWorkspace().loadProjectDescription(stream);
						stream.close();
						projectName = description.getName();
					}

				}

				// If we don't have the project name try again
				if (projectName == null) {
					final IPath path = new Path(projectSystemFile.getPath());
					// if the file is in the default location, use the directory
					// name as the project name
					if (isDefaultLocation(path)) {
						projectName = path.segment(path.segmentCount() - 2);
						description = IDEWorkbenchPlugin.getPluginWorkspace().newProjectDescription(projectName);
					} else {
						description = IDEWorkbenchPlugin.getPluginWorkspace().loadProjectDescription(path);
						projectName = description.getName();
					}

				}
			} catch (final CoreException e) {
				// project definition file could not be parsed
				this.projectName = DataTransferMessages.WizardProjectsImportPage_invalidProjectName;
				this.isInvalid = true;

			} catch (final IOException e) {
				this.projectName = DataTransferMessages.WizardProjectsImportPage_invalidProjectName;
				this.isInvalid = true;
			}
		}

		/**
		 * Returns whether the given project description file path is in the default location for a project
		 *
		 * @param path
		 *            The path to examine
		 * @return Whether the given path is the default location for a project
		 */
		private boolean isDefaultLocation(final IPath path) {
			// The project description file must at least be within the project,
			// which is within the workspace location
			if (path.segmentCount() < 2) { return false; }
			return path.removeLastSegments(2).toFile().equals(Platform.getLocation().toFile());
		}

		/**
		 * Get the name of the project
		 *
		 * @return String
		 */
		public String getProjectName() {
			return projectName;
		}

		/**
		 * Returns whether the given project description file was invalid
		 *
		 * @return boolean
		 */
		public boolean isInvalidProject() {
			return isInvalid;
		}

		/**
		 * Gets the label to be used when rendering this project record in the UI.
		 *
		 * @return String the label
		 * @since 3.4
		 */
		public String getProjectLabel() {
			final String path =
					projectSystemFile == null ? structureProvider.getLabel(parent) : projectSystemFile.getParent();

			return NLS.bind(DataTransferMessages.WizardProjectsImportPage_projectLabel, projectName, path);
		}

		/**
		 * @return Returns the hasConflicts.
		 */
		public boolean hasConflicts() {
			return hasConflicts;
		}
	}

	/**
	 * A filter to remove conflicting projects
	 */
	class ConflictingProjectFilter extends ViewerFilter {

		@Override
		public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
			return !((ProjectRecord) element).hasConflicts;
		}

	}

	// dialog store id constants
	private final static String STORE_DIRECTORIES = "WizardProjectsImportPage.STORE_DIRECTORIES";//$NON-NLS-1$
	private final static String STORE_ARCHIVES = "WizardProjectsImportPage.STORE_ARCHIVES";//$NON-NLS-1$

	private final static String STORE_NESTED_PROJECTS = "WizardProjectsImportPage.STORE_NESTED_PROJECTS"; //$NON-NLS-1$

	private final static String STORE_COPY_PROJECT_ID = "WizardProjectsImportPage.STORE_COPY_PROJECT_ID"; //$NON-NLS-1$

	private final static String STORE_ARCHIVE_SELECTED = "WizardProjectsImportPage.STORE_ARCHIVE_SELECTED"; //$NON-NLS-1$

	private Combo directoryPathField;

	private CheckboxTreeViewer projectsList;

	private Button nestedProjectsCheckbox;

	private boolean nestedProjects = false;

	private Button copyCheckbox;

	private boolean copyFiles = false;

	private ProjectRecord[] selectedProjects = new ProjectRecord[0];

	// Keep track of the directory that we browsed to last time
	// the wizard was invoked.
	private static String previouslyBrowsedDirectory = ""; //$NON-NLS-1$

	// Keep track of the archive that we browsed to last time
	// the wizard was invoked.
	private static String previouslyBrowsedArchive = ""; //$NON-NLS-1$

	private Button projectFromDirectoryRadio;

	private Button projectFromArchiveRadio;

	private Combo archivePathField;

	private Button browseDirectoriesButton;

	private Button browseArchivesButton;

	// The last selected path; to minimize searches
	private String lastPath;

	// constant from WizardArchiveFileResourceImportPage1
	private static final String[] FILE_IMPORT_MASK = { "*.jar;*.zip;*.tar;*.tar.gz;*.tgz", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$

	// The initial path to set
	private final String initialPath;

	private final IStructuredSelection currentSelection;

	private Button hideConflictingProjects;

	private final ConflictingProjectFilter conflictingProjectsFilter = new ConflictingProjectFilter();

	/**
	 * Creates a new project creation wizard page.
	 *
	 */
	public ImportProjectWizardPage() {
		this("wizardExternalProjectsPage", null, null); //$NON-NLS-1$
	}

	/**
	 * Create a new instance of the receiver.
	 *
	 * @param pageName
	 */
	public ImportProjectWizardPage(final String pageName) {
		this(pageName, null, null);
	}

	/**
	 * More (many more) parameters.
	 *
	 * @param pageName
	 * @param initialPath
	 * @param currentSelection
	 * @since 3.5
	 */
	public ImportProjectWizardPage(final String pageName, final String initialPath,
			final IStructuredSelection currentSelection) {
		super(pageName);
		this.initialPath = initialPath;
		this.currentSelection = currentSelection;
		setPageComplete(false);
		setTitle("Import GAMA projects");
		setMessage("Select a directory or an archive to search for existing GAMA projects.");
	}

	@Override
	public void createControl(final Composite parent) {

		initializeDialogUnits(parent);

		final Composite workArea = new Composite(parent, SWT.NONE);
		setControl(workArea);

		workArea.setLayout(new GridLayout());
		workArea.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

		createProjectsRoot(workArea);
		createProjectsList(workArea);
		createOptionsGroup(workArea);
		restoreWidgetValues();
		Dialog.applyDialogFont(workArea);

	}

	@Override
	protected void createOptionsGroupButtons(final Group optionsGroup) {
		nestedProjectsCheckbox = new Button(optionsGroup, SWT.CHECK);
		nestedProjectsCheckbox.setText(DataTransferMessages.WizardProjectsImportPage_SearchForNestedProjects);
		nestedProjectsCheckbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nestedProjectsCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				nestedProjects = nestedProjectsCheckbox.getSelection();
				if (projectFromDirectoryRadio.getSelection()) {
					updateProjectsList(directoryPathField.getText().trim(), true);
				} else {
					updateProjectsList(archivePathField.getText().trim(), true);
				}
			}
		});

		copyCheckbox = new Button(optionsGroup, SWT.CHECK);
		copyCheckbox.setText(DataTransferMessages.WizardProjectsImportPage_CopyProjectsIntoWorkspace);
		copyCheckbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		copyCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				copyFiles = copyCheckbox.getSelection();
				// need to refresh the project list as projects already
				// in the workspace directory are treated as conflicts
				// and should be hidden too
				projectsList.refresh(true);
			}
		});

		hideConflictingProjects = new Button(optionsGroup, SWT.CHECK);
		hideConflictingProjects.setText(DataTransferMessages.WizardProjectsImportPage_hideExistingProjects);
		hideConflictingProjects.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		hideConflictingProjects.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				projectsList.removeFilter(conflictingProjectsFilter);
				if (hideConflictingProjects.getSelection()) {
					projectsList.addFilter(conflictingProjectsFilter);
				}
			}
		});
		Dialog.applyDialogFont(hideConflictingProjects);
	}

	/**
	 * Create the checkbox list for the found projects.
	 *
	 * @param workArea
	 */
	private void createProjectsList(final Composite workArea) {

		final Label title = new Label(workArea, SWT.NONE);
		title.setText(DataTransferMessages.WizardProjectsImportPage_ProjectsListTitle);

		final Composite listComposite = new Composite(workArea, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.makeColumnsEqualWidth = false;
		listComposite.setLayout(layout);

		listComposite
				.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		projectsList = new CheckboxTreeViewer(listComposite, SWT.BORDER);
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = new PixelConverter(projectsList.getControl()).convertWidthInCharsToPixels(25);
		gridData.heightHint = new PixelConverter(projectsList.getControl()).convertHeightInCharsToPixels(10);
		projectsList.getControl().setLayoutData(gridData);
		projectsList.setContentProvider(new ITreeContentProvider() {

			@Override
			public Object[] getChildren(final Object parentElement) {
				return null;
			}

			@Override
			public Object[] getElements(final Object inputElement) {
				return getProjectRecords();
			}

			@Override
			public boolean hasChildren(final Object element) {
				return false;
			}

			@Override
			public Object getParent(final Object element) {
				return null;
			}

			@Override
			public void dispose() {

			}

			@Override
			public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {}

		});

		projectsList.setLabelProvider(new ProjectLabelProvider());

		projectsList.addCheckStateListener(event -> {
			final ProjectRecord element = (ProjectRecord) event.getElement();
			if (element.hasConflicts || element.isInvalid) {
				projectsList.setChecked(element, false);
			}
			setPageComplete(projectsList.getCheckedElements().length > 0);
		});

		projectsList.setInput(this);
		projectsList.setComparator(new ViewerComparator());
		createSelectionButtons(listComposite);
	}

	/**
	 * Create the selection buttons in the listComposite.
	 *
	 * @param listComposite
	 */
	private void createSelectionButtons(final Composite listComposite) {
		final Composite buttonsComposite = new Composite(listComposite, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonsComposite.setLayout(layout);

		buttonsComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		final Button selectAll = new Button(buttonsComposite, SWT.PUSH);
		selectAll.setText(DataTransferMessages.DataTransfer_selectAll);
		selectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				for (final ProjectRecord selectedProject : selectedProjects) {
					if (selectedProject.hasConflicts || selectedProject.isInvalid) {
						projectsList.setChecked(selectedProject, false);
					} else {
						projectsList.setChecked(selectedProject, true);
					}
				}
				setPageComplete(projectsList.getCheckedElements().length > 0);
			}
		});
		Dialog.applyDialogFont(selectAll);
		setButtonLayoutData(selectAll);

		final Button deselectAll = new Button(buttonsComposite, SWT.PUSH);
		deselectAll.setText(DataTransferMessages.DataTransfer_deselectAll);
		deselectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				projectsList.setCheckedElements(new Object[0]);
				setPageComplete(false);
			}
		});
		Dialog.applyDialogFont(deselectAll);
		setButtonLayoutData(deselectAll);

		final Button refresh = new Button(buttonsComposite, SWT.PUSH);
		refresh.setText(DataTransferMessages.DataTransfer_refresh);
		refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (projectFromDirectoryRadio.getSelection()) {
					updateProjectsList(directoryPathField.getText().trim(), true);
				} else {
					updateProjectsList(archivePathField.getText().trim(), true);
				}
			}
		});
		Dialog.applyDialogFont(refresh);
		setButtonLayoutData(refresh);
	}

	/**
	 * Create the area where you select the root directory for the projects.
	 *
	 * @param workArea
	 *            Composite
	 */
	private void createProjectsRoot(final Composite workArea) {

		// project specification group
		final Composite projectGroup = new Composite(workArea, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = false;
		layout.marginWidth = 0;
		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// new project from directory radio button
		projectFromDirectoryRadio = new Button(projectGroup, SWT.RADIO);
		projectFromDirectoryRadio.setText(DataTransferMessages.WizardProjectsImportPage_RootSelectTitle);

		// project location entry combo
		this.directoryPathField = new Combo(projectGroup, SWT.BORDER);

		final GridData directoryPathData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		directoryPathData.widthHint = new PixelConverter(directoryPathField).convertWidthInCharsToPixels(25);
		directoryPathField.setLayoutData(directoryPathData);

		// browse button
		browseDirectoriesButton = new Button(projectGroup, SWT.PUSH);
		browseDirectoriesButton.setText(DataTransferMessages.DataTransfer_browse);
		setButtonLayoutData(browseDirectoriesButton);

		// new project from archive radio button
		projectFromArchiveRadio = new Button(projectGroup, SWT.RADIO);
		projectFromArchiveRadio.setText(DataTransferMessages.WizardProjectsImportPage_ArchiveSelectTitle);

		// project location entry combo
		archivePathField = new Combo(projectGroup, SWT.BORDER);

		final GridData archivePathData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		archivePathData.widthHint = new PixelConverter(archivePathField).convertWidthInCharsToPixels(25);
		archivePathField.setLayoutData(archivePathData); // browse button
		browseArchivesButton = new Button(projectGroup, SWT.PUSH);
		browseArchivesButton.setText(DataTransferMessages.DataTransfer_browse);
		setButtonLayoutData(browseArchivesButton);

		projectFromDirectoryRadio.setSelection(true);
		archivePathField.setEnabled(false);
		browseArchivesButton.setEnabled(false);

		browseDirectoriesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleLocationDirectoryButtonPressed();
			}

		});

		browseArchivesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleLocationArchiveButtonPressed();
			}

		});

		directoryPathField.addTraverseListener(e -> {
			if (e.detail == SWT.TRAVERSE_RETURN) {
				e.doit = false;
				updateProjectsList(directoryPathField.getText().trim());
			}
		});

		directoryPathField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final org.eclipse.swt.events.FocusEvent e) {
				updateProjectsList(directoryPathField.getText().trim());
			}

		});

		directoryPathField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateProjectsList(directoryPathField.getText().trim());
			}
		});

		archivePathField.addTraverseListener(e -> {
			if (e.detail == SWT.TRAVERSE_RETURN) {
				e.doit = false;
				updateProjectsList(archivePathField.getText().trim());
			}
		});

		archivePathField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final org.eclipse.swt.events.FocusEvent e) {
				updateProjectsList(archivePathField.getText().trim());
			}
		});

		archivePathField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateProjectsList(archivePathField.getText().trim());
			}
		});

		projectFromDirectoryRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				directoryRadioSelected();
			}
		});

		projectFromArchiveRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				archiveRadioSelected();
			}
		});
	}

	private void archiveRadioSelected() {
		if (projectFromArchiveRadio.getSelection()) {
			directoryPathField.setEnabled(false);
			browseDirectoriesButton.setEnabled(false);
			archivePathField.setEnabled(true);
			browseArchivesButton.setEnabled(true);
			updateProjectsList(archivePathField.getText());
			archivePathField.setFocus();
			nestedProjectsCheckbox.setSelection(true);
			nestedProjectsCheckbox.setEnabled(false);
			copyCheckbox.setSelection(true);
			copyCheckbox.setEnabled(false);
		}
	}

	private void directoryRadioSelected() {
		if (projectFromDirectoryRadio.getSelection()) {
			directoryPathField.setEnabled(true);
			browseDirectoriesButton.setEnabled(true);
			archivePathField.setEnabled(false);
			browseArchivesButton.setEnabled(false);
			updateProjectsList(directoryPathField.getText());
			directoryPathField.setFocus();
			nestedProjectsCheckbox.setEnabled(true);
			nestedProjectsCheckbox.setSelection(nestedProjects);
			copyCheckbox.setEnabled(true);
			copyCheckbox.setSelection(copyFiles);
		}
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible && this.projectFromDirectoryRadio.getSelection()) {
			this.directoryPathField.setFocus();
		}
		if (visible && this.projectFromArchiveRadio.getSelection()) {
			this.archivePathField.setFocus();
		}
	}

	/**
	 * Update the list of projects based on path. Method declared public only for test suite.
	 *
	 * @param path
	 */
	public void updateProjectsList(final String path) {
		updateProjectsList(path, false);
	}

	private void updateProjectsList(final String path, final boolean forceUpdate) {
		// on an empty path empty selectedProjects
		if (path == null || path.length() == 0) {
			setMessage("Select a directory or an archive to search for existing GAMA projects.");
			selectedProjects = new ProjectRecord[0];
			projectsList.refresh(true);
			projectsList.setCheckedElements(selectedProjects);
			setPageComplete(projectsList.getCheckedElements().length > 0);
			lastPath = path;
			return;
		}

		final File directory = new File(path);
		if (path.equals(lastPath) && !forceUpdate) {
			// unchanged; lastPath is updated here and in the refresh
			return;
		}

		// We can't access the radio button from the inner class so get the
		// status beforehand
		final boolean dirSelected = this.projectFromDirectoryRadio.getSelection();
		try {
			getContainer().run(true, true, monitor -> {

				monitor.beginTask(DataTransferMessages.WizardProjectsImportPage_SearchingMessage, 100);
				selectedProjects = new ProjectRecord[0];
				final Collection<ProjectRecord> files = new ArrayList<>();
				monitor.worked(10);
				if (!dirSelected && ArchiveFileManipulations.isTarFile(path)) {
					final TarFile sourceTarFile = getSpecifiedTarSourceFile(path);
					if (sourceTarFile == null) { return; }

					structureProvider = new TarLeveledStructureProvider(sourceTarFile);
					final Object child1 = structureProvider.getRoot();

					if (!collectProjectFilesFromProvider(files, child1, 0, monitor)) { return; }
					final Iterator<?> filesIterator1 = files.iterator();
					selectedProjects = new ProjectRecord[files.size()];
					int index1 = 0;
					monitor.worked(50);
					monitor.subTask(DataTransferMessages.WizardProjectsImportPage_ProcessingMessage);
					while (filesIterator1.hasNext()) {
						selectedProjects[index1++] = (ProjectRecord) filesIterator1.next();
					}
				} else if (!dirSelected && ArchiveFileManipulations.isZipFile(path)) {
					final ZipFile sourceFile = getSpecifiedZipSourceFile(path);
					if (sourceFile == null) { return; }
					structureProvider = new ZipLeveledStructureProvider(sourceFile);
					final Object child2 = structureProvider.getRoot();

					if (!collectProjectFilesFromProvider(files, child2, 0, monitor)) { return; }
					final Iterator<?> filesIterator2 = files.iterator();
					selectedProjects = new ProjectRecord[files.size()];
					int index2 = 0;
					monitor.worked(50);
					monitor.subTask(DataTransferMessages.WizardProjectsImportPage_ProcessingMessage);
					while (filesIterator2.hasNext()) {
						selectedProjects[index2++] = (ProjectRecord) filesIterator2.next();
					}
				}

				else if (dirSelected && directory.isDirectory()) {

					if (!collectProjectFilesFromDirectory(files, directory, null, monitor)) { return; }
					final Iterator<?> filesIterator3 = files.iterator();
					selectedProjects = new ProjectRecord[files.size()];
					int index3 = 0;
					monitor.worked(50);
					monitor.subTask(DataTransferMessages.WizardProjectsImportPage_ProcessingMessage);
					while (filesIterator3.hasNext()) {
						final File file = (File) filesIterator3.next();
						selectedProjects[index3] = new ProjectRecord(file);
						index3++;
					}
				} else {
					monitor.worked(60);
				}
				monitor.done();
			});
		} catch (final InvocationTargetException e) {
			IDEWorkbenchPlugin.log(e.getMessage(), e);
		} catch (final InterruptedException e) {
			// Nothing to do if the user interrupts.
		}

		lastPath = path;
		updateProjectsStatus();
	}

	private void updateProjectsStatus() {
		projectsList.refresh(true);
		final ProjectRecord[] projects = getProjectRecords();

		boolean displayConflictWarning = false;
		boolean displayInvalidWarning = false;

		for (final ProjectRecord project : projects) {
			if (project.hasConflicts || project.isInvalid) {
				projectsList.setGrayed(project, true);
				displayConflictWarning |= project.hasConflicts;
				displayInvalidWarning |= project.isInvalid;
			} else {
				projectsList.setChecked(project, true);
			}
		}

		if (displayConflictWarning && displayInvalidWarning) {
			setMessage(DataTransferMessages.WizardProjectsImportPage_projectsInWorkspaceAndInvalid, WARNING);
		} else if (displayConflictWarning) {
			setMessage(DataTransferMessages.WizardProjectsImportPage_projectsInWorkspace, WARNING);
		} else if (displayInvalidWarning) {
			setMessage(DataTransferMessages.WizardProjectsImportPage_projectsInvalid, WARNING);
		} else {
			setMessage("Select a directory or an archive to search for existing GAMA projects.");
		}
		setPageComplete(projectsList.getCheckedElements().length > 0);
		if (selectedProjects.length == 0) {
			setMessage(DataTransferMessages.WizardProjectsImportPage_noProjectsToImport, WARNING);
		}
	}

	/**
	 * Answer a handle to the zip file currently specified as being the source. Return null if this file does not exist
	 * or is not of valid format.
	 */
	private ZipFile getSpecifiedZipSourceFile(final String fileName) {
		if (fileName.length() == 0) { return null; }

		try {
			return new ZipFile(fileName);
		} catch (final ZipException e) {
			displayErrorDialog(DataTransferMessages.ZipImport_badFormat);
		} catch (final IOException e) {
			displayErrorDialog(DataTransferMessages.ZipImport_couldNotRead);
		}

		archivePathField.setFocus();
		return null;
	}

	/**
	 * Answer a handle to the zip file currently specified as being the source. Return null if this file does not exist
	 * or is not of valid format.
	 */
	private TarFile getSpecifiedTarSourceFile(final String fileName) {
		if (fileName.length() == 0) { return null; }

		try {
			return new TarFile(fileName);
		} catch (final TarException e) {
			displayErrorDialog(DataTransferMessages.TarImport_badFormat);
		} catch (final IOException e) {
			displayErrorDialog(DataTransferMessages.ZipImport_couldNotRead);
		}

		archivePathField.setFocus();
		return null;
	}

	/**
	 * Collect the list of .project files that are under directory into files.
	 *
	 * @param files
	 * @param directory
	 * @param directoriesVisited
	 *            Set of canonical paths of directories, used as recursion guard
	 * @param monitor
	 *            The monitor to report to
	 * @return boolean <code>true</code> if the operation was completed.
	 */
	@SuppressWarnings ({ "rawtypes", "unchecked" })
	private boolean collectProjectFilesFromDirectory(final Collection files, final File directory,
			Set<String> directoriesVisited, final IProgressMonitor monitor) {

		if (monitor.isCanceled()) { return false; }
		monitor.subTask(NLS.bind(DataTransferMessages.WizardProjectsImportPage_CheckingMessage, directory.getPath()));
		final File[] contents = directory.listFiles();
		if (contents == null) { return false; }

		// Initialize recursion guard for recursive symbolic links
		if (directoriesVisited == null) {
			directoriesVisited = new HashSet<String>();
			try {
				directoriesVisited.add(directory.getCanonicalPath());
			} catch (final IOException exception) {
				StatusManager.getManager()
						.handle(StatusUtil.newStatus(IStatus.ERROR, exception.getLocalizedMessage(), exception));
			}
		}

		// first look for project description files
		final String dotProject = IProjectDescription.DESCRIPTION_FILE_NAME;
		for (final File file : contents) {
			if (file.isFile() && file.getName().equals(dotProject)) {
				files.add(file);
				if (!nestedProjects) {
					// don't search sub-directories since we can't have nested
					// projects
					return true;
				}
			}
		}
		// no project description found or search for nested projects enabled,
		// so recurse into sub-directories
		for (int i = 0; i < contents.length; i++) {
			if (contents[i].isDirectory()) {
				if (!contents[i].getName().equals(METADATA_FOLDER)) {
					try {
						final String canonicalPath = contents[i].getCanonicalPath();
						if (!directoriesVisited.add(canonicalPath)) {
							// already been here --> do not recurse
							continue;
						}
					} catch (final IOException exception) {
						StatusManager.getManager().handle(
								StatusUtil.newStatus(IStatus.ERROR, exception.getLocalizedMessage(), exception));

					}
					collectProjectFilesFromDirectory(files, contents[i], directoriesVisited, monitor);
				}
			}
		}
		return true;
	}

	/**
	 * Collect the list of .project files that are under directory into files.
	 *
	 * @param files
	 * @param monitor
	 *            The monitor to report to
	 * @return boolean <code>true</code> if the operation was completed.
	 */
	private boolean collectProjectFilesFromProvider(final Collection<ProjectRecord> files, final Object entry,
			final int level, final IProgressMonitor monitor) {

		if (monitor.isCanceled()) { return false; }
		monitor.subTask(NLS.bind(DataTransferMessages.WizardProjectsImportPage_CheckingMessage,
				structureProvider.getLabel(entry)));
		List<?> children = structureProvider.getChildren(entry);
		if (children == null) {
			children = new ArrayList<Object>(1);
		}
		final Iterator<?> childrenEnum = children.iterator();
		while (childrenEnum.hasNext()) {
			final Object child = childrenEnum.next();
			if (structureProvider.isFolder(child)) {
				collectProjectFilesFromProvider(files, child, level + 1, monitor);
			}
			final String elementLabel = structureProvider.getLabel(child);
			if (elementLabel.equals(IProjectDescription.DESCRIPTION_FILE_NAME)) {
				files.add(new ProjectRecord(child, entry, level));
			}
		}
		return true;
	}

	/**
	 * The browse button has been selected. Select the location.
	 */
	protected void handleLocationDirectoryButtonPressed() {

		final DirectoryDialog dialog = new DirectoryDialog(directoryPathField.getShell(), SWT.SHEET);
		dialog.setMessage(DataTransferMessages.WizardProjectsImportPage_SelectDialogTitle);

		String dirName = directoryPathField.getText().trim();
		if (dirName.length() == 0) {
			dirName = previouslyBrowsedDirectory;
		}

		if (dirName.length() == 0) {
			dialog.setFilterPath(IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getLocation().toOSString());
		} else {
			final File path = new File(dirName);
			if (path.exists()) {
				dialog.setFilterPath(new Path(dirName).toOSString());
			}
		}

		final String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			previouslyBrowsedDirectory = selectedDirectory;
			directoryPathField.setText(previouslyBrowsedDirectory);
			updateProjectsList(selectedDirectory);
		}

	}

	/**
	 * The browse button has been selected. Select the location.
	 */
	protected void handleLocationArchiveButtonPressed() {

		final FileDialog dialog = new FileDialog(archivePathField.getShell(), SWT.SHEET);
		dialog.setFilterExtensions(FILE_IMPORT_MASK);
		dialog.setText(DataTransferMessages.WizardProjectsImportPage_SelectArchiveDialogTitle);

		String fileName = archivePathField.getText().trim();
		if (fileName.length() == 0) {
			fileName = previouslyBrowsedArchive;
		}

		if (fileName.length() == 0) {
			dialog.setFilterPath(IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getLocation().toOSString());
		} else {
			final File path = new File(fileName).getParentFile();
			if (path != null && path.exists()) {
				dialog.setFilterPath(path.toString());
			}
		}

		final String selectedArchive = dialog.open();
		if (selectedArchive != null) {
			previouslyBrowsedArchive = selectedArchive;
			archivePathField.setText(previouslyBrowsedArchive);
			updateProjectsList(selectedArchive);
		}

	}

	/**
	 * Create the selected projects
	 *
	 * @return boolean <code>true</code> if all project creations were successful.
	 */
	public boolean createProjects() {
		saveWidgetValues();

		final Object[] selected = projectsList.getCheckedElements();
		createdProjects = new ArrayList<IProject>();
		final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			@Override
			protected void execute(final IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					monitor.beginTask("", selected.length); //$NON-NLS-1$
					if (monitor.isCanceled()) { throw new OperationCanceledException(); }
					// Import as many projects as we can; accumulate errors to
					// report to the user
					final MultiStatus status = new MultiStatus(IDEWorkbenchPlugin.IDE_WORKBENCH, 1,
							DataTransferMessages.WizardProjectsImportPage_projectsInWorkspaceAndInvalid, null);
					for (final Object element : selected) {
						status.add(createExistingProject((ProjectRecord) element, new SubProgressMonitor(monitor, 1)));
					}
					if (!status.isOK()) { throw new InvocationTargetException(new CoreException(status)); }
				} finally {
					monitor.done();
				}
			}
		};
		// run the new project creation operation
		try {
			getContainer().run(true, true, op);
		} catch (final InterruptedException e) {
			return false;
		} catch (final InvocationTargetException e) {
			// one of the steps resulted in a core exception
			final Throwable t = e.getTargetException();
			final String message = DataTransferMessages.WizardExternalProjectImportPage_errorMessage;
			IStatus status;
			if (t instanceof CoreException) {
				status = ((CoreException) t).getStatus();
			} else {
				status = new Status(IStatus.ERROR, IDEWorkbenchPlugin.IDE_WORKBENCH, 1, message, t);
			}
			// Update the visible status on error so the user can see what's
			// been imported
			updateProjectsStatus();
			ErrorDialog.openError(getShell(), message, null, status);
			return false;
		} finally {
			ArchiveFileManipulations.closeStructureProvider(structureProvider, getShell());
		}
		return true;
	}

	List<IProject> createdProjects;

	/**
	 * Performs clean-up if the user cancels the wizard without doing anything
	 */
	public void performCancel() {
		ArchiveFileManipulations.closeStructureProvider(structureProvider, getShell());
	}

	/**
	 * Create the project described in record.
	 *
	 * @param record
	 * @return status of the creation
	 * @throws InterruptedException
	 */
	private IStatus createExistingProject(final ProjectRecord record, final IProgressMonitor monitor)
			throws InterruptedException {
		final String projectName = record.getProjectName();
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject project = workspace.getRoot().getProject(projectName);
		createdProjects.add(project);
		if (record.description == null) {
			// error case
			record.description = workspace.newProjectDescription(projectName);
			final IPath locationPath = new Path(record.projectSystemFile.getAbsolutePath());

			// If it is under the root use the default location
			if (Platform.getLocation().isPrefixOf(locationPath)) {
				record.description.setLocation(null);
			} else {
				record.description.setLocation(locationPath);
			}
		} else {
			record.description.setName(projectName);
		}
		if (record.projectArchiveFile != null) {
			// import from archive
			final List<?> fileSystemObjects = structureProvider.getChildren(record.parent);
			structureProvider.setStrip(record.level);
			final ImportOperation operation = new ImportOperation(project.getFullPath(), structureProvider.getRoot(),
					structureProvider, this, fileSystemObjects);
			operation.setContext(getShell());
			try {
				operation.run(monitor);
			} catch (final InvocationTargetException e) {
				if (e.getCause() instanceof CoreException) { return ((CoreException) e.getCause()).getStatus(); }
				return new Status(IStatus.ERROR, IDEWorkbenchPlugin.IDE_WORKBENCH, 2,
						e.getCause().getLocalizedMessage(), e);
			}
			return operation.getStatus();
		}
		// import from file system
		File importSource = null;
		if (copyFiles) {
			// import project from location copying files - use default project
			// location for this workspace
			final URI locationURI = record.description.getLocationURI();
			// if location is null, project already exists in this location or
			// some error condition occured.
			if (locationURI != null) {
				// validate the location of the project being copied
				final IStatus result = ResourcesPlugin.getWorkspace().validateProjectLocationURI(project, locationURI);
				if (!result.isOK()) { return result; }

				importSource = new File(locationURI);
				final IProjectDescription desc = workspace.newProjectDescription(projectName);
				desc.setBuildSpec(record.description.getBuildSpec());
				desc.setComment(record.description.getComment());
				desc.setDynamicReferences(record.description.getDynamicReferences());
				desc.setNatureIds(record.description.getNatureIds());
				desc.setReferencedProjects(record.description.getReferencedProjects());
				record.description = desc;
			}
		}

		try {
			monitor.beginTask(DataTransferMessages.WizardProjectsImportPage_CreateProjectsTask, 100);
			project.create(record.description, new SubProgressMonitor(monitor, 30));
			project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 70));
		} catch (final CoreException e) {
			return e.getStatus();
		} finally {
			monitor.done();
		}

		// import operation to import project files if copy checkbox is selected
		if (copyFiles && importSource != null) {
			final List<?> filesToImport = FileSystemStructureProvider.INSTANCE.getChildren(importSource);
			final ImportOperation operation = new ImportOperation(project.getFullPath(), importSource,
					FileSystemStructureProvider.INSTANCE, this, filesToImport);
			operation.setContext(getShell());
			operation.setOverwriteResources(true); // need to overwrite
			// .project, .classpath
			// files
			operation.setCreateContainerStructure(false);
			try {
				operation.run(monitor);
			} catch (final InvocationTargetException e) {
				if (e.getCause() instanceof CoreException) { return ((CoreException) e.getCause()).getStatus(); }
				return new Status(IStatus.ERROR, IDEWorkbenchPlugin.IDE_WORKBENCH, 2,
						e.getCause().getLocalizedMessage(), e);
			}
			return operation.getStatus();
		}

		return Status.OK_STATUS;
	}

	/**
	 * Method used for test suite.
	 *
	 * @return Button the Import from Directory RadioButton
	 */
	public Button getProjectFromDirectoryRadio() {
		return projectFromDirectoryRadio;
	}

	/**
	 * Method used for test suite.
	 *
	 * @return CheckboxTreeViewer the viewer containing all the projects found
	 */
	public CheckboxTreeViewer getProjectsList() {
		return projectsList;
	}

	/**
	 * Retrieve all the projects in the current workspace.
	 *
	 * @return IProject[] array of IProject in the current workspace
	 */
	private IProject[] getProjectsInWorkspace() {
		return IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getProjects();
	}

	/**
	 * Get the array of project records that can be imported from the source workspace or archive, selected by the user.
	 * If a project with the same name exists in both the source workspace and the current workspace, then the
	 * hasConflicts flag would be set on that project record.
	 *
	 * Method declared public for test suite.
	 *
	 * @return ProjectRecord[] array of projects that can be imported into the workspace
	 */
	public ProjectRecord[] getProjectRecords() {
		final List<ProjectRecord> projectRecords = new ArrayList<ProjectRecord>();
		for (int i = 0; i < selectedProjects.length; i++) {
			final String projectName = selectedProjects[i].getProjectName();
			selectedProjects[i].hasConflicts =
					isProjectInWorkspacePath(projectName) && copyFiles || isProjectInWorkspace(projectName);
			projectRecords.add(selectedProjects[i]);
		}
		return projectRecords.toArray(new ProjectRecord[projectRecords.size()]);
	}

	/**
	 * Determine if there is a directory with the project name in the workspace path.
	 *
	 * @param projectName
	 *            the name of the project
	 * @return true if there is a directory with the same name of the imported project
	 */
	private boolean isProjectInWorkspacePath(final String projectName) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IPath wsPath = workspace.getRoot().getLocation();
		final IPath localProjectPath = wsPath.append(projectName);
		return localProjectPath.toFile().exists();
	}

	/**
	 * Determine if the project with the given name is in the current workspace.
	 *
	 * @param projectName
	 *            String the project name to check
	 * @return boolean true if the project with the given name is in this workspace
	 */
	private boolean isProjectInWorkspace(final String projectName) {
		if (projectName == null) { return false; }
		final IProject[] workspaceProjects = getProjectsInWorkspace();
		for (final IProject workspaceProject : workspaceProjects) {
			if (projectName.equals(workspaceProject.getName())) { return true; }
		}
		return false;
	}

	/**
	 * Use the dialog store to restore widget values to the values that they held last time this wizard was used to
	 * completion, or alternatively, if an initial path is specified, use it to select values.
	 *
	 * Method declared public only for use of tests.
	 */
	@Override
	public void restoreWidgetValues() {

		// First, check to see if we have resore settings, and
		// take care of the checkbox
		final IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			restoreFromHistory(settings, STORE_DIRECTORIES, directoryPathField);
			restoreFromHistory(settings, STORE_ARCHIVES, archivePathField);

			// checkbox
			nestedProjects = settings.getBoolean(STORE_NESTED_PROJECTS);
			nestedProjectsCheckbox.setSelection(nestedProjects);

			// checkbox
			copyFiles = settings.getBoolean(STORE_COPY_PROJECT_ID);
			copyCheckbox.setSelection(copyFiles);
		}

		// Second, check to see if we don't have an initial path,
		// and if we do have restore settings. If so, set the
		// radio selection properly to restore settings

		if (initialPath == null && settings != null) {
			// radio selection
			final boolean archiveSelected = settings.getBoolean(STORE_ARCHIVE_SELECTED);
			projectFromDirectoryRadio.setSelection(!archiveSelected);
			projectFromArchiveRadio.setSelection(archiveSelected);
			if (archiveSelected) {
				archiveRadioSelected();
			} else {
				directoryRadioSelected();
			}
		}
		// Third, if we do have an initial path, set the proper
		// path and radio buttons to the initial value. Move
		// cursor to the end of the path so user can see the
		// most relevant part (directory / archive name)
		else if (initialPath != null) {
			final boolean dir = new File(initialPath).isDirectory();

			projectFromDirectoryRadio.setSelection(dir);
			projectFromArchiveRadio.setSelection(!dir);

			if (dir) {
				directoryPathField.setText(initialPath);
				directoryPathField.setSelection(new Point(initialPath.length(), initialPath.length()));
				directoryRadioSelected();
			} else {
				archivePathField.setText(initialPath);
				archivePathField.setSelection(new Point(initialPath.length(), initialPath.length()));
				archiveRadioSelected();
			}
		}
	}

	private void restoreFromHistory(final IDialogSettings settings, final String key, final Combo combo) {
		final String[] sourceNames = settings.getArray(key);
		if (sourceNames == null) { return; // ie.- no values stored, so stop
		}

		for (final String sourceName : sourceNames) {
			combo.add(sourceName);
		}
	}

	/**
	 * Since Finish was pressed, write widget values to the dialog store so that they will persist into the next
	 * invocation of this wizard page.
	 *
	 * Method declared public only for use of tests.
	 */
	@Override
	public void saveWidgetValues() {
		final IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			saveInHistory(settings, STORE_DIRECTORIES, directoryPathField.getText());
			saveInHistory(settings, STORE_ARCHIVES, archivePathField.getText());

			settings.put(STORE_NESTED_PROJECTS, nestedProjectsCheckbox.getSelection());

			settings.put(STORE_COPY_PROJECT_ID, copyCheckbox.getSelection());

			settings.put(STORE_ARCHIVE_SELECTED, projectFromArchiveRadio.getSelection());
		}
	}

	private void saveInHistory(final IDialogSettings settings, final String key, final String value) {
		String[] sourceNames = settings.getArray(key);
		if (sourceNames == null) {
			sourceNames = new String[0];
		}
		sourceNames = addToHistory(sourceNames, value);
		settings.put(key, sourceNames);
	}

	/**
	 * Method used for test suite.
	 *
	 * @return Button copy checkbox
	 */
	public Button getCopyCheckbox() {
		return copyCheckbox;
	}

	/**
	 * Method used for test suite.
	 *
	 * @return Button nested projects checkbox
	 */
	public Button getNestedProjectsCheckbox() {
		return nestedProjectsCheckbox;
	}

	@Override
	public void handleEvent(final Event event) {}

	@Override
	protected boolean allowNewContainerName() {
		return true;
	}

}
