package com.dotweblabs.todogwt.client.local.repository;

import com.dotweblabs.shape.client.HttpRequestException;
import com.dotweblabs.shape.client.Shape;
import com.dotweblabs.todogwt.client.local.model.Todo;
import com.google.gwt.json.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.errai.common.client.logging.util.Console;
import org.parseplatform.client.Parse;
import org.parseplatform.client.ParseObject;
import org.parseplatform.client.ParseResponse;
import org.parseplatform.client.Where;

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

    public void addTodo(final Todo todo, final AsyncCallback<Todo> callback) {
        if(todoMap == null) {
            todoMap = new LinkedHashMap<Integer, Todo>();
        }
        int index = count();
        todoMap.put(index, todo);
        ParseObject testObject = new ParseObject("Todo");
        testObject.put("todo", new JSONString(todo.getTodo()));
        testObject.put("isDone", JSONBoolean.getInstance(todo.getDone()));
        Parse.Objects.create(testObject, new AsyncCallback<ParseResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                HttpRequestException ex = (HttpRequestException) throwable;
                Console.log("POST Error: " + ex.getCode());
                callback.onFailure(throwable);
            }
            @Override
            public void onSuccess(ParseResponse parseResponse) {
                Console.log("POST Success objectId: " + parseResponse.getObjectId() + " createdAt: " + parseResponse.getCreatedAt());
                todo.setObjectId(parseResponse.getObjectId());
                callback.onSuccess(todo);
            }
        });
    }

    public void setDone(String objectId, Boolean isDone, final AsyncCallback<Todo> callback) {
        ParseObject testObject = new ParseObject("Todo");
        testObject.put("isDone", JSONBoolean.getInstance(isDone));
        testObject.setObjectId(objectId);
        Parse.Objects.update(testObject, new AsyncCallback<ParseResponse>() {
            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
            @Override
            public void onSuccess(ParseResponse parseResponse) {
                String objectId = parseResponse.getObjectId();
                String todo = parseResponse.get("todo").isString().stringValue();
                Boolean isDone = parseResponse.get("isDone").isBoolean().booleanValue();
                Todo t = new Todo(todo, isDone);
                callback.onSuccess(t);
            }
        });
    }

    @Deprecated
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

    public void remove(String objectId) {
        ParseObject ref = new ParseObject("Todo");
        ref.setObjectId(objectId);
        Parse.Objects.delete(ref, new AsyncCallback<ParseResponse>() {
            @Override
            public void onFailure(Throwable throwable) {

            }
            @Override
            public void onSuccess(ParseResponse parseResponse) {

            }
        });
    }

    public void listTodo(final AsyncCallback<List<Todo>> callback) {
        Shape.get("http://localhost:1337/parse/classes/Todo")
                .header("X-Parse-Application-Id", "myAppId")
                .header("X-Parse-REST-API-Key", "myRestAPIKey")
                .asJson(new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onFailure(throwable);
                    }

                    @Override
                    public void onSuccess(String s) {
                        Console.log("Query response: " + s);
                        List<Todo> todos = new LinkedList<Todo>();
                        JSONObject response =  JSONParser.parseStrict(s).isObject();
                        if(response != null) {
                            JSONArray results = response.get("results").isArray();
                            if(results != null) {
                                for(int i =0;i<results.size();i++) {
                                    JSONObject jsonObject = results.get(i).isObject();
                                    String objectId = jsonObject.get("objectId") != null ?
                                            jsonObject.get("objectId").isString().stringValue() : null;
                                    String todo = jsonObject.get("todo") != null ?
                                            jsonObject.get("todo").isString().stringValue() : null;
                                    Boolean isDone = jsonObject.get("isDone") != null ?
                                            jsonObject.get("isDone").isBoolean().booleanValue() : null;
                                    Todo t = new Todo(todo, isDone);
                                    t.setObjectId(objectId);
                                    todos.add(t);
                                }
                            }
                        }

                        callback.onSuccess(todos);
                    }
                });
//        ParseObject todoObject = Parse.Objects.extend("Todo");
//        Parse.Query query = Parse.Query.extend(todoObject);
//        Where where = new Where("isDone", JSONBoolean.getInstance(false));
//        query.where(where).find(new AsyncCallback<ParseResponse>() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                callback.onFailure(throwable);
//            }
//            @Override
//            public void onSuccess(ParseResponse parseResponse) {
//                List<Todo> todos = new LinkedList<Todo>();
//                JSONArray results = parseResponse.getResults();
//                if(results != null) {
//                    Console.log("Query result: "  + results.toString());
//                    try {
//                        for(int i =0;i<results.size();i++) {
//                            JSONObject jsonObject = results.get(i).isObject();
//                            String objectId = jsonObject.get("objectId").isString().stringValue();
//                            String todo = jsonObject.get("todo").isString().stringValue();
//                            Boolean isDone = jsonObject.get("isDone").isBoolean().booleanValue();
//                            Todo t = new Todo(todo, isDone);
//                            t.setObjectId(objectId);
//                            todos.add(t);
//                        }
//                    } catch (Exception e) {
//                        Console.log("Error: "  + e.getMessage());
//                    }
//                    callback.onSuccess(todos);
//                } else {
//                    Console.log(parseResponse.get("error").isString().stringValue());
//                }
//            }
//        });

    }
}
