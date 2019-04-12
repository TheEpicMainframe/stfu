package Game;
/**
 * Attack cycler handlers. Game handlers for the attack cycle
 * Date: February 10, 2019
 * @author Jared
 * @version 1.0
 */

import java.awt.Color;
import java.util.Optional;

import Components.AttackCycle;
import Components.CardinalDirections;
import Components.HasAnimation;
import Components.AnimationWindowAssets;
import Components.Movement;
import Components.MovementDirection;
import Components.PCollisionBody;
import Components.PHitBox;
import Components.PhysicsPCollisionBody;
import Components.WorldAttributes;
import EntitySets.BossSet;
import EntitySets.Bullet;
import EntitySets.MobSet;
import EntitySets.PlayerSet;
import EntitySets.TurretSet;
import Game.GameEvents.PlayGameEvent;
import Resources.GameConfig;
import Resources.GameResources;

import poj.EngineState;
import poj.Collisions.GJK;
import poj.GameWindow.InputPoller;
import poj.Logger.Logger;
import poj.Component.*;
import poj.linear.Vector2f;


public class AttackCycleHandlers
{

	/**
	 * Runs the attack cycles
	 * @param playGame: playgame
	 */
	public static void runAttackCyclers(PlayGame playGame)
	{


		AttackCycleHandlers.runAttackCyclerHandler(playGame,
							   PlayerSet.class);
		AttackCycleHandlers.runAttackCyclerHandler(playGame,
							   MobSet.class);
		AttackCycleHandlers.runAttackCyclerHandler(playGame,
							   BossSet.class);
		AttackCycleHandlers.runAttackCyclerHandler(playGame,
							   TurretSet.class);
	}

	/**
	 * Generalized way to query an entity's direcion towards the mouse
	 * @param playGame godly game state
	 * @param focus  focused entity
	 */
	public static Vector2f
	queryEntitySetWithPHitBoxToMouseDirection(PlayGame playGame, int focus)
	{
		EngineState engineState = playGame.getEngineState();
		InputPoller ip = playGame.getInputPoller();

		Camera invCam = playGame.getInvCam();

		Vector2f focusPos =
			engineState.unsafeGetComponentAt(PHitBox.class, focus)
				.pureGetCenter();

		Vector2f mousePosition = ip.getMousePosition();
		mousePosition.matrixMultiply(invCam);

		Vector2f tmp = focusPos.pureSubtract(mousePosition);
		tmp.negate();

		Vector2f unitVecToMouse = tmp.pureNormalize();

		return unitVecToMouse;
	}


	/**
	 * Generalized melee attack handler -- freeze aand point in the
	 * direction
	 * @param engineState  engineState
	 * @param focus  focused entity
	 * @param c  Entity set to run the melee attack handler
	 * @param animationFlag  flag for the animation
	 * @param d  direction for animation
	 */
	public static void meleeAttackPrimerHandler(EngineState engineState,
						    int focus,
						    int animationFlag,
						    CardinalDirections d)
	{
		final Optional<Movement> mOpt =
			engineState.getComponentAt(Movement.class, focus);

		final Optional<HasAnimation> animationOpt =
			engineState.getComponentAt(HasAnimation.class, focus);

		final Optional<AnimationWindowAssets> animAssetsOpt =
			engineState.getComponentAt(AnimationWindowAssets.class,
						   focus);

		if (!animationOpt.isPresent())
			return;

		if (!mOpt.isPresent())
			return;

		if (!animAssetsOpt.isPresent())
			return;

		HasAnimation animation = animationOpt.get();

		animation.setAnimation(
			animAssetsOpt.get().getAnimation(d, animationFlag));

		mOpt.get().setVelocity(0f, 0f);
	}


	/**
	 * queries entity attack set handler from class
	 * @param c: type
	 */
	// reduces the allocations during the main game loop
	private static EntityAttackSetHandler PLAYER_ATTACK_CYCLE_HANDLER_MEMO =
		new PlayerAttackCycleHandler();
	private static EntityAttackSetHandler MOBSET_ATTACK_CYCLE_HANDLER_MEMO =
		new MobSetAttackCycleHandler();
	private static EntityAttackSetHandler
		TURRETSET_ATTACK_CYCLE_HANDLER_MEMO =
			new TurretAttackCyclerHandler();

	public static EntityAttackSetHandler
	queryEntityAttackSetHandler(Class<? extends Component> c)
	{
		if (c == PlayerSet.class)
			return PLAYER_ATTACK_CYCLE_HANDLER_MEMO;
		else if (c == MobSet.class || c == BossSet.class)
			return MOBSET_ATTACK_CYCLE_HANDLER_MEMO;
		else if (c == TurretSet.class) {
			return TURRETSET_ATTACK_CYCLE_HANDLER_MEMO;
		} else {
			Logger.logMessage("error in attack cycle handler");
			return PLAYER_ATTACK_CYCLE_HANDLER_MEMO;
		}
	}


	/**
	 * generalized way to run attack cycle handlers
	 * @param playGame: play game state
	 * @param c: type
	 */
	public static void runAttackCyclerHandler(PlayGame playGame,
						  Class<? extends Component> c)
	{

		EngineState engineState = playGame.getEngineState();
		double gameElapsedTime = playGame.getPlayTime();

		EntityAttackSetHandler atkHandler =
			queryEntityAttackSetHandler(c);

		for (int i = engineState.getInitialSetIndex(c);
		     engineState.isValidEntity(i);
		     i = engineState.getNextSetIndex(c, i)) {

			Optional<AttackCycle> aOpt = engineState.getComponentAt(
				AttackCycle.class, i);

			if (!aOpt.isPresent())
				continue;

			AttackCycle a = aOpt.get();

			if (a.isAttacking()) {
				switch (a.getAttackState()) {
				case 0: // starting
					pushAttackEventToAttackHandler(
						playGame,
						atkHandler.startingHandler(
							playGame, i));
					break;

				case 1: // priming
					pushAttackEventToAttackHandler(
						playGame,
						atkHandler.primerHandler(
							playGame, i));
					break;

				case 2: // attack
					pushAttackEventToAttackHandler(
						playGame,
						atkHandler.attackHandler(
							playGame, i));
					break;
				case 3: // recoil
					pushAttackEventToAttackHandler(
						playGame,
						atkHandler.recoilHandler(
							playGame, i));
					break;

				case 4: // end attack cycle
					pushAttackEventToAttackHandler(
						playGame,
						atkHandler.endAttackHandler(
							playGame, i));

					a.endAttackCycle();
					a.resetCycle();
					break;
				}
			}
		}
	}

	/**
	 * pushes the events to attack handler
	 * @param g: play game state
	 * @param e: event
	 */
	private static void pushAttackEventToAttackHandler(PlayGame g,
							   PlayGameEvent e)
	{
		if (e != null)
			g.pushEventToEventHandler(e);
	}
}
