package com.baitapso05.controllers.admin;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.baitapso05.entitiy.Category;
import com.baitapso05.model.CategoryModel;
import com.baitapso05.service.CategoryService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String getListCategory(Model model) {
        List<Category> categories = this.categoryService.findAll();
        model.addAttribute("categories", categories);
        return "list";
    }

    @GetMapping("/add")
    public String getCreateCategoryPage(Model model) {
        CategoryModel category = new CategoryModel();
        category.setIsEdit(false);
        model.addAttribute("category", category);
        return "addOrEdit";
    }

    @PostMapping("/save")
    public ModelAndView handleModifyForm(ModelMap model,
                                         @Valid @ModelAttribute("category") CategoryModel categoryModel,
                                         BindingResult bindingResult,
                                         @RequestParam("image") MultipartFile file) {
        Category entity = new Category();
        if (bindingResult.hasErrors()) {
            return new ModelAndView("addOrEdit");
        }

        String image = this.handleSaveUploadFile(file, "D:\\Lap_trinh_Web\\baitapso05\\upload");
        categoryModel.setImages(image);

        BeanUtils.copyProperties(categoryModel, entity);

        categoryService.save(entity);

        String message = "";

        if (categoryModel.getIsEdit()) {
            message="Category is edited successfully";
        } else {
            message=" Category is saved successfully";
        }

        model.addAttribute("message", message);
        return new ModelAndView("redirect:/admin/categories", model);
    }

    @GetMapping("/edit/{categoryId}")
    public ModelAndView getEditCategoryPage(ModelMap model, @PathVariable("categoryId") Long categoryId) {
        Optional<Category> optionalCategory = categoryService.findById(categoryId);
        CategoryModel category = new CategoryModel();
        if (optionalCategory.isPresent()) {
            Category categoryEntity = optionalCategory.get();
            BeanUtils.copyProperties(categoryEntity, category);
            category.setIsEdit(true);
            model.addAttribute("category", category);
            return new ModelAndView("addOrEdit",model);
        }
        model.addAttribute("message", "Category is not existed");
        return new ModelAndView("forward:/admin/categories", model);
    }

    @GetMapping("/delete/{categoryId}")
    public ModelAndView handleDeleteForm(ModelMap model, @PathVariable("categoryId") Long categoryId) {
        categoryService.deleteById(categoryId);
        model.addAttribute("message", "Category deleted successfully");
        return new ModelAndView("redirect:/admin/categories", model);
    }

    @RequestMapping("/searchpaginated")
    public String search(ModelMap model,
                         @RequestParam(name = "name", required = false) String name,
                         @RequestParam(name = "page", defaultValue = "1") int currentPage,
                         @RequestParam(name = "size", defaultValue = "3") int pageSize) {

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("name"));
        Page<Category> resultPage;

        // Kiểm tra có tên được truyền vào để tìm kiếm hay không
        if (StringUtils.hasText(name)) {
            resultPage = categoryService.findByCategoryNameContaining(name, pageable);
            model.addAttribute("name", name);
        } else {
            resultPage = categoryService.findAll(pageable);
        }

        int totalPages = resultPage.getTotalPages();

        if (totalPages > 0) {
            int start = Math.max(1, currentPage - 2);
            int end = Math.min(currentPage + 2, totalPages);

            if (totalPages > 5) { // Giới hạn số trang hiển thị trong khoảng 5 trang
                if (end == totalPages) start = Math.max(1, end - 4);
                else if (start == 1) end = Math.min(totalPages, start + 4);
            }

            List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("categoryPage", resultPage);
        return "list";
    }


    public String handleSaveUploadFile(MultipartFile file, String uploadDir) {
        if (!file.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
                Path path = Paths.get(uploadDir, fileName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                return fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}