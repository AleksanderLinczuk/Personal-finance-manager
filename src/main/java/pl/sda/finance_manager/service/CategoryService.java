package pl.sda.finance_manager.service;

import com.mysql.cj.util.StringUtils;
import pl.sda.finance_manager.dto.SimpleCategoryDto;
import pl.sda.finance_manager.entity.Category;
import pl.sda.finance_manager.repository.Repository;

import java.util.List;

public class CategoryService {

    private final Repository<Category, Long> categoryRepository;


    public CategoryService(Repository<Category, Long> categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void addCategory(String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            Category category = new Category(name);
            categoryRepository.create(category);
        } else {
            throw new IllegalArgumentException("Provided data is incorrect! ");
        }
    }
    public List<SimpleCategoryDto> findAll(){
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(category -> new SimpleCategoryDto(category.getId(), category.getName())).toList();
    }
    public void readAll(){
        List<SimpleCategoryDto> all = findAll();
        all.forEach(simpleCategoryDto -> System.out.println("CATEGORY: id = " + simpleCategoryDto.getId() + " name = " + simpleCategoryDto.getName()));
    }
    public Category findById(Long id){
        if(id != null){
            return categoryRepository.findById(id);
        }else {
            throw new IllegalArgumentException("Provided data is incorrect! ");
        }
    }
    public void deleteById(Long id){
        if (id!=null){
            categoryRepository.deleteById(id);
        }else {
            throw new IllegalArgumentException("Provided data is incorrect! ");
        }
    }
}
