package com.sxpi.pan.aimallproduct.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallproduct.dto.ProductDTO;
import com.sxpi.pan.aimallproduct.dto.ProductQueryDTO;
import com.sxpi.pan.aimallproduct.vo.ProductDetailVO;
import com.sxpi.pan.aimallproduct.vo.ProductVO;

public interface ProductService {
    Page<ProductVO> getProductList(ProductQueryDTO query);
    ProductDetailVO getProductDetail(Long id);
    void addProduct(Long merchantId, ProductDTO dto);
    void updateProduct(Long id, Long merchantId, ProductDTO dto);
    void updateProductStatus(Long id, Long merchantId, Integer status);
    void deleteProduct(Long id, Long merchantId);
    Page<ProductVO> getMerchantProducts(Long merchantId, Integer status, Integer page, Integer size);
    void auditProduct(Long id, Integer status, String remark);
    long countProducts();
    void updateField(Long id, String field, Object value);
}
