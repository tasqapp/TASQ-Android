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
import android.location.Address;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import com.google.android.gms.maps.model.LatLng;

import tasq.app.AddressParser;
import tasq.app.MainActivity;
import tasq.app.Priority;
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
        RadioButton green = getActivity().findViewById(R.id.edit_greenbutton);
        RadioGroup buttons = getActivity().findViewById(R.id.edit_radiobuttons);
        EditText address = getActivity().findViewById(R.id.address_input);
        Spinner priority = getActivity().findViewById(R.id.edit_priority_spinner);

        String[] setDate = sp.getString("taskDate", "---").split("\\.") ;
        date.init(Integer.parseInt(setDate[2]), Integer.parseInt(setDate[0]) - 1, Integer.parseInt(setDate[1]), null);
        name.setText(sp.getString("taskName", "---"));

        String color = sp.getString("taskColor", "---");
        if (color.compareTo("Red") == 0) {
            red.toggle();
        } else if (color.compareTo("Green") == 0) {
            green.toggle();
        } else if (color.compareTo("Blue") == 0) {
            blue.toggle();
        }

        address.setText(sp.getString("taskAddress", ""));

        priority.setSelection(((ArrayAdapter)priority.getAdapter())
                .getPosition(sp.getString("taskPriority", "Low")));

        poolBuilder = new SoundPool.Builder() ;
        poolBuilder.setMaxStreams(1) ;
        pool = poolBuilder.build() ;
        updateFinishedSoundId = pool.load(getActivity(), R.raw.checkmarksound, 1) ;

        navController = Navigation.findNavController(getView());
        model = new ViewModelProvider(requireActivity()).get(AddEditViewModel.class);

        // creating listener for the submit button once user is done updating task
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressParser addressParser = new AddressParser();
                LatLng latLng;
                // updating task information
                String oldDate = sp.getString("taskDate", "---");
                String oldName = sp.getString("taskName", "---");
                String oldColor = sp.getString("taskColor", "---");
                String oldPriority = sp.getString("taskPriority", "Low");
                Priority oldPri = Priority.getPriorityFromString(oldPriority);
                Task oldTask = new Task(oldColor, oldDate, oldName, oldPri, false); //TODO: implement proper 'completed' field fetching/setting
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

                String userAddress = address.getText().toString();
                editor.putString("taskAddress", userAddress);

                editor.apply();

                //TODO: implement proper 'completed' field fetching/setting
                Task newTask;
                if (!userAddress.equals("")) {
                    Address address = addressParser.parseAddress(getContext(), userAddress);
                    if (address != null) {
                        latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        newTask = new Task(taskColor, updatedDate, name.getText().toString(), Priority.getPriorityFromString(priority.getSelectedItem().toString()), false, userAddress, latLng, address);
                    } else {
                        newTask = new Task(taskColor, updatedDate, name.getText().toString(), Priority.getPriorityFromString(priority.getSelectedItem().toString()), false, userAddress);
                    }
                } else {
                    newTask = new Task(taskColor, updatedDate, name.getText().toString(), Priority.getPriorityFromString(priority.getSelectedItem().toString()), false);
                }
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