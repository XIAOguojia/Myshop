package hh.szu.dao;

import hh.szu.domain.User;
import hh.szu.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

/**
 * Created by intellij IDEA
 *
 * @author Raven
 * Date:2018/7/24
 * Time:11:19
 */
public class UserDao {
    public int regist(User user) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "insert into user values(?,?,?,?,?,?,?,?,?,?)";
        int row = queryRunner.update(sql,user.getUid(),user.getUsername(),user.getPassword(),
                user.getName(),user.getEmail(),user.getTelephone(),user.getBirthday(),
                user.getSex(),user.getState(),user.getCode());
        return row;
    }

    //激活
    public void active(String activeCode) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "update user set state=? where code=?";
        queryRunner.update(sql,1,activeCode);
    }

    //校验用户名是否存在
    public Long checkUserName(String username) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select count(*) from user where username=?";
        Long query = (Long) runner.query(sql, new ScalarHandler(), username);
        return query;
    }

    //用户登录的方法
    public User login(String username, String password) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from user where username=? and password=?";
        return runner.query(sql, new BeanHandler<User>(User.class), username,password);
    }
}
