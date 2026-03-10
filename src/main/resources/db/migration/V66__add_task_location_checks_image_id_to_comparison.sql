ALTER TABLE task_check_comparison
    ADD COLUMN task_location_checks_image_id BIGINT;

ALTER TABLE task_check_comparison
    ADD CONSTRAINT fk_comparison_location_checks_image
    FOREIGN KEY (task_location_checks_image_id)
    REFERENCES task_location_checks_image(id);