// (c) https://github.com/MontiCore/monticore
package replayer;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.paho.client.mqttv3.*;

import java.util.*;
import java.util.stream.Collectors;

public class Replayer implements MqttCallback {

  /**
   * all messages received from ports
   * topic -> [(timestamp, payload)]
   */
  Map<String, List<Pair<Long, String>>> receivedMessages = new HashMap<>();

  /**
   * All connectors
   * targetPort -> sourcePorts
   * MontiArc / MontiThings only allows one source port currently
   */
  Map<String, Set<String>> datasources = new HashMap<>();
  Map<String, Set<String>> datareceivers = new HashMap<>();

  /**
   * Logical timestamp
   */
  Long timestamp = 0L;

  MqttClient client;

  StateStore stateStore;

  public Replayer(String brokerURI) throws MqttException {
    client = new MqttClient(brokerURI, MqttClient.generateClientId());
    client.connect();
    client.setCallback(this);
    client.subscribe("/ports/#");
    client.subscribe("/connectors/#");
    client.subscribe("/reqestReplay/ports/#");
  }

  @Override public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
    if (topic.startsWith("/ports/")) {
      storeMessage(topic, mqttMessage);
    }
    if (topic.startsWith("/reqestReplay/ports/")) {
      String instanceName = topic.substring("/reqestReplay/ports/".length());
      replayTopics(instanceName);
    }
    if (topic.startsWith("/connectors/")) {
      String target = topic.substring("/connectors/".length());
      String source = new String(mqttMessage.getPayload());
      if (!datasources.containsKey(target)) {
        datasources.put(target, new HashSet<>());
      }
      datasources.get(target).add(source);

      if (!datareceivers.containsKey(source)) {
        datareceivers.put(source, new HashSet<>());
      }
      datareceivers.get(source).add(target);
    }
  }

  protected void storeMessage(String topic, MqttMessage mqttMessage) {
    ensureTopicExists(topic);
    String payload = new String(mqttMessage.getPayload());
    receivedMessages.get(topic).add(Pair.of(timestamp++, payload));
  }

  protected void ensureTopicExists(String topic) {
    if (!receivedMessages.containsKey(topic)) {
      receivedMessages.put(topic, new ArrayList<>());
    }
  }

  protected void replayTopics(String instanceName) throws MqttException {
    Set<String> topicsToReplay = getTopicsToReplay(instanceName);
    Map<String, Integer> iterator = stateStore.getMessagesToSkip(instanceName);
    while (!allTopicsAreReplayed(topicsToReplay, iterator)) {
      // Find next message
      String nextTopic = getNextTopicToReplayMessageFrom(iterator, topicsToReplay);
      List<Pair<Long, String>> nextTopicContents = receivedMessages.get(nextTopic);
      String nextPaylaod = nextTopicContents.get(iterator.get(nextTopic)).getRight();

      // Publish message
      for (String target : datareceivers.get(nextTopic.substring("/ports/".length()))) {
        publishMessage("/ports/" + target, nextPaylaod);
      }

      // Increase iterator for that topic
      iterator.put(nextTopic, iterator.get(nextTopic) + 1);
    }
  }

  protected void publishMessage(String topic, String payload) throws MqttException {
    MqttMessage mqttMessage = new MqttMessage();
    mqttMessage.setPayload(payload.getBytes());
    client.publish(topic, mqttMessage);
  }

  protected Set<String> getTopicsToReplay(String instanceName) {
    Set<String> allTargetPorts = datasources.keySet();
    Set<String> portsOfInstance = allTargetPorts.stream()
      .filter(t -> t.startsWith(instanceName))
      .collect(Collectors.toSet());
    Set<String> datasourcesOfInstance = new HashSet<>();
    for (String port : portsOfInstance) {
      datasourcesOfInstance.addAll(datasources.get(port));
    }
    return datasourcesOfInstance.stream().map(t -> "/ports/" + t).collect(Collectors.toSet());
  }

  protected String getNextTopicToReplayMessageFrom(Map<String, Integer> iterators,
    Set<String> topicsToReplay) {

    String nextTopic = null;
    Pair<Long, String> nextMessage = null;
    for (String current : iterators.keySet()) {
      if (!topicsToReplay.contains(current)) {
        continue;
      }
      if (nextTopic == null || nextMessage.getLeft() <
        receivedMessages.get(current).get(iterators.get(current)).getLeft()) {
        nextTopic = current;
        nextMessage = receivedMessages.get(current).get(iterators.get(current));
      }
    }
    return nextTopic;
  }

  private boolean allTopicsAreReplayed(Set<String> topicsToReplay, Map<String, Integer> iterator) {
    topicsToReplay.forEach(t -> ensureTopicExists(t));
    return topicsToReplay.stream()
      .noneMatch(t -> iterator.get(t) < receivedMessages.get(t).size());
  }

  @Override public void connectionLost(Throwable throwable) {
    // useless for us
  }

  @Override public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    // useless for us
  }

  public Map<String, List<Pair<Long, String>>> getReceivedMessages() {
    return receivedMessages;
  }

  public StateStore getStateStore() {
    return stateStore;
  }

  public void setStateStore(StateStore stateStore) {
    this.stateStore = stateStore;
  }
}