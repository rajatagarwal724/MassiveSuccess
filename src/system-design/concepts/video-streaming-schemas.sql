-- ==================== VIDEO STREAMING SYSTEM SCHEMAS ====================
-- Netflix-like system with multipart uploads, transcoding, and adaptive streaming

-- ==================== CORE VIDEO METADATA ====================

CREATE TABLE videos (
    video_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    user_id UUID NOT NULL,
    category_id UUID,
    
    -- Original file info
    original_filename VARCHAR(500) NOT NULL,
    original_size_bytes BIGINT NOT NULL,
    original_duration_seconds INTEGER,
    original_resolution VARCHAR(20), -- e.g., "1920x1080"
    original_codec VARCHAR(50),
    original_bitrate INTEGER,
    
    -- S3 storage info
    s3_bucket VARCHAR(100) NOT NULL,
    s3_key VARCHAR(500) NOT NULL,
    s3_region VARCHAR(50) DEFAULT 'us-east-1',
    
    -- Status and timestamps
    status VARCHAR(20) DEFAULT 'uploading', -- uploading, processing, ready, failed
    upload_completed_at TIMESTAMP,
    processing_started_at TIMESTAMP,
    processing_completed_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- ==================== MULTIPART UPLOAD SESSION ====================

CREATE TABLE upload_sessions (
    session_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_id UUID NOT NULL,
    s3_upload_id VARCHAR(200) NOT NULL, -- S3 multipart upload ID
    
    -- Upload details
    total_size_bytes BIGINT NOT NULL,
    total_parts INTEGER NOT NULL,
    part_size_bytes INTEGER DEFAULT 5242880, -- 5MB default
    
    -- Status tracking
    status VARCHAR(20) DEFAULT 'initiated', -- initiated, in_progress, completed, failed, aborted
    completed_parts INTEGER DEFAULT 0,
    
    -- Metadata
    content_type VARCHAR(100),
    checksum_md5 VARCHAR(32),
    
    -- Timestamps
    initiated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    expires_at TIMESTAMP DEFAULT (CURRENT_TIMESTAMP + INTERVAL '24 hours'),
    
    FOREIGN KEY (video_id) REFERENCES videos(video_id) ON DELETE CASCADE,
    INDEX idx_video_id (video_id),
    INDEX idx_status (status),
    INDEX idx_expires_at (expires_at)
);

-- ==================== UPLOAD PARTS TRACKING ====================

CREATE TABLE upload_parts (
    part_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL,
    part_number INTEGER NOT NULL,
    
    -- Part details
    size_bytes INTEGER NOT NULL,
    etag VARCHAR(100), -- S3 ETag after successful upload
    checksum_md5 VARCHAR(32),
    
    -- Status and timing
    status VARCHAR(20) DEFAULT 'pending', -- pending, uploading, completed, failed
    upload_started_at TIMESTAMP,
    upload_completed_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (session_id) REFERENCES upload_sessions(session_id) ON DELETE CASCADE,
    UNIQUE KEY unique_session_part (session_id, part_number),
    INDEX idx_session_id (session_id),
    INDEX idx_status (status)
);

-- ==================== TRANSCODING JOBS ====================

CREATE TABLE transcoding_jobs (
    job_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_id UUID NOT NULL,
    
    -- Job configuration
    job_type VARCHAR(20) NOT NULL, -- 'resolution', 'hls_segments', 'thumbnail'
    target_resolution VARCHAR(20), -- '1080p', '720p', '480p', '360p'
    target_bitrate INTEGER,
    target_codec VARCHAR(50) DEFAULT 'H.264',
    
    -- Input/Output S3 paths
    input_s3_key VARCHAR(500) NOT NULL,
    output_s3_key VARCHAR(500),
    output_s3_bucket VARCHAR(100),
    
    -- Job status
    status VARCHAR(20) DEFAULT 'queued', -- queued, processing, completed, failed
    progress_percentage INTEGER DEFAULT 0,
    
    -- AWS MediaConvert/Elemental details
    aws_job_id VARCHAR(200),
    aws_job_arn VARCHAR(500),
    
    -- Error handling
    error_message TEXT,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    
    -- Timestamps
    queued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    
    FOREIGN KEY (video_id) REFERENCES videos(video_id) ON DELETE CASCADE,
    INDEX idx_video_id (video_id),
    INDEX idx_status (status),
    INDEX idx_job_type (job_type)
);

-- ==================== VIDEO VARIANTS (Different Resolutions) ====================

CREATE TABLE video_variants (
    variant_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_id UUID NOT NULL,
    transcoding_job_id UUID,
    
    -- Variant specifications
    resolution VARCHAR(20) NOT NULL, -- '1080p', '720p', '480p', '360p'
    width INTEGER NOT NULL,
    height INTEGER NOT NULL,
    bitrate INTEGER NOT NULL,
    codec VARCHAR(50) NOT NULL,
    container_format VARCHAR(20) DEFAULT 'mp4',
    
    -- Storage info
    s3_bucket VARCHAR(100) NOT NULL,
    s3_key VARCHAR(500) NOT NULL,
    file_size_bytes BIGINT,
    duration_seconds INTEGER,
    
    -- Quality metrics
    quality_score DECIMAL(3,2), -- 0.00 to 5.00
    
    -- Status
    status VARCHAR(20) DEFAULT 'processing', -- processing, ready, failed
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (video_id) REFERENCES videos(video_id) ON DELETE CASCADE,
    FOREIGN KEY (transcoding_job_id) REFERENCES transcoding_jobs(job_id),
    UNIQUE KEY unique_video_resolution (video_id, resolution),
    INDEX idx_video_id (video_id),
    INDEX idx_resolution (resolution)
);

-- ==================== HLS STREAMING SEGMENTS ====================

CREATE TABLE hls_segments (
    segment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    variant_id UUID NOT NULL,
    
    -- Segment details
    segment_number INTEGER NOT NULL,
    duration_seconds DECIMAL(5,3) NOT NULL, -- e.g., 10.000 seconds
    start_time_seconds DECIMAL(10,3) NOT NULL,
    
    -- Storage
    s3_bucket VARCHAR(100) NOT NULL,
    s3_key VARCHAR(500) NOT NULL, -- path to .ts file
    file_size_bytes INTEGER NOT NULL,
    
    -- Status
    status VARCHAR(20) DEFAULT 'ready', -- ready, failed
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (variant_id) REFERENCES video_variants(variant_id) ON DELETE CASCADE,
    UNIQUE KEY unique_variant_segment (variant_id, segment_number),
    INDEX idx_variant_id (variant_id),
    INDEX idx_segment_number (segment_number)
);

-- ==================== HLS PLAYLISTS ====================

CREATE TABLE hls_playlists (
    playlist_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_id UUID NOT NULL,
    
    -- Playlist types
    playlist_type VARCHAR(20) NOT NULL, -- 'master', 'media'
    variant_id UUID, -- NULL for master playlist
    
    -- Storage
    s3_bucket VARCHAR(100) NOT NULL,
    s3_key VARCHAR(500) NOT NULL, -- path to .m3u8 file
    
    -- Content
    playlist_content TEXT, -- Actual m3u8 content
    total_duration_seconds DECIMAL(10,3),
    segment_count INTEGER,
    
    -- Status
    status VARCHAR(20) DEFAULT 'ready',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (video_id) REFERENCES videos(video_id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES video_variants(variant_id) ON DELETE CASCADE,
    INDEX idx_video_id (video_id),
    INDEX idx_playlist_type (playlist_type)
);

-- ==================== THUMBNAILS ====================

CREATE TABLE thumbnails (
    thumbnail_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_id UUID NOT NULL,
    
    -- Thumbnail details
    thumbnail_type VARCHAR(20) NOT NULL, -- 'poster', 'timeline', 'preview'
    timestamp_seconds DECIMAL(10,3), -- When in video this thumbnail was taken
    width INTEGER NOT NULL,
    height INTEGER NOT NULL,
    
    -- Storage
    s3_bucket VARCHAR(100) NOT NULL,
    s3_key VARCHAR(500) NOT NULL,
    file_size_bytes INTEGER,
    format VARCHAR(10) DEFAULT 'jpg', -- jpg, png, webp
    
    -- Status
    status VARCHAR(20) DEFAULT 'ready',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (video_id) REFERENCES videos(video_id) ON DELETE CASCADE,
    INDEX idx_video_id (video_id),
    INDEX idx_thumbnail_type (thumbnail_type)
);

-- ==================== S3 EVENT NOTIFICATIONS LOG ====================

CREATE TABLE s3_event_notifications (
    event_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- S3 Event details
    event_name VARCHAR(50) NOT NULL, -- s3:ObjectCreated:*, s3:ObjectRemoved:*
    bucket_name VARCHAR(100) NOT NULL,
    object_key VARCHAR(500) NOT NULL,
    object_size BIGINT,
    object_etag VARCHAR(100),
    
    -- Event metadata
    event_time TIMESTAMP NOT NULL,
    source_ip_address VARCHAR(45),
    user_identity VARCHAR(200),
    request_id VARCHAR(100),
    
    -- Processing status
    processed BOOLEAN DEFAULT FALSE,
    processing_started_at TIMESTAMP,
    processing_completed_at TIMESTAMP,
    processing_error TEXT,
    
    -- Related entities (populated after processing)
    video_id UUID,
    upload_session_id UUID,
    transcoding_job_id UUID,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_event_name (event_name),
    INDEX idx_bucket_object (bucket_name, object_key),
    INDEX idx_processed (processed),
    INDEX idx_event_time (event_time)
);

-- ==================== PROCESSING PIPELINE STATUS ====================

CREATE TABLE processing_pipeline_status (
    pipeline_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    video_id UUID NOT NULL,
    
    -- Pipeline stages
    upload_status VARCHAR(20) DEFAULT 'pending', -- pending, completed, failed
    transcoding_status VARCHAR(20) DEFAULT 'pending',
    segmentation_status VARCHAR(20) DEFAULT 'pending',
    thumbnail_status VARCHAR(20) DEFAULT 'pending',
    
    -- Progress tracking
    total_variants_needed INTEGER DEFAULT 4, -- 1080p, 720p, 480p, 360p
    variants_completed INTEGER DEFAULT 0,
    
    total_thumbnails_needed INTEGER DEFAULT 10,
    thumbnails_completed INTEGER DEFAULT 0,
    
    -- Overall status
    overall_status VARCHAR(20) DEFAULT 'processing', -- processing, completed, failed
    overall_progress_percentage INTEGER DEFAULT 0,
    
    -- Timestamps
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    
    -- Error handling
    error_stage VARCHAR(50),
    error_message TEXT,
    
    FOREIGN KEY (video_id) REFERENCES videos(video_id) ON DELETE CASCADE,
    UNIQUE KEY unique_video_pipeline (video_id),
    INDEX idx_overall_status (overall_status)
);

-- ==================== SAMPLE QUERIES ====================

-- Find all videos ready for streaming
/*
SELECT v.video_id, v.title, v.status,
       COUNT(vv.variant_id) as available_resolutions,
       COUNT(hs.segment_id) as total_segments
FROM videos v
LEFT JOIN video_variants vv ON v.video_id = vv.video_id AND vv.status = 'ready'
LEFT JOIN hls_segments hs ON vv.variant_id = hs.variant_id
WHERE v.status = 'ready'
GROUP BY v.video_id, v.title, v.status;
*/

-- Track upload progress
/*
SELECT us.session_id, v.title,
       us.completed_parts, us.total_parts,
       ROUND((us.completed_parts * 100.0 / us.total_parts), 2) as progress_percentage
FROM upload_sessions us
JOIN videos v ON us.video_id = v.video_id
WHERE us.status = 'in_progress';
*/

-- Monitor transcoding jobs
/*
SELECT tj.job_id, v.title, tj.job_type, tj.target_resolution,
       tj.status, tj.progress_percentage,
       tj.started_at, tj.completed_at
FROM transcoding_jobs tj
JOIN videos v ON tj.video_id = v.video_id
WHERE tj.status IN ('queued', 'processing')
ORDER BY tj.queued_at;
*/
