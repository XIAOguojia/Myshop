package hh.szu.web.servlet;

import hh.szu.domain.Category;
import hh.szu.domain.Product;
import hh.szu.sevice.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "IndexServlet", urlPatterns = "/Index")
public class IndexServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        ProductService service = new ProductService();
        //准备热门商品
        List<Product> hotProductList = service.findHotProductList();

        //准备最新商品
        List<Product> newProductList = service.findNewProductList();

        //准备分类数据
//        List<Category> categoryList = service.findAllCategory();
//
//        request.setAttribute("categoryList",categoryList);
        request.setAttribute("hotProductList", hotProductList);
        request.setAttribute("newProductList", newProductList);

        request.getRequestDispatcher("/index.jsp").forward(request,response);
    }
}

