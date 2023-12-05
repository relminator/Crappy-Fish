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

import net.phatcode.rel.utils.Recycler;



public class ButtonFactory extends EntityFactory
{
	public ButtonFactory( int size )
	{
		entities = new Recycler<Entity>( size, new Button() );
		// need to do this for recycler to work
		for( int i = 0; i < size; i++ )
		{
			entities.add( new Button() );  // fill with unique references
		}
		entities.clear();  // reset size to 0 so that we could spawn
	}
	
	public void spawn( float x, float y, float sx, float sy  )
	{
		Button e = (Button)entities.getFreeElement();
		if( e != null )
		{
			e.spawn( x, y, sx, sy );
		}
	}

}