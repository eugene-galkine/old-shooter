package eg.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import eg.engine.State;
import eg.engine.input.Input;
import eg.engine.render.Draw;
import eg.engine.sprites.Rectangle;
import eg.game.Util.*;
import eg.game.graphics.ParticleEmitter;
import eg.game.net.Client;
import eg.game.net.NetworkPlayer;
import eg.game.weapons.Weapon;
import eg.game.weapons.Throwable;
import eg.game.weapons.Expolive;;

public class Game extends State
{
	private final int BACKGROUNDS = 1024;
	private final int RESPAWN_DELAY = 300;
	private int MPPLAYEROFFSETX, MPPLAYEROFFSETY;
	private int HALFHEIGHT;
	
	public static Game instance;
	//FIXME:
	//1. add spawn points to map maker
	//2. gernade explosion
	//3. blood
	//TODO, in no order:
	//-bodies
	//-death unused
	//-add ai with map maker
	//-anti-cheat
	//-host game ui
	//-map maker in menu
	//-better graphics
	//-shadows?
	@SuppressWarnings("unused")
	private int x, y, dispX, dispY, speed, shootTimer, offx, offy, health, ID, spawnDelay, kills, deaths, currentWeapon, ammo, changeWeaponTimer, gernadeTimer, gernades, joinTimer;
	private float dir, reloading;
	private Texture player, background, player1;
	private List<Bullet> bullets;
	private List<EBullet> ebullets;
	private List<Throwable> throwables;
	private List<ParticleEmitter> particles;
	private Random r;
	private List<eg.engine.sprites.Rectangle> rects;
	private Textures[] textures;
	private Client gameClient;
	private String name, message, acceptedChars, fileName;
	private List<NetworkPlayer> mpplayers;
	private int spawnPoints[][];
	private ArrayList<Message> messages;
	private MainMenu menu;
	private boolean dead, inMenu, typing, mbreleased, canSwitchWeapons, isSpecialWeapon, readyToLoad;
	
	public boolean paused;
	
	public Game ()
	{
		instance = this;
		dir = 0;
		speed = 3;
		bullets = Collections.synchronizedList(new ArrayList<Bullet>());
		shootTimer = 0;
		r = new Random();
		rects = new ArrayList<eg.engine.sprites.Rectangle>();
		mpplayers = Collections.synchronizedList(new ArrayList<NetworkPlayer>());
		health = 100;
		ID = -1;
		ebullets = Collections.synchronizedList(new ArrayList<EBullet>());
		messages = new ArrayList<Message>();
		throwables = Collections.synchronizedList(new ArrayList<Throwable>());
		particles = Collections.synchronizedList(new ArrayList<ParticleEmitter>());
		inMenu = true;
		menu = new MainMenu();
		kills = 0;
		deaths = 0;
		message = "";
		acceptedChars = "[]1234567890-=!@#$%^&*()_+`~qwertyuiop[]asdfghjkl;'zxcvbnm,./ QWERTYUIOP{}|ASDFGHJKL:ZXCVBNM<>?";
		paused = false;
		currentWeapon = 0;
		ammo = Weapon.weapons[currentWeapon].maxClip;
		reloading = 0;
		dead = false;
		mbreleased = true;
		canSwitchWeapons = true;
		isSpecialWeapon = false;
		HALFHEIGHT = 32;
		gernades = 4;
		joinTimer = 0;
		
		Input.load();
		resized();
	}
	
	public void setInMenu(boolean inMenu)
	{
		this.inMenu = inMenu;
	}
	
	public void resized()
	{
		//load textures
		try
		{
			player = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("assets/images/player2.png"));
			background = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("assets/images/environments/grassGround.png"));
			player1 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("assets/images/player.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
				
		//loadMap();
		
		//center player in screen
		dispX = (Display.getWidth() / 2) - HALFHEIGHT;
		dispY = (Display.getHeight() / 2) - HALFHEIGHT;
		MPPLAYEROFFSETX = (Display.getWidth() / 2) - HALFHEIGHT;
		MPPLAYEROFFSETY = (Display.getHeight() / 2) - HALFHEIGHT;
		//for offset of spawning so screen is centered right no matter the resolution
		offx = 1900 - (Display.getWidth() / 2);
		offy = 2000 - (Display.getHeight() / 2);
	}
	
