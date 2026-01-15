package com.example.erp.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "product_gallery")
public class ProductGallery implements Serializable {

  
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;   // vd: 169999999_sp1.jpg
    private String filePath;   // vd: uploads/products/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /* ===== GETTERS / SETTERS ===== */

    public Long getId() { return id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}