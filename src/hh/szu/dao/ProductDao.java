package hh.szu.dao;

import hh.szu.domain.*;
import hh.szu.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by intellij IDEA
 *
 * @author Raven
 * Date:2018/7/29
 * Time:14:04
 */
public class ProductDao {
    //获得热门商品
    public List<Product> findHotProductList() throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where is_hot=? limit ?,?";
        return queryRunner.query(sql, new BeanListHandler<Product>(Product.class), 1, 0, 9);
    }

    //获得热门商品
    public List<Product> findNewProductList() throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product order by pdate desc limit ?,?";
        return queryRunner.query(sql, new BeanListHandler<Product>(Product.class),  0, 9);
    }

    //准备分类数据
    public List<Category> findAllCategory() throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from category";
        return queryRunner.query(sql, new BeanListHandler<Category>(Category.class));
    }

    //获得总条数
    public int getTotalCount(String cid) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select count(*) from product where cid = ?";
        Long totalCount = (Long)queryRunner.query(sql,new ScalarHandler(),cid);
        return totalCount.intValue();
    }

    public List<Product> findProductByPage(String cid, int index, int curretCount) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where cid=? limit ?,?";
        List<Product> list = queryRunner.query(sql,new BeanListHandler<Product>(Product.class),cid,index,curretCount);
        return list;
    }

    public Product findProductByPid(String pid) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where pid=?";
        return queryRunner.query(sql,new BeanHandler<>(Product.class),pid);
    }

    //调用dao存储order表数据的方法
    public void addOrders(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "insert into orders values(?,?,?,?,?,?,?,?)";
        Connection conn = DataSourceUtils.getConnection();
        runner.update(conn,sql, order.getOid(),order.getOrdertime(),order.getTotal(),
                order.getState(),order.getAddress(),order.getName(),order.getTelephone(),
                order.getUser().getUid());
    }

    // 调用dao存储orderItem的方法
    public void addOrderItem(Order order) throws SQLException {
        QueryRunner runner = new QueryRunner();
        String sql = "insert into orderitem values(?,?,?,?,?)";
        Connection connection = DataSourceUtils.getConnection();
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem: orderItems){
            runner.update(connection,sql,orderItem.getItemid(),orderItem.getCount(),
                    orderItem.getSubtotal(),orderItem.getProduct().getPid(),
                    orderItem.getOrder().getOid());
        }
    }

    public void updateOrder(Order order) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update orders set address=?,name=?,telephone=? where oid=?";
        queryRunner.update(sql,order.getAddress(),order.getName(),order.getTelephone(),order.getOid());
    }

    public void updateOrderState(String r6_order) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update orders set state=? where oid=?";
        queryRunner.update(sql,1,r6_order);
    }

    public List<Order> findAllOrders(String uid) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from orders where uid=?";
        return queryRunner.query(sql,new BeanListHandler<>(Order.class),uid);
    }

    public List<Map<String, Object>> findAllOrderItemByOid(String oid) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select i.count,i.subtotal,p.pimage,p.pname,p.shop_price from orderitem i,product p where i.pid=p.pid and i.oid=?";
        return queryRunner.query(sql,new MapListHandler(),oid);
    }
}
