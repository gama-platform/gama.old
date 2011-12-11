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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * RNG seed strategy that gets data from {@literal /dev/random} on systems
 * that provide it (e.g. Solaris/Linux).  If {@literal /dev/random} does not
 * exist or is not accessible, a {@link SeedException} is thrown.
 * @author Daniel Dyer
 */
public class DevRandomSeedGenerator implements SeedGenerator
{
    private static final File DEV_RANDOM = new File("/dev/random");

    /**
     * {@inheritDoc}
     * @return The requested number of random bytes, read directly from
     * {@literal /dev/random}.
     * @throws SeedException If {@literal /dev/random} does not exist or is
     * not accessible
     */
    public byte[] generateSeed(int length) throws SeedException
    {
        FileInputStream file = null;
        try
        {
            file = new FileInputStream(DEV_RANDOM);
            byte[] randomSeed = new byte[length];
            int count = 0;
            while (count < length)
            {
                int bytesRead = file.read(randomSeed, count, length - count);
                if (bytesRead == -1)
                {
                    throw new SeedException("EOF encountered reading random data.");
                }
                count += bytesRead;
            }
            return randomSeed;
        }
        catch (IOException ex)
        {
            throw new SeedException("Failed reading from " + DEV_RANDOM.getName(), ex);
        }
        catch (SecurityException ex)
        {
            // Might be thrown if resource access is restricted (such as in
            // an applet sandbox).
            throw new SeedException("SecurityManager prevented access to " + DEV_RANDOM.getName(), ex);
        }
        finally
        {
            if (file != null)
            {
                try
                {
                    file.close();
                }
                catch (IOException ex)
                {
                    // Ignore.
                }
            }
        }
    }


    @Override
    public String toString()
    {
        return "/dev/random";
    }
}
