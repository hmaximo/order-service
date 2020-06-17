package br.com.codenation.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

public class OrderServiceImpl implements OrderService {

	private ProductRepository productRepository = new ProductRepositoryImpl();

	@Override
	public Double calculateOrderValue(List<OrderItem> items) {
		Double total = items
			.stream()
			.mapToDouble(orderItem -> productRepository.findById(orderItem.getProductId())
				.map(product -> product.getIsSale() ? (product.getValue() * 0.8) : product.getValue())
			.orElse(0.0) * orderItem.getQuantity())
			.sum();
		return total;
	}

	@Override
	public Set<Product> findProductsById(List<Long> ids) {
		Set<Product> foundProducts = ids
			.stream()
			.map(productRepository::findById)
			.filter(opt -> opt.isPresent())
			.map( product -> product.get())
			.collect(Collectors.toSet());
		return foundProducts;
	}

	@Override
	public Double calculateMultipleOrders(List<List<OrderItem>> orders) {
		Double total = orders.
			stream().
			mapToDouble(orderItemList -> this.calculateOrderValue(orderItemList)).
			sum();
		return total;
	}

	@Override
	public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {
		Map<Boolean, List<Product>> productsBySale = this.findProductsById(productIds)
			.stream()
			.collect(Collectors.groupingBy(product -> product.getIsSale()));
		return productsBySale;
	}

}