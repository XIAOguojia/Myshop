package hh.szu.web.servlet;

import com.google.gson.Gson;
import hh.szu.domain.*;
import hh.szu.sevice.ProductService;
import hh.szu.utils.CommonUtils;
import hh.szu.utils.PaymentUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.ietf.jgss.Oid;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
        response.setContentType("text/html;charset=utf-8");

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

    //提交订单
    public void submitOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");
        //判断用户是否登录
        if (user == null){
            response.sendRedirect(request.getContextPath()+"/login.jsp");
            return;
        }

        //目的：封装好一个Order对象 传递给service层
        Order order = new Order();

        //1、private String oid;//该订单的订单号
        String oid = CommonUtils.getUUID();
        order.setOid(oid);

        //2、private Date ordertime;//下单时间
        //取当前时间
        Date nowdate = new Date();
        //转换时间格式
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        order.setOrdertime(Timestamp.valueOf(simpleDate.format(nowdate)));

        //3、private double total;//该订单的总金额
        //获得session中的购物车
        Cart cart = (Cart) session.getAttribute("cart");
        double total = cart.getTotal();
        order.setTotal(total);

        //4、private int state;//订单支付状态 1代表已付款 0代表未付款
        order.setState(0);

        //5、private String address;//收货地址
        order.setAddress(null);

        //6、private String name;//收货人
        order.setName(null);

        //7、private String telephone;//收货人电话
        order.setTelephone(null);

        //8、private User user;//该订单属于哪个用户
        order.setUser(user);

        //9、该订单中有多少订单项List<OrderItem> orderItems = new ArrayList<OrderItem>();
        //获得购物车中的购物项的集合map
        Map<String, CartItem> cartItems = cart.getCartItems();
        for (Map.Entry<String, CartItem> entry : cartItems.entrySet()) {
            //取出每一个购物项
            CartItem cartItem = entry.getValue();
            //创建新的订单项
            OrderItem orderItem = new OrderItem();
            //1)private String itemid;//订单项的id
            orderItem.setItemid(CommonUtils.getUUID());
            //2)private int count;//订单项内商品的购买数量
            orderItem.setCount(cartItem.getBuyNum());
            //3)private double subtotal;//订单项小计
            orderItem.setSubtotal(cartItem.getSubtotal());
            //4)private Product product;//订单项内部的商品
            orderItem.setProduct(cartItem.getProduct());
            //5)private Order order;//该订单项属于哪个订单
            orderItem.setOrder(order);

            //将该订单项添加到订单的订单项集合中
            order.getOrderItems().add(orderItem);
        }

        //order对象封装完毕
        //传递数据到service层
        ProductService service = new ProductService();
        service.submitOrder(order);

        session.setAttribute("order", order);

        //页面跳转
        response.sendRedirect(request.getContextPath() + "/order_info.jsp");
    }


    //确认订单的信息--更新收货人信息和在线支付
    public void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1、更新收货人信息
        Map<String, String[]> properties = request.getParameterMap();
        Order order = new Order();
        try {
            BeanUtils.populate(order, properties);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        ProductService service = new ProductService();
        service.updateOrder(order);

        //2、在线支付
		/*if(pd_FrpId.equals("ABC-NET-B2C")){
			//介入农行的接口
		}else if(pd_FrpId.equals("ICBC-NET-B2C")){
			//接入工行的接口
		}*/
        //.......

        //只接入一个接口，这个接口已经集成所有的银行接口了  ，这个接口是第三方支付平台提供的
        //接入的是易宝支付
        // 获得 支付必须基本数据
        String orderid = request.getParameter("oid");
        //String money = order.getTotal()+"";//支付金额
        String money = "0.01";//支付金额
        // 银行
        String pd_FrpId = request.getParameter("pd_FrpId");

        // 发给支付公司需要哪些数据
        String p0_Cmd = "Buy";
        String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
        String p2_Order = orderid;
        String p3_Amt = money;
        String p4_Cur = "CNY";
        String p5_Pid = "";
        String p6_Pcat = "";
        String p7_Pdesc = "";
        // 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
        // 第三方支付可以访问网址
        String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
        String p9_SAF = "";
        String pa_MP = "";
        String pr_NeedResponse = "1";
        // 加密hmac 需要密钥
        String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
                "keyValue");
        String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
                p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
                pd_FrpId, pr_NeedResponse, keyValue);


        String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId=" + pd_FrpId +
                "&p0_Cmd=" + p0_Cmd +
                "&p1_MerId=" + p1_MerId +
                "&p2_Order=" + p2_Order +
                "&p3_Amt=" + p3_Amt +
                "&p4_Cur=" + p4_Cur +
                "&p5_Pid=" + p5_Pid +
                "&p6_Pcat=" + p6_Pcat +
                "&p7_Pdesc=" + p7_Pdesc +
                "&p8_Url=" + p8_Url +
                "&p9_SAF=" + p9_SAF +
                "&pa_MP=" + pa_MP +
                "&pr_NeedResponse=" + pr_NeedResponse +
                "&hmac=" + hmac;

        //重定向到第三方支付平台
        response.sendRedirect(url);
    }

    //我的订单
        public void myOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        //判断用户是否登录
        if (user == null){
            response.sendRedirect(request.getContextPath()+"/login.jsp");
            return;
        }

        ProductService service = new ProductService();
        //查询该用户的所有的订单信息(单表查询orders表)
        //集合中的每一个Order对象的数据是不完整的 缺少List<OrderItem> orderItems数据
        List<Order> orderList = service.findAllOrders(user.getUid());
        //循环所有的订单 为每个订单填充订单项集合信息
        if (orderList != null) {
            for (Order order : orderList) {
                //获得每一个订单的oid
                String oid = order.getOid();
                //查询该订单的所有的订单项---mapList封装的是多个订单项和该订单项中的商品的信息
                List<Map<String, Object>> mapList = service.findAllOrderItemByOid(oid);
                //循环所有订单为每个订单填充订单项集合信息
                for (Map<String,Object> map:mapList){
                    try {
                        //从map中取出count，subtotal封装到OrderItem中
                        OrderItem item = new OrderItem();
                        BeanUtils.populate(item,map);
                        //从map中取出pimage，pname，shop_price封装到Product中
                        Product product = new Product();
                        BeanUtils.populate(product,map);
                        //将Product封装到OrderItem中
                        item.setProduct(product);
                        //将OrderItem封装到order的orderItems中
                        order.getOrderItems().add(item);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

            }
        }


        //orderList封装完整了
        request.setAttribute("orderList", orderList);

        request.getRequestDispatcher("/order_list.jsp").forward(request, response);
    }

    //退出登录
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //将user从session中删除
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        //将cookie从客户端删除
        Cookie cookie_username = new Cookie("cookie_username","");
        cookie_username.setMaxAge(0);
        //创建存储密码的cookie
        Cookie cookie_password = new Cookie("cookie_password","");
        cookie_password.setMaxAge(0);

        response.addCookie(cookie_username);
        response.addCookie(cookie_password);

        response.sendRedirect(request.getContextPath()+"/login.jsp");
    }
}

