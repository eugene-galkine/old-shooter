package eg.game.weapons;

import eg.engine.render.Draw;
import eg.game.Game;
import eg.game.Util.Drawable;
import eg.game.Util.Useable;

public class Weapon
{
	public static final Weapon[] standardWeapons = 
		{
			new Weapon("Pistol", 8, 5, 4f, 15, 15, 1.1f, false, null, null),
			new Weapon("Machine Pistol", 18, 7, 18f, 20, 15, 1.8f, true, null, new Drawable()
			{
				@Override
				public void draw(int x, int y, float dir)
				{
					Draw.Quad(x, y, x + 4, y - 5, (float)Math.toDegrees(dir) + 90);
				}
			}),
			new Weapon("Double Barrel", 2, 10, 20f, 15, 12, 2.5f, false, new Useable()
			{
				@Override
				public void use()
				{
					for (int i = 0; i < 10; i++)
						Game.instance.AddBullet();
				}
			}, new Drawable()
			{
				@Override
				public void draw(int x, int y, float dir)
				{
					Draw.Quad(x, y, x + 4, y - 5, (float)Math.toDegrees(dir) + 90);
				}
			}),
			new Weapon("Assult Rifle", 20, 9, 6.8f, 40, 15, 2f, true, null, null),
			new Weapon("Rifle", 5, 40, 1f, 100, 25, 3.6f, false, null, new Drawable()
			{
				@Override
				public void draw(int x, int y, float dir)
				{
					Draw.Quad(x, y, x + 4, y - 20, (float)Math.toDegrees(dir) + 90);
				}
			})
		};
	
	public static final Weapon[] specialWeapon =
		{
			new Weapon("Mini-gun", 100, 2, 25f, 15, 15, 7f, true, null, new Drawable()
			{
				@Override
				public void draw(int x, int y, float dir)
				{
					Draw.Quad(x, y, x + 4, y - 20, (float)Math.toDegrees(dir) + 90);
				}
			}),
			new Weapon("Auto Rifle", 10, 10, 1f, 75, 23, 3.8f, false, null, new Drawable()
			{
				@Override
				public void draw(int x, int y, float dir)
				{
					Draw.Quad(x, y, x + 4, y - 20, (float)Math.toDegrees(dir) + 90);
				}
			}),
			new Weapon("RPG", 1, 1, 1.5f, 75, 23, 2.5f, false, new Useable()
			{
				@Override
				public void use()
				{
					Game.instance.AddGernade(1);
				}
			}, new Drawable()
			{
				@Override
				public void draw(int x, int y, float dir)
				{
					//Draw.Quad(x, y, x + 4, y - 20, (float)Math.toDegrees(dir) + 90);
				}
			}),
			new Weapon("Gernade Launcher", 6, 7, 5f, 75, 23, 7f, false, new Useable()
			{
				@Override
				public void use()
				{
					Game.instance.AddGernade(2);
				}
			}, new Drawable()
			{
				@Override
				public void draw(int x, int y, float dir)
				{
					//Draw.Quad(x, y, x + 4, y - 20, (float)Math.toDegrees(dir) + 90);
				}
			})
		};
	
	public static Weapon[] weapons = standardWeapons;
	
	public String name;
	public int maxClip, shootDelay, damage, speed;
	public float recoil, reload;
	Useable shootEvent;
	Drawable draw;
	public boolean auto;

	public Weapon (String _name, int _maxClip, int _shootDelay, float _recoil, int _damage, int _speed, float _reload, boolean _auto, Useable a, Drawable b)
	{
		name = _name;
		maxClip = _maxClip;
		shootDelay = _shootDelay;
		recoil = _recoil;
		damage = _damage;
		speed = _speed;
		reload = _reload;
		auto = _auto;
		if (a == null)
		{
			shootEvent = new Useable()
			{
				@Override
				public void use()
				{
					Game.instance.AddBullet();
				}
			};
		} else
			shootEvent = a;
		
		if (b == null)
		{
			draw = new Drawable()
			{
				@Override
				public void draw(int x, int y, float dir)
				{
					Draw.Quad(x, y, x + 4, y - 10, (float)Math.toDegrees(dir) + 90);
				}
			};
		} else
			draw = b;
	}

	public void shoot()
	{
		shootEvent.use();
	}
	
	public void DrawBullet (int x, int y, float dir)
	{
		draw.draw(x, y, dir);
	}
}
