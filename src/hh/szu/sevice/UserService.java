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
}