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


public class Scroller
{

	private float speed = Constants.SCROLL_SPEED;
	private float scrollPosition = 0;
	private int tileSize =  32;
	private int tileOffset = 0;
	private int pixelOffset = 0;
	private int startIndex = 0;
	private int numImages = 1;
	
	public Scroller( int tileSize, float speed, 
					 int startIndex, int numImages )
	{
		this.tileSize = tileSize;
		this.speed = speed;
		this.startIndex = startIndex;
		this.numImages = numImages;
		
		scrollPosition = 0;
	}
	
	public void update()
	{
		scrollPosition += speed;
		pixelOffset = (int)scrollPosition;
		if( pixelOffset > (tileSize) ) 
		{
			pixelOffset = pixelOffset % tileSize;
			tileOffset++; 
			scrollPosition = 0;
		}
	}
	
	public void render( int y, int numTiles, ImageAtlas imageAtlas, SpriteBatcher spriteBatcher )
	{
	

			
		for( int i = 0; i < numTiles; i++ )
		{
			int index = (tileOffset + i) % numImages;
			spriteBatcher.sprite( -tileSize + (i * tileSize) - pixelOffset, y,
		 				  		  SpriteGL.FLIP_NONE, imageAtlas.getSprite(index + startIndex) );	
		}
		
	}

	public void render( int y, int numTiles, 
						float r, float g, float b, float a,
						ImageAtlas imageAtlas, SpriteBatcher spriteBatcher )
	{
	

			
		for( int i = 0; i < numTiles; i++ )
		{
			int index = (tileOffset + i) % numImages;
			spriteBatcher.sprite( -tileSize + (i * tileSize) - pixelOffset, y,
								  r, g, b, a,
		 				  		  SpriteGL.FLIP_NONE, imageAtlas.getSprite(index + startIndex) );	
		}
		
	}

	public void setSpeed(float speed)
	{
		this.speed = speed;
	}

	public void setTileSize(int tileSize)
	{
		this.tileSize = tileSize;
	}

	public int getTileSize()
	{
		return tileSize;
	}
	
	
}
