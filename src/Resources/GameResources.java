package Resources;
import java.awt.image.*;

import poj.Render.ImageLoader;
import poj.Animation;
import poj.Render.ImageWindow;

public class GameResources
{
	public static BufferedImage testImage =
		ImageLoader.load("resources/playerspritesheet.png");

	public static BufferedImage testTile =
		ImageLoader.load("resources/iso-64x64-building.png");

	public static String mapConfig = "resources/map1Config.json",
			     tileSet = "resources/tiles1.json",
			     mapLayer0 = "resources/map1_ground.csv",
			     mapLayer1 = "resources/map1_not_ground.csv",
			     mapLayer2 = "resources/map1_roof.csv";

	public static Animation testImageAnimation = new Animation(
		new ImageWindow(0, 0, 60, 30), 30, 60, 0, 0, 0, 120, 0);

	// player resources
}
