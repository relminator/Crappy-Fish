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

import net.phatcode.rel.Pipe.ID;
import net.phatcode.rel.utils.AABB;
import net.phatcode.rel.utils.LineBatcher;
import net.phatcode.rel.utils.Recycler;

public class PipeFactory extends EntityFactory
{

	int ticks = 0;
	private Pipe.ID id = ID.NORMAL;
	
 	public PipeFactory( int size )
 	{
 		entities = new Recycler<Entity>( size, new Pipe() );
 		// need to do this for recycler to work
 		for( int i = 0; i < size; i++ )
 		{
 			entities.add( new Pipe() );  // fill with unique references
 		}
 		entities.clear();  // reset size to 0 so that we could spawn
 	}
 	
 	public void spawn( float x, float y, float speed )
 	{
 		Pipe e = (Pipe)entities.getFreeElement();
 		if( e != null )
 		{
 			e.spawn( x, y, speed, id  );
 		}
 	}
 	
 	public void update()
	{
 		if( (ticks++ % (60*2)) == 0 )
 		{
 			if( Math.random() > 0.2 )
			{
 				id = Pipe.ID.NORMAL;
	 			spawn( Constants.SCREEN_WIDTH + 40, 
	 				   Constants.SCREEN_HEIGHT/2 + (-80 + (float)Math.random() * 80 ),
	 				   Constants.SCROLL_SPEED );
			}
 			else
 			{
 				id = Pipe.ID.WAVER;
	 			spawn( Constants.SCREEN_WIDTH + 40, 
	 				   Constants.SCREEN_HEIGHT/2,
	 				   Constants.SCROLL_SPEED );
 			}
 		}
 		super.update();	
	}
 	
 	public int collideWith( Player player )
	{
		int size = entities.getSize();
		for( int i = 0; i < size; i++ )
		{
			Pipe e = (Pipe)entities.get(i);
			int retVal = e.collidesWith( player );
			switch( retVal )
			{
				case 1:
					e.destroy();
					return 1;
				case 2:
					return 2;
				default:
					
			}
		}
		
		return 0;
	}
	
 	public void drawAabbs( LineBatcher lineBatcher, float r, float g, float b, float a )
	{
		
		int size = entities.getSize();
		for( int i = 0; i < size; i++ )
		{
			Pipe e = (Pipe)entities.get(i);
			AABB hitBox = e.getCollisionBox();
			lineBatcher.box( hitBox.x1, hitBox.y1, 
					 hitBox.x1 + hitBox.width, hitBox.y1 + hitBox.height, 
					 r, g, b, a );
			
			hitBox = e.getCollisionBoxTop();
			lineBatcher.box( hitBox.x1, hitBox.y1, 
					 hitBox.x1 + hitBox.width, hitBox.y1 + hitBox.height, 
					 1, 1, 1, a );

			hitBox = e.getCollisionBoxBottom();
			lineBatcher.box( hitBox.x1, hitBox.y1, 
					 hitBox.x1 + hitBox.width, hitBox.y1 + hitBox.height, 
					 0, 1, 0, a );

		}
		
	}
	
}
