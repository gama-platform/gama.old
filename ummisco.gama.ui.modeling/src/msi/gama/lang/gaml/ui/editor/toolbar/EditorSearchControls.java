/*******************************************************************************************************
 *
 * EditorSearchControls.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editor.toolbar;

import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IFindReplaceTargetExtension;
import org.eclipse.jface.text.IFindReplaceTargetExtension3;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.swt.IFocusService;

import msi.gama.lang.gaml.ui.editor.GamlEditor;
import msi.gama.application.workbench.ThemeHelper;
import ummisco.gama.ui.bindings.GamaKeyBindings;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.utils.PlatformHelper;
import ummisco.gama.ui.views.toolbar.GamaToolbarSimple;

/**
 * The class EditToolbarFindControls.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */
public class EditorSearchControls {

	/** The Constant EMPTY. */
	static final String EMPTY = "Find... (" + GamaKeyBindings.format(SWT.MOD1, 'G') + ")"; //$NON-NLS-1$

	/** The find. */
	Text find;

	/** The incremental offset. */
	int incrementalOffset = -1;

	/** The editor. */
	final GamlEditor editor;

	/**
	 * Instantiates a new editor search controls.
	 *
	 * @param editor
	 *            the editor
	 */
	public EditorSearchControls(final GamlEditor editor) {
		this.editor = editor;
	}

	/**
	 * Fill.
	 *
	 * @param toolbar
	 *            the toolbar
	 * @return the editor search controls
	 */
	public EditorSearchControls fill(final GamaToolbarSimple toolbar) {
		Composite parent = toolbar;
		Color c = parent.getBackground();
		if (PlatformHelper.isWindows()) {
			parent = new Composite(toolbar, SWT.NONE);
			final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			data.heightHint = 24;
			data.widthHint = 100;
			parent.setLayoutData(data);
			final GridLayout layout = new GridLayout();
			parent.setLayout(layout);
			GamaColors.setBackground(parent, c);
		}
		find = new Text(parent, SWT.SEARCH | SWT.ICON_SEARCH);
		final IFocusService focusService = editor.getSite().getService(IFocusService.class);
		focusService.addFocusTracker(find, "search");

		final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 24;
		data.widthHint = 100;
		find.setLayoutData(data);
		// find.setBackground(IGamaColors.WHITE.color());
		// find.setForeground(IGamaColors.BLACK.color());
		find.setMessage(EMPTY);
		find.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				find.setText("");
			}

