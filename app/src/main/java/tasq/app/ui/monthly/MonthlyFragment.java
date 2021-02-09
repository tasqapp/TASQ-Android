package tasq.app.ui.monthly;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
//import androidx.lifecycle.ViewModelProviders;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tasq.app.MainActivity;
import tasq.app.R;
import tasq.app.Task;
import tasq.app.ui.addedit.AddEditViewModel;

public class MonthlyFragment extends Fragment {
    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    private MonthlyViewModel mViewModel;
    private AddEditViewModel model;
    List<Event> allEvents = new ArrayList<Event>();
    ArrayList<Task> allTasks = new ArrayList<Task>();

    public static MonthlyFragment newInstance() {
        return new MonthlyFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.monthly_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(MonthlyViewModel.class);
        mViewModel.getTask().observe(getViewLifecycleOwner(), item -> {
           //do nothing currently
        });

        model = new ViewModelProvider(requireActivity()).get(AddEditViewModel.class);
        model.getTask().observe(getViewLifecycleOwner(), item -> {
            Log.d("MONTHLY", "Got event");
            updateTaskList(item);
        });

        Toolbar actionBar = ((MainActivity) getActivity()).findViewById(R.id.toolbar);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#EBC91E"));
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(cal.getTime());
        SimpleDateFormat formatNowYear = new SimpleDateFormat("yyyy");
        java.util.Date nowDate = new java.util.Date();
        String currentYear = formatNowYear.format(nowDate);
        actionBar.setTitle(month_name + "- " + currentYear);

        compactCalendar = (CompactCalendarView) getActivity().findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);


        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getActivity().getApplicationContext();
                String toastText = "";
                int dateMatched = 0;
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
                        dateMatched++;
                        if(dateMatched==1) {
                            toastText = toastText + text;
                        } else {
                            toastText = toastText + ", " + text;
                        }
                    }
                }
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
            }

        });

    }

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
      public void newEvent(String [] taskInfo) {
        int eventColor;
        String color = taskInfo[0];
        String date = taskInfo[1];
        String text = taskInfo[2];
        if(color.equals("Red")) {
            eventColor = Color.RED;
        } else if (color.equals("Blue")) {
            eventColor = Color.BLUE;
        } else {
            eventColor = Color.GREEN;
        }
        long time = convertTime(date);
        Event ev1 = new Event(eventColor,time, text);
        allEvents.add(ev1);
        for (int i = 0; i < allEvents.size(); i++) {
            compactCalendar.addEvent(allEvents.get(i));
        }
    }

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
