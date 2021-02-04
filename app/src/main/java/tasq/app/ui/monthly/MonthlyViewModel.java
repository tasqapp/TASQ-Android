package tasq.app.ui.monthly;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MonthlyViewModel extends ViewModel {

    private MutableLiveData<String[]> newTask = new MutableLiveData<String[]>();

    public MonthlyViewModel() {
        this.newTask = newTask;
    }
    public MonthlyViewModel(MutableLiveData<String[]> newTask) {
        this.newTask = newTask;
    }

    public void setTask(String[] item) {
        newTask.setValue(item);
    }

    public LiveData<String[]> getTask() {
        return newTask;
    }
}