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

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

    //this method updates the value of the LiveData object
    public void setTask(Task addition) {
        ArrayList<Task> newArr = addNewTask(addition);
        userTasks.setValue(newArr);
    }

    // updating a task with new information
    public void updateTask(Task oldTask, Task newTask) {
        ArrayList<Task> tasklist = userTasks.getValue();
        for (int i = 0; i < tasklist.size(); i++) {
            Task currentTask = tasklist.get(i);
            if (Task.getText(currentTask).equals(Task.getText(oldTask))
                    && Task.getDate(currentTask).equals(Task.getDate(oldTask))
                    && Task.getColor(currentTask).equals(Task.getColor(oldTask))) {
                tasklist.set(i,newTask);
            }
        }
        userTasks.setValue(tasklist);
        for (int i = 0; i < tasklist.size(); i++) {
            Task task = tasklist.get(i);
        }
    }

    // adding a new task to the list of tasks
    public ArrayList<Task> addNewTask(Task addition) {
        ArrayList<Task> oldarr = userTasks.getValue();
        ArrayList<Task> copyarr;
        if (oldarr == null) {
            copyarr = new ArrayList<Task>();
        } else {
            copyarr = new ArrayList<Task>(oldarr.size());
            for (int i = 0; i < oldarr.size(); i++) {
                copyarr.add(oldarr.get(i));
            }
        }
        copyarr.add(addition);
        return copyarr;
    }
}