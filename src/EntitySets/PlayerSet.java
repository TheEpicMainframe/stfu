package EntitySets;
/**
 * PlayerSet. The Player entity set
 * Date: February 10, 2019
 * @author Jared Pon, Haiyang He, Alex Stark, Romiro Piquer
 * @version 1.0
 */
import poj.EntitySet.*;
import Resources.GameResources;
import Resources.GameConfig;
import poj.Render.ImageRenderObject;
import poj.linear.Vector2f;
import Components.*;
import poj.linear.*;

import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PlayerSet extends EntitySet
{
	public PlayerSet()
	{
		super();
		addComponent(new Render(new ImageRenderObject(
			0, 0, GameResources.playerSpriteSheet)));
		addComponent(new WorldAttributes(
			new Vector2f(GameConfig.PLAYER_SPAWNNING_POS),
			GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT));
		addComponent(new HasAnimation(
			GameResources.playerNGunIdleAnimation));
		addComponent(new Movement(GameConfig.PLAYER_SPEED));
		addComponent(new MovementDirection(CardinalDirections.N));
		addComponent(new FacingDirection(CardinalDirections.N));
		// collisions
		addComponent(new PhysicsPCollisionBody(
			GameConfig.PLAYER_COLLISION_BODY));
		addComponent(new PHitBox(GameConfig.PLAYER_HITBOX_BODY));
		addComponent(new AttackCycle(GameConfig.PLAYER_ATTACK_CYCLE));
		// player sound effects:
		addComponent(
			new SoundEffectAssets(GameResources.playerSoundAsset));
		addComponent(new AnimationWindowAssets(
			GameConfig.PLAYER_ANIMATION_WINDOW_ASSETS));
		// resources
		addComponent(new Ammo(GameConfig.PLAYER_STARTING_AMMO,
				      GameConfig.PLAYER_MAX_AMMO));
		addComponent(new HitPoints(GameConfig.PLAYER_HP,
					   GameConfig.PLAYER_MAX_HP));
		addComponent(new Money(GameConfig.PLAYER_STARTING_CASH));
		addComponent(new Damage(GameConfig.PLAYER_STARTING_DAMAGE));
		addComponent(new KillCount());

		addComponent(new OctoMeleeAttackBuffer(
			GameConfig.PLAYER_MELEE_ATTACK_BUFFER));
	}
}
