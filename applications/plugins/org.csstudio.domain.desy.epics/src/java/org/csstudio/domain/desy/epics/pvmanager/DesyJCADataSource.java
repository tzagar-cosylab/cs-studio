/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.desy.epics.pvmanager;

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.jca.JCADataSource;

import com.google.common.collect.MapMaker;


/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 30.08.2011
 */
public class DesyJCADataSource extends JCADataSource {

    private final Map<String, DesyJCAChannelHandler> _handlerMap;

    public DesyJCADataSource(@Nonnull final String className,
                             final int monitorMask) {
        super(className, monitorMask);

        _handlerMap = new MapMaker().concurrencyLevel(5).softValues().makeMap();
    }

    @Override
    @Nonnull
    protected ChannelHandler<?> createChannel(@Nonnull final String channelName) {
        if(_handlerMap.containsKey(channelName)) {
            return _handlerMap.get(channelName);
        }
        return createHandlerFor(channelName, null);
    }

    @CheckForNull
    public DesyJCAChannelHandler getHandler(@Nonnull final String channelName) {
        return _handlerMap.get(channelName);
    }

    @Nonnull
    public DesyJCAChannelHandler createHandlerFor(@Nonnull final String name, @Nullable final String dataType) {
        final DesyJCAChannelHandler handler = new DesyJCAChannelHandler(name, dataType, getContext(), getMonitorMask());
        _handlerMap.put(name, handler);
        return handler;
    }
}