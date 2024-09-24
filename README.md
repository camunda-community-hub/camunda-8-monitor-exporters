# camunda-8-monitor-exporters
Monitor exporter (Zeebe Exporter, Operate Importer, TaskList importer, Optimize importer)


Zeebe export data to Elastic search.

Operate import the data.
Does Operate importer follow the throughput? Are late behind?
This project help to monitor this section.

it will take every X seconds a photography on different tools, and on different item (process instance, variable) and calculate the delay.
It will provide the tendency: does Operate retrieve the backlog? Or diverge?

# Run as a project
Run the command

# run as a POD in the cluster


# integrate as a library
To integrate the monitoring in a another tool, follow this instruction


# Create a Docker image for a new version

Run
```shell
mvn package

```

Manual operation
Now, create a docker image.
````
docker build -t pierre-yves-monnet/camunda-8-monitor-exporters:1.0.0 .
````


Push the image to the Camunda hub (you must be login first to the docker registry)

````
docker tag pierre-yves-monnet/camunda-8-monitor-exporters:1.0.0 ghcr.io/camunda-community-hub/camunda-8-monitor-exporters:1.0.0
docker push ghcr.io/camunda-community-hub/camunda-8-monitor-exporters:1.0.0
````


Tag as the latest:
````
docker tag pierre-yves-monnet/camunda-8-monitor-exporters:1.0.0 ghcr.io/camunda-community-hub/camunda-8-monitor-exporters:latest
docker push ghcr.io/camunda-community-hub/camunda-8-monitor-exporters:latest
````


The image is available here.

