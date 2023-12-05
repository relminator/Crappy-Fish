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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.phatcode.rel.utils.VirtualViewport;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;




public class GameSurfaceView extends GLSurfaceView implements Renderer
{

	private Context context;
	
	private GameEngine game;
	
	private GL10 gfxContext;
	
	private VirtualViewport virtualViewport = new VirtualViewport();
	
	public GameSurfaceView( Context context )
	{
		super(context);
		
		this.setRenderer(this);
		this.requestFocus();
		this.setFocusableInTouchMode(true);
		
		this.context = context;
		
		game = new GameEngine(this.context);
		
		this.setOnTouchListener( game.getTouchHandler() );
		
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{

		game.update();
		
		game.renderBatchers( gl );
		
	}

	@Override
	public void onSurfaceChanged( GL10 gl, int width, int height )
	{
		if(height == 0) 
		{ 						
			height = 1;
		}

		game.getTouchHandler().init( width, height );
		
		virtualViewport.initialize( Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, width, height );
		
		gl.glViewport( virtualViewport.getCropX(), virtualViewport.getCropY(),
					   virtualViewport.getWidth(), virtualViewport.getHeight() ); 	
		
		gl.glMatrixMode(GL10.GL_PROJECTION); 	
		gl.glLoadIdentity(); 					
		gl.glOrthof( 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, 0, 1, -1 );
		
		gl.glMatrixMode(GL10.GL_MODELVIEW); 	
		gl.glLoadIdentity(); 						
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		gfxContext = gl;
		
		game.loadTextures( gl );
		
		gl.glEnable(GL10.GL_TEXTURE_2D);			
		gl.glShadeModel(GL10.GL_SMOOTH); 			
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 	
		gl.glClearDepthf(1.0f); 					
		gl.glDisable(GL10.GL_DEPTH_TEST); 			
		gl.glDepthFunc(GL10.GL_LEQUAL); 			
		//gl.glEnable(GL10.GL_ALPHA_TEST);
		//gl.glAlphaFunc(GL10.GL_GREATER, 0);
		gl.glEnable(GL10.GL_BLEND);			
		
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 

	}


	@Override
	public boolean onTouchEvent( MotionEvent event ) 
	{   
        return true;
	}
	
	public void shutDown()
	{
		game.shutDown( gfxContext );
		
	}
	
	
}
