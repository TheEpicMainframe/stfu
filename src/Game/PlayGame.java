package Game;

import Resources.*;
import Components.*;

import poj.Component.Components;
import poj.linear.Vector2f;
import poj.Render.*;
import poj.Render.MinYFirstSortedRenderObjectBuffer;

import Game.Camera;
import EntitySets.*;
import TileMap.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Collections;

import java.awt.*;

public class PlayGame extends World
{
	// Render
	private Map map;

	// buffers for the renderer
	private Queue<RenderObject> groundBuffer;
	private MinYFirstSortedRenderObjectBuffer entityBuffer;
	private Queue<RenderObject> guiBuffer;
	private Queue<RenderObject> debugBuffer;

	// private MapLayer mapLayer;
	// Camera
	private Camera cam;    // camera
	private Camera invCam; // inverse camera


	private static ArrayList<Double> coolDownMax = new ArrayList<Double>(
		Collections.nCopies(poj.GameWindow.InputPoller.MAX_KEY, 0d));
	private ArrayList<Double> lastCoolDown = new ArrayList<Double>(
		Collections.nCopies(poj.GameWindow.InputPoller.MAX_KEY, 0d));
	// Higher level game logic
	private int player;
	// private int mob1;
	public static double EPSILON = 0.0001d;
	private Vector2f unitVecPlayerPosToMouseDelta;
	private CardinalDirections prevDirection = CardinalDirections.N;

	// ASE
	private double timeOfLastMobSpawn = 0.0;
	private double timeOfLastCashSpawn =
		0.0 - GameConfig.PICKUP_CASH_SPAWN_TIME;
	private int cash = 1000;

	private StringRenderObject gameTimer =
		new StringRenderObject("", 5, 10, Color.WHITE);
	private StringRenderObject cashDisplay = new StringRenderObject(
		"Your Cash: " + this.cash, 5, 20, Color.WHITE);
	// /ASE

	public PlayGame()
	{
		super();

		// World loading
		this.map = new Map(3);
		this.map.addTileSet(GameResources.tileSet);
		this.map.addMapConfig(GameResources.demo1Config);
		this.map.addMapLayer(GameResources.demo1LayerGround);
		this.map.addMapLayer(GameResources.demo1LayerWall);
		// this.map.addMapConfig(GameResources.pathFindTest3Config);
		// this.map.addMapLayer(GameResources.pathFindTest1Layer);


		// setting the build turret coolDown
		for (int i = 0; i < GameConfig.COOL_DOWN_KEYS.size(); ++i) {
			coolDownMax.set(GameConfig.COOL_DOWN_KEYS.get(i).fst,
					GameConfig.COOL_DOWN_KEYS.get(i).snd);
		}
		// lastCoolDown.set(GameConfig.BUILD_TOWER, 0d);
		// this.map.addMapConfig(GameResources.pathFindTest1Config);
		// this.map.addMapLayer(GameResources.pathFindTest1Layer);

		// this.map.addMapConfig(GameResources.renderPerformanceConf);
		// this.map.addMapLayer(GameResources.renderPerformanceLayer);

		// mapLayer = this.map.getLayerEngineState(0);

		// this.map.addMapLayer(GameResources.mapLayer1);
		// this.map.addMapLayer(GameResources.mapLayer1);
		// this.map.addMapLayer(GameResources.mapLayer2);

		this.cam = new Camera();
		this.unitVecPlayerPosToMouseDelta = new Vector2f();

		this.groundBuffer = new LinkedList<RenderObject>();
		this.entityBuffer = new MinYFirstSortedRenderObjectBuffer();
		this.guiBuffer = new LinkedList<RenderObject>();
		this.debugBuffer = new LinkedList<RenderObject>();

		// camera initialization
		resetCamera();

		this.invCam = new Camera();
		this.updateInverseCamera();
	}

