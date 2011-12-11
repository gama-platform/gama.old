/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
//   Copyright 2006-2010 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.maths.random;

import java.security.SecureRandom;

/**
 * <p>{@link SeedGenerator} implementation that uses Java's bundled
 * {@link SecureRandom} RNG to generate random seed data.</p>
 *
 * <p>The advantage of using SecureRandom for seeding but not as the
 * primary RNG is that we can use it to seed RNGs that are much faster
 * than SecureRandom.</p>
 *
 * <p>This is the only seeding strategy that is guaranteed to work on all
 * platforms and therefore is provided as a fall-back option should
 * none of the other provided {@link SeedGenerator} implementations be
 * useable.</p>
 * @author Daniel Dyer
 */
public class SecureRandomSeedGenerator implements SeedGenerator
{
    private static final SecureRandom SOURCE = new SecureRandom();

    /**
     * {@inheritDoc}
     */
    public byte[] generateSeed(int length) throws SeedException
    {
        return SOURCE.generateSeed(length);
    }


    @Override
    public String toString()
    {
        return "java.security.SecureRandom";
    }
}
