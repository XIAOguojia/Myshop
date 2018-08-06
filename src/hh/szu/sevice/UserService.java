package hh.szu.sevice;

import hh.szu.dao.UserDao;
import hh.szu.domain.User;

import java.sql.SQLException;

/**
 * Created by intellij IDEA
 *
 * @author Raven
 * Date:2018/7/24
 * Time:11:12
 */
public class UserService {
    public boolean regist(User user) {
        UserDao dao = new UserDao();
        int row = 0;
        try {
            row = dao.regist(user);
        }catch (SQLException e){
            e.printStackTrace();
        }
       //注册信息成功插入数据库，row>0
        return row>0?true:false;
    }

    public void active(String activeCode) throws SQLException {
        UserDao dao = new UserDao();
        dao.active(activeCode);
    }

    public boolean checkUserName(String username) {
        UserDao dao = new UserDao();
        Long count = 0L;
        try {
            count = dao.checkUserName(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count>0;
    }

    //用户登录的方法
    public User login(String username, String password) throws SQLException {
        UserDao dao = new UserDao();
        return dao.login(username,password);
    }
}
