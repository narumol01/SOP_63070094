package com.example.sop_63070094;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private  ProductRepository repo;
    public ProductService(ProductRepository repo){
        this.repo = repo;
    }
    public boolean addProduct(Product product){
        try{
            repo.insert(product);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean updateProduct(Product product){
        try{
            repo.save(product);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean deleteProduct(Product product){
        try{
            repo.delete(product);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public List<Product> getAllProduct(){
        try{
            return repo.findAll();
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
    public Product getProductByName(String name){
        try{
            return repo.findByName(name);
        }catch (Exception e){
            return null;
        }
    }
}
