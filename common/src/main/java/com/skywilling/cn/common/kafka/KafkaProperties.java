package com.skywilling.cn.common.kafka;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Properties;


public class KafkaProperties {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaProperties.class);
  /**
   * kafka configuration
   */
  private static final String SERVERS = "bootstrap.servers";

  /**
   * kafka producer configuration
   */
  private static final String ACK = "acks";

  private static final String RETRIES = "retries";

  private static final String BATCH_SIZE = "batch.size";

  private static final String LINGER = "linger.ms";

  private static final String BUFFER = "buffer.memory";

  private static final String KEY_SERIALIZER = "key.serializer";

  private static final String VALUE_SERIALIZER = "value.serializer";

  /**
   * kafka consumer configuration
   */
  private static final String ENABL_AUTO_COMMIT = "enable.auto.commit";

  private static final String AUTO_COMMIT_INTERVAL = "auto.commit.interval.ms";

  private static final String KEY_DESERIALIZER = "key.deserializer";

  private static final String VALUE_DESERIALIZER = "value.deserializer";

  private static final String GROUP_ID = "group.id";

  private PropertiesConfiguration propConfig;

  private Properties producerProperties;

  private Properties consumerProperties;

  public KafkaProperties(String filename) throws Exception {
    try {
      Configurations configurations = new Configurations();
      FileBasedConfigurationBuilder.setDefaultEncoding(PropertiesConfiguration.class, "UTF-8");
      propConfig = configurations
          .properties(this.getClass().getClassLoader().getResource(filename));

      producerProperties = new Properties();
      consumerProperties = new Properties();
      loadProducerProperties();
      loadConsumerProperties();
    } catch (Throwable e) {
      LOG.error("[KafkaProducerProperties] error", e);
      throw e;
    }
  }

  private void loadProducerProperties() {
    producerProperties.put(SERVERS, propConfig.getString(SERVERS));
    producerProperties.put(ACK, propConfig.getString(ACK));
    producerProperties.put(RETRIES, propConfig.getInt(RETRIES, 0));
    producerProperties.put(BATCH_SIZE, propConfig.getInt(BATCH_SIZE, 16384));
    producerProperties.put(LINGER, propConfig.getInt(LINGER, 1));
    producerProperties.put(BUFFER, propConfig.getInt(BUFFER, 33554432));
    producerProperties.put(KEY_SERIALIZER, propConfig.getString(KEY_SERIALIZER));
    producerProperties.put(VALUE_SERIALIZER, propConfig.getString(VALUE_SERIALIZER));
  }

  private void loadConsumerProperties() {
    consumerProperties.put(GROUP_ID, propConfig.getString(GROUP_ID));
    consumerProperties.put(SERVERS, propConfig.getString(SERVERS));
    consumerProperties.put(ENABL_AUTO_COMMIT, propConfig.getString(ENABL_AUTO_COMMIT));
    consumerProperties.put(AUTO_COMMIT_INTERVAL, propConfig.getString(AUTO_COMMIT_INTERVAL));
    consumerProperties.put(KEY_DESERIALIZER, propConfig.getString(KEY_DESERIALIZER));
    consumerProperties.put(VALUE_DESERIALIZER, propConfig.getString(VALUE_DESERIALIZER));
  }

  public Properties getProducerProperties() {
    return producerProperties;
  }

  public Properties getConsumerProperties() {
    return consumerProperties;
  }
}
