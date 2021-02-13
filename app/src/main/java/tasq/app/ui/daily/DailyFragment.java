package tasq.app.ui.daily;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import tasq.app.R;
import tasq.app.Task;
import tasq.app.ui.addedit.AddEditViewModel;

public class DailyFragment extends Fragment {

    private DailyViewModel mViewModel;
    private AddEditViewModel model;
    private TextView dailyDate ;
    private Date curDate ;

    private NavController navController ;

    public static DailyFragment newInstance() {
        return new DailyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.daily_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(DailyViewModel.class);
        model = new ViewModelProvider(requireActivity()).get(AddEditViewModel.class);
        navController = Navigation.findNavController(getView()) ;
        curDate = new Date() ;
        model.getTask().observe(getViewLifecycleOwner(), item -> {
            Log.d("DAILY", "Got event");
            updateUI(item) ;
        });
        dailyDate = getActivity().findViewById(R.id.daily_screen_date) ;
        //TODO: make sure the date displayed is the date of the current day selected
        dailyDate.setText(curDate.toString());
    }
    //TODO: reformat daily date to only display month, year
    private void updateUI(ArrayList<Task> arr) {
        LinearLayout ll = getActivity().findViewById(R.id.daily_central_layout) ;
        ArrayList<Task> allTasks = new ArrayList<Task>() ;
        curDate = new Date() ;
        for (int i = 0 ; i < arr.size() ; i++) {
            Task curTask = arr.get(i) ;
            String date = Task.getDate(curTask) ;
            SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy") ;
            Date curTaskDate = null ;
            try {
                curTaskDate = formatter.parse(date) ;
                curDate = formatter.parse(date) ;
            } catch (ParseException e) {
                e.printStackTrace() ;
            }
            if(curDate.toString().compareTo(curTaskDate.toString()) == 0) {
                allTasks.add(curTask);
                Log.d("DAILY", "Size is " + allTasks.size());
            }
        }
        for (Task task : allTasks) {
            Button taskButton = new Button(getActivity()) ;
            taskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity()) ;
                    SharedPreferences.Editor editor = sharedPreferences.edit() ;
                    editor.putString("taskName", Task.getText(task)) ;
                    editor.putString("taskDate", Task.getDate(task)) ;
                    editor.putString("taskColor", Task.getColor(task)) ;
                    editor.apply() ;
                    navController.navigate(R.id.displayTask_page) ;
                    task.setText(sharedPreferences.getString("taskName", "---")) ;
                    task.setDate(sharedPreferences.getString("taskDate", "---")) ;
                    task.setColor(sharedPreferences.getString("taskColor", "---")) ;
                }
            });
            //TODO: finish button visuals
            taskButton.setText(Task.getText(task)) ;
            taskButton.setBackgroundResource(R.drawable.task_plain) ;
            taskButton.setWidth(LinearLayout.LayoutParams.MATCH_PARENT) ;
            taskButton.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT) ;
            //taskButton.
            ll.addView(taskButton) ;
        }
    }
}