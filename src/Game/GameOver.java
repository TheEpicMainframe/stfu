package Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import Components.PCollisionBody;
import Components.Render;
import EntitySets.MenuButton;
import Resources.GameConfig;
import Resources.GameResources;

import poj.Collisions.Polygon;
import poj.GameWindow.InputPoller;
import poj.Logger.Logger;
import poj.Render.ImageRenderObject;
import poj.Render.RenderObject;
import poj.Render.RenderRect;
import poj.Render.Renderer;
import poj.Render.StringRenderObject;
import poj.linear.Vector2f;

/**
 * GameOver game state.
 * @author Jared and Alex
 * @version 2.0 - 04/03/19
 */
public class GameOver extends World
{
	protected int newScore;
	protected ArrayList<ScoreTuple> scores = new ArrayList<ScoreTuple>();
	protected boolean isHighScore;

	protected Queue<RenderObject> renderBuffer;
	protected Queue<RenderObject> backgroundBuffer =
		new LinkedList<RenderObject>();
	protected Queue<RenderObject> buttonsBuffer =
		new LinkedList<RenderObject>();
	protected Queue<RenderObject> collisioBoxBuffer =
		new LinkedList<RenderObject>();
	protected Queue<PCollisionBody> buttonHitBoxBuffer =
		new LinkedList<PCollisionBody>();

	protected static final String SCORES_FILE_NAME = "scores.txt";
	protected static final int FONT_SIZE = 32;

	protected static Font FONT;

	protected ArrayList<RenderObject> menuImageROBuffer =
		new ArrayList<RenderObject>();
	protected ArrayList<PCollisionBody> buttonHitBoxROBuffer =
		new ArrayList<PCollisionBody>();
	protected BufferedImage backButton;
	protected ArrayList<Render> buttonRenderLayer;


	protected ArrayList<Integer> initials = new ArrayList<Integer>() {
		{
			add(65);
			add(65);
			add(65);
		}
	};
	protected int currentInitial = 0;
	protected StringRenderObject initialRender = new StringRenderObject(
		initialsToString(), 30, 30, Color.DARK_GRAY, FONT);

	protected Scanner is = null;


	public GameOver(int width, int height, Renderer renderer,
			InputPoller inputPoller, int newScore)
	{
		super(width, height, renderer, inputPoller);
		this.newScore = newScore;
		this.renderBuffer = new LinkedList<RenderObject>();

		FONT = GameResources.CREEPER_FONT; // copies the pointer for the
						   // manual font "creeper"
		GraphicsEnvironment ge =
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(FONT);

		try {
			Scanner findFile =
				new Scanner(new File(SCORES_FILE_NAME));
			findFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find file: "
					   + SCORES_FILE_NAME);
			try {
				PrintWriter writer = new PrintWriter(
					new BufferedWriter(new FileWriter(
						SCORES_FILE_NAME)));
				writer.write("" + 0 + "\nAAA");
				for (int i = 0; i < 9; i++) {
					writer.write("\n" + 0 + "\nAAA");
				}
				writer.close();
			} catch (IOException ioe) {
				Logger.lassert("Cannot create file: "
					       + SCORES_FILE_NAME);
			}
		}

		try {
			is = new Scanner(new File(SCORES_FILE_NAME));
			String thisName = "";
			int thisScore = 0;
			while (is.hasNextLine()) {
				thisScore = Integer.parseInt(is.nextLine());
				thisName = is.nextLine();
				scores.add(new ScoreTuple(thisScore, thisName));
			}
			is.close();
		} catch (IOException ioe) {
			Logger.lassert("Cannot load high score info");
		} catch (Exception e) {
			Logger.lassert("Cannot load high score info");
		}

		this.isHighScore = true;
		for (ScoreTuple t : scores) {
			if (this.newScore <= t.getScore()) {
				this.isHighScore = false;
				break;
			}
		}

		Collections.sort(scores);
		Collections.reverse(scores);

		// deep copies the coolDown keys
		for (int i = 0; i < GameConfig.COOL_DOWN_KEYS.size(); ++i) {
			coolDownMax.set(GameConfig.COOL_DOWN_KEYS.get(i).fst,
					GameConfig.COOL_DOWN_KEYS.get(i).snd);
		}


		/*
		 * No need ratio:
		 * 	-background
		 *	-enter initials
		 *	-middle thing
		 *	-scores
		 *	-your scores
		 */

