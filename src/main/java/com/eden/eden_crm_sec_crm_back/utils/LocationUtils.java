package com.eden.eden_crm_sec_crm_back.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class LocationUtils {
    private static final BigDecimal EARTH_RADIUS = new BigDecimal("6371000"); // Earth's radius in meters
    private static final MathContext MATH_CONTEXT = new MathContext(16, RoundingMode.HALF_UP); // Sufficient precision for GPS coordinates

    /**
     * Checks if user's location is within the allowed tolerance distance.
     *
     * @param userLat User's latitude
     * @param userLng User's longitude
     * @param siteLat Operation site's latitude
     * @param siteLng Operation site's longitude
     * @param tolerance Allowed distance in meters
     * @return true if within tolerance, false otherwise
     */
    public static boolean isWithinTolerance(
            BigDecimal userLat, BigDecimal userLng, BigDecimal siteLat, BigDecimal siteLng, BigDecimal tolerance
    ) {
        // Convert degrees to radians
        BigDecimal latDistance = toRadians(siteLat.subtract(userLat, MATH_CONTEXT));
        BigDecimal lngDistance = toRadians(siteLng.subtract(userLng, MATH_CONTEXT));

        // Calculate haversine formula components
        BigDecimal sinLatHalf = sin(latDistance.divide(BigDecimal.valueOf(2), MATH_CONTEXT));
        BigDecimal sinLngHalf = sin(lngDistance.divide(BigDecimal.valueOf(2), MATH_CONTEXT));
        
        BigDecimal a = sinLatHalf.pow(2, MATH_CONTEXT).add(
            cos(toRadians(userLat)).multiply(
                cos(toRadians(siteLat)), MATH_CONTEXT
            ).multiply(
                sinLngHalf.pow(2, MATH_CONTEXT), MATH_CONTEXT
            ), 
            MATH_CONTEXT
        );

        BigDecimal c = BigDecimal.valueOf(2).multiply(
            atan2(sqrt(a), sqrt(BigDecimal.ONE.subtract(a, MATH_CONTEXT))),
            MATH_CONTEXT
        );
        
        BigDecimal distance = EARTH_RADIUS.multiply(c, MATH_CONTEXT); // Distance in meters

        return distance.compareTo(tolerance) <= 0;
    }
    
    // Helper methods for BigDecimal trigonometric functions
    private static BigDecimal toRadians(BigDecimal degrees) {
        return degrees.multiply(BigDecimal.valueOf(Math.PI), MATH_CONTEXT)
                     .divide(BigDecimal.valueOf(180), MATH_CONTEXT);
    }
    
    private static BigDecimal sin(BigDecimal x) {
        return new BigDecimal(
            Math.sin(x.doubleValue()), 
            MATH_CONTEXT
        );
    }
    
    private static BigDecimal cos(BigDecimal x) {
        return new BigDecimal(
            Math.cos(x.doubleValue()),
            MATH_CONTEXT
        );
    }
    
    private static BigDecimal atan2(BigDecimal y, BigDecimal x) {
        return new BigDecimal(
            Math.atan2(y.doubleValue(), x.doubleValue()),
            MATH_CONTEXT
        );
    }
    
    private static BigDecimal sqrt(BigDecimal value) {
        return value.sqrt(MATH_CONTEXT);
    }
}
