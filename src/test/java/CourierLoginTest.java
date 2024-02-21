import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static constants.Handle.*;
import static constants.Url.URL_SAMOKAT;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {
    String login = "luna";
    String password = "1234";
    String idCourier;
    String incorrectLogin = "aaaa";
    String incorrectPassword = "1111";
    String withoutPassword = "";

    @Before
    public void setUp() {
        RestAssured.baseURI = URL_SAMOKAT;
    }

    @Test
    @DisplayName("Проверка авторизации") // имя теста
    @Description("Курьер может авторизоваться при заполнении обязательных полей (логин,пароль)") // описание теста
    public void checkAuthorization() {
        Courier courier = new Courier(login, password,null);
        courier.createCourier();

        Courier courierForAuthorization = new Courier(login, password, null);
        //Проверяем по логину, что курьер с такими данными создан
        Response responseCourierId = courierForAuthorization.courierAuthorization();
        responseCourierId.then().assertThat().body("id", notNullValue())
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Проверка авторизации без логина")
    @Description("Курьер не может авторизоваться при не заполнении обязательных полей (логин)")
    public void checkAuthorizationWithoutLogin() {
        Courier courier = new Courier(login, password,null);
        courier.createCourier();

        Courier courierForAuthorization = new Courier(null, password, null);
        Response responseCourierId = courierForAuthorization.courierAuthorization();
        responseCourierId.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Проверка авторизации без пароля")
    @Description("Курьер не может авторизоваться при не заполнении обязательных полей (пароль)")
    public void checkAuthorizationWithoutPassword() {
        Courier courier = new Courier(login, password,null);
        courier.createCourier();

        Courier courierForAuthorization = new Courier(login, withoutPassword, null);
        Response responseCourierId = courierForAuthorization.courierAuthorization();
        responseCourierId.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(400);
    }

    @Test
    @DisplayName("Проверка авторизации c неверным логином (несуществующий пользователь)")
    @Description("Курьер не может авторизоваться при вводе неверного логина")
    public void checkAuthorizationWithWrongLogin() {
        Courier courier = new Courier(login, password,null);
        courier.createCourier();

        Courier courierForAuthorization = new Courier(incorrectLogin, password, null);
        Response responseCourierId = courierForAuthorization.courierAuthorization();
        responseCourierId.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

    @Test
    @DisplayName("Проверка авторизации c неверным паролем (несуществующий пользователь)")
    @Description("Курьер не может авторизоваться при вводе неверного пароля")
    public void checkAuthorizationWithWrongPassword() {
        Courier courier = new Courier(login, password,null);
        courier.createCourier();

        Courier courierForAuthorization = new Courier(login, incorrectPassword, null);
        Response responseCourierId = courierForAuthorization.courierAuthorization();
        responseCourierId.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(404);
    }

    @After
    public void courierDeletion() {
        Courier courierForAuthorization = new Courier(login, password, null);
        Response responseCourierId = courierForAuthorization.courierAuthorization();
        this.idCourier = responseCourierId.jsonPath().getString("id");

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
