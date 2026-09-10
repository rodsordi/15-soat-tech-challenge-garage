CREATE SCHEMA IF NOT EXISTS garage;

-- ---------------------------------------------------------
-- CUSTOMER
-- ---------------------------------------------------------
CREATE TABLE garage.customer (
                                 id UUID NOT NULL,
                                 name VARCHAR(255) NOT NULL,
                                 email VARCHAR(255) NOT NULL,
                                 document VARCHAR(14) NOT NULL UNIQUE,
                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP,
                                 CONSTRAINT pk_customer PRIMARY KEY (id),
                                 CONSTRAINT uk_customer_email UNIQUE (email)
);

COMMENT ON COLUMN garage.customer.id IS 'Customer id. Owner: db';
COMMENT ON COLUMN garage.customer.name IS 'Customer name. Owner: self';
COMMENT ON COLUMN garage.customer.email IS 'Customer e-mail. Owner: self';
COMMENT ON COLUMN garage.customer.document IS 'Customer document (CPF/CNPJ). Owner: self';
COMMENT ON COLUMN garage.customer.created_at IS 'Register created at. Owner: db';
COMMENT ON COLUMN garage.customer.updated_at IS 'Register updated at. Owner: db';

-- ---------------------------------------------------------
-- EMPLOYEE
-- ---------------------------------------------------------
CREATE TABLE garage.employee (
                                 id UUID NOT NULL,
                                 name VARCHAR(255) NOT NULL,
                                 email VARCHAR(255) NOT NULL,
                                 cpf VARCHAR(11) NOT NULL UNIQUE,
                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP,
                                 CONSTRAINT pk_employee PRIMARY KEY (id),
                                 CONSTRAINT uk_employee_email UNIQUE (email)
);

COMMENT ON COLUMN garage.employee.id IS 'Employee id. Owner: db';
COMMENT ON COLUMN garage.employee.name IS 'Employee name. Owner: self';
COMMENT ON COLUMN garage.employee.email IS 'Employee e-mail. Owner: self';
COMMENT ON COLUMN garage.employee.cpf IS 'Employee cpf. Owner: self';
COMMENT ON COLUMN garage.employee.created_at IS 'Register created at. Owner: db';
COMMENT ON COLUMN garage.employee.updated_at IS 'Register updated at. Owner: db';

-- ---------------------------------------------------------
-- VEHICLE
-- ---------------------------------------------------------
CREATE TABLE garage.vehicle (
                                id UUID NOT NULL,
                                make VARCHAR(100) NOT NULL,
                                model VARCHAR(100) NOT NULL,
                                license_plate VARCHAR(10) NOT NULL,
                                manufacture_year INTEGER NOT NULL,
                                customer_id UUID,
                                created_at TIMESTAMP NOT NULL,
                                updated_at TIMESTAMP,
                                CONSTRAINT pk_vehicle PRIMARY KEY (id),
                                CONSTRAINT fk_vehicle_customer FOREIGN KEY (customer_id) REFERENCES garage.customer(id)
);

COMMENT ON COLUMN garage.vehicle.id IS 'Vehicle id. Owner: db';
COMMENT ON COLUMN garage.vehicle.make IS 'Vehicle make. Owner: self';
COMMENT ON COLUMN garage.vehicle.model IS 'Vehicle model. Owner: self';
COMMENT ON COLUMN garage.vehicle.license_plate IS 'Vehicle license plate. Owner: self';
COMMENT ON COLUMN garage.vehicle.manufacture_year IS 'Vehicle manufacture year. Owner: self';
COMMENT ON COLUMN garage.vehicle.customer_id IS 'Customer id. Owner: db';
COMMENT ON COLUMN garage.vehicle.created_at IS 'Register created at. Owner: db';
COMMENT ON COLUMN garage.vehicle.updated_at IS 'Register updated at. Owner: db';

-- ---------------------------------------------------------
-- EMAIL
-- ---------------------------------------------------------
CREATE TABLE garage.email (
                              id UUID NOT NULL,
                              recipient VARCHAR(255) NOT NULL,
                              bcc VARCHAR(255) NOT NULL,
                              subject VARCHAR(255) NOT NULL,
                              message TEXT NOT NULL,
                              created_at TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP,
                              CONSTRAINT pk_email PRIMARY KEY (id)
);

COMMENT ON COLUMN garage.email.id IS 'Email id. Owner: db';
COMMENT ON COLUMN garage.email.recipient IS 'Email recipient. Owner: self';
COMMENT ON COLUMN garage.email.bcc IS 'Email bcc. Owner: self';
COMMENT ON COLUMN garage.email.subject IS 'Email subject. Owner: self';
COMMENT ON COLUMN garage.email.message IS 'Email message. Owner: self';
COMMENT ON COLUMN garage.email.created_at IS 'Register created at. Owner: db';
COMMENT ON COLUMN garage.email.updated_at IS 'Register updated at. Owner: db';

