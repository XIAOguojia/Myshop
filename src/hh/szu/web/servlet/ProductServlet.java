package hh.szu.web.servlet;

import com.google.gson.Gson;
import hh.szu.domain.*;
import hh.szu.sevice.ProductService;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "ProductServlet", urlPatterns = "/Product")
public class ProductServlet extends BaseServlet {
//    @Override
//    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.setCharacterEncoding("utf-8");
//        response.setContentType("text/html;charset=utf-8");
//    }
//
//    @Override
//    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.setCharacterEncoding("utf-8");
//    }

    //显示商品类别的功能
    public void category(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        ProductService service = new ProductService();
        List<Category> categoryList = service.findAllCategory();
        Gson gson = new Gson();
        String json = gson.toJson(categoryList);
        response.getWriter().write(json);
    }

    //显示首页
    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        ProductService service = new ProductService();
        //准备热门商品
        List<Product> hotProductList = service.findHotProductList();

        //准备最新商品
        List<Product> newProductList = service.findNewProductList();

        //准备分类数据
//        List<Category> categoryList = service.findAllCategory();

//        request.setAttribute("categoryList",categoryList);
        request.setAttribute("hotProductList", hotProductList);
        request.setAttribute("newProductList", newProductList);

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    //显示商品的详细信息
    public void productInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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


    //根据商品的类别获得商品的列表
    public void productListByCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        request.setAttribute("historyProductList", historyProductList);

        request.getRequestDispatcher("/product_list.jsp").forward(request, response);
    }


    //将商品添加到购物车中
    public void addProductToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        ProductService service = new ProductService();
        //获得要放到购物车中的商品的pid
        String pid = request.getParameter("pid");
        //获得商品的购买数量
        int buyNum = Integer.parseInt(request.getParameter("buyNum"));
        if (buyNum <= 0) {
            throw new IllegalArgumentException("购买商品的数量不能少于1");
        }

        //获得Product对象
        Product product = service.findProductByPid(pid);
        //计算该商品总共的价钱
        double subTotal = product.getShop_price() * buyNum;
        //将Product、buyNum、subTotal封装到CartItem中
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setBuyNum(buyNum);
        cartItem.setSubtotal(subTotal);

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }

        //先判断购物车中是否已经包含次购物项了--判断key是否已经存在
        //如果购物车中已经存在该商品--将现在买的数量与原有的数量进行相加操作
        Map<String, CartItem> cartItems = cart.getCartItems();
        if (cartItems.containsKey(pid)) {
            cartItem = cartItems.get(pid);
            //取出原有的商品购买数量与现在的相加
            cartItem.setBuyNum(cartItem.getBuyNum() + buyNum);
            //重新计算小计
            cartItem.setSubtotal(cartItem.getSubtotal() + subTotal);
        }
        //将购物项放入购物车中
        cartItems.put(pid, cartItem);


        //计算总计
        double total = cart.getTotal() + subTotal;
        cart.setTotal(total);

        //将购物车放回session中
        session.setAttribute("cart", cart);

        //重定向到cart.jsp,转发会有问题，以为转发地址没变会导致用户在刷新网页的时候自动再购买一次有bug
//        request.getRequestDispatcher("cart.jsp").forward(request,response);
        response.sendRedirect(request.getContextPath() + "/cart.jsp");
    }

    //将单一商品从购物车删除
    public void delProFromCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获得商品pid
        String pid = request.getParameter("pid");
        //获得购物车对象
        HttpSession session = request.getSession();
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            //获得购物项
            Map<String, CartItem> cartItems = cart.getCartItems();
            //重新计算总价钱,当前总价钱-对应购物项的小计
            //先计算金额再删，否则有空指针异常
            cart.setTotal(cart.getTotal() - cartItems.get(pid).getSubtotal());
            //删除对应pid的商品
            cartItems.remove(pid);
            //封装回去
            cart.setCartItems(cartItems);
        }

        session.setAttribute("cart", cart);

        response.sendRedirect(request.getContextPath() + "/cart.jsp");
    }
    //清空购物车
    public void clearCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("cart");
        response.sendRedirect(request.getContextPath() + "/cart.jsp");
    }
}

