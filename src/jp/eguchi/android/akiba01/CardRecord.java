package jp.eguchi.android.akiba01;

// NFC 神経衰弱　橙幻郷バージョン
// Original Source Code by 大和田 健一
// Modify Source Code by Kazuyuki Eguchi


public class CardRecord
{
	public int id;
	public String tag;
	public int num;
	public int set;

	public CardRecord()
	{
		setRecord( 0, "", 0, 0 );
	}

	public CardRecord( String _tag, int _num, int _set )
	{
		setRecord( 0, _tag, _num, _set  );
	}
	
	public CardRecord( int _id, String _tag, int _num, int _set )
	{
		setRecord( _id, _tag, _num, _set );
	}

	public void setRecord( int _id, String _tag, int _num, int _set )
	{
		id = _id;
		tag = _tag;
		num = _num;
		set = _set;
	}
	
	public String getIdString()
	{
		return Integer.toString( id );
	}

	public String getNumString()
	{
		return Integer.toString( num );
	}

	public String getSetString()
	{
		return Integer.toString( set );
	}
}
