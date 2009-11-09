/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
 * AT http://www.desy.de/legal/license.htm
 */
/*
 * $Id$
 */
package org.csstudio.config.ioconfig.model;

import java.util.Date;

import javax.persistence.MappedSuperclass;

/**
 * This class represent a DB Table with Name and SortIndex and extends the {@link DBClass}
 * 
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 03.06.2009
 */
@MappedSuperclass
public class NamedDBClass extends DBClass {

    public static long _oldTime = 0;
    public static long _classCallCount = 0;
    public static StringBuilder _diagString = new StringBuilder();
    
    /**
     * The Node Name.
     */
    private String _name;

    /**
     * The Index to sort the node inside his parent.
     */
    private Short _sortIndex = -1;

    /**
     * 
     * @param name
     *            set the Name of this Node.
     */
    public void setName(final String name) {
        this._name = name;
       _classCallCount++; 
        long time = new Date().getTime();
        long l = time-_oldTime;
        if(l>50)
            _diagString.append(_classCallCount+": \t\t"+time+"\t"+l+"\t"+_name+"\t"+this.getClass().getSimpleName()+"\r\n");
        _oldTime = time;
    }

    /**
     * 
     * @return the Name of this Node.
     */
    public String getName() {
        return _name;
    }

    /**
     * 
     * @return the Index to sort the node inside his parent.
     */
    public Short getSortIndex() {
        return _sortIndex;
    }

    /**
     * 
     * @param sortIndex
     *            set the Index to sort the node inside his parent.
     */
    public void setSortIndex(Short sortIndex) {
        _sortIndex = sortIndex;
    }

    /**
     * @return The Name of this Node.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (getSortIndex() != null) {
            sb.append(getSortIndex());
        }
        if (getName() != null) {
            sb.append(':');
            sb.append(getName());
        }
        return sb.toString();
    }
}
