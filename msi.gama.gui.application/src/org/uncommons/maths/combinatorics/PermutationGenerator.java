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
package org.uncommons.maths.combinatorics;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.uncommons.maths.Maths;

/**
 * Permutation generator for generating all permutations for all sets up to
 * 20 elements in size.  While 20 may seem a low limit, bear in mind that
 * the number of permutations of a set of size n is n!  For a set of 21
 * items, the number of permutations is bigger than can be stored in Java's
 * 64-bit long integer data type.  Therefore it seems unlikely that you
 * could ever generate, let alone process, all of the permutations in a
 * reasonable time frame.  For this reason the implementation is optimised for
 * sets of size 20 or less (this affords better performance by allowing primitive
 * numeric types to be used for calculations rather than
 * {@link java.math.BigInteger}).
 * @param <T> The type of element that the permutation will consist of.
 * @author Daniel Dyer (modified from the original version written by Michael
 * Gilleland of Merriam Park Software -
 * <a href="http://www.merriampark.com/perm.htm">http://www.merriampark.com/perm.htm</a>).
 * @see CombinationGenerator
 */
public class PermutationGenerator<T> implements Iterable<List<T>>
{
    private final T[] elements;
    private final int[] permutationIndices;
    private long remainingPermutations;
    private long totalPermutations;


    /**
     * Permutation generator that generates all possible orderings of
     * the elements in the specified set.
     * @param elements The elements to permute.
     */
    public PermutationGenerator(T[] elements)
    {
        if (elements.length > 20)
        {
            throw new IllegalArgumentException("Size must be less than or equal to 20.");
        }
        this.elements = elements.clone();
        permutationIndices = new int[elements.length];
        totalPermutations = Maths.factorial(elements.length);
        reset();
    }


    /**
     * Permutation generator that generates all possible orderings of
     * the elements in the specified set.
     * @param elements The elements to permute.
     */
    @SuppressWarnings("unchecked")
    public PermutationGenerator(Collection<T> elements)
    {
        this(elements.toArray((T[]) new Object[elements.size()]));
    }


    /**
     * Resets the generator state.
     */
    public final void reset()
    {
        for (int i = 0; i < permutationIndices.length; i++)
        {
            permutationIndices[i] = i;
        }
        remainingPermutations = totalPermutations;
    }


    /**
     * Returns the number of permutations not yet generated.
     * @return The number of unique permutations still to be generated.
     */
    public long getRemainingPermutations()
    {
        return remainingPermutations;
    }


    /**
     * Returns the total number of unique permutations that can be
     * generated for the given set of elements.
     * @return The total number of permutations.
     */
    public long getTotalPermutations()
    {
        return totalPermutations;
    }


    /**
     * Are there more permutations that have not yet been returned?
     * @return true if there are more permutations, false otherwise.
     */
    public boolean hasMore()
    {
        return remainingPermutations > 0;
    }


    /**
     * Generate the next permutation and return an array containing
     * the elements in the appropriate order.
     * @see #nextPermutationAsArray(Object[])
     * @see #nextPermutationAsList()
     * @return The next permutation as an array.
     */
    @SuppressWarnings("unchecked")
    public T[] nextPermutationAsArray()
    {
        T[] permutation = (T[]) Array.newInstance(elements.getClass().getComponentType(),
                                                  permutationIndices.length);
        return nextPermutationAsArray(permutation);
    }


    /**
     * Generate the next permutation and return an array containing
     * the elements in the appropriate order.  This overloaded method
     * allows the caller to provide an array that will be used and returned.
     * The purpose of this is to improve performance when iterating over
     * permutations.  If the {@link #nextPermutationAsArray()} method is
     * used it will create a new array every time.  When iterating over
     * permutations this will result in lots of short-lived objects that
     * have to be garbage collected.  This method allows a single array
     * instance to be reused in such circumstances.
     * @param destination Provides an array to use to create the
     * permutation.  The specified array must be the same length as a
     * permutation.  This is the array that will be returned, once
     * it has been filled with the elements in the appropriate order.
     * @return The next permutation as an array.
     */
    public T[] nextPermutationAsArray(T[] destination)
    {
        if (destination.length != elements.length)
        {
            throw new IllegalArgumentException("Destination array must be the same length as permutations.");
        }
        generateNextPermutationIndices();
        // Generate actual permutation.
        for (int i = 0; i < permutationIndices.length; i++)
        {
            destination[i] = elements[permutationIndices[i]];
        }
        return destination;
    }


