package Game;


import Components.*;
import EntitySets.*;
import Resources.GameConfig;

import poj.EngineState;
import poj.GameWindow.InputPoller;
import poj.Component.*;
import poj.linear.Vector2f;
import poj.Animation;
import poj.Collisions.*;

public class PlayerAttackCycleHandler implements EntityAttackSetHandler
{

	class PlayerAttackPrimerEvent extends FocusedPlayGameEvent
	{
		PlayerAttackPrimerEvent(PlayGame g, int e)
		{
			super(g, e);
		}

		public void f()
		{
			EngineState engineState =
				super.getPlayGame().getEngineState();
			WeaponState playerCurWPState =
				super.getPlayGame().curWeaponState;
			InputPoller ip = super.getPlayGame().getInputPoller();
			Camera invCam = super.getPlayGame().invCam;

			int player =
				engineState.getInitialSetIndex(PlayerSet.class);
			Vector2f playerPosition =
				engineState
					.unsafeGetComponentAt(PHitBox.class,
							      player)
					.pureGetCenter();

			switch (playerCurWPState) {
			case Gun:
				break;
			case Melee:
				System.out.println(
					"attacked with melee weapon");
				break;
			}
		}
	}

	public class PlayerAttackEvent extends FocusedPlayGameEvent
	{
		PlayerAttackEvent(PlayGame g, int e)
		{
			super(g, e);
		}
		public void f()
		{

			EngineState engineState =
				super.getPlayGame().getEngineState();
			WeaponState playerCurWPState =
				super.getPlayGame().curWeaponState;
			InputPoller ip = super.getPlayGame().getInputPoller();
			Camera invCam = super.getPlayGame().invCam;

			int player =
				engineState.getInitialSetIndex(PlayerSet.class);
			Vector2f playerPosition =
				engineState
					.unsafeGetComponentAt(PHitBox.class,
							      player)
					.pureGetCenter();

			switch (playerCurWPState) {
			case Gun:

				Vector2f unitVecToMouse =
					AttackCycleHandlers
						.queryEntitySetWithPHitBoxToMouseDirection(
							super.getPlayGame(),
							player);

				engineState
					.unsafeGetComponentAt(
						HasAnimation.class, player)
					.setAnimation(AnimationGetter.queryPlayerSprite(
						CardinalDirections
							.getClosestDirectionFromDirectionVector(
								unitVecToMouse),
						0));

				// generation of the bullet
				if (super.getPlayGame().playerAmmo > 0) {
					int e = engineState.spawnEntitySet(
						new Bullet(playerPosition));
					engineState
						.unsafeGetComponentAt(
							PhysicsPCollisionBody
								.class,
							e)
						.setPositionPoint(
							engineState
								.unsafeGetComponentAt(
									PHitBox.class
									,
									player)
								.getCenter());
					float bulletSpeed =
						engineState
							.unsafeGetComponentAt(
								Movement.class,
								e)
							.getSpeed();

					engineState
						.unsafeGetComponentAt(
							Movement.class, e)
						.setVelocity(
							unitVecToMouse.pureMul(
								bulletSpeed));

					super.getPlayGame().playerAmmo -= 1;
				}
				break;
			case Melee:
				System.out.println(
					"attacked with melee weapon");
				break;
			}
		}
	}


	public class PlayerRecoilEvent extends FocusedPlayGameEvent
	{
		PlayerRecoilEvent(PlayGame g, int e)
		{
			super(g, e);
		}
		public void f()
		{
		}
	}

	public PlayGameEvent primerHandler(PlayGame g, int focus)
	{
		return new PlayerAttackPrimerEvent(g, focus);
	}
	public PlayGameEvent attackHandler(PlayGame g, int focus)
	{
		return new PlayerAttackEvent(g, focus);
	}
	public PlayGameEvent recoilHandler(PlayGame g, int focus)
	{
		return new PlayerRecoilEvent(g, focus);
	}
}
