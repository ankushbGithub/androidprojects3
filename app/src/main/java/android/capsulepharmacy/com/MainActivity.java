package android.capsulepharmacy.com;

import android.capsulepharmacy.com.activity.AboutActivity;
import android.capsulepharmacy.com.activity.AdminMyOrders;
import android.capsulepharmacy.com.activity.LoginActivity;
import android.capsulepharmacy.com.activity.MyAccountActivity;
import android.capsulepharmacy.com.activity.MyHistory;
import android.capsulepharmacy.com.activity.MyOrders;
import android.capsulepharmacy.com.activity.OffersActivity;
import android.capsulepharmacy.com.activity.Support;
import android.capsulepharmacy.com.activity.TermsActivity;
import android.capsulepharmacy.com.base.BaseActivity;
import android.capsulepharmacy.com.customerModule.activity.CustomerListActivity;
import android.capsulepharmacy.com.fragment.HomeFragment;
import android.capsulepharmacy.com.notifications.NotificationUtils;
import android.capsulepharmacy.com.utility.AppConstants;
import android.capsulepharmacy.com.utility.Config;
import android.capsulepharmacy.com.utility.Prefs;
import android.capsulepharmacy.com.vendor.activity.VendorCreationActivity;
import android.capsulepharmacy.com.vendor.activity.VendorListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private int mContainerId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    TextView name;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContainerId = R.id.fragment_container;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        int mainScreen = 0;
        Intent i = getIntent();
        if (i != null) {
            mainScreen = i.getIntExtra("mainScreen", 0);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();


        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("admin")) {
            menu.findItem(R.id.nav_home).setVisible(true);
            menu.findItem(R.id.customer_creation).setVisible(true);
            menu.findItem(R.id.vendor_creation).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(true);
            menu.findItem(R.id.nav_myorder).setVisible(true);
            //  menu.findItem(R.id.nav_trackorder).setVisible(false);
            // menu.findItem(R.id.nav_reorder).setVisible(false);
        } else if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("vendor")){
            menu.findItem(R.id.nav_myorder).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(true);
            menu.findItem(R.id.nav_home).setVisible(true);
            //   menu.findItem(R.id.nav_trackorder).setVisible(true);
            //  menu.findItem(R.id.nav_reorder).setVisible(true);
        }else{
            menu.findItem(R.id.nav_home).setVisible(true);
            menu.findItem(R.id.nav_myorder).setVisible(true);
            menu.findItem(R.id.nav_trackorder).setVisible(true);
            menu.findItem(R.id.nav_reorder).setVisible(true);
            menu.findItem(R.id.support).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(true);
        }

        View viewHeader = navigationView.getHeaderView(0);
        LinearLayout viewProfile = (LinearLayout) viewHeader.findViewById(R.id.llProfile);
        name = viewHeader.findViewById(R.id.name);
        if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
            name.setText("Login or Register");
        } else {
            name.setText(Prefs.getStringPrefs(AppConstants.USER_NAME));
        }


        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    Intent i = new Intent(MainActivity.this, MyAccountActivity.class);
                    startActivity(i);
                }

            }
        });
        addFragment(new HomeFragment(), mContainerId);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                    String regId = pref.getString("regId", null);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                }
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (Prefs.getStringPrefs(AppConstants.USER_NAME).equalsIgnoreCase("")) {
                    name.setText("Login or Register");
                } else {
                    name.setText(Prefs.getStringPrefs("name"));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (menu != null) {

        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            replaceFragment(new HomeFragment(), mContainerId);
            // Handle the camera action
        } else if (id == R.id.nav_myorder) {
            Intent i = new Intent(mContext, AdminMyOrders.class);
            startActivity(i);
           /* if (Prefs.getStringPrefs("id").equalsIgnoreCase("")) {
               Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
            } else {
                if (Prefs.getStringPrefs("type").equalsIgnoreCase("admin")) {
                    Intent i  =new Intent(mContext, AdminMyOrders.class);
                    startActivity(i);
                }else {
                    Intent i  =new Intent(mContext, MyOrders.class);
                    startActivity(i);
                }

            }*/

        } else if (id == R.id.nav_trackorder) {
            Intent i = new Intent(mContext, MyOrders.class);
            startActivity(i);
           /* if (Prefs.getStringPrefs("id").equalsIgnoreCase("")) {
                Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
            } else {
                if (Prefs.getStringPrefs("type").equalsIgnoreCase("admin")) {
                    Intent i  =new Intent(mContext, AdminMyOrders.class);
                    startActivity(i);
                }else {
                    Intent i  =new Intent(mContext, MyOrders.class);
                    startActivity(i);
                }
            }*/

        } else if (id == R.id.nav_about) {
            Intent i = new Intent(mContext, AboutActivity.class);
            mContext.startActivity(i);
        } else if (id == R.id.nav_reorder) {
            Intent i = new Intent(mContext, MyHistory.class);
            startActivity(i);
           /* if (Prefs.getStringPrefs("id").equalsIgnoreCase("")) {
                Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
            } else {
                if (Prefs.getStringPrefs("type").equalsIgnoreCase("admin")) {
                    Intent i  =new Intent(mContext, AdminMyOrders.class);
                    startActivity(i);
                }else {
                    Intent i  =new Intent(mContext, MyOrders.class);
                    startActivity(i);
                }
            }*/
        } else if (id == R.id.nav_offers) {
            Intent i = new Intent(mContext, OffersActivity.class);
            mContext.startActivity(i);
        } else if (id == R.id.nav_tc) {
            Intent i = new Intent(mContext, TermsActivity.class);
            mContext.startActivity(i);
        } else if (id == R.id.nav_logout) {
            Prefs.clearSharedPrefs();
            Intent i = new Intent(mContext, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(i);
            finish();

        } else if (id == R.id.vendor_creation) {
            Intent i = new Intent(mContext, VendorListActivity.class);
            mContext.startActivity(i);
        } else if (id == R.id.customer_creation) {
            Intent i = new Intent(mContext, CustomerListActivity.class);
            mContext.startActivity(i);
        } else if (id == R.id.support) {
            Intent i = new Intent(mContext, Support.class);
            mContext.startActivity(i);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
