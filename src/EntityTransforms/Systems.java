package EntityTransforms;

import Components.*;
import Game.Camera;
import poj.linear.*;
import poj.Render.Renderer;

public class Systems
{
	public static boolean areCollisionBodiesColliding(CollisionBody a,
							  CollisionBody b)
	{

		for (Rectanglef i : a.getCollisionBodies()) {

			for (Rectanglef j : b.getCollisionBodies()) {
				if (Rectanglef.isColliding(i, j))
					return true;
			}
		}
		return false;
	}

	public static void updateRenderScreenCoordinatesFromWorldCoordinates(
		WorldAttributes p, Render r, final Camera c)
	{
		Vector2f tmp =
			Systems.getRenderScreenCoordinateFromWorldCoordinate(p,
									     c);
		r.setTopLeftCornerPosition(Math.round(tmp.getX()),
					   Math.round(tmp.getY()));
		r.addTranslation();
	}

	public static Vector2f
	getRenderScreenCoordinateFromWorldCoordinate(final WorldAttributes p,
						     final Camera c)
	{
		// Vector2f topleftcoord = p.getTopLeftCoordFromOrigin();
		Vector2f topleftcoord = p.getOriginCoord();
		topleftcoord.matrixMultiply(c);
		return topleftcoord;
	}

	public static void pushRenderComponentToRenderer(Render r, Renderer ren)
	{
		ren.pushRenderObject(r.getGraphic());
	}

	public static void
	updateRenderComponentWindowFromHasAnimation(Render r, HasAnimation a)
	{
		r.setImageWindow(a.getImageWindow());
	}

	public static void updateHasAnimationComponent(HasAnimation a,
						       long dtms)
	{
		a.updateAnimation(dtms);
	}


	public static Vector2f
	getVelocityFromDirectionAndSpeed(MovementDirection d, Speed v)
	{
		Vector2f tmp = d.getUnitVector();
		tmp.mul(v.getSpeed());
		return tmp;
	}
}
