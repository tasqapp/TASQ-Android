package tasq.app;

import android.util.Log;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class VoiceCommands {
    public static final String LOC_KEY = "page";
    public static final float MIN_CONFIDENCE = 0.5f;
    public static final String DEFAULT_STRING = "Daily";

    private ArrayList<String> fragments;

    public VoiceCommands( ArrayList<String> newFragments ) {
        fragments = newFragments;
    }

    public String firstMatchWithMinConfidence( ArrayList<String> words,
                                               float [] confidLevels ) {
        if( words == null || confidLevels == null )
            return DEFAULT_STRING;

        int numberOfWords = words.size( );
        Enumeration<String> screens;
        for( int i = 0; i < numberOfWords && i < confidLevels.length; i++ ) {
            if( confidLevels[i] < MIN_CONFIDENCE )
                break;
            String word = words.get( i );
            for (int j =0; j <fragments.size(); j++) {
                String locations = fragments.get(j);
                Log.d("LOCATION", locations);
                Log.d("WORD", word);
                if(word.toLowerCase().contains(locations.toLowerCase())) {
                    Log.d("FOUNDWORD", word);
                    Log.d("FOUNDWORD", locations);
                    return locations.toLowerCase();
                }
            }
        }
        return DEFAULT_STRING;
    }
}

