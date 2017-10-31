package msi.gaml.statements.test;

import java.util.Collection;

import org.eclipse.emf.common.util.URI;

public interface WithTestSummary<T extends AbstractSummary<?>> {

	T getSummary();

	String getTitleForSummary();

	URI getURI();

	Collection<? extends WithTestSummary<?>> getSubElements();

}
