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

public class Grass extends Entity
{

	
	private int screenX = 0;
	private int screenY = 0;
	
	public Grass()
	{
		width = 256;
		height = 128;
		
		x = 2000;
		y = 2000;
		dx = 0;
		dy = 0;
		
		baseFrame = 97;
		numFrames = 1;
		
		setAlive(false);
		setActive(false);
		collisionBox.init(-20000, -20000, 1, 1);
	}
	
	@Override
	public void update()
	{
		ticks++;
		
		x += dx;
		y += dy;
		
		screenX = (int)x - width/2;
		screenY = (int)y - height/2;
		
		
		if( x < -width ) kill();
		
	}
	
	@Override
	public void render( ImageAtlas imageAtlas, SpriteBatcher spriteBatcher )
	{
		
		if( alive )
		{
			spriteBatcher.sprite( screenX, screenY, 
							     SpriteGL.FLIP_NONE, 
							     imageAtlas.getSprite(baseFrame + frame) );
		}
	}
	
	public void spawn( float x, float y, float speed )
	{
	
		this.x = x;
		this.y = y;
		
		this.speed = speed;
		dx = -speed;
		dy = 0;
		baseFrame = 97;
		
		setActive(true);
		setAlive(true);
		
	}
	
	public boolean collidesWith( AABB aabb ) 
	{
		if (collisionBox.intersects(aabb))
		{
			return true;
		}
		return false;
	}
		
	public void destroy()
	{
		alive = false;
		dy = 0;
		dx = -Constants.SCROLL_SPEED;
	}
	
}
