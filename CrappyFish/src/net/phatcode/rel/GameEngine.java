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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import net.phatcode.rel.Player.MyState;
import net.phatcode.rel.assets.ImageTextureDataDefault;
import net.phatcode.rel.math.Utils;
import net.phatcode.rel.utils.AABB;
import net.phatcode.rel.utils.AndroidFileIO;
import net.phatcode.rel.utils.ImageAtlas;
import net.phatcode.rel.utils.LineBatcher;
import net.phatcode.rel.utils.Sonics;
import net.phatcode.rel.utils.SpriteBatcher;
import net.phatcode.rel.utils.SpriteFont;
import net.phatcode.rel.utils.SpriteGL;
import net.phatcode.rel.utils.TouchHandler;
import android.content.Context;


public class GameEngine 
{

	enum GameState
	{
		TITLE,
		PLAY,
		LOOSE,
		CREDITS,
		SPLASH,
		GAME_OVER,
	}
	
	private Context context;
	
	private Player player = new Player();
	
	private SpriteBatcher spriteBatchObjects = new SpriteBatcher(1024);
	private SpriteBatcher spriteBatchBg = new SpriteBatcher(64);
	private SpriteBatcher spriteBatchLevel = new SpriteBatcher(16);
	
	private ImageAtlas objectImages = new ImageAtlas();
	private ImageAtlas levelImages = new ImageAtlas();
	private ImageAtlas scoreFontImages = new ImageAtlas();
	private ImageAtlas[] bgImages = new ImageAtlas[2];
	
	private SpriteFont kromFont = new SpriteFont();
	private SpriteFont geebeeFont = new SpriteFont();
	private SpriteFont smallFont = new SpriteFont();
	private SpriteFont scoreFont = new SpriteFont();
	
	
	private Scroller bgScroller = new Scroller( 32, Constants.SCROLL_SPEED,
												0, 512/32);
	
	private TouchHandler touchHandler = new TouchHandler();
	
	private AndroidFileIO fileIO;
	
	private long oldTimer;
	private long currentTime;
	private long logicFps = 0;
	private long fpsFrames = 0;
	
	int fps = 0;
	int framesPerSecond = 0;
	double previousTime = 0;
	double oldTime = 0;
	double secondsElapsed = 0;
	double dt = 0;
	
	private int logicFrames = 0;
	double accumulator = 0;
	
	StringBuilder textDisplayFPS = new StringBuilder(31);
	StringBuilder textLogicFPS = new StringBuilder(31);
	
	
	private GameState gameState = GameState.TITLE;
	private final int MAX_DELAY = 60 * 2;
	private int delayCounter = 0;
	
	private float interpolator;
	private float bgInterpolator;
	private float scoreInterpolator;
	private final int MAX_WAIT_TIME = 60 * 7;
	private int waitTime = MAX_WAIT_TIME;
	private boolean brokeRecord = false;
	
	private EntityFactory pipes = new PipeFactory(15);
	private EntityFactory bubbles = new BubbleFactory(63);
	private EntityFactory grasses = new GrassFactory(15);
	
	private List<EntityFactory> entityFactories = new ArrayList<EntityFactory>();
	
	private Menu mainMenuButtons = new Menu();
	
	
	private final static int MENU_PLAY = 0;
	private final static int MENU_CREDITS = 1;
	private final static int MENU_FACE_BOOK = 2;
	
	private int viewType = 0;
	private boolean blinkMe = true;
	
	private float scrollValue = Constants.SCREEN_HEIGHT;
	
	private LineBatcher lineBatcher = new LineBatcher(2048);
	
