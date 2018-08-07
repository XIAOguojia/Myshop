package hh.szu.sevice;

import hh.szu.dao.ProductDao;
import hh.szu.domain.Category;
import hh.szu.domain.Order;
import hh.szu.domain.PageBean;
import hh.szu.domain.Product;
import hh.szu.utils.DataSourceUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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

    //提交订单 将订单和订单项的数据存储到数据库中
    public void submitOrder(Order order) {
        try {
            //1、开启事务
            DataSourceUtils.startTransaction();
            ProductDao dao = new ProductDao();
            //2、调用dao存储order表数据的方法
            dao.addOrders(order);
            //3、调用dao存储orderItem的方法
            dao.addOrderItem(order);
        } catch (SQLException e) {
            try {
                //有任何异常则回滚事务
                DataSourceUtils.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            try {
                //事务提交完成，然后释放资源
                DataSourceUtils.commitAndRelease();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateOrder(Order order) {
        ProductDao dao =new ProductDao();
        try {
            dao.updateOrder(order);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrderState(String r6_order) {
        ProductDao dao =new ProductDao();
        try {
            dao.updateOrderState(r6_order);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> findAllOrders(String uid) {
        ProductDao dao = new ProductDao();
        List<Order> orderList = null;
        try {
            orderList = dao.findAllOrders(uid);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderList;
    }

    public List<Map<String, Object>> findAllOrderItemByOid(String oid) {
        ProductDao dao = new ProductDao();
        List<Map<String,Object>> mapList =null;
        try {
            mapList= dao.findAllOrderItemByOid(oid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapList;

    }
}
