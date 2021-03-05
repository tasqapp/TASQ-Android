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

package tasq.app.ui.monthly;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
//import androidx.lifecycle.ViewModelProviders;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tasq.app.MainActivity;
import tasq.app.R;
import tasq.app.Task;
import tasq.app.TaskPriorityComparator;
import tasq.app.ui.addedit.AddEditViewModel;

public class MonthlyFragment extends Fragment {
    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth =
            new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
    private MonthlyViewModel mViewModel;
    private NavController navController;
    private AddEditViewModel model;
    List<Event> allEvents = new ArrayList<Event>();
    ArrayList<Task> allTasks = new ArrayList<Task>();

    public static MonthlyFragment newInstance() {
        return new MonthlyFragment();
    }

    /**
     * inflating the appropriate xml layout
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("DEBUG", "onCreateView of LoginFragment");
        return inflater.inflate(R.layout.monthly_fragment, container, false);
    }

    /**
     * creating the view with the calendar and filling in pre-existing data, if any
     */

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("DEBUG", "onActivityCreated of LoginFragment");
        navController = Navigation.findNavController(getView());
        // creating toolbar, calendar, and fetching appropriate dates
        Toolbar actionBar = ((MainActivity) getActivity()).findViewById(R.id.toolbar);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#EBC91E"));
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(cal.getTime());
        SimpleDateFormat formatNowYear = new SimpleDateFormat("yyyy");
        java.util.Date nowDate = new java.util.Date();
        String currentYear = formatNowYear.format(nowDate);
        actionBar.setTitle(month_name + ", " + currentYear);
        model = new ViewModelProvider(requireActivity()).get(AddEditViewModel.class);
        model.getTask().observe(getViewLifecycleOwner(), item -> {
            updateTaskList(item);
        });
        compactCalendar = (CompactCalendarView) getActivity()
                .findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        // creating listener for the daily buttons to act when the user selects a specific date
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getActivity().getApplicationContext();
                ArrayList<Task> dayTasks = new ArrayList<Task>();
                for (int i=0; i < allTasks.size(); i++) {
                    Task currentTask = allTasks.get(i);
                    String date = Task.getDate(currentTask);
                    String text = Task.getText(currentTask);
                    SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
                    Date currentDate = null;
                    try {
                        currentDate = formatter.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (dateClicked.toString().compareTo(currentDate.toString()) == 0) {
                        dayTasks.add(currentTask);
                    }
                }
                // adding textviews of tasks to bottom of relative layout

                Collections.sort(allTasks, new TaskPriorityComparator());

                RelativeLayout rl=(RelativeLayout) getActivity().findViewById(R.id.relativeview);
                LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.linear);
                ll.removeAllViews();
                ScrollView sv = new ScrollView(context);
                RelativeLayout.LayoutParams params =
                        new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, R.id.compactcalendar_view);
                sv.setLayoutParams(params);
                if(ll.getParent() != null) {
                    ((ViewGroup)ll.getParent()).removeView(ll);
                }
                sv.addView(ll);
                TextView label = new TextView(getActivity());
                label.setText("Your tasks for this day:");
                label.setTextSize(30);
                label.setTextColor(Color.parseColor("#EBC91E"));
                ll.addView(label);
                Button dailyButton = new Button(getActivity());
                dailyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("dateSent", dateClicked.toString());

                        editor.apply();
                        String test = sharedPreferences.getString("dateSent", "nope");
                        Log.d("INMONTHLY", test);
                        navController.navigate(R.id.daily_page);
                    }
                });
                dailyButton.setText("View on Daily Page");
                ll.addView(dailyButton);
                for (int i=0; i < dayTasks.size(); i++) {
                    Task task = dayTasks.get(i);
                    TextView b = new TextView(getActivity());
                    b.setText(Task.getText(task));
                    if(task.isCompleted() == true) {
                        b.setPaintFlags(b.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    b.setTextSize(30);
                    b.setTextColor(Color.parseColor("#EBC91E"));
                    ll.addView(b);
                }
                if(sv.getParent() != null) {
                    ((ViewGroup)sv.getParent()).removeView(sv);
                }
                rl.addView(sv);
            }

            // updating the appropriate month for when the user scrolls through the calendar
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
            }

        });

    }

    /**
     * updating the current task list to display appropriate tasks
     */
    public void updateTaskList(ArrayList<Task> arr) {
        allTasks = (ArrayList<Task>)arr.clone();
        for (int i=0; i < allTasks.size(); i++) {
            Task currentTask = allTasks.get(i);
            String date = Task.getDate(currentTask);
            String text = Task.getText(currentTask);
            int eventColor;
            String color = Task.getColor(currentTask);
            if (color.equals("Red")) {
                eventColor = Color.RED;
            } else if (color.equals("Blue")) {
                eventColor = Color.BLUE;
            } else {
                eventColor = Color.GREEN;
            }
            long time = convertTime(date);
            Event ev1 = new Event(eventColor, time, text);
            compactCalendar.addEvent(ev1);
            }
    }

    /**
     * method for converting the date into appropriate format
     */
    public Long convertTime(String Date)
    {
        long timeInMilliseconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy");
        try {
            Date mDate = sdf.parse(Date);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  timeInMilliseconds;
    }
}