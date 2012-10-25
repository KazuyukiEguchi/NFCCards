package jp.eguchi.android.akiba01;

// NFC 神経衰弱　橙幻郷バージョン
// Original Source Code by 大和田 健一
// Modify Source Code by Kazuyuki Eguchi

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class MainActivity extends NfcCommonActivity
{
    private static final int MSG_WHAT = Constant.MSG_WHAT_FINISH;
	private static final String IMAGE_NAME_START = Constant.IMAGE_NAME_START;

    private static final int INTERVAL = 1000; // 1 sec;
    private static final int VIBRATE_TIME = 500;	// 0.5 sec
	private final static int STATUS_PREVIEW = 0;
	private final static int STATUS_START = 1;
	private final static int STATUS_STOP = 2;	
	private final static int STATUS_COMPLETE = 3;	
	private final static int CARD_SET_1 = 1;
	private final static int CARD_SET_2 = 2;
		
	// class object
	private CardHelper mHelper;		
	private Vibrator mVibrator;
	private PreferenceUtility mPreference;
	
	private ImageView mImageViewPhoto1;
	private ImageView mImageViewPhoto2; 
	private TextView mTextViewLeft;
	private TextView mTextViewCenter;
	private TextView mTextViewTime;

    private TimeView mTimeView;
	private ImageUtility mImageUtility;

	private int mCardNum = 0;		    
	private boolean[] mCardNumArray = null;
	private int mStatus = STATUS_PREVIEW ;
	private int mCardSet = CARD_SET_1;
	private int mNum1 = 0;
	private int mSet1 = 0;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message m)
        {
        	updateFinish();
        }
    };

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

		mVibrator = (Vibrator) getSystemService( VIBRATOR_SERVICE );
		mPreference = new PreferenceUtility( this );
		mHelper = new CardHelper( this );	
		mImageUtility = new ImageUtility( this );	

        mImageViewPhoto1 = (ImageView) findViewById( R.id.imageview_photo_1 );
		mImageViewPhoto2 = (ImageView) findViewById( R.id.imageview_photo_2 );
        mTextViewLeft = (TextView) findViewById( R.id.textview_left );
		mTextViewCenter = (TextView) findViewById( R.id.textview_center );
		mTextViewTime = (TextView) findViewById( R.id.textview_time );
		mTimeView = new TimeView( this, mTextViewTime );

		mImageViewPhoto1.setOnClickListener(new OnClickListener()
		{
	 		@Override
			public void onClick( View v )
	 		{
				clickPhoto1();
			}
		});
		
		// mFileUtility.init();	
		showPreview();
		prepareIntent();
	}

    private void startGame()
    {
		mStatus = STATUS_START ;
		mTimeView.start();
		mCardNum = mPreference.getNum();
		mCardNumArray = new boolean[ mCardNum + 1 ];
		
		for ( int i=0; i <= mCardNum; i++ )
		{
			mCardNumArray[i] = false;
		}
	}
		
    private void stopGame()
    {
		mStatus = STATUS_STOP ;
		mTimeView.stop();
	}

    private boolean isFinishGame()
    {
    	for ( int i=1; i <= mCardNum; i++ )
    	{
			if ( !mCardNumArray[i] )
			{
				return false;
			}
		}
		return true;
	}

    private void showPreview()
    {
    	mStatus = STATUS_PREVIEW ;
		mTimeView.showTime() ;
		mTextViewLeft.setText( "カードを読み取ってください" );
		mTextViewLeft.setTextColor( Color.BLACK );
		mTextViewCenter.setText( "" );
		mTextViewCenter.setTextColor( Color.BLACK );
		mImageUtility.restart();
		mImageUtility.showImage( mImageViewPhoto1, IMAGE_NAME_START );
		mImageViewPhoto2.setImageDrawable( null );
	}

	private void showFinish()
    {
		stopGame();
		mTimeView.saveTime() ;
		mHandler.sendMessageDelayed( Message.obtain(mHandler, MSG_WHAT), INTERVAL ); 
	}

    private void clickPhoto1()
    {
		if ( mStatus == STATUS_PREVIEW )
		{
			jump_hp();
		}
	}

    private void jump_hp()
    {
    	try
    	{
    		Uri uri = Uri.parse("http://tougenkyoumaid.com/");
    		Intent i = new Intent(Intent.ACTION_VIEW,uri);
    		startActivity(i);
    	}
    	catch(Exception ex)
    	{
    		log_d(ex.toString());
    	}
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
		enableForegroundDispatch();
    }

    @Override
    public void onNewIntent( Intent intent )
    {
		if (( mStatus == STATUS_STOP )||( mStatus == STATUS_COMPLETE ))
		{
			log_d( "onNewIntent: invalid status: " + mStatus );
			return;
		}

		String tag = intentToTagID( intent );

		if ( tag == null ) 
		{
			log_d( "onNewIntent: tag is null" );
			return;
		}

		CardRecord record = mHelper.getRecordByTag( tag );

		if ( record == null )
		{
			toast_short("登録されていないカードです");
			return;
		}

		if ( mStatus == STATUS_PREVIEW )
		{
			startGame();
		}		
        
		switch ( mCardSet )
		{
			case CARD_SET_2:
				showSecond( record );
				break;
			
			case CARD_SET_1:
			default:
				showFirst( record );
				break;
		}
	}

    private void showFirst( CardRecord record )
    {
		String tag = record.tag;
		int num = record.num;
		int set = record.set;
		mNum1 = num;
		mSet1 = set;
		mTextViewLeft.setText( "１枚目: " + tag );
		mTextViewCenter.setText( "" );
		mImageUtility.showImageByNum( mImageViewPhoto1, num );
		mImageViewPhoto2.setImageResource( R.drawable.white );
		mCardSet = CARD_SET_2; 
	}

    private void showSecond( CardRecord record )
    {
		if ( record.num == mNum1 )
		{
			showSecondSame( record );
		}
		else if ( record.set == mSet1 )
		{
			showSecondMatch( record );
		}
		else
		{
			showSecondUnmatch( record );
		}
	}
   
    private void showSecondMatch( CardRecord record )
    {
    	showSecondCommon( record );

		mTextViewCenter.setText( "正解！" );
		mTextViewCenter.setTextColor( Color.BLUE );
		mCardNumArray[ record.num ] = true;
		mCardNumArray[ mNum1 ] = true;
		
		mVibrator.vibrate( VIBRATE_TIME );

		if ( isFinishGame() == true)
		{
			showFinish();
		}
	}

    private void showSecondUnmatch( CardRecord record )
    {
    	showSecondCommon( record );
		mTextViewCenter.setText( "違います" );
		mTextViewCenter.setTextColor( Color.RED );
	}

    private void showSecondSame( CardRecord record )
    {
		showSecondCommon( record );
		mTextViewCenter.setText( "同じカードです" );
		mTextViewCenter.setTextColor( Color.RED );
    }
	
    private void showSecondCommon( CardRecord record )
    {
		mTextViewLeft.setText( "２枚目: " + record.tag );
		mImageUtility.showImageByNum( mImageViewPhoto1, mNum1 );
		mImageUtility.showImageByNum( mImageViewPhoto2, record.num );
		mCardSet = CARD_SET_1; 
	}

    @Override
    public void onPause()
    {
        super.onPause();
		disableForegroundDispatch();
    } 
    
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
		switch ( item.getItemId() )
		{
			case R.id.menu_restart:
				showPreview();
				return true;

			case R.id.menu_finish:
				finish();
				return true;
			
			case R.id.menu_setting:
				startSettingActivity();
				return true;
		}
		
		return false;
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        super.onActivityResult( requestCode, resultCode, data );
		showPreview();
    }

    private synchronized void updateFinish()
    {
		startVideoActivity();
    }
}
