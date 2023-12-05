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
import net.phatcode.rel.utils.Recycler;
import net.phatcode.rel.utils.SpriteBatcher;


public class EntityFactory
{
	protected Recycler<Entity> entities;
	protected ImageAtlas imageAtlas;
	protected SpriteBatcher spriteBatcher;
	
	public EntityFactory()
	{
		
	}
	
	public EntityFactory( int size )
	{
		
	}
	
	public boolean add( Entity e )
	{
		return entities.add( e );
	}
	
	public void remove( int index )
	{
		entities.remove( index );
	}
	
	public void clear()
	{
		entities.clear();
	}
	
	public int getSize()
	{
		return entities.getSize();
	}
	
	public void update()
	{
		int size = entities.getSize();
		for( int i = 0; i < size; i++ )
		{
			Entity e = entities.get(i);
			if( e.isActive() )
			{
				e.update();
			}
			else
			{
				entities.remove(i);
			}
				
		}
			
	}
	
	public void attachImageAtlas( ImageAtlas imageAtlas )
	{
		this.imageAtlas = imageAtlas;
	}
	
	public void attachSpriteBatcher( SpriteBatcher spriteBatcher )
	{
		this.spriteBatcher = spriteBatcher;
	}
	
	public void draw()
	{
		int size = entities.getSize();
		for( int i = 0; i < size; i++ )
		{
			Entity e = entities.get(i);
			e.render( imageAtlas, spriteBatcher );
				
		}
			
	}
	
	public void draw( ImageAtlas imageAtlas, SpriteBatcher spriteBatcher )
	{
		int size = entities.getSize();
		for( int i = 0; i < size; i++ )
		{
			Entity e = entities.get(i);
			e.render( imageAtlas, spriteBatcher );
				
		}
			
	}
	
	public void drawAabbs( LineBatcher lineBatcher, float r, float g, float b, float a )
	{
		
		int size = entities.getSize();
		for( int i = 0; i < size; i++ )
		{
			Entity e = entities.get(i);
			AABB hitBox = e.getCollisionBox();
			lineBatcher.box( hitBox.x1, hitBox.y1, 
					 hitBox.x1 + hitBox.width, hitBox.y1 + hitBox.height, 
					 r, g, b, a );

		}
		
	}
	
	public boolean collideWith( AABB aabb )
	{
		
		int size = entities.getSize();
		for( int i = 0; i < size; i++ )
		{
			Entity e = entities.get(i);
			if( e.collidesWith( aabb ) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	public int getCollisionIndex( AABB aabb )
	{
		
		int size = entities.getSize();
		for( int i = 0; i < size; i++ )
		{
			Entity e = entities.get(i);
			if( e.collidesWith( aabb ) )
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public void destroyAll()
	{
		int size = entities.getSize();
		for( int i = 0; i < size; i++ )
		{
			Entity e = entities.get(i);
			e.kill();	
		}
			
	}
	
	public void killAll()
	{
		int size = entities.getSize();
		for( int i = 0; i < size; i++ )
		{
			Entity e = entities.get(i);
			e.kill();
				
		}
			
	}
	
	public Entity getEntity( int index )
	{
		return entities.get( index );
	}

	public void spawn( float x, float y )
	{
		
	}

	public void spawn( float x, float y, float sx, float sy )
	{
		
	}

	public void spawn( float x, float y, float tx, float ty, float speed )
	{
		
	}
	
	public void spawn( float x, float y, float dx, float dy, 
					   float r, float g, float b,
					   int lifeSpan )
	{
		
	}
	
	public void spawnMulti( float x, float y, float dx, float dy, int i )
	{
		
	}

	public boolean collideWith( AABB collisionBox, EntityFactory moneyUpFactory,
								    Player player )
	{
		return false;
	}


	
	
}
