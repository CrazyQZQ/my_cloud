package com.qq.product.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qq.common.core.web.page.BaseQuery;
import com.qq.common.system.pojo.SysProduct;

import java.io.IOException;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【sys_product(商品表)】的数据库操作Service
 * @createDate 2022-05-06 16:44:17
 */
public interface SysProductService extends IService<SysProduct> {

    /**
     * @param sysProduct 商品信息
     * @return
     * @description 新增商品
     */
    void addProduct(SysProduct sysProduct) throws IOException;

    /**
     * @param id
     * @return
     * @description 根据商品id查询商品
     */
    SysProduct getProductById(Long id);

    /**
     * @return
     * @description 分页查询商品
     */
    List<SysProduct> getProductList(BaseQuery query);

    /**
     * @param product
     * @return
     * @description 更新商品
     */
    int updateProduct(SysProduct product) throws IOException;

    /**
     * @param id
     * @return
     * @description 删除商品
     */
    int deleteProduct(Long id) throws IOException;
}
