package org.easyblog.service.base;

import org.easyblog.bean.Category;

import java.util.List;
import java.util.Map;

public interface ICategoryService {


    /**
     * 获取某个用户的所有允许显示的分类
     * @param userId
     * @return
     */
    List<Category> getUserAllViableCategory(int userId);

    /**
     * 根据分类ID获得一个分类记录
     * @param categoryId
     * @return
     */
    Category getCategory(int categoryId);


    /**
     * 保存一条分类信息
     * @param userId
     * @param categoryName
     * @return
     */
    int saveCategory(int userId,String categoryName);

    /**
     * 选择性更新分类列表的内容
     * @param categoryId
     * @param params
     * @return
     */
    boolean updateCategoryInfo(int categoryId, Map<String,Object> params);

}
