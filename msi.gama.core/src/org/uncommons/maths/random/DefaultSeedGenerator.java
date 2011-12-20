/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
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

import org.uncommons.maths.binary.BinaryUtils;

/**
 * Seed generator that maintains multiple strategies for seed
 * generation and will delegate to the best one available for the
 * current operating environment.
 * @author Daniel Dyer
 */
public final class DefaultSeedGenerator implements SeedGenerator {

	private static final String DEBUG_PROPERTY = "org.uncommons.maths.random.debug";

	/** Singleton instance. */
	private static final DefaultSeedGenerator INSTANCE = new DefaultSeedGenerator();

	/** Delegate generators. */
	private static final SeedGenerator[] GENERATORS = new SeedGenerator[] {
		new DevRandomSeedGenerator(),
		/* new RandomDotOrgSeedGenerator(), */
		new SecureRandomSeedGenerator() };

	private DefaultSeedGenerator() {
		// Private constructor prevents external instantiation.
	}

	/**
	 * @return The singleton instance of this class.
	 */
	public static DefaultSeedGenerator getInstance() {
		return INSTANCE;
	}

	/**
	 * Generates a seed by trying each of the available strategies in
	 * turn until one succeeds. Tries the most suitable strategy first
	 * and eventually degrades to the least suitable (but guaranteed to
	 * work) strategy.
	 * @param length The length (in bytes) of the seed.
	 * @return A random seed of the requested length.
	 */
	@Override
	public byte[] generateSeed(final int length) {
		for ( SeedGenerator generator : GENERATORS ) {
			try {
				byte[] seed = generator.generateSeed(length);
				try {
					boolean debug = System.getProperty(DEBUG_PROPERTY, "false").equals("true");
					if ( debug ) {
						String seedString = BinaryUtils.convertBytesToHexString(seed);
						System.out.println(seed.length + " bytes of seed data acquired from " +
							generator + ":");
						System.out.println("  " + seedString);
					}
				} catch (SecurityException ex) {
					// Ignore, means we can't read the property so just default to false.
				}

				return seed;
			} catch (SeedException ex) {
				// Ignore and try the next generator...
			}
		}
		// This shouldn't happen as at least one the generators should be
		// able to generate a seed.
		throw new IllegalStateException("All available seed generation strategies failed.");
	}
}
