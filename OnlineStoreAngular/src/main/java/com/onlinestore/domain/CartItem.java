package com.onlinestore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
public class CartItem implements Serializable{

	private static final long serialVersionUID = 45445547471L;
		
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private int qty;
	//https://www.geeksforgeeks.org/bigdecimal-class-java/
	private BigDecimal subtotal;
	
	@OneToOne
	private Product product;
	//TODO
	@OneToMany(mappedBy ="cartItem")
	@JsonIgnore
	private List<ProductToCartItem> productToCartItemList;
	
	@ManyToOne
	@JoinColumn(name="shopping_cart_id")
	@JsonIgnore
	private ShoppingCart shoppingCart;

	@ManyToOne
	@JoinColumn(name = "order_id")
	@JsonIgnore
	private Order order;




	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public List<ProductToCartItem> getProductToCartItemList() {
		return productToCartItemList;
	}

	public void setProductToCartItemList(List<ProductToCartItem> productToCartItemList) {
		this.productToCartItemList = productToCartItemList;
	}

	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}

	public void setShoppingCart(ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
}
