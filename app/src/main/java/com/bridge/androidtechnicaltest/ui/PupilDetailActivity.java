package com.bridge.androidtechnicaltest.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bridge.androidtechnicaltest.R;
import com.bridge.androidtechnicaltest.imagecache.ImageLoader;
import com.bridge.androidtechnicaltest.model.PupilDetails;


public class PupilDetailActivity extends AppCompatActivity {

    private ImageLoader mImgLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pupil_profile);
        mImgLoader = new ImageLoader(getApplicationContext());
        initUI();
    }

    private void initUI() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PupilDetails pupilDetails = null;
        Bundle b = getIntent().getExtras();
        if (b != null) {
            pupilDetails = (PupilDetails) b.getParcelable(PupilListFragment.BUNDLE_KEY_PUPIL_DETAILS);
        }

        if (pupilDetails != null) {

            getSupportActionBar().setTitle(pupilDetails.getName());

            ((TextView) findViewById(R.id.user_profile_name)).setText(pupilDetails.getName());
            ((TextView) findViewById(R.id.user_profile_short_bio)).setText(pupilDetails.getCountry());

            ((TextView) findViewById(R.id.user_loc_latitude)).setText(getString(R.string.latitude,
                    pupilDetails.getLatitude()));
            ((TextView) findViewById(R.id.user_loc_longitude)).setText(getString(R.string.longitude,
                    pupilDetails.getLongitude()));

            ImageButton userPic = (ImageButton) findViewById(R.id.user_profile_photo);

            // Load image into User Profile View
            mImgLoader.displayImage(pupilDetails.getImage(), userPic);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
