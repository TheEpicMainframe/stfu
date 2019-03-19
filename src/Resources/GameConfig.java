package Resources;

import java.awt.event.KeyEvent;
import java.awt.*;
import java.util.ArrayList;

import Components.*;

import poj.Pair;
import poj.linear.Vector2f;


// IMPORTANT: Everything in this document should be in screen coordinates. That
// is, the height and width of things are expressed as ratios of the tile size.
public class GameConfig
{

	//////////////////////////
	////// player config /////
	//////////////////////////
	public static final float PLAYER_SPEED = 0.003f;
	public static final float PLAYER_WIDTH =
		GameResources.PLAYER_SPRITE_WIDTH
		/ GameResources.TILE_SCREEN_WIDTH;
	// public static final int PLAYER_HP = 100;
	public static final int PLAYER_HP = Integer.MAX_VALUE;
	public static final int PLAYER_DIFFUSION_VALUE = (int)Math.pow(2, 12);
	public static final int TOWER_DIFFUSION_VALUE = (int)Math.pow(2, 5);
	public static final float PLAYER_HEIGHT =
		GameResources.PLAYER_SPRITE_HEIGHT
		/ GameResources.TILE_SCREEN_HEIGHT;
	public static final Vector2f PLAYER_SPAWNNING_POS =
		new Vector2f(3f, 3f);
	public static final PCollisionBody PLAYER_COLLISION_BODY =
		new PCollisionBody(
			// new Vector2f(0f, 0f),
			new Vector2f(0f, 0.7f),     // displacement
			new Vector2f(0.25f, 0.25f), // center
			// collision body:
			new Vector2f(0.125f, 0.5f), new Vector2f(0.375f, 0.5f),
			new Vector2f(0, 0.375f), new Vector2f(0.5f, 0.375f),
			new Vector2f(0, 0.125f), new Vector2f(0.125f, 0),
			new Vector2f(0.375f, 0), new Vector2f(0.5f, 0.125f));

	// hitbox by inspection
	public static final PCollisionBody PLAYER_HITBOX_BODY =
		new PCollisionBody(
			new Vector2f(-0.5f, 0f),   // displacement
			new Vector2f(1.5f, 0.75f), // center
						   // collision body:
			new Vector2f(1, 0.5f), new Vector2f(1.25f, 0.2f),
			new Vector2f(1.7f, 1.25f), new Vector2f(1.95f, 1f));

	public static final AttackCycle PLAYER_ATTACK_CYCLE =
		new AttackCycle(45, 45);

	public static final int PLAYER_STARTING_CASH = Integer.MAX_VALUE;

	/////////////////////////
	///// turret config /////
	/////////////////////////
	public static final int TOWER_BUILD_COST = 250;
	public static final PCollisionBody TURRET_COLLISION_BODY =
		new PCollisionBody(
			new Vector2f(-0.25f, 1f), // displacement
			new Vector2f(0.5f, 0.5f), // center
						  // collision body:
			new Vector2f(0.25f, 1), new Vector2f(0.75f, 1),
			new Vector2f(0, 0.75f), new Vector2f(1, 0.75f),
			new Vector2f(0, 0.25f), new Vector2f(0.25f, 0),
			new Vector2f(0.75f, 0), new Vector2f(1, 0.25f));
	public static final int CONSTRUCT_HP = 100;
	public static final float SHELL_SPEED = 0.04f;
	public static final int SHELL_DAMAGE = 100;
	public static final PCollisionBody TURRET_HITBOX_BODY =
		TURRET_COLLISION_BODY;

	public static final AttackCycle TURRET_ATTACK_CYCLE =
		new AttackCycle(1000, 1000);

	//////////////////////////
	////// bullet config /////
	//////////////////////////
	public static final float BULLET_SPEED = 0.02f;
	public static final float BULLET_WIDTH =
		GameResources.BULLET_SPRITE_WIDTH
		/ GameResources.TILE_SCREEN_WIDTH;
	public static final float BULLET_HEIGHT =
		GameResources.BULLET_SPRITE_HEIGHT
		/ GameResources.TILE_SCREEN_HEIGHT;
	public static final double BULLET_LIFE_SPAN = 0.8;
	public static final int BULLET_DAMAGE = 30;

