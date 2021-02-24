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
import android.widget.LinearLayout;

import java.util.ArrayList;

import tasq.app.R;
import tasq.app.Task;
import tasq.app.ui.addedit.AddEditViewModel;

public class SometimeFragment extends Fragment {

    private SometimeViewModel mViewModel;
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
        mViewModel = new ViewModelProvider(this).get(SometimeViewModel.class);
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
            // TODO: Decide how to determine a sometime task.
            Log.d("SOMEDAY", "null task date: " + Task.getDate(t));
            if (Task.getDate(t).compareTo("") == 0) {
                taskList.add(t);
            }
        }

        for (Task t : taskList) {
            Button taskButton = new Button(getActivity());
            taskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("taskName", Task.getDate(t));
                    editor.putString("taskColor", Task.getColor(t));
                    editor.apply();
                    navController.navigate(R.id.displayTask_page);
                    t.setText(sharedPreferences.getString("taskName", "--"));
                    t.setColor(sharedPreferences.getString("taskColor", "--"));
                }
            });

            taskButton.setText(Task.getText(t));
            taskButton.setTextColor(getResources().getColor(R.color.white));
            taskButton.setBackgroundResource(R.drawable.task_plain);
            taskButton.setTextAlignment(Button.TEXT_ALIGNMENT_VIEW_START);
            taskButton.setTextSize(25);
            taskButton.setPadding(70, 20,70, 20);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(30, 15, 140, 15);
            taskButton.setLayoutParams(lp);
            ll.addView(taskButton);
        }
    }
}