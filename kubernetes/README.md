# Introduction
This directory contains a help deployment to deploy Monitor Exporter runtime on 1 pods

# Deployment
Check the deployment file, and adapt it.

## Connection to ElasticSearch
The deployment is expected to run in the same cluster as Zeebe. 
If it is a different cluster,or using an external ElasticSearch or OpenSearch, then the Elasticsearch connection must be adapted.

```json
monitor:
  elasticsearch:
    host: localhost
    port: 9200
    protocol: http
```

# Operation

```shell
kubectl create -f camunda-8-monitor-exporters.yaml
```
Check the logs

Search the pods `camunda-8-monitor-exporters-*`

```shell
kubectl get pods
kubectl logs -f <podName>
```

```shell
kubectl delete -f camunda-8-monitor-exporters.yaml
```