	public static final PCollisionBody BULLET_COLLISION_BODY =
		new PCollisionBody(
			new Vector2f(0.0f, 0.0f), // displacement
			new Vector2f(BULLET_WIDTH / 2,
				     BULLET_HEIGHT / 2), // center
							 // collision body:
			new Vector2f(0, 0), new Vector2f(0, BULLET_HEIGHT),
			new Vector2f(BULLET_WIDTH, 0),
			new Vector2f(BULLET_WIDTH, BULLET_HEIGHT));

	///////////////////////
	////// mob config /////
	///////////////////////
	public static final float MOB_SPEED = 0.6f * PLAYER_SPEED;
	public static final float MOB_HEIGHT =
		GameResources.ENEMY_SPRITE_HEIGHT
		/ GameResources.TILE_SCREEN_HEIGHT;
	public static final float MOB_WIDTH = GameResources.ENEMY_SPRITE_WIDTH
					      / GameResources.TILE_SCREEN_WIDTH;
	public static final int MOB_ATTACK_DAMAGE = 1;

	public static final int MOB_HP = 100;

	public static final AttackCycle MOB_ATTACK_CYCLE =
		new AttackCycle(100, 100);
	public static final PCollisionBody ENEMY_HITBOX_BODY =
		PLAYER_HITBOX_BODY;

	public static final PCollisionBody MOB_COLLISION_BODY =
		// clang-format off
		new PCollisionBody(
			new Vector2f(0f , 0.7f), // displacement
			new Vector2f(0.25f ,0.25f), // center
						  // collision body:
			new Vector2f(0.25f/2f,   1    /2f), new Vector2f(0.75f /2f, 1    /2f),
			new Vector2f(0    /2f,   0.75f/2f), new Vector2f(1     /2f, 0.75f/2f),
			new Vector2f(0    /2f, 0.25f  /2f), new Vector2f(0.25f /2f, 0    /2f),
			new Vector2f(0.75f/2f, 0      /2f), new Vector2f(1     /2f, 0.25f/2f));
	// clang-format on

	public static final PCollisionBody MOB_MELEE_ATTACK_BODY =
		new PCollisionBody(new Vector2f(-0.5f, 0),   // displacement
				   new Vector2f(0.5f, 0.5f), // center
							     // collision body:
				   new Vector2f(0, 0), new Vector2f(2, 0),
				   new Vector2f(0, 2), new Vector2f(2, 2));

	public static final AggroRange MOB_AGGRO_RANGE =
		new AggroRange(new Vector2f(-0.4f, 0f),  // displacement
			       new Vector2f(0.5f, 0.5f), // center
							 // collision body:
			       new Vector2f(0f, 0f), new Vector2f(1f, 1f),
			       new Vector2f(1f, 0f), new Vector2f(0f, 1f));

	public static final float MOB_SPAWN_TIMER = 10.0f;
	public static final double MOB_DESPAWN_TIMER = 5000d; // in ms

	// spawn points:
	public static final Vector2f MOB_SPAWNER_0 = new Vector2f(14f, 7f);
	public static final Vector2f MOB_SPAWNER_1 = new Vector2f(20f, 20f);


	///////////////////////
	///// menu config /////
	///////////////////////

	// clang-format off
	public static final PCollisionBody PLAY_BUTTON_COLLISION_BODY =
		new PCollisionBody(
			new Vector2f(0f, 0f), // displacement
			new Vector2f((float)(1024 / 2 - 70), (float)(768 / 2 + 90)), 
			new Vector2f((float)(1024 / 2 - 70), (float)(768 / 2 + 90)), 
			new Vector2f((float)(1024 / 2 - 70+ GameResources.playButtonWidth),(float)(768 / 2 + 90) ), 
			new Vector2f((float)(1024 / 2 - 70) ,(float) (768 / 2 + 90+ GameResources.playButtonHeight)), 
			new Vector2f((float)(1024 / 2 - 70 +GameResources.playButtonWidth),(float)(768 / 2 + 90 + GameResources.playButtonHeight)));

	public static final PCollisionBody HOW_TO_PLAY_BUTTON_COLLISION_BODY =
		new PCollisionBody(
			new Vector2f(0f, 0f), // displacement
			new Vector2f((float)(1024 / 2 - 70), (float)(768 / 2 -90)), 
			new Vector2f((float)(1024 / 2 - 70), (float)(768 / 2 -90)), 
			new Vector2f((float)(1024 / 2 - 70+ GameResources.howToPlayButtonWidth),(float)(768 / 2 -90) ), 
			new Vector2f((float)(1024 / 2 - 70) ,(float) (768 / 2 -90+ GameResources.howToPlayButtonHeight)), 
			new Vector2f((float)(1024 / 2 - 70 +GameResources.howToPlayButtonWidth),(float)(768 / 2 -90 + GameResources.howToPlayButtonHeight)));


