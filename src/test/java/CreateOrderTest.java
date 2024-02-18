import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName; // импорт DisplayName
import io.qameta.allure.Description; // импорт Description
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static constants.Handle.*;
import static constants.Url.URL_SAMOKAT;

@RunWith(Parameterized.class) // указали, что тесты будет запускать раннер Parameterized
public class CreateOrderTest {
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private String rentTime;
    private String deliveryDate;
    private String comment;
    private ArrayList<String> color;

    int trackOrder;

    public CreateOrderTest(String firstName, String lastName, String address, String metroStation, String phone, String rentTime, String deliveryDate, String comment, ArrayList color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    static String colorSamokatBlack = "BLACK";
    static String colorSamokatGray = "GRAY";
    static String colorSamokatWithoutColor = "";

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Настя", "Иванова", "Усачева 3", "4", "79110930605", "5", "2024-02-27", "Заранее спасибо", new ArrayList<String>(Arrays.asList(colorSamokatBlack, colorSamokatGray))},
                {"Настя", "Иванова", "Усачева 3", "4", "79110930605", "5", "2024-02-27", "Заранее спасибо", new ArrayList<String>(Arrays.asList(colorSamokatBlack))},
                {"Настя", "Иванова", "Усачева 3", "4", "79110930605", "5", "2024-02-27", "Заранее спасибо", new ArrayList<String>(Arrays.asList(colorSamokatGray))},
                {"Настя", "Иванова", "Усачева 3", "4", "79110930605", "5", "2024-02-27", "Заранее спасибо", new ArrayList<String>(Arrays.asList(colorSamokatWithoutColor))},
        });
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = URL_SAMOKAT;
    }

    @Test
    @DisplayName("Создание заказа") // имя теста
    @Description("Создание заказа с разными самокатами (применение параметризации)") // описание теста
    public void createOrder() {
        Order order = new Order(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        Response createOrder = order.creteOrder();
        createOrder.then().assertThat().body("track", notNullValue())
                .and()
                .statusCode(201);

        this.trackOrder = createOrder.jsonPath().getInt("track");

        Response getOrder = given().spec(BaseHttpClient.baseRequestSpec())
                .get(GET_ORDER + trackOrder);
        getOrder.then().assertThat().body("order", notNullValue())
                .and()
                .statusCode(200);
    }

    @After
    public void cancelOrder() {
        // Отправляем DELETE-запрос на отмену заказа
        try {
            given()
                    .header("Content-type", "application/json")
                    .put(CANCEL_ORDER + trackOrder);
        } catch (Exception e) {
            System.out.println("Такого заказа не существует - отмена невозможна.");
        }
    }
}
