package hh.szu.web.servlet;

import hh.szu.domain.Product;
import hh.szu.sevice.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
        //获得当前页
        String currentPage = request.getParameter("currentPage");
        //获得商品类别
        String cid = request.getParameter("cid");

        //获得要查询商品的pid
        String pid = request.getParameter("pid");
        ProductService service = new ProductService();
        Product product = service.findProductByPid(pid);

        request.setAttribute("product", product);
        request.setAttribute("cid", cid);
        request.setAttribute("currentPage", currentPage);

        //获得客户端携带cookie--获得名字是pids的cookie
        String pids = pid;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pids".equals(cookie.getName())) {
                    pids = cookie.getValue();
                    String[] strings = pids.split("-");
                    List<String> list = new LinkedList<>(Arrays.asList(strings));
                    //判断集合中是否存在当前pid，如果存在则先删除再添加到头部，否则直接添加到头部
                    if (list.contains(pid)) {
                        list.remove(pid);
                    }
                    ((LinkedList<String>) list).addFirst(pid);

                    //将集合转回字符串
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < list.size() && i < 7; i++) {
                        stringBuilder.append(list.get(i));
                        if (i != list.size() - 1) {
                            stringBuilder.append("-");
                        }
                    }
                    pids = stringBuilder.toString();
                }
            }
        }

        //转发之前，创建cookie保存pid
        Cookie cookie = new Cookie("pids", pids);
        response.addCookie(cookie);

        request.getRequestDispatcher("/product_info.jsp").forward(request, response);
    }
}

