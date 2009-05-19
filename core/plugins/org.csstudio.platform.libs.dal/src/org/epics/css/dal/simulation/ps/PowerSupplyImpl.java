/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.epics.css.dal.simulation.ps;

import com.cosylab.naming.URIName;

import com.cosylab.util.BitCondition;

import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.PatternProperty;
import org.epics.css.dal.PatternPropertyCharacteristics;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.context.LinkBlocker;
import org.epics.css.dal.device.PowerSupply;
import org.epics.css.dal.directory.Attributes;
import org.epics.css.dal.impl.AbstractDeviceImpl;
import org.epics.css.dal.proxy.DeviceProxy;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.simulation.CommandProxyImpl;
import org.epics.css.dal.simulation.DoublePropertyProxyImpl;
import org.epics.css.dal.simulation.PatternPropertyProxyImpl;
import org.epics.css.dal.simulation.PropertyProxyImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.spi.Plugs;

import java.lang.reflect.InvocationTargetException;

import java.util.BitSet;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;


/**
 *
 * <code>PowerSupplyImpl</code> is a default implementation of the PowerSupply
 * interface.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 * @since VERSION
 */
public class PowerSupplyImpl extends AbstractDeviceImpl implements PowerSupply
{
	private static BitSet statusOff;
	private static BitSet statusOn;

	public PowerSupplyImpl(String name)
	{
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.impl.AbstractDeviceImpl#initialize(org.epics.css.dal.proxy.DeviceProxy, org.epics.css.dal.proxy.DirectoryProxy)
	 */
	public void initialize(DeviceProxy devp, DirectoryProxy dirp)
	{
		super.initialize(devp, dirp);

		if (devp instanceof PSDeviceProxy) {
			CommandProxyImpl[] commands = null;

			try {
				commands = new CommandProxyImpl[]{
						new CommandProxyImpl(devp, this,
						    this.getClass().getMethod("turnOn", null)),
						new CommandProxyImpl(devp, this,
						    this.getClass().getMethod("turnOff", null))
					};
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			((PSDeviceProxy)devp).initalizeCommands(commands);

			statusOff = new BitSet(1);
			statusOff.clear();

			statusOn = new BitSet(1);
			statusOn.set(0);

			String[] names = { "current", "readback", "status" };
			Class[] propTypes = {
					DoubleProperty.class, DoubleProperty.class,
					PatternProperty.class
				};
			Class[] proxyTypes = {
					DoublePropertyProxyImpl.class, DoublePropertyProxyImpl.class,
					PatternPropertyProxyImpl.class
				};

			((PSDeviceProxy)devp).initalizeProperties(names, propTypes,
			    proxyTypes);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.device.PowerSupply#getCurrent()
	 */
	public DoubleProperty getCurrent()
	{
		return (DoubleProperty)getProperty("current");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.device.PowerSupply#getReadback()
	 */
	public DoubleProperty getReadback()
	{
		return (DoubleProperty)getProperty("readback");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.device.PowerSupply#getStatus()
	 */
	public PatternProperty getStatus()
	{
		return (PatternProperty)getProperty("status");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.impl.AbstractDeviceImpl#createProperty(java.lang.String)
	 */
	protected DynamicValueProperty<?> createProperty(String name)
		throws RemoteException, IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException
	{
		DynamicValueProperty p = super.createProperty(name);

		if (name.equals("status")) {
			String[] bitDescriptions = new String[]{ "Power" };

			BitCondition[] conditionsWhenCleared = new BitCondition[1];
			BitCondition[] conditionsWhenSet = new BitCondition[1];

			for (int i = 0; i < conditionsWhenCleared.length; i++) {
				conditionsWhenCleared[i] = BitCondition.UNUSED;
				conditionsWhenSet[i] = BitCondition.OK;
			}

			BitSet bitMask = new BitSet(1);
			bitMask.set(0, 1);

			try {
				DirContext ctx = SimulatorPlug.getInstance()
					.getDefaultDirectory();
				URIName uri = new URIName(null,
					    SimulatorPlug.DEFAULT_AUTHORITY, p.getUniqueName(), null);
				Attributes attr = new Attributes();
				attr.put(PatternPropertyCharacteristics.C_BIT_DESCRIPTIONS,
				    bitDescriptions);
				attr.put(PatternPropertyCharacteristics.C_CONDITION_WHEN_CLEARED,
				    conditionsWhenCleared);
				attr.put(PatternPropertyCharacteristics.C_CONDITION_WHEN_SET,
				    conditionsWhenSet);
				attr.put(PatternPropertyCharacteristics.C_BIT_MASK, bitMask);
				ctx.bind(uri, new PatternPropertyProxyImpl(p.getUniqueName()),
				    attr);
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			PropertyProxy proxy = deviceProxy.getPropertyProxy(name);
			((PropertyProxyImpl)proxy).setSettable(false);
		}

		LinkBlocker.blockUntillConnected(p,
		    Plugs.getConnectionTimeout(null, 30000) * 2, true);

		return p;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.device.PowerSupply#off()
	 */
	public void off() throws RemoteException
	{
		getCommand("turnOff").execute();
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.device.PowerSupply#on()
	 */
	public void on() throws RemoteException
	{
		getCommand("turnOn").execute();
	}

	/*
	 * do not call this two methods (turnOff and turnOn)
	 * they are here to allow use of commands and CommandProxyImpl
	 */
	public void turnOff() throws RemoteException
	{
		if (getStatus().getValue().equals(statusOff)) {
			return;
		}

		getStatus().setValue(statusOff);
	}

	public void turnOn() throws RemoteException
	{
		if (getStatus().getValue().equals(statusOn)) {
			return;
		}

		getStatus().setValue(statusOn);
	}
}

/* __oOo__ */
