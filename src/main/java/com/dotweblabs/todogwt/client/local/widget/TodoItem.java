package com.dotweblabs.todogwt.client.local.widget;

import com.dotweblabs.todogwt.client.local.event.Delete;
import com.dotweblabs.todogwt.client.local.event.Updated;
import com.dotweblabs.todogwt.client.local.model.Todo;
import com.dotweblabs.todogwt.client.local.model.TodoWrapper;
import com.dotweblabs.todogwt.client.local.repository.TodoRepository;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
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
    @DataField("edit-text")
    TextBox editText;

    @Inject
    TodoRepository repository;

    @Inject
    @Delete
    Event<TodoWrapper> delete;

    @Inject
    @Updated
    Event<TodoWrapper> updated;

    @Override
    protected void onAttach() {
        super.onAttach();
        final int index = getElementIndex(this.getElement());
        final Element first = this.getElement().getFirstChildElement();

        text.addDoubleClickHandler(new DoubleClickHandler() {
            public void onDoubleClick(DoubleClickEvent doubleClickEvent) {
                first.addClassName("editing");
                editText.setText(text.getText());
            }
        });

        editText.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                if(keyUpEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    Todo todo = new Todo(editText.getText(), false);
                    repository.updateTodo(index, todo);
                    first.removeClassName("editing");
                    setModel(todo);
                }
            }
        });
    }

    public Todo getModel() {
        return model;
    }

    public void setModel(Todo todoItem) {
        this.model = todoItem;
        this.text.setText(todoItem.getTodo());
    }

    @EventHandler("todo-check")
    public void check(ClickEvent event) {
        Console.log(todoCheck.getValue() + "");
        if(this.getModel() != null) {
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
