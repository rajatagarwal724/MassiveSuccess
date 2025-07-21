package lld.pubsub;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

record Message(long offset, String payload, Instant timestamp) { }

@RequiredArgsConstructor
class Topic {
    private final String name;
    private final Duration retention;
    private final AtomicLong currentOffset;
    private final Map<Subscriber, AtomicLong> subscriberOffsetsMap;
    private final ConcurrentNavigableMap<Long, Message> log;
    private final ScheduledExecutorService purger;

    public Topic(String name, Duration retention) {
        this.name = name;
        this.retention = retention;
        this.currentOffset = new AtomicLong();
        this.subscriberOffsetsMap = new ConcurrentHashMap<>();
        this.log = new ConcurrentSkipListMap<>();
        this.purger = Executors.newSingleThreadScheduledExecutor();
        enforceRetentionPolicy();
    }

    private void enforceRetentionPolicy() {
        purger.scheduleAtFixedRate(this::purgeExpiredMessages, retention.getSeconds(), retention.getSeconds(), TimeUnit.SECONDS);
    }

    private void purgeExpiredMessages() {
        Instant cutOff = Instant.now().minus(retention);
        log
                .entrySet()
                .removeIf(entry -> entry.getValue().timestamp().isBefore(cutOff));
    }

    public void shutdown() {
        purger.shutdown();
        subscriberOffsetsMap.forEach((subscriber, atomicLong) -> subscriber.stop());
    }

    public boolean addMessage(String payload) {
        long nextOffset = currentOffset.incrementAndGet();
        var message = new Message(nextOffset, payload, Instant.now());
        log.put(nextOffset, message);
        return true;
    }

    public void addSubscriber(Subscriber subscriber) {
        subscriberOffsetsMap.putIfAbsent(subscriber, new AtomicLong(0));
    }

    public Map<Subscriber, Long> getLagPerConsumer() {
        return subscriberOffsetsMap
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> Math.max(0, (currentOffset.get() - entry.getValue().get()))
                        )
                );
    }

    public void resetOffset(Long offset, Subscriber subscriber) {
        subscriberOffsetsMap.putIfAbsent(subscriber, new AtomicLong(offset));
    }

    public List<Message> consume(Subscriber subscriber) {
        var offset = subscriberOffsetsMap.get(subscriber);
        if (null == offset) {
            throw new IllegalArgumentException("No Subscriber : " + subscriber);
        }

        var tailedMessages = log.tailMap(offset.get(), false);
        if (!tailedMessages.isEmpty()) {
            offset.set(tailedMessages.lastEntry().getKey());
        }
        return new ArrayList<>(tailedMessages.values());
    }
}

@RequiredArgsConstructor
@Data
class Subscriber {
    private final String id;
    private final Topic topic;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private volatile boolean isRunning = true;

    public void start(Consumer<Message> handler) {
        executorService.submit(() -> {
            while (isRunning) {
                topic.consume(this).forEach(handler);
            }
        });
    }

    public void stop() {
        isRunning = false;
        executorService.shutdown();
    }
}

@RequiredArgsConstructor
class PubSubService {
    private final Map<String, Topic> topics;
    private final ExecutorService dispatcher;

    public PubSubService() {
        this.topics = new ConcurrentHashMap<>();
        this.dispatcher = Executors.newCachedThreadPool();
    }

    public void createTopic(String name, Duration durationInSeconds) {
        topics.computeIfAbsent(name, k -> new Topic(name, durationInSeconds));
    }

    public void deleteTopic(String name) {
        var topic = topics.get(name);
        if (null != topic) {
            topic.shutdown();
        }
    }

    public void publish(String topicName, String payload) {
        Topic topic = topics.get(topicName);
        if (null == topic) {
            throw new IllegalArgumentException("No Topic by Name: " + topicName);
        }
        dispatcher.submit(() -> topic.addMessage(payload));
    }

    public Subscriber createSubscriber(String topicName, String subscriberId) {
        var topic = topics.get(topicName);
        if (null == topic) {
            throw new IllegalArgumentException("No Topic by Name: " + topicName);
        }
        var subscriber = new Subscriber(subscriberId, topic);
        topic.addSubscriber(subscriber);
        return subscriber;
    }

    public Map<Subscriber, Long> getConsumersLag(String topicName) {
        Topic topic = topics.get(topicName);

        if (null == topic) {
            throw new IllegalArgumentException("No Topic by Name: " + topicName);
        }

        return topic.getLagPerConsumer();
    }

    public void resetOffset(String topicName, Long offset, Subscriber subscriber) {
        Topic topic = topics.get(topicName);

        if (null == topic) {
            throw new IllegalArgumentException("No Topic by Name: " + topicName);
        }

        topic.resetOffset(offset, subscriber);
    }

    public void shutdown() {
        topics.forEach((s, topic) -> topic.shutdown());
    }
}

public class KafkaPubSub {
    public static void main(String[] args) throws InterruptedException {
        var pubSubService = new PubSubService();

        pubSubService.createTopic("topic-1", Duration.ofSeconds(100));

        var executor = Executors.newCachedThreadPool();
        IntStream.rangeClosed(1, 10).boxed().forEach(integer -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executor.submit(() -> pubSubService.publish("topic-1", "Test"));
        });

        var sub1 = pubSubService.createSubscriber("topic-1", "subscriber-1");
//        var sub2 = pubSubService.createSubscriber("topic-1", "subscriber-2");

        sub1.start(msg -> System.out.println("C1: " + msg));
//        sub2.start(msg -> System.out.println("C2: " + msg));

        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);

        Thread.sleep(1000);

//        pubSubService.getConsumersLag("topic-1").forEach((subscriber, aLong) -> System.out.println(subscriber + " -- " + aLong));

//        System.out.println("Resetting C2 offset to 0 (replay)");
//        pubSubService.resetOffset("topic-1", 0L, sub1);
//
//        Thread.sleep(1000);
//        pubSubService.getConsumersLag("topic-1").forEach((subscriber, aLong) -> System.out.println(subscriber + " -- " + aLong));

        pubSubService.shutdown();
    }
}
