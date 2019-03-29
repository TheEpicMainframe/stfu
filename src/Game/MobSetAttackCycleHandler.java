package Game;

import java.awt.Color;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import Components.AggroRange;
import Components.PhysicsPCollisionBody;
import Components.CardinalDirections;
import Components.Movement;
import Components.MovementDirection;
import Components.PCollisionBody;
import Components.WorldAttributes;
import EntitySets.MobSet;
import EntitySets.PlayerSet;
import EntitySets.TurretSet;
import Resources.GameConfig;
import Resources.GameResources;

import poj.EngineState;
import poj.Collisions.GJK;


public class MobSetAttackCycleHandler implements EntityAttackSetHandler
{

	class MobStartingEvent extends FocusedPlayGameEvent
	{

		public MobStartingEvent(PlayGame g, int focus)
		{
			super(g, focus);
		}

		public void f()
		{
			EngineState engineState =
				getPlayGame().getEngineState();

			Optional<MovementDirection> dopt =
				engineState.getComponentAt(
					MovementDirection.class, focus);

			Optional<Movement> mopt = engineState.getComponentAt(
				Movement.class, focus);

			if (!dopt.isPresent()) {
				return;
			}

			if (!mopt.isPresent()) {
				return;
			}

			MovementDirection d = dopt.get();

			AttackCycleHandlers.meleeAttackPrimerHandler(
				engineState, focus, MobSet.class, 10,
				d.getDirection());
		}
	}

	class MobAttackEvent extends FocusedPlayGameEvent
	{
		public MobAttackEvent(PlayGame g, int f)
		{
			super(g, f);
		}

		public void f()
		{
			PlayGame playGame = super.getPlayGame();
			EngineState engineState = playGame.getEngineState();
			GJK gjk = playGame.gjk;

			// in the future make the hit boxes attack forward and
			// just witch upon them
			final MovementDirection n =
				engineState.unsafeGetComponentAt(
					MovementDirection.class, focus);

			// Spawn the hitbox in the correct location and check
			// against all enemies
			PCollisionBody pmob = new PCollisionBody(
				queryMeleeAttackBody(n.getDirection()));
			Systems.updatePCollisionBodyPositionFromWorldAttr(
				pmob, engineState.unsafeGetComponentAt(
					      WorldAttributes.class, focus));

			// debug rendering
			Systems.pCollisionBodyDebugRenderer(
				pmob, playGame.debugBuffer, playGame.cam,
				Color.orange);

			boolean playerHitByMob =
				EngineTransforms
					.doDamageInSetifPCollisionBodyAndSetPHitBoxAreColliding(
						engineState, pmob,
						PlayerSet.class,
						GameConfig.MOB_ATTACK_DAMAGE);
			if (playerHitByMob) {
				try {
					// play player hurt sound
					int hurtSoundPlay =
						ThreadLocalRandom.current()
							.nextInt(0, 4);
					switch (hurtSoundPlay) {
					case 0:
						GameResources.playerHpDropSound1
							.play();
						break;
					case 1:
						GameResources.playerHpDropSound2
							.play();
						break;
					case 2:
						GameResources.playerHpDropSound3
							.play();
						break;
					case 3:
						GameResources.playerHpDropSound4
							.play();
						break;
					}
				} catch (NullPointerException e) {
					System.out.println(
						"Error: Problem playing player hp drop sound");
					e.printStackTrace();
				}
			}

			EngineTransforms
				.doDamageInSetifPCollisionBodyAndSetPHitBoxAreColliding(
					engineState, pmob, TurretSet.class,
					GameConfig.MOB_ATTACK_DAMAGE);
		}
	}


	class MobAttackEnd extends FocusedPlayGameEvent
	{

		public MobAttackEnd(PlayGame g, int focus)
		{
			super(g, focus);
		}

		public void f()
		{
			EngineState engineState =
				super.getPlayGame().getEngineState();

			Optional<PhysicsPCollisionBody> pplayeropt =
				engineState.getComponentAt(
					PhysicsPCollisionBody.class,
					engineState.getInitialSetIndex(
						PlayerSet.class));

			Optional<MovementDirection> dmobopt =
				engineState.getComponentAt(
					MovementDirection.class, focus);

			Optional<AggroRange> agopt = engineState.getComponentAt(
				AggroRange.class, focus);

			if (!agopt.isPresent())
				return;

			if (!pplayeropt.isPresent())
				return;

			if (!dmobopt.isPresent())
				return;

			CardinalDirections d =
				CardinalDirections.getClosestDirectionFromDirectionVector(
					pplayeropt.get()
						.pureGetCenter()
						.pureSubtract(
							agopt.get()
								.pureGetCenter()));
			dmobopt.get().setDirection(d);
		}
	}

	private PCollisionBody queryMeleeAttackBody(CardinalDirections d)
	{

		switch (d) {
		case N:
			return GameConfig.MOB_MELEE_N_ATK_BODY;
		case NE:
			return GameConfig.MOB_MELEE_NE_ATK_BODY;
		case NW:
			return GameConfig.MOB_MELEE_NW_ATK_BODY;
		case S:
			return GameConfig.MOB_MELEE_S_ATK_BODY;
		case SE:
			return GameConfig.MOB_MELEE_SE_ATK_BODY;
		case SW:
			return GameConfig.MOB_MELEE_SW_ATK_BODY;
		case W:
			return GameConfig.MOB_MELEE_W_ATK_BODY;
		case E:
			return GameConfig.MOB_MELEE_E_ATK_BODY;
		default:
			return GameConfig.MOB_MELEE_E_ATK_BODY;
		}
	}

	public PlayGameEvent startingHandler(PlayGame g, int focus)
	{
		return new MobStartingEvent(g, focus);
	}
	public PlayGameEvent attackHandler(PlayGame g, int focus)
	{
		return new MobAttackEvent(g, focus);
	}
	public PlayGameEvent endAttackHandler(PlayGame g, int focus)
	{
		return new MobAttackEnd(g, focus);
	}
}
