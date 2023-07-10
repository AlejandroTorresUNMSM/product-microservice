package com.atorres.nttdata.productomicroservice.repository;

import com.atorres.nttdata.productomicroservice.model.dao.ProductDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<ProductDao,String> {
}
