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
import net.phatcode.rel.utils.LineBatcher;
import net.phatcode.rel.utils.SpriteBatcher;

public class Menu
{
	private EntityFactory buttons = new ButtonFactory(16);
	
	public Menu()
	{
		
	}
	
	public void update()
	{
		buttons.update();
	}
	
	public void draw()
	{
		buttons.draw();
	}
	
	public void drawAabbs( LineBatcher lineBatcher, float r, float g, float b, float a )
	{
		buttons.drawAabbs( lineBatcher, r, g, b, a );
	}
	
	public void attachImageAtlas( ImageAtlas imageAtlas )
	{
		buttons.imageAtlas = imageAtlas;
	}
	
	public void attachSpriteBatcher( SpriteBatcher spriteBatcher )
	{
		buttons.spriteBatcher = spriteBatcher;
	}
	
	public void spawnButton( float x, float y, 
						     float sx, float sy,
						     int width,
						     int height,
						     int baseFrame,
						     int numFrames,
						     boolean isAnimated )
	{
		buttons.spawn( x, y, sx, sy );
		Button b = (Button)buttons.getEntity(buttons.getSize()-1);
		b.setBaseFrame(baseFrame);
		b.setNumFrames(numFrames);
		b.setAnimated(isAnimated);
		b.setSize(width, height);
	}
	
	public void clear()
	{
		buttons.clear();
	}
	
	public void reverseMovement()
	{
		int size = buttons.getSize();
		for( int i = 0; i < size; i++ )
		{
			Button b = (Button)buttons.getEntity(i);
			b.reverseMovement();
		}
	}
	
	public Button getButton( int index )
	{
		return (Button)buttons.getEntity(index);
	}
	
	public int getCollision( AABB aabb )
	{
		return buttons.getCollisionIndex(aabb);
	}
	
}
