package Restaurant;

import Utils.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantFilterTest {

    private RestaurantRepo mockRepo;
    private RestaurantFilter filter;
    private Restaurant openRestaurant;
    private Restaurant closedRestaurant;

    @BeforeEach
    void setUp() throws Exception {
        // --- Création de deux restaurants ---
        openRestaurant = mock(Restaurant.class);
        when(openRestaurant.getEstablishmentType()).thenReturn(establishmentType.RESTAURANT);
        when(openRestaurant.isOpenNow()).thenReturn(true);
        when(openRestaurant.isOpenAt(any())).thenReturn(true);

        closedRestaurant = mock(Restaurant.class);
        when(closedRestaurant.getEstablishmentType()).thenReturn(establishmentType.CAFE);
        when(closedRestaurant.isOpenNow()).thenReturn(false);
        when(closedRestaurant.isOpenAt(any())).thenReturn(false);

        // --- Mock du repo ---
        mockRepo = mock(RestaurantRepo.class);
        when(mockRepo.getRestaurants()).thenReturn(List.of(openRestaurant, closedRestaurant));

        filter = new RestaurantFilter(mockRepo);
    }

    @Test
    void filterByType() {
        List<Restaurant> result = filter.filterByType(establishmentType.RESTAURANT);
        assertEquals(1, result.size());
        assertTrue(result.contains(openRestaurant));
    }

    @Test
    void filterByAvailibility() {
        List<Restaurant> result = filter.filterByAvailibility();
        assertEquals(1, result.size());
        assertTrue(result.contains(openRestaurant));
        assertFalse(result.contains(closedRestaurant));
    }

    @Test
    void testFilterByAvailibilityAtSpecificTime() throws Exception {
        Date targetTime = new Date.Builder()
                .day(5).month(10).year(2025)
                .hour(12).minute(0)
                .build();

        List<Restaurant> result = filter.filterByAvailibility(targetTime);
        assertEquals(1, result.size());
        assertTrue(result.contains(openRestaurant));
    }
}
