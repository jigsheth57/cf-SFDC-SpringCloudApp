package io.pivotal.sfdc.zuul.filters.post;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.context.RequestContext;

import io.pivotal.sfdc.domain.Account;
import io.pivotal.sfdc.domain.AccountList;

/**
 * This class implements error filter. When error occurs during routing of the HTTP Request to micro-service, 
 * this error filter is invoked before responding to user's request. This error filter will attempt to retrieve
 * response from the redis, if available.
 * 
 * @author Jignesh Sheth
 *
 */

public class FallBackFilter extends SendErrorFilter {

	
	private static final Logger logger = LoggerFactory.getLogger(FallBackFilter.class);

	private StringRedisTemplate redisTemplate;
    
	@Resource
    private JedisConnectionFactory redisConnFactory;

	@Value("${sfdc.service.unavailable}")
	private String unavailable;

	final ObjectMapper mapper = new ObjectMapper();
	
    @PostConstruct
    public void init() {
		this.redisTemplate = new StringRedisTemplate(redisConnFactory);
    	logger.debug("HostName: "+redisConnFactory.getHostName());
    	logger.debug("Port: "+redisConnFactory.getPort());
    	logger.debug("Password: "+redisConnFactory.getPassword());
    }

    @Override
	public Object run() {
		logger.debug("inside filter.run");
		
		try {
			RequestContext ctx = RequestContext.getCurrentContext();
			logger.debug("CurrentContext: "+ctx.toString());
			String uri = (String) ctx.get("requestURI");
			String method = ctx.getRequest().getMethod();
			if(method.equalsIgnoreCase("get")) {
				if(uri.equalsIgnoreCase("/accounts") || uri.equalsIgnoreCase("/opp_by_accts")) {
					List<Account> result = null;
					String jsonDataStr = null;
					try {
						result = ((AccountList)retrieve(uri, AccountList.class)).getAccounts();
						StringWriter jsonData = new StringWriter();
						mapper.writeValue(jsonData, result);
						jsonDataStr = jsonData.toString();
						writeResponse(jsonDataStr);
					} catch (Exception e) {
						logger.error(e.getMessage());
						writeResponse(unavailable);
					}
				} else if((uri.indexOf("/account/") != -1) || (uri.indexOf("/contact/") != -1) || (uri.indexOf("/opportunity/") != -1)) {
					try {
						String jsonDataStr = this.redisTemplate.opsForValue().get(uri.substring(uri.lastIndexOf('/')+1));
						if(jsonDataStr == null || jsonDataStr.isEmpty())
							jsonDataStr = unavailable;
						writeResponse(jsonDataStr);
					} catch (Exception e) {
						logger.error(e.getMessage());
						writeResponse(unavailable);
					}
				}
			} else {
				writeResponse(unavailable);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			try {
				writeResponse(unavailable);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return null;
	}

	private void writeResponse(String body) throws Exception {
		RequestContext context = RequestContext.getCurrentContext();

		HttpServletResponse servletResponse = context.getResponse();
		servletResponse.setContentType("application/json; charset=UTF-8");
		if(body == null || body.isEmpty() || body.equals(unavailable)) {
			logger.error("error: "+body);
			servletResponse.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		} else {
			servletResponse.setStatus(HttpServletResponse.SC_OK);
		}
			OutputStream outStream = servletResponse.getOutputStream();
			try {
					writeResponse(new ByteArrayInputStream(body.getBytes()), outStream);
					return;
			}
			finally {
				try {
					outStream.flush();
					outStream.close();
				}
				catch (IOException ex) {
				}
			}
	}

	private void writeResponse(InputStream zin, OutputStream out) throws Exception {
		byte[] bytes = new byte[1024];
		int bytesRead = -1;
		while ((bytesRead = zin.read(bytes)) != -1) {
			try {
				out.write(bytes, 0, bytesRead);
				out.flush();
			}
			catch (IOException ex) {
				// ignore
			}
			// doubles buffer size if previous read filled it
			if (bytesRead == bytes.length) {
				bytes = new byte[bytes.length * 2];
			}
		}
	}

	private Object retrieve(String key, Class classType) throws Exception {
			logger.debug("key: "+key);
			String jsonDataStr = this.redisTemplate.opsForValue().get(key);
	        logger.debug("value: "+jsonDataStr);
			Object obj = mapper.readValue(jsonDataStr, classType);
			
			return obj;
		}
}
