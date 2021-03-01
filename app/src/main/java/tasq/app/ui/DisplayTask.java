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

package tasq.app.ui;

import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import tasq.app.MainActivity;
import tasq.app.R;
import tasq.app.Task;
import tasq.app.ui.addedit.AddEditViewModel;

public class DisplayTask extends Fragment {

    private DisplayTaskViewModel mViewModel;
    private NavController navController;
    private AddEditViewModel model;

    private SoundPool.Builder poolBuilder ;
    private SoundPool pool ;
    private int updateFinishedSoundId;

    public static DisplayTask newInstance() {
        return new DisplayTask();
    }

    /**
     * inflating the appropriate xml layout for the screen
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.display_task_fragment, container, false);
    }

    /**
     * creating references to the models and finding UI elements
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Task oldTask;
        Task newTask;
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DisplayTaskViewModel.class);
        Toolbar actionBar = ((MainActivity) getActivity()).findViewById(R.id.toolbar);
        actionBar.setTitle("Edit Task");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Button submit = getActivity().findViewById(R.id.submitbutton);
        EditText date = getActivity().findViewById(R.id.due_date);
        EditText name = getActivity().findViewById(R.id.task_name_label);

        RadioButton red = getActivity().findViewById(R.id.redbutton);
        RadioButton blue = getActivity().findViewById(R.id.bluebutton);
        RadioGroup buttons = getActivity().findViewById(R.id.radiobuttons);

        date.setText(sp.getString("taskDate", "---"));
        name.setText(sp.getString("taskName", "---"));

        poolBuilder = new SoundPool.Builder() ;
        poolBuilder.setMaxStreams(1) ;
        pool = poolBuilder.build() ;
        updateFinishedSoundId = pool.load(getActivity(), R.raw.checkmarksound, 1) ;


        //TODO: fill in remaining attributes

        navController = Navigation.findNavController(getView());
        model = new ViewModelProvider(requireActivity()).get(AddEditViewModel.class);

        // creating listener for the submit button once user is done updating task
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // updating task information
                String oldDate = sp.getString("taskDate", "---");
                String oldName = sp.getString("taskName", "---");
                String oldColor = sp.getString("taskColor", "---");
                Task oldTask = new Task(oldColor, oldDate, oldName, false); //TODO: implement proper 'completed' field fetching/setting
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("taskName", name.getText().toString());
                editor.putString("taskDate", date.getText().toString());
                int selectedId = buttons.getCheckedRadioButtonId();
                String taskColor;
                if (selectedId == red.getId()) {
                    taskColor = "Red";
                    editor.putString("taskColor", "Red");
                } else if (selectedId == blue.getId()) {
                    taskColor = "Blue";
                    editor.putString("taskColor", "Blue");
                } else {
                    taskColor = "Green";
                    editor.putString("taskColor", "Green");
                }
                editor.apply();
                Task newTask = new Task(taskColor,
                        date.getText().toString(),
                        name.getText().toString(),
                        false); //TODO: implement proper 'completed' field fetching/setting
                if (newTask != null && oldTask != null) {
                    Log.d("DISPLAY", "Task text: " + taskColor);
                    Log.d("DISPLAY", "Task text: " + Task.getColor(oldTask));
                } else {
                    Log.d("DISPLAY", "Null something ");
                }

                // setting new information, and returning to previous screen
                pool.play(updateFinishedSoundId, 0.2f, 0.2f, 1,0, 1.0f) ;
                model.updateTask(oldTask, newTask);
                navController.navigateUp();
            }
        });
    }
}