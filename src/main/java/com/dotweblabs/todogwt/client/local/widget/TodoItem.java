package com.dotweblabs.todogwt.client.local.widget;

import com.dotweblabs.todogwt.client.local.event.Delete;
import com.dotweblabs.todogwt.client.local.event.Updated;
import com.dotweblabs.todogwt.client.local.model.Todo;
import com.dotweblabs.todogwt.client.local.model.TodoWrapper;
import com.dotweblabs.todogwt.client.local.repository.TodoRepository;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import org.jboss.errai.common.client.logging.util.Console;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.enterprise.event.Event;
import javax.inject.Inject;

@Templated
public class TodoItem extends Composite implements HasModel<Todo> {

    Todo model;

    @Inject
    @DataField("todo-check")
    CheckBox todoCheck;

    @Inject
    @DataField("todo-text")
    Label text;

    @Inject
    @DataField("todo-destroy")
    Anchor todoDestroy;

    @Inject
    TodoRepository repository;

    @Inject
    @Delete
    Event<TodoWrapper> delete;

    @Inject
    @Updated
    Event<TodoWrapper> updated;

    public Todo getModel() {
        return model;
    }

    public void setModel(Todo todoItem) {
        this.model = todoItem;
        this.text.setText(todoItem.getTodo());
    }

    @EventHandler("todo-check")
    public void check(ClickEvent event) {
        //event.preventDefault();
        if(this.getModel() != null) {
            Console.log(todoCheck.getValue() + "");
            this.model.setDone(todoCheck.getValue());
            if(todoCheck.getValue()) {
                this.getElement().addClassName("done");
            } else {
                this.getElement().removeClassName("done");
            }
            TodoWrapper todoWrapper = new TodoWrapper();

            int index = getElementIndex(this.getElement());

            todoWrapper.setIndex(index);
            todoWrapper.setTodo(this.getModel());
            updated.fire(todoWrapper);
        }
    }

    @EventHandler("todo-destroy")
    public void remove(ClickEvent event) {
        event.preventDefault();

        int index = getElementIndex(this.getElement());

        this.getElement().removeFromParent();

        if(index != -1) {
            repository.remove(index);
            TodoWrapper todoWrapper = new TodoWrapper();
            todoWrapper.setIndex(index);
            todoWrapper.setTodo(this.getModel());
            delete.fire(todoWrapper);
        }

    }

    private static native int getElementIndex(Element el)/*-{
        for (var i = 0; el = el.previousElementSibling; i++);
        return i;
    }-*/;
}
