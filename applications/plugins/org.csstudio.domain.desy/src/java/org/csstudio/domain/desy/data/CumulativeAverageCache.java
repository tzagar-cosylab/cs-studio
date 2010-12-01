/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.domain.desy.data;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;



/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 26.11.2010
 */
public class CumulativeAverageCache<A extends BaseValueType> extends AccumulatorCache<A, DDouble> {

    /**
     * Constructor.
     */
    public CumulativeAverageCache() {
        super(DDouble.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    protected DDouble calculateAccumulation(@CheckForNull final DDouble accumulatedValue,
                                            @Nonnull final A nextValue) {
        final Double curVal = accumulatedValue.getValue();
        final DDouble nextDVal = ConversionTypeSupport.toDDouble(nextValue);

        final Double nextVal = nextDVal.getValue();
        Double result;
        if (curVal != null) {
            result = curVal + nextVal;
            //final int n = getNumberOfAccumulations();
            //result = curVal + (nextVal - curVal)/(n + 1);
        } else {
            result = nextVal;
        }
        return new DDouble(result, nextDVal.getTimestamp());
    }

    /**
     * {@inheritDoc}
     */
    @CheckForNull
    @Override
    public DDouble getValue() {
        final DDouble dVal = super.getValue();
        if (dVal != null) {
            final Double value = dVal.getValue();
            if (value!= null) {
                final int n = getNumberOfAccumulations();
                return new DDouble(value / n, dVal.getTimestamp());
            }
        }
        return null;
    }
}
