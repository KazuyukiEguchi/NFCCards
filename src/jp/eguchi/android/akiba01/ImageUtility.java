package jp.eguchi.android.akiba01;

// NFC 神経衰弱　橙幻郷バージョン
// Original Source Code by 大和田 健一
// Modify Source Code by Kazuyuki Eguchi

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.widget.ImageView;

public class ImageUtility 
{
	private final static String IMAGE_PREFIX = Constant.IMAGE_PREFIX ; 
	private final static String IMAGE_EXT = Constant.IMAGE_EXT ;

	private final static int DRAWABLE_COFF = 2 ;
	private String mMainPath = "";
		
	public ImageUtility( Context context )
	{
		String sd = Environment.getDataDirectory().getPath();
		mMainPath = sd + "/data/" + context.getPackageName() + "/files";
		restart();
	}

	public void restart()
	{
	}

	public String[] getSubDirs()
	{
		ArrayList<String> list = getSubDirList();
		int size = list.size();
		String[] dirs = new String[ size ];
		for ( int i=0; i < size; i++ )
		{
			dirs[ i ] = list.get( i ); 
		}
		
		return dirs;
	}
	
	private ArrayList<String> getSubDirList()
	{
		ArrayList<String> list = new ArrayList<String>();
		File main = new File( mMainPath );
		File[] files = main.listFiles();
		for ( File f : files )
		{
			if ( f.isDirectory() )
			{
				list.add( f.getName() );
			}
		}
		return list;
	}
	
	public boolean existsFile( String filename )
	{
		File file = new File( getPath( filename )  );
		return file.exists();
	}

	public void showImageByNum( ImageView view, int num )
	{
		String file = getNameByNum( num );
		showImage( view, file );
		log_d( "showImageByNum: " + num + " " + file );
	}
		
	public void showImage( ImageView view, String file )
	{
		try
		{
			InputStream is = view.getResources().getAssets().open(file);
		    Bitmap bm = BitmapFactory.decodeStream(is);
		    view.setImageBitmap(bm);
		}
		catch(Exception ex)
		{
			log_d(ex.toString());
		}
	}

	public String getPath( String file )
	{
		String path = "file:///android_asset/" + file;
		log_d("getPath " + path );
		return path;
	}

	public String getPathByNum( int num )
	{
		return getPath( getNameByNum( num ) );
	}
	
	public String getNameByNum( int num )
	{
		String name = IMAGE_PREFIX + num + "." + IMAGE_EXT ;
		return name;
	}

	public Spanned getHtmlImage( String msg, int num )
	{
		ImageGetter imageGetter = new ImageGetter()
		{ 
			@Override 
			public Drawable getDrawable( String source )
			{
				log_d( "getHtmlImage source: " + source ); 
				Drawable d = Drawable.createFromPath( source );
				int w = DRAWABLE_COFF * d.getIntrinsicWidth();
				int h = DRAWABLE_COFF * d.getIntrinsicHeight();
				log_d( "getHtmlImage size " + w + " x "+ h );  
				d.setBounds( 0, 0, w, h ); 
				return d; 
			} 
		}; 

		String path = getPathByNum( num );
        String html = msg + "<img src=\"" + path + "\">";
        
        Spanned spanned = Html.fromHtml( html, imageGetter, null );
		log_d( "getHtmlImage spanned: " + spanned );
		return spanned;
	}
					           
	private void log_d( String msg )
	{
		// if (D) Log.d( TAG, TAG_SUB + msg );
	} 
}
