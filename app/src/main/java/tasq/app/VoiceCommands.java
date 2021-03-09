package tasq.app;

import android.util.Log;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class VoiceCommands {
    public static final float MIN_CONFIDENCE = 0.5f;
    public static final String DEFAULT_STRING = "Daily";

    private ArrayList<String> fragments; //arrayList of all possible navigation destinations

    public VoiceCommands( ArrayList<String> newFragments ) {
        fragments = newFragments;
    }
    /**
     * Find the word from 'fragments' list (list of possible navigation destinations) that
     * matches with a possible word/string that the user has given via voice input.
     * Return match with highest confidence rating.
     */
    public String firstMatchWithMinConfidence( ArrayList<String> words,
                                               float [] confidLevels ) {
        //if no confidence levels or possible words are given, then return "Daily Page" as default
        if( words == null || confidLevels == null )
            return DEFAULT_STRING;
        //iterate through all possible input phrases and word/phrase in 'fragments' list &
        // look for a match
        for( int i = 0; i < words.size() && i < confidLevels.length; i++ ) {
            if( confidLevels[i] < MIN_CONFIDENCE )
                break;
            String word = words.get( i );
            for (int j =0; j <fragments.size(); j++) {
                String locations = fragments.get(j);
                //check if user input contains any of the screen names, if so return as a match
                if(word.toLowerCase().contains(locations.toLowerCase())) {
                    return locations.toLowerCase();
                }
            }
        }
        return DEFAULT_STRING;
    }
}

