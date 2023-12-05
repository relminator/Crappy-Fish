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
import net.phatcode.rel.utils.Recycler;

public class BubbleFactory extends EntityFactory
{

	private int ticks = 0;
	private float playerX = 0;
	private float playerY = 0;
	
 	public BubbleFactory( int size )
 	{
 		entities = new Recycler<Entity>( size, new Bubble() );
 		// need to do this for recycler to work
 		for( int i = 0; i < size; i++ )
 		{
 			entities.add( new Bubble() );  // fill with unique references
 		}
 		entities.clear();  // reset size to 0 so that we could spawn
 	}
 	
 	public void spawn( float x, float y, float speed, int tickIncrement )
 	{
 		Bubble e = (Bubble)entities.getFreeElement();
 		if( e != null )
 		{
 			e.spawn( x, y, speed, tickIncrement );
 		}
 	}
 	
 	public void update()
	{
 		if( (ticks++ & 15) == 0 )
		{
			spawn( playerX + 16, 
				   playerY - 8,
			       0.5f + (float)(Math.random() * 5), 1 + (int)(Math.random() * 2) );
		}
		
 		super.update();		
	}
 	
 	public boolean collideWith( AABB aabb,
 								Player player )
	{		
		return false;
	}

	public void setPlayerX(float playerX)
	{
		this.playerX = playerX;
	}

	public void setPlayerY(float playerY)
	{
		this.playerY = playerY;
	}
	
		
}
