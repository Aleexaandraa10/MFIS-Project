package avmf.model;

/**
 * SwapOrganizersResult — obiectul returnat de swapOrganizersTestable()
 * Conține statusul obținut și detaliile swap-ului (dacă inputul e valid).
 */
public class SwapOrganizersResult {

    private final SwapOrganizersStatus status;
    private final String org1Name;
    private final String org2Name;
    private final String event1Name;
    private final String event2Name;

    public SwapOrganizersResult(SwapOrganizersStatus status,
                                String org1Name, String org2Name,
                                String event1Name, String event2Name) {
        this.status     = status;
        this.org1Name   = org1Name;
        this.org2Name   = org2Name;
        this.event1Name = event1Name;
        this.event2Name = event2Name;
    }

    public SwapOrganizersStatus getStatus() { return status; }
    public String getOrg1Name()             { return org1Name; }
    public String getOrg2Name()             { return org2Name; }
    public String getEvent1Name()           { return event1Name; }
    public String getEvent2Name()           { return event2Name; }

    @Override
    public String toString() {
        return "SwapOrganizersResult{" +
                "status="     + status     +
                ", org1='"    + org1Name   + '\'' +
                ", org2='"    + org2Name   + '\'' +
                ", event1='"  + event1Name + '\'' +
                ", event2='"  + event2Name + '\'' +
                '}';
    }
}