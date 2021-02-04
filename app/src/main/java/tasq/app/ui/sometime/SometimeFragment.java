package tasq.app.ui.sometime;

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

public class SometimeFragment extends Fragment {

    private SometimeViewModel mViewModel;
    private AddEditViewModel model;

    public static SometimeFragment newInstance() {
        return new SometimeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sometime_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SometimeViewModel.class);
        model = new ViewModelProvider(requireActivity()).get(AddEditViewModel.class);
        model.getTask().observe(getViewLifecycleOwner(), item -> {
            Log.d("SOMETIME", "Got event");
        });
    }
}