package com.qq.order.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qq.common.core.web.controller.BaseController;
import com.qq.common.core.web.domain.AjaxResult;
import com.qq.common.core.web.page.BaseQuery;
import com.qq.common.core.web.page.TableDataInfo;
import com.qq.common.log.annotation.Log;
import com.qq.common.system.pojo.SysOrder;
import com.qq.order.server.service.SysOrderService;
import com.qq.order.server.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author QinQiang
 * @Date 2022/5/9
 **/
@RestController
@RequestMapping("order")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController extends BaseController {
    private final SysOrderService orderService;

    @GetMapping("list")
    @Log(title = "order", funcDesc = "查询订单列表")
    public TableDataInfo list(BaseQuery query) {
        startPage();
        QueryWrapper<SysOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("number", query.getKeyword())
                .in("user_id", query.getIds())
                .between("create_time", query.getStartTime(), query.getEndTime());
        TableDataInfo dataTable = getDataTable(orderService.list(queryWrapper));
        clearPage();
        return dataTable;
    }

    @PostMapping("/saveOrder")
    @Log(title = "order", funcDesc = "保存订单")
    public AjaxResult saveOrder(@RequestBody ProductVO productVO) {
        log.info("订单服务开始保存订单");
        return AjaxResult.success(orderService.saveOrder(productVO));
    }

    @GetMapping("/detail")
    @Log(title = "order", funcDesc = "订单详情")
    public AjaxResult orderDetailInfo(Long orderId) {
        log.info("订单服务开始保存订单");
        return AjaxResult.success(orderService.getOrderInfo(orderId));
    }


}