	public void registerComponents()
	{
		super.engineState.registerComponent(HasAnimation.class);
		super.engineState.registerComponent(Render.class);
		super.engineState.registerComponent(WorldAttributes.class);
		super.engineState.registerComponent(MovementDirection.class);
		super.engineState.registerComponent(FacingDirection.class);
		super.engineState.registerComponent(Movement.class);
		super.engineState.registerComponent(CollisionAabbBodies.class);
		super.engineState.registerComponent(AabbCollisionBody.class);
		super.engineState.registerComponent(CircleCollisionBody.class);
		super.engineState.registerComponent(Lifespan.class);
	}
	public void registerEntitySets()
	{
		super.engineState.registerSet(PlayerSet.class);
		super.engineState.registerSet(MobSet.class);
		super.engineState.registerSet(ConstructSet.class);
		super.engineState.registerSet(Bullet.class);
		super.engineState.registerSet(TurretSet.class);
		super.engineState.registerSet(CollectibleSet.class);
	}

	// higher game logic functions
	public void spawnWorld()

	{
		// Player
		this.player = super.engineState.spawnEntitySet(new PlayerSet());
		for (int i = 0; i < 1; ++i) {
			super.engineState.spawnEntitySet(new MobSet());
		}

		EngineTransforms.addPlayerDiffusionValAtPlayerPos(
			this.engineState, this.map, 0, this.player);
		// TODO: HAIYANG get the layer number for the path finding!
		// right now for testing it only have 1 layer

		clearTime();
	}
	public void clearWorld()
	{
	}

	// use super.acct for the accumulated time, use this.dt for the time
	// step. Time is all in milliseconds
	public void runGame()
	{
		this.processInputs();

		// ASE
		this.mobSpawner();

		this.cashDropDespawner();
		this.bulletDespawner();

		// TODO: make mobs drop cash on death?
		this.cashSpawner(true, 4f, 7f);
		this.collectCash(GameConfig.PICKUP_CASH_AMOUNT);

		this.updateGameTimer();

		this.updateCashDisplay();

		// /ASE

		// SYSTEMS Go here
		// this.setMovementVelocityFromMovementDirection();
		// this.updateWorldAttribPositionFromMovement(this.dt);

		// will set the enemy direction and speed, then will render them
		// next frame


		// updating positions
		EngineTransforms.setMovementVelocityFromMovementDirection(
			this.engineState);

		EngineTransforms.updateCircleCollisionFromWorldAttributes(
			engineState);

		EngineTransforms.updateAabbCollisionFromWorldAttributes(
			engineState);

		// debug renderers
		EngineTransforms.debugCircleCollisionRender(
			engineState, debugBuffer, this.cam);
		EngineTransforms.debugAabbCollisionRender(
			engineState, debugBuffer, this.cam);
		for (int i = 0; i < this.map.getLayerNumber(); ++i) {
			EngineTransforms.debugMapAabbCollisionRender(
				map, i, debugBuffer, this.cam);
		}

		// collision
		/*
	       EngineTransforms
		       .resolveCircleCollisionBodyWithAabbCollisionBody(
			       engineState, PlayerSet.class, MobSet.class,
			       this.dt);*/
		EngineTransforms.areCirclesCollidingAgainstAabb(
			engineState, PlayerSet.class, MobSet.class);


		// changing world attrib position
		EngineTransforms.updateWorldAttribPositionFromMovement(
			this.engineState, this.dt);
		EngineTransforms.generateDiffusionMap(this.map, 0, 1f / 8f);

		for (int i = this.engineState.getInitialSetIndex(MobSet.class);
		     this.engineState.isValidEntity(i);
		     i = this.engineState.getNextSetIndex(MobSet.class, i)) {

			EngineTransforms.updateEnemyPositionFromPlayer(
				this.engineState, this.map, 0, this.player, i);
		}

		// this.generateDiffusionMap(0, 1f / 8f);
		// this.updateEnemyPositionFromPlayer();

		// updating the camera
		centerCamerasPositionToPlayer();
		updateInverseCamera();
		updateCoolDownKeys();

		EngineTransforms.updateAnimationWindows(this.engineState,
							this.dt);
		EngineTransforms.cropSpriteSheetsFromAnimationWindows(
			this.engineState);

		EngineTransforms
			.updateRenderScreenCoordinatesFromWorldCoordinatesWithCamera(
				this.engineState, this.cam);

		System.out.println("----------------------- end one loop");
		// rendering is run after this is run
	}


