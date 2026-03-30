DROP TABLE test_drives IF EXISTS;
DROP TABLE order_requirements IF EXISTS;
DROP TABLE orders IF EXISTS;
DROP TABLE cars IF EXISTS;
DROP TABLE clients IF EXISTS;
DROP TABLE car_brands IF EXISTS;

CREATE TABLE car_brands (
    brand_id BIGINT NOT NULL,
    brand_name VARCHAR(100) NOT NULL,
    manufacturer_name VARCHAR(120) NOT NULL,
    CONSTRAINT pk_car_brands PRIMARY KEY (brand_id),
    CONSTRAINT uq_car_brands UNIQUE (brand_name, manufacturer_name)
);

CREATE TABLE clients (
    client_id BIGINT NOT NULL,
    full_name VARCHAR(200) NOT NULL,
    address VARCHAR(300),
    phone VARCHAR(32) NOT NULL,
    email VARCHAR(120),
    CONSTRAINT pk_clients PRIMARY KEY (client_id),
    CONSTRAINT uq_clients_phone UNIQUE (phone),
    CONSTRAINT uq_clients_email UNIQUE (email)
);

CREATE TABLE cars (
    car_id BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,
    registration_number VARCHAR(20) NOT NULL,
    engine_volume_l DECIMAL(3,1) NOT NULL,
    engine_power_hp INTEGER NOT NULL,
    fuel_consumption_l_100km DECIMAL(4,1),
    doors_count SMALLINT,
    seats_count SMALLINT,
    trunk_capacity_l INTEGER,
    transmission_type VARCHAR(16) NOT NULL,
    has_cruise_control BOOLEAN NOT NULL,
    required_fuel VARCHAR(20) NOT NULL,
    has_air_conditioner BOOLEAN NOT NULL,
    has_radio BOOLEAN NOT NULL,
    has_video_system BOOLEAN NOT NULL,
    has_gps BOOLEAN NOT NULL,
    interior_trim VARCHAR(80),
    color VARCHAR(50),
    mileage_km INTEGER NOT NULL,
    last_service_date DATE,
    price DECIMAL(12,2) NOT NULL,
    CONSTRAINT pk_cars PRIMARY KEY (car_id),
    CONSTRAINT uq_cars_registration_number UNIQUE (registration_number),
    CONSTRAINT chk_cars_transmission_type
        CHECK (transmission_type IN ('AT', 'MT', 'CVT', 'AMT')),
    CONSTRAINT chk_cars_price CHECK (price >= 0),
    CONSTRAINT chk_cars_mileage CHECK (mileage_km >= 0),
    CONSTRAINT fk_cars_brand
        FOREIGN KEY (brand_id) REFERENCES car_brands (brand_id)
);

CREATE TABLE orders (
    order_id BIGINT NOT NULL,
    ordered_at TIMESTAMP NOT NULL,
    client_id BIGINT NOT NULL,
    car_id BIGINT,
    need_test_drive BOOLEAN NOT NULL,
    status VARCHAR(24) NOT NULL,
    CONSTRAINT pk_orders PRIMARY KEY (order_id),
    CONSTRAINT chk_orders_status
        CHECK (status IN (
            'IN_PROGRESS',
            'WAITING_SUPPLY',
            'IN_SHOWROOM',
            'TEST_DRIVE',
            'COMPLETED'
        )),
    CONSTRAINT fk_orders_client
        FOREIGN KEY (client_id) REFERENCES clients (client_id),
    CONSTRAINT fk_orders_car
        FOREIGN KEY (car_id) REFERENCES cars (car_id)
);

CREATE TABLE order_requirements (
    order_id BIGINT NOT NULL,
    desired_brand_id BIGINT,
    desired_engine_volume_min DECIMAL(3,1),
    desired_engine_power_min INTEGER,
    desired_transmission_type VARCHAR(16),
    desired_required_fuel VARCHAR(20),
    desired_color VARCHAR(50),
    desired_price_max DECIMAL(12,2),
    comment_text LONGVARCHAR,
    CONSTRAINT pk_order_requirements PRIMARY KEY (order_id),
    CONSTRAINT chk_order_req_transmission_type
        CHECK (
            desired_transmission_type IS NULL OR
            desired_transmission_type IN ('AT', 'MT', 'CVT', 'AMT')
        ),
    CONSTRAINT chk_order_req_price_max
        CHECK (desired_price_max IS NULL OR desired_price_max >= 0),
    CONSTRAINT fk_order_requirements_order
        FOREIGN KEY (order_id) REFERENCES orders (order_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_order_requirements_brand
        FOREIGN KEY (desired_brand_id) REFERENCES car_brands (brand_id)
);

CREATE TABLE test_drives (
    test_drive_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    car_id BIGINT NOT NULL,
    test_drive_at TIMESTAMP NOT NULL,
    notes VARCHAR(300),
    CONSTRAINT pk_test_drives PRIMARY KEY (test_drive_id),
    CONSTRAINT fk_test_drives_client
        FOREIGN KEY (client_id) REFERENCES clients (client_id),
    CONSTRAINT fk_test_drives_car
        FOREIGN KEY (car_id) REFERENCES cars (car_id),
    CONSTRAINT uq_test_drives UNIQUE (client_id, car_id, test_drive_at)
);

CREATE INDEX idx_cars_brand ON cars (brand_id);
CREATE INDEX idx_cars_price ON cars (price);
CREATE INDEX idx_cars_color ON cars (color);
CREATE INDEX idx_clients_full_name ON clients (full_name);
CREATE INDEX idx_orders_client ON orders (client_id);
CREATE INDEX idx_orders_car ON orders (car_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_ordered_at ON orders (ordered_at);
CREATE INDEX idx_order_requirements_brand ON order_requirements (desired_brand_id);
CREATE INDEX idx_test_drives_client ON test_drives (client_id);
CREATE INDEX idx_test_drives_car ON test_drives (car_id);
