/*******************************************************************************************************
 *
 * GamlQuickfixProvider.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package msi.gama.lang.gaml.ui.quickfix;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IModification;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.validation.Issue;

import msi.gama.common.interfaces.IGamlIssue;
import ummisco.gama.ui.commands.FileOpener;

/**
 * The Class GamlQuickfixProvider.
 */
public class GamlQuickfixProvider extends DefaultQuickfixProvider {

	/**
	 * The Class Replace.
	 */
	private static class Replace implements IModification {

		/** The length. */
		final protected int length;

		/** The offset. */
		final protected int offset;

		/** The text. */
		final protected String text;

		/**
		 * Instantiates a new replace.
		 *
		 * @param offset the offset
		 * @param length the length
		 * @param text the text
		 */
		Replace(final int offset, final int length, final String text) {
			this.length = length;
			this.offset = offset;
			this.text = text;
		}

		@Override
		public void apply(final IModificationContext context) throws BadLocationException {
			final IXtextDocument xtextDocument = context.getXtextDocument();
			xtextDocument.replace(offset, length, text);
		}

	}

	/**
	 * The Class Surround.
	 */
	private static class Surround extends Replace {

		/** The suffix. */
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
			final IXtextDocument xtextDocument = context.getXtextDocument();
			final String tmp = text + xtextDocument.get(offset, length) + suffix;
			xtextDocument.replace(offset, length, tmp);
		}

	}

	/**
	 * Removes the issue.
	 *
	 * @param label the label
	 * @param issue the issue
	 * @param acceptor the acceptor
	 */
	public void removeIssue(final String label, final Issue issue, final IssueResolutionAcceptor acceptor) {
		acceptor.accept(issue, label, "", "", (IModification) context -> {
			final IXtextDocument xtextDocument = context.getXtextDocument();
			xtextDocument.replace(issue.getOffset(), issue.getLength(), "");
		});
	}

	/**
	 * Should cast.
	 *
	 * @param issue the issue
	 * @param acceptor the acceptor
	 */
	@Fix (IGamlIssue.SHOULD_CAST)
	public void shouldCast(final Issue issue, final IssueResolutionAcceptor acceptor) {
		final String[] data = issue.getData();
		if (data == null || data.length == 0) { return; }
		final String castingString = data[0];
		acceptor.accept(issue, "Cast the expression to " + castingString + "...", "", "",
				new Surround(issue.getOffset(), issue.getLength(), castingString + "(", ")"));
	}

	/**
	 * Adds the init.
	 *
	 * @param issue the issue
	 * @param acceptor the acceptor
	 */
	@Fix (IGamlIssue.NO_INIT)
	public void addInit(final Issue issue, final IssueResolutionAcceptor acceptor) {
		acceptor.accept(issue, "Add an init facet...", "", "", (IModification) context -> {
			final IXtextDocument doc = context.getXtextDocument();
			doc.replace(issue.getOffset() + issue.getLength() + 1, 0, " <- " + issue.getData()[0] + " ");

		});
	}

	/**
	 * As array.
	 *
	 * @param issue the issue
	 * @param acceptor the acceptor
	 */
	@Fix (IGamlIssue.AS_ARRAY)
	public void asArray(final Issue issue, final IssueResolutionAcceptor acceptor) {
		acceptor.accept(issue, "Enclose the skill in a list...", "", "",
				new Surround(issue.getOffset(), issue.getLength(), "[", "]"));
	}

	/**
	 * Goto import.
	 *
	 * @param issue the issue
	 * @param acceptor the acceptor
	 */
	@Fix (IGamlIssue.IMPORT_ERROR)
	public void gotoImport(final Issue issue, final IssueResolutionAcceptor acceptor) {
		final String[] data = issue.getData();
		if (data == null || data.length == 0) { return; }
		final String path = data[0];
		final URI uri = URI.createURI(path, false);
		acceptor.accept(issue, "Open " + uri.lastSegment() + "...", "Open file " + uri.lastSegment() + " to fix it", "",
				(IModification) context -> FileOpener.openFile(uri));

	}

	/**
	 * Replace value.
	 *
	 * @param issue the issue
	 * @param acceptor the acceptor
	 */
	@Fix (IGamlIssue.WRONG_VALUE)
	public void replaceValue(final Issue issue, final IssueResolutionAcceptor acceptor) {
		final String[] data = issue.getData();
		if (data == null || data.length == 0) { return; }
		final String value = data[0];
		acceptor.accept(issue, "Replace with " + value + "...", "", "",
				new Replace(issue.getOffset(), issue.getLength(), value));
	}

	/**
	 * Adds the facet.
	 *
	 * @param issue the issue
	 * @param acceptor the acceptor
	 */
	@Fix (IGamlIssue.MISSING_FACET)
	public void addFacet(final Issue issue, final IssueResolutionAcceptor acceptor) {
		return;
		// TODO
		// final String[] data = issue.getData();
		// if (data == null || data.length < 2) { return; }
		// final String facet = data[0];
		// final String value = data[1];
		// acceptor.accept(issue, "Add '" + facet + ": " + value + "' ...", "", "",
		// );
	}

}
