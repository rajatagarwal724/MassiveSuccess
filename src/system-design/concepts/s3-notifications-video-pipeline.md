# S3 Notifications & Video Processing Pipeline

## Overview
S3 Event Notifications enable event-driven architecture for video processing. When objects are uploaded/modified in S3, notifications trigger downstream processing automatically.

## S3 Notification Types

### Supported Events
```
s3:ObjectCreated:*              # Any object creation
s3:ObjectCreated:Put            # PUT operations
s3:ObjectCreated:Post           # POST operations (multipart)
s3:ObjectCreated:Copy           # Copy operations
s3:ObjectCreated:CompleteMultipartUpload  # Multipart upload completion

s3:ObjectRemoved:*              # Any object deletion
s3:ObjectRemoved:Delete         # DELETE operations
s3:ObjectRemoved:DeleteMarkerCreated  # Versioned bucket deletions
```

### Notification Destinations
1. **SQS Queue** (Most Common)
2. **SNS Topic** 
3. **Lambda Function**
4. **EventBridge**

## Video Processing Pipeline Architecture

```
┌─────────────────┐    ┌──────────────┐    ┌─────────────────┐
│   Client App    │───▶│   Backend    │───▶│   S3 Bucket     │
│                 │    │   Server     │    │   (Raw Video)   │
└─────────────────┘    └──────────────┘    └─────────────────┘
                                                     │
                                                     │ S3 Event
                                                     ▼
┌─────────────────┐    ┌──────────────┐    ┌─────────────────┐
│   Video Ready   │◀───│   SQS Queue  │◀───│  S3 Notification│
│   for Streaming │    │              │    │                 │
└─────────────────┘    └──────────────┘    └─────────────────┘
         ▲                       │
         │                       ▼
┌─────────────────┐    ┌──────────────┐
│  HLS Segments   │◀───│  Transcoding │
│   & Playlists   │    │   Service    │
└─────────────────┘    └──────────────┘
```

## S3 Bucket Configuration

### Event Notification Setup
```json
{
  "NotificationConfiguration": {
    "QueueConfigurations": [
      {
        "Id": "VideoUploadComplete",
        "QueueArn": "arn:aws:sqs:us-east-1:123456789012:video-processing-queue",
        "Events": ["s3:ObjectCreated:CompleteMultipartUpload"],
        "Filter": {
          "Key": {
            "FilterRules": [
              {
                "Name": "prefix",
                "Value": "videos/raw/"
              },
              {
                "Name": "suffix", 
                "Value": ".mp4"
              }
            ]
          }
        }
      },
      {
        "Id": "TranscodingComplete",
        "QueueArn": "arn:aws:sqs:us-east-1:123456789012:transcoding-complete-queue",
        "Events": ["s3:ObjectCreated:Put"],
        "Filter": {
          "Key": {
            "FilterRules": [
              {
                "Name": "prefix",
                "Value": "videos/transcoded/"
              }
            ]
          }
        }
      }
    ]
  }
}
```

## Event Processing Flow

### 1. Raw Video Upload Complete
```java
@Component
public class VideoUploadEventProcessor {
    
    @SqsListener("video-processing-queue")
    public void processVideoUpload(S3EventNotification event) {
        for (S3EventNotification.S3EventNotificationRecord record : event.getRecords()) {
            String eventName = record.getEventName();
            String bucketName = record.getS3().getBucket().getName();
            String objectKey = record.getS3().getObject().getKey();
            
            if ("s3:ObjectCreated:CompleteMultipartUpload".equals(eventName)) {
                handleVideoUploadComplete(bucketName, objectKey);
            }
        }
    }
    
    private void handleVideoUploadComplete(String bucket, String key) {
        // 1. Update upload session status
        uploadSessionService.markCompleted(key);
        
        // 2. Update video status
        videoService.updateStatus(key, VideoStatus.PROCESSING);
        
        // 3. Create transcoding jobs
        createTranscodingJobs(bucket, key);
        
        // 4. Log S3 event
        s3EventService.logEvent(bucket, key, "CompleteMultipartUpload");
    }
    
    private void createTranscodingJobs(String bucket, String key) {
        String videoId = extractVideoIdFromKey(key);
        
        // Create multiple resolution jobs
        String[] resolutions = {"1080p", "720p", "480p", "360p"};
        
        for (String resolution : resolutions) {
            TranscodingJob job = TranscodingJob.builder()
                .videoId(videoId)
                .jobType("resolution")
                .targetResolution(resolution)
                .inputS3Key(key)
                .outputS3Key("videos/transcoded/" + videoId + "/" + resolution + "/")
                .status("queued")
                .build();
                
            transcodingJobService.create(job);
            
            // Submit to AWS MediaConvert
            mediaConvertService.submitJob(job);
        }
        
        // Create HLS segmentation job
        TranscodingJob hlsJob = TranscodingJob.builder()
            .videoId(videoId)
            .jobType("hls_segments")
            .inputS3Key(key)
            .outputS3Key("videos/hls/" + videoId + "/")
            .status("queued")
            .build();
            
        transcodingJobService.create(hlsJob);
        mediaConvertService.submitJob(hlsJob);
    }
}
```

