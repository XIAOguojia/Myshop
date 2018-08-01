package hh.szu.web.servlet;

import com.google.gson.Gson;
import hh.szu.domain.Category;
import hh.szu.sevice.ProductService;
import hh.szu.utils.JedisUtils;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CategoryServlet", urlPatterns = "/Category")
public class CategoryServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        ProductService service = new ProductService();

        //优化方案，使用缓存不用每次都从数据库中获取数据，但是要用到Linux，嫌烦就注释了。。
        //先从缓存中查询categoryList，如果有直接使用，没有则从数据库中查询，然后存到缓存中
        //1、获得jedis对象，连接redis数据库
//        Jedis redis = JedisUtils.getJedis();
//        String categoryListJson = redis.get("categoryListJson");
//        //2、判断categoryListJson是否为空
//        if (categoryListJson == null){
//            //准备分类数据
//            List<Category> categoryList = service.findAllCategory();
//            Gson gson = new Gson();
//            categoryListJson = gson.toJson(categoryList);
//            redis.set("categoryListJson",categoryListJson);
//        }
//
//
//        response.getWriter().write(categoryListJson);

        List<Category> categoryList = service.findAllCategory();
        Gson gson = new Gson();
        String json = gson.toJson(categoryList);
        response.getWriter().write(json);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

    }
}

