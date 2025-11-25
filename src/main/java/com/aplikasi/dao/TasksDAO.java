/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aplikasi.dao;

import com.aplikasi.model.Tasks;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author H P
 */
public class TasksDAO {
    private static final List<Tasks> allTasks = new ArrayList<>();
    private static int nextTasks_id = 1;
    
    public static void insertEntry(Tasks tasks){
        tasks.setTask_id(nextTasks_id++);
        allTasks.add(tasks);
    }
    
    public static List<Tasks> getAllTasks(){
        return new ArrayList<>(allTasks);
    }
    
    public static void updateEntry(Tasks updateTasks){
        for(int i = 0; i < allTasks.size(); i++){
            if(allTasks.get(i).getTask_id() == updateTasks.getTask_id());
            allTasks.set(i, updateTasks);
        }
    }
    
    public static void removeEntry(Tasks tasks){
        allTasks.remove(tasks);
    }
}
