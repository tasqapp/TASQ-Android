package tasq.app.ui;

import androidx.appcompat.widget.Toolbar;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import tasq.app.MainActivity;
import tasq.app.R;
import tasq.app.Task;

public class DisplayTask extends Fragment {

    private DisplayTaskViewModel mViewModel;
    private NavController navController ;

    public static DisplayTask newInstance() {
        return new DisplayTask();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.display_task_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DisplayTaskViewModel.class);
        Toolbar actionBar = ((MainActivity) getActivity()).findViewById(R.id.toolbar);
        actionBar.setTitle("Edit Task");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity()) ;

        Button submit = getActivity().findViewById(R.id.submitbutton);
        EditText date = getActivity().findViewById(R.id.due_date);
        EditText name = getActivity().findViewById(R.id.task_name_label);

        RadioButton red = getActivity().findViewById(R.id.redbutton);
        RadioButton blue = getActivity().findViewById(R.id.bluebutton);
        RadioGroup buttons = getActivity().findViewById(R.id.radiobuttons);

        date.setText(sp.getString("taskDate", "---")) ;
        name.setText(sp.getString("taskName", "---")) ;
        //TODO: fill in remaining attributes

        navController = Navigation.findNavController(getView()) ;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity()) ;
                SharedPreferences.Editor editor = sharedPreferences.edit() ;
                editor.putString("taskName", name.getText().toString()) ;
                editor.putString("taskDate", date.getText().toString()) ;
                int selectedId = buttons.getCheckedRadioButtonId();
                if(selectedId == red.getId()) {
                    editor.putString("taskColor", "Red") ;
                } else if (selectedId == blue.getId()) {
                    editor.putString("taskColor", "Green") ;
                } else {
                    editor.putString("taskColor", "Blue") ;
                }
                editor.apply() ;
                navController.navigateUp();
            }
        });
    }
}