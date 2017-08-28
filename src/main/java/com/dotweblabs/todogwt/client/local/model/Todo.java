package com.dotweblabs.todogwt.client.local.model;

public class Todo {

    private String objectId;
    private String todo;
    private Boolean isDone;

    public Todo(String todo, Boolean isDone) {
        setTodo(todo);
        setDone(isDone);
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
