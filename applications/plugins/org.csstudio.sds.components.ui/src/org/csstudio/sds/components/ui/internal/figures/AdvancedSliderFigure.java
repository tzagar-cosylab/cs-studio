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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RangeModel;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.ScrollBar;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

/**
 * A slider figure.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class AdvancedSliderFigure extends Panel implements IAdaptable {
	/**
	 * Insets for the whole figure.
	 */
	private static final int INSETS = 5;

	/**
	 * Width of the value marker (the little triangle).
	 */
	private static final int VALUE_MARKER_WIDTH = 9;

	/**
	 * Height of the value marker (the little triangle).
	 */
	private static final int VALUE_MARKER_HEIGHT = (VALUE_MARKER_WIDTH - (VALUE_MARKER_WIDTH % 2)) / 2 + 1;

	/**
	 * Height of the scale.
	 */
	private static final int SCALE_HEIGHT = 9;

	/**
	 * Static constructor, which checks the chosen constants.
	 */
	static {
		assert (VALUE_MARKER_WIDTH % 2) == 1 : "The width of the value marker must be an odd value, due to layout constraints.";
	}

	/**
	 * Listeners that react on slider events.
	 */
	private List<ISliderListener> _sliderListeners;

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;

	/**
	 * The scroll bar figure.
	 */
	private ScrollBar _scrollBar;

	/**
	 * Lower border of the displayed value.
	 */
	private int _min = 0;

	/**
	 * Upper border of the displayed value.
	 */
	private int _max = 100;

	/**
	 * The current value.
	 */
	private int _currentValue = 30;

	/**
	 * The current manual value.
	 */
	private int _manualValue = 30;

	/**
	 * Flag which is used to disable slider events. When the current value is
	 * set on the scrollbar, eventing must be turned off.
	 */
	private boolean _populateEvents = true;

	/**
	 * The LowLow border.
	 */
	private int _lolo = 10;

	/**
	 * The Low border.
	 */
	private int _lo = 25;

	/**
	 * The High border.
	 */
	private int _hi = 70;

	/**
	 * The HighHigh border.
	 */
	private int _hihi = 95;

	/**
	 * The number of slots in the scale.
	 */
	private int _scaleSlotCount = 10;

	/**
	 * The scale panel.
	 */
	private ScaleFigure _scalePanel;

	/**
	 * The value marker figure (little triangle).
	 */
	private ValueMarkerFigure _valueMarkerFigure;

	/**
	 * The manual value marker figure (little triangle).
	 */
	private ManualValueMarkerFigure _manualValueMarkerFigure;

	/**
	 * The value label panel (displays textual values).
	 */
	private ValueLabelPanel _valueLabelPanel;

	/**
	 * Standard constructor.
	 */
	public AdvancedSliderFigure() {

		_sliderListeners = new ArrayList<ISliderListener>();

		setLayoutManager(new XYLayout());

		// listen to figure movement events
		addFigureListener(new FigureListener() {
			public void figureMoved(final IFigure source) {
				doRefreshPositions();
			}
		});

		_valueLabelPanel = new ValueLabelPanel();
		add(_valueLabelPanel);

		_scalePanel = new ScaleFigure();
		add(_scalePanel);
		setConstraint(_scalePanel, new Rectangle(40, VALUE_MARKER_HEIGHT, 100,
				SCALE_HEIGHT));

		_valueMarkerFigure = new ValueMarkerFigure();
		add(_valueMarkerFigure);
		setConstraint(_valueMarkerFigure, new Rectangle(30, 0,
				VALUE_MARKER_WIDTH, VALUE_MARKER_HEIGHT));

		_manualValueMarkerFigure = new ManualValueMarkerFigure();
		ManualValueDragger tb = new ManualValueDragger();
		_manualValueMarkerFigure.addMouseListener(tb);
		_manualValueMarkerFigure.addMouseMotionListener(tb);
		add(_manualValueMarkerFigure);
		setConstraint(_manualValueMarkerFigure, new Rectangle(30, 0,
				VALUE_MARKER_WIDTH, VALUE_MARKER_HEIGHT));

		_scrollBar = createScrollbarFigure();
		add(_scrollBar);
		setConstraint(_scrollBar, new Rectangle(0, 20, 100, 20));

	}

	/**
	 * Calculate the layout constraints for the scale panel.
	 * 
	 * @return the layout constraints for the scale panel
	 */
	private Rectangle calculateScaleConstraints() {
		return new Rectangle(INSETS, VALUE_MARKER_HEIGHT + 2, bounds.width - 2
				* INSETS, SCALE_HEIGHT);
	}

	/**
	 * Calculate the width of the scale.
	 * 
	 * @return the scale width
	 */
	public int calculateScaleWidth() {
		Rectangle scaleBounds = calculateScaleConstraints();

		int neededScaleLines = _scaleSlotCount + 1;
		return (scaleBounds.width - ((scaleBounds.width - neededScaleLines) % _scaleSlotCount));
	}

	/**
	 * Calculate the layout constraints for the scrollbar.
	 * 
	 * @return the layout constraints for the scrollbar
	 */
	private Rectangle calculateScrollbarConstraints() {
		int usedHeight = VALUE_MARKER_HEIGHT + SCALE_HEIGHT + 5;
		int availableHeight = bounds.height;
		int height = availableHeight - usedHeight;

		return new Rectangle(INSETS, usedHeight,
				(int) (calculateScaleWidth() * 0.6), height > 0 ? Math.min(
						height, 20) : 1);
	}

	/**
	 * Calculate the layout constraints for the value panel.
	 * 
	 * @return the layout constraints for the value panel
	 */
	private Rectangle calculateValuePanelConstraints() {
		int usedHeight = VALUE_MARKER_HEIGHT + SCALE_HEIGHT + 5;
		//int availableHeight = bounds.height;

		Point topLeft = new Point(INSETS + (int) (calculateScaleWidth() * 0.6)
				+ 5, usedHeight);
		Point botRight = new Point(INSETS + calculateScaleWidth(),
				bounds.height - INSETS);

		return new Rectangle(topLeft, botRight);
	}

	/**
	 * Calculate the layout constraints for the value marker.
	 * 
	 * @return the layout constraints for the value marker
	 */
	private Rectangle calculateValueMarkerConstraints() {
		int scaleWidth = calculateScaleWidth() - 1;

		int pos = 0;

		if (scaleWidth > 0) {
			// rescale
			assert _currentValue >= _min && _currentValue <= _max
					&& _min < _max;

			double quota = (double) (_currentValue - _min) / (_max - _min);

			pos = (int) Math.abs(scaleWidth * quota);
		}

		return new Rectangle(pos + INSETS - VALUE_MARKER_HEIGHT + 1, 0,
				VALUE_MARKER_WIDTH, VALUE_MARKER_HEIGHT);
	}

	/**
	 * Calculate the layout constraints for the manual value marker.
	 * 
	 * @return the layout constraints for the manual value marker
	 */
	private Rectangle calculateManualValueMarkerConstraints() {
		int scaleWidth = calculateScaleWidth() - 1;

		int pos = 0;

		if (scaleWidth > 0) {
			// rescale
			assert _manualValue >= _min && _manualValue <= _max && _min < _max;

			double quota = (double) (_manualValue - _min) / (_max - _min);

			pos = (int) Math.abs(scaleWidth * quota);
		}

		return new Rectangle(pos + INSETS - VALUE_MARKER_HEIGHT + 1, 0,
				VALUE_MARKER_WIDTH, VALUE_MARKER_HEIGHT);
	}

	/**
	 * Calculates the relative position on the scale for the specified value.
	 * 
	 * @param value
	 *            the value
	 * @return the x position on the scale
	 */
	private int calculateRelativePointOnScale(final int value) {
		int scaleWidth = calculateScaleWidth();

		int pos = 0;

		if (scaleWidth > 0) {
			// rescale
			assert value >= _min && value <= _max && _min < _max;

			double quota = (double) (value - _min) / (_max - _min);

			pos = (int) Math.abs(scaleWidth * quota);
		}

		return pos;
	}

	/**
	 * Refresh the positions of all child figures.
	 */
	private void doRefreshPositions() {
		setConstraint(_scalePanel, calculateScaleConstraints());
		_scalePanel.invalidate();

		setConstraint(_valueMarkerFigure, calculateValueMarkerConstraints());

		setConstraint(_manualValueMarkerFigure,
				calculateManualValueMarkerConstraints());

		setConstraint(_scrollBar, calculateScrollbarConstraints());

		setConstraint(_valueLabelPanel, calculateValuePanelConstraints());
	}

	/**
	 * Creates the scrollbar.
	 * @return the scrollbar figure
	 */
	private ScrollBar createScrollbarFigure() {
		ScrollBar bar = new ScrollBar();
		bar.setExtent(0);
		bar.setMaximum(1000);
		bar.setMinimum(1);
		bar.setValue(400);
		bar.setStepIncrement(1);
		bar.setOrientation(ScrollBar.VERTICAL);
		bar.setBackgroundColor(ColorConstants.blue);
		Ellipse thumb = new Ellipse();
		thumb.setSize(new Dimension(40, 40));
		thumb.setFont(CustomMediaFactory.getInstance().getDefaultFont(SWT.BOLD));
		thumb.setBackgroundColor(ColorConstants.red);

		thumb.setBorder(new SchemeBorder(SchemeBorder.SCHEMES.RIDGED));
		bar.validate();

		// add listener
		bar.addPropertyChangeListener(RangeModel.PROPERTY_VALUE,
				new PropertyChangeListener() {
					public void propertyChange(final PropertyChangeEvent event) {
						fireManualValueChange((Integer) event.getNewValue());
					}
				});

		return bar;
	}

	/**
	 * Inform all slider listeners, that the manual value has changed.
	 * 
	 * @param newManualValue the new manual value
	 */
	private void fireManualValueChange(final int newManualValue) {
		int tmp = newManualValue;

		if (tmp < _min) {
			tmp = _min;
		}
		if (tmp > _max) {
			tmp = _max;
		}

		if (_populateEvents) {
			for (ISliderListener l : _sliderListeners) {
				l.sliderValueChanged(tmp);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnabled(final boolean value) {
		super.setEnabled(value);
		_scrollBar.setEnabled(value);
	}

	/**
	 * Set the minimum value.
	 * 
	 * @param min
	 *            The minimum value.
	 */
	public void setMin(final int min) {
		_scrollBar.setMinimum(min);
		_min = min;

		_scalePanel.repaint();

	}

	/**
	 * Set the maximum value.
	 * 
	 * @param max
	 *            The maximum value.
	 */
	public void setMax(final int max) {
		_scrollBar.setMaximum(max);
		_max = max;
		_scalePanel.repaint();
	}

	/**
	 * Set the increment value.
	 * 
	 * @param increment
	 *            The increment value.
	 */
	public void setIncrement(final int increment) {
		_scrollBar.setStepIncrement(increment);
	}

	/**
	 * Sets the orientation. Choose one of {@link PositionConstants#HORIZONTAL}
	 * or {@link PositionConstants#VERTICAL}.
	 * 
	 * @param horizontal
	 *            true for horizontal and false for vertical layout
	 */
	public void setOrientation(final boolean horizontal) {
		_scrollBar.setOrientation(horizontal ? ScrollBar.HORIZONTAL
				: ScrollBar.VERTICAL);
	}

	/**
	 * Set the current slider value.
	 * 
	 * <b>Important:</b> This method should only get called by the Controller
	 * and not by the figure itself!
	 * 
	 * @param value
	 *            the current slider value
	 */
	public void setValue(final int value) {
		assert value >= _min && value <= _max;

		// disable eventing
		_populateEvents = false;

		// store current value
		_currentValue = value;

		// update scrollbar
		_scrollBar.setValue(value);

		// update textual value representation
		_valueLabelPanel.setLastDalValue(value);

		// move the value marker
		setConstraint(_valueMarkerFigure, calculateValueMarkerConstraints());

		// enable eventing
		_populateEvents = true;
	}

	/**
	 * Set the current manual value.
	 * 
	 * <b>Important:</b> This method should only get called by the Controller
	 * and not by the figure itself!
	 * 
	 * @param value
	 *            the current slider value
	 */
	public void setManualValue(final int value) {
		assert value >= _min && value <= _max;

		_manualValue = value;

		// update textual value representation
		_valueLabelPanel.setLastManualValue(value);

		// move the manual value marker
		setConstraint(_manualValueMarkerFigure,
				calculateManualValueMarkerConstraints());
	}

	/**
	 * Add a slider listener.
	 * 
	 * @param listener
	 *            The slider listener to add.
	 */
	public void addSliderListener(final ISliderListener listener) {
		_sliderListeners.add(listener);
	}

	/**
	 * Remove a slider listener.
	 * 
	 * @param listener
	 *            The slider listener that is to be removed.
	 */
	public void removeSliderListener(final ISliderListener listener) {
		_sliderListeners.remove(listener);
	}

	/**
	 * This method is a tribute to unit tests, which need a way to test the
	 * performance of the figure implementation. Implementors should produce
	 * some random changes and refresh the figure, when this method is called.
	 * 
	 */
	public void randomNoiseRefresh() {
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if (_borderAdapter == null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
		return null;
	}

	/**
	 * A figure, which draws the scale.
	 * 
	 * @author swende
	 */
	class ScaleFigure extends Panel {
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void paintFigure(final Graphics graphics) {
			Rectangle bounds = getBounds();

			graphics.setBackgroundColor(ColorConstants.yellow);

			// draw scale background
			Rectangle r;

			int relLoLo = calculateRelativePointOnScale(_lolo);
			int relLo = calculateRelativePointOnScale(_lo);
			int relHi = calculateRelativePointOnScale(_hi);
			int relHiHi = calculateRelativePointOnScale(_hihi);

			int top = 2;

			graphics.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor(255, 64, 64));
			r = new Rectangle(new Point(0, top), new Point(relLoLo,
					SCALE_HEIGHT - 1));
			graphics.fillRectangle(r.translate(bounds.getTopLeft()));

			graphics.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor(255, 128, 128));
			r = new Rectangle(new Point(relLoLo, top), new Point(relLo,
					SCALE_HEIGHT - 1));
			graphics.fillRectangle(r.translate(bounds.getTopLeft()));

			graphics.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor(192, 255, 192));
			r = new Rectangle(new Point(relLo, top), new Point(relHi,
					SCALE_HEIGHT - 1));
			graphics.fillRectangle(r.translate(bounds.getTopLeft()));

			graphics.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor(255, 128, 128));
			r = new Rectangle(new Point(relHi, top), new Point(relHiHi,
					SCALE_HEIGHT - 1));
			graphics.fillRectangle(r.translate(bounds.getTopLeft()));

			graphics.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor(255, 64, 64));
			r = new Rectangle(new Point(relHiHi, top), new Point(
					calculateScaleWidth() - 1, SCALE_HEIGHT - 1));
			graphics.fillRectangle(r.translate(bounds.getTopLeft()));

			// draw scale
			graphics.setForegroundColor(ColorConstants.lightBlue);

			int scaleLinesCount = _scaleSlotCount + 1;

			// check available space and do layout corrections (the following
			// calculations are based on the assumption, that each scale line is
			// drawn with 1px width
			int slotSize = (calculateScaleWidth() - scaleLinesCount)
					/ _scaleSlotCount;

			int scaleLineDistance = slotSize + 1;
			if (slotSize >= 1) {
				Point from, to;
				int scaleLineHeight = SCALE_HEIGHT - 1;
				for (int i = 0; i < scaleLinesCount; i++) {
					from = new Point(i * scaleLineDistance, scaleLineHeight);
					to = new Point(i * scaleLineDistance, 0);
					graphics.drawLine(from.translate(getBounds().getTopLeft()),
							to.translate(getBounds().getTopLeft()));
				}

				// draw bottom line
				graphics.setForegroundColor(ColorConstants.black);
				from = new Point(0 * scaleLineDistance, SCALE_HEIGHT - 1);
				to = new Point((scaleLinesCount - 1) * scaleLineDistance,
						SCALE_HEIGHT - 1);
				graphics.drawLine(from.translate(getBounds().getTopLeft()), to
						.translate(getBounds().getTopLeft()));
			}
		}

	}

	/**
	 * A panel, which displays the current value and the manual value in textual
	 * form.
	 * 
	 * @author swende
	 */
	class ValueLabelPanel extends Panel {
		/**
		 * A label, which displays the current manual value.
		 */
		private Label _lastManualValueLabel;

		/**
		 * A label, which displays the current value.
		 */
		private Label _lastDalValueLabel;

		/**
		 * Constructor.
		 */
		public ValueLabelPanel() {
			setLayoutManager(new ToolbarLayout(false));

			_lastDalValueLabel = new Label();
			_lastDalValueLabel.setFont(CustomMediaFactory.getInstance()
					.getFont("Courier New", 8, SWT.None));
			add(_lastDalValueLabel);

			_lastManualValueLabel = new Label();
			_lastManualValueLabel.setFont(CustomMediaFactory.getInstance()
					.getFont("Courier New", 8, SWT.None));
			add(_lastManualValueLabel);
		}

		/**
		 * Sets the current value.
		 * 
		 * @param value
		 *            the current value
		 */
		public void setLastDalValue(final int value) {
			_lastDalValueLabel.setText("IOC: " + value);
		}

		/**
		 * Sets the current manual value.
		 * 
		 * @param manualValue
		 *            the current manual value
		 */
		public void setLastManualValue(final int manualValue) {
			_lastManualValueLabel.setText("MAN: " + manualValue);
		}
	}

	/**
	 * A triangle figure, which represents the value marker.
	 * 
	 * @author swende
	 */
	class ValueMarkerFigure extends Panel {
		/**
		 * A pointlist, which contains the points of the triangle.
		 */
		private PointList _points;

		/**
		 * Constructor.
		 */
		public ValueMarkerFigure() {
			_points = new PointList();
			_points.addPoint(new Point(0, 0));
			_points.addPoint(new Point(VALUE_MARKER_WIDTH - 1, 0));
			_points.addPoint(new Point(VALUE_MARKER_HEIGHT - 1,
					VALUE_MARKER_HEIGHT - 1));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void paintFigure(final Graphics graphics) {
			PointList points = _points.getCopy();
			points.translate(getBounds().getTopLeft());

			graphics.setBackgroundColor(ColorConstants.blue);
			graphics.setForegroundColor(ColorConstants.blue);
			graphics.fillPolygon(points);
		}
	}

	/**
	 * A triangle figure, which represents the manual value marker.
	 * 
	 * @author swende
	 */
	class ManualValueMarkerFigure extends Panel {
		/**
		 * A pointlist, which contains the points of the triangle.
		 */
		private PointList _points;

		/**
		 * Constructor.
		 */
		public ManualValueMarkerFigure() {
			_points = new PointList();
			_points.addPoint(new Point(0, 0));
			_points.addPoint(new Point(VALUE_MARKER_WIDTH - 1, 0));
			_points.addPoint(new Point(VALUE_MARKER_HEIGHT - 1,
					VALUE_MARKER_HEIGHT - 1));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void paintFigure(final Graphics graphics) {
			PointList points = _points.getCopy();
			points.translate(getBounds().getTopLeft());

			graphics.setBackgroundColor(ColorConstants.blue);
			graphics.setForegroundColor(ColorConstants.blue);
			graphics.drawPolygon(points);
		}
	}

	/**
	 * A dragger for the manual value marker.
	 * 
	 * @author swende
	 * 
	 */
	class ManualValueDragger extends MouseMotionListener.Stub implements
			MouseListener {
		/**
		 * Starting point of a drag operation.
		 */
		private Point _dragStartPosition;

		/**
		 * The drag range.
		 */
		private int _dragRange;

		/**
		 * The value, which is manipulated by the drag operation.
		 */
		private int _revertValue;

		/**
		 * Flag, which indicates whether a drag operation is in process.
		 */
		private boolean _armed;

		/**
		 * {@inheritDoc}
		 */
		public void mousePressed(final MouseEvent event) {
			_armed = true;
			_dragStartPosition = event.getLocation();
			_dragRange = calculateScaleWidth();
			_revertValue = _manualValue;
			event.consume();
		}

		/**
		 * {@inheritDoc}
		 */
		public void mouseDragged(final MouseEvent event) {
			if (!_armed) {
				return;
			}

			Dimension difference = event.getLocation().getDifference(
					_dragStartPosition);

			int change = (_max - _min) * difference.width / _dragRange;

			fireManualValueChange(_revertValue + change);

			event.consume();
		}

		/**
		 * {@inheritDoc}
		 */
		public void mouseReleased(final MouseEvent me) {
			if (!_armed) {
				return;
			}
			_armed = false;
			me.consume();
		}

		/**
		 * {@inheritDoc}
		 */
		public void mouseDoubleClicked(final MouseEvent me) {
		}
	}

	/**
	 * Definition of listeners that react on slider events.
	 * 
	 * @author Sven Wende
	 * @version $Revision$
	 * 
	 */
	public interface ISliderListener {
		/**
		 * React on a slider event.
		 * 
		 * @param newValue
		 *            The new slider value.
		 */
		void sliderValueChanged(int newValue);
	}

}
