package hh.szu.web.servlet;

import hh.szu.domain.User;
import hh.szu.sevice.UserService;
import hh.szu.utils.CommonUtils;
import hh.szu.utils.MailUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import javax.mail.MessagingException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@WebServlet(name = "UserServlet", urlPatterns = "/User")
public class UserServlet extends BaseServlet {

    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String activeCode = request.getParameter("activeCode");
        UserService service = new UserService();
        try {
            service.active(activeCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //激活完毕，跳转到登录页面
        response.sendRedirect(request.getContextPath()+"/login.jsp");
    }

    public void checkUserName(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=utf-8");

        String username = request.getParameter("username");
        UserService service = new UserService();
        boolean isExist = service.checkUserName(username);
        String json = "{\"isExist\":" + isExist + "}";

        response.getWriter().write(json);

    }

    public void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            String emailMsg = user.getName()+"小可爱注册成功了啊，过来激活吧<a " +
                    "href='http://localhost:8080/myshop/active?activeCode="+activeCode+"'>" +
                    "http://localhost:8080/myshop/active?activeCode="+activeCode+"</a>";
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

    //用户登录
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        //获得输入的用户名和密码
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        //对密码进行加密
        //password = MD5Utils.md5(password);

        //将用户名和密码传递给service层
        UserService service = new UserService();
        User user = null;
        try {
            user = service.login(username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //判断用户是否登录成功 user是否是null
        if(user!=null){
            //登录成功
            //***************判断用户是否勾选了自动登录*****************
            String autoLogin = request.getParameter("autoLogin");

//            System.out.println(username+"  "+password+"  "+autoLogin);
            if("true".equals(autoLogin)){
                //要自动登录
                //创建存储用户名的cookie
                Cookie cookie_username = new Cookie("cookie_username",user.getUsername());
                cookie_username.setMaxAge(10*60);
                //创建存储密码的cookie
                Cookie cookie_password = new Cookie("cookie_password",user.getPassword());
                cookie_password.setMaxAge(10*60);

                response.addCookie(cookie_username);
                response.addCookie(cookie_password);

            }

            //***************************************************
            //将user对象存到session中
            session.setAttribute("user", user);

            //重定向到首页
            response.sendRedirect(request.getContextPath()+"/index.jsp");
        }else{
            request.setAttribute("loginError", "用户名或密码错误");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}

