package Resources;

import java.awt.event.KeyEvent;


// IMPORTANT: Everything in this document should be in screen coordinates. That
// is, the height and width of things are expressed as ratios of the tile size.
public class GameConfig
{

	// player config
	// not gonna lie, idk why we really need this, but the math checks out
	// with this
	// TODO someone figure out how this all works so we're no longer just
	// guessing random numbers sto make everything line up. pretty sure this
	// happened just because of coincidence.
	private static final float PLAYER_SCALE = 2f;

	public static final float PLAYER_SPEED = 0.003f;
	public static final float PLAYER_WIDTH =
		GameResources.TILE_SCREEN_WIDTH
		/ GameResources.PLAYER_SPRITE_WIDTH * PLAYER_SCALE;
	public static final float PLAYER_HEIGHT =
		GameResources.TILE_SCREEN_HEIGHT
		/ GameResources.PLAYER_SPRITE_HEIGHT * PLAYER_SCALE;

	// bullet config
	public static final float BULLET_SPEED = 0.02f;
	public static final float BULLET_WIDTH =
		GameResources.BULLET_SPRITE_WIDTH
		/ GameResources.TILE_SCREEN_WIDTH;
	public static final float BULLET_HEIGHT =
		GameResources.BULLET_SPRITE_HEIGHT
		/ GameResources.TILE_SCREEN_HEIGHT;

	// mob config
	public static final float MOB_VELOCITY = 0.9f * PLAYER_SPEED;
	public static final float MOB_HEIGHT = PLAYER_WIDTH;
	public static final float MOB_WIDTH = PLAYER_HEIGHT;
	public static final float MOB_HP = 100;

	// construct config
	public static final float CONSTRUCT_HEIGHT = 64;
	public static final float CONSTRUCT_WIDTH = 48;
	public static final float CONSTRUCT_HP = 100;

	// input config
	public static final int SWITCH_WEAPONS = KeyEvent.VK_X;
	public static final int ATTACK_KEY = KeyEvent.VK_SPACE;
	public static final int BUILD_TOWER = KeyEvent.VK_Q;
	public static final int BUILD_TRAP = KeyEvent.VK_E;
}
