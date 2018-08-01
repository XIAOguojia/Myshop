package hh.szu.web.servlet;

import hh.szu.domain.PageBean;
import hh.szu.sevice.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        if (currentPageStr == null){
            currentPageStr = "1";
        }
        int currentPage = Integer.parseInt(currentPageStr);
        int curretCount = 12;
        ProductService service = new ProductService();

        PageBean pageBean = service.findProductListByCategory(cid,currentPage,curretCount);

        request.setAttribute("pageBean",pageBean);
        request.setAttribute("cid",cid);

        request.getRequestDispatcher("/product_list.jsp").forward(request,response);
    }
}

