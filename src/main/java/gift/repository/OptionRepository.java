package gift.repository;

import gift.dto.product.ProductWithOptionDTO;
import gift.entity.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OptionRepository extends JpaRepository<Option, Integer> {
    @Query("SELECT new gift.dto.product.ProductWithOptionDTO(p.id, p.name , p.price , p.imageUrl , o.option, p.category.name) FROM Product p join Option o ON p.id = o.product.id")
    Page<ProductWithOptionDTO> findAllWithOption(Pageable pageable);
}
