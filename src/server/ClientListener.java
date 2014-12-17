/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import classesdb.*;
import java.io.*;
import java.net.*;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author insane
 */
public class ClientListener implements Serializable, Runnable {

    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    private int shopId = 0;
    private int userId = 0;
    private JTextArea log;

    public static final int GET_USERS_LIST = 3;
    public static final int ADMINISTRATOR = 4;
    public static final int DIRECTOR = 5;
    public static final int CASHIER = 6;
    public static final int DROP_USER_BY_ID = 7;
    public static final int ADD_USER = 8;
    public static final int UPDATE_USER_ROLE = 9;
    public static final int GET_TYPE_CATEGORY = 10;
    public static final int ADD_TYPE_CATEGORY = 11;
    public static final int ADD_PRODUCT = 12;
    public static final int GET_ALL_PRODUCT = 13;
    public static final int DROP_PRODUCT_BY_ID = 14;
    public static final int DROP_PRODUCT_FROM_SHOP = 15;
    public static final int UPDATE_PRODUCT = 16;
    public static final int GET_ALL_BUYERS = 17;
    public static final int ADD_BUYERS = 18;
    public static final int ADD_ORDER = 19;
    public static final int GET_ALL_ORDER = 20;
    public static final int UPDATE_ORDER_STATUS = 21;
    public static final int DELETE_ORDER = 22;
    public static final int ADD_SHOP = 23;
    public static final int GET_ALL_SHOPS = 24;
    public static final int GET_ITEM_ORDERS_BY_ORDER = 25;
    public static final int DROP_TYPE = 26;
    public static final int DROP_STORE = 27;

    public ClientListener(Socket client, DataInputStream in, DataOutputStream out, int shop, int userId, JTextArea log) throws IOException {
        this.client = client;
        this.in = in;
        this.out = out;
        this.shopId = shop;
        this.userId = userId;
        this.log = log;
        (new Thread(this)).start();
    }

