package io.iemdevs.apnalibrary.ui.activities;

import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.iemdevs.apnalibrary.R;
import io.iemdevs.apnalibrary.ui.adapters.ViewPagerAdapter;
import io.iemdevs.apnalibrary.ui.fragments.ExploreFragment;
import io.iemdevs.apnalibrary.ui.fragments.MyBooksFragment;
import io.iemdevs.apnalibrary.ui.fragments.UpdatesFragment;
import io.iemdevs.apnalibrary.utils.Config;

/**
 * Main Library Activity, containing three fragments
 */

public class LibraryActivity extends AppCompatActivity {

    // UI Elements are dynamically bound by Butterknife, which saves us a lot of boilerplate
    @Bind(R.id.toolbar) public Toolbar toolbar;
    @Bind(R.id.tabs) public TabLayout tabLayout;
    @Bind(R.id.viewpager) public ViewPager viewPager;

    private Drawer navigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationDrawer.openDrawer();
            }
        });
        setNavigationDrawer();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Method to setup the View Pager
     * View Pager is used to display fragments in one activity. Notice we are adding each
     * fragment in an adapter here
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UpdatesFragment(), Config.Fragments.UPDATES);
        adapter.addFragment(new MyBooksFragment(), Config.Fragments.MYBOOKS);
        adapter.addFragment(new ExploreFragment(), Config.Fragments.EXPLORE);
        viewPager.setAdapter(adapter);
    }

    /**
     * Method to set Navigation Drawer
     * Uses library https://github.com/mikepenz/MaterialDrawer
     */
    private void setNavigationDrawer() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ProfileDrawerItem profileDrawerItem;

                profileDrawerItem = new ProfileDrawerItem()
                        .withName("John Davis")
                        .withEmail("john.davis@gmail.com")
                        .withIcon(getResources()
                                .getDrawable(R.drawable.ic_person_pink_600_48dp));
                // Create the AccountHeader
                AccountHeader headerResult = new AccountHeaderBuilder()
                        .withActivity(LibraryActivity.this)
                        .addProfiles(profileDrawerItem)
                        .withHeaderBackground(R.drawable.header)
                        .withAlternativeProfileHeaderSwitching(false)
                        .withCurrentProfileHiddenInList(true)
                        .withResetDrawerOnProfileListClick(false)
                        .withSelectionFirstLineShown(false)
                        .withSelectionListEnabledForSingleProfile(true)
                        .build();

                navigationDrawer = new DrawerBuilder()
                        .withActivity(LibraryActivity.this)
                        .withAccountHeader(headerResult)
                        .build();
            }
        }, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_library, menu);
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
}
