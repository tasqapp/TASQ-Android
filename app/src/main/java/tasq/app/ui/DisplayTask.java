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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

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
    private int updateFinishedSoundId ;

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

        Button submit = getActivity().findViewById(R.id.edit_submitbutton);
        DatePicker date = getActivity().findViewById(R.id.edit_date_picker);
        EditText name = getActivity().findViewById(R.id.edit_task_name_label);

        RadioButton red = getActivity().findViewById(R.id.edit_redbutton);
        RadioButton blue = getActivity().findViewById(R.id.edit_bluebutton);
        RadioGroup buttons = getActivity().findViewById(R.id.edit_radiobuttons);

        Spinner priority = getActivity().findViewById(R.id.edit_priority_spinner);

        String[] setDate = sp.getString("taskDate", "---").split("\\.") ;
        date.init(Integer.parseInt(setDate[2]), Integer.parseInt(setDate[0]) - 1, Integer.parseInt(setDate[1]), null);
        name.setText(sp.getString("taskName", "---"));

        String curPriority = sp.getString("taskPriority", "Low") ;
        switch(curPriority) {
            case "Low":
                priority.setSelection(0);
                break ;
            case "Medium":
                priority.setSelection(1);
                break ;
            case "High":
                priority.setSelection(2);
                break ;
            case "Extreme":
                priority.setSelection(3);
                break ;
        }

        poolBuilder = new SoundPool.Builder() ;
        poolBuilder.setMaxStreams(1) ;
        pool = poolBuilder.build() ;
        updateFinishedSoundId = pool.load(getActivity(), R.raw.checkmarksound, 1) ;

        //TODO: fill in remaining attributes (subtasks, files, etc.)

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
                String oldPriority = sp.getString("taskPriority", "Low") ;
                Task oldTask = new Task(oldColor, oldDate, oldName, false, oldPriority);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("taskName", name.getText().toString());

                String updatedDate = (date.getMonth()+1) + "." + date.getDayOfMonth() + "." + date.getYear() ;
                editor.putString("taskDate", updatedDate);

                editor.putString("taskPriority", priority.getSelectedItem().toString());
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
                        updatedDate,
                        name.getText().toString(),
                        false,
                        priority.getSelectedItem().toString());
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