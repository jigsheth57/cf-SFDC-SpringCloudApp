FROM ubuntu
VOLUME /tmp
ADD testpcf.sh /tmp/testpcf.sh
ENV SFDC_API_GATEWAY_BASEURL http://sfdcapigateway:9011
RUN apt-get update && apt-get install -y curl
RUN chmod 755 /tmp/testpcf.sh

ENTRYPOINT ["/bin/bash", "-c", "/tmp/testpcf.sh $SFDC_API_GATEWAY_BASEURL"]
