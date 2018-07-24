package hh.szu.dao;

import hh.szu.domain.User;
import hh.szu.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;

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
}
