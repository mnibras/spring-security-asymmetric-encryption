package com.nibras.app.todo;

import com.nibras.app.todo.request.TodoRequest;
import com.nibras.app.todo.request.TodoUpdateRequest;
import com.nibras.app.todo.response.TodoResponse;

import java.util.List;

public interface TodoService {

    String createTodo(TodoRequest request, String userId);

    void updateTodo(TodoUpdateRequest request, String todoId, String userId);

    TodoResponse findTodoById(String todoId);

    List<TodoResponse> findAllTodosForToday(String userId);

    List<TodoResponse> findAllTodosByCategory(String catId, String userId);

    List<TodoResponse> findAllDueTodos(String userId);

    void deleteTodoById(String todoId);

}
