{{/* Generate basic labels */}}
{{- define "sfdcapps.labels" }}
generator: helm
demo: sfdcapps
date: {{ now | htmlDate }}
chart: {{ .Chart.Name }}
version: {{ .Chart.Version }}
{{- end }}
{{- define "rabbitmq.healthProbes" }}
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.rabbitmq.server.port }}
  initialDelaySeconds: 60
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.rabbitmq.server.port }}
  initialDelaySeconds: 90
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 15
{{- end }}
{{- define "redis.healthProbes" }}
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.redis.port }}
  initialDelaySeconds: 60
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.redis.port }}
  initialDelaySeconds: 90
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 15
{{- end }}
{{- define "configserver.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.configserver.port }}
    scheme: HTTP
  initialDelaySeconds: 60
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.configserver.port }}
  initialDelaySeconds: 90
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 15
{{- end }}
{{- define "discovery.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.discovery.port }}
    scheme: HTTP
  initialDelaySeconds: 60
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.discovery.port }}
  initialDelaySeconds: 90
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 15
{{- end }}
{{- define "hystrixdashboard.healthProbes" }}
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.hystrixdashboard.port }}
  initialDelaySeconds: 60
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.hystrixdashboard.port }}
  initialDelaySeconds: 90
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 15
{{- end }}
{{- define "turbine.healthProbes" }}
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.turbine.port }}
  initialDelaySeconds: 60
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.turbine.port }}
  initialDelaySeconds: 90
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 15
{{- end }}
{{- define "authservice.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.authservice.port }}
    scheme: HTTP
  initialDelaySeconds: 60
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.authservice.port }}
  initialDelaySeconds: 90
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 15
{{- end }}
{{- define "accountservice.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.accountservice.port }}
    scheme: HTTP
  initialDelaySeconds: 60
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.accountservice.port }}
  initialDelaySeconds: 90
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 15
{{- end }}
{{- define "contactservice.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.contactservice.port }}
    scheme: HTTP
  initialDelaySeconds: 60
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.contactservice.port }}
  initialDelaySeconds: 90
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 15
{{- end }}
{{- define "opportunityservice.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.opportunityservice.port }}
    scheme: HTTP
  initialDelaySeconds: 60
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.opportunityservice.port }}
  initialDelaySeconds: 90
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 15
{{- end }}
{{- define "gatewayservice.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.gatewayservice.port }}
    scheme: HTTP
  initialDelaySeconds: 60
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.gatewayservice.port }}
  initialDelaySeconds: 90
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 15
{{- end }}
{{- define "portal.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.portal.port }}
    scheme: HTTP
  initialDelaySeconds: 60
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.portal.port }}
  initialDelaySeconds: 90
  failureThreshold: 5
  successThreshold: 1
  timeoutSeconds: 1
  periodSeconds: 15
{{- end }}
