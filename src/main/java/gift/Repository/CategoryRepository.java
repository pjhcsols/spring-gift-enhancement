package gift.Repository;

import gift.Model.Category;
import gift.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findCategoryById(Long id);
}
