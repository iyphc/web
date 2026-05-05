package ru.msu.cmc.webprac.system;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class WebSystemTest {

    private String baseUrl;

    @BeforeClass
    public void setUp() throws Exception {
        HttpUnitOptions.setExceptionsThrownOnErrorStatus(false);
        HttpUnitOptions.setScriptingEnabled(false);
        baseUrl = System.getProperty("system.baseUrl");
        if (baseUrl == null || baseUrl.trim().isEmpty() || baseUrl.contains("${")) {
            baseUrl = "http://localhost:8080/car-dealership";
        }
        baseUrl = baseUrl.replaceAll("/+$", "");
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "/api/dashboard").openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            if (connection.getResponseCode() >= 500) {
                throw new SkipException("Web-приложение недоступно по адресу " + baseUrl);
            }
        } catch (Exception ex) {
            throw new SkipException("Web-приложение недоступно по адресу " + baseUrl, ex);
        }
    }

    @Test
    public void dashboardPageAndApiAreAvailable() throws Exception {
        WebConversation wc = new WebConversation();

        WebResponse page = wc.getResponse(new GetMethodWebRequest(baseUrl + "/"));
        assertEquals(page.getResponseCode(), 200);
        assertTrue(page.getText().contains("root"));
        assertTrue(page.getText().contains("app.js"));

        WebResponse api = wc.getResponse(new GetMethodWebRequest(baseUrl + "/api/dashboard"));
        assertEquals(api.getResponseCode(), 200);
        assertTrue(api.getText().contains("\"cars\""));
        assertTrue(api.getText().contains("\"orderStatuses\""));
    }

    @Test
    public void carSearchReturnsSeededVehicle() throws Exception {
        WebConversation wc = new WebConversation();

        WebResponse response = wc.getResponse(new GetMethodWebRequest(baseUrl + "/api/cars?registrationNumber=A123"));

        assertEquals(response.getResponseCode(), 200);
        assertTrue(response.getText().contains("A123BC77"));
        assertTrue(response.getText().contains("Toyota"));
    }

    @Test
    public void clientCreationValidatesRequiredFields() throws Exception {
        WebConversation wc = new WebConversation();
        PostMethodWebRequest request = new PostMethodWebRequest(baseUrl + "/api/clients");
        request.setParameter("fullName", "System Test Client");

        WebResponse response = wc.getResponse(request);

        assertEquals(response.getResponseCode(), 400);
        assertTrue(response.getText().contains("Телефон"));
    }

    @Test
    public void clientCanBeCreatedAndFound() throws Exception {
        WebConversation wc = new WebConversation();
        String suffix = String.valueOf(System.currentTimeMillis());
        PostMethodWebRequest request = new PostMethodWebRequest(baseUrl + "/api/clients");
        request.setParameter("fullName", "System Test Client " + suffix);
        request.setParameter("phone", "+7999" + suffix.substring(Math.max(0, suffix.length() - 7)));
        request.setParameter("email", "system-" + suffix + "@example.com");
        request.setParameter("address", "Moscow");

        WebResponse createResponse = wc.getResponse(request);
        assertEquals(createResponse.getResponseCode(), 201);
        assertTrue(createResponse.getText().contains("Клиент добавлен"));

        WebResponse searchResponse = wc.getResponse(
                new GetMethodWebRequest(baseUrl + "/api/clients?q=" + suffix));
        assertEquals(searchResponse.getResponseCode(), 200);
        assertTrue(searchResponse.getText().contains("System Test Client " + suffix));
    }

    @Test
    public void orderCreationRequiresClient() throws Exception {
        WebConversation wc = new WebConversation();
        PostMethodWebRequest request = new PostMethodWebRequest(baseUrl + "/api/orders");
        request.setParameter("needTestDrive", "true");

        WebResponse response = wc.getResponse(request);

        assertEquals(response.getResponseCode(), 400);
        assertTrue(response.getText().contains("message"));
    }

    @Test
    public void brandCanBeCreated() throws Exception {
        WebConversation wc = new WebConversation();
        String suffix = String.valueOf(System.currentTimeMillis());
        PostMethodWebRequest request = new PostMethodWebRequest(baseUrl + "/api/brands");
        request.setParameter("brandName", "SystemBrand" + suffix);
        request.setParameter("manufacturerName", "System Manufacturer");

        WebResponse response = wc.getResponse(request);

        assertEquals(response.getResponseCode(), 201);
        assertTrue(response.getText().contains("Марка добавлена"));
        assertTrue(response.getText().contains("SystemBrand" + suffix));
    }

    @Test
    public void orderStatusCanBeChanged() throws Exception {
        WebConversation wc = new WebConversation();
        PostMethodWebRequest request = new PostMethodWebRequest(baseUrl + "/api/orders/1/status");
        request.setParameter("status", "IN_SHOWROOM");

        WebResponse response = wc.getResponse(request);

        assertEquals(response.getResponseCode(), 200);
        assertTrue(response.getText().contains("Статус заказа обновлен"));
        assertTrue(response.getText().contains("IN_SHOWROOM"));
    }

    @Test
    public void testDriveCreationValidatesClientAndCar() throws Exception {
        WebConversation wc = new WebConversation();
        PostMethodWebRequest request = new PostMethodWebRequest(baseUrl + "/api/test-drives");

        WebResponse response = wc.getResponse(request);

        assertEquals(response.getResponseCode(), 400);
        assertTrue(response.getText().contains("message"));
    }

    @Test
    public void testDriveCanBeCreated() throws Exception {
        WebConversation wc = new WebConversation();
        PostMethodWebRequest request = new PostMethodWebRequest(baseUrl + "/api/test-drives");
        request.setParameter("clientId", "1");
        request.setParameter("carId", "1");
        request.setParameter("testDriveAt", LocalDateTime.now().withNano(0).plusDays(10).toString());
        request.setParameter("notes", "Created by system test");

        WebResponse response = wc.getResponse(request);

        assertEquals(response.getResponseCode(), 201);
        assertTrue(response.getText().contains("Тест-драйв зафиксирован"));
        assertTrue(response.getText().contains("Created by system test"));
    }
}
