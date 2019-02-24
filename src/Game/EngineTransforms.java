package Game;
import poj.EngineState;
import poj.Component.*;
import poj.Logger.Logger;

import java.util.Optional;

import Components.*;

import poj.linear.Vector2f;

import Resources.*;
import TileMap.*;

import poj.Render.Renderer;

public class EngineTransforms
{
	public static void updateAnimationWindows(EngineState engineState,
						  double dt)
	{
		for (HasAnimation i :
		     engineState.getRawComponentArrayListPackedData(
			     HasAnimation.class)) {
			Systems.updateHasAnimationComponent(i, dt);
		}
	}

	public static void
	cropSpriteSheetsFromAnimationWindows(EngineState engineState)
	{

		for (int i = engineState.getInitialComponentIndex(
			     HasAnimation.class);
		     Components.isValidEntity(i);
		     i = engineState.getNextComponentIndex(HasAnimation.class,
							   i)) {
			Systems.updateRenderComponentWindowFromHasAnimation(
				engineState.getComponentAt(Render.class, i),
				engineState.getComponentAt(HasAnimation.class,
							   i));
		}
	}


	public static void
	updateRenderScreenCoordinatesFromWorldCoordinatesWithCamera(
		EngineState engineState, final Camera cam)
	{
		for (int i = engineState.getInitialComponentIndex(
			     WorldAttributes.class);
		     Components.isValidEntity(i);
		     i = engineState.getNextComponentIndex(
			     WorldAttributes.class, i)) {
			Systems.updateRenderScreenCoordinatesFromWorldCoordinates(
				engineState.getComponentAt(
					WorldAttributes.class, i),
				engineState.getComponentAt(Render.class, i),
				cam);
		}
	}

	public static void
	updateWorldAttribPositionFromMovement(EngineState engineState,
					      double dt)
	{
		for (int i = engineState.getInitialComponentIndex(
			     Movement.class);
		     Components.isValidEntity(i);
		     i = engineState.getNextComponentIndex(Movement.class, i)) {
			Systems.updateWorldAttribPositionFromMovement(
				engineState.getComponentAt(
					WorldAttributes.class, i),
				engineState.getComponentAt(Movement.class, i),
				dt);
		}
	}


	public static void
	setMovementVelocityFromMovementDirection(EngineState engineState)
	{
		for (int i = engineState.getInitialSetIndex(
			     MovementDirection.class);
		     Components.isValidEntity(i);
		     i = engineState.getNextSetIndex(MovementDirection.class,
						     i)) {
			Systems.setMovementVelocityFromMovementDirection(
				engineState.getComponentAt(Movement.class, i),
				engineState.getComponentAt(
					MovementDirection.class, i));
		}
	}

	public static void
	addPlayerDiffusionValAtPlayerPos(EngineState engineState, Map map,
					 MapLayer mapLayer, int player)
	{


		Vector2f playerPosition =
			engineState
				.getComponentAt(WorldAttributes.class, player)
				.getCenteredBottomQuarter();
		/*
		System.out.println("player X=" + playerPosition.x);
		System.out.println("player Y=" + playerPosition.y);
		System.out.println(
			"this.map.getEcsIndexFromWorldVector2f(playerPosition)"
			+ this.map.getEcsIndexFromWorldVector2f(
				  playerPosition));
		*/
		System.out.println(
			"player x position inside addPlayerDiffusionValAtPlayerPos ="
			+ playerPosition.x);
		System.out.println(
			"player y position inside addPlayerDiffusionValAtPlayerPos ="
			+ playerPosition.y);
		if (map.getEcsIndexFromWorldVector2f(playerPosition) != -1) {
			// map.printPathfindCord(0);

			if (!mapLayer.getComponentAt(
					     PathFindCord.class,
					     map.getEcsIndexFromWorldVector2f(
						     playerPosition))
				     .getIsWall()) {

				mapLayer.getComponentAt(
						PathFindCord.class,
						map.getEcsIndexFromWorldVector2f(
							playerPosition))
					.setDiffusionValue(
						GameConfig
							.PLAYER_DIFFUSION_VALUE);
			} else {

				mapLayer.getComponentAt(
						PathFindCord.class,
						map.getEcsIndexFromWorldVector2f(
							playerPosition))
					.setDiffusionValue(0f);
			}
		}
	}


