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

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class CrappyFishMainActivity extends Activity
{

private GameSurfaceView glSurfaceView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    
    	super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
							  WindowManager.LayoutParams.FLAG_FULLSCREEN );
        
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				  			  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
        
        glSurfaceView = new GameSurfaceView(this);
        
        setContentView(glSurfaceView);
            
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC); 
  
    }

	@Override
	protected void onResume() 
	{
		super.onResume();
		glSurfaceView.onResume();
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		glSurfaceView.onPause();
	}
	
	@Override
    protected void onStop()
    {
        super.onStop();
        
        glSurfaceView.shutDown();
        
        this.finish();
        
    }
	
	@Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

}
