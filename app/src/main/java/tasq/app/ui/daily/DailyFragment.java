package tasq.app.ui.daily;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date ;
import java.util.Locale;

import tasq.app.R;
import tasq.app.Task;
import tasq.app.ui.addedit.AddEditViewModel;

public class DailyFragment extends Fragment {

    private DailyViewModel mViewModel;
    private AddEditViewModel model;
    private TextView dailyDate ;
    private ArrayList<Task> allTasks = new ArrayList<Task>() ;
    private Date curDate ;

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
        curDate = new Date() ;
        model.getTask().observe(getViewLifecycleOwner(), item -> {
            Log.d("DAILY", "Got event");
            updateUI(item) ;
        });
        dailyDate = getActivity().findViewById(R.id.daily_screen_date) ;
        //TODO: make sure the date displayed is the date of the current day selected
        dailyDate.setText(curDate.toString());
    }

    private void updateUI(ArrayList<Task> arr) {
        curDate = new Date() ;
        for (int i = 0 ; i < arr.size() ; i++) {
            Task curTask = arr.get(i) ;
            String date = Task.getDate(curTask) ;
            SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy") ;
            Date curTaskDate = null ;
            try {
                curTaskDate = formatter.parse(date) ;
                curDate = formatter.parse(date) ;
            } catch (ParseException e){
                e.printStackTrace() ;
            }
            Log.d("DAILY", curDate.toString() + " " + curTaskDate.toString()) ;
            if(curDate.equals(curTaskDate.toString())) {
                allTasks.add(curTask) ;
            }
        }
        System.out.println(allTasks.size());
    }
}