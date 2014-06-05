/*********************************************************************************************
 * 
 * 
 * 'SeedGenerator.java', in plugin 'msi.gama.core', is part of the source code of the
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
package org.uncommons.maths.random;

/**
 * Strategy interface for seeding random number generators.
 * @author Daniel Dyer
 */
public interface SeedGenerator {

	/**
	 * Generate a seed value for a random number generator.
	 * @param length The length of the seed to generate (in bytes).
	 * @return A byte array containing the seed data.
	 * @throws SeedException If a seed cannot be generated for any reason.
	 */
	byte[] generateSeed(int length);
}