    @Override
    public void run() {
        try {
            while (true) {

                int key = in.readInt();

                switch (key) {
                    case GET_USERS_LIST:
                        sendUsersList();
                        break;
                    case DROP_USER_BY_ID:
                        dropUserById();
                        break;
                    case ADD_USER:
                        addUser();
                        break;
                    case UPDATE_USER_ROLE:
                        updateUserRole();
                        break;
                    case GET_TYPE_CATEGORY:
                        getTypeCategory();
                        break;
                    case ADD_TYPE_CATEGORY:
                        addTypeCategory();
                        break;
                    case ADD_PRODUCT:
                        addProduct();
                        break;
                    case GET_ALL_PRODUCT:
                        getAllProduct();
                        break;
                    case DROP_PRODUCT_BY_ID:
                        dropProductById();
                        break;
                    case DROP_PRODUCT_FROM_SHOP:
                        dropProductFromShop();
                        break;
                    case UPDATE_PRODUCT:
                        updateProduct();
                        break;
                    case GET_ALL_BUYERS:
                        sendAllBuyers();
                        break;
                    case ADD_BUYERS:
                        addBuyer();
                        break;
                    case ADD_ORDER:
                        addOrder();
                        break;
                    case GET_ALL_ORDER:
                        getAllOrders();
                        break;
                    case UPDATE_ORDER_STATUS:
                        updateOrderStatus();
                        break;
                    case DELETE_ORDER:
                        deleteOrder();
                        break;
                    case ADD_SHOP:
                        addShop();
                        break;
                    case GET_ALL_SHOPS:
                        getAllShops();
                        break;
                    case GET_ITEM_ORDERS_BY_ORDER:
                        getItemOrderbyOrder();
                        break;
                    case DROP_TYPE:
                        dropType();
                        break;
                    case DROP_STORE:
                        dropStore();
                        break;

                }

            }
        } catch (IOException ex) {
            log.setText(log.getText() + "\n" + ex.getMessage());
        } catch (SQLException ex) {
            log.setText(log.getText() + "\n" + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            log.setText(log.getText() + "\n" + ex.getMessage());
        }
    }

    private void sendUsersList() throws IOException, SQLException {
        ObjectOutputStream outObject = new ObjectOutputStream(out);

        ResultSet set = DataBaseConnection.getInstance().getStatement().executeQuery("SELECT * FROM users u LEFT OUTER JOIN statistics s ON u.id_users = s.id_user WHERE id_store = " + shopId);

        while (set.next()) {
            User u = new User(set.getInt(1), set.getInt(2), set.getString(3), set.getString(4), set.getString(5), set.getString(6), set.getInt(7));
            Statistics s = new Statistics(set.getInt(8), set.getInt(9), set.getInt(10));
            u.setStatistics(s);
            outObject.writeObject(u);
        }
        System.out.println("111111");

        outObject.writeObject(null);
    }

    private void dropUserById() throws IOException, SQLException {
        DataBaseConnection.getInstance().getStatement().executeUpdate("DELETE FROM users WHERE id_users = " + in.readInt());
    }

    private void addUser() throws IOException, SQLException, ClassNotFoundException {
        ObjectInputStream inObject = new ObjectInputStream(in);
        User user = (User) inObject.readObject();
        DataBaseConnection.getInstance().getStatement().executeUpdate(
                "INSERT INTO users(first_name,last_name,login,password,role,id_store) VALUES('"
                + user.getFirstName() + "','"
                + user.getLastName() + "','"
                + user.getLogin() + "','"
                + user.getPassword() + "',"
                + user.getRole() + ","
                + user.getStoreId() + ")");
    }

    private void updateUserRole() throws IOException, SQLException {
        DataBaseConnection.getInstance().getStatement().executeUpdate("UPDATE users SET role = " + in.readInt() + " WHERE id_users = " + in.readInt());
    }

    private void getTypeCategory() throws IOException, SQLException {
        ObjectOutputStream outObject = new ObjectOutputStream(out);
        ResultSet result = DataBaseConnection.getInstance().getStatement().executeQuery("SELECT * FROM type");
        while (result.next()) {
            Type type = new Type();
//            type.setDiscount(result.getInt("discount"));
            type.setName(result.getString("name"));
            type.setTypeId(result.getInt("id_type"));
            type.setParentId(result.getInt("id_parent"));
            type.setDiscount(result.getInt("discount"));
            outObject.writeObject(type);
        }
        outObject.writeObject(null);
    }

    private void addTypeCategory() throws IOException, SQLException, ClassNotFoundException {
        ObjectInputStream inObject = new ObjectInputStream(in);
        Type type = (Type) inObject.readObject();
        DataBaseConnection.getInstance().getStatement().executeUpdate(
                "INSERT INTO type(name,id_parent,discount) VALUES('"
                + type.getName() + "',"
                + ((type.getParentId() != -1) ? type.getParentId() : "null") + "," + type.getDiscount() + ")");
    }

    private void addProduct() throws IOException, SQLException, ClassNotFoundException {
        ObjectInputStream inObject = new ObjectInputStream(in);
        Product p = (Product) inObject.readObject();
        Statement statement = DataBaseConnection.getInstance().getStatement();
        ResultSet result = statement.executeQuery(
                "SELECT * FROM product WHERE color = '" + p.getColor()
                + "' AND "
                + " price = " + p.getPrice() + " AND size = " + p.getSize()
                + " AND id_type = " + p.getType().getTypeId());
        if (result.next()) {
            int id_p = result.getInt("id_product");
            result = statement.executeQuery(
                    "SELECT * FROM product_in_shop WHERE id_product = " + id_p + " AND id_store = " + shopId);
            if (result.next()) {
                statement.executeUpdate("UPDATE product_in_shop SET count_product = "
                        + p.getShops().iterator().next().getCount() + " WHERE id_product = "
                        + id_p + " AND id_store = " + shopId);
            } else {
                statement.executeUpdate("INSERT INTO product_in_shop VALUES(" + shopId + "," + id_p + ","
                        + p.getShops().iterator().next().getCount() + ")");
            }
        } else {
            statement.executeUpdate(
                    "INSERT INTO product(id_type, color,price,size) VALUES ("
                    + p.getType().getTypeId() + ",'" + p.getColor() + "'," + p.getPrice()
                    + "," + p.getSize() + ")");
            ResultSet rs = statement.executeQuery("select last_insert_id() as last_id from product");
            if (rs.next()) {
                String lastid = rs.getString("last_id");
                statement.executeUpdate("INSERT INTO product_in_shop VALUES("
                        + shopId + "," + lastid + "," + p.getShops().iterator().next().getCount() + ")");
//                System.out.println("Account id is: " + lastid);
//                account.setAccount_id(Integer.parseInt(lastid));
            }

        }
    }

    private void getAllProduct() throws IOException, SQLException {
        ObjectOutputStream outObject = new ObjectOutputStream(out);
        ResultSet result = DataBaseConnection.getInstance().getStatement().executeQuery(
                "SELECT * FROM product p "
                + "INNER JOIN type t "
                + "ON p.id_type = t.id_type "
                + "LEFT OUTER JOIN product_in_shop pis "
                + "ON p.id_product = pis.id_product "
                + "INNER JOIN stores s "
                + "ON s.id_store = pis.id_store "
                + "ORDER BY p.id_product");
        result.next();
        boolean end = false;
        do {
            Product product = new Product();
            product.setColor(result.getString("color"));
            product.setPrice(result.getInt("price"));
            product.setProductid(result.getInt("id_product"));
            product.setSize(result.getInt("size"));
            Type type = new Type();
//            type.setDiscount(result.getInt("discount"));
            type.setName(result.getString("name"));
            type.setTypeId(result.getInt("id_type"));
            type.setParentId(result.getInt("id_parent"));
            product.setType(type);
            Set<ProductInShop> set = new TreeSet<>();
            ProductInShop p = new ProductInShop();

            Store store = new Store();
            store.setAddress(result.getString("address"));
            store.setStoreId(result.getInt("id_store"));
            p.setCount(result.getInt("count_product"));
            p.setProduct(product);
            p.setStore(store);
            set.add(p);

            while ((end = result.next())) {
                if (result.getInt("id_product") != product.getProductid()) {
                    break;
                }
                store.setAddress(result.getString("address"));
                store.setStoreId(result.getInt("id_store"));
                p.setCount(result.getInt("count_product"));
                p.setProduct(product);
                p.setStore(store);
                set.add(p);
            }
            product.setShops(set);
            outObject.writeObject(product);

        } while (end);
        outObject.writeObject(null);
    }

    private void dropProductById() throws IOException, SQLException {
        DataBaseConnection.getInstance().getStatement().executeUpdate("DELETE FROM product WHERE id_product = " + in.readInt());

    }

    private void dropProductFromShop() throws IOException, SQLException {
        DataBaseConnection.getInstance().getStatement().executeUpdate(
                "DELETE FROM product_in_shop WHERE id_product = " + in.readInt() + " AND id_store = " + shopId);

    }

    private void updateProduct() throws IOException, SQLException, ClassNotFoundException {
        ObjectInputStream inObject = new ObjectInputStream(in);
        Product p = (Product) inObject.readObject();
        Statement statement = DataBaseConnection.getInstance().getStatement();
        statement.executeUpdate("UPDATE product SET price = " + p.getPrice()
                + ", color = '" + p.getColor() + "', size = " + p.getSize()
                + ", id_type = " + p.getType().getTypeId()
                + " WHERE id_product = " + p.getProductid()
        );
        statement.executeUpdate("UPDATE product_in_shop SET count_product =" + p.getShops().iterator().next().getCount() + " WHERE id_product = "
                + p.getProductid() + " AND id_store = " + shopId);
    }

    private void sendAllBuyers() throws IOException, SQLException {
        Statement statement = DataBaseConnection.getInstance().getStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM buyers");
        ObjectOutputStream outObject = new ObjectOutputStream(out);
        while (result.next()) {
            Buyers b = new Buyers(result.getInt("id_buyer"),
                    result.getString("first_name"),
                    result.getString("last_name"), result.getString("phone")
            );
            b.setDiscount(result.getInt("discount"));
            outObject.writeObject(b);
        }
        outObject.writeObject(null);
    }

    private void addBuyer() throws IOException, SQLException, ClassNotFoundException {
        ObjectInputStream inObject = new ObjectInputStream(in);
        Buyers p = (Buyers) inObject.readObject();
        DataBaseConnection.getInstance().getStatement().executeUpdate(
                "INSERT INTO buyers(first_name,last_name,phone,discount) VALUES('"
                + p.getFirstName() + "','" + p.getLastName() + "',' " + p.getPhone() + "',0)");
    }

    private void addOrder() throws IOException, SQLException, ClassNotFoundException {
        ObjectInputStream inObject = new ObjectInputStream(in);
        Order order = (Order) inObject.readObject();
        Statement statement = DataBaseConnection.getInstance().getStatement();
        statement.executeUpdate("INSERT INTO orders(id_buyers,id_user,status)"
                + " VALUES(" + order.getBuyer().getBuyersId() + ","
                + userId + ",'" + order.getStatus() + "')");
        int orderId = 0;
        ResultSet rs = statement.executeQuery("select last_insert_id() as last_id from orders");
        if (rs.next()) {
            String lastid = rs.getString("last_id");
            orderId = Integer.parseInt(lastid);
        }

        for (ItemOrder item : order.getItemOrders()) {
            statement.executeUpdate("INSERT INTO item_orders(id_order,id_product,count) "
                    + "VALUES(" + orderId + "," + item.getProduct().getProductid() + "," + item.getCount() + ")");
        }

        CallableStatement callableStatement
                = DataBaseConnection.getInstance().getConnection().prepareCall("call set_discount(?)");
        callableStatement.setInt(1, orderId);
        callableStatement.executeUpdate();
    }

    private void getAllOrders() throws IOException, SQLException {
        ResultSet result = DataBaseConnection.getInstance().getStatement().executeQuery("SELECT * FROM orders o INNER JOIN buyers b ON o.id_buyers = b.id_buyer"
                + " INNER JOIN users u ON u.id_users = o.id_user");
        ObjectOutputStream outObject = new ObjectOutputStream(out);
        System.out.println("awdawd");
        while (result.next()) {
            Order order = new Order();
            Buyers b = new Buyers();
            b.setBuyersId(result.getInt("id_buyer"));
            b.setFirstName(result.getString(5));
            b.setLastName(result.getString(6));
            b.setPhone(result.getString("phone"));
            order.setBuyer(b);
            User user = new User();
            user.setUserId(result.getInt("id_users"));
            user.setFirstName(result.getString(12));
            user.setLastName(result.getString(13));
            order.setUser(user);
            order.setStatus(result.getString("status"));
            order.setOrderId(result.getInt("id_orders"));
            outObject.writeObject(order);
        }
        outObject.writeObject(null);
    }

    private void updateOrderStatus() throws IOException, SQLException {
        int orderId = in.readInt();
        String newStatus = in.readUTF();
        DataBaseConnection.getInstance().getStatement().executeUpdate("UPDATE orders SET status ='" + newStatus + "' WHERE id_orders = "
                + orderId);
    }

    private void deleteOrder() throws IOException, SQLException {
        DataBaseConnection.getInstance().getStatement().executeUpdate("DELETE FROM orders WHERE id_orders = " + in.readInt());
    }

    private void addShop() throws IOException, SQLException {
        DataBaseConnection.getInstance().getStatement().executeUpdate(
                "INSERT INTO stores(address,phone) VALUES('" + in.readUTF() + "','" + in.readUTF() + "')");
    }

    private void getAllShops() throws IOException, SQLException {
        ResultSet result = DataBaseConnection.getInstance().getStatement().executeQuery("select * from stores");
        ObjectOutputStream outObject = new ObjectOutputStream(out);
        while (result.next()) {
            Store store = new Store();
            store.setStoreId(result.getInt("id_store"));
            store.setAddress(result.getString("address"));
            store.setPhone(result.getString("phone"));
            outObject.writeObject(store);
        }
        outObject.writeObject(null);
    }

    private void getItemOrderbyOrder() throws IOException, SQLException {
        int orderId = in.readInt();
        ResultSet result = DataBaseConnection.getInstance().getStatement().executeQuery("select * from item_orders io "
                + " INNER JOIN product p ON io.id_product = p.id_product"
                + " INNER JOIN type t ON t.id_type = p.id_type where id_order = " + orderId);
        ObjectOutputStream outObject = new ObjectOutputStream(out);
        while (result.next()) {
            ItemOrder item = new ItemOrder();
            item.setCount(result.getInt("count"));
            item.setItemOrderId(result.getInt("id_item_orders"));
            item.setOrderId(result.getInt("id_order"));
            Product p = new Product();
            p.setColor(result.getString("color"));
            p.setPrice(result.getInt("price"));
            p.setProductid(result.getInt("id_product"));
            p.setSize(result.getInt("size"));
            Type t = new Type();
            t.setName(result.getString("name"));
            p.setType(t);
            item.setProduct(p);
            outObject.writeObject(item);
        }
        outObject.writeObject(null);

    }

    private void dropType() throws IOException, SQLException {
        DataBaseConnection.getInstance().getStatement().executeUpdate("DELETE FROM type WHERE name = '" + in.readUTF() + "'");

    }

    private void dropStore() throws IOException, SQLException {
        DataBaseConnection.getInstance().getStatement().executeUpdate("DELETE FROM stores WHERE id_store = " + in.readInt());
    }

}