	public static void
	updateCollisionAabbBoxBodiesTopLeftFromWorldAttributes(
		EngineState engineState)
	{

		for (int i = engineState.getInitialComponentIndex(
			     CollisionAabbBodies.class);
		     Components.isValidEntity(i);
		     i = engineState.getNextSetIndex(CollisionAabbBodies.class,
						     i)) {
			Systems.updateAabbCollisionBodiesTopLeftFromWorldAttributes(
				engineState.getComponentAt(
					CollisionAabbBodies.class, i),
				engineState.getComponentAt(
					WorldAttributes.class, i));
		}
	}

	public static void
	aabbCollisionBodiesResolve(EngineState engineState,
				   Class<? extends Component> set0,
				   Class<? extends Component> set1, double dt)
	{
		for (int i = engineState.getInitialSetIndex(set0);
		     Components.isValidEntity(i);
		     i = engineState.getNextSetIndex(set0, i)) {

			final CollisionAabbBodies a =
				engineState.getComponentAt(
					CollisionAabbBodies.class, i);
			Movement va =
				engineState.getComponentAt(Movement.class, i);

			for (int j = engineState.getInitialSetIndex(set1);
			     Components.isValidEntity(j);
			     j = engineState.getNextSetIndex(set1, j)) {

				final CollisionAabbBodies b =
					engineState.getComponentAt(
						CollisionAabbBodies.class, j);
				Movement vb = engineState.getComponentAt(
					Movement.class, j);


				final Optional<Double> tmp =
					Systems.calcTimeForCollisionForAabbBodies(
						a, b, va.getVelocity(),
						vb.getVelocity());

				if (tmp.isPresent()) {
					double timestep;
					if (tmp.get() == 0d) {
						timestep = -dt;
					} else {
						timestep =
							(tmp.get().doubleValue()
							 - 0.01f)
							/ dt;
					}
					va.getVelocity().mul((float)timestep);
					vb.getVelocity().mul((float)timestep);
					System.out.println("collisions?");
					break;
				}
			}
		}
	}

	public static void
	aabbCollisionBodiesCheckCollision(EngineState engineState,
					  Class<? extends Component> set0,
					  Class<? extends Component> set1)
	{
		for (int i = engineState.getInitialSetIndex(set0);
		     Components.isValidEntity(i);
		     i = engineState.getNextSetIndex(set0, i)) {

			final CollisionAabbBodies a =
				engineState.getComponentAt(
					CollisionAabbBodies.class, i);

			for (int j = engineState.getInitialSetIndex(set1);
			     Components.isValidEntity(j);
			     j = engineState.getNextSetIndex(set1, j)) {

				final CollisionAabbBodies b =
					engineState.getComponentAt(
						CollisionAabbBodies.class, j);
				if (Systems.areAabbCollisionBodiesColliding(
					    a, b)) {
					System.out.println("Are colliding");
					break;
				}
			}
		}
	}

	public static void
	debugAabbCollisionBodiesRender(EngineState engineState, Renderer r,
				       final Camera cam)
	{
		for (int i = engineState.getInitialSetIndex(
			     CollisionAabbBodies.class);
		     Components.isValidEntity(i);
		     i = engineState.getNextSetIndex(CollisionAabbBodies.class,
						     i)) {
			Systems.aabbCollisionBodiesDebugRender(
				engineState.getComponentAt(
					CollisionAabbBodies.class, i),
				r, cam);
		}
	}

	public static void
	updateCircleCollisionFromWorldAttributes(EngineState engineState)
	{
		for (int i = engineState.getInitialSetIndex(
			     CircleCollisionBody.class);
		     Components.isValidEntity(i);
		     i = engineState.getNextSetIndex(CircleCollisionBody.class,
						     i)) {
			Systems.updateCircleCollisionFromWorldAttributes(
				engineState.getComponentAt(
					CircleCollisionBody.class, i),
				engineState.getComponentAt(
					WorldAttributes.class, i));
		}
	}

	public static void
	updateAabbCollisionFromWorldAttributes(EngineState engineState)
	{
		for (int i = engineState.getInitialSetIndex(
			     AabbCollisionBody.class);
		     Components.isValidEntity(i);
		     i = engineState.getNextSetIndex(AabbCollisionBody.class,
						     i)) {
			Systems.updateAabbCollisionBodyFromWorldAttributes(
				engineState.getComponentAt(
					AabbCollisionBody.class, i),
				engineState.getComponentAt(
					WorldAttributes.class, i));
		}
	}

