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

package tasq.app.ui.sometime;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;

import tasq.app.Priority;
import tasq.app.R;
import tasq.app.Task;
import tasq.app.TaskPriorityComparator;
import tasq.app.ui.addedit.AddEditViewModel;

public class SometimeFragment extends Fragment {

    private AddEditViewModel model;
    private NavController navController;

    public static SometimeFragment newInstance() {
        return new SometimeFragment();
    }

    /**
     * inflating the appropriate xml layout for the screen
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sometime_fragment, container, false);
    }

    /**
     * creating the view with and filling in pre-existing data, if any
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(AddEditViewModel.class);
        navController = Navigation.findNavController(getView());
        model.getTask().observe(getViewLifecycleOwner(), item -> {
            Log.d("SOMETIME", "Got event");
            updateUI(item);
        });
    }

    /**
     * method for updating the visuals of the current screen
     */
    private void updateUI(ArrayList<Task> list) {
        LinearLayout ll = getActivity().findViewById(R.id.sometime_scroll_view_linear_layout);
        ArrayList<Task> taskList = new ArrayList<>();

        for (Task t : list) {
            Log.d("SOMEDAY", "null task date: " + Task.getDate(t));
            if (Task.getDate(t).compareTo("") == 0) {
                taskList.add(t);
            }

            // Sort sometime tasks by priority
            Collections.sort(taskList, new TaskPriorityComparator());
        }
        for (Task task : taskList) {
            Button taskButton = new Button(getActivity());
            taskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("taskName", Task.getText(task));
                    editor.putString("taskDate", Task.getDate(task));
                    editor.putString("taskColor", Task.getColor(task));
                    editor.putString("taskPriority",
                            Priority.getCapaitalizedStringFromPriority(task.getPriority()));
                    editor.apply();
                    navController.navigate(R.id.displayTask_page);
                    task.setText(sharedPreferences.getString("taskName", "---"));
                    task.setDate(sharedPreferences.getString("taskDate", "---"));
                    task.setColor(sharedPreferences.getString("taskColor", "---"));
                    task.setPriority(Priority.getPriorityFromString(
                            sharedPreferences.getString("taskPriority", "Low")));
                }
            });
            taskButton.setAllCaps(false);
            taskButton.setText(Task.getText(task));
            CheckBox ch = new CheckBox(getActivity());
            ch.setText("");
            if(task.isCompleted()) {
                ch.setChecked(true);
                taskButton.setPaintFlags(taskButton.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            taskButton.setTextColor(getResources().getColor(R.color.white));
            taskButton.setBackgroundResource(R.drawable.task_plain);
            taskButton.setTextAlignment(Button.TEXT_ALIGNMENT_VIEW_START);
            taskButton.setTextSize(25);
            taskButton.setPadding(70, 20, 70, 20);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(30, 15, 30, 15);
            taskButton.setLayoutParams(lp);
            LinearLayout linear = new LinearLayout(getActivity());
            linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linear.setOrientation(LinearLayout.HORIZONTAL);
            ch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: add sound when task is completed
                    if(task.isCompleted()) {
                        Task newTask = new Task(Task.getColor(task),
                                Task.getDate(task),
                                Task.getText(task),
                                task.getPriority(),
                                false);
                        ll.removeAllViews();
                        model.updateTask(task, newTask);
                    } else {
                        Task newTask = new Task(Task.getColor(task),
                                Task.getDate(task),
                                Task.getText(task),
                                task.getPriority(),
                                true);
                        ll.removeAllViews();
                        model.updateTask(task, newTask);
                    }
                }
            });
            linear.addView(ch);
            linear.addView(taskButton);
            ll.addView(linear);
        }
    }
}