package EntitySets;

import poj.EntitySet.*;
import Resources.GameResources;
import Resources.GameConfig;
import poj.linear.*;
import poj.Render.ImageRenderObject;
import Components.*;

public class CollectibleSet extends EntitySet{

	public CollectibleSet( double spawnTime ) {
		super();
		
		addComponent(new Render(new ImageRenderObject(
				0, 0, GameResources.cashImage)));
		
		addComponent(new WorldAttributes(new Vector2f(1f, 1f),
				 GameConfig.MOB_WIDTH,
				 GameConfig.MOB_HEIGHT));
		
		addComponent(new Lifespan( GameConfig.PICKUP_CASH_SPAWN_TIME , spawnTime));
	}
	
	public CollectibleSet( float x , float y , double spawnTime ) {
		super();
		
		addComponent(new Render(new ImageRenderObject(
				0, 0, GameResources.cashImage)));
		
		addComponent(new WorldAttributes(new Vector2f( x , y ),
				 GameConfig.MOB_WIDTH,
				 GameConfig.MOB_HEIGHT));
		
		addComponent(new Lifespan( GameConfig.PICKUP_CASH_SPAWN_TIME , spawnTime ));
	}
}