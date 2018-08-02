package hh.szu.web.servlet;

import hh.szu.domain.PageBean;
import hh.szu.domain.Product;
import hh.szu.sevice.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "productListByCategoryServlet", urlPatterns = "/productListByCategory")
public class productListByCategoryServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        String cid = request.getParameter("cid");
        String currentPageStr = request.getParameter("currentPage");
        if (currentPageStr == null) {
            currentPageStr = "1";
        }
        int currentPage = Integer.parseInt(currentPageStr);
        int curretCount = 12;
        ProductService service = new ProductService();

        PageBean pageBean = service.findProductListByCategory(cid, currentPage, curretCount);

        request.setAttribute("pageBean", pageBean);
        request.setAttribute("cid", cid);

        //浏览过的商品集合
        List<Product> historyProductList = new ArrayList<>();

        //将cookies进行解析，获得名为pids的cookie，获得浏览过的商品的pid
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pids".equals(cookie.getName())) {
                    String str = cookie.getValue();
                    String[] pids = str.split("-");
                    for (String pid : pids) {
                        historyProductList.add(service.findProductByPid(pid));
                    }
                }
            }
        }

        request.setAttribute("historyProductList",historyProductList);

        request.getRequestDispatcher("/product_list.jsp").forward(request, response);
    }
}

