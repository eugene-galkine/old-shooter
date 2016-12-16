package eg.engine;

import java.util.ArrayList;

import eg.engine.sprites.EObject;

public abstract class State
{
	protected ArrayList<EObject> objects = new ArrayList<EObject>();
	
	public abstract void update();
	public abstract void fixedUpdate();
	
	public void draw()
	{
		for (EObject object : objects)
			object.draw();
	}
	
	public void addToList (EObject obj)
	{
		objects.add(obj);
	}
}
