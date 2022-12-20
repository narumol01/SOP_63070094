package com.example.sop_63070094;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

import static com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER;

@Route(value = "productView")
public class ProductView extends VerticalLayout {
    private ComboBox<String> cm_products;
    private TextField tf_name;
    private NumberField nf_cost, nf_profit, nf_price;
    private HorizontalLayout hl_button;
    private Button btn_add, btn_update, btn_del, btn_clear;
    private Notification nf;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public ProductView() {
        cm_products = new ComboBox<>();
        cm_products.setLabel("Product List");
        cm_products.setWidth("600px");

        tf_name = new TextField();
        tf_name.setLabel("Product Name");
        tf_name.setValue("");
        tf_name.setWidth("600px");

        nf_cost = new NumberField();
        nf_cost.setLabel("Product Cost");
        nf_cost.setValue(0.00);
        nf_cost.setWidth("600px");

        nf_profit = new NumberField();
        nf_profit.setLabel("Product Profit");
        nf_profit.setValue(0.00);
        nf_profit.setWidth("600px");

        nf_price = new NumberField();
        nf_price.setLabel("Product Price");
        nf_price.setValue(0.00);
        nf_price.setEnabled(false);
        nf_price.setWidth("600px");

        hl_button = new HorizontalLayout();
        btn_add = new Button("Add Product");
        btn_update = new Button("Update Product");
        btn_del = new Button("Delete Product");
        btn_clear = new Button("Clear Product");

        hl_button.add(btn_add, btn_update, btn_del, btn_clear);

        this.add(cm_products, tf_name, nf_cost, nf_profit, nf_price, hl_button);

        btn_add.addClickListener(event -> {
            String product_name = tf_name.getValue();
            Double product_cost = nf_cost.getValue();
            Double product_profit = nf_profit.getValue();
            Double product_price = WebClient
                    .create()
                    .get()
                    .uri("http://localhost:8080/getPrice/" + product_cost + "/" + product_profit)
                    .retrieve()
                    .bodyToMono(Double.class)
                    .block();
            nf_price.setValue(product_price);
            boolean success = (boolean) rabbitTemplate.convertSendAndReceive("ProductExchange", "add", new Product(product_name, product_cost, product_profit, product_price));
            if(success) {
                nf = Notification.show("เพิ่ม Product สำเร็จแล้ว", 500, TOP_CENTER);
            } else {
                nf = Notification.show("เพิ่ม Product ไม่สำเร็จ", 500, TOP_CENTER);
            }
        });

        btn_update.addClickListener(event -> {
            String product_name = tf_name.getValue();
            Double product_cost = nf_cost.getValue();
            Double product_profit = nf_profit.getValue();
            Double product_price = WebClient
                    .create()
                    .get()
                    .uri("http://localhost:8080/getPrice/" + product_cost + "/" + product_profit)
                    .retrieve()
                    .bodyToMono(Double.class)
                    .block();
            nf_price.setValue(product_price);
            Product product = (Product) rabbitTemplate.convertSendAndReceive("ProductExchange", "getname", product_name);
            boolean success = (boolean) rabbitTemplate.convertSendAndReceive("ProductExchange", "update", new Product(product.get_id(), product_name, product_cost, product_profit, product_price));
            if(success) {
                nf = Notification.show("อัพเดท Product สำเร็จแล้ว", 500, TOP_CENTER);
            } else {
                nf = Notification.show("อัพเดท Product ไม่สำเร็จ", 500, TOP_CENTER);
            }
        });

        btn_del.addClickListener(event -> {
            String product_name = tf_name.getValue();
            Product product = (Product) rabbitTemplate.convertSendAndReceive("ProductExchange", "getname", product_name);
            boolean success = (boolean) rabbitTemplate.convertSendAndReceive("ProductExchange", "delete", product);
            if(success) {
                nf = Notification.show("ลบ Product สำเร็จแล้ว", 500, TOP_CENTER);
                cm_products.setValue(null);
                tf_name.setValue("");
                nf_cost.setValue(0.00);
                nf_profit.setValue(0.00);
                nf_price.setValue(0.00);
            } else {
                nf = Notification.show("ลบ Product ไม่สำเร็จ", 500, TOP_CENTER);
            }
        });

        btn_clear.addClickListener(event -> {
            cm_products.setValue(null);
            tf_name.setValue("");
            nf_cost.setValue(0.00);
            nf_profit.setValue(0.00);
            nf_price.setValue(0.00);
        });

        cm_products.addFocusListener(event -> {
            List<Product> products = (List<Product>) rabbitTemplate.convertSendAndReceive("ProductExchange", "getall", "");
            ArrayList<String> product_name_list = new ArrayList<>();
            for(int i = 0; i < products.size(); i++){
                product_name_list.add(products.get(i).getProductName());
            }
            cm_products.setItems(product_name_list);
        });

        cm_products.addValueChangeListener(event -> {
            String product_name = event.getValue();

            if(product_name != null) {
                Product product = (Product) rabbitTemplate.convertSendAndReceive("ProductExchange", "getname", product_name);
                tf_name.setValue(product.getProductName());
                nf_cost.setValue(product.getProductCost());
                nf_profit.setValue(product.getProductProfit());
                nf_price.setValue(product.getProductPrice());

            }
        });

    }
}
