package Game;
/**
 * Attack cycler handlers. Game handlers for the attack cycle
 * Date: February 10, 2019
 * @author Jared
 * @version 1.0
 */

import java.awt.Color;

import Components.AttackCycle;
import Components.CardinalDirections;
import Components.HasAnimation;
import Components.Movement;
import Components.MovementDirection;
import Components.PCollisionBody;
import Components.PHitBox;
import Components.PhysicsPCollisionBody;
import Components.WorldAttributes;
import EntitySets.Bullet;
import EntitySets.MobSet;
import EntitySets.PlayerSet;
import EntitySets.TurretSet;
import Resources.GameConfig;
import Resources.GameResources;

import poj.EngineState;
import poj.Collisions.GJK;
import poj.GameWindow.InputPoller;
import poj.linear.Vector2f;

public class AttackCycleHandlers
{

	public static void
	runAttackCycleHandlersAndFreezeMovement(PlayGame playGame)
	{
		EngineState engineState = playGame.getEngineState();

		double gameElapsedTime = playGame.getPlayTime();

		// players attacking
		for (int i = engineState.getInitialSetIndex(PlayerSet.class);
		     engineState.isValidEntity(i);
		     i = engineState.getNextSetIndex(PlayerSet.class, i)) {
			AttackCycle a = engineState.unsafeGetComponentAt(
				AttackCycle.class, i);

			if (a.isAttacking() && playGame.playerAmmo > 0) {
				switch (a.getAttackState()) {
				case 0:
					break;

				case 1:
					AttackCycleHandlers.playerAttackHandler(
						playGame);
					break;
				case 2:
					break;
				case 3:
					a.endAttackCycle();
					a.resetCycle();
					break;
				}
				// setting velocity to 0
				engineState
					.unsafeGetComponentAt(Movement.class, i)
					.setVelocity(new Vector2f(0, 0));
			} else {
				a.endAttackCycle();
				a.resetCycle();
			}
		}

		// mobs attacking
		for (int i = engineState.getInitialSetIndex(MobSet.class);
		     EngineState.isValidEntity(i);
		     i = engineState.getNextSetIndex(MobSet.class, i)) {
			AttackCycle a = engineState.unsafeGetComponentAt(
				AttackCycle.class, i);

			if (a.isAttacking()) {
				switch (a.getAttackState()) {
				case 0:
					AttackCycleHandlers
						.mobMeleeAttackPrimerHandler(
							engineState, i);
					break;

				case 1:
					AttackCycleHandlers
						.mobMeleeAttackHandler(playGame,
								       i);
					break;
				case 2:
					break;
				case 3:

					a.endAttackCycle();
					a.resetCycle();

					break;
				}

				// setting velocity to 0
				engineState
					.unsafeGetComponentAt(Movement.class, i)
					.setVelocity(new Vector2f(0, 0));
			}
		}

		// turret attack
		for (int i = engineState.getInitialSetIndex(TurretSet.class);
		     EngineState.isValidEntity(i);
		     i = engineState.getNextSetIndex(TurretSet.class, i)) {
			AttackCycle a = engineState.unsafeGetComponentAt(
				AttackCycle.class, i);

			if (a.isAttacking()) {
				switch (a.getAttackState()) {
				case 0:
					break;
				case 1:
					AttackCycleHandlers.turretAttackHandler(
						engineState, i,
						gameElapsedTime);
					break;
				case 2:
					break;
				case 3:
					a.endAttackCycle();
					a.resetCycle();
					break;
				}
			}
		}
	}

