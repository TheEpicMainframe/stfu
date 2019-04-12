package poj.Collisions;

/**
 * Quad tree -- quad tree for efficient collision querying.
 *
 * n cross dv cross n == perpendicular vector
 *
 * Date: March 10, 2019
 * @author  Jared Pon and code was taken / translated from / heavily influenced
 * from the following links:
 * https://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
 * @version  1.00
 */

import poj.linear.*;
import java.util.ArrayList;


public class QuadTree
{

	private static int MAX_OBJ =
		20; // number of objects it can hold before splitting
	private static int MAX_HEIGHT = 8; // deepest sublevel node


	public ArrayList<CollisionShape> objects;
	public Rectangle bounds;

	public int height;

	/*
	 * -------------
	 * |  2  |  3  |
	 * |     |     |
	 * |-----------|
	 * |  0  |  1  |
	 * |_____|____ |
	 */
	public QuadTree[] nodes; // subnodes


	/**
	 *  constrcutor
	 *  @param lvl ---height or level of this node
	 *  @param bouns -- rectangle bounds of the node
	 */
	public QuadTree(int lvl, Rectangle bounds)
	{
		this.height = lvl;
		this.bounds = bounds;
		objects = new ArrayList<CollisionShape>();
		nodes = new QuadTree[4];
	}


	/**
	 *  clears the quad tree
	 */
	public void clear()
	{
		objects.clear();

		for (int i = 0; i < nodes.length; ++i) {
			nodes[i].clear();
		}
	}

	/**
	 *  splits the quad tree according to the ascii diagram above
	 */
	private void split()
	{
		float w = bounds.getWidth();
		float h = bounds.getHeight();

		float subW = w / 2f;
		float subH = h / 2f;

		float x = bounds.getMinX();
		float y = bounds.getMinY();

		nodes[0] = new QuadTree(
			height + 1, new Rectangle(x, y, x + subW, y + subH));

		nodes[1] = new QuadTree(
			height + 1,
			new Rectangle(x + subW, y, x + w, y + subH));

		nodes[2] = new QuadTree(
			height + 1,
			new Rectangle(x, y + subH, x + subW, y + h));

		nodes[3] = new QuadTree(
			height + 1,
			new Rectangle(x + subW, y + subH, x + w, y + h));
	}


	/**
	 * getIndexQuadrant
	 *  gets the index of the quadrant for a collision shape
	 * returns the index quadrant (0,1,2,3) **see the ascii diagram for a
	 * description of what those mean, or -1 for an error
	 * @param c : collisionshape
	 * @return index of the quadrant
	 */
	private int getIndexQuadrant(CollisionShape c)
	{
		float xmin = bounds.getMinX();
		float ymin = bounds.getMinY();

		float xmid = bounds.getMinX() + (bounds.getWidth() / 2f);
		float ymid = bounds.getMinY() + (bounds.getHeight() / 2f);

		float xmax = bounds.getMaxX();
		float ymax = bounds.getMaxY();


		Rectangle cr = c.getBoundingRectangle();

		// 2,3
		boolean inTopQuadrant =
			(ymid <= cr.getMinY() && cr.getMaxY() <= ymax);

		// 0,1
		// boolean inBotQuadrant = !inTopQuadrant;
		boolean inBotQuadrant =
			(ymin <= cr.getMinY() && cr.getMaxY() <= ymid);

		// 3,1
		boolean inRightQuadrant =
			(xmid <= cr.getMinX() && cr.getMaxX() <= xmax);

		// 2,0
		// boolean inLeftQuadrant = !inRightQuadrant;
		boolean inLeftQuadrant =
			(xmin <= cr.getMinX() && cr.getMaxX() <= xmid);

		// completily in region 3
		if (inTopQuadrant && inRightQuadrant) {
			return 3;
		}

		if (inTopQuadrant && inLeftQuadrant) {
			return 2;
		}

		if (inBotQuadrant && inRightQuadrant) {
			return 1;
		}
		if (inBotQuadrant && inLeftQuadrant) {
			return 0;
		}

		return -1;
	}

	/**
	 *  has children. Checks to see if it has children
	 *  @return true if it has children
	 */
	private boolean hasChildren()
	{
		// either all should have children or none should have children
		return nodes[0] != null;
	}

	/**
	 *  insert a collisoin shape in the tree
	 *  @param c collision shape
	 */
	public void insert(CollisionShape c)
	{
		// has children, so just insert it down
		if (hasChildren()) {
			int q = getIndexQuadrant(c);

			if (q != -1) {
				nodes[q].insert(c);
				return;
			}
		}

		// other wise, just add the object to the final "leaf node"
		objects.add(c);

		// if the focused node has more than the max objects and there
		// are still levels to go down, then split the tree and
		// propogate the collision box down
		if (objects.size() > MAX_OBJ && height < MAX_HEIGHT) {
			if (!hasChildren()) {
				split();
			}

		} else {
			return;
		}

		for (int i = 0; i < objects.size(); ++i) {
			int q = getIndexQuadrant(objects.get(i));

			if (q != -1) {
				nodes[q].insert(objects.remove(i));
				--i;
			}
		}
	}


	/**
	 *  queries the possible collisions
	 *  @param c collison shape to query against
	 *  @param destBuffer buffer to hold the possible collisions
	 */
	public void queryCollisions(CollisionShape c,
				    ArrayList<CollisionShape> destBuffer)
	{
		int q = getIndexQuadrant(c);

		if (hasChildren()) {
			if (q != -1)
				nodes[q].queryCollisions(c, destBuffer);
			else {
				for (int i = 0; i < 4; ++i)
					destBuffer.addAll(nodes[i].objects);
			}
		}

		// else if
		destBuffer.addAll(objects); // sorta like a monoid
	}
}
