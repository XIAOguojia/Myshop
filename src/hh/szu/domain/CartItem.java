package hh.szu.domain;

/**
 * Created by intellij IDEA
 *
 * @author Raven
 * Date:2018/8/3
 * Time:19:20
 */
public class CartItem {
    private  Product product;
    private  int buyNum;
    private double subtotal;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(int buyNum) {
        this.buyNum = buyNum;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
