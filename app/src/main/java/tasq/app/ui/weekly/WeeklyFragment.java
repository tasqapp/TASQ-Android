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

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import tasq.app.R;
import tasq.app.Task;
import tasq.app.ui.addedit.AddEditViewModel;
import tasq.app.ui.monthly.MonthlyViewModel;

public class WeeklyFragment extends Fragment {

    private WeeklyViewModel mViewModel;
    private AddEditViewModel model;

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
        model.getTask().observe(getViewLifecycleOwner(), item -> {
            Log.d("WEEKLY", "Got event");
        });
    }

}