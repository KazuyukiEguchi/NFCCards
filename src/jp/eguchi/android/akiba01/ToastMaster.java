package jp.eguchi.android.akiba01;

// NFC 神経衰弱　橙幻郷バージョン
// Original Source Code by 大和田 健一
// Modify Source Code by Kazuyuki Eguchi

import android.content.Context;
import android.widget.Toast;

public class ToastMaster extends Toast
{
	private static Toast sToast = null;

    public ToastMaster( Context context )
    {
		super( context );
	}

    @Override
    public void show()
    {
    	ToastMaster.setToast( this );
    	super.show();
    }

    public static void setToast( Toast toast )
    {
        if (sToast != null)
        	sToast.cancel();
        
        sToast = toast;
    }

    public static void cancelToast()
    {
        if (sToast != null)
        	sToast.cancel();
        
        sToast = null;
    }

    public static void showLong( Context context, int resId )
    {
		makeText( context, resId, LENGTH_LONG ).show();
	}

    public static void showLong( Context context, CharSequence text )
    {
		makeText( context, text, LENGTH_LONG ).show();
	}

    public static void showShort( Context context, int resId )
    {
		makeText( context, resId, LENGTH_SHORT ).show();
	}

    public static void showShort( Context context, CharSequence text )
    {
		makeText( context, text, LENGTH_SHORT ).show();
	}
}
