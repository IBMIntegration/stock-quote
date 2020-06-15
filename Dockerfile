#       Copyright 2019 IBM Corp All Rights Reserved
#
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.

FROM open-liberty:kernel


COPY src/main/liberty/config /config/
USER root
#RUN yum -y  install curl
RUN chmod -R 777 /config

#ENV API_CONNECT_PROXY_URL https://stock-trader-quote.us-south.cf.appdomain.cloud

RUN configure.sh

RUN find /opt/ol/wlp -type d -exec chmod 777 {} \;

RUN chown -R 1001 /opt/ol/wlp
USER 1001

COPY target/stock-quote.war /config/apps/StockQuote.war
