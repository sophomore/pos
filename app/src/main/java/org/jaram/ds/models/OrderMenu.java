package org.jaram.ds.models;

import com.google.gson.annotations.SerializedName;

import org.jaram.ds.data.Data;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jdekim43 on 2016. 1. 28..
 * TODO: id 대신 object 로 다룰지 확인
 */
public class OrderMenu extends RealmObject {

    @PrimaryKey
    @SerializedName("id") private int id;
    @Ignore
    @SerializedName("menu_id") private int menuId;
    private Menu menu;
    @SerializedName("order") private Order order;
    @SerializedName("pay") private int pay;
    @SerializedName("curry") private boolean curry;
    @SerializedName("twice") private boolean twice;
    @SerializedName("takeout") private boolean takeout;
    @SerializedName("totalprice") private int totalPrice;
    @Ignore private boolean isPay;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public Menu getMenu() {
        if (menu == null) {
            menu = Realm.getDefaultInstance().where(Menu.class)
                    .equalTo("id", menuId)
                    .findFirst();
        }
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public boolean isCurry() {
        return curry;
    }

    public void setCurry(boolean curry) {
        this.curry = curry;
    }

    public boolean isTwice() {
        return twice;
    }

    public void setTwice(boolean twice) {
        this.twice = twice;
    }

    public boolean isTakeout() {
        return takeout;
    }

    public void setTakeout(boolean takeout) {
        this.takeout = takeout;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isPay() {
        return isPay;
    }

    public void setPay(boolean pay) {
        isPay = pay;
    }

    public static int calculateTotalPrice(OrderMenu orderMenu) {
        if (orderMenu.menu == null) {
            return 0;
        }
        int totalPrice = orderMenu.menu.getPrice();
        totalPrice += orderMenu.isCurry() ? Data.CURRY : 0;
        totalPrice += orderMenu.isTwice() ? Data.TWICE : 0;
        totalPrice += orderMenu.isTakeout() ? Data.TAKEOUT : 0;
        return totalPrice;
    }
}
