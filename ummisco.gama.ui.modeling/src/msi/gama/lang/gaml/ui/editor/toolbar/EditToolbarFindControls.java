/*********************************************************************************************
 *
 * 'EditToolbarFindControls.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;

import msi.gama.lang.gaml.ui.editor.GamlEditor;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.views.toolbar.GamaToolbarSimple;

/**
 * The class EditToolbarFindControls.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */
public class EditToolbarFindControls {

	private static final String EMPTY = "Search..."; //$NON-NLS-1$
	private Composite composite;
	private Text find;
	private ToolItem previous;
	private ToolItem next;
	private ToolItem caseSensitive;
	private ToolItem wholeWord;
	private int incrementalOffset = -1;
	final GamlEditor editor;

	public EditToolbarFindControls(final GamlEditor editor) {
		this.editor = editor;
	}

	public void fill(final GamaToolbarSimple toolbar) {

		composite = new Composite(toolbar, SWT.NONE);
		composite.setBackground(IGamaColors.WHITE.color());
		final GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		previous = toolbar.button("editor.previous2", null, "Previous", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				findPrevious();
			}
		});

		find = new Text(composite, SWT.CENTER);
		// find.setFont(SwtGui.getLabelfont());
		find.setBackground(IGamaColors.BLUE.color());
		find.setForeground(IGamaColors.WHITE.color());
		final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.heightHint = 16;
		find.setLayoutData(data);
		find.setText(EMPTY);
		find.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				if (find.getText().isEmpty()) {
					find.setText(EMPTY);
				}
			}

			@Override
			public void focusGained(final FocusEvent e) {
				if (find.getText().equals(EMPTY)) {
					find.setText("");
				}
				adjustEnablement(false, null);
				incrementalOffset = -1;
			}
		});

		toolbar.control(composite, 100);

		next = toolbar.button("editor.next2", null, "Next", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				findNext();
			}

		});

		caseSensitive = toolbar.check("editor.case2", null, "Case sensitive", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				find(true, true);
			}
		});

		wholeWord = toolbar.check("editor.word2", null, "Whole word", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				toggleWholeWordMode();
			}
		});

		find.addModifyListener(modifyListener);
		// find.addKeyListener(new KeyListener() {
		//
		// @Override
		// public void keyReleased(final KeyEvent e) {}
		//
		// @Override
		// public void keyPressed(final KeyEvent e) {
		// if ( (e.stateMask & SWT.CTRL) != 0 || (e.stateMask & SWT.COMMAND) !=
		// 0 ) {
		// switch (e.character) {
		// case 'c':
		// find.copy();
		// e.doit = false;
		// break;
		// case 'v':
		// find.paste();
		// e.doit = false;
		// break;
		// case 'x':
		// find.cut();
		// e.doit = false;
		// }
		// }
		// }
		// });
		// toolbar.addMouseTrackListener(new MouseTrackListener() {
		//
		// IContextActivation ca;
		//
		// @Override
		// public void mouseHover(final MouseEvent e) {}
		//
		// @Override
		// public void mouseExit(final MouseEvent e) {
		// editor.getSite().getService(IContextService.class).deactivateContext(ca);
		// System.out.println("SearchText context deactivated");
		// ca = null;
		// }
		//
		// @Override
		// public void mouseEnter(final MouseEvent e) {
		// ca =
		// editor.getSite().getService(IContextService.class).activateContext("SearchText");
		// System.out.println("SearchText context activated");
		//
		// }
		// });
		// toolbar.addFocusListener(new FocusListener() {
		//
		// IContextActivation ca;
		//
		// @Override
		// public void focusLost(final FocusEvent e) {
		// editor.getSite().getService(IContextService.class).deactivateContext(ca);
		// System.out.println("SearchText context deactivated via focus");
		// ca = null;
		// }
		//
		// @Override
		// public void focusGained(final FocusEvent e) {
		// ca =
		// editor.getSite().getService(IContextService.class).activateContext("SearchText");
		// System.out.println("SearchText context activated via focus");
		//
		// }
		// });
		this.adjustEnablement(false, null);
		// IFocusService focusService =
		// editor.getSite().getService(IFocusService.class);
		// focusService.addFocusTracker(find, "msi.gama.editor.search");
		// //$NON-NLS-1$

	}

	private void toggleWholeWordMode() {
		if (wholeWord.isEnabled()) {
			find(true, true);
		}
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
		final String text = find.getText();
		previous.setEnabled(found);
		next.setEnabled(found);
		wholeWord.setEnabled(found && isWord(text));
		if (color == null) {
			composite.setBackground(IGamaColors.WHITE.color());
		} else {
			composite.setBackground(color);
		}
	}

	private void findPrevious() {
		find(false);
	}

	private void findNext() {
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
			// boolean foundOne = false;
			try {
				final String findText = find.getText();
				// final Pattern pattern = null;
				if (findReplaceTarget instanceof IFindReplaceTargetExtension) {
					final IFindReplaceTargetExtension findReplaceTargetExtension = (IFindReplaceTargetExtension) findReplaceTarget;
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
							forward, caseSensitive.getSelection(), wholeWord.getEnabled() && wholeWord.getSelection(),
							false
					/* regularExpression.getSelection() */);
				} else {
					newOffset = findReplaceTarget.findAndSelect(offset, findText, forward, caseSensitive.getSelection(),
							wholeWord.getEnabled() && wholeWord.getSelection());
				}

				if (newOffset != -1) {
					// foundOne = true;
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
						// parent.layout();
						// parent.update();
					}
				}
			} finally {

				if (findReplaceTarget instanceof IFindReplaceTargetExtension) {
					final IFindReplaceTargetExtension findReplaceTargetExtension = (IFindReplaceTargetExtension) findReplaceTarget;
					findReplaceTargetExtension.endSession();
				}
			}
		}
	}

	/**
	 * Tests whether each character in the given string is a letter.
	 *
	 * @param str
	 *            the string to check
	 * @return <code>true</code> if the given string is a word
	 */
	private boolean isWord(final String str) {
		if (str == null || str.length() == 0) {
			return false;
		}

		for (int i = 0; i < str.length(); i++) {
			if (!Character.isJavaIdentifierPart(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the sourceView of the active textEditor
	 */
	private ISourceViewer getSourceViewer() {
		return (ISourceViewer) editor.getAdapter(ITextOperationTarget.class);
	}
}
