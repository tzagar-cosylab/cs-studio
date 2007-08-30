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
package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

/**
 * A action button figure.
 * 
 * @author Sven Wende
 * 
 */
public final class RefreshableActionButtonFigure extends RectangleFigure implements
		IAdaptable {
	/**
	 * Default label font.
	 */
	public static final Font FONT = CustomMediaFactory.getInstance().getFont(
			"Arial", 8, SWT.NONE); //$NON-NLS-1$
	
	/**
	 * An Array, which contains the PositionConstants for Center, Top, Bottom, Left, Right.
	 */
	private final int[] _alignments = new int[] {PositionConstants.CENTER, PositionConstants.TOP, PositionConstants.BOTTOM, PositionConstants.LEFT, PositionConstants.RIGHT};
	
	private SdsButton _button;

	/**
	 * Constructor.
	 */
	public RefreshableActionButtonFigure() {
//		_label = new Label("");
//		setContents(_label);
//		setFont(FONT);
		this.setLayoutManager(new XYLayout());
		_button = new SdsButton();
		//Rectangle bounds = new Rectangle(this.getBounds().x+2,this.getBounds().y+2,this.getBounds().width-4, this.getBounds().height-4);
		_button.setBounds(bounds);
		this.add(_button);
		// listen to figure movement events
		addFigureListener(new FigureListener() {
			public void figureMoved(final IFigure source) {
				refreshConstraints();
			}
		});
	}
	
	private void refreshConstraints() {
		Rectangle bounds = new Rectangle(2,2,this.getBounds().width-4, this.getBounds().height-4);
		this.setConstraint(_button, bounds);
	}
	
	@Override
	public void paintFigure(Graphics graphics) {
		//super.paintFigure(graphics);
		this.refreshConstraints();
	}

	/**
	 * This method is a tribute to unit tests, which need a way to test the
	 * performance of the figure implementation. Implementors should produce
	 * some random changes and refresh the figure, when this method is called.
	 * 
	 */
	public void randomNoiseRefresh() {
		setText("" + Math.random()); //$NON-NLS-1$
	}

	/**
	 * Sets the text for the Button.
	 * @param s
	 * 			The text for the button
	 */
	public void setText(final String s) {
		//_label.setText(s);
		_button.setText(s);
	}
	
	/**
	 * Sets the alignment of the buttons text.
	 * The parameter is a {@link PositionConstants} (LEFT, RIGHT, TOP, CENTER, BOTTOM)
	 * @param alignment
	 * 			The alignment for the text 
	 */
	public void setTextAlignment(final int alignment) {
		_button.setTextAlignment(alignment);
	}
	
	public void addActionListener(final  ActionListener listener) {
		_button.addActionListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Class adapter) {
		return null;
	}
	
	private final class SdsButton extends Button {
		
		/**
		 * The Label for the Button.
		 */
		private Label _label;
		
		public SdsButton() {
			_label = new Label("");
			setContents(_label);
			setFont(FONT);	
		}
		
		public void setText(final String text) {
			_label.setText(text);
		}
		
		/**
		 * Sets the alignment of the buttons text.
		 * The parameter is a {@link PositionConstants} (LEFT, RIGHT, TOP, CENTER, BOTTOM)
		 * @param alignment
		 * 			The alignment for the text 
		 */
		public void setTextAlignment(final int alignment) {
			if (alignment>=0 && alignment<_alignments.length) {
				if (_alignments[alignment]==PositionConstants.LEFT || _alignments[alignment]==PositionConstants.RIGHT) {
					_label.setTextPlacement(PositionConstants.NORTH);
				} else {
					_label.setTextPlacement(PositionConstants.EAST);
				}
				_label.setTextAlignment(_alignments[alignment]);
			}
		}
	}
	
}
