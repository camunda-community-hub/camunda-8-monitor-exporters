# Introduction
This directory contains a help deployment to deploy Monitor Exporter runtime on 1 pods

# Deployment
Check the deployment file, and adapt it.

## Connection to ElasticSearch
The deployment must be run in the same cluster as Zeebe. If it is a different cluster, then the zeebe connection must be adapted

# Operation

```shell
kubectl create -f camunda-8-monitor-exporters.yaml
```
Check the logs

```shell
kubectl get pods
kubectl logs -f 
```

```shell
kubectl delete -f camunda-8-monitor-exporters.yaml
```