	@Override
	public void update()
	{
		if (!inMenu || paused)
		{
			if (ID == -1 && joinTimer > 0)
			{
				Draw.String("Connecting to Server...", 10, 10, 24);
				return;
			}  else if (ID == -1 && joinTimer <= -60)
			{
				inMenu = true;
				menu.setStateMain();
				return;
			} else if (ID == -1 && joinTimer <= 0)
			{
				Draw.String("Connection Timed out.", 10, 10, 24);
				return;
			}
			
			if (readyToLoad)
			{
				readyToLoad = false;
				loadMap();
			}
			
			//player look at mouse
			dir = (float) Math.toDegrees(Math.atan2(Mouse.getX() - (dispX + HALFHEIGHT), (dispY + HALFHEIGHT) - (Display.getHeight() - Mouse.getY())));
			
			//draw backgrounds
			for (int i = 0; i < 5; i++)
				for (int j = 0; j < 5; j++)
					Draw.Image(background, x - (BACKGROUNDS * i) + offx, y - (BACKGROUNDS * j) + offy);
			
			//draw environment
			for (int i = 0; i < rects.size(); i++)
				rects.get(i).draw(x - offx, y - offy);
			
			//draw bullets
			synchronized (bullets)
			{
				GL11.glColor3f(0,0,0);
				for (Bullet b : bullets)
					if (b.isSpecial)
						Weapon.specialWeapon[b.weaponID].DrawBullet(b.x, b.y, b.dir);
					else
						Weapon.standardWeapons[b.weaponID].DrawBullet(b.x, b.y, b.dir);
			}
			
			//draw ebullets
			synchronized (ebullets)
			{
				GL11.glColor3f(0.1f,0.1f,0.1f);
				for (EBullet b : ebullets)
					if (b.isSpecial)
						Weapon.specialWeapon[b.weaponID].DrawBullet(b.x, b.y, b.dir);
					else
						Weapon.standardWeapons[b.weaponID].DrawBullet(b.x, b.y, b.dir);
			}
			
			GL11.glColor3f(1,1,1);
			//draw particles
			synchronized (throwables)
			{
				for (ParticleEmitter b : particles)
					b.draw();
			}
			
			//draw other players
			synchronized (mpplayers)
			{
				for (NetworkPlayer p : mpplayers)
				{
					if (p.isAlive())
						Draw.Image(player1, p.getX() + x + MPPLAYEROFFSETX, p.getY() + y + MPPLAYEROFFSETY, p.getDir(), 1);
				}
			}
			
			//draw the player
			if (!dead)
				Draw.Image(player, dispX, dispY, dir, 1);
			
			//draw gernades
			synchronized (throwables)
			{
				for (Throwable b : throwables)
					b.draw();
			}
			
			/** 
			 * UI after this
			 * */
			
			//draw health Bar
			Draw.Quad(9, 9, 111, 31);
			GL11.glColor3f(1f,0.2f,0.2f);
			Draw.Quad(10, 10, 10 + health, 30);
			GL11.glColor3f(1,1,1);
			
			//draw ammo count
			if (reloading <= 0)
				Draw.String("Bullets: " + ammo, 120, 25, 25);
			else
			{
				Draw.String("Reloading: " + String.format("%.2f", reloading) + "s", 120, 25, 25);
				if (currentWeapon < Weapon.weapons.length && currentWeapon >= 0 && !isSpecialWeapon)
					Draw.String("Current Weapon: " + Weapon.weapons[currentWeapon].name, 120, 45, 25);
				else if (currentWeapon < Weapon.weapons.length && currentWeapon >= 0 && isSpecialWeapon)
					Draw.String("SPECIAL WEAPON: " + Weapon.weapons[currentWeapon].name, 120, 45, 25);
			}
			
			Draw.String("Gernades: " + gernades, 120, 5, 25);
			
			//tells player they are dead
			if (dead)
				Draw.String("Dead, Spawning: " + String.format("%.2f", spawnDelay * 0.016) + "s", dispX - 100, dispY - 9, 18);
			
			for (int i = 0; i < messages.size(); i++)
			{
				Draw.String(messages.get(i).text, 10, 64 + (i * 20), 16);
				if (messages.get(i).timer <= 0)
					messages.remove(i);
			}
			//typing a message
			if (typing)
			{
				GL11.glColor3f(0.2f,0.2f,0.2f);
				Draw.Quad(10, 64 + (messages.size() * 20), 10 + (11 * message.length()), 80 + (messages.size() * 20));
				GL11.glColor3f(1,1,1);
				Draw.String(message, 10, 64 + (messages.size() * 20), 16);
			}
		}
		if (inMenu)
		{
			menu.update();
		}
	}

