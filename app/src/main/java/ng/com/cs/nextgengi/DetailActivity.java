package ng.com.cs.nextgengi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import ng.com.cs.nextgengi.database.DatabaseVirtualTable;



public class   DetailActivity extends ActionBarActivity {
    public String pquery;
    public String pquery2;
    private DatabaseVirtualTable db;
    public static Cursor cursor;
    public static Cursor cursor2;
    public static Cursor cursora;
    public static Cursor cursorb;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        db = new DatabaseVirtualTable(this);

        EditText clone = (EditText) findViewById(R.id.clone_on_detail);
        EditText syn1 = (EditText) findViewById(R.id.syn1_on_detail);
        EditText syn2 = (EditText) findViewById(R.id.syn2_on_detail);
        EditText index = (EditText) findViewById(R.id.index_on_detail);
        EditText cluster = (EditText) findViewById(R.id.cluster_on_detail);
        EditText group = (EditText) findViewById(R.id.group_on_detail);
        EditText pedigree = (EditText) findViewById(R.id.pedigree_on_detail);
        EditText female = (EditText) findViewById(R.id.female_on_detail);
        EditText male = (EditText) findViewById(R.id.male_on_detail);
        EditText selection = (EditText) findViewById(R.id.selection_on_detail);
        EditText chara = (EditText) findViewById(R.id.chara_on_detail);
        EditText released = (EditText) findViewById(R.id.released_on_detail);
        EditText additional = (EditText) findViewById(R.id.addition_on_detail);

        if (getIntent() != null) {
            String dataReceived[] = getIntent().getStringArrayExtra("DATA");

            clone.setText(dataReceived[0].trim());
            syn1.setText(dataReceived[1].trim());
            syn2.setText(dataReceived[2].trim());
            index.setText(dataReceived[3].trim());
            cluster.setText(dataReceived[4].trim());
            group.setText(dataReceived[5].trim());
            pedigree.setText(dataReceived[6].trim());
            //Checking to see if pedigree is also searchable;
            // male and female actually
            pquery = dataReceived[7].trim();
            pquery2 = dataReceived[8].trim();
            //String p="TMEB419";
            //cursor = db.getWordMatches(pquery, null);
            if(!TextUtils.isEmpty(pquery)) {
                // cursor = null;
                cursor = db.getItemSelected(pquery, null);
                if (cursor == null) {
                    female.setText(pquery);
                    female.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast  toast = Toast.makeText(getApplicationContext(), "Clone not available", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    });
                } else {
                    String udata = pquery;
                    SpannableString content = new SpannableString(udata);
                    content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
                    female.setText(content);
                    female.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            cursor.moveToFirst();

                            //cursora = db2.getItemSelected(pquery, null);
                            String dataRetrieved[] = new String[13];
                            for (int i = 0; i < dataRetrieved.length; i++) {
                                dataRetrieved[i] = cursor.getString(i);//cursor.moveToNext();
                            }
                            Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                            intent.putExtra("DATA", dataRetrieved);
                            startActivity(intent);
                        }
                    });
                }
            }else{
                female.setText("");
            }
            cursor2=null;
            //cursor2 = db.getWordMatches(pquery2, null);
            if(!TextUtils.isEmpty(pquery2)) {

                cursor2 = db.getItemSelected(pquery2, null);
                if (cursor2 == null) {
                    male.setText(pquery2);
                    male.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast toast = Toast.makeText(getApplicationContext(), "Clone not available", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    });
                } else {
                    String udata = pquery2;
                    SpannableString content = new SpannableString(udata);
                    content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
                    male.setText(content);
                    //male.setClickable(true);
                    male.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            cursor2.moveToFirst();
                            //cursora = db2.getItemSelected(pquery, null);
                            String dataRetrieved[] = new String[13];
                            for (int i = 0; i < dataRetrieved.length; i++) {
                                dataRetrieved[i] = cursor2.getString(i);
                                //cursor.moveToNext();
                            }
                            Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                            intent.putExtra("DATA", dataRetrieved);
                            startActivity(intent);
                        }
                    });

                }
            }else{
                male.setText("");

            }

            selection.setText(dataReceived[9].trim());
            released.setText(dataReceived[10].trim());
            chara.setText(dataReceived[11].replace("\"",""));
            additional.setText(dataReceived[12].replace("\"",""));
            String imagename=dataReceived[0];

            imagename=imagename.replaceAll("-","");
            imagename=imagename.replaceAll("\\p{P}", "");
            imagename=imagename.trim();
            imagename= imagename.toLowerCase();

            String imageString=imagename;
            // StringBuilder images = new StringBuilder(imagename);
            //String imageString=images.toString();

            //Toast.makeText(DetailActivity.this,imageString,Toast.LENGTH_LONG).show();

            String noimage="noimage";
            final int resId=getResources().getIdentifier(imageString,"drawable","ng.com.cs.nextgengi");
            final int noId=getResources().getIdentifier(noimage, "drawable", "ng.com.cs.nextgengi");
            final View myImage=findViewById(R.id.imagebutton);
            //myImage.setBackgroundResource(resId)


            //Toast.makeText(DetailActivity.this,resId,Toast.LENGTH_LONG).show();
            if (resId==0){
                myImage.setBackgroundResource(noId);
                myImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        zoomImageFromThumb(myImage, 0);

                    }
                });
            }
            else {
                myImage.setBackgroundResource(resId);
                myImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        zoomImageFromThumb(myImage, resId);

                    }
                });
            }
            myImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(resId<0){
                        zoomImageFromThumb(myImage,noId);
                    }
                    else {
                        zoomImageFromThumb(myImage, resId);
                    }
                }
            });


        }
    }


    private DatabaseVirtualTable db2=new DatabaseVirtualTable(this);
    public void clickMe()
    {
        cursor.moveToFirst();
        cursora = db2.getItemSelected(pquery, null);
        if (cursora!=null) {
            cursora.moveToFirst();
            String dataRetrieved[] = new String[13];
            for (int i = 0; i < dataRetrieved.length; i++) {
                dataRetrieved[i] = cursora.getString(i);
                //cursor.moveToNext();

            }
            Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
            intent.putExtra("DATA", dataRetrieved);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    public void clickMe2 ()
    {
        cursor2.moveToFirst();
        cursorb = db.getItemSelected(pquery2, null);//getting all the columns for the selected record

        if (cursorb!=null) {
            cursorb.moveToFirst();
            String dataRetrieved[] = new String[13];
            for (int i = 0; i < dataRetrieved.length; i++) {
                dataRetrieved[i] = cursorb.getString(i);
            }
            Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
            intent.putExtra("DATA", dataRetrieved);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else{
            Toast toast = Toast.makeText(DetailActivity.this, "No Result Found", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
       if (id == R.id.action_glossary) {
           Intent intent = new Intent(DetailActivity.this,glossary.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_key) {
            Intent intent = new Intent(DetailActivity.this,key.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

 /*  private boolean chkcam(){

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){

            snapphoto(0);
            return true;
        }
        else{
            return false;
        }
    }
    private void snapphoto(int TAKE_PICTURE){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePicture, TAKE_PICTURE);
        }
    }*/
    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(R.id.expanded_image);
        if(imageResId==0) {
            expandedImageView.setImageResource(R.drawable.noimage);
        }
        else{
            expandedImageView.setImageResource(imageResId);
        }
        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.maincontainer).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }
        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
        // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
        // and show the thumbnail instead of the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}

