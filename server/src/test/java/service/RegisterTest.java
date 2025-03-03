//package service;
//
//import dataaccess.DataAccess;
//import dataaccess.MemoryDataAccess;
//import model.RegisterRequest;
//import model.RegisterResponse;
//import model.UserData;
//import org.junit.jupiter.api.BeforeEach;
//import service.UserService;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static service.UserService.dataAccess;
//
//public class RegisterTest {
//
//
//
//    @Test
//    @BeforeEach
//    void setUp() {
//        dataAccess.Users.clear();
//    }
//
//    void RegisterPositiveTest() {
//        RegisterRequest req = new RegisterRequest("username","password","email");
//        RegisterResponse response = UserService.register(req);
//
//        assertNotNull(response, "Response should not be null");
//        assertTrue(dataAccess.Users.containsKey("username"), "User should be registered");
//
//        UserData storedUser = dataAccess.Users.get("username");
//        assertEquals("username", storedUser.username());
//        assertEquals("password", storedUser.password());
//        assertEquals("email", storedUser.email());
//
//    }
//
//    @Test
//    void RegisterNegativeTest() {
//        DataAccess dataAccess = new MemoryDataAccess();
//    }
//}
