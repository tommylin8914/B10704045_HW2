package com.example.b10704045_hw2;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.b10704045_hw2.data.WaitlistContract;
import com.example.b10704045_hw2.data.WaitlistDbHelper;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private GuestListAdapter mAdapter;
    private SQLiteDatabase mDb;
    static EditText mNewGuestNameEditText;
    static EditText mNewPartySizeEditText;
    static String inputname = null;
    static String inputnumber = null;
    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    Context mContext;
    TextView textView;
    View mVisualizerView;
    private int backgroundColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RecyclerView waitlistRecyclerView;


        // Set local attributes to corresponding views
        waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.all_guests_list_view);
        //mNewGuestNameEditText = (EditText) this.findViewById(R.id.person_name_edit_text);
        //mNewPartySizeEditText = (EditText) this.findViewById(R.id.party_count_edit_text);
        //mNewGuestNameEditText = (EditText) this.findViewById(R.id.ET1);
        //mNewPartySizeEditText = (EditText) this.findViewById(R.id.ET2);


        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Create a DB helper (this will create the DB if run for the first time)
        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();
        // Get all guest info from the database and save in a cursor
        Cursor cursor = getAllGuests();

        // Create an adapter for that cursor to display the data
        mAdapter = new GuestListAdapter(this, cursor);

        // Link the adapter to the RecyclerView
        waitlistRecyclerView.setAdapter(mAdapter);

        Intent intent = getIntent();
        if (intent.hasExtra("name")) {
            inputname = intent.getStringExtra("name");
            inputnumber = intent.getStringExtra("number");
            addNewGuest(inputname, Integer.parseInt(inputnumber));
            mAdapter.swapCursor(getAllGuests());
        }

        //mVisualizerView=findViewById(R.id.activity_main);
        //View MainView = findViewById(R.id.activity_main);
        //textView = findViewById(R.id.party_size_text_view);
        //Log.d("測試","成功");
        //setupSharedPreference();
        //Log.d("測試","失敗");

        // COMPLETED (3) Create a new ItemTouchHelper with a SimpleCallback that handles both LEFT and RIGHT swipe directions
        // Create an item touch helper to handle swiping items off the list
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // COMPLETED (4) Override onMove and simply return false inside
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return false;
            }

            // COMPLETED (5) Override onSwiped
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // COMPLETED (8) Inside, get the viewHolder's itemView's tag and store in a long variable id
                //get the id of the item being swiped
                final RecyclerView.ViewHolder viewHoldert = viewHolder;
                AlertDialog a = new AlertDialog.Builder(MainActivity.this).create();
                a.setTitle("是否刪除");
                a.setMessage("");
                a.setButton(AlertDialog.BUTTON_POSITIVE, "是",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                long id = (long) viewHoldert.itemView.getTag();
                                removeGuest(id);
                                mAdapter.swapCursor(getAllGuests());
                            }
                        });
                a.setButton(AlertDialog.BUTTON_NEGATIVE, "否",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAdapter.swapCursor(getAllGuests());
                                dialogInterface.dismiss();
                            }
                        });
                // COMPLETED (9) call removeGuest and pass through that id
                //remove from DB
                // COMPLETED (10) call swapCursor on mAdapter passing in getAllGuests() as the argument
                //update the list
                a.show();
            }

            //COMPLETED (11) attach the ItemTouchHelper to the waitlistRecyclerView
        }).attachToRecyclerView(waitlistRecyclerView);
    }
    /**
     * This method is called when user clicks on the Add to waitlist button
     *
     * @param view The calling view (button)
     */
    /*public void addToWaitlist(View view) {
            if (mNewGuestNameEditText.getText().length() == 0 ||
                    mNewPartySizeEditText.getText().length() == 0) {
                return;
            }
            //default party size to 1
            int partySize = 1;
            try {
                //mNewPartyCountEditText inputType="number", so this should always work
                partySize = Integer.parseInt(mNewPartySizeEditText.getText().toString());
            } catch (NumberFormatException ex) {
                Log.e(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
            }

            // Add guest info to mDb
            addNewGuest(mNewGuestNameEditText.getText().toString(), partySize);

        // Update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor(getAllGuests());

        //clear UI text fields
        mNewPartySizeEditText.clearFocus();
        mNewGuestNameEditText.getText().clear();
        mNewPartySizeEditText.getText().clear();
    }*/

    /**
     * Query the mDb and get all guests from the waitlist table
     *
     * @return Cursor containing the list of guests
     */
    private Cursor getAllGuests() {
        return mDb.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
    }

    /**
     * Adds a new guest to the mDb including the party count and the current timestamp
     *
     * @param name      Guest's name
     * @param partySize Number in party
     * @return id of new record added
     */


    private long addNewGuest(String name, int partySize) {
        ContentValues cv = new ContentValues();
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);
        return mDb.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);
    }


    // COMPLETED (1) Create a new function called removeGuest that takes long id as input and returns a boolean

    /**
     * Removes the record with the specified id
     *
     * @param id the DB id to be removed
     * @return True: if removed successfully, False: if failed
     */
    private boolean removeGuest(long id) {
        // COMPLETED (2) Inside, call mDb.delete to pass in the TABLE_NAME and the condition that WaitlistEntry._ID equals id
        return mDb.delete(WaitlistContract.WaitlistEntry.TABLE_NAME, WaitlistContract.WaitlistEntry._ID + "=" + id, null) > 0;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "add");
        menu.add(2, 2, 2, "setting");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            //mAdapter.swapCursor(getAllGuests());
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            setResult(AppCompatActivity.RESULT_OK, intent);
            startActivity(intent);
            mAdapter.swapCursor(getAllGuests());

        }
        if (item.getItemId() == 2) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    private void setupSharedPreference() {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
            loadColorFromPreferences(sharedPreferences); //有問題
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
    private void loadColorFromPreferences(SharedPreferences sharedPreferences){
        //需修改
    }

    public void setColor(String newColorKey) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_color_key))) {
            //loadColorFromPreferences(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}