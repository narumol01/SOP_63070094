package com.example.sop_63070094;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductService service;

    @RabbitListener(queues = "AddProductQueue")
    public boolean serviceAddProduct(Product product){
        return service.addProduct(product);
    }

    @RabbitListener(queues = "UpdateProductQueue")
    public boolean  serviceUpdateProduct(Product product){
        return service.updateProduct(product);
    }

    @RabbitListener(queues = "DeleteProductQueue")
    public boolean  serviceDeleteProduct(Product product){
        return service.deleteProduct(product);
    }

    @RabbitListener(queues = "GetNameProductQueue")
    public Product serviceGetProductName(String name){
        return service.getProductByName(name);
    }

    @RabbitListener(queues = "GetAllProductQueue")
    public List<Product> serviceGetAllProduct(){
        return service.getAllProduct();
    }
}
