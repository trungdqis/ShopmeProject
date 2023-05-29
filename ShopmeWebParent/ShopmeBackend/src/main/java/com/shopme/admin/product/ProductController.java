package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;

    @Autowired
    public ProductController(ProductService productService, BrandService brandService, CategoryService categoryService) {
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        return listByPage(1, "name", "asc", null, 0, model);
    }

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword,
                             @Param("categoryId") Integer categoryId,
                             Model model) {
        Page<Product> pageProducts = productService.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
        List<Product> products = pageProducts.getContent();

        List<Category> categories = categoryService.listCategoriesUsedInForm();

        long startCount = (long) (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;

        if (endCount > pageProducts.getTotalElements()) {
            endCount = pageProducts.getTotalElements();
        }

        String reverseSortDir = "asc".equals(sortDir) ? "desc" : "asc";

        if (null != categoryId) {
            model.addAttribute("categoryId", categoryId);
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);

        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> brands = brandService.listAll();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("brands", brands);
        model.addAttribute("pageTitle", "Create New Product");

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, @RequestParam("fileImage") MultipartFile mainImageMultipart,
                              @RequestParam("extraImage") MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              RedirectAttributes redirectAttributes) throws IOException {
        setMainImageName(mainImageMultipart, product);
        setExistingExtraImageNames(imageIDs, imageNames, product);
        setNewExtraImageNames(extraImageMultiparts, product);
        setProductDetails(detailIDs, detailNames, detailValues, product);

        Product savedProduct = productService.save(product);

        saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);
        deleteExtraImagesWeredRemovedOnForm(product);

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");

        return "redirect:/products";
    }

    private void deleteExtraImagesWeredRemovedOnForm(Product product) {
        String extraImageDir = "../product-images/" + product.getId() + "/extras";

        Path dirPath = Paths.get(extraImageDir);

        try {
            Files.list(dirPath).forEach(file -> {
                String fileName = file.toFile().getName();

                if (!product.containsImageName(fileName)) {
                    try {
                        Files.delete(file);
                        LOGGER.info("Deleted extra image: {}", fileName);
                    } catch (IOException exception) {
                        LOGGER.error("Could note delete extra image: {}", fileName);
                    }
                }
            });
        } catch (IOException exception) {
            LOGGER.error("Could not list directoryL: {}", dirPath);
        }
    }

    private void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
        if (null == imageIDs || 0 == imageIDs.length) {
            return;
        }

        Set<ProductImage> images = new HashSet<>();

        for (int count = 0; count < imageIDs.length; count++) {
            Integer id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];
            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);
    }

    private void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
        if (null == detailNames || 0 == detailNames.length) return;

        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];
            Integer id = Integer.parseInt(detailIDs[count]);

            if (0 != id) {
                product.addDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }
    }

    private void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));

            String uploadDir = "../product-images/" + savedProduct.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        if (0 < extraImageMultiparts.length) {
            String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";

            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        }
    }

    private void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
        if (0 < extraImageMultiparts.length) {
            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }

    private void setMainImageName(MultipartFile mainImageMultipart, Product product) {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));
            product.setMainImage(fileName);
        }
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateProductEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
                                              RedirectAttributes redirectAttributes) {
        productService.updateProductEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The product ID " + id + " has been " + status;

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            String productExtraImagesDir = "../product-images/" + id + "/extras";
            String productImagesDir = "../product-images/" + id;

            FileUploadUtil.removeDir(productExtraImagesDir);
            FileUploadUtil.removeDir(productImagesDir);

            redirectAttributes.addFlashAttribute("message",
                    "The product ID " + id + " has been deleted successfully");
        } catch (ProductNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);
            List<Brand> brands = brandService.listAll();
            Integer numberOfExistingExtraImages = product.getImages().size();

            model.addAttribute("product", product);
            model.addAttribute("brands", brands);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);

            return "products/product_form";
        } catch (ProductNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());

            return "redirect:/products";
        }
    }

    @GetMapping("/products/detail/{id}")
    public String viewProductDetails(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.get(id);

            model.addAttribute("product", product);

            return "products/product_detail_modal";
        } catch (ProductNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());

            return "redirect:/products";
        }
    }
}
