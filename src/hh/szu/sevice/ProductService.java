package hh.szu.sevice;

import hh.szu.dao.ProductDao;
import hh.szu.domain.Category;
import hh.szu.domain.PageBean;
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

    public PageBean findProductListByCategory(String cid, int currentPage, int curretCount) {
        ProductDao dao = new ProductDao();

        PageBean<Product> pageBean = new PageBean<>();

        //封装当前页
        pageBean.setCurrentPage(currentPage);
        //封装每页的条数
        pageBean.setCurrentCount(curretCount);
        //封装总条数
        int totalCount = 0;
        try {
            totalCount = dao.getTotalCount(cid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setTotalCount(totalCount);
        //封装总页数
        int totalPage = (int) Math.ceil(1.0 * totalCount / curretCount);
        pageBean.setTotalPage(totalPage);
        //封装当前页的显示数据
        int index = (currentPage - 1) * curretCount;
        List<Product> list = null;
        try {
            list = dao.findProductByPage(cid, index, curretCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setList(list);

        return pageBean;
    }

    public Product findProductByPid(String pid) {
        ProductDao dao = new ProductDao();
        Product product = null;
        try {
            product = dao.findProductByPid(pid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }
}