	/**
	 * Player's attack handler.
	 * Variable names should be intuitive.
	 */
	public static void playerAttackHandler(PlayGame playGame)
	{

		EngineState engineState = playGame.getEngineState();
		WeaponState playerCurWPState = playGame.curWeaponState;
		InputPoller ip = playGame.getInputPoller();
		Camera invCam = playGame.invCam;

		int player = engineState.getInitialSetIndex(PlayerSet.class);
		Vector2f playerPosition =
			engineState
				.unsafeGetComponentAt(
					PhysicsPCollisionBody.class, player)
				.getPolygon()
				.pureGetAPointInPolygon(0);

		switch (playerCurWPState) {
		case Gun:
			playerPosition.add(-GameConfig.PLAYER_WIDTH / 3,
					   -GameConfig.PLAYER_HEIGHT / 3);
			Vector2f mousePosition = ip.getMousePosition();
			mousePosition.matrixMultiply(invCam);

			Vector2f tmp =
				playerPosition.pureSubtract(mousePosition);
			tmp.negate();
			Vector2f unitVecPlayerPosToMouseDelta =
				tmp.pureNormalize();


			// generation of the bullet
			if (playGame.playerAmmo > 0) {
				int e = engineState.spawnEntitySet(
					new Bullet(playerPosition));
				engineState
					.unsafeGetComponentAt(
						PhysicsPCollisionBody.class, e)
					.setPositionPoint(
						engineState
							.unsafeGetComponentAt(
								WorldAttributes
									.class,
								player)
							.getCenteredBottomQuarter());
				float bulletSpeed =
					engineState
						.unsafeGetComponentAt(
							Movement.class, e)
						.getSpeed();

				engineState
					.unsafeGetComponentAt(Movement.class, e)
					.setVelocity(
						unitVecPlayerPosToMouseDelta
							.pureMul(bulletSpeed));

				// update animation and
				// play sound based on the availability
				// of the ammo!!
				GameResources.gunSound.play();

				engineState
					.unsafeGetComponentAt(
						HasAnimation.class, player)
					.setAnimation(AnimationGetter.queryPlayerSprite(
						CardinalDirections
							.getClosestDirectionFromDirectionVector(
								tmp),
						0));
				playGame.playerAmmo -= 1;
			}
			break;
		case Melee:
			System.out.println("attacked with melee weapon");
			break;
		}
	}


	public static void mobMeleeAttackPrimerHandler(EngineState engineState,
						       int focus)
	{
		final MovementDirection n = engineState.unsafeGetComponentAt(
			MovementDirection.class, focus);
		engineState.unsafeGetComponentAt(HasAnimation.class, focus)
			.setAnimation(AnimationGetter.queryEnemySprite(
				n.getDirection(), 2));
	}
	public static void mobMeleeAttackHandler(PlayGame playGame, int focus)
	{
		EngineState engineState = playGame.getEngineState();
		GJK gjk = playGame.gjk;

		// in the future make the hit boxes attack forward and just
		// witch upon them
		final MovementDirection n = engineState.unsafeGetComponentAt(
			MovementDirection.class, focus);

		// Spawn the hitbox in the correct location and check against
		// all enemies
		PCollisionBody pmob =
			new PCollisionBody(GameConfig.MOB_MELEE_ATTACK_BODY);
		Systems.updatePCollisionBodyPositionFromWorldAttr(
			pmob, engineState.unsafeGetComponentAt(
				      WorldAttributes.class, focus));

		// debug rendering
		Systems.pCollisionBodyDebugRenderer(pmob, playGame.debugBuffer,
						    playGame.cam, Color.orange);

		// testing for hits against  the player
		for (int i = engineState.getInitialSetIndex(PlayerSet.class);
		     engineState.isValidEntity(i);
		     i = engineState.getNextSetIndex(PlayerSet.class, i)) {

			PHitBox pplayer = engineState.unsafeGetComponentAt(
				PHitBox.class, i);

			if (Systems.arePCollisionBodiesColliding(gjk, pplayer,
								 pmob)) {
				CombatFunctions.handlePlayerDamage(
					engineState, i,
					GameConfig.MOB_ATTACK_DAMAGE);
				return; // shouldn't do damage to multiple
					// things
			}
		}

		// testing for hits against towers
		for (int i = engineState.getInitialSetIndex(TurretSet.class);
		     engineState.isValidEntity(i);
		     i = engineState.getNextSetIndex(TurretSet.class, i)) {
			PHitBox pturret = engineState.unsafeGetComponentAt(
				PHitBox.class, i);

			if (Systems.arePCollisionBodiesColliding(gjk, pturret,
								 pmob)) {
				CombatFunctions.handleMobDamageTurret(
					engineState, i);
				return;
			}
		}
	}

	public static void turretAttackHandler(EngineState engineState,
					       int turret, double gameTime)
	{
		CombatFunctions.turretTargeting(engineState, turret);
	}
}
