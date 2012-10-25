package jp.eguchi.android.akiba01;

// NFC 神経衰弱　橙幻郷バージョン
// Original Source Code by 大和田 健一
// Modify Source Code by Kazuyuki Eguchi

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;

public class NfcCommonActivity extends Activity
{
	protected String TAG_SUB = "NfcCommon : ";
	protected final static String TAG = Constant.TAG;
    protected final static boolean D = Constant.DEBUG; 

	protected final static int REQUEST_CODE_LIST = Constant.REQUEST_CODE_LIST ;
	protected final static int REQUEST_CODE_CREATE = Constant.REQUEST_CODE_CREATE ;
	protected final static int REQUEST_CODE_UPDATE = Constant.REQUEST_CODE_UPDATE ;
	protected final static int REQUEST_CODE_SETTING = Constant.REQUEST_CODE_SETTING ;
	protected final static int REQUEST_CODE_VIDEO = Constant.REQUEST_CODE_VIDEO ;
	protected final static String BUNDLE_EXTRA_ID  = Constant.BUNDLE_EXTRA_ID;
	protected final static String BUNDLE_EXTRA_TAG  = Constant.BUNDLE_EXTRA_TAG;

	protected final static int REQUEST_CODE_NFC = 0 ;    
	protected final static int FLAG_NONE = 0;
	
    protected NfcAdapter mAdapter;
    protected PendingIntent mPendingIntent;
    protected IntentFilter[] mFilters;
    protected String[][] mTechLists;
	
	protected void prepareIntent()
	{					
		mAdapter = NfcAdapter.getDefaultAdapter( this );

		Intent intent = new Intent( this, getClass() ).addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
        
		mPendingIntent = PendingIntent.getActivity( this, REQUEST_CODE_NFC, intent, FLAG_NONE ) ;
        
		IntentFilter ndef = new IntentFilter( NfcAdapter.ACTION_NDEF_DISCOVERED );
        
		try
		{
            ndef.addDataType( "*/*" );
        }
		catch ( MalformedMimeTypeException e ) {
			e.printStackTrace();
        }
		
        mFilters = new IntentFilter[] { ndef, };
 		mTechLists = new String[][]
 		{ 
			new String[] { NfcA.class.getName() } , 
			new String[] { NfcB.class.getName() } , 
			new String[] { NfcF.class.getName() } 
		};
    }

    protected void enableForegroundDispatch()
    {
        if ( mAdapter != null )
        {
        	mAdapter.enableForegroundDispatch( this, mPendingIntent, mFilters, mTechLists );
        }
    }

    protected String intentToTagID( Intent intent )
    {
		if ( intent == null )
		{
			log_d( "intentToTagID: intent is null" );
			return null;
		}			

		String action = intent.getAction();	
		if ( !action.equals( NfcAdapter.ACTION_TECH_DISCOVERED ) )
		{
			log_d( "intentToTagID: invalid action: " + action );
			return null;
		}
	
    	byte[] byte_id = intent.getByteArrayExtra( NfcAdapter.EXTRA_ID );
		if ( byte_id == null )
		{
			log_d( "intentToTagID: tag is null" );
			return null;
		}

		String tag_id = bytesToText( byte_id );
        log_d( "Discovered tag with intent: " + intent );
		log_d( "id: " + tag_id  );

		return tag_id;
    }

    protected String bytesToText( byte[] bytes )
    {	 
    	StringBuilder buffer = new StringBuilder();	 
    	for ( byte b : bytes )
    	{ 
    		String hex = String.format( "%02X", b );	 
			buffer.append( hex );	 
    	}	 
    	
    	String text = buffer.toString().trim();	 
    	
    	return text;	
    }
		
    protected void disableForegroundDispatch()
    {
        if ( this.isFinishing() && ( mAdapter != null ) )
        {
            mAdapter.disableForegroundDispatch( this );
        }
    } 

	protected void startVideoActivity()
	{	
		Intent intent = new Intent( this, VideoActivity.class );
		startActivityForResult( intent, REQUEST_CODE_VIDEO );
	}

	protected void startSettingActivity()
	{	
		Intent intent = new Intent( this, SettingActivity.class );
		startActivityForResult( intent, REQUEST_CODE_SETTING );
	}
	
	protected void log_d( String msg )
	{
		// if (D) Log.d( TAG, TAG_SUB + msg );
	} 

	protected void toast_short( String msg )
	{
		ToastMaster.showShort( this, msg );
	}
}
