package com.example.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"product"})
    private Inventory inventory;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(255)", nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    private String image;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String sizes;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String colors;
    @Transient
    private Integer quantity;

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"createDate", "createBy", "updateDate", "updateBy"})
    private ProductCategory category;

    // ⚡ Fix N+1 query cho priceHistory
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnoreProperties({"product"})
    private List<ProductPrice> priceHistory = new ArrayList<>();

    // ⚡ Fix N+1 query cho gallery
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnoreProperties({"product"})
    private List<ProductGallery> galleries = new ArrayList<>();
    
    // --- Getters & Setters ---
    
    public Long getId() { return id; }
    /**
	 * @return the inventory
	 */
	public Inventory getInventory() {
		return inventory;
	}
	/**
	 * @param inventory the inventory to set
	 */
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSizes() { return sizes; }
    public void setSizes(String sizes) { this.sizes = sizes; }

    public String getColors() { return colors; }
    public void setColors(String colors) { this.colors = colors; }

    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }

    public List<ProductPrice> getPriceHistory() { return priceHistory; }
    public void setPriceHistory(List<ProductPrice> priceHistory) { this.priceHistory = priceHistory; }

    public List<ProductGallery> getGalleries() { return galleries; }
    public void setGalleries(List<ProductGallery> galleries) { this.galleries = galleries; }
}