### 2. Transcoding Complete Handler
```java
@Component
public class TranscodingCompleteProcessor {
    
    @SqsListener("transcoding-complete-queue")
    public void processTranscodingComplete(S3EventNotification event) {
        for (S3EventNotification.S3EventNotificationRecord record : event.getRecords()) {
            String objectKey = record.getS3().getObject().getKey();
            
            if (objectKey.contains("/transcoded/")) {
                handleResolutionComplete(objectKey);
            } else if (objectKey.contains("/hls/")) {
                handleHlsSegmentComplete(objectKey);
            }
        }
    }
    
    private void handleResolutionComplete(String objectKey) {
        // Extract video ID and resolution from key
        // videos/transcoded/video-123/720p/output.mp4
        String[] parts = objectKey.split("/");
        String videoId = parts[2];
        String resolution = parts[3];
        
        // Update video variant
        VideoVariant variant = VideoVariant.builder()
            .videoId(videoId)
            .resolution(resolution)
            .s3Key(objectKey)
            .status("ready")
            .build();
            
        videoVariantService.create(variant);
        
        // Update transcoding job status
        transcodingJobService.updateStatus(videoId, resolution, "completed");
        
        // Check if all variants are complete
        checkAndUpdateVideoStatus(videoId);
    }
    
    private void handleHlsSegmentComplete(String objectKey) {
        // Process HLS segments and create playlist
        if (objectKey.endsWith(".m3u8")) {
            // Master playlist created
            createHlsPlaylist(objectKey);
        } else if (objectKey.endsWith(".ts")) {
            // Individual segment created
            createHlsSegment(objectKey);
        }
    }
}
```

## S3 Event Message Format

### Sample SQS Message
```json
{
  "Records": [
    {
      "eventVersion": "2.1",
      "eventSource": "aws:s3",
      "awsRegion": "us-east-1",
      "eventTime": "2024-01-15T10:30:00.000Z",
      "eventName": "s3:ObjectCreated:CompleteMultipartUpload",
      "userIdentity": {
        "principalId": "AWS:AIDAI..."
      },
      "requestParameters": {
        "sourceIPAddress": "192.168.1.1"
      },
      "responseElements": {
        "x-amz-request-id": "C3D13FE58DE4C810",
        "x-amz-id-2": "FMyUVURIY8/IgAtTv8xRjskZQpcIZ9KG4V5Wp6S7S/JRweUWerMUE5JgHvANOjpD"
      },
      "s3": {
        "s3SchemaVersion": "1.0",
        "configurationId": "VideoUploadComplete",
        "bucket": {
          "name": "my-video-bucket",
          "ownerIdentity": {
            "principalId": "A3NL1KOZZKExample"
          },
          "arn": "arn:aws:s3:::my-video-bucket"
        },
        "object": {
          "key": "videos/raw/video-123/original.mp4",
          "size": 1024000000,
          "eTag": "d41d8cd98f00b204e9800998ecf8427e",
          "sequencer": "0A1B2C3D4E5F678901"
        }
      }
    }
  ]
}
```

## Advanced S3 Notification Patterns

### 1. Fan-out Pattern with SNS
```
S3 Event → SNS Topic → Multiple SQS Queues
                   ├── Transcoding Queue
                   ├── Thumbnail Generation Queue  
                   ├── Metadata Extraction Queue
                   └── Analytics Queue
```