	protected void processInputs()
	{

		////// Movement Commands //////
		if (super.inputPoller.isKeyDown(KeyEvent.VK_W)
		    && super.inputPoller.isKeyDown(KeyEvent.VK_D)) {
			System.out.println("wd key is down");
			super.getComponentAt(MovementDirection.class,
					     this.player)
				.setDirection(CardinalDirections.NW);
			super.getComponentAt(Movement.class, this.player)
				.setSpeed(GameConfig.PLAYER_SPEED);
			prevDirection = CardinalDirections.NW;
			super.getComponentAt(HasAnimation.class, this.player)
				.setAnimation(
					GameResources.playerNMoveAnimation);
		} else if (super.inputPoller.isKeyDown(KeyEvent.VK_W)
			   && super.inputPoller.isKeyDown(KeyEvent.VK_A)) {
			System.out.println("wa key is down");
			super.getComponentAt(MovementDirection.class,
					     this.player)
				.setDirection(CardinalDirections.SW);
			super.getComponentAt(Movement.class, this.player)
				.setSpeed(GameConfig.PLAYER_SPEED);
			prevDirection = CardinalDirections.SW;
			super.getComponentAt(HasAnimation.class, this.player)
				.setAnimation(
					GameResources.playerNMoveAnimation);
		} else if (super.inputPoller.isKeyDown(KeyEvent.VK_S)
			   && super.inputPoller.isKeyDown(KeyEvent.VK_A)) {
			System.out.println("sa key is down");
			super.getComponentAt(MovementDirection.class,
					     this.player)
				.setDirection(CardinalDirections.SE);
			prevDirection = CardinalDirections.SE;
			super.getComponentAt(Movement.class, this.player)
				.setSpeed(GameConfig.PLAYER_SPEED);
			super.getComponentAt(HasAnimation.class, this.player)
				.setAnimation(
					GameResources.playerSMoveAnimation);
		} else if (super.inputPoller.isKeyDown(KeyEvent.VK_S)
			   && super.inputPoller.isKeyDown(KeyEvent.VK_D)) {

			System.out.println("sd key is down");
			super.getComponentAt(MovementDirection.class,
					     this.player)
				.setDirection(CardinalDirections.NE);
			super.getComponentAt(Movement.class, this.player)
				.setSpeed(GameConfig.PLAYER_SPEED);
			prevDirection = CardinalDirections.NE;
			super.getComponentAt(HasAnimation.class, this.player)
				.setAnimation(
					GameResources.playerSMoveAnimation);

		} else if (super.inputPoller.isKeyDown(
				   KeyEvent.VK_W)) { // single Key movements
			System.out.println("w key is down");
			super.getComponentAt(MovementDirection.class,
					     this.player)
				.setDirection(CardinalDirections.W);
			super.getComponentAt(Movement.class, this.player)
				.setSpeed(GameConfig.PLAYER_SPEED);
			prevDirection = CardinalDirections.W;
			super.getComponentAt(HasAnimation.class, this.player)
				.setAnimation(
					GameResources.playerWMoveAnimation);
		} else if (super.inputPoller.isKeyDown(KeyEvent.VK_D)) {
			System.out.println("d key is down");
			super.getComponentAt(MovementDirection.class,
					     this.player)
				.setDirection(CardinalDirections.N);
			super.getComponentAt(Movement.class, this.player)
				.setSpeed(GameConfig.PLAYER_SPEED);
			prevDirection = CardinalDirections.N;
			super.getComponentAt(HasAnimation.class, this.player)
				.setAnimation(
					GameResources.playerNMoveAnimation);
		} else if (super.inputPoller.isKeyDown(KeyEvent.VK_A)) {
			System.out.println("a key is down");
			super.getComponentAt(MovementDirection.class,
					     this.player)
				.setDirection(CardinalDirections.S);
			super.getComponentAt(Movement.class, this.player)
				.setSpeed(GameConfig.PLAYER_SPEED);
			prevDirection = CardinalDirections.S;
			super.getComponentAt(HasAnimation.class, this.player)
				.setAnimation(
					GameResources.playerSMoveAnimation);
		} else if (super.inputPoller.isKeyDown(KeyEvent.VK_S)) {
			System.out.println("s key is down");
			super.getComponentAt(MovementDirection.class,
					     this.player)
				.setDirection(CardinalDirections.E);
			super.getComponentAt(Movement.class, this.player)
				.setSpeed(GameConfig.PLAYER_SPEED);
			prevDirection = CardinalDirections.E;
			super.getComponentAt(HasAnimation.class, this.player)
				.setAnimation(
					GameResources.playerEMoveAnimation);
		} else // no movement key is pressed
		{
			super.getComponentAt(Movement.class, this.player)
				.setSpeed(0);
			// TODO idle direction!!!!!
			super.getComponentAt(FacingDirection.class,
					     this.player);
			switch (prevDirection) {
			case N:
				super.getComponentAt(HasAnimation.class,
						     this.player)
					.setAnimation(
						GameResources
							.playerNIdleAnimation);
				break;
			case NE:
				super.getComponentAt(HasAnimation.class,
						     this.player)
					.setAnimation(
						GameResources
							.playerNIdleAnimation);
				break;
			case NW:
				super.getComponentAt(HasAnimation.class,
						     this.player)
					.setAnimation(
						GameResources
							.playerNIdleAnimation);
				break;
			case S:
				super.getComponentAt(HasAnimation.class,
						     this.player)
					.setAnimation(
						GameResources
							.playerSIdleAnimation);
				break;
			case SE:
				super.getComponentAt(HasAnimation.class,
						     this.player)
					.setAnimation(
						GameResources
							.playerSIdleAnimation);
				break;
			case SW:
				super.getComponentAt(HasAnimation.class,
						     this.player)
					.setAnimation(
						GameResources
							.playerSIdleAnimation);
				break;
			case W:
				super.getComponentAt(HasAnimation.class,
						     this.player)
					.setAnimation(
						GameResources
							.playerWIdleAnimation);
				break;
			case E:
				super.getComponentAt(HasAnimation.class,
						     this.player)
					.setAnimation(
						GameResources
							.playerEIdleAnimation);
				break;
			}
		}


		////// Build Commands //////
		if (super.inputPoller.isKeyDown(GameConfig.BUILD_TOWER)) {

			System.out.println("will build turret!!!!");
			System.out.println(
				" last cooldown = "
				+ lastCoolDown.get(GameConfig.BUILD_TOWER));
			if (Math.abs(lastCoolDown.get(GameConfig.BUILD_TOWER))
				    == 0d
			    && this.cash >= GameConfig.TOWER_BUILD_COST) {
				Vector2f playerPosition =
					super.getComponentAt(
						     WorldAttributes.class,
						     this.player)
						.getOriginCoord();

				int tmp = super.engineState.spawnEntitySet(
					new TurretSet());
				this.cash -= 250;
				super.getComponentAt(WorldAttributes.class, tmp)
					.setOriginCoord(playerPosition);

				System.out.println(
					"Built a tower. It cost $"
					+ GameConfig.TOWER_BUILD_COST);
				// reset the lastCooldown key to the max
				// cooldown of that key
				updateDtForKey(GameConfig.BUILD_TOWER,
					       -coolDownMax.get(
						       GameConfig.BUILD_TOWER));
				// lastCoolDown.set(GameConfig.BUILD_TOWER,
				//-coolDownMax.get(
				// GameConfig.BUILD_TOWER));
			} else if (this.cash < GameConfig.TOWER_BUILD_COST)
				System.out.print(
					"Not enough cash to build a turret\nYou need at least $"
					+ GameConfig.TOWER_BUILD_COST + "\n");
		}
		if (super.inputPoller.isKeyDown(GameConfig.BUILD_TRAP)) {
			System.out.print(
				"e key is down. Should spawn trap at player location\n");
			// TODO: get tile player is stood on
			// TODO: highlight that tile?
			// TODO: spawn new trap entity on tile
			// TODO: make trap spawn on key up? (to prevent
			// constant spawning if key down for more than 1
			// frame)
		}

		////// Combat Commands //////
		if (super.inputPoller.isKeyDown(GameConfig.ATTACK_KEY)
		    || super.inputPoller.isLeftMouseButtonDown()) {
			System.out.print(
				"space key is down. Player character should be attacking\n");

			System.out.println("the cd value of attack key = "
					   + Math.abs(lastCoolDown.get(
						     GameConfig.ATTACK_KEY)));
			if (Math.abs(lastCoolDown.get(GameConfig.ATTACK_KEY))
			    == 0d) {
				updateDtForKey(GameConfig.ATTACK_KEY,
					       -coolDownMax.get(
						       GameConfig.ATTACK_KEY));
				this.playerShootBullet();
				// lastCoolDown.set(GameConfig.BUILD_TOWER,
				//-coolDownMax.get(
				// GameConfig.BUILD_TOWER));
			}

			// TODO: find adjacent tiles (and any enemies on
			// them)
			// TODO: apply damage to enemies
			// TODO: attack on mouse click instead?
		}
		if (super.inputPoller.isKeyDown(GameConfig.SWITCH_WEAPONS)) {
			System.out.print(
				"x key is down. Player character should be changing weapons\n");
			// TODO: implement different weapons
			// TODO: switch between weapons
			// player.switchWeapon();
		}

		////// Mouse handling  //////
		Vector2f playerPosition =
			super.getComponentAt(WorldAttributes.class, this.player)
				.getCenteredBottomQuarter();

		Vector2f mousePosition = super.inputPoller.getMousePosition();
		mousePosition.matrixMultiply(this.invCam);

		mousePosition.log("Mouse position in world coordinates");

		Vector2f tmp = playerPosition.pureSubtract(mousePosition);
		tmp.negate();
		CardinalDirections facingDirection =
			CardinalDirections
				.getClosestDirectionFromDirectionVector(tmp);

		super.getComponentAt(FacingDirection.class, player)
			.setDirection(facingDirection);

		this.unitVecPlayerPosToMouseDelta = tmp.pureNormalize();

		System.out.println("PLAYER INFROMATION: ");
		super.getComponentAt(FacingDirection.class, player).print();
		super.getComponentAt(WorldAttributes.class, this.player)
			.print();
	}

