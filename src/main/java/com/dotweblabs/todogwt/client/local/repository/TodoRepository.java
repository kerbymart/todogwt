package com.dotweblabs.todogwt.client.local.repository;

import com.dotweblabs.todogwt.client.local.model.Todo;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class TodoRepository {

    private Map<Integer, Todo> todoMap;

    public int count() {
        if(todoMap == null) {
            todoMap = new LinkedHashMap<Integer, Todo>();
        }
        return todoMap.size();
    }

    public int countNotDone() {
        int count = 0;
        if(todoMap == null) {
            todoMap = new LinkedHashMap<Integer, Todo>();
        }
        Iterator<Integer> it = todoMap.keySet().iterator();
        while(it.hasNext()) {
            Integer key = it.next();
            Todo value = todoMap.get(key);
            if(!value.getDone()) {
                count = count + 1;
            }
        }
        return count;
    }

    public void updateTodo(int index, Todo todo) {
        if(todoMap == null) {
            todoMap = new LinkedHashMap<Integer, Todo>();
        }
        todoMap.put(index, todo);
    }

    public void addTodo(Todo todo) {
        if(todoMap == null) {
            todoMap = new LinkedHashMap<Integer, Todo>();
        }
        int index = count();
        todoMap.put(index, todo);
    }

    public void setDone(int index, Boolean isDone) {
        if(todoMap == null) {
            return;
        }
        Todo todo = todoMap.get(index);
        if(todo != null) {
            todo.setDone(isDone);
            todoMap.put(index, todo);
        }
    }

    public void remove(int index) {
        if(todoMap != null) {
            todoMap.remove(index);
        }
    }

    public List<Todo> listTodo() {
        if(todoMap == null) {
            todoMap = new LinkedHashMap<Integer, Todo>();
        }
        List<Todo> todos = new LinkedList<Todo>();
        Iterator<Integer> it = todoMap.keySet().iterator();
        while(it.hasNext()) {
            Integer key = it.next();
            Todo value = todoMap.get(key);
            todos.add(value);
        }
        return todos;
    }
}
