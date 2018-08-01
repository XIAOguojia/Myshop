package hh.szu.sevice;

import hh.szu.dao.ProductDao;
import hh.szu.domain.Category;
import hh.szu.domain.Product;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by intellij IDEA
 *
 * @author Raven
 * Date:2018/7/29
 * Time:14:00
 */
public class ProductService {
    //获得热门商品
    public List<Product> findHotProductList() {
        ProductDao dao = new ProductDao();
        List<Product> hotProductList = null;
        try {
            hotProductList = dao.findHotProductList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hotProductList;
    }

    //获得最新商品
    public List<Product> findNewProductList() {
        ProductDao dao = new ProductDao();
        List<Product> newProductList = null;
        try {
            newProductList = dao.findNewProductList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newProductList;
    }

    //准备分类数据
    public List<Category> findAllCategory() {
        ProductDao dao = new ProductDao();
        List<Category> categoryList = null;
        try {
            categoryList = dao.findAllCategory();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryList;
    }
}
