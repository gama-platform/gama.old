/*********************************************************************************************
 *
 * 'GamlQuickfixProvider.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

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

public class GamlQuickfixProvider extends DefaultQuickfixProvider {

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
			final IXtextDocument xtextDocument = context.getXtextDocument();
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
			final IXtextDocument xtextDocument = context.getXtextDocument();
			final String tmp = text + xtextDocument.get(offset, length) + suffix;
			xtextDocument.replace(offset, length, tmp);
		}

	}

	public void removeIssue(final String label, final Issue issue, final IssueResolutionAcceptor acceptor) {
		acceptor.accept(issue, label, "", "", (IModification) context -> {
			final IXtextDocument xtextDocument = context.getXtextDocument();
			xtextDocument.replace(issue.getOffset(), issue.getLength(), "");
		});
	}

	@Fix (IGamlIssue.SHOULD_CAST)
	public void shouldCast(final Issue issue, final IssueResolutionAcceptor acceptor) {
		final String[] data = issue.getData();
		if (data == null || data.length == 0) { return; }
		final String castingString = data[0];
		acceptor.accept(issue, "Cast the expression to " + castingString + "...", "", "",
				new Surround(issue.getOffset(), issue.getLength(), castingString + "(", ")"));
	}

	@Fix (IGamlIssue.AS_ARRAY)
	public void asArray(final Issue issue, final IssueResolutionAcceptor acceptor) {
		acceptor.accept(issue, "Enclose the skill in a list...", "", "",
				new Surround(issue.getOffset(), issue.getLength(), "[", "]"));
	}

	@Fix (IGamlIssue.IMPORT_ERROR)
	public void gotoImport(final Issue issue, final IssueResolutionAcceptor acceptor) {
		final String[] data = issue.getData();
		if (data == null || data.length == 0) { return; }
		final String path = data[0];
		final URI uri = URI.createURI(path, false);
		acceptor.accept(issue, "Open " + uri.lastSegment() + "...", "Open file " + uri.lastSegment() + " to fix it", "",
				(IModification) context -> FileOpener.openFile(uri));

	}

}
