package io.pivotal.sfdc;

public interface SFDC_Constant {

    public static String ACCESS_TOKEN = "access_token";
    public static String INSTANCE_URL = "instance_url";

    public static String ACCOUNT_CONTACT_LIST = "accounts";
    public static String ACCOUNT_OPPORTUNITY_LIST = "opp_by_accts";

    public static String STATUS_URL = "https://api.status.salesforce.com/v1/instances/";

    public static final String CIRCUIT_BREAKER_DEFAULTS = "cacheBackend";
}