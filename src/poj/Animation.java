package poj;

import poj.Render.ImageWindow;

public class Animation
{
	private ImageWindow focusedWindow;

	private long accTimems;

	private long frameDurationms;
	private int xstride;
	private int ystride;

	// initial value of the top left corner position (inclusive)
	private int xmin;
	private int ymin;

	// max value of the top left corner position (inclusive)
	private int xmax;
	private int ymax;

	public Animation(ImageWindow w, long fdms, int xstride, int ystride,
			 int xmin, int ymin, int xmax, int ymax)
	{
		this.focusedWindow = w;
		this.frameDurationms = fdms;
		this.xstride = xstride;
		this.ystride = ystride;
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}

	public void updateAnimationWindow(long xms)
	{
		addToAccTime(xms);

		if (this.accTimems >= this.frameDurationms)
			slideImageWindow();
	}

	final public ImageWindow getImageWindow()
	{
		return this.focusedWindow;
	}

	private void addToAccTime(long xms)
	{
		this.accTimems += xms;
	}

	// will reset the image window iff the current focus is outside of the
	// max (exclusive)
	private void slideImageWindow()
	{
		if (this.focusedWindow.getX() >= this.xmax
		    && this.focusedWindow.getY() >= this.ymax) {
			this.focusedWindow.setX(xmin);
			this.focusedWindow.setY(ymin);
			return;
		} else {
			this.focusedWindow.setX(focusedWindow.getX() + xstride);
			this.focusedWindow.setY(focusedWindow.getY() + ystride);
		}
	}
}
