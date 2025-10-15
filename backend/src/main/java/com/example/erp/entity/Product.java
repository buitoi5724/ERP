package com.example.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "NVARCHAR(255)", nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price; // giá hiện tại của sản phẩm

    private String image;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    // 🔹 Thêm thuộc tính mới
    @Column(columnDefinition = "NVARCHAR(255)")
    private String sizes; // ví dụ: "S,M,L,XL"

    @Column(columnDefinition = "NVARCHAR(255)")
    private String colors; // ví dụ: "Đen,Trắng,Hồng"

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"createDate", "createBy", "updateDate", "updateBy"})
    private ProductCategory category;

    // 🔹 Quan hệ với lịch sử giá
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"product"}) // tránh vòng lặp vô hạn khi trả JSON
    private List<ProductPrice> priceHistory = new ArrayList<>();

    // --- Getters & Setters ---
    public Long getId() { return id; }
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
}