### 2. Lambda Direct Processing
```java
@Component
public class S3EventLambdaHandler implements RequestHandler<S3Event, String> {
    
    @Override
    public String handleRequest(S3Event event, Context context) {
        for (S3EventNotification.S3EventNotificationRecord record : event.getRecords()) {
            processS3Event(record);
        }
        return "OK";
    }
    
    private void processS3Event(S3EventNotification.S3EventNotificationRecord record) {
        // Direct processing without queue
        String eventName = record.getEventName();
        
        switch (eventName) {
            case "s3:ObjectCreated:CompleteMultipartUpload":
                handleVideoUpload(record);
                break;
            case "s3:ObjectCreated:Put":
                handleTranscodingComplete(record);
                break;
        }
    }
}
```

## Error Handling & Retry Logic

### Dead Letter Queue Setup
```java
@Configuration
public class SqsConfig {
    
    @Bean
    public Queue videoProcessingQueue() {
        return QueueBuilder.durable("video-processing-queue")
            .withArgument("x-dead-letter-exchange", "video-processing-dlq")
            .withArgument("x-message-ttl", 300000) // 5 minutes
            .withArgument("x-max-retries", 3)
            .build();
    }
    
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("video-processing-dlq").build();
    }
}
```

### Retry Processing
```java
@Component
public class S3EventProcessor {
    
    @Retryable(value = {Exception.class}, maxAttempts = 3, 
               backoff = @Backoff(delay = 1000, multiplier = 2))
    public void processEvent(S3EventNotification.S3EventNotificationRecord record) {
        try {
            // Process event
            handleS3Event(record);
        } catch (Exception e) {
            log.error("Failed to process S3 event: {}", record.getS3().getObject().getKey(), e);
            throw e; // Trigger retry
        }
    }
    
    @Recover
    public void recover(Exception ex, S3EventNotification.S3EventNotificationRecord record) {
        // Send to DLQ or alert
        log.error("Failed to process after retries: {}", record.getS3().getObject().getKey());
        alertService.sendAlert("S3 Event Processing Failed", ex.getMessage());
    }
}
```

## Monitoring & Observability

### CloudWatch Metrics
```java
@Component
public class S3EventMetrics {
    
    private final MeterRegistry meterRegistry;
    
    public void recordEventProcessed(String eventType, String status) {
        Counter.builder("s3.events.processed")
            .tag("event_type", eventType)
            .tag("status", status)
            .register(meterRegistry)
            .increment();
    }
    
    public void recordProcessingTime(String eventType, Duration duration) {
        Timer.builder("s3.events.processing.time")
            .tag("event_type", eventType)
            .register(meterRegistry)
            .record(duration);
    }
}
```

### Logging Best Practices
```java
@Slf4j
@Component
public class S3EventLogger {
    
    public void logEventReceived(S3EventNotification.S3EventNotificationRecord record) {
        log.info("S3 Event received: eventName={}, bucket={}, key={}, size={}", 
            record.getEventName(),
            record.getS3().getBucket().getName(),
            record.getS3().getObject().getKey(),
            record.getS3().getObject().getSize());
    }
    
    public void logProcessingComplete(String videoId, long processingTimeMs) {
        log.info("Video processing complete: videoId={}, processingTime={}ms", 
            videoId, processingTimeMs);
    }
}
```

## Benefits of S3 Event-Driven Architecture

✅ **Decoupled Processing** - Services react to events independently  
✅ **Scalable** - Auto-scaling based on queue depth  
✅ **Reliable** - Built-in retry and DLQ mechanisms  
✅ **Real-time** - Near-instant processing triggers  
✅ **Cost-effective** - Pay only for events processed  
✅ **Fault-tolerant** - Failed events don't block others  

This event-driven approach ensures your video processing pipeline is robust, scalable, and responds immediately to uploads without constant polling! 🚀

## S3 Events to Kafka Integration Patterns

### Pattern 1: SQS → Kafka Bridge (Recommended)

**Architecture:**
```
S3 Event → SQS Queue → Kafka Connect SQS Source → Kafka Topic
```

**Kafka Connect SQS Source Configuration:**
```json
{
  "name": "s3-events-sqs-source",
  "config": {
    "connector.class": "io.confluent.connect.sqs.SqsSourceConnector",
    "tasks.max": "3",
    "sqs.url": "https://sqs.us-east-1.amazonaws.com/123456789012/video-processing-queue",
    "sqs.max.messages": "10",
    "sqs.wait.time.seconds": "20",
    "kafka.topic": "video-processing-events",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable": "false",
    "transforms": "ExtractS3Event",
    "transforms.ExtractS3Event.type": "org.apache.kafka.connect.transforms.ExtractField$Value",
    "transforms.ExtractS3Event.field": "Records"
  }
}
```

