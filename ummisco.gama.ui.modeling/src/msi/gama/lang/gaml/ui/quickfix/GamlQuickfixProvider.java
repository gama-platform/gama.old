/*********************************************************************************************
 * 
 * 
 * 'GamlQuickfixProvider.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/

package msi.gama.lang.gaml.ui.quickfix;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.lang.gaml.ui.utils.FileOpener;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.*;
import org.eclipse.xtext.ui.editor.quickfix.*;
import org.eclipse.xtext.validation.Issue;
import com.google.inject.Inject;

public class GamlQuickfixProvider extends DefaultQuickfixProvider {

	// public static final String QF_NOTFACETOFKEY = "NOTFACETOFKEY";
	// public static final String QF_UNKNOWNFACET = "UNKNOWNFACET";
	// public static final String QF_KEYHASNOFACET = "KEYHASNOFACET";
	// public static final String QF_NOTKEYOFCONTEXT = "NOTKEYOFCONTEXT";
	// public static final String QF_NOTKEYOFMODEL = "NOTKEYOFMODEL";
	// public static final String QF_INVALIDSETVAR = "INVALIDSETVAR";
	// public static final String QF_BADEXPRESSION = "QF_BADEXPRESSION";

	// @Fix(MyJavaValidator.INVALID_NAME)
	// public void capitalizeName(final Issue issue, IssueResolutionAcceptor acceptor) {
	// acceptor.accept(issue, "Capitalize name", "Capitalize the name.", "upcase.png", new
	// IModification() {
	// public void apply(IModificationContext context) throws BadLocationException {
	// IXtextDocument xtextDocument = context.getXtextDocument();
	// String firstLetter = xtextDocument.get(issue.getOffset(), 1);
	// xtextDocument.replace(issue.getOffset(), 1, firstLetter.toUpperCase());
	// }
	// });
	// }

	@Inject
	private FileOpener fileOpener;

	private static class Replace implements IModification {

		final protected int length;

		final protected int offset;

		final protected String text;

		Replace(final int offset, final int length, final String text) {
			this.length = length;
			this.offset = offset;
			this.text = text;
		}

		@Override
		public void apply(final IModificationContext context) throws BadLocationException {
			IXtextDocument xtextDocument = context.getXtextDocument();
			xtextDocument.replace(offset, length, text);
		}

	}

	private static class Surround extends Replace {

		private final String suffix;

		/**
		 * @param offset
		 * @param length
		 * @param text
		 *            text used before and after the replaced text
		 */
		Surround(final int offset, final int length, final String text) {
			super(offset, length, text);
			suffix = text;
		}

		/**
		 * Surrounds text with prefix, suffix
		 * 
		 * @param offset
		 *            start of section to surround
		 * @param length
		 *            length of section to surround
		 * @param prefix
		 *            text before the section
		 * @param suffix
		 *            text after the section
		 */
		Surround(final int offset, final int length, final String prefix, final String suffix) {
			super(offset, length, prefix);
			this.suffix = suffix;
		}

		@Override
		public void apply(final IModificationContext context) throws BadLocationException {
			IXtextDocument xtextDocument = context.getXtextDocument();
			String tmp = text + xtextDocument.get(offset, length) + suffix;
			xtextDocument.replace(offset, length, tmp);
		}

	}

	public void removeIssue(final String label, final Issue issue, final IssueResolutionAcceptor acceptor) {
		acceptor.accept(issue, label, "", "", new IModification() {

			@Override
			public void apply(final IModificationContext context) throws BadLocationException {
				IXtextDocument xtextDocument = context.getXtextDocument();
				xtextDocument.replace(issue.getOffset(), issue.getLength(), "");
			}
		});
	}

	@Fix(IGamlIssue.SHOULD_CAST)
	public void shouldCast(final Issue issue, final IssueResolutionAcceptor acceptor) {
		String[] data = issue.getData();
		if ( data == null || data.length == 0 ) { return; }
		String castingString = data[0];
		acceptor.accept(issue, "Cast the expression to " + castingString + "...", "", "",
			new Surround(issue.getOffset(), issue.getLength(), castingString + "(", ")"));
	}

	@Fix(IGamlIssue.AS_ARRAY)
	public void asArray(final Issue issue, final IssueResolutionAcceptor acceptor) {
		acceptor.accept(issue, "Enclose the skill in a list...", "", "",
			new Surround(issue.getOffset(), issue.getLength(), "[", "]"));
	}

	@Fix(IGamlIssue.IMPORT_ERROR)
	public void gotoImport(final Issue issue, final IssueResolutionAcceptor acceptor) {
		String[] data = issue.getData();
		if ( data == null || data.length == 0 ) { return; }
		final String path = data[0];
		final URI uri = URI.createURI(path, false);
		acceptor.accept(issue, "Open " + uri.lastSegment() + "...", "Open file " + uri.lastSegment() + " to fix it",
			"", new IModification() {

				@Override
				public void apply(final IModificationContext context) throws Exception {
					fileOpener.openFileInWorkspace(uri);
				}

			});

	}

	// @Fix(QF_NOTKEYOFMODEL)
	// public void fixKeyOfModel(final Issue issue, final IssueResolutionAcceptor acceptor) {
	// removeIssue("Remove this keyword", issue, acceptor);
	// }
	//
	// @Fix(QF_NOTKEYOFCONTEXT)
	// public void fixKeyOfContext(final Issue issue, final IssueResolutionAcceptor acceptor) {
	// removeIssue("Remove this keyword", issue, acceptor);
	// }
	//
	// @Fix(QF_NOTFACETOFKEY)
	// public void fixFacetOfKey(final Issue issue, final IssueResolutionAcceptor acceptor) {
	// removeIssue("Remove this facet", issue, acceptor);
	// }

}
