package com.nibras.app.category;

import com.nibras.app.category.request.CategoryRequest;
import com.nibras.app.category.request.CategoryUpdateRequest;
import com.nibras.app.category.response.CategoryResponse;
import com.nibras.app.common.RestResponse;
import com.nibras.app.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category API")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<RestResponse> createCategory(@RequestBody @Valid final CategoryRequest request,
                                                       final Authentication authentication) {
        final String userId = ((User) authentication.getPrincipal()).getId();
        final String catId = this.categoryService.createCategory(request, userId);
        return ResponseEntity.status(CREATED).body(new RestResponse(catId));
    }

    @PutMapping("/{category-id}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<Void> updateCategory(@RequestBody @Valid final CategoryUpdateRequest request,
                                               @PathVariable("category-id") final String categoryId,
                                               final Authentication authentication) {
        final String userId = ((User) authentication.getPrincipal()).getId();
        this.categoryService.updateCategory(request, categoryId, userId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAllCategories(final Authentication authentication) {
        final String userId = ((User) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(this.categoryService.findAllByOwner(userId));
    }

    @GetMapping("/{category-id}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<CategoryResponse> findCategoryById(@PathVariable("category-id") final String categoryId) {
        return ResponseEntity.ok(this.categoryService.findCategoryById(categoryId));
    }

    @DeleteMapping("/{category-id}")
    @PreAuthorize("@categorySecurityService.isCategoryOwner(#categoryId)")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable("category-id") final String categoryId) {
        this.categoryService.deleteCategoryById(categoryId);
        return ResponseEntity.ok().build();
    }
}
