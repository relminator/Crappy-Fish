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

import net.phatcode.rel.utils.ImageAtlas;
import net.phatcode.rel.utils.SpriteBatcher;
import net.phatcode.rel.utils.SpriteGL;

public class Bubble extends Entity
{
	
	private float radius = 0;
	private float maxRadius = 3;
	private int tickIncrement = 1;
	
	private float scale =1;
	
	public Bubble()
	{
		dy = -1;
		dx = 0;
		width = 16;
		height = 16;
		frame  = 0;
		baseFrame = 8;
		numFrames = 1;
		
		scale = 0.5f + (float)(Math.random()*0.6);

		collisionBox.init(-20000, -20000, 1, 1);
	}
	
	@Override
	public void update()
	{
		ticks += tickIncrement;
		
		dx = (float) Math.sin(ticks/10.f) * radius;
		if( radius < maxRadius )
		{
			radius += 0.01f;
		}
		x += dx;
		y -= dy;
		
		x -= Constants.SCROLL_SPEED/2.0f;
				
		if( x < -16 )
		{
			kill();
		}
		
		
	}
	
	public void render( ImageAtlas imageAtlas, SpriteBatcher spriteBatcher )
	{
		
		if( active )
		{
			spriteBatcher.spriteRotateScale( x, y,
											 0, scale, scale,
							     			 SpriteGL.FLIP_NONE, 
							     			 imageAtlas.getSprite(baseFrame + frame) );
		}
	}
	
	public void spawn( float x, float y, float speed, int tickIncrement )
	{
		
		this.x = x;
		this.y = y;
		this.dy = speed;
		this.tickIncrement = tickIncrement;
		active = true;
		alive = true;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	
}
