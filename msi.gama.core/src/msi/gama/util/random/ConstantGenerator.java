/*********************************************************************************************
 * 
 *
 * 'ConstantGenerator.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit http://gama-platform.googlecode.com for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package msi.gama.util.random;


/**
 * Convenience implementation of {@link NumberGenerator} that always
 * returns the same value.
 * @param <T> The numeric type (Integer, Long, Double, etc.) of the constant.
 * @author Daniel Dyer
 */
public class ConstantGenerator<T extends Number> implements NumberGenerator<T> {

	private final T constant;

	/**
	 * Creates a number generator that always returns the same
	 * values.
	 * @param constant The value to be returned by all invocations
	 *            of the {@link #nextValue()} method.
	 */
	public ConstantGenerator(final T constant) {
		this.constant = constant;
	}

	/**
	 * @return The constant value specified when the generator was constructed.
	 */
	@Override
	public T nextValue() {
		return constant;
	}
}