	// Render function
	protected void render()
	{

		pushTileMapLayerToQueue(map.getLayerEngineState(0),
					groundBuffer);

		pushTileMapLayerToQueue(map.getLayerEngineState(1),
					entityBuffer);

		for (Render r :
		     super.getRawComponentArrayListPackedData(Render.class)) {
			Systems.cullPushRenderComponentToQueue(
				r, entityBuffer, this.windowWidth,
				this.windowHeight);
		}

		guiBuffer.add(this.gameTimer);
		guiBuffer.add(this.cashDisplay);

		super.renderer.renderBuffers(groundBuffer, entityBuffer,
					     debugBuffer, guiBuffer);

		// TODO FIX THE BADdebug render
		// super.renderer.render();
	}

	protected void pushTileMapLayerToQueue(MapLayer n,
					       Queue<RenderObject> q)
	{
		EngineTransforms.pushTileMapLayerToQueue(
			this.map, n, windowWidth, this.windowHeight,
			(int)GameResources.TILE_SCREEN_WIDTH,
			(int)GameResources.TILE_SCREEN_HEIGHT, this.cam,
			this.invCam, q);
	}


	private void updateInverseCamera()
	{
		if (this.cam.isInvertible()) {
			this.invCam =
				new Camera((this.cam.unsafePureInverse()));
		}
	}

