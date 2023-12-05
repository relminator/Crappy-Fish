/**
 *
 * @author Richard Eric M. Lope (Relminator)
 * @version 1.00 2014/29/03
 * 
 * Http://rel.phatcode.net
 * 
 * License: GNU LGPLv2 or later
 * 
 */

package net.phatcode.rel;

import net.phatcode.rel.utils.AABB;
import net.phatcode.rel.utils.ImageAtlas;
import net.phatcode.rel.utils.SpriteBatcher;
import net.phatcode.rel.utils.SpriteGL;




public abstract class Entity
{
	enum RenderMode
	{
		NORMAL,
		COLORED,
	}
	
	protected boolean active = false;
	protected boolean alive;
		
	// protected para magamit ng extended classes na Letter at Fighter
	protected int width = 32; // size
	protected int height = 32;

	protected float x; // position
	protected float y;
	protected float dx; // direction
	protected float dy;

	protected float speed = 3.0f; // initial speed

	protected int frame = 0; // Animation frames
	protected int baseFrame = 0;
	protected int numFrames = 1;

	protected int ticks = 0; // counter para sa animation
	
	protected RenderMode renderMode = RenderMode.NORMAL;
	
	protected AABB collisionBox = new AABB(); // para sa collision detection

	public Entity()
	{
		collisionBox.init(x, y, width, height);
	}

	// override to sa mga extended classes
	public void update()
	{

	}

	public void render( ImageAtlas imageAtlas, SpriteBatcher spriteBatcher )
	{
		spriteBatcher.sprite( x, y, SpriteGL.FLIP_NONE, imageAtlas.getSprite(baseFrame + frame) );
	}

	public void animate( int delay )
	{
		if( (ticks %  delay) == 0 )
		{
			frame = (frame + 1) % numFrames;
		}
	}
	public void spawn( float x, float y )
	{
		this.x = x;
		this.y = y;
		active = true;
		alive = true;
	}
	
	public boolean collidesWith( AABB aabb )
	{
		return collisionBox.intersects( aabb );
	}
	
	public void kill()
	{
		active = false;
	}
	
	public void destroy()
	{
		alive = false;
	}
	
	public float getWidth()
	{
		return width;
	}

	public float getHeight()
	{
		return height;
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public float getSpeed()
	{
		return speed;
	}

	public int getBaseFrame()
	{
		return baseFrame;
	}

	public int getNumFrames()
	{
		return numFrames;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public void addX(float x)
	{
		this.x += x;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	public void addY(float y)
	{
		this.y += y;
	}

	public float getDx()
	{
		return dx;
	}

	public float getDy()
	{
		return dy;
	}

	public void setDx(float dx)
	{
		this.dx = dx;
	}

	public void setDy(float dy)
	{
		this.dy = dy;
	}

	public void addDy(float y)
	{
		this.dy += y;
	}

	public void setSpeed(float speed)
	{
		this.speed = speed;
	}

	public void setBaseFrame(int baseFrame)
	{
		this.baseFrame = baseFrame;
	}

	public void setNumFrames(int numFrames)
	{
		this.numFrames = numFrames;
	}

	public AABB getCollisionBox()
	{
		return collisionBox;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public boolean isAlive()
	{
		return alive;
	}

	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}


}
