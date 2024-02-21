import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName; // импорт DisplayName
import io.qameta.allure.Description; // импорт Description
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static constants.Url.*;
import static constants.Handle.*;

public class CreateСourierTest {
    String login = "luna";
    String password = "1234";
    String idCourier;

    @Before
    public void setUp() {
        RestAssured.baseURI = URL_SAMOKAT;
    }

    @Test
    @DisplayName("Создание курьера") // имя теста
    @Description("Создание курьера со всеми заполненными необходимими полями") // описание теста
    public void createCourier() {
        Courier courier = new Courier(login, password,null);
        courier.createCourier()
                .then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);

        //Проверяем по логину, что курьер с такими данными создан
        Response responseCourierId = courier.courierAuthorization();
        responseCourierId.then().assertThat().body("id", notNullValue())
                .and()
                .statusCode(200);

        this.idCourier = responseCourierId.jsonPath().getString("id");
    }

    @Test
    @DisplayName("Создание курьера дубликата")
    @Description("Создание дубликата курьера со всеми заполненными необходимими полями")
    public void createDuplicateCourier() {
        Courier courier = new Courier(login, password, null);
        courier.createCourier()
                .then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);

        //Проверяем по логину, что курьер с такими данными создан
        Response responseCourierId = courier.courierAuthorization();
        responseCourierId.then().assertThat().body("id", notNullValue())
                .and()
                .statusCode(200);

        this.idCourier = responseCourierId.jsonPath().getString("id");

        //Создаем еще одного курьера с такими данными
        courier.createCourier()
                .then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and()
                .statusCode(409);
    }

    @Test
    @DisplayName("Создание курьера без логина")
    @Description("Создание курьера без заполненного обязательного поля - логин (null)")
    public void createCourierWithoutLogin() {
        Courier courier = new Courier(null, password,null);
        courier.createCourier()
                .then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Description("Создание курьера без заполненного обязательного поля - пароль (null)")
    public void createCourierWithoutPassword() {
        Courier courier = new Courier(login, null,null);
        courier.createCourier()
                .then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

    @After
    public void courierDeletion() {
        // Отправляем DELETE-запрос на удаление курьера
        try {
            given()
                    .header("Content-type", "application/json")
                    .delete(DELETE_COURIER + idCourier);
        } catch (Exception e) {
            System.out.println("Такого курьера не существует - удаление невозможно.");
        }
    }
}
