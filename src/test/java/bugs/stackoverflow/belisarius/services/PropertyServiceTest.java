package bugs.stackoverflow.belisarius.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bugs.stackoverflow.belisarius.services.PropertyService;

import org.junit.jupiter.api.Test;

public class PropertyServiceTest {
    private final PropertyService propertyService = new PropertyService();

    @Test
    public void getPropertyTest() {
        assertEquals("email@gmail.com", propertyService.getProperty("email"));
        assertEquals("suPerSTrOngPASswORd", propertyService.getProperty("password"));
        assertEquals("111347", propertyService.getProperty("roomid"));
        assertEquals("stackoverflow", propertyService.getProperty("site"));
        assertEquals("U4DMV*8nvpm3EOpvf69Rxw((", propertyService.getProperty("apikey"));
        assertEquals("true/false", propertyService.getProperty("useHiggs"));
        assertEquals("https://api.higgs.sobotics.org", propertyService.getProperty("higgsurl"));
        assertEquals("3", propertyService.getProperty("higgsBotId"));
        assertEquals("abc123DEF456", propertyService.getProperty("higgsSecret"));
        assertEquals("false", propertyService.getProperty("outputMessage"));
    }
}