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

package tasq.app.ui.daily;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import tasq.app.Priority;
import tasq.app.R;
import tasq.app.Task;
import tasq.app.TaskPriorityComparator;
import tasq.app.ui.addedit.AddEditViewModel;

public class DailyFragment extends Fragment {

    private AddEditViewModel model;
    private TextView dailyDate;
    private Date curDate;
    private Date displayDate;
    private String monthlyDate;

    private NavController navController;
    private SoundPool.Builder poolBuilder ;
    private SoundPool pool ;
    private int taskFinishedSoundId ;

    public static DailyFragment newInstance() {
        return new DailyFragment();
    }

    /**
     * inflating the appropriate layout when screen is being accessed
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.daily_fragment, container, false);
    }

    /**
     * creating references to the appropriate models and navcontroller
     * updating the screen with existing information
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(AddEditViewModel.class);
        navController = Navigation.findNavController(getView());
        curDate = new Date();

        poolBuilder = new SoundPool.Builder() ;
        poolBuilder.setMaxStreams(1) ;
        pool = poolBuilder.build() ;
        taskFinishedSoundId = pool.load(getActivity(), R.raw.taskdonesound, 1) ;
        //observer that listens for changes in Task list and updates UI every time task list changes
        model.getTask().observe(getViewLifecycleOwner(), item -> {
            updateUI(item);
        });
        //reformat date for display
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String monthDate = sharedPreferences.getString("dateSent", "---");
        DateFormat df = new SimpleDateFormat("EEE MMM dd");
        String formatted = "";
        //if date was sent from monthly screen, display date is this date, otherwise display date
        //is current day (set textView for date display to date)
        if(monthDate.compareTo("---") != 0) {
            monthlyDate = monthDate;
            SimpleDateFormat oldFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            DateFormat newFormat = new SimpleDateFormat("EEE MMM dd");
            try {
                formatted = newFormat.format(oldFormat.parse(monthDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dailyDate = getActivity().findViewById(R.id.daily_screen_date);
            dailyDate.setText(formatted);
            sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("dateSent", "---");
            editor.apply();
        } else {
            monthlyDate = null;
            Date date = new Date();
            dailyDate.setText(df.format(date));
        }
    }

    /**
     * method for updating the screen with any new or updated information
     */
    private void updateUI(ArrayList<Task> arr) {
        LinearLayout ll = getActivity().findViewById(R.id.daily_central_layout);
        ArrayList<Task> allTasks = new ArrayList<Task>(); //list of all user tasks
        String currentDate = new SimpleDateFormat("MM.dd.yyyy").format(new Date());
        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        Date curDate = null;
        Date monthDate = null;
        String monthString;
        //format monthly date for display if it is not null (not null if date sent from monthly page)
        if(monthlyDate != null) {
            SimpleDateFormat oldFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            SimpleDateFormat newFormat = new SimpleDateFormat("MM.dd.yyyy");
            try {
                monthString = newFormat.format(oldFormat.parse(monthlyDate));
                monthDate = formatter.parse(monthString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // generate list of tasks with date equal to dailyDate
        for (int i = 0; i < arr.size(); i++) {
            Task curTask = arr.get(i);
            String date = Task.getDate(curTask);
            if (date.compareTo("") != 0) {
                Date curTaskDate = null;
                try {
                    curTaskDate = formatter.parse(date);
                    curDate = formatter.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (monthlyDate != null) {
                    if (monthDate.toString().compareTo(curTaskDate.toString()) == 0) {
                        allTasks.add(curTask);
                    }
                } else {
                    if (curDate.toString().compareTo(curTaskDate.toString()) == 0) {
                        allTasks.add(curTask);
                    }
                }
            }

            // Sort the list of tasks according to their priority
            Collections.sort(allTasks, new TaskPriorityComparator());
        }

        // iterating through tasks and updating/adding them on the screen
        for (Task task : allTasks) {
            Button taskButton = new Button(getActivity());
            //on click listener that directs to display task screen when clicked
            taskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //used to give task information to Display screen
                    editor.putString("taskName", Task.getText(task));
                    editor.putString("taskDate", Task.getDate(task));
                    editor.putString("taskColor", Task.getColor(task));
                    editor.putString("taskPriority",
                            Priority.getCapaitalizedStringFromPriority(task.getPriority()));
                    editor.putString("taskAddress", task.getAddress());
                    editor.apply();
                    navController.navigate(R.id.displayTask_page);
                    //set task properties to updated properties from display task page
                    task.setText(sharedPreferences.getString("taskName", "---"));
                    task.setDate(sharedPreferences.getString("taskDate", "---"));
                    task.setColor(sharedPreferences.getString("taskColor", "---"));
                    task.setPriority(Priority.getPriorityFromString(
                            sharedPreferences.getString("taskPriority", "Low")));
                    task.setAddress(sharedPreferences.getString("taskAddress", ""));
                }
            });
            //formatting for task button
            taskButton.setAllCaps(false);
            taskButton.setText(Task.getText(task));
            CheckBox ch = new CheckBox(getActivity());
            ch.setText("");
            //if task is completed, then cross out text
            if(task.isCompleted() == true) {
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
            //checkbox listener (crosses out text for task if box not already checked)
            //un-crosses text if task box already clicked
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
                        pool.play(taskFinishedSoundId, 1.0f, 1.0f, 1, 0, 1.0f) ;
                        newTask = new Task(Task.getColor(task),
                                Task.getDate(task),
                                Task.getText(task),
                                task.getPriority(),
                                true);
                    }
                    //re-set views in scrollview to account for update
                    ll.removeAllViews();
                    //send updated tasks to model (to notify other screens of the update also)
                    model.updateTask(task, newTask);
                }
            });
            ch.setScaleX((float) 1.4);
            ch.setScaleY((float) 1.4);
            taskAndCheckbox.addView(ch);
            taskAndCheckbox.addView(taskButton);
            ll.addView(taskAndCheckbox);
        }
    }
}