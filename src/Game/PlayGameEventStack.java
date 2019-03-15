package Game;

import java.util.Stack;

public class PlayGameEventStack extends Stack<PlayGameEvent>
{
	private static final long serialVersionUID = 0l;

	public void runGameEventStack(PlayGame g)
	{

		while (!this.isEmpty()) {
			PlayGameEvent tmp = this.pop();
			tmp.f(g);
		}
	}
}
