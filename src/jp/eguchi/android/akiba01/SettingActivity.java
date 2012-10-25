package jp.eguchi.android.akiba01;

// NFC 神経衰弱　橙幻郷バージョン
// Original Source Code by 大和田 健一
// Modify Source Code by Kazuyuki Eguchi

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingActivity extends NfcCommonActivity
{
	private final static int MODE_NONE = 0;	
	private final static int MODE_BULK = 1;	// default

	private CardHelper mHelper;		
	private ImageUtility mImageUtility;
	private PreferenceUtility mPreference;
    
	private TextView mTextViewTitle;
	private ImageView mImageViewMain;
	private Spinner mSpinner;
	private Button mButton1;
       
    private int mMode = MODE_NONE; 
	private int mCardNum = 0;	
	private boolean bInit = false;
	
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_setting );

		mHelper = new CardHelper( this );	
		mImageUtility = new ImageUtility( this );	
		mPreference = new PreferenceUtility( this );

        mTextViewTitle = (TextView) findViewById( R.id.textview_title );
        mImageViewMain = (ImageView) findViewById( R.id.imageview_main );
        mSpinner = (Spinner) findViewById(R.id.spinner1);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        adapter.add("2枚");
        adapter.add("4枚");
        adapter.add("6枚");
        adapter.add("8枚");
        adapter.add("10枚");
        
        mSpinner.setAdapter(adapter);
        
        int num = mPreference.getNum();
        
        switch(num)
        {
        	case 2:
        		mSpinner.setSelection(0);
        		break;
        		
        	case 4:
        		mSpinner.setSelection(1);
        		break;
        		
        	case 6:
        		mSpinner.setSelection(2);
        		break;
        		
        	case 8:
        		mSpinner.setSelection(3);
        		break;
        		
        	default:
        		mSpinner.setSelection(4);
        		break;
        }
        
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,int position, long id)
			{
				int num = mPreference.getNum();
				int num2 = 0;
				switch(position)
				{
					case 0:
						num2 = 2;
						break;

					case 1:
						num2 = 4;
						break;

					case 2:
						num2 = 6;
						break;

					case 3:
						num2 = 8;
						break;

					default:
						num2 = 10;
						break;
				}
				
				if(num != num2 && bInit == true)
				{
					mPreference.setNum("" + num2);

					List<CardRecord> lists = mHelper.getRecordList(10);
					
					if(lists != null)
					{
						int size = lists.size();
						log_d("size=" + size);

						for(int i = 0 ; i < size ; i++)
						{
							CardRecord card = lists.get(i);
							
							if(card != null)
							{
								mHelper.delete(card);
							}
						}
					}
				}
				else
				{
					bInit = true;
				}
				// log_d("num=" + num + ",position=" + position);
				showMenu();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
			}
		});
        
        mButton1 = (Button)findViewById(R.id.button1);
        
        mButton1.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				List<CardRecord> lists = mHelper.getRecordList(10);
				
				if(lists != null)
				{
					int size = lists.size();
					log_d("size=" + size);

					for(int i = 0 ; i < size ; i++)
					{
						CardRecord card = lists.get(i);
						
						if(card != null)
						{
							mHelper.delete(card);
						}
					}
				}

				showMenu();
			}
		});
				
		showMenu();		
		prepareIntent();
	}

    private void clickBulk()
    {
		mMode = MODE_BULK;
		showPrepare();
	}

	private void showPrepare()
	{
		mCardNum = mPreference.getNum();
		List<CardRecord> lists = mHelper.getRecordList(mCardNum);

		if(lists != null)
		{
			if(lists.size() < mCardNum)
			{
				mTextViewTitle.setText( "NFCカードを読み込んでください:(登録済み" + lists.size() + "/" + mCardNum + "中)");
			}
			else
			{
				mTextViewTitle.setText( "カードはすべて登録済みです");
			}
		}
		else
		{
			mTextViewTitle.setText( "NFCカードを読み込んでください:(登録済み" + 0 + "/" + mCardNum + "中)");
		}

		mTextViewTitle.setTextColor( Color.BLACK );
		mImageViewMain.setImageResource( R.drawable.white );
	}

    private void showMenu()
    {
    	mMode = MODE_NONE; 
		mCardNum = mPreference.getNum();
		List<CardRecord> lists = mHelper.getRecordList(mCardNum);
		
		if(lists != null)
		{
			if(lists.size() < mCardNum)
			{
				mTextViewTitle.setText( "NFCカードを読み込んでください:(登録済み" + lists.size() + "/" + mCardNum + "中)");
			}
			else
			{
				mTextViewTitle.setText( "カードはすべて登録済みです");
			}
		}
		else
		{
			mTextViewTitle.setText( "NFCカードを読み込んでください:(登録済み" + 0 + "/" + mCardNum + "中)");
		}
		
		mTextViewTitle.setTextColor( Color.BLACK );
		mImageViewMain.setImageDrawable( null );
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
		String tag = intentToTagID( intent );
        log_d( "Discovered tag with intent: " + intent );
		log_d( "tag: " + tag  );

		if ( mMode == MODE_NONE )
		{
			clickBulk();
		}
	
		CardRecord record = mHelper.getRecordByTag( tag );
		if ( record == null )
		{
			addCardBulk( tag );
		}
		else
		{
			showDialogAleady( record );
        }
    }

	private void addCardBulk( String tag )
	{
		int num = getNewCardNum();
		
		if ( num == 0 )
		{
			String title = "登録できるカードの上限を超えています";
			String msg = "tag = " + tag;
			AlertDialog dialog = new AlertDialog.Builder(this)
			.setTitle( title )
			.setMessage( msg )
			.setCancelable( true )		               
			.setNegativeButton( "Close",new DialogInterface.OnClickListener()
				{
                    @Override
                    public void onClick( DialogInterface dialog, int which )
                    {
						// close
                    }
                }).create();
			dialog.show();
			return;
		}
		
 		int set = ( num + 1 ) / 2;	
		
 		CardRecord r = new CardRecord( tag, num, set );
        
		long ret = mHelper.insert( r ); 

        if ( ret > 0 )
        {
	    	mTextViewTitle.setText( "登録したカード: " + num + " " + tag );
	    	mTextViewTitle.setTextColor( Color.BLUE );
			mImageUtility.showImageByNum( mImageViewMain, num );      		
	    }
        else
        {
	    	 toast_short( "Add Card Failed " + tag );    	   
	    }
	}

	private int getNewCardNum()
	{
		for ( int i=1; i <= mCardNum; i++ )
		{
			CardRecord r = mHelper.getRecordByNum( i ); 
			
			if ( r == null )
				return i;
		}
		return 0;
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
        inflater.inflate( R.menu.add, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
		switch ( item.getItemId() )
		{
			case R.id.menu_restart:
				finish();
				return true;
		
			case R.id.menu_setting:
				showMenu();
				return true;
        }
		return false;
    }
    
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        super.onActivityResult( requestCode, resultCode, data );
		showMenu();
    }

	private void showDialogAleady( CardRecord record  )
	{
		String tag_id = record.tag;
		String title = "登録済みのカードです";
		String msg = "tag = " + tag_id;

		AlertDialog dialog = new AlertDialog.Builder( this )
			.setTitle( title )
			.setMessage( msg )
			.setCancelable( true )		               
			.setNegativeButton( "Close",
                new DialogInterface.OnClickListener()
				{
                    @Override
                    public void onClick( DialogInterface dialog, int which )
                    {
                    }
                })
			.create();
        dialog.show();
    }	
}
