ALTER TABLE outbox_events
ADD COLUMN retry_count INT NOT NULL DEFAULT 0;

ALTER TABLE outbox_events
RENAME COLUMN sent to sent_at;

ALTER TABLE outbox_events
ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;