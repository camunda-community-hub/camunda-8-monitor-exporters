package io.camunda.monitor.elasticsearch;

public class ElasticSearchConnection {

  private String host;
  private int port;
  private String protocol;
  public void setConnection(String host, int port, String protocol){
    this.host = host;
    this.port = port;
    this.protocol=protocol;
  }
  public String getUrl(String uri) {
    return this.protocol+"://"+this.host+":"+this.port+uri;
  }
}