### Pattern 2: Lambda → Kafka Producer

**Lambda Function:**
```java
@Component
public class S3ToKafkaLambda implements RequestHandler<S3Event, String> {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Override
    public String handleRequest(S3Event event, Context context) {
        for (S3EventNotification.S3EventNotificationRecord record : event.getRecords()) {
            publishToKafka(record);
        }
        return "OK";
    }
    
    private void publishToKafka(S3EventNotification.S3EventNotificationRecord record) {
        try {
            S3EventMessage message = S3EventMessage.builder()
                .eventName(record.getEventName())
                .bucketName(record.getS3().getBucket().getName())
                .objectKey(record.getS3().getObject().getKey())
                .objectSize(record.getS3().getObject().getSize())
                .eventTime(record.getEventTime().toInstant())
                .eTag(record.getS3().getObject().geteTag())
                .build();
            
            // Route to different topics based on event type
            String topic = getTopicForEvent(record.getEventName());
            String key = extractVideoIdFromKey(record.getS3().getObject().getKey());
            
            kafkaTemplate.send(topic, key, message);
            
            log.info("Published S3 event to Kafka: topic={}, key={}, event={}", 
                topic, key, record.getEventName());
                
        } catch (Exception e) {
            log.error("Failed to publish S3 event to Kafka", e);
            throw new RuntimeException(e);
        }
    }
    
    private String getTopicForEvent(String eventName) {
        if (eventName.contains("CompleteMultipartUpload")) {
            return "video-upload-complete";
        } else if (eventName.contains("ObjectCreated")) {
            return "video-transcoding-complete";
        }
        return "video-processing-events";
    }
}
```

### Pattern 3: SNS → Kafka Connect

**Architecture:**
```
S3 Event → SNS Topic → Kafka Connect SNS Source → Kafka Topic
```

**SNS to Kafka Connect Configuration:**
```json
{
  "name": "s3-events-sns-source",
  "config": {
    "connector.class": "io.confluent.connect.sns.SnsSourceConnector",
    "tasks.max": "3",
    "sns.topic.arn": "arn:aws:sns:us-east-1:123456789012:video-processing-events",
    "kafka.topic": "video-processing-events",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable": "false"
  }
}
```

### Pattern 4: EventBridge → Kafka

**EventBridge Rule:**
```json
{
  "Rules": [
    {
      "Name": "S3VideoEvents",
      "EventPattern": {
        "source": ["aws.s3"],
        "detail-type": ["Object Created", "Object Deleted"],
        "detail": {
          "bucket": {
            "name": ["my-video-bucket"]
          },
          "object": {
            "key": [{"prefix": "videos/"}]
          }
        }
      },
      "Targets": [
        {
          "Id": "1",
          "Arn": "arn:aws:lambda:us-east-1:123456789012:function:s3-to-kafka-forwarder"
        }
      ]
    }
  ]
}
```

### Pattern 5: Custom Spring Boot Bridge Service

