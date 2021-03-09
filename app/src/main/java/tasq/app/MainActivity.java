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

import tasq.app.ui.addedit.AddEditViewModel;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private FloatingActionButton micFab;
    private FloatingActionButton addFab;
    private static final int PAGE_REQUEST = 1;
    public static VoiceCommands pages;
    private NavController navController;
    public NavigationView navView;
    private AddEditViewModel model;

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

        //Array List containing possible matches for what the user gives via voice commands
        //each option is a page option to navigate to
        ArrayList<String> fragments = new ArrayList<>();
        fragments.add("Daily");
        fragments.add( "Weekly");
        fragments.add( "Monthly");
        fragments.add( "Someday");
        fragments.add( "Add");
        pages = new VoiceCommands(fragments);

        /**
         * Check if user's device supports speech recoginition and set onClick listener for
         * every page's mic button
         */
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

        /**
         * Set on click listener for the "add task" button on every screen. If it is pressed, navigate to
         * the add/edit task page.
         */
        addFab = findViewById(R.id.addFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.add_edit_task);
            }
        });

        //if the screen is the add/edit task screen, hide the add task button
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

        /**
         * Set up listener for destination selection via the slide out menu. Navigate to screen
         * selected by user, unless the user is already on that screen, then just close the menu (drawer).
         */
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

    /**
     * Display intent that prompts user for a screen to navigate to and listens for user
     * voice input
     */
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
        //get list of possible words given by user input
        if( requestCode == PAGE_REQUEST && resultCode == RESULT_OK ) {
            ArrayList<String> returnedWords =
                    data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS );
            float [] scores = data.getFloatArrayExtra(
                    RecognizerIntent.EXTRA_CONFIDENCE_SCORES );
            //return matched word with highest confidence score (from list of possible phrases)
            String firstMatch =
                    pages.firstMatchWithMinConfidence( returnedWords, scores );
            // Create Intent for map
            String curDest = navController.getCurrentDestination().getLabel().toString() ;
            //navigate to page that user stated via voice command (unless already on that screen)
            switch(firstMatch)
            {
                case "weekly":
                    if(!curDest.equals("Weekly")) {
                        navController.navigate(R.id.weekly_page);
                    }
                    break;
                case "monthly":
                    if(!curDest.equals("Monthly")) {
                        navController.navigate(R.id.monthly_page);
                    }
                    break;
                case "someday":
                    if(!curDest.equals("Sometime")) {
                        navController.navigate(R.id.sometime_page);
                    }
                    break;
                case "add":
                    if(!curDest.equals("Add Task")) {
                        navController.navigate(R.id.add_edit_task);
                    }
                    break;
                case "daily":
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