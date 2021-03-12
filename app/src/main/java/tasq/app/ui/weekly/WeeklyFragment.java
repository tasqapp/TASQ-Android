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



package tasq.app.ui.weekly;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.media.SoundPool;
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
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tasq.app.Priority;
import tasq.app.R;
import tasq.app.Task;
import tasq.app.ui.addedit.AddEditViewModel;
import tasq.app.ui.monthly.MonthlyViewModel;

public class WeeklyFragment extends Fragment {

    private WeeklyViewModel mViewModel;
    private AddEditViewModel model;
    private ScrollView mScrollView;
    private NavController navController;
    private SoundPool.Builder poolBuilder ;
    private SoundPool pool ;
    private int taskFinishedSoundId ;


    public static WeeklyFragment newInstance() {
        return new WeeklyFragment();
    }

    /**
     * inflating the appropriate xml layout for the screen
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weekly_fragment, container, false);
    }

    /**
     * creating the view with and filling in pre-existing data, if any
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WeeklyViewModel.class);
        model = new ViewModelProvider(requireActivity()).get(AddEditViewModel.class);
        navController = Navigation.findNavController(getView());


        poolBuilder = new SoundPool.Builder() ;
        poolBuilder.setMaxStreams(1) ;
        pool = poolBuilder.build() ;
        taskFinishedSoundId = pool.load(getActivity(), R.raw.taskdonesound, 1) ;

        model.getTask().observe(getViewLifecycleOwner(), item -> {
            Log.d("WEEKLY", "Got event");
            updateUI(item);
        });

        // Show the current week
        String weekOfString = getFirstWeekDay() + " to " + getLastWeekDay();
        TextView headerWeekOf = getActivity().findViewById(R.id.weekly_text_weekOf);
        headerWeekOf.setText(getString(R.string.weekly_week_of) + " " + weekOfString);
    }

    /**
     * Updates the Fragment with the current week's tasks, separating by each weekday
     * @param tasks an ArrayList<Task> to find tasks in this week to display.
     */
    private void updateUI(ArrayList<Task> tasks) {
        ArrayList<Task> weekTasks = new ArrayList<>();
        LinearLayout layout = getActivity().findViewById(R.id.weekly_linear_view);
        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");

        Date weekStart, weekEnd, curTaskDate = null;
        Calendar cal = Calendar.getInstance();
        // Get the first day of the week
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        weekStart = cal.getTime();
        // Get the last day of the week
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        weekEnd = cal.getTime();

        for (Task task : tasks) {

            // Get the current task's day during the week
            try {
                Log.d("update-weekly", "Checking Task: " + Task.getText(task));
                curTaskDate = formatter.parse(Task.getDate(task));
            } catch (ParseException e) {
                e.printStackTrace();
                Log.w("update-weekly", "could not parse task date: " + Task.getDate(task));
                return;
            }

            // compare and add if the current task is during this week
            if (weekStart.compareTo(curTaskDate) * weekEnd.compareTo(curTaskDate) < 0) {
                weekTasks.add(task);
            }
        }

        Log.d("update-weekly", "start day: " + weekStart.toString());
        Log.d("update-weekly", "start day: " + weekEnd.toString());

        for (Task task :
                weekTasks) {
            // Make each task selectable to edit it's attributes.
            Button taskButton = new Button(getActivity());
            taskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Store the task's information in Shared preferences
                    // when changing intent to add/edit screen
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("taskName", Task.getText(task));
                    editor.putString("taskDate", Task.getDate(task));
                    editor.putString("taskColor", Task.getColor(task));
                    editor.putString("taskPriority",
                            Priority.getCapaitalizedStringFromPriority(task.getPriority()));
                    editor.putString("taskAddress", task.getAddress());
                    editor.apply();
                    navController.navigate(R.id.displayTask_page);
                    task.setText(sharedPreferences.getString("taskName", "---"));
                    task.setDate(sharedPreferences.getString("taskDate", "---"));
                    task.setColor(sharedPreferences.getString("taskColor", "---"));
                    task.setPriority(Priority.getPriorityFromString(
                            sharedPreferences.getString("taskPriority", "Low")));
                    task.setAddress(sharedPreferences.getString("taskAddress", ""));
                }
            });

            // set the text for the task and all lowercase
            taskButton.setAllCaps(false);
            taskButton.setText(Task.getText(task));

            // Create a checkbox for the task
            CheckBox ch = new CheckBox(getActivity());
            ch.setText("");

            //
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
            LinearLayout taskAndCheckbox = new LinearLayout(getActivity());
            taskAndCheckbox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            taskAndCheckbox.setOrientation(LinearLayout.HORIZONTAL);

            // Create listener for checkbox
            ch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Task newTask;
                    if(task.isCompleted()) {
                        newTask = new Task(Task.getColor(task),
                                Task.getDate(task),
                                Task.getText(task),
                                task.getPriority(),
                                false);
                    } else {
                        pool.play(taskFinishedSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
                        newTask = new Task(Task.getColor(task),
                                Task.getDate(task),
                                Task.getText(task),
                                task.getPriority(),
                                true);
                    }
                    layout.removeAllViews();
                    model.updateTask(task, newTask);
                }
            });

            // Scale checkbox to be larger and easy to view
            ch.setScaleX((float) 1.4);
            ch.setScaleY((float) 1.4);

            // Add object to LinearLayout
            taskAndCheckbox.addView(ch);
            taskAndCheckbox.addView(taskButton);
            layout.addView(taskAndCheckbox);
        }
    }

    // TODO: TEST & DOC returns the first day of the week (Sunday) in the current week in format "MM-dd"
    private String getLastWeekDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

        return sdf.format(cal.getTime());
    }

    // TODO: TEST & DOC returns the last day of the week (Saturday) in the current week in format "MM-dd"
    private String getFirstWeekDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

        return sdf.format(cal.getTime());
    }

}