-- ---------------------------------------------------------
-- NOTIFICATION
-- ---------------------------------------------------------
CREATE TABLE garage.notification (
                                     id UUID NOT NULL,
                                     external_id UUID NOT NULL,
                                     created_at TIMESTAMP NOT NULL,
                                     updated_at TIMESTAMP,
                                     CONSTRAINT pk_notification PRIMARY KEY (id),
                                     CONSTRAINT fk_notification_email FOREIGN KEY (id) REFERENCES garage.email(id)
);

COMMENT ON COLUMN garage.notification.id IS 'Notification id. Owner: db';
COMMENT ON COLUMN garage.notification.external_id IS 'Notification id. Owner: db';
COMMENT ON COLUMN garage.notification.created_at IS 'Register created at. Owner: db';
COMMENT ON COLUMN garage.notification.updated_at IS 'Register updated at. Owner: db';

-- ---------------------------------------------------------
-- MATERIAL
-- ---------------------------------------------------------
CREATE TABLE garage.material (
                                 id UUID NOT NULL,
                                 type VARCHAR(55) NOT NULL,
                                 name VARCHAR(255) NOT NULL,
                                 description VARCHAR(255),
                                 cost NUMERIC NOT NULL,
                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP,
                                 CONSTRAINT pk_material PRIMARY KEY (id)
);

COMMENT ON COLUMN garage.material.id IS 'Material id. Owner: db';
COMMENT ON COLUMN garage.material.type IS 'Material type. Owner: self';
COMMENT ON COLUMN garage.material.name IS 'Material name. Owner: self';
COMMENT ON COLUMN garage.material.description IS 'Material description. Owner: self';
COMMENT ON COLUMN garage.material.cost IS 'Material cost. Owner: self';
COMMENT ON COLUMN garage.material.created_at IS 'Register created at. Owner: db';
COMMENT ON COLUMN garage.material.updated_at IS 'Register updated at. Owner: db';

-- ---------------------------------------------------------
-- INVENTORY_MATERIAL
-- ---------------------------------------------------------
CREATE TABLE garage.inventory_material (
                                           id UUID NOT NULL,
                                           quantity_in_stock INTEGER NOT NULL,
                                           reserved_quantity INTEGER NOT NULL DEFAULT 0,
                                           created_at TIMESTAMP NOT NULL,
                                           updated_at TIMESTAMP,
                                           CONSTRAINT pk_inventory_material PRIMARY KEY (id),
                                           CONSTRAINT fk_inventory_material_material FOREIGN KEY (id) REFERENCES garage.material(id)
);

COMMENT ON COLUMN garage.inventory_material.id IS 'Inventory id. Owner: db';
COMMENT ON COLUMN garage.inventory_material.quantity_in_stock IS 'Inventory quantity in stock. Owner: self';
COMMENT ON COLUMN garage.inventory_material.reserved_quantity IS 'Inventory reserved quantity. Owner: self';
COMMENT ON COLUMN garage.inventory_material.created_at IS 'Register created at. Owner: db';
COMMENT ON COLUMN garage.inventory_material.updated_at IS 'Register updated at. Owner: db';

-- ---------------------------------------------------------
-- SERVICE
-- ---------------------------------------------------------
CREATE TABLE garage.service (
                                id UUID NOT NULL,
                                name VARCHAR(255) NOT NULL,
                                description VARCHAR(255),
                                cost NUMERIC NOT NULL,
                                average_time_in_minutes BIGINT,
                                created_at TIMESTAMP NOT NULL,
                                updated_at TIMESTAMP,
                                CONSTRAINT pk_service PRIMARY KEY (id)
);

COMMENT ON COLUMN garage.service.id IS 'Service id. Owner: db';
COMMENT ON COLUMN garage.service.name IS 'Service name. Owner: self';
COMMENT ON COLUMN garage.service.description IS 'Service description. Owner: self';
COMMENT ON COLUMN garage.service.cost IS 'Service cost. Owner: self';
COMMENT ON COLUMN garage.service.created_at IS 'Register created at. Owner: db';
COMMENT ON COLUMN garage.service.updated_at IS 'Register updated at. Owner: db';

-- ---------------------------------------------------------
-- SERVICE_INVENTORY_MATERIAL (Join Table)
-- ---------------------------------------------------------
CREATE TABLE garage.service_inventory_material (
                                                   service_id UUID NOT NULL,
                                                   inventory_material_id UUID NOT NULL,
                                                   CONSTRAINT pk_service_inventory_material PRIMARY KEY (service_id, inventory_material_id),
                                                   CONSTRAINT fk_sim_service FOREIGN KEY (service_id) REFERENCES garage.service(id),
                                                   CONSTRAINT fk_sim_inventory FOREIGN KEY (inventory_material_id) REFERENCES garage.inventory_material(id)
);