	@Override
	public void fixedUpdate()
	{
		try
		{
			if (inMenu)
				menu.fixedUpdate();
			if (ID == -1)
				joinTimer--;
			if (gameClient == null)
				return;
			
			if (reloading > 0)
			{
				reloading -= 0.016;
				if (reloading <= 0)
					ammo = Weapon.weapons[currentWeapon].maxClip;
			}
			
			if (changeWeaponTimer > 0)
				changeWeaponTimer--;
			
			if (kills >= 4)//killstreak
			{
				kills = 0;
				canSwitchWeapons = false;
				isSpecialWeapon = true;
				Weapon.weapons = Weapon.specialWeapon;
				currentWeapon = r.nextInt(Weapon.weapons.length);
			}
			
			while (Keyboard.next())
			{
				if (Keyboard.getEventKeyState())
				{
					if (Keyboard.getEventKey() == Input.keys[Input.START_TYPING] && !typing && !paused)
					{
						typing = true;
						message = "";
					} else if (Keyboard.getEventKey() == Input.keys[Input.RELOAD] && !typing && !paused && reloading <= 0 && ammo < Weapon.weapons[currentWeapon].maxClip)
					{
						reloading = Weapon.weapons[currentWeapon].reload;
					} else if (Keyboard.getEventKey() == Keyboard.KEY_RETURN && typing && !paused)
						
					{
						typing = false;
						if (message.length() > 0)
						{
							message = name + ": " + message;
							gameClient.sendData(("6" + message).getBytes());
						}
					} else if (typing && !paused)
					{
						if (Keyboard.getEventKey() == Keyboard.KEY_BACK && message.length() > 0)
							message = message.substring(0, message.length() - 1);
						else if (acceptedChars.contains("" + Keyboard.getEventCharacter()))
							message += Keyboard.getEventCharacter();
					} else
						if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE)
						{
							paused = !paused;
							inMenu = paused;
							menu.resume();
						}
				}
			}
			
			while (Mouse.next())
				if (!Mouse.getEventButtonState() && Mouse.getEventButton() == 0)
					mbreleased = true;

			//count down to next shot
			if (shootTimer > 0)
				shootTimer--;
			
			//count down to spawn
			if (spawnDelay > 0)
				spawnDelay--;
			
			if (spawnDelay <= 0 && dead)
				Spawn();
			
			if (gernadeTimer > 0)
				gernadeTimer--;
			
			boolean downBlocked = false;
			boolean upBlocked = false;
			boolean leftBlocked = false;
			boolean rightBlocked = false;
			//collision detection
			java.awt.Rectangle rect = new java.awt.Rectangle(dispX + 10, dispY + 10, player.getImageWidth() - 20, player.getImageHeight() - 20);
			for (int i = 0; i < rects.size(); i++)
			{
				if (!rects.get(i).textureName.equals("floor.png"))
				{
					java.awt.Rectangle rect1 = rects.get(i).getRectangle(x - offx, y - offy);
					float w = (float) (0.5f * (rect.getWidth() + rect1.getWidth()));
					float h = (float) (0.5f * (rect.getHeight() + rect1.getHeight()));
					float dx = (float) (rect.getCenterX() - rect1.getCenterX());
					float dy = (float) (rect.getCenterY() - rect1.getCenterY());
	
					if (Math.abs(dx) <= w && Math.abs(dy) <= h)
					{
					    float wy = w * dy;
					    float hx = h * dx;
	
					    if (wy > hx)
					    {
					        if (wy > -hx)//bottom
					        	downBlocked = true;
					        else//left
					        	leftBlocked = true;
					    }
					    else
					        if (wy > -hx)//right
					        	rightBlocked = true;
					        else//top
					        	upBlocked = true;
					}
					
					//bullet collision with walls
					synchronized (bullets)
					{
						for (int b = 0; b < bullets.size(); b++)
						{
							if (bullets.get(b).getRect().intersects(rect1))
							{
								bullets.remove(b);
								b--;
							}
							else
								synchronized (mpplayers)
								{
									for (int b1 = 0; b1 < mpplayers.size(); b1++)
									{
										if (mpplayers.get(b1).isAlive())
										{
											java.awt.Rectangle rect2 = new java.awt.Rectangle(mpplayers.get(b1).getX() + x + dispX + 10, mpplayers.get(b1).getY() + y + dispY + 10, player.getImageWidth() - 20, player.getImageHeight() - 20);
											if (bullets.get(b).getRect().intersects(rect2))
											{
												bullets.remove(b);
												break;
											}
										}
									}
								}
						}
					}
					synchronized (ebullets)
					{
						for (int b = 0; b < ebullets.size(); b++)
						{
							if (ebullets.get(b).getRect().intersects(rect1))
							{
								ebullets.remove(b);
								b--;
							}
							else if (ebullets.get(b).getRect().intersects(rect) && !dead)
							{
								if (!ebullets.get(b).isSpecial)
									health-=Weapon.standardWeapons[ebullets.get(b).weaponID].damage;
								else
									health-=Weapon.specialWeapon[ebullets.get(b).weaponID].damage;
								if (health <= 0 && !dead)
									die(ebullets.get(b).owner);
									
								ebullets.remove(b);
							} else
								synchronized (mpplayers)
								{
									for (int b1 = 0; b1 < mpplayers.size(); b1++)
									{
										if (mpplayers.get(b1).isAlive() && ebullets.get(b).owner != mpplayers.get(b1).getID())
										{
											java.awt.Rectangle rect2 = new java.awt.Rectangle(mpplayers.get(b1).getX() + x + dispX + 10, mpplayers.get(b1).getY() + y + dispY + 10, player.getImageWidth() - 20, player.getImageHeight() - 20);
											if (ebullets.get(b).getRect().intersects(rect2))
											{
												ebullets.remove(b);
												b--;
												break;
											}
										}
									}
								}
						}
					}
					synchronized (throwables)
					{
						for (int z = 0; z < throwables.size(); z++)
						{
							Throwable b = throwables.get(z);
							if (b.getVelocity() <= 0)
								continue;
							
							float w1 = (float) (0.5f * (rect1.getWidth() + 2));
							float h1 = (float) (0.5f * (rect1.getHeight() + 2));
							float dx1 = (float) ((b.getX()) - rect1.getCenterX());
							float dy1 = (float) ((b.getY()) - rect1.getCenterY());

							if (Math.abs(dx1) <= w && Math.abs(dy1) <= h)
							{
							    float wy1 = w1 * dy1;
							    float hx1 = h1 * dx1;
							    
							    float xv = (float) b.getxVel();
							    float yv = (float) b.getyVel();
							    if (wy1 > hx1)
							    {
							        if (wy1 > -hx1)//bottom
							        {
							        	if (b.isInstant())
							        	{
							        		b.explode();
							        		throwables.remove(z);
							        	} else if (yv < 0)
							        	{
							        		yv *= -1;
							        		b.setDir(Math.atan2(yv, xv));
							        	}
							        }
							        else//left
							        {
							        	if (b.isInstant())
							        	{
							        		b.explode();
							        		throwables.remove(z);
							        	} else if (xv > 0)
							        	{
							        		xv *= -1;
							        		b.setDir(Math.atan2(yv, xv));
							        	}
							        }
							    }
							    else
							        if (wy1 > -hx1)//right
							        {
							        	if (b.isInstant())
							        	{
							        		b.explode();
							        		throwables.remove(z);
							        	} else if (xv < 0)
							        	{
							        		xv *= -1;
							        		b.setDir(Math.atan2(yv, xv));
							        	}
							        }
							        else//top
							        {
							        	if (b.isInstant())
							        	{
							        		b.explode();
							        		throwables.remove(z);
							        	} else if (yv > 0)
							        	{
							        		yv *= -1;
							        		b.setDir(Math.atan2(yv, xv));
							        	}
							        }
							}
							
						}
					}
				}
			}
			
			//used for bullets moving with player and collision detection
			int oldy = y, oldx = x;
			
			if (!dead && !typing && !paused)
			{
				//player walking
				if (Keyboard.isKeyDown(Input.keys[Input.WALK_FORWARD]) && !downBlocked)
					y += speed;
				else if (Keyboard.isKeyDown(Input.keys[Input.WALK_BACKWARD]) && !upBlocked)
					y -= speed;
				
				if (Keyboard.isKeyDown(Input.keys[Input.WALK_RIGHT]) && !leftBlocked)
					x -= speed;
				else if (Keyboard.isKeyDown(Input.keys[Input.WALK_LEFT]) && !rightBlocked)
					x += speed;
				
				if (canSwitchWeapons)//switch weapons
				{
					int temp = currentWeapon;
					if (Keyboard.isKeyDown(Input.keys[Input.PREV_WEAPON]) && changeWeaponTimer <= 0)
					{
						currentWeapon--;
						changeWeaponTimer = 10;
					}
					else if (Keyboard.isKeyDown(Input.keys[Input.NEXT_WEAPON]) && changeWeaponTimer <= 0)
					{
						currentWeapon++;
						changeWeaponTimer = 10;
					}
					
					if (currentWeapon >= Weapon.weapons.length)
						currentWeapon = 0;
					else if (currentWeapon < 0)
						currentWeapon = Weapon.weapons.length-1;
					
					if (currentWeapon != temp)
						reloading = Weapon.weapons[currentWeapon].reload;
				}
			}
			
			int newx = (x - oldx);
			int newy = (y - oldy);
			//move bullets in correct direction
			synchronized (bullets)
			{
				for (int i = 0; i < bullets.size(); i++)
				{
					//move in direction
					int speed = 0;
					if (!bullets.get(i).isSpecial)
						speed = Weapon.standardWeapons[bullets.get(i).weaponID].speed;
					else
						speed = Weapon.specialWeapon[bullets.get(i).weaponID].speed;
					bullets.get(i).x += speed * Math.cos(bullets.get(i).dir);
					bullets.get(i).y += speed * Math.sin(bullets.get(i).dir);
					
					//move the bullets if player moves
					bullets.get(i).x += newx;
					bullets.get(i).y += newy;
					
					//delete bullet when off screen
					if (bullets.get(i).y < -10000 || bullets.get(i).y > 10000 || bullets.get(i).x < -10000 || bullets.get(i).x > 10000)
						bullets.remove(i);
				}
			}
			
			synchronized (ebullets)
			{
				for (int i = 0; i < ebullets.size(); i++)
				{
					//move in direction
					int speed = 0;
					if (!ebullets.get(i).isSpecial)
						speed = Weapon.standardWeapons[ebullets.get(i).weaponID].speed;
					else
						speed = Weapon.specialWeapon[ebullets.get(i).weaponID].speed;
					ebullets.get(i).x += speed * Math.cos(ebullets.get(i).dir);
					ebullets.get(i).y += speed * Math.sin(ebullets.get(i).dir);
					
					//move the bullets if player moves
					ebullets.get(i).x += newx;
					ebullets.get(i).y += newy;
					
					//delete bullet when off screen
					if (ebullets.get(i).y < -10000 || ebullets.get(i).y > 10000 || ebullets.get(i).x < -10000 || ebullets.get(i).x > 10000)
						ebullets.remove(i);
				}
			}
			
			if (Weapon.weapons[currentWeapon].auto)
			{
				//shoot if holding left click and shoot timer is out
				if (Mouse.isButtonDown(0) && shootTimer <= 0 && !dead && !paused && ammo > 0 && reloading <= 0)
				{
					ammo--;
					shootTimer = Weapon.weapons[currentWeapon].shootDelay;
					Weapon.weapons[currentWeapon].shoot();
				} else if (Mouse.isButtonDown(0) && reloading <= 0 && ammo <= 0)
					reloading = Weapon.weapons[currentWeapon].reload;
			} else//semi auto
			{
				if (Mouse.isButtonDown(0) && shootTimer <= 0 && !dead && !paused && ammo > 0 && mbreleased && reloading <= 0)
				{
					ammo--;
					mbreleased = false;
					shootTimer = Weapon.weapons[currentWeapon].shootDelay;
					Weapon.weapons[currentWeapon].shoot();
				} else if (Mouse.isButtonDown(0) && reloading <= 0 && ammo <= 0)
					reloading = Weapon.weapons[currentWeapon].reload;
			}
			
			synchronized (throwables)
			{
				for (int i = 0; i < throwables.size(); i++)
					if (throwables.get(i).update(newx, newy, 0.016f))
						throwables.remove(i);
			}
			
			if (!typing && Keyboard.isKeyDown(Input.keys[Input.GERNADE]) && gernades > 0 && gernadeTimer <= 0)
			{
				gernadeTimer = 30;
				gernades--;
				throwables.add((Throwable)Expolive.gernades[0].use(dir, dispX + HALFHEIGHT, dispY + HALFHEIGHT));
				gameClient.sendData(("7" + ID + "," + dir + "," + (-x) + "," + (-y) + "," + 0).getBytes());
			}
			
			//send data to server
			gameClient.sendData(("0" + ID + "," + (-x) + "," + (-y) + "," + dir).getBytes());
			
			//message timeout
			for (Message m : messages)
				m.timer--;
		} catch (Exception e) {}
	}
	
	public void readyToLoadMap (String _fileName)
	{
		fileName = _fileName;
		readyToLoad = true;
	}
	
	private void loadMap() 
	{
		//loads the map
		try {
			BufferedReader read = new BufferedReader(new FileReader("assets/maps/" + fileName + ".map"));
			String x1;
			rects = new ArrayList<Rectangle>();
			
			textures = new Textures[1];
			int i = 1;
			
			//load textures that are needed for this map
			while ((x1 = read.readLine()) != null)
			{
				if (x1.equals("-"))
					break;
				
				Textures[] temp = new Textures[i];
				for (int j = 0; j < textures.length; j++)
					temp[j] = textures[j];
				
				textures = temp;
				textures[i - 1] = new Textures(x1);
				textures[i - 1].load();
				i++;
			}
			
			//load spawn points
			x1 = read.readLine();
			spawnPoints = new int[new Integer(x1)][2];
			i = 0;
			
			while ((x1 = read.readLine()) != null)
			{
				if (x1.equals("-"))
					break;
				
				spawnPoints[i][0] = new Integer(x1);
				x1 = read.readLine();
				spawnPoints[i][1] = new Integer(x1);
				
				i++;
			}
			
			//load map objects
			while ((x1 = read.readLine()) != null)
			{
				rects.add(new Rectangle(new Integer(x1), new Integer(read.readLine()), new Integer(read.readLine()), new Integer(read.readLine())));
				String tn = read.readLine();
				rects.get(rects.size() - 1).textureName = tn;
				
				for (i = 0; i < textures.length; i++)
				{
					if (tn.isEmpty())
						break;
					
					if (textures[i].name.compareTo(tn) == 0)
						rects.get(rects.size() - 1).t = textures[i].t;
				}
			}
			read.close();
			Spawn();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getName ()
	{
		return name;
	}
	
	public int getID ()
	{
		return ID;
	}
	
	public void setID (int _id)
	{
		ID = _id;
	}
	
	public void addMPPlayer (String data)//new player joined the server
	{
		String name = data.substring(0, data.indexOf(","));
		int id = new Integer(data.substring(data.indexOf(",") + 1, data.length()));
		mpplayers.add(new NetworkPlayer(name, id));
		
		synchronized (messages)
		{
			messages.add(new Message("player " + name + " connected"));
		}
	}
	
	public void removeMPPlayer (int id)//player disconnected
	{
		String n = null;
		for (int i = 0; i < mpplayers.size(); i++)
			if (mpplayers.get(i).getID() == id)
			{
				n = mpplayers.get(i).getName();
				mpplayers.remove(i);
				break;
			}
		
		synchronized (messages)
		{
			if (n != null)
				messages.add(new Message("player " + n + " disconnected"));
		}
	}
	
	public void moveMPPlayer(String data)//move a network player
	{
		int id = new Integer(data.substring(0, data.indexOf(",")));
		data = data.substring(data.indexOf(",") + 1, data.length());
		int mpx = new Integer(data.substring(0, data.indexOf(",")));
		data = data.substring(data.indexOf(",") + 1, data.length());
		int mpy = new Integer(data.substring(0, data.indexOf(",")));
		data = data.substring(data.indexOf(",") + 1, data.length());
		float mdir = new Float(data);
		
		for (int i = 0; i < mpplayers.size(); i++)
		{
			if (mpplayers.get(i).getID() == id)
			{
				mpplayers.get(i).setX(mpx);
				mpplayers.get(i).setY(mpy);
				mpplayers.get(i).setDir(mdir);
				return;
			}
		}
	}
	
	public void setName(String data)
	{
		name = data;
	}
	
	public void Spawn ()//respawn
	{
		kills = 0;
		health = 100;
		dead = false;
		canSwitchWeapons = true;
		currentWeapon = 0;
		isSpecialWeapon = false;
		Weapon.weapons = Weapon.standardWeapons;
		gernades = 4;
		gernadeTimer = 0;
		ammo = Weapon.weapons[currentWeapon].maxClip;
		
		int spawn[] = spawnPoints[r.nextInt(spawnPoints.length)];
		x = spawn[0];
		y = spawn[1];
		gameClient.sendData(("5" + ID).getBytes());
	}
	
	public void newBullet(String data)//new bullet spawned in network
	{
		int id = new Integer(data.substring(0, data.indexOf(",")));
		data = data.substring(data.indexOf(",") + 1, data.length());
		int mpx = new Integer(data.substring(0, data.indexOf(",")));
		data = data.substring(data.indexOf(",") + 1, data.length());
		int mpy = new Integer(data.substring(0, data.indexOf(",")));
		data = data.substring(data.indexOf(",") + 1, data.length());
		float mdir = new Float(data.substring(0, data.indexOf(",")));
		data = data.substring(data.indexOf(",") + 1, data.length());
		int wid = new Integer(data.substring(0, data.indexOf(",")));
		data = data.substring(data.indexOf(",") + 1, data.length());
		boolean isSpecial = new Boolean(data);
		
		ebullets.add(new EBullet(mdir, mpx + x + dispX + 32, mpy + y + dispY + 32, wid, id, isSpecial));
	}
	
	public void death(String data)//someone died
	{
		int killer = new Integer(data.substring(0, data.indexOf(",")));
		data = data.substring(data.indexOf(",") + 1, data.length());
		int victim = new Integer(data);
		
		String killerN = null, victimN = null;
		
		if (ID == killer)
		{
			kills++;
			killerN = name;
		} else if (ID == victim)
		{
			victimN = name;
			deaths++;
		}
		
		synchronized (mpplayers)
		{
			for (NetworkPlayer p : mpplayers)
			{
				if (killer == p.getID())
					killerN = p.getName();
				else if (victim == p.getID())
				{
					victimN = p.getName();
					p.setAlive(false);
				}
				
				if (killerN != null && victimN != null)
					break;
			}
		}
		
		synchronized (messages)
		{
			messages.add(new Message(killerN + " killed " + victimN));
		}
	}
	
	public void respawned(String data)//network player restart
	{
		int id = new Integer(data);
		
		synchronized (mpplayers)
		{
			for (NetworkPlayer p : mpplayers)
			{
				if (id == p.getID())
				{
					p.setAlive(true);
					break;
				}
			}
		}
	}
	
	public void newMessage(String data)//someone typed a message in chat
	{
		messages.add(new Message(data));
	}
	
	public boolean joinServer(String text)//join server with this ip
	{
		try
		{
			gameClient = new Client(this, text);
			gameClient.start();
			gameClient.sendData((1 + name).getBytes());
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		inMenu = false;
		//Spawn();
		return true;
	}
	
	public void disconnect()
	{
		inMenu = true;
		paused = false;
		gameClient.sendData (("2" + getID()).getBytes());
		gameClient = null;
	}
	
	public void AddBullet()
	{
		synchronized (bullets)
		{
			bullets.add(new Bullet((float) Math.toRadians(dir - 90 + (r.nextInt((int)Weapon.weapons[currentWeapon].recoil) - (Weapon.weapons[currentWeapon].recoil/2))), dispX + HALFHEIGHT, dispY + HALFHEIGHT, currentWeapon, isSpecialWeapon));
			gameClient.sendData(("3" + ID + "," + (-x) + "," + (-y) + "," + bullets.get(bullets.size() - 1).dir + "," + currentWeapon + "," + isSpecialWeapon).getBytes());
		}
	}

	public void gernade(String data)
	{
		int pID = new Integer(data.substring(0, data.indexOf(",")));
		data = data.substring(data.indexOf(",") + 1, data.length());
		float dir = new Float(data.substring(0, data.indexOf(",")));
		data = data.substring(data.indexOf(",") + 1, data.length());
		int _x = new Integer(data.substring(0, data.indexOf(",")));
		data = data.substring(data.indexOf(",") + 1, data.length());
		int _y = new Integer(data.substring(0, data.indexOf(",")));
		data = data.substring(data.indexOf(",") + 1, data.length());
		int id = new Integer(data);
		
		if (pID != ID)
			throwables.add((Throwable)Expolive.gernades[id].use(dir, _x + x + dispX + HALFHEIGHT, _y + y + dispY + HALFHEIGHT, pID));
	}

	public void AddGernade(int i)
	{
		throwables.add((Throwable)Expolive.gernades[i].use(dir, dispX + HALFHEIGHT, dispY + HALFHEIGHT));
		gameClient.sendData(("7" + ID +"," + i + "," + dir + "," + (-x) + "," + (-y)).getBytes());
	}
	
	public void DoDamageInRadius (int _x, int _y, float radius, float damage, int sourceID)
	{
		float distance = (int)Math.sqrt((_x - (dispX + HALFHEIGHT))*(_x - (dispX + HALFHEIGHT)) + (_y - (dispY + HALFHEIGHT))*(_y - (dispY + HALFHEIGHT)));
		if (distance <= radius)
			health -= Math.sin(((radius - damage)/radius) * (3.1415/2)) * damage;
		
		if (health <= 0 && !dead)
			die(sourceID);
	}
	
	public void die (int killer)
	{
		dead = true;
		kills = 0;
		health = 1;
		spawnDelay = RESPAWN_DELAY;
		gameClient.sendData(("4" + killer + "," + ID).getBytes());
	}

	public void joiningServer()
	{
		joinTimer = 200;
	}
}
