package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.product.ModifyProductDTO;
import gift.dto.product.ProductWithOptionDTO;
import gift.dto.product.SaveProductDTO;
import gift.dto.product.ShowProductDTO;

import gift.entity.Category;
import gift.entity.Option;
import gift.entity.Product;
import gift.exception.exception.BadRequestException;
import gift.exception.exception.UnAuthException;
import gift.exception.exception.NotFoundException;
import gift.repository.CategoryRepository;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;


@Service
@Validated
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public Page<ProductWithOptionDTO> getAllProductsWithOption(Pageable pageable) {
        return optionRepository.findAllWithOption(pageable);

    }

    public Page<ShowProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAllProduct(pageable);
    }

    public void saveProduct(SaveProductDTO product) {
        if(categoryRepository.findById(product.categoryId()).isEmpty())
            throw new NotFoundException("해당 카테고리가 없음");

        Category category = categoryRepository.findById(product.categoryId()).get();
        Product saveProduct = new Product(product.name(), product.price(), product.imageUrl(),category);
        List<String> optionList = stream(product.option().split(",")).toList();
        optionList= optionList.stream().distinct().collect(Collectors.toList());
        if(optionList.isEmpty())
            throw new BadRequestException("하나의 옵션은 필요");

        if(isValidProduct(saveProduct)){
            saveProduct = productRepository.save(saveProduct);
            category.addProduct(saveProduct);
        }
    }

    private void addOptionToProduct(List<String> optionList, Product product) {
        for(String str : optionList){
            Option option = new Option(product, str);
            if(isValidOption(option)) {
                product.addOptions(option);
                optionRepository.save(option);
            }
        }
    }

    private boolean isValidProduct(@Validated Product product){
        if(product.getName().contentEquals("카카오"))
            throw new UnAuthException("MD와 상담해주세요.");

        Optional<Product> productOptional = productRepository.findById(product.getId());
        return productOptional.map(value -> value.equals(product)).orElse(true);
    }

    private boolean isValidOption(@Validated Option option){
        if(optionRepository.findById(option.getId()).isPresent())
            throw new BadRequestException("이미 존재하는 옵션입니다.");
        return true;
    }

    public void deleteProduct(int id) {
        if(productRepository.findById(id).isEmpty())
            throw new NotFoundException("존재하지 않는 id입니다.");
        Product product = productRepository.findById(id).get();
        Category category = product.getCategory() ;
        category.deleteProduct(product);
        optionRepository.deleteAll();
        productRepository.deleteById(id);
    }


    public String getProductByID(int id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isEmpty())
            throw new NotFoundException("해당 물건이 없습니다.");
        Product product = optionalProduct.get();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonProduct="";

        try {
            jsonProduct = objectMapper.writeValueAsString(product);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonProduct;
    }
  
    public void modifyProduct(Product product) {
        if(productRepository.findById(product.getId()).isEmpty())
            throw new NotFoundException("물건이 없습니다.");
        productRepository.updateProductById(product.id(), product.name(),product.price(),product.imageUrl());
    }

}
