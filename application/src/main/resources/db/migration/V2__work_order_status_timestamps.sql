ALTER TABLE garage.work_order
    ADD COLUMN IF NOT EXISTS diagnosing_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS waiting_approval_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS executing_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS finished_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS released_at TIMESTAMP;

COMMENT ON COLUMN garage.work_order.diagnosing_at IS 'Work Order diagnosing start timestamp. Owner: self';
COMMENT ON COLUMN garage.work_order.waiting_approval_at IS 'Work Order waiting approval timestamp. Owner: self';
COMMENT ON COLUMN garage.work_order.executing_at IS 'Work Order executing start timestamp. Owner: self';
COMMENT ON COLUMN garage.work_order.finished_at IS 'Work Order finished timestamp. Owner: self';
COMMENT ON COLUMN garage.work_order.released_at IS 'Work Order released timestamp. Owner: self';
