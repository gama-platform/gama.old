/*******************************************************************************************************
 *
 * GamlEditorDragAndDropHandler.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import static org.eclipse.swt.dnd.DND.DROP_COPY;
import static org.eclipse.swt.dnd.DND.DROP_MOVE;
import static ummisco.gama.ui.metadata.FileMetaDataProvider.getContentTypeId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.dnd.IDragAndDropService;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.xtext.ui.editor.model.XtextDocument;

import msi.gama.common.util.FileUtils;
import msi.gama.runtime.PlatformHelper;
import msi.gaml.operators.Strings;
import msi.gaml.types.GamaFileType;
import msi.gaml.types.ParametricFileType;
import ummisco.gama.ui.metadata.FileMetaDataProvider;

/**
 * The class GamlEditorDragAndDropHandler.
 *
 * @author drogoul
 * @since 22 juil. 2018
 *
 */
public class GamlEditorDragAndDropHandler {

	/** The text. */
	TextTransfer TEXT = TextTransfer.getInstance();
	
	/** The file. */
	FileTransfer FILE = FileTransfer.getInstance();
	
	/** The rsrc. */
	ResourceTransfer RSRC = ResourceTransfer.getInstance();

	/** The transfer all. */
	Transfer[] TRANSFER_ALL = new Transfer[] { FILE, RSRC, TEXT };
	
	/** The transfer text. */
	Transfer[] TRANSFER_TEXT = new Transfer[] { TEXT };

	/** The editor. */
	final GamlEditor editor;
	
	/** The is text drag and drop installed. */
	boolean fIsTextDragAndDropInstalled;
	
	/** The text drag and drop token. */
	protected Object fTextDragAndDropToken;
	
	/** The used names. */
	protected static Set<String> usedNames = new HashSet<>();

	/**
	 * Instantiates a new gaml editor drag and drop handler.
	 *
	 * @param editor the editor
	 */
	public GamlEditorDragAndDropHandler(final GamlEditor editor) {
		this.editor = editor;
	}

	/**
	 * Gets the styled text.
	 *
	 * @return the styled text
	 */
	public StyledText getStyledText() {
		return getViewer().getTextWidget();
	}

	/**
	 * Gets the document.
	 *
	 * @return the document
	 */
	public XtextDocument getDocument() {
		return (XtextDocument) getViewer().getDocument();
	}

	/**
	 * Gets the viewer.
	 *
	 * @return the viewer
	 */
	public GamaSourceViewer getViewer() {
		return editor.getInternalSourceViewer();
	}

	/**
	 * Gets the selection provider.
	 *
	 * @return the selection provider
	 */
	public ISelectionProvider getSelectionProvider() {
		return getViewer().getSelectionProvider();
	}

	/**
	 * Install.
	 *
	 * @param onlyText the only text
	 */
	public void install(final boolean onlyText) {
		if (getViewer() == null || fIsTextDragAndDropInstalled) { return; }
		final StyledText st = getStyledText();
		final ISelectionProvider selectionProvider = getSelectionProvider();
		final IDragAndDropService dndService = editor.getSite().getService(IDragAndDropService.class);
		if (dndService == null) { return; }

		// Install drag source
		final DragSource source = new DragSource(st, DND.DROP_COPY | DND.DROP_MOVE);
		source.setTransfer(TRANSFER_TEXT);
		source.addDragListener(new DragSourceAdapter() {
			String fSelectedText;
			Point fSelection;

			@Override
			public void dragStart(final DragSourceEvent event) {
				fTextDragAndDropToken = null;
				try {
					fSelection = st.getSelection();
					event.doit = isLocationSelected(new Point(event.x, event.y));

					final ISelection selection = selectionProvider.getSelection();
					if (selection instanceof ITextSelection) {
						fSelectedText = ((ITextSelection) selection).getText();
					} else {
						fSelectedText = st.getSelectionText();
					}
				} catch (final IllegalArgumentException ex) {
					event.doit = false;
				}
			}

			private boolean isLocationSelected(final Point point) {
				// FIXME: https://bugs.eclipse.org/bugs/show_bug.cgi?id=260922
				if (editor.isBlockSelectionModeEnabled()) { return false; }

				int offset = st.getOffsetAtPoint(point);
				final Point p = st.getLocationAtOffset(offset);
				if (p.x > point.x) {
					offset--;
				}
				return offset >= fSelection.x && offset < fSelection.y;
			}

			@Override
			public void dragSetData(final DragSourceEvent event) {
				event.data = fSelectedText;
				fTextDragAndDropToken = this; // Can be any non-null object
			}

			@Override
			public void dragFinished(final DragSourceEvent event) {
				try {
					if (event.detail == DND.DROP_MOVE && editor.validateEditorInputState()) {
						final Point newSelection = st.getSelection();
						final int length = fSelection.y - fSelection.x;
						int delta = 0;
						if (newSelection.x < fSelection.x) {
							delta = length;
						}
						st.replaceTextRange(fSelection.x + delta, length, ""); //$NON-NLS-1$

						if (fTextDragAndDropToken == null) {
							// Move in same editor - end compound change
							final Object target = editor.getAdapter(IRewriteTarget.class);
							if (target != null) {
								((IRewriteTarget) target).endCompoundChange();
							}
						}

					}
				} finally {
					fTextDragAndDropToken = null;
				}
			}
		});

		// Install drag target
		final DropTargetListener listener = new DropTargetAdapter() {

			private Point fSelection;

			/**
			 * @see org.eclipse.swt.dnd.DropTargetAdapter#dropAccept(org.eclipse.swt.dnd.DropTargetEvent)
			 */
			@Override
			public void dropAccept(final DropTargetEvent event) {
				if (RSRC.isSupportedType(event.currentDataType) || FILE.isSupportedType(event.currentDataType)) {
					event.detail = DND.DROP_COPY;
					return;
				}
			}

			@Override
			public void dragEnter(final DropTargetEvent event) {
				if (RSRC.isSupportedType(event.currentDataType) || FILE.isSupportedType(event.currentDataType)) {
					event.detail = DND.DROP_COPY;
					return;
				}
				fTextDragAndDropToken = null;
				fSelection = st.getSelection();
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_MOVE) != 0) {
						event.detail = DND.DROP_MOVE;
					} else if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
			}

