package com.sxpi.pan.aimallproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sxpi.pan.aimallcommon.exception.BusinessException;
import com.sxpi.pan.aimallproduct.dto.ProductDTO;
import com.sxpi.pan.aimallproduct.dto.ProductQueryDTO;
import com.sxpi.pan.aimallproduct.entity.Product;
import com.sxpi.pan.aimallproduct.entity.ProductAttribute;
import com.sxpi.pan.aimallproduct.entity.Sku;
import com.sxpi.pan.aimallproduct.entity.AttributeTemplate;
import com.sxpi.pan.aimallproduct.mapper.ProductAttributeMapper;
import com.sxpi.pan.aimallproduct.mapper.ProductMapper;
import com.sxpi.pan.aimallproduct.mapper.SkuMapper;
import com.sxpi.pan.aimallproduct.mapper.AttributeTemplateMapper;
import com.sxpi.pan.aimallproduct.service.ProductService;
import com.sxpi.pan.aimallproduct.vo.ProductDetailVO;
import com.sxpi.pan.aimallproduct.vo.ProductVO;
import com.sxpi.pan.aimallproduct.vo.SkuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductAttributeMapper attributeMapper;
    private final SkuMapper skuMapper;
    private final AttributeTemplateMapper templateMapper;

    @Override
    public Page<ProductVO> getProductList(ProductQueryDTO query) {
        Page<Product> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        // 如果指定了状态，则按状态筛选；否则只显示上架商品
        if (query.getStatus() != null) {
            wrapper.eq(Product::getStatus, query.getStatus());
        } else {
            wrapper.eq(Product::getStatus, 1); // 默认只显示上架商品
        }
        if (query.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, query.getCategoryId());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Product::getTitle, query.getKeyword());
        }
        if (query.getMinPrice() != null) {
            wrapper.ge(Product::getPrice, query.getMinPrice());
        }
        if (query.getMaxPrice() != null) {
            wrapper.le(Product::getPrice, query.getMaxPrice());
        }
        if ("price_asc".equals(query.getSort())) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(query.getSort())) {
            wrapper.orderByDesc(Product::getPrice);
        } else if ("sales".equals(query.getSort())) {
            wrapper.orderByDesc(Product::getSales);
        } else {
            wrapper.orderByDesc(Product::getCreateTime);
        }

        Page<Product> result = productMapper.selectPage(page, wrapper);
        return toVOPage(result);
    }

    @Override
    public ProductDetailVO getProductDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(40414, "商品不存在");
        }
        // 浏览量+1
        product.setViews(product.getViews() != null ? product.getViews() + 1 : 1);
        productMapper.updateById(product);

        ProductDetailVO vo = new ProductDetailVO();
        BeanUtils.copyProperties(product, vo);

        // 查询商品属性
        List<ProductAttribute> attrs = attributeMapper.selectList(
                new LambdaQueryWrapper<ProductAttribute>()
                        .eq(ProductAttribute::getProductId, id));
        vo.setAttributes(attrs.stream().map(a -> {
            ProductDetailVO.AttributeVO attrVO = new ProductDetailVO.AttributeVO();
            attrVO.setTemplateId(a.getTemplateId());
            attrVO.setValue(a.getValue());
            AttributeTemplate template = templateMapper.selectById(a.getTemplateId());
            attrVO.setTemplateName(template != null ? template.getName() : "");
            return attrVO;
        }).toList());

        // 查询SKU
        List<Sku> skus = skuMapper.selectList(
                new LambdaQueryWrapper<Sku>().eq(Sku::getProductId, id));
        vo.setSkus(skus.stream().map(s -> {
            SkuVO skuVO = new SkuVO();
            BeanUtils.copyProperties(s, skuVO);
            return skuVO;
        }).toList());

        return vo;
    }

    @Override
    @Transactional
    public void addProduct(Long merchantId, ProductDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setMerchantId(merchantId);
        product.setStock(0);
        product.setSales(0);
        product.setViews(0);
        product.setStatus(2); // 待审核
        productMapper.insert(product);

        // 保存商品属性
        if (dto.getAttributes() != null) {
            for (ProductDTO.ProductAttributeDTO attr : dto.getAttributes()) {
                ProductAttribute pa = new ProductAttribute();
                pa.setProductId(product.getId());
                pa.setTemplateId(attr.getTemplateId());
                pa.setValue(attr.getValue());
                attributeMapper.insert(pa);
            }
        }
    }

    @Override
    @Transactional
    public void updateProduct(Long id, Long merchantId, ProductDTO dto) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(40414, "商品不存在");
        }
        if (!product.getMerchantId().equals(merchantId)) {
            throw new BusinessException(40302, "无权操作此商品");
        }
        BeanUtils.copyProperties(dto, product);
        productMapper.updateById(product);

        // 更新属性：先删后插
        attributeMapper.delete(new LambdaQueryWrapper<ProductAttribute>()
                .eq(ProductAttribute::getProductId, id));
        if (dto.getAttributes() != null) {
            for (ProductDTO.ProductAttributeDTO attr : dto.getAttributes()) {
                ProductAttribute pa = new ProductAttribute();
                pa.setProductId(id);
                pa.setTemplateId(attr.getTemplateId());
                pa.setValue(attr.getValue());
                attributeMapper.insert(pa);
            }
        }
    }

    @Override
    public void updateProductStatus(Long id, Long merchantId, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(40414, "商品不存在");
        }
        if (!product.getMerchantId().equals(merchantId)) {
            throw new BusinessException(40302, "无权操作此商品");
        }

        Integer currentStatus = product.getStatus();

        // 业务逻辑检查
        if (status == 1) { // 商家想要上架
            if (currentStatus == 0) {
                // 下架商品想要重新上架，需要重新提交审核
                product.setStatus(2); // 改为待审核状态
                productMapper.updateById(product);
                throw new BusinessException(40033, "商品已提交审核，请等待管理员审核");
            } else if (currentStatus == 2) {
                throw new BusinessException(40034, "商品正在审核中，请等待管理员审核");
            } else if (currentStatus == 1) {
                throw new BusinessException(40035, "商品已上架");
            }
        } else if (status == 0) { // 商家想要下架
            if (currentStatus == 1) {
                // 上架商品可以下架
                product.setStatus(0);
                productMapper.updateById(product);
            } else if (currentStatus == 0) {
                throw new BusinessException(40036, "商品已下架");
            } else if (currentStatus == 2) {
                throw new BusinessException(40037, "商品正在审核中，无法下架");
            }
        } else {
            throw new BusinessException(40038, "无效的状态操作");
        }
    }

    @Override
    public void deleteProduct(Long id, Long merchantId) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(40414, "商品不存在");
        }
        if (!product.getMerchantId().equals(merchantId)) {
            throw new BusinessException(40302, "无权操作此商品");
        }
        if (product.getStatus() != 0) {
            throw new BusinessException(40031, "只有下架状态的商品才能删除");
        }
        productMapper.deleteById(id);
    }

    @Override
    public Page<ProductVO> getMerchantProducts(Long merchantId, Integer status, Integer page, Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getMerchantId, merchantId);

        // 添加状态筛选（支持可选）
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }

        wrapper.orderByDesc(Product::getCreateTime);
        Page<Product> result = productMapper.selectPage(pageParam, wrapper);
        return toVOPage(result);
    }

    @Override
    public void auditProduct(Long id, Integer status, String remark) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(40414, "商品不存在");
        }
        if (product.getStatus() != 2) {
            throw new BusinessException(40032, "该商品不在待审核状态");
        }
        product.setStatus(status);
        product.setAuditRemark(remark);
        productMapper.updateById(product);
    }

    @Override
    public long countProducts() {
        return productMapper.selectCount(null);
    }

    @Override
    public void updateField(Long id, String field, Object value) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(40414, "商品不存在");
        }
        UpdateWrapper<Product> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id).set(field, value);
        productMapper.update(null, wrapper);
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Page<ProductVO> toVOPage(Page<Product> page) {
        Page<ProductVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(p -> {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(p, vo);
            // 手动转换 createTime
            if (p.getCreateTime() != null) {
                vo.setCreateTime(p.getCreateTime().format(FORMATTER));
            }
            return vo;
        }).toList());
        return voPage;
    }
}
