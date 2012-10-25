package jp.eguchi.android.akiba01;

// NFC 神経衰弱　橙幻郷バージョン
// Original Source Code by 大和田 健一
// Modify Source Code by Kazuyuki Eguchi

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CardHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME = "card.db";
    private static final int DB_VERSION = 1;

    private static final String TBL_NAME = "card";

	private static final String COL_ID = "_id";
	private static final String COL_TAG = "tag";
	private static final String COL_NUM = "num";
	private static final String COL_SET = "_set";	// "set" is reserved word

	private	static final String[] COLUMNS =
			new String[]
			{ COL_ID, COL_TAG, COL_NUM, COL_SET  } ;

	private static final String ORDER_BY =  "_id desc" ;

    private static final String CREATE_SQL =
    	"CREATE TABLE IF NOT EXISTS " 
		+ TBL_NAME 
		+ " ( " 
		+ COL_ID
		+ " INTEGER PRIMARY KEY AUTOINCREMENT, " 
		+ COL_TAG
		+ " TEXT, " 
		+ COL_NUM
		+ " INTEGER, " 
		+ COL_SET
		+ " INTEGER )" ;

    private static final String DROP_SQL =
		"DROP TABLE IF EXISTS " + TBL_NAME ;
    
    public CardHelper( Context context )
    {
        super( context, DB_NAME, null, DB_VERSION );
    }

    @Override
    public void onCreate( SQLiteDatabase db )
    {
    	createDb( db );
    }

    private void createDb( SQLiteDatabase db )
    {
        db.execSQL( CREATE_SQL );
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
    {
        db.execSQL( DROP_SQL );
        createDb( db );
    }

    public long insert( CardRecord record )
    {
    	SQLiteDatabase db = getWritableDatabase();
    	
    	if ( db == null )
    		return 0;
		
    	long ret = db.insert( 
			TBL_NAME, 
			null, 
			buildValues( record ) );
		
    	db.close();
		return ret;
    }

	public int update( CardRecord record  )
	{
    	SQLiteDatabase db = getWritableDatabase();
    	if ( db == null )
    		return 0;
    	
		int ret = db.update(
			TBL_NAME, 
			buildValues( record ),
			buildWhereId( record ), 
			null );
		
		db.close();
		return ret;
	}

	public int delete( CardRecord record )
	{
		return delete( buildWhereId( record ) );
	}

	public int delete( int id  )
	{
		return delete( buildWhereId( id ) );
	}	

	public int delete(  String where )
	{
		SQLiteDatabase db = getWritableDatabase();
		
		if ( db == null )
			return 0;
		
		int ret = db.delete(
			TBL_NAME, 
			where, 
			null );		
		
		db.close();
		return ret;	
	}
	
    private ContentValues buildValues( CardRecord r )
    {
		ContentValues v = new ContentValues();	
		
		v.put( COL_TAG, r.tag );
		v.put( COL_NUM, r.num );
		v.put( COL_SET, r.set );
		
		return v;
	}

	private String buildWhereId( CardRecord r )
	{
		return buildWhereId( r.id );
	}

	private String buildWhereId( int id )
	{
		String s = COL_ID + "=" + id ;
		return s;
	}

	private String buildWhereNum( int num )
	{
		String s = COL_NUM + "=" + num;
		return s;
	}

	private String buildWhereTag( String tag )
	{
		String s = COL_TAG + "= '" + tag + "'";
		return s;
	}
			
	private String buildLimit( int limit, int offset )
	{
		String limit_str = Integer.toString( limit );
		String offset_str = Integer.toString( offset );
		
		String str = null;
		
		if (( limit > 0 )&&( offset > 0 ))
		{
			str = limit_str+ ", " + offset_str;
		}
		else if (( limit > 0 )&&( offset== 0 ))
		{
			str = limit_str;
		}
		else if (( limit == 0 )&&( offset > 0 ))
		{
			str = "0, " + offset_str;
		}
		
		return str;
	}	
		
	public CardRecord getRecordById( int id )
	{
		return getRecordCommon( buildWhereId( id ) );
	}

	public CardRecord getRecordByNum( int num )
	{
		return getRecordCommon( buildWhereNum( num ) );
	}
	
	public CardRecord getRecordByTag( String tag )
	{
		return getRecordCommon( buildWhereTag( tag ) );
	}

	private CardRecord getRecordCommon( String where )
	{
		SQLiteDatabase db = getReadableDatabase();
		
		if ( db == null )
			return null;
        
		Cursor c = getCursorCommon( db, where, null );
		
		if (( c == null )||( c.getCount() == 0 ))
		{
			db.close();
			return null;
		}
		
		c.moveToFirst();   
		
		CardRecord r = buildRecord( c );
		
		db.close();
		return r;
	}
		
	public List<CardRecord> getRecordList( int limit )
	{
		return getRecordList( limit, 0 );
	}
	
	public List<CardRecord> getRecordList( int limit, int offset )
	{
		log_d( "getRecordList : " + limit + " : " + offset );
		SQLiteDatabase db = getReadableDatabase();
		if ( db == null )
			return null;
        
		Cursor c = getCursorCommon( 
			db, null,  buildLimit( limit, offset ) );
		
		if (( c == null )||( c.getCount() == 0 ))
		{
			db.close();
			return null;
		}
		
		List<CardRecord> list = buildRecordList( c );
		
		db.close();
		
		return list;
	}

	private Cursor getCursorCommon( SQLiteDatabase db, String where, String limit )
	{
		log_d( "getCursorCommon : "  + where + " : "+ limit );
		
		String[] param = null;
		String groupby = null;
		String having = null;
				
       return db.query( 
        	TBL_NAME,
        	COLUMNS,
			where , 
			param, 
			groupby , 
			having, 
			ORDER_BY , 
			limit );
	}
	
	private CardRecord buildRecord( Cursor c )
	{
		CardRecord r = new CardRecord();
		r.id = c.getInt(0);
		r.tag = c.getString(1);
		r.num = c.getInt(2);
		r.set = c.getInt(3);
		return r;
	}

	private List<CardRecord> buildRecordList( Cursor c )
	{
		log_d( "buildRecordList" );
		
		List<CardRecord> list = new ArrayList<CardRecord>();
		        
		int count = c.getCount();
		
		if ( count == 0 )
		{
			log_d( "buildRecordList no data" );
			return list;
		}
		
        c.moveToFirst();   
        
		for ( int i = 0; i < count; i++ )
		{
			list.add( buildRecord( c ) );
			c.moveToNext();
 		}
 		
		c.close();		
		return list;
	}
	
	private void log_d( String msg )
	{
		// if (D) Log.d( TAG, TAG_SUB + msg );
	} 
}
