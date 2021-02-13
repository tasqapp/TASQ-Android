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

import java.text.SimpleDateFormat;
import java.util.Date ;
import java.util.Locale;

import tasq.app.R;
import tasq.app.ui.addedit.AddEditViewModel;

public class DailyFragment extends Fragment {

    private DailyViewModel mViewModel;
    private AddEditViewModel model;

    private TextView dailyDate ;

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

        model.getTask().observe(getViewLifecycleOwner(), item -> {
            Log.d("DAILY", "Got event");
        });

        dailyDate = getActivity().findViewById(R.id.daily_screen_date) ;
        String curDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date()) ;
        dailyDate.setText(curDate);
    }

    //add buttons to the scrollView

}