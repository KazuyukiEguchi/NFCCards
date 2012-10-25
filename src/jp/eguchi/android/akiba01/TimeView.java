package jp.eguchi.android.akiba01;

// NFC 神経衰弱　橙幻郷バージョン
// Original Source Code by 大和田 健一
// Modify Source Code by Kazuyuki Eguchi

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class TimeView
{
	protected String TAG_SUB = "TimeView : ";
	protected final static String TAG = Constant.TAG;
    protected final static boolean D = Constant.DEBUG; 

    private final static int MSG_WHAT = Constant.MSG_WHAT_TIME;
 
    private final static int INTERVAL = 100; // 0.1 sec;

	private PreferenceUtility mPreference;
    private TextView mTextView;    

    private long mStartTime = 0L;
    private boolean isStart = false;
    private boolean runningFlag = false;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message m)
        {
            if (runningFlag)
            {
                updateText();
                sendMessageDelayed(Message.obtain(this, MSG_WHAT), INTERVAL );
            }
        }
    };

    public TimeView( Context context, TextView view )
    {
        mTextView = view;
		mPreference = new PreferenceUtility( context );
    }

    public void saveTime()
    {
   		mPreference.setTime( getFormatedTime() );
	}

    public void showTime()
    {
		mTextView.setText(  mPreference.getTime() );	
	}

    private String getFormatedTime()
    {	
        long msec = System.currentTimeMillis() - mStartTime ;
        long sec = (long) ( msec / 1000 );
        long msec_r = msec - ( sec * 1000 );
        long msec_v = (long) ( msec_r / 100 );
        String time = sec + "." + msec_v + " 秒" ;
        return time;
    }       

    public void start()
    {
        isStart = true;
        mStartTime = System.currentTimeMillis();
        updateRunning();
    }

    public void stop()
    {
        isStart = false;
        updateRunning();
    }

	private void updateRunning()
    {
        boolean running = isStart;

        if (running != runningFlag)
        {
        	if (running)
            {
        		mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_WHAT), INTERVAL );                
            }
        	else
        	{
                mHandler.removeMessages(MSG_WHAT);
            }
            
        	runningFlag = running;
        }
    }

	private synchronized void updateText()
    {   
		mTextView.setText( getFormatedTime() );	
    }
    
	protected void log_d( String msg )
	{
		// if (D) Log.d( TAG, TAG_SUB + msg );
	}    
}
