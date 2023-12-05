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



public class Button extends Entity
{

	
	private int screenX = 0;
	private int screenY = 0;
	private float sx;
	private float sy;
	private float ex;
	private float ey;
	private boolean animated;
	private boolean movementNormal;
	
	private float interpolator;
	
	public Button()
	{
		width = 64;
		height = 64;
		
		x = 2000;
		y = 2000;
		dx = 0;
		dy = 0;
		
		baseFrame = 12;
		numFrames = 1;
	}
	
	@Override
	public void update()
	{
		ticks++;
		
		if( movementNormal )
		{
			interpolator = Utils.clamp( interpolator+=0.01, 0.0f, 1.0f );
		}
		else
		{
			interpolator = Utils.clamp( interpolator-=0.01, 0.0f, 1.0f );
		}
		
		x = Utils.lerpSmooth(sx, ex, Utils.smoothStep(interpolator));
		y = Utils.lerpSmooth(sy, ey, Utils.smoothStep(interpolator));
		
		screenX = (int)x - width/2;
		screenY = (int)y - height/2;
		
		// update box collision values
		collisionBox.init(screenX, screenY, width, height);		
		
	}
	
	@Override
	public void render( ImageAtlas imageAtlas, SpriteBatcher spriteBatcher )
	{
		if( animated)
		{
				spriteBatcher.spriteRotateScale( x, y, 
			     0, 
			     0.7f + (float)Math.abs(Math.sin(ticks/10d)*0.3f), 
			     0.7f + (float)Math.abs(Math.sin(ticks/10d)*0.3f), 
			     SpriteGL.FLIP_NONE, 
			     imageAtlas.getSprite(baseFrame + frame) );
		}
		else
		{
			spriteBatcher.spriteRotateScale( x, y, 
				     0, 
				     1, 1, 
				     SpriteGL.FLIP_NONE, 
				     imageAtlas.getSprite(baseFrame + frame) );
		}
	}

	public void spawn( float ex, float ey, float sx, float sy )
	{
	
		this.ex = ex;
		this.ey = ey;
		this.sx = sx;
		this.sy = sy;
		movementNormal = true;
		interpolator = 0;
		animated = true;
		active = true;
		alive = true;
		
		x = sx;
		y = sy;
		
	}
	
	
	public boolean collidesWith( AABB aabb ) 
	{
		if (collisionBox.intersects(aabb))
		{
			return true;
		}
		return false;
	}

	public boolean isAnimated()
	{
		return animated;
	}

	public void setAnimated(boolean animated)
	{
		this.animated = animated;
	}

	public void reverseMovement()
	{
		movementNormal = false;
	}

	public void setSize( int width, int height )
	{
		this.setWidth(width);
		this.setHeight(height);
	}

		
}
