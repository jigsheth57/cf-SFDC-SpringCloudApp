{{/* Generate basic labels */}}
{{- define "sfdcapps.labels" }}
generator: helm
demo: sfdcapps
date: {{ now | htmlDate }}
chart: {{ .Chart.Name }}
version: {{ .Chart.Version }}
{{- end }}
{{- define "rabbitmq.healthProbes" }}
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.rabbitmq.server.port }}
  failureThreshold: 1
  periodSeconds: 10
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.rabbitmq.server.port }}
  failureThreshold: 1
  periodSeconds: 10
{{- end }}
{{- define "redis.healthProbes" }}
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.redis.port }}
  failureThreshold: 1
  periodSeconds: 10
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.redis.port }}
  failureThreshold: 1
  periodSeconds: 10
{{- end }}
{{- define "configserver.healthProbes" }}
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.configserver.port }}
  failureThreshold: 1
  periodSeconds: 10
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.configserver.port }}
  failureThreshold: 1
  periodSeconds: 10
{{- end }}
{{- define "authservice.healthProbes" }}
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.authservice.port }}
  failureThreshold: 1
  periodSeconds: 10
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.authservice.port }}
  failureThreshold: 1
  periodSeconds: 10
{{- end }}
{{- define "accountservice.healthProbes" }}
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.accountservice.port }}
  failureThreshold: 1
  periodSeconds: 10
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.accountservice.port }}
  failureThreshold: 1
  periodSeconds: 10
{{- end }}
{{- define "contactservice.healthProbes" }}
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.contactservice.port }}
  failureThreshold: 1
  periodSeconds: 10
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.contactservice.port }}
  failureThreshold: 1
  periodSeconds: 10
{{- end }}
{{- define "opportunityservice.healthProbes" }}
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.opportunityservice.port }}
  failureThreshold: 1
  periodSeconds: 10
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.opportunityservice.port }}
  failureThreshold: 1
  periodSeconds: 10
{{- end }}
{{- define "portal.healthProbes" }}
livenessProbe:
  tcpSocket:
    port: {{ .Values.global.portal.port }}
  failureThreshold: 1
  periodSeconds: 10
readinessProbe:
  tcpSocket:
    port: {{ .Values.global.portal.port }}
  failureThreshold: 1
  periodSeconds: 10
{{- end }}
