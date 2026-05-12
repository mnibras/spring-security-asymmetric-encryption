package com.nibras.app.todo;

import com.nibras.app.category.Category;
import com.nibras.app.common.BaseEntity;
import com.nibras.app.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "todo")
public class Todo extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_done", nullable = false)
    private boolean done;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
