package Game.GameEvents;

import Components.*;
import EntitySets.*;
import Game.PlayGame;
import Resources.GameConfig;
import Resources.GameResources;

import java.awt.Color;
import java.util.Optional;

import poj.EngineState;
import poj.GameWindow.InputPoller;
import poj.Component.*;
import poj.linear.Vector2f;
import poj.Animation;
import poj.Collisions.*;

public class SetSpeedToZeroEvent extends FocusedPlayGameEvent
{

	public SetSpeedToZeroEvent(PlayGame g, int e)
	{
		super(g, e);
	}

	public void f()
	{
		EngineState engineState = super.getPlayGame().getEngineState();

		Optional<Movement> mopt =
			engineState.getComponentAt(Movement.class, focus1);

		if (!mopt.isPresent())
			return;

		mopt.get().setSpeed(0);
	}
}
