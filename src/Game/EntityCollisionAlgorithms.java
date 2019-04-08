package Game;

/**
 * PlayGame -- main class that plays the game (input, render, engine transforms,
 * etc) - giant conglomeration of all the state and the seat and tears and blood
 * of our team put togtether to put together this project.
 *
 * Date: March 12, 2019
 * 2019
 * @author Jared Pon, Haiyang He, Romirio Piqer, Alex Stark
 * @version 1.0
 */
import java.util.Optional;

import Components.*;
import poj.Component.*;
import poj.EngineState;


public class EntityCollisionAlgorithms
{
	public static <T extends PCollisionBody, U extends PCollisionBody> void
	ifSetAAndBPCollisionBodyAreCollidingAndAreUniqueRunGameEvent(
		PlayGame g, Class<? extends Component> a,
		Class<? extends Component> b, Class<T> collisionBodyTypeA,
		Class<U> collisionBodyTypeB, FocusedPlayGameEvent event)
	{
		EngineState engineState = g.getEngineState();

		for (int i = engineState.getInitialSetIndex(a);
		     engineState.isValidEntity(i);
		     i = engineState.getNextSetIndex(a, i)) {

			Optional<? extends Component> apopt =
				engineState.getComponentAt(collisionBodyTypeA,
							   i);

			if (!apopt.isPresent())
				continue;

			PCollisionBody ap = (PCollisionBody)apopt.get();

			for (int j = engineState.getInitialSetIndex(b);
			     engineState.isValidEntity(j);
			     j = engineState.getNextSetIndex(b, j)) {

				Optional<? extends Component> bpopt =
					engineState.getComponentAt(
						collisionBodyTypeB, j);

				if (!bpopt.isPresent())
					continue;

				PCollisionBody bp = (PCollisionBody)bpopt.get();

				if (ap.isCollidingWith(bp) && i != j) {
					event.setFocus(i);
					event.f();
				}
			}
		}
	}


	public static <T extends PCollisionBody> void
	ifSetAAndBPCollisionBodyAreCollidingAndAreUniqueRunGameEvent(
		PlayGame g, Class<? extends Component> a,
		Class<? extends Component> b, Class<T> collisionBodyType,
		FocusedPlayGameEvent event)
	{

		ifSetAAndBPCollisionBodyAreCollidingAndAreUniqueRunGameEvent(
			g, a, b, collisionBodyType, collisionBodyType, event);
	}


	private static NudgeAOutOfBPCollisionBodyEvent<
		PhysicsPCollisionBody> NUDGE_A_OUT_OF_B_P_COLLISION_BODY_MEMO =
		new NudgeAOutOfBPCollisionBodyEvent<PhysicsPCollisionBody>(
			PhysicsPCollisionBody.class);
	public static void
	nudgeSetAAndBIfPCollisionBodiesAreTouching(PlayGame g,
						   Class<? extends Component> a,
						   Class<? extends Component> b)
	{
		NUDGE_A_OUT_OF_B_P_COLLISION_BODY_MEMO.setPlayGame(g);
		ifSetAAndBPCollisionBodyAreCollidingAndAreUniqueRunGameEvent(
			g, a, b, PhysicsPCollisionBody.class,
			NUDGE_A_OUT_OF_B_P_COLLISION_BODY_MEMO);
	}


	private static StartAttackCycleEvent START_ATTACK_CYCLE_EVENT_MEMO =
		new StartAttackCycleEvent();
	public static void
	startAttackCycleIfAggroRadiusCollidesPhysicsPCollisionBody(
		PlayGame g, Class<? extends Component> a,
		Class<? extends Component> b)
	{
		START_ATTACK_CYCLE_EVENT_MEMO.setPlayGame(g);

		ifSetAAndBPCollisionBodyAreCollidingAndAreUniqueRunGameEvent(
			g, a, b, AggroRange.class, PhysicsPCollisionBody.class,
			START_ATTACK_CYCLE_EVENT_MEMO);
	}
}
