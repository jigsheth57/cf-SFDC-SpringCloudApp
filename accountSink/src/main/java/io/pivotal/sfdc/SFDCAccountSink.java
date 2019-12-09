package io.pivotal.sfdc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.pivotal.sfdc.domain.Account;
import io.pivotal.sfdc.domain.AccountList;
import io.pivotal.sfdc.sink.SFDCAccountNotificationSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import java.io.StringWriter;
import java.util.List;

@EnableBinding(SFDCAccountNotificationSink.class)
public class SFDCAccountSink {
    @Autowired
    SFDCAccountNotificationSink sinker;

    private static final Logger LOGGER = LoggerFactory.getLogger(SFDCAccountSink.class);

    @Autowired
    private StatefulRedisConnection<String, String> redisConnection;

    private RedisCommands<String, String> redisCommands;

    final ObjectMapper mapper = new ObjectMapper();

    @StreamListener(SFDCAccountNotificationSink.INPUT)
    public void updateCachedAccounts(AccountList accountList) throws Exception {
        LOGGER.info("updating cache for accounts: {}", accountList);
        List<Account> accounts = accountList.getAccounts();
        for (Account account : accounts) {
            store(account.getId(),account);
        }
    }

    /**
     * Stores serialized domain object in redis
     *
     * @param key
     * @param obj
     * @return String
     * @throws Exception
     */
    private String store(String key, Object obj) throws Exception {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        //writing to console, can write to any output stream such as file
        StringWriter jsonData = new StringWriter();
        mapper.writeValue(jsonData, obj);
        String jsonDataStr = jsonData.toString();
        LOGGER.debug("key: {}, value: {}",key,jsonDataStr);
        this.redisCommands = redisConnection.sync();
        redisCommands.set(key, jsonDataStr);

        return jsonDataStr;
    }

}
