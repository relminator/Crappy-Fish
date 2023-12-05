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

public class GrassFactory extends EntityFactory
{

	private int ticks = 0;
	private int viewType = 0;
	
 	public GrassFactory( int size )
 	{
 		entities = new Recycler<Entity>( size, new Grass() );
 		// need to do this for recycler to work
 		for( int i = 0; i < size; i++ )
 		{
 			entities.add( new Grass() );  // fill with unique references
 		}
 		entities.clear();  // reset size to 0 so that we could spawn
 	}
 	
 	public void spawn( float x, float y, float speed, int frameOffset )
 	{
 		Grass e = (Grass)entities.getFreeElement();
 		if( e != null )
 		{
 			e.spawn( x, y, speed );
 			e.setBaseFrame( e.getBaseFrame()+frameOffset );
 		}
 	}
 	
 	public void update()
	{
 		if( ( ticks++ % (60 * 2) ) == 0 )
		{
			if( Math.random() > 0.3 )
			{
				spawn( Constants.SCREEN_WIDTH + 256 , 
					   Constants.SCREEN_HEIGHT - 64, Constants.SCROLL_SPEED * 1.4f, viewType );
			}
		}
		
 		super.update();	
 		
	}
 	
 	public boolean collideWith( AABB aabb,
 								Player player )
	{		
		return false;
	}

	public void setViewType(int viewType)
	{
		this.viewType = viewType;
	}
	
		
}
