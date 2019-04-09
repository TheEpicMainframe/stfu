package poj.Collisions;

import poj.linear.Vector2f;

public interface CollisionShape {

	// furthest point in direction vector.
	public Vector2f furthestPointInDirection(Vector2f d);

	public default Rectangle calculateBoundingRectangle()
	{
		Vector2f xmax = furthestPointInDirection(Constants.E);
		Vector2f xmin = furthestPointInDirection(Constants.W);

		Vector2f ymax = furthestPointInDirection(Constants.N);
		Vector2f ymin = furthestPointInDirection(Constants.S);

		return new Rectangle(new Vector2f(ymin.y(), xmin.x()),
				     new Vector2f(ymax.y(), xmax.x()));
	}

	public default float getHeight()
	{
		return calculateBoundingRectangle().getHeight();
	}

	public default float getWidth()
	{
		return calculateBoundingRectangle().getWidth();
	}


	public default Rectangle getBoundingRectangle()
	{
		return calculateBoundingRectangle();
	}

	public default Rectangle pureGetBoundingRectangle()
	{
		return new Rectangle(getBoundingRectangle());
	}

	public static float
	calculateBoundingAreaOfCollisionShapes(CollisionShape a,
					       CollisionShape b)
	{
		// float abA = new Rectangle(a.getMin(), b.getMin());

		return 1;
	}
}
