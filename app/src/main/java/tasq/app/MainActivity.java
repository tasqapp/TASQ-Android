/**
 * HANNAH BUZARD
 * DAVID KIPNIS
 * TYLER KJELDGAARD
 * DANIEL SHTUNYUK
 *
 * WESTERN WASHINGTON UNIVERSITY
 * CSCI 412 - WINTER 2021
 *
 * TASQ APPLICATION PROJECT
 */

/**
 * Main Activity Class
 * Class in charge of instantiating and starting the application's main activity
 */

package tasq.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.Voice;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private FloatingActionButton micFab;
    private FloatingActionButton addFab;
    private static final int PAGE_REQUEST = 1;
    public static VoiceCommands pages;
    private NavController navController;
    public NavigationView navView;

    /**
     * Instantiating the app, and filling the navigation controller
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Add each top-level location to the builder.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.daily_page,
                R.id.weekly_page,
                R.id.monthly_page,
                R.id.sometime_page)
                .setDrawerLayout(drawer)
                .build();
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Mic button logic for voice commands

        ArrayList<String> fragments = new ArrayList<>();
        fragments.add("Daily page");
        fragments.add( "Weekly page");
        fragments.add( "Monthly page");
        fragments.add( "Someday page");
        fragments.add( "Add Task");
        pages = new VoiceCommands(fragments);

        micFab = findViewById(R.id.micFab);
        micFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Test if device supports speech recognition
                PackageManager manager = getPackageManager( );
                List<ResolveInfo> listOfMatches = manager.queryIntentActivities(
                        new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH ), 0 );
                if( listOfMatches.size( ) > 0 ) {
                    listen();
                } else { // speech recognition not supported
                    micFab.setEnabled( false );
                    Toast.makeText(MainActivity.this, "Sorry, your device does" +
                            "not support voice enabled commands.", Toast.LENGTH_LONG).show();
                }
            }
        });

        addFab = findViewById(R.id.addFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.add_edit_task);
            }
        });

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination,
                                             @Nullable Bundle arguments) {
                if (destination.getId() == R.id.add_edit_task) {
                    addFab.hide();
                } else {
                    addFab.show();
                }
            }
        });
        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                String selectedDest = menuItem.toString() ;
                String curDest = navController.getCurrentDestination().getLabel().toString() ;
                if(!selectedDest.equals(curDest)) {
                    int navId = 0;
                    switch (selectedDest) {
                        case "Daily":
                            navId = R.id.daily_page ;
                            break;
                        case "Monthly":
                            navId = R.id.monthly_page ;
                            break;
                        case "Weekly":
                            navId = R.id.weekly_page ;
                            break;
                        case "Sometime":
                            navId = R.id.sometime_page ;
                            break;
                    }
                    navController.navigate(navId) ;
                }
                drawer.close();
                return false ;
            }
        });
    }

    //Methods for voice command functionality
    public void startSpeaking( View v ) {
        listen( );
    }

    private void listen( ) {
        micFab.setEnabled( false );
        Intent listenIntent =
                new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        listenIntent.putExtra( RecognizerIntent.EXTRA_PROMPT, "Where can I add a task for you?");
        listenIntent.putExtra( RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        listenIntent.putExtra( RecognizerIntent.EXTRA_MAX_RESULTS, 5 );
        startActivityForResult( listenIntent, PAGE_REQUEST);
    }



    /**
     * inflating the appropriate menu on the start of the app
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    /**
     * Method for tracking user navigation activity
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        if( requestCode == PAGE_REQUEST && resultCode == RESULT_OK ) {
            ArrayList<String> returnedWords =
                    data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );
            float [] scores = data.getFloatArrayExtra(
                    RecognizerIntent.EXTRA_CONFIDENCE_SCORES );
            String firstMatch =
                    pages.firstMatchWithMinConfidence( returnedWords, scores );
            // Create Intent for map
            String curDest = navController.getCurrentDestination().getLabel().toString() ;
            switch(firstMatch)
            {
                case "weekly page":
                    if(!curDest.equals("Weekly")) {
                        navController.navigate(R.id.weekly_page);
                    }
                    break;
                case "monthly page":
                    if(!curDest.equals("Monthly")) {
                        navController.navigate(R.id.monthly_page);
                    }
                    break;
                case "someday page":
                    if(!curDest.equals("Sometime")) {
                        navController.navigate(R.id.sometime_page);
                    }
                    break;
                case "add task":
                    if(!curDest.equals("Add Task")) {
                        navController.navigate(R.id.add_edit_task);
                    }
                    break;
                case "daily page":
                    if(!curDest.equals("Daily")) {
                        navController.navigate(R.id.daily_page);
                    }
                    break;
                default:
                    Toast.makeText(MainActivity.this, "Sorry, I did not understand" +
                            " that. Please try again.", Toast.LENGTH_LONG).show();
            }
        }
        micFab.setEnabled( true );
    }

}