package ng.com.cs.nextgengi.database;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import ng.com.cs.nextgengi.R;
import ng.com.cs.nextgengi.SplashActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.StringTokenizer;


public class DatabaseVirtualTable {


    private static final String TAG = "DictionaryDatabase";
    public static String timetaken=null;
    //The columns we'll include in the cvis table
    public static final String COLUMN_ID="id";
    public static final String CLONE ="clone";
    public static final String SYN1 = "syn1";
    public static final String SYN2 = "syn2";
    public static final String INDEX ="ind";
    public static final String CLUSTER = "cluster";
    public static final String GROUP = "grou";
    public static final String PEDIGREE ="pedigree";
    public static final String FEMALE = "female";
    public static final String MALE = "male";
    public static final String SELECTION ="selection";
    public static final String RELEASED = "released";
    public static final String CHARA = "chara";
    public static final String ADDITIONALPROP = "additional";



    private static final String DATABASE_NAME = "CVIS";
    private static final String FTS_VIRTUAL_TABLE = "FTSCVIS";
    private static final int DATABASE_VERSION = 1;

    private final DatabaseOpenHelper mDatabaseOpenHelper;




    public DatabaseVirtualTable(Context context) {
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }

    //inner class to create the database
    private static class DatabaseOpenHelper extends SQLiteOpenHelper {


        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        //A strung to create the virtual table
        private static final String FTS_TABLE_CREATE =
                "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                        " USING fts3 (" +
                        CLONE + " TEXT, " +
                        SYN1 + " TEXT, " +
                        SYN2 + " TEXT, " +
                        INDEX + " TEXT, " +
                        CLUSTER + " TEXT, " +
                        GROUP + " TEXT, " +
                        PEDIGREE + " TEXT, " +
                        FEMALE  + " TEXT, " +
                        MALE + " TEXT, " +
                        SELECTION + " TEXT, " +
                        RELEASED + " TEXT," +
                        CHARA + " TEXT," +
                        ADDITIONALPROP + " TEXT)";

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL(FTS_TABLE_CREATE);
            loadRecords(); // loading the records into the virtual table
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
        }

        private void loadRecords() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        loadRecordsFromFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        private void loadRecordsFromFile() throws IOException {
            long begintime = (long)System.currentTimeMillis();
            final Resources resources = mHelperContext.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.newdata);
            // BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

            StringTokenizer recordFields=null;
            String line="";

            try {
                while ((line = reader.readLine()) != null) {
//                    Toast.makeText(mHelperContext,line, Toast.LENGTH_LONG).show();
                    recordFields = new StringTokenizer(line, ";");
                    //String[] recordFields = TextUtils.split(line, ";");
                    //Toast.makeText(mHelperContext, recordFields[0], Toast.LENGTH_LONG).show();
                    //if (recordFields.length < 13) continue;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(CLONE, recordFields.nextToken().trim().toUpperCase());
                    contentValues.put(SYN1, recordFields.nextToken().trim().toUpperCase());
                    contentValues.put(SYN2, recordFields.nextToken().trim().toUpperCase());
                    contentValues.put(INDEX, recordFields.nextToken().trim().toUpperCase());
                    contentValues.put(CLUSTER, recordFields.nextToken().trim().toUpperCase());
                    contentValues.put(GROUP, recordFields.nextToken().trim().toUpperCase());
                    contentValues.put(PEDIGREE, recordFields.nextToken().trim().toUpperCase());
                    contentValues.put(FEMALE, recordFields.nextToken().toUpperCase().trim());
                    contentValues.put(MALE, recordFields.nextToken().trim().toUpperCase().trim());
                    contentValues.put(SELECTION, recordFields.nextToken().trim().toUpperCase());
                    contentValues.put(RELEASED, recordFields.nextToken().trim().toUpperCase());
                    contentValues.put(CHARA, recordFields.nextToken().trim().toLowerCase());
                    contentValues.put(ADDITIONALPROP, recordFields.nextToken().trim().toLowerCase());
                    Log.e(TAG, "record added with clone: " + contentValues);
                    mDatabase.insert(FTS_VIRTUAL_TABLE, null, contentValues);

                    long endtime = (long)System.currentTimeMillis();
                    timetaken = Long.toString((endtime - begintime)/1000);

                    // SplashActivity.sp.edit().putInt("firstrun",1).commit();

                }

            }
            catch (Exception en){
                Log.v(TAG, "Error Occured " + en.toString());
            }
            finally {
                reader.close();
            }

        }
    }

    public Cursor getWordMatches(String query, String[] columns) {
        String selection = CLONE + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};

        return query(selection, selectionArgs, columns);
    }
    public Cursor getItemSelected(String item, String columns[]){
        String selection = CLONE + " = ?";
        String[] selectionArgs = new String[] {item};
        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);
        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        return cursor;
    }

}