		registerComponents();
		registerEntitySets();
		/*
		 * Note: the order of index for the menuImageROBuffer is inside
		 * gameresources
		 */
		// if the resolution is 1920x1080
		if (this.windowWidth == 1920 && this.windowHeight == 1080) {
			// create the imageRenderObject from the bufferedImages
			for (int i = 0; i < GameResources.menuImage90.size();
			     ++i) {
				menuImageROBuffer.add(new ImageRenderObject(
					0, 0, GameResources.goImage90.get(i)));
			}
			// make the menuButton pointer point to the 1920x1080
			// res vector
			backButton = GameResources.goBackButton90;

		}
		// any other resolution will be using the default resolution
		// images: 1366x768 (an OK assumption since modern computers
		// have common resolution ratio of  1366x768)
		else {
			// create the imageRenderObject from the bufferedImages
			for (int i = 0; i < GameResources.menuImage38.size();
			     ++i) {
				menuImageROBuffer.add(new ImageRenderObject(
					0, 0, GameResources.goImage38.get(i)));
			}
			// make the menuButton pointer point to the 1920x1080
			// res vector
			backButton = GameResources.goBackButton38;
		}
		// add the menu buttons
		addMenuButtons();
	}


	public void addMenuButtons()
	{
		// clang-format off
		//registers all of the button entity sets with its render and collision box 
			super.engineState.spawnEntitySet(new MenuButton(this.windowWidth, this.windowHeight, GameResources.gameOverButtonValue[0], GameResources.gameOverButtonValue[1], GameResources.gameOverButtonValue[2], GameResources.gameOverButtonValue[3],this.backButton));
		// clang-format on
		// store the render components for the buttons
		buttonRenderLayer = super.engineState.getComponents()
					    .getRawComponentArrayListPackedData(
						    Render.class);
		buttonHitBoxROBuffer =
			super.engineState.getComponents()
				.getRawComponentArrayListPackedData(
					PCollisionBody.class);
	}

	public void registerComponents()
	{
		super.engineState.registerComponent(PCollisionBody.class);
		super.engineState.registerComponent(Render.class);
	}

	public void registerEntitySets()
	{
		super.engineState.registerSet(MenuButton.class);
	}

	public void runGame()
	{
		poj.Time.Timer.sleepNMilliseconds(20);

		PlayGameProcessInputs.updateCoolDownKeys(this);


		this.processInputs();
	}

	public void processInputs()
	{
		if (inputPoller.isKeyDown(KeyEvent.VK_ENTER))
			quit();

		// Choose letter
		if (inputPoller.isKeyDown(GameConfig.ARROW_DOWN)
		    && Math.abs(lastCoolDown.get(GameConfig.ARROW_DOWN)) == 0) {
			if (initials.get(currentInitial)
			    < GameConfig.Z_INTEGER) {
				Integer initial = initials.get(currentInitial);
				initials.set(currentInitial, initial + 1);
			} else {
				initials.set(currentInitial,
					     GameConfig.A_INTEGER);
			}
			initialRender.setStr(initialsToString());
			PlayGameProcessInputs.updateDtForKey(
				this, GameConfig.ARROW_DOWN,
				-PlayGame.coolDownMax.get(
					GameConfig.ARROW_DOWN));
		}
		if (inputPoller.isKeyDown(GameConfig.ARROW_UP)
		    && Math.abs(lastCoolDown.get(GameConfig.ARROW_UP)) == 0) {
			if (initials.get(currentInitial)
			    > GameConfig.A_INTEGER) {
				Integer initial = initials.get(currentInitial);
				initials.set(currentInitial, initial - 1);
			} else {
				initials.set(currentInitial,
					     GameConfig.Z_INTEGER);
			}
			initialRender.setStr(initialsToString());
			PlayGameProcessInputs.updateDtForKey(
				this, GameConfig.ARROW_UP,
				-PlayGame.coolDownMax.get(GameConfig.ARROW_UP));
		}
		// Choose slot
		if (inputPoller.isKeyDown(GameConfig.ARROW_LEFT)
		    && Math.abs(lastCoolDown.get(GameConfig.ARROW_LEFT)) == 0) {
			if (currentInitial > 0)
				currentInitial--;
			PlayGameProcessInputs.updateDtForKey(
				this, GameConfig.ARROW_LEFT,
				-PlayGame.coolDownMax.get(
					GameConfig.ARROW_LEFT));
		}
		if (inputPoller.isKeyDown(GameConfig.ARROW_RIGHT)
		    && Math.abs(lastCoolDown.get(GameConfig.ARROW_RIGHT))
			       == 0) {
			if (currentInitial < 2)
				currentInitial++;
			PlayGameProcessInputs.updateDtForKey(
				this, GameConfig.ARROW_RIGHT,
				-PlayGame.coolDownMax.get(
					GameConfig.ARROW_RIGHT));
		}
	}

	public void addGameOverPictureRenderBuffer()
	{
		for (int i = 0; i < menuImageROBuffer.size(); ++i) {
			backgroundBuffer.add(menuImageROBuffer.get(i));
		}
		// only have 1 button
		buttonsBuffer.add(buttonRenderLayer.get(0).getGraphic());
		buttonHitBoxBuffer.add(this.buttonHitBoxROBuffer.get(0));
		pCollisionBodyDebugRenderer(this.buttonHitBoxROBuffer.get(0),
					    collisioBoxBuffer, Color.BLUE);
	}

	public void render()
	{
		addGameOverPictureRenderBuffer();

		// adding the text
		renderBuffer.add(new StringRenderObject(
			"HIGH SCORES", super.windowWidth / 2 - 100,
			4 * FONT_SIZE, Color.darkGray, FONT));

		renderBuffer.add(new StringRenderObject(
			"YOUR SCORE", super.windowWidth / 2 - 90, 5 * FONT_SIZE,
			Color.darkGray, FONT));

		renderBuffer.add(new StringRenderObject(
			"" + newScore, super.windowWidth / 2, 6 * FONT_SIZE,
			Color.darkGray, FONT));

		renderBuffer.add(new StringRenderObject(
			"ZOMBIES SLAIN", super.windowWidth / 2 - 100,
			7 * FONT_SIZE, Color.darkGray, FONT));

		if (isHighScore)
			renderBuffer.add(new StringRenderObject(
				"CONGRATS NEW HIGH SCORE",
				super.windowWidth / 2 - 200, 8 * FONT_SIZE,
				Color.darkGray, FONT));

		renderBuffer.add(new StringRenderObject(
			"OTHER SCORES", super.windowWidth / 2 - 100,
			9 * FONT_SIZE, Color.darkGray, FONT));

		for (int i = 0; i < 5 && i < scores.size(); ++i) {
			renderBuffer.add(new StringRenderObject(
				scores.get(i).getName() + ": "
					+ scores.get(i).getScore(),
				super.windowWidth / 2 - 20,
				(i + 10) * FONT_SIZE, Color.darkGray, FONT));
		}

		renderBuffer.add(new StringRenderObject(
			"PRESS ENTER TO GO BACK TO THE MENU",
			super.windowWidth / 2 - 350,
			super.windowHeight - FONT_SIZE - 20, Color.darkGray,
			FONT));

		renderBuffer.add(initialRender);

		renderer.renderBuffers(backgroundBuffer, buttonsBuffer,
				       renderBuffer, collisioBoxBuffer);

		// if the loop will quit
		if (super.quit) {
			overWriteScoresTextFile();
		}
	}

	public void overWriteScoresTextFile()
	{
		try {
			FileWriter fw =
				new FileWriter(new File(SCORES_FILE_NAME));
			String tmp = "";
			for (int i = 0; i < scores.size(); i++) {
				if (i != 0)
					tmp += "\n" + scores.get(i).getScore()
					       + "\n" + scores.get(i).getName();
				else
					tmp += scores.get(i).getScore() + "\n"
					       + scores.get(i).getName();
			}

			System.out.println("file io sht" + tmp);
			fw.write(tmp);
			fw.close();

		} catch (IOException e) {
			Logger.logMessage(
				"Error overwriting scores text file.");
			e.printStackTrace();
		}
	}

	protected String initialsToString()
	{
		String stringOfInitials = "";
		for (int i : initials) {
			String thisInitial = Character.toString((char)i);
			stringOfInitials = stringOfInitials + thisInitial;
		}
		return stringOfInitials;
	}

	public void quit()
	{

		scores.add(new ScoreTuple(newScore, initialsToString()));

		Collections.sort(scores);
		Collections.reverse(scores);

		while (scores.size() > 10) {
			scores.remove(10);
		}
		super.quit();
	}


	public void pCollisionBodyDebugRenderer(final PCollisionBody pc,
						Queue<RenderObject> q, Color r)
	{
		Polygon p = pc.getPolygon();
		Vector2f[] pts = p.pts();

		for (int i = 0; i < p.getSize(); ++i) {
			q.add(new RenderRect((int)pts[i].x, (int)pts[i].y, 5, 5,
					     r));
		}
	}

	private class ScoreTuple implements Comparable<ScoreTuple>
	{
		private String name;
		private int score;

		public ScoreTuple(int defScore, String defName)
		{
			name = defName;
			score = defScore;
		}

		public String getName()
		{
			return name;
		}

		public int getScore()
		{
			return score;
		}

		public void print()
		{
			System.out.println("Name: " + name
					   + " | Score: " + score);
		}

		@Override public int compareTo(ScoreTuple o)
		{
			if (this.getScore() > o.getScore())
				return 1;
			else if (this.getScore() < o.getScore())
				return -1;
			else
				return 0;
		}
	}
}