-- ---------------------------------------------------------
-- WORK_ORDER
-- ---------------------------------------------------------
CREATE TABLE garage.work_order (
                                   id UUID NOT NULL,
                                   status VARCHAR(255),
                                   total_amount NUMERIC NOT NULL,
                                   vehicle_id UUID,
                                   employee_id UUID,
                                   created_at TIMESTAMP NOT NULL,
                                   updated_at TIMESTAMP,
                                   CONSTRAINT pk_work_order PRIMARY KEY (id),
                                   CONSTRAINT fk_work_order_vehicle FOREIGN KEY (vehicle_id) REFERENCES garage.vehicle(id),
                                   CONSTRAINT fk_work_order_employee FOREIGN KEY (employee_id) REFERENCES garage.employee(id)
);

COMMENT ON COLUMN garage.work_order.id IS 'Work Order id. Owner: db';
COMMENT ON COLUMN garage.work_order.status IS 'Work Order status. Owner: self';
COMMENT ON COLUMN garage.work_order.total_amount IS 'Work Order total amount estimation. Owner: self';
COMMENT ON COLUMN garage.work_order.vehicle_id IS 'Vehicle id. Owner: db';
COMMENT ON COLUMN garage.work_order.employee_id IS 'Employee id. Owner: db';
COMMENT ON COLUMN garage.work_order.created_at IS 'Register created at. Owner: db';
COMMENT ON COLUMN garage.work_order.updated_at IS 'Register updated at. Owner: db';

-- ---------------------------------------------------------
-- ESTIMATED_SERVICE
-- ---------------------------------------------------------
CREATE TABLE garage.estimated_service (
                                          id BIGINT GENERATED BY DEFAULT AS IDENTITY,
                                          service_id UUID NOT NULL,
                                          name VARCHAR(255) NOT NULL,
                                          description VARCHAR(255),
                                          cost NUMERIC NOT NULL,
                                          finished_at TIMESTAMP,
                                          work_order_id UUID NOT NULL,
                                          created_at TIMESTAMP NOT NULL,
                                          updated_at TIMESTAMP,
                                          CONSTRAINT pk_estimated_service PRIMARY KEY (id),
                                          CONSTRAINT fk_es_work_order FOREIGN KEY (work_order_id) REFERENCES garage.work_order(id)
);

COMMENT ON COLUMN garage.estimated_service.id IS 'Estimated Service id. Owner: db';
COMMENT ON COLUMN garage.estimated_service.name IS 'Estimated Service name. Owner: self';
COMMENT ON COLUMN garage.estimated_service.description IS 'Estimated Service description. Owner: self';
COMMENT ON COLUMN garage.estimated_service.cost IS 'Estimated Service cost. Owner: self';
COMMENT ON COLUMN garage.estimated_service.finished_at IS 'Estimated Service finished at. Owner: self';
COMMENT ON COLUMN garage.estimated_service.work_order_id IS 'Work Order id. Owner: db';
COMMENT ON COLUMN garage.estimated_service.created_at IS 'Register created at. Owner: db';
COMMENT ON COLUMN garage.estimated_service.updated_at IS 'Register updated at. Owner: db';

-- ---------------------------------------------------------
-- ESTIMATED_MATERIAL
-- ---------------------------------------------------------
CREATE TABLE garage.estimated_material (
                                           id BIGINT GENERATED BY DEFAULT AS IDENTITY,
                                           material_id UUID NOT NULL,
                                           type VARCHAR(55) NOT NULL,
                                           name VARCHAR(55) NOT NULL,
                                           description VARCHAR(255),
                                           cost NUMERIC NOT NULL,
                                           estimated_service_id BIGINT NOT NULL,
                                           created_at TIMESTAMP NOT NULL,
                                           updated_at TIMESTAMP,
                                           CONSTRAINT pk_estimated_material PRIMARY KEY (id),
                                           CONSTRAINT fk_em_estimated_service FOREIGN KEY (estimated_service_id) REFERENCES garage.estimated_service(id)
);

COMMENT ON COLUMN garage.estimated_material.id IS 'Estimated Material id. Owner: db';
COMMENT ON COLUMN garage.estimated_material.type IS 'Estimated Material type. Owner: self';
COMMENT ON COLUMN garage.estimated_material.name IS 'Estimated Material name. Owner: self';
COMMENT ON COLUMN garage.estimated_material.description IS 'Estimated Material description. Owner: self';
COMMENT ON COLUMN garage.estimated_material.cost IS 'Estimated Material cost. Owner: self';
COMMENT ON COLUMN garage.estimated_material.created_at IS 'Register created at. Owner: db';
COMMENT ON COLUMN garage.estimated_material.updated_at IS 'Register updated at. Owner: db';