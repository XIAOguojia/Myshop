package hh.szu.web.servlet;

import com.google.gson.Gson;
import hh.szu.domain.Category;
import hh.szu.domain.Order;
import hh.szu.sevice.AdminService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminServlet", urlPatterns = "/admin")
public class AdminServlet extends BaseServlet {
    //查找所有分类
    public void findAllCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdminService service = new AdminService();
        List<Category> categoryList = service.findAllCategory();
        if (categoryList != null) {
            Gson gson = new Gson();
            String json = gson.toJson(categoryList);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(json);

            System.out.println(json);
        }
    }

    //查找所有订单
    public void findAllOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdminService service = new AdminService();
        List<Order> orderList = service.findAllOrders();

        request.setAttribute("orderList",orderList);
        request.getRequestDispatcher("/admin/order/list.jsp").forward(request,response);
    }

    //查找该订单的订单详情
    public void findOrderInfoByOid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //测试加载
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String oid = request.getParameter("oid");
        AdminService service = new AdminService();
        List<Map<String,Object>> mapList = service.findOrderInfoByOid(oid);
        Gson gson = new Gson();
        String json = gson.toJson(mapList);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(json);

    }
}

