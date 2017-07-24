package com.nikhilkoganti.easycontacts;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nikhilkoganti.easycontacts.dummy.DummyContent;

public class MainActivity extends AppCompatActivity implements NewContactFragment.OnFragmentInteractionListener, CallLogFragment.OnListFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /*    DatabaseStore database;
        Cursor cursor;
        ListView list;
        Button deleteAll;
        //    New_Contact Newct;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    /**
     * The main fragment containing a Contact list.
     */
    public static class MainFragment extends Fragment {
        private static final int REQUEST_CONTACT_PROVIDER = 36362;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        DatabaseStore database;
        Cursor cursor;
        ListView list;
        Button deleteAll;
        Context mContext;
        FragmentManager fm = getFragmentManager();
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(fm);
        private SimpleCursorAdapter adapter;
        private static final String ARG_SECTION_NUMBER = "section_number";
        // Defines the id of the loader for later reference
        public static final int CONTACT_LOADER_ID = 78; // From docs: A unique identifier for this loader. Can be whatever you want.

        public MainFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MainFragment newInstance(int sectionNumber) {
            MainFragment fragment = new MainFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mContext = getContext();
            database = new DatabaseStore(mContext);
            database.openDatabase();
            list = (ListView) rootView.findViewById(R.id.id_list);
            if ((ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                        REQUEST_CONTACT_PROVIDER);
            } else {
                Log.i("DB", "PERMISSION GRANTED");
            }
            deleteAll = (Button) rootView.findViewById(R.id.id_btndelete);
            deleteAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    alert.setMessage("Do You want to delete all records");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            database.deleteAllRecords();
                            updateListView();
                        }
                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateListView();
                        }
                    });
                    AlertDialog alertdia = alert.create();
                    alertdia.show();
                }
            });
            setupCursorAdapter();
            // Initialize the loader with a special ID and the defined callbacks from above
            getActivity().getSupportLoaderManager().initLoader(CONTACT_LOADER_ID,
                    new Bundle(), contactsLoader);
            list.setAdapter(adapter);
            return rootView;
        }

        // Create simple cursor adapter to connect the cursor dataset we load with a ListView
        private void setupCursorAdapter() {
            // Column data from cursor to bind views from
            String[] uiBindFrom = {ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_URI};
            // View IDs which will have the respective column data inserted
            int[] uiBindTo = {R.id.contact_name, R.id.contact_image};
            // Create the simple cursor adapter to use for our list
            // specifying the template to inflate (item_contact),
            adapter = new SimpleCursorAdapter(
                    mContext, R.layout.contact_item,
                    null, uiBindFrom, uiBindTo,
                    0);
        }

        // Defines the asynchronous callback for the contacts data loader
        private LoaderManager.LoaderCallbacks<Cursor> contactsLoader =
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    // Create and return the actual cursor loader for the contacts data
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        // Define the columns to retrieve
                        String[] projectionFields = new String[]{ContactsContract.Contacts._ID,
                                ContactsContract.Contacts.DISPLAY_NAME,
                                ContactsContract.Contacts.PHOTO_URI};
                        // Construct the loader
                        CursorLoader cursorLoader = new CursorLoader(mContext,
                                ContactsContract.Contacts.CONTENT_URI, // URI
                                projectionFields, // projection fields
                                null, // the selection criteria
                                null, // the selection args
                                null // the sort order
                        );
                        // Return the loader for use
                        return cursorLoader;
                    }

                    //
                    // When the system finishes retrieving the Cursor through the CursorLoader,
                    // a call to the onLoadFinished() method takes place.
                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                        // The swapCursor() method assigns the new Cursor to the adapter
                        adapter.swapCursor(cursor);
                    }

                    // This method is triggered when the loader is being reset
                    // and the loader data is no longer available. Called if the data
                    // in the provider changes and the Cursor becomes stale.
                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {
                        // Clear the Cursor we were using with another call to the swapCursor()
                        adapter.swapCursor(null);
                    }
                };

        private void updateListView() {
            cursor = database.getAllValues();
            ListAdapter ls = new ListAdapter() {
                @Override
                public void registerDataSetObserver(DataSetObserver observer) {

                }

                @Override
                public void unregisterDataSetObserver(DataSetObserver observer) {

                }

                @Override
                public int getCount() {
                    return 0;
                }

                @Override
                public Object getItem(int position) {
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public boolean hasStableIds() {
                    return false;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    return null;
                }

                @Override
                public int getItemViewType(int position) {
                    return 0;
                }

                @Override
                public int getViewTypeCount() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public boolean areAllItemsEnabled() {
                    return false;
                }

                @Override
                public boolean isEnabled(int position) {
                    return false;
                }
            };
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a MainFragment (defined as a static inner class below).

            switch (position) {
                case 0:

                    return MainFragment.newInstance(position + 1);

                case 1:

                    return NewContactFragment.newInstance("", "");

                case 2:

                    return CallLogFragment.newInstance(1);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