	private void resetCamera()
	{
		this.cam.clearBackToIdentity();
		this.cam.setScalingForVector2(-GameResources.TILE_SCREEN_WIDTH,
					      GameResources.TILE_SCREEN_HEIGHT);
		this.cam.composeWithRotationForVector2XaxisCC(
			GameResources.TILE_SCREEN_ROTATION);
		this.cam.composeSetScalingForVector2(
			GameResources.MAGIC_CONSTANT,
			GameResources.MAGIC_CONSTANT);
		/*
		this.cam.setScalingForVector2(-GameResources.TILE_SCREEN_WIDTH,
					      GameResources.TILE_SCREEN_HEIGHT);
		this.cam.composeWithRotationForVector2XaxisCC(
			GameResources.TILE_SCREEN_ROTATION);*/
	}

	private void centerCamerasPositionsToWorldAttribute(WorldAttributes n)
	{
		this.resetCamera();
		Vector2f tmp = n.getOriginCoord();
		tmp.matrixMultiply(this.cam);

		this.cam.setTranslationForVector2(
			-tmp.x + super.windowWidth / 2f,
			-tmp.y + super.windowHeight / 2f);
	}

	private void playerShootBullet()
	{
		int e = super.engineState.spawnEntitySet(
			new Bullet(this.getPlayTime()));
		float bulletSpeed =
			super.getComponentAt(Movement.class, e).getSpeed();
		Vector2f tmp = new Vector2f(
			super.getComponentAt(WorldAttributes.class, this.player)
				.getOriginCoord());

		super.getComponentAt(WorldAttributes.class, e)
			.setOriginCoord(tmp);

		super.getComponentAt(Movement.class, e)
			.setVelocity(this.unitVecPlayerPosToMouseDelta.pureMul(
				bulletSpeed));
	}


