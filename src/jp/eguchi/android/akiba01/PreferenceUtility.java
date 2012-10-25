package jp.eguchi.android.akiba01;

// NFC 神経衰弱　橙幻郷バージョン
// Original Source Code by 大和田 健一
// Modify Source Code by Kazuyuki Eguchi

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtility
{

	private final static String PREF_KEY_DIR = Constant.PREF_KEY_DIR;    
	private final static String PREF_KEY_NUM = Constant.PREF_KEY_NUM;
	private final static String PREF_KEY_TIME = Constant.PREF_KEY_TIME;
	private final static String DIR_SUB_DEFAULT = Constant.DIR_SUB_DEFAULT;
	private final static int CARD_NUM_DEFAULT = Constant.CARD_NUM_DEFAULT;
	private final static String STR_EMPTY = "";
	
	private SharedPreferences mPreferences;

    public PreferenceUtility( Context context )
    {		
		mPreferences = PreferenceManager.getDefaultSharedPreferences( context );
	}

   public void setDir( String value )
   {
   		log_d( "setDir " + value );
		mPreferences.edit().putString( PREF_KEY_DIR, value ).commit(); 
	}
	
    public String getDir()
    {
		return mPreferences.getString( PREF_KEY_DIR, DIR_SUB_DEFAULT );
	}

    public void setNum( String value )
    {
		log_d( "setNum " + value );
		int v = Integer.parseInt( value );
		mPreferences.edit().putInt( PREF_KEY_NUM, v ).commit(); 	
	}
				
    public int getNum()
    {		
		return mPreferences.getInt( PREF_KEY_NUM,  CARD_NUM_DEFAULT );
	}

   public void setTime( String value )
   {
   		log_d( "setTime " + value );
		mPreferences.edit().putString( PREF_KEY_TIME, value ).commit(); 
	}

   public String getTime()
   {
   		return mPreferences.getString( PREF_KEY_TIME,  STR_EMPTY );
   }

	private void log_d( String msg )
	{
		// if (D) Log.d( TAG, TAG_SUB + msg );
	}
}
