package avmf.model;

/**
 * BuyTicketStatus — toate stările posibile ale funcției buyTicketTestable()
 * Fiecare stare corespunde unui branch din funcție.
 */
public enum BuyTicketStatus {
    INVALID_NAME_INDEX,           // branch 1: nameIndex în afara [0,5]
    INVALID_AGE,                  // branch 2: age < 14 sau age > 60
    INVALID_NAME,                 // branch 3: numele nu trece regex-ul
    INVALID_BASE_PRICE,           // branch 4: basePrice < 200 sau > 400
    INVALID_DISCOUNT_FOR_UNDER25, // branch 5: under25 dar discount invalid
    INVALID_DISCOUNT_FOR_REGULAR, // branch 6: regular dar discount != 0
    UNDER25_SUCCESS,              // totul valid, age <= 25
    REGULAR_SUCCESS               // totul valid, age > 25
}