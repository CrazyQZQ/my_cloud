package com.qq.product.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qq.common.core.exception.ServiceException;
import com.qq.common.core.web.page.BaseQuery;
import com.qq.common.system.mapper.SysObjectImagesMapper;
import com.qq.common.system.pojo.SysBrand;
import com.qq.common.system.pojo.SysObjectImages;
import com.qq.common.system.pojo.SysProductType;
import com.qq.common.system.service.MinIoService;
import com.qq.common.system.utils.OauthUtils;
import com.qq.product.server.mapper.SysBrandMapper;
import com.qq.product.server.mapper.SysProductTypeMapper;
import com.qq.product.server.service.SysProductTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author QinQiang
 * @Date 2022/5/16
 **/
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SysProductTypeServiceImpl extends ServiceImpl<SysProductTypeMapper, SysProductType>
        implements SysProductTypeService {

    private final MinIoService minIoService;
    private final SysObjectImagesMapper sysObjectImagesMapper;
    private final SysBrandMapper sysBrandMapper;

    /**
     * 查询品类树
     *
     * @return
     */
    @Override
    public List<Tree<Long>> queryTreeList() {

        List<TreeNode<Long>> nodeList = CollUtil.newArrayList();
        List<SysProductType> productTypes = this.baseMapper.getProductTypeList(null);
        for (SysProductType type : productTypes) {
            TreeNode<Long> node = new TreeNode<>(type.getId(), type.getParentId(), type.getName(), type.getOrderNum());
            Map<String, Object> extra = MapUtil.newHashMap();
            extra.put("imageUrls", type.getImageUrls());
            node.setExtra(extra);
            nodeList.add(node);
        }
        // 0表示最顶层的id是0
        return TreeUtil.build(nodeList, 0L);
    }

    /**
     * 新增品类
     *
     * @param productType
     */
    @Override
    @Transactional
    public void addProductType(SysProductType productType) {
        productType.setCreateBy(OauthUtils.getCurrentUserName());
        productType.setCreateTime(new Date());
        this.baseMapper.insert(productType);
        // 保存图片
        if (CollUtil.isNotEmpty(productType.getImageUrls())) {
            for (String imageUrl : productType.getImageUrls()) {
                SysObjectImages sysObjectImages = new SysObjectImages();
                sysObjectImages.setObjectId(productType.getId());
                sysObjectImages.setImageUrl(imageUrl);
                sysObjectImages.setObjectType(1);
                sysObjectImagesMapper.insert(sysObjectImages);
            }
        }
    }

    /**
     * 修改品类
     *
     * @param productType
     */
    @Override
    @Transactional
    public void updateProductType(SysProductType productType) {
        if (productType.getId() == null) {
            throw new ServiceException("品类id不能为空！");
        }
        productType.setUpdateBy(OauthUtils.getCurrentUserName());
        productType.setUpdateTime(new Date());
        int i = this.baseMapper.updateById(productType);
        if (i == 0) {
            throw new ServiceException("品类不存在！");
        }
        // 保存图片
        if (CollUtil.isNotEmpty(productType.getImageUrls())) {
            sysObjectImagesMapper.delete(new QueryWrapper<SysObjectImages>().eq("object_id", productType.getId()).eq("object_type", 1));
            for (String imageUrl : productType.getImageUrls()) {
                SysObjectImages sysObjectImages = new SysObjectImages();
                sysObjectImages.setObjectId(productType.getId());
                sysObjectImages.setImageUrl(imageUrl);
                sysObjectImages.setObjectType(1);
                sysObjectImagesMapper.insert(sysObjectImages);
            }
        }
    }

    /**
     * 删除品类
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteProductType(Long id) {
        if (id == null) {
            throw new ServiceException("品类id不能为空！");
        }
        SysProductType productType = this.baseMapper.selectById(id);
        if (productType == null) {
            throw new ServiceException("品类不存在！");
        }
        Integer children = this.baseMapper.selectCount(new QueryWrapper<SysProductType>().eq("parent_id", id));
        if (children > 0) {
            throw new ServiceException("该品类下有子品类，不能删除！");
        }
        Integer childBrandCount = sysBrandMapper.selectCount(new QueryWrapper<SysBrand>().eq("type_id", id));
        if (childBrandCount > 0) {
            throw new ServiceException("该品类下有品牌，不能删除！");
        }
        List<String> images = sysObjectImagesMapper.selectList(new QueryWrapper<SysObjectImages>().eq("object_id", id).eq("object_type", 1))
                .stream().map(SysObjectImages::getImageUrl)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(images)) {
            sysObjectImagesMapper.delete(new QueryWrapper<SysObjectImages>().eq("object_id", id).eq("object_type", 1));
            minIoService.deleteFileByFullPath(images);
        }
    }

    /**
     * 查询品类列表
     *
     * @param query
     * @return
     */
    @Override
    public List<SysProductType> list(BaseQuery query) {
        return this.baseMapper.getProductTypeList(query);
    }
}
