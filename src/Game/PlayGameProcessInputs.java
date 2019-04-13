package Game;

import Resources.*;

import java.awt.event.KeyEvent;

import poj.EngineState;
import poj.GameWindow.InputPoller;
import poj.Logger.Logger;
import poj.linear.*;

import Components.*;
import EntitySets.*;
import Game.GameEvents.*;

public class PlayGameProcessInputs
{

	protected static void updateCoolDownKeys(World g)
	{
		for (int i = 0; i < Resources.GameConfig.COOL_DOWN_KEYS.size();
		     ++i) {
			updateDtForKey(
				g,
				Resources.GameConfig.COOL_DOWN_KEYS.get(i).fst,
				g.dt / 1000);
		}
	}
	public static void updateDtForKey(World g, int keyIndex, double val)
	{
		// if the key cooldown is not 0 is necessary here
		// because we don't want to subtract it to neg infinity..
		if (g.lastCoolDown.get(keyIndex) - val > EPSILON) {
			g.lastCoolDown.set(keyIndex,
					   g.lastCoolDown.get(keyIndex) - val);
		} else {
			g.lastCoolDown.set(keyIndex, 0d);
		}
	}

	public static void playGameProcessInputs(PlayGame g)
	{
		EngineState engineState = g.getEngineState();
		InputPoller inputPoller = g.getInputPoller();

		for (int player =
			     engineState.getInitialSetIndex(PlayerSet.class);
		     poj.EngineState.isValidEntity(player);
		     player = engineState.getNextSetIndex(PlayerSet.class,
							  player)) {


			////// Combat Commands //////

			if (inputPoller.isKeyDown(GameConfig.ATTACK_KEY)
			    || inputPoller.isLeftMouseButtonDown()) {
				if (Math.abs(g.lastCoolDown.get(
					    GameConfig.ATTACK_KEY))
				    == 0d) {


					updateDtForKey(
						g, GameConfig.ATTACK_KEY,
						-PlayGame.coolDownMax.get(
							GameConfig.ATTACK_KEY));

					engineState
						.unsafeGetComponentAt(
							AttackCycle.class,
							player)
						.startAttackCycle();
					return;
				}
			}
			if (inputPoller.isKeyDown(GameConfig.SWITCH_WEAPONS)) {

				if (Math.abs(g.lastCoolDown.get(
					    GameConfig.SWITCH_WEAPONS))
				    == 0d) {
					updateDtForKey(
						g, GameConfig.SWITCH_WEAPONS,
						-PlayGame.coolDownMax.get(
							GameConfig
								.SWITCH_WEAPONS));
					System.out.print(
						"x key is down. Player character should be changing weapons\n");
					System.out.println(
						"old weapon state = "
						+ g.curWeaponState
							  .currentWeaponState());
					g.curWeaponState =
						g.curWeaponState.next();
					System.out.println(
						"new weapon state = "
						+ g.curWeaponState
							  .currentWeaponState());
				}
			}

			boolean hasMovementKeyBeenPressed = true;

			////// Movement Commands //////
			if (inputPoller.isKeyDown(KeyEvent.VK_W)
			    && inputPoller.isKeyDown(KeyEvent.VK_D)) {
				engineState
					.unsafeGetComponentAt(
						MovementDirection.class, player)
					.setDirection(CardinalDirections.W);
				engineState
					.unsafeGetComponentAt(Movement.class,
							      player)
					.setSpeed(GameConfig.PLAYER_SPEED);
			} else if (inputPoller.isKeyDown(KeyEvent.VK_W)
				   && inputPoller.isKeyDown(KeyEvent.VK_A)) {
				engineState
					.unsafeGetComponentAt(
						MovementDirection.class, player)
					.setDirection(CardinalDirections.S);
				engineState
					.unsafeGetComponentAt(Movement.class,
							      player)
					.setSpeed(GameConfig.PLAYER_SPEED);
			} else if (inputPoller.isKeyDown(KeyEvent.VK_S)
				   && inputPoller.isKeyDown(KeyEvent.VK_A)) {
				engineState
					.unsafeGetComponentAt(
						MovementDirection.class, player)
					.setDirection(CardinalDirections.E);
				engineState
					.unsafeGetComponentAt(Movement.class,
							      player)
					.setSpeed(GameConfig.PLAYER_SPEED);
			} else if (inputPoller.isKeyDown(KeyEvent.VK_S)
				   && inputPoller.isKeyDown(KeyEvent.VK_D)) {
				engineState
					.unsafeGetComponentAt(
						MovementDirection.class, player)
					.setDirection(CardinalDirections.N);
				engineState
					.unsafeGetComponentAt(Movement.class,
							      player)
					.setSpeed(GameConfig.PLAYER_SPEED);

			} else if (inputPoller.isKeyDown(
					   KeyEvent.VK_W)) { // single Key
							     // movements
				engineState
					.unsafeGetComponentAt(
						MovementDirection.class, player)
					.setDirection(CardinalDirections.SW);
				engineState
					.unsafeGetComponentAt(Movement.class,
							      player)
					.setSpeed(GameConfig.PLAYER_SPEED);
			} else if (inputPoller.isKeyDown(KeyEvent.VK_D)) {
				engineState
					.unsafeGetComponentAt(
						MovementDirection.class, player)
					.setDirection(CardinalDirections.NW);
				engineState
					.unsafeGetComponentAt(Movement.class,
							      player)
					.setSpeed(GameConfig.PLAYER_SPEED);
			} else if (inputPoller.isKeyDown(KeyEvent.VK_A)) {
				engineState
					.unsafeGetComponentAt(
						MovementDirection.class, player)
					.setDirection(CardinalDirections.SE);
				engineState
					.unsafeGetComponentAt(Movement.class,
							      player)
					.setSpeed(GameConfig.PLAYER_SPEED);
			} else if (inputPoller.isKeyDown(KeyEvent.VK_S)) {
				engineState
					.unsafeGetComponentAt(
						MovementDirection.class, player)
					.setDirection(CardinalDirections.NE);
				engineState
					.unsafeGetComponentAt(Movement.class,
							      player)
					.setSpeed(GameConfig.PLAYER_SPEED);
			} else // no movement key is pressed
			{
				hasMovementKeyBeenPressed = false;
				engineState
					.unsafeGetComponentAt(Movement.class,
							      player)
					.setSpeed(0);

				engineState.unsafeGetComponentAt(
					FacingDirection.class, player);
			}


			if (!engineState
				     .unsafeGetComponentAt(AttackCycle.class,
							   player)
				     .isAttacking()) {
				int flag = hasMovementKeyBeenPressed ? 1 : 0;


				if (g.curWeaponState == WeaponState.Melee) {
					flag += 2;
				}
				engineState
					.unsafeGetComponentAt(
						HasAnimation.class, player)
					.setAnimation(
						engineState
							.unsafeGetComponentAt(
								AnimationWindowAssets
									.class,
								player)
							.getAnimation(
								engineState
									.unsafeGetComponentAt(
										MovementDirection
											.class
										,
										player)
									.getDirection(),
								flag));
			} else {
				engineState
					.unsafeGetComponentAt(Movement.class,
							      player)
					.setSpeed(0);
			}


			////// Build Commands //////
			if (inputPoller.isKeyDown(GameConfig.BUILD_TOWER)) {

				Logger.logMessage(
					" last cooldown = "
					+ g.lastCoolDown.get(
						  GameConfig.BUILD_TOWER));
				if (Math.abs(g.lastCoolDown.get(
					    GameConfig.BUILD_TOWER))
					    == 0d
				    && g.playerMoney.get()
					       >= GameConfig.TOWER_BUILD_COST) {
					// player position is also the top left
					// of the polygon !
					Vector2f playerPosition =
						engineState
							.unsafeGetComponentAt(
								PhysicsPCollisionBody
									.class,
								player)
							.pureGetCenter();

					int tmp = engineState.spawnEntitySet(
						new TurretSet());
					g.playerMoney.decrease(250);
					engineState
						.unsafeGetComponentAt(
							WorldAttributes.class,
							tmp)
						.setOriginCoord(playerPosition.pureSubtract(
							engineState
								.unsafeGetComponentAt(
									PhysicsPCollisionBody
										.class
									,
									tmp)
								.getDisplacement()));


					// make the turret face in a random
					// direction
					engineState
						.unsafeGetComponentAt(
							HasAnimation.class, tmp)
						.setAnimation(
							engineState
								.unsafeGetComponentAt(
									AnimationWindowAssets
										.class
									,
									tmp)
								.getAnimation(
									CardinalDirections
										.getRandomCardinalDirection(),
									GameConfig
										.ATTACK_ANIMATION));


					System.out.println(
						"Built a tower. It cost $"
						+ GameConfig.TOWER_BUILD_COST);
					// reset the lastCooldown key to the max
					// cooldown of that key
					updateDtForKey(
						g, GameConfig.BUILD_TOWER,
						-PlayGame.coolDownMax.get(
							GameConfig
								.BUILD_TOWER));
					// lastCoolDown.set(GameConfig.BUILD_TOWER,
					//-coolDownMax.get(
					// GameConfig.BUILD_TOWER));
				} else if (g.playerMoney.get()
					   < GameConfig.TOWER_BUILD_COST)
					System.out.print(
						"Not enough cash to build a turret\nYou need at least $"
						+ GameConfig.TOWER_BUILD_COST
						+ "\n");
			}

			if (inputPoller.isKeyDown(GameConfig.BUILD_TRAP)) {
				System.out.println();
				if (Math.abs(g.lastCoolDown.get(
					    GameConfig.BUILD_TRAP))
					    == 0d
				    && g.playerMoney.get()
					       >= GameConfig.TRAP_COST) {
					System.out.print(
						"e key is down. Should spawn trap at player location\n");

					engineState
						.unsafeGetComponentAt(
							Money.class, g.player)
						.decrease(GameConfig.TRAP_COST);

					new SpawnTrapEvent(
						g,
						engineState
							.unsafeGetComponentAt(
								WorldAttributes
									.class,
								g.player)
							.getOriginCoord())
						.f();


					updateDtForKey(
						g, GameConfig.BUILD_TRAP,
						-PlayGame.coolDownMax.get(
							GameConfig.BUILD_TRAP));
				}
				// TODO: get tile player is stood on
				// TODO: highlight that tile?
				// TODO: spawn new trap entity on tile
			}

			// buy ammo
			if (inputPoller.isKeyDown(GameConfig.BUY_AMMO)) {
				// TODO: cooldown for key press
				if (Math.abs(g.lastCoolDown.get(
					    GameConfig.BUY_AMMO))
					    == 0d
				    && g.playerMoney.get()
					       >= GameConfig.PURCHASE_AMMOPACK_AMOUNT
							  * GameConfig
								    .BULLET_COST) {
					g.playerAmmo.increaseAmmo(
						GameConfig
							.PURCHASE_AMMOPACK_AMOUNT);
					g.playerMoney.decrease(
						GameConfig
							.PURCHASE_AMMOPACK_AMOUNT
						* GameConfig.BULLET_COST);
					updateDtForKey(
						g, GameConfig.BUY_AMMO,
						-PlayGame.coolDownMax.get(
							GameConfig.BUY_AMMO));
					System.out.println("Bought some ammo");
				} else
					System.out.println(
						"Either is on cooldown or not enough money to buy more ammo");
			}

			// buy ammo
			if (inputPoller.isKeyDown(GameConfig.PAUSE_GAME)) {
				// TODO: cooldown for key press
				if (Math.abs(g.lastCoolDown.get(
					    GameConfig.PAUSE_GAME))
				    == 0d) {

					PlayGameEvent pauseEvent =
						new PauseGameEvent(g);

					updateDtForKey(
						g, GameConfig.PAUSE_GAME,
						-PlayGame.coolDownMax.get(
							GameConfig.PAUSE_GAME));

					pauseEvent.f();

					updateDtForKey(
						g, GameConfig.PAUSE_GAME,
						-PlayGame.coolDownMax.get(
							GameConfig.PAUSE_GAME));
				}
			}

			///// Quit Game /////
			if (inputPoller.isKeyDown(GameConfig.QUIT_KEY)) {
				g.clearWorld();
				g.quit();
			}
		}
	}


	protected static double EPSILON = 0.0001d;
}
