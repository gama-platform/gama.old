/**
 * Created by drogoul, 3 déc. 2015
 *
 */
package ummisco.gama.ui.modeling.folding;

import java.util.Collection;
import java.util.regex.Matcher;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.xtext.ui.editor.folding.DefaultFoldedPosition;
import org.eclipse.xtext.ui.editor.folding.DefaultFoldingRegionAcceptor;
import org.eclipse.xtext.ui.editor.folding.DefaultFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.folding.FoldedPosition;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionAcceptor;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionAcceptorExtension;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.util.TextRegion;

/**
 * The class GamaFoldingRegionProvider.
 *
 * @author drogoul
 * @since 3 déc. 2015
 *
 */
public class GamaFoldingRegionProvider extends DefaultFoldingRegionProvider {

	public class TypedFoldedPosition extends DefaultFoldedPosition {
		String type;

		public TypedFoldedPosition(final int offset, final int length, final int contentStart, final int contentLength,
				final String type) {
			super(offset, length, contentStart, contentLength);
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}

	private class GamaFoldingRegionAcceptor extends DefaultFoldingRegionAcceptor {

		String type;

		public GamaFoldingRegionAcceptor(final IXtextDocument document, final Collection<FoldedPosition> result) {
			super(document, result);
		}

		@Override
		protected FoldedPosition newFoldedPosition(final IRegion region, final ITextRegion significantRegion) {
			FoldedPosition result = null;
			if (region != null) {
				if (type != null && significantRegion != null) {
					result = new TypedFoldedPosition(region.getOffset(), region.getLength(),
							significantRegion.getOffset() - region.getOffset() - 1, significantRegion.getLength(),
							type);
				} else {
					result = super.newFoldedPosition(region, significantRegion);
				}
			}
			return result;
		}

	}

	@Override
	protected IFoldingRegionAcceptor<ITextRegion> createAcceptor(final IXtextDocument xtextDocument,
			final Collection<FoldedPosition> foldedPositions) {
		return new GamaFoldingRegionAcceptor(xtextDocument, foldedPositions);
	}

	@Override
	protected void computeCommentFolding(final IXtextDocument xtextDocument,
			final IFoldingRegionAcceptor<ITextRegion> foldingRegionAcceptor, final ITypedRegion typedRegion,
			final boolean initiallyFolded) throws BadLocationException {
		final int offset = typedRegion.getOffset();
		final int length = typedRegion.getLength();
		final Matcher matcher = getTextPatternInComment().matcher(xtextDocument.get(offset, length));
		((GamaFoldingRegionAcceptor) foldingRegionAcceptor).type = typedRegion.getType();
		if (matcher.find()) {
			final TextRegion significant = new TextRegion(offset + matcher.start(), 0);
			((IFoldingRegionAcceptorExtension<ITextRegion>) foldingRegionAcceptor).accept(offset, length,
					initiallyFolded, significant);
		} else {
			((IFoldingRegionAcceptorExtension<ITextRegion>) foldingRegionAcceptor).accept(offset, length,
					initiallyFolded);
		}
	}

}
