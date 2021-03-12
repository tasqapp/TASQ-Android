/*
 * HANNAH BUZARD
 * DAVID KIPNIS
 * TYLER KJELDGAARD
 * DANIEL SHTUNYUK
 * <p>
 * WESTERN WASHINGTON UNIVERSITY
 * CSCI 412 - WINTER 2021
 * <p>
 * TASQ APPLICATION PROJECT
 */

package tasq.app.ui.addedit;

import android.location.Address;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.model.LatLng;

import kotlin.LateinitKt;
import tasq.app.AddressParser;
import tasq.app.Priority;
import tasq.app.R;
import tasq.app.Task;

public class AddEditFragment extends Fragment {

    // constants for tracking UI elements
    RadioButton red;
    RadioButton blue;
    RadioButton green;
    RadioGroup radio;

    DatePicker date;

    EditText address;

    EditText description;
    Button submit;
    Spinner prioritySpinner;

    // constants for tracking models and controllers
    private AddEditViewModel mViewModel;
    private NavController navController;

    private SoundPool.Builder poolBuilder;
    private SoundPool pool;
    private int updateFinishedSoundId;
    private CheckBox checkDate;

    //TODO: potentially delete
    public static AddEditFragment newInstance() {
        return new AddEditFragment();
    }

    // inflating the respective add/edit screen
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_edit_fragment, container, false);
    }

    // laying out and displaying the page appropriately
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        // finding respective buttons from the .xml file
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(AddEditViewModel.class);
        //find views and buttons by ID
        prioritySpinner = this.getActivity().findViewById(R.id.priority_spinner);
        submit = (Button) this.getActivity().findViewById(R.id.submitbutton);
        red = (RadioButton) this.getActivity().findViewById(R.id.redbutton);
        blue = (RadioButton) this.getActivity().findViewById(R.id.bluebutton);
        green = (RadioButton) this.getActivity().findViewById(R.id.greenbutton);
        radio = (RadioGroup) this.getActivity().findViewById(R.id.radiobuttons);
        description = (EditText) this.getActivity().findViewById(R.id.task_name_label);
        navController = Navigation.findNavController(getView());

        date = (DatePicker) this.getActivity().findViewById(R.id.add_date_picker);

        address = (EditText) this.getActivity().findViewById(R.id.address_input);

        poolBuilder = new SoundPool.Builder();
        poolBuilder.setMaxStreams(1);
        pool = poolBuilder.build();
        updateFinishedSoundId = pool.load(getActivity(), R.raw.checkmarksound, 1);
        checkDate = (CheckBox) this.getActivity().findViewById(R.id.no_due_date_check);

        // setting the listener for the submission button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressParser addressParser = new AddressParser();
                LatLng latLng;
                // updating new task information and setting task
                String selectedColor;
                int color = radio.getCheckedRadioButtonId();
                if (color == red.getId()) {
                    selectedColor = "Red";
                } else if (color == blue.getId()) {
                    selectedColor = "Blue";
                } else {
                    selectedColor = "Green";
                }
                String taskDesc = description.getText().toString();
                String userAddress = address.getText().toString();
                String dueDate = (date.getMonth() + 1) + "." + date.getDayOfMonth() + "." + date.getYear();
                Log.d("Date ", dueDate);

                String[] arr = {selectedColor, dueDate, taskDesc};

                Priority priority = Priority.getPriorityFromString((String) prioritySpinner.getSelectedItem());
                //add to global arrayList of tasks (using add/edit model)
                Task newTask;
                if(checkDate.isChecked()) {
                    dueDate = "";
                }
                if (!userAddress.equals("")) {
                    Address address = addressParser.parseAddress(getContext(), userAddress);
                    if (address != null) {
                        latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        newTask = new Task(selectedColor, dueDate, taskDesc, priority, false, userAddress, latLng, address);
                    } else {
                        newTask = new Task(selectedColor, dueDate, taskDesc, priority, false, userAddress);
                    }
                } else {
                    newTask = new Task(selectedColor, dueDate, taskDesc, priority, false);
                }
                //TODO: implement proper 'completed' field fetching/setting
                mViewModel.setTask(newTask);
                pool.play(updateFinishedSoundId, 0.2f, 0.2f, 1, 0, 1.0f);
                // Return to previous screen.
                navController.navigateUp();
            }
        });
    }


}