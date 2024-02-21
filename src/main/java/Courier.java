import io.restassured.response.Response;
import static constants.Handle.COURIER_LOGIN;
import static constants.Handle.CREATE_COURIER;
import static io.restassured.RestAssured.given;

public class Courier {
    // ключ login стал полем типа String
    private String login;
    private String password;
    private String firstName;

    // конструктор со всеми параметрами
    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    // конструктор без параметров
    public Courier() {
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Response createCourier() {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .and()
                .body(new Courier(login, password, firstName))
                .when()
                .post(CREATE_COURIER);
    }

    public Response courierAuthorization() {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .and()
                .body(new Courier(login, password, firstName))
                .when()
                .post(COURIER_LOGIN);
    }
}