	public static void
	areCirclesCollidingFromSets(EngineState engineState,
				    Class<? extends Component> set0,
				    Class<? extends Component> set1)
	{

		for (int i = engineState.getInitialSetIndex(set0);
		     Components.isValidEntity(i);
		     i = engineState.getNextSetIndex(set0, i)) {

			final CircleCollisionBody a =
				engineState.getComponentAt(
					CircleCollisionBody.class, i);

			for (int j = engineState.getInitialSetIndex(set1);
			     Components.isValidEntity(j);
			     j = engineState.getNextSetIndex(set1, j)) {

				final CircleCollisionBody b =
					engineState.getComponentAt(
						CircleCollisionBody.class, j);
				if (Systems.areCollisionCirclesColliding(a,
									 b)) {
					System.out.println("Are colliding");
					break;
				}
			}
		}
	}

	public static void
	areCirclesCollidingAgainstAabb(EngineState engineState,
				       Class<? extends Component> set0,
				       Class<? extends Component> set1)
	{
		for (int i = engineState.getInitialSetIndex(set0);
		     Components.isValidEntity(i);
		     i = engineState.getNextSetIndex(set0, i)) {

			final CircleCollisionBody a =
				engineState.getComponentAt(
					CircleCollisionBody.class, i);

			for (int j = engineState.getInitialSetIndex(set1);
			     Components.isValidEntity(j);
			     j = engineState.getNextSetIndex(set1, j)) {

				final AabbCollisionBody b =
					engineState.getComponentAt(
						AabbCollisionBody.class, j);
				if (Systems.areCollisionCirclesCollidingAgainstAabb(
					    a, b)) {
					System.out.println("Are colliding");
					break;
				}
			}
		}
	}

	public static void resolveCircleCollisionBodyWithAabbCollisionBody(
		EngineState engineState, Class<? extends Component> set0,
		Class<? extends Component> set1, double dt)
	{
		for (int i = engineState.getInitialSetIndex(set0);
		     Components.isValidEntity(i);
		     i = engineState.getNextSetIndex(set0, i)) {

			final CircleCollisionBody a =
				engineState.getComponentAt(
					CircleCollisionBody.class, i);
			Movement m =
				engineState.getComponentAt(Movement.class, i);

			for (int j = engineState.getInitialSetIndex(set1);
			     Components.isValidEntity(j);
			     j = engineState.getNextSetIndex(set1, j)) {

				final AabbCollisionBody b =
					engineState.getComponentAt(
						AabbCollisionBody.class, j);

				Systems.resolveCircleCollisionBodyWithAabbCollisionBody(
					m, a, b, dt);
			}
		}
	}

	public static void pushCircleCollisionBodyOutOfAabbCollisionBody(
		EngineState engineState, Class<? extends Component> set0,
		Class<? extends Component> set1)
	{
		for (int i = engineState.getInitialSetIndex(set0);
		     Components.isValidEntity(i);
		     i = engineState.getNextSetIndex(set0, i)) {

			final CircleCollisionBody a =
				engineState.getComponentAt(
					CircleCollisionBody.class, i);
			WorldAttributes w = engineState.getComponentAt(
				WorldAttributes.class, i);

			for (int j = engineState.getInitialSetIndex(set1);
			     Components.isValidEntity(j);
			     j = engineState.getNextSetIndex(set1, j)) {

				final AabbCollisionBody b =
					engineState.getComponentAt(
						AabbCollisionBody.class, j);

				if (Systems.areCollisionCirclesCollidingAgainstAabb(
					    a, b)) {
					System.out.println("coollisions");
					Systems.pushCircleCollisionBodyOutOfAabbCollisionBody(
						w, a, b);
					break;
				}
			}
		}
	}


	public static void debugCircleCollisionRender(EngineState engineState,
						      Renderer r,
						      final Camera cam)
	{
		for (int i = engineState.getInitialSetIndex(
			     CircleCollisionBody.class);
		     Components.isValidEntity(i);
		     i = engineState.getNextSetIndex(CircleCollisionBody.class,
						     i)) {
			Systems.circleCollisionDebugRenderer(
				engineState.getComponentAt(
					CircleCollisionBody.class, i),
				r, cam);
		}
	}

	public static void debugAabbCollisionRender(EngineState engineState,
						    Renderer r,
						    final Camera cam)
	{
		for (int i = engineState.getInitialSetIndex(
			     AabbCollisionBody.class);
		     Components.isValidEntity(i);
		     i = engineState.getNextSetIndex(AabbCollisionBody.class,
						     i)) {
			Systems.aabbCollisionBodyDebugRender(
				engineState.getComponentAt(
					AabbCollisionBody.class, i),
				r, cam);
		}
	}
}
