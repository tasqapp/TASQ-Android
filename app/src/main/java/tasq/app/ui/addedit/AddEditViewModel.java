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
        ArrayList<Task> newArr = addNewTask(addition);
        userTasks.setValue(newArr);
    }

    // updating a task with new information
    public void updateTask(Task oldTask, Task newTask) {
        ArrayList<Task> favorites = userTasks.getValue();
        for (int i = 0; i < favorites.size(); i++) {
            Task currentTask = favorites.get(i);
            if (Task.getText(currentTask).equals(Task.getText(oldTask))
                    && Task.getDate(currentTask).equals(Task.getDate(oldTask))
                    && Task.getColor(currentTask).equals(Task.getColor(oldTask))) {
                Log.d("ADDEDIT", "In if statement");
                //Task task = new Task(Task.getColor(newTask), Task.getDate(newTask), Task.getText(newTask));
                favorites.set(i,newTask);
            }
        }
        userTasks.setValue(favorites);
        for (int i = 0; i < favorites.size(); i++) {
            Task task = favorites.get(i);
            Log.d("ARRAYLIST", "Item name:" + Task.getText(task));
        }
    }

    // adding a new task to the list of tasks
    public ArrayList<Task> addNewTask(Task addition) {
        ArrayList<Task> favorites = userTasks.getValue();
        ArrayList<Task> clonedFavs;
        if (favorites == null) {
            clonedFavs = new ArrayList<Task>();
        } else {
            clonedFavs = new ArrayList<Task>(favorites.size());
            for (int i = 0; i < favorites.size(); i++) {
                clonedFavs.add(favorites.get(i));
            }
        }
        clonedFavs.add(addition);
        int arrsize = clonedFavs.size();
        String size = String.valueOf(arrsize);
        Log.d("MODEL", "size equals " + size);
        return clonedFavs;
    }
}