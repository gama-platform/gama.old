/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package msi.gama.lang.gaml.linking;

import static com.google.common.base.Objects.equal;
import static java.util.Arrays.copyOf;
import org.eclipse.xtext.diagnostics.AbstractDiagnostic;
import org.eclipse.xtext.nodemodel.INode;
import com.google.common.base.Objects;

/**
 * <code>{@link Diagnostic}</code> that supports appending text to its message.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 * @adaptation Alexis Drogoul
 */
public class GamlDiagnostic extends AbstractDiagnostic {

	private final String code;
	private final String[] data;
	private final StringBuilder message;
	private final INode node;

	public GamlDiagnostic(final String code, final String[] data, final String message,
		final INode node) {
		if ( node == null || code == null || data == null || message == null ) { throw new NullPointerException(
			"An incorrect diagnostic has been emitted. Please debug to find the root cause."); }
		this.code = code;
		this.data = copyOf(data, data.length);
		this.message = new StringBuilder(message);
		this.node = node;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String[] getData() {
		return copyOf(data, data.length);
	}

	@Override
	public String getMessage() {
		return message.toString();
	}

	@Override
	protected INode getNode() {
		return node;
	}

	/**
	 * Appends the given text to this diagnostic's message.
	 * @param s the text to append.
	 */
	public void appendToMessage(final String s) {
		message.append(s);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(message, node);
	}

	@Override
	public boolean equals(final Object obj) {
		if ( this == obj ) { return true; }
		if ( obj == null || getClass() != obj.getClass() ) { return false; }
		GamlDiagnostic other = (GamlDiagnostic) obj;
		if ( !equal(message, other.message) ) { return false; }
		return equal(node, other.node);
	}
}