			@Override
			public void focusGained(final FocusEvent e) {
				adjustEnablement(false, null);
				incrementalOffset = -1;
			} 
		});
		GamaColors.setBackground(find, c);
		GamaColors.setForeground(find, ThemeHelper.isDark() ? IGamaColors.VERY_LIGHT_GRAY.color() : IGamaColors.VERY_DARK_GRAY.color());
		toolbar.control(parent == toolbar ? find : parent, 100);
		find.addModifyListener(modifyListener);
		find.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(final KeyEvent e) {}

			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.character == SWT.ESC) { editor.setFocus(); }
			}
		});
		this.adjustEnablement(false, null);
		return this;

	}

	/**
	 * Gets the find control.
	 *
	 * @return the find control
	 */
	public Text getFindControl() { return find; }

	/** The modify listener. */
	private final ModifyListener modifyListener = new ModifyListener() {

		private String lastText = EMPTY;

		@Override
		public void modifyText(final ModifyEvent e) {

			boolean wrap = true;
			final String text = find.getText();
			if (lastText.startsWith(text)) { wrap = false; }
			lastText = text;
			if (EMPTY.equals(text) || "".equals(text)) {
				adjustEnablement(false, null);
				final ISelectionProvider selectionProvider = editor.getSelectionProvider();
				if (selectionProvider != null) {
					final ISelection selection = selectionProvider.getSelection();
					if (selection instanceof TextSelection) {
						final ITextSelection textSelection = (ITextSelection) selection;
						selectionProvider.setSelection(new TextSelection(textSelection.getOffset(), 0));
					}
				}
			} else {
				find(true, true, wrap);
			}
		}
	};

	/**
	 * Adjust enablement.
	 *
	 * @param found
	 *            the found
	 * @param color
	 *            the color
	 */
	void adjustEnablement(final boolean found, final Color color) {
		if (color == null) {
			find.setForeground(IGamaColors.WIDGET_FOREGROUND.color());
		} else {
			find.setForeground(color);
		}
	}

	/**
	 * Find previous.
	 */
	public void findPrevious() {
		find(false);
	}

	/**
	 * Find next.
	 */
	public void findNext() {
		find(true);
	}

	/**
	 * Find.
	 *
	 * @param forward
	 *            the forward
	 */
	private void find(final boolean forward) {
		find(forward, false);
	}

	/**
	 * Find.
	 *
	 * @param forward
	 *            the forward
	 * @param incremental
	 *            the incremental
	 */
	private void find(final boolean forward, final boolean incremental) {
		find(forward, incremental, true, false);
	}

	/**
	 * Find.
	 *
	 * @param forward
	 *            the forward
	 * @param incremental
	 *            the incremental
	 * @param wrap
	 *            the wrap
	 */
	void find(final boolean forward, final boolean incremental, final boolean wrap) {
		find(forward, incremental, wrap, false);
	}

	/**
	 * Find.
	 *
	 * @param forward
	 *            the forward
	 * @param incremental
	 *            the incremental
	 * @param wrap
	 *            the wrap
	 * @param wrapping
	 *            the wrapping
	 */
	private void find(final boolean forward, final boolean incremental, final boolean wrap, final boolean wrapping) {

		final IFindReplaceTarget findReplaceTarget = editor.getAdapter(IFindReplaceTarget.class);
		if (findReplaceTarget != null) {
			try {
				final String findText = find.getText();
				if (findReplaceTarget instanceof IFindReplaceTargetExtension) {
					final IFindReplaceTargetExtension findReplaceTargetExtension =
							(IFindReplaceTargetExtension) findReplaceTarget;
					findReplaceTargetExtension.beginSession();
				}
				final ISourceViewer sourceViewer = getSourceViewer();
				final StyledText textWidget = sourceViewer.getTextWidget();
				int offset = textWidget.getCaretOffset();
				Point selection = textWidget.getSelection();
				if (wrapping) {
					if (forward) {
						offset = 0;
					} else {
						offset = sourceViewer.getDocument().getLength() - 1;
					}
				} else if (forward) {
					if (incremental) {
						if (incrementalOffset == -1) {
							incrementalOffset = offset;
						} else {
							offset = incrementalOffset;
						}
					} else {
						incrementalOffset = selection.x;
					}
				} else {
					incrementalOffset = selection.x;
					if (selection.x != offset) { offset = selection.x; }
				}
				int newOffset = -1;
				if (findReplaceTarget instanceof IFindReplaceTargetExtension3) {
					newOffset = ((IFindReplaceTargetExtension3) findReplaceTarget).findAndSelect(offset, findText,
							forward, false, false, false);

				} else {
					newOffset = findReplaceTarget.findAndSelect(offset, findText, forward, false, false);
				}

				if (newOffset != -1) {
					adjustEnablement(true, IGamaColors.OK.inactive());
					selection = textWidget.getSelection();
					if (!forward) { incrementalOffset = selection.x; }
				} else {
					if (wrap && !wrapping) {
						find(forward, incremental, wrap, true);
						return;
					}
					if (!EMPTY.equals(findText) && !"".equals(findText)) {
						adjustEnablement(false, IGamaColors.ERROR.inactive());
					}
				}
			} finally {

				if (findReplaceTarget instanceof IFindReplaceTargetExtension) {
					final IFindReplaceTargetExtension findReplaceTargetExtension =
							(IFindReplaceTargetExtension) findReplaceTarget;
					findReplaceTargetExtension.endSession();
				}
			}
		}
	}

	/**
	 * @return the sourceView of the active textEditor
	 */
	private ISourceViewer getSourceViewer() { return (ISourceViewer) editor.getAdapter(ITextOperationTarget.class); }
}
