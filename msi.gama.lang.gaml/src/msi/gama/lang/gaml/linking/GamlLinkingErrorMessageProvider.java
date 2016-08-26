package msi.gama.lang.gaml.linking;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.diagnostics.DiagnosticMessage;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.linking.impl.IllegalNodeException;
import org.eclipse.xtext.linking.impl.LinkingDiagnosticMessageProvider;

public class GamlLinkingErrorMessageProvider extends LinkingDiagnosticMessageProvider {

	@Override
	public DiagnosticMessage getUnresolvedProxyMessage(final ILinkingDiagnosticContext context) {
		final EClass referenceType = context.getReference().getEReferenceType();
		String linkText = "";
		try {
			linkText = context.getLinkText();
		} catch (final IllegalNodeException e) {
			linkText = e.getNode().getText();
		}
		final String msg = "Couldn't resolve reference to " + referenceType.getName() + " '" + linkText + "'.";
		return new DiagnosticMessage(msg, Severity.ERROR, Diagnostic.LINKING_DIAGNOSTIC);
	}

	@Override
	public DiagnosticMessage getIllegalNodeMessage(final ILinkingDiagnosticContext context,
			final IllegalNodeException ex) {
		final String message = ex.getMessage();
		return new DiagnosticMessage(message, Severity.ERROR, Diagnostic.LINKING_DIAGNOSTIC);
	}

	@Override
	public DiagnosticMessage getIllegalCrossReferenceMessage(final ILinkingDiagnosticContext context,
			final CrossReference reference) {
		final String message = "Cannot find reference " + reference;
		return new DiagnosticMessage(message, Severity.ERROR, Diagnostic.LINKING_DIAGNOSTIC);
	}

	@Override
	public DiagnosticMessage getViolatedBoundsConstraintMessage(final ILinkingDiagnosticContext context,
			final int size) {
		final String message = "Too many matches for reference to '" + context.getLinkText() + "'. " + "Feature "
				+ context.getReference().getName() + " can only hold " + context.getReference().getUpperBound()
				+ " reference" + (context.getReference().getUpperBound() != 1 ? "s" : "") + " but found " + size
				+ " candidate" + (size != 1 ? "s" : "");
		return new DiagnosticMessage(message, Severity.ERROR, Diagnostic.LINKING_DIAGNOSTIC);
	}

	@Override
	public DiagnosticMessage getViolatedUniqueConstraintMessage(final ILinkingDiagnosticContext context) {
		final String message = "Cannot refer to '" + context.getLinkText() + "' more than once.";
		return new DiagnosticMessage(message, Severity.ERROR, Diagnostic.LINKING_DIAGNOSTIC);
	}

}
