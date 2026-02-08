-- Create task_distribution table
CREATE TABLE task_distribution (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT REFERENCES customer_contract(id) ON DELETE CASCADE ON UPDATE CASCADE,
    customer_id BIGINT REFERENCES customers(id) ON DELETE CASCADE ON UPDATE CASCADE,
    task_id BIGINT REFERENCES task(id) ON DELETE CASCADE ON UPDATE CASCADE,
    distribution_type VARCHAR(50) NOT NULL CHECK (distribution_type IN ('PATROL', 'IMMEDIATE')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Create indexes for task_distribution
CREATE INDEX idx_task_distribution_contract_type ON task_distribution(contract_id, distribution_type);
CREATE INDEX idx_task_distribution_customer_id ON task_distribution(customer_id);
CREATE INDEX idx_task_distribution_task_id ON task_distribution(task_id);
CREATE INDEX idx_task_distribution_created_at ON task_distribution(created_at);
CREATE INDEX idx_task_distribution_distribution_type ON task_distribution(distribution_type);
CREATE INDEX idx_task_distribution_customer_created ON task_distribution(customer_id, created_at);
CREATE INDEX idx_task_distribution_contract_customer ON task_distribution(contract_id, customer_id);

-- Create task_assignment table
CREATE TABLE task_assignment (
    id BIGSERIAL PRIMARY KEY,
    slot_number INTEGER NOT NULL,
    workforce_id BIGINT NOT NULL,
    customer_id BIGINT REFERENCES customers(id) ON DELETE CASCADE ON UPDATE CASCADE,
    assigned_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Create indexes for task_assignment
CREATE INDEX idx_task_assignment_workforce_id ON task_assignment(workforce_id);
CREATE INDEX idx_task_assignment_customer_id ON task_assignment(customer_id);
CREATE INDEX idx_task_assignment_slot_number ON task_assignment(slot_number);
CREATE INDEX idx_task_assignment_assigned_at ON task_assignment(assigned_at);
CREATE INDEX idx_task_assignment_workforce_customer ON task_assignment(workforce_id, customer_id);
CREATE INDEX idx_task_assignment_customer_assigned ON task_assignment(customer_id, assigned_at);
CREATE INDEX idx_task_assignment_workforce_slot ON task_assignment(workforce_id, slot_number);

-- Create task_execution_slot table
CREATE TABLE task_execution_slot (
    id BIGSERIAL PRIMARY KEY,
    task_distribution_id BIGINT REFERENCES task_distribution(id) ON DELETE CASCADE ON UPDATE CASCADE,
    task_assignment_id BIGINT REFERENCES task_assignment(id) ON DELETE CASCADE ON UPDATE CASCADE,
    start_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('FINISHED', 'MISSED', 'CREATED', 'CURRENT')),
    task_execution_id BIGINT REFERENCES task_patrol_execution(id) ON DELETE CASCADE ON UPDATE CASCADE,
    executed_by_workforce_id BIGINT,
    customer_id BIGINT REFERENCES customers(id) ON DELETE CASCADE ON UPDATE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Create indexes for task_execution_slot
CREATE INDEX idx_task_execution_slot_task_distribution_id ON task_execution_slot(task_distribution_id);
CREATE INDEX idx_task_execution_slot_task_assignment_id ON task_execution_slot(task_assignment_id);
CREATE INDEX idx_task_execution_slot_customer_date_range ON task_execution_slot(customer_id, start_date_time, end_date_time);
CREATE INDEX idx_task_execution_slot_status ON task_execution_slot(status);
CREATE INDEX idx_task_execution_slot_executed_by_workforce_id ON task_execution_slot(executed_by_workforce_id);
CREATE INDEX idx_task_execution_slot_task_execution_id ON task_execution_slot(task_execution_id);
CREATE INDEX idx_task_execution_slot_status_created_at ON task_execution_slot(status, created_at);
CREATE INDEX idx_task_execution_slot_workforce_status ON task_execution_slot(executed_by_workforce_id, status);

-- Create immediate_task_distribution table
CREATE TABLE immediate_task_distribution (
    id BIGSERIAL PRIMARY KEY,
    task_distribution_id BIGINT REFERENCES task_distribution(id) ON DELETE CASCADE ON UPDATE CASCADE UNIQUE,
    dispatcher_id BIGINT REFERENCES customer_users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    location_id BIGINT REFERENCES location(id) ON DELETE CASCADE ON UPDATE CASCADE,
    location_name VARCHAR(255),
    longitude NUMERIC(13,10),
    latitude NUMERIC(13,10),
    customer_id BIGINT REFERENCES customers(id) ON DELETE CASCADE ON UPDATE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Create indexes for immediate_task_distribution
CREATE INDEX idx_immediate_task_distribution_task_distribution_id ON immediate_task_distribution(task_distribution_id);
CREATE INDEX idx_immediate_task_distribution_location_id ON immediate_task_distribution(location_id);
CREATE INDEX idx_immediate_task_distribution_customer_id ON immediate_task_distribution(customer_id);
CREATE INDEX idx_immediate_task_distribution_dispatcher_id ON immediate_task_distribution(dispatcher_id);
CREATE INDEX idx_immediate_task_distribution_created_at ON immediate_task_distribution(created_at);
CREATE INDEX idx_immediate_task_distribution_customer_created ON immediate_task_distribution(customer_id, created_at);
CREATE INDEX idx_immediate_task_distribution_dispatcher_customer ON immediate_task_distribution(dispatcher_id, customer_id);

-- Create patrol_task_distribution table
CREATE TABLE patrol_task_distribution (
    id BIGSERIAL PRIMARY KEY,
    task_distribution_id BIGINT REFERENCES task_distribution(id) ON DELETE CASCADE ON UPDATE CASCADE UNIQUE,
    patrol_detail_id BIGINT REFERENCES patrol_detail(id) ON DELETE CASCADE ON UPDATE CASCADE,
    service_id BIGINT REFERENCES customer_contract_service(id) ON DELETE CASCADE ON UPDATE CASCADE,
    location_id BIGINT REFERENCES location(id) ON DELETE CASCADE ON UPDATE CASCADE,
    service_time_id BIGINT REFERENCES contract_operation_site_distribution_details(id) ON DELETE CASCADE ON UPDATE CASCADE,
    distributed_quantity INTEGER,
    frequency_rate VARCHAR(100),
    customer_id BIGINT REFERENCES customers(id) ON DELETE CASCADE ON UPDATE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uc_patroltaskdistribution_patroldetailid_servicetimeid UNIQUE (patrol_detail_id, service_time_id)
);

-- Create indexes for patrol_task_distribution
CREATE INDEX idx_patrol_task_distribution_task_distribution_id ON patrol_task_distribution(task_distribution_id);
CREATE INDEX idx_patrol_task_distribution_service_lookup ON patrol_task_distribution(service_id, service_time_id);
CREATE INDEX idx_patrol_task_distribution_location_id ON patrol_task_distribution(location_id);
CREATE INDEX idx_patrol_task_distribution_customer_id ON patrol_task_distribution(customer_id);
CREATE INDEX idx_patrol_task_distribution_patrol_detail_id ON patrol_task_distribution(patrol_detail_id);
CREATE INDEX idx_patrol_task_distribution_customer_location ON patrol_task_distribution(customer_id, location_id);
CREATE INDEX idx_patrol_task_distribution_service_customer ON patrol_task_distribution(service_id, customer_id);
