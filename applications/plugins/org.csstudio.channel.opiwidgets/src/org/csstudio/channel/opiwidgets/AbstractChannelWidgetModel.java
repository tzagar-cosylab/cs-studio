package org.csstudio.channel.opiwidgets;

import gov.bnl.channelfinder.api.ChannelQuery;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public abstract class AbstractChannelWidgetModel extends AbstractWidgetModel {
	
	public final String ID = "org.csstudio.channel.opiwidgets.ChannelTreeByProperty"; //$NON-NLS-1$
	
	public static final String CHANNEL_QUERY = "channel_query"; //$NON-NLS-1$	
	
	@Override
	protected void configureBaseProperties() {
		super.configureBaseProperties();
		addProperty(new StringProperty(CHANNEL_QUERY, "Channel query", WidgetPropertyCategory.Basic, ""));
	}

	
	public ChannelQuery getChannelQuery() {
		return ChannelQuery.Builder.query((String) getCastedPropertyValue(CHANNEL_QUERY)).create();
	}

}
