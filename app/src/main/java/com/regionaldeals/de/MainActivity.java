package com.regionaldeals.de;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.regionaldeals.de.fragment.Main;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    private Context context;
    private Activity activity;
    private int userId = 0;
    public static final int LOGIN_REQUEST_CODE = 1;
    private TextView emailMenu;
    private static boolean shouldRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        activity = this;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new Main();
        fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
        fragmentTransaction.commit();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);

        ImageView loginImg = (ImageView) header.findViewById(R.id.imageView);
        loginImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
                shouldRefresh = true;
            }
        });

        emailMenu = (TextView)header.findViewById(R.id.textemail);

        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
        String restoredText = prefs.getString("userObject", null);
        if (restoredText != null) {
            try {
                JSONObject obj = new JSONObject(restoredText);
                userId = obj.getInt("userId");
                String email = obj.getString("email");
                emailMenu.setText(email);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Throwable t) {
            }
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_favourite) {
                    ViewPager vp = fragment.getView().findViewById(R.id.view_pager);
                    vp.setCurrentItem(5);
                } else if (id == R.id.nav_ort) {
                    Intent startActivityIntent = new Intent(MainActivity.this, GooglePlacesAutocompleteActivity.class);
                    startActivity(startActivityIntent);
                }
                else if (id == R.id.nav_benachrichtigungen) {
                //    fragment = new Deals();
                }
                else if (id == R.id.nav_einstellungen) {
                    //    fragment = new Deals();
                }
                else if (id == R.id.nav_hilfe) {
                //    fragment = new Deals();
                }
                else if (id == R.id.nav_uberDealSpok) {
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_appTeilen) {
                //    fragment = new Deals();
                    ShareCompat.IntentBuilder.from(activity)
                            .setType("text/plain")
                            .setChooserTitle("Chooser title")
                            .setText("http://play.google.com/store/apps/details?id=" + activity.getPackageName())
                            .startChooser();
                }
                else if (id == R.id.abo_buchen) {
                    SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
                    String restoredText = prefs.getString("userObject", null);
                    if (restoredText != null) {
                        Intent intent = new Intent(MainActivity.this, SubscribeActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivityForResult(intent, LOGIN_REQUEST_CODE);
                        shouldRefresh = true;
                    }

                }
                else if (id == R.id.meine_anzeigen) {
                    SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
                    String restoredText = prefs.getString("userObject", null);
                    if (restoredText != null) {
                        Intent startActivityIntent = new Intent(MainActivity.this, CreateDealsActivity.class);
                        startActivity(startActivityIntent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivityForResult(intent, LOGIN_REQUEST_CODE);
                        shouldRefresh = true;
                    }
                }
                else if (id == R.id.meine_gutscheien) {
                    SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
                    String restoredText = prefs.getString("userObject", null);
                    if (restoredText != null) {
                        Intent startActivityIntent = new Intent(MainActivity.this, CreateGutscheineActivity.class);
                        startActivity(startActivityIntent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivityForResult(intent, LOGIN_REQUEST_CODE);
                        shouldRefresh = true;
                    }
                }
                else if (id == R.id.anzeigen_erstellen) {
                    SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
                    String restoredText = prefs.getString("userObject", null);
                    if (restoredText != null) {
                        Intent startActivityIntent = new Intent(MainActivity.this, ShopActivity.class);
                        startActivity(startActivityIntent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivityForResult(intent, LOGIN_REQUEST_CODE);
                        shouldRefresh = true;
                    }
                }
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container_wrapper, fragment);
                transaction.commit();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                assert drawer != null;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(shouldRefresh){
            SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.sharedPredName), MODE_PRIVATE);
            String restoredText = prefs.getString("userObject", null);
            if (restoredText != null) {
                try {
                    JSONObject obj = new JSONObject(restoredText);
                    userId = obj.getInt("userId");
                    String email = obj.getString("email");
                    emailMenu.setText(email);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Throwable t) {
                }
            }
        }

    }


    public void setShouldRefresh(boolean val) {
        shouldRefresh = val;
    }


    public boolean getShouldRefresh() {
        return shouldRefresh;
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String email = data.getStringExtra("userEmail");
                emailMenu.setText(email);
            }
        }
    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
}
