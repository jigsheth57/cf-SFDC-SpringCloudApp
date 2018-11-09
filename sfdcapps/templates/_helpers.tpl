{{/* Generate basic labels */}}
{{- define "sfdcapps.labels" }}
generator: helm
date: {{ now | htmlDate }}
chart: {{ .Chart.Name }}
version: {{ .Chart.Version }}
{{- end }}
{{- define "rabbitmq.healthProbes" }}
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.rabbitmq.port }}
  initialDelaySeconds: 60
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.rabbitmq.port }}
  initialDelaySeconds: 90
  periodSeconds: 15
{{- end }}
{{- define "redis.healthProbes" }}
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.redis.port }}
  initialDelaySeconds: 60
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.redis.port }}
  initialDelaySeconds: 90
  periodSeconds: 15
{{- end }}
{{- define "configserver.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.configserver.port }}
  initialDelaySeconds: 60
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.configserver.port }}
  initialDelaySeconds: 90
  periodSeconds: 15
{{- end }}
{{- define "discovery.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.discovery.port }}
  initialDelaySeconds: 60
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.discovery.port }}
  initialDelaySeconds: 90
  periodSeconds: 15
{{- end }}
{{- define "hystrixdashboard.healthProbes" }}
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.hystrixdashboard.port }}
  initialDelaySeconds: 90
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.hystrixdashboard.port }}
  initialDelaySeconds: 120
  periodSeconds: 15
{{- end }}
{{- define "turbine.healthProbes" }}
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.turbine.port }}
  initialDelaySeconds: 60
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.turbine.port }}
  initialDelaySeconds: 90
  periodSeconds: 15
{{- end }}
{{- define "authservice.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.authservice.port }}
  initialDelaySeconds: 60
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.authservice.port }}
  initialDelaySeconds: 90
  periodSeconds: 15
{{- end }}
{{- define "accountservice.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.accountservice.port }}
  initialDelaySeconds: 60
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.accountservice.port }}
  initialDelaySeconds: 90
  periodSeconds: 15
{{- end }}
{{- define "contactservice.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.contactservice.port }}
  initialDelaySeconds: 60
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.contactservice.port }}
  initialDelaySeconds: 90
  periodSeconds: 15
{{- end }}
{{- define "opportunityservice.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.opportunityservice.port }}
  initialDelaySeconds: 60
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.opportunityservice.port }}
  initialDelaySeconds: 90
  periodSeconds: 15
{{- end }}
{{- define "gatewayservice.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.gatewayservice.port }}
  initialDelaySeconds: 60
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.gatewayservice.port }}
  initialDelaySeconds: 90
  periodSeconds: 15
{{- end }}
{{- define "portalservice.healthProbes" }}
readinessProbe:
  httpGet:
    path: /manage/health
    port: {{ .Values.global.portalservice.port }}
  initialDelaySeconds: 60
  periodSeconds: 10
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.portalservice.port }}
  initialDelaySeconds: 90
  periodSeconds: 15
{{- end }}