```
ghcr.io/camunda-community-hub/camunda-8-monitor-exporters:latest
````

# REST API Used

See https://confluence.camunda.com/pages/viewpage.action?pageId=207061205

## Zeebe
The Rest API collect

```shell
curl --location 'localhost:9200/zeebe-record*/_search?pretty=true' --header 'Content-Type: application/json' --data '{
  "size": 0,
  "aggs": {
    "value_types": {
      "terms": {
        "field": "valueType",
        "size": 20
      },
      "aggs": {
        "partitions": {
          "terms": {
            "field": "partitionId"
          },
          "aggs": {
            "min_sequence": {
              "min": {
                "field": "sequence"
              }
            },
}'
```


```json
{
  "took" : 1009,
  "timed_out" : false,
  "_shards" : {
    "total" : 20,
    "successful" : 20,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 10000,
      "relation" : "gte"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "value_types" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : "PROCESS_INSTANCE",
          "doc_count" : 6000477,
          "partitions" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : 2,
                "doc_count" : 2077331,
                "max_sequence" : {
                  "value" : 4.503599629447827E15
                },
                "min_sequence" : {
                  "value" : 4.503599627370497E15
                }
              },
              {
                "key" : 1,
                "doc_count" : 2036481,
                "max_sequence" : {
                  "value" : 2.251799815721729E15
                },
                "min_sequence" : {
                  "value" : 2.251799813685249E15
                }
              },
              {
                "key" : 3,
                "doc_count" : 1886665,
                "max_sequence" : {
                  "value" : 6.755399442942409E15
                },
                "min_sequence" : {
                  "value" : 6.755399441055745E15
                }
              }
            ]
          }
        },
        {
          "key" : "VARIABLE",
          "doc_count" : 2126476,
          "partitions" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : 1,
                "doc_count" : 1060055,
                "max_sequence" : {
                  "value" : 2.251799814745303E15
                },
                "min_sequence" : {
                  "value" : 2.251799813685249E15
                }
              },
              {
                "key" : 3,
                "doc_count" : 865758,
                "max_sequence" : {
                  "value" : 6.755399441921502E15
                },
                "min_sequence" : {
                  "value" : 6.755399441055745E15
                }
              },
              {
                "key" : 2,
                "doc_count" : 200663,
                "max_sequence" : {
                  "value" : 4.503599627571159E15
                },
                "min_sequence" : {
                  "value" : 4.503599627370497E15
                }
              }
            ]
          }
        },
        {
          "key" : "JOB",
          "doc_count" : 695117,
          "partitions" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : 2,
                "doc_count" : 357268,
                "max_sequence" : {
                  "value" : 4.503599627727764E15
                },
                "min_sequence" : {
                  "value" : 4.503599627370497E15
                }
              },
              {
                "key" : 3,
                "doc_count" : 169327,
                "max_sequence" : {
                  "value" : 6.755399441225071E15
                },
                "min_sequence" : {
                  "value" : 6.755399441055745E15
                }
              },
              {
                "key" : 1,
                "doc_count" : 168522,
                "max_sequence" : {
                  "value" : 2.25179981385377E15
                },
                "min_sequence" : {
                  "value" : 2.251799813685249E15
                }
              }
            ]
          }
        },
        {
          "key" : "PROCESS_INSTANCE_CREATION",
          "doc_count" : 42827,
          "partitions" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : 1,
                "doc_count" : 22408,
                "max_sequence" : {
                  "value" : 2.251799813707656E15
                },
                "min_sequence" : {
                  "value" : 2.251799813685249E15
                }
              },
              {
                "key" : 3,
                "doc_count" : 17693,
                "max_sequence" : {
                  "value" : 6.755399441073437E15
                },
                "min_sequence" : {
                  "value" : 6.755399441055745E15
                }
              },
              {
                "key" : 2,
                "doc_count" : 2726,
                "max_sequence" : {
                  "value" : 4.503599627373222E15
                },
                "min_sequence" : {
                  "value" : 4.503599627370497E15
                }
              }
            ]
          }
        },
        {
          "key" : "INCIDENT",
          "doc_count" : 12624,
          "partitions" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : 1,
                "doc_count" : 4276,
                "max_sequence" : {
                  "value" : 2.251799813689524E15
                },
                "min_sequence" : {
                  "value" : 2.251799813685249E15
                }
              },
              {
                "key" : 2,
                "doc_count" : 4182,
                "max_sequence" : {
                  "value" : 4.503599627374678E15
                },
                "min_sequence" : {
                  "value" : 4.503599627370497E15
                }
              },
              {
                "key" : 3,
                "doc_count" : 4166,
                "max_sequence" : {
                  "value" : 6.75539944105991E15
                },
                "min_sequence" : {
                  "value" : 6.755399441055745E15
                }
              }
            ]
          }
        },
        {
          "key" : "COMMAND_DISTRIBUTION",
          "doc_count" : 150,
          "partitions" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : 1,
                "doc_count" : 150,
                "max_sequence" : {
                  "value" : 2.251799813685398E15
                },
                "min_sequence" : {
                  "value" : 2.251799813685249E15
                }
              }
            ]
          }
        },
        {
          "key" : "DEPLOYMENT",
          "doc_count" : 75,
          "partitions" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : 1,
                "doc_count" : 25,
                "max_sequence" : {
                  "value" : 2.251799813685273E15
                },
                "min_sequence" : {
                  "value" : 2.251799813685249E15
                }
              },
              {
                "key" : 2,
                "doc_count" : 25,
                "max_sequence" : {
                  "value" : 4.503599627370521E15
                },
                "min_sequence" : {
                  "value" : 4.503599627370497E15
                }
              },
              {
                "key" : 3,
                "doc_count" : 25,
                "max_sequence" : {
                  "value" : 6.755399441055769E15
                },
                "min_sequence" : {
                  "value" : 6.755399441055745E15
                }
              }
            ]
          }
        },
        {
          "key" : "PROCESS",
          "doc_count" : 36,
          "partitions" : {
            "doc_count_error_upper_bound" : 0,
            "sum_other_doc_count" : 0,
            "buckets" : [
              {
                "key" : 1,
                "doc_count" : 12,
                "max_sequence" : {
                  "value" : 2.25179981368526E15
                },
                "min_sequence" : {
                  "value" : 2.251799813685249E15
                }
              },
              {
                "key" : 2,
                "doc_count" : 12,
                "max_sequence" : {
                  "value" : 4.503599627370508E15
                },
                "min_sequence" : {
                  "value" : 4.503599627370497E15
                }
              },
              {
                "key" : 3,
                "doc_count" : 12,
                "max_sequence" : {
                  "value" : 6.755399441055756E15
                },
                "min_sequence" : {
                  "value" : 6.755399441055745E15
                }
              }
            ]
          }
        }
      ]
    }
  }
}

```