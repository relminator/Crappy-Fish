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
import net.phatcode.rel.utils.LineBatcher;
import net.phatcode.rel.utils.Sonics;
import net.phatcode.rel.utils.SpriteBatcher;
import net.phatcode.rel.utils.SpriteGL;


public class Player extends Entity
{

	enum MyState
	{
		IDLE,
		PLAY,
		DIE,
	}

	private int screenX;
	private int screenY;
	
	private MyState state = MyState.IDLE;
	
	private int score = 0;
	private int hiScore = 0;
	private boolean killed = false;
	private float interpolator = 0;
	
	public Player()
	{
		baseFrame = 1;
		numFrames = 2;
		x = -200;
		y = Constants.SCREEN_HEIGHT/2;
		
		dy = 0;
		dx = 0;
		
		interpolator = 0;
		
	}
	
	@Override
	public void update()
	{
		
		switch( state )
		{
			case IDLE:   // walang ginagawa so fly lang with a little wave motion
				interpolator = Utils.clamp( interpolator+=0.01f, 0.0f, 1.0f );
				dy = (float)Math.sin(ticks/10.f) * 1.5f;
				y += dy;
				x = Utils.lerpSmooth(-200, 100.0f, Utils.smoothStep(interpolator));
				break;
				
			case PLAY:
				
				if(y > Constants.SCREEN_HEIGHT - 32)  // kill player when it hits the floor
				{
					kill();
				}
				
				if(y < 10)  // limit flight above the water
				{
					dy = 0;
				}
				
				// move the bird
				dy += Constants.GRAVITY;
				y += dy;
				break;
			
			case DIE:
				dy += Constants.GRAVITY;
				y += dy;
				if(y > Constants.SCREEN_HEIGHT - 32)  // bounce player when it hits the floor
				{
					dy = -Constants.FLAP_HEIGHT;
					Sonics.getInstance().playEffect(0);
				}
				break;
			
		}
		
		
		if( (ticks++ % 5) == 0 )
		{
			frame = (frame + 1) & 1;
		}
		
		screenX = (int)x - width/2;
		screenY = (int)y - height/2;
		
		// update box collision values
		collisionBox.init(screenX, screenY, width, height);
		collisionBox.resize(0.9f);
	}
	
	
	
	@Override
	public void render( ImageAtlas imageAtlas, SpriteBatcher spriteBatcher )
	{
		
		float angle = 0;
		switch( state )
		{
			case IDLE:
			case PLAY:
				angle = (float)Math.atan2(dy, 10);   // make a direction vector and get the angle
				break;
			
			case DIE:
				angle = ticks/3.0f;
				break;
			
		}
		
		spriteBatcher.spriteRotateScale( x, y, 
									     angle, 1, 1, 
									     SpriteGL.FLIP_NONE, 
									     imageAtlas.getSprite(baseFrame + frame) );
		
	}

	public void drawAabb( LineBatcher lineBatcher, float r, float g, float b, float a )
	{
		AABB hitBox = getCollisionBox();
		lineBatcher.box( hitBox.x1, hitBox.y1, 
				 hitBox.x1 + hitBox.width, hitBox.y1 + hitBox.height, 
				 r, g, b, a );
	
	}
	
	public void flap()
	{
		dy = -Constants.FLAP_HEIGHT;
	}
	
	public boolean collidesWith(Entity e)
	{
		if (collisionBox.intersects(e.getCollisionBox()))
		{
			return true;
		}
		return false;
	}
	
	public void reset()
	{
		ticks = 0;
		state = MyState.IDLE;
		killed = false;
		x = -200;
		y = Constants.SCREEN_HEIGHT/2;
		score = 0;
		interpolator = 0;
		
		if( Math.random() > 0.5 )
		{
			baseFrame = 3;
		}
		else
		{
			baseFrame = 1;
		}
		
	}
	
	public void kill()
	{
		state = MyState.DIE;
		killed = true;
	}

	public MyState getState()
	{
		return state;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public void addToScore( int score )
	{
		this.score += score; 
	}

	public boolean isKilled()
	{
		return killed;
	}

	public void setState(MyState state)
	{
		this.state = state;
	}

	public boolean isInPlay()
	{
		return state == MyState.PLAY;
	}

	public int getHiScore()
	{
		return hiScore;
	}

	public void setHiScore(int hiScore)
	{
		this.hiScore = hiScore;
	}

}
