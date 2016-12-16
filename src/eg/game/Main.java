package eg.game;

import eg.engine.EEngine;

public class Main extends EEngine
{
	public static final String VERSION = "0.4.5";
	
	@Override
	protected void init()
	{
		state = new Game();
	}
	
	public static void main (String[] args)
	{
		GameName = "Game (v" + VERSION + ")";
		new Main().Start();
	}
}
