package hh.szu.web.servlet;

import hh.szu.domain.Product;
import hh.szu.sevice.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ProductInfoServlet", urlPatterns = "/productInfo")
public class ProductInfoServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        String pid =request.getParameter("pid");
        ProductService service = new ProductService();
        Product product = service.findProductByPid(pid);

        request.setAttribute("product",product);
        request.getRequestDispatcher("/product_info.jsp").forward(request,response);
    }
}

