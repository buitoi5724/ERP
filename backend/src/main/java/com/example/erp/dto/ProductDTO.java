package com.example.erp.dto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.example.erp.controller.ProductController;
import com.example.erp.controller.ProductController.PriceHistoryDTO;
import com.example.erp.entity.Product;

public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private String image;
    private CategoryDTO category;
    private Integer quantity; // thêm quantity
    private List<PriceHistoryDTO> priceHistory;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescription();

        this.image = (product.getImage() != null && !product.getImage().isEmpty())
                ? "http://localhost:8080/api/products/image/" + product.getImage()
                : null;

        if (product.getCategory() != null) {
            this.category = new CategoryDTO(product.getCategory());
        }

        if (product.getPriceHistory() != null) {
            this.priceHistory = IntStream.range(0, product.getPriceHistory().size())
                    .mapToObj(i -> new PriceHistoryDTO(product.getPriceHistory().get(i), i + 1))
                    .collect(Collectors.toList());
        }

        // ✅ Thêm map quantity
        if (product.getInventory() != null) {
            this.quantity = product.getInventory().getQuantity();
        } else {
            this.quantity = 0; // hoặc null nếu muốn
        }
    }

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}

	public Long getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImage() { return image; }
    public CategoryDTO getCategory() { return category; }
    public List<PriceHistoryDTO> getPriceHistory() { return priceHistory; }
}