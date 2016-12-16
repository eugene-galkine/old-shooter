/**
 * @author Eugene Galkine
 */
package eg.engine.render;

import java.io.IOException;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Draw 
{	
	static Texture font;
	
	static
	{
		//GL11.glRenderMode(GL11.GL_POLYGON_SMOOTH_HINT);
		reload();
	}
	
	public static void reload()
	{
		try {
			font = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("assets/images/fonts/Arial.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void Image (Texture texture, int x1, int y1, float scale)//with scaler
	{
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		texture.bind();
		GL11.glEnable(GL11.GL_BLEND);
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2i(x1, (int) ((int)texture.getImageHeight()*scale + y1));
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2i((int) ((int)texture.getImageWidth()*scale + x1), (int) ((int)texture.getImageHeight()*scale + y1));
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2i((int) ((int)texture.getImageWidth()*scale + x1), y1);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2i(x1, y1);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void Image (Texture texture, int x1, int y1)//without scaler
	{
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		texture.bind();
		GL11.glEnable(GL11.GL_BLEND);
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2i(x1, texture.getImageHeight()+ y1);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2i(texture.getImageWidth() + x1, texture.getImageHeight() + y1);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2i(texture.getImageWidth() + x1, y1);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2i(x1, y1);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void Image (Texture texture, int x1, int y1, float dir, float scale)
	{
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		texture.bind();
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glTranslatef(x1 + texture.getImageWidth()*scale/2, y1 + texture.getImageHeight()*scale/2, 0);
		GL11.glRotatef(dir, 0, 0, 1);
		GL11.glTranslatef(-(x1 + texture.getImageWidth()*scale/2), -(y1 + texture.getImageHeight()*scale/2), 0);
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2i(x1, (int) ((int)texture.getImageHeight()*scale + y1));
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2i((int) ((int)texture.getImageWidth()*scale + x1), (int) ((int)texture.getImageHeight()*scale + y1));
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2i((int) ((int)texture.getImageWidth()*scale + x1), y1);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2i(x1, y1);
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void Image (Texture texture, int x1, int y1, int x2, int y2)
	{
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		texture.bind();
		GL11.glEnable(GL11.GL_BLEND);
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2i(x1, y2);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2i(x2, y2);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2i(x2, y1);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2i(x1, y1);
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void Quad (int x1, int y1, int x2,  int y2)
	{
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(x1, y1);
			GL11.glVertex2i(x2, y1);
			GL11.glVertex2i(x2, y2);
			GL11.glVertex2i(x1, y2);
		GL11.glEnd();
	}
	
	public static void Quad (int x1, int y1, int x2,  int y2, float rot)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((x1 + Math.abs((x1 - x2)/2)), (y1 + Math.abs((y1 - y2)/2)), 0);
		GL11.glRotatef(rot, 0, 0, 1);
		GL11.glTranslatef(-(x1 + Math.abs((x1 - x2)/2)), -(y1 + Math.abs((y1 - y2)/2)), 0);
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(x1, y1);
			GL11.glVertex2i(x2, y1);
			GL11.glVertex2i(x2, y2);
			GL11.glVertex2i(x1, y2);
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public static void Line (int x1, int x2, int y1, int y2)
	{
		GL11.glBegin(GL11.GL_LINE);
			GL11.glVertex2i(x1, y1);
			GL11.glVertex2i(x2, y2);
		GL11.glEnd();
	}
	
	public static void String (String text, int x, int y, int size)
	{
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		font.bind();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0);
		
		GL11.glBegin(GL11.GL_QUADS);
			for (int i = 0; i < text.length(); i++)
			{
				int ascii = text.charAt(i);
				float scale = 1.0f/16.0f;
				float gridx = (ascii % 16) * scale;
				float gridy = ((int)(ascii / 16.0f)) * scale;
				
				GL11.glTexCoord2f(gridx, gridy);
				GL11.glVertex2f(i * (size / 1.5f), 0);
				GL11.glTexCoord2f(gridx + scale, gridy);
				GL11.glVertex2f(size + (i * (size / 1.5f)), 0);
				GL11.glTexCoord2f(gridx + scale, gridy + scale);
				GL11.glVertex2f(size + (i * (size / 1.5f)), size);
				GL11.glTexCoord2f(gridx, gridy + scale);
				GL11.glVertex2f(i * (size / 1.5f), size);
			}
		GL11.glEnd();
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	public static void Image(Texture texture, int x1, int y1, int x2, int y2, float rot) 
	{
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		texture.bind();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glPushMatrix();
		GL11.glTranslatef((x1 + Math.abs((x1 - x2)/2)), (y1 + Math.abs((y1 - y2)/2)), 0);
		GL11.glRotatef(rot, 0, 0, 1);
		GL11.glTranslatef(-(x1 + Math.abs((x1 - x2)/2)), -(y1 + Math.abs((y1 - y2)/2)), 0);
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2i(x1, y1);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2i(x2, y1);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2i(x2, y2);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2i(x1, y2);
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
}
