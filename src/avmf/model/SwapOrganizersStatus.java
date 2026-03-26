package avmf.model;

/**
 * SwapOrganizersStatus — toate stările posibile ale funcției swapOrganizersTestable()
 * Fiecare stare corespunde unui branch din funcție.
 */
public enum SwapOrganizersStatus {
    INVALID_ORG1_INDEX,    // branch 1: org1Index în afara [0,2]
    INVALID_ORG2_INDEX,    // branch 2: org2Index în afara [0,2]
    SAME_ORGANIZER,        // branch 3: org1Index == org2Index
    INVALID_EVENT1_INDEX,  // branch 4: ev1Index invalid pentru org1
    INVALID_EVENT2_INDEX,  // branch 5: ev2Index invalid pentru org2
    SWAP_SUCCESS           // totul valid, swap simulat
}