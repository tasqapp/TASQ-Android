package tasq.app.ui.addedit;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;


import tasq.app.Task;

public class AddEditViewModel extends ViewModel {
    public MutableLiveData<ArrayList<Task>> userTasks = new MutableLiveData<ArrayList<Task>>();

    public AddEditViewModel() {
        userTasks = userTasks;
    }
    public AddEditViewModel(MutableLiveData<ArrayList<Task>> newTask) {
        userTasks = userTasks;
    }

    public LiveData<ArrayList<Task>> getTask() {
        return userTasks;
    }

    public void setTask(Task addition) {
        Log.d("MODEL", "In model");
       ArrayList<Task> newArr =  addNewTask(addition);
       userTasks.setValue(newArr);
    }

    public void updateTask(Task oldTask, Task newTask) {
        ArrayList<Task> favourites = userTasks.getValue();
        for (int i = 0; i < favourites.size(); i++) {
            Task currentTask = favourites.get(i);
            if (Task.getText(currentTask).equals(Task.getText(oldTask)) && Task.getDate(currentTask).equals(Task.getDate(oldTask)) && Task.getColor(currentTask).equals(Task.getColor(oldTask))) {
                Log.d("ADDEDIT", "In if statement");
                //Task task = new Task(Task.getColor(newTask), Task.getDate(newTask), Task.getText(newTask));
                favourites.add(newTask);
                favourites.remove(currentTask);
            }
        }
        userTasks.setValue(favourites);
        for (int i = 0; i < favourites.size(); i++) {
            Task task = favourites.get(i);
            Log.d("ARRAYLIST", "Item name:" + Task.getText(task));
        }
    }

    public ArrayList<Task> addNewTask(Task addition) {
        ArrayList<Task> favourites = userTasks.getValue();
        ArrayList<Task> clonedFavs;
        if (favourites == null) {
            clonedFavs = new ArrayList<Task>();
        } else {
            clonedFavs = new ArrayList<Task>(favourites.size());
            for (int i = 0; i < favourites.size(); i++) {
                clonedFavs.add(favourites.get(i));
            }
        }
        clonedFavs.add(addition);
        int arrsize = clonedFavs.size();
        String size = String.valueOf(arrsize);
        Log.d("MODEL", "size equals " + size);
        return clonedFavs;
    }
}