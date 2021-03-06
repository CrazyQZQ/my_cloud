package com.qq.order.server.service;

import com.qq.common.system.pojo.ShoppingCartItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【shopping_cart_item(购物车清单)】的数据库操作Service
 * @createDate 2022-06-02 09:32:58
 */
public interface ShoppingCartItemService extends IService<ShoppingCartItem> {

    /**
     * 查询购物车列表
     * @param userId
     * @return
     */
    List<ShoppingCartItem> list(Long userId);

    /**
     * 添加购物车
     * @param shoppingCartItem
     */
    void addCartItem(ShoppingCartItem shoppingCartItem);
}