	private void centerCamerasPositionToPlayer()
	{
		this.centerCamerasPositionsToWorldAttribute(
			engineState.getComponentAt(WorldAttributes.class,
						   this.player));
	}
	private void updateCoolDownKeys()
	{
		for (int i = 0; i < Resources.GameConfig.COOL_DOWN_KEYS.size();
		     ++i) {
			updateDtForKey(
				Resources.GameConfig.COOL_DOWN_KEYS.get(i).fst,
				this.dt / 1000);
		}
	}
	private void updateDtForKey(int keyIndex, double val)
	{
		// if the key cooldown is not 0.. i put a if statement here
		// because i don't want to subtract it to neg infinity..
		if (lastCoolDown.get(keyIndex) - val > EPSILON) {
			lastCoolDown.set(keyIndex,
					 lastCoolDown.get(keyIndex) - val);
		} else {
			lastCoolDown.set(keyIndex, 0d);
		}
	}

	// ASE
	/** @return: current time the game has been running in seconds */
	private double getPlayTime()
	{
		double playTime = Math.floor((super.acct / 1000) * 100) / 100;
		return playTime;
	}

	/** updates the gameTimer string with the current play time */
	private void updateGameTimer()
	{
		this.gameTimer.setStr("" + getPlayTime());
	}

	/**  updates the cashDisplay string with the players current cash */
	private void updateCashDisplay()
	{
		this.cashDisplay.setStr("Your Cash: $" + this.cash);
	}

	/** spawns a new mob entity if it has been at least 
	 * MOB_SPAWN_TIMER seconds since the last spawn  */
	private void mobSpawner()
	{
		double currentPlayTime = this.getPlayTime();
		if (currentPlayTime - this.timeOfLastMobSpawn
		    > GameConfig.MOB_SPAWN_TIMER) {
			super.engineState.spawnEntitySet(new MobSet());
			this.timeOfLastMobSpawn = currentPlayTime;
			System.out.println("Spawning new mob at time: "
					   + this.timeOfLastMobSpawn);
		}
	}

