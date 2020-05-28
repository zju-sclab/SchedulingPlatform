package com.skywilling.cn.web.model;

import com.skywilling.cn.common.kafka.KafkaProperties;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockProducer {

  private static final Logger LOG = LoggerFactory.getLogger(LockProducer.class);

  private static final String LOCK_TOPIC = "LOCK";
  private static final String LOCK_OPERATION = "lock";
  private static final String UNLOCK_OPERATION = "unlock";

  private Producer<String, String> producer;
  private KafkaProperties kafkaProperties;

  LockProducer(KafkaProperties kafkaProperties) {
    this.kafkaProperties = kafkaProperties;
  }


  void init() {
    producer = new KafkaProducer<>(kafkaProperties.getProducerProperties());
  }

  /**
   * write Lock Msg
   */
  public void writeLockMsg(String vin) {
    producer.send(new ProducerRecord<>(LOCK_TOPIC, vin, LOCK_OPERATION),
        new Callback() {
          @Override
          public void onCompletion(RecordMetadata metadata, Exception exception) {
            LOG.info("writeLockMsg success", "write topic: %s, offset: %s", metadata.topic(),
                metadata.offset());
          }
        });
  }

  /**
   * write Unlock Operation
   */
  public void writeUnlockMsg(String vin) {
    producer.send(new ProducerRecord<>(LOCK_TOPIC, vin, UNLOCK_OPERATION));
  }

  void destroy() {
    if (producer != null) {
      producer.close();
    }
  }
}
