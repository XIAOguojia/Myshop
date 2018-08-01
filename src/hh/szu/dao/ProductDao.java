package hh.szu.dao;

import hh.szu.domain.Category;
import hh.szu.domain.PageBean;
import hh.szu.domain.Product;
import hh.szu.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

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
}
