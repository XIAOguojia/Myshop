package hh.szu.web.servlet;

import hh.szu.domain.User;
import hh.szu.sevice.UserService;
import hh.szu.utils.CommonUtils;
import hh.szu.utils.MailUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@WebServlet(name = "RegisterServlet", urlPatterns = "/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        Map<String, String[]> properties = request.getParameterMap();
        User user = new User();
        try {
            //自己指定一个类型转换器（将String转成Date)
            ConvertUtils.register(new Converter() {
                @Override
                public Object convert(Class aClass, Object o) {
                    //将String转成date
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = simpleDateFormat.parse(o.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return date;
                }
            }, Date.class);
            BeanUtils.populate(user, properties);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

//        private String uid;用uuid来设置uid
        user.setUid(CommonUtils.getUUID());
//        private String telephone;设置电话号码为空
        user.setTelephone(null);
//        private int state;  //是否激活
//        一开始为未激活的状态，故设置为0
        user.setState(0);
//        private String code; //激活码
//        激活码可用uuid来替代
        String activeCode = CommonUtils.getUUID();
        user.setCode(activeCode);

        UserService service = new UserService();
        boolean isRegisterSuccess = service.regist(user);

        if (isRegisterSuccess) {
            //发送邮件激活
            String emailMsg = user.getName()+"你这小傻逼，终于注册成功了啊，过来激活吧<a " +
                    "href='http://localhost:8080/Myshop/activeCode="+activeCode+"'>" +
                    "http://localhost:8080/Myshop/activeCode="+activeCode+"</a>";
            try {
                MailUtils.sendMail(user.getEmail(),emailMsg);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            //注册成功，跳转到邮箱激活页面
            response.sendRedirect(request.getContextPath()+"/registerSuccess.jsp");
        } else {
            //注册失败，返回错误页面
            response.sendRedirect(request.getContextPath()+"/registerFail.jsp");
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
    }
}

