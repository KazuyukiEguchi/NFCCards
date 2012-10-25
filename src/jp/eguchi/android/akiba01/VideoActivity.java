package jp.eguchi.android.akiba01;

// NFC 神経衰弱　橙幻郷バージョン
// Original Source Code by 大和田 健一
// Modify Source Code by Kazuyuki Eguchi

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoActivity extends Activity implements SurfaceHolder.Callback
{
	private final static String VIDEO_NAME_COMPLETE = Constant.VIDEO_NAME_COMPLETE;
	
	private MediaPlayer mp = null;
	private SurfaceHolder holder = null;
	private SurfaceView video = null;

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_video );

        log_d( "onCreate" );

        getWindow().setFormat(PixelFormat.TRANSPARENT);
        
        video = (SurfaceView) findViewById(R.id.videoview);
        
        if(video == null)
        {
        	log_d("video is null");
        	return;
        }

        holder = video.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);
    } 
    
	private void log_d( String msg )
	{
		// if (D) Log.d( TAG, TAG_SUB + msg );
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height)
	{
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
        mp = new MediaPlayer();
    	mp.setDisplay(holder);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
        	@Override
        	public void onCompletion( MediaPlayer mp )
        	{
				finish();
        	}
        });

		try
		{
			AssetFileDescriptor afd = getAssets().openFd(VIDEO_NAME_COMPLETE);
			mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(), afd.getLength());
			mp.prepareAsync();
			mp.setOnPreparedListener(new OnPreparedListener()
			{
			    @Override
			    public void onPrepared(MediaPlayer mp)
			    {
			        mp.start();
			    }
			});
		}
		catch (Exception e)
		{
			log_d(e.toString());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		 if(mp != null)
		 {
		      mp.release();
		      mp = null;
		 }
	}
}
