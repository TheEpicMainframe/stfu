package poj.Collisions;
/**
 * Polygon -- polygon shape for collisions.
 *
 * date March 10, 2019
 * @author Jared Pon
 * @version 1.0
 */

import poj.linear.*;

// Algorthims from various authors
// https://blog.hamaluik.ca/posts/swept-aabb-collision-using-minkowski-difference/
// http://www.dyn4j.org/2010/04/gjk-gilbert-johnson-keerthi/
// https://github.com/kroitor/gjk.c
// http://entropyinteractive.com/2011/04/gjk-algorithm/
// https://www.haroldserrano.com/blog/visualizing-the-gjk-collision-algorithm

public class Polygon implements CollisionShape
{
	public Vector2f pts[];
	public Rectangle bounds;
	public int size;

	/**
	 *  Constructor
	 *  @param ..pts -- set of points
	 */
	public Polygon(Vector2f... pts)
	{
		this.size = pts.length;
		this.pts = new Vector2f[this.size];

		int i = 0;
		for (Vector2f p : pts) {
			this.pts[i] = new Vector2f(p);
			++i;
		}
		bounds = this.getBoundingRectangle();
	}

	/**
	 *  Copy Constructor
	 *  @param p polygon to copy
	 */
	public Polygon(Polygon p)
	{
		this.pts = p.purePts();
		this.size = p.size;
		this.bounds = new Rectangle(p.bounds);
	}

	/**
	 *  shift all points
	 *  @param x x direction shift
	 *  @param y y direction shift
	 */
	public void shiftAllPoints(float x, float y)
	{
		for (int i = 0; i < size; ++i) {
			pts[i].add(x, y);
		}
	}

	/**
	 *  shift all points
	 *  @param n vector to sihft by
	 */
	public void shiftAllPoints(Vector2f n)
	{
		shiftAllPoints(n.x(), n.y());
	}


	/**
	 *  gets the points
	 *  @return  the set of points
	 */
	public Vector2f[] pts()
	{
		return pts;
	}

	/**
	 *  pure get a point in the polygon
	 *  @param index -- index of point to get
	 *  @return the point
	 */
	public Vector2f pureGetAPointInPolygon(int index)
	{
		return new Vector2f(pts[index]);
	}

	/**
	 *  pure gets the points
	 *  @return  the set of points
	 */
	public Vector2f[] purePts()
	{
		Vector2f tmp[] = new Vector2f[size];
		for (int i = 0; i < size; ++i) {
			tmp[i] = new Vector2f(pts[i]);
		}

		return tmp;
	}

	/**
	 *  get size
	 *  @return  size of the points
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 *  get size
	 *  @return  size of the points
	 */
	public int size()
	{
		return getSize();
	}

	/**
	 * Sets the first position point and shifts the rest of the points
	 * accordingly. Returns the vector that it shifted all the points by
	 *
	 * @param  n the new point
	 * @return   vector all points shifted by
	 */
	public Vector2f setFirstPositionAndShiftAll(Vector2f n)
	{
		Vector2f d = n.pureSubtract(pts[0]);
		shiftAllPoints(d);
		bounds.shiftRectangleBy(d);
		return d;
	}

	/**
	 * Sets the first position point and shifts the rest of the points
	 * accordingly. Returns the vector that it shifted all the points by
	 *
	 * @param  x x shift
	 * @param  y y shift
	 * @return   vector all points shifted by
	 */
	public Vector2f setFirstPositionAndShiftAll(float x, float y)
	{
		return setFirstPositionAndShiftAll(new Vector2f(x, y));
	}

	/**
	 * Sets the first position point and shifts the rest of the points
	 * accordingly but does not mutate the orignal buffer and returns a new
	 * one. Returns the vector that it shifted all the points by
	 *
	 * @param  x x shift
	 * @param  y y shift
	 * @return   polygon of the new shifted points
	 */
	public Polygon pureSetFirstPositionAndShiftAll(float x, float y)
	{
		Polygon p = new Polygon(this);
		p.setFirstPositionAndShiftAll(x, y);
		return p;
	}


	/**
	 * Gets the index of of the furthest point in a direction
	 *
	 * @param  dir direction
	 * @return   index of the point in the polygon
	 */
	public int indexOfFurthestPointInDirection(Vector2f dir)
	{
		Vector2f d = dir;
		int max = 0;
		float maxdist = -Float.MAX_VALUE;

		for (int i = 0; i < size; ++i) {
			// scalar projection on d
			final float tmp = Vector2f.dot(pts[i], d);

			if (tmp > maxdist) {
				max = i;
				maxdist = tmp;
			}
		}
		return max;
	}


	/**
	 * Gets the index of of the closest point in a direction
	 *
	 * @param  dir direction
	 * @return   index of the point in the polygon
	 */
	public int indexOfClosestPointInDirection(Vector2f dir)
	{
		Vector2f d = dir;
		int min = 0;
		float mindist = Float.MAX_VALUE;

		for (int i = 0; i < size; ++i) {
			// scalar projection on d
			final float tmp = Vector2f.dot(pts[i], d);

			if (tmp < mindist) {
				min = i;
				mindist = tmp;
			}
		}
		return min;
	}

	/**
	 * Gets the vector of of the furthest point in a direction
	 *
	 * @param  dir direction
	 * @return   vector of the point in the polygon
	 */
	public Vector2f furthestPointInDirection(Vector2f d)
	{
		return pts[indexOfFurthestPointInDirection(d)];
	}

	/**
	 * Gets the vector of of the closest point in a direction
	 *
	 * @param  dir direction
	 * @return   vector of the point in the polygon
	 */
	public Vector2f closestPointInDirection(Vector2f d)
	{
		return pts[indexOfClosestPointInDirection(d)];
	}

	/**
	 * to string metho
	 *
	 * @return   string of the data
	 */
	public String toString()
	{

		String str = "Polygon(" + super.toString() + "): size = " + size
			     + ", pts: ";

		for (Vector2f i : pts) {
			str += i.toString() + ", ";
		}

		return str;
	}

	/**
	 * gets the bounding rect
	 *
	 * @return   bounding rect
	 */
	public Rectangle getBoundingRectangle()
	{
		if (bounds == null) {
			return calculateBoundingRectangle();
		} else
			return bounds;
	}

	/**
	 * pure get bounding rect
	 *
	 * @return   bounding rect
	 */
	public Rectangle pureGetBoundingRectangle()
	{
		return new Rectangle(getBoundingRectangle());
	}

	/**
	 * get height of bounding rect
	 *
	 * @return   height of bounding rect
	 */
	public float getHeight()
	{
		return bounds.getHeight();
	}

	/**
	 * get width of bounding rect
	 *
	 * @return   width of bounding rect
	 */
	public float getWidth()
	{
		return bounds.getWidth();
	}
}
