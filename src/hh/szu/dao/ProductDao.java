package hh.szu.dao;

import hh.szu.domain.Product;
import hh.szu.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

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
}
