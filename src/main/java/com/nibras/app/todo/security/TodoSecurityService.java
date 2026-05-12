package com.nibras.app.todo.security;

import com.nibras.app.todo.Todo;
import com.nibras.app.todo.TodoRepository;
import com.nibras.app.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoSecurityService {

    private final TodoRepository todoRepository;

    public boolean isTodoOwner(final String todoId) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String userId = ((User) authentication.getPrincipal()).getId();

        final Todo todo = this.todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("Todo is not found with id: " + todoId));

        return todo.getUser().getId().equals(userId);
    }
}
