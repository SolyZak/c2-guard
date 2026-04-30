-- ============================================================
-- V80: Drop all legacy task tables
--
-- Verified: zero FK references exist to any of these tables.
-- Drop children first, then parents.
-- CASCADE used as safety net.
-- ============================================================

-- 1. Drop patrol execution children (check-type-specific)
DROP TABLE IF EXISTS task_check_decimal_patrol_execution CASCADE;
DROP TABLE IF EXISTS task_check_list_patrol_execution    CASCADE;
DROP TABLE IF EXISTS task_check_number_patrol_execution  CASCADE;
DROP TABLE IF EXISTS task_check_text_patrol_execution    CASCADE;

-- 2. Drop base patrol execution tables
DROP TABLE IF EXISTS task_check_patrol_execution CASCADE;
DROP TABLE IF EXISTS task_patrol_execution       CASCADE;

-- 3. Drop check-type-specific tables
DROP TABLE IF EXISTS task_check_decimal CASCADE;
DROP TABLE IF EXISTS task_check_list    CASCADE;
DROP TABLE IF EXISTS task_check_number  CASCADE;
DROP TABLE IF EXISTS task_check_text    CASCADE;

-- 4. Drop task_check (parent of check subtypes)
DROP TABLE IF EXISTS task_check CASCADE;

-- 5. Drop task (root parent)
DROP TABLE IF EXISTS task CASCADE;

-- 6. Drop orphaned sequences if they exist
DROP SEQUENCE IF EXISTS task_seq                                CASCADE;
DROP SEQUENCE IF EXISTS task_id_seq                             CASCADE;
DROP SEQUENCE IF EXISTS task_check_seq                          CASCADE;
DROP SEQUENCE IF EXISTS task_check_id_seq                       CASCADE;
DROP SEQUENCE IF EXISTS task_check_decimal_seq                  CASCADE;
DROP SEQUENCE IF EXISTS task_check_decimal_id_seq               CASCADE;
DROP SEQUENCE IF EXISTS task_check_list_seq                     CASCADE;
DROP SEQUENCE IF EXISTS task_check_list_id_seq                  CASCADE;
DROP SEQUENCE IF EXISTS task_check_number_seq                   CASCADE;
DROP SEQUENCE IF EXISTS task_check_number_id_seq                CASCADE;
DROP SEQUENCE IF EXISTS task_check_text_seq                     CASCADE;
DROP SEQUENCE IF EXISTS task_check_text_id_seq                  CASCADE;
DROP SEQUENCE IF EXISTS task_check_patrol_exec_seq              CASCADE;
DROP SEQUENCE IF EXISTS task_check_patrol_execution_seq         CASCADE;
DROP SEQUENCE IF EXISTS task_check_decimal_patrol_exec_seq      CASCADE;
DROP SEQUENCE IF EXISTS task_check_decimal_patrol_execution_seq CASCADE;
DROP SEQUENCE IF EXISTS task_check_list_patrol_exec_seq         CASCADE;
DROP SEQUENCE IF EXISTS task_check_list_patrol_execution_seq    CASCADE;
DROP SEQUENCE IF EXISTS task_check_number_patrol_exec_seq       CASCADE;
DROP SEQUENCE IF EXISTS task_check_number_patrol_execution_seq  CASCADE;
DROP SEQUENCE IF EXISTS task_check_text_patrol_exec_seq         CASCADE;
DROP SEQUENCE IF EXISTS task_check_text_patrol_execution_seq    CASCADE;
DROP SEQUENCE IF EXISTS task_patrol_execution_seq               CASCADE;
DROP SEQUENCE IF EXISTS task_patrol_execution_id_seq            CASCADE;