package ng.com.cs.nextgengi;


import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import ng.com.cs.nextgengi.database.DatabaseVirtualTable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    //implements android.widget.SearchView.OnQueryTextListener{

    private ListView myList;
    private android.support.v7.widget.SearchView searchView;

    private ArrayList<String> nameList;
    public static Cursor cursor;
    private List<String> items;
    private DatabaseVirtualTable db;
    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseVirtualTable(this);
        setContentView(R.layout.activity_main);

        //relate the listView from java to the one created in xml
        myList = (ListView) findViewById(R.id.list);

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())){
            //a suggestion was clicked... do something about it...
            Toast.makeText(getApplicationContext(),intent.getDataString(),Toast.LENGTH_SHORT).show();
        }
        nameList = new ArrayList<>();
        myList = (ListView) findViewById(R.id.list);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this actvity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        // handleIntent(intent);

    }
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean onCreateOptionsMenu(Menu menu) {

        items = new ArrayList<>();
        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.menu = menu;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            SearchView search = (SearchView) menu.findItem(R.id.menu_search).getActionView();

            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            search.setIconifiedByDefault(false);
            search.setFocusable(true);
            search.setSubmitButtonEnabled(true);

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextChange(String query) {

                    query=query.toUpperCase();
                    loadHistory(query);

                    return true;

                }

                @Override
                public boolean onQueryTextSubmit(String thequery) {
                    String query = "*" + thequery + "*";
                    query = query.replaceAll("\\*", ".*");
                    Cursor cursor = db.getItemSelected(query, null);
                    if (cursor == null) {
                        Toast toast = Toast.makeText(MainActivity.this, "No Result Found", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        cursor.moveToFirst();
                        String dataRetrieved[] = new String[12];
                        for (int i = 0; i < dataRetrieved.length; i++) {
                            dataRetrieved[i] = cursor.getString(i);
                        }
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("DATA", dataRetrieved);
                        startActivity(intent);
                    }

                    return true;
                }
            });

        }

        return true;

    }

    // History
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadHistory(String query) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Cursor cursor = db.getWordMatches(query, null);
            if (cursor != null) {
                Log.d("Values", cursor.getString(0));
                //Toast.makeText(getApplicationContext(),cursor.getString(0),Toast.LENGTH_SHORT).show();
                if (items != null) items.clear();
                do {
                    items.add(cursor.getString(cursor.getColumnIndex(DatabaseVirtualTable.CLONE)));
                } while (cursor.moveToNext());

                if (TextUtils.isEmpty(query)) {
                    myList.clearTextFilter();

                } else {
                    myList.setFilterText(query);
                }
                CustomAdapter adapter = new CustomAdapter(this, items);

                adapter.getFilter().filter(query);
                myList.setAdapter(adapter);

                myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String item = parent.getItemAtPosition(position).toString();

                        Cursor cursor1 = db.getItemSelected(item, null);//getting all the columns for the selected record
                        if (cursor1 == null) {
                            Toast toast = Toast.makeText(MainActivity.this, "No Result Found", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            cursor1.moveToFirst();
                            String dataRetrieved[] = new String[13];
                            for (int i = 0; i < dataRetrieved.length; i++) {
                                dataRetrieved[i] = cursor1.getString(i);
                            }
                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                            intent.putExtra("DATA", dataRetrieved);
                            startActivity(intent);
                        }


                    }
                });
            }

        }
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.menu_search) {

            onSearchRequested();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }







}