	public static final PCollisionBody EXIT_BUTTON_COLLISION_BODY =
		new PCollisionBody(
			new Vector2f(0f, 0f), // displacement
			new Vector2f((float)(1024 / 2 - 70), (float)(768 / 2 +290)), 
			new Vector2f((float)(1024 / 2 - 70), (float)(768 / 2 +290)), 
			new Vector2f((float)(1024 / 2 - 70+ GameResources.exitButtonWidth),(float)(768 / 2 +290) ), 
			new Vector2f((float)(1024 / 2 - 70) ,(float) (768 / 2 +290+ GameResources.exitButtonHeight)), 
			new Vector2f((float)(1024 / 2 - 70 +GameResources.exitButtonWidth),(float)(768 / 2 +290 + GameResources.exitButtonHeight)));

	public static final PCollisionBody BACK_BUTTON_COLLISION_BODY =
		new PCollisionBody(
			new Vector2f(0f, 0f), // displacement
			new Vector2f((float)(1024 / 2 - 70), (float)(768 / 2 +190)), 
			new Vector2f((float)(1024 / 2 - 70), (float)(768 / 2 +190)), 
			new Vector2f((float)(1024 / 2 - 70+ GameResources.backButtonWidth),(float)(768 / 2 +190) ), 
			new Vector2f((float)(1024 / 2 - 70) ,(float) (768 / 2 +190+ GameResources.backButtonHeight)), 
			new Vector2f((float)(1024 / 2 - 70 +GameResources.backButtonWidth),(float)(768 / 2 +190 + GameResources.backButtonHeight)));
	// clang-format on

	////////////////////////////
	///// construct config /////
	////////////////////////////
	public static final float CONSTRUCT_HEIGHT = 64;
	public static final float CONSTRUCT_WIDTH = 48;

	/////////////////////////
	///// pickup config /////
	/////////////////////////
	public static final float PICKUP_WIDTH =
		GameResources.CASH_SPRITE_WIDTH
		/ GameResources.TILE_SCREEN_WIDTH;
	public static final float PICKUP_HEIGHT =
		GameResources.CASH_SPRITE_HEIGHT
		/ GameResources.TILE_SCREEN_HEIGHT;
	public static final PCollisionBody PICKUP_COLLISION_BODY =
		new PCollisionBody(
			new Vector2f(0.0f, 0.0f), // displacement
			new Vector2f(PICKUP_WIDTH / 2,
				     PICKUP_HEIGHT / 2), // center
							 // collision body:
			new Vector2f(0, 0), new Vector2f(0, PICKUP_HEIGHT),
			new Vector2f(PICKUP_WIDTH, 0),
			new Vector2f(PICKUP_WIDTH, PICKUP_HEIGHT));

	// money:
	public static final int PICKUP_CASH_AMOUNT = 100;
	public static final float PICKUP_CASH_SPAWN_TIME = 20.0f;
	// power-up:
	public static final double PICKUP_POWERUP_AMOUNT = 0.05;
	public static final float PICKUP_POWERUP_SPAWN_TIME = 20.0f;
	// health:
	// TODO: health pick-up
	// ammo:
	// TODO: ammo pick-up

	////////////////////////
	///// input config /////
	////////////////////////
	public static final int SWITCH_WEAPONS = KeyEvent.VK_X;
	public static final int ATTACK_KEY = KeyEvent.VK_SPACE;
	public static final int BUILD_TOWER = KeyEvent.VK_Q;
	public static final int BUILD_TRAP = KeyEvent.VK_E;

	// key, cooldown (ms)
	public static final ArrayList<Pair<Integer, Double>> COOL_DOWN_KEYS =
		new ArrayList<Pair<Integer, Double>>() {
			{
				add(new Pair<Integer, Double>(ATTACK_KEY,
							      0.3d));
				add(new Pair<Integer, Double>(BUILD_TOWER, 1d));
				add(new Pair<Integer, Double>(SWITCH_WEAPONS,
							      1d));
				add(new Pair<Integer, Double>(BUILD_TRAP, 1d));
			}
		};

	public static final Color APP_CLEAR_COLOR = new Color(131, 173, 239);
}