			@Override
			public void dragOperationChanged(final DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_MOVE) != 0) {
						event.detail = DND.DROP_MOVE;
					} else if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
			}

			@Override
			public void dragOver(final DropTargetEvent event) {
				event.feedback |= DND.FEEDBACK_SCROLL;
			}

			@Override
			public void drop(final DropTargetEvent event) {
				final Object data = event.data;
				try {

					if (RSRC.isSupportedType(event.currentDataType) && data instanceof IResource[]) {
						tryDropResources((IResource[]) data);
						return;
					}
					if (FILE.isSupportedType(event.currentDataType) && data instanceof String[]) {
						tryDropFiles((String[]) data);
						return;
					}
					if (fTextDragAndDropToken != null && event.detail == DND.DROP_MOVE) {
						// Move in same editor
						final int caretOffset = st.getCaretOffset();
						if (fSelection.x <= caretOffset && caretOffset <= fSelection.y) {
							event.detail = DND.DROP_NONE;
							return;
						}

						// Start compound change
						final Object target = editor.getAdapter(IRewriteTarget.class);
						if (target != null) {
							((IRewriteTarget) target).beginCompoundChange();
						}
					}

					if (!editor.validateEditorInputState()) {
						event.detail = DND.DROP_NONE;
						return;
					}

					final String text = (String) data;
					if (editor.isBlockSelectionModeEnabled()) {} else {
						final Point newSelection = st.getSelection();
						try {
							final int modelOffset = getViewer().widgetOffset2ModelOffset(newSelection.x);
							getDocument().replace(modelOffset, 0, text);
						} catch (final BadLocationException e) {
							return;
						}
						st.setSelectionRange(newSelection.x, text.length());
					}
				} finally {
					fTextDragAndDropToken = null;
				}
			}
		};
		dndService.addMergedDropTarget(st, DROP_MOVE | DROP_COPY, onlyText ? TRANSFER_TEXT : TRANSFER_ALL, listener);
		fIsTextDragAndDropInstalled = true;

	}

	/**
	 * Uninstall.
	 */
	protected void uninstall() {
		if (getViewer() == null || !fIsTextDragAndDropInstalled) { return; }

		final IDragAndDropService dndService = editor.getSite().getService(IDragAndDropService.class);
		if (dndService == null) { return; }

		final StyledText st = getStyledText();
		dndService.removeMergedDropTarget(st);

		final DragSource dragSource = (DragSource) st.getData(DND.DRAG_SOURCE_KEY);
		if (dragSource != null) {
			dragSource.dispose();
			st.setData(DND.DRAG_SOURCE_KEY, null);
		}

		fIsTextDragAndDropInstalled = false;
	}

	/**
	 * Try drop files.
	 *
	 * @param data the data
	 */
	void tryDropFiles(final String[] data) {
		final List<IFile> files = new ArrayList<>();
		for (final String raw : data) {
			String path = raw;
			if (PlatformHelper.isWindows()) {
				// Bug in getLocation().toString() under Windows. The documentation states that
				// the returned string is platform independant, but it is not
				path = path.replace('\\', '/');
			}

			if (FileUtils.isDirectoryOrNullExternalFile(path)) {
				continue;
			}
			final IFile file = FileUtils.createLinkToExternalFile(path, editor.getURI());
			if (file != null) {
				files.add(file);
			}
		}
		if (files.isEmpty()) { return; }
		final IResource[] resources = files.toArray(new IResource[0]);
		tryDropResources(resources);
	}

	/**
	 * Try drop resources.
	 *
	 * @param data the data
	 */
	void tryDropResources(final IResource[] data) {
		final List<IFile> imports = new ArrayList<>();
		final List<IFile> declarations = new ArrayList<>();
		for (final IResource resource : data) {
			if (resource instanceof IFile) {
				final IFile file = (IFile) resource;
				switch (getContentTypeId(file)) {
					case FileMetaDataProvider.GAML_CT_ID:
						imports.add(file);
						break;
					default:
						declarations.add(file);
				}
			}
		}
		addFilesToText(declarations, false);
		addFilesToText(imports, true);
		usedNames.clear();
	}

	/**
	 * Adds the files to text.
	 *
	 * @param files the files
	 * @param imports the imports
	 */
	private void addFilesToText(final List<IFile> files, final boolean imports) {
		if (files.size() == 0) { return; }
		final StringBuilder sb = new StringBuilder();
		int index = getStyledText().getSelection().x;
		if (imports) {
			for (final IFile file : files) {
				index = addDropImport(sb, file);
			}
		} else {
			for (final IFile file : files) {
				addDropFile(sb, file);
			}
		}
		if (sb.length() == 0) { return; }
		if (index == -1) { return; }
		try {
			final int modelOffset = getViewer().widgetOffset2ModelOffset(index);
			getDocument().replace(modelOffset, 0, addGlobalIfNecessary(sb.toString()));
		} catch (final BadLocationException e) {
			return;
		}
		getStyledText().setSelectionRange(index, sb.length());

	}

	/**
	 * @param sb
	 * @param file
	 */
	private int addDropFile(final StringBuilder sb, final IFile file) {
		final ParametricFileType type = GamaFileType.extensionsToFullType.get(file.getFileExtension());
		final String fullType = type == null ? "file" : type.toString();
		final String name = obtainRelativePath(file);
		final String varName = clean(file.getName()) + "_" + fullType;
		sb.append(Strings.LN).append(Strings.TAB).append(fullType).append(' ').append(varName).append(" <- ")
				.append(fullType).append("(").append('"').append(name).append('"').append(");").append(Strings.LN);
		return -1;

	}

	/**
	 * Clean.
	 *
	 * @param name the name
	 * @return the string
	 */
	private static String clean(final String name) {
		int i = 1;
		String rootName = name.substring(0, name.indexOf('.'));
		rootName = StringUtils.replaceChars(rootName, " .,;:-()'&@%*?!<>=+#", "_");
		String result = rootName + "0";
		while (usedNames.contains(result)) {
			result = rootName + String.valueOf(i++);
		}
		usedNames.add(result);
		return result;
	}

	/**
	 * Obtain relative path.
	 *
	 * @param file the file
	 * @return the string
	 */
	private String obtainRelativePath(final IFile file) {
		final IPath path = file.getFullPath();
		final IPath editorFile = new Path(editor.getURI().toPlatformString(true)).removeLastSegments(1);
		final IPath newRelativePath = path.makeRelativeTo(editorFile);
		final String name = newRelativePath.toString();
		return name;
	}

	/**
	 * Adds the global if necessary.
	 *
	 * @param fileDeclarations the file declarations
	 * @return the string
	 */
	private String addGlobalIfNecessary(final String fileDeclarations) {
		if (fileDeclarations.contains("import \"")) { return fileDeclarations; }
		final StyledText st = getStyledText();
		final String text = st.getText();
		final int startOfGlobal = text.indexOf("global");
		if (startOfGlobal == -1) { return "global {" + Strings.LN + fileDeclarations + Strings.LN + "}"; }
		return fileDeclarations;
	}

	/**
	 * @param sb
	 * @param file
	 * @return
	 */
	private int addDropImport(final StringBuilder sb, final IFile file) {
		final String name = obtainRelativePath(file);
		sb.append(Strings.LN).append("import ").append('"').append(name).append('"').append(Strings.LN);
		final StyledText st = getStyledText();
		final String text = st.getText();
		final int startOfGlobal = text.indexOf("global");
		final int startOfModel = text.indexOf("\nmodel");
		if (startOfGlobal == -1 && startOfModel == -1) { return -1; }
		final int endOfModel = text.indexOf("\n", startOfModel + 1);
		return endOfModel + 1;
	}
}