    /**
     * Generate the next permutation and return a list containing
     * the elements in the appropriate order.
     * @see #nextPermutationAsList(java.util.List)
     * @see #nextPermutationAsArray()
     * @return The next permutation as a list.
     */
    public List<T> nextPermutationAsList()
    {
        List<T> permutation = new ArrayList<T>(elements.length);
        return nextPermutationAsList(permutation);
    }


    /**
     * Generate the next permutation and return a list containing
     * the elements in the appropriate order.  This overloaded method
     * allows the caller to provide a list that will be used and returned.
     * The purpose of this is to improve performance when iterating over
     * permutations.  If the {@link #nextPermutationAsList()} method is
     * used it will create a new list every time.  When iterating over
     * permutations this will result in lots of short-lived objects that
     * have to be garbage collected.  This method allows a single list
     * instance to be reused in such circumstances.
     * @param destination Provides a list to use to create the
     * permutation.  This is the list that will be returned, once
     * it has been filled with the elements in the appropriate order.
     * @return The next permutation as a list.
     */
    public List<T> nextPermutationAsList(List<T> destination)
    {
        generateNextPermutationIndices();
        // Generate actual permutation.
        destination.clear();
        for (int i : permutationIndices)
        {
            destination.add(elements[i]);
        }
        return destination;
    }


    /**
     * Generate the indices into the elements array for the next permutation.  The
     * algorithm is from Kenneth H. Rosen, Discrete Mathematics and its Applications,
     * 2nd edition (NY: McGraw-Hill, 1991), p. 284)
     */
    private void generateNextPermutationIndices()
    {
        if (remainingPermutations == 0)
        {
            throw new IllegalStateException("There are no permutations remaining.  " +
                                            "Generator must be reset to continue using.");
        }
        else if (remainingPermutations < totalPermutations)
        {
            // Find largest index j with permutationIndices[j] < permutationIndices[j + 1]
            int j = permutationIndices.length - 2;
            while (permutationIndices[j] > permutationIndices[j + 1])
            {
                j--;
            }

            // Find index k such that permutationIndices[k] is smallest integer greater than
            // permutationIndices[j] to the right of permutationIndices[j].
            int k = permutationIndices.length - 1;
            while (permutationIndices[j] > permutationIndices[k])
            {
                k--;
            }

            // Interchange permutation indices.
            int temp = permutationIndices[k];
            permutationIndices[k] = permutationIndices[j];
            permutationIndices[j] = temp;

            // Put tail end of permutation after jth position in increasing order.
            int r = permutationIndices.length - 1;
            int s = j + 1;

            while (r > s)
            {
                temp = permutationIndices[s];
                permutationIndices[s] = permutationIndices[r];
                permutationIndices[r] = temp;
                r--;
                s++;
            }
        }
        --remainingPermutations;
    }


    /**
     * <p>Provides a read-only iterator for iterating over the permutations
     * generated by this object.  This method is the implementation of the
     * {@link Iterable} interface that permits instances of this class to be
     * used with the new-style for loop.</p>
     * <p>For example:</p>
     * <pre>
     * List&lt;Integer&gt; elements = Arrays.asList(1, 2, 3);
     * PermutationGenerator&lt;Integer&gt; permutations = new PermutationGenerator(elements);
     * for (List&lt;Integer&gt; p : permutations)
     * {
     *     // Do something with each permutation.
     * }
     * </pre>
     * @return An iterator.
     * @since 1.1
     */
    public Iterator<List<T>> iterator()
    {
        return new Iterator<List<T>>()
        {
            public boolean hasNext()
            {
                return hasMore();
            }


            public List<T> next()
            {
                return nextPermutationAsList();
            }


            public void remove()
            {
                throw new UnsupportedOperationException("Iterator does not support removal.");
            }
        };
    }
}
