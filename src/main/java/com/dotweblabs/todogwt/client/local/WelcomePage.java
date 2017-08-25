package com.dotweblabs.todogwt.client.local;

import com.dotweblabs.todogwt.client.local.event.Delete;
import com.dotweblabs.todogwt.client.local.event.Updated;
import com.dotweblabs.todogwt.client.local.model.Todo;
import com.dotweblabs.todogwt.client.local.model.TodoWrapper;
import com.dotweblabs.todogwt.client.local.repository.TodoRepository;
import com.dotweblabs.todogwt.client.local.widget.TodoItem;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.logging.util.Console;
import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageShowing;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;


/**
 * Main page
 *
 * @author kerbymart
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
@Dependent
@Templated
@Page(role = DefaultPage.class, path = "/")
public class WelcomePage extends Composite {

    @Inject
    @DataField("new-todo")
    TextBox newTodo;

    @Inject
    @DataField("todo-list")
    FlowPanel rows;

    @Inject
    Instance<TodoItem> todoItems;

    @Inject
    TodoRepository repository;

    @Inject
    @DataField("todo-count")
    @Named("span")
    SpanElement todoCount;

    @Inject
    @DataField("milk-time")
    @Named("span")
    SpanElement milkTime;

    @Inject
    @DataField("clear-completed")
    @Named("span")
    SpanElement clearCompleted;

    @PostConstruct
    public void init() {
        newTodo.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                if(keyUpEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER) {

                    Todo todo = new Todo(newTodo.getText(), false);
                    repository.addTodo(todo);

                    TodoItem item = todoItems.get();
                    item.setModel(todo);
                    rows.add(item);

                    newTodo.setText("");

                    countTodos();
                }
            }
        });
    }

    @PageShowing
    public void showing() {
        countTodos();
        rows.clear();
        List<Todo> todoList = repository.listTodo();
        for(Todo todo : todoList) {
            TodoItem item = todoItems.get();
            item.setModel(todo);
            rows.add(item);
        }
    }

    @EventHandler("clear-completed")
    public void clear(ClickEvent event) {
        event.preventDefault();
        for(Widget w : rows){
            TodoItem item = (TodoItem) w;
            Todo todo = item.getModel();
            if(todo.getDone()) {
                item.removeFromParent();
            }
        }
    }

    private void countTodos() {
        int count = repository.countNotDone();
        int total = repository.count();
        Console.log("Count: " + count);
        todoCount.setInnerText(count + "");
        if(count > 0) {
            milkTime.setAttribute("style", "display: none;");
        } else {
            milkTime.removeAttribute("style");
        }
        if(total > 0) {
            clearCompleted.removeAttribute("style");
        } else {
            clearCompleted.setAttribute("style", "display: none;");
        }
    }

    public void deleted(@Observes @Delete TodoWrapper wrapper) {
        if(wrapper != null && wrapper.getTodo() != null) {
            Console.log("Removed: " + wrapper.getIndex());
            Todo todo = wrapper.getTodo();

        }
        countTodos();
    }

    public void updated(@Observes @Updated TodoWrapper wrapper) {
        Console.log("Updated");
        if(wrapper != null && wrapper.getTodo() != null) {
            Console.log("Index: "  + wrapper.getIndex());
            Console.log("Done: "  + wrapper.getTodo().getDone());
            repository.setDone(wrapper.getIndex(), wrapper.getTodo().getDone());
        }
        countTodos();
    }

}