	/** spawns a new cash pick-up at a location
	 * @param timed: only spawn if PICKUP_CASH_SPAWN_TIME seconds have passed since last spawn
	 * @param x: x-coordinate to spawn the drop at
	 * @param y: y-coordinate to spawn the drop at
	 *   */
	private void cashSpawner(boolean timed, float x, float y)
	{
		double currentPlayTime = this.getPlayTime();
		if (timed == true
		    && currentPlayTime - this.timeOfLastCashSpawn
			       > GameConfig.PICKUP_CASH_SPAWN_TIME) {
			super.engineState.spawnEntitySet(
				new CollectibleSet(x, y, currentPlayTime));
			this.timeOfLastCashSpawn = currentPlayTime;
			System.out.println("Spawning new timed cash drop.");
		} else if (timed == false) {
			super.engineState.spawnEntitySet(
				new CollectibleSet(x, y, currentPlayTime));
			this.timeOfLastCashSpawn = currentPlayTime;
			System.out.println("Spawning new cash drop.");
		}
	}

	/** deletes cash drops older than the lifespan  
	 * prevents drops that have not been collected from piling up*/
	private void cashDropDespawner()
	{
		for (int i = this.engineState.getInitialSetIndex(
			     CollectibleSet.class);
		     this.engineState.isValidEntity(i);
		     i = this.engineState.getNextSetIndex(CollectibleSet.class,
							  i)) {

			double spawnTime =
				engineState.getComponentAt(Lifespan.class, i)
					.getSpawnTime();
			double lifespan =
				engineState.getComponentAt(Lifespan.class, i)
					.getLifespan();

			if (this.getPlayTime() - spawnTime >= lifespan) {
				this.engineState.deleteComponentAt(
					CollectibleSet.class, i);
				this.engineState.deleteComponentAt(Render.class,
								   i);
				this.engineState.deleteComponentAt(
					WorldAttributes.class, i);
				this.engineState.deleteComponentAt(
					Lifespan.class, i);
			}
		}
	}

	private void bulletDespawner()
	{
		for (int i = this.engineState.getInitialSetIndex(Bullet.class);
		     this.engineState.isValidEntity(i);
		     i = this.engineState.getNextSetIndex(Bullet.class, i)) {

			double spawnTime =
				engineState.getComponentAt(Lifespan.class, i)
					.getSpawnTime();
			double lifespan =
				engineState.getComponentAt(Lifespan.class, i)
					.getLifespan();

			if (this.getPlayTime() - spawnTime >= lifespan) {
				this.engineState.deleteComponentAt(Bullet.class,
								   i);
				this.engineState.deleteComponentAt(Render.class,
								   i);
				this.engineState.deleteComponentAt(
					WorldAttributes.class, i);
				this.engineState.deleteComponentAt(
					Movement.class, i);
				this.engineState.deleteComponentAt(
					Lifespan.class, i);
			}
		}
	}

	private void collectCash(int amount)
	{
		Vector2f playerPosition =
			engineState
				.getComponentAt(WorldAttributes.class,
						this.player)
				.getCenteredBottomQuarter();

		for (int i = this.engineState.getInitialSetIndex(
			     CollectibleSet.class);
		     this.engineState.isValidEntity(i);
		     i = this.engineState.getNextSetIndex(CollectibleSet.class,
							  i)) {

			Vector2f collectiblePosition =
				engineState
					.getComponentAt(WorldAttributes.class,
							i)
					.getCenteredBottomQuarter();

			if ((int)playerPosition.x == (int)collectiblePosition.x
			    && (int)playerPosition.y
				       == (int)collectiblePosition.y) {
				this.cash += amount;
				System.out.println("Picked up $" + amount
						   + ". You now have $"
						   + this.cash);
				this.engineState.deleteComponentAt(
					CollectibleSet.class, i);
				this.engineState.deleteComponentAt(Render.class,
								   i);
				this.engineState.deleteComponentAt(
					WorldAttributes.class, i);
				this.engineState.deleteComponentAt(
					Lifespan.class, i);
			}
		}
	}
	// /ASE
}
