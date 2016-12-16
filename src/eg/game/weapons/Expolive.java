package eg.game.weapons;

import eg.engine.render.Draw;
import eg.game.Game;
import eg.game.Util.Drawable;
import eg.game.Util.Useable1;

public class Expolive
{
	public static final Expolive[] gernades = 
		{
			new Expolive ("frag", 0.02f, 4, false, 4f, new Useable1()
			{
				@Override
				public void use(int x, int y, int owner)//explode
				{
					Game.instance.DoDamageInRadius(x, y, 200, 100, owner);
				}
			}, new Drawable()
			{
				@Override
				public void draw(int x, int y, float dir)
				{
					Draw.Quad(x - 4, y - 4, x + 4, y + 4, (float)Math.toDegrees(dir));
				}
			}), 
			new Expolive ("rocket", 0.01f, 10, true, 3f, new Useable1()
			{
				@Override
				public void use(int x, int y, int owner)//explode
				{
					Game.instance.DoDamageInRadius(x, y, 300, 120, owner);
				}
			}, new Drawable()
			{
				@Override
				public void draw(int x, int y, float dir)
				{
					Draw.Quad(x - 5, y - 4, x + 10, y + 4, (float)Math.toDegrees(dir));
				}
			}),
			new Expolive ("launched", 0.02f, 7, false, 2f, new Useable1()
			{
				@Override
				public void use(int x, int y, int owner)//explode
				{
					Game.instance.DoDamageInRadius(x, y, 200, 90, owner);
				}
			}, new Drawable()
			{
				@Override
				public void draw(int x, int y, float dir)
				{
					Draw.Quad(x - 5, y - 5, x + 5, y + 5, (float)Math.toDegrees(dir));
				}
			})
		};
	
	public String name;
	int speed;
	boolean instant;
	float time, range;
	Useable1 explosion;
	Drawable draw;
	
	public Expolive (String _name, float _range, int _speed, boolean _instant, float _time, Useable1 _explosion, Drawable _draw)
	{
		name = _name;
		range = _range;
		speed = _speed;
		instant = _instant;
		time = _time;
		explosion = _explosion;
		draw = _draw;
	}
	
	public void explode(int x, int y, int owner)
	{
		explosion.use(x, y, owner);
	}
	
	public Object use(float dir, int x, int y)
	{
		return new Throwable(x, y, range, dir, speed, time, instant, explosion, Game.instance.getID(), draw);
	}
	
	public Object use(float dir, int x, int y, int pID)
	{
		return new Throwable(x, y, range, dir, speed, time, instant, explosion, pID, draw);
	}
}
