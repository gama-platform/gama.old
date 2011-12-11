/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
//   Copyright 2006, 2007, 2008, 2008 Daniel W. Dyer
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
package org.uncommons.maths;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Maths operations not provided by {@link Math java.lang.Math}.
 * @author Daniel Dyer
 */
public final class Maths
{
    // The biggest factorial that can be calculated using 64-bit signed longs.
    private static final int MAX_LONG_FACTORIAL = 20;

    // Cache BigInteger factorial values because they are expensive to generate.
    private static final int CACHE_SIZE = 256;
    private static final ConcurrentMap<Integer, BigInteger> BIG_FACTORIALS
        = new ConcurrentHashMap<Integer, BigInteger>();

    private Maths()
    {
        // Prevent instantiation.
    }


    /**
     * Calculates the factorial of n where n is a number in the
     * range 0 - 20.  Zero factorial is equal to 1.  For values of
     * n greater than 20 you must use {@link #bigFactorial(int)}.
     * @param n The factorial to calculate.
     * @return The factorial of n.
     * @see #bigFactorial(int)
     */
    public static long factorial(int n)
    {
        if (n < 0 || n > MAX_LONG_FACTORIAL)
        {
            throw new IllegalArgumentException("Argument must be in the range 0 - 20.");
        }
        long factorial = 1;
        for (int i = n; i > 1; i--)
        {
            factorial *= i;
        }
        return factorial;
    }


    /**
     * Calculates the factorial of n where n is a positive integer.
     * Zero factorial is equal to 1.  For values of n up to 20, consider
     * using {@link #factorial(int)} instead since it uses a faster
     * implementation.
     * @param n The factorial to calculate.
     * @return The factorial of n.
     * @see #factorial(int)
     */
    public static BigInteger bigFactorial(int n)
    {
        if (n < 0)
        {
            throw new IllegalArgumentException("Argument must greater than or equal to zero.");
        }

        BigInteger factorial = null;
        if (n < CACHE_SIZE) // Check for a cached value.
        {
            factorial = BIG_FACTORIALS.get(n);
        }

        if (factorial == null)
        {
            factorial = BigInteger.ONE;
            for (int i = n; i > 1; i--)
            {
                factorial = factorial.multiply(BigInteger.valueOf(i));
            }
            if (n < CACHE_SIZE) // Cache value.
            {
                BIG_FACTORIALS.putIfAbsent(n, factorial);
            }
        }

        return factorial;
    }


    /**
     * Calculate the first argument raised to the power of the second.
     * This method only supports non-negative powers.
     * @param value The number to be raised.
     * @param power The exponent (must be positive).
     * @return {@code value} raised to {@code power}.
     */
    public static long raiseToPower(int value, int power)
    {
        if (power < 0)
        {
            throw new IllegalArgumentException("This method does not support negative powers.");
        }
        long result = 1;
        for (int i = 0; i < power; i++)
        {
            result *= value;
        }
        return result;
    }


    /**
     * Calculate logarithms for arbitrary bases.
     * @param base The base for the logarithm.
     * @param arg The value to calculate the logarithm for.
     * @return The log of {@code arg} in the specified {@code base}.
     */
    public static double log(double base, double arg)
    {
        // Use natural logarithms and change the base.
        return Math.log(arg) / Math.log(base);
    }


    /**
     * Checks that two values are approximately equal (plus or minus a specified tolerance).
     * @param value1 The first value to compare.
     * @param value2 The second value to compare.
     * @param tolerance How much (in percentage terms, as a percentage of the first value)
     * the values are allowed to differ and still be considered equal.  Expressed as a value
     * between 0 and 1.
     * @return true if the values are approximately equal, false otherwise.
     */
    public static boolean approxEquals(double value1,
                                       double value2,
                                       double tolerance)
    {
        if (tolerance < 0 || tolerance > 1)
        {
            throw new IllegalArgumentException("Tolerance must be between 0 and 1.");
        }
        return Math.abs(value1 - value2) <= value1 * tolerance;
    }


    /**
     * If the specified value is not greater than or equal to the specified minimum and
     * less than or equal to the specified maximum, adjust it so that it is.
     * @param value The value to check.
     * @param min The minimum permitted value.
     * @param max The maximum permitted value.
     * @return {@code value} if it is between the specified limits, {@code min} if the value
     * is too low, or {@code max} if the value is too high.
     * @since 1.2
     */
    public static int restrictRange(int value, int min, int max)
    {
        return Math.min((Math.max(value, min)), max);
    }


    /**
     * If the specified value is not greater than or equal to the specified minimum and
     * less than or equal to the specified maximum, adjust it so that it is.
     * @param value The value to check.
     * @param min The minimum permitted value.
     * @param max The maximum permitted value.
     * @return {@code value} if it is between the specified limits, {@code min} if the value
     * is too low, or {@code max} if the value is too high.
     * @since 1.2
     */
    public static long restrictRange(long value, long min, long max)
    {
        return Math.min((Math.max(value, min)), max);
    }


    /**
     * If the specified value is not greater than or equal to the specified minimum and
     * less than or equal to the specified maximum, adjust it so that it is.
     * @param value The value to check.
     * @param min The minimum permitted value.
     * @param max The maximum permitted value.
     * @return {@code value} if it is between the specified limits, {@code min} if the value
     * is too low, or {@code max} if the value is too high.
     * @since 1.2
     */
    public static double restrictRange(double value, double min, double max)
    {
        return Math.min((Math.max(value, min)), max);
    }


    /**
     * Determines the greatest common divisor of a pair of natural numbers
     * using the Euclidean algorithm.  This method only works with natural
     * numbers.  If negative integers are passed in, the absolute values will
     * be used.  The return value is always positive.
     * @param a The first value.
     * @param b The second value.
     * @return The greatest common divisor.
     * @since 1.2
     */
    public static long greatestCommonDivisor(long a, long b)
    {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0)
        {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
