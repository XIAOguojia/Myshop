package hh.szu.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by intellij IDEA
 *
 * @author Raven
 * Date:2018/8/3
 * Time:19:21
 */
public class Cart {
    //该购物车存储的n个购物项
    private Map<String,CartItem> cartItems = new HashMap<>();

    //商品总计
    private double total;

    public Map<String, CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Map<String, CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
