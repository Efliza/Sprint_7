import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName; // импорт DisplayName
import io.qameta.allure.Description; // импорт Description
import java.util.Arrays;
import java.util.ArrayList;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static constants.Handle.*;
import static constants.Url.URL_SAMOKAT;

public class OrderListTest {
    int trackOrderForList;

    @Before
    public void setUp() {
        RestAssured.baseURI = URL_SAMOKAT;

        //Делаем заказ на случай, если до этого никто не оформлял заказ, и список пуст
        String colorSamokatBlack = "BLACK";
        Order order = new Order("Настя", "Иванова", "Усачева 3", "4", "79110930605", "5", "2024-02-27", "Заранее спасибо", new ArrayList<String>(Arrays.asList(colorSamokatBlack)));
        Response createOrder = order.creteOrder();
        createOrder.then().assertThat().body("track", notNullValue())
                .and()
                .statusCode(201);

        this.trackOrderForList = createOrder.jsonPath().getInt("track");
    }

    @Test
    @DisplayName("Список заказов")
    @Description("Проверка, что в тело ответа возвращается список заказов.")
    public void checkOrderList() {
        Orders orders = new Orders();
        Response getOrderList = orders.getOrders();
        getOrderList.then().assertThat().body("orders", notNullValue())
                .and()
                .statusCode(200);
    }

    @After
    public void cancelOrder() {
        // Отправляем DELETE-запрос на отмену заказа
        try {
            given()
                    .header("Content-type", "application/json")
                    .put(CANCEL_ORDER + trackOrderForList);
        } catch (Exception e) {
            System.out.println("Такого заказа не существует - отмена невозможна.");
        }
    }
}
