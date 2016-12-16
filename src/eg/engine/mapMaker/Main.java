package eg.engine.mapMaker;

import eg.engine.EEngine;

public class Main extends EEngine
{
	
	public static void main(String[] args) 
	{
		GameName = "Map Maker";
		new Main().Start();
	}

	@Override
	protected void init()
	{
		state = new MapMaker();
	}
}
