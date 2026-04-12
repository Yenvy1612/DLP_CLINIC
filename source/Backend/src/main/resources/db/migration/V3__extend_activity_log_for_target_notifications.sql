-- V3: Extend activity_log to support direct user-targeted notifications.

ALTER TABLE activity_log
    ADD COLUMN actor_user_id BIGINT NULL,
    ADD COLUMN target_user_id BIGINT NULL,
    ADD COLUMN appointment_id BIGINT NULL;

CREATE INDEX idx_activity_log_target_user ON activity_log(target_user_id);
CREATE INDEX idx_activity_log_actor_user ON activity_log(actor_user_id);
CREATE INDEX idx_activity_log_appointment ON activity_log(appointment_id);

ALTER TABLE activity_log
    ADD CONSTRAINT fk_activity_log_actor_user
        FOREIGN KEY (actor_user_id) REFERENCES users(id)
            ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE activity_log
    ADD CONSTRAINT fk_activity_log_target_user
        FOREIGN KEY (target_user_id) REFERENCES users(id)
            ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE activity_log
    ADD CONSTRAINT fk_activity_log_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments(id)
            ON UPDATE CASCADE ON DELETE SET NULL;
