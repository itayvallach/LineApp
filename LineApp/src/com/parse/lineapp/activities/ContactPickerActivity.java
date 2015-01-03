package com.parse.lineapp.activities;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.lineapp.R;

public class ContactPickerActivity extends Activity {

	ContactListAdapter adapter;
	ListView list_Contact;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_picker_activity);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
 
			// set title
			alertDialogBuilder.setTitle(getResources().getString(R.string.explanation_dialog_title));
 
			// set dialog message
			alertDialogBuilder
				.setMessage(getResources().getString(R.string.contacts_picker_explanation_dialog))
				.setCancelable(false)
				.setNeutralButton(getResources().getString(R.string.got_it),new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});

				AlertDialog alertDialog = alertDialogBuilder.create();

				alertDialog.show();
		

		list_Contact = (ListView) findViewById(R.id.lst_contactList);

		adapter = new ContactListAdapter(this,new G_ContactList());
		list_Contact.setAdapter(adapter);

		try
		{
			//Running AsyncLoader with adapter and blank filter
			new AsyncContactLoader(adapter).execute("%");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		EditText srchBox = (EditText) findViewById(R.id.txt_searchContact);

		//Adding text change listener for filtering contacts
		srchBox.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) 
			{
				String filter=s.toString().trim()+"%";


				//Running AsyncLoader with adapter and search text as parameters

				try
				{
					new AsyncContactLoader(adapter).execute(filter);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

		});

		//Code to return selected contacts...
		Button btnDone = (Button) findViewById(R.id.btnDone);

		btnDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {



				Intent intent = new Intent();
				if(adapter.selectedContacts.getCount()>0)
				{

					String[][] sel_cons = new String[adapter.selectedContacts.getCount()][4];
					for(int i=0;i<adapter.selectedContacts.getCount();i++)
					{
						sel_cons[i][0] = adapter.selectedContacts.getContacts().get(i).id;
						sel_cons[i][1] = adapter.selectedContacts.getContacts().get(i).name;
						sel_cons[i][2] = adapter.selectedContacts.getContacts().get(i).phone;
						sel_cons[i][3] = adapter.selectedContacts.getContacts().get(i).label;
					}



					//Bundling up the contacts to pass
					Bundle data_to_pass = new Bundle();

					data_to_pass.putSerializable("selectedContacts", sel_cons);

					intent.putExtras(data_to_pass);
					setResult(RESULT_OK,intent);
					Log.v("Result", "ok");
				}
				else
				{
					//If user presses back button without selecting any contact
					Log.v("Result", "cancelled");
					setResult(RESULT_CANCELED,intent);
				}
				//Ending Activity and passing result

				finish();

			}
		});

	}//End of onCreate()
	
	
	//AsyncContactLoader Class Definition Goes Here
	class AsyncContactLoader extends AsyncTask<String, G_ContactList, G_ContactList>
	{


		ContactListAdapter cla;
		ProgressDialog pgdlg;


		AsyncContactLoader(ContactListAdapter adap)
		{
			//init AsyncLoader with the ListView Adapter

			cla = adap;
		}

		protected void onPreExecute()
		{
			//Show a pop up message

			pgdlg = ProgressDialog.show(ContactPickerActivity.this, getResources().getString(R.string.please_wait), getResources().getString(R.string.loading_contacts),true);
		}


		//Loading Contacts

		@Override
		protected G_ContactList doInBackground(String... filters ) 
		{
			G_ContactList glst=null;

			//Filter = text in search textbox 

			String filter = filters[0];
			ContentResolver cr = getContentResolver();
			int count=0;

			//Code to fetch contacts...

			Uri uri = ContactsContract.Contacts.CONTENT_URI;

			//Fields to select from database 
			String[] projection = new String[]{
					ContactsContract.Contacts._ID,
					ContactsContract.Contacts.DISPLAY_NAME,
					ContactsContract.Contacts.HAS_PHONE_NUMBER
			};

			/*Querying database (Select fields in projection from database where contact name like 'filter%', sort by name, in ascending order)*/
			Cursor cursor = cr.query(uri, projection,  ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?", new  String[] {filter.toString()},  ContactsContract.Contacts.DISPLAY_NAME+ " ASC");

			//Log.v("", "Contacts : "+cursor.getCount());


			if(cursor.getCount()>0)
			{

				glst=new G_ContactList();

				while(cursor.moveToNext())
				{

					//Filtering Contacts with Phone Numbers

					if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0)
					{

						String id =  cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
						String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

						//Phone numbers lies in a separate table. Querying that table with Contact ID

						Cursor ph_cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"=?", new String[] {id}, null);
						while(ph_cur.moveToNext())
						{

							String phId = ph_cur.getString(ph_cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

							//Label eg : home, office etc. They are stored as int values

							String customLabel = ph_cur.getString(ph_cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
							String label = (String)ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(),ph_cur.getInt(ph_cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)),customLabel);
							String ph_no = ph_cur.getString(ph_cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							G_Contact tmp = new G_Contact(phId,name,ph_no,label);
							glst.addContact(tmp);
							count++;


							//Refresh ListView upon loading 100 Contacts 

							if(count==100)
							{
								publishProgress(glst);
								count=0;
							}

						}
						ph_cur.close();
					}


				}
				cursor.close();

			}


			return glst;
		}

		//Code to refresh list view 

		@Override
		protected void onProgressUpdate(G_ContactList... glsts )
		{
			if(pgdlg.isShowing())
				pgdlg.dismiss();
			cla.gcl = glsts[0];
			cla.notifyDataSetChanged();
			//Log.v("Progress", cla.getCount()+" loaded");
		}

		@Override
		//Loading contacts finished, refresh list view to load any missed out contacts 

		protected void onPostExecute(G_ContactList result)
		{
			if(pgdlg.isShowing())
				pgdlg.dismiss();
			cla.gcl=result;
			cla.notifyDataSetChanged();
			//Log.v("Progress ::", cla.getCount()+" total loaded");
			//Toast.makeText(ContactPicker.this, cla.getCount()+" Contact(s) Found", Toast.LENGTH_LONG).show();
		}


	}
	//ContactListAdapter Class Definition Goes Here
	class ContactListAdapter extends BaseAdapter
	{
		Context context;
		G_ContactList gcl;
		G_ContactList selectedContacts;

		public ContactListAdapter(Context context,G_ContactList gcl)
		{
			super();
			this.context = context;
			this.gcl=gcl;
			selectedContacts = new G_ContactList();

		}
		/*Custom View Generation(You may modify this to include other Views) */
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			LayoutInflater inflater = (LayoutInflater)    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View view_row = inflater.inflate(R.layout.contact_list_layout, parent,false);

			CheckBox chk_contact = (CheckBox) view_row.findViewById(R.id.chkbxContact);
			chk_contact.setId(Integer.parseInt(gcl.getContacts().get(position).id));

			//Text to display near checkbox [Here, Contact_Name (Number Label : Phone Number)]
			chk_contact.setText(gcl.getContacts().get(position).name.toString() + " ( "+gcl.getContacts().get(position).label+" : " + gcl.getContacts().get(position).phone.toString() + ")");

			if(alreadySelected(gcl.getContacts().get(position)))
			{
				chk_contact.setChecked(true);
			}

			//Code to get Selected Contacts.
			chk_contact.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {


					G_Contact t = gcl.getContact(arg0.getId());
					if(t!=null && arg1)
					{
						if(!alreadySelected(t))
							selectedContacts.addContact(t);
					}
					else if(!arg1 && t!=null)
					{
						selectedContacts.removeContact(arg0.getId());
					}


				}

			});

			return view_row;
		}
		public boolean alreadySelected(G_Contact t)
		{
			boolean ret = false;

			if(selectedContacts.getContact(Integer.parseInt(t.id))!=null)
				ret=true;

			return ret;
		}
		@Override
		public int getCount() {
			
			return gcl.getCount();
		}

		@Override
		public G_Contact getItem(int arg0) {
			// TODO Auto-generated method stub
			return gcl.getContacts().get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return Long.parseLong(gcl.getContacts().get(arg0).id);
		}
	}
	//G_ContactList Class Definition Goes Here
	class G_Contact
	{

		public String id,name,phone,label;

		G_Contact(String tid, String tname,String tphone,String tlabel)
		{
			this.id=tid;
			this.name=tname;
			this.phone=tphone;
			this.label=tlabel;
		}
	}
	//G_Contact Class Definition Goes Here
	class G_ContactList
	{

		private ArrayList<G_Contact> contacts = new ArrayList<G_Contact>();

		public int getCount()
		{
			return this.contacts.size();
		}
		public void addContact(G_Contact c)
		{
			this.contacts.add(c);
		}
		public void removeContact(G_Contact c)
		{
			this.contacts.remove(c);
		}
		public void removeContact(int id)
		{
			for(int i=0;i<this.getCount();i++)
			{
				if(id==Integer.parseInt(this.contacts.get(i).id))
				{
					this.contacts.remove(this.contacts.get(i));
				}
			}
		}
		public G_Contact getContact(int id)
		{
			G_Contact tmp=null;
			for(int i=0;i<this.getCount();i++)
			{
				if(id==Integer.parseInt(this.contacts.get(i).id))
				{
					tmp = new G_Contact(this.contacts.get(i).id,this.contacts.get(i).name,this.contacts.get(i).phone,this.contacts.get(i).label);
				}
			}
			return tmp;
		}
		public ArrayList<G_Contact> getContacts()
		{
			return contacts;
		}
		public void setContacts(ArrayList<G_Contact> c)
		{
			this.contacts=c;
		}

	}
	

}//End of Class ContactPicker


