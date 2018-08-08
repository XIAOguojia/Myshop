package hh.szu.sevice;

import hh.szu.dao.AdminDao;
import hh.szu.domain.Category;
import hh.szu.domain.Order;
import hh.szu.domain.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by intellij IDEA
 *
 * @author Raven
 * Date:2018/8/7
 * Time:11:29
 */
public class AdminService {
    public List<Category> findAllCategory() {
        AdminDao dao = new AdminDao();
        try {
            return dao.findAllCategory();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveProduct(Product product) throws SQLException {
        AdminDao dao = new AdminDao();
        dao.saveProduct(product);
    }

    public List<Order> findAllOrders() {
        AdminDao dao = new AdminDao();
        List<Order> orderList =null;
        try {
            orderList = dao.findAllOrders();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    public List<Map<String, Object>> findOrderInfoByOid(String oid) {
        AdminDao dao = new AdminDao();
        List<Map<String,Object>> mapList = null;
        try {
            mapList = dao.findOrderInfoByOid(oid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapList;
    }
}
