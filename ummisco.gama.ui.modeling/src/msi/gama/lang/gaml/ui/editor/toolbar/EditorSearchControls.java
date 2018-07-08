/*********************************************************************************************
 *
 * 'EditorSearchControls.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
import ummisco.gama.ui.bindings.GamaKeyBindings;
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

	private static final String EMPTY = "Find... (" + GamaKeyBindings.format(SWT.MOD1, 'G') + ")"; //$NON-NLS-1$
	private Text find;
	private int incrementalOffset = -1;
	final GamlEditor editor;

	public EditorSearchControls(final GamlEditor editor) {
		this.editor = editor;
	}

	public EditorSearchControls fill(final GamaToolbarSimple toolbar) {
		Composite parent = toolbar;
		if (PlatformHelper.isWin32()) ((ummisco.gama.ui.views.toolbar.GamaToolbar2) toolbar.getParent()).sep(4, SWT.RIGHT);
		if (PlatformHelper.isWin32()) {
			parent = new Composite(toolbar, SWT.NONE);
			final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			data.heightHint = 24;
			data.widthHint = 100;
			parent.setData(data);
			GridLayout layout = new GridLayout();
			parent.setLayout(layout);
		}
		find = new Text(parent, SWT.SEARCH | SWT.ICON_SEARCH);
		final IFocusService focusService = editor.getSite().getService(IFocusService.class);
		focusService.addFocusTracker(find, "search");

		final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 24;
		find.setLayoutData(data);
		find.setBackground(IGamaColors.WHITE.color());
		find.setForeground(IGamaColors.BLACK.color());
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

		toolbar.control(parent == toolbar ? find:parent, 100);
		find.addModifyListener(modifyListener);
		find.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(final KeyEvent e) {}

			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.character == SWT.ESC) {
					editor.setFocus();
				}
			}
		});
		this.adjustEnablement(false, null);
		return this;

	}

	public Text getFindControl() {
		return find;
	}

	private final ModifyListener modifyListener = new ModifyListener() {

		private String lastText = EMPTY;

		@Override
		public void modifyText(final ModifyEvent e) {

			boolean wrap = true;
			final String text = find.getText();
			if (lastText.startsWith(text)) {
				wrap = false;
			}
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

	private void adjustEnablement(final boolean found, final Color color) {
		//		final String text = find.getText();
		if (color == null) {
			find.setForeground(IGamaColors.VERY_LIGHT_GRAY.color());
			// composite.setBackground(IGamaColors.VERY_LIGHT_GRAY.color());
		} else {
			find.setForeground(color);
			// composite.setBackground(color);
		}
	}

	private void findPrevious() {
		find(false);
	}

	public void findNext() {
		find(true);
	}

	private void find(final boolean forward) {
		find(forward, false);
	}

	private void find(final boolean forward, final boolean incremental) {
		find(forward, incremental, true, false);
	}

	private void find(final boolean forward, final boolean incremental, final boolean wrap) {
		find(forward, incremental, wrap, false);
	}

	private void find(final boolean forward, final boolean incremental, final boolean wrap, final boolean wrapping) {

		final IFindReplaceTarget findReplaceTarget = (IFindReplaceTarget) editor.getAdapter(IFindReplaceTarget.class);
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
				} else {
					if (forward) {
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
						if (selection.x != offset) {
							offset = selection.x;
						}
					}
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
					if (!forward) {
						incrementalOffset = selection.x;
					}
				} else {
					if (wrap) {
						if (!wrapping) {
							find(forward, incremental, wrap, true);
							return;
						}
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
	private ISourceViewer getSourceViewer() {
		return (ISourceViewer) editor.getAdapter(ITextOperationTarget.class);
	}
}
