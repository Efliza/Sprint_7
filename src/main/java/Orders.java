import io.restassured.response.Response;
import java.util.ArrayList;
import static constants.Handle.*;
import static io.restassured.RestAssured.given;

public class Orders {

    public ArrayList<String> orderList;
    public Orders() {
        this.orderList = orderList;
    }
    public ArrayList<String> getOrderList() {
        return orderList;
    }
    public void setOrderList(ArrayList<String> orderList) {
        this.orderList = orderList;
    }

    public Response getOrders() {
        return given().spec(BaseHttpClient.baseRequestSpec())
                .when()
                .get(GET_ORDER_LIST);
    }
}