	public GameEngine( Context context )
	{
	
		this.context = context;
		
		fileIO = new AndroidFileIO( this.context );
		
		dt = getDeltaTime( Utils.getSystemSeconds() );
		
		bgImages[0] = new ImageAtlas();
		bgImages[1] = new ImageAtlas();
		
		Sonics.getInstance().loadMusic( context, R.raw.title );
		Sonics.getInstance().stopMusic();
		Sonics.getInstance().loadEffect( context, R.raw.bounce, 0 );
		Sonics.getInstance().loadEffect( context, R.raw.tap, 1 );
		Sonics.getInstance().loadEffect( context, R.raw.hit, 2 );
		Sonics.getInstance().loadEffect( context, R.raw.score, 3 );
		Sonics.getInstance().loadEffect( context, R.raw.point, 4 );
		
		// Attach both atlases and spritebatchers to factory
		pipes.attachImageAtlas(objectImages);
		pipes.attachSpriteBatcher(spriteBatchObjects);
		
		bubbles.attachImageAtlas(objectImages);
		bubbles.attachSpriteBatcher(spriteBatchObjects);
		
		grasses.attachImageAtlas(objectImages);
		grasses.attachSpriteBatcher(spriteBatchObjects);
		
		// Register all entity manager to array list so that
		// we can draw and update in a loop.
		// Also batches the virtual calls to limit cache misses
		entityFactories.add(pipes);
		entityFactories.add(bubbles);
		entityFactories.add(grasses);
		
				
		initialize();
		
		reset();
		
		gameState = GameState.SPLASH;
		
		InputStream inputStream = null;
		try
		{
			inputStream = fileIO.readFile("crappyhighscore.his");
			int highScore = inputStream.read(); 
			player.setHiScore(highScore);
			inputStream.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
				
	}
	
	private void initialize()
	{
		
		mainMenuButtons.attachImageAtlas( objectImages );
		mainMenuButtons.attachSpriteBatcher( spriteBatchObjects );
				
	}
	
	public void update()
	{

		getTouchInput();
		
		
		dt = getDeltaTime( Utils.getSystemSeconds() );
		if( dt > Constants.FIXED_TIME_STEP ) dt = Constants.FIXED_TIME_STEP;
		
		accumulator += dt;
		secondsElapsed += dt;
		
		while( accumulator >= Constants.FIXED_TIME_STEP )
		{
			fpsFrames++;
			updateAll();
			accumulator -= Constants.FIXED_TIME_STEP;
			
		}
		
		currentTime = System.nanoTime();
		if( currentTime - oldTimer >= 1000000000 )
		{
			logicFps = (fpsFrames * 1000000000)/(currentTime - oldTimer);
			oldTimer = currentTime;
			fpsFrames = 0;
		}
		
		

	}
	
	private void reset()
	{
		brokeRecord = false;
				
		viewType = (++viewType) & 1;
		((GrassFactory)grasses).setViewType(viewType);
		
		interpolator = 0;
		bgInterpolator = 0;
		scoreInterpolator = 0;
		
		waitTime = MAX_WAIT_TIME;
		
		delayCounter = MAX_DELAY;

		player.reset();
		
		int size = entityFactories.size();
		for( int i = 0; i < size; i++ )
		{
			entityFactories.get(i).clear();
		}
	
		mainMenuButtons.clear();
		
		scrollValue = Constants.SCREEN_HEIGHT;
		
		gameState = GameState.TITLE;
		
		
	}
	
	private void getTouchInput()
	{
		switch( gameState )
		{
			case TITLE:
				processTitle( touchHandler );
				break;
			case PLAY:
				if( touchHandler.isTouchedDown() )
				{
					if( player.isInPlay() )  
					{
						player.flap();
						Sonics.getInstance().playEffect( 1 );
					}
			
				}
				break;
			case LOOSE:
				break;
			case CREDITS:
				if( touchHandler.isTouchedDown() )
				{
					interpolator = 0;
					spawnMainMenuButtons();
					player.reset();
					gameState = GameState.TITLE;
					Sonics.getInstance().stopMusic();
					Sonics.getInstance().loadMusic( context, R.raw.title );
				}
				break;
			case SPLASH:
				break;
			case GAME_OVER:
				if( touchHandler.isTouchedDown() )
				{
					interpolator = 0;
					gameState = GameState.TITLE;
					Sonics.getInstance().stopMusic();
					Sonics.getInstance().loadMusic( context, R.raw.title );
					reset();
					spawnMainMenuButtons();
				}
				break;
			default:
				break;
		}
		
			
	}
	
	private void processTitle( TouchHandler touchHandler )
	{
		if( touchHandler.isTouchedDown() )
		{
			AABB aabb = new AABB(touchHandler.getTouchX(), touchHandler.getTouchY(), 1, 1);
			int choice = mainMenuButtons.getCollision(aabb);
			if( choice > -1 )
			{
				switch( choice ) 
				{
					case MENU_PLAY:
						mainMenuButtons.clear();
						waitTime = MAX_WAIT_TIME;						
						delayCounter = MAX_DELAY;
						gameState = GameState.PLAY;
						player.setState( MyState.PLAY );
						Sonics.getInstance().stopMusic();
						Sonics.getInstance().loadMusic( context, R.raw.level );
						player.flap();
						Sonics.getInstance().playEffect( 1 );
						break;
					case MENU_CREDITS:
						mainMenuButtons.clear();
						Sonics.getInstance().stopMusic();
						Sonics.getInstance().loadMusic( context, R.raw.intermission );
						waitTime = MAX_WAIT_TIME;						
						delayCounter = MAX_DELAY;
						player.reset();
						scrollValue = Constants.SCREEN_HEIGHT;
						gameState = GameState.CREDITS;
						break;
					case MENU_FACE_BOOK:
						//reset();
						//gameState = GameState.TITLE;
						break; 
				}
				
			}
		}
		
	}
	
	private void handleCollisions()
	{
	
		if( player.getState() == MyState.PLAY )
		{
			int collision = ((PipeFactory)pipes).collideWith( player );
			if( collision > 0 )
			{
				switch( collision )
				{
					case 1:
						Sonics.getInstance().playEffect( 4 );
						break;
					case 2:
						Sonics.getInstance().playEffect( 2 );
						player.kill();
						break;
					default:
						
				}
			}
		}
	}
	
	private void stateTitle()
	{
		interpolator = Utils.clamp( interpolator+=0.01, 0.0f, 1.0f );
		bgInterpolator = Utils.clamp( bgInterpolator+=0.01, 0.0f, 1.0f );
		
		mainMenuButtons.update();
	}
	
	private void statePlay()
	{
		scoreInterpolator = Utils.clamp( scoreInterpolator+=0.01, 0.0f, 1.0f );
		BubbleFactory b = (BubbleFactory)bubbles;
		b.setPlayerX( player.getX() );
		b.setPlayerY( player.getY() );
		
		int size = entityFactories.size();
		for( int i = 0; i < size; i++ )
		{
			entityFactories.get(i).update();
		}
		
		handleCollisions();
		
		if( player.isKilled() && (gameState != GameState.LOOSE) )
		{
			gameState = GameState.LOOSE;
		}
	}
	
	private void stateLoose()
	{
		
		scoreInterpolator = Utils.clamp( scoreInterpolator+=0.01, 0.0f, 1.0f );
		BubbleFactory b = (BubbleFactory)bubbles;
		b.setPlayerX( player.getX() );
		b.setPlayerY( player.getY() );
		
		int size = entityFactories.size();
		for( int i = 0; i < size; i++ )
		{
			entityFactories.get(i).update();
		}
		
		delayCounter--;
		if( delayCounter < 0 )
		{
			gameState = GameState.GAME_OVER;
			player.setState(Player.MyState.IDLE);
			player.setX(-200);
			interpolator = 0;
			if( player.getScore() > player.getHiScore() ) 
			{
				brokeRecord = true;
				player.setHiScore(player.getScore());
				OutputStream outputStream = null;
				try
				{
					outputStream = fileIO.writeFile("crappyhighscore.his");
					outputStream.write(player.getHiScore()); 
					outputStream.close();
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
				

			}
		}
		
	}
	
	private void stateCredits()
	{
		scrollValue -= 1.5f;
		if( scrollValue < -1300 )
		{
			
			Sonics.getInstance().stopMusic();
			Sonics.getInstance().loadMusic( context, R.raw.title );
			interpolator = 0;
			spawnMainMenuButtons();
			player.reset();
			gameState = GameState.TITLE;
		}
	}
	
	private void stateSplash()
	{
		
		if( --waitTime < 0 )
		{
			reset();
			spawnMainMenuButtons();
			Sonics.getInstance().stopMusic();
			Sonics.getInstance().loadMusic( context, R.raw.title );
		}
		if( waitTime > MAX_WAIT_TIME/2)
		{
			interpolator = Utils.clamp( interpolator+=0.01, 0.0f, 1.0f );
		}
		else
		{
			interpolator = Utils.clamp( interpolator-=0.01, 0.0f, 1.0f );
		}
		
	}
	
	private void stateGameOver()
	{
		interpolator = Utils.clamp( interpolator+=0.02, 0.0f, 1.0f );
	}

	private void updateAll()
	{
		
		
		logicFrames++;
		
		if( (logicFrames & 31) == 0 ) blinkMe = !blinkMe;
		
		bgScroller.update();	
		
		player.update();
		
		
		switch( gameState )
		{
			case TITLE:
				stateTitle();
				break;
			case PLAY:
				statePlay();
				break;
			case LOOSE:
				stateLoose();
				break;
			case CREDITS:
				stateCredits();
				break;
			case SPLASH:
				stateSplash();
				break;
			case GAME_OVER:
				stateGameOver();
				break;
			default:
				break;
		}
		
		
	}

	public void renderBatchers( GL10 gl )
	{
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glColor4f(1, 1, 1, 1);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef( 0.375f, 0.375f, 0 );	// magic trick
		

		renderAll();
		
		
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		spriteBatchBg.render( gl, bgImages[viewType].getTextureID() );
		spriteBatchLevel.render( gl, levelImages.getTextureID() );
		spriteBatchObjects.render( gl, objectImages.getTextureID() );
		
		
		kromFont.render( gl );
		geebeeFont.render( gl );
		smallFont.render( gl );
		
		scoreFont.render( gl );
		
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		lineBatcher.render(gl);
			
	}
	
	private void renderScrollers()
	{
		
		float alpha = Utils.lerpSmooth(0.0f, 1.0f, Utils.smoothStep(bgInterpolator));
		
		bgScroller.render( 0,  
			  			   Constants.SCREEN_WIDTH / bgScroller.getTileSize() + bgScroller.getTileSize(), 
			  			   1, 1, 1, alpha,
			  			   bgImages[viewType],
			  			   spriteBatchBg );
		
		
		int index = 4 + viewType;
		
		spriteBatchLevel.spriteOnBoxOffset( 0, 
											Constants.SCREEN_HEIGHT - levelImages.getSprite(index).height + 8, 
											Constants.SCREEN_WIDTH, levelImages.getSprite(index).height, 
								       		1, 1, 1, alpha,
								       		logicFrames * Constants.SCROLL_SPEED/(float)(Constants.SCREEN_WIDTH*4),
								       		0,
								       		1,
								       		levelImages.getSprite(index).height/(float)levelImages.getHeight(),
								       		SpriteGL.FLIP_NONE, levelImages.getSprite(index) );
		
		index = 2 + viewType;
		spriteBatchLevel.spriteOnBoxOffset( 0, 
											Constants.SCREEN_HEIGHT - levelImages.getSprite(index).height, 
											Constants.SCREEN_WIDTH, levelImages.getSprite(index).height, 
								       		1, 1, 1, alpha,
								       		logicFrames * Constants.SCROLL_SPEED/(float)(Constants.SCREEN_WIDTH*2),
								       		0,
								       		1,
								       		levelImages.getSprite(index).height/(float)levelImages.getHeight(),
								       		SpriteGL.FLIP_NONE, levelImages.getSprite(index) );

		index = 0 + viewType;
		spriteBatchLevel.spriteOnBoxOffset( 0, 
											Constants.SCREEN_HEIGHT - levelImages.getSprite(index).height + 4, 
											Constants.SCREEN_WIDTH, levelImages.getSprite(index).height, 
								       		1, 1, 1, alpha,
								       		logicFrames * Constants.SCROLL_SPEED/(float)Constants.SCREEN_WIDTH,
								       		0,
								       		1,
								       		levelImages.getSprite(index).height/(float)levelImages.getHeight(),
								       		SpriteGL.FLIP_NONE, levelImages.getSprite(index) );

	}
	
	private void renderEntities()
	{
		
		int size = entityFactories.size();
		for( int i = 0; i < size; i++ )
		{
			entityFactories.get(i).draw();
			//entityFactories.get(i).drawAabbs(lineBatcher, 1, 0, 0, 1);
		}
		
		player.render( objectImages, spriteBatchObjects );
		//player.drawAabb( lineBatcher, 1, 1, 0, 1 );			
	}
	
	private void renderHud()
	{
		textDisplayFPS.delete(0, 31);
		textDisplayFPS.append( "GAMEFPS = " );
		textDisplayFPS.append( fps );
		
		textLogicFPS.delete(0, 31);
		textLogicFPS.append( "LOGICFPS = " );
		textLogicFPS.append( logicFps );
		
		textLogicFPS.append( " " );
		
		
		scoreFont.printCenter( 0, (int)Utils.lerpSmooth(-300.0f, 50.0f, Utils.smoothStep(scoreInterpolator)), 
						       Constants.SCREEN_WIDTH, 
						       "" + player.getScore() );
		
		//kromFont.print( (int)Utils.lerpSmooth(-800.0f, 0.0f, Utils.smoothStep(interpolator)), 0, 
		// 		        "HIGH:" + Utils.int2Score(player.getHiScore(), "0000"), 1, 0.5f, 0.5f, 1 );

		/*
		smallFont.print( 0,  
                         300, 
                        "BUBBLES = " + bubbles.getSize() );
		
		smallFont.print( 0,  
                		 320, 
                		 "GRASSES = " + grasses.getSize() );
		 */
	}
	
	private void renderTitle()
	{
	
		
		renderScrollers();
		renderEntities();
		renderHud();
		
		spriteBatchObjects.spriteRotateScale( Constants.SCREEN_WIDTH/2, 
											  (int)Utils.lerpSmooth(800.0f, Constants.SCREEN_HEIGHT/5, Utils.smoothStep(interpolator)),
											  1, 1, 1, 1,
											  0, 
											  0.5f + (float)Math.abs(Math.sin(logicFrames/40.0)/2.0f), 
											  0.5f + (float)Math.abs(Math.sin(logicFrames/20.0)/2.0f),
											  SpriteGL.FLIP_NONE, objectImages.getSprite(244) );

		kromFont.printCenter( 0,  (int)Utils.lerpSmooth(-300.0f, 464.0f, Utils.smoothStep(interpolator)), 
						      Constants.SCREEN_WIDTH,
							  "VERSION 0.5.0" );
		
		mainMenuButtons.draw();
		//mainMenuButtons.drawAabbs(lineBatcher, 1, 0, 0, 1);
	

	}
	
	private void renderPlay()
	{
		
		renderScrollers();
		renderEntities();
		renderHud();
		
	}
	
	private void renderLoose()
	{
		
		renderScrollers();
		renderEntities();
		renderHud();
		
	}
	
	private void renderCredits()
	{
		String[] items = { "CODE:",
				   "",
				   "RICHARD ERIC M. LOPE",
				   "",
				   "",
				   "DESIGN:",
				   "",
				   "ANYA THERESE B. LOPE",
				   "",
				   "",
				   "GFX AND AUDIO:",
				   "",
				   "ENTWICKLER X (FLAPPY FISH GAME GRAPHICS)",
				   "MARC RUSSELL (FONTS)",
				   "JAROD NEY (FLAPPY BIRD APK)",
				   "RICHARD ERIC M. LOPE",
				   "",
				   "",
				   "GREETZ:",
				   "",
				   "VINVIN SAGUN",
				   "JOHN CARLO FRANCO",
				   "VRIGZ ALEJO",
				   "JIN DE",
				   "RUEL RULE",
				   "RAMON LANSANGAN",
				   "ROMMEL DE TORRES",
				   "",
				   "DOC D",
				   "RYAN LLOYD",
				   "V1CTOR",
				   "PLASMA",
				   "JOFERS",
				   "DAV",
				   "PIPTOL",
				   "",
				   "MAKIT LOPE ABEJUELA",
				   "XTINA LOPE SENOSA",
				   "CHIVAS BALLANTINES AKA RICH ABEJUELA",
				   "STANLEY SENOSA",
				   "PETER LOPE",
				   "LILY LOPE",
				   "CJ L. SENOSA",
				   "",
				   "DARKSNIPER GUNNER",
				   "ONII LAX",
				   "ROLLY CALMA",
				   "PAUL EFRAIM",
				   "MAQLIT",
				   "NICOL GADINGAN",
				   "ZUZAMEN ESCASENAS",
				   "JOSE MARIE DIAZ",
				   "GAARA",
				   "JOHN JOHN",
				   "TROJAN KIER",
				   "",
				   "PROGRAMMERS, DEVELOPERS GROUP",
				   "PINOY PROGRAMMERS LEAGUE GROUP",
				   "ANIME ZONE GROUP",
				   "HD MOVIES GROUP",
				   "FACEBOOK DRIVE GROUP",
				   "",
				   "REL.PHATCODE.NET",
			  };


		renderScrollers();
		for( int i = 0; i < items.length; i++ )
		{
			if( items[i].contains(":"))
			{
				kromFont.printCenter( 0,  
		                              (int)(scrollValue + i * 20), 
		                              Constants.SCREEN_WIDTH,
		  				              items[i] );
			}
			else
			{
				geebeeFont.printCenter( 0,  
						                (int)(scrollValue + i * 20), 
						                Constants.SCREEN_WIDTH,
						  				items[i] );
			}
		}
		
		//smallFont.print( 0,  
         //                0, 
         //                "SCROLL = " + scrollValue );
		
	}
	
	private void renderGameOver()
	{
		renderScrollers();
		
		spriteBatchObjects.spriteStretch( 10, (int)Utils.lerpSmooth(-90, 50, Utils.smoothStep(interpolator)), 
										  300, (int)Utils.lerpSmooth(32, 420, Utils.smoothStep(interpolator)),
										  1, 1, 1, 1,  
										  objectImages.getSprite(9) );

		kromFont.printCenter( 0, 
							  (int)Utils.lerpSmooth(-90, 140, Utils.smoothStep(interpolator)), 
							  Constants.SCREEN_WIDTH, 
							  "HI SCORE", 1,0,0,1 );	
		
		scoreFont.printCenter( 0, 
							  (int)Utils.lerpSmooth(-90, 170, Utils.smoothStep(interpolator)), 
							  Constants.SCREEN_WIDTH, 
							  "" + player.getHiScore(), 0,1,1,1 );	

		kromFont.printCenter( 0, 
							  (int)Utils.lerpSmooth(-90, 230, Utils.smoothStep(interpolator)), 
							  Constants.SCREEN_WIDTH, 
							  "YOUR SCORE", 1,0,0,1 );	

		scoreFont.printCenter( 0, 
							   (int)Utils.lerpSmooth(-90, 260, Utils.smoothStep(interpolator)), 
							   Constants.SCREEN_WIDTH, 
							   "" + player.getScore(), 0,1,1,1 );	
		
		
		if( blinkMe )
		{
			geebeeFont.printCenter( 0, 
								   (int)Utils.lerpSmooth(-90, 430, Utils.smoothStep(interpolator)), 
								   Constants.SCREEN_WIDTH, 
								   "TAP THE SCREEN TO GO BACK" );	
		}
		
		if( player.getScore() > 9  )
		{
			drawMedals( (int)Utils.lerpSmooth(-90, 350, Utils.smoothStep(interpolator)) );
			
			if( brokeRecord )
			{
				scoreFont.printCenterSine( 0, 
										   (int)Utils.lerpSmooth(-90, 80, Utils.smoothStep(interpolator)), 
										   Constants.SCREEN_WIDTH, 
										   10, 1, (logicFrames*4), "NEW RECORD!" );	

				kromFont.printCenter( 0, 
									  (int)Utils.lerpSmooth(-90, 320, Utils.smoothStep(interpolator)), 
									  Constants.SCREEN_WIDTH, 
									  "MAY MEDALS KA!", 1,0,1,1 );	
			}
			else
			{
				scoreFont.printCenterSine( 0, 
										   (int)Utils.lerpSmooth(-90, 80, Utils.smoothStep(interpolator)), 
										   Constants.SCREEN_WIDTH, 
										   10, 1, (logicFrames*4), "GAME OVER" );	

				kromFont.printCenter( 0, 
									  (int)Utils.lerpSmooth(-90, 320, Utils.smoothStep(interpolator)), 
									  Constants.SCREEN_WIDTH, 
									  "MAY MEDALS KA!", 1,0,1,1 );	
		}

		}
		else
		{

			if( brokeRecord )
			{
				scoreFont.printCenterSine( 0, 
										   (int)Utils.lerpSmooth(-90, 80, Utils.smoothStep(interpolator)), 
										   Constants.SCREEN_WIDTH, 
										   10, 1, (logicFrames*4), "NEW RECORD!" );
				kromFont.printCenter( 0, 
						  			 (int)Utils.lerpSmooth(-90, 320, Utils.smoothStep(interpolator)), 
						  			 Constants.SCREEN_WIDTH, 
						  			 "NO MEDALS", 1,0,1,1 );

			}
			else
			{
				scoreFont.printCenterSine( 0, 
										   (int)Utils.lerpSmooth(-90, 80, Utils.smoothStep(interpolator)), 
										   Constants.SCREEN_WIDTH, 
										   10, 1, (logicFrames*4), "GAME OVER" );	

				kromFont.printCenter( 0, 
						  			 (int)Utils.lerpSmooth(-90, 320, Utils.smoothStep(interpolator)), 
						  			 Constants.SCREEN_WIDTH, 
						  			 "KAPE PA!", 1,0,1,1 );	
			}

		}
		

		
	}
	
	private void renderSplash()
	{

		float angle = Utils.lerpSmooth(Utils.PI * 10, 0, Utils.smoothStep(interpolator));
		float scale = Utils.lerpSmooth(0.0f, 1.0f, Utils.smoothStep(interpolator));
		float alpha = Utils.lerpSmooth(0.0f, 1.0f, Utils.smoothStep(interpolator));
		
		spriteBatchObjects.spriteRotateScale( Constants.SCREEN_WIDTH/2, Constants.SCREEN_HEIGHT/2 - Constants.SCREEN_HEIGHT/4,
											  1, 1, 1, alpha,
											  0, scale, scale,
							     			  SpriteGL.FLIP_NONE, objectImages.getSprite(234) );

		spriteBatchObjects.spriteRotateScale( Constants.SCREEN_WIDTH/2, Constants.SCREEN_HEIGHT/2 + Constants.SCREEN_HEIGHT/4,
											 1, 1, 1, alpha,
							     			 angle, scale, scale,
							      			 SpriteGL.FLIP_NONE, objectImages.getSprite(235) );

	}

	private void renderAll()
	{
		
		
		switch( gameState )
		{
			case TITLE:
				renderTitle();
				break;
			case PLAY:
				renderPlay();
				break;
			case LOOSE:
				renderLoose();
				break;
			case CREDITS:
				renderCredits();
				break;
			case SPLASH:
				renderSplash();
				break;
			case GAME_OVER:
				renderGameOver();
				break;
			default:
				break;
		}
		
	}
	
	public void loadTextures( GL10 gl )
	{
		
		objectImages.loadTexture( gl, fileIO, "gfx/objects.png",
				  				  new ImageTextureDataObjectImages(),
				  				  GL10.GL_LINEAR );

		levelImages.loadTexture( gl, fileIO, "gfx/level.png",
								 new ImageTextureDataLevelImages(),
								 GL10.GL_LINEAR );
		bgImages[0].loadTexture( gl, fileIO, "gfx/background01.png",
							  new ImageTextureDataDefault(),
					  	  	  32,
					  	  	  512,
					  	  	  GL10.GL_LINEAR );
		bgImages[1].loadTexture( gl, fileIO, "gfx/background02.png",
							   new ImageTextureDataDefault(),
					  	  	   32,
					  	  	   512,
					  	  	   GL10.GL_LINEAR );

		scoreFontImages.loadTexture( gl, fileIO, "gfx/scorefont.png",
									 new ImageTextureDataDefault(),
							  	  	 24,
							  	  	 32,
							  	  	 GL10.GL_NEAREST );

		kromFont.loadAtlas( objectImages, 99 );
		geebeeFont.loadAtlas( objectImages, 20 );
		smallFont.loadAtlas( objectImages, 158 );
		scoreFont.loadAtlas( scoreFontImages );
		

	}
	
	
	public void shutDown( GL10 gl )
	{
		
		Sonics.getInstance().shutDown();
		
		objectImages.shutDown(gl);
		bgImages[0].shutDown(gl);
		bgImages[1].shutDown(gl);
		levelImages.shutDown(gl);
		scoreFontImages.shutDown(gl);
		
		kromFont.shutDown(gl);
		geebeeFont.shutDown(gl);
		smallFont.shutDown(gl);
		scoreFont.shutDown(gl);
	}
	
	
	private double getDeltaTime( double timerInSeconds )
	{
		double currentTime = timerInSeconds;
		double elapsedTime = currentTime - oldTime;
		oldTime = currentTime;
		
		framesPerSecond++;
		
		if( (currentTime - previousTime) > 1.0 )
		{
			previousTime = currentTime;
			fps = framesPerSecond;
			framesPerSecond = 0;
		}
		
		return elapsedTime;
	}
	
	public TouchHandler getTouchHandler()
	{
		return touchHandler;
	}
	
	void spawnMainMenuButtons()
	{
		int[][] mainMenuCoords = { { (int)(Constants.SCREEN_WIDTH*0.50f), 310 },
								   { (int)(Constants.SCREEN_WIDTH*0.74f), 370 },
								   { (int)(Constants.SCREEN_WIDTH*0.80f), 430 },			
							      };

		for( int i = 0; i < 3; i++ )
		{
			mainMenuButtons.spawnButton( mainMenuCoords[i][0], mainMenuCoords[i][1],  
								         500, -50,
								         160, 45,
								         20, 1,
								         false );
		}
		
		mainMenuButtons.getButton(0).setBaseFrame(19);  // play
		mainMenuButtons.getButton(0).setSize(160,45);
		mainMenuButtons.getButton(0).setAnimated(true);
		
		mainMenuButtons.getButton(1).setBaseFrame(16);  // credits
		mainMenuButtons.getButton(1).setSize(160,45);
		
		mainMenuButtons.getButton(2).setBaseFrame(14);  // fb
		mainMenuButtons.getButton(2).setSize(130,60);
		
		
		
	}
	
	public void drawMedals( int y )
	{
		int numMedals = player.getScore();
		int smallMedals = numMedals % 10;
		int bigMedals = numMedals / 10;
		
		int currentRow = y + 16;
		int x = ( Constants.SCREEN_WIDTH - (bigMedals * (43)) )/2 + 21;
		
		float r = (float)Math.abs(0.5 + Math.sin(logicFrames/ 5.0)*0.5 );
		float g = (float)Math.abs(0.5 + Math.sin(logicFrames/10.0)*0.5 );
		float b = (float)Math.abs(0.5 + Math.sin(logicFrames/15.0)*0.5 );
		if( bigMedals < 6 )
		{
			for( int i = 0; i < bigMedals; i++ )
			{
				spriteBatchObjects.spriteRotateScale( x + i * 43, 
													  currentRow,
													  r, g, b, 1,
													  0, 1, 1,
									     			  SpriteGL.FLIP_NONE, objectImages.getSprite(245) );
		
			}
		}
		else
		{
			spriteBatchObjects.spriteRotateScale( Constants.SCREEN_WIDTH/2, 
												  currentRow,
												  r, g, b, 1,
												  0, 1, 1,
								     			  SpriteGL.FLIP_NONE, objectImages.getSprite(245) );
			kromFont.printCenter( 0, currentRow-8, Constants.SCREEN_WIDTH, "" + bigMedals );
		}
		
		x = ( Constants.SCREEN_WIDTH - (smallMedals * 32) )/2 + 16;
		float scale = 0.5f + (float)Math.abs(Math.sin(logicFrames/10.0)) * 0.5f;
		float rotation = (float)Math.sin(logicFrames/15.0)/2.0f;
		for( int i = 0; i < smallMedals; i++ )
		{
			spriteBatchObjects.spriteRotateScale( x + i * 32, 
												  currentRow + 44,
												  1, 1, 1, 1,
												  rotation + i, scale, scale,
								     			  SpriteGL.FLIP_NONE, objectImages.getSprite(5) );
	
		}
	
		
	}
	
	
}
