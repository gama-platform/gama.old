/* Copyright 2011 csvedit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ummisco.gama.ui.viewers.csv.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;

/**
 *
 * @author fhenri
 *
 */
public class SearchResultStyle {

    /**
     * @param searchTerm
     * @param content
     * @return
     */
    public static int[] getSearchTermOccurrences(
            String searchTerm,
            String content)
    {
        List<StyleRange> styleRange;
        List<Integer> ranges;
        Display disp = Display.getCurrent();
        StyleRange myStyleRange =
            new StyleRange(0, 0, null, disp.getSystemColor(SWT.COLOR_YELLOW));

        // reset the StyleRange-Array for each new field
        styleRange = new ArrayList<StyleRange>();
        ranges = new ArrayList<Integer>(); // reset the ranges-array
        if (searchTerm.equals("")) {
            return new int[] {};
        }

        // determine all occurrences of the searchText and write the beginning
        // and length of each occurrence into an array
        for (int i = 0; i < content.length(); i++) {
            if (i + searchTerm.length() <= content.length()
                    && content.substring(i, i + searchTerm.length())
                            .equalsIgnoreCase(searchTerm)) {
                // ranges format: n->start of the range, n+1->length of the
                // range
                ranges.add(i);
                ranges.add(searchTerm.length());
            }
        }
        // convert the list into an int[] and make sure that overlapping
        // search term occurrences are are merged
        int[] intRanges = new int[ranges.size()];
        int arrayIndexCounter = 0;
        for (int listIndexCounter = 0; listIndexCounter < ranges.size(); listIndexCounter++) {
            if (listIndexCounter % 2 == 0) {
                if (searchTerm.length() > 1
                        && listIndexCounter != 0
                        && ranges.get(listIndexCounter - 2)
                                + ranges.get(listIndexCounter - 1) >= ranges
                                .get(listIndexCounter)) {
                    intRanges[arrayIndexCounter - 1] = 0
                            - ranges.get(listIndexCounter - 2)
                            + ranges.get(listIndexCounter)
                            + ranges.get(++listIndexCounter);
                } else {
                    intRanges[arrayIndexCounter++] = ranges
                            .get(listIndexCounter);
                }
            } else {
                intRanges[arrayIndexCounter++] = ranges.get(listIndexCounter);
                styleRange.add(myStyleRange);
            }
        }
        // if there have been any overlappings we need to reduce the size of
        // the array to avoid conflicts in the setStyleRanges method
        int[] intRangesCorrectSize = new int[arrayIndexCounter];
        System.arraycopy(intRanges, 0, intRangesCorrectSize, 0,
                arrayIndexCounter);

        return intRangesCorrectSize;
    }
}