**Microservice Approach:**
```java
@Service
@Slf4j
public class S3EventKafkaBridge {
    
    private final KafkaTemplate<String, S3EventMessage> kafkaTemplate;
    
    @SqsListener("${app.sqs.video-processing-queue}")
    public void handleS3Event(S3EventNotification event) {
        for (S3EventNotification.S3EventNotificationRecord record : event.getRecords()) {
            processAndForward(record);
        }
    }
    
    private void processAndForward(S3EventNotification.S3EventNotificationRecord record) {
        try {
            // Enrich the event with additional metadata
            S3EventMessage enrichedMessage = enrichS3Event(record);
            
            // Determine partitioning key (video_id for ordering)
            String partitionKey = extractVideoIdFromKey(record.getS3().getObject().getKey());
            
            // Send to appropriate Kafka topic
            String topic = determineKafkaTopic(record);
            
            kafkaTemplate.send(topic, partitionKey, enrichedMessage)
                .addCallback(
                    result -> log.info("Successfully sent S3 event to Kafka: {}", topic),
                    failure -> log.error("Failed to send S3 event to Kafka", failure)
                );
                
        } catch (Exception e) {
            log.error("Error processing S3 event for Kafka", e);
            // Send to DLQ or retry
        }
    }
    
    private S3EventMessage enrichS3Event(S3EventNotification.S3EventNotificationRecord record) {
        return S3EventMessage.builder()
            .eventId(UUID.randomUUID().toString())
            .eventName(record.getEventName())
            .eventTime(record.getEventTime().toInstant())
            .bucketName(record.getS3().getBucket().getName())
            .objectKey(record.getS3().getObject().getKey())
            .objectSize(record.getS3().getObject().getSize())
            .eTag(record.getS3().getObject().geteTag())
            .videoId(extractVideoIdFromKey(record.getS3().getObject().getKey()))
            .processingStage(determineProcessingStage(record))
            .metadata(extractMetadataFromKey(record.getS3().getObject().getKey()))
            .build();
    }
    
    private String determineKafkaTopic(S3EventNotification.S3EventNotificationRecord record) {
        String objectKey = record.getS3().getObject().getKey();
        String eventName = record.getEventName();
        
        if (eventName.contains("CompleteMultipartUpload")) {
            return "video.upload.completed";
        } else if (objectKey.contains("/transcoded/")) {
            return "video.transcoding.completed";
        } else if (objectKey.contains("/hls/") && objectKey.endsWith(".m3u8")) {
            return "video.hls.playlist.ready";
        } else if (objectKey.contains("/hls/") && objectKey.endsWith(".ts")) {
            return "video.hls.segment.ready";
        } else if (objectKey.contains("/thumbnails/")) {
            return "video.thumbnail.generated";
        }
        
        return "video.processing.events";
    }
}
```

### Kafka Topic Structure

**Topic Design:**
```yaml
Topics:
  video.upload.completed:
    partitions: 12
    replication-factor: 3
    retention: 7d
    
  video.transcoding.completed:
    partitions: 12  
    replication-factor: 3
    retention: 7d
    
  video.hls.playlist.ready:
    partitions: 6
    replication-factor: 3
    retention: 3d
    
  video.hls.segment.ready:
    partitions: 24
    replication-factor: 3
    retention: 1d
    
  video.thumbnail.generated:
    partitions: 6
    replication-factor: 3
    retention: 3d
```

### Kafka Consumer Examples

**Video Processing Consumer:**
```java
@KafkaListener(topics = "video.upload.completed", groupId = "video-processor")
public void handleVideoUploadComplete(S3EventMessage event) {
    log.info("Processing video upload complete: videoId={}", event.getVideoId());
    
    // Start transcoding pipeline
    transcodingService.startTranscoding(event.getVideoId(), event.getObjectKey());
}

@KafkaListener(topics = "video.transcoding.completed", groupId = "video-processor") 
public void handleTranscodingComplete(S3EventMessage event) {
    log.info("Transcoding complete: videoId={}, resolution={}", 
        event.getVideoId(), event.getMetadata().get("resolution"));
        
    // Update video variant status
    videoVariantService.markReady(event.getVideoId(), event.getMetadata().get("resolution"));
}

@KafkaListener(topics = "video.hls.playlist.ready", groupId = "streaming-service")
public void handleHlsPlaylistReady(S3EventMessage event) {
    log.info("HLS playlist ready: videoId={}", event.getVideoId());
    
    // Update video status to ready for streaming
    videoService.markReadyForStreaming(event.getVideoId());
}
```

### Benefits of Kafka Integration

✅ **Event Streaming** - Real-time event processing across multiple consumers  
✅ **Durability** - Events persisted in Kafka for replay/recovery  
✅ **Scalability** - Horizontal scaling with partitions  
✅ **Decoupling** - Multiple services can consume same events  
✅ **Event Sourcing** - Complete audit trail of video processing  
✅ **Batch Processing** - Can process events in batches for analytics  

### Monitoring Kafka Integration

**Metrics to Track:**
```java
@Component
public class S3KafkaMetrics {
    
    @EventListener
    public void onS3EventPublished(S3EventPublishedEvent event) {
        Metrics.counter("s3.events.published", 
            "topic", event.getTopic(),
            "event_type", event.getEventType())
            .increment();
    }
    
    @EventListener  
    public void onKafkaPublishFailure(KafkaPublishFailureEvent event) {
        Metrics.counter("s3.events.publish.failed",
            "topic", event.getTopic(),
            "error", event.getError())
            .increment();
    }
}
```

This Kafka integration provides a robust, scalable event streaming foundation for your video processing pipeline! 🚀
