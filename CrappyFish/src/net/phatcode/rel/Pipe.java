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

import net.phatcode.rel.math.Utils;
import net.phatcode.rel.utils.AABB;
import net.phatcode.rel.utils.ImageAtlas;
import net.phatcode.rel.utils.SpriteBatcher;
import net.phatcode.rel.utils.SpriteGL;

/*
 *   #    Two pipes
 *   #    Three AABBS
 *   #    
 * 
 *   #
 *   #
 *   #
 * 
 */
public class Pipe extends Entity
{

	enum ID
	{
		NORMAL,
		WAVER,
	}
	
	private final int HEIGHT = 128;
	private final int WAVE_HEIGHT = 80;
	
	private int screenX = 0;
	private int screenY = 0;
	
	private AABB collisionBoxTop = new AABB(); 
	private AABB collisionBoxBottom = new AABB(); 
	
	private float sy = 0;
	
	private ID id = ID.NORMAL;
	
	private float coinX = 0;
	private float coinY = 0;
	private float interpolator = 0;
	
	public Pipe()
	{
		width = 35;
		height = HEIGHT;
		
		x = 2000;
		y = 2000;
		dx = 0;
		dy = 0;
		
		baseFrame = 6;
		numFrames = 1;
		
		setAlive(false);
		setActive(false);
		
	}
	
	@Override
	public void update()
	{
		ticks++;
		
		x += dx;
		if( id == ID.NORMAL )
		{
			y = sy;
		}
		else
		{
			y = sy + (float)Math.sin(ticks/50.0d) * WAVE_HEIGHT;			
		}
		
		screenX = (int)x - width/2;
		screenY = (int)y - height/2;
		
		
		if( x < -width ) kill();
		
		// update box collision values
		if( isAlive() )
		{
			coinX = x;
			coinY = y + (float)Math.sin(ticks/15.0) * HEIGHT/2.0f;
			collisionBox.init(screenX, screenY, width, height);
			collisionBox.resize(0.3f, 1 );
		}
		else
		{
			interpolator = Utils.clamp(interpolator+=0.01f, 0.0f, 1.0f);
			coinX = Utils.lerpSmooth(coinX, 30, Utils.smoothStep(interpolator));
			coinY = Utils.lerpSmooth(coinY, -16, Utils.smoothStep(interpolator));
			collisionBox.init(-20000, -20000, 1, 1);
		}
		
		collisionBoxTop.init(screenX, screenY-259, width, 259);		
		collisionBoxBottom.init(screenX, screenY + height, width, 259);		

	}
	
	@Override
	public void render( ImageAtlas imageAtlas, SpriteBatcher spriteBatcher )
	{
		
		// top
		spriteBatcher.sprite( screenX, screenY - 259, 
						      SpriteGL.FLIP_V, 
						      imageAtlas.getSprite(baseFrame + frame) );
		// bottom
		spriteBatcher.sprite( screenX, screenY + height, 
						      SpriteGL.FLIP_NONE, 
						      imageAtlas.getSprite(baseFrame + frame) );

		// coin
		int coinFrame = 236 + ((ticks/2) & 7);
		spriteBatcher.spriteRotateScale( coinX, coinY, 
										 0, 1, 1,
						      			 SpriteGL.FLIP_V, 
						      			 imageAtlas.getSprite(coinFrame) );

		
	}
	
	public void spawn( float x, float y, float speed, ID id )
	{
	
		this.x = x;
		this.y = y;
		
		this.speed = speed;
		this.id = id;
		
		sy = y;
		dx = -speed;
		dy = 0;
		
		coinX = x;
		coinY = y;
		interpolator = 0;
		
		if( id == ID.NORMAL )
		{
			baseFrame = 6;
		}
		else
		{
			baseFrame = 7;	
		}
		
		setActive(true);
		setAlive(true);
		
	}
	
	public int collidesWith( Player player ) 
	{
		if(collisionBox.intersects(player.getCollisionBox()))
		{
			if( id == ID.NORMAL )
			{
				player.addToScore(1);
			}
			else
			{
				player.addToScore(3);
			}
			return 1;
		}
		
		if(collisionBoxTop.intersects(player.getCollisionBox()))
		{
			return 2;
		}
		
		if(collisionBoxBottom.intersects(player.getCollisionBox()))
		{
			return 2;
		}
		
		return 0;
	}
		
	public void destroy()
	{
		alive = false;
		dy = 0;
		dx = -Constants.SCROLL_SPEED;
	}

	public AABB getCollisionBoxTop()
	{
		return collisionBoxTop;
	}

	public AABB getCollisionBoxBottom()
	{
		return collisionBoxBottom;
	}
	
}
