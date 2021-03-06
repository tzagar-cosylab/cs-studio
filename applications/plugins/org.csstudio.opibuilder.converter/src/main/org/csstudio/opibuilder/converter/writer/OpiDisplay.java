/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmDisplay;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML output class for EdmDisplay data.
 * @author Matevz
 */
public class OpiDisplay {

	private static final String version = "1.0";
	
	protected Context context;

	/**
	 * Converts the EdmDisplay d to OPI display widget XML, together with all
	 * containing widgets.  
	 */
	public OpiDisplay(Document doc, EdmDisplay d, String displayFileName) {

		Element element = doc.createElement("display");
		element.setAttribute("typeId", "org.csstudio.opibuilder.Display");
		doc.appendChild(element);
		
		/* Treat position of widgets inside as absolute positions, not relative
		 * to the display position.
		 */
		context = new Context(doc, element, 0, 0);
		
		writeHeader(context, d);
		OpiWriter.writeWidgets(context, d.getWidgets());
	}

	/**
	 * Converts the attributes on the display.  
	 */
	private void writeHeader(Context con, EdmDisplay d) {

		context.getElement().setAttribute("version", version);
		
		new OpiInt(context, "x", d.getX());
		new OpiInt(context, "y", d.getY());
		new OpiInt(context, "width", d.getW());
		new OpiInt(context, "height", d.getH());
		
		new OpiFont(context, "font", d.getFont());
		new OpiFont(context, "font_ctl", d.getCtlFont());
		new OpiFont(context, "font_button", d.getBtnFont());
		
		new OpiColor(context, "color_foreground", d.getFgColor());
		new OpiColor(context, "color_background", d.getBgColor());
		new OpiColor(context, "color_text", d.getTextColor());
		new OpiColor(context, "color_ctlFgColor1", d.getCtlFgColor1());
		new OpiColor(context, "color_ctlFgColor2", d.getCtlFgColor2());
		new OpiColor(context, "color_ctlBgColor1", d.getCtlBgColor1());
		new OpiColor(context, "color_ctlBgColor2", d.getCtlBgColor2());
		new OpiColor(context, "color_topshadowcolor", d.getTopShadowColor());
		new OpiColor(context, "color_botshadowcolor", d.getBotShadowColor());
		
		if (d.getAttribute("title").isInitialized())
			new OpiString(context, "name", d.getTitle());
		new OpiBoolean(context, "grid_show", d.isShowGrid());
		if (d.getAttribute("gridSize").isInitialized())
			new OpiInt(context, "grid_space", d.getGridSize());
		new OpiBoolean(context, "scroll_disable", d.isDisableScroll());
		
	}

}
