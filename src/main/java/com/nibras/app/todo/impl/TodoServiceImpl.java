package com.nibras.app.todo.impl;

import com.nibras.app.category.Category;
import com.nibras.app.category.CategoryRepository;
import com.nibras.app.todo.Todo;
import com.nibras.app.todo.TodoMapper;
import com.nibras.app.todo.TodoRepository;
import com.nibras.app.todo.TodoService;
import com.nibras.app.todo.request.TodoRequest;
import com.nibras.app.todo.request.TodoUpdateRequest;
import com.nibras.app.todo.response.TodoResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final TodoMapper todoMapper;

    @Override
    public String createTodo(final TodoRequest request, final String userId) {
        final Category category = checkAndReturnCategory(request.getCategoryId(), userId);
        final Todo todo = this.todoMapper.toTodo(request);
        todo.setCategory(category);
        return this.todoRepository.save(todo).getId();
    }

    @Override
    public void updateTodo(final TodoUpdateRequest request, final String todoId, final String userId) {
        final Todo todoToUpdate = this.todoRepository.findById(todoId)
                                                     .orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + todoId));
        final Category category = checkAndReturnCategory(request.getCategoryId(), userId);

        this.todoMapper.mergerTodo(todoToUpdate, request);
        todoToUpdate.setCategory(category);

        this.todoRepository.save(todoToUpdate);
    }

    @Override
    public TodoResponse findTodoById(final String todoId) {
        return this.todoRepository.findById(todoId)
                                  .map(this.todoMapper::toTodoResponse)
                                  .orElseThrow(() -> new EntityNotFoundException("No todo found with id " + todoId));
    }

    @Override
    public List<TodoResponse> findAllTodosForToday(final String userId) {
        return this.todoRepository.findAllByUserId(userId)
                                  .stream()
                                  .map(this.todoMapper::toTodoResponse)
                                  .toList();
    }

    @Override
    public List<TodoResponse> findAllTodosByCategory(final String catId, final String userId) {
        return this.todoRepository.findAllByUserIdAndCategoryId(userId, catId)
                                  .stream()
                                  .map(this.todoMapper::toTodoResponse)
                                  .toList();
    }

    @Override
    public List<TodoResponse> findAllDueTodos(final String userId) {
        return this.todoRepository.findAllDueTodos(userId)
                                  .stream()
                                  .map(this.todoMapper::toTodoResponse)
                                  .toList();
    }

    @Override
    public void deleteTodoById(final String todoId) {
        this.todoRepository.deleteById(todoId);
    }

    private Category checkAndReturnCategory(final String categoryId, final String userId) {
        return this.categoryRepository.findByIdAndUserId(categoryId, userId)
                                      .orElseThrow(() -> new EntityNotFoundException("No category was found for that user with id " + categoryId));
    